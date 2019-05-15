package com.ibm.icu.text;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.io.InvalidObjectException;
import java.text.FieldPosition;
import java.util.Locale;



































/**
 * @deprecated
 */
public class ChineseDateFormat
  extends SimpleDateFormat
{
  static final long serialVersionUID = -4610300753104099899L;
  
  /**
   * @deprecated
   */
  public ChineseDateFormat(String pattern, Locale locale)
  {
    this(pattern, ULocale.forLocale(locale));
  }
  


  /**
   * @deprecated
   */
  public ChineseDateFormat(String pattern, ULocale locale)
  {
    this(pattern, null, locale);
  }
  









  /**
   * @deprecated
   */
  public ChineseDateFormat(String pattern, String override, ULocale locale)
  {
    super(pattern, new ChineseDateFormatSymbols(locale), new ChineseCalendar(TimeZone.getDefault(), locale), locale, true, override);
  }
  

































  /**
   * @deprecated
   */
  protected void subFormat(StringBuffer buf, char ch, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal)
  {
    super.subFormat(buf, ch, count, beginOffset, fieldNum, capitalizationContext, pos, cal);
  }
  














  /**
   * @deprecated
   */
  protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal)
  {
    return super.subParse(text, start, ch, count, obeyCount, allowNegative, ambiguousYear, cal);
  }
  


  /**
   * @deprecated
   */
  protected DateFormat.Field patternCharToDateFormatField(char ch)
  {
    return super.patternCharToDateFormatField(ch);
  }
  




  /**
   * @deprecated
   */
  public static class Field
    extends DateFormat.Field
  {
    private static final long serialVersionUID = -5102130532751400330L;
    



    /**
     * @deprecated
     */
    public static final Field IS_LEAP_MONTH = new Field("is leap month", 22);
    







    /**
     * @deprecated
     */
    protected Field(String name, int calendarField)
    {
      super(calendarField);
    }
    











    /**
     * @deprecated
     */
    public static DateFormat.Field ofCalendarField(int calendarField)
    {
      if (calendarField == 22) {
        return IS_LEAP_MONTH;
      }
      return DateFormat.Field.ofCalendarField(calendarField);
    }
    

    /**
     * @deprecated
     */
    protected Object readResolve()
      throws InvalidObjectException
    {
      if (getClass() != Field.class) {
        throw new InvalidObjectException("A subclass of ChineseDateFormat.Field must implement readResolve.");
      }
      if (getName().equals(IS_LEAP_MONTH.getName())) {
        return IS_LEAP_MONTH;
      }
      throw new InvalidObjectException("Unknown attribute name.");
    }
  }
}
