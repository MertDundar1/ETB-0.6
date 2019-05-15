package com.ibm.icu.impl.duration;

public abstract interface PeriodFormatterFactory
{
  public abstract PeriodFormatterFactory setLocale(String paramString);
  
  public abstract PeriodFormatterFactory setDisplayLimit(boolean paramBoolean);
  
  public abstract PeriodFormatterFactory setDisplayPastFuture(boolean paramBoolean);
  
  public abstract PeriodFormatterFactory setSeparatorVariant(int paramInt);
  
  public abstract PeriodFormatterFactory setUnitVariant(int paramInt);
  
  public abstract PeriodFormatterFactory setCountVariant(int paramInt);
  
  public abstract PeriodFormatter getFormatter();
}
