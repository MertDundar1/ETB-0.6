package com.ibm.icu.util;

import java.util.NoSuchElementException;































public class UResourceBundleIterator
{
  private UResourceBundle bundle;
  private int index = 0;
  private int size = 0;
  





  public UResourceBundleIterator(UResourceBundle bndl)
  {
    bundle = bndl;
    size = bundle.getSize();
  }
  




  public UResourceBundle next()
    throws NoSuchElementException
  {
    if (index < size) {
      return bundle.get(index++);
    }
    throw new NoSuchElementException();
  }
  




  public String nextString()
    throws NoSuchElementException, UResourceTypeMismatchException
  {
    if (index < size) {
      return bundle.getString(index++);
    }
    throw new NoSuchElementException();
  }
  




  public void reset()
  {
    index = 0;
  }
  




  public boolean hasNext()
  {
    return index < size;
  }
}
