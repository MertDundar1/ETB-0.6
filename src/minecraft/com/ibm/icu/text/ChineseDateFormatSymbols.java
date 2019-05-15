package com.ibm.icu.text;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import java.util.Locale;




















/**
 * @deprecated
 */
public class ChineseDateFormatSymbols
  extends DateFormatSymbols
{
  static final long serialVersionUID = 6827816119783952890L;
  String[] isLeapMonth;
  
  /**
   * @deprecated
   */
  public ChineseDateFormatSymbols()
  {
    this(ULocale.getDefault(ULocale.Category.FORMAT));
  }
  

  /**
   * @deprecated
   */
  public ChineseDateFormatSymbols(Locale locale)
  {
    super(ChineseCalendar.class, ULocale.forLocale(locale));
  }
  

  /**
   * @deprecated
   */
  public ChineseDateFormatSymbols(ULocale locale)
  {
    super(ChineseCalendar.class, locale);
  }
  


  /**
   * @deprecated
   */
  public ChineseDateFormatSymbols(Calendar cal, Locale locale)
  {
    super(cal == null ? null : cal.getClass(), locale);
  }
  


  /**
   * @deprecated
   */
  public ChineseDateFormatSymbols(Calendar cal, ULocale locale)
  {
    super(cal == null ? null : cal.getClass(), locale);
  }
  
  /**
   * @deprecated
   */
  public String getLeapMonth(int leap)
  {
    return isLeapMonth[leap];
  }
  
  /**
   * @deprecated
   */
  protected void initializeData(ULocale loc, CalendarData calData)
  {
    super.initializeData(loc, calData);
    initializeIsLeapMonth();
  }
  
  void initializeData(DateFormatSymbols dfs) {
    super.initializeData(dfs);
    if ((dfs instanceof ChineseDateFormatSymbols))
    {
      isLeapMonth = isLeapMonth;
    } else {
      initializeIsLeapMonth();
    }
  }
  



  private void initializeIsLeapMonth()
  {
    isLeapMonth = new String[2];
    isLeapMonth[0] = "";
    isLeapMonth[1] = (leapMonthPatterns != null ? leapMonthPatterns[0].replace("{0}", "") : "");
  }
}
