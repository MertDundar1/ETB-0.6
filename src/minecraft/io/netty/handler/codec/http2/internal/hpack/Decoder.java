package io.netty.handler.codec.http2.internal.hpack;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.internal.ThrowableUtil;










































public final class Decoder
{
  private static final Http2Exception DECODE_DECOMPRESSION_EXCEPTION = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - decompression failure", new Object[0]), Decoder.class, "decode(...)");
  
  private static final Http2Exception DECODE_ULE_128_DECOMPRESSION_EXCEPTION = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - decompression failure", new Object[0]), Decoder.class, "decodeULE128(...)");
  
  private static final Http2Exception DECODE_ILLEGAL_INDEX_VALUE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value", new Object[0]), Decoder.class, "decode(...)");
  
  private static final Http2Exception INDEX_HEADER_ILLEGAL_INDEX_VALUE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value", new Object[0]), Decoder.class, "indexHeader(...)");
  
  private static final Http2Exception READ_NAME_ILLEGAL_INDEX_VALUE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value", new Object[0]), Decoder.class, "readName(...)");
  
  private static final Http2Exception INVALID_MAX_DYNAMIC_TABLE_SIZE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - invalid max dynamic table size", new Object[0]), Decoder.class, "setDynamicTableSize(...)");
  

  private static final Http2Exception MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - max dynamic table size change required", new Object[0]), Decoder.class, "decode(...)");
  
  private static final byte READ_HEADER_REPRESENTATION = 0;
  
  private static final byte READ_MAX_DYNAMIC_TABLE_SIZE = 1;
  private static final byte READ_INDEXED_HEADER = 2;
  private static final byte READ_INDEXED_HEADER_NAME = 3;
  private static final byte READ_LITERAL_HEADER_NAME_LENGTH_PREFIX = 4;
  private static final byte READ_LITERAL_HEADER_NAME_LENGTH = 5;
  private static final byte READ_LITERAL_HEADER_NAME = 6;
  private static final byte READ_LITERAL_HEADER_VALUE_LENGTH_PREFIX = 7;
  private static final byte READ_LITERAL_HEADER_VALUE_LENGTH = 8;
  private static final byte READ_LITERAL_HEADER_VALUE = 9;
  private final DynamicTable dynamicTable;
  private final HuffmanDecoder huffmanDecoder;
  private long maxHeaderListSize;
  private long maxDynamicTableSize;
  private long encoderMaxDynamicTableSize;
  private boolean maxDynamicTableSizeChangeRequired;
  
  public Decoder()
  {
    this(32);
  }
  
  public Decoder(int initialHuffmanDecodeCapacity) {
    this(initialHuffmanDecodeCapacity, 4096);
  }
  



  Decoder(int initialHuffmanDecodeCapacity, int maxHeaderTableSize)
  {
    maxHeaderListSize = 8192L;
    maxDynamicTableSize = (this.encoderMaxDynamicTableSize = maxHeaderTableSize);
    maxDynamicTableSizeChangeRequired = false;
    dynamicTable = new DynamicTable(maxHeaderTableSize);
    huffmanDecoder = new HuffmanDecoder(initialHuffmanDecodeCapacity);
  }
  



