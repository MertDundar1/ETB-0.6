package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

















public final class ImmediateEventExecutor
  extends AbstractEventExecutor
{
  public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
  
  private final Future<?> terminationFuture = new FailedFuture(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());
  


  private ImmediateEventExecutor() {}
  

  public EventExecutorGroup parent()
  {
    return null;
  }
  
  public boolean inEventLoop()
  {
    return true;
  }
  
  public boolean inEventLoop(Thread thread)
  {
    return true;
  }
  
  public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit)
  {
    return terminationFuture();
  }
  
  public Future<?> terminationFuture()
  {
    return terminationFuture;
  }
  

  @Deprecated
  public void shutdown() {}
  
  public boolean isShuttingDown()
  {
    return false;
  }
  
  public boolean isShutdown()
  {
    return false;
  }
  
  public boolean isTerminated()
  {
    return false;
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit)
  {
    return false;
  }
  
  public void execute(Runnable command)
  {
    if (command == null) {
      throw new NullPointerException("command");
    }
    command.run();
  }
  
  public <V> Promise<V> newPromise()
  {
    return new ImmediatePromise(this);
  }
  
  public <V> ProgressivePromise<V> newProgressivePromise()
  {
    return new ImmediateProgressivePromise(this);
  }
  
  static class ImmediatePromise<V> extends DefaultPromise<V> {
    ImmediatePromise(EventExecutor executor) {
      super();
    }
    
    protected void checkDeadLock() {}
  }
  
  static class ImmediateProgressivePromise<V>
    extends DefaultProgressivePromise<V>
  {
    ImmediateProgressivePromise(EventExecutor executor)
    {
      super();
    }
    
    protected void checkDeadLock() {}
  }
}
