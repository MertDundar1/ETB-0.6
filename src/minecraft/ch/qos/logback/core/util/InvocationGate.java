package ch.qos.logback.core.util;












public class InvocationGate
{
  private static final int MAX_MASK = 65535;
  









  private volatile long mask = 15L;
  private volatile long lastMaskCheck = System.currentTimeMillis();
  





  private long invocationCounter = 0L;
  



  private static final long thresholdForMaskIncrease = 100L;
  


  private final long thresholdForMaskDecrease = 800L;
  
  public InvocationGate() {}
  
  public boolean skipFurtherWork() { return (invocationCounter++ & mask) != mask; }
  

  public void updateMaskIfNecessary(long now)
  {
    long timeElapsedSinceLastMaskUpdateCheck = now - lastMaskCheck;
    lastMaskCheck = now;
    if ((timeElapsedSinceLastMaskUpdateCheck < 100L) && (mask < 65535L)) {
      mask = (mask << 1 | 1L);
    } else if (timeElapsedSinceLastMaskUpdateCheck > 800L) {
      mask >>>= 2;
    }
  }
}