  public void decode(int streamId, ByteBuf in, Http2Headers headers)
    throws Http2Exception
  {
    int index = 0;
    long headersLength = 0L;
    int nameLength = 0;
    int valueLength = 0;
    byte state = 0;
    boolean huffmanEncoded = false;
    CharSequence name = null;
    HpackUtil.IndexType indexType = HpackUtil.IndexType.NONE;
    while (in.isReadable()) {
      switch (state) {
      case 0: 
        byte b = in.readByte();
        if ((maxDynamicTableSizeChangeRequired) && ((b & 0xE0) != 32))
        {
          throw MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED;
        }
        if (b < 0)
        {
          index = b & 0x7F;
          switch (index) {
          case 0: 
            throw DECODE_ILLEGAL_INDEX_VALUE;
          case 127: 
            state = 2;
            break;
          default: 
            headersLength = indexHeader(streamId, index, headers, headersLength);break;
          }
        } else if ((b & 0x40) == 64)
        {
          indexType = HpackUtil.IndexType.INCREMENTAL;
          index = b & 0x3F;
          switch (index) {
          case 0: 
            state = 4;
            break;
          case 63: 
            state = 3;
            break;
          
          default: 
            name = readName(index);
            state = 7;break;
          }
        } else if ((b & 0x20) == 32)
        {
          index = b & 0x1F;
          if (index == 31) {
            state = 1;
          } else {
            setDynamicTableSize(index);
            state = 0;
          }
        }
        else {
          indexType = (b & 0x10) == 16 ? HpackUtil.IndexType.NEVER : HpackUtil.IndexType.NONE;
          index = b & 0xF;
          switch (index) {
          case 0: 
            state = 4;
            break;
          case 15: 
            state = 3;
            break;
          
          default: 
            name = readName(index);
            state = 7;
          }
        }
        break;
      
      case 1: 
        setDynamicTableSize(decodeULE128(in, index));
        state = 0;
        break;
      
      case 2: 
        headersLength = indexHeader(streamId, decodeULE128(in, index), headers, headersLength);
        state = 0;
        break;
      

      case 3: 
        name = readName(decodeULE128(in, index));
        state = 7;
        break;
      
      case 4: 
        byte b = in.readByte();
        huffmanEncoded = (b & 0x80) == 128;
        index = b & 0x7F;
        if (index == 127) {
          state = 5;
        } else {
          if (index > maxHeaderListSize - headersLength) {
            Http2CodecUtil.headerListSizeExceeded(streamId, maxHeaderListSize);
          }
          nameLength = index;
          state = 6;
        }
        break;
      

      case 5: 
        nameLength = decodeULE128(in, index);
        
        if (nameLength > maxHeaderListSize - headersLength) {
          Http2CodecUtil.headerListSizeExceeded(streamId, maxHeaderListSize);
        }
        state = 6;
        break;
      

      case 6: 
        if (in.readableBytes() < nameLength) {
          throw notEnoughDataException(in);
        }
        
        name = readStringLiteral(in, nameLength, huffmanEncoded);
        
        state = 7;
        break;
      
      case 7: 
        byte b = in.readByte();
        huffmanEncoded = (b & 0x80) == 128;
        index = b & 0x7F;
        switch (index) {
        case 127: 
          state = 8;
          break;
        case 0: 
          headersLength = insertHeader(streamId, headers, name, AsciiString.EMPTY_STRING, indexType, headersLength);
          
          state = 0;
          break;
        
        default: 
          if (index + nameLength > maxHeaderListSize - headersLength) {
            Http2CodecUtil.headerListSizeExceeded(streamId, maxHeaderListSize);
          }
          valueLength = index;
          state = 9;
        }
        
        break;
      

      case 8: 
        valueLength = decodeULE128(in, index);
        

        if (valueLength + nameLength > maxHeaderListSize - headersLength) {
          Http2CodecUtil.headerListSizeExceeded(streamId, maxHeaderListSize);
        }
        state = 9;
        break;
      

      case 9: 
        if (in.readableBytes() < valueLength) {
          throw notEnoughDataException(in);
        }
        
        CharSequence value = readStringLiteral(in, valueLength, huffmanEncoded);
        headersLength = insertHeader(streamId, headers, name, value, indexType, headersLength);
        state = 0;
        break;
      
      default: 
        throw new Error("should not reach here state: " + state);
      }
      
    }
  }
  

  public void setMaxHeaderTableSize(long maxHeaderTableSize)
    throws Http2Exception
  {
    if ((maxHeaderTableSize < 0L) || (maxHeaderTableSize > 4294967295L)) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", new Object[] { Long.valueOf(0L), Long.valueOf(4294967295L), Long.valueOf(maxHeaderTableSize) });
    }
    
