package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;
import org.slf4j.Marker;























public abstract class TurboFilter
  extends ContextAwareBase
  implements LifeCycle
{
  private String name;
  boolean start = false;
  




  public TurboFilter() {}
  




  public abstract FilterReply decide(Marker paramMarker, Logger paramLogger, Level paramLevel, String paramString, Object[] paramArrayOfObject, Throwable paramThrowable);
  




  public void start()
  {
    start = true;
  }
  
  public boolean isStarted() {
    return start;
  }
  
  public void stop() {
    start = false;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
