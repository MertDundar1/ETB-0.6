package com.enjoytheban.utils;




public class TimerUtil
{
  private long lastMS;
  


  public TimerUtil() {}
  


  private long getCurrentMS()
  {
    return System.nanoTime() / 1000000L;
  }
  
  public boolean hasReached(double milliseconds)
  {
    return getCurrentMS() - lastMS >= milliseconds;
  }
  
  public void reset()
  {
    lastMS = getCurrentMS();
  }
  
  public boolean delay(float milliSec) {
    return (float)(getTime() - lastMS) >= milliSec;
  }
  
  public long getTime()
  {
    return System.nanoTime() / 1000000L;
  }
}
