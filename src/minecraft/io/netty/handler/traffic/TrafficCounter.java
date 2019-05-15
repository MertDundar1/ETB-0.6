package io.netty.handler.traffic;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;



























public class TrafficCounter
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(TrafficCounter.class);
  



  private final AtomicLong currentWrittenBytes = new AtomicLong();
  



  private final AtomicLong currentReadBytes = new AtomicLong();
  



  private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
  



  private final AtomicLong cumulativeReadBytes = new AtomicLong();
  



  private long lastCumulativeTime;
  



  private long lastWriteThroughput;
  



  private long lastReadThroughput;
  



  private final AtomicLong lastTime = new AtomicLong();
  



  private long lastWrittenBytes;
  



  private long lastReadBytes;
  



  private long lastNonNullWrittenBytes;
  



  private long lastNonNullWrittenTime;
  



  private long lastNonNullReadTime;
  



  private long lastNonNullReadBytes;
  



  final AtomicLong checkInterval = new AtomicLong(1000L);
  



  final String name;
  



  private final AbstractTrafficShapingHandler trafficShapingHandler;
  



  private final ScheduledExecutorService executor;
  



  private Runnable monitor;
  



  private volatile ScheduledFuture<?> scheduledFuture;
  



  final AtomicBoolean monitorActive = new AtomicBoolean();
  





  private static class TrafficMonitoringTask
    implements Runnable
  {
    private final AbstractTrafficShapingHandler trafficShapingHandler1;
    




    private final TrafficCounter counter;
    




    protected TrafficMonitoringTask(AbstractTrafficShapingHandler trafficShapingHandler, TrafficCounter counter)
    {
      trafficShapingHandler1 = trafficShapingHandler;
      this.counter = counter;
    }
    
    public void run()
    {
      if (!counter.monitorActive.get()) {
        return;
      }
      long endTime = System.currentTimeMillis();
      counter.resetAccounting(endTime);
      if (trafficShapingHandler1 != null) {
        trafficShapingHandler1.doAccounting(counter);
      }
      counter.scheduledFuture = counter.executor.schedule(this, counter.checkInterval.get(), TimeUnit.MILLISECONDS);
    }
  }
  



  public synchronized void start()
  {
    if (monitorActive.get()) {
      return;
    }
    lastTime.set(System.currentTimeMillis());
    if (checkInterval.get() > 0L) {
      monitorActive.set(true);
      monitor = new TrafficMonitoringTask(trafficShapingHandler, this);
      scheduledFuture = executor.schedule(monitor, checkInterval.get(), TimeUnit.MILLISECONDS);
    }
  }
  


  public synchronized void stop()
  {
    if (!monitorActive.get()) {
      return;
    }
    monitorActive.set(false);
    resetAccounting(System.currentTimeMillis());
    if (trafficShapingHandler != null) {
      trafficShapingHandler.doAccounting(this);
    }
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
  }
  





  synchronized void resetAccounting(long newLastTime)
  {
    long interval = newLastTime - lastTime.getAndSet(newLastTime);
    if (interval == 0L)
    {
      return;
    }
    if ((logger.isDebugEnabled()) && (interval > 2L * checkInterval())) {
      logger.debug("Acct schedule not ok: " + interval + " > 2*" + checkInterval() + " from " + name);
    }
    lastReadBytes = currentReadBytes.getAndSet(0L);
    lastWrittenBytes = currentWrittenBytes.getAndSet(0L);
    lastReadThroughput = (lastReadBytes / interval * 1000L);
    
    lastWriteThroughput = (lastWrittenBytes / interval * 1000L);
    
    if (lastWrittenBytes > 0L) {
      lastNonNullWrittenBytes = lastWrittenBytes;
      lastNonNullWrittenTime = newLastTime;
    }
    if (lastReadBytes > 0L) {
      lastNonNullReadBytes = lastReadBytes;
      lastNonNullReadTime = newLastTime;
    }
  }
  













  public TrafficCounter(AbstractTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval)
  {
    this.trafficShapingHandler = trafficShapingHandler;
    this.executor = executor;
    this.name = name;
    lastCumulativeTime = System.currentTimeMillis();
    configure(checkInterval);
  }
  





  public void configure(long newcheckInterval)
  {
    long newInterval = newcheckInterval / 10L * 10L;
    if (checkInterval.get() != newInterval) {
      checkInterval.set(newInterval);
      if (newInterval <= 0L) {
        stop();
        
        lastTime.set(System.currentTimeMillis());
      }
      else {
        start();
      }
    }
  }
  





  void bytesRecvFlowControl(long recv)
  {
    currentReadBytes.addAndGet(recv);
    cumulativeReadBytes.addAndGet(recv);
  }
  





  void bytesWriteFlowControl(long write)
  {
    currentWrittenBytes.addAndGet(write);
    cumulativeWrittenBytes.addAndGet(write);
  }
  




  public long checkInterval()
  {
    return checkInterval.get();
  }
  



  public long lastReadThroughput()
  {
    return lastReadThroughput;
  }
  



  public long lastWriteThroughput()
  {
    return lastWriteThroughput;
  }
  



  public long lastReadBytes()
  {
    return lastReadBytes;
  }
  



  public long lastWrittenBytes()
  {
    return lastWrittenBytes;
  }
  



  public long currentReadBytes()
  {
    return currentReadBytes.get();
  }
  



  public long currentWrittenBytes()
  {
    return currentWrittenBytes.get();
  }
  


  public long lastTime()
  {
    return lastTime.get();
  }
  


  public long cumulativeWrittenBytes()
  {
    return cumulativeWrittenBytes.get();
  }
  


  public long cumulativeReadBytes()
  {
    return cumulativeReadBytes.get();
  }
  



  public long lastCumulativeTime()
  {
    return lastCumulativeTime;
  }
  


  public void resetCumulativeTime()
  {
    lastCumulativeTime = System.currentTimeMillis();
    cumulativeReadBytes.set(0L);
    cumulativeWrittenBytes.set(0L);
  }
  


  public String name()
  {
    return name;
  }
  











  public synchronized long readTimeToWait(long size, long limitTraffic, long maxTime)
  {
    long now = System.currentTimeMillis();
    bytesRecvFlowControl(size);
    if (limitTraffic == 0L) {
      return 0L;
    }
    long sum = currentReadBytes.get();
    long interval = now - lastTime.get();
    
    if ((interval > 10L) && (sum > 0L)) {
      long time = (sum * 1000L / limitTraffic - interval) / 10L * 10L;
      if (time > 10L) {
        if (logger.isDebugEnabled()) {
          logger.debug("Time: " + time + ":" + sum + ":" + interval);
        }
        return time > maxTime ? maxTime : time;
      }
      return 0L;
    }
    
    if ((lastNonNullReadBytes > 0L) && (lastNonNullReadTime + 10L < now)) {
      long lastsum = sum + lastNonNullReadBytes;
      long lastinterval = now - lastNonNullReadTime;
      long time = (lastsum * 1000L / limitTraffic - lastinterval) / 10L * 10L;
      if (time > 10L) {
        if (logger.isDebugEnabled()) {
          logger.debug("Time: " + time + ":" + lastsum + ":" + lastinterval);
        }
        return time > maxTime ? maxTime : time;
      }
    }
    else {
      sum += lastReadBytes;
      long lastinterval = 10L;
      long time = (sum * 1000L / limitTraffic - lastinterval) / 10L * 10L;
      if (time > 10L) {
        if (logger.isDebugEnabled()) {
          logger.debug("Time: " + time + ":" + sum + ":" + lastinterval);
        }
        return time > maxTime ? maxTime : time;
      }
    }
    return 0L;
  }
  











  public synchronized long writeTimeToWait(long size, long limitTraffic, long maxTime)
  {
    bytesWriteFlowControl(size);
    if (limitTraffic == 0L) {
      return 0L;
    }
    long sum = currentWrittenBytes.get();
    long now = System.currentTimeMillis();
    long interval = now - lastTime.get();
    if ((interval > 10L) && (sum > 0L)) {
      long time = (sum * 1000L / limitTraffic - interval) / 10L * 10L;
      if (time > 10L) {
        if (logger.isDebugEnabled()) {
          logger.debug("Time: " + time + ":" + sum + ":" + interval);
        }
        return time > maxTime ? maxTime : time;
      }
      return 0L;
    }
    if ((lastNonNullWrittenBytes > 0L) && (lastNonNullWrittenTime + 10L < now)) {
      long lastsum = sum + lastNonNullWrittenBytes;
      long lastinterval = now - lastNonNullWrittenTime;
      long time = (lastsum * 1000L / limitTraffic - lastinterval) / 10L * 10L;
      if (time > 10L) {
        if (logger.isDebugEnabled()) {
          logger.debug("Time: " + time + ":" + lastsum + ":" + lastinterval);
        }
        return time > maxTime ? maxTime : time;
      }
    } else {
      sum += lastWrittenBytes;
      long lastinterval = 10L + Math.abs(interval);
      long time = (sum * 1000L / limitTraffic - lastinterval) / 10L * 10L;
      if (time > 10L) {
        if (logger.isDebugEnabled()) {
          logger.debug("Time: " + time + ":" + sum + ":" + lastinterval);
        }
        return time > maxTime ? maxTime : time;
      }
    }
    return 0L;
  }
  



  public String toString()
  {
    return "Monitor " + name + " Current Speed Read: " + (lastReadThroughput >> 10) + " KB/s, Write: " + (lastWriteThroughput >> 10) + " KB/s Current Read: " + (currentReadBytes.get() >> 10) + " KB Current Write: " + (currentWrittenBytes.get() >> 10) + " KB";
  }
}
