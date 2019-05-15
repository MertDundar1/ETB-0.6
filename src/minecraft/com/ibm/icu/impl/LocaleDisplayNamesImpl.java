package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.DisplayContext;
import com.ibm.icu.text.DisplayContext.Type;
import com.ibm.icu.text.LocaleDisplayNames;
import com.ibm.icu.text.LocaleDisplayNames.DialectHandling;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;





public class LocaleDisplayNamesImpl
  extends LocaleDisplayNames
{
  private final ULocale locale;
  private final LocaleDisplayNames.DialectHandling dialectHandling;
  private final DisplayContext capitalization;
  private final DataTable langData;
  private final DataTable regionData;
  private final Appender appender;
  private final MessageFormat format;
  private final MessageFormat keyTypeFormat;
  private static final Cache cache = new Cache(null);
  


  private static enum CapitalizationContextUsage
  {
    LANGUAGE, 
    SCRIPT, 
    TERRITORY, 
    VARIANT, 
    KEY, 
    TYPE;
    

    private CapitalizationContextUsage() {}
  }
  

  private Map<CapitalizationContextUsage, boolean[]> capitalizationUsage = null;
  




  private static final Map<String, CapitalizationContextUsage> contextUsageTypeMap = new HashMap();
  static { contextUsageTypeMap.put("languages", CapitalizationContextUsage.LANGUAGE);
    contextUsageTypeMap.put("script", CapitalizationContextUsage.SCRIPT);
    contextUsageTypeMap.put("territory", CapitalizationContextUsage.TERRITORY);
    contextUsageTypeMap.put("variant", CapitalizationContextUsage.VARIANT);
    contextUsageTypeMap.put("key", CapitalizationContextUsage.KEY);
    contextUsageTypeMap.put("type", CapitalizationContextUsage.TYPE);
  }
  
  public static LocaleDisplayNames getInstance(ULocale locale, LocaleDisplayNames.DialectHandling dialectHandling) {
    synchronized (cache) {
      return cache.get(locale, dialectHandling);
    }
  }
  
  public static LocaleDisplayNames getInstance(ULocale locale, DisplayContext... contexts) {
    synchronized (cache) {
      return cache.get(locale, contexts);
    }
  }
  
  public LocaleDisplayNamesImpl(ULocale locale, LocaleDisplayNames.DialectHandling dialectHandling) {
    this(locale, new DisplayContext[] { dialectHandling == LocaleDisplayNames.DialectHandling.STANDARD_NAMES ? DisplayContext.STANDARD_NAMES : DisplayContext.DIALECT_NAMES, DisplayContext.CAPITALIZATION_NONE });
  }
  
  public LocaleDisplayNamesImpl(ULocale locale, DisplayContext... contexts)
  {
    LocaleDisplayNames.DialectHandling dialectHandling = LocaleDisplayNames.DialectHandling.STANDARD_NAMES;
    DisplayContext capitalization = DisplayContext.CAPITALIZATION_NONE;
    for (DisplayContext contextItem : contexts) {
      switch (contextItem.type()) {
      case DIALECT_HANDLING: 
        dialectHandling = contextItem.value() == DisplayContext.STANDARD_NAMES.value() ? LocaleDisplayNames.DialectHandling.STANDARD_NAMES : LocaleDisplayNames.DialectHandling.DIALECT_NAMES;
        
        break;
      case CAPITALIZATION: 
        capitalization = contextItem;
      }
      
    }
    


    this.dialectHandling = dialectHandling;
    this.capitalization = capitalization;
    langData = LangDataTables.impl.get(locale);
    regionData = RegionDataTables.impl.get(locale);
    this.locale = (ULocale.ROOT.equals(langData.getLocale()) ? regionData.getLocale() : langData.getLocale());
    





    String sep = langData.get("localeDisplayPattern", "separator");
    if ("separator".equals(sep)) {
      sep = ", ";
    }
    appender = new Appender(sep);
    
    String pattern = langData.get("localeDisplayPattern", "pattern");
    if ("pattern".equals(pattern)) {
      pattern = "{0} ({1})";
    }
    format = new MessageFormat(pattern);
    
    String keyTypePattern = langData.get("localeDisplayPattern", "keyTypePattern");
    if ("keyTypePattern".equals(keyTypePattern)) {
      keyTypePattern = "{0}={1}";
    }
    keyTypeFormat = new MessageFormat(keyTypePattern);
    


    if ((capitalization == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU) || (capitalization == DisplayContext.CAPITALIZATION_FOR_STANDALONE))
    {
      capitalizationUsage = new HashMap();
      boolean[] noTransforms = new boolean[2];
      noTransforms[0] = false;
      noTransforms[1] = false;
      CapitalizationContextUsage[] allUsages = CapitalizationContextUsage.values();
      for (CapitalizationContextUsage usage : allUsages) {
        capitalizationUsage.put(usage, noTransforms);
      }
      ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
      UResourceBundle contextTransformsBundle = null;
      try {
        contextTransformsBundle = rb.getWithFallback("contextTransforms");
      }
      catch (MissingResourceException e) {
        contextTransformsBundle = null;
      }
      if (contextTransformsBundle != null) {
        UResourceBundleIterator ctIterator = contextTransformsBundle.getIterator();
        while (ctIterator.hasNext()) {
          UResourceBundle contextTransformUsage = ctIterator.next();
          int[] intVector = contextTransformUsage.getIntVector();
          if (intVector.length >= 2) {
            String usageKey = contextTransformUsage.getKey();
            CapitalizationContextUsage usage = (CapitalizationContextUsage)contextUsageTypeMap.get(usageKey);
            if (usage != null) {
              boolean[] transforms = new boolean[2];
              transforms[0] = (intVector[0] != 0 ? 1 : false);
              transforms[1] = (intVector[1] != 0 ? 1 : false);
              capitalizationUsage.put(usage, transforms);
            }
          }
        }
      }
    }
  }
  
  public ULocale getLocale()
  {
    return locale;
  }
  
  public LocaleDisplayNames.DialectHandling getDialectHandling()
  {
    return dialectHandling;
  }
  
  public DisplayContext getContext(DisplayContext.Type type)
  {
    DisplayContext result;
    switch (type) {
    case DIALECT_HANDLING: 
      result = dialectHandling == LocaleDisplayNames.DialectHandling.STANDARD_NAMES ? DisplayContext.STANDARD_NAMES : DisplayContext.DIALECT_NAMES;
      break;
    case CAPITALIZATION: 
      result = capitalization;
      break;
    default: 
      result = DisplayContext.STANDARD_NAMES;
    }
    
    return result;
  }
  
  private String adjustForUsageAndContext(CapitalizationContextUsage usage, String name) {
    String result = name;
    boolean titlecase = false;
    switch (1.$SwitchMap$com$ibm$icu$text$DisplayContext[capitalization.ordinal()]) {
    case 1: 
      titlecase = true;
      break;
    case 2: 
    case 3: 
      if (capitalizationUsage != null) {
        boolean[] transforms = (boolean[])capitalizationUsage.get(usage);
        titlecase = capitalization == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU ? transforms[0] : transforms[1];
      }
      break;
    }
    
    

    if (titlecase)
    {







      int stopPosLimit = 8;int len = name.length();
      if (stopPosLimit > len) {
        stopPosLimit = len;
      }
      for (int stopPos = 0; stopPos < stopPosLimit; stopPos++) {
        int ch = name.codePointAt(stopPos);
        if ((ch < 65) || ((ch > 90) && (ch < 97)) || ((ch > 122) && (ch < 192))) {
          break;
        }
        if (ch >= 65536) {
          stopPos++;
        }
      }
      if ((stopPos > 0) && (stopPos < len)) {
        String firstWord = name.substring(0, stopPos);
        String firstWordTitleCase = UCharacter.toTitleCase(locale, firstWord, null, 768);
        
        result = firstWordTitleCase.concat(name.substring(stopPos));
      }
      else {
        result = UCharacter.toTitleCase(locale, name, null, 768);
      }
    }
    
    return result;
  }
  
  public String localeDisplayName(ULocale locale)
  {
    return localeDisplayNameInternal(locale);
  }
  
  public String localeDisplayName(Locale locale)
  {
    return localeDisplayNameInternal(ULocale.forLocale(locale));
  }
  
  public String localeDisplayName(String localeId)
  {
    return localeDisplayNameInternal(new ULocale(localeId));
  }
  




  private String localeDisplayNameInternal(ULocale locale)
  {
    String resultName = null;
    
    String lang = locale.getLanguage();
    



    if (locale.getBaseName().length() == 0) {
      lang = "root";
    }
    String script = locale.getScript();
    String country = locale.getCountry();
    String variant = locale.getVariant();
    
    boolean hasScript = script.length() > 0;
    boolean hasCountry = country.length() > 0;
    boolean hasVariant = variant.length() > 0;
    

    if (dialectHandling == LocaleDisplayNames.DialectHandling.DIALECT_NAMES)
    {
      if ((hasScript) && (hasCountry)) {
        String langScriptCountry = lang + '_' + script + '_' + country;
        String result = localeIdName(langScriptCountry);
        if (!result.equals(langScriptCountry)) {
          resultName = result;
          hasScript = false;
          hasCountry = false;
          break label285;
        }
      }
      if (hasScript) {
        String langScript = lang + '_' + script;
        String result = localeIdName(langScript);
        if (!result.equals(langScript)) {
          resultName = result;
          hasScript = false;
          break label285;
        }
      }
      if (hasCountry) {
        String langCountry = lang + '_' + country;
        String result = localeIdName(langCountry);
        if (!result.equals(langCountry)) {
          resultName = result;
          hasCountry = false;
        }
      }
    }
    
    label285:
    
    if (resultName == null) {
      resultName = localeIdName(lang);
    }
    
    StringBuilder buf = new StringBuilder();
    if (hasScript)
    {
      buf.append(scriptDisplayNameInContext(script));
    }
    if (hasCountry) {
      appender.append(regionDisplayName(country), buf);
    }
    if (hasVariant) {
      appender.append(variantDisplayName(variant), buf);
    }
    
    Iterator<String> keys = locale.getKeywords();
    if (keys != null) {
      while (keys.hasNext()) {
        String key = (String)keys.next();
        String value = locale.getKeywordValue(key);
        String keyDisplayName = keyDisplayName(key);
        String valueDisplayName = keyValueDisplayName(key, value);
        if (!valueDisplayName.equals(value)) {
          appender.append(valueDisplayName, buf);
        } else if (!key.equals(keyDisplayName)) {
          String keyValue = keyTypeFormat.format(new String[] { keyDisplayName, valueDisplayName });
          
          appender.append(keyValue, buf);
        } else {
          appender.append(keyDisplayName, buf).append("=").append(valueDisplayName);
        }
      }
    }
    


    String resultRemainder = null;
    if (buf.length() > 0) {
      resultRemainder = buf.toString();
    }
    
    if (resultRemainder != null) {
      resultName = format.format(new Object[] { resultName, resultRemainder });
    }
    
    return adjustForUsageAndContext(CapitalizationContextUsage.LANGUAGE, resultName);
  }
  
  private String localeIdName(String localeId) {
    return langData.get("Languages", localeId);
  }
  

  public String languageDisplayName(String lang)
  {
    if ((lang.equals("root")) || (lang.indexOf('_') != -1)) {
      return lang;
    }
    return adjustForUsageAndContext(CapitalizationContextUsage.LANGUAGE, langData.get("Languages", lang));
  }
  
  public String scriptDisplayName(String script)
  {
    String str = langData.get("Scripts%stand-alone", script);
    if (str.equals(script)) {
      str = langData.get("Scripts", script);
    }
    return adjustForUsageAndContext(CapitalizationContextUsage.SCRIPT, str);
  }
  
  public String scriptDisplayNameInContext(String script)
  {
    return adjustForUsageAndContext(CapitalizationContextUsage.SCRIPT, langData.get("Scripts", script));
  }
  
  public String scriptDisplayName(int scriptCode)
  {
    return adjustForUsageAndContext(CapitalizationContextUsage.SCRIPT, scriptDisplayName(UScript.getShortName(scriptCode)));
  }
  
  public String regionDisplayName(String region)
  {
    return adjustForUsageAndContext(CapitalizationContextUsage.TERRITORY, regionData.get("Countries", region));
  }
  
  public String variantDisplayName(String variant)
  {
    return adjustForUsageAndContext(CapitalizationContextUsage.VARIANT, langData.get("Variants", variant));
  }
  
  public String keyDisplayName(String key)
  {
    return adjustForUsageAndContext(CapitalizationContextUsage.KEY, langData.get("Keys", key));
  }
  


  public String keyValueDisplayName(String key, String value) { return adjustForUsageAndContext(CapitalizationContextUsage.TYPE, langData.get("Types", key, value)); }
  
  public static class DataTable {
    public DataTable() {}
    
    ULocale getLocale() { return ULocale.ROOT; }
    
    String get(String tableName, String code)
    {
      return get(tableName, null, code);
    }
    
    String get(String tableName, String subTableName, String code) {
      return code;
    }
  }
  
  static class ICUDataTable extends LocaleDisplayNamesImpl.DataTable {
    private final ICUResourceBundle bundle;
    
    public ICUDataTable(String path, ULocale locale) {
      bundle = ((ICUResourceBundle)UResourceBundle.getBundleInstance(path, locale.getBaseName()));
    }
    
    public ULocale getLocale()
    {
      return bundle.getULocale();
    }
    

    public String get(String tableName, String subTableName, String code) { return ICUResourceTableAccess.getTableString(bundle, tableName, subTableName, code); }
  }
  
  static abstract class DataTables {
    DataTables() {}
    
    public abstract LocaleDisplayNamesImpl.DataTable get(ULocale paramULocale);
    
    public static DataTables load(String className) {
      try { return (DataTables)Class.forName(className).newInstance();
      } catch (Throwable t) {
        LocaleDisplayNamesImpl.DataTable NO_OP = new LocaleDisplayNamesImpl.DataTable();
        new DataTables() {
          public LocaleDisplayNamesImpl.DataTable get(ULocale locale) {
            return val$NO_OP;
          }
        };
      }
    }
  }
  
  static abstract class ICUDataTables extends LocaleDisplayNamesImpl.DataTables {
    private final String path;
    
    protected ICUDataTables(String path) {
      this.path = path;
    }
    
    public LocaleDisplayNamesImpl.DataTable get(ULocale locale)
    {
      return new LocaleDisplayNamesImpl.ICUDataTable(path, locale);
    }
  }
  
  static class LangDataTables {
    static final LocaleDisplayNamesImpl.DataTables impl = LocaleDisplayNamesImpl.DataTables.load("com.ibm.icu.impl.ICULangDataTables");
    
    LangDataTables() {} }
  
  static class RegionDataTables { static final LocaleDisplayNamesImpl.DataTables impl = LocaleDisplayNamesImpl.DataTables.load("com.ibm.icu.impl.ICURegionDataTables");
    
    RegionDataTables() {} }
  
  public static enum DataTableType { LANG,  REGION;
    
    private DataTableType() {} }
  
  public static boolean haveData(DataTableType type) { switch (type) {
    case LANG:  return LangDataTables.impl instanceof ICUDataTables;
    case REGION:  return RegionDataTables.impl instanceof ICUDataTables;
    }
    throw new IllegalArgumentException("unknown type: " + type);
  }
  

  static class Appender
  {
    private final String sep;
    
    Appender(String sep) { this.sep = sep; }
    
    StringBuilder append(String s, StringBuilder b) {
      if (b.length() > 0) {
        b.append(sep);
      }
      b.append(s);
      return b;
    } }
  
  private static class Cache { private ULocale locale;
    private LocaleDisplayNames.DialectHandling dialectHandling;
    private DisplayContext capitalization;
    private LocaleDisplayNames cache;
    
    private Cache() {}
    
    public LocaleDisplayNames get(ULocale locale, LocaleDisplayNames.DialectHandling dialectHandling) { if ((dialectHandling != this.dialectHandling) || (DisplayContext.CAPITALIZATION_NONE != capitalization) || (!locale.equals(this.locale))) {
        this.locale = locale;
        this.dialectHandling = dialectHandling;
        capitalization = DisplayContext.CAPITALIZATION_NONE;
        cache = new LocaleDisplayNamesImpl(locale, dialectHandling);
      }
      return cache;
    }
    
    public LocaleDisplayNames get(ULocale locale, DisplayContext... contexts) { LocaleDisplayNames.DialectHandling dialectHandlingIn = LocaleDisplayNames.DialectHandling.STANDARD_NAMES;
      DisplayContext capitalizationIn = DisplayContext.CAPITALIZATION_NONE;
      for (DisplayContext contextItem : contexts) {
        switch (LocaleDisplayNamesImpl.1.$SwitchMap$com$ibm$icu$text$DisplayContext$Type[contextItem.type().ordinal()]) {
        case 1: 
          dialectHandlingIn = contextItem.value() == DisplayContext.STANDARD_NAMES.value() ? LocaleDisplayNames.DialectHandling.STANDARD_NAMES : LocaleDisplayNames.DialectHandling.DIALECT_NAMES;
          
          break;
        case 2: 
          capitalizationIn = contextItem;
        }
        
      }
      

      if ((dialectHandlingIn != dialectHandling) || (capitalizationIn != capitalization) || (!locale.equals(this.locale))) {
        this.locale = locale;
        dialectHandling = dialectHandlingIn;
        capitalization = capitalizationIn;
        cache = new LocaleDisplayNamesImpl(locale, contexts);
      }
      return cache;
    }
  }
}
