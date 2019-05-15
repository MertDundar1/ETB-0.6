package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.impl.PeriodFormatterDataService;
import com.ibm.icu.impl.duration.impl.ResourceBasedPeriodFormatterDataService;
import java.util.Collection;















public class BasicPeriodFormatterService
  implements PeriodFormatterService
{
  private static BasicPeriodFormatterService instance;
  private PeriodFormatterDataService ds;
  
  public static BasicPeriodFormatterService getInstance()
  {
    if (instance == null) {
      PeriodFormatterDataService ds = ResourceBasedPeriodFormatterDataService.getInstance();
      
      instance = new BasicPeriodFormatterService(ds);
    }
    return instance;
  }
  





  public BasicPeriodFormatterService(PeriodFormatterDataService ds)
  {
    this.ds = ds;
  }
  
  public DurationFormatterFactory newDurationFormatterFactory() {
    return new BasicDurationFormatterFactory(this);
  }
  
  public PeriodFormatterFactory newPeriodFormatterFactory() {
    return new BasicPeriodFormatterFactory(ds);
  }
  
  public PeriodBuilderFactory newPeriodBuilderFactory() {
    return new BasicPeriodBuilderFactory(ds);
  }
  
  public Collection<String> getAvailableLocaleNames() {
    return ds.getAvailableLocales();
  }
}
