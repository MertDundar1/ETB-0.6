package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import java.io.PrintStream;

















public class ContextAwareBase
  implements ContextAware
{
  private int noContextWarning = 0;
  protected Context context;
  final Object declaredOrigin;
  
  public ContextAwareBase() {
    declaredOrigin = this;
  }
  
  public ContextAwareBase(ContextAware declaredOrigin) { this.declaredOrigin = declaredOrigin; }
  
  public void setContext(Context context)
  {
    if (this.context == null) {
      this.context = context;
    } else if (this.context != context) {
      throw new IllegalStateException("Context has been already set");
    }
  }
  
  public Context getContext() {
    return context;
  }
  
  public StatusManager getStatusManager() {
    if (context == null) {
      return null;
    }
    return context.getStatusManager();
  }
  





  protected Object getDeclaredOrigin()
  {
    return declaredOrigin;
  }
  
  public void addStatus(Status status) {
    if (context == null) {
      if (noContextWarning++ == 0) {
        System.out.println("LOGBACK: No context given for " + this);
      }
      return;
    }
    StatusManager sm = context.getStatusManager();
    if (sm != null) {
      sm.add(status);
    }
  }
  
  public void addInfo(String msg) {
    addStatus(new InfoStatus(msg, getDeclaredOrigin()));
  }
  
  public void addInfo(String msg, Throwable ex) {
    addStatus(new InfoStatus(msg, getDeclaredOrigin(), ex));
  }
  
  public void addWarn(String msg) {
    addStatus(new WarnStatus(msg, getDeclaredOrigin()));
  }
  
  public void addWarn(String msg, Throwable ex) {
    addStatus(new WarnStatus(msg, getDeclaredOrigin(), ex));
  }
  
  public void addError(String msg) {
    addStatus(new ErrorStatus(msg, getDeclaredOrigin()));
  }
  
  public void addError(String msg, Throwable ex) {
    addStatus(new ErrorStatus(msg, getDeclaredOrigin(), ex));
  }
}
