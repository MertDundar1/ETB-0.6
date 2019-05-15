package com.ibm.icu.util;

import com.ibm.icu.impl.Grego;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
























public class RuleBasedTimeZone
  extends BasicTimeZone
{
  private static final long serialVersionUID = 7580833058949327935L;
  private final InitialTimeZoneRule initialRule;
  private List<TimeZoneRule> historicRules;
  private AnnualTimeZoneRule[] finalRules;
  private transient List<TimeZoneTransition> historicTransitions;
  private transient boolean upToDate;
  
  public RuleBasedTimeZone(String id, InitialTimeZoneRule initialRule)
  {
    super(id);
    this.initialRule = initialRule;
  }
  









  public void addTransitionRule(TimeZoneRule rule)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen RuleBasedTimeZone instance.");
    }
    if (!rule.isTransitionRule()) {
      throw new IllegalArgumentException("Rule must be a transition rule");
    }
    if (((rule instanceof AnnualTimeZoneRule)) && (((AnnualTimeZoneRule)rule).getEndYear() == Integer.MAX_VALUE))
    {

      if (finalRules == null) {
        finalRules = new AnnualTimeZoneRule[2];
        finalRules[0] = ((AnnualTimeZoneRule)rule);
      } else if (finalRules[1] == null) {
        finalRules[1] = ((AnnualTimeZoneRule)rule);
      }
      else {
        throw new IllegalStateException("Too many final rules");
      }
    }
    else {
      if (historicRules == null) {
        historicRules = new ArrayList();
      }
      historicRules.add(rule);
    }
    

    upToDate = false;
  }
  






  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds)
  {
    if (era == 0)
    {
      year = 1 - year;
    }
    long time = Grego.fieldsToDay(year, month, day) * 86400000L + milliseconds;
    int[] offsets = new int[2];
    getOffset(time, true, 3, 1, offsets);
    return offsets[0] + offsets[1];
  }
  





  public void getOffset(long time, boolean local, int[] offsets)
  {
    getOffset(time, local, 4, 12, offsets);
  }
  



  /**
   * @deprecated
   */
  public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets)
  {
    getOffset(date, true, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
  }
  







  public int getRawOffset()
  {
    long now = System.currentTimeMillis();
    int[] offsets = new int[2];
    getOffset(now, false, offsets);
    return offsets[0];
  }
  





  public boolean inDaylightTime(Date date)
  {
    int[] offsets = new int[2];
    getOffset(date.getTime(), false, offsets);
    return offsets[1] != 0;
  }
  







  public void setRawOffset(int offsetMillis)
  {
    throw new UnsupportedOperationException("setRawOffset in RuleBasedTimeZone is not supported.");
  }
  









  public boolean useDaylightTime()
  {
    long now = System.currentTimeMillis();
    int[] offsets = new int[2];
    getOffset(now, false, offsets);
    if (offsets[1] != 0) {
      return true;
    }
    
    TimeZoneTransition tt = getNextTransition(now, false);
    if ((tt != null) && (tt.getTo().getDSTSavings() != 0)) {
      return true;
    }
    return false;
  }
  




  public boolean observesDaylightTime()
  {
    long time = System.currentTimeMillis();
    

    int[] offsets = new int[2];
    getOffset(time, false, offsets);
    if (offsets[1] != 0) {
      return true;
    }
    

    BitSet checkFinals = finalRules == null ? null : new BitSet(finalRules.length);
    for (;;) {
      TimeZoneTransition tt = getNextTransition(time, false);
      if (tt == null) {
        break;
      }
      
      TimeZoneRule toRule = tt.getTo();
      if (toRule.getDSTSavings() != 0) {
        return true;
      }
      if (checkFinals != null)
      {
        for (int i = 0; i < finalRules.length; i++) {
          if (finalRules[i].equals(toRule)) {
            checkFinals.set(i);
          }
        }
        if (checkFinals.cardinality() == finalRules.length) {
          break;
        }
      }
      
      time = tt.getTime();
    }
    return false;
  }
  





  public boolean hasSameRules(TimeZone other)
  {
    if (this == other) {
      return true;
    }
    
    if (!(other instanceof RuleBasedTimeZone))
    {
      return false;
    }
    RuleBasedTimeZone otherRBTZ = (RuleBasedTimeZone)other;
    

    if (!initialRule.isEquivalentTo(initialRule)) {
      return false;
    }
    

    if ((finalRules != null) && (finalRules != null)) {
      for (int i = 0; i < finalRules.length; i++) {
        if ((finalRules[i] != null) || (finalRules[i] != null))
        {

          if ((finalRules[i] == null) || (finalRules[i] == null) || (!finalRules[i].isEquivalentTo(finalRules[i])))
          {



            return false; } }
      }
    } else if ((finalRules != null) || (finalRules != null)) {
      return false;
    }
    

    if ((historicRules != null) && (historicRules != null)) {
      if (historicRules.size() != historicRules.size()) {
        return false;
      }
      for (TimeZoneRule rule : historicRules) {
        boolean foundSameRule = false;
        for (TimeZoneRule orule : historicRules) {
          if (rule.isEquivalentTo(orule)) {
            foundSameRule = true;
            break;
          }
        }
        if (!foundSameRule) {
          return false;
        }
      }
    } else if ((historicRules != null) || (historicRules != null)) {
      return false;
    }
    return true;
  }
  







  public TimeZoneRule[] getTimeZoneRules()
  {
    int size = 1;
    if (historicRules != null) {
      size += historicRules.size();
    }
    
    if (finalRules != null) {
      if (finalRules[1] != null) {
        size += 2;
      } else {
        size++;
      }
    }
    TimeZoneRule[] rules = new TimeZoneRule[size];
    rules[0] = initialRule;
    
    int idx = 1;
    if (historicRules != null) {
      for (; idx < historicRules.size() + 1; idx++) {
        rules[idx] = ((TimeZoneRule)historicRules.get(idx - 1));
      }
    }
    if (finalRules != null) {
      rules[(idx++)] = finalRules[0];
      if (finalRules[1] != null) {
        rules[idx] = finalRules[1];
      }
    }
    return rules;
  }
  





  public TimeZoneTransition getNextTransition(long base, boolean inclusive)
  {
    complete();
    if (historicTransitions == null) {
      return null;
    }
    boolean isFinal = false;
    TimeZoneTransition result = null;
    TimeZoneTransition tzt = (TimeZoneTransition)historicTransitions.get(0);
    long tt = tzt.getTime();
    if ((tt > base) || ((inclusive) && (tt == base))) {
      result = tzt;
    } else {
      int idx = historicTransitions.size() - 1;
      tzt = (TimeZoneTransition)historicTransitions.get(idx);
      tt = tzt.getTime();
      if ((inclusive) && (tt == base)) {
        result = tzt;
      } else if (tt <= base) {
        if (finalRules != null)
        {
          Date start0 = finalRules[0].getNextStart(base, finalRules[1].getRawOffset(), finalRules[1].getDSTSavings(), inclusive);
          
          Date start1 = finalRules[1].getNextStart(base, finalRules[0].getRawOffset(), finalRules[0].getDSTSavings(), inclusive);
          

          if (start1.after(start0)) {
            tzt = new TimeZoneTransition(start0.getTime(), finalRules[1], finalRules[0]);
          } else {
            tzt = new TimeZoneTransition(start1.getTime(), finalRules[0], finalRules[1]);
          }
          result = tzt;
          isFinal = true;
        } else {
          return null;
        }
      }
      else {
        idx--;
        TimeZoneTransition prev = tzt;
        while (idx > 0) {
          tzt = (TimeZoneTransition)historicTransitions.get(idx);
          tt = tzt.getTime();
          if ((tt < base) || ((!inclusive) && (tt == base))) {
            break;
          }
          idx--;
          prev = tzt;
        }
        result = prev;
      }
    }
    if (result != null)
    {
      TimeZoneRule from = result.getFrom();
      TimeZoneRule to = result.getTo();
      if ((from.getRawOffset() == to.getRawOffset()) && (from.getDSTSavings() == to.getDSTSavings()))
      {

        if (isFinal) {
          return null;
        }
        result = getNextTransition(result.getTime(), false);
      }
    }
    
    return result;
  }
  





  public TimeZoneTransition getPreviousTransition(long base, boolean inclusive)
  {
    complete();
    if (historicTransitions == null) {
      return null;
    }
    TimeZoneTransition result = null;
    TimeZoneTransition tzt = (TimeZoneTransition)historicTransitions.get(0);
    long tt = tzt.getTime();
    if ((inclusive) && (tt == base)) {
      result = tzt;
    } else { if (tt >= base) {
        return null;
      }
      int idx = historicTransitions.size() - 1;
      tzt = (TimeZoneTransition)historicTransitions.get(idx);
      tt = tzt.getTime();
      if ((inclusive) && (tt == base)) {
        result = tzt;
      } else if (tt < base) {
        if (finalRules != null)
        {
          Date start0 = finalRules[0].getPreviousStart(base, finalRules[1].getRawOffset(), finalRules[1].getDSTSavings(), inclusive);
          
          Date start1 = finalRules[1].getPreviousStart(base, finalRules[0].getRawOffset(), finalRules[0].getDSTSavings(), inclusive);
          

          if (start1.before(start0)) {
            tzt = new TimeZoneTransition(start0.getTime(), finalRules[1], finalRules[0]);
          } else {
            tzt = new TimeZoneTransition(start1.getTime(), finalRules[0], finalRules[1]);
          }
        }
        result = tzt;
      }
      else {
        idx--;
        while (idx >= 0) {
          tzt = (TimeZoneTransition)historicTransitions.get(idx);
          tt = tzt.getTime();
          if ((tt < base) || ((inclusive) && (tt == base))) {
            break;
          }
          idx--;
        }
        result = tzt;
      }
    }
    if (result != null)
    {
      TimeZoneRule from = result.getFrom();
      TimeZoneRule to = result.getTo();
      if ((from.getRawOffset() == to.getRawOffset()) && (from.getDSTSavings() == to.getDSTSavings()))
      {

        result = getPreviousTransition(result.getTime(), false);
      }
    }
    return result;
  }
  




  public Object clone()
  {
    if (isFrozen()) {
      return this;
    }
    return cloneAsThawed();
  }
  





  private void complete()
  {
    if (upToDate)
    {
      return;
    }
    


    if ((finalRules != null) && (finalRules[1] == null)) {
      throw new IllegalStateException("Incomplete final rules");
    }
    

    if ((historicRules != null) || (finalRules != null)) {
      TimeZoneRule curRule = initialRule;
      long lastTransitionTime = -184303902528000000L;
      


      if (historicRules != null) {
        BitSet done = new BitSet(historicRules.size());
        for (;;)
        {
          int curStdOffset = curRule.getRawOffset();
          int curDstSavings = curRule.getDSTSavings();
          long nextTransitionTime = 183882168921600000L;
          TimeZoneRule nextRule = null;
          


          for (int i = 0; i < historicRules.size(); i++) {
            if (!done.get(i))
            {

              TimeZoneRule r = (TimeZoneRule)historicRules.get(i);
              Date d = r.getNextStart(lastTransitionTime, curStdOffset, curDstSavings, false);
              if (d == null)
              {
                done.set(i);
              }
              else if ((r != curRule) && ((!r.getName().equals(curRule.getName())) || (r.getRawOffset() != curRule.getRawOffset()) || (r.getDSTSavings() != curRule.getDSTSavings())))
              {




                long tt = d.getTime();
                if (tt < nextTransitionTime) {
                  nextTransitionTime = tt;
                  nextRule = r;
                }
              }
            }
          }
          if (nextRule == null)
          {
            boolean bDoneAll = true;
            for (int j = 0; j < historicRules.size(); j++) {
              if (!done.get(j)) {
                bDoneAll = false;
                break;
              }
            }
            if (bDoneAll) {
              break;
            }
          }
          
          if (finalRules != null)
          {
            for (int i = 0; i < 2; i++) {
              if (finalRules[i] != curRule)
              {

                Date d = finalRules[i].getNextStart(lastTransitionTime, curStdOffset, curDstSavings, false);
                if (d != null) {
                  long tt = d.getTime();
                  if (tt < nextTransitionTime) {
                    nextTransitionTime = tt;
                    nextRule = finalRules[i];
                  }
                }
              }
            }
          }
          if (nextRule == null) {
            break;
          }
          

          if (historicTransitions == null) {
            historicTransitions = new ArrayList();
          }
          historicTransitions.add(new TimeZoneTransition(nextTransitionTime, curRule, nextRule));
          lastTransitionTime = nextTransitionTime;
          curRule = nextRule;
        }
      }
      if (finalRules != null) {
        if (historicTransitions == null) {
          historicTransitions = new ArrayList();
        }
        
        Date d0 = finalRules[0].getNextStart(lastTransitionTime, curRule.getRawOffset(), curRule.getDSTSavings(), false);
        Date d1 = finalRules[1].getNextStart(lastTransitionTime, curRule.getRawOffset(), curRule.getDSTSavings(), false);
        if (d1.after(d0)) {
          historicTransitions.add(new TimeZoneTransition(d0.getTime(), curRule, finalRules[0]));
          d1 = finalRules[1].getNextStart(d0.getTime(), finalRules[0].getRawOffset(), finalRules[0].getDSTSavings(), false);
          historicTransitions.add(new TimeZoneTransition(d1.getTime(), finalRules[0], finalRules[1]));
        } else {
          historicTransitions.add(new TimeZoneTransition(d1.getTime(), curRule, finalRules[1]));
          d0 = finalRules[0].getNextStart(d1.getTime(), finalRules[1].getRawOffset(), finalRules[1].getDSTSavings(), false);
          historicTransitions.add(new TimeZoneTransition(d0.getTime(), finalRules[1], finalRules[0]));
        }
      }
    }
    upToDate = true;
  }
  


  private void getOffset(long time, boolean local, int NonExistingTimeOpt, int DuplicatedTimeOpt, int[] offsets)
  {
    complete();
    TimeZoneRule rule = null;
    if (historicTransitions == null) {
      rule = initialRule;
    } else {
      long tstart = getTransitionTime((TimeZoneTransition)historicTransitions.get(0), local, NonExistingTimeOpt, DuplicatedTimeOpt);
      
      if (time < tstart) {
        rule = initialRule;
      } else {
        int idx = historicTransitions.size() - 1;
        long tend = getTransitionTime((TimeZoneTransition)historicTransitions.get(idx), local, NonExistingTimeOpt, DuplicatedTimeOpt);
        
        if (time > tend) {
          if (finalRules != null) {
            rule = findRuleInFinal(time, local, NonExistingTimeOpt, DuplicatedTimeOpt);
          }
          if (rule == null)
          {

            rule = ((TimeZoneTransition)historicTransitions.get(idx)).getTo();
          }
        }
        else {
          while ((idx >= 0) && 
            (time < getTransitionTime((TimeZoneTransition)historicTransitions.get(idx), local, NonExistingTimeOpt, DuplicatedTimeOpt)))
          {


            idx--;
          }
          rule = ((TimeZoneTransition)historicTransitions.get(idx)).getTo();
        }
      }
    }
    offsets[0] = rule.getRawOffset();
    offsets[1] = rule.getDSTSavings();
  }
  


  private TimeZoneRule findRuleInFinal(long time, boolean local, int NonExistingTimeOpt, int DuplicatedTimeOpt)
  {
    if ((finalRules == null) || (finalRules.length != 2) || (finalRules[0] == null) || (finalRules[1] == null)) {
      return null;
    }
    




    long base = time;
    if (local) {
      int localDelta = getLocalDelta(finalRules[1].getRawOffset(), finalRules[1].getDSTSavings(), finalRules[0].getRawOffset(), finalRules[0].getDSTSavings(), NonExistingTimeOpt, DuplicatedTimeOpt);
      

      base -= localDelta;
    }
    Date start0 = finalRules[0].getPreviousStart(base, finalRules[1].getRawOffset(), finalRules[1].getDSTSavings(), true);
    
    base = time;
    if (local) {
      int localDelta = getLocalDelta(finalRules[0].getRawOffset(), finalRules[0].getDSTSavings(), finalRules[1].getRawOffset(), finalRules[1].getDSTSavings(), NonExistingTimeOpt, DuplicatedTimeOpt);
      

      base -= localDelta;
    }
    Date start1 = finalRules[1].getPreviousStart(base, finalRules[0].getRawOffset(), finalRules[0].getDSTSavings(), true);
    
    if ((start0 == null) || (start1 == null)) {
      if (start0 != null)
        return finalRules[0];
      if (start1 != null) {
        return finalRules[1];
      }
      
      return null;
    }
    
    return start0.after(start1) ? finalRules[0] : finalRules[1];
  }
  



  private static long getTransitionTime(TimeZoneTransition tzt, boolean local, int NonExistingTimeOpt, int DuplicatedTimeOpt)
  {
    long time = tzt.getTime();
    if (local) {
      time += getLocalDelta(tzt.getFrom().getRawOffset(), tzt.getFrom().getDSTSavings(), tzt.getTo().getRawOffset(), tzt.getTo().getDSTSavings(), NonExistingTimeOpt, DuplicatedTimeOpt);
    }
    

    return time;
  }
  



  private static int getLocalDelta(int rawBefore, int dstBefore, int rawAfter, int dstAfter, int NonExistingTimeOpt, int DuplicatedTimeOpt)
  {
    int delta = 0;
    
    int offsetBefore = rawBefore + dstBefore;
    int offsetAfter = rawAfter + dstAfter;
    
    boolean dstToStd = (dstBefore != 0) && (dstAfter == 0);
    boolean stdToDst = (dstBefore == 0) && (dstAfter != 0);
    
    if (offsetAfter - offsetBefore >= 0)
    {
      if ((((NonExistingTimeOpt & 0x3) == 1) && (dstToStd)) || (((NonExistingTimeOpt & 0x3) == 3) && (stdToDst)))
      {
        delta = offsetBefore;
      } else if ((((NonExistingTimeOpt & 0x3) == 1) && (stdToDst)) || (((NonExistingTimeOpt & 0x3) == 3) && (dstToStd)))
      {
        delta = offsetAfter;
      } else if ((NonExistingTimeOpt & 0xC) == 12) {
        delta = offsetBefore;
      }
      else
      {
        delta = offsetAfter;
      }
      
    }
    else if ((((DuplicatedTimeOpt & 0x3) == 1) && (dstToStd)) || (((DuplicatedTimeOpt & 0x3) == 3) && (stdToDst)))
    {
      delta = offsetAfter;
    } else if ((((DuplicatedTimeOpt & 0x3) == 1) && (stdToDst)) || (((DuplicatedTimeOpt & 0x3) == 3) && (dstToStd)))
    {
      delta = offsetBefore;
    } else if ((DuplicatedTimeOpt & 0xC) == 4) {
      delta = offsetBefore;
    }
    else
    {
      delta = offsetAfter;
    }
    
    return delta;
  }
  

  private transient boolean isFrozen = false;
  



  public boolean isFrozen()
  {
    return isFrozen;
  }
  



  public TimeZone freeze()
  {
    complete();
    isFrozen = true;
    return this;
  }
  



  public TimeZone cloneAsThawed()
  {
    RuleBasedTimeZone tz = (RuleBasedTimeZone)super.cloneAsThawed();
    if (historicRules != null) {
      historicRules = new ArrayList(historicRules);
    }
    if (finalRules != null) {
      finalRules = ((AnnualTimeZoneRule[])finalRules.clone());
    }
    isFrozen = false;
    return tz;
  }
}
