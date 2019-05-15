package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;




















public class JdkZlibDecoder
  extends ZlibDecoder
{
  private static final int FHCRC = 2;
  private static final int FEXTRA = 4;
  private static final int FNAME = 8;
  private static final int FCOMMENT = 16;
  private static final int FRESERVED = 224;
  private Inflater inflater;
  private final byte[] dictionary;
  private final CRC32 crc;
  
  private static enum GzipState
  {
    HEADER_START, 
    HEADER_END, 
    FLG_READ, 
    XLEN_READ, 
    SKIP_FNAME, 
    SKIP_COMMENT, 
    PROCESS_FHCRC, 
    FOOTER_START;
    
    private GzipState() {} }
  private GzipState gzipState = GzipState.HEADER_START;
  private int flags = -1;
  private int xlen = -1;
  

  private volatile boolean finished;
  
  private boolean decideZlibOrNone;
  

  public JdkZlibDecoder()
  {
    this(ZlibWrapper.ZLIB, null);
  }
  




  public JdkZlibDecoder(byte[] dictionary)
  {
    this(ZlibWrapper.ZLIB, dictionary);
  }
  




  public JdkZlibDecoder(ZlibWrapper wrapper)
  {
    this(wrapper, null);
  }
  
  private JdkZlibDecoder(ZlibWrapper wrapper, byte[] dictionary) {
    if (wrapper == null) {
      throw new NullPointerException("wrapper");
    }
    switch (1.$SwitchMap$io$netty$handler$codec$compression$ZlibWrapper[wrapper.ordinal()]) {
    case 1: 
      inflater = new Inflater(true);
      crc = new CRC32();
      break;
    case 2: 
      inflater = new Inflater(true);
      crc = null;
      break;
    case 3: 
      inflater = new Inflater();
      crc = null;
      break;
    
    case 4: 
      decideZlibOrNone = true;
      crc = null;
      break;
    default: 
      throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + wrapper);
    }
    this.dictionary = dictionary;
  }
  
  public boolean isClosed()
  {
    return finished;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    if (finished)
    {
      in.skipBytes(in.readableBytes());
      return;
    }
    
    if (!in.isReadable()) {
      return;
    }
    
    if (decideZlibOrNone)
    {
      if (in.readableBytes() < 2) {
        return;
      }
      
      boolean nowrap = !looksLikeZlib(in.getShort(0));
      inflater = new Inflater(nowrap);
      decideZlibOrNone = false;
    }
    
    if (crc != null) {
      switch (gzipState) {
      case FOOTER_START: 
        if (readGZIPFooter(in)) {
          finished = true;
        }
        return;
      }
      if ((gzipState != GzipState.HEADER_END) && 
        (!readGZIPHeader(in))) {
        return;
      }
    }
    


    int readableBytes = in.readableBytes();
    if (in.hasArray()) {
      inflater.setInput(in.array(), in.arrayOffset() + in.readerIndex(), in.readableBytes());
    } else {
      byte[] array = new byte[in.readableBytes()];
      in.getBytes(in.readerIndex(), array);
      inflater.setInput(array);
    }
    
    int maxOutputLength = inflater.getRemaining() << 1;
    ByteBuf decompressed = ctx.alloc().heapBuffer(maxOutputLength);
    try {
      boolean readFooter = false;
      byte[] outArray = decompressed.array();
      while (!inflater.needsInput()) {
        int writerIndex = decompressed.writerIndex();
        int outIndex = decompressed.arrayOffset() + writerIndex;
        int length = decompressed.writableBytes();
        
        if (length == 0)
        {
          out.add(decompressed);
          decompressed = ctx.alloc().heapBuffer(maxOutputLength);
          outArray = decompressed.array();
        }
        else
        {
          int outputLength = inflater.inflate(outArray, outIndex, length);
          if (outputLength > 0) {
            decompressed.writerIndex(writerIndex + outputLength);
            if (crc != null) {
              crc.update(outArray, outIndex, outputLength);
            }
          }
          else if (inflater.needsDictionary()) {
            if (dictionary == null) {
              throw new DecompressionException("decompression failure, unable to set dictionary as non was specified");
            }
            
            inflater.setDictionary(dictionary);
          }
          

          if (inflater.finished()) {
            if (crc == null) {
              finished = true; break;
            }
            readFooter = true;
            
            break;
          }
        }
      }
      in.skipBytes(readableBytes - inflater.getRemaining());
      
      if (readFooter) {
        gzipState = GzipState.FOOTER_START;
        if (readGZIPFooter(in)) {
          finished = true;
        }
      }
    } catch (DataFormatException e) {
      throw new DecompressionException("decompression failure", e);
    }
    finally {
      if (decompressed.isReadable()) {
        out.add(decompressed);
      } else {
        decompressed.release();
      }
    }
  }
  
  protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception
  {
    super.handlerRemoved0(ctx);
    if (inflater != null) {
      inflater.end();
    }
  }
  
  private boolean readGZIPHeader(ByteBuf in) {
    switch (1.$SwitchMap$io$netty$handler$codec$compression$JdkZlibDecoder$GzipState[gzipState.ordinal()]) {
    case 2: 
      if (in.readableBytes() < 10) {
        return false;
      }
      
      int magic0 = in.readByte();
      int magic1 = in.readByte();
      
      if (magic0 != 31) {
        throw new DecompressionException("Input is not in the GZIP format");
      }
      crc.update(magic0);
      crc.update(magic1);
      
      int method = in.readUnsignedByte();
      if (method != 8) {
        throw new DecompressionException("Unsupported compression method " + method + " in the GZIP header");
      }
      
      crc.update(method);
      
      flags = in.readUnsignedByte();
      crc.update(flags);
      
      if ((flags & 0xE0) != 0) {
        throw new DecompressionException("Reserved flags are set in the GZIP header");
      }
      


      crc.update(in.readByte());
      crc.update(in.readByte());
      crc.update(in.readByte());
      crc.update(in.readByte());
      
      crc.update(in.readUnsignedByte());
      crc.update(in.readUnsignedByte());
      
      gzipState = GzipState.FLG_READ;
    case 3: 
      if ((flags & 0x4) != 0) {
        if (in.readableBytes() < 2) {
          return false;
        }
        int xlen1 = in.readUnsignedByte();
        int xlen2 = in.readUnsignedByte();
        crc.update(xlen1);
        crc.update(xlen2);
        
        xlen |= xlen1 << 8 | xlen2;
      }
      gzipState = GzipState.XLEN_READ;
    case 4: 
      if (xlen != -1) {
        if (in.readableBytes() < xlen) {
          return false;
        }
        byte[] xtra = new byte[xlen];
        in.readBytes(xtra);
        crc.update(xtra);
      }
      gzipState = GzipState.SKIP_FNAME;
    case 5: 
      if ((flags & 0x8) != 0) {
        if (!in.isReadable())
          return false;
        int b;
        do {
          b = in.readUnsignedByte();
          crc.update(b);
        } while ((b != 0) && 
        

          (in.isReadable()));
      }
      gzipState = GzipState.SKIP_COMMENT;
    case 6: 
      if ((flags & 0x10) != 0) {
        if (!in.isReadable())
          return false;
        int b;
        do {
          b = in.readUnsignedByte();
          crc.update(b);
        } while ((b != 0) && 
        

          (in.isReadable()));
      }
      gzipState = GzipState.PROCESS_FHCRC;
    case 7: 
      if ((flags & 0x2) != 0) {
        if (in.readableBytes() < 4) {
          return false;
        }
        verifyCrc(in);
      }
      crc.reset();
      gzipState = GzipState.HEADER_END;
    case 8: 
      return true;
    }
    throw new IllegalStateException();
  }
  
  private boolean readGZIPFooter(ByteBuf buf)
  {
    if (buf.readableBytes() < 8) {
      return false;
    }
    
    verifyCrc(buf);
    

    int dataLength = 0;
    for (int i = 0; i < 4; i++) {
      dataLength |= buf.readUnsignedByte() << i * 8;
    }
    int readLength = inflater.getTotalOut();
    if (dataLength != readLength) {
      throw new DecompressionException("Number of bytes mismatch. Expected: " + dataLength + ", Got: " + readLength);
    }
    
    return true;
  }
  
  private void verifyCrc(ByteBuf in) {
    long crcValue = 0L;
    for (int i = 0; i < 4; i++) {
      crcValue |= in.readUnsignedByte() << i * 8;
    }
    long readCrc = crc.getValue();
    if (crcValue != readCrc) {
      throw new DecompressionException("CRC value missmatch. Expected: " + crcValue + ", Got: " + readCrc);
    }
  }
  







  private static boolean looksLikeZlib(short cmf_flg)
  {
    return ((cmf_flg & 0x7800) == 30720) && (cmf_flg % 31 == 0);
  }
}
