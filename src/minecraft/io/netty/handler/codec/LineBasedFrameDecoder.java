package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;































public class LineBasedFrameDecoder
  extends ByteToMessageDecoder
{
  private final int maxLength;
  private final boolean failFast;
  private final boolean stripDelimiter;
  private boolean discarding;
  private int discardedBytes;
  
  public LineBasedFrameDecoder(int maxLength)
  {
    this(maxLength, true, false);
  }
  














  public LineBasedFrameDecoder(int maxLength, boolean stripDelimiter, boolean failFast)
  {
    this.maxLength = maxLength;
    this.failFast = failFast;
    this.stripDelimiter = stripDelimiter;
  }
  
  protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    Object decoded = decode(ctx, in);
    if (decoded != null) {
      out.add(decoded);
    }
  }
  






  protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer)
    throws Exception
  {
    int eol = findEndOfLine(buffer);
    if (!discarding) {
      if (eol >= 0)
      {
        int length = eol - buffer.readerIndex();
        int delimLength = buffer.getByte(eol) == 13 ? 2 : 1;
        
        if (length > maxLength) {
          buffer.readerIndex(eol + delimLength);
          fail(ctx, length);
          return null;
        }
        ByteBuf frame;
        if (stripDelimiter) {
          ByteBuf frame = buffer.readSlice(length);
          buffer.skipBytes(delimLength);
        } else {
          frame = buffer.readSlice(length + delimLength);
        }
        
        return frame.retain();
      }
      int length = buffer.readableBytes();
      if (length > maxLength) {
        discardedBytes = length;
        buffer.readerIndex(buffer.writerIndex());
        discarding = true;
        if (failFast) {
          fail(ctx, "over " + discardedBytes);
        }
      }
      return null;
    }
    
    if (eol >= 0) {
      int length = discardedBytes + eol - buffer.readerIndex();
      int delimLength = buffer.getByte(eol) == 13 ? 2 : 1;
      buffer.readerIndex(eol + delimLength);
      discardedBytes = 0;
      discarding = false;
      if (!failFast) {
        fail(ctx, length);
      }
    } else {
      discardedBytes = buffer.readableBytes();
      buffer.readerIndex(buffer.writerIndex());
    }
    return null;
  }
  
  private void fail(ChannelHandlerContext ctx, int length)
  {
    fail(ctx, String.valueOf(length));
  }
  
  private void fail(ChannelHandlerContext ctx, String length) {
    ctx.fireExceptionCaught(new TooLongFrameException("frame length (" + length + ") exceeds the allowed maximum (" + maxLength + ')'));
  }
  





  private static int findEndOfLine(ByteBuf buffer)
  {
    int n = buffer.writerIndex();
    for (int i = buffer.readerIndex(); i < n; i++) {
      byte b = buffer.getByte(i);
      if (b == 10)
        return i;
      if ((b == 13) && (i < n - 1) && (buffer.getByte(i + 1) == 10)) {
        return i;
      }
    }
    return -1;
  }
}
