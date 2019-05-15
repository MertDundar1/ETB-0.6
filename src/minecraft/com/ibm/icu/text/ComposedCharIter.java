package com.ibm.icu.text;

import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Normalizer2Impl;



















































/**
 * @deprecated
 */
public final class ComposedCharIter
{
  /**
   * @deprecated
   */
  public static final char DONE = '￿';
  private final Normalizer2Impl n2impl;
  private String decompBuf;
  
  /**
   * @deprecated
   */
  public ComposedCharIter()
  {
    this(false, 0);
  }
  






  /**
   * @deprecated
   */
  public ComposedCharIter(boolean compat, int options)
  {
    if (compat) {
      n2impl = getNFKCInstanceimpl;
    } else {
      n2impl = getNFCInstanceimpl;
    }
  }
  

  /**
   * @deprecated
   */
  public boolean hasNext()
  {
    if (nextChar == -1) {
      findNextChar();
    }
    return nextChar != -1;
  }
  




  /**
   * @deprecated
   */
  public char next()
  {
    if (nextChar == -1) {
      findNextChar();
    }
    curChar = nextChar;
    nextChar = -1;
    return (char)curChar;
  }
  





  /**
   * @deprecated
   */
  public String decomposition()
  {
    if (decompBuf != null) {
      return decompBuf;
    }
    return "";
  }
  
  private void findNextChar()
  {
    int c = curChar + 1;
    decompBuf = null;
    
    while (c < 65535) {
      decompBuf = n2impl.getDecomposition(c);
      if (decompBuf != null) {
        break label51;
      }
      

      c++;
    }
    c = -1;
    
    label51:
    
    nextChar = c;
  }
  


  private int curChar = 0;
  private int nextChar = -1;
}
