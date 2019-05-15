package io.netty.handler.codec.http2.internal.hpack;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersEncoder.SensitivityDetector;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.MathUtil;
import java.util.Arrays;
import java.util.Map.Entry;















































public final class Encoder
{
  private final HeaderEntry[] headerFields;
  private final HeaderEntry head = new HeaderEntry(-1, AsciiString.EMPTY_STRING, AsciiString.EMPTY_STRING, Integer.MAX_VALUE, null);
  
  private final HuffmanEncoder huffmanEncoder = new HuffmanEncoder();
  
  private final byte hashMask;
  
  private final boolean ignoreMaxHeaderListSize;
  private long size;
  private long maxHeaderTableSize;
  private long maxHeaderListSize;
  
  public Encoder()
  {
    this(false);
  }
  


  public Encoder(boolean ignoreMaxHeaderListSize)
  {
    this(ignoreMaxHeaderListSize, 16);
  }
  


  public Encoder(boolean ignoreMaxHeaderListSize, int arraySizeHint)
  {
    this.ignoreMaxHeaderListSize = ignoreMaxHeaderListSize;
    maxHeaderTableSize = 4096L;
    maxHeaderListSize = 8192L;
    

    headerFields = new HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
    hashMask = ((byte)(headerFields.length - 1));
    head.before = (head.after = head);
  }
  




  public void encodeHeaders(ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector)
    throws Http2Exception
  {
    if (ignoreMaxHeaderListSize) {
      encodeHeadersIgnoreMaxHeaderListSize(out, headers, sensitivityDetector);
    } else {
      encodeHeadersEnforceMaxHeaderListSize(out, headers, sensitivityDetector);
    }
  }
  
