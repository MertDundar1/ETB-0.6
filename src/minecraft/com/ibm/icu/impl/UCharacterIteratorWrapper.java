package com.ibm.icu.impl;

import com.ibm.icu.text.UCharacterIterator;
import java.text.CharacterIterator;










public class UCharacterIteratorWrapper
  implements CharacterIterator
{
  private UCharacterIterator iterator;
  
  public UCharacterIteratorWrapper(UCharacterIterator iter)
  {
    iterator = iter;
  }
  









  public char first()
  {
    iterator.setToStart();
    return (char)iterator.current();
  }
  





  public char last()
  {
    iterator.setToLimit();
    return (char)iterator.previous();
  }
  





  public char current()
  {
    return (char)iterator.current();
  }
  








  public char next()
  {
    iterator.next();
    return (char)iterator.current();
  }
  







  public char previous()
  {
    return (char)iterator.previous();
  }
  







  public char setIndex(int position)
  {
    iterator.setIndex(position);
    return (char)iterator.current();
  }
  




  public int getBeginIndex()
  {
    return 0;
  }
  




  public int getEndIndex()
  {
    return iterator.getLength();
  }
  



  public int getIndex()
  {
    return iterator.getIndex();
  }
  


  public Object clone()
  {
    try
    {
      UCharacterIteratorWrapper result = (UCharacterIteratorWrapper)super.clone();
      iterator = ((UCharacterIterator)iterator.clone());
      return result;
    } catch (CloneNotSupportedException e) {}
    return null;
  }
}
