package io.netty.handler.codec.http2;

public abstract interface Http2HeadersFrame
  extends Http2StreamFrame
{
  public abstract Http2Headers headers();
  
  public abstract boolean isEndStream();
  
  public abstract int padding();
}
