package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.io.InputStream;
import java.io.PushbackInputStream;




























public class ChunkedStream
  implements ChunkedInput<ByteBuf>
{
  static final int DEFAULT_CHUNK_SIZE = 8192;
  private final PushbackInputStream in;
  private final int chunkSize;
  private long offset;
  
  public ChunkedStream(InputStream in)
  {
    this(in, 8192);
  }
  





  public ChunkedStream(InputStream in, int chunkSize)
  {
    if (in == null) {
      throw new NullPointerException("in");
    }
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
    }
    


    if ((in instanceof PushbackInputStream)) {
      this.in = ((PushbackInputStream)in);
    } else {
      this.in = new PushbackInputStream(in);
    }
    this.chunkSize = chunkSize;
  }
  


  public long transferredBytes()
  {
    return offset;
  }
  
  public boolean isEndOfInput() throws Exception
  {
    int b = in.read();
    if (b < 0) {
      return true;
    }
    in.unread(b);
    return false;
  }
  
  public void close()
    throws Exception
  {
    in.close();
  }
  
  public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception
  {
    if (isEndOfInput()) {
      return null;
    }
    
    int availableBytes = in.available();
    int chunkSize;
    int chunkSize; if (availableBytes <= 0) {
      chunkSize = this.chunkSize;
    } else {
      chunkSize = Math.min(this.chunkSize, in.available());
    }
    
    boolean release = true;
    ByteBuf buffer = ctx.alloc().buffer(chunkSize);
    try
    {
      offset += buffer.writeBytes(in, chunkSize);
      release = false;
      return buffer;
    } finally {
      if (release) {
        buffer.release();
      }
    }
  }
}
