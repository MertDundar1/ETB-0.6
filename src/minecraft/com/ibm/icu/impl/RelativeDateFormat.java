package com.ibm.icu.impl;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Comparator;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;




public class RelativeDateFormat
  extends DateFormat
{
  private static final long serialVersionUID = 1131984966440549435L;
  private DateFormat fDateFormat;
  private DateFormat fTimeFormat;
  private MessageFormat fCombinedFormat;
  
  public static class URelativeString
  {
    public int offset;
    public String string;
    
    URelativeString(int offset, String string)
    {
      this.offset = offset;
      this.string = string;
    }
    
    URelativeString(String offset, String string) { this.offset = Integer.parseInt(offset);
      this.string = string;
    }
  }
  








  public RelativeDateFormat(int timeStyle, int dateStyle, ULocale locale)
  {
    fLocale = locale;
    fTimeStyle = timeStyle;
    fDateStyle = dateStyle;
    
    if (fDateStyle != -1) {
      int newStyle = fDateStyle & 0xFF7F;
      DateFormat df = DateFormat.getDateInstance(newStyle, locale);
      if ((df instanceof SimpleDateFormat)) {
        fDateTimeFormat = ((SimpleDateFormat)df);
      } else {
        throw new IllegalArgumentException("Can't create SimpleDateFormat for date style");
      }
      fDatePattern = fDateTimeFormat.toPattern();
      if (fTimeStyle != -1) {
        newStyle = fTimeStyle & 0xFF7F;
        df = DateFormat.getTimeInstance(newStyle, locale);
        if ((df instanceof SimpleDateFormat)) {
          fTimePattern = ((SimpleDateFormat)df).toPattern();
        }
      }
    }
    else {
      int newStyle = fTimeStyle & 0xFF7F;
      DateFormat df = DateFormat.getTimeInstance(newStyle, locale);
      if ((df instanceof SimpleDateFormat)) {
        fDateTimeFormat = ((SimpleDateFormat)df);
      } else {
        throw new IllegalArgumentException("Can't create SimpleDateFormat for time style");
      }
      fTimePattern = fDateTimeFormat.toPattern();
    }
    
    initializeCalendar(null, fLocale);
    loadDates();
    initializeCombinedFormat(calendar, fLocale);
  }
  









  public StringBuffer format(Calendar cal, StringBuffer toAppendTo, FieldPosition fieldPosition)
  {
    String relativeDayString = null;
    if (fDateStyle != -1)
    {
      int dayDiff = dayDifference(cal);
      

      relativeDayString = getStringForDay(dayDiff);
    }
    
    if ((fDateTimeFormat != null) && ((fDatePattern != null) || (fTimePattern != null)))
    {
      if (fDatePattern == null)
      {
        fDateTimeFormat.applyPattern(fTimePattern);
        fDateTimeFormat.format(cal, toAppendTo, fieldPosition);
      } else if (fTimePattern == null)
      {
        if (relativeDayString != null) {
          toAppendTo.append(relativeDayString);
        } else {
          fDateTimeFormat.applyPattern(fDatePattern);
          fDateTimeFormat.format(cal, toAppendTo, fieldPosition);
        }
      } else {
        String datePattern = fDatePattern;
        if (relativeDayString != null)
        {
          datePattern = "'" + relativeDayString.replace("'", "''") + "'";
        }
        StringBuffer combinedPattern = new StringBuffer("");
        fCombinedFormat.format(new Object[] { fTimePattern, datePattern }, combinedPattern, new FieldPosition(0));
        fDateTimeFormat.applyPattern(combinedPattern.toString());
        fDateTimeFormat.format(cal, toAppendTo, fieldPosition);
      }
    } else if (fDateFormat != null)
    {

      if (relativeDayString != null) {
        toAppendTo.append(relativeDayString);
      } else {
        fDateFormat.format(cal, toAppendTo, fieldPosition);
      }
    }
    
    return toAppendTo;
  }
  


  public void parse(String text, Calendar cal, ParsePosition pos)
  {
    throw new UnsupportedOperationException("Relative Date parse is not implemented yet");
  }
  




  private SimpleDateFormat fDateTimeFormat = null;
  private String fDatePattern = null;
  private String fTimePattern = null;
  
  int fDateStyle;
  
  int fTimeStyle;
  ULocale fLocale;
  private transient URelativeString[] fDates = null;
  





  private String getStringForDay(int day)
  {
    if (fDates == null) {
      loadDates();
    }
    for (int i = 0; i < fDates.length; i++) {
      if (fDates[i].offset == day) {
        return fDates[i].string;
      }
    }
    return null;
  }
  


  private synchronized void loadDates()
  {
    ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", fLocale);
    ICUResourceBundle rdb = rb.getWithFallback("fields/day/relative");
    
    Set<URelativeString> datesSet = new TreeSet(new Comparator()
    {
      public int compare(RelativeDateFormat.URelativeString r1, RelativeDateFormat.URelativeString r2) {
        if (offset == offset)
          return 0;
        if (offset < offset) {
          return -1;
        }
        return 1;
      }
    });
    

    for (UResourceBundleIterator i = rdb.getIterator(); i.hasNext();) {
      UResourceBundle line = i.next();
      
      String k = line.getKey();
      String v = line.getString();
      URelativeString rs = new URelativeString(k, v);
      datesSet.add(rs);
    }
    fDates = ((URelativeString[])datesSet.toArray(new URelativeString[0]));
  }
  


  private static int dayDifference(Calendar until)
  {
    Calendar nowCal = (Calendar)until.clone();
    Date nowDate = new Date(System.currentTimeMillis());
    nowCal.clear();
    nowCal.setTime(nowDate);
    int dayDiff = until.get(20) - nowCal.get(20);
    return dayDiff;
  }
  






  private Calendar initializeCalendar(TimeZone zone, ULocale locale)
  {
    if (calendar == null) {
      if (zone == null) {
        calendar = Calendar.getInstance(locale);
      } else {
        calendar = Calendar.getInstance(zone, locale);
      }
    }
    return calendar;
  }
  
  private MessageFormat initializeCombinedFormat(Calendar cal, ULocale locale) {
    String pattern = "{1} {0}";
    try {
      CalendarData calData = new CalendarData(locale, cal.getType());
      String[] patterns = calData.getDateTimePatterns();
      if ((patterns != null) && (patterns.length >= 9)) {
        int glueIndex = 8;
        if (patterns.length >= 13)
        {
          switch (fDateStyle)
          {
          case 0: 
          case 128: 
            glueIndex++;
            break;
          case 1: 
          case 129: 
            glueIndex += 2;
            break;
          case 2: 
          case 130: 
            glueIndex += 3;
            break;
          case 3: 
          case 131: 
            glueIndex += 4;
            break;
          }
          
        }
        
        pattern = patterns[glueIndex];
      }
    }
    catch (MissingResourceException e) {}
    
    fCombinedFormat = new MessageFormat(pattern, locale);
    return fCombinedFormat;
  }
}
