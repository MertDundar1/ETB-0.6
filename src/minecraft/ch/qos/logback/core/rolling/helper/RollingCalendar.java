package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

























public class RollingCalendar
  extends GregorianCalendar
{
  private static final long serialVersionUID = -5937537740925066161L;
  static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
  
  PeriodicityType periodicityType = PeriodicityType.ERRONEOUS;
  

  public RollingCalendar() {}
  
  public RollingCalendar(TimeZone tz, Locale locale)
  {
    super(tz, locale);
  }
  
  public void init(String datePattern) {
    periodicityType = computePeriodicityType(datePattern);
  }
  
  private void setPeriodicityType(PeriodicityType periodicityType) {
    this.periodicityType = periodicityType;
  }
  
  public PeriodicityType getPeriodicityType() {
    return periodicityType;
  }
  
  public long getNextTriggeringMillis(Date now) {
    return getNextTriggeringDate(now).getTime();
  }
  







  public PeriodicityType computePeriodicityType(String datePattern)
  {
    RollingCalendar rollingCalendar = new RollingCalendar(GMT_TIMEZONE, Locale.getDefault());
    


    Date epoch = new Date(0L);
    
    if (datePattern != null) {
      for (PeriodicityType i : PeriodicityType.VALID_ORDERED_LIST) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        simpleDateFormat.setTimeZone(GMT_TIMEZONE);
        

        String r0 = simpleDateFormat.format(epoch);
        rollingCalendar.setPeriodicityType(i);
        
        Date next = new Date(rollingCalendar.getNextTriggeringMillis(epoch));
        String r1 = simpleDateFormat.format(next);
        

        if ((r0 != null) && (r1 != null) && (!r0.equals(r1))) {
          return i;
        }
      }
    }
    
    return PeriodicityType.ERRONEOUS;
  }
  
  public void printPeriodicity(ContextAwareBase cab) {
    switch (1.$SwitchMap$ch$qos$logback$core$rolling$helper$PeriodicityType[periodicityType.ordinal()]) {
    case 1: 
      cab.addInfo("Roll-over every millisecond.");
      break;
    
    case 2: 
      cab.addInfo("Roll-over every second.");
      break;
    
    case 3: 
      cab.addInfo("Roll-over every minute.");
      break;
    
    case 4: 
      cab.addInfo("Roll-over at the top of every hour.");
      break;
    
    case 5: 
      cab.addInfo("Roll-over at midday and midnight.");
      break;
    
    case 6: 
      cab.addInfo("Roll-over at midnight.");
      break;
    
    case 7: 
      cab.addInfo("Rollover at the start of week.");
      break;
    
    case 8: 
      cab.addInfo("Rollover at start of every month.");
      break;
    
    default: 
      cab.addInfo("Unknown periodicity.");
    }
  }
  
  public long periodsElapsed(long start, long end) {
    if (start > end) {
      throw new IllegalArgumentException("Start cannot come before end");
    }
    long diff = end - start;
    switch (1.$SwitchMap$ch$qos$logback$core$rolling$helper$PeriodicityType[periodicityType.ordinal()])
    {
    case 1: 
      return diff;
    case 2: 
      return diff / 1000L;
    case 3: 
      return diff / 60000L;
    case 4: 
      return (int)diff / 3600000;
    case 6: 
      return diff / 86400000L;
    case 7: 
      return diff / 604800000L;
    case 8: 
      return diffInMonths(start, end);
    }
    throw new IllegalStateException("Unknown periodicity type.");
  }
  
  public static int diffInMonths(long startTime, long endTime)
  {
    if (startTime > endTime)
      throw new IllegalArgumentException("startTime cannot be larger than endTime");
    Calendar startCal = Calendar.getInstance();
    startCal.setTimeInMillis(startTime);
    Calendar endCal = Calendar.getInstance();
    endCal.setTimeInMillis(endTime);
    int yearDiff = endCal.get(1) - startCal.get(1);
    int monthDiff = endCal.get(2) - startCal.get(2);
    return yearDiff * 12 + monthDiff;
  }
  
  public Date getRelativeDate(Date now, int periods) {
    setTime(now);
    
    switch (1.$SwitchMap$ch$qos$logback$core$rolling$helper$PeriodicityType[periodicityType.ordinal()]) {
    case 1: 
      add(14, periods);
      break;
    
    case 2: 
      set(14, 0);
      add(13, periods);
      break;
    
    case 3: 
      set(13, 0);
      set(14, 0);
      add(12, periods);
      break;
    
    case 4: 
      set(12, 0);
      set(13, 0);
      set(14, 0);
      add(11, periods);
      break;
    
    case 6: 
      set(11, 0);
      set(12, 0);
      set(13, 0);
      set(14, 0);
      add(5, periods);
      break;
    
    case 7: 
      set(7, getFirstDayOfWeek());
      set(11, 0);
      set(12, 0);
      set(13, 0);
      set(14, 0);
      add(3, periods);
      break;
    
    case 8: 
      set(5, 1);
      set(11, 0);
      set(12, 0);
      set(13, 0);
      set(14, 0);
      add(2, periods);
      break;
    case 5: 
    default: 
      throw new IllegalStateException("Unknown periodicity type.");
    }
    
    return getTime();
  }
  
  public Date getNextTriggeringDate(Date now) {
    return getRelativeDate(now, 1);
  }
}
