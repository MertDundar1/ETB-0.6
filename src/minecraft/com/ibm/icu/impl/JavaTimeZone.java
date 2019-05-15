package com.ibm.icu.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeSet;
























public class JavaTimeZone
  extends com.ibm.icu.util.TimeZone
{
  private static final long serialVersionUID = 6977448185543929364L;
  private static final TreeSet<String> AVAILABLESET = new TreeSet();
  static { String[] availableIds = java.util.TimeZone.getAvailableIDs();
    for (int i = 0; i < availableIds.length; i++) {
      AVAILABLESET.add(availableIds[i]);
    }
    try
    {
      mObservesDaylightTime = java.util.TimeZone.class.getMethod("observesDaylightTime", (Class[])null);
    }
    catch (NoSuchMethodException e) {}catch (SecurityException e) {}
  }
  

  private java.util.TimeZone javatz;
  
  private transient Calendar javacal;
  private static Method mObservesDaylightTime;
  public JavaTimeZone()
  {
    this(java.util.TimeZone.getDefault(), null);
  }
  





  public JavaTimeZone(java.util.TimeZone jtz, String id)
  {
    if (id == null) {
      id = jtz.getID();
    }
    javatz = jtz;
    setID(id);
    javacal = new GregorianCalendar(javatz);
  }
  





  public static JavaTimeZone createTimeZone(String id)
  {
    java.util.TimeZone jtz = null;
    
    if (AVAILABLESET.contains(id)) {
      jtz = java.util.TimeZone.getTimeZone(id);
    }
    
    if (jtz == null)
    {
      boolean[] isSystemID = new boolean[1];
      String canonicalID = com.ibm.icu.util.TimeZone.getCanonicalID(id, isSystemID);
      if ((isSystemID[0] != 0) && (AVAILABLESET.contains(canonicalID))) {
        jtz = java.util.TimeZone.getTimeZone(canonicalID);
      }
    }
    
    if (jtz == null) {
      return null;
    }
    
    return new JavaTimeZone(jtz, id);
  }
  


  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds)
  {
    return javatz.getOffset(era, year, month, day, dayOfWeek, milliseconds);
  }
  


  public void getOffset(long date, boolean local, int[] offsets)
  {
    synchronized (javacal) {
      if (local) {
        int[] fields = new int[6];
        Grego.timeToFields(date, fields);
        
        int tmp = fields[5];
        int mil = tmp % 1000;
        tmp /= 1000;
        int sec = tmp % 60;
        tmp /= 60;
        int min = tmp % 60;
        int hour = tmp / 60;
        javacal.clear();
        javacal.set(fields[0], fields[1], fields[2], hour, min, sec);
        javacal.set(14, mil);
        

        int doy1 = javacal.get(6);
        int hour1 = javacal.get(11);
        int min1 = javacal.get(12);
        int sec1 = javacal.get(13);
        int mil1 = javacal.get(14);
        
        if ((fields[4] != doy1) || (hour != hour1) || (min != min1) || (sec != sec1) || (mil != mil1))
        {


          int dayDelta = Math.abs(doy1 - fields[4]) > 1 ? 1 : doy1 - fields[4];
          int delta = (((dayDelta * 24 + hour1 - hour) * 60 + min1 - min) * 60 + sec1 - sec) * 1000 + mil1 - mil;
          

          javacal.setTimeInMillis(javacal.getTimeInMillis() - delta - 1L);
        }
      } else {
        javacal.setTimeInMillis(date);
      }
      offsets[0] = javacal.get(15);
      offsets[1] = javacal.get(16);
    }
  }
  


  public int getRawOffset()
  {
    return javatz.getRawOffset();
  }
  


  public boolean inDaylightTime(Date date)
  {
    return javatz.inDaylightTime(date);
  }
  


  public void setRawOffset(int offsetMillis)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen JavaTimeZone instance.");
    }
    javatz.setRawOffset(offsetMillis);
  }
  


  public boolean useDaylightTime()
  {
    return javatz.useDaylightTime();
  }
  


  public boolean observesDaylightTime()
  {
    if (mObservesDaylightTime != null) {
      try
      {
        return ((Boolean)mObservesDaylightTime.invoke(javatz, (Object[])null)).booleanValue();
      }
      catch (IllegalAccessException e) {}catch (IllegalArgumentException e) {}catch (InvocationTargetException e) {}
    }
    

    return super.observesDaylightTime();
  }
  


  public int getDSTSavings()
  {
    return javatz.getDSTSavings();
  }
  
  public java.util.TimeZone unwrap() {
    return javatz;
  }
  


  public Object clone()
  {
    if (isFrozen()) {
      return this;
    }
    return cloneAsThawed();
  }
  


  public int hashCode()
  {
    return super.hashCode() + javatz.hashCode();
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    javacal = new GregorianCalendar(javatz);
  }
  

  private transient boolean isFrozen = false;
  


  public boolean isFrozen()
  {
    return isFrozen;
  }
  


  public com.ibm.icu.util.TimeZone freeze()
  {
    isFrozen = true;
    return this;
  }
  


  public com.ibm.icu.util.TimeZone cloneAsThawed()
  {
    JavaTimeZone tz = (JavaTimeZone)super.cloneAsThawed();
    javatz = ((java.util.TimeZone)javatz.clone());
    javacal = ((GregorianCalendar)javacal.clone());
    isFrozen = false;
    return tz;
  }
}
