package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.CompressionException;
















class SpdyHeaderBlockJZlibEncoder
  extends SpdyHeaderBlockRawEncoder
{
  private final Deflater z = new Deflater();
  
  private boolean finished;
  
  SpdyHeaderBlockJZlibEncoder(SpdyVersion version, int compressionLevel, int windowBits, int memLevel)
  {
    super(version);
    if ((compressionLevel < 0) || (compressionLevel > 9)) {
      throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
    }
    
    if ((windowBits < 9) || (windowBits > 15)) {
      throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
    }
    
    if ((memLevel < 1) || (memLevel > 9)) {
      throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
    }
    

    int resultCode = z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
    
    if (resultCode != 0) {
      throw new CompressionException("failed to initialize an SPDY header block deflater: " + resultCode);
    }
    
    resultCode = z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
    if (resultCode != 0) {
      throw new CompressionException("failed to set the SPDY dictionary: " + resultCode);
    }
  }
  

  private void setInput(ByteBuf decompressed)
  {
    byte[] in = new byte[decompressed.readableBytes()];
    decompressed.readBytes(in);
    z.next_in = in;
    z.next_in_index = 0;
    z.avail_in = in.length;
  }
  
  private void encode(ByteBuf compressed) {
    try {
      byte[] out = new byte[(int)Math.ceil(z.next_in.length * 1.001D) + 12];
      z.next_out = out;
      z.next_out_index = 0;
      z.avail_out = out.length;
      
      int resultCode = z.deflate(2);
      if (resultCode != 0) {
        throw new CompressionException("compression failure: " + resultCode);
      }
      
      if (z.next_out_index != 0) {
        compressed.writeBytes(out, 0, z.next_out_index);
      }
      

    }
    finally
    {
      z.next_in = null;
      z.next_out = null;
    }
  }
  
  public ByteBuf encode(SpdyHeadersFrame frame) throws Exception
  {
    if (frame == null) {
      throw new IllegalArgumentException("frame");
    }
    
    if (finished) {
      return Unpooled.EMPTY_BUFFER;
    }
    
    ByteBuf decompressed = super.encode(frame);
    if (decompressed.readableBytes() == 0) {
      return Unpooled.EMPTY_BUFFER;
    }
    
    ByteBuf compressed = decompressed.alloc().buffer();
    setInput(decompressed);
    encode(compressed);
    return compressed;
  }
  
  public void end()
  {
    if (finished) {
      return;
    }
    finished = true;
    z.deflateEnd();
    z.next_in = null;
    z.next_out = null;
  }
}
