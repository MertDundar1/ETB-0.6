package ch.qos.logback.core.status;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.PrintStream;
import java.util.List;
















abstract class OnPrintStreamStatusListenerBase
  extends ContextAwareBase
  implements StatusListener, LifeCycle
{
  boolean isStarted = false;
  
  static final long DEFAULT_RETROSPECTIVE = 300L;
  long retrospective = 300L;
  

  OnPrintStreamStatusListenerBase() {}
  

  protected abstract PrintStream getPrintStream();
  
  private void print(Status status)
  {
    StringBuilder sb = new StringBuilder();
    StatusPrinter.buildStr(sb, "", status);
    getPrintStream().print(sb);
  }
  
  public void addStatusEvent(Status status) {
    if (!isStarted)
      return;
    print(status);
  }
  


  private void retrospectivePrint()
  {
    if (context == null)
      return;
    long now = System.currentTimeMillis();
    StatusManager sm = context.getStatusManager();
    List<Status> statusList = sm.getCopyOfStatusList();
    for (Status status : statusList) {
      long timestamp = status.getDate().longValue();
      if (now - timestamp < retrospective) {
        print(status);
      }
    }
  }
  
  public void start() {
    isStarted = true;
    if (retrospective > 0L) {
      retrospectivePrint();
    }
  }
  
  public void setRetrospective(long retrospective) {
    this.retrospective = retrospective;
  }
  
  public long getRetrospective() {
    return retrospective;
  }
  
  public void stop() {
    isStarted = false;
  }
  
  public boolean isStarted() {
    return isStarted;
  }
}