    maxDynamicTableSize = maxHeaderTableSize;
    if (maxDynamicTableSize < encoderMaxDynamicTableSize)
    {

      maxDynamicTableSizeChangeRequired = true;
      dynamicTable.setCapacity(maxDynamicTableSize);
    }
  }
  
  public void setMaxHeaderListSize(long maxHeaderListSize) throws Http2Exception {
    if ((maxHeaderListSize < 0L) || (maxHeaderListSize > 4294967295L)) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header List Size must be >= %d and <= %d but was %d", new Object[] { Long.valueOf(0L), Long.valueOf(4294967295L), Long.valueOf(maxHeaderListSize) });
    }
    
    this.maxHeaderListSize = maxHeaderListSize;
  }
  
  public long getMaxHeaderListSize() {
    return maxHeaderListSize;
  }
  



  public long getMaxHeaderTableSize()
  {
    return dynamicTable.capacity();
  }
  


  int length()
  {
    return dynamicTable.length();
  }
  


  long size()
  {
    return dynamicTable.size();
  }
  


  HeaderField getHeaderField(int index)
  {
    return dynamicTable.getEntry(index + 1);
  }
  
  private void setDynamicTableSize(int dynamicTableSize) throws Http2Exception {
    if (dynamicTableSize > maxDynamicTableSize) {
      throw INVALID_MAX_DYNAMIC_TABLE_SIZE;
    }
    encoderMaxDynamicTableSize = dynamicTableSize;
    maxDynamicTableSizeChangeRequired = false;
    dynamicTable.setCapacity(dynamicTableSize);
  }
  
  private CharSequence readName(int index) throws Http2Exception {
    if (index <= StaticTable.length) {
      HeaderField headerField = StaticTable.getEntry(index);
      return name;
    }
    if (index - StaticTable.length <= dynamicTable.length()) {
      HeaderField headerField = dynamicTable.getEntry(index - StaticTable.length);
      return name;
    }
    throw READ_NAME_ILLEGAL_INDEX_VALUE;
  }
  
  private long indexHeader(int streamId, int index, Http2Headers headers, long headersLength) throws Http2Exception {
    if (index <= StaticTable.length) {
      HeaderField headerField = StaticTable.getEntry(index);
      return addHeader(streamId, headers, name, value, headersLength);
    }
    if (index - StaticTable.length <= dynamicTable.length()) {
      HeaderField headerField = dynamicTable.getEntry(index - StaticTable.length);
      return addHeader(streamId, headers, name, value, headersLength);
    }
    throw INDEX_HEADER_ILLEGAL_INDEX_VALUE;
  }
  
  private long insertHeader(int streamId, Http2Headers headers, CharSequence name, CharSequence value, HpackUtil.IndexType indexType, long headerSize) throws Http2Exception
  {
    headerSize = addHeader(streamId, headers, name, value, headerSize);
    
    switch (1.$SwitchMap$io$netty$handler$codec$http2$internal$hpack$HpackUtil$IndexType[indexType.ordinal()])
    {
    case 1: 
    case 2: 
      break;
    case 3: 
      dynamicTable.add(new HeaderField(name, value));
      break;
    
    default: 
      throw new Error("should not reach here");
    }
    
    return headerSize;
  }
  
  private long addHeader(int streamId, Http2Headers headers, CharSequence name, CharSequence value, long headersLength) throws Http2Exception
  {
    headersLength += name.length() + value.length();
    if (headersLength > maxHeaderListSize) {
      Http2CodecUtil.headerListSizeExceeded(streamId, maxHeaderListSize);
    }
    headers.add(name, value);
    return headersLength;
  }
  
  private CharSequence readStringLiteral(ByteBuf in, int length, boolean huffmanEncoded) throws Http2Exception {
    if (huffmanEncoded) {
      return huffmanDecoder.decode(in, length);
    }
    byte[] buf = new byte[length];
    in.readBytes(buf);
    return new AsciiString(buf, false);
  }
  
  private static IllegalArgumentException notEnoughDataException(ByteBuf in) {
    return new IllegalArgumentException("decode only works with an entire header block! " + in);
  }
  
  private static int decodeULE128(ByteBuf in, int result) throws Http2Exception
  {
    assert ((result <= 127) && (result >= 0));
    int writerIndex = in.writerIndex();
    int readerIndex = in.readerIndex(); for (int shift = 0; 
        readerIndex < writerIndex; shift += 7) {
      byte b = in.getByte(readerIndex);
      if ((shift == 28) && (((b & 0x80) != 0) || (b > 6)))
      {


        in.readerIndex(readerIndex + 1);
        break;
      }
      
      if ((b & 0x80) == 0) {
        in.readerIndex(readerIndex + 1);
        return result + ((b & 0x7F) << shift);
      }
      result += ((b & 0x7F) << shift);readerIndex++;
    }
    
    throw DECODE_ULE_128_DECOMPRESSION_EXCEPTION;
  }
}
