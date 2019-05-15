package com.ibm.icu.impl.duration;

import java.util.Locale;
import java.util.TimeZone;


















class BasicDurationFormatterFactory
  implements DurationFormatterFactory
{
  private BasicPeriodFormatterService ps;
  private PeriodFormatter formatter;
  private PeriodBuilder builder;
  private DateFormatter fallback;
  private long fallbackLimit;
  private String localeName;
  private TimeZone timeZone;
  private BasicDurationFormatter f;
  
  BasicDurationFormatterFactory(BasicPeriodFormatterService ps)
  {
    this.ps = ps;
    localeName = Locale.getDefault().toString();
    timeZone = TimeZone.getDefault();
  }
  






  public DurationFormatterFactory setPeriodFormatter(PeriodFormatter formatter)
  {
    if (formatter != this.formatter) {
      this.formatter = formatter;
      reset();
    }
    return this;
  }
  






  public DurationFormatterFactory setPeriodBuilder(PeriodBuilder builder)
  {
    if (builder != this.builder) {
      this.builder = builder;
      reset();
    }
    return this;
  }
  





  public DurationFormatterFactory setFallback(DateFormatter fallback)
  {
    boolean doReset = this.fallback != null;
    

    if (doReset) {
      this.fallback = fallback;
      reset();
    }
    return this;
  }
  





  public DurationFormatterFactory setFallbackLimit(long fallbackLimit)
  {
    if (fallbackLimit < 0L) {
      fallbackLimit = 0L;
    }
    if (fallbackLimit != this.fallbackLimit) {
      this.fallbackLimit = fallbackLimit;
      reset();
    }
    return this;
  }
  






  public DurationFormatterFactory setLocale(String localeName)
  {
    if (!localeName.equals(this.localeName)) {
      this.localeName = localeName;
      if (builder != null) {
        builder = builder.withLocale(localeName);
      }
      if (formatter != null) {
        formatter = formatter.withLocale(localeName);
      }
      reset();
    }
    return this;
  }
  






  public DurationFormatterFactory setTimeZone(TimeZone timeZone)
  {
    if (!timeZone.equals(this.timeZone)) {
      this.timeZone = timeZone;
      if (builder != null) {
        builder = builder.withTimeZone(timeZone);
      }
      reset();
    }
    return this;
  }
  




  public DurationFormatter getFormatter()
  {
    if (f == null) {
      if (fallback != null) {
        fallback = fallback.withLocale(localeName).withTimeZone(timeZone);
      }
      formatter = getPeriodFormatter();
      builder = getPeriodBuilder();
      
      f = createFormatter();
    }
    return f;
  }
  




  public PeriodFormatter getPeriodFormatter()
  {
    if (formatter == null) {
      formatter = ps.newPeriodFormatterFactory().setLocale(localeName).getFormatter();
    }
    

    return formatter;
  }
  




  public PeriodBuilder getPeriodBuilder()
  {
    if (builder == null) {
      builder = ps.newPeriodBuilderFactory().setLocale(localeName).setTimeZone(timeZone).getSingleUnitBuilder();
    }
    


    return builder;
  }
  





  public DateFormatter getFallback()
  {
    return fallback;
  }
  




  public long getFallbackLimit()
  {
    return fallback == null ? 0L : fallbackLimit;
  }
  




  public String getLocaleName()
  {
    return localeName;
  }
  




  public TimeZone getTimeZone()
  {
    return timeZone;
  }
  


  protected BasicDurationFormatter createFormatter()
  {
    return new BasicDurationFormatter(formatter, builder, fallback, fallbackLimit, localeName, timeZone);
  }
  






  protected void reset()
  {
    f = null;
  }
}
