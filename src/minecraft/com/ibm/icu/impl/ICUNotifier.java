package com.ibm.icu.impl;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
















public abstract class ICUNotifier
{
  private final Object notifyLock;
  private NotifyThread notifyThread;
  private List<EventListener> listeners;
  
  public ICUNotifier()
  {
    notifyLock = new Object();
  }
  







  public void addListener(EventListener l)
  {
    if (l == null) {
      throw new NullPointerException();
    }
    
    if (acceptsListener(l)) {
      synchronized (notifyLock) {
        if (listeners == null) {
          listeners = new ArrayList();
        }
        else {
          for (EventListener ll : listeners) {
            if (ll == l) {
              return;
            }
          }
        }
        
        listeners.add(l);
      }
    } else {
      throw new IllegalStateException("Listener invalid for this notifier.");
    }
  }
  




  public void removeListener(EventListener l)
  {
    if (l == null) {
      throw new NullPointerException();
    }
    synchronized (notifyLock) {
      if (listeners != null)
      {
        Iterator<EventListener> iter = listeners.iterator();
        while (iter.hasNext()) {
          if (iter.next() == l) {
            iter.remove();
            if (listeners.size() == 0) {
              listeners = null;
            }
            return;
          }
        }
      }
    }
  }
  




  public void notifyChanged()
  {
    if (listeners != null)
      synchronized (notifyLock) {
        if (listeners != null) {
          if (notifyThread == null) {
            notifyThread = new NotifyThread(this);
            notifyThread.setDaemon(true);
            notifyThread.start();
          }
          notifyThread.queue((EventListener[])listeners.toArray(new EventListener[listeners.size()]));
        }
      }
  }
  
  protected abstract boolean acceptsListener(EventListener paramEventListener);
  
  protected abstract void notifyListener(EventListener paramEventListener);
  
  private static class NotifyThread extends Thread {
    private final ICUNotifier notifier;
    private final List<EventListener[]> queue = new ArrayList();
    
    NotifyThread(ICUNotifier notifier) {
      this.notifier = notifier;
    }
    


    public void queue(EventListener[] list)
    {
      synchronized (this) {
        queue.add(list);
        notify();
      }
    }
    

    public void run()
    {
      try
      {
        for (;;)
        {
          EventListener[] list;
          synchronized (this) {
            if (queue.isEmpty()) {
              wait(); continue;
            }
            list = (EventListener[])queue.remove(0);
          }
          
          for (int i = 0; i < list.length; i++) {
            notifier.notifyListener(list[i]);
          }
        }
      }
      catch (InterruptedException e) {}
    }
  }
}
