package io.netty.handler.codec.http2;

public abstract interface Http2StreamFrame
  extends Http2Frame
{
  public abstract Http2StreamFrame streamId(int paramInt);
  
  public abstract int streamId();
}
