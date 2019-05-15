package io.netty.handler.timeout;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;





















































public class ReadTimeoutHandler
  extends ChannelInboundHandlerAdapter
{
  private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
  

  private final long timeoutNanos;
  

  private volatile ScheduledFuture<?> timeout;
  

  private volatile long lastReadTime;
  
  private volatile int state;
  
  private boolean closed;
  

  public ReadTimeoutHandler(int timeoutSeconds)
  {
    this(timeoutSeconds, TimeUnit.SECONDS);
  }
  







  public ReadTimeoutHandler(long timeout, TimeUnit unit)
  {
    if (unit == null) {
      throw new NullPointerException("unit");
    }
    
    if (timeout <= 0L) {
      timeoutNanos = 0L;
    } else {
      timeoutNanos = Math.max(unit.toNanos(timeout), MIN_TIMEOUT_NANOS);
    }
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
    ctx.fireChannelRead(msg);
  }
  

  private void initialize(ChannelHandlerContext ctx)
  {
    switch (state) {
    case 1: 
    case 2: 
      return;
    }
    
    state = 1;
    
    lastReadTime = System.nanoTime();
    if (timeoutNanos > 0L) {
      timeout = ctx.executor().schedule(new ReadTimeoutTask(ctx), timeoutNanos, TimeUnit.NANOSECONDS);
    }
  }
  

  private void destroy()
  {
    state = 2;
    
    if (timeout != null) {
      timeout.cancel(false);
      timeout = null;
    }
  }
  

  protected void readTimedOut(ChannelHandlerContext ctx)
    throws Exception
  {
    if (!closed) {
      ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
      ctx.close();
      closed = true;
    }
  }
  
  private final class ReadTimeoutTask implements Runnable
  {
    private final ChannelHandlerContext ctx;
    
    ReadTimeoutTask(ChannelHandlerContext ctx) {
      this.ctx = ctx;
    }
    
    public void run()
    {
      if (!ctx.channel().isOpen()) {
        return;
      }
      
      long currentTime = System.nanoTime();
      long nextDelay = timeoutNanos - (currentTime - lastReadTime);
      if (nextDelay <= 0L)
      {
        timeout = ctx.executor().schedule(this, timeoutNanos, TimeUnit.NANOSECONDS);
        try {
          readTimedOut(ctx);
        } catch (Throwable t) {
          ctx.fireExceptionCaught(t);
        }
      }
      else {
        timeout = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
      }
    }
  }
}