  private void encodeHeadersEnforceMaxHeaderListSize(ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector)
    throws Http2Exception
  {
    long headerSize = 0L;
    for (Map.Entry<CharSequence, CharSequence> header : headers) {
      CharSequence name = (CharSequence)header.getKey();
      CharSequence value = (CharSequence)header.getValue();
      long currHeaderSize = HeaderField.sizeOf(name, value);
      

      headerSize += currHeaderSize;
      if (headerSize > maxHeaderListSize) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header list size octets (%d) exceeds maxHeaderListSize (%d)", new Object[] { Long.valueOf(headerSize), Long.valueOf(maxHeaderListSize) });
      }
      
      encodeHeader(out, name, value, sensitivityDetector.isSensitive(name, value), currHeaderSize);
    }
  }
  
  private void encodeHeadersIgnoreMaxHeaderListSize(ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception
  {
    for (Map.Entry<CharSequence, CharSequence> header : headers) {
      CharSequence name = (CharSequence)header.getKey();
      CharSequence value = (CharSequence)header.getValue();
      encodeHeader(out, name, value, sensitivityDetector.isSensitive(name, value), HeaderField.sizeOf(name, value));
    }
  }
  






  private void encodeHeader(ByteBuf out, CharSequence name, CharSequence value, boolean sensitive, long headerSize)
  {
    if (sensitive) {
      int nameIndex = getNameIndex(name);
      encodeLiteral(out, name, value, HpackUtil.IndexType.NEVER, nameIndex);
      return;
    }
    

    if (maxHeaderTableSize == 0L) {
      int staticTableIndex = StaticTable.getIndex(name, value);
      if (staticTableIndex == -1) {
        int nameIndex = StaticTable.getIndex(name);
        encodeLiteral(out, name, value, HpackUtil.IndexType.NONE, nameIndex);
      } else {
        encodeInteger(out, 128, 7, staticTableIndex);
      }
      return;
    }
    

    if (headerSize > maxHeaderTableSize) {
      int nameIndex = getNameIndex(name);
      encodeLiteral(out, name, value, HpackUtil.IndexType.NONE, nameIndex);
      return;
    }
    
    HeaderEntry headerField = getEntry(name, value);
    if (headerField != null) {
      int index = getIndex(index) + StaticTable.length;
      
      encodeInteger(out, 128, 7, index);
    } else {
      int staticTableIndex = StaticTable.getIndex(name, value);
      if (staticTableIndex != -1)
      {
        encodeInteger(out, 128, 7, staticTableIndex);
      } else {
        ensureCapacity(headerSize);
        encodeLiteral(out, name, value, HpackUtil.IndexType.INCREMENTAL, getNameIndex(name));
        add(name, value, headerSize);
      }
    }
  }
  

  public void setMaxHeaderTableSize(ByteBuf out, long maxHeaderTableSize)
    throws Http2Exception
  {
    if ((maxHeaderTableSize < 0L) || (maxHeaderTableSize > 4294967295L)) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", new Object[] { Long.valueOf(0L), Long.valueOf(4294967295L), Long.valueOf(maxHeaderTableSize) });
    }
    
    if (this.maxHeaderTableSize == maxHeaderTableSize) {
      return;
    }
    this.maxHeaderTableSize = maxHeaderTableSize;
    ensureCapacity(0L);
    
    encodeInteger(out, 32, 5, (int)maxHeaderTableSize);
  }
  


  public long getMaxHeaderTableSize()
  {
    return maxHeaderTableSize;
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
  


  private static void encodeInteger(ByteBuf out, int mask, int n, int i)
  {
    assert ((n >= 0) && (n <= 8)) : ("N: " + n);
    int nbits = 255 >>> 8 - n;
    if (i < nbits) {
      out.writeByte(mask | i);
    } else {
      out.writeByte(mask | nbits);
      int length = i - nbits;
      for (; (length & 0xFFFFFF80) != 0; length >>>= 7) {
        out.writeByte(length & 0x7F | 0x80);
      }
      out.writeByte(length);
    }
  }
  


  private void encodeStringLiteral(ByteBuf out, CharSequence string)
  {
    int huffmanLength = huffmanEncoder.getEncodedLength(string);
    if (huffmanLength < string.length()) {
      encodeInteger(out, 128, 7, huffmanLength);
      huffmanEncoder.encode(out, string);
    } else {
      encodeInteger(out, 0, 7, string.length());
      if ((string instanceof AsciiString))
      {
        AsciiString asciiString = (AsciiString)string;
        out.writeBytes(asciiString.array(), asciiString.arrayOffset(), asciiString.length());
      }
      else
      {
        out.writeCharSequence(string, CharsetUtil.ISO_8859_1);
      }
    }
  }
  



  private void encodeLiteral(ByteBuf out, CharSequence name, CharSequence value, HpackUtil.IndexType indexType, int nameIndex)
  {
    boolean nameIndexValid = nameIndex != -1;
    switch (1.$SwitchMap$io$netty$handler$codec$http2$internal$hpack$HpackUtil$IndexType[indexType.ordinal()]) {
    case 1: 
      encodeInteger(out, 64, 6, nameIndexValid ? nameIndex : 0);
      break;
    case 2: 
      encodeInteger(out, 0, 4, nameIndexValid ? nameIndex : 0);
      break;
    case 3: 
      encodeInteger(out, 16, 4, nameIndexValid ? nameIndex : 0);
      break;
    default: 
      throw new Error("should not reach here");
    }
    if (!nameIndexValid) {
      encodeStringLiteral(out, name);
    }
    encodeStringLiteral(out, value);
  }
  
  private int getNameIndex(CharSequence name) {
    int index = StaticTable.getIndex(name);
    if (index == -1) {
      index = getIndex(name);
      if (index >= 0) {
        index += StaticTable.length;
      }
    }
    return index;
  }
  



  private void ensureCapacity(long headerSize)
  {
    while (maxHeaderTableSize - size < headerSize) {
      int index = length();
      if (index == 0) {
        break;
      }
      remove();
    }
  }
  


  int length()
  {
    return size == 0L ? 0 : head.after.index - head.before.index + 1;
  }
  


  long size()
  {
    return size;
  }
  


  HeaderField getHeaderField(int index)
  {
    HeaderEntry entry = head;
    while (index-- >= 0) {
      entry = before;
    }
    return entry;
  }
  



  private HeaderEntry getEntry(CharSequence name, CharSequence value)
  {
    if ((length() == 0) || (name == null) || (value == null)) {
      return null;
    }
    int h = AsciiString.hashCode(name);
    int i = index(h);
    for (HeaderEntry e = headerFields[i]; e != null; e = next)
    {
      if ((hash == h) && ((HpackUtil.equalsConstantTime(name, name) & HpackUtil.equalsConstantTime(value, value)) != 0)) {
        return e;
      }
    }
    return null;
  }
  



  private int getIndex(CharSequence name)
  {
    if ((length() == 0) || (name == null)) {
      return -1;
    }
    int h = AsciiString.hashCode(name);
    int i = index(h);
    for (HeaderEntry e = headerFields[i]; e != null; e = next) {
      if ((hash == h) && (HpackUtil.equalsConstantTime(name, name) != 0)) {
        return getIndex(index);
      }
    }
    return -1;
  }
  


  private int getIndex(int index)
  {
    return index == -1 ? -1 : index - head.before.index + 1;
  }
  





  private void add(CharSequence name, CharSequence value, long headerSize)
  {
    if (headerSize > maxHeaderTableSize) {
      clear();
      return;
    }
    

    while (maxHeaderTableSize - size < headerSize) {
      remove();
    }
    
    int h = AsciiString.hashCode(name);
    int i = index(h);
    HeaderEntry old = headerFields[i];
    HeaderEntry e = new HeaderEntry(h, name, value, head.before.index - 1, old);
    headerFields[i] = e;
    e.addBefore(head);
    size += headerSize;
  }
  


  private HeaderField remove()
  {
    if (size == 0L) {
      return null;
    }
    HeaderEntry eldest = head.after;
    int h = hash;
    int i = index(h);
    HeaderEntry prev = headerFields[i];
    HeaderEntry e = prev;
    while (e != null) {
      HeaderEntry next = next;
      if (e == eldest) {
        if (prev == eldest) {
          headerFields[i] = next;
        } else {
          next = next;
        }
        eldest.remove();
        size -= eldest.size();
        return eldest;
      }
      prev = e;
      e = next;
    }
    return null;
  }
  


  private void clear()
  {
    Arrays.fill(headerFields, null);
    head.before = (head.after = head);
    size = 0L;
  }
  


  private int index(int h)
  {
    return h & hashMask;
  }
  


  private static class HeaderEntry
    extends HeaderField
  {
    HeaderEntry before;
    
    HeaderEntry after;
    
    HeaderEntry next;
    
    int hash;
    
    int index;
    

    HeaderEntry(int hash, CharSequence name, CharSequence value, int index, HeaderEntry next)
    {
      super(value);
      this.index = index;
      this.hash = hash;
      this.next = next;
    }
    


    private void remove()
    {
      before.after = after;
      after.before = before;
      before = null;
      after = null;
      next = null;
    }
    


    private void addBefore(HeaderEntry existingEntry)
    {
      after = existingEntry;
      before = before;
      before.after = this;
      after.before = this;
    }
  }
}
