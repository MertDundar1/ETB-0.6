package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;









































public class ChannelTrafficShapingHandler
  extends AbstractTrafficShapingHandler
{
  private List<ToSend> messagesQueue = new LinkedList();
  













  public ChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime)
  {
    super(writeLimit, readLimit, checkInterval, maxTime);
  }
  











  public ChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval)
  {
    super(writeLimit, readLimit, checkInterval);
  }
  








  public ChannelTrafficShapingHandler(long writeLimit, long readLimit)
  {
    super(writeLimit, readLimit);
  }
  






  public ChannelTrafficShapingHandler(long checkInterval)
  {
    super(checkInterval);
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    TrafficCounter trafficCounter = new TrafficCounter(this, ctx.executor(), "ChannelTC" + ctx.channel().hashCode(), checkInterval);
    
    setTrafficCounter(trafficCounter);
    trafficCounter.start();
  }
  
  public synchronized void handlerRemoved(ChannelHandlerContext ctx) throws Exception
  {
    if (trafficCounter != null) {
      trafficCounter.stop();
    }
    for (ToSend toSend : messagesQueue) {
      if ((toSend instanceof ByteBuf)) {
        ((ByteBuf)toSend).release();
      }
    }
    messagesQueue.clear();
  }
  
  private static final class ToSend {
    final long date;
    final Object toSend;
    final ChannelPromise promise;
    
    private ToSend(long delay, Object toSend, ChannelPromise promise) {
      date = (System.currentTimeMillis() + delay);
      this.toSend = toSend;
      this.promise = promise;
    }
  }
  

  protected synchronized void submitWrite(final ChannelHandlerContext ctx, Object msg, long delay, ChannelPromise promise)
  {
    if ((delay == 0L) && (messagesQueue.isEmpty())) {
      ctx.write(msg, promise);
      return;
    }
    ToSend newToSend = new ToSend(delay, msg, promise, null);
    messagesQueue.add(newToSend);
    ctx.executor().schedule(new Runnable()
    {

      public void run() { ChannelTrafficShapingHandler.this.sendAllValid(ctx); } }, delay, TimeUnit.MILLISECONDS);
  }
  

  private synchronized void sendAllValid(ChannelHandlerContext ctx)
  {
    while (!messagesQueue.isEmpty()) {
      ToSend newToSend = (ToSend)messagesQueue.remove(0);
      if (date <= System.currentTimeMillis()) {
        ctx.write(toSend, promise);
      } else {
        messagesQueue.add(0, newToSend);
        break;
      }
    }
    ctx.flush();
  }
}
