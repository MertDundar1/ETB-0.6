package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;


























public abstract class AbstractTrafficShapingHandler
  extends ChannelDuplexHandler
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
  




  public static final long DEFAULT_CHECK_INTERVAL = 1000L;
  




  public static final long DEFAULT_MAX_TIME = 15000L;
  



  static final long MINIMAL_WAIT = 10L;
  



  protected TrafficCounter trafficCounter;
  



  private long writeLimit;
  



  private long readLimit;
  



  protected long maxTime = 15000L;
  



  protected long checkInterval = 1000L;
  
  private static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED");
  
  private static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".REOPEN_TASK");
  





  void setTrafficCounter(TrafficCounter newTrafficCounter)
  {
    trafficCounter = newTrafficCounter;
  }
  










  protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime)
  {
    this.writeLimit = writeLimit;
    this.readLimit = readLimit;
    this.checkInterval = checkInterval;
    this.maxTime = maxTime;
  }
  








  protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval)
  {
    this(writeLimit, readLimit, checkInterval, 15000L);
  }
  







  protected AbstractTrafficShapingHandler(long writeLimit, long readLimit)
  {
    this(writeLimit, readLimit, 1000L, 15000L);
  }
  


  protected AbstractTrafficShapingHandler()
  {
    this(0L, 0L, 1000L, 15000L);
  }
  






  protected AbstractTrafficShapingHandler(long checkInterval)
  {
    this(0L, 0L, checkInterval, 15000L);
  }
  









  public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval)
  {
    configure(newWriteLimit, newReadLimit);
    configure(newCheckInterval);
  }
  







  public void configure(long newWriteLimit, long newReadLimit)
  {
    writeLimit = newWriteLimit;
    readLimit = newReadLimit;
    if (trafficCounter != null) {
      trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
    }
  }
  





  public void configure(long newCheckInterval)
  {
    checkInterval = newCheckInterval;
    if (trafficCounter != null) {
      trafficCounter.configure(checkInterval);
    }
  }
  


  public long getWriteLimit()
  {
    return writeLimit;
  }
  


  public void setWriteLimit(long writeLimit)
  {
    this.writeLimit = writeLimit;
    if (trafficCounter != null) {
      trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
    }
  }
  


  public long getReadLimit()
  {
    return readLimit;
  }
  


  public void setReadLimit(long readLimit)
  {
    this.readLimit = readLimit;
    if (trafficCounter != null) {
      trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
    }
  }
  


  public long getCheckInterval()
  {
    return checkInterval;
  }
  


  public void setCheckInterval(long checkInterval)
  {
    this.checkInterval = checkInterval;
    if (trafficCounter != null) {
      trafficCounter.configure(checkInterval);
    }
  }
  




  public void setMaxTimeWait(long maxTime)
  {
    this.maxTime = maxTime;
  }
  


  public long getMaxTimeWait()
  {
    return maxTime;
  }
  



  protected void doAccounting(TrafficCounter counter) {}
  



  private static final class ReopenReadTimerTask
    implements Runnable
  {
    final ChannelHandlerContext ctx;
    



    ReopenReadTimerTask(ChannelHandlerContext ctx)
    {
      this.ctx = ctx;
    }
    
    public void run() {
      if ((!ctx.channel().config().isAutoRead()) && (AbstractTrafficShapingHandler.isHandlerActive(ctx)))
      {

        if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
          AbstractTrafficShapingHandler.logger.debug("Channel:" + ctx.channel().hashCode() + " Not Unsuspend: " + ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(ctx));
        }
        
        ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(Boolean.valueOf(false));
      }
      else {
        if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
          if ((ctx.channel().config().isAutoRead()) && (!AbstractTrafficShapingHandler.isHandlerActive(ctx))) {
            AbstractTrafficShapingHandler.logger.debug("Channel:" + ctx.channel().hashCode() + " Unsuspend: " + ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(ctx));
          }
          else {
            AbstractTrafficShapingHandler.logger.debug("Channel:" + ctx.channel().hashCode() + " Normal Unsuspend: " + ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(ctx));
          }
        }
        

        ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(Boolean.valueOf(false));
        ctx.channel().config().setAutoRead(true);
        ctx.channel().read();
      }
      if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
        AbstractTrafficShapingHandler.logger.debug("Channel:" + ctx.channel().hashCode() + " Unsupsend final status => " + ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(ctx));
      }
    }
  }
  

  public void channelRead(ChannelHandlerContext ctx, Object msg)
    throws Exception
  {
    long size = calculateSize(msg);
    
    if ((size > 0L) && (trafficCounter != null))
    {
      long wait = trafficCounter.readTimeToWait(size, readLimit, maxTime);
      if (wait >= 10L)
      {

        if (logger.isDebugEnabled()) {
          logger.debug("Channel:" + ctx.channel().hashCode() + " Read Suspend: " + wait + ":" + ctx.channel().config().isAutoRead() + ":" + isHandlerActive(ctx));
        }
        

        if ((ctx.channel().config().isAutoRead()) && (isHandlerActive(ctx))) {
          ctx.channel().config().setAutoRead(false);
          ctx.attr(READ_SUSPENDED).set(Boolean.valueOf(true));
          

          Attribute<Runnable> attr = ctx.attr(REOPEN_TASK);
          Runnable reopenTask = (Runnable)attr.get();
          if (reopenTask == null) {
            reopenTask = new ReopenReadTimerTask(ctx);
            attr.set(reopenTask);
          }
          ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
          if (logger.isDebugEnabled()) {
            logger.debug("Channel:" + ctx.channel().hashCode() + " Suspend final status => " + ctx.channel().config().isAutoRead() + ":" + isHandlerActive(ctx) + " will reopened at: " + wait);
          }
        }
      }
    }
    


    ctx.fireChannelRead(msg);
  }
  
  protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
    Boolean suspended = (Boolean)ctx.attr(READ_SUSPENDED).get();
    return (suspended == null) || (Boolean.FALSE.equals(suspended));
  }
  
  public void read(ChannelHandlerContext ctx)
  {
    if (isHandlerActive(ctx))
    {
      ctx.read();
    }
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
    throws Exception
  {
    long size = calculateSize(msg);
    
    if ((size > 0L) && (trafficCounter != null))
    {
      long wait = trafficCounter.writeTimeToWait(size, writeLimit, maxTime);
      if (wait >= 10L)
      {






        if (logger.isDebugEnabled()) {
          logger.debug("Channel:" + ctx.channel().hashCode() + " Write suspend: " + wait + ":" + ctx.channel().config().isAutoRead() + ":" + isHandlerActive(ctx));
        }
        

        submitWrite(ctx, msg, wait, promise);
        return;
      }
    }
    
    submitWrite(ctx, msg, 0L, promise);
  }
  



  protected abstract void submitWrite(ChannelHandlerContext paramChannelHandlerContext, Object paramObject, long paramLong, ChannelPromise paramChannelPromise);
  


  public TrafficCounter trafficCounter()
  {
    return trafficCounter;
  }
  
  public String toString()
  {
    return "TrafficShaping with Write Limit: " + writeLimit + " Read Limit: " + readLimit + " and Counter: " + (trafficCounter != null ? trafficCounter.toString() : "none");
  }
  









  protected long calculateSize(Object msg)
  {
    if ((msg instanceof ByteBuf)) {
      return ((ByteBuf)msg).readableBytes();
    }
    if ((msg instanceof ByteBufHolder)) {
      return ((ByteBufHolder)msg).content().readableBytes();
    }
    return -1L;
  }
}
