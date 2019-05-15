package ch.qos.logback.core.boolex;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public abstract interface EventEvaluator<E>
  extends ContextAware, LifeCycle
{
  public abstract boolean evaluate(E paramE)
    throws NullPointerException, EvaluationException;
  
  public abstract String getName();
  
  public abstract void setName(String paramString);
}
