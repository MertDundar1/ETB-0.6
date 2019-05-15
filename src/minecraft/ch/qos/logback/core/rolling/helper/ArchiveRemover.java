package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAware;
import java.util.Date;

public abstract interface ArchiveRemover
  extends ContextAware
{
  public abstract void clean(Date paramDate);
  
  public abstract void setMaxHistory(int paramInt);
}
