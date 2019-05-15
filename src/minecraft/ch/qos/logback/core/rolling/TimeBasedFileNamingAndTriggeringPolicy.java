package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.spi.ContextAware;

public abstract interface TimeBasedFileNamingAndTriggeringPolicy<E>
  extends TriggeringPolicy<E>, ContextAware
{
  public abstract void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> paramTimeBasedRollingPolicy);
  
  public abstract String getElapsedPeriodsFileName();
  
  public abstract String getCurrentPeriodsFileNameWithoutCompressionSuffix();
  
  public abstract ArchiveRemover getArchiveRemover();
  
  public abstract long getCurrentTime();
  
  public abstract void setCurrentTime(long paramLong);
}
