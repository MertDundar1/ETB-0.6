package ch.qos.logback.core.recovery;





public class RecoveryCoordinator
{
  public static final long BACKOFF_COEFFICIENT_MIN = 20L;
  



  public RecoveryCoordinator() {}
  



  static long BACKOFF_COEFFICIENT_MAX = 327680L;
  
  private long backOffCoefficient = 20L;
  
  private static long UNSET = -1L;
  private long currentTime = UNSET;
  long next = System.currentTimeMillis() + getBackoffCoefficient();
  
  public boolean isTooSoon() {
    long now = getCurrentTime();
    if (now > next) {
      next = (now + getBackoffCoefficient());
      return false;
    }
    return true;
  }
  
  void setCurrentTime(long forcedTime)
  {
    currentTime = forcedTime;
  }
  
  private long getCurrentTime() {
    if (currentTime != UNSET) {
      return currentTime;
    }
    return System.currentTimeMillis();
  }
  
  private long getBackoffCoefficient() {
    long currentCoeff = backOffCoefficient;
    if (backOffCoefficient < BACKOFF_COEFFICIENT_MAX) {
      backOffCoefficient *= 4L;
    }
    return currentCoeff;
  }
}
