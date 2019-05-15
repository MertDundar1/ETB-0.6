package com.ibm.icu.impl.duration;

import java.util.TimeZone;

public abstract interface PeriodBuilder
{
  public abstract Period create(long paramLong);
  
  public abstract Period createWithReferenceDate(long paramLong1, long paramLong2);
  
  public abstract PeriodBuilder withLocale(String paramString);
  
  public abstract PeriodBuilder withTimeZone(TimeZone paramTimeZone);
}
