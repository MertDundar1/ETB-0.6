package ch.qos.logback.core.boolex;

import ch.qos.logback.core.spi.ContextAwareBase;










public abstract class EventEvaluatorBase<E>
  extends ContextAwareBase
  implements EventEvaluator<E>
{
  String name;
  boolean started;
  
  public EventEvaluatorBase() {}
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name) {
    if (this.name != null) {
      throw new IllegalStateException("name has been already set");
    }
    this.name = name;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public void start() {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
}
