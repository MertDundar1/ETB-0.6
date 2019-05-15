package com.ibm.icu.util;

import java.io.Serializable;




















public final class DateInterval
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final long fromDate;
  private final long toDate;
  
  public DateInterval(long from, long to)
  {
    fromDate = from;
    toDate = to;
  }
  





  public long getFromDate()
  {
    return fromDate;
  }
  





  public long getToDate()
  {
    return toDate;
  }
  



  public boolean equals(Object a)
  {
    if ((a instanceof DateInterval)) {
      DateInterval di = (DateInterval)a;
      return (fromDate == fromDate) && (toDate == toDate);
    }
    return false;
  }
  



  public int hashCode()
  {
    return (int)(fromDate + toDate);
  }
  



  public String toString()
  {
    return String.valueOf(fromDate) + " " + String.valueOf(toDate);
  }
}
