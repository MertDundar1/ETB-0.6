package com.ibm.icu.util;

import java.util.Date;

public abstract interface DateRule
{
  public abstract Date firstAfter(Date paramDate);
  
  public abstract Date firstBetween(Date paramDate1, Date paramDate2);
  
  public abstract boolean isOn(Date paramDate);
  
  public abstract boolean isBetween(Date paramDate1, Date paramDate2);
}
