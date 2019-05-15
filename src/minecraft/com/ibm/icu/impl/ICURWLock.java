package com.ibm.icu.impl;



public class ICURWLock
{
  private Object writeLock;
  

  private Object readLock;
  

  private int wwc;
  

  private int rc;
  

  private int wrc;
  

  private Stats stats;
  

  private static final int NOTIFY_NONE = 0;
  
  private static final int NOTIFY_WRITERS = 1;
  
  private static final int NOTIFY_READERS = 2;
  

  public ICURWLock()
  {
    writeLock = new Object();
    readLock = new Object();
    



    stats = new Stats(null);
  }
  



  public static final class Stats
  {
    public int _rc;
    


    public int _mrc;
    


    public int _wrc;
    


    public int _wc;
    


    public int _wwc;
    



    private Stats() {}
    


    private Stats(int rc, int mrc, int wrc, int wc, int wwc)
    {
      _rc = rc;
      _mrc = mrc;
      _wrc = wrc;
      _wc = wc;
      _wwc = wwc;
    }
    
    private Stats(Stats rhs) {
      this(_rc, _mrc, _wrc, _wc, _wwc);
    }
    


    public String toString()
    {
      return " rc: " + _rc + " mrc: " + _mrc + " wrc: " + _wrc + " wc: " + _wc + " wwc: " + _wwc;
    }
  }
  






  public synchronized Stats resetStats()
  {
    Stats result = stats;
    stats = new Stats(null);
    return result;
  }
  


  public synchronized Stats clearStats()
  {
    Stats result = stats;
    stats = null;
    return result;
  }
  


  public synchronized Stats getStats()
  {
    return stats == null ? null : new Stats(stats, null);
  }
  

  private synchronized boolean gotRead()
  {
    rc += 1;
    if (stats != null) {
      stats._rc += 1;
      if (rc > 1) stats._mrc += 1;
    }
    return true;
  }
  
  private synchronized boolean getRead() {
    if ((rc >= 0) && (wwc == 0)) {
      return gotRead();
    }
    wrc += 1;
    return false;
  }
  
  private synchronized boolean retryRead() {
    if (stats != null) stats._wrc += 1;
    if ((rc >= 0) && (wwc == 0)) {
      wrc -= 1;
      return gotRead();
    }
    return false;
  }
  
  private synchronized boolean finishRead() {
    if (rc > 0) {
      return (0 == --rc) && (wwc > 0);
    }
    throw new IllegalStateException("no current reader to release");
  }
  
  private synchronized boolean gotWrite() {
    rc = -1;
    if (stats != null) {
      stats._wc += 1;
    }
    return true;
  }
  
  private synchronized boolean getWrite() {
    if (rc == 0) {
      return gotWrite();
    }
    wwc += 1;
    return false;
  }
  
  private synchronized boolean retryWrite() {
    if (stats != null) stats._wwc += 1;
    if (rc == 0) {
      wwc -= 1;
      return gotWrite();
    }
    return false;
  }
  



  private synchronized int finishWrite()
  {
    if (rc < 0) {
      rc = 0;
      if (wwc > 0)
        return 1;
      if (wrc > 0) {
        return 2;
      }
      return 0;
    }
    
    throw new IllegalStateException("no current writer to release");
  }
  









  public void acquireRead()
  {
    if (!getRead()) {
      try {
        for (;;) {
          synchronized (readLock) {
            readLock.wait();
          }
          if (retryRead()) {
            return;
          }
        }
      }
      catch (InterruptedException e) {}
    }
  }
  








  public void releaseRead()
  {
    if (finishRead()) {
      synchronized (writeLock) {
        writeLock.notify();
      }
    }
  }
  










  public void acquireWrite()
  {
    if (!getWrite()) {
      try {
        for (;;) {
          synchronized (writeLock) {
            writeLock.wait();
          }
          if (retryWrite()) {
            return;
          }
        }
      }
      catch (InterruptedException e) {}
    }
  }
  









  public void releaseWrite()
  {
    switch (finishWrite()) {
    case 1: 
      synchronized (writeLock) {
        writeLock.notify();
      }
      break;
    case 2: 
      synchronized (readLock) {
        readLock.notifyAll();
      }
      break;
    }
  }
}
