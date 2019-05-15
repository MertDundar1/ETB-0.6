package com.ibm.icu.impl.duration;

import java.util.Date;
import java.util.TimeZone;

public abstract interface DurationFormatter
{
  public abstract String formatDurationFromNowTo(Date paramDate);
  
  public abstract String formatDurationFromNow(long paramLong);
  
  public abstract String formatDurationFrom(long paramLong1, long paramLong2);
  
  public abstract DurationFormatter withLocale(String paramString);
  
  public abstract DurationFormatter withTimeZone(TimeZone paramTimeZone);
}
