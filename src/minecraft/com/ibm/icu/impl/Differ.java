package com.ibm.icu.impl;




public final class Differ<T>
{
  private int STACKSIZE;
  


  private int EQUALSIZE;
  


  private T[] a;
  


  private T[] b;
  


  public Differ(int stackSize, int matchCount)
  {
    STACKSIZE = stackSize;
    EQUALSIZE = matchCount;
    a = ((Object[])new Object[stackSize + matchCount]);
    b = ((Object[])new Object[stackSize + matchCount]);
  }
  
  public void add(T aStr, T bStr) {
    addA(aStr);
    addB(bStr);
  }
  
  public void addA(T aStr) {
    flush();
    a[(aCount++)] = aStr;
  }
  
  public void addB(T bStr) {
    flush();
    b[(bCount++)] = bStr;
  }
  
  public int getALine(int offset) {
    return aLine + maxSame + offset;
  }
  
  public T getA(int offset) {
    if (offset < 0) return last;
    if (offset > aTop - maxSame) return next;
    return a[offset];
  }
  
  public int getACount() {
    return aTop - maxSame;
  }
  
  public int getBCount() {
    return bTop - maxSame;
  }
  
  public int getBLine(int offset) {
    return bLine + maxSame + offset;
  }
  
  public T getB(int offset) {
    if (offset < 0) return last;
    if (offset > bTop - maxSame) return next;
    return b[offset];
  }
  
  public void checkMatch(boolean finalPass)
  {
    int max = aCount;
    if (max > bCount) { max = bCount;
    }
    for (int i = 0; i < max; i++) {
      if (!a[i].equals(b[i]))
        break;
    }
    maxSame = i;
    aTop = (this.bTop = maxSame);
    if (maxSame > 0) last = a[(maxSame - 1)];
    next = null;
    
    if (finalPass) {
      aTop = aCount;
      bTop = bCount;
      next = null;
      return;
    }
    
    if ((aCount - maxSame < EQUALSIZE) || (bCount - maxSame < EQUALSIZE)) { return;
    }
    
    int match = find(a, aCount - EQUALSIZE, aCount, b, maxSame, bCount);
    if (match != -1) {
      aTop = (aCount - EQUALSIZE);
      bTop = match;
      next = a[aTop];
      return;
    }
    match = find(b, bCount - EQUALSIZE, bCount, a, maxSame, aCount);
    if (match != -1) {
      bTop = (bCount - EQUALSIZE);
      aTop = match;
      next = b[bTop];
      return;
    }
    if ((aCount >= STACKSIZE) || (bCount >= STACKSIZE))
    {
      aCount = ((aCount + maxSame) / 2);
      bCount = ((bCount + maxSame) / 2);
      next = null;
    }
  }
  




  public int find(T[] aArr, int aStart, int aEnd, T[] bArr, int bStart, int bEnd)
  {
    int len = aEnd - aStart;
    int bEndMinus = bEnd - len;
    label65:
    for (int i = bStart; i <= bEndMinus; i++) {
      for (int j = 0; j < len; j++)
        if (!bArr[(i + j)].equals(aArr[(aStart + j)]))
          break label65;
      return i;
    }
    return -1;
  }
  

  private void flush()
  {
    if (aTop != 0) {
      int newCount = aCount - aTop;
      System.arraycopy(a, aTop, a, 0, newCount);
      aCount = newCount;
      aLine += aTop;
      aTop = 0;
    }
    
    if (bTop != 0) {
      int newCount = bCount - bTop;
      System.arraycopy(b, bTop, b, 0, newCount);
      bCount = newCount;
      bLine += bTop;
      bTop = 0;
    }
  }
  





  private T last = null;
  private T next = null;
  private int aCount = 0;
  private int bCount = 0;
  private int aLine = 1;
  private int bLine = 1;
  private int maxSame = 0; private int aTop = 0; private int bTop = 0;
}
