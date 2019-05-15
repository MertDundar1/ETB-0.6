package com.ibm.icu.text;

import com.ibm.icu.impl.CurrencyData;
import com.ibm.icu.impl.CurrencyData.CurrencyDisplayInfoProvider;
import com.ibm.icu.util.ULocale;
import java.util.Map;
























public abstract class CurrencyDisplayNames
{
  public static CurrencyDisplayNames getInstance(ULocale locale)
  {
    return CurrencyData.provider.getInstance(locale, true);
  }
  













  public static CurrencyDisplayNames getInstance(ULocale locale, boolean noSubstitute)
  {
    return CurrencyData.provider.getInstance(locale, !noSubstitute);
  }
  


  /**
   * @deprecated
   */
  public static boolean hasData()
  {
    return CurrencyData.provider.hasData();
  }
  
  public abstract ULocale getULocale();
  
  public abstract String getSymbol(String paramString);
  
  public abstract String getName(String paramString);
  
  public abstract String getPluralName(String paramString1, String paramString2);
  
  public abstract Map<String, String> symbolMap();
  
  public abstract Map<String, String> nameMap();
  
  /**
   * @deprecated
   */
  protected CurrencyDisplayNames() {}
}
