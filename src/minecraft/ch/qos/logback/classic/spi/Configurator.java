package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ContextAware;

public abstract interface Configurator
  extends ContextAware
{
  public abstract void configure(LoggerContext paramLoggerContext);
}
