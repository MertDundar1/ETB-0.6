package com.ibm.icu.impl.duration;

public abstract interface PeriodFormatter
{
  public abstract String format(Period paramPeriod);
  
  public abstract PeriodFormatter withLocale(String paramString);
}
