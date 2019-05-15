package ch.qos.logback.core.helpers;

import java.util.ArrayList;
import java.util.List;




























public class CyclicBuffer<E>
{
  E[] ea;
  int first;
  int last;
  int numElems;
  int maxSize;
  
  public CyclicBuffer(int maxSize)
    throws IllegalArgumentException
  {
    if (maxSize < 1) {
      throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
    }
    
    init(maxSize);
  }
  
  public CyclicBuffer(CyclicBuffer<E> other) {
    maxSize = maxSize;
    ea = ((Object[])new Object[maxSize]);
    System.arraycopy(ea, 0, ea, 0, maxSize);
    last = last;
    first = first;
    numElems = numElems;
  }
  
  private void init(int maxSize)
  {
    this.maxSize = maxSize;
    ea = ((Object[])new Object[maxSize]);
    first = 0;
    last = 0;
    numElems = 0;
  }
  


  public void clear()
  {
    init(maxSize);
  }
  



  public void add(E event)
  {
    ea[last] = event;
    if (++last == maxSize) {
      last = 0;
    }
    if (numElems < maxSize) {
      numElems += 1;
    } else if (++first == maxSize) {
      first = 0;
    }
  }
  



  public E get(int i)
  {
    if ((i < 0) || (i >= numElems)) {
      return null;
    }
    return ea[((first + i) % maxSize)];
  }
  
  public int getMaxSize() {
    return maxSize;
  }
  



  public E get()
  {
    E r = null;
    if (numElems > 0) {
      numElems -= 1;
      r = ea[first];
      ea[first] = null;
      if (++first == maxSize)
        first = 0;
    }
    return r;
  }
  
  public List<E> asList() {
    List<E> tList = new ArrayList();
    for (int i = 0; i < length(); i++) {
      tList.add(get(i));
    }
    return tList;
  }
  



  public int length()
  {
    return numElems;
  }
  






  public void resize(int newSize)
  {
    if (newSize < 0) {
      throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
    }
    
    if (newSize == numElems) {
      return;
    }
    
    E[] temp = (Object[])new Object[newSize];
    
    int loopLen = newSize < numElems ? newSize : numElems;
    
    for (int i = 0; i < loopLen; i++) {
      temp[i] = ea[first];
      ea[first] = null;
      if (++first == numElems)
        first = 0;
    }
    ea = temp;
    first = 0;
    numElems = loopLen;
    maxSize = newSize;
    if (loopLen == newSize) {
      last = 0;
    } else {
      last = loopLen;
    }
  }
}
