package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;

public abstract interface AppenderFactory<E>
{
  public abstract Appender<E> buildAppender(Context paramContext, String paramString)
    throws JoranException;
}
