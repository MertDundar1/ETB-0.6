package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.spi.LifeCycle;

public abstract interface RollingPolicy
  extends LifeCycle
{
  public abstract void rollover()
    throws RolloverFailure;
  
  public abstract String getActiveFileName();
  
  public abstract CompressionMode getCompressionMode();
  
  public abstract void setParent(FileAppender paramFileAppender);
}
