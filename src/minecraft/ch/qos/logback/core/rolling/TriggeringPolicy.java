package ch.qos.logback.core.rolling;

import ch.qos.logback.core.spi.LifeCycle;
import java.io.File;

public abstract interface TriggeringPolicy<E>
  extends LifeCycle
{
  public abstract boolean isTriggeringEvent(File paramFile, E paramE);
}
