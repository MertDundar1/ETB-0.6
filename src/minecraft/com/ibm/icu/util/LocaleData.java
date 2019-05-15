package com.ibm.icu.util;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.UnicodeSet;
import java.util.MissingResourceException;






















































































public final class LocaleData
{
  private static final String MEASUREMENT_SYSTEM = "MeasurementSystem";
  private static final String PAPER_SIZE = "PaperSize";
  private static final String LOCALE_DISPLAY_PATTERN = "localeDisplayPattern";
  private static final String PATTERN = "pattern";
  private static final String SEPARATOR = "separator";
  private boolean noSubstitute;
  private ICUResourceBundle bundle;
  private ICUResourceBundle langBundle;
  public static final int ES_STANDARD = 0;
  public static final int ES_AUXILIARY = 1;
  public static final int ES_INDEX = 2;
  /**
   * @deprecated
   */
  public static final int ES_CURRENCY = 3;
  public static final int ES_PUNCTUATION = 4;
  public static final int ES_COUNT = 5;
  public static final int QUOTATION_START = 0;
  public static final int QUOTATION_END = 1;
  public static final int ALT_QUOTATION_START = 2;
  public static final int ALT_QUOTATION_END = 3;
  public static final int DELIMITER_COUNT = 4;
  
  private LocaleData() {}
  
  public static UnicodeSet getExemplarSet(ULocale locale, int options)
  {
    return getInstance(locale).getExemplarSet(options, 0);
  }
  
















  public static UnicodeSet getExemplarSet(ULocale locale, int options, int extype)
  {
    return getInstance(locale).getExemplarSet(options, extype);
  }
  















  public UnicodeSet getExemplarSet(int options, int extype)
  {
    String[] exemplarSetTypes = { "ExemplarCharacters", "AuxExemplarCharacters", "ExemplarCharactersIndex", "ExemplarCharactersCurrency", "ExemplarCharactersPunctuation" };
    




    if (extype == 3)
    {
      return new UnicodeSet();
    }
    try
    {
      ICUResourceBundle stringBundle = (ICUResourceBundle)bundle.get(exemplarSetTypes[extype]);
      
      if ((noSubstitute) && (stringBundle.getLoadingStatus() == 2)) {
        return null;
      }
      String unicodeSetPattern = stringBundle.getString();
      if (extype == 4) {
        try {
          return new UnicodeSet(unicodeSetPattern, 0x1 | options);
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Can't create exemplars for " + exemplarSetTypes[extype] + " in " + bundle.getLocale(), e);
        }
      }
      return new UnicodeSet(unicodeSetPattern, 0x1 | options);
    } catch (MissingResourceException ex) {
      if (extype == 1)
        return new UnicodeSet();
      if (extype == 2) {
        return null;
      }
      throw ex;
    }
  }
  






  public static final LocaleData getInstance(ULocale locale)
  {
    LocaleData ld = new LocaleData();
    bundle = ((ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale));
    langBundle = ((ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/lang", locale));
    noSubstitute = false;
    return ld;
  }
  






  public static final LocaleData getInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT));
  }
  








  public void setNoSubstitute(boolean setting)
  {
    noSubstitute = setting;
  }
  








  public boolean getNoSubstitute()
  {
    return noSubstitute;
  }
  
  private static final String[] DELIMITER_TYPES = { "quotationStart", "quotationEnd", "alternateQuotationStart", "alternateQuotationEnd" };
  













  public String getDelimiter(int type)
  {
    ICUResourceBundle delimitersBundle = (ICUResourceBundle)bundle.get("delimiters");
    
    ICUResourceBundle stringBundle = delimitersBundle.getWithFallback(DELIMITER_TYPES[type]);
    
    if ((noSubstitute) && (stringBundle.getLoadingStatus() == 2)) {
      return null;
    }
    return stringBundle.getString();
  }
  








  public static final class MeasurementSystem
  {
    public static final MeasurementSystem SI = new MeasurementSystem(0);
    




    public static final MeasurementSystem US = new MeasurementSystem(1);
    private int systemID;
    
    private MeasurementSystem(int id) {
      systemID = id;
    }
    
    private boolean equals(int id) {
      return systemID == id;
    }
  }
  






  public static final MeasurementSystem getMeasurementSystem(ULocale locale)
  {
    UResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
    UResourceBundle sysBundle = bundle.get("MeasurementSystem");
    
    int system = sysBundle.getInt();
    if (MeasurementSystem.US.equals(system)) {
      return MeasurementSystem.US;
    }
    if (MeasurementSystem.SI.equals(system)) {
      return MeasurementSystem.SI;
    }
    

    return null;
  }
  

  public static final class PaperSize
  {
    private int height;
    
    private int width;
    

    private PaperSize(int h, int w)
    {
      height = h;
      width = w;
    }
    



    public int getHeight()
    {
      return height;
    }
    



    public int getWidth()
    {
      return width;
    }
  }
  






  public static final PaperSize getPaperSize(ULocale locale)
  {
    UResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
    UResourceBundle obj = bundle.get("PaperSize");
    int[] size = obj.getIntVector();
    return new PaperSize(size[0], size[1], null);
  }
  




  public String getLocaleDisplayPattern()
  {
    ICUResourceBundle locDispBundle = (ICUResourceBundle)langBundle.get("localeDisplayPattern");
    String localeDisplayPattern = locDispBundle.getStringWithFallback("pattern");
    return localeDisplayPattern;
  }
  




  public String getLocaleSeparator()
  {
    ICUResourceBundle locDispBundle = (ICUResourceBundle)langBundle.get("localeDisplayPattern");
    String localeSeparator = locDispBundle.getStringWithFallback("separator");
    return localeSeparator;
  }
  
  private static VersionInfo gCLDRVersion = null;
  




  public static VersionInfo getCLDRVersion()
  {
    if (gCLDRVersion == null)
    {
      UResourceBundle supplementalDataBundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
      UResourceBundle cldrVersionBundle = supplementalDataBundle.get("cldrVersion");
      gCLDRVersion = VersionInfo.getInstance(cldrVersionBundle.getString());
    }
    return gCLDRVersion;
  }
}
