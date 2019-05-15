package com.ibm.icu.text;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.impl.DateNumberFormat;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.Calendar.FormatConfiguration;
import com.ibm.icu.util.HebrewCalendar;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneRule;
import com.ibm.icu.util.TimeZoneTransition;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.FieldPosition;
import java.text.Format.Field;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;








































































































































































































































































































































































































































































































































































































































































































public class SimpleDateFormat
  extends DateFormat
{
  private static final long serialVersionUID = 4774881970558875024L;
  static final int currentSerialVersion = 2;
  static boolean DelayedHebrewMonthCheck = false;
  








  private static final int[] CALENDAR_FIELD_TO_LEVEL = { 0, 10, 20, 20, 30, 30, 20, 30, 30, 40, 50, 50, 60, 70, 80, 0, 0, 10, 30, 10, 0, 40 };
  



















  private static final int[] PATTERN_CHAR_TO_LEVEL = { -1, 40, -1, -1, 20, 30, 30, 0, 50, -1, -1, 50, 20, 20, -1, 0, -1, 20, -1, 80, -1, 10, 0, 30, 0, 10, 0, -1, -1, -1, -1, -1, -1, 40, -1, 30, 30, 30, -1, 0, 50, -1, -1, 50, -1, 60, -1, -1, -1, 20, -1, 70, -1, 10, 0, 20, 0, 10, 0, -1, -1, -1, -1, -1 };
  









  private static final int HEBREW_CAL_CUR_MILLENIUM_START_YEAR = 5000;
  









  private static final int HEBREW_CAL_CUR_MILLENIUM_END_YEAR = 6000;
  








  private int serialVersionOnStream = 2;
  




  private String pattern;
  




  private String override;
  




  private HashMap<String, NumberFormat> numberFormatters;
  




  private HashMap<Character, String> overrideMap;
  




  private DateFormatSymbols formatData;
  



  private transient ULocale locale;
  



  private Date defaultCenturyStart;
  



  private transient int defaultCenturyStartYear;
  



  private transient long defaultCenturyBase;
  



  private transient TimeZoneFormat.TimeType tztype = TimeZoneFormat.TimeType.UNKNOWN;
  




  private static final int millisPerHour = 3600000;
  




  private static final int ISOSpecialEra = -32000;
  




  private static final String SUPPRESS_NEGATIVE_PREFIX = "꬀";
  



  private transient boolean useFastFormat;
  



  private volatile TimeZoneFormat tzFormat;
  



  private transient DisplayContext capitalizationSetting;
  




  private static enum ContextValue
  {
    UNKNOWN, 
    CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE, 
    CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE, 
    CAPITALIZATION_FOR_UI_LIST_OR_MENU, 
    CAPITALIZATION_FOR_STANDALONE;
    



    private ContextValue() {}
  }
  



  public SimpleDateFormat()
  {
    this(getDefaultPattern(), null, null, null, null, true, null);
  }
  







  public SimpleDateFormat(String pattern)
  {
    this(pattern, null, null, null, null, true, null);
  }
  






  public SimpleDateFormat(String pattern, Locale loc)
  {
    this(pattern, null, null, null, ULocale.forLocale(loc), true, null);
  }
  






  public SimpleDateFormat(String pattern, ULocale loc)
  {
    this(pattern, null, null, null, loc, true, null);
  }
  













  public SimpleDateFormat(String pattern, String override, ULocale loc)
  {
    this(pattern, null, null, null, loc, false, override);
  }
  






  public SimpleDateFormat(String pattern, DateFormatSymbols formatData)
  {
    this(pattern, (DateFormatSymbols)formatData.clone(), null, null, null, true, null);
  }
  

  /**
   * @deprecated
   */
  public SimpleDateFormat(String pattern, DateFormatSymbols formatData, ULocale loc)
  {
    this(pattern, (DateFormatSymbols)formatData.clone(), null, null, loc, true, null);
  }
  






  SimpleDateFormat(String pattern, DateFormatSymbols formatData, Calendar calendar, ULocale locale, boolean useFastFormat, String override)
  {
    this(pattern, (DateFormatSymbols)formatData.clone(), (Calendar)calendar.clone(), null, locale, useFastFormat, override);
  }
  



  private SimpleDateFormat(String pattern, DateFormatSymbols formatData, Calendar calendar, NumberFormat numberFormat, ULocale locale, boolean useFastFormat, String override)
  {
    this.pattern = pattern;
    this.formatData = formatData;
    this.calendar = calendar;
    this.numberFormat = numberFormat;
    this.locale = locale;
    this.useFastFormat = useFastFormat;
    this.override = override;
    initialize();
  }
  




  /**
   * @deprecated
   */
  public static SimpleDateFormat getInstance(Calendar.FormatConfiguration formatConfig)
  {
    String ostr = formatConfig.getOverrideString();
    boolean useFast = (ostr != null) && (ostr.length() > 0);
    
    return new SimpleDateFormat(formatConfig.getPatternString(), formatConfig.getDateFormatSymbols(), formatConfig.getCalendar(), null, formatConfig.getLocale(), useFast, formatConfig.getOverrideString());
  }
  








  private void initialize()
  {
    if (locale == null) {
      locale = ULocale.getDefault(ULocale.Category.FORMAT);
    }
    if (formatData == null) {
      formatData = new DateFormatSymbols(locale);
    }
    if (calendar == null) {
      calendar = Calendar.getInstance(locale);
    }
    if (numberFormat == null) {
      NumberingSystem ns = NumberingSystem.getInstance(locale);
      if (ns.isAlgorithmic()) {
        numberFormat = NumberFormat.getInstance(locale);
      } else {
        String digitString = ns.getDescription();
        String nsName = ns.getName();
        
        numberFormat = new DateNumberFormat(locale, digitString, nsName);
      }
    }
    

    defaultCenturyBase = System.currentTimeMillis();
    
    setLocale(calendar.getLocale(ULocale.VALID_LOCALE), calendar.getLocale(ULocale.ACTUAL_LOCALE));
    initLocalZeroPaddingNumberFormat();
    
    if (override != null) {
      initNumberFormatters(locale);
    }
    
    capitalizationSetting = DisplayContext.CAPITALIZATION_NONE;
  }
  






  private synchronized void initializeTimeZoneFormat(boolean bForceUpdate)
  {
    if ((bForceUpdate) || (tzFormat == null)) {
      tzFormat = TimeZoneFormat.getInstance(locale);
      
      String digits = null;
      if ((numberFormat instanceof DecimalFormat)) {
        DecimalFormatSymbols decsym = ((DecimalFormat)numberFormat).getDecimalFormatSymbols();
        digits = new String(decsym.getDigits());
      } else if ((numberFormat instanceof DateNumberFormat)) {
        digits = new String(((DateNumberFormat)numberFormat).getDigits());
      }
      
      if ((digits != null) && 
        (!tzFormat.getGMTOffsetDigits().equals(digits))) {
        if (tzFormat.isFrozen()) {
          tzFormat = tzFormat.cloneAsThawed();
        }
        tzFormat.setGMTOffsetDigits(digits);
      }
    }
  }
  




  private TimeZoneFormat tzFormat()
  {
    if (tzFormat == null) {
      initializeTimeZoneFormat(false);
    }
    return tzFormat;
  }
  

  private static ULocale cachedDefaultLocale = null;
  private static String cachedDefaultPattern = null;
  
  private static final String FALLBACKPATTERN = "yy/MM/dd HH:mm";
  
  private static final int PATTERN_CHAR_BASE = 64;
  
  private static synchronized String getDefaultPattern()
  {
    ULocale defaultLocale = ULocale.getDefault(ULocale.Category.FORMAT);
    if (!defaultLocale.equals(cachedDefaultLocale)) {
      cachedDefaultLocale = defaultLocale;
      Calendar cal = Calendar.getInstance(cachedDefaultLocale);
      try {
        CalendarData calData = new CalendarData(cachedDefaultLocale, cal.getType());
        String[] dateTimePatterns = calData.getDateTimePatterns();
        int glueIndex = 8;
        if (dateTimePatterns.length >= 13)
        {
          glueIndex += 4;
        }
        cachedDefaultPattern = MessageFormat.format(dateTimePatterns[glueIndex], new Object[] { dateTimePatterns[3], dateTimePatterns[7] });
      }
      catch (MissingResourceException e) {
        cachedDefaultPattern = "yy/MM/dd HH:mm";
      }
    }
    return cachedDefaultPattern;
  }
  


  private void parseAmbiguousDatesAsAfter(Date startDate)
  {
    defaultCenturyStart = startDate;
    calendar.setTime(startDate);
    defaultCenturyStartYear = calendar.get(1);
  }
  


  private void initializeDefaultCenturyStart(long baseTime)
  {
    defaultCenturyBase = baseTime;
    

    Calendar tmpCal = (Calendar)calendar.clone();
    tmpCal.setTimeInMillis(baseTime);
    tmpCal.add(1, -80);
    defaultCenturyStart = tmpCal.getTime();
    defaultCenturyStartYear = tmpCal.get(1);
  }
  
  private Date getDefaultCenturyStart()
  {
    if (defaultCenturyStart == null)
    {
      initializeDefaultCenturyStart(defaultCenturyBase);
    }
    return defaultCenturyStart;
  }
  
  private int getDefaultCenturyStartYear()
  {
    if (defaultCenturyStart == null)
    {
      initializeDefaultCenturyStart(defaultCenturyBase);
    }
    return defaultCenturyStartYear;
  }
  






  public void set2DigitYearStart(Date startDate)
  {
    parseAmbiguousDatesAsAfter(startDate);
  }
  






  public Date get2DigitYearStart()
  {
    return getDefaultCenturyStart();
  }
  













  public StringBuffer format(Calendar cal, StringBuffer toAppendTo, FieldPosition pos)
  {
    TimeZone backupTZ = null;
    if ((cal != calendar) && (!cal.getType().equals(calendar.getType())))
    {


      calendar.setTimeInMillis(cal.getTimeInMillis());
      backupTZ = calendar.getTimeZone();
      calendar.setTimeZone(cal.getTimeZone());
      cal = calendar;
    }
    StringBuffer result = format(cal, capitalizationSetting, toAppendTo, pos, null);
    if (backupTZ != null)
    {
      calendar.setTimeZone(backupTZ);
    }
    return result;
  }
  



  private StringBuffer format(Calendar cal, DisplayContext capitalizationContext, StringBuffer toAppendTo, FieldPosition pos, List<FieldPosition> attributes)
  {
    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    




    Object[] items = getPatternItems();
    for (int i = 0; i < items.length; i++) {
      if ((items[i] instanceof String)) {
        toAppendTo.append((String)items[i]);
      } else {
        PatternItem item = (PatternItem)items[i];
        int start = 0;
        if (attributes != null)
        {
          start = toAppendTo.length();
        }
        if (useFastFormat) {
          subFormat(toAppendTo, type, length, toAppendTo.length(), i, capitalizationContext, pos, cal);
        }
        else {
          toAppendTo.append(subFormat(type, length, toAppendTo.length(), i, capitalizationContext, pos, cal));
        }
        
        if (attributes != null)
        {
          int end = toAppendTo.length();
          if (end - start > 0)
          {
            DateFormat.Field attr = patternCharToDateFormatField(type);
            FieldPosition fp = new FieldPosition(attr);
            fp.setBeginIndex(start);
            fp.setEndIndex(end);
            attributes.add(fp);
          }
        }
      }
    }
    return toAppendTo;
  }
  



  private static final int[] PATTERN_CHAR_TO_INDEX = { -1, 22, -1, -1, 10, 9, 11, 0, 5, -1, -1, 16, 26, 2, -1, 31, -1, 27, -1, 8, -1, 30, 29, 13, 32, 18, 23, -1, -1, -1, -1, -1, -1, 14, -1, 25, 3, 19, -1, 21, 15, -1, -1, 4, -1, 6, -1, -1, -1, 28, -1, 7, -1, 20, 24, 12, 33, 1, 17, -1, -1, -1, -1, -1 };
  











  private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = { 0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15, 17, 18, 19, 20, 21, 15, 15, 18, 2, 2, 2, 15, 1, 15, 15, 15 };
  



















  private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
  


















  private static final DateFormat.Field[] PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE = { DateFormat.Field.ERA, DateFormat.Field.YEAR, DateFormat.Field.MONTH, DateFormat.Field.DAY_OF_MONTH, DateFormat.Field.HOUR_OF_DAY1, DateFormat.Field.HOUR_OF_DAY0, DateFormat.Field.MINUTE, DateFormat.Field.SECOND, DateFormat.Field.MILLISECOND, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.DAY_OF_YEAR, DateFormat.Field.DAY_OF_WEEK_IN_MONTH, DateFormat.Field.WEEK_OF_YEAR, DateFormat.Field.WEEK_OF_MONTH, DateFormat.Field.AM_PM, DateFormat.Field.HOUR1, DateFormat.Field.HOUR0, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR_WOY, DateFormat.Field.DOW_LOCAL, DateFormat.Field.EXTENDED_YEAR, DateFormat.Field.JULIAN_DAY, DateFormat.Field.MILLISECONDS_IN_DAY, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.MONTH, DateFormat.Field.QUARTER, DateFormat.Field.QUARTER, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE };
  

























  protected DateFormat.Field patternCharToDateFormatField(char ch)
  {
    int patternCharIndex = -1;
    if (('A' <= ch) && (ch <= 'z')) {
      patternCharIndex = PATTERN_CHAR_TO_INDEX[(ch - '@')];
    }
    if (patternCharIndex != -1) {
      return PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE[patternCharIndex];
    }
    return null;
  }
  















  protected String subFormat(char ch, int count, int beginOffset, FieldPosition pos, DateFormatSymbols fmtData, Calendar cal)
    throws IllegalArgumentException
  {
    return subFormat(ch, count, beginOffset, 0, DisplayContext.CAPITALIZATION_NONE, pos, cal);
  }
  







  /**
   * @deprecated
   */
  protected String subFormat(char ch, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal)
  {
    StringBuffer buf = new StringBuffer();
    subFormat(buf, ch, count, beginOffset, fieldNum, capitalizationContext, pos, cal);
    return buf.toString();
  }
  














  /**
   * @deprecated
   */
  protected void subFormat(StringBuffer buf, char ch, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal)
  {
    int maxIntCount = Integer.MAX_VALUE;
    int bufstart = buf.length();
    TimeZone tz = cal.getTimeZone();
    long date = cal.getTimeInMillis();
    String result = null;
    

    int patternCharIndex = -1;
    if (('A' <= ch) && (ch <= 'z')) {
      patternCharIndex = PATTERN_CHAR_TO_INDEX[(ch - '@')];
    }
    
    if (patternCharIndex == -1) {
      if (ch == 'l') {
        return;
      }
      throw new IllegalArgumentException("Illegal pattern character '" + ch + "' in \"" + pattern + '"');
    }
    



    int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
    int value = cal.get(field);
    
    NumberFormat currentNumberFormat = getNumberFormat(ch);
    DateFormatSymbols.CapitalizationContextUsage capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.OTHER;
    
    switch (patternCharIndex) {
    case 0: 
      if (cal.getType().equals("chinese"))
      {
        zeroPaddingNumber(currentNumberFormat, buf, value, 1, 9);
      }
      else if (count == 5) {
        safeAppend(formatData.narrowEras, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_NARROW;
      } else if (count == 4) {
        safeAppend(formatData.eraNames, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_WIDE;
      } else {
        safeAppend(formatData.eras, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_ABBREV;
      }
      
      break;
    case 30: 
      if ((formatData.shortYearNames != null) && (value <= formatData.shortYearNames.length))
        safeAppend(formatData.shortYearNames, value - 1, buf);
      break;
    

    case 1: 
    case 18: 
      if ((override != null) && ((override.compareTo("hebr") == 0) || (override.indexOf("y=hebr") >= 0)) && (value > 5000) && (value < 6000))
      {
        value -= 5000;
      }
      





      if (count == 2) {
        zeroPaddingNumber(currentNumberFormat, buf, value, 2, 2);
      } else {
        zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
      }
      break;
    case 2: 
    case 26: 
      if (cal.getType().equals("hebrew")) {
        boolean isLeap = HebrewCalendar.isLeapYear(cal.get(1));
        if ((isLeap) && (value == 6) && (count >= 3)) {
          value = 13;
        }
        if ((!isLeap) && (value >= 6) && (count < 3)) {
          value--;
        }
      }
      int isLeapMonth = (formatData.leapMonthPatterns != null) && (formatData.leapMonthPatterns.length >= 7) ? cal.get(22) : 0;
      

      if (count == 5) {
        if (patternCharIndex == 2) {
          safeAppendWithMonthPattern(formatData.narrowMonths, value, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[2] : null);
        } else {
          safeAppendWithMonthPattern(formatData.standaloneNarrowMonths, value, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[5] : null);
        }
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_NARROW;
      } else if (count == 4) {
        if (patternCharIndex == 2) {
          safeAppendWithMonthPattern(formatData.months, value, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[0] : null);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_FORMAT;
        } else {
          safeAppendWithMonthPattern(formatData.standaloneMonths, value, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[3] : null);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_STANDALONE;
        }
      } else if (count == 3) {
        if (patternCharIndex == 2) {
          safeAppendWithMonthPattern(formatData.shortMonths, value, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[1] : null);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_FORMAT;
        } else {
          safeAppendWithMonthPattern(formatData.standaloneShortMonths, value, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[4] : null);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_STANDALONE;
        }
      } else {
        StringBuffer monthNumber = new StringBuffer();
        zeroPaddingNumber(currentNumberFormat, monthNumber, value + 1, count, Integer.MAX_VALUE);
        String[] monthNumberStrings = new String[1];
        monthNumberStrings[0] = monthNumber.toString();
        safeAppendWithMonthPattern(monthNumberStrings, 0, buf, isLeapMonth != 0 ? formatData.leapMonthPatterns[6] : null);
      }
      break;
    case 4: 
      if (value == 0) {
        zeroPaddingNumber(currentNumberFormat, buf, cal.getMaximum(11) + 1, count, Integer.MAX_VALUE);
      }
      else
      {
        zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
      }
      break;
    

    case 8: 
      numberFormat.setMinimumIntegerDigits(Math.min(3, count));
      numberFormat.setMaximumIntegerDigits(Integer.MAX_VALUE);
      if (count == 1) {
        value /= 100;
      } else if (count == 2) {
        value /= 10;
      }
      FieldPosition p = new FieldPosition(-1);
      numberFormat.format(value, buf, p);
      if (count > 3) {
        numberFormat.setMinimumIntegerDigits(count - 3);
        numberFormat.format(0L, buf, p);
      }
      
      break;
    case 19: 
      if (count < 3) {
        zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
        
        break label2190;
      }
      
      value = cal.get(7);
    
    case 9: 
      if (count == 5) {
        safeAppend(formatData.narrowWeekdays, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_NARROW;
      } else if (count == 4) {
        safeAppend(formatData.weekdays, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
      } else if ((count == 6) && (formatData.shorterWeekdays != null)) {
        safeAppend(formatData.shorterWeekdays, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
      } else {
        safeAppend(formatData.shortWeekdays, value, buf);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
      }
      break;
    case 14: 
      safeAppend(formatData.ampms, value, buf);
      break;
    case 15: 
      if (value == 0) {
        zeroPaddingNumber(currentNumberFormat, buf, cal.getLeastMaximum(10) + 1, count, Integer.MAX_VALUE);
      }
      else
      {
        zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
      }
      break;
    
    case 17: 
      if (count < 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.SPECIFIC_SHORT, tz, date);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_SHORT;
      } else {
        result = tzFormat().format(TimeZoneFormat.Style.SPECIFIC_LONG, tz, date);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_LONG;
      }
      buf.append(result);
      break;
    case 23: 
      if (count < 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL, tz, date);
      } else if (count == 5)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FULL, tz, date);
      }
      else {
        result = tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT, tz, date);
      }
      buf.append(result);
      break;
    case 24: 
      if (count == 1)
      {
        result = tzFormat().format(TimeZoneFormat.Style.GENERIC_SHORT, tz, date);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_SHORT;
      } else if (count == 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.GENERIC_LONG, tz, date);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_LONG;
      }
      buf.append(result);
      break;
    case 29: 
      if (count == 1)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ZONE_ID_SHORT, tz, date);
      } else if (count == 2)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ZONE_ID, tz, date);
      } else if (count == 3)
      {
        result = tzFormat().format(TimeZoneFormat.Style.EXEMPLAR_LOCATION, tz, date);
      } else if (count == 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.GENERIC_LOCATION, tz, date);
        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ZONE_LONG;
      }
      buf.append(result);
      break;
    case 31: 
      if (count == 1)
      {
        result = tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT_SHORT, tz, date);
      } else if (count == 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT, tz, date);
      }
      buf.append(result);
      break;
    case 32: 
      if (count == 1)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_SHORT, tz, date);
      } else if (count == 2)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_FIXED, tz, date);
      } else if (count == 3)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FIXED, tz, date);
      } else if (count == 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_FULL, tz, date);
      } else if (count == 5)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FULL, tz, date);
      }
      buf.append(result);
      break;
    case 33: 
      if (count == 1)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_SHORT, tz, date);
      } else if (count == 2)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FIXED, tz, date);
      } else if (count == 3)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FIXED, tz, date);
      } else if (count == 4)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL, tz, date);
      } else if (count == 5)
      {
        result = tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FULL, tz, date);
      }
      buf.append(result);
      break;
    
    case 25: 
      if (count < 3) {
        zeroPaddingNumber(currentNumberFormat, buf, value, 1, Integer.MAX_VALUE);

      }
      else
      {
        value = cal.get(7);
        if (count == 5) {
          safeAppend(formatData.standaloneNarrowWeekdays, value, buf);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_NARROW;
        } else if (count == 4) {
          safeAppend(formatData.standaloneWeekdays, value, buf);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
        } else if ((count == 6) && (formatData.standaloneShorterWeekdays != null)) {
          safeAppend(formatData.standaloneShorterWeekdays, value, buf);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
        } else {
          safeAppend(formatData.standaloneShortWeekdays, value, buf);
          capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
        } }
      break;
    case 27: 
      if (count >= 4) {
        safeAppend(formatData.quarters, value / 3, buf);
      } else if (count == 3) {
        safeAppend(formatData.shortQuarters, value / 3, buf);
      } else {
        zeroPaddingNumber(currentNumberFormat, buf, value / 3 + 1, count, Integer.MAX_VALUE);
      }
      break;
    case 28: 
      if (count >= 4) {
        safeAppend(formatData.standaloneQuarters, value / 3, buf);
      } else if (count == 3) {
        safeAppend(formatData.standaloneShortQuarters, value / 3, buf);
      } else {
        zeroPaddingNumber(currentNumberFormat, buf, value / 3 + 1, count, Integer.MAX_VALUE);
      }
      break;
    }
    
    











    zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
    
    label2190:
    
    if (fieldNum == 0) {
      boolean titlecase = false;
      if (capitalizationContext != null) {
        switch (1.$SwitchMap$com$ibm$icu$text$DisplayContext[capitalizationContext.ordinal()]) {
        case 1: 
          titlecase = true;
          break;
        case 2: 
        case 3: 
          if (formatData.capitalization != null) {
            boolean[] transforms = (boolean[])formatData.capitalization.get(capContextUsageType);
            titlecase = capitalizationContext == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU ? transforms[0] : transforms[1];
          }
          break;
        }
        
      }
      

      if (titlecase) {
        String firstField = buf.substring(bufstart);
        String firstFieldTitleCase = UCharacter.toTitleCase(locale, firstField, null, 768);
        
        buf.replace(bufstart, buf.length(), firstFieldTitleCase);
      }
    }
    

    if (pos.getBeginIndex() == pos.getEndIndex()) {
      if (pos.getField() == PATTERN_INDEX_TO_DATE_FORMAT_FIELD[patternCharIndex]) {
        pos.setBeginIndex(beginOffset);
        pos.setEndIndex(beginOffset + buf.length() - bufstart);
      } else if (pos.getFieldAttribute() == PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE[patternCharIndex])
      {
        pos.setBeginIndex(beginOffset);
        pos.setEndIndex(beginOffset + buf.length() - bufstart);
      }
    }
  }
  
  private static void safeAppend(String[] array, int value, StringBuffer appendTo) {
    if ((array != null) && (value >= 0) && (value < array.length)) {
      appendTo.append(array[value]);
    }
  }
  
  private static void safeAppendWithMonthPattern(String[] array, int value, StringBuffer appendTo, String monthPattern) {
    if ((array != null) && (value >= 0) && (value < array.length)) {
      if (monthPattern == null) {
        appendTo.append(array[value]);
      } else {
        appendTo.append(MessageFormat.format(monthPattern, new Object[] { array[value] }));
      }
    }
  }
  

  private static class PatternItem
  {
    final char type;
    final int length;
    final boolean isNumeric;
    
    PatternItem(char type, int length)
    {
      this.type = type;
      this.length = length;
      isNumeric = SimpleDateFormat.isNumeric(type, length);
    }
  }
  
  private static ICUCache<String, Object[]> PARSED_PATTERN_CACHE = new SimpleCache();
  private transient Object[] patternItems;
  private transient boolean useLocalZeroPaddingNumberFormat;
  private transient char[] decDigits;
  private transient char[] decimalBuf;
  private static final String NUMERIC_FORMAT_CHARS = "MYyudehHmsSDFwWkK";
  
  private Object[] getPatternItems()
  {
    if (patternItems != null) {
      return patternItems;
    }
    
    patternItems = ((Object[])PARSED_PATTERN_CACHE.get(pattern));
    if (patternItems != null) {
      return patternItems;
    }
    
    boolean isPrevQuote = false;
    boolean inQuote = false;
    StringBuilder text = new StringBuilder();
    char itemType = '\000';
    int itemLength = 1;
    
    List<Object> items = new ArrayList();
    
    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      if (ch == '\'') {
        if (isPrevQuote) {
          text.append('\'');
          isPrevQuote = false;
        } else {
          isPrevQuote = true;
          if (itemType != 0) {
            items.add(new PatternItem(itemType, itemLength));
            itemType = '\000';
          }
        }
        inQuote = !inQuote;
      } else {
        isPrevQuote = false;
        if (inQuote) {
          text.append(ch);
        }
        else if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')))
        {
          if (ch == itemType) {
            itemLength++;
          } else {
            if (itemType == 0) {
              if (text.length() > 0) {
                items.add(text.toString());
                text.setLength(0);
              }
            } else {
              items.add(new PatternItem(itemType, itemLength));
            }
            itemType = ch;
            itemLength = 1;
          }
        }
        else {
          if (itemType != 0) {
            items.add(new PatternItem(itemType, itemLength));
            itemType = '\000';
          }
          text.append(ch);
        }
      }
    }
    

    if (itemType == 0) {
      if (text.length() > 0) {
        items.add(text.toString());
        text.setLength(0);
      }
    } else {
      items.add(new PatternItem(itemType, itemLength));
    }
    
    patternItems = items.toArray(new Object[items.size()]);
    
    PARSED_PATTERN_CACHE.put(pattern, patternItems);
    
    return patternItems;
  }
  






  /**
   * @deprecated
   */
  protected void zeroPaddingNumber(NumberFormat nf, StringBuffer buf, int value, int minDigits, int maxDigits)
  {
    if ((useLocalZeroPaddingNumberFormat) && (value >= 0)) {
      fastZeroPaddingNumber(buf, value, minDigits, maxDigits);
    } else {
      nf.setMinimumIntegerDigits(minDigits);
      nf.setMaximumIntegerDigits(maxDigits);
      nf.format(value, buf, new FieldPosition(-1));
    }
  }
  




  public void setNumberFormat(NumberFormat newNumberFormat)
  {
    super.setNumberFormat(newNumberFormat);
    initLocalZeroPaddingNumberFormat();
    initializeTimeZoneFormat(true);
  }
  
  private void initLocalZeroPaddingNumberFormat() {
    if ((numberFormat instanceof DecimalFormat)) {
      decDigits = ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getDigits();
      useLocalZeroPaddingNumberFormat = true;
    } else if ((numberFormat instanceof DateNumberFormat)) {
      decDigits = ((DateNumberFormat)numberFormat).getDigits();
      useLocalZeroPaddingNumberFormat = true;
    } else {
      useLocalZeroPaddingNumberFormat = false;
    }
    
    if (useLocalZeroPaddingNumberFormat) {
      decimalBuf = new char[10];
    }
  }
  















  private void fastZeroPaddingNumber(StringBuffer buf, int value, int minDigits, int maxDigits)
  {
    int limit = decimalBuf.length < maxDigits ? decimalBuf.length : maxDigits;
    int index = limit - 1;
    for (;;) {
      decimalBuf[index] = decDigits[(value % 10)];
      value /= 10;
      if ((index == 0) || (value == 0)) {
        break;
      }
      index--;
    }
    int padding = minDigits - (limit - index);
    while ((padding > 0) && (index > 0)) {
      decimalBuf[(--index)] = decDigits[0];
      padding--;
    }
    while (padding > 0)
    {

      buf.append(decDigits[0]);
      padding--;
    }
    buf.append(decimalBuf, index, limit - index);
  }
  




  protected String zeroPaddingNumber(long value, int minDigits, int maxDigits)
  {
    numberFormat.setMinimumIntegerDigits(minDigits);
    numberFormat.setMaximumIntegerDigits(maxDigits);
    return numberFormat.format(value);
  }
  









  private static final boolean isNumeric(char formatChar, int count)
  {
    int i = "MYyudehHmsSDFwWkK".indexOf(formatChar);
    return (i > 0) || ((i == 0) && (count < 3));
  }
  





  public void parse(String text, Calendar cal, ParsePosition parsePos)
  {
    TimeZone backupTZ = null;
    Calendar resultCal = null;
    if ((cal != calendar) && (!cal.getType().equals(calendar.getType())))
    {


      calendar.setTimeInMillis(cal.getTimeInMillis());
      backupTZ = calendar.getTimeZone();
      calendar.setTimeZone(cal.getTimeZone());
      resultCal = cal;
      cal = calendar;
    }
    
    int pos = parsePos.getIndex();
    int start = pos;
    

    tztype = TimeZoneFormat.TimeType.UNKNOWN;
    boolean[] ambiguousYear = { false };
    

    int numericFieldStart = -1;
    
    int numericFieldLength = 0;
    
    int numericStartPos = 0;
    
    MessageFormat numericLeapMonthFormatter = null;
    if ((formatData.leapMonthPatterns != null) && (formatData.leapMonthPatterns.length >= 7)) {
      numericLeapMonthFormatter = new MessageFormat(formatData.leapMonthPatterns[6], locale);
    }
    
    Object[] items = getPatternItems();
    int i = 0;
    while (i < items.length) {
      if ((items[i] instanceof PatternItem))
      {
        PatternItem field = (PatternItem)items[i];
        if (isNumeric)
        {






          if (numericFieldStart == -1)
          {
            if ((i + 1 < items.length) && ((items[(i + 1)] instanceof PatternItem)) && (1isNumeric))
            {


              numericFieldStart = i;
              numericFieldLength = length;
              numericStartPos = pos;
            }
          }
        }
        if (numericFieldStart != -1)
        {
          int len = length;
          if (numericFieldStart == i) {
            len = numericFieldLength;
          }
          

          pos = subParse(text, pos, type, len, true, false, ambiguousYear, cal, numericLeapMonthFormatter);
          

          if (pos < 0)
          {


            numericFieldLength--;
            if (numericFieldLength == 0)
            {
              parsePos.setIndex(start);
              parsePos.setErrorIndex(pos);
              if (backupTZ != null) {
                calendar.setTimeZone(backupTZ);
              }
              return;
            }
            i = numericFieldStart;
            pos = numericStartPos;
            continue;
          }
        }
        else if (type != 'l')
        {
          numericFieldStart = -1;
          
          int s = pos;
          pos = subParse(text, pos, type, length, false, true, ambiguousYear, cal, numericLeapMonthFormatter);
          

          if (pos < 0) {
            if (pos == 33536)
            {
              pos = s;
              
              if (i + 1 < items.length)
              {
                String patl = null;
                try
                {
                  patl = (String)items[(i + 1)];
                } catch (ClassCastException cce) {
                  parsePos.setIndex(start);
                  parsePos.setErrorIndex(s);
                  if (backupTZ != null) {
                    calendar.setTimeZone(backupTZ);
                  }
                  return;
                }
                

                if (patl == null)
                  patl = (String)items[(i + 1)];
                int plen = patl.length();
                int idx = 0;
                


                while (idx < plen)
                {
                  char pch = patl.charAt(idx);
                  if (!PatternProps.isWhiteSpace(pch)) break;
                  idx++;
                }
                



                if (idx == plen) {
                  i++;
                }
              }
            }
            else {
              parsePos.setIndex(start);
              parsePos.setErrorIndex(s);
              if (backupTZ != null) {
                calendar.setTimeZone(backupTZ);
              }
              return;
            }
          }
        }
      }
      else
      {
        numericFieldStart = -1;
        boolean[] complete = new boolean[1];
        pos = matchLiteral(text, pos, items, i, complete);
        if (complete[0] == 0)
        {
          parsePos.setIndex(start);
          parsePos.setErrorIndex(pos);
          if (backupTZ != null) {
            calendar.setTimeZone(backupTZ);
          }
          return;
        }
      }
      i++;
    }
    

    if (pos < text.length()) {
      char extra = text.charAt(pos);
      if ((extra == '.') && (isLenient()) && (items.length != 0))
      {
        Object lastItem = items[(items.length - 1)];
        if (((lastItem instanceof PatternItem)) && (!isNumeric)) {
          pos++;
        }
      }
    }
    




    parsePos.setIndex(pos);
    





















    try
    {
      if ((ambiguousYear[0] != 0) || (tztype != TimeZoneFormat.TimeType.UNKNOWN))
      {




        if (ambiguousYear[0] != 0) {
          Calendar copy = (Calendar)cal.clone();
          Date parsedDate = copy.getTime();
          if (parsedDate.before(getDefaultCenturyStart()))
          {
            cal.set(1, getDefaultCenturyStartYear() + 100);
          }
        }
        if (tztype != TimeZoneFormat.TimeType.UNKNOWN) {
          Calendar copy = (Calendar)cal.clone();
          TimeZone tz = copy.getTimeZone();
          BasicTimeZone btz = null;
          if ((tz instanceof BasicTimeZone)) {
            btz = (BasicTimeZone)tz;
          }
          

          copy.set(15, 0);
          copy.set(16, 0);
          long localMillis = copy.getTimeInMillis();
          


          int[] offsets = new int[2];
          if (btz != null) {
            if (tztype == TimeZoneFormat.TimeType.STANDARD) {
              btz.getOffsetFromLocal(localMillis, 1, 1, offsets);
            }
            else {
              btz.getOffsetFromLocal(localMillis, 3, 3, offsets);
            }
            
          }
          else
          {
            tz.getOffset(localMillis, true, offsets);
            
            if (((tztype == TimeZoneFormat.TimeType.STANDARD) && (offsets[1] != 0)) || ((tztype == TimeZoneFormat.TimeType.DAYLIGHT) && (offsets[1] == 0)))
            {





              tz.getOffset(localMillis - 86400000L, true, offsets);
            }
          }
          


          int resolvedSavings = offsets[1];
          if (tztype == TimeZoneFormat.TimeType.STANDARD) {
            if (offsets[1] != 0)
            {
              resolvedSavings = 0;
            }
          }
          else if (offsets[1] == 0) {
            if (btz != null) {
              long time = localMillis + offsets[0];
              

              long beforeT = time;long afterT = time;
              int beforeSav = 0;int afterSav = 0;
              TimeZoneTransition beforeTrs;
              for (;;)
              {
                beforeTrs = btz.getPreviousTransition(beforeT, true);
                if (beforeTrs != null)
                {

                  beforeT = beforeTrs.getTime() - 1L;
                  beforeSav = beforeTrs.getFrom().getDSTSavings();
                  if (beforeSav != 0) {
                    break;
                  }
                }
              }
              TimeZoneTransition afterTrs;
              for (;;) {
                afterTrs = btz.getNextTransition(afterT, false);
                if (afterTrs != null)
                {

                  afterT = afterTrs.getTime();
                  afterSav = afterTrs.getTo().getDSTSavings();
                  if (afterSav != 0) {
                    break;
                  }
                }
              }
              if ((beforeTrs != null) && (afterTrs != null)) {
                if (time - beforeT > afterT - time) {
                  resolvedSavings = afterSav;
                } else {
                  resolvedSavings = beforeSav;
                }
              } else if ((beforeTrs != null) && (beforeSav != 0)) {
                resolvedSavings = beforeSav;
              } else if ((afterTrs != null) && (afterSav != 0)) {
                resolvedSavings = afterSav;
              } else {
                resolvedSavings = btz.getDSTSavings();
              }
            } else {
              resolvedSavings = tz.getDSTSavings();
            }
            if (resolvedSavings == 0)
            {
              resolvedSavings = 3600000;
            }
          }
          
          cal.set(15, offsets[0]);
          cal.set(16, resolvedSavings);
        }
        
      }
    }
    catch (IllegalArgumentException e)
    {
      parsePos.setErrorIndex(pos);
      parsePos.setIndex(start);
      if (backupTZ != null) {
        calendar.setTimeZone(backupTZ);
      }
      return;
    }
    

    if (resultCal != null) {
      resultCal.setTimeZone(cal.getTimeZone());
      resultCal.setTimeInMillis(cal.getTimeInMillis());
    }
    
    if (backupTZ != null) {
      calendar.setTimeZone(backupTZ);
    }
  }
  








  private int matchLiteral(String text, int pos, Object[] items, int itemIndex, boolean[] complete)
  {
    int originalPos = pos;
    String patternLiteral = (String)items[itemIndex];
    int plen = patternLiteral.length();
    int tlen = text.length();
    int idx = 0;
    while ((idx < plen) && (pos < tlen)) {
      char pch = patternLiteral.charAt(idx);
      char ich = text.charAt(pos);
      if ((PatternProps.isWhiteSpace(pch)) && (PatternProps.isWhiteSpace(ich)))
      {


        while ((idx + 1 < plen) && (PatternProps.isWhiteSpace(patternLiteral.charAt(idx + 1))))
        {
          idx++; } }
      for (;;) {
        if ((pos + 1 < tlen) && (PatternProps.isWhiteSpace(text.charAt(pos + 1))))
        {
          pos++; continue;
          
          if (pch != ich) {
            if ((ich != '.') || (pos != originalPos) || (0 >= itemIndex) || (!isLenient())) break label212;
            Object before = items[(itemIndex - 1)];
            if ((before instanceof PatternItem)) {
              boolean isNumeric = isNumeric;
              if (!isNumeric) {
                pos++;
                break;
              }
            }
            break label212;
          }
        } }
      idx++;
      pos++; }
    label212:
    complete[0] = (idx == plen ? 1 : false);
    if ((complete[0] == 0) && (isLenient()) && (0 < itemIndex) && (itemIndex < items.length - 1))
    {


      if (originalPos < tlen) {
        Object before = items[(itemIndex - 1)];
        Object after = items[(itemIndex + 1)];
        if (((before instanceof PatternItem)) && ((after instanceof PatternItem))) {
          char beforeType = type;
          char afterType = type;
          if (DATE_PATTERN_TYPE.contains(beforeType) != DATE_PATTERN_TYPE.contains(afterType)) {
            int newPos = originalPos;
            for (;;) {
              char ich = text.charAt(newPos);
              if (!PatternProps.isWhiteSpace(ich)) {
                break;
              }
              newPos++;
            }
            complete[0] = (newPos > originalPos ? 1 : false);
            pos = newPos;
          }
        }
      }
    }
    return pos;
  }
  
  static final UnicodeSet DATE_PATTERN_TYPE = new UnicodeSet("[GyYuUQqMLlwWd]").freeze();
  


















  protected int matchString(String text, int start, int field, String[] data, Calendar cal)
  {
    return matchString(text, start, field, data, null, cal);
  }
  




















  protected int matchString(String text, int start, int field, String[] data, String monthPattern, Calendar cal)
  {
    int i = 0;
    int count = data.length;
    
    if (field == 7) { i = 1;
    }
    



    int bestMatchLength = 0;int bestMatch = -1;
    int isLeapMonth = 0;
    int matchLength = 0;
    for (; 
        i < count; i++)
    {
      int length = data[i].length();
      

      if ((length > bestMatchLength) && ((matchLength = regionMatchesWithOptionalDot(text, start, data[i], length)) >= 0))
      {

        bestMatch = i;
        bestMatchLength = matchLength;
        isLeapMonth = 0;
      }
      if (monthPattern != null) {
        String leapMonthName = MessageFormat.format(monthPattern, new Object[] { data[i] });
        length = leapMonthName.length();
        if ((length > bestMatchLength) && ((matchLength = regionMatchesWithOptionalDot(text, start, leapMonthName, length)) >= 0))
        {

          bestMatch = i;
          bestMatchLength = matchLength;
          isLeapMonth = 1;
        }
      }
    }
    if (bestMatch >= 0)
    {
      if (field == 1) {
        bestMatch++;
      }
      cal.set(field, bestMatch);
      if (monthPattern != null) {
        cal.set(22, isLeapMonth);
      }
      return start + bestMatchLength;
    }
    return -start;
  }
  
  private int regionMatchesWithOptionalDot(String text, int start, String data, int length) {
    boolean matches = text.regionMatches(true, start, data, 0, length);
    if (matches) {
      return length;
    }
    if ((data.length() > 0) && (data.charAt(data.length() - 1) == '.') && 
      (text.regionMatches(true, start, data, 0, length - 1))) {
      return length - 1;
    }
    
    return -1;
  }
  

















  protected int matchQuarterString(String text, int start, int field, String[] data, Calendar cal)
  {
    int i = 0;
    int count = data.length;
    




    int bestMatchLength = 0;int bestMatch = -1;
    int matchLength = 0;
    for (; i < count; i++) {
      int length = data[i].length();
      

      if ((length > bestMatchLength) && ((matchLength = regionMatchesWithOptionalDot(text, start, data[i], length)) >= 0))
      {

        bestMatch = i;
        bestMatchLength = matchLength;
      }
    }
    
    if (bestMatch >= 0) {
      cal.set(field, bestMatch * 3);
      return start + bestMatchLength;
    }
    
    return -start;
  }
  






















  protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal)
  {
    return subParse(text, start, ch, count, obeyCount, allowNegative, ambiguousYear, cal, null);
  }
  
























  protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal, MessageFormat numericLeapMonthFormatter)
  {
    Number number = null;
    NumberFormat currentNumberFormat = null;
    int value = 0;
    
    ParsePosition pos = new ParsePosition(0);
    boolean lenient = isLenient();
    

    int patternCharIndex = -1;
    if (('A' <= ch) && (ch <= 'z')) {
      patternCharIndex = PATTERN_CHAR_TO_INDEX[(ch - '@')];
    }
    
    if (patternCharIndex == -1) {
      return -start;
    }
    
    currentNumberFormat = getNumberFormat(ch);
    
    int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
    
    if (numericLeapMonthFormatter != null) {
      numericLeapMonthFormatter.setFormatByArgumentIndex(0, currentNumberFormat);
    }
    

    for (;;)
    {
      if (start >= text.length()) {
        return -start;
      }
      int c = UTF16.charAt(text, start);
      if ((!UCharacter.isUWhiteSpace(c)) || (!PatternProps.isWhiteSpace(c))) {
        break;
      }
      start += UTF16.getCharCount(c);
    }
    pos.setIndex(start);
    




    if ((patternCharIndex == 4) || (patternCharIndex == 15) || ((patternCharIndex == 2) && (count <= 2)) || ((patternCharIndex == 26) && (count <= 2)) || (patternCharIndex == 1) || (patternCharIndex == 18) || (patternCharIndex == 30) || ((patternCharIndex == 0) && (cal.getType().equals("chinese"))) || (patternCharIndex == 8))
    {










      boolean parsedNumericLeapMonth = false;
      if ((numericLeapMonthFormatter != null) && ((patternCharIndex == 2) || (patternCharIndex == 26)))
      {
        Object[] args = numericLeapMonthFormatter.parse(text, pos);
        if ((args != null) && (pos.getIndex() > start) && ((args[0] instanceof Number))) {
          parsedNumericLeapMonth = true;
          number = (Number)args[0];
          cal.set(22, 1);
        } else {
          pos.setIndex(start);
          cal.set(22, 0);
        }
      }
      
      if (!parsedNumericLeapMonth) {
        if (obeyCount) {
          if (start + count > text.length()) {
            return -start;
          }
          number = parseInt(text, count, pos, allowNegative, currentNumberFormat);
        } else {
          number = parseInt(text, pos, allowNegative, currentNumberFormat);
        }
        if ((number == null) && (patternCharIndex != 30)) {
          return -start;
        }
      }
      
      if (number != null) {
        value = number.intValue();
      }
    }
    
    switch (patternCharIndex)
    {
    case 0: 
      if (cal.getType().equals("chinese"))
      {

        cal.set(0, value);
        return pos.getIndex();
      }
      int ps = 0;
      if (count == 5) {
        ps = matchString(text, start, 0, formatData.narrowEras, null, cal);
      } else if (count == 4) {
        ps = matchString(text, start, 0, formatData.eraNames, null, cal);
      } else {
        ps = matchString(text, start, 0, formatData.eras, null, cal);
      }
      



      if (ps == -start) {
        ps = 33536;
      }
      return ps;
    








    case 1: 
    case 18: 
      if ((override != null) && ((override.compareTo("hebr") == 0) || (override.indexOf("y=hebr") >= 0)) && (value < 1000)) {
        value += 5000;
      } else if ((count == 2) && (pos.getIndex() - start == 2) && (!cal.getType().equals("chinese")) && (UCharacter.isDigit(text.charAt(start))) && (UCharacter.isDigit(text.charAt(start + 1))))
      {










        int ambiguousTwoDigitYear = getDefaultCenturyStartYear() % 100;
        ambiguousYear[0] = (value == ambiguousTwoDigitYear ? 1 : false);
        value += getDefaultCenturyStartYear() / 100 * 100 + (value < ambiguousTwoDigitYear ? 100 : 0);
      }
      
      cal.set(field, value);
      

      if (DelayedHebrewMonthCheck) {
        if (!HebrewCalendar.isLeapYear(value)) {
          cal.add(2, 1);
        }
        DelayedHebrewMonthCheck = false;
      }
      return pos.getIndex();
    case 30: 
      if (formatData.shortYearNames != null) {
        int newStart = matchString(text, start, 1, formatData.shortYearNames, null, cal);
        if (newStart > 0) {
          return newStart;
        }
      }
      if ((number != null) && ((lenient) || (formatData.shortYearNames == null) || (value > formatData.shortYearNames.length))) {
        cal.set(1, value);
        return pos.getIndex();
      }
      return -start;
    case 2: 
    case 26: 
      if (count <= 2)
      {


        cal.set(2, value - 1);
        



        if ((cal.getType().equals("hebrew")) && (value >= 6)) {
          if (cal.isSet(1)) {
            if (!HebrewCalendar.isLeapYear(cal.get(1))) {
              cal.set(2, value);
            }
          } else {
            DelayedHebrewMonthCheck = true;
          }
        }
        return pos.getIndex();
      }
      

      boolean haveMonthPat = (formatData.leapMonthPatterns != null) && (formatData.leapMonthPatterns.length >= 7);
      
      int newStart = patternCharIndex == 2 ? matchString(text, start, 2, formatData.months, haveMonthPat ? formatData.leapMonthPatterns[0] : null, cal) : matchString(text, start, 2, formatData.standaloneMonths, haveMonthPat ? formatData.leapMonthPatterns[3] : null, cal);
      



      if (newStart > 0) {
        return newStart;
      }
      return patternCharIndex == 2 ? matchString(text, start, 2, formatData.shortMonths, haveMonthPat ? formatData.leapMonthPatterns[1] : null, cal) : matchString(text, start, 2, formatData.standaloneShortMonths, haveMonthPat ? formatData.leapMonthPatterns[4] : null, cal);
    






    case 4: 
      if (value == cal.getMaximum(11) + 1) {
        value = 0;
      }
      cal.set(11, value);
      return pos.getIndex();
    
    case 8: 
      int i = pos.getIndex() - start;
      if (i < 3) {
        while (i < 3) {
          value *= 10;
          i++;
        }
      }
      int a = 1;
      while (i > 3) {
        a *= 10;
        i--;
      }
      value /= a;
      
      cal.set(14, value);
      return pos.getIndex();
    
    case 9: 
      int newStart = matchString(text, start, 7, formatData.weekdays, null, cal);
      if (newStart > 0)
        return newStart;
      if ((newStart = matchString(text, start, 7, formatData.shortWeekdays, null, cal)) > 0)
        return newStart;
      if (formatData.shorterWeekdays != null) {
        return matchString(text, start, 7, formatData.shorterWeekdays, null, cal);
      }
      return newStart;
    

    case 25: 
      int newStart = matchString(text, start, 7, formatData.standaloneWeekdays, null, cal);
      if (newStart > 0)
        return newStart;
      if ((newStart = matchString(text, start, 7, formatData.standaloneShortWeekdays, null, cal)) > 0)
        return newStart;
      if (formatData.standaloneShorterWeekdays != null) {
        return matchString(text, start, 7, formatData.standaloneShorterWeekdays, null, cal);
      }
      return newStart;
    
    case 14: 
      return matchString(text, start, 9, formatData.ampms, null, cal);
    
    case 15: 
      if (value == cal.getLeastMaximum(10) + 1) {
        value = 0;
      }
      cal.set(10, value);
      return pos.getIndex();
    
    case 17: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.SPECIFIC_SHORT : TimeZoneFormat.Style.SPECIFIC_LONG;
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    

    case 23: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      TimeZoneFormat.Style style = count == 5 ? TimeZoneFormat.Style.ISO_EXTENDED_FULL : count < 4 ? TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL : TimeZoneFormat.Style.LOCALIZED_GMT;
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    

    case 24: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      
      TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.GENERIC_SHORT : TimeZoneFormat.Style.GENERIC_LONG;
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    

    case 29: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      TimeZoneFormat.Style style = null;
      switch (count) {
      case 1: 
        style = TimeZoneFormat.Style.ZONE_ID_SHORT;
        break;
      case 2: 
        style = TimeZoneFormat.Style.ZONE_ID;
        break;
      case 3: 
        style = TimeZoneFormat.Style.EXEMPLAR_LOCATION;
        break;
      default: 
        style = TimeZoneFormat.Style.GENERIC_LOCATION;
      }
      
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    

    case 31: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.LOCALIZED_GMT_SHORT : TimeZoneFormat.Style.LOCALIZED_GMT;
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    

    case 32: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      TimeZoneFormat.Style style;
      switch (count) {
      case 1: 
        style = TimeZoneFormat.Style.ISO_BASIC_SHORT;
        break;
      case 2: 
        style = TimeZoneFormat.Style.ISO_BASIC_FIXED;
        break;
      case 3: 
        style = TimeZoneFormat.Style.ISO_EXTENDED_FIXED;
        break;
      case 4: 
        style = TimeZoneFormat.Style.ISO_BASIC_FULL;
        break;
      default: 
        style = TimeZoneFormat.Style.ISO_EXTENDED_FULL;
      }
      
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    

    case 33: 
      Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
      TimeZoneFormat.Style style;
      switch (count) {
      case 1: 
        style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_SHORT;
        break;
      case 2: 
        style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_FIXED;
        break;
      case 3: 
        style = TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FIXED;
        break;
      case 4: 
        style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL;
        break;
      default: 
        style = TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FULL;
      }
      
      TimeZone tz = tzFormat().parse(style, text, pos, tzTimeType);
      if (tz != null) {
        tztype = ((TimeZoneFormat.TimeType)value);
        cal.setTimeZone(tz);
        return pos.getIndex();
      }
      return -start;
    
    case 27: 
      if (count <= 2)
      {


        cal.set(2, (value - 1) * 3);
        return pos.getIndex();
      }
      


      int newStart = matchQuarterString(text, start, 2, formatData.quarters, cal);
      
      if (newStart > 0) {
        return newStart;
      }
      return matchQuarterString(text, start, 2, formatData.shortQuarters, cal);
    



    case 28: 
      if (count <= 2)
      {


        cal.set(2, (value - 1) * 3);
        return pos.getIndex();
      }
      


      int newStart = matchQuarterString(text, start, 2, formatData.standaloneQuarters, cal);
      
      if (newStart > 0) {
        return newStart;
      }
      return matchQuarterString(text, start, 2, formatData.standaloneShortQuarters, cal);
    }
    
    

















    if (obeyCount) {
      if (start + count > text.length()) return -start;
      number = parseInt(text, count, pos, allowNegative, currentNumberFormat);
    } else {
      number = parseInt(text, pos, allowNegative, currentNumberFormat);
    }
    if (number != null) {
      cal.set(field, number.intValue());
      return pos.getIndex();
    }
    return -start;
  }
  







  private Number parseInt(String text, ParsePosition pos, boolean allowNegative, NumberFormat fmt)
  {
    return parseInt(text, -1, pos, allowNegative, fmt);
  }
  







  private Number parseInt(String text, int maxDigits, ParsePosition pos, boolean allowNegative, NumberFormat fmt)
  {
    int oldPos = pos.getIndex();
    Number number; Number number; if (allowNegative) {
      number = fmt.parse(text, pos);

    }
    else if ((fmt instanceof DecimalFormat)) {
      String oldPrefix = ((DecimalFormat)fmt).getNegativePrefix();
      ((DecimalFormat)fmt).setNegativePrefix("꬀");
      Number number = fmt.parse(text, pos);
      ((DecimalFormat)fmt).setNegativePrefix(oldPrefix);
    } else {
      boolean dateNumberFormat = fmt instanceof DateNumberFormat;
      if (dateNumberFormat) {
        ((DateNumberFormat)fmt).setParsePositiveOnly(true);
      }
      number = fmt.parse(text, pos);
      if (dateNumberFormat) {
        ((DateNumberFormat)fmt).setParsePositiveOnly(false);
      }
    }
    
    if (maxDigits > 0)
    {

      int nDigits = pos.getIndex() - oldPos;
      if (nDigits > maxDigits) {
        double val = number.doubleValue();
        nDigits -= maxDigits;
        while (nDigits > 0) {
          val /= 10.0D;
          nDigits--;
        }
        pos.setIndex(oldPos + maxDigits);
        number = Integer.valueOf((int)val);
      }
    }
    return number;
  }
  




  private String translatePattern(String pat, String from, String to)
  {
    StringBuilder result = new StringBuilder();
    boolean inQuote = false;
    for (int i = 0; i < pat.length(); i++) {
      char c = pat.charAt(i);
      if (inQuote) {
        if (c == '\'') {
          inQuote = false;
        }
      } else if (c == '\'') {
        inQuote = true;
      } else if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
        int ci = from.indexOf(c);
        if (ci != -1) {
          c = to.charAt(ci);
        }
      }
      


      result.append(c);
    }
    if (inQuote) {
      throw new IllegalArgumentException("Unfinished quote in pattern");
    }
    return result.toString();
  }
  



  public String toPattern()
  {
    return pattern;
  }
  



  public String toLocalizedPattern()
  {
    return translatePattern(pattern, "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx", formatData.localPatternChars);
  }
  






  public void applyPattern(String pat)
  {
    pattern = pat;
    setLocale(null, null);
    
    patternItems = null;
  }
  



  public void applyLocalizedPattern(String pat)
  {
    pattern = translatePattern(pat, formatData.localPatternChars, "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx");
    

    setLocale(null, null);
  }
  






  public DateFormatSymbols getDateFormatSymbols()
  {
    return (DateFormatSymbols)formatData.clone();
  }
  





  public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols)
  {
    formatData = ((DateFormatSymbols)newFormatSymbols.clone());
  }
  



  protected DateFormatSymbols getSymbols()
  {
    return formatData;
  }
  








  public TimeZoneFormat getTimeZoneFormat()
  {
    return tzFormat().freeze();
  }
  






  public void setTimeZoneFormat(TimeZoneFormat tzfmt)
  {
    if (tzfmt.isFrozen())
    {
      tzFormat = tzfmt;
    }
    else {
      tzFormat = tzfmt.cloneAsThawed().freeze();
    }
  }
  







  public void setContext(DisplayContext context)
  {
    if (context.type() == DisplayContext.Type.CAPITALIZATION) {
      capitalizationSetting = context;
    }
  }
  








  public DisplayContext getContext(DisplayContext.Type type)
  {
    return (type == DisplayContext.Type.CAPITALIZATION) && (capitalizationSetting != null) ? capitalizationSetting : DisplayContext.CAPITALIZATION_NONE;
  }
  




  public Object clone()
  {
    SimpleDateFormat other = (SimpleDateFormat)super.clone();
    formatData = ((DateFormatSymbols)formatData.clone());
    return other;
  }
  





  public int hashCode()
  {
    return pattern.hashCode();
  }
  





  public boolean equals(Object obj)
  {
    if (!super.equals(obj)) return false;
    SimpleDateFormat that = (SimpleDateFormat)obj;
    return (pattern.equals(pattern)) && (formatData.equals(formatData));
  }
  



  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    if (defaultCenturyStart == null)
    {

      initializeDefaultCenturyStart(defaultCenturyBase);
    }
    initializeTimeZoneFormat(false);
    stream.defaultWriteObject();
    stream.writeInt(capitalizationSetting.value());
  }
  



  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    int capitalizationSettingValue = serialVersionOnStream > 1 ? stream.readInt() : -1;
    

    if (serialVersionOnStream < 1)
    {
      defaultCenturyBase = System.currentTimeMillis();

    }
    else
    {
      parseAmbiguousDatesAsAfter(defaultCenturyStart);
    }
    serialVersionOnStream = 2;
    locale = getLocale(ULocale.VALID_LOCALE);
    if (locale == null)
    {


      locale = ULocale.getDefault(ULocale.Category.FORMAT);
    }
    
    initLocalZeroPaddingNumberFormat();
    
    capitalizationSetting = DisplayContext.CAPITALIZATION_NONE;
    if (capitalizationSettingValue >= 0) {
      for (DisplayContext context : DisplayContext.values()) {
        if (context.value() == capitalizationSettingValue) {
          capitalizationSetting = context;
          break;
        }
      }
    }
  }
  








  public AttributedCharacterIterator formatToCharacterIterator(Object obj)
  {
    Calendar cal = calendar;
    if ((obj instanceof Calendar)) {
      cal = (Calendar)obj;
    } else if ((obj instanceof Date)) {
      calendar.setTime((Date)obj);
    } else if ((obj instanceof Number)) {
      calendar.setTimeInMillis(((Number)obj).longValue());
    } else {
      throw new IllegalArgumentException("Cannot format given Object as a Date");
    }
    StringBuffer toAppendTo = new StringBuffer();
    FieldPosition pos = new FieldPosition(0);
    List<FieldPosition> attributes = new ArrayList();
    format(cal, capitalizationSetting, toAppendTo, pos, attributes);
    
    AttributedString as = new AttributedString(toAppendTo.toString());
    

    for (int i = 0; i < attributes.size(); i++) {
      FieldPosition fp = (FieldPosition)attributes.get(i);
      Format.Field attribute = fp.getFieldAttribute();
      as.addAttribute(attribute, attribute, fp.getBeginIndex(), fp.getEndIndex());
    }
    
    return as.getIterator();
  }
  






  ULocale getLocale()
  {
    return locale;
  }
  











  boolean isFieldUnitIgnored(int field)
  {
    return isFieldUnitIgnored(pattern, field);
  }
  










  static boolean isFieldUnitIgnored(String pattern, int field)
  {
    int fieldLevel = CALENDAR_FIELD_TO_LEVEL[field];
    

    boolean inQuote = false;
    char prevCh = '\000';
    int count = 0;
    
    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      if ((ch != prevCh) && (count > 0)) {
        int level = PATTERN_CHAR_TO_LEVEL[(prevCh - '@')];
        if (fieldLevel <= level) {
          return false;
        }
        count = 0;
      }
      if (ch == '\'') {
        if ((i + 1 < pattern.length()) && (pattern.charAt(i + 1) == '\'')) {
          i++;
        } else {
          inQuote = !inQuote;
        }
      } else if ((!inQuote) && (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))))
      {
        prevCh = ch;
        count++;
      }
    }
    if (count > 0)
    {
      int level = PATTERN_CHAR_TO_LEVEL[(prevCh - '@')];
      if (fieldLevel <= level) {
        return false;
      }
    }
    return true;
  }
  




















  /**
   * @deprecated
   */
  public final StringBuffer intervalFormatByAlgorithm(Calendar fromCalendar, Calendar toCalendar, StringBuffer appendTo, FieldPosition pos)
    throws IllegalArgumentException
  {
    if (!fromCalendar.isEquivalentTo(toCalendar)) {
      throw new IllegalArgumentException("can not format on two different calendars");
    }
    
    Object[] items = getPatternItems();
    int diffBegin = -1;
    int diffEnd = -1;
    

    try
    {
      for (int i = 0; i < items.length; i++) {
        if (diffCalFieldValue(fromCalendar, toCalendar, items, i)) {
          diffBegin = i;
          break;
        }
      }
      
      if (diffBegin == -1)
      {
        return format(fromCalendar, appendTo, pos);
      }
      

      for (int i = items.length - 1; i >= diffBegin; i--) {
        if (diffCalFieldValue(fromCalendar, toCalendar, items, i)) {
          diffEnd = i;
          break;
        }
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.toString());
    }
    

    if ((diffBegin == 0) && (diffEnd == items.length - 1)) {
      format(fromCalendar, appendTo, pos);
      appendTo.append(" – ");
      format(toCalendar, appendTo, pos);
      return appendTo;
    }
    


    int highestLevel = 1000;
    for (int i = diffBegin; i <= diffEnd; i++) {
      if (!(items[i] instanceof String))
      {

        PatternItem item = (PatternItem)items[i];
        char ch = type;
        int patternCharIndex = -1;
        if (('A' <= ch) && (ch <= 'z')) {
          patternCharIndex = PATTERN_CHAR_TO_LEVEL[(ch - '@')];
        }
        
        if (patternCharIndex == -1) {
          throw new IllegalArgumentException("Illegal pattern character '" + ch + "' in \"" + pattern + '"');
        }
        


        if (patternCharIndex < highestLevel) {
          highestLevel = patternCharIndex;
        }
      }
    }
    

    try
    {
      for (int i = 0; i < diffBegin; i++) {
        if (lowerLevel(items, i, highestLevel)) {
          diffBegin = i;
          break;
        }
      }
      

      for (int i = items.length - 1; i > diffEnd; i--) {
        if (lowerLevel(items, i, highestLevel)) {
          diffEnd = i;
          break;
        }
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.toString());
    }
    


    if ((diffBegin == 0) && (diffEnd == items.length - 1)) {
      format(fromCalendar, appendTo, pos);
      appendTo.append(" – ");
      format(toCalendar, appendTo, pos);
      return appendTo;
    }
    



    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    

    for (int i = 0; i <= diffEnd; i++) {
      if ((items[i] instanceof String)) {
        appendTo.append((String)items[i]);
      } else {
        PatternItem item = (PatternItem)items[i];
        if (useFastFormat) {
          subFormat(appendTo, type, length, appendTo.length(), i, capitalizationSetting, pos, fromCalendar);
        }
        else {
          appendTo.append(subFormat(type, length, appendTo.length(), i, capitalizationSetting, pos, fromCalendar));
        }
      }
    }
    

    appendTo.append(" – ");
    

    for (int i = diffBegin; i < items.length; i++) {
      if ((items[i] instanceof String)) {
        appendTo.append((String)items[i]);
      } else {
        PatternItem item = (PatternItem)items[i];
        if (useFastFormat) {
          subFormat(appendTo, type, length, appendTo.length(), i, capitalizationSetting, pos, toCalendar);
        }
        else {
          appendTo.append(subFormat(type, length, appendTo.length(), i, capitalizationSetting, pos, toCalendar));
        }
      }
    }
    
    return appendTo;
  }
  

















  private boolean diffCalFieldValue(Calendar fromCalendar, Calendar toCalendar, Object[] items, int i)
    throws IllegalArgumentException
  {
    if ((items[i] instanceof String)) {
      return false;
    }
    PatternItem item = (PatternItem)items[i];
    char ch = type;
    int patternCharIndex = -1;
    if (('A' <= ch) && (ch <= 'z')) {
      patternCharIndex = PATTERN_CHAR_TO_INDEX[(ch - '@')];
    }
    
    if (patternCharIndex == -1) {
      throw new IllegalArgumentException("Illegal pattern character '" + ch + "' in \"" + pattern + '"');
    }
    


    int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
    int value = fromCalendar.get(field);
    int value_2 = toCalendar.get(field);
    if (value != value_2) {
      return true;
    }
    return false;
  }
  














  private boolean lowerLevel(Object[] items, int i, int level)
    throws IllegalArgumentException
  {
    if ((items[i] instanceof String)) {
      return false;
    }
    PatternItem item = (PatternItem)items[i];
    char ch = type;
    int patternCharIndex = -1;
    if (('A' <= ch) && (ch <= 'z')) {
      patternCharIndex = PATTERN_CHAR_TO_LEVEL[(ch - '@')];
    }
    
    if (patternCharIndex == -1) {
      throw new IllegalArgumentException("Illegal pattern character '" + ch + "' in \"" + pattern + '"');
    }
    


    if (patternCharIndex >= level) {
      return true;
    }
    return false;
  }
  


  /**
   * @deprecated
   */
  protected NumberFormat getNumberFormat(char ch)
  {
    Character ovrField = Character.valueOf(ch);
    if ((overrideMap != null) && (overrideMap.containsKey(ovrField))) {
      String nsName = ((String)overrideMap.get(ovrField)).toString();
      NumberFormat nf = (NumberFormat)numberFormatters.get(nsName);
      return nf;
    }
    return numberFormat;
  }
  

  private void initNumberFormatters(ULocale loc)
  {
    numberFormatters = new HashMap();
    overrideMap = new HashMap();
    processOverrideString(loc, override);
  }
  

  private void processOverrideString(ULocale loc, String str)
  {
    if ((str == null) || (str.length() == 0)) {
      return;
    }
    int start = 0;
    


    boolean moreToProcess = true;
    

    while (moreToProcess) {
      int delimiterPosition = str.indexOf(";", start);
      int end; int end; if (delimiterPosition == -1) {
        moreToProcess = false;
        end = str.length();
      } else {
        end = delimiterPosition;
      }
      
      String currentString = str.substring(start, end);
      int equalSignPosition = currentString.indexOf("=");
      boolean fullOverride; String nsName; boolean fullOverride; if (equalSignPosition == -1) {
        String nsName = currentString;
        fullOverride = true;
      } else {
        nsName = currentString.substring(equalSignPosition + 1);
        Character ovrField = Character.valueOf(currentString.charAt(0));
        overrideMap.put(ovrField, nsName);
        fullOverride = false;
      }
      
      ULocale ovrLoc = new ULocale(loc.getBaseName() + "@numbers=" + nsName);
      NumberFormat nf = NumberFormat.createInstance(ovrLoc, 0);
      nf.setGroupingUsed(false);
      
      if (fullOverride) {
        setNumberFormat(nf);
      }
      else
      {
        useLocalZeroPaddingNumberFormat = false;
      }
      
      if (!numberFormatters.containsKey(nsName)) {
        numberFormatters.put(nsName, nf);
      }
      
      start = delimiterPosition + 1;
    }
  }
}
