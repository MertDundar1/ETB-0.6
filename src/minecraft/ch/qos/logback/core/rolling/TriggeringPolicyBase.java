package ch.qos.logback.core.rolling;

import ch.qos.logback.core.spi.ContextAwareBase;

















public abstract class TriggeringPolicyBase<E>
  extends ContextAwareBase
  implements TriggeringPolicy<E>
{
  private boolean start;
  
  public TriggeringPolicyBase() {}
  
  public void start()
  {
    start = true;
  }
  
  public void stop() {
    start = false;
  }
  
  public boolean isStarted() {
    return start;
  }
}
