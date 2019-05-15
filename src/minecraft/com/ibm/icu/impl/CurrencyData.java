package com.ibm.icu.impl;

import com.ibm.icu.util.ULocale;

public class CurrencyData
{
  public static final CurrencyDisplayInfoProvider provider;
  public CurrencyData() {}
  
  public static abstract interface CurrencyDisplayInfoProvider
  {
    public abstract CurrencyData.CurrencyDisplayInfo getInstance(ULocale paramULocale, boolean paramBoolean);
    
    public abstract boolean hasData();
  }
  
  public static abstract class CurrencyDisplayInfo extends com.ibm.icu.text.CurrencyDisplayNames
  {
    public CurrencyDisplayInfo() {}
    
    public abstract java.util.Map<String, String> getUnitPatterns();
    
    public abstract CurrencyData.CurrencyFormatInfo getFormatInfo(String paramString);
    
    public abstract CurrencyData.CurrencySpacingInfo getSpacingInfo();
  }
  
  public static final class CurrencyFormatInfo
  {
    public final String currencyPattern;
    public final char monetarySeparator;
    public final char monetaryGroupingSeparator;
    
    public CurrencyFormatInfo(String currencyPattern, char monetarySeparator, char monetaryGroupingSeparator)
    {
      this.currencyPattern = currencyPattern;
      this.monetarySeparator = monetarySeparator;
      this.monetaryGroupingSeparator = monetaryGroupingSeparator;
    }
  }
  
  public static final class CurrencySpacingInfo {
    public final String beforeCurrencyMatch;
    public final String beforeContextMatch;
    public final String beforeInsert;
    public final String afterCurrencyMatch;
    public final String afterContextMatch;
    public final String afterInsert;
    private static final String DEFAULT_CUR_MATCH = "[:letter:]";
    private static final String DEFAULT_CTX_MATCH = "[:digit:]";
    private static final String DEFAULT_INSERT = " ";
    
    public CurrencySpacingInfo(String beforeCurrencyMatch, String beforeContextMatch, String beforeInsert, String afterCurrencyMatch, String afterContextMatch, String afterInsert) { this.beforeCurrencyMatch = beforeCurrencyMatch;
      this.beforeContextMatch = beforeContextMatch;
      this.beforeInsert = beforeInsert;
      this.afterCurrencyMatch = afterCurrencyMatch;
      this.afterContextMatch = afterContextMatch;
      this.afterInsert = afterInsert;
    }
    





    public static final CurrencySpacingInfo DEFAULT = new CurrencySpacingInfo("[:letter:]", "[:digit:]", " ", "[:letter:]", "[:digit:]", " ");
  }
  

  static
  {
    CurrencyDisplayInfoProvider temp = null;
    try {
      Class<?> clzz = Class.forName("com.ibm.icu.impl.ICUCurrencyDisplayInfoProvider");
      temp = (CurrencyDisplayInfoProvider)clzz.newInstance();
    } catch (Throwable t) {
      temp = new CurrencyDisplayInfoProvider() {
        public CurrencyData.CurrencyDisplayInfo getInstance(ULocale locale, boolean withFallback) {
          return CurrencyData.DefaultInfo.getWithFallback(withFallback);
        }
        
        public boolean hasData() {
          return false;
        }
      };
    }
    provider = temp;
  }
  
  public static class DefaultInfo extends CurrencyData.CurrencyDisplayInfo {
    private final boolean fallback;
    
    private DefaultInfo(boolean fallback) {
      this.fallback = fallback;
    }
    
    public static final CurrencyData.CurrencyDisplayInfo getWithFallback(boolean fallback) {
      return fallback ? FALLBACK_INSTANCE : NO_FALLBACK_INSTANCE;
    }
    
    public String getName(String isoCode)
    {
      return fallback ? isoCode : null;
    }
    
    public String getPluralName(String isoCode, String pluralType)
    {
      return fallback ? isoCode : null;
    }
    
    public String getSymbol(String isoCode)
    {
      return fallback ? isoCode : null;
    }
    
    public java.util.Map<String, String> symbolMap()
    {
      return java.util.Collections.emptyMap();
    }
    
    public java.util.Map<String, String> nameMap()
    {
      return java.util.Collections.emptyMap();
    }
    
    public ULocale getULocale()
    {
      return ULocale.ROOT;
    }
    
    public java.util.Map<String, String> getUnitPatterns()
    {
      if (fallback) {
        return java.util.Collections.emptyMap();
      }
      return null;
    }
    
    public CurrencyData.CurrencyFormatInfo getFormatInfo(String isoCode)
    {
      return null;
    }
    
    public CurrencyData.CurrencySpacingInfo getSpacingInfo()
    {
      return fallback ? CurrencyData.CurrencySpacingInfo.DEFAULT : null;
    }
    
    private static final CurrencyData.CurrencyDisplayInfo FALLBACK_INSTANCE = new DefaultInfo(true);
    private static final CurrencyData.CurrencyDisplayInfo NO_FALLBACK_INSTANCE = new DefaultInfo(false);
  }
}
