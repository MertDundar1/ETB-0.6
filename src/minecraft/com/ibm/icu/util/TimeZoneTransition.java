package com.ibm.icu.util;






public class TimeZoneTransition
{
  private final TimeZoneRule from;
  




  private final TimeZoneRule to;
  




  private final long time;
  





  public TimeZoneTransition(long time, TimeZoneRule from, TimeZoneRule to)
  {
    this.time = time;
    this.from = from;
    this.to = to;
  }
  






  public long getTime()
  {
    return time;
  }
  






  public TimeZoneRule getTo()
  {
    return to;
  }
  






  public TimeZoneRule getFrom()
  {
    return from;
  }
  






  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("time=" + time);
    buf.append(", from={" + from + "}");
    buf.append(", to={" + to + "}");
    return buf.toString();
  }
}
