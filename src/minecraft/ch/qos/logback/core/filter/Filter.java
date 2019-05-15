package ch.qos.logback.core.filter;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;





















public abstract class Filter<E>
  extends ContextAwareBase
  implements LifeCycle
{
  private String name;
  
  public Filter() {}
  
  boolean start = false;
  
  public void start() {
    start = true;
  }
  
  public boolean isStarted() {
    return start;
  }
  
  public void stop() {
    start = false;
  }
  





  public abstract FilterReply decide(E paramE);
  




  public String getName()
  {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
