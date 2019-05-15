package com.ibm.icu.util;

import com.ibm.icu.impl.Grego;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
















































public class SimpleTimeZone
  extends BasicTimeZone
{
  private static final long serialVersionUID = -7034676239311322769L;
  public static final int WALL_TIME = 0;
  public static final int STANDARD_TIME = 1;
  public static final int UTC_TIME = 2;
  
  public SimpleTimeZone(int rawOffset, String ID)
  {
    super(ID);
    construct(rawOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3600000);
  }
  































































  public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime)
  {
    super(ID);
    construct(rawOffset, startMonth, startDay, startDayOfWeek, startTime, 0, endMonth, endDay, endDayOfWeek, endTime, 0, 3600000);
  }
  













































  public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int startTimeMode, int endMonth, int endDay, int endDayOfWeek, int endTime, int endTimeMode, int dstSavings)
  {
    super(ID);
    construct(rawOffset, startMonth, startDay, startDayOfWeek, startTime, startTimeMode, endMonth, endDay, endDayOfWeek, endTime, endTimeMode, dstSavings);
  }
  





































  public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime, int dstSavings)
  {
    super(ID);
    construct(rawOffset, startMonth, startDay, startDayOfWeek, startTime, 0, endMonth, endDay, endDayOfWeek, endTime, 0, dstSavings);
  }
  










  public void setID(String ID)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    super.setID(ID);
    transitionRulesInitialized = false;
  }
  







  public void setRawOffset(int offsetMillis)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    raw = offsetMillis;
    transitionRulesInitialized = false;
  }
  






  public int getRawOffset()
  {
    return raw;
  }
  





  public void setStartYear(int year)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfosy = year;
    startYear = year;
    transitionRulesInitialized = false;
  }
  





















  public void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfo().setStart(month, dayOfWeekInMonth, dayOfWeek, time, -1, false);
    setStartRule(month, dayOfWeekInMonth, dayOfWeek, time, 0);
  }
  





































  private void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time, int mode)
  {
    assert (!isFrozen());
    
    startMonth = month;
    startDay = dayOfWeekInMonth;
    startDayOfWeek = dayOfWeek;
    startTime = time;
    startTimeMode = mode;
    decodeStartRule();
    
    transitionRulesInitialized = false;
  }
  











  public void setStartRule(int month, int dayOfMonth, int time)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfo().setStart(month, -1, -1, time, dayOfMonth, false);
    setStartRule(month, dayOfMonth, 0, time, 0);
  }
  
















  public void setStartRule(int month, int dayOfMonth, int dayOfWeek, int time, boolean after)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfo().setStart(month, -1, dayOfWeek, time, dayOfMonth, after);
    setStartRule(month, after ? dayOfMonth : -dayOfMonth, -dayOfWeek, time, 0);
  }
  




















  public void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfo().setEnd(month, dayOfWeekInMonth, dayOfWeek, time, -1, false);
    setEndRule(month, dayOfWeekInMonth, dayOfWeek, time, 0);
  }
  











  public void setEndRule(int month, int dayOfMonth, int time)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfo().setEnd(month, -1, -1, time, dayOfMonth, false);
    setEndRule(month, dayOfMonth, 0, time);
  }
  
















  public void setEndRule(int month, int dayOfMonth, int dayOfWeek, int time, boolean after)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    getSTZInfo().setEnd(month, -1, dayOfWeek, time, dayOfMonth, after);
    setEndRule(month, dayOfMonth, dayOfWeek, time, 0, after);
  }
  
  private void setEndRule(int month, int dayOfMonth, int dayOfWeek, int time, int mode, boolean after)
  {
    assert (!isFrozen());
    setEndRule(month, after ? dayOfMonth : -dayOfMonth, -dayOfWeek, time, mode);
  }
  
















  private void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time, int mode)
  {
    assert (!isFrozen());
    
    endMonth = month;
    endDay = dayOfWeekInMonth;
    endDayOfWeek = dayOfWeek;
    endTime = time;
    endTimeMode = mode;
    decodeEndRule();
    
    transitionRulesInitialized = false;
  }
  






  public void setDSTSavings(int millisSavedDuringDST)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
    }
    
    if (millisSavedDuringDST <= 0) {
      throw new IllegalArgumentException();
    }
    dst = millisSavedDuringDST;
    
    transitionRulesInitialized = false;
  }
  







  public int getDSTSavings()
  {
    return dst;
  }
  








  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    





















    if (xinfo != null) {
      xinfo.applyTo(this);
    }
  }
  





  public String toString()
  {
    return "SimpleTimeZone: " + getID();
  }
  
  private STZInfo getSTZInfo() {
    if (xinfo == null) {
      xinfo = new STZInfo();
    }
    return xinfo;
  }
  










  private static final byte[] staticMonthLength = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  

  private static final int DOM_MODE = 1;
  

  private static final int DOW_IN_MONTH_MODE = 2;
  
  private static final int DOW_GE_DOM_MODE = 3;
  
  private static final int DOW_LE_DOM_MODE = 4;
  
  private int raw;
  

  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis)
  {
    if ((month < 0) || (month > 11)) {
      throw new IllegalArgumentException();
    }
    
    return getOffset(era, year, month, day, dayOfWeek, millis, Grego.monthLength(year, month));
  }
  









  /**
   * @deprecated
   */
  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis, int monthLength)
  {
    if ((month < 0) || (month > 11)) {
      throw new IllegalArgumentException();
    }
    
    return getOffset(era, year, month, day, dayOfWeek, millis, Grego.monthLength(year, month), Grego.previousMonthLength(year, month));
  }
  







  private int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis, int monthLength, int prevMonthLength)
  {
    if (((era != 1) && (era != 0)) || (month < 0) || (month > 11) || (day < 1) || (day > monthLength) || (dayOfWeek < 1) || (dayOfWeek > 7) || (millis < 0) || (millis >= 86400000) || (monthLength < 28) || (monthLength > 31) || (prevMonthLength < 28) || (prevMonthLength > 31))
    {











      throw new IllegalArgumentException();
    }
    


































    int result = raw;
    

    if ((!useDaylight) || (year < startYear) || (era != 1)) { return result;
    }
    

    boolean southern = startMonth > endMonth;
    


    int startCompare = compareToRule(month, monthLength, prevMonthLength, day, dayOfWeek, millis, startTimeMode == 2 ? -raw : 0, startMode, startMonth, startDayOfWeek, startDay, startTime);
    



    int endCompare = 0;
    






    if (southern != startCompare >= 0)
    {


      endCompare = compareToRule(month, monthLength, prevMonthLength, day, dayOfWeek, millis, endTimeMode == 2 ? -raw : endTimeMode == 0 ? dst : 0, endMode, endMonth, endDayOfWeek, endDay, endTime);
    }
    









    if (((!southern) && (startCompare >= 0) && (endCompare < 0)) || ((southern) && ((startCompare >= 0) || (endCompare < 0))))
    {
      result += dst;
    }
    return result;
  }
  


  /**
   * @deprecated
   */
  public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets)
  {
    offsets[0] = getRawOffset();
    int[] fields = new int[6];
    Grego.timeToFields(date, fields);
    offsets[1] = (getOffset(1, fields[0], fields[1], fields[2], fields[3], fields[5]) - offsets[0]);
    


    boolean recalc = false;
    

    if (offsets[1] > 0) {
      if (((nonExistingTimeOpt & 0x3) == 1) || (((nonExistingTimeOpt & 0x3) != 3) && ((nonExistingTimeOpt & 0xC) != 12)))
      {

        date -= getDSTSavings();
        recalc = true;
      }
    }
    else if (((duplicatedTimeOpt & 0x3) == 3) || (((duplicatedTimeOpt & 0x3) != 1) && ((duplicatedTimeOpt & 0xC) == 4)))
    {

      date -= getDSTSavings();
      recalc = true;
    }
    

    if (recalc) {
      Grego.timeToFields(date, fields);
      offsets[1] = (getOffset(1, fields[0], fields[1], fields[2], fields[3], fields[5]) - offsets[0]);
    }
  }
  























  private int compareToRule(int month, int monthLen, int prevMonthLen, int dayOfMonth, int dayOfWeek, int millis, int millisDelta, int ruleMode, int ruleMonth, int ruleDayOfWeek, int ruleDay, int ruleMillis)
  {
    millis += millisDelta;
    
    while (millis >= 86400000) {
      millis -= 86400000;
      dayOfMonth++;
      dayOfWeek = 1 + dayOfWeek % 7;
      if (dayOfMonth > monthLen) {
        dayOfMonth = 1;
        



        month++;
      }
    }
    









    while (millis < 0)
    {
      dayOfMonth--;
      dayOfWeek = 1 + (dayOfWeek + 5) % 7;
      if (dayOfMonth < 1) {
        dayOfMonth = prevMonthLen;
        month--;
      }
      millis += 86400000;
    }
    
    if (month < ruleMonth) return -1;
    if (month > ruleMonth) { return 1;
    }
    int ruleDayOfMonth = 0;
    

    if (ruleDay > monthLen) {
      ruleDay = monthLen;
    }
    
    switch (ruleMode)
    {
    case 1: 
      ruleDayOfMonth = ruleDay;
      break;
    
    case 2: 
      if (ruleDay > 0) {
        ruleDayOfMonth = 1 + (ruleDay - 1) * 7 + (7 + ruleDayOfWeek - (dayOfWeek - dayOfMonth + 1)) % 7;
      }
      else
      {
        ruleDayOfMonth = monthLen + (ruleDay + 1) * 7 - (7 + (dayOfWeek + monthLen - dayOfMonth) - ruleDayOfWeek) % 7;
      }
      
      break;
    case 3: 
      ruleDayOfMonth = ruleDay + (49 + ruleDayOfWeek - ruleDay - dayOfWeek + dayOfMonth) % 7;
      
      break;
    case 4: 
      ruleDayOfMonth = ruleDay - (49 - ruleDayOfWeek + ruleDay + dayOfWeek - dayOfMonth) % 7;
    }
    
    



    if (dayOfMonth < ruleDayOfMonth) return -1;
    if (dayOfMonth > ruleDayOfMonth) { return 1;
    }
    if (millis < ruleMillis)
      return -1;
    if (millis > ruleMillis) {
      return 1;
    }
    return 0;
  }
  



  private int dst = 3600000;
  private STZInfo xinfo = null;
  
  private int startMonth;
  
  private int startDay;
  
  private int startDayOfWeek;
  
  private int startTime;
  private int startTimeMode;
  private int endTimeMode;
  private int endMonth;
  private int endDay;
  private int endDayOfWeek;
  
  public boolean useDaylightTime()
  {
    return useDaylight;
  }
  



  public boolean observesDaylightTime()
  {
    return useDaylight;
  }
  





  public boolean inDaylightTime(Date date)
  {
    GregorianCalendar gc = new GregorianCalendar(this);
    gc.setTime(date);
    return gc.inDaylightTime();
  }
  













  private void construct(int _raw, int _startMonth, int _startDay, int _startDayOfWeek, int _startTime, int _startTimeMode, int _endMonth, int _endDay, int _endDayOfWeek, int _endTime, int _endTimeMode, int _dst)
  {
    raw = _raw;
    startMonth = _startMonth;
    startDay = _startDay;
    startDayOfWeek = _startDayOfWeek;
    startTime = _startTime;
    startTimeMode = _startTimeMode;
    endMonth = _endMonth;
    endDay = _endDay;
    endDayOfWeek = _endDayOfWeek;
    endTime = _endTime;
    endTimeMode = _endTimeMode;
    dst = _dst;
    startYear = 0;
    startMode = 1;
    endMode = 1;
    
    decodeRules();
    
    if (_dst <= 0)
      throw new IllegalArgumentException();
  }
  
  private void decodeRules() {
    decodeStartRule();
    decodeEndRule();
  }
  























  private void decodeStartRule()
  {
    useDaylight = ((startDay != 0) && (endDay != 0));
    if ((useDaylight) && (dst == 0)) {
      dst = 86400000;
    }
    if (startDay != 0) {
      if ((startMonth < 0) || (startMonth > 11)) {
        throw new IllegalArgumentException();
      }
      if ((startTime < 0) || (startTime > 86400000) || (startTimeMode < 0) || (startTimeMode > 2))
      {
        throw new IllegalArgumentException();
      }
      if (startDayOfWeek == 0) {
        startMode = 1;
      } else {
        if (startDayOfWeek > 0) {
          startMode = 2;
        } else {
          startDayOfWeek = (-startDayOfWeek);
          if (startDay > 0) {
            startMode = 3;
          } else {
            startDay = (-startDay);
            startMode = 4;
          }
        }
        if (startDayOfWeek > 7) {
          throw new IllegalArgumentException();
        }
      }
      if (startMode == 2) {
        if ((startDay < -5) || (startDay > 5)) {
          throw new IllegalArgumentException();
        }
      } else if ((startDay < 1) || (startDay > staticMonthLength[startMonth])) {
        throw new IllegalArgumentException();
      }
    }
  }
  




  private void decodeEndRule()
  {
    useDaylight = ((startDay != 0) && (endDay != 0));
    if ((useDaylight) && (dst == 0)) {
      dst = 86400000;
    }
    if (endDay != 0) {
      if ((endMonth < 0) || (endMonth > 11)) {
        throw new IllegalArgumentException();
      }
      if ((endTime < 0) || (endTime > 86400000) || (endTimeMode < 0) || (endTimeMode > 2))
      {
        throw new IllegalArgumentException();
      }
      if (endDayOfWeek == 0) {
        endMode = 1;
      } else {
        if (endDayOfWeek > 0) {
          endMode = 2;
        } else {
          endDayOfWeek = (-endDayOfWeek);
          if (endDay > 0) {
            endMode = 3;
          } else {
            endDay = (-endDay);
            endMode = 4;
          }
        }
        if (endDayOfWeek > 7) {
          throw new IllegalArgumentException();
        }
      }
      if (endMode == 2) {
        if ((endDay < -5) || (endDay > 5)) {
          throw new IllegalArgumentException();
        }
      } else if ((endDay < 1) || (endDay > staticMonthLength[endMonth])) {
        throw new IllegalArgumentException();
      }
    }
  }
  





  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;
    SimpleTimeZone that = (SimpleTimeZone)obj;
    return (raw == raw) && (useDaylight == useDaylight) && (idEquals(getID(), that.getID())) && ((!useDaylight) || ((dst == dst) && (startMode == startMode) && (startMonth == startMonth) && (startDay == startDay) && (startDayOfWeek == startDayOfWeek) && (startTime == startTime) && (startTimeMode == startTimeMode) && (endMode == endMode) && (endMonth == endMonth) && (endDay == endDay) && (endDayOfWeek == endDayOfWeek) && (endTime == endTime) && (endTimeMode == endTimeMode) && (startYear == startYear)));
  }
  

















  private boolean idEquals(String id1, String id2)
  {
    if ((id1 == null) && (id2 == null)) {
      return true;
    }
    if ((id1 != null) && (id2 != null)) {
      return id1.equals(id2);
    }
    return false;
  }
  




  public int hashCode()
  {
    int ret = super.hashCode() + raw ^ (raw >>> 8) + (useDaylight ? 0 : 1);
    

    if (!useDaylight) {
      ret += (dst ^ (dst >>> 10) + startMode ^ (startMode >>> 11) + startMonth ^ (startMonth >>> 12) + startDay ^ (startDay >>> 13) + startDayOfWeek ^ (startDayOfWeek >>> 14) + startTime ^ (startTime >>> 15) + startTimeMode ^ (startTimeMode >>> 16) + endMode ^ (endMode >>> 17) + endMonth ^ (endMonth >>> 18) + endDay ^ (endDay >>> 19) + endDayOfWeek ^ (endDayOfWeek >>> 20) + endTime ^ (endTime >>> 21) + endTimeMode ^ (endTimeMode >>> 22) + startYear ^ startYear >>> 23);
    }
    












    return ret;
  }
  




  public Object clone()
  {
    if (isFrozen()) {
      return this;
    }
    return cloneAsThawed();
  }
  






  public boolean hasSameRules(TimeZone othr)
  {
    if (this == othr) {
      return true;
    }
    if (!(othr instanceof SimpleTimeZone)) {
      return false;
    }
    SimpleTimeZone other = (SimpleTimeZone)othr;
    return (other != null) && (raw == raw) && (useDaylight == useDaylight) && ((!useDaylight) || ((dst == dst) && (startMode == startMode) && (startMonth == startMonth) && (startDay == startDay) && (startDayOfWeek == startDayOfWeek) && (startTime == startTime) && (startTimeMode == startTimeMode) && (endMode == endMode) && (endMonth == endMonth) && (endDay == endDay) && (endDayOfWeek == endDayOfWeek) && (endTime == endTime) && (endTimeMode == endTimeMode) && (startYear == startYear)));
  }
  


  private int endTime;
  

  private int startYear;
  

  private boolean useDaylight;
  
  private int startMode;
  
  private int endMode;
  
  private transient boolean transitionRulesInitialized;
  
  private transient InitialTimeZoneRule initialRule;
  
  private transient TimeZoneTransition firstTransition;
  
  private transient AnnualTimeZoneRule stdRule;
  
  private transient AnnualTimeZoneRule dstRule;
  
  public TimeZoneTransition getNextTransition(long base, boolean inclusive)
  {
    if (!useDaylight) {
      return null;
    }
    
    initTransitionRules();
    long firstTransitionTime = firstTransition.getTime();
    if ((base < firstTransitionTime) || ((inclusive) && (base == firstTransitionTime))) {
      return firstTransition;
    }
    Date stdDate = stdRule.getNextStart(base, dstRule.getRawOffset(), dstRule.getDSTSavings(), inclusive);
    
    Date dstDate = dstRule.getNextStart(base, stdRule.getRawOffset(), stdRule.getDSTSavings(), inclusive);
    
    if ((stdDate != null) && ((dstDate == null) || (stdDate.before(dstDate)))) {
      return new TimeZoneTransition(stdDate.getTime(), dstRule, stdRule);
    }
    if ((dstDate != null) && ((stdDate == null) || (dstDate.before(stdDate)))) {
      return new TimeZoneTransition(dstDate.getTime(), stdRule, dstRule);
    }
    return null;
  }
  




  public TimeZoneTransition getPreviousTransition(long base, boolean inclusive)
  {
    if (!useDaylight) {
      return null;
    }
    
    initTransitionRules();
    long firstTransitionTime = firstTransition.getTime();
    if ((base < firstTransitionTime) || ((!inclusive) && (base == firstTransitionTime))) {
      return null;
    }
    Date stdDate = stdRule.getPreviousStart(base, dstRule.getRawOffset(), dstRule.getDSTSavings(), inclusive);
    
    Date dstDate = dstRule.getPreviousStart(base, stdRule.getRawOffset(), stdRule.getDSTSavings(), inclusive);
    
    if ((stdDate != null) && ((dstDate == null) || (stdDate.after(dstDate)))) {
      return new TimeZoneTransition(stdDate.getTime(), dstRule, stdRule);
    }
    if ((dstDate != null) && ((stdDate == null) || (dstDate.after(stdDate)))) {
      return new TimeZoneTransition(dstDate.getTime(), stdRule, dstRule);
    }
    return null;
  }
  




  public TimeZoneRule[] getTimeZoneRules()
  {
    initTransitionRules();
    
    int size = useDaylight ? 3 : 1;
    TimeZoneRule[] rules = new TimeZoneRule[size];
    rules[0] = initialRule;
    if (useDaylight) {
      rules[1] = stdRule;
      rules[2] = dstRule;
    }
    return rules;
  }
  





  private synchronized void initTransitionRules()
  {
    if (transitionRulesInitialized) {
      return;
    }
    if (useDaylight) {
      DateTimeRule dtRule = null;
      



      int timeRuleType = startTimeMode == 2 ? 2 : startTimeMode == 1 ? 1 : 0;
      
      switch (startMode) {
      case 1: 
        dtRule = new DateTimeRule(startMonth, startDay, startTime, timeRuleType);
        break;
      case 2: 
        dtRule = new DateTimeRule(startMonth, startDay, startDayOfWeek, startTime, timeRuleType);
        
        break;
      case 3: 
        dtRule = new DateTimeRule(startMonth, startDay, startDayOfWeek, true, startTime, timeRuleType);
        
        break;
      case 4: 
        dtRule = new DateTimeRule(startMonth, startDay, startDayOfWeek, false, startTime, timeRuleType);
      }
      
      

      dstRule = new AnnualTimeZoneRule(getID() + "(DST)", getRawOffset(), getDSTSavings(), dtRule, startYear, Integer.MAX_VALUE);
      


      long firstDstStart = dstRule.getFirstStart(getRawOffset(), 0).getTime();
      

      timeRuleType = endTimeMode == 2 ? 2 : endTimeMode == 1 ? 1 : 0;
      
      switch (endMode) {
      case 1: 
        dtRule = new DateTimeRule(endMonth, endDay, endTime, timeRuleType);
        break;
      case 2: 
        dtRule = new DateTimeRule(endMonth, endDay, endDayOfWeek, endTime, timeRuleType);
        break;
      case 3: 
        dtRule = new DateTimeRule(endMonth, endDay, endDayOfWeek, true, endTime, timeRuleType);
        
        break;
      case 4: 
        dtRule = new DateTimeRule(endMonth, endDay, endDayOfWeek, false, endTime, timeRuleType);
      }
      
      

      stdRule = new AnnualTimeZoneRule(getID() + "(STD)", getRawOffset(), 0, dtRule, startYear, Integer.MAX_VALUE);
      


      long firstStdStart = stdRule.getFirstStart(getRawOffset(), dstRule.getDSTSavings()).getTime();
      

      if (firstStdStart < firstDstStart) {
        initialRule = new InitialTimeZoneRule(getID() + "(DST)", getRawOffset(), dstRule.getDSTSavings());
        
        firstTransition = new TimeZoneTransition(firstStdStart, initialRule, stdRule);
      } else {
        initialRule = new InitialTimeZoneRule(getID() + "(STD)", getRawOffset(), 0);
        firstTransition = new TimeZoneTransition(firstDstStart, initialRule, dstRule);
      }
    }
    else
    {
      initialRule = new InitialTimeZoneRule(getID(), getRawOffset(), 0);
    }
    transitionRulesInitialized = true;
  }
  

  private transient boolean isFrozen = false;
  



  public boolean isFrozen()
  {
    return isFrozen;
  }
  



  public TimeZone freeze()
  {
    isFrozen = true;
    return this;
  }
  



  public TimeZone cloneAsThawed()
  {
    SimpleTimeZone tz = (SimpleTimeZone)super.cloneAsThawed();
    isFrozen = false;
    return tz;
  }
}
