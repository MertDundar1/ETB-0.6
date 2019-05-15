package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;



















public class ThreadPerChannelEventLoopGroup
  extends AbstractEventExecutorGroup
  implements EventLoopGroup
{
  private final Object[] childArgs;
  private final int maxChannels;
  final ThreadFactory threadFactory;
  final Set<ThreadPerChannelEventLoop> activeChildren = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
  
  final Queue<ThreadPerChannelEventLoop> idleChildren = new ConcurrentLinkedQueue();
  
  private final ChannelException tooManyChannels;
  private volatile boolean shuttingDown;
  private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
  private final FutureListener<Object> childTerminationListener = new FutureListener()
  {
    public void operationComplete(Future<Object> future) throws Exception
    {
      if (isTerminated()) {
        terminationFuture.trySuccess(null);
      }
    }
  };
  


  protected ThreadPerChannelEventLoopGroup()
  {
    this(0);
  }
  








  protected ThreadPerChannelEventLoopGroup(int maxChannels)
  {
    this(maxChannels, Executors.defaultThreadFactory(), new Object[0]);
  }
  











  protected ThreadPerChannelEventLoopGroup(int maxChannels, ThreadFactory threadFactory, Object... args)
  {
    if (maxChannels < 0) {
      throw new IllegalArgumentException(String.format("maxChannels: %d (expected: >= 0)", new Object[] { Integer.valueOf(maxChannels) }));
    }
    
    if (threadFactory == null) {
      throw new NullPointerException("threadFactory");
    }
    
    if (args == null) {
      childArgs = EmptyArrays.EMPTY_OBJECTS;
    } else {
      childArgs = ((Object[])args.clone());
    }
    
    this.maxChannels = maxChannels;
    this.threadFactory = threadFactory;
    
    tooManyChannels = new ChannelException("too many channels (max: " + maxChannels + ')');
    tooManyChannels.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
  }
  


  protected ThreadPerChannelEventLoop newChild(Object... args)
    throws Exception
  {
    return new ThreadPerChannelEventLoop(this);
  }
  
  public Iterator<EventExecutor> iterator()
  {
    return new ReadOnlyIterator(activeChildren.iterator());
  }
  
  public EventLoop next()
  {
    throw new UnsupportedOperationException();
  }
  
  public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit)
  {
    shuttingDown = true;
    
    for (EventLoop l : activeChildren) {
      l.shutdownGracefully(quietPeriod, timeout, unit);
    }
    for (EventLoop l : idleChildren) {
      l.shutdownGracefully(quietPeriod, timeout, unit);
    }
    

    if (isTerminated()) {
      terminationFuture.trySuccess(null);
    }
    
    return terminationFuture();
  }
  
  public Future<?> terminationFuture()
  {
    return terminationFuture;
  }
  
  @Deprecated
  public void shutdown()
  {
    shuttingDown = true;
    
    for (EventLoop l : activeChildren) {
      l.shutdown();
    }
    for (EventLoop l : idleChildren) {
      l.shutdown();
    }
    

    if (isTerminated()) {
      terminationFuture.trySuccess(null);
    }
  }
  
  public boolean isShuttingDown()
  {
    for (EventLoop l : activeChildren) {
      if (!l.isShuttingDown()) {
        return false;
      }
    }
    for (EventLoop l : idleChildren) {
      if (!l.isShuttingDown()) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isShutdown()
  {
    for (EventLoop l : activeChildren) {
      if (!l.isShutdown()) {
        return false;
      }
    }
    for (EventLoop l : idleChildren) {
      if (!l.isShutdown()) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isTerminated()
  {
    for (EventLoop l : activeChildren) {
      if (!l.isTerminated()) {
        return false;
      }
    }
    for (EventLoop l : idleChildren) {
      if (!l.isTerminated()) {
        return false;
      }
    }
    return true;
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit)
    throws InterruptedException
  {
    long deadline = System.nanoTime() + unit.toNanos(timeout);
    for (EventLoop l : activeChildren) {
      for (;;) {
        long timeLeft = deadline - System.nanoTime();
        if (timeLeft <= 0L) {
          return isTerminated();
        }
        if (l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
          break;
        }
      }
    }
    for (EventLoop l : idleChildren) {
      for (;;) {
        long timeLeft = deadline - System.nanoTime();
        if (timeLeft <= 0L) {
          return isTerminated();
        }
        if (l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
          break;
        }
      }
    }
    return isTerminated();
  }
  
  public ChannelFuture register(Channel channel)
  {
    if (channel == null) {
      throw new NullPointerException("channel");
    }
    try {
      EventLoop l = nextChild();
      return l.register(channel, new DefaultChannelPromise(channel, l));
    } catch (Throwable t) {
      return new FailedChannelFuture(channel, GlobalEventExecutor.INSTANCE, t);
    }
  }
  
  public ChannelFuture register(Channel channel, ChannelPromise promise)
  {
    if (channel == null) {
      throw new NullPointerException("channel");
    }
    try {
      return nextChild().register(channel, promise);
    } catch (Throwable t) {
      promise.setFailure(t); }
    return promise;
  }
  
  private EventLoop nextChild() throws Exception
  {
    if (shuttingDown) {
      throw new RejectedExecutionException("shutting down");
    }
    
    ThreadPerChannelEventLoop loop = (ThreadPerChannelEventLoop)idleChildren.poll();
    if (loop == null) {
      if ((maxChannels > 0) && (activeChildren.size() >= maxChannels)) {
        throw tooManyChannels;
      }
      loop = newChild(childArgs);
      loop.terminationFuture().addListener(childTerminationListener);
    }
    activeChildren.add(loop);
    return loop;
  }
}
