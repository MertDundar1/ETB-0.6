package com.ibm.icu.util;













public abstract class Measure
{
  private Number number;
  










  private MeasureUnit unit;
  











  protected Measure(Number number, MeasureUnit unit)
  {
    if ((number == null) || (unit == null)) {
      throw new NullPointerException();
    }
    this.number = number;
    this.unit = unit;
  }
  




  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (obj == this) return true;
    try {
      Measure m = (Measure)obj;
      return (unit.equals(unit)) && (numbersEqual(number, number));
    } catch (ClassCastException e) {}
    return false;
  }
  







  private static boolean numbersEqual(Number a, Number b)
  {
    if (a.equals(b)) {
      return true;
    }
    if (a.doubleValue() == b.doubleValue()) {
      return true;
    }
    return false;
  }
  




  public int hashCode()
  {
    return number.hashCode() ^ unit.hashCode();
  }
  





  public String toString()
  {
    return number.toString() + ' ' + unit.toString();
  }
  




  public Number getNumber()
  {
    return number;
  }
  




  public MeasureUnit getUnit()
  {
    return unit;
  }
}
