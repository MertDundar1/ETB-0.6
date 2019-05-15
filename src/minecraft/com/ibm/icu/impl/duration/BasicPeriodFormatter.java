package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.impl.PeriodFormatterData;

















class BasicPeriodFormatter
  implements PeriodFormatter
{
  private BasicPeriodFormatterFactory factory;
  private String localeName;
  private PeriodFormatterData data;
  private BasicPeriodFormatterFactory.Customizations customs;
  
  BasicPeriodFormatter(BasicPeriodFormatterFactory factory, String localeName, PeriodFormatterData data, BasicPeriodFormatterFactory.Customizations customs)
  {
    this.factory = factory;
    this.localeName = localeName;
    this.data = data;
    this.customs = customs;
  }
  
  public String format(Period period) {
    if (!period.isSet()) {
      throw new IllegalArgumentException("period is not set");
    }
    return format(timeLimit, inFuture, counts);
  }
  
  public PeriodFormatter withLocale(String locName) {
    if (!localeName.equals(locName)) {
      PeriodFormatterData newData = factory.getData(locName);
      return new BasicPeriodFormatter(factory, locName, newData, customs);
    }
    
    return this;
  }
  
  private String format(int tl, boolean inFuture, int[] counts) {
    int mask = 0;
    for (int i = 0; i < counts.length; i++) {
      if (counts[i] > 0) {
        mask |= 1 << i;
      }
    }
    




    if (!data.allowZero()) {
      int i = 0; for (int m = 1; i < counts.length; m <<= 1) {
        if (((mask & m) != 0) && (counts[i] == 1)) {
          mask &= (m ^ 0xFFFFFFFF);
        }
        i++;
      }
      


      if (mask == 0) {
        return null;
      }
    }
    



    boolean forceD3Seconds = false;
    if ((data.useMilliseconds() != 0) && ((mask & 1 << MILLISECONDordinal) != 0))
    {
      int sx = SECONDordinal;
      int mx = MILLISECONDordinal;
      int sf = 1 << sx;
      int mf = 1 << mx;
      switch (data.useMilliseconds())
      {
      case 2: 
        if ((mask & sf) != 0) {
          counts[sx] += (counts[mx] - 1) / 1000;
          mask &= (mf ^ 0xFFFFFFFF);
          forceD3Seconds = true;
        }
        
        break;
      case 1: 
        if ((mask & sf) == 0) {
          mask |= sf;
          counts[sx] = 1;
        }
        counts[sx] += (counts[mx] - 1) / 1000;
        mask &= (mf ^ 0xFFFFFFFF);
        forceD3Seconds = true;
      }
      
    }
    

    int first = 0;
    int last = counts.length - 1;
    while ((first < counts.length) && ((mask & 1 << first) == 0)) first++;
    while ((last > first) && ((mask & 1 << last) == 0)) { last--;
    }
    
    boolean isZero = true;
    for (int i = first; i <= last; i++) {
      if (((mask & 1 << i) != 0) && (counts[i] > 1)) {
        isZero = false;
        break;
      }
    }
    
    StringBuffer sb = new StringBuffer();
    


    if ((!customs.displayLimit) || (isZero)) {
      tl = 0;
    }
    
    int td;
    
    int td;
    if ((!customs.displayDirection) || (isZero)) {
      td = 0;
    } else {
      td = inFuture ? 2 : 1;
    }
    



    boolean useDigitPrefix = data.appendPrefix(tl, td, sb);
    

    boolean multiple = first != last;
    boolean wasSkipped = true;
    boolean skipped = false;
    boolean countSep = customs.separatorVariant != 0;
    

    int i = first; for (int j = i; i <= last; i = j) {
      if (skipped)
      {
        data.appendSkippedUnit(sb);
        skipped = false;
        wasSkipped = true;
      }
      for (;;) {
        j++; if ((j >= last) || ((mask & 1 << j) != 0)) break;
        skipped = true;
      }
      
      TimeUnit unit = TimeUnit.units[i];
      int count = counts[i] - 1;
      
      int cv = customs.countVariant;
      if (i == last) {
        if (forceD3Seconds) {
          cv = 5;
        }
      }
      else {
        cv = 0;
      }
      boolean isLast = i == last;
      boolean mustSkip = data.appendUnit(unit, count, cv, customs.unitVariant, countSep, useDigitPrefix, multiple, isLast, wasSkipped, sb);
      
      skipped |= mustSkip;
      wasSkipped = false;
      
      if ((customs.separatorVariant != 0) && (j <= last)) {
        boolean afterFirst = i == first;
        boolean beforeLast = j == last;
        boolean fullSep = customs.separatorVariant == 2;
        useDigitPrefix = data.appendUnitSeparator(unit, fullSep, afterFirst, beforeLast, sb);
      } else {
        useDigitPrefix = false;
      }
    }
    data.appendSuffix(tl, td, sb);
    
    return sb.toString();
  }
}
