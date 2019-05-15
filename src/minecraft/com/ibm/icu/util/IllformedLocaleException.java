package com.ibm.icu.util;








public class IllformedLocaleException
  extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  





  private int _errIdx = -1;
  







  public IllformedLocaleException() {}
  






  public IllformedLocaleException(String message)
  {
    super(message);
  }
  










  public IllformedLocaleException(String message, int errorIndex)
  {
    super(message + (errorIndex < 0 ? "" : new StringBuilder().append(" [at index ").append(errorIndex).append("]").toString()));
    _errIdx = errorIndex;
  }
  






  public int getErrorIndex()
  {
    return _errIdx;
  }
}
