package com.ibm.icu.impl.duration;

import java.util.TimeZone;

public abstract interface DurationFormatterFactory
{
  public abstract DurationFormatterFactory setPeriodFormatter(PeriodFormatter paramPeriodFormatter);
  
  public abstract DurationFormatterFactory setPeriodBuilder(PeriodBuilder paramPeriodBuilder);
  
  public abstract DurationFormatterFactory setFallback(DateFormatter paramDateFormatter);
  
  public abstract DurationFormatterFactory setFallbackLimit(long paramLong);
  
  public abstract DurationFormatterFactory setLocale(String paramString);
  
  public abstract DurationFormatterFactory setTimeZone(TimeZone paramTimeZone);
  
  public abstract DurationFormatter getFormatter();
}
