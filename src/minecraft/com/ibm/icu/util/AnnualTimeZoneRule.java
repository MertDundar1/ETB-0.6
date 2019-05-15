package com.ibm.icu.util;

import com.ibm.icu.impl.Grego;
import java.util.Date;





































public class AnnualTimeZoneRule
  extends TimeZoneRule
{
  private static final long serialVersionUID = -8870666707791230688L;
  public static final int MAX_YEAR = Integer.MAX_VALUE;
  private final DateTimeRule dateTimeRule;
  private final int startYear;
  private final int endYear;
  
  public AnnualTimeZoneRule(String name, int rawOffset, int dstSavings, DateTimeRule dateTimeRule, int startYear, int endYear)
  {
    super(name, rawOffset, dstSavings);
    this.dateTimeRule = dateTimeRule;
    this.startYear = startYear;
    this.endYear = (endYear > Integer.MAX_VALUE ? Integer.MAX_VALUE : endYear);
  }
  







  public DateTimeRule getRule()
  {
    return dateTimeRule;
  }
  







  public int getStartYear()
  {
    return startYear;
  }
  







  public int getEndYear()
  {
    return endYear;
  }
  













  public Date getStartInYear(int year, int prevRawOffset, int prevDSTSavings)
  {
    if ((year < startYear) || (year > endYear)) {
      return null;
    }
    

    int type = dateTimeRule.getDateRuleType();
    long ruleDay;
    long ruleDay; if (type == 0) {
      ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), dateTimeRule.getRuleDayOfMonth());
    } else {
      boolean after = true;
      if (type == 1) {
        int weeks = dateTimeRule.getRuleWeekInMonth();
        if (weeks > 0) {
          long ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), 1);
          ruleDay += 7 * (weeks - 1);
        } else {
          after = false;
          long ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), Grego.monthLength(year, dateTimeRule.getRuleMonth()));
          
          ruleDay += 7 * (weeks + 1);
        }
      } else {
        int month = dateTimeRule.getRuleMonth();
        int dom = dateTimeRule.getRuleDayOfMonth();
        if (type == 3) {
          after = false;
          
          if ((month == 1) && (dom == 29) && (!Grego.isLeapYear(year))) {
            dom--;
          }
        }
        ruleDay = Grego.fieldsToDay(year, month, dom);
      }
      
      int dow = Grego.dayOfWeek(ruleDay);
      int delta = dateTimeRule.getRuleDayOfWeek() - dow;
      if (after) {
        delta = delta < 0 ? delta + 7 : delta;
      } else {
        delta = delta > 0 ? delta - 7 : delta;
      }
      ruleDay += delta;
    }
    
    long ruleTime = ruleDay * 86400000L + dateTimeRule.getRuleMillisInDay();
    if (dateTimeRule.getTimeRuleType() != 2) {
      ruleTime -= prevRawOffset;
    }
    if (dateTimeRule.getTimeRuleType() == 0) {
      ruleTime -= prevDSTSavings;
    }
    return new Date(ruleTime);
  }
  



  public Date getFirstStart(int prevRawOffset, int prevDSTSavings)
  {
    return getStartInYear(startYear, prevRawOffset, prevDSTSavings);
  }
  



  public Date getFinalStart(int prevRawOffset, int prevDSTSavings)
  {
    if (endYear == Integer.MAX_VALUE) {
      return null;
    }
    return getStartInYear(endYear, prevRawOffset, prevDSTSavings);
  }
  



  public Date getNextStart(long base, int prevRawOffset, int prevDSTSavings, boolean inclusive)
  {
    int[] fields = Grego.timeToFields(base, null);
    int year = fields[0];
    if (year < startYear) {
      return getFirstStart(prevRawOffset, prevDSTSavings);
    }
    Date d = getStartInYear(year, prevRawOffset, prevDSTSavings);
    if ((d != null) && ((d.getTime() < base) || ((!inclusive) && (d.getTime() == base)))) {
      d = getStartInYear(year + 1, prevRawOffset, prevDSTSavings);
    }
    return d;
  }
  



  public Date getPreviousStart(long base, int prevRawOffset, int prevDSTSavings, boolean inclusive)
  {
    int[] fields = Grego.timeToFields(base, null);
    int year = fields[0];
    if (year > endYear) {
      return getFinalStart(prevRawOffset, prevDSTSavings);
    }
    Date d = getStartInYear(year, prevRawOffset, prevDSTSavings);
    if ((d != null) && ((d.getTime() > base) || ((!inclusive) && (d.getTime() == base)))) {
      d = getStartInYear(year - 1, prevRawOffset, prevDSTSavings);
    }
    return d;
  }
  



  public boolean isEquivalentTo(TimeZoneRule other)
  {
    if (!(other instanceof AnnualTimeZoneRule)) {
      return false;
    }
    AnnualTimeZoneRule otherRule = (AnnualTimeZoneRule)other;
    if ((startYear == startYear) && (endYear == endYear) && (dateTimeRule.equals(dateTimeRule)))
    {

      return super.isEquivalentTo(other);
    }
    return false;
  }
  




  public boolean isTransitionRule()
  {
    return true;
  }
  






  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append(super.toString());
    buf.append(", rule={" + dateTimeRule + "}");
    buf.append(", startYear=" + startYear);
    buf.append(", endYear=");
    if (endYear == Integer.MAX_VALUE) {
      buf.append("max");
    } else {
      buf.append(endYear);
    }
    return buf.toString();
  }
}
