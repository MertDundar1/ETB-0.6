package com.ibm.icu.impl;

import com.ibm.icu.text.TimeZoneNames;
import com.ibm.icu.text.TimeZoneNames.Factory;
import com.ibm.icu.util.ULocale;










public class TimeZoneNamesFactoryImpl
  extends TimeZoneNames.Factory
{
  public TimeZoneNamesFactoryImpl() {}
  
  public TimeZoneNames getTimeZoneNames(ULocale locale)
  {
    return new TimeZoneNamesImpl(locale);
  }
}
