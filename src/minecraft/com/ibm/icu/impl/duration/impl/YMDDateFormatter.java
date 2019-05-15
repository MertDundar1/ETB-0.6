package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.DateFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
















public class YMDDateFormatter
  implements DateFormatter
{
  private String requestedFields;
  private String localeName;
  private TimeZone timeZone;
  private SimpleDateFormat df;
  
  public YMDDateFormatter(String requestedFields)
  {
    this(requestedFields, Locale.getDefault().toString(), TimeZone.getDefault());
  }
  









  public YMDDateFormatter(String requestedFields, String localeName, TimeZone timeZone)
  {
    this.requestedFields = requestedFields;
    this.localeName = localeName;
    this.timeZone = timeZone;
    
    Locale locale = Utils.localeFromString(localeName);
    df = new SimpleDateFormat("yyyy/mm/dd", locale);
    df.setTimeZone(timeZone);
  }
  



  public String format(long date)
  {
    return format(new Date(date));
  }
  









  public String format(Date date)
  {
    return df.format(date);
  }
  


  public DateFormatter withLocale(String locName)
  {
    if (!locName.equals(localeName)) {
      return new YMDDateFormatter(requestedFields, locName, timeZone);
    }
    return this;
  }
  


  public DateFormatter withTimeZone(TimeZone tz)
  {
    if (!tz.equals(timeZone)) {
      return new YMDDateFormatter(requestedFields, localeName, tz);
    }
    return this;
  }
}
