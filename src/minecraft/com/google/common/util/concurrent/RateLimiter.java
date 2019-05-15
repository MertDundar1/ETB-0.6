package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;





























































































































































































































@ThreadSafe
@Beta
public abstract class RateLimiter
{
  private final SleepingTicker ticker;
  private final long offsetNanos;
  double storedPermits;
  double maxPermits;
  volatile double stableIntervalMicros;
  
  public static RateLimiter create(double permitsPerSecond)
  {
    return create(SleepingTicker.SYSTEM_TICKER, permitsPerSecond);
  }
  
  @VisibleForTesting
  static RateLimiter create(SleepingTicker ticker, double permitsPerSecond) {
    RateLimiter rateLimiter = new Bursty(ticker, 1.0D);
    rateLimiter.setRate(permitsPerSecond);
    return rateLimiter;
  }
  





















  public static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit)
  {
    return create(SleepingTicker.SYSTEM_TICKER, permitsPerSecond, warmupPeriod, unit);
  }
  
  @VisibleForTesting
  static RateLimiter create(SleepingTicker ticker, double permitsPerSecond, long warmupPeriod, TimeUnit unit)
  {
    RateLimiter rateLimiter = new WarmingUp(ticker, warmupPeriod, unit);
    rateLimiter.setRate(permitsPerSecond);
    return rateLimiter;
  }
  
  @VisibleForTesting
  static RateLimiter createWithCapacity(SleepingTicker ticker, double permitsPerSecond, long maxBurstBuildup, TimeUnit unit)
  {
    double maxBurstSeconds = unit.toNanos(maxBurstBuildup) / 1.0E9D;
    Bursty rateLimiter = new Bursty(ticker, maxBurstSeconds);
    rateLimiter.setRate(permitsPerSecond);
    return rateLimiter;
  }
  




























  private final Object mutex = new Object();
  




  private long nextFreeTicketMicros = 0L;
  
  private RateLimiter(SleepingTicker ticker) {
    this.ticker = ticker;
    offsetNanos = ticker.read();
  }
  

















  public final void setRate(double permitsPerSecond)
  {
    Preconditions.checkArgument((permitsPerSecond > 0.0D) && (!Double.isNaN(permitsPerSecond)), "rate must be positive");
    
    synchronized (mutex) {
      resync(readSafeMicros());
      double stableIntervalMicros = TimeUnit.SECONDS.toMicros(1L) / permitsPerSecond;
      this.stableIntervalMicros = stableIntervalMicros;
      doSetRate(permitsPerSecond, stableIntervalMicros);
    }
  }
  



  abstract void doSetRate(double paramDouble1, double paramDouble2);
  



  public final double getRate()
  {
    return TimeUnit.SECONDS.toMicros(1L) / stableIntervalMicros;
  }
  








  public double acquire()
  {
    return acquire(1);
  }
  







  public double acquire(int permits)
  {
    long microsToWait = reserve(permits);
    ticker.sleepMicrosUninterruptibly(microsToWait);
    return 1.0D * microsToWait / TimeUnit.SECONDS.toMicros(1L);
  }
  







  long reserve()
  {
    return reserve(1);
  }
  





  long reserve(int permits)
  {
    checkPermits(permits);
    synchronized (mutex) {
      return reserveNextTicket(permits, readSafeMicros());
    }
  }
  











  public boolean tryAcquire(long timeout, TimeUnit unit)
  {
    return tryAcquire(1, timeout, unit);
  }
  









  public boolean tryAcquire(int permits)
  {
    return tryAcquire(permits, 0L, TimeUnit.MICROSECONDS);
  }
  









  public boolean tryAcquire()
  {
    return tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
  }
  










  public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
  {
    long timeoutMicros = unit.toMicros(timeout);
    checkPermits(permits);
    long microsToWait;
    synchronized (mutex) {
      long nowMicros = readSafeMicros();
      if (nextFreeTicketMicros > nowMicros + timeoutMicros) {
        return false;
      }
      microsToWait = reserveNextTicket(permits, nowMicros);
    }
    
    ticker.sleepMicrosUninterruptibly(microsToWait);
    return true;
  }
  
  private static void checkPermits(int permits) {
    Preconditions.checkArgument(permits > 0, "Requested permits must be positive");
  }
  




  private long reserveNextTicket(double requiredPermits, long nowMicros)
  {
    resync(nowMicros);
    long microsToNextFreeTicket = Math.max(0L, nextFreeTicketMicros - nowMicros);
    double storedPermitsToSpend = Math.min(requiredPermits, storedPermits);
    double freshPermits = requiredPermits - storedPermitsToSpend;
    
    long waitMicros = storedPermitsToWaitTime(storedPermits, storedPermitsToSpend) + (freshPermits * stableIntervalMicros);
    

    nextFreeTicketMicros += waitMicros;
    storedPermits -= storedPermitsToSpend;
    return microsToNextFreeTicket;
  }
  




  abstract long storedPermitsToWaitTime(double paramDouble1, double paramDouble2);
  




  private void resync(long nowMicros)
  {
    if (nowMicros > nextFreeTicketMicros) {
      storedPermits = Math.min(maxPermits, storedPermits + (nowMicros - nextFreeTicketMicros) / stableIntervalMicros);
      
      nextFreeTicketMicros = nowMicros;
    }
  }
  
  private long readSafeMicros() {
    return TimeUnit.NANOSECONDS.toMicros(ticker.read() - offsetNanos);
  }
  
  public String toString()
  {
    return String.format("RateLimiter[stableRate=%3.1fqps]", new Object[] { Double.valueOf(1000000.0D / stableIntervalMicros) });
  }
  




















  private static class WarmingUp
    extends RateLimiter
  {
    final long warmupPeriodMicros;
    


















    private double slope;
    


















    private double halfPermits;
    



















    WarmingUp(RateLimiter.SleepingTicker ticker, long warmupPeriod, TimeUnit timeUnit)
    {
      super(null);
      warmupPeriodMicros = timeUnit.toMicros(warmupPeriod);
    }
    
    void doSetRate(double permitsPerSecond, double stableIntervalMicros)
    {
      double oldMaxPermits = maxPermits;
      maxPermits = (warmupPeriodMicros / stableIntervalMicros);
      halfPermits = (maxPermits / 2.0D);
      
      double coldIntervalMicros = stableIntervalMicros * 3.0D;
      slope = ((coldIntervalMicros - stableIntervalMicros) / halfPermits);
      if (oldMaxPermits == Double.POSITIVE_INFINITY)
      {
        storedPermits = 0.0D;
      } else {
        storedPermits = (oldMaxPermits == 0.0D ? maxPermits : storedPermits * maxPermits / oldMaxPermits);
      }
    }
    


    long storedPermitsToWaitTime(double storedPermits, double permitsToTake)
    {
      double availablePermitsAboveHalf = storedPermits - halfPermits;
      long micros = 0L;
      
      if (availablePermitsAboveHalf > 0.0D) {
        double permitsAboveHalfToTake = Math.min(availablePermitsAboveHalf, permitsToTake);
        micros = (permitsAboveHalfToTake * (permitsToTime(availablePermitsAboveHalf) + permitsToTime(availablePermitsAboveHalf - permitsAboveHalfToTake)) / 2.0D);
        
        permitsToTake -= permitsAboveHalfToTake;
      }
      
      micros = (micros + stableIntervalMicros * permitsToTake);
      return micros;
    }
    
    private double permitsToTime(double permits) {
      return stableIntervalMicros + permits * slope;
    }
  }
  


  private static class Bursty
    extends RateLimiter
  {
    final double maxBurstSeconds;
    


    Bursty(RateLimiter.SleepingTicker ticker, double maxBurstSeconds)
    {
      super(null);
      this.maxBurstSeconds = maxBurstSeconds;
    }
    
    void doSetRate(double permitsPerSecond, double stableIntervalMicros)
    {
      double oldMaxPermits = maxPermits;
      maxPermits = (maxBurstSeconds * permitsPerSecond);
      storedPermits = (oldMaxPermits == 0.0D ? 0.0D : storedPermits * maxPermits / oldMaxPermits);
    }
    


    long storedPermitsToWaitTime(double storedPermits, double permitsToTake)
    {
      return 0L;
    }
  }
  
  @VisibleForTesting
  static abstract class SleepingTicker
    extends Ticker
  {
    static final SleepingTicker SYSTEM_TICKER = new SleepingTicker()
    {
      public long read() {
        return systemTicker().read();
      }
      
      public void sleepMicrosUninterruptibly(long micros)
      {
        if (micros > 0L) {
          Uninterruptibles.sleepUninterruptibly(micros, TimeUnit.MICROSECONDS);
        }
      }
    };
    
    SleepingTicker() {}
    
    abstract void sleepMicrosUninterruptibly(long paramLong);
  }
}
