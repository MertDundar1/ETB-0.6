package com.ibm.icu.impl;

import com.ibm.icu.util.AnnualTimeZoneRule;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.DateTimeRule;
import com.ibm.icu.util.InitialTimeZoneRule;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeArrayTimeZoneRule;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneRule;
import com.ibm.icu.util.TimeZoneTransition;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.MissingResourceException;






























































































public class OlsonTimeZone
  extends BasicTimeZone
{
  static final long serialVersionUID = -6281977362477515376L;
  private int transitionCount;
  private int typeCount;
  private long[] transitionTimes64;
  private int[] typeOffsets;
  private byte[] typeMapData;
  
  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds)
  {
    if ((month < 0) || (month > 11)) {
      throw new IllegalArgumentException("Month is not in the legal range: " + month);
    }
    return getOffset(era, year, month, day, dayOfWeek, milliseconds, Grego.monthLength(year, month));
  }
  




  public int getOffset(int era, int year, int month, int dom, int dow, int millis, int monthLength)
  {
    if (((era != 1) && (era != 0)) || (month < 0) || (month > 11) || (dom < 1) || (dom > monthLength) || (dow < 1) || (dow > 7) || (millis < 0) || (millis >= 86400000) || (monthLength < 28) || (monthLength > 31))
    {









      throw new IllegalArgumentException();
    }
    
    if (era == 0) {
      year = -year;
    }
    
    if ((finalZone != null) && (year >= finalStartYear)) {
      return finalZone.getOffset(era, year, month, dom, dow, millis);
    }
    

    long time = Grego.fieldsToDay(year, month, dom) * 86400000L + millis;
    
    int[] offsets = new int[2];
    getHistoricalOffset(time, true, 3, 1, offsets);
    return offsets[0] + offsets[1];
  }
  



  public void setRawOffset(int offsetMillis)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen OlsonTimeZone instance.");
    }
    
    if (getRawOffset() == offsetMillis) {
      return;
    }
    long current = System.currentTimeMillis();
    
    if (current < finalStartMillis) {
      SimpleTimeZone stz = new SimpleTimeZone(offsetMillis, getID());
      
      boolean bDst = useDaylightTime();
      if (bDst) {
        TimeZoneRule[] currentRules = getSimpleTimeZoneRulesNear(current);
        if (currentRules.length != 3)
        {



          TimeZoneTransition tzt = getPreviousTransition(current, false);
          if (tzt != null) {
            currentRules = getSimpleTimeZoneRulesNear(tzt.getTime() - 1L);
          }
        }
        if ((currentRules.length == 3) && ((currentRules[1] instanceof AnnualTimeZoneRule)) && ((currentRules[2] instanceof AnnualTimeZoneRule)))
        {


          AnnualTimeZoneRule r1 = (AnnualTimeZoneRule)currentRules[1];
          AnnualTimeZoneRule r2 = (AnnualTimeZoneRule)currentRules[2];
          
          int offset1 = r1.getRawOffset() + r1.getDSTSavings();
          int offset2 = r2.getRawOffset() + r2.getDSTSavings();
          int sav;
          DateTimeRule start; DateTimeRule end; int sav; if (offset1 > offset2) {
            DateTimeRule start = r1.getRule();
            DateTimeRule end = r2.getRule();
            sav = offset1 - offset2;
          } else {
            start = r2.getRule();
            end = r1.getRule();
            sav = offset2 - offset1;
          }
          
          stz.setStartRule(start.getRuleMonth(), start.getRuleWeekInMonth(), start.getRuleDayOfWeek(), start.getRuleMillisInDay());
          
          stz.setEndRule(end.getRuleMonth(), end.getRuleWeekInMonth(), end.getRuleDayOfWeek(), end.getRuleMillisInDay());
          

          stz.setDSTSavings(sav);


        }
        else
        {


          stz.setStartRule(0, 1, 0);
          stz.setEndRule(11, 31, 86399999);
        }
      }
      
      int[] fields = Grego.timeToFields(current, null);
      
      finalStartYear = fields[0];
      finalStartMillis = Grego.fieldsToDay(fields[0], 0, 1);
      
      if (bDst)
      {

        stz.setStartYear(finalStartYear);
      }
      
      finalZone = stz;
    }
    else {
      finalZone.setRawOffset(offsetMillis);
    }
    
    transitionRulesInitialized = false;
  }
  
  public Object clone()
  {
    if (isFrozen()) {
      return this;
    }
    return cloneAsThawed();
  }
  



  public void getOffset(long date, boolean local, int[] offsets)
  {
    if ((finalZone != null) && (date >= finalStartMillis)) {
      finalZone.getOffset(date, local, offsets);
    } else {
      getHistoricalOffset(date, local, 4, 12, offsets);
    }
  }
  




  /**
   * @deprecated
   */
  public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets)
  {
    if ((finalZone != null) && (date >= finalStartMillis)) {
      finalZone.getOffsetFromLocal(date, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
    } else {
      getHistoricalOffset(date, true, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
    }
  }
  



  public int getRawOffset()
  {
    int[] ret = new int[2];
    getOffset(System.currentTimeMillis(), false, ret);
    return ret[0];
  }
  








  public boolean useDaylightTime()
  {
    long current = System.currentTimeMillis();
    
    if ((finalZone != null) && (current >= finalStartMillis)) {
      return (finalZone != null) && (finalZone.useDaylightTime());
    }
    
    int[] fields = Grego.timeToFields(current, null);
    

    long start = Grego.fieldsToDay(fields[0], 0, 1) * 86400L;
    long limit = Grego.fieldsToDay(fields[0] + 1, 0, 1) * 86400L;
    


    for (int i = 0; i < transitionCount; i++) {
      if (transitionTimes64[i] >= limit) {
        break;
      }
      if (((transitionTimes64[i] >= start) && (dstOffsetAt(i) != 0)) || ((transitionTimes64[i] > start) && (i > 0) && (dstOffsetAt(i - 1) != 0)))
      {
        return true;
      }
    }
    return false;
  }
  



  public boolean observesDaylightTime()
  {
    long current = System.currentTimeMillis();
    
    if (finalZone != null) {
      if (finalZone.useDaylightTime())
        return true;
      if (current >= finalStartMillis) {
        return false;
      }
    }
    

    long currentSec = Grego.floorDivide(current, 1000L);
    int trsIdx = transitionCount - 1;
    if (dstOffsetAt(trsIdx) != 0) {
      return true;
    }
    while ((trsIdx >= 0) && 
      (transitionTimes64[trsIdx] > currentSec))
    {

      if (dstOffsetAt(trsIdx - 1) != 0) {
        return true;
      }
    }
    return false;
  }
  




  public int getDSTSavings()
  {
    if (finalZone != null) {
      return finalZone.getDSTSavings();
    }
    return super.getDSTSavings();
  }
  



  public boolean inDaylightTime(Date date)
  {
    int[] temp = new int[2];
    getOffset(date.getTime(), false, temp);
    return temp[1] != 0;
  }
  



  public boolean hasSameRules(TimeZone other)
  {
    if (this == other) {
      return true;
    }
    

    if (!super.hasSameRules(other)) {
      return false;
    }
    
    if (!(other instanceof OlsonTimeZone))
    {
      return false;
    }
    

    OlsonTimeZone o = (OlsonTimeZone)other;
    if (finalZone == null) {
      if (finalZone != null) {
        return false;
      }
    }
    else if ((finalZone == null) || (finalStartYear != finalStartYear) || (!finalZone.hasSameRules(finalZone)))
    {

      return false;
    }
    



    if ((transitionCount != transitionCount) || (!Arrays.equals(transitionTimes64, transitionTimes64)) || (typeCount != typeCount) || (!Arrays.equals(typeMapData, typeMapData)) || (!Arrays.equals(typeOffsets, typeOffsets)))
    {



      return false;
    }
    return true;
  }
  


  public String getCanonicalID()
  {
    if (canonicalID == null) {
      synchronized (this) {
        if (canonicalID == null) {
          canonicalID = getCanonicalID(getID());
          
          assert (canonicalID != null);
          if (canonicalID == null)
          {
            canonicalID = getID();
          }
        }
      }
    }
    return canonicalID;
  }
  



  private void constructEmpty()
  {
    transitionCount = 0;
    transitionTimes64 = null;
    typeMapData = null;
    
    typeCount = 1;
    typeOffsets = new int[] { 0, 0 };
    finalZone = null;
    finalStartYear = Integer.MAX_VALUE;
    finalStartMillis = Double.MAX_VALUE;
    
    transitionRulesInitialized = false;
  }
  






  public OlsonTimeZone(UResourceBundle top, UResourceBundle res, String id)
  {
    super(id);
    construct(top, res);
  }
  
  private void construct(UResourceBundle top, UResourceBundle res)
  {
    if ((top == null) || (res == null)) {
      throw new IllegalArgumentException();
    }
    if (DEBUG) { System.out.println("OlsonTimeZone(" + res.getKey() + ")");
    }
    int[] transPost32;
    int[] trans32;
    int[] transPre32 = trans32 = transPost32 = null;
    
    transitionCount = 0;
    
    try
    {
      r = res.get("transPre32");
      transPre32 = r.getIntVector();
      if (transPre32.length % 2 != 0)
      {
        throw new IllegalArgumentException("Invalid Format");
      }
      transitionCount += transPre32.length / 2;
    }
    catch (MissingResourceException e) {}
    

    try
    {
      r = res.get("trans");
      trans32 = r.getIntVector();
      transitionCount += trans32.length;
    }
    catch (MissingResourceException e) {}
    

    try
    {
      r = res.get("transPost32");
      transPost32 = r.getIntVector();
      if (transPost32.length % 2 != 0)
      {
        throw new IllegalArgumentException("Invalid Format");
      }
      transitionCount += transPost32.length / 2;
    }
    catch (MissingResourceException e) {}
    

    if (transitionCount > 0) {
      transitionTimes64 = new long[transitionCount];
      int idx = 0;
      if (transPre32 != null) {
        for (int i = 0; i < transPre32.length / 2; idx++) {
          transitionTimes64[idx] = ((transPre32[(i * 2)] & 0xFFFFFFFF) << 32 | transPre32[(i * 2 + 1)] & 0xFFFFFFFF);i++;
        }
      }
      

      if (trans32 != null) {
        for (int i = 0; i < trans32.length; idx++) {
          transitionTimes64[idx] = trans32[i];i++;
        }
      }
      if (transPost32 != null) {
        for (int i = 0; i < transPost32.length / 2; idx++) {
          transitionTimes64[idx] = ((transPost32[(i * 2)] & 0xFFFFFFFF) << 32 | transPost32[(i * 2 + 1)] & 0xFFFFFFFF);i++;
        }
      }
    }
    else
    {
      transitionTimes64 = null;
    }
    

    UResourceBundle r = res.get("typeOffsets");
    typeOffsets = r.getIntVector();
    if ((typeOffsets.length < 2) || (typeOffsets.length > 32766) || (typeOffsets.length % 2 != 0)) {
      throw new IllegalArgumentException("Invalid Format");
    }
    typeCount = (typeOffsets.length / 2);
    

    if (transitionCount > 0) {
      r = res.get("typeMap");
      typeMapData = r.getBinary(null);
      if (typeMapData.length != transitionCount) {
        throw new IllegalArgumentException("Invalid Format");
      }
    } else {
      typeMapData = null;
    }
    

    finalZone = null;
    finalStartYear = Integer.MAX_VALUE;
    finalStartMillis = Double.MAX_VALUE;
    
    String ruleID = null;
    try {
      ruleID = res.getString("finalRule");
      
      r = res.get("finalRaw");
      int ruleRaw = r.getInt() * 1000;
      r = loadRule(top, ruleID);
      int[] ruleData = r.getIntVector();
      
      if ((ruleData == null) || (ruleData.length != 11)) {
        throw new IllegalArgumentException("Invalid Format");
      }
      finalZone = new SimpleTimeZone(ruleRaw, "", ruleData[0], ruleData[1], ruleData[2], ruleData[3] * 1000, ruleData[4], ruleData[5], ruleData[6], ruleData[7], ruleData[8] * 1000, ruleData[9], ruleData[10] * 1000);
      







      r = res.get("finalYear");
      finalStartYear = r.getInt();
      
















      finalStartMillis = (Grego.fieldsToDay(finalStartYear, 0, 1) * 86400000L);
    } catch (MissingResourceException e) {
      if (ruleID != null)
      {

        throw new IllegalArgumentException("Invalid Format");
      }
    }
  }
  
  public OlsonTimeZone(String id)
  {
    super(id);
    UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
    
    UResourceBundle res = ZoneMeta.openOlsonResource(top, id);
    construct(top, res);
    if (finalZone != null) {
      finalZone.setID(id);
    }
  }
  



  public void setID(String id)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen OlsonTimeZone instance.");
    }
    

    if (canonicalID == null) {
      canonicalID = getCanonicalID(getID());
      assert (canonicalID != null);
      if (canonicalID == null)
      {
        canonicalID = getID();
      }
    }
    
    if (finalZone != null) {
      finalZone.setID(id);
    }
    super.setID(id);
    transitionRulesInitialized = false;
  }
  
  private void getHistoricalOffset(long date, boolean local, int NonExistingTimeOpt, int DuplicatedTimeOpt, int[] offsets)
  {
    if (transitionCount != 0) {
      long sec = Grego.floorDivide(date, 1000L);
      if ((!local) && (sec < transitionTimes64[0]))
      {
        offsets[0] = (initialRawOffset() * 1000);
        offsets[1] = (initialDstOffset() * 1000);

      }
      else
      {
        for (int transIdx = transitionCount - 1; transIdx >= 0; transIdx--) {
          long transition = transitionTimes64[transIdx];
          if (local) {
            int offsetBefore = zoneOffsetAt(transIdx - 1);
            boolean dstBefore = dstOffsetAt(transIdx - 1) != 0;
            
            int offsetAfter = zoneOffsetAt(transIdx);
            boolean dstAfter = dstOffsetAt(transIdx) != 0;
            
            boolean dstToStd = (dstBefore) && (!dstAfter);
            boolean stdToDst = (!dstBefore) && (dstAfter);
            
            if (offsetAfter - offsetBefore >= 0)
            {
              if ((((NonExistingTimeOpt & 0x3) == 1) && (dstToStd)) || (((NonExistingTimeOpt & 0x3) == 3) && (stdToDst)))
              {
                transition += offsetBefore;
              } else if ((((NonExistingTimeOpt & 0x3) == 1) && (stdToDst)) || (((NonExistingTimeOpt & 0x3) == 3) && (dstToStd)))
              {
                transition += offsetAfter;
              } else if ((NonExistingTimeOpt & 0xC) == 12) {
                transition += offsetBefore;
              }
              else
              {
                transition += offsetAfter;
              }
              
            }
            else if ((((DuplicatedTimeOpt & 0x3) == 1) && (dstToStd)) || (((DuplicatedTimeOpt & 0x3) == 3) && (stdToDst)))
            {
              transition += offsetAfter;
            } else if ((((DuplicatedTimeOpt & 0x3) == 1) && (stdToDst)) || (((DuplicatedTimeOpt & 0x3) == 3) && (dstToStd)))
            {
              transition += offsetBefore;
            } else if ((DuplicatedTimeOpt & 0xC) == 4) {
              transition += offsetBefore;
            }
            else
            {
              transition += offsetAfter;
            }
          }
          
          if (sec >= transition) {
            break;
          }
        }
        
        offsets[0] = (rawOffsetAt(transIdx) * 1000);
        offsets[1] = (dstOffsetAt(transIdx) * 1000);
      }
    }
    else {
      offsets[0] = (initialRawOffset() * 1000);
      offsets[1] = (initialDstOffset() * 1000);
    }
  }
  
  private int getInt(byte val) {
    return val & 0xFF;
  }
  



  private int zoneOffsetAt(int transIdx)
  {
    int typeIdx = transIdx >= 0 ? getInt(typeMapData[transIdx]) * 2 : 0;
    return typeOffsets[typeIdx] + typeOffsets[(typeIdx + 1)];
  }
  
  private int rawOffsetAt(int transIdx) {
    int typeIdx = transIdx >= 0 ? getInt(typeMapData[transIdx]) * 2 : 0;
    return typeOffsets[typeIdx];
  }
  
  private int dstOffsetAt(int transIdx) {
    int typeIdx = transIdx >= 0 ? getInt(typeMapData[transIdx]) * 2 : 0;
    return typeOffsets[(typeIdx + 1)];
  }
  
  private int initialRawOffset() {
    return typeOffsets[0];
  }
  
  private int initialDstOffset() {
    return typeOffsets[1];
  }
  

  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append(super.toString());
    buf.append('[');
    buf.append("transitionCount=" + transitionCount);
    buf.append(",typeCount=" + typeCount);
    buf.append(",transitionTimes=");
    if (transitionTimes64 != null) {
      buf.append('[');
      for (int i = 0; i < transitionTimes64.length; i++) {
        if (i > 0) {
          buf.append(',');
        }
        buf.append(Long.toString(transitionTimes64[i]));
      }
      buf.append(']');
    } else {
      buf.append("null");
    }
    buf.append(",typeOffsets=");
    if (typeOffsets != null) {
      buf.append('[');
      for (int i = 0; i < typeOffsets.length; i++) {
        if (i > 0) {
          buf.append(',');
        }
        buf.append(Integer.toString(typeOffsets[i]));
      }
      buf.append(']');
    } else {
      buf.append("null");
    }
    buf.append(",typeMapData=");
    if (typeMapData != null) {
      buf.append('[');
      for (int i = 0; i < typeMapData.length; i++) {
        if (i > 0) {
          buf.append(',');
        }
        buf.append(Byte.toString(typeMapData[i]));
      }
    } else {
      buf.append("null");
    }
    buf.append(",finalStartYear=" + finalStartYear);
    buf.append(",finalStartMillis=" + finalStartMillis);
    buf.append(",finalZone=" + finalZone);
    buf.append(']');
    
    return buf.toString();
  }
  































  private int finalStartYear = Integer.MAX_VALUE;
  



  private double finalStartMillis = Double.MAX_VALUE;
  




  private SimpleTimeZone finalZone = null;
  




  private volatile String canonicalID = null;
  
  private static final String ZONEINFORES = "zoneinfo64";
  
  private static final boolean DEBUG = ICUDebug.enabled("olson");
  private static final int SECONDS_PER_DAY = 86400;
  private transient InitialTimeZoneRule initialRule;
  
  private static UResourceBundle loadRule(UResourceBundle top, String ruleid) { UResourceBundle r = top.get("Rules");
    r = r.get(ruleid);
    return r;
  }
  
  public boolean equals(Object obj)
  {
    if (!super.equals(obj)) { return false;
    }
    OlsonTimeZone z = (OlsonTimeZone)obj;
    
    return (Utility.arrayEquals(typeMapData, typeMapData)) || ((finalStartYear == finalStartYear) && (((finalZone == null) && (finalZone == null)) || ((finalZone != null) && (finalZone != null) && (finalZone.equals(finalZone)) && (transitionCount == transitionCount) && (typeCount == typeCount) && (Utility.arrayEquals(transitionTimes64, transitionTimes64)) && (Utility.arrayEquals(typeOffsets, typeOffsets)) && (Utility.arrayEquals(typeMapData, typeMapData)))));
  }
  

  private transient TimeZoneTransition firstTZTransition;
  
  private transient int firstTZTransitionIdx;
  
  private transient TimeZoneTransition firstFinalTZTransition;
  
  private transient TimeArrayTimeZoneRule[] historicRules;
  
  private transient SimpleTimeZone finalZoneWithStartYear;
  
  private transient boolean transitionRulesInitialized;
  
  private static final int currentSerialVersion = 1;
  public int hashCode()
  {
    int ret = (int)(finalStartYear ^ (finalStartYear >>> 4) + transitionCount ^ (transitionCount >>> 6) + typeCount ^ (typeCount >>> 8) + Double.doubleToLongBits(finalStartMillis) + (finalZone == null ? 0 : finalZone.hashCode()) + super.hashCode());
    




    if (transitionTimes64 != null) {
      for (int i = 0; i < transitionTimes64.length; i++) {
        ret = (int)(ret + (transitionTimes64[i] ^ transitionTimes64[i] >>> 8));
      }
    }
    for (int i = 0; i < typeOffsets.length; i++) {
      ret += (typeOffsets[i] ^ typeOffsets[i] >>> 8);
    }
    if (typeMapData != null) {
      for (int i = 0; i < typeMapData.length; i++) {
        ret += (typeMapData[i] & 0xFF);
      }
    }
    return ret;
  }
  







  public TimeZoneTransition getNextTransition(long base, boolean inclusive)
  {
    initTransitionRules();
    
    if (finalZone != null) {
      if ((inclusive) && (base == firstFinalTZTransition.getTime()))
        return firstFinalTZTransition;
      if (base >= firstFinalTZTransition.getTime()) {
        if (finalZone.useDaylightTime())
        {
          return finalZoneWithStartYear.getNextTransition(base, inclusive);
        }
        
        return null;
      }
    }
    
    if (historicRules != null)
    {
      for (int ttidx = transitionCount - 1; 
          ttidx >= firstTZTransitionIdx; ttidx--) {
        long t = transitionTimes64[ttidx] * 1000L;
        if ((base > t) || ((!inclusive) && (base == t))) {
          break;
        }
      }
      if (ttidx == transitionCount - 1)
        return firstFinalTZTransition;
      if (ttidx < firstTZTransitionIdx) {
        return firstTZTransition;
      }
      
      TimeZoneRule to = historicRules[getInt(typeMapData[(ttidx + 1)])];
      TimeZoneRule from = historicRules[getInt(typeMapData[ttidx])];
      long startTime = transitionTimes64[(ttidx + 1)] * 1000L;
      

      if ((from.getName().equals(to.getName())) && (from.getRawOffset() == to.getRawOffset()) && (from.getDSTSavings() == to.getDSTSavings()))
      {
        return getNextTransition(startTime, false);
      }
      
      return new TimeZoneTransition(startTime, from, to);
    }
    
    return null;
  }
  



  public TimeZoneTransition getPreviousTransition(long base, boolean inclusive)
  {
    initTransitionRules();
    
    if (finalZone != null) {
      if ((inclusive) && (base == firstFinalTZTransition.getTime()))
        return firstFinalTZTransition;
      if (base > firstFinalTZTransition.getTime()) {
        if (finalZone.useDaylightTime())
        {
          return finalZoneWithStartYear.getPreviousTransition(base, inclusive);
        }
        return firstFinalTZTransition;
      }
    }
    

    if (historicRules != null)
    {
      for (int ttidx = transitionCount - 1; 
          ttidx >= firstTZTransitionIdx; ttidx--) {
        long t = transitionTimes64[ttidx] * 1000L;
        if ((base > t) || ((inclusive) && (base == t))) {
          break;
        }
      }
      if (ttidx < firstTZTransitionIdx)
      {
        return null; }
      if (ttidx == firstTZTransitionIdx) {
        return firstTZTransition;
      }
      
      TimeZoneRule to = historicRules[getInt(typeMapData[ttidx])];
      TimeZoneRule from = historicRules[getInt(typeMapData[(ttidx - 1)])];
      long startTime = transitionTimes64[ttidx] * 1000L;
      

      if ((from.getName().equals(to.getName())) && (from.getRawOffset() == to.getRawOffset()) && (from.getDSTSavings() == to.getDSTSavings()))
      {
        return getPreviousTransition(startTime, false);
      }
      
      return new TimeZoneTransition(startTime, from, to);
    }
    
    return null;
  }
  



  public TimeZoneRule[] getTimeZoneRules()
  {
    initTransitionRules();
    int size = 1;
    if (historicRules != null)
    {

      for (int i = 0; i < historicRules.length; i++) {
        if (historicRules[i] != null) {
          size++;
        }
      }
    }
    if (finalZone != null) {
      if (finalZone.useDaylightTime()) {
        size += 2;
      } else {
        size++;
      }
    }
    
    TimeZoneRule[] rules = new TimeZoneRule[size];
    int idx = 0;
    rules[(idx++)] = initialRule;
    
    if (historicRules != null) {
      for (int i = 0; i < historicRules.length; i++) {
        if (historicRules[i] != null) {
          rules[(idx++)] = historicRules[i];
        }
      }
    }
    
    if (finalZone != null) {
      if (finalZone.useDaylightTime()) {
        TimeZoneRule[] stzr = finalZoneWithStartYear.getTimeZoneRules();
        
        rules[(idx++)] = stzr[1];
        rules[(idx++)] = stzr[2];
      }
      else {
        rules[(idx++)] = new TimeArrayTimeZoneRule(getID() + "(STD)", finalZone.getRawOffset(), 0, new long[] { finalStartMillis }, 2);
      }
    }
    
    return rules;
  }
  








  private synchronized void initTransitionRules()
  {
    if (transitionRulesInitialized) {
      return;
    }
    
    initialRule = null;
    firstTZTransition = null;
    firstFinalTZTransition = null;
    historicRules = null;
    firstTZTransitionIdx = 0;
    finalZoneWithStartYear = null;
    
    String stdName = getID() + "(STD)";
    String dstName = getID() + "(DST)";
    



    int raw = initialRawOffset() * 1000;
    int dst = initialDstOffset() * 1000;
    initialRule = new InitialTimeZoneRule(dst == 0 ? stdName : dstName, raw, dst);
    
    if (transitionCount > 0)
    {




      for (int transitionIdx = 0; transitionIdx < transitionCount; transitionIdx++) {
        if (getInt(typeMapData[transitionIdx]) != 0) {
          break;
        }
        firstTZTransitionIdx += 1;
      }
      if (transitionIdx != transitionCount)
      {


        long[] times = new long[transitionCount];
        for (int typeIdx = 0; typeIdx < typeCount; typeIdx++)
        {
          int nTimes = 0;
          for (transitionIdx = firstTZTransitionIdx; transitionIdx < transitionCount; transitionIdx++) {
            if (typeIdx == getInt(typeMapData[transitionIdx])) {
              long tt = transitionTimes64[transitionIdx] * 1000L;
              if (tt < finalStartMillis)
              {
                times[(nTimes++)] = tt;
              }
            }
          }
          if (nTimes > 0) {
            long[] startTimes = new long[nTimes];
            System.arraycopy(times, 0, startTimes, 0, nTimes);
            
            raw = typeOffsets[(typeIdx * 2)] * 1000;
            dst = typeOffsets[(typeIdx * 2 + 1)] * 1000;
            if (historicRules == null) {
              historicRules = new TimeArrayTimeZoneRule[typeCount];
            }
            historicRules[typeIdx] = new TimeArrayTimeZoneRule(dst == 0 ? stdName : dstName, raw, dst, startTimes, 2);
          }
        }
        


        typeIdx = getInt(typeMapData[firstTZTransitionIdx]);
        firstTZTransition = new TimeZoneTransition(transitionTimes64[firstTZTransitionIdx] * 1000L, initialRule, historicRules[typeIdx]);
      }
    }
    


    if (finalZone != null)
    {
      long startTime = finalStartMillis;
      TimeZoneRule firstFinalRule;
      if (finalZone.useDaylightTime())
      {







        finalZoneWithStartYear = ((SimpleTimeZone)finalZone.clone());
        finalZoneWithStartYear.setStartYear(finalStartYear);
        
        TimeZoneTransition tzt = finalZoneWithStartYear.getNextTransition(startTime, false);
        TimeZoneRule firstFinalRule = tzt.getTo();
        startTime = tzt.getTime();
      } else {
        finalZoneWithStartYear = finalZone;
        firstFinalRule = new TimeArrayTimeZoneRule(finalZone.getID(), finalZone.getRawOffset(), 0, new long[] { startTime }, 2);
      }
      
      TimeZoneRule prevRule = null;
      if (transitionCount > 0) {
        prevRule = historicRules[getInt(typeMapData[(transitionCount - 1)])];
      }
      if (prevRule == null)
      {
        prevRule = initialRule;
      }
      firstFinalTZTransition = new TimeZoneTransition(startTime, prevRule, firstFinalRule);
    }
    
    transitionRulesInitialized = true;
  }
  











  private int serialVersionOnStream = 1;
  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    
    if (serialVersionOnStream < 1)
    {

      boolean initialized = false;
      String tzid = getID();
      if (tzid != null) {
        try {
          UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
          
          UResourceBundle res = ZoneMeta.openOlsonResource(top, tzid);
          construct(top, res);
          if (finalZone != null) {
            finalZone.setID(tzid);
          }
          initialized = true;
        }
        catch (Exception e) {}
      }
      
      if (!initialized)
      {
        constructEmpty();
      }
    }
    

    transitionRulesInitialized = false;
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
    OlsonTimeZone tz = (OlsonTimeZone)super.cloneAsThawed();
    if (finalZone != null)
    {
      finalZone.setID(getID());
      finalZone = ((SimpleTimeZone)finalZone.clone());
    }
    







    isFrozen = false;
    return tz;
  }
}
