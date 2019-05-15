package com.ibm.icu.impl.duration;

import java.util.Date;
import java.util.TimeZone;

public abstract interface DateFormatter
{
  public abstract String format(Date paramDate);
  
  public abstract String format(long paramLong);
  
  public abstract DateFormatter withLocale(String paramString);
  
  public abstract DateFormatter withTimeZone(TimeZone paramTimeZone);
}
