package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ContextAware;

public abstract interface LoggerContextAware
  extends ContextAware
{
  public abstract void setLoggerContext(LoggerContext paramLoggerContext);
}
