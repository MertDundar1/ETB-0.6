package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public abstract interface Layout<E>
  extends ContextAware, LifeCycle
{
  public abstract String doLayout(E paramE);
  
  public abstract String getFileHeader();
  
  public abstract String getPresentationHeader();
  
  public abstract String getPresentationFooter();
  
  public abstract String getFileFooter();
  
  public abstract String getContentType();
}
