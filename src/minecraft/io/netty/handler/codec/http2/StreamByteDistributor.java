package io.netty.handler.codec.http2;

public abstract interface StreamByteDistributor
{
  public abstract void updateStreamableBytes(StreamState paramStreamState);
  
  public abstract boolean distribute(int paramInt, Writer paramWriter)
    throws Http2Exception;
  
  public static abstract interface Writer
  {
    public abstract void write(Http2Stream paramHttp2Stream, int paramInt);
  }
  
  public static abstract interface StreamState
  {
    public abstract Http2Stream stream();
    
    public abstract int pendingBytes();
    
    public abstract boolean hasFrame();
    
    public abstract int windowSize();
  }
}
