package io.netty.handler.codec.http2;

public abstract interface Http2StreamStateEvent
{
  public abstract int streamId();
}
