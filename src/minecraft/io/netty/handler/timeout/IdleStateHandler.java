package io.netty.handler.timeout;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;



















































































public class IdleStateHandler
  extends ChannelDuplexHandler
{
  private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
  
  private final long readerIdleTimeNanos;
  
  private final long writerIdleTimeNanos;
  private final long allIdleTimeNanos;
  volatile ScheduledFuture<?> readerIdleTimeout;
  volatile long lastReadTime;
  private boolean firstReaderIdleEvent = true;
  
  volatile ScheduledFuture<?> writerIdleTimeout;
  volatile long lastWriteTime;
  private boolean firstWriterIdleEvent = true;
  
  volatile ScheduledFuture<?> allIdleTimeout;
  private boolean firstAllIdleEvent = true;
  









  private volatile int state;
  










  public IdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds)
  {
    this(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
  }
  





















  public IdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit)
  {
    if (unit == null) {
      throw new NullPointerException("unit");
    }
    
    if (readerIdleTime <= 0L) {
      readerIdleTimeNanos = 0L;
    } else {
      readerIdleTimeNanos = Math.max(unit.toNanos(readerIdleTime), MIN_TIMEOUT_NANOS);
    }
    if (writerIdleTime <= 0L) {
      writerIdleTimeNanos = 0L;
    } else {
      writerIdleTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
    }
    if (allIdleTime <= 0L) {
      allIdleTimeNanos = 0L;
    } else {
      allIdleTimeNanos = Math.max(unit.toNanos(allIdleTime), MIN_TIMEOUT_NANOS);
    }
  }
  



  public long getReaderIdleTimeInMillis()
  {
    return TimeUnit.NANOSECONDS.toMillis(readerIdleTimeNanos);
  }
  



  public long getWriterIdleTimeInMillis()
  {
    return TimeUnit.NANOSECONDS.toMillis(writerIdleTimeNanos);
  }
  



  public long getAllIdleTimeInMillis()
  {
    return TimeUnit.NANOSECONDS.toMillis(allIdleTimeNanos);
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    if ((ctx.channel().isActive()) && (ctx.channel().isRegistered()))
    {

      initialize(ctx);
    }
  }
  


  public void handlerRemoved(ChannelHandlerContext ctx)
    throws Exception
  {
    destroy();
  }
  
  public void channelRegistered(ChannelHandlerContext ctx)
    throws Exception
  {
    if (ctx.channel().isActive()) {
      initialize(ctx);
    }
    super.channelRegistered(ctx);
  }
  


  public void channelActive(ChannelHandlerContext ctx)
    throws Exception
  {
    initialize(ctx);
    super.channelActive(ctx);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    destroy();
    super.channelInactive(ctx);
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
  {
    lastReadTime = System.nanoTime();
    firstReaderIdleEvent = (this.firstAllIdleEvent = 1);
    ctx.fireChannelRead(msg);
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
  {
    promise.addListener(new ChannelFutureListener()
    {
      public void operationComplete(ChannelFuture future) throws Exception {
        lastWriteTime = System.nanoTime();
        firstWriterIdleEvent = IdleStateHandler.access$102(IdleStateHandler.this, true);
      }
    });
    ctx.write(msg, promise);
  }
  

  private void initialize(ChannelHandlerContext ctx)
  {
    switch (state) {
    case 1: 
    case 2: 
      return;
    }
    
    state = 1;
    
    EventExecutor loop = ctx.executor();
    
    lastReadTime = (this.lastWriteTime = System.nanoTime());
    if (readerIdleTimeNanos > 0L) {
      readerIdleTimeout = loop.schedule(new ReaderIdleTimeoutTask(ctx), readerIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
    

    if (writerIdleTimeNanos > 0L) {
      writerIdleTimeout = loop.schedule(new WriterIdleTimeoutTask(ctx), writerIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
    

    if (allIdleTimeNanos > 0L) {
      allIdleTimeout = loop.schedule(new AllIdleTimeoutTask(ctx), allIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
  }
  

  private void destroy()
  {
    state = 2;
    
    if (readerIdleTimeout != null) {
      readerIdleTimeout.cancel(false);
      readerIdleTimeout = null;
    }
    if (writerIdleTimeout != null) {
      writerIdleTimeout.cancel(false);
      writerIdleTimeout = null;
    }
    if (allIdleTimeout != null) {
      allIdleTimeout.cancel(false);
      allIdleTimeout = null;
    }
  }
  


  protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt)
    throws Exception
  {
    ctx.fireUserEventTriggered(evt);
  }
  
  private final class ReaderIdleTimeoutTask implements Runnable
  {
    private final ChannelHandlerContext ctx;
    
    ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
      this.ctx = ctx;
    }
    
    public void run()
    {
      if (!ctx.channel().isOpen()) {
        return;
      }
      
      long currentTime = System.nanoTime();
      long lastReadTime = IdleStateHandler.this.lastReadTime;
      long nextDelay = readerIdleTimeNanos - (currentTime - lastReadTime);
      if (nextDelay <= 0L)
      {
        readerIdleTimeout = ctx.executor().schedule(this, readerIdleTimeNanos, TimeUnit.NANOSECONDS);
        try {
          IdleStateEvent event;
          IdleStateEvent event;
          if (firstReaderIdleEvent) {
            firstReaderIdleEvent = false;
            event = IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT;
          } else {
            event = IdleStateEvent.READER_IDLE_STATE_EVENT;
          }
          channelIdle(ctx, event);
        } catch (Throwable t) {
          ctx.fireExceptionCaught(t);
        }
      }
      else {
        readerIdleTimeout = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
      }
    }
  }
  
  private final class WriterIdleTimeoutTask implements Runnable
  {
    private final ChannelHandlerContext ctx;
    
    WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
      this.ctx = ctx;
    }
    
    public void run()
    {
      if (!ctx.channel().isOpen()) {
        return;
      }
      
      long currentTime = System.nanoTime();
      long lastWriteTime = IdleStateHandler.this.lastWriteTime;
      long nextDelay = writerIdleTimeNanos - (currentTime - lastWriteTime);
      if (nextDelay <= 0L)
      {
        writerIdleTimeout = ctx.executor().schedule(this, writerIdleTimeNanos, TimeUnit.NANOSECONDS);
        try {
          IdleStateEvent event;
          IdleStateEvent event;
          if (firstWriterIdleEvent) {
            firstWriterIdleEvent = false;
            event = IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT;
          } else {
            event = IdleStateEvent.WRITER_IDLE_STATE_EVENT;
          }
          channelIdle(ctx, event);
        } catch (Throwable t) {
          ctx.fireExceptionCaught(t);
        }
      }
      else {
        writerIdleTimeout = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
      }
    }
  }
  
  private final class AllIdleTimeoutTask implements Runnable
  {
    private final ChannelHandlerContext ctx;
    
    AllIdleTimeoutTask(ChannelHandlerContext ctx) {
      this.ctx = ctx;
    }
    
    public void run()
    {
      if (!ctx.channel().isOpen()) {
        return;
      }
      
      long currentTime = System.nanoTime();
      long lastIoTime = Math.max(lastReadTime, lastWriteTime);
      long nextDelay = allIdleTimeNanos - (currentTime - lastIoTime);
      if (nextDelay <= 0L)
      {

        allIdleTimeout = ctx.executor().schedule(this, allIdleTimeNanos, TimeUnit.NANOSECONDS);
        try {
          IdleStateEvent event;
          IdleStateEvent event;
          if (firstAllIdleEvent) {
            firstAllIdleEvent = false;
            event = IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT;
          } else {
            event = IdleStateEvent.ALL_IDLE_STATE_EVENT;
          }
          channelIdle(ctx, event);
        } catch (Throwable t) {
          ctx.fireExceptionCaught(t);
        }
      }
      else
      {
        allIdleTimeout = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
      }
    }
  }
}
