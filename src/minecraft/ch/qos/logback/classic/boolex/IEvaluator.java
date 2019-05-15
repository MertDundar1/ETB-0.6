package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;

public abstract interface IEvaluator
{
  public abstract boolean doEvaluate(ILoggingEvent paramILoggingEvent);
}
