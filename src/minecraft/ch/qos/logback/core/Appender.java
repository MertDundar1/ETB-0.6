package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.LifeCycle;

public abstract interface Appender<E>
  extends LifeCycle, ContextAware, FilterAttachable<E>
{
  public abstract String getName();
  
  public abstract void doAppend(E paramE)
    throws LogbackException;
  
  public abstract void setName(String paramString);
}
