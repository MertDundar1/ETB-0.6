package com.ibm.icu.util;

import java.util.Arrays;
import java.util.Date;





























public class TimeArrayTimeZoneRule
  extends TimeZoneRule
{
  private static final long serialVersionUID = -1117109130077415245L;
  private final long[] startTimes;
  private final int timeType;
  
  public TimeArrayTimeZoneRule(String name, int rawOffset, int dstSavings, long[] startTimes, int timeType)
  {
    super(name, rawOffset, dstSavings);
    if ((startTimes == null) || (startTimes.length == 0)) {
      throw new IllegalArgumentException("No start times are specified.");
    }
    this.startTimes = ((long[])startTimes.clone());
    Arrays.sort(this.startTimes);
    
    this.timeType = timeType;
  }
  






  public long[] getStartTimes()
  {
    return (long[])startTimes.clone();
  }
  







  public int getTimeType()
  {
    return timeType;
  }
  



  public Date getFirstStart(int prevRawOffset, int prevDSTSavings)
  {
    return new Date(getUTC(startTimes[0], prevRawOffset, prevDSTSavings));
  }
  



  public Date getFinalStart(int prevRawOffset, int prevDSTSavings)
  {
    return new Date(getUTC(startTimes[(startTimes.length - 1)], prevRawOffset, prevDSTSavings));
  }
  



  public Date getNextStart(long base, int prevOffset, int prevDSTSavings, boolean inclusive)
  {
    for (int i = startTimes.length - 1; 
        i >= 0; i--) {
      long time = getUTC(startTimes[i], prevOffset, prevDSTSavings);
      if ((time < base) || ((!inclusive) && (time == base))) {
        break;
      }
    }
    if (i == startTimes.length - 1) {
      return null;
    }
    return new Date(getUTC(startTimes[(i + 1)], prevOffset, prevDSTSavings));
  }
  



  public Date getPreviousStart(long base, int prevOffset, int prevDSTSavings, boolean inclusive)
  {
    for (int i = startTimes.length - 1; 
        i >= 0; i--) {
      long time = getUTC(startTimes[i], prevOffset, prevDSTSavings);
      if ((time < base) || ((inclusive) && (time == base))) {
        return new Date(time);
      }
    }
    return null;
  }
  



  public boolean isEquivalentTo(TimeZoneRule other)
  {
    if (!(other instanceof TimeArrayTimeZoneRule)) {
      return false;
    }
    if ((timeType == timeType) && (Arrays.equals(startTimes, startTimes)))
    {
      return super.isEquivalentTo(other);
    }
    return false;
  }
  




  public boolean isTransitionRule()
  {
    return true;
  }
  
  private long getUTC(long time, int raw, int dst)
  {
    if (timeType != 2) {
      time -= raw;
    }
    if (timeType == 0) {
      time -= dst;
    }
    return time;
  }
  






  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append(super.toString());
    buf.append(", timeType=");
    buf.append(timeType);
    buf.append(", startTimes=[");
    for (int i = 0; i < startTimes.length; i++) {
      if (i != 0) {
        buf.append(", ");
      }
      buf.append(Long.toString(startTimes[i]));
    }
    buf.append("]");
    return buf.toString();
  }
}
