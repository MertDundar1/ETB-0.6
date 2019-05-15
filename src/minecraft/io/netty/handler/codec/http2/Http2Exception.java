package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;





















public class Http2Exception
  extends Exception
{
  private static final long serialVersionUID = -6941186345430164209L;
  private final Http2Error error;
  private final ShutdownHint shutdownHint;
  
  public Http2Exception(Http2Error error)
  {
    this(error, ShutdownHint.HARD_SHUTDOWN);
  }
  
  public Http2Exception(Http2Error error, ShutdownHint shutdownHint) {
    this.error = ((Http2Error)ObjectUtil.checkNotNull(error, "error"));
    this.shutdownHint = ((ShutdownHint)ObjectUtil.checkNotNull(shutdownHint, "shutdownHint"));
  }
  
  public Http2Exception(Http2Error error, String message) {
    this(error, message, ShutdownHint.HARD_SHUTDOWN);
  }
  
  public Http2Exception(Http2Error error, String message, ShutdownHint shutdownHint) {
    super(message);
    this.error = ((Http2Error)ObjectUtil.checkNotNull(error, "error"));
    this.shutdownHint = ((ShutdownHint)ObjectUtil.checkNotNull(shutdownHint, "shutdownHint"));
  }
  
  public Http2Exception(Http2Error error, String message, Throwable cause) {
    this(error, message, cause, ShutdownHint.HARD_SHUTDOWN);
  }
  
  public Http2Exception(Http2Error error, String message, Throwable cause, ShutdownHint shutdownHint) {
    super(message, cause);
    this.error = ((Http2Error)ObjectUtil.checkNotNull(error, "error"));
    this.shutdownHint = ((ShutdownHint)ObjectUtil.checkNotNull(shutdownHint, "shutdownHint"));
  }
  
  public Http2Error error() {
    return error;
  }
  


  public ShutdownHint shutdownHint()
  {
    return shutdownHint;
  }
  







  public static Http2Exception connectionError(Http2Error error, String fmt, Object... args)
  {
    return new Http2Exception(error, String.format(fmt, args));
  }
  









  public static Http2Exception connectionError(Http2Error error, Throwable cause, String fmt, Object... args)
  {
    return new Http2Exception(error, String.format(fmt, args), cause);
  }
  







  public static Http2Exception closedStreamError(Http2Error error, String fmt, Object... args)
  {
    return new ClosedStreamCreationException(error, String.format(fmt, args));
  }
  











  public static Http2Exception streamError(int id, Http2Error error, String fmt, Object... args)
  {
    return 0 == id ? connectionError(error, fmt, args) : new StreamException(id, error, String.format(fmt, args));
  }
  















  public static Http2Exception streamError(int id, Http2Error error, Throwable cause, String fmt, Object... args)
  {
    return 0 == id ? connectionError(error, cause, fmt, args) : new StreamException(id, error, String.format(fmt, args), cause);
  }
  







  public static boolean isStreamError(Http2Exception e)
  {
    return e instanceof StreamException;
  }
  





  public static int streamId(Http2Exception e)
  {
    return isStreamError(e) ? ((StreamException)e).streamId() : 0;
  }
  





  public static enum ShutdownHint
  {
    NO_SHUTDOWN, 
    



    GRACEFUL_SHUTDOWN, 
    


    HARD_SHUTDOWN;
    
    private ShutdownHint() {}
  }
  
  public static final class ClosedStreamCreationException extends Http2Exception
  {
    private static final long serialVersionUID = -6746542974372246206L;
    
    public ClosedStreamCreationException(Http2Error error) {
      super();
    }
    
    public ClosedStreamCreationException(Http2Error error, String message) {
      super(message);
    }
    
    public ClosedStreamCreationException(Http2Error error, String message, Throwable cause) {
      super(message, cause);
    }
  }
  
  public static final class StreamException
    extends Http2Exception
  {
    private static final long serialVersionUID = 602472544416984384L;
    private final int streamId;
    
    StreamException(int streamId, Http2Error error, String message)
    {
      super(message, Http2Exception.ShutdownHint.NO_SHUTDOWN);
      this.streamId = streamId;
    }
    
    StreamException(int streamId, Http2Error error, String message, Throwable cause) {
      super(message, cause, Http2Exception.ShutdownHint.NO_SHUTDOWN);
      this.streamId = streamId;
    }
    
    public int streamId() {
      return streamId;
    }
  }
  
  public static final class CompositeStreamException
    extends Http2Exception implements Iterable<Http2Exception.StreamException>
  {
    private static final long serialVersionUID = 7091134858213711015L;
    private final List<Http2Exception.StreamException> exceptions;
    
    public CompositeStreamException(Http2Error error, int initialCapacity)
    {
      super(Http2Exception.ShutdownHint.NO_SHUTDOWN);
      exceptions = new ArrayList(initialCapacity);
    }
    
    public void add(Http2Exception.StreamException e) {
      exceptions.add(e);
    }
    
    public Iterator<Http2Exception.StreamException> iterator()
    {
      return exceptions.iterator();
    }
  }
}
