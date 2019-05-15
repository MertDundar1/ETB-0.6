package ch.qos.logback.core.hook;

import ch.qos.logback.core.util.Duration;























public class DelayingShutdownHook
  extends ShutdownHookBase
{
  public static final Duration DEFAULT_DELAY = Duration.buildByMilliseconds(0.0D);
  



  private Duration delay = DEFAULT_DELAY;
  
  public DelayingShutdownHook() {}
  
  public Duration getDelay()
  {
    return delay;
  }
  




  public void setDelay(Duration delay)
  {
    this.delay = delay;
  }
  
  public void run() {
    try {
      Thread.sleep(delay.getMilliseconds());
    }
    catch (InterruptedException e) {}
    super.stop();
  }
}
