package com.ibm.icu.impl.duration.impl;

import java.util.Collection;

public abstract class PeriodFormatterDataService
{
  public PeriodFormatterDataService() {}
  
  public abstract PeriodFormatterData get(String paramString);
  
  public abstract Collection<String> getAvailableLocales();
}
