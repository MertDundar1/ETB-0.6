package io.netty.handler.codec.spdy;








public abstract class DefaultSpdyStreamFrame
  implements SpdyStreamFrame
{
  private int streamId;
  






  private boolean last;
  







  protected DefaultSpdyStreamFrame(int streamId)
  {
    setStreamId(streamId);
  }
  
  public int streamId()
  {
    return streamId;
  }
  
  public SpdyStreamFrame setStreamId(int streamId)
  {
    if (streamId <= 0) {
      throw new IllegalArgumentException("Stream-ID must be positive: " + streamId);
    }
    
    this.streamId = streamId;
    return this;
  }
  
  public boolean isLast()
  {
    return last;
  }
  
  public SpdyStreamFrame setLast(boolean last)
  {
    this.last = last;
    return this;
  }
}
