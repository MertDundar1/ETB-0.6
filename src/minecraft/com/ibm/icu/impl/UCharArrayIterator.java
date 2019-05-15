package com.ibm.icu.impl;

import com.ibm.icu.text.UCharacterIterator;











public final class UCharArrayIterator
  extends UCharacterIterator
{
  private final char[] text;
  private final int start;
  private final int limit;
  private int pos;
  
  public UCharArrayIterator(char[] text, int start, int limit)
  {
    if ((start < 0) || (limit > text.length) || (start > limit)) {
      throw new IllegalArgumentException("start: " + start + " or limit: " + limit + " out of range [0, " + text.length + ")");
    }
    

    this.text = text;
    this.start = start;
    this.limit = limit;
    
    pos = start;
  }
  
  public int current() {
    return pos < limit ? text[pos] : -1;
  }
  
  public int getLength() {
    return limit - start;
  }
  
  public int getIndex() {
    return pos - start;
  }
  
  public int next() {
    return pos < limit ? text[(pos++)] : -1;
  }
  
  public int previous() {
    return pos > start ? text[(--pos)] : -1;
  }
  
  public void setIndex(int index) {
    if ((index < 0) || (index > limit - start)) {
      throw new IndexOutOfBoundsException("index: " + index + " out of range [0, " + (limit - start) + ")");
    }
    

    pos = (start + index);
  }
  
  public int getText(char[] fillIn, int offset) {
    int len = limit - start;
    System.arraycopy(text, start, fillIn, offset, len);
    return len;
  }
  



  public Object clone()
  {
    try
    {
      return super.clone();
    } catch (CloneNotSupportedException e) {}
    return null;
  }
}
