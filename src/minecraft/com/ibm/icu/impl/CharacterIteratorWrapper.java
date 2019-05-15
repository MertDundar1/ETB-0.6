package com.ibm.icu.impl;

import com.ibm.icu.text.UCharacterIterator;
import java.text.CharacterIterator;













public class CharacterIteratorWrapper
  extends UCharacterIterator
{
  private CharacterIterator iterator;
  
  public CharacterIteratorWrapper(CharacterIterator iter)
  {
    if (iter == null) {
      throw new IllegalArgumentException();
    }
    iterator = iter;
  }
  


  public int current()
  {
    int c = iterator.current();
    if (c == 65535) {
      return -1;
    }
    return c;
  }
  


  public int getLength()
  {
    return iterator.getEndIndex() - iterator.getBeginIndex();
  }
  


  public int getIndex()
  {
    return iterator.getIndex();
  }
  


  public int next()
  {
    int i = iterator.current();
    iterator.next();
    if (i == 65535) {
      return -1;
    }
    return i;
  }
  


  public int previous()
  {
    int i = iterator.previous();
    if (i == 65535) {
      return -1;
    }
    return i;
  }
  

  public void setIndex(int index)
  {
    try
    {
      iterator.setIndex(index);
    } catch (IllegalArgumentException e) {
      throw new IndexOutOfBoundsException();
    }
  }
  


  public void setToLimit()
  {
    iterator.setIndex(iterator.getEndIndex());
  }
  


  public int getText(char[] fillIn, int offset)
  {
    int length = iterator.getEndIndex() - iterator.getBeginIndex();
    int currentIndex = iterator.getIndex();
    if ((offset < 0) || (offset + length > fillIn.length)) {
      throw new IndexOutOfBoundsException(Integer.toString(length));
    }
    
    for (char ch = iterator.first(); ch != 65535; ch = iterator.next()) {
      fillIn[(offset++)] = ch;
    }
    iterator.setIndex(currentIndex);
    
    return length;
  }
  


  public Object clone()
  {
    try
    {
      CharacterIteratorWrapper result = (CharacterIteratorWrapper)super.clone();
      iterator = ((CharacterIterator)iterator.clone());
      return result;
    } catch (CloneNotSupportedException e) {}
    return null;
  }
  

  public int moveIndex(int delta)
  {
    int length = iterator.getEndIndex() - iterator.getBeginIndex();
    int idx = iterator.getIndex() + delta;
    
    if (idx < 0) {
      idx = 0;
    } else if (idx > length) {
      idx = length;
    }
    return iterator.setIndex(idx);
  }
  


  public CharacterIterator getCharacterIterator()
  {
    return (CharacterIterator)iterator.clone();
  }
}
