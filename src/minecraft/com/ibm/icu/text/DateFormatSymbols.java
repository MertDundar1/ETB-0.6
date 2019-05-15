package com.ibm.icu.text;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.impl.CalendarUtil;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import com.ibm.icu.util.ULocale.Type;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
















































































































































public class DateFormatSymbols
  implements Serializable, Cloneable
{
  public static final int FORMAT = 0;
  public static final int STANDALONE = 1;
  /**
   * @deprecated
   */
  public static final int DT_CONTEXT_COUNT = 2;
  public static final int ABBREVIATED = 0;
  public static final int WIDE = 1;
  public static final int NARROW = 2;
  public static final int SHORT = 3;
  /**
   * @deprecated
   */
  public static final int DT_WIDTH_COUNT = 4;
  static final int DT_LEAP_MONTH_PATTERN_FORMAT_WIDE = 0;
  static final int DT_LEAP_MONTH_PATTERN_FORMAT_ABBREV = 1;
  static final int DT_LEAP_MONTH_PATTERN_FORMAT_NARROW = 2;
  static final int DT_LEAP_MONTH_PATTERN_STANDALONE_WIDE = 3;
  static final int DT_LEAP_MONTH_PATTERN_STANDALONE_ABBREV = 4;
  static final int DT_LEAP_MONTH_PATTERN_STANDALONE_NARROW = 5;
  static final int DT_LEAP_MONTH_PATTERN_NUMERIC = 6;
  static final int DT_MONTH_PATTERN_COUNT = 7;
  
  public DateFormatSymbols()
  {
    this(ULocale.getDefault(ULocale.Category.FORMAT));
  }
  








  public DateFormatSymbols(Locale locale)
  {
    this(ULocale.forLocale(locale));
  }
  








  public DateFormatSymbols(ULocale locale)
  {
    initializeData(locale, CalendarUtil.getCalendarType(locale));
  }
  










  public static DateFormatSymbols getInstance()
  {
    return new DateFormatSymbols();
  }
  











  public static DateFormatSymbols getInstance(Locale locale)
  {
    return new DateFormatSymbols(locale);
  }
  











  public static DateFormatSymbols getInstance(ULocale locale)
  {
    return new DateFormatSymbols(locale);
  }
  












  public static Locale[] getAvailableLocales()
  {
    return ICUResourceBundle.getAvailableLocales();
  }
  













  public static ULocale[] getAvailableULocales()
  {
    return ICUResourceBundle.getAvailableULocales();
  }
  





  String[] eras = null;
  





  String[] eraNames = null;
  





  String[] narrowEras = null;
  






  String[] months = null;
  







  String[] shortMonths = null;
  







  String[] narrowMonths = null;
  






  String[] standaloneMonths = null;
  







  String[] standaloneShortMonths = null;
  







  String[] standaloneNarrowMonths = null;
  







  String[] weekdays = null;
  








  String[] shortWeekdays = null;
  








  String[] shorterWeekdays = null;
  







  String[] narrowWeekdays = null;
  







  String[] standaloneWeekdays = null;
  








  String[] standaloneShortWeekdays = null;
  








  String[] standaloneShorterWeekdays = null;
  







  String[] standaloneNarrowWeekdays = null;
  






  String[] ampms = null;
  





  String[] shortQuarters = null;
  





  String[] quarters = null;
  





  String[] standaloneShortQuarters = null;
  





  String[] standaloneQuarters = null;
  





  String[] leapMonthPatterns = null;
  





  String[] shortYearNames = null;
  

































  private String[][] zoneStrings = (String[][])null;
  







  static final String patternChars = "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx";
  







  String localPatternChars = null;
  

  private static final long serialVersionUID = -5987973545549424702L;
  
  private static final String[][] CALENDAR_CLASSES = { { "GregorianCalendar", "gregorian" }, { "JapaneseCalendar", "japanese" }, { "BuddhistCalendar", "buddhist" }, { "TaiwanCalendar", "roc" }, { "PersianCalendar", "persian" }, { "IslamicCalendar", "islamic" }, { "HebrewCalendar", "hebrew" }, { "ChineseCalendar", "chinese" }, { "IndianCalendar", "indian" }, { "CopticCalendar", "coptic" }, { "EthiopicCalendar", "ethiopic" } };
  
















  static enum CapitalizationContextUsage
  {
    OTHER, 
    MONTH_FORMAT, 
    MONTH_STANDALONE, 
    MONTH_NARROW, 
    DAY_FORMAT, 
    DAY_STANDALONE, 
    DAY_NARROW, 
    ERA_WIDE, 
    ERA_ABBREV, 
    ERA_NARROW, 
    ZONE_LONG, 
    ZONE_SHORT, 
    METAZONE_LONG, 
    METAZONE_SHORT;
    

    private CapitalizationContextUsage() {}
  }
  

  private static final Map<String, CapitalizationContextUsage> contextUsageTypeMap = new HashMap();
  static { contextUsageTypeMap.put("month-format-except-narrow", CapitalizationContextUsage.MONTH_FORMAT);
    contextUsageTypeMap.put("month-standalone-except-narrow", CapitalizationContextUsage.MONTH_STANDALONE);
    contextUsageTypeMap.put("month-narrow", CapitalizationContextUsage.MONTH_NARROW);
    contextUsageTypeMap.put("day-format-except-narrow", CapitalizationContextUsage.DAY_FORMAT);
    contextUsageTypeMap.put("day-standalone-except-narrow", CapitalizationContextUsage.DAY_STANDALONE);
    contextUsageTypeMap.put("day-narrow", CapitalizationContextUsage.DAY_NARROW);
    contextUsageTypeMap.put("era-name", CapitalizationContextUsage.ERA_WIDE);
    contextUsageTypeMap.put("era-abbr", CapitalizationContextUsage.ERA_ABBREV);
    contextUsageTypeMap.put("era-narrow", CapitalizationContextUsage.ERA_NARROW);
    contextUsageTypeMap.put("zone-long", CapitalizationContextUsage.ZONE_LONG);
    contextUsageTypeMap.put("zone-short", CapitalizationContextUsage.ZONE_SHORT);
    contextUsageTypeMap.put("metazone-long", CapitalizationContextUsage.METAZONE_LONG);
    contextUsageTypeMap.put("metazone-short", CapitalizationContextUsage.METAZONE_SHORT);
  }
  






  Map<CapitalizationContextUsage, boolean[]> capitalization = null;
  

  static final int millisPerHour = 3600000;
  

  public String[] getEras()
  {
    return duplicate(eras);
  }
  




  public void setEras(String[] newEras)
  {
    eras = duplicate(newEras);
  }
  




  public String[] getEraNames()
  {
    return duplicate(eraNames);
  }
  




  public void setEraNames(String[] newEraNames)
  {
    eraNames = duplicate(newEraNames);
  }
  




  public String[] getMonths()
  {
    return duplicate(months);
  }
  







  public String[] getMonths(int context, int width)
  {
    String[] returnValue = null;
    switch (context) {
    case 0: 
      switch (width) {
      case 1: 
        returnValue = months;
        break;
      case 0: 
      case 3: 
        returnValue = shortMonths;
        break;
      case 2: 
        returnValue = narrowMonths;
      }
      
      break;
    case 1: 
      switch (width) {
      case 1: 
        returnValue = standaloneMonths;
        break;
      case 0: 
      case 3: 
        returnValue = standaloneShortMonths;
        break;
      case 2: 
        returnValue = standaloneNarrowMonths;
      }
      
      break;
    }
    return duplicate(returnValue);
  }
  




  public void setMonths(String[] newMonths)
  {
    months = duplicate(newMonths);
  }
  







  public void setMonths(String[] newMonths, int context, int width)
  {
    switch (context) {
    case 0: 
      switch (width) {
      case 1: 
        months = duplicate(newMonths);
        break;
      case 0: 
        shortMonths = duplicate(newMonths);
        break;
      case 2: 
        narrowMonths = duplicate(newMonths);
      }
      
      break;
    

    case 1: 
      switch (width) {
      case 1: 
        standaloneMonths = duplicate(newMonths);
        break;
      case 0: 
        standaloneShortMonths = duplicate(newMonths);
        break;
      case 2: 
        standaloneNarrowMonths = duplicate(newMonths); }
      break;
    }
    
  }
  







  public String[] getShortMonths()
  {
    return duplicate(shortMonths);
  }
  




  public void setShortMonths(String[] newShortMonths)
  {
    shortMonths = duplicate(newShortMonths);
  }
  





  public String[] getWeekdays()
  {
    return duplicate(weekdays);
  }
  








  public String[] getWeekdays(int context, int width)
  {
    String[] returnValue = null;
    switch (context) {
    case 0: 
      switch (width) {
      case 1: 
        returnValue = weekdays;
        break;
      case 0: 
        returnValue = shortWeekdays;
        break;
      case 3: 
        returnValue = shorterWeekdays != null ? shorterWeekdays : shortWeekdays;
        break;
      case 2: 
        returnValue = narrowWeekdays;
      }
      
      break;
    case 1: 
      switch (width) {
      case 1: 
        returnValue = standaloneWeekdays;
        break;
      case 0: 
        returnValue = standaloneShortWeekdays;
        break;
      case 3: 
        returnValue = standaloneShorterWeekdays != null ? standaloneShorterWeekdays : standaloneShortWeekdays;
        break;
      case 2: 
        returnValue = standaloneNarrowWeekdays;
      }
      
      break;
    }
    return duplicate(returnValue);
  }
  







  public void setWeekdays(String[] newWeekdays, int context, int width)
  {
    switch (context) {
    case 0: 
      switch (width) {
      case 1: 
        weekdays = duplicate(newWeekdays);
        break;
      case 0: 
        shortWeekdays = duplicate(newWeekdays);
        break;
      case 3: 
        shorterWeekdays = duplicate(newWeekdays);
        break;
      case 2: 
        narrowWeekdays = duplicate(newWeekdays);
      }
      
      break;
    case 1: 
      switch (width) {
      case 1: 
        standaloneWeekdays = duplicate(newWeekdays);
        break;
      case 0: 
        standaloneShortWeekdays = duplicate(newWeekdays);
        break;
      case 3: 
        standaloneShorterWeekdays = duplicate(newWeekdays);
        break;
      case 2: 
        standaloneNarrowWeekdays = duplicate(newWeekdays);
      }
      
      


      break;
    }
    
  }
  


  public void setWeekdays(String[] newWeekdays)
  {
    weekdays = duplicate(newWeekdays);
  }
  







  public String[] getShortWeekdays()
  {
    return duplicate(shortWeekdays);
  }
  








  public void setShortWeekdays(String[] newAbbrevWeekdays)
  {
    shortWeekdays = duplicate(newAbbrevWeekdays);
  }
  






  public String[] getQuarters(int context, int width)
  {
    String[] returnValue = null;
    switch (context) {
    case 0: 
      switch (width) {
      case 1: 
        returnValue = quarters;
        break;
      case 0: 
      case 3: 
        returnValue = shortQuarters;
        break;
      case 2: 
        returnValue = null;
      }
      
      break;
    
    case 1: 
      switch (width) {
      case 1: 
        returnValue = standaloneQuarters;
        break;
      case 0: 
      case 3: 
        returnValue = standaloneShortQuarters;
        break;
      case 2: 
        returnValue = null;
      }
      
      break;
    }
    return duplicate(returnValue);
  }
  







  public void setQuarters(String[] newQuarters, int context, int width)
  {
    switch (context) {
    case 0: 
      switch (width) {
      case 1: 
        quarters = duplicate(newQuarters);
        break;
      case 0: 
        shortQuarters = duplicate(newQuarters);
        break;
      case 2: 
        
      }
      
      break;
    

    case 1: 
      switch (width) {
      case 1: 
        standaloneQuarters = duplicate(newQuarters);
        break;
      case 0: 
        standaloneShortQuarters = duplicate(newQuarters);
      }
      
      
      break;
    }
    
  }
  







  public String[] getAmPmStrings()
  {
    return duplicate(ampms);
  }
  




  public void setAmPmStrings(String[] newAmpms)
  {
    ampms = duplicate(newAmpms);
  }
  
























  public String[][] getZoneStrings()
  {
    if (zoneStrings != null) {
      return duplicate(zoneStrings);
    }
    
    String[] tzIDs = TimeZone.getAvailableIDs();
    TimeZoneNames tznames = TimeZoneNames.getInstance(validLocale);
    long now = System.currentTimeMillis();
    String[][] array = new String[tzIDs.length][5];
    for (int i = 0; i < tzIDs.length; i++) {
      String canonicalID = TimeZone.getCanonicalID(tzIDs[i]);
      if (canonicalID == null) {
        canonicalID = tzIDs[i];
      }
      
      array[i][0] = tzIDs[i];
      array[i][1] = tznames.getDisplayName(canonicalID, TimeZoneNames.NameType.LONG_STANDARD, now);
      array[i][2] = tznames.getDisplayName(canonicalID, TimeZoneNames.NameType.SHORT_STANDARD, now);
      array[i][3] = tznames.getDisplayName(canonicalID, TimeZoneNames.NameType.LONG_DAYLIGHT, now);
      array[i][4] = tznames.getDisplayName(canonicalID, TimeZoneNames.NameType.SHORT_DAYLIGHT, now);
    }
    
    zoneStrings = array;
    return zoneStrings;
  }
  















  public void setZoneStrings(String[][] newZoneStrings)
  {
    zoneStrings = duplicate(newZoneStrings);
  }
  








  public String getLocalPatternChars()
  {
    return localPatternChars;
  }
  





  public void setLocalPatternChars(String newLocalPatternChars)
  {
    localPatternChars = newLocalPatternChars;
  }
  



  public Object clone()
  {
    try
    {
      return (DateFormatSymbols)super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }
  






  public int hashCode()
  {
    return requestedLocale.toString().hashCode();
  }
  




  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;
    DateFormatSymbols that = (DateFormatSymbols)obj;
    return (Utility.arrayEquals(eras, eras)) && (Utility.arrayEquals(eraNames, eraNames)) && (Utility.arrayEquals(months, months)) && (Utility.arrayEquals(shortMonths, shortMonths)) && (Utility.arrayEquals(narrowMonths, narrowMonths)) && (Utility.arrayEquals(standaloneMonths, standaloneMonths)) && (Utility.arrayEquals(standaloneShortMonths, standaloneShortMonths)) && (Utility.arrayEquals(standaloneNarrowMonths, standaloneNarrowMonths)) && (Utility.arrayEquals(weekdays, weekdays)) && (Utility.arrayEquals(shortWeekdays, shortWeekdays)) && (Utility.arrayEquals(shorterWeekdays, shorterWeekdays)) && (Utility.arrayEquals(narrowWeekdays, narrowWeekdays)) && (Utility.arrayEquals(standaloneWeekdays, standaloneWeekdays)) && (Utility.arrayEquals(standaloneShortWeekdays, standaloneShortWeekdays)) && (Utility.arrayEquals(standaloneShorterWeekdays, standaloneShorterWeekdays)) && (Utility.arrayEquals(standaloneNarrowWeekdays, standaloneNarrowWeekdays)) && (Utility.arrayEquals(ampms, ampms)) && (arrayOfArrayEquals(zoneStrings, zoneStrings)) && (requestedLocale.getDisplayName().equals(requestedLocale.getDisplayName())) && (Utility.arrayEquals(localPatternChars, localPatternChars));
  }
  































  private static ICUCache<String, DateFormatSymbols> DFSCACHE = new SimpleCache();
  

  private ULocale requestedLocale;
  

  private ULocale validLocale;
  
  private ULocale actualLocale;
  

  protected void initializeData(ULocale desiredLocale, String type)
  {
    String key = desiredLocale.getBaseName() + "+" + type;
    DateFormatSymbols dfs = (DateFormatSymbols)DFSCACHE.get(key);
    if (dfs == null)
    {
      CalendarData calData = new CalendarData(desiredLocale, type);
      initializeData(desiredLocale, calData);
      
      if (getClass().getName().equals("com.ibm.icu.text.DateFormatSymbols")) {
        dfs = (DateFormatSymbols)clone();
        DFSCACHE.put(key, dfs);
      }
    } else {
      initializeData(dfs);
    }
  }
  




  void initializeData(DateFormatSymbols dfs)
  {
    eras = eras;
    eraNames = eraNames;
    narrowEras = narrowEras;
    months = months;
    shortMonths = shortMonths;
    narrowMonths = narrowMonths;
    standaloneMonths = standaloneMonths;
    standaloneShortMonths = standaloneShortMonths;
    standaloneNarrowMonths = standaloneNarrowMonths;
    weekdays = weekdays;
    shortWeekdays = shortWeekdays;
    shorterWeekdays = shorterWeekdays;
    narrowWeekdays = narrowWeekdays;
    standaloneWeekdays = standaloneWeekdays;
    standaloneShortWeekdays = standaloneShortWeekdays;
    standaloneShorterWeekdays = standaloneShorterWeekdays;
    standaloneNarrowWeekdays = standaloneNarrowWeekdays;
    ampms = ampms;
    shortQuarters = shortQuarters;
    quarters = quarters;
    standaloneShortQuarters = standaloneShortQuarters;
    standaloneQuarters = standaloneQuarters;
    leapMonthPatterns = leapMonthPatterns;
    shortYearNames = shortYearNames;
    
    zoneStrings = zoneStrings;
    localPatternChars = localPatternChars;
    
    capitalization = capitalization;
    
    actualLocale = actualLocale;
    validLocale = validLocale;
    requestedLocale = requestedLocale;
  }
  








  /**
   * @deprecated
   */
  protected void initializeData(ULocale desiredLocale, CalendarData calData)
  {
    eras = calData.getEras("abbreviated");
    
    eraNames = calData.getEras("wide");
    
    narrowEras = calData.getEras("narrow");
    
    months = calData.getStringArray("monthNames", "wide");
    shortMonths = calData.getStringArray("monthNames", "abbreviated");
    narrowMonths = calData.getStringArray("monthNames", "narrow");
    
    standaloneMonths = calData.getStringArray("monthNames", "stand-alone", "wide");
    standaloneShortMonths = calData.getStringArray("monthNames", "stand-alone", "abbreviated");
    standaloneNarrowMonths = calData.getStringArray("monthNames", "stand-alone", "narrow");
    
    String[] lWeekdays = calData.getStringArray("dayNames", "wide");
    weekdays = new String[8];
    weekdays[0] = "";
    System.arraycopy(lWeekdays, 0, weekdays, 1, lWeekdays.length);
    
    String[] aWeekdays = calData.getStringArray("dayNames", "abbreviated");
    shortWeekdays = new String[8];
    shortWeekdays[0] = "";
    System.arraycopy(aWeekdays, 0, shortWeekdays, 1, aWeekdays.length);
    
    String[] sWeekdays = calData.getStringArray("dayNames", "short");
    shorterWeekdays = new String[8];
    shorterWeekdays[0] = "";
    System.arraycopy(sWeekdays, 0, shorterWeekdays, 1, sWeekdays.length);
    
    String[] nWeekdays = null;
    try {
      nWeekdays = calData.getStringArray("dayNames", "narrow");
    }
    catch (MissingResourceException e) {
      try {
        nWeekdays = calData.getStringArray("dayNames", "stand-alone", "narrow");
      }
      catch (MissingResourceException e1) {
        nWeekdays = calData.getStringArray("dayNames", "abbreviated");
      }
    }
    narrowWeekdays = new String[8];
    narrowWeekdays[0] = "";
    System.arraycopy(nWeekdays, 0, narrowWeekdays, 1, nWeekdays.length);
    
    String[] swWeekdays = null;
    swWeekdays = calData.getStringArray("dayNames", "stand-alone", "wide");
    standaloneWeekdays = new String[8];
    standaloneWeekdays[0] = "";
    System.arraycopy(swWeekdays, 0, standaloneWeekdays, 1, swWeekdays.length);
    
    String[] saWeekdays = null;
    saWeekdays = calData.getStringArray("dayNames", "stand-alone", "abbreviated");
    standaloneShortWeekdays = new String[8];
    standaloneShortWeekdays[0] = "";
    System.arraycopy(saWeekdays, 0, standaloneShortWeekdays, 1, saWeekdays.length);
    
    String[] ssWeekdays = null;
    ssWeekdays = calData.getStringArray("dayNames", "stand-alone", "short");
    standaloneShorterWeekdays = new String[8];
    standaloneShorterWeekdays[0] = "";
    System.arraycopy(ssWeekdays, 0, standaloneShorterWeekdays, 1, ssWeekdays.length);
    
    String[] snWeekdays = null;
    snWeekdays = calData.getStringArray("dayNames", "stand-alone", "narrow");
    standaloneNarrowWeekdays = new String[8];
    standaloneNarrowWeekdays[0] = "";
    System.arraycopy(snWeekdays, 0, standaloneNarrowWeekdays, 1, snWeekdays.length);
    
    ampms = calData.getStringArray("AmPmMarkers");
    
    quarters = calData.getStringArray("quarters", "wide");
    shortQuarters = calData.getStringArray("quarters", "abbreviated");
    
    standaloneQuarters = calData.getStringArray("quarters", "stand-alone", "wide");
    standaloneShortQuarters = calData.getStringArray("quarters", "stand-alone", "abbreviated");
    


    ICUResourceBundle monthPatternsBundle = null;
    try {
      monthPatternsBundle = calData.get("monthPatterns");
    }
    catch (MissingResourceException e) {
      monthPatternsBundle = null;
    }
    if (monthPatternsBundle != null) {
      leapMonthPatterns = new String[7];
      leapMonthPatterns[0] = calData.get("monthPatterns", "wide").get("leap").getString();
      leapMonthPatterns[1] = calData.get("monthPatterns", "abbreviated").get("leap").getString();
      leapMonthPatterns[2] = calData.get("monthPatterns", "narrow").get("leap").getString();
      leapMonthPatterns[3] = calData.get("monthPatterns", "stand-alone", "wide").get("leap").getString();
      leapMonthPatterns[4] = calData.get("monthPatterns", "stand-alone", "abbreviated").get("leap").getString();
      leapMonthPatterns[5] = calData.get("monthPatterns", "stand-alone", "narrow").get("leap").getString();
      leapMonthPatterns[6] = calData.get("monthPatterns", "numeric", "all").get("leap").getString();
    }
    
    ICUResourceBundle cyclicNameSetsBundle = null;
    try {
      cyclicNameSetsBundle = calData.get("cyclicNameSets");
    }
    catch (MissingResourceException e) {
      cyclicNameSetsBundle = null;
    }
    if (cyclicNameSetsBundle != null) {
      shortYearNames = calData.get("cyclicNameSets", "years", "format", "abbreviated").getStringArray();
    }
    
    requestedLocale = desiredLocale;
    
    ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", desiredLocale);
    







    localPatternChars = "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx";
    

    ULocale uloc = rb.getULocale();
    setLocale(uloc, uloc);
    
    capitalization = new HashMap();
    boolean[] noTransforms = new boolean[2];
    noTransforms[0] = false;
    noTransforms[1] = false;
    CapitalizationContextUsage[] allUsages = CapitalizationContextUsage.values();
    for (CapitalizationContextUsage usage : allUsages) {
      capitalization.put(usage, noTransforms);
    }
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
            capitalization.put(usage, transforms);
          }
        }
      }
    }
  }
  
  private static final boolean arrayOfArrayEquals(Object[][] aa1, Object[][] aa2) {
    if (aa1 == aa2) {
      return true;
    }
    if ((aa1 == null) || (aa2 == null)) {
      return false;
    }
    if (aa1.length != aa2.length) {
      return false;
    }
    boolean equal = true;
    for (int i = 0; i < aa1.length; i++) {
      equal = Utility.arrayEquals(aa1[i], aa2[i]);
      if (!equal) {
        break;
      }
    }
    return equal;
  }
  










  private final String[] duplicate(String[] srcArray)
  {
    return (String[])srcArray.clone();
  }
  
  private final String[][] duplicate(String[][] srcArray)
  {
    String[][] aCopy = new String[srcArray.length][];
    for (int i = 0; i < srcArray.length; i++)
      aCopy[i] = duplicate(srcArray[i]);
    return aCopy;
  }
  












































































  public DateFormatSymbols(Calendar cal, Locale locale)
  {
    initializeData(ULocale.forLocale(locale), cal.getType());
  }
  



























































  public DateFormatSymbols(Calendar cal, ULocale locale)
  {
    initializeData(locale, cal.getType());
  }
  





  public DateFormatSymbols(Class<? extends Calendar> calendarClass, Locale locale)
  {
    this(calendarClass, ULocale.forLocale(locale));
  }
  





  public DateFormatSymbols(Class<? extends Calendar> calendarClass, ULocale locale)
  {
    String fullName = calendarClass.getName();
    int lastDot = fullName.lastIndexOf('.');
    String className = fullName.substring(lastDot + 1);
    String calType = null;
    for (String[] calClassInfo : CALENDAR_CLASSES) {
      if (calClassInfo[0].equals(className)) {
        calType = calClassInfo[1];
        break;
      }
    }
    if (calType == null) {
      calType = className.replaceAll("Calendar", "").toLowerCase(Locale.ENGLISH);
    }
    
    initializeData(locale, calType);
  }
  






  public DateFormatSymbols(ResourceBundle bundle, Locale locale)
  {
    this(bundle, ULocale.forLocale(locale));
  }
  






  public DateFormatSymbols(ResourceBundle bundle, ULocale locale)
  {
    initializeData(locale, new CalendarData((ICUResourceBundle)bundle, CalendarUtil.getCalendarType(locale)));
  }
  













  /**
   * @deprecated
   */
  public static ResourceBundle getDateFormatBundle(Class<? extends Calendar> calendarClass, Locale locale)
    throws MissingResourceException
  {
    return null;
  }
  












  /**
   * @deprecated
   */
  public static ResourceBundle getDateFormatBundle(Class<? extends Calendar> calendarClass, ULocale locale)
    throws MissingResourceException
  {
    return null;
  }
  






  /**
   * @deprecated
   */
  public static ResourceBundle getDateFormatBundle(Calendar cal, Locale locale)
    throws MissingResourceException
  {
    return null;
  }
  






  /**
   * @deprecated
   */
  public static ResourceBundle getDateFormatBundle(Calendar cal, ULocale locale)
    throws MissingResourceException
  {
    return null;
  }
  

























  public final ULocale getLocale(ULocale.Type type)
  {
    return type == ULocale.ACTUAL_LOCALE ? actualLocale : validLocale;
  }
  

















  final void setLocale(ULocale valid, ULocale actual)
  {
    if ((valid == null ? 1 : 0) != (actual == null ? 1 : 0))
    {
      throw new IllegalArgumentException();
    }
    


    validLocale = valid;
    actualLocale = actual;
  }
  

















  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
  }
}
