package com.ibm.icu.impl.duration;






public final class TimeUnit
{
  final String name;
  



  final byte ordinal;
  




  private TimeUnit(String name, int ordinal)
  {
    this.name = name;
    this.ordinal = ((byte)ordinal);
  }
  
  public String toString() {
    return name;
  }
  

  public static final TimeUnit YEAR = new TimeUnit("year", 0);
  

  public static final TimeUnit MONTH = new TimeUnit("month", 1);
  

  public static final TimeUnit WEEK = new TimeUnit("week", 2);
  

  public static final TimeUnit DAY = new TimeUnit("day", 3);
  

  public static final TimeUnit HOUR = new TimeUnit("hour", 4);
  

  public static final TimeUnit MINUTE = new TimeUnit("minute", 5);
  

  public static final TimeUnit SECOND = new TimeUnit("second", 6);
  

  public static final TimeUnit MILLISECOND = new TimeUnit("millisecond", 7);
  
  public TimeUnit larger()
  {
    return ordinal == 0 ? null : units[(ordinal - 1)];
  }
  
  public TimeUnit smaller()
  {
    return ordinal == units.length - 1 ? null : units[(ordinal + 1)];
  }
  

  static final TimeUnit[] units = { YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND };
  


  public int ordinal()
  {
    return ordinal;
  }
  






  static final long[] approxDurations = { 31557600000L, 2630880000L, 604800000L, 86400000L, 3600000L, 60000L, 1000L, 1L };
}
