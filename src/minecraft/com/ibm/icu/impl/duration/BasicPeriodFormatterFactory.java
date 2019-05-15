package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.impl.PeriodFormatterData;
import com.ibm.icu.impl.duration.impl.PeriodFormatterDataService;
import java.util.Locale;















































public class BasicPeriodFormatterFactory
  implements PeriodFormatterFactory
{
  private final PeriodFormatterDataService ds;
  private PeriodFormatterData data;
  private Customizations customizations;
  private boolean customizationsInUse;
  private String localeName;
  
  BasicPeriodFormatterFactory(PeriodFormatterDataService ds)
  {
    this.ds = ds;
    customizations = new Customizations();
    localeName = Locale.getDefault().toString();
  }
  




  public static BasicPeriodFormatterFactory getDefault()
  {
    return (BasicPeriodFormatterFactory)BasicPeriodFormatterService.getInstance().newPeriodFormatterFactory();
  }
  



  public PeriodFormatterFactory setLocale(String localeName)
  {
    data = null;
    this.localeName = localeName;
    return this;
  }
  





  public PeriodFormatterFactory setDisplayLimit(boolean display)
  {
    updateCustomizationsdisplayLimit = display;
    return this;
  }
  




  public boolean getDisplayLimit()
  {
    return customizations.displayLimit;
  }
  





  public PeriodFormatterFactory setDisplayPastFuture(boolean display)
  {
    updateCustomizationsdisplayDirection = display;
    return this;
  }
  




  public boolean getDisplayPastFuture()
  {
    return customizations.displayDirection;
  }
  





  public PeriodFormatterFactory setSeparatorVariant(int variant)
  {
    updateCustomizationsseparatorVariant = ((byte)variant);
    return this;
  }
  




  public int getSeparatorVariant()
  {
    return customizations.separatorVariant;
  }
  





  public PeriodFormatterFactory setUnitVariant(int variant)
  {
    updateCustomizationsunitVariant = ((byte)variant);
    return this;
  }
  




  public int getUnitVariant()
  {
    return customizations.unitVariant;
  }
  





  public PeriodFormatterFactory setCountVariant(int variant)
  {
    updateCustomizationscountVariant = ((byte)variant);
    return this;
  }
  




  public int getCountVariant()
  {
    return customizations.countVariant;
  }
  
  public PeriodFormatter getFormatter() {
    customizationsInUse = true;
    return new BasicPeriodFormatter(this, localeName, getData(), customizations);
  }
  
  private Customizations updateCustomizations()
  {
    if (customizationsInUse) {
      customizations = customizations.copy();
      customizationsInUse = false;
    }
    return customizations;
  }
  
  PeriodFormatterData getData()
  {
    if (data == null) {
      data = ds.get(localeName);
    }
    return data;
  }
  


  PeriodFormatterData getData(String locName) { return ds.get(locName); }
  
  static class Customizations {
    Customizations() {}
    
    boolean displayLimit = true;
    boolean displayDirection = true;
    byte separatorVariant = 2;
    byte unitVariant = 0;
    byte countVariant = 0;
    
    public Customizations copy() {
      Customizations result = new Customizations();
      displayLimit = displayLimit;
      displayDirection = displayDirection;
      separatorVariant = separatorVariant;
      unitVariant = unitVariant;
      countVariant = countVariant;
      return result;
    }
  }
}
