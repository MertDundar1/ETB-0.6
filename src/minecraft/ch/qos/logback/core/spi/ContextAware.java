package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;

public abstract interface ContextAware
{
  public abstract void setContext(Context paramContext);
  
  public abstract Context getContext();
  
  public abstract void addStatus(Status paramStatus);
  
  public abstract void addInfo(String paramString);
  
  public abstract void addInfo(String paramString, Throwable paramThrowable);
  
  public abstract void addWarn(String paramString);
  
  public abstract void addWarn(String paramString, Throwable paramThrowable);
  
  public abstract void addError(String paramString);
  
  public abstract void addError(String paramString, Throwable paramThrowable);
}
