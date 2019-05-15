package ch.qos.logback.core;

import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import java.util.ArrayList;
import java.util.List;















public class BasicStatusManager
  implements StatusManager
{
  public static final int MAX_HEADER_COUNT = 150;
  public static final int TAIL_SIZE = 150;
  int count = 0;
  

  protected final List<Status> statusList = new ArrayList();
  protected final CyclicBuffer<Status> tailBuffer = new CyclicBuffer(150);
  
  protected final LogbackLock statusListLock = new LogbackLock();
  
  int level = 0;
  

  protected final List<StatusListener> statusListenerList = new ArrayList();
  protected final LogbackLock statusListenerListLock = new LogbackLock();
  






  public BasicStatusManager() {}
  





  public void add(Status newStatus)
  {
    fireStatusAddEvent(newStatus);
    
    count += 1;
    if (newStatus.getLevel() > level) {
      level = newStatus.getLevel();
    }
    
    synchronized (statusListLock) {
      if (statusList.size() < 150) {
        statusList.add(newStatus);
      } else {
        tailBuffer.add(newStatus);
      }
    }
  }
  
  public List<Status> getCopyOfStatusList()
  {
    synchronized (statusListLock) {
      List<Status> tList = new ArrayList(statusList);
      tList.addAll(tailBuffer.asList());
      return tList;
    }
  }
  
  private void fireStatusAddEvent(Status status) {
    synchronized (statusListenerListLock) {
      for (StatusListener sl : statusListenerList) {
        sl.addStatusEvent(status);
      }
    }
  }
  
  public void clear() {
    synchronized (statusListLock) {
      count = 0;
      statusList.clear();
      tailBuffer.clear();
    }
  }
  
  public int getLevel() {
    return level;
  }
  
  public int getCount() {
    return count;
  }
  



  public void add(StatusListener listener)
  {
    synchronized (statusListenerListLock) {
      if ((listener instanceof OnConsoleStatusListener)) {
        boolean alreadyPresent = checkForPresence(statusListenerList, listener.getClass());
        if (alreadyPresent)
          return;
      }
      statusListenerList.add(listener);
    }
  }
  
  private boolean checkForPresence(List<StatusListener> statusListenerList, Class<?> aClass) {
    for (StatusListener e : statusListenerList) {
      if (e.getClass() == aClass)
        return true;
    }
    return false;
  }
  
  public void remove(StatusListener listener)
  {
    synchronized (statusListenerListLock) {
      statusListenerList.remove(listener);
    }
  }
  
  public List<StatusListener> getCopyOfStatusListenerList() {
    synchronized (statusListenerListLock) {
      return new ArrayList(statusListenerList);
    }
  }
}
