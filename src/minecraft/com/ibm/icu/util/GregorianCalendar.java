package com.ibm.icu.util;

import java.util.Date;
import java.util.Locale;




























































































































































































































public class GregorianCalendar
  extends Calendar
{
  private static final long serialVersionUID = 9199388694351062137L;
  public static final int BC = 0;
  public static final int AD = 1;
  private static final int EPOCH_YEAR = 1970;
  private static final int[][] MONTH_COUNT = { { 31, 31, 0, 0 }, { 28, 29, 31, 31 }, { 31, 31, 59, 60 }, { 30, 30, 90, 91 }, { 31, 31, 120, 121 }, { 30, 30, 151, 152 }, { 31, 31, 181, 182 }, { 31, 31, 212, 213 }, { 30, 30, 243, 244 }, { 31, 31, 273, 274 }, { 30, 30, 304, 305 }, { 31, 31, 334, 335 } };
  





















  private static final int[][] LIMITS = { { 0, 0, 1, 1 }, { 1, 1, 5828963, 5838270 }, { 0, 0, 11, 11 }, { 1, 1, 52, 53 }, new int[0], { 1, 1, 28, 31 }, { 1, 1, 365, 366 }, new int[0], { -1, -1, 4, 5 }, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], { -5838270, -5838270, 5828964, 5838271 }, new int[0], { -5838269, -5838269, 5828963, 5838270 }, new int[0], new int[0] };
  



























  protected int handleGetLimit(int field, int limitType)
  {
    return LIMITS[field][limitType];
  }
  












  private long gregorianCutover = -12219292800000L;
  



  private transient int cutoverJulianDay = 2299161;
  




  private transient int gregorianCutoverYear = 1582;
  






  protected transient boolean isGregorian;
  





  protected transient boolean invertGregorian;
  






  public GregorianCalendar()
  {
    this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
  }
  






  public GregorianCalendar(TimeZone zone)
  {
    this(zone, ULocale.getDefault(ULocale.Category.FORMAT));
  }
  





  public GregorianCalendar(Locale aLocale)
  {
    this(TimeZone.getDefault(), aLocale);
  }
  





  public GregorianCalendar(ULocale locale)
  {
    this(TimeZone.getDefault(), locale);
  }
  






  public GregorianCalendar(TimeZone zone, Locale aLocale)
  {
    super(zone, aLocale);
    setTimeInMillis(System.currentTimeMillis());
  }
  






  public GregorianCalendar(TimeZone zone, ULocale locale)
  {
    super(zone, locale);
    setTimeInMillis(System.currentTimeMillis());
  }
  









  public GregorianCalendar(int year, int month, int date)
  {
    super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
    set(0, 1);
    set(1, year);
    set(2, month);
    set(5, date);
  }
  














  public GregorianCalendar(int year, int month, int date, int hour, int minute)
  {
    super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
    set(0, 1);
    set(1, year);
    set(2, month);
    set(5, date);
    set(11, hour);
    set(12, minute);
  }
  
















  public GregorianCalendar(int year, int month, int date, int hour, int minute, int second)
  {
    super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
    set(0, 1);
    set(1, year);
    set(2, month);
    set(5, date);
    set(11, hour);
    set(12, minute);
    set(13, second);
  }
  















  public void setGregorianChange(Date date)
  {
    gregorianCutover = date.getTime();
    



    if (gregorianCutover <= -184303902528000000L) {
      gregorianCutoverYear = (this.cutoverJulianDay = Integer.MIN_VALUE);
    } else if (gregorianCutover >= 183882168921600000L) {
      gregorianCutoverYear = (this.cutoverJulianDay = Integer.MAX_VALUE);

    }
    else
    {
      cutoverJulianDay = ((int)floorDivide(gregorianCutover, 86400000L));
      

      GregorianCalendar cal = new GregorianCalendar(getTimeZone());
      cal.setTime(date);
      gregorianCutoverYear = cal.get(19);
    }
  }
  







  public final Date getGregorianChange()
  {
    return new Date(gregorianCutover);
  }
  






  public boolean isLeapYear(int year)
  {
    return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
  }
  








  public boolean isEquivalentTo(Calendar other)
  {
    return (super.isEquivalentTo(other)) && (gregorianCutover == gregorianCutover);
  }
  





  public int hashCode()
  {
    return super.hashCode() ^ (int)gregorianCutover;
  }
  




  public void roll(int field, int amount)
  {
    switch (field)
    {







    case 3: 
      int woy = get(3);
      

      int isoYear = get(17);
      int isoDoy = internalGet(6);
      if (internalGet(2) == 0) {
        if (woy >= 52) {
          isoDoy += handleGetYearLength(isoYear);
        }
      }
      else if (woy == 1) {
        isoDoy -= handleGetYearLength(isoYear - 1);
      }
      
      woy += amount;
      
      if ((woy < 1) || (woy > 52))
      {





        int lastDoy = handleGetYearLength(isoYear);
        int lastRelDow = (lastDoy - isoDoy + internalGet(7) - getFirstDayOfWeek()) % 7;
        
        if (lastRelDow < 0) lastRelDow += 7;
        if (6 - lastRelDow >= getMinimalDaysInFirstWeek()) lastDoy -= 7;
        int lastWoy = weekNumber(lastDoy, lastRelDow + 1);
        woy = (woy + lastWoy - 1) % lastWoy + 1;
      }
      set(3, woy);
      set(1, isoYear);
      return;
    }
    
    
    super.roll(field, amount);
  }
  






  public int getActualMinimum(int field)
  {
    return getMinimum(field);
  }
  

























  public int getActualMaximum(int field)
  {
    switch (field)
    {




















    case 1: 
      Calendar cal = (Calendar)clone();
      cal.setLenient(true);
      
      int era = cal.get(0);
      Date d = cal.getTime();
      



      int lowGood = LIMITS[1][1];
      int highBad = LIMITS[1][2] + 1;
      while (lowGood + 1 < highBad) {
        int y = (lowGood + highBad) / 2;
        cal.set(1, y);
        if ((cal.get(1) == y) && (cal.get(0) == era)) {
          lowGood = y;
        } else {
          highBad = y;
          cal.setTime(d);
        }
      }
      
      return lowGood;
    }
    
    
    return super.getActualMaximum(field);
  }
  








  boolean inDaylightTime()
  {
    if (!getTimeZone().useDaylightTime()) return false;
    complete();
    return internalGet(16) != 0;
  }
  









  protected int handleGetMonthLength(int extendedYear, int month)
  {
    if ((month < 0) || (month > 11)) {
      int[] rem = new int[1];
      extendedYear += floorDivide(month, 12, rem);
      month = rem[0];
    }
    
    return MONTH_COUNT[month][0];
  }
  


  protected int handleGetYearLength(int eyear)
  {
    return isLeapYear(eyear) ? 366 : 365;
  }
  


  protected void handleComputeFields(int julianDay)
  {
    int eyear;
    

    int eyear;
    

    int dayOfYear;
    

    int month;
    

    int dayOfMonth;
    

    if (julianDay >= cutoverJulianDay) {
      int month = getGregorianMonth();
      int dayOfMonth = getGregorianDayOfMonth();
      int dayOfYear = getGregorianDayOfYear();
      eyear = getGregorianYear();
    }
    else
    {
      long julianEpochDay = julianDay - 1721424;
      eyear = (int)floorDivide(4L * julianEpochDay + 1464L, 1461L);
      

      long january1 = 365 * (eyear - 1) + floorDivide(eyear - 1, 4);
      dayOfYear = (int)(julianEpochDay - january1);
      






      boolean isLeap = (eyear & 0x3) == 0;
      

      int correction = 0;
      int march1 = isLeap ? 60 : 59;
      if (dayOfYear >= march1) {
        correction = isLeap ? 1 : 2;
      }
      month = (12 * (dayOfYear + correction) + 6) / 367;
      dayOfMonth = dayOfYear - MONTH_COUNT[month][2] + 1;
      dayOfYear++;
    }
    internalSet(2, month);
    internalSet(5, dayOfMonth);
    internalSet(6, dayOfYear);
    internalSet(19, eyear);
    int era = 1;
    if (eyear < 1) {
      era = 0;
      eyear = 1 - eyear;
    }
    internalSet(0, era);
    internalSet(1, eyear);
  }
  


  protected int handleGetExtendedYear()
  {
    int year;
    

    int year;
    
    if (newerField(19, 1) == 19) {
      year = internalGet(19, 1970);
    }
    else {
      int era = internalGet(0, 1);
      int year; if (era == 0) {
        year = 1 - internalGet(1, 1);
      } else {
        year = internalGet(1, 1970);
      }
    }
    return year;
  }
  



  protected int handleComputeJulianDay(int bestField)
  {
    invertGregorian = false;
    
    int jd = super.handleComputeJulianDay(bestField);
    


    if (isGregorian != jd >= cutoverJulianDay) {
      invertGregorian = true;
      jd = super.handleComputeJulianDay(bestField);
    }
    
    return jd;
  }
  






  protected int handleComputeMonthStart(int eyear, int month, boolean useMonth)
  {
    if ((month < 0) || (month > 11)) {
      int[] rem = new int[1];
      eyear += floorDivide(month, 12, rem);
      month = rem[0];
    }
    
    boolean isLeap = eyear % 4 == 0;
    int y = eyear - 1;
    int julianDay = 365 * y + floorDivide(y, 4) + 1721423;
    
    isGregorian = (eyear >= gregorianCutoverYear);
    if (invertGregorian) {
      isGregorian = (!isGregorian);
    }
    if (isGregorian) {
      isLeap = (isLeap) && ((eyear % 100 != 0) || (eyear % 400 == 0));
      

      julianDay += floorDivide(y, 400) - floorDivide(y, 100) + 2;
    }
    




    if (month != 0) {
      julianDay += MONTH_COUNT[month][2];
    }
    
    return julianDay;
  }
  



  public String getType()
  {
    return "gregorian";
  }
}
