package io.netty.handler.codec.http2;


public abstract interface Http2Stream
{
  public abstract int id();
  

  public abstract State state();
  

  public abstract Http2Stream open(boolean paramBoolean)
    throws Http2Exception;
  

  public abstract Http2Stream close();
  
  public abstract Http2Stream closeLocalSide();
  
  public abstract Http2Stream closeRemoteSide();
  
  public abstract boolean isResetSent();
  
  public abstract Http2Stream resetSent();
  
  public abstract <V> V setProperty(Http2Connection.PropertyKey paramPropertyKey, V paramV);
  
  public static enum State
  {
    IDLE(false, false), 
    RESERVED_LOCAL(false, false), 
    RESERVED_REMOTE(false, false), 
    OPEN(true, true), 
    HALF_CLOSED_LOCAL(false, true), 
    HALF_CLOSED_REMOTE(true, false), 
    CLOSED(false, false);
    
    private final boolean localSideOpen;
    private final boolean remoteSideOpen;
    
    private State(boolean localSideOpen, boolean remoteSideOpen) {
      this.localSideOpen = localSideOpen;
      this.remoteSideOpen = remoteSideOpen;
    }
    



    public boolean localSideOpen()
    {
      return localSideOpen;
    }
    



    public boolean remoteSideOpen()
    {
      return remoteSideOpen;
    }
  }
  
  public abstract <V> V getProperty(Http2Connection.PropertyKey paramPropertyKey);
  
  public abstract <V> V removeProperty(Http2Connection.PropertyKey paramPropertyKey);
  
  public abstract Http2Stream setPriority(int paramInt, short paramShort, boolean paramBoolean)
    throws Http2Exception;
  
  public abstract boolean isRoot();
  
  public abstract boolean isLeaf();
  
  public abstract short weight();
  
  public abstract Http2Stream parent();
  
  public abstract boolean isDescendantOf(Http2Stream paramHttp2Stream);
  
  public abstract int numChildren();
  
  public abstract Http2Stream forEachChild(Http2StreamVisitor paramHttp2StreamVisitor)
    throws Http2Exception;
}
