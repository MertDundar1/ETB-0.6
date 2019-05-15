package com.ibm.icu.impl;

import com.ibm.icu.util.Freezable;










public class Row<C0, C1, C2, C3, C4>
  implements Comparable, Cloneable, Freezable<Row<C0, C1, C2, C3, C4>>
{
  protected Object[] items;
  protected boolean frozen;
  
  public Row() {}
  
  public static <C0, C1> R2<C0, C1> of(C0 p0, C1 p1)
  {
    return new R2(p0, p1);
  }
  
  public static <C0, C1, C2> R3<C0, C1, C2> of(C0 p0, C1 p1, C2 p2) { return new R3(p0, p1, p2); }
  
  public static <C0, C1, C2, C3> R4<C0, C1, C2, C3> of(C0 p0, C1 p1, C2 p2, C3 p3) {
    return new R4(p0, p1, p2, p3);
  }
  
  public static <C0, C1, C2, C3, C4> R5<C0, C1, C2, C3, C4> of(C0 p0, C1 p1, C2 p2, C3 p3, C4 p4) { return new R5(p0, p1, p2, p3, p4); }
  
  public static class R2<C0, C1> extends Row<C0, C1, C1, C1, C1>
  {
    public R2(C0 a, C1 b) {
      items = new Object[] { a, b };
    }
  }
  
  public static class R3<C0, C1, C2> extends Row<C0, C1, C2, C2, C2> {
    public R3(C0 a, C1 b, C2 c) { items = new Object[] { a, b, c }; }
  }
  
  public static class R4<C0, C1, C2, C3> extends Row<C0, C1, C2, C3, C3> {
    public R4(C0 a, C1 b, C2 c, C3 d) {
      items = new Object[] { a, b, c, d };
    }
  }
  
  public static class R5<C0, C1, C2, C3, C4> extends Row<C0, C1, C2, C3, C4> {
    public R5(C0 a, C1 b, C2 c, C3 d, C4 e) { items = new Object[] { a, b, c, d, e }; }
  }
  
  public Row<C0, C1, C2, C3, C4> set0(C0 item)
  {
    return set(0, item);
  }
  
  public C0 get0() { return items[0]; }
  
  public Row<C0, C1, C2, C3, C4> set1(C1 item) {
    return set(1, item);
  }
  
  public C1 get1() { return items[1]; }
  
  public Row<C0, C1, C2, C3, C4> set2(C2 item) {
    return set(2, item);
  }
  
  public C2 get2() { return items[2]; }
  
  public Row<C0, C1, C2, C3, C4> set3(C3 item) {
    return set(3, item);
  }
  
  public C3 get3() { return items[3]; }
  
  public Row<C0, C1, C2, C3, C4> set4(C4 item) {
    return set(4, item);
  }
  
  public C4 get4() { return items[4]; }
  
  protected Row<C0, C1, C2, C3, C4> set(int i, Object item)
  {
    if (frozen) {
      throw new UnsupportedOperationException("Attempt to modify frozen object");
    }
    items[i] = item;
    return this;
  }
  
  public int hashCode() {
    int sum = items.length;
    for (Object item : items) {
      sum = sum * 37 + Utility.checkHash(item);
    }
    return sum;
  }
  
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (this == other) {
      return true;
    }
    try {
      Row<C0, C1, C2, C3, C4> that = (Row)other;
      if (items.length != items.length) {
        return false;
      }
      int i = 0;
      for (Object item : items) {
        if (!Utility.objectEquals(item, items[(i++)])) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {}
    return false;
  }
  

  public int compareTo(Object other)
  {
    Row<C0, C1, C2, C3, C4> that = (Row)other;
    int result = items.length - items.length;
    if (result != 0) {
      return result;
    }
    int i = 0;
    for (Object item : items) {
      result = Utility.checkCompare((Comparable)item, (Comparable)items[(i++)]);
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder("[");
    boolean first = true;
    for (Object item : items) {
      if (first) {
        first = false;
      } else {
        result.append(", ");
      }
      result.append(item);
    }
    return "]";
  }
  
  public boolean isFrozen() {
    return frozen;
  }
  
  public Row<C0, C1, C2, C3, C4> freeze() {
    frozen = true;
    return this;
  }
  
  public Object clone() {
    if (frozen) return this;
    try {
      Row<C0, C1, C2, C3, C4> result = (Row)super.clone();
      items = ((Object[])items.clone());
      return result;
    } catch (CloneNotSupportedException e) {}
    return null;
  }
  
  public Row<C0, C1, C2, C3, C4> cloneAsThawed()
  {
    try {
      Row<C0, C1, C2, C3, C4> result = (Row)super.clone();
      items = ((Object[])items.clone());
      frozen = false;
      return result;
    } catch (CloneNotSupportedException e) {}
    return null;
  }
}
