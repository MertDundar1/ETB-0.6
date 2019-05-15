package io.netty.handler.codec.http2;

public abstract interface Http2HeaderTable
{
  public abstract void maxHeaderTableSize(long paramLong)
    throws Http2Exception;
  
  public abstract long maxHeaderTableSize();
  
  public abstract void maxHeaderListSize(long paramLong)
    throws Http2Exception;
  
  public abstract long maxHeaderListSize();
}
