package com.ibm.icu.impl;

import java.util.Date;


































public class TimeZoneAdapter
  extends java.util.TimeZone
{
  static final long serialVersionUID = -2040072218820018557L;
  private com.ibm.icu.util.TimeZone zone;
  
  public static java.util.TimeZone wrap(com.ibm.icu.util.TimeZone tz)
  {
    return new TimeZoneAdapter(tz);
  }
  


  public com.ibm.icu.util.TimeZone unwrap()
  {
    return zone;
  }
  


  public TimeZoneAdapter(com.ibm.icu.util.TimeZone zone)
  {
    this.zone = zone;
    super.setID(zone.getID());
  }
  


  public void setID(String ID)
  {
    super.setID(ID);
    zone.setID(ID);
  }
  


  public boolean hasSameRules(java.util.TimeZone other)
  {
    return ((other instanceof TimeZoneAdapter)) && (zone.hasSameRules(zone));
  }
  




  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis)
  {
    return zone.getOffset(era, year, month, day, dayOfWeek, millis);
  }
  


  public int getRawOffset()
  {
    return zone.getRawOffset();
  }
  


  public void setRawOffset(int offsetMillis)
  {
    zone.setRawOffset(offsetMillis);
  }
  


  public boolean useDaylightTime()
  {
    return zone.useDaylightTime();
  }
  


  public boolean inDaylightTime(Date date)
  {
    return zone.inDaylightTime(date);
  }
  


  public Object clone()
  {
    return new TimeZoneAdapter((com.ibm.icu.util.TimeZone)zone.clone());
  }
  


  public synchronized int hashCode()
  {
    return zone.hashCode();
  }
  


  public boolean equals(Object obj)
  {
    if ((obj instanceof TimeZoneAdapter)) {
      obj = zone;
    }
    return zone.equals(obj);
  }
  



  public String toString()
  {
    return "TimeZoneAdapter: " + zone.toString();
  }
}
