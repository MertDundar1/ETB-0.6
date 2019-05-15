package com.ibm.icu.util;

import java.io.Serializable;













































































public class DateTimeRule
  implements Serializable
{
  private static final long serialVersionUID = 2183055795738051443L;
  public static final int DOM = 0;
  public static final int DOW = 1;
  public static final int DOW_GEQ_DOM = 2;
  public static final int DOW_LEQ_DOM = 3;
  public static final int WALL_TIME = 0;
  public static final int STANDARD_TIME = 1;
  public static final int UTC_TIME = 2;
  private final int dateRuleType;
  private final int month;
  private final int dayOfMonth;
  private final int dayOfWeek;
  private final int weekInMonth;
  private final int timeRuleType;
  private final int millisInDay;
  
  public DateTimeRule(int month, int dayOfMonth, int millisInDay, int timeType)
  {
    dateRuleType = 0;
    this.month = month;
    this.dayOfMonth = dayOfMonth;
    
    this.millisInDay = millisInDay;
    timeRuleType = timeType;
    

    dayOfWeek = 0;
    weekInMonth = 0;
  }
  
















  public DateTimeRule(int month, int weekInMonth, int dayOfWeek, int millisInDay, int timeType)
  {
    dateRuleType = 1;
    this.month = month;
    this.weekInMonth = weekInMonth;
    this.dayOfWeek = dayOfWeek;
    
    this.millisInDay = millisInDay;
    timeRuleType = timeType;
    

    dayOfMonth = 0;
  }
  
















  public DateTimeRule(int month, int dayOfMonth, int dayOfWeek, boolean after, int millisInDay, int timeType)
  {
    dateRuleType = (after ? 2 : 3);
    this.month = month;
    this.dayOfMonth = dayOfMonth;
    this.dayOfWeek = dayOfWeek;
    
    this.millisInDay = millisInDay;
    timeRuleType = timeType;
    

    weekInMonth = 0;
  }
  






  public int getDateRuleType()
  {
    return dateRuleType;
  }
  






  public int getRuleMonth()
  {
    return month;
  }
  







  public int getRuleDayOfMonth()
  {
    return dayOfMonth;
  }
  







  public int getRuleDayOfWeek()
  {
    return dayOfWeek;
  }
  








  public int getRuleWeekInMonth()
  {
    return weekInMonth;
  }
  







  public int getTimeRuleType()
  {
    return timeRuleType;
  }
  






  public int getRuleMillisInDay()
  {
    return millisInDay;
  }
  
  private static final String[] DOWSTR = { "", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
  private static final String[] MONSTR = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
  






  public String toString()
  {
    String sDate = null;
    String sTimeRuleType = null;
    
    switch (dateRuleType) {
    case 0: 
      sDate = Integer.toString(dayOfMonth);
      break;
    case 1: 
      sDate = Integer.toString(weekInMonth) + DOWSTR[dayOfWeek];
      break;
    case 2: 
      sDate = DOWSTR[dayOfWeek] + ">=" + Integer.toString(dayOfMonth);
      break;
    case 3: 
      sDate = DOWSTR[dayOfWeek] + "<=" + Integer.toString(dayOfMonth);
    }
    
    
    switch (timeRuleType) {
    case 0: 
      sTimeRuleType = "WALL";
      break;
    case 1: 
      sTimeRuleType = "STD";
      break;
    case 2: 
      sTimeRuleType = "UTC";
    }
    
    
    int time = millisInDay;
    int millis = time % 1000;
    time /= 1000;
    int secs = time % 60;
    time /= 60;
    int mins = time % 60;
    int hours = time / 60;
    
    StringBuilder buf = new StringBuilder();
    buf.append("month=");
    buf.append(MONSTR[month]);
    buf.append(", date=");
    buf.append(sDate);
    buf.append(", time=");
    buf.append(hours);
    buf.append(":");
    buf.append(mins / 10);
    buf.append(mins % 10);
    buf.append(":");
    buf.append(secs / 10);
    buf.append(secs % 10);
    buf.append(".");
    buf.append(millis / 100);
    buf.append(millis / 10 % 10);
    buf.append(millis % 10);
    buf.append("(");
    buf.append(sTimeRuleType);
    buf.append(")");
    return buf.toString();
  }
}
