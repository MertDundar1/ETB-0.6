package com.ibm.icu.util;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.impl.CalendarUtil;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DateFormatSymbols;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.SimpleDateFormat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































public abstract class Calendar
  implements Serializable, Cloneable, Comparable<Calendar>
{
  public static final int ERA = 0;
  public static final int YEAR = 1;
  public static final int MONTH = 2;
  public static final int WEEK_OF_YEAR = 3;
  public static final int WEEK_OF_MONTH = 4;
  public static final int DATE = 5;
  public static final int DAY_OF_MONTH = 5;
  public static final int DAY_OF_YEAR = 6;
  public static final int DAY_OF_WEEK = 7;
  public static final int DAY_OF_WEEK_IN_MONTH = 8;
  public static final int AM_PM = 9;
  public static final int HOUR = 10;
  public static final int HOUR_OF_DAY = 11;
  public static final int MINUTE = 12;
  public static final int SECOND = 13;
  public static final int MILLISECOND = 14;
  public static final int ZONE_OFFSET = 15;
  public static final int DST_OFFSET = 16;
  public static final int YEAR_WOY = 17;
  public static final int DOW_LOCAL = 18;
  public static final int EXTENDED_YEAR = 19;
  public static final int JULIAN_DAY = 20;
  public static final int MILLISECONDS_IN_DAY = 21;
  public static final int IS_LEAP_MONTH = 22;
  protected static final int BASE_FIELD_COUNT = 23;
  protected static final int MAX_FIELD_COUNT = 32;
  public static final int SUNDAY = 1;
  public static final int MONDAY = 2;
  public static final int TUESDAY = 3;
  public static final int WEDNESDAY = 4;
  public static final int THURSDAY = 5;
  public static final int FRIDAY = 6;
  public static final int SATURDAY = 7;
  public static final int JANUARY = 0;
  public static final int FEBRUARY = 1;
  public static final int MARCH = 2;
  public static final int APRIL = 3;
  public static final int MAY = 4;
  public static final int JUNE = 5;
  public static final int JULY = 6;
  public static final int AUGUST = 7;
  public static final int SEPTEMBER = 8;
  public static final int OCTOBER = 9;
  public static final int NOVEMBER = 10;
  public static final int DECEMBER = 11;
  public static final int UNDECIMBER = 12;
  public static final int AM = 0;
  public static final int PM = 1;
  public static final int WEEKDAY = 0;
  public static final int WEEKEND = 1;
  public static final int WEEKEND_ONSET = 2;
  public static final int WEEKEND_CEASE = 3;
  public static final int WALLTIME_LAST = 0;
  public static final int WALLTIME_FIRST = 1;
  public static final int WALLTIME_NEXT_VALID = 2;
  protected static final int ONE_SECOND = 1000;
  protected static final int ONE_MINUTE = 60000;
  protected static final int ONE_HOUR = 3600000;
  protected static final long ONE_DAY = 86400000L;
  protected static final long ONE_WEEK = 604800000L;
  protected static final int JAN_1_1_JULIAN_DAY = 1721426;
  protected static final int EPOCH_JULIAN_DAY = 2440588;
  protected static final int MIN_JULIAN = -2130706432;
  protected static final long MIN_MILLIS = -184303902528000000L;
  protected static final Date MIN_DATE = new Date(-184303902528000000L);
  






  protected static final int MAX_JULIAN = 2130706432;
  





  protected static final long MAX_MILLIS = 183882168921600000L;
  





  protected static final Date MAX_DATE = new Date(183882168921600000L);
  








  private transient int[] fields;
  








  private transient int[] stamp;
  








  private long time;
  








  private transient boolean isTimeSet;
  







  private transient boolean areFieldsSet;
  







  private transient boolean areAllFieldsSet;
  







  private transient boolean areFieldsVirtuallySet;
  







  private boolean lenient = true;
  






  private TimeZone zone;
  






  private int firstDayOfWeek;
  






  private int minimalDaysInFirstWeek;
  






  private int weekendOnset;
  






  private int weekendOnsetMillis;
  





  private int weekendCease;
  





  private int weekendCeaseMillis;
  





  private int repeatedWallTime = 0;
  



  private int skippedWallTime = 0;
  




  private static ICUCache<ULocale, WeekData> cachedLocaleData = new SimpleCache();
  







  protected static final int UNSET = 0;
  







  protected static final int INTERNALLY_SET = 1;
  






  protected static final int MINIMUM_USER_STAMP = 2;
  






  private transient int nextStamp = 2;
  










  private static final long serialVersionUID = 6222646104888790989L;
  










  private transient int internalSetMask;
  









  private transient int gregorianYear;
  









  private transient int gregorianMonth;
  









  private transient int gregorianDayOfYear;
  









  private transient int gregorianDayOfMonth;
  










  protected Calendar()
  {
    this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
  }
  






  protected Calendar(TimeZone zone, Locale aLocale)
  {
    this(zone, ULocale.forLocale(aLocale));
  }
  






  protected Calendar(TimeZone zone, ULocale locale)
  {
    this.zone = zone;
    setWeekData(locale);
    initInternal();
  }
  



  private void recalculateStamp()
  {
    nextStamp = 1;
    
    for (int j = 0; j < stamp.length; j++) {
      int currentValue = STAMP_MAX;
      int index = -1;
      
      for (int i = 0; i < stamp.length; i++) {
        if ((stamp[i] > nextStamp) && (stamp[i] < currentValue)) {
          currentValue = stamp[i];
          index = i;
        }
      }
      
      if (index < 0) break;
      stamp[index] = (++nextStamp);
    }
    


    nextStamp += 1;
  }
  


  private void initInternal()
  {
    fields = handleCreateFields();
    

    if ((fields == null) || (fields.length < 23) || (fields.length > 32))
    {
      throw new IllegalStateException("Invalid fields[]");
    }
    
    stamp = new int[fields.length];
    int mask = 4718695;
    





    for (int i = 23; i < fields.length; i++) {
      mask |= 1 << i;
    }
    internalSetMask = mask;
  }
  





  public static synchronized Calendar getInstance()
  {
    return getInstanceInternal(null, null);
  }
  






  public static synchronized Calendar getInstance(TimeZone zone)
  {
    return getInstanceInternal(zone, null);
  }
  






  public static synchronized Calendar getInstance(Locale aLocale)
  {
    return getInstanceInternal(null, ULocale.forLocale(aLocale));
  }
  






  public static synchronized Calendar getInstance(ULocale locale)
  {
    return getInstanceInternal(null, locale);
  }
  







  public static synchronized Calendar getInstance(TimeZone zone, Locale aLocale)
  {
    return getInstanceInternal(zone, ULocale.forLocale(aLocale));
  }
  







  public static synchronized Calendar getInstance(TimeZone zone, ULocale locale)
  {
    return getInstanceInternal(zone, locale);
  }
  



  private static Calendar getInstanceInternal(TimeZone tz, ULocale locale)
  {
    if (locale == null) {
      locale = ULocale.getDefault(ULocale.Category.FORMAT);
    }
    if (tz == null) {
      tz = TimeZone.getDefault();
    }
    Calendar cal = getShim().createInstance(locale);
    cal.setTimeZone(tz);
    cal.setTimeInMillis(System.currentTimeMillis());
    return cal;
  }
  
  private static int STAMP_MAX = 10000;
  
  private static final String[] calTypes = { "gregorian", "japanese", "buddhist", "roc", "persian", "islamic-civil", "islamic", "hebrew", "chinese", "indian", "coptic", "ethiopic", "ethiopic-amete-alem", "iso8601", "dangi" };
  
  private static final int CALTYPE_GREGORIAN = 0;
  
  private static final int CALTYPE_JAPANESE = 1;
  
  private static final int CALTYPE_BUDDHIST = 2;
  
  private static final int CALTYPE_ROC = 3;
  
  private static final int CALTYPE_PERSIAN = 4;
  
  private static final int CALTYPE_ISLAMIC_CIVIL = 5;
  
  private static final int CALTYPE_ISLAMIC = 6;
  
  private static final int CALTYPE_HEBREW = 7;
  
  private static final int CALTYPE_CHINESE = 8;
  
  private static final int CALTYPE_INDIAN = 9;
  
  private static final int CALTYPE_COPTIC = 10;
  
  private static final int CALTYPE_ETHIOPIC = 11;
  
  private static final int CALTYPE_ETHIOPIC_AMETE_ALEM = 12;
  
  private static final int CALTYPE_ISO8601 = 13;
  
  private static final int CALTYPE_DANGI = 14;
  
  private static final int CALTYPE_UNKNOWN = -1;
  private static CalendarShim shim;
  
  private static int getCalendarTypeForLocale(ULocale l)
  {
    String s = CalendarUtil.getCalendarType(l);
    if (s != null) {
      s = s.toLowerCase(Locale.ENGLISH);
      for (int i = 0; i < calTypes.length; i++) {
        if (s.equals(calTypes[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  





  public static Locale[] getAvailableLocales()
  {
    if (shim == null) {
      return ICUResourceBundle.getAvailableLocales();
    }
    return getShim().getAvailableLocales();
  }
  






  public static ULocale[] getAvailableULocales()
  {
    if (shim == null) {
      return ICUResourceBundle.getAvailableULocales();
    }
    return getShim().getAvailableULocales();
  }
  







  static abstract class CalendarFactory
  {
    public boolean visible()
    {
      return true;
    }
    
    public abstract Set<String> getSupportedLocaleNames();
    
    public Calendar createCalendar(ULocale loc) {
      return null;
    }
    






    protected CalendarFactory() {}
  }
  





  private static CalendarShim getShim()
  {
    if (shim == null) {
      try {
        Class<?> cls = Class.forName("com.ibm.icu.util.CalendarServiceShim");
        shim = (CalendarShim)cls.newInstance();
      }
      catch (MissingResourceException e) {
        throw e;
      }
      catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return shim;
  }
  
  static Calendar createInstance(ULocale locale) {
    Calendar cal = null;
    TimeZone zone = TimeZone.getDefault();
    int calType = getCalendarTypeForLocale(locale);
    if (calType == -1)
    {
      calType = 0;
    }
    
    switch (calType) {
    case 0: 
      cal = new GregorianCalendar(zone, locale);
      break;
    case 1: 
      cal = new JapaneseCalendar(zone, locale);
      break;
    case 2: 
      cal = new BuddhistCalendar(zone, locale);
      break;
    case 3: 
      cal = new TaiwanCalendar(zone, locale);
      break;
    case 4: 
      cal = new PersianCalendar(zone, locale);
      break;
    case 5: 
      cal = new IslamicCalendar(zone, locale);
      break;
    case 6: 
      cal = new IslamicCalendar(zone, locale);
      ((IslamicCalendar)cal).setCivil(false);
      break;
    case 7: 
      cal = new HebrewCalendar(zone, locale);
      break;
    case 8: 
      cal = new ChineseCalendar(zone, locale);
      break;
    case 9: 
      cal = new IndianCalendar(zone, locale);
      break;
    case 10: 
      cal = new CopticCalendar(zone, locale);
      break;
    case 11: 
      cal = new EthiopicCalendar(zone, locale);
      break;
    case 12: 
      cal = new EthiopicCalendar(zone, locale);
      ((EthiopicCalendar)cal).setAmeteAlemEra(true);
      break;
    case 14: 
      cal = new DangiCalendar(zone, locale);
      break;
    
    case 13: 
      cal = new GregorianCalendar(zone, locale);
      cal.setFirstDayOfWeek(2);
      cal.setMinimalDaysInFirstWeek(4);
      break;
    

    default: 
      throw new IllegalArgumentException("Unknown calendar type");
    }
    
    return cal;
  }
  






  static Object registerFactory(CalendarFactory factory)
  {
    if (factory == null) {
      throw new IllegalArgumentException("factory must not be null");
    }
    return getShim().registerFactory(factory);
  }
  




  static boolean unregister(Object registryKey)
  {
    if (registryKey == null) {
      throw new IllegalArgumentException("registryKey must not be null");
    }
    
    if (shim == null) {
      return false;
    }
    
    return shim.unregister(registryKey);
  }
  































































  public static final String[] getKeywordValuesForLocale(String key, ULocale locale, boolean commonlyUsed)
  {
    String prefRegion = locale.getCountry();
    if (prefRegion.length() == 0) {
      ULocale loc = ULocale.addLikelySubtags(locale);
      prefRegion = loc.getCountry();
    }
    

    ArrayList<String> values = new ArrayList();
    
    UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
    


    UResourceBundle calPref = rb.get("calendarPreferenceData");
    UResourceBundle order = null;
    try {
      order = calPref.get(prefRegion);
    }
    catch (MissingResourceException mre) {
      order = calPref.get("001");
    }
    
    String[] caltypes = order.getStringArray();
    if (commonlyUsed)
    {
      return caltypes;
    }
    

    for (int i = 0; i < caltypes.length; i++) {
      values.add(caltypes[i]);
    }
    
    for (int i = 0; i < calTypes.length; i++) {
      if (!values.contains(calTypes[i])) {
        values.add(calTypes[i]);
      }
    }
    return (String[])values.toArray(new String[values.size()]);
  }
  




  public final Date getTime()
  {
    return new Date(getTimeInMillis());
  }
  








  public final void setTime(Date date)
  {
    setTimeInMillis(date.getTime());
  }
  




  public long getTimeInMillis()
  {
    if (!isTimeSet) updateTime();
    return time;
  }
  







  public void setTimeInMillis(long millis)
  {
    if (millis > 183882168921600000L) {
      if (isLenient()) {
        millis = 183882168921600000L;
      } else {
        throw new IllegalArgumentException("millis value greater than upper bounds for a Calendar : " + millis);
      }
    } else if (millis < -184303902528000000L) {
      if (isLenient()) {
        millis = -184303902528000000L;
      } else {
        throw new IllegalArgumentException("millis value less than lower bounds for a Calendar : " + millis);
      }
    }
    time = millis;
    areFieldsSet = (this.areAllFieldsSet = 0);
    isTimeSet = (this.areFieldsVirtuallySet = 1);
    
    for (int i = 0; i < fields.length; tmp145_144++) {
      int tmp145_144 = 0;stamp[i] = tmp145_144;fields[i] = tmp145_144;
    }
  }
  







  public final int get(int field)
  {
    complete();
    return fields[field];
  }
  







  protected final int internalGet(int field)
  {
    return fields[field];
  }
  









  protected final int internalGet(int field, int defaultValue)
  {
    return stamp[field] > 0 ? fields[field] : defaultValue;
  }
  






  public final void set(int field, int value)
  {
    if (areFieldsVirtuallySet) {
      computeFields();
    }
    fields[field] = value;
    
    if (nextStamp == STAMP_MAX) {
      recalculateStamp();
    }
    stamp[field] = (nextStamp++);
    isTimeSet = (this.areFieldsSet = this.areFieldsVirtuallySet = 0);
  }
  










  public final void set(int year, int month, int date)
  {
    set(1, year);
    set(2, month);
    set(5, date);
  }
  












  public final void set(int year, int month, int date, int hour, int minute)
  {
    set(1, year);
    set(2, month);
    set(5, date);
    set(11, hour);
    set(12, minute);
  }
  














  public final void set(int year, int month, int date, int hour, int minute, int second)
  {
    set(1, year);
    set(2, month);
    set(5, date);
    set(11, hour);
    set(12, minute);
    set(13, second);
  }
  




  public final void clear()
  {
    for (int i = 0; i < fields.length; i++) {
      int tmp22_21 = 0;stamp[i] = tmp22_21;fields[i] = tmp22_21;
    }
    isTimeSet = (this.areFieldsSet = this.areAllFieldsSet = this.areFieldsVirtuallySet = 0);
  }
  





  public final void clear(int field)
  {
    if (areFieldsVirtuallySet) {
      computeFields();
    }
    fields[field] = 0;
    stamp[field] = 0;
    isTimeSet = (this.areFieldsSet = this.areAllFieldsSet = this.areFieldsVirtuallySet = 0);
  }
  





  public final boolean isSet(int field)
  {
    return (areFieldsVirtuallySet) || (stamp[field] != 0);
  }
  




  protected void complete()
  {
    if (!isTimeSet) updateTime();
    if (!areFieldsSet) {
      computeFields();
      areFieldsSet = true;
      areAllFieldsSet = true;
    }
  }
  









  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    
    Calendar that = (Calendar)obj;
    
    return (isEquivalentTo(that)) && (getTimeInMillis() == that.getTime().getTime());
  }
  










  public boolean isEquivalentTo(Calendar other)
  {
    return (getClass() == other.getClass()) && (isLenient() == other.isLenient()) && (getFirstDayOfWeek() == other.getFirstDayOfWeek()) && (getMinimalDaysInFirstWeek() == other.getMinimalDaysInFirstWeek()) && (getTimeZone().equals(other.getTimeZone())) && (getRepeatedWallTimeOption() == other.getRepeatedWallTimeOption()) && (getSkippedWallTimeOption() == other.getSkippedWallTimeOption());
  }
  















  public int hashCode()
  {
    return (lenient ? 1 : 0) | firstDayOfWeek << 1 | minimalDaysInFirstWeek << 4 | repeatedWallTime << 7 | skippedWallTime << 9 | zone.hashCode() << 11;
  }
  





  private long compare(Object that)
  {
    long thatMs;
    



    if ((that instanceof Calendar)) {
      thatMs = ((Calendar)that).getTimeInMillis(); } else { long thatMs;
      if ((that instanceof Date)) {
        thatMs = ((Date)that).getTime();
      } else
        throw new IllegalArgumentException(that + "is not a Calendar or Date"); }
    long thatMs;
    return getTimeInMillis() - thatMs;
  }
  







  public boolean before(Object when)
  {
    return compare(when) < 0L;
  }
  







  public boolean after(Object when)
  {
    return compare(when) > 0L;
  }
  












  public int getActualMaximum(int field)
  {
    int result;
    










    switch (field)
    {
    case 5: 
      Calendar cal = (Calendar)clone();
      cal.setLenient(true);
      cal.prepareGetActual(field, false);
      result = handleGetMonthLength(cal.get(19), cal.get(2));
      
      break;
    

    case 6: 
      Calendar cal = (Calendar)clone();
      cal.setLenient(true);
      cal.prepareGetActual(field, false);
      result = handleGetYearLength(cal.get(19));
      
      break;
    

    case 0: 
    case 7: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 18: 
    case 20: 
    case 21: 
      result = getMaximum(field);
      break;
    case 1: case 2: case 3: 
    case 4: case 8: case 17: 
    case 19: default: 
      result = getActualHelper(field, getLeastMaximum(field), getMaximum(field));
    }
    
    return result;
  }
  











  public int getActualMinimum(int field)
  {
    int result;
    










    switch (field)
    {
    case 7: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 18: 
    case 20: 
    case 21: 
      result = getMinimum(field);
      break;
    case 8: case 17: 
    case 19: 
    default: 
      result = getActualHelper(field, getGreatestMinimum(field), getMinimum(field));
    }
    
    return result;
  }
  




















  protected void prepareGetActual(int field, boolean isMinimum)
  {
    set(21, 0);
    
    switch (field) {
    case 1: 
    case 19: 
      set(6, getGreatestMinimum(6));
      break;
    
    case 17: 
      set(3, getGreatestMinimum(3));
      break;
    
    case 2: 
      set(5, getGreatestMinimum(5));
      break;
    


    case 8: 
      set(5, 1);
      set(7, get(7));
      break;
    





    case 3: 
    case 4: 
      int dow = firstDayOfWeek;
      if (isMinimum) {
        dow = (dow + 6) % 7;
        if (dow < 1) {
          dow += 7;
        }
      }
      set(7, dow);
    }
    
    


    set(field, getGreatestMinimum(field));
  }
  
  private int getActualHelper(int field, int startValue, int endValue)
  {
    if (startValue == endValue)
    {
      return startValue;
    }
    
    int delta = endValue > startValue ? 1 : -1;
    


    Calendar work = (Calendar)clone();
    


    work.complete();
    
    work.setLenient(true);
    work.prepareGetActual(field, delta < 0);
    




    work.set(field, startValue);
    




    if ((work.get(field) != startValue) && (field != 4) && (delta > 0))
    {
      return startValue;
    }
    int result = startValue;
    do {
      startValue += delta;
      work.add(field, delta);
      if (work.get(field) != startValue) {
        break;
      }
      result = startValue;
    } while (startValue != endValue);
    
    return result;
  }
  


























































  public final void roll(int field, boolean up)
  {
    roll(field, up ? 1 : -1);
  }
  








  static abstract class CalendarShim
  {
    CalendarShim() {}
    








    abstract Locale[] getAvailableLocales();
    








    abstract ULocale[] getAvailableULocales();
    








    abstract Object registerFactory(Calendar.CalendarFactory paramCalendarFactory);
    







    abstract boolean unregister(Object paramObject);
    







    abstract Calendar createInstance(ULocale paramULocale);
  }
  







  public void roll(int field, int amount)
  {
    if (amount == 0) {
      return;
    }
    
    complete();
    
    switch (field)
    {



    case 0: 
    case 5: 
    case 9: 
    case 12: 
    case 13: 
    case 14: 
    case 21: 
      int min = getActualMinimum(field);
      int max = getActualMaximum(field);
      int gap = max - min + 1;
      
      int value = internalGet(field) + amount;
      value = (value - min) % gap;
      if (value < 0) {
        value += gap;
      }
      value += min;
      
      set(field, value);
      return;
    










    case 10: 
    case 11: 
      long start = getTimeInMillis();
      int oldHour = internalGet(field);
      int max = getMaximum(field);
      int newHour = (oldHour + amount) % (max + 1);
      if (newHour < 0) {
        newHour += max + 1;
      }
      setTimeInMillis(start + 3600000L * (newHour - oldHour));
      return;
    






    case 2: 
      int max = getActualMaximum(2);
      int mon = (internalGet(2) + amount) % (max + 1);
      
      if (mon < 0) {
        mon += max + 1;
      }
      set(2, mon);
      



      pinField(5);
      return;
    





    case 1: 
    case 17: 
      boolean era0WithYearsThatGoBackwards = false;
      int era = get(0);
      if (era == 0) {
        String calType = getType();
        if ((calType.equals("gregorian")) || (calType.equals("roc")) || (calType.equals("coptic"))) {
          amount = -amount;
          era0WithYearsThatGoBackwards = true;
        }
      }
      int newYear = internalGet(field) + amount;
      if ((era > 0) || (newYear >= 1)) {
        int maxYear = getActualMaximum(field);
        if (maxYear < 32768)
        {
          if (newYear < 1) {
            newYear = maxYear - -newYear % maxYear;
          } else if (newYear > maxYear) {
            newYear = (newYear - 1) % maxYear + 1;
          }
        }
        else if (newYear < 1) {
          newYear = 1;
        }
        

      }
      else if (era0WithYearsThatGoBackwards) {
        newYear = 1;
      }
      set(field, newYear);
      pinField(2);
      pinField(5);
      return;
    

    case 19: 
      set(field, internalGet(field) + amount);
      pinField(2);
      pinField(5);
      return;
    






























    case 4: 
      int dow = internalGet(7) - getFirstDayOfWeek();
      if (dow < 0) { dow += 7;
      }
      

      int fdm = (dow - internalGet(5) + 1) % 7;
      if (fdm < 0) { fdm += 7;
      }
      
      int start;
      
      int start;
      
      if (7 - fdm < getMinimalDaysInFirstWeek()) {
        start = 8 - fdm;
      } else {
        start = 1 - fdm;
      }
      

      int monthLen = getActualMaximum(5);
      int ldm = (monthLen - internalGet(5) + dow) % 7;
      





      int limit = monthLen + 7 - ldm;
      

      int gap = limit - start;
      int day_of_month = (internalGet(5) + amount * 7 - start) % gap;
      
      if (day_of_month < 0) day_of_month += gap;
      day_of_month += start;
      

      if (day_of_month < 1) day_of_month = 1;
      if (day_of_month > monthLen) { day_of_month = monthLen;
      }
      





      set(5, day_of_month);
      return;
    







    case 3: 
      int dow = internalGet(7) - getFirstDayOfWeek();
      if (dow < 0) { dow += 7;
      }
      

      int fdy = (dow - internalGet(6) + 1) % 7;
      if (fdy < 0) { fdy += 7;
      }
      
      int start;
      
      int start;
      
      if (7 - fdy < getMinimalDaysInFirstWeek()) {
        start = 8 - fdy;
      } else {
        start = 1 - fdy;
      }
      

      int yearLen = getActualMaximum(6);
      int ldy = (yearLen - internalGet(6) + dow) % 7;
      





      int limit = yearLen + 7 - ldy;
      

      int gap = limit - start;
      int day_of_year = (internalGet(6) + amount * 7 - start) % gap;
      
      if (day_of_year < 0) day_of_year += gap;
      day_of_year += start;
      

      if (day_of_year < 1) day_of_year = 1;
      if (day_of_year > yearLen) { day_of_year = yearLen;
      }
      



      set(6, day_of_year);
      clear(2);
      return;
    



    case 6: 
      long delta = amount * 86400000L;
      long min2 = time - (internalGet(6) - 1) * 86400000L;
      int yearLength = getActualMaximum(6);
      time = ((time + delta - min2) % (yearLength * 86400000L));
      if (time < 0L) time += yearLength * 86400000L;
      setTimeInMillis(time + min2);
      return;
    




    case 7: 
    case 18: 
      long delta = amount * 86400000L;
      

      int leadDays = internalGet(field);
      leadDays -= (field == 7 ? getFirstDayOfWeek() : 1);
      if (leadDays < 0) leadDays += 7;
      long min2 = time - leadDays * 86400000L;
      time = ((time + delta - min2) % 604800000L);
      if (time < 0L) time += 604800000L;
      setTimeInMillis(time + min2);
      return;
    




    case 8: 
      long delta = amount * 604800000L;
      

      int preWeeks = (internalGet(5) - 1) / 7;
      

      int postWeeks = (getActualMaximum(5) - internalGet(5)) / 7;
      

      long min2 = time - preWeeks * 604800000L;
      long gap2 = 604800000L * (preWeeks + postWeeks + 1);
      
      time = ((time + delta - min2) % gap2);
      if (time < 0L) time += gap2;
      setTimeInMillis(time + min2);
      return;
    
    case 20: 
      set(field, internalGet(field) + amount);
      return;
    }
    
    throw new IllegalArgumentException("Calendar.roll(" + fieldName(field) + ") not supported");
  }
  
























































  public void add(int field, int amount)
  {
    if (amount == 0) {
      return;
    }
    





















    long delta = amount;
    boolean keepHourInvariant = true;
    
    switch (field) {
    case 0: 
      set(field, get(field) + amount);
      pinField(0);
      return;
    








    case 1: 
    case 17: 
      int era = get(0);
      if (era == 0) {
        String calType = getType();
        if ((calType.equals("gregorian")) || (calType.equals("roc")) || (calType.equals("coptic"))) {
          amount = -amount;
        }
      }
    


    case 2: 
    case 19: 
      boolean oldLenient = isLenient();
      setLenient(true);
      set(field, get(field) + amount);
      pinField(5);
      if (!oldLenient) {
        complete();
        setLenient(oldLenient);
      }
      
      return;
    
    case 3: 
    case 4: 
    case 8: 
      delta *= 604800000L;
      break;
    
    case 9: 
      delta *= 43200000L;
      break;
    
    case 5: 
    case 6: 
    case 7: 
    case 18: 
    case 20: 
      delta *= 86400000L;
      break;
    
    case 10: 
    case 11: 
      delta *= 3600000L;
      keepHourInvariant = false;
      break;
    
    case 12: 
      delta *= 60000L;
      keepHourInvariant = false;
      break;
    
    case 13: 
      delta *= 1000L;
      keepHourInvariant = false;
      break;
    
    case 14: 
    case 21: 
      keepHourInvariant = false;
      break;
    }
    
    throw new IllegalArgumentException("Calendar.add(" + fieldName(field) + ") not supported");
    






    int prevOffset = 0;
    int hour = 0;
    if (keepHourInvariant) {
      prevOffset = get(16) + get(15);
      hour = internalGet(11);
    }
    
    setTimeInMillis(getTimeInMillis() + delta);
    
    if (keepHourInvariant) {
      int newOffset = get(16) + get(15);
      if (newOffset != prevOffset)
      {











        long adjAmount = (prevOffset - newOffset) % 86400000L;
        if (adjAmount != 0L) {
          long t = time;
          setTimeInMillis(time + adjAmount);
          if (get(11) != hour) {
            setTimeInMillis(t);
          }
        }
      }
    }
  }
  



  public String getDisplayName(Locale loc)
  {
    return getClass().getName();
  }
  



  public String getDisplayName(ULocale loc)
  {
    return getClass().getName();
  }
  


















  public int compareTo(Calendar that)
  {
    long v = getTimeInMillis() - that.getTimeInMillis();
    return v > 0L ? 1 : v < 0L ? -1 : 0;
  }
  









  public DateFormat getDateTimeFormat(int dateStyle, int timeStyle, Locale loc)
  {
    return formatHelper(this, ULocale.forLocale(loc), dateStyle, timeStyle);
  }
  





  public DateFormat getDateTimeFormat(int dateStyle, int timeStyle, ULocale loc)
  {
    return formatHelper(this, loc, dateStyle, timeStyle);
  }
  










  protected DateFormat handleGetDateFormat(String pattern, Locale locale)
  {
    return handleGetDateFormat(pattern, null, ULocale.forLocale(locale));
  }
  

















  protected DateFormat handleGetDateFormat(String pattern, String override, Locale locale)
  {
    return handleGetDateFormat(pattern, override, ULocale.forLocale(locale));
  }
  










  protected DateFormat handleGetDateFormat(String pattern, ULocale locale)
  {
    return handleGetDateFormat(pattern, null, locale);
  }
  











  protected DateFormat handleGetDateFormat(String pattern, String override, ULocale locale)
  {
    FormatConfiguration fmtConfig = new FormatConfiguration(null);
    pattern = pattern;
    override = override;
    formatData = new DateFormatSymbols(this, locale);
    loc = locale;
    cal = this;
    
    return SimpleDateFormat.getInstance(fmtConfig);
  }
  

  private static final ICUCache<String, PatternData> PATTERN_CACHE = new SimpleCache();
  

  private static final String[] DEFAULT_PATTERNS = { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm", "EEEE, yyyy MMMM dd", "yyyy MMMM d", "yyyy MMM d", "yy/MM/dd", "{1} {0}", "{1} {0}", "{1} {0}", "{1} {0}", "{1} {0}" };
  



  private static final char QUOTE = '\'';
  



  private static final int FIELD_DIFF_MAX_INT = Integer.MAX_VALUE;
  




  private static DateFormat formatHelper(Calendar cal, ULocale loc, int dateStyle, int timeStyle)
  {
    PatternData patternData = PatternData.make(cal, loc);
    String override = null;
    

    String pattern = null;
    if ((timeStyle >= 0) && (dateStyle >= 0)) {
      pattern = MessageFormat.format(patternData.getDateTimePattern(dateStyle), new Object[] { patterns[timeStyle], patterns[(dateStyle + 4)] });
      




      if (overrides != null) {
        String dateOverride = overrides[(dateStyle + 4)];
        String timeOverride = overrides[timeStyle];
        override = mergeOverrideStrings(patterns[(dateStyle + 4)], patterns[timeStyle], dateOverride, timeOverride);
      }
      

    }
    else if (timeStyle >= 0) {
      pattern = patterns[timeStyle];
      if (overrides != null) {
        override = overrides[timeStyle];
      }
    } else if (dateStyle >= 0) {
      pattern = patterns[(dateStyle + 4)];
      if (overrides != null) {
        override = overrides[(dateStyle + 4)];
      }
    } else {
      throw new IllegalArgumentException("No date or time style specified");
    }
    DateFormat result = cal.handleGetDateFormat(pattern, override, loc);
    result.setCalendar(cal);
    return result;
  }
  

  static class PatternData
  {
    public PatternData(String[] patterns, String[] overrides)
    {
      this.patterns = patterns;
      this.overrides = overrides;
    }
    
    private String getDateTimePattern(int dateStyle) { int glueIndex = 8;
      if (patterns.length >= 13) {
        glueIndex += dateStyle + 1;
      }
      String dateTimePattern = patterns[glueIndex];
      return dateTimePattern; }
    
    private String[] patterns;
    private String[] overrides;
    private static PatternData make(Calendar cal, ULocale loc) { String calType = cal.getType();
      String key = loc.getBaseName() + "+" + calType;
      PatternData patternData = (PatternData)Calendar.PATTERN_CACHE.get(key);
      if (patternData == null)
      {
        try {
          CalendarData calData = new CalendarData(loc, calType);
          patternData = new PatternData(calData.getDateTimePatterns(), calData.getOverrides());
        }
        catch (MissingResourceException e) {
          patternData = new PatternData(Calendar.DEFAULT_PATTERNS, null);
        }
        Calendar.PATTERN_CACHE.put(key, patternData);
      }
      return patternData;
    }
  }
  
  /**
   * @deprecated
   */
  public static String getDateTimePattern(Calendar cal, ULocale uLocale, int dateStyle)
  {
    PatternData patternData = PatternData.make(cal, uLocale);
    return patternData.getDateTimePattern(dateStyle);
  }
  

  private static String mergeOverrideStrings(String datePattern, String timePattern, String dateOverride, String timeOverride)
  {
    if ((dateOverride == null) && (timeOverride == null)) {
      return null;
    }
    
    if (dateOverride == null) {
      return expandOverride(timePattern, timeOverride);
    }
    
    if (timeOverride == null) {
      return expandOverride(datePattern, dateOverride);
    }
    
    if (dateOverride.equals(timeOverride)) {
      return dateOverride;
    }
    
    return expandOverride(datePattern, dateOverride) + ";" + expandOverride(timePattern, timeOverride);
  }
  



  private static String expandOverride(String pattern, String override)
  {
    if (override.indexOf('=') >= 0) {
      return override;
    }
    boolean inQuotes = false;
    char prevChar = ' ';
    StringBuilder result = new StringBuilder();
    
    StringCharacterIterator it = new StringCharacterIterator(pattern);
    
    for (char c = it.first(); c != 65535; c = it.next())
      if (c == '\'') {
        inQuotes = !inQuotes;
        prevChar = c;
      }
      else {
        if ((!inQuotes) && (c != prevChar)) {
          if (result.length() > 0) {
            result.append(";");
          }
          result.append(c);
          result.append("=");
          result.append(override);
        }
        prevChar = c;
      }
    return result.toString();
  }
  

  /**
   * @deprecated
   */
  public static class FormatConfiguration
  {
    private String pattern;
    
    private String override;
    
    private DateFormatSymbols formatData;
    
    private Calendar cal;
    
    private ULocale loc;
    

    private FormatConfiguration() {}
    

    /**
     * @deprecated
     */
    public String getPatternString()
    {
      return pattern;
    }
    
    /**
     * @deprecated
     */
    public String getOverrideString()
    {
      return override;
    }
    


    /**
     * @deprecated
     */
    public Calendar getCalendar()
    {
      return cal;
    }
    


    /**
     * @deprecated
     */
    public ULocale getLocale()
    {
      return loc;
    }
    


    /**
     * @deprecated
     */
    public DateFormatSymbols getDateFormatSymbols()
    {
      return formatData;
    }
  }
  































  protected void pinField(int field)
  {
    int max = getActualMaximum(field);
    int min = getActualMinimum(field);
    
    if (fields[field] > max) {
      set(field, max);
    } else if (fields[field] < min) {
      set(field, min);
    }
  }
  













































  protected int weekNumber(int desiredDay, int dayOfPeriod, int dayOfWeek)
  {
    int periodStartDayOfWeek = (dayOfWeek - getFirstDayOfWeek() - dayOfPeriod + 1) % 7;
    if (periodStartDayOfWeek < 0) { periodStartDayOfWeek += 7;
    }
    


    int weekNo = (desiredDay + periodStartDayOfWeek - 1) / 7;
    



    if (7 - periodStartDayOfWeek >= getMinimalDaysInFirstWeek()) { weekNo++;
    }
    return weekNo;
  }
  






























  protected final int weekNumber(int dayOfPeriod, int dayOfWeek)
  {
    return weekNumber(dayOfPeriod, dayOfPeriod, dayOfWeek);
  }
  

























































  public int fieldDifference(Date when, int field)
  {
    int min = 0;
    long startMs = getTimeInMillis();
    long targetMs = when.getTime();
    





    if (startMs < targetMs) {
      int max = 1;
      for (;;)
      {
        setTimeInMillis(startMs);
        add(field, max);
        long ms = getTimeInMillis();
        if (ms == targetMs)
          return max;
        if (ms > targetMs)
          break;
        if (max < Integer.MAX_VALUE) {
          min = max;
          max <<= 1;
          if (max < 0) {
            max = Integer.MAX_VALUE;
          }
        }
        else {
          throw new RuntimeException();
        }
      }
      
      while (max - min > 1) {
        int t = min + (max - min) / 2;
        setTimeInMillis(startMs);
        add(field, t);
        long ms = getTimeInMillis();
        if (ms == targetMs)
          return t;
        if (ms > targetMs) {
          max = t;
        } else {
          min = t;
        }
      }
    } else if (startMs > targetMs)
    {







      int max = -1;
      for (;;)
      {
        setTimeInMillis(startMs);
        add(field, max);
        long ms = getTimeInMillis();
        if (ms == targetMs)
          return max;
        if (ms < targetMs) {
          break;
        }
        min = max;
        max <<= 1;
        if (max == 0)
        {
          throw new RuntimeException();
        }
      }
      

      while (min - max > 1) {
        int t = min + (max - min) / 2;
        setTimeInMillis(startMs);
        add(field, t);
        long ms = getTimeInMillis();
        if (ms == targetMs)
          return t;
        if (ms < targetMs) {
          max = t;
        } else {
          min = t;
        }
      }
    }
    
    setTimeInMillis(startMs);
    add(field, min);
    return min;
  }
  





  public void setTimeZone(TimeZone value)
  {
    zone = value;
    








    areFieldsSet = false;
  }
  





  public TimeZone getTimeZone()
  {
    return zone;
  }
  










  public void setLenient(boolean lenient)
  {
    this.lenient = lenient;
  }
  




  public boolean isLenient()
  {
    return lenient;
  }
  




















  public void setRepeatedWallTimeOption(int option)
  {
    if ((option != 0) && (option != 1)) {
      throw new IllegalArgumentException("Illegal repeated wall time option - " + option);
    }
    repeatedWallTime = option;
  }
  












  public int getRepeatedWallTimeOption()
  {
    return repeatedWallTime;
  }
  


























  public void setSkippedWallTimeOption(int option)
  {
    if ((option != 0) && (option != 1) && (option != 2)) {
      throw new IllegalArgumentException("Illegal skipped wall time option - " + option);
    }
    skippedWallTime = option;
  }
  













  public int getSkippedWallTimeOption()
  {
    return skippedWallTime;
  }
  






  public void setFirstDayOfWeek(int value)
  {
    if (firstDayOfWeek != value) {
      if ((value < 1) || (value > 7)) {
        throw new IllegalArgumentException("Invalid day of week");
      }
      firstDayOfWeek = value;
      areFieldsSet = false;
    }
  }
  






  public int getFirstDayOfWeek()
  {
    return firstDayOfWeek;
  }
  












  public void setMinimalDaysInFirstWeek(int value)
  {
    if (value < 1) {
      value = 1;
    } else if (value > 7) {
      value = 7;
    }
    if (minimalDaysInFirstWeek != value) {
      minimalDaysInFirstWeek = value;
      areFieldsSet = false;
    }
  }
  









  public int getMinimalDaysInFirstWeek()
  {
    return minimalDaysInFirstWeek;
  }
  
  private static final int[][] LIMITS = { new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], { 1, 1, 7, 7 }, new int[0], { 0, 0, 1, 1 }, { 0, 0, 11, 11 }, { 0, 0, 23, 23 }, { 0, 0, 59, 59 }, { 0, 0, 59, 59 }, { 0, 0, 999, 999 }, { -43200000, -43200000, 43200000, 43200000 }, { 0, 0, 3600000, 3600000 }, new int[0], { 1, 1, 7, 7 }, new int[0], { -2130706432, -2130706432, 2130706432, 2130706432 }, { 0, 0, 86399999, 86399999 }, { 0, 0, 1, 1 } };
  







  protected static final int MINIMUM = 0;
  







  protected static final int GREATEST_MINIMUM = 1;
  






  protected static final int LEAST_MAXIMUM = 2;
  






  protected static final int MAXIMUM = 3;
  






  protected static final int RESOLVE_REMAP = 32;
  







  protected abstract int handleGetLimit(int paramInt1, int paramInt2);
  







  protected int getLimit(int field, int limitType)
  {
    switch (field) {
    case 7: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 18: 
    case 20: 
    case 21: 
    case 22: 
      return LIMITS[field][limitType];
    case 4: 
      int limit;
      
      int limit;
      if (limitType == 0) {
        limit = getMinimalDaysInFirstWeek() == 1 ? 1 : 0; } else { int limit;
        if (limitType == 1) {
          limit = 1;
        } else {
          int minDaysInFirst = getMinimalDaysInFirstWeek();
          int daysInMonth = handleGetLimit(5, limitType);
          int limit; if (limitType == 2) {
            limit = (daysInMonth + (7 - minDaysInFirst)) / 7;
          } else
            limit = (daysInMonth + 6 + (7 - minDaysInFirst)) / 7;
        }
      }
      return limit;
    }
    
    
    return handleGetLimit(field, limitType);
  }
  










































  public final int getMinimum(int field)
  {
    return getLimit(field, 0);
  }
  






  public final int getMaximum(int field)
  {
    return getLimit(field, 3);
  }
  






  public final int getGreatestMinimum(int field)
  {
    return getLimit(field, 1);
  }
  






  public final int getLeastMaximum(int field)
  {
    return getLimit(field, 2);
  }
  





























  public int getDayOfWeekType(int dayOfWeek)
  {
    if ((dayOfWeek < 1) || (dayOfWeek > 7)) {
      throw new IllegalArgumentException("Invalid day of week");
    }
    if (weekendOnset < weekendCease) {
      if ((dayOfWeek < weekendOnset) || (dayOfWeek > weekendCease)) {
        return 0;
      }
    }
    else if ((dayOfWeek > weekendCease) && (dayOfWeek < weekendOnset)) {
      return 0;
    }
    
    if (dayOfWeek == weekendOnset) {
      return weekendOnsetMillis == 0 ? 1 : 2;
    }
    if (dayOfWeek == weekendCease) {
      return weekendCeaseMillis == 0 ? 0 : 3;
    }
    return 1;
  }
  
















  public int getWeekendTransition(int dayOfWeek)
  {
    if (dayOfWeek == weekendOnset)
      return weekendOnsetMillis;
    if (dayOfWeek == weekendCease) {
      return weekendCeaseMillis;
    }
    throw new IllegalArgumentException("Not weekend transition day");
  }
  











  public boolean isWeekend(Date date)
  {
    setTime(date);
    return isWeekend();
  }
  









  public boolean isWeekend()
  {
    int dow = get(7);
    int dowt = getDayOfWeekType(dow);
    switch (dowt) {
    case 0: 
      return false;
    case 1: 
      return true;
    }
    
    



    int millisInDay = internalGet(14) + 1000 * (internalGet(13) + 60 * (internalGet(12) + 60 * internalGet(11)));
    
    int transition = getWeekendTransition(dow);
    return millisInDay >= transition;
  }
  











  public Object clone()
  {
    try
    {
      Calendar other = (Calendar)super.clone();
      
      fields = new int[fields.length];
      stamp = new int[fields.length];
      System.arraycopy(fields, 0, fields, 0, fields.length);
      System.arraycopy(stamp, 0, stamp, 0, fields.length);
      
      zone = ((TimeZone)zone.clone());
      return other;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }
  








  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append(getClass().getName());
    buffer.append("[time=");
    buffer.append(isTimeSet ? String.valueOf(time) : "?");
    buffer.append(",areFieldsSet=");
    buffer.append(areFieldsSet);
    buffer.append(",areAllFieldsSet=");
    buffer.append(areAllFieldsSet);
    buffer.append(",lenient=");
    buffer.append(lenient);
    buffer.append(",zone=");
    buffer.append(zone);
    buffer.append(",firstDayOfWeek=");
    buffer.append(firstDayOfWeek);
    buffer.append(",minimalDaysInFirstWeek=");
    buffer.append(minimalDaysInFirstWeek);
    buffer.append(",repeatedWallTime=");
    buffer.append(repeatedWallTime);
    buffer.append(",skippedWallTime=");
    buffer.append(skippedWallTime);
    for (int i = 0; i < fields.length; i++) {
      buffer.append(',').append(fieldName(i)).append('=');
      buffer.append(isSet(i) ? String.valueOf(fields[i]) : "?");
    }
    buffer.append(']');
    return buffer.toString();
  }
  

  private static class WeekData
  {
    public int firstDayOfWeek;
    
    public int minimalDaysInFirstWeek;
    
    public int weekendOnset;
    
    public int weekendOnsetMillis;
    
    public int weekendCease;
    public int weekendCeaseMillis;
    public ULocale actualLocale;
    
    public WeekData(int fdow, int mdifw, int weekendOnset, int weekendOnsetMillis, int weekendCease, int weekendCeaseMillis, ULocale actualLoc)
    {
      firstDayOfWeek = fdow;
      minimalDaysInFirstWeek = mdifw;
      actualLocale = actualLoc;
      this.weekendOnset = weekendOnset;
      this.weekendOnsetMillis = weekendOnsetMillis;
      this.weekendCease = weekendCease;
      this.weekendCeaseMillis = weekendCeaseMillis;
    }
  }
  






  private void setWeekData(ULocale locale)
  {
    WeekData data = (WeekData)cachedLocaleData.get(locale);
    
    if (data == null)
    {








      CalendarData calData = new CalendarData(locale, getType());
      ULocale min = ULocale.minimizeSubtags(calData.getULocale());
      ULocale useLocale; ULocale useLocale; if (min.getCountry().length() > 0) {
        useLocale = min;
      } else {
        ULocale max = ULocale.addLikelySubtags(min);
        StringBuilder buf = new StringBuilder();
        buf.append(min.getLanguage());
        if (min.getScript().length() > 0) {
          buf.append("_" + min.getScript());
        }
        if (max.getCountry().length() > 0) {
          buf.append("_" + max.getCountry());
        }
        if (min.getVariant().length() > 0) {
          buf.append("_" + min.getVariant());
        }
        useLocale = new ULocale(buf.toString());
      }
      
      UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
      


      UResourceBundle weekDataInfo = rb.get("weekData");
      UResourceBundle weekDataBundle = null;
      try {
        weekDataBundle = weekDataInfo.get(useLocale.getCountry());
      }
      catch (MissingResourceException mre) {
        weekDataBundle = weekDataInfo.get("001");
      }
      
      int[] wdi = weekDataBundle.getIntVector();
      data = new WeekData(wdi[0], wdi[1], wdi[2], wdi[3], wdi[4], wdi[5], calData.getULocale());
      

      cachedLocaleData.put(locale, data);
    }
    
    setFirstDayOfWeek(firstDayOfWeek);
    setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
    weekendOnset = weekendOnset;
    weekendOnsetMillis = weekendOnsetMillis;
    weekendCease = weekendCease;
    weekendCeaseMillis = weekendCeaseMillis;
    

    ULocale uloc = actualLocale;
    setLocale(uloc, uloc);
  }
  




  private void updateTime()
  {
    computeTime();
    


    if ((isLenient()) || (!areAllFieldsSet)) areFieldsSet = false;
    isTimeSet = true;
    areFieldsVirtuallySet = false;
  }
  





  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    if (!isTimeSet) {
      try {
        updateTime();
      }
      catch (IllegalArgumentException e) {}
    }
    

    stream.defaultWriteObject();
  }
  



  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    
    initInternal();
    
    isTimeSet = true;
    areFieldsSet = (this.areAllFieldsSet = 0);
    areFieldsVirtuallySet = true;
    nextStamp = 2;
  }
  













  protected void computeFields()
  {
    int[] offsets = new int[2];
    getTimeZone().getOffset(time, false, offsets);
    long localMillis = time + offsets[0] + offsets[1];
    

    int mask = internalSetMask;
    for (int i = 0; i < fields.length; i++) {
      if ((mask & 0x1) == 0) {
        stamp[i] = 1;
      } else {
        stamp[i] = 0;
      }
      mask >>= 1;
    }
    









    long days = floorDivide(localMillis, 86400000L);
    
    fields[20] = ((int)days + 2440588);
    
    computeGregorianAndDOWFields(fields[20]);
    




    handleComputeFields(fields[20]);
    


    computeWeekFields();
    



    int millisInDay = (int)(localMillis - days * 86400000L);
    fields[21] = millisInDay;
    fields[14] = (millisInDay % 1000);
    millisInDay /= 1000;
    fields[13] = (millisInDay % 60);
    millisInDay /= 60;
    fields[12] = (millisInDay % 60);
    millisInDay /= 60;
    fields[11] = millisInDay;
    fields[9] = (millisInDay / 12);
    fields[10] = (millisInDay % 12);
    fields[15] = offsets[0];
    fields[16] = offsets[1];
  }
  





  private final void computeGregorianAndDOWFields(int julianDay)
  {
    computeGregorianFields(julianDay);
    

    int dow = fields[7] = julianDayToDayOfWeek(julianDay);
    

    int dowLocal = dow - getFirstDayOfWeek() + 1;
    if (dowLocal < 1) {
      dowLocal += 7;
    }
    fields[18] = dowLocal;
  }
  













  protected final void computeGregorianFields(int julianDay)
  {
    long gregorianEpochDay = julianDay - 1721426;
    




    int[] rem = new int[1];
    int n400 = floorDivide(gregorianEpochDay, 146097, rem);
    int n100 = floorDivide(rem[0], 36524, rem);
    int n4 = floorDivide(rem[0], 1461, rem);
    int n1 = floorDivide(rem[0], 365, rem);
    int year = 400 * n400 + 100 * n100 + 4 * n4 + n1;
    int dayOfYear = rem[0];
    if ((n100 == 4) || (n1 == 4)) {
      dayOfYear = 365;
    } else {
      year++;
    }
    
    boolean isLeap = ((year & 0x3) == 0) && ((year % 100 != 0) || (year % 400 == 0));
    

    int correction = 0;
    int march1 = isLeap ? 60 : 59;
    if (dayOfYear >= march1) correction = isLeap ? 1 : 2;
    int month = (12 * (dayOfYear + correction) + 6) / 367;
    int dayOfMonth = dayOfYear - GREGORIAN_MONTH_COUNT[month][2] + 1;
    

    gregorianYear = year;
    gregorianMonth = month;
    gregorianDayOfMonth = dayOfMonth;
    gregorianDayOfYear = (dayOfYear + 1);
  }
  



















  private final void computeWeekFields()
  {
    int eyear = fields[19];
    int dayOfWeek = fields[7];
    int dayOfYear = fields[6];
    









    int yearOfWeekOfYear = eyear;
    int relDow = (dayOfWeek + 7 - getFirstDayOfWeek()) % 7;
    int relDowJan1 = (dayOfWeek - dayOfYear + 7001 - getFirstDayOfWeek()) % 7;
    int woy = (dayOfYear - 1 + relDowJan1) / 7;
    if (7 - relDowJan1 >= getMinimalDaysInFirstWeek()) {
      woy++;
    }
    


    if (woy == 0)
    {




      int prevDoy = dayOfYear + handleGetYearLength(eyear - 1);
      woy = weekNumber(prevDoy, dayOfWeek);
      yearOfWeekOfYear--;
    } else {
      int lastDoy = handleGetYearLength(eyear);
      





      if (dayOfYear >= lastDoy - 5) {
        int lastRelDow = (relDow + lastDoy - dayOfYear) % 7;
        if (lastRelDow < 0) {
          lastRelDow += 7;
        }
        if ((6 - lastRelDow >= getMinimalDaysInFirstWeek()) && (dayOfYear + 7 - relDow > lastDoy))
        {
          woy = 1;
          yearOfWeekOfYear++;
        }
      }
    }
    fields[3] = woy;
    fields[17] = yearOfWeekOfYear;
    

    int dayOfMonth = fields[5];
    fields[4] = weekNumber(dayOfMonth, dayOfWeek);
    fields[8] = ((dayOfMonth - 1) / 7 + 1);
  }
  













  static final int[][][] DATE_PRECEDENCE = { { { 5 }, { 3, 7 }, { 4, 7 }, { 8, 7 }, { 3, 18 }, { 4, 18 }, { 8, 18 }, { 6 }, { 37, 1 }, { 35, 17 } }, { { 3 }, { 4 }, { 8 }, { 40, 7 }, { 40, 18 } } };
  




















  static final int[][][] DOW_PRECEDENCE = { { { 7 }, { 18 } } };
  































  protected int resolveFields(int[][][] precedenceTable)
  {
    int bestField = -1;
    
    for (int g = 0; (g < precedenceTable.length) && (bestField < 0); g++) {
      int[][] group = precedenceTable[g];
      int bestStamp = 0;
      label164:
      for (int l = 0; l < group.length; l++) {
        int[] line = group[l];
        int lineStamp = 0;
        
        for (int i = line[0] >= 32 ? 1 : 0; i < line.length; i++) {
          int s = stamp[line[i]];
          
          if (s == 0) {
            break label164;
          }
          lineStamp = Math.max(lineStamp, s);
        }
        

        if (lineStamp > bestStamp) {
          int tempBestField = line[0];
          if (tempBestField >= 32) {
            tempBestField &= 0x1F;
            
            if ((tempBestField != 5) || (stamp[4] < stamp[tempBestField])) {
              bestField = tempBestField;
            }
          } else {
            bestField = tempBestField;
          }
          
          if (bestField == tempBestField) {
            bestStamp = lineStamp;
          }
        }
      }
    }
    return bestField >= 32 ? bestField & 0x1F : bestField;
  }
  



  protected int newestStamp(int first, int last, int bestStampSoFar)
  {
    int bestStamp = bestStampSoFar;
    for (int i = first; i <= last; i++) {
      if (stamp[i] > bestStamp) {
        bestStamp = stamp[i];
      }
    }
    return bestStamp;
  }
  



  protected final int getStamp(int field)
  {
    return stamp[field];
  }
  




  protected int newerField(int defaultField, int alternateField)
  {
    if (stamp[alternateField] > stamp[defaultField]) {
      return alternateField;
    }
    return defaultField;
  }
  







  protected void validateFields()
  {
    for (int field = 0; field < fields.length; field++) {
      if (stamp[field] >= 2) {
        validateField(field);
      }
    }
  }
  




  protected void validateField(int field)
  {
    int y;
    


    switch (field) {
    case 5: 
      y = handleGetExtendedYear();
      validateField(field, 1, handleGetMonthLength(y, internalGet(2)));
      break;
    case 6: 
      y = handleGetExtendedYear();
      validateField(field, 1, handleGetYearLength(y));
      break;
    case 8: 
      if (internalGet(field) == 0) {
        throw new IllegalArgumentException("DAY_OF_WEEK_IN_MONTH cannot be zero");
      }
      validateField(field, getMinimum(field), getMaximum(field));
      break;
    case 7: default: 
      validateField(field, getMinimum(field), getMaximum(field));
    }
    
  }
  







  protected final void validateField(int field, int min, int max)
  {
    int value = fields[field];
    if ((value < min) || (value > max)) {
      throw new IllegalArgumentException(fieldName(field) + '=' + value + ", valid range=" + min + ".." + max);
    }
  }
  






  protected void computeTime()
  {
    if (!isLenient()) {
      validateFields();
    }
    

    int julianDay = computeJulianDay();
    
    long millis = julianDayToMillis(julianDay);
    

    int millisInDay;
    

    int millisInDay;
    

    if ((stamp[21] >= 2) && (newestStamp(9, 14, 0) <= stamp[21]))
    {
      millisInDay = internalGet(21);
    } else {
      millisInDay = computeMillisInDay();
    }
    
    if ((stamp[15] >= 2) || (stamp[16] >= 2))
    {
      time = (millis + millisInDay - (internalGet(15) + internalGet(16)));































    }
    else if ((!lenient) || (skippedWallTime == 2))
    {


      int zoneOffset = computeZoneOffset(millis, millisInDay);
      long tmpTime = millis + millisInDay - zoneOffset;
      
      int zoneOffset1 = zone.getOffset(tmpTime);
      


      if (zoneOffset != zoneOffset1) {
        if (!lenient) {
          throw new IllegalArgumentException("The specified wall time does not exist due to time zone offset transition.");
        }
        
        assert (skippedWallTime == 2) : skippedWallTime;
        


        if ((zone instanceof BasicTimeZone)) {
          TimeZoneTransition transition = ((BasicTimeZone)zone).getPreviousTransition(tmpTime, true);
          if (transition == null)
          {
            throw new RuntimeException("Could not locate previous zone transition");
          }
          time = transition.getTime();
        }
        else
        {
          Long transitionT = getPreviousZoneTransitionTime(zone, tmpTime, 7200000L);
          if (transitionT == null) {
            transitionT = getPreviousZoneTransitionTime(zone, tmpTime, 108000000L);
            if (transitionT == null)
            {
              throw new RuntimeException("Could not locate previous zone transition within 30 hours from " + tmpTime);
            }
          }
          time = transitionT.longValue();
        }
      } else {
        time = tmpTime;
      }
    } else {
      time = (millis + millisInDay - computeZoneOffset(millis, millisInDay));
    }
  }
  









  private Long getPreviousZoneTransitionTime(TimeZone tz, long base, long duration)
  {
    assert (duration > 0L);
    
    long upper = base;
    long lower = base - duration - 1L;
    int offsetU = tz.getOffset(upper);
    int offsetL = tz.getOffset(lower);
    if (offsetU == offsetL) {
      return null;
    }
    return findPreviousZoneTransitionTime(tz, offsetU, upper, lower);
  }
  




  private static final int[] FIND_ZONE_TRANSITION_TIME_UNITS = { 3600000, 1800000, 60000, 1000 };
  












  private Long findPreviousZoneTransitionTime(TimeZone tz, int upperOffset, long upper, long lower)
  {
    boolean onUnitTime = false;
    long mid = 0L;
    
    for (int unit : FIND_ZONE_TRANSITION_TIME_UNITS) {
      long lunits = lower / unit;
      long uunits = upper / unit;
      if (uunits > lunits) {
        mid = (lunits + uunits + 1L >>> 1) * unit;
        onUnitTime = true;
        break;
      }
    }
    

    if (!onUnitTime) {
      mid = upper + lower >>> 1;
    }
    
    if (onUnitTime) {
      if (mid != upper) {
        int midOffset = tz.getOffset(mid);
        if (midOffset != upperOffset) {
          return findPreviousZoneTransitionTime(tz, upperOffset, upper, mid);
        }
        upper = mid;
      }
      
      mid -= 1L;
    } else {
      mid = upper + lower >>> 1;
    }
    
    if (mid == lower) {
      return Long.valueOf(upper);
    }
    int midOffset = tz.getOffset(mid);
    if (midOffset != upperOffset) {
      if (onUnitTime) {
        return Long.valueOf(upper);
      }
      return findPreviousZoneTransitionTime(tz, upperOffset, upper, mid);
    }
    return findPreviousZoneTransitionTime(tz, upperOffset, mid, lower);
  }
  








  protected int computeMillisInDay()
  {
    int millisInDay = 0;
    



    int hourOfDayStamp = stamp[11];
    int hourStamp = Math.max(stamp[10], stamp[9]);
    int bestStamp = hourStamp > hourOfDayStamp ? hourStamp : hourOfDayStamp;
    

    if (bestStamp != 0) {
      if (bestStamp == hourOfDayStamp)
      {

        millisInDay += internalGet(11);
      }
      else
      {
        millisInDay += internalGet(10);
        millisInDay += 12 * internalGet(9);
      }
    }
    


    millisInDay *= 60;
    millisInDay += internalGet(12);
    millisInDay *= 60;
    millisInDay += internalGet(13);
    millisInDay *= 1000;
    millisInDay += internalGet(14);
    
    return millisInDay;
  }
  







  protected int computeZoneOffset(long millis, int millisInDay)
  {
    int[] offsets = new int[2];
    long wall = millis + millisInDay;
    if ((zone instanceof BasicTimeZone)) {
      int duplicatedTimeOpt = repeatedWallTime == 1 ? 4 : 12;
      int nonExistingTimeOpt = skippedWallTime == 1 ? 12 : 4;
      ((BasicTimeZone)zone).getOffsetFromLocal(wall, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
    }
    else {
      zone.getOffset(wall, true, offsets);
      
      boolean sawRecentNegativeShift = false;
      if (repeatedWallTime == 1)
      {
        long tgmt = wall - (offsets[0] + offsets[1]);
        



        int offsetBefore6 = zone.getOffset(tgmt - 21600000L);
        int offsetDelta = offsets[0] + offsets[1] - offsetBefore6;
        
        assert (offsetDelta < -21600000) : offsetDelta;
        if (offsetDelta < 0) {
          sawRecentNegativeShift = true;
          


          zone.getOffset(wall + offsetDelta, true, offsets);
        }
      }
      if ((!sawRecentNegativeShift) && (skippedWallTime == 1))
      {




        long tgmt = wall - (offsets[0] + offsets[1]);
        zone.getOffset(tgmt, false, offsets);
      }
    }
    return offsets[0] + offsets[1];
  }
  












  protected int computeJulianDay()
  {
    if (stamp[20] >= 2) {
      int bestStamp = newestStamp(0, 8, 0);
      bestStamp = newestStamp(17, 19, bestStamp);
      if (bestStamp <= stamp[20]) {
        return internalGet(20);
      }
    }
    
    int bestField = resolveFields(getFieldResolutionTable());
    if (bestField < 0) {
      bestField = 5;
    }
    
    return handleComputeJulianDay(bestField);
  }
  







  protected int[][][] getFieldResolutionTable()
  {
    return DATE_PRECEDENCE;
  }
  











  protected abstract int handleComputeMonthStart(int paramInt1, int paramInt2, boolean paramBoolean);
  










  protected abstract int handleGetExtendedYear();
  










  protected int handleGetMonthLength(int extendedYear, int month)
  {
    return handleComputeMonthStart(extendedYear, month + 1, true) - handleComputeMonthStart(extendedYear, month, true);
  }
  








  protected int handleGetYearLength(int eyear)
  {
    return handleComputeMonthStart(eyear + 1, 0, false) - handleComputeMonthStart(eyear, 0, false);
  }
  








  protected int[] handleCreateFields()
  {
    return new int[23];
  }
  









  protected int getDefaultMonthInYear(int extendedYear)
  {
    return 0;
  }
  










  protected int getDefaultDayInMonth(int extendedYear, int month)
  {
    return 1;
  }
  







  protected int handleComputeJulianDay(int bestField)
  {
    boolean useMonth = (bestField == 5) || (bestField == 4) || (bestField == 8);
    
    int year;
    
    int year;
    
    if (bestField == 3)
    {



      year = internalGet(17, handleGetExtendedYear());
    } else {
      year = handleGetExtendedYear();
    }
    
    internalSet(19, year);
    
    int month = useMonth ? internalGet(2, getDefaultMonthInYear(year)) : 0;
    


    int julianDay = handleComputeMonthStart(year, month, useMonth);
    
    if (bestField == 5) {
      if (isSet(5)) {
        return julianDay + internalGet(5, getDefaultDayInMonth(year, month));
      }
      return julianDay + getDefaultDayInMonth(year, month);
    }
    

    if (bestField == 6) {
      return julianDay + internalGet(6);
    }
    
    int firstDOW = getFirstDayOfWeek();
    












    int first = julianDayToDayOfWeek(julianDay + 1) - firstDOW;
    if (first < 0) {
      first += 7;
    }
    


    int dowLocal = 0;
    switch (resolveFields(DOW_PRECEDENCE)) {
    case 7: 
      dowLocal = internalGet(7) - firstDOW;
      break;
    case 18: 
      dowLocal = internalGet(18) - 1;
    }
    
    dowLocal %= 7;
    if (dowLocal < 0) {
      dowLocal += 7;
    }
    



    int date = 1 - first + dowLocal;
    
    if (bestField == 8)
    {

      if (date < 1) {
        date += 7;
      }
      


      int dim = internalGet(8, 1);
      if (dim >= 0) {
        date += 7 * (dim - 1);



      }
      else
      {


        int m = internalGet(2, 0);
        int monthLength = handleGetMonthLength(year, m);
        date += ((monthLength - date) / 7 + dim + 1) * 7;
      }
      
    }
    else
    {
      if (7 - first < getMinimalDaysInFirstWeek()) {
        date += 7;
      }
      

      date += 7 * (internalGet(bestField) - 1);
    }
    
    return julianDay + date;
  }
  














  protected int computeGregorianMonthStart(int year, int month)
  {
    if ((month < 0) || (month > 11)) {
      int[] rem = new int[1];
      year += floorDivide(month, 12, rem);
      month = rem[0];
    }
    
    boolean isLeap = (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
    int y = year - 1;
    


    int julianDay = 365 * y + floorDivide(y, 4) - floorDivide(y, 100) + floorDivide(y, 400) + 1721426 - 1;
    



    if (month != 0) {
      julianDay += GREGORIAN_MONTH_COUNT[month][2];
    }
    
    return julianDay;
  }
  































  protected void handleComputeFields(int julianDay)
  {
    internalSet(2, getGregorianMonth());
    internalSet(5, getGregorianDayOfMonth());
    internalSet(6, getGregorianDayOfYear());
    int eyear = getGregorianYear();
    internalSet(19, eyear);
    int era = 1;
    if (eyear < 1) {
      era = 0;
      eyear = 1 - eyear;
    }
    internalSet(0, era);
    internalSet(1, eyear);
  }
  











  protected final int getGregorianYear()
  {
    return gregorianYear;
  }
  





  protected final int getGregorianMonth()
  {
    return gregorianMonth;
  }
  





  protected final int getGregorianDayOfYear()
  {
    return gregorianDayOfYear;
  }
  





  protected final int getGregorianDayOfMonth()
  {
    return gregorianDayOfMonth;
  }
  





  public final int getFieldCount()
  {
    return fields.length;
  }
  









  protected final void internalSet(int field, int value)
  {
    if ((1 << field & internalSetMask) == 0) {
      throw new IllegalStateException("Subclass cannot set " + fieldName(field));
    }
    
    fields[field] = value;
    stamp[field] = 1;
  }
  
  private static final int[][] GREGORIAN_MONTH_COUNT = { { 31, 31, 0, 0 }, { 28, 29, 31, 31 }, { 31, 31, 59, 60 }, { 30, 30, 90, 91 }, { 31, 31, 120, 121 }, { 30, 30, 151, 152 }, { 31, 31, 181, 182 }, { 31, 31, 212, 213 }, { 30, 30, 243, 244 }, { 31, 31, 273, 274 }, { 30, 30, 304, 305 }, { 31, 31, 334, 335 } };
  
























  protected static final boolean isGregorianLeapYear(int year)
  {
    return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
  }
  






  protected static final int gregorianMonthLength(int y, int m)
  {
    return GREGORIAN_MONTH_COUNT[m][0];
  }
  






  protected static final int gregorianPreviousMonthLength(int y, int m)
  {
    return m > 0 ? gregorianMonthLength(y, m - 1) : 31;
  }
  












  protected static final long floorDivide(long numerator, long denominator)
  {
    return numerator >= 0L ? numerator / denominator : (numerator + 1L) / denominator - 1L;
  }
  














  protected static final int floorDivide(int numerator, int denominator)
  {
    return numerator >= 0 ? numerator / denominator : (numerator + 1) / denominator - 1;
  }
  
















  protected static final int floorDivide(int numerator, int denominator, int[] remainder)
  {
    if (numerator >= 0) {
      remainder[0] = (numerator % denominator);
      return numerator / denominator;
    }
    int quotient = (numerator + 1) / denominator - 1;
    remainder[0] = (numerator - quotient * denominator);
    return quotient;
  }
  














  protected static final int floorDivide(long numerator, int denominator, int[] remainder)
  {
    if (numerator >= 0L) {
      remainder[0] = ((int)(numerator % denominator));
      return (int)(numerator / denominator);
    }
    int quotient = (int)((numerator + 1L) / denominator - 1L);
    remainder[0] = ((int)(numerator - quotient * denominator));
    return quotient;
  }
  
  private static final String[] FIELD_NAME = { "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH", "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR", "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET", "DST_OFFSET", "YEAR_WOY", "DOW_LOCAL", "EXTENDED_YEAR", "JULIAN_DAY", "MILLISECONDS_IN_DAY" };
  


  private ULocale validLocale;
  

  private ULocale actualLocale;
  


  protected String fieldName(int field)
  {
    try
    {
      return FIELD_NAME[field];
    } catch (ArrayIndexOutOfBoundsException e) {}
    return "Field " + field;
  }
  






  protected static final int millisToJulianDay(long millis)
  {
    return (int)(2440588L + floorDivide(millis, 86400000L));
  }
  





  protected static final long julianDayToMillis(int julian)
  {
    return (julian - 2440588) * 86400000L;
  }
  





  protected static final int julianDayToDayOfWeek(int julian)
  {
    int dayOfWeek = (julian + 2) % 7;
    if (dayOfWeek < 1) {
      dayOfWeek += 7;
    }
    return dayOfWeek;
  }
  



  protected final long internalGetTimeInMillis()
  {
    return time;
  }
  










  public String getType()
  {
    return "unknown";
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
}
