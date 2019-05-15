package ch.qos.logback.core.status;

import java.util.List;

public abstract interface StatusManager
{
  public abstract void add(Status paramStatus);
  
  public abstract List<Status> getCopyOfStatusList();
  
  public abstract int getCount();
  
  public abstract void add(StatusListener paramStatusListener);
  
  public abstract void remove(StatusListener paramStatusListener);
  
  public abstract void clear();
  
  public abstract List<StatusListener> getCopyOfStatusListenerList();
}
