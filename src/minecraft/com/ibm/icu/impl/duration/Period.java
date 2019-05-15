package com.ibm.icu.impl.duration;









public final class Period
{
  final byte timeLimit;
  






  final boolean inFuture;
  






  final int[] counts;
  







  public static Period at(float count, TimeUnit unit)
  {
    checkCount(count);
    return new Period(0, false, count, unit);
  }
  






  public static Period moreThan(float count, TimeUnit unit)
  {
    checkCount(count);
    return new Period(2, false, count, unit);
  }
  






  public static Period lessThan(float count, TimeUnit unit)
  {
    checkCount(count);
    return new Period(1, false, count, unit);
  }
  










  public Period and(float count, TimeUnit unit)
  {
    checkCount(count);
    return setTimeUnitValue(unit, count);
  }
  





  public Period omit(TimeUnit unit)
  {
    return setTimeUnitInternalValue(unit, 0);
  }
  




  public Period at()
  {
    return setTimeLimit((byte)0);
  }
  




  public Period moreThan()
  {
    return setTimeLimit((byte)2);
  }
  




  public Period lessThan()
  {
    return setTimeLimit((byte)1);
  }
  




  public Period inFuture()
  {
    return setFuture(true);
  }
  




  public Period inPast()
  {
    return setFuture(false);
  }
  






  public Period inFuture(boolean future)
  {
    return setFuture(future);
  }
  






  public Period inPast(boolean past)
  {
    return setFuture(!past);
  }
  



  public boolean isSet()
  {
    for (int i = 0; i < counts.length; i++) {
      if (counts[i] != 0) {
        return true;
      }
    }
    return false;
  }
  




  public boolean isSet(TimeUnit unit)
  {
    return counts[ordinal] > 0;
  }
  





  public float getCount(TimeUnit unit)
  {
    int ord = ordinal;
    if (counts[ord] == 0) {
      return 0.0F;
    }
    return (counts[ord] - 1) / 1000.0F;
  }
  





  public boolean isInFuture()
  {
    return inFuture;
  }
  





  public boolean isInPast()
  {
    return !inFuture;
  }
  





  public boolean isMoreThan()
  {
    return timeLimit == 2;
  }
  





  public boolean isLessThan()
  {
    return timeLimit == 1;
  }
  




  public boolean equals(Object rhs)
  {
    try
    {
      return equals((Period)rhs);
    }
    catch (ClassCastException e) {}
    return false;
  }
  










  public boolean equals(Period rhs)
  {
    if ((rhs != null) && (timeLimit == timeLimit) && (inFuture == inFuture))
    {

      for (int i = 0; i < counts.length; i++) {
        if (counts[i] != counts[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  



  public int hashCode()
  {
    int hc = timeLimit << 1 | (inFuture ? 1 : 0);
    for (int i = 0; i < counts.length; i++) {
      hc = hc << 2 ^ counts[i];
    }
    return hc;
  }
  


  private Period(int limit, boolean future, float count, TimeUnit unit)
  {
    timeLimit = ((byte)limit);
    inFuture = future;
    counts = new int[TimeUnit.units.length];
    counts[ordinal] = ((int)(count * 1000.0F) + 1);
  }
  


  Period(int timeLimit, boolean inFuture, int[] counts)
  {
    this.timeLimit = ((byte)timeLimit);
    this.inFuture = inFuture;
    this.counts = counts;
  }
  


  private Period setTimeUnitValue(TimeUnit unit, float value)
  {
    if (value < 0.0F) {
      throw new IllegalArgumentException("value: " + value);
    }
    return setTimeUnitInternalValue(unit, (int)(value * 1000.0F) + 1);
  }
  







  private Period setTimeUnitInternalValue(TimeUnit unit, int value)
  {
    int ord = ordinal;
    if (counts[ord] != value) {
      int[] newCounts = new int[counts.length];
      for (int i = 0; i < counts.length; i++) {
        newCounts[i] = counts[i];
      }
      newCounts[ord] = value;
      return new Period(timeLimit, inFuture, newCounts);
    }
    return this;
  }
  




  private Period setFuture(boolean future)
  {
    if (inFuture != future) {
      return new Period(timeLimit, future, counts);
    }
    return this;
  }
  





  private Period setTimeLimit(byte limit)
  {
    if (timeLimit != limit) {
      return new Period(limit, inFuture, counts);
    }
    
    return this;
  }
  


  private static void checkCount(float count)
  {
    if (count < 0.0F) {
      throw new IllegalArgumentException("count (" + count + ") cannot be negative");
    }
  }
}
