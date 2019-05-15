package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.impl.PeriodFormatterData;
import com.ibm.icu.impl.duration.impl.PeriodFormatterDataService;
import java.util.TimeZone;












class BasicPeriodBuilderFactory
  implements PeriodBuilderFactory
{
  private PeriodFormatterDataService ds;
  private Settings settings;
  private static final short allBits = 255;
  
  BasicPeriodBuilderFactory(PeriodFormatterDataService ds)
  {
    this.ds = ds;
    settings = new Settings();
  }
  

  static long approximateDurationOf(TimeUnit unit) { return TimeUnit.approxDurations[ordinal]; }
  
  class Settings { boolean inUse;
    
    Settings() {}
    short uset = 255;
    TimeUnit maxUnit = TimeUnit.YEAR;
    TimeUnit minUnit = TimeUnit.MILLISECOND;
    int maxLimit;
    int minLimit;
    boolean allowZero = true;
    boolean weeksAloneOnly;
    boolean allowMillis = true;
    
    Settings setUnits(int uset) {
      if (this.uset == uset) {
        return this;
      }
      Settings result = inUse ? copy() : this;
      
      uset = ((short)uset);
      
      if ((uset & 0xFF) == 255) {
        uset = 255;
        maxUnit = TimeUnit.YEAR;
        minUnit = TimeUnit.MILLISECOND;
      } else {
        int lastUnit = -1;
        for (int i = 0; i < TimeUnit.units.length; i++) {
          if (0 != (uset & 1 << i)) {
            if (lastUnit == -1) {
              maxUnit = TimeUnit.units[i];
            }
            lastUnit = i;
          }
        }
        if (lastUnit == -1)
        {
          minUnit = (result.maxUnit = null);
        } else {
          minUnit = TimeUnit.units[lastUnit];
        }
      }
      
      return result;
    }
    
    short effectiveSet() {
      if (allowMillis) {
        return uset;
      }
      return (short)(uset & (1 << MILLISECONDordinal ^ 0xFFFFFFFF));
    }
    
    TimeUnit effectiveMinUnit() {
      if ((allowMillis) || (minUnit != TimeUnit.MILLISECOND)) {
        return minUnit;
      }
      
      int i = TimeUnit.units.length - 1; do { i--; if (i < 0) break;
      } while (0 == (uset & 1 << i));
      return TimeUnit.units[i];
      

      return TimeUnit.SECOND;
    }
    
    Settings setMaxLimit(float maxLimit) {
      int val = maxLimit <= 0.0F ? 0 : (int)(maxLimit * 1000.0F);
      if (maxLimit == val) {
        return this;
      }
      Settings result = inUse ? copy() : this;
      maxLimit = val;
      return result;
    }
    
    Settings setMinLimit(float minLimit) {
      int val = minLimit <= 0.0F ? 0 : (int)(minLimit * 1000.0F);
      if (minLimit == val) {
        return this;
      }
      Settings result = inUse ? copy() : this;
      minLimit = val;
      return result;
    }
    
    Settings setAllowZero(boolean allow) {
      if (allowZero == allow) {
        return this;
      }
      Settings result = inUse ? copy() : this;
      allowZero = allow;
      return result;
    }
    
    Settings setWeeksAloneOnly(boolean weeksAlone) {
      if (weeksAloneOnly == weeksAlone) {
        return this;
      }
      Settings result = inUse ? copy() : this;
      weeksAloneOnly = weeksAlone;
      return result;
    }
    
    Settings setAllowMilliseconds(boolean allowMillis) {
      if (this.allowMillis == allowMillis) {
        return this;
      }
      Settings result = inUse ? copy() : this;
      allowMillis = allowMillis;
      return result;
    }
    
    Settings setLocale(String localeName) {
      PeriodFormatterData data = ds.get(localeName);
      return setAllowZero(data.allowZero()).setWeeksAloneOnly(data.weeksAloneOnly()).setAllowMilliseconds(data.useMilliseconds() != 1);
    }
    


    Settings setInUse()
    {
      inUse = true;
      return this;
    }
    
    Period createLimited(long duration, boolean inPast) {
      if (maxLimit > 0) {
        long maxUnitDuration = BasicPeriodBuilderFactory.approximateDurationOf(maxUnit);
        if (duration * 1000L > maxLimit * maxUnitDuration) {
          return Period.moreThan(maxLimit / 1000.0F, maxUnit).inPast(inPast);
        }
      }
      
      if (minLimit > 0) {
        TimeUnit emu = effectiveMinUnit();
        long emud = BasicPeriodBuilderFactory.approximateDurationOf(emu);
        long eml = emu == minUnit ? minLimit : Math.max(1000L, BasicPeriodBuilderFactory.approximateDurationOf(minUnit) * minLimit / emud);
        
        if (duration * 1000L < eml * emud) {
          return Period.lessThan((float)eml / 1000.0F, emu).inPast(inPast);
        }
      }
      return null;
    }
    
    public Settings copy() {
      Settings result = new Settings(BasicPeriodBuilderFactory.this);
      inUse = inUse;
      uset = uset;
      maxUnit = maxUnit;
      minUnit = minUnit;
      maxLimit = maxLimit;
      minLimit = minLimit;
      allowZero = allowZero;
      weeksAloneOnly = weeksAloneOnly;
      allowMillis = allowMillis;
      return result;
    }
  }
  
  public PeriodBuilderFactory setAvailableUnitRange(TimeUnit minUnit, TimeUnit maxUnit)
  {
    int uset = 0;
    for (int i = ordinal; i <= ordinal; i++) {
      uset |= 1 << i;
    }
    if (uset == 0) {
      throw new IllegalArgumentException("range " + minUnit + " to " + maxUnit + " is empty");
    }
    settings = settings.setUnits(uset);
    return this;
  }
  
  public PeriodBuilderFactory setUnitIsAvailable(TimeUnit unit, boolean available)
  {
    int uset = settings.uset;
    if (available) {
      uset |= 1 << ordinal;
    } else {
      uset &= (1 << ordinal ^ 0xFFFFFFFF);
    }
    settings = settings.setUnits(uset);
    return this;
  }
  
  public PeriodBuilderFactory setMaxLimit(float maxLimit) {
    settings = settings.setMaxLimit(maxLimit);
    return this;
  }
  
  public PeriodBuilderFactory setMinLimit(float minLimit) {
    settings = settings.setMinLimit(minLimit);
    return this;
  }
  
  public PeriodBuilderFactory setAllowZero(boolean allow) {
    settings = settings.setAllowZero(allow);
    return this;
  }
  
  public PeriodBuilderFactory setWeeksAloneOnly(boolean aloneOnly) {
    settings = settings.setWeeksAloneOnly(aloneOnly);
    return this;
  }
  
  public PeriodBuilderFactory setAllowMilliseconds(boolean allow) {
    settings = settings.setAllowMilliseconds(allow);
    return this;
  }
  
  public PeriodBuilderFactory setLocale(String localeName) {
    settings = settings.setLocale(localeName);
    return this;
  }
  
  public PeriodBuilderFactory setTimeZone(TimeZone timeZone)
  {
    return this;
  }
  
  private Settings getSettings() {
    if (settings.effectiveSet() == 0) {
      return null;
    }
    return settings.setInUse();
  }
  






  public PeriodBuilder getFixedUnitBuilder(TimeUnit unit)
  {
    return FixedUnitBuilder.get(unit, getSettings());
  }
  





  public PeriodBuilder getSingleUnitBuilder()
  {
    return SingleUnitBuilder.get(getSettings());
  }
  







  public PeriodBuilder getOneOrTwoUnitBuilder()
  {
    return OneOrTwoUnitBuilder.get(getSettings());
  }
  






  public PeriodBuilder getMultiUnitBuilder(int periodCount)
  {
    return MultiUnitBuilder.get(periodCount, getSettings());
  }
}
