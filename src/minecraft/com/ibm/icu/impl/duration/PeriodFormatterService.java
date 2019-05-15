package com.ibm.icu.impl.duration;

import java.util.Collection;

public abstract interface PeriodFormatterService
{
  public abstract DurationFormatterFactory newDurationFormatterFactory();
  
  public abstract PeriodFormatterFactory newPeriodFormatterFactory();
  
  public abstract PeriodBuilderFactory newPeriodBuilderFactory();
  
  public abstract Collection<String> getAvailableLocaleNames();
}
