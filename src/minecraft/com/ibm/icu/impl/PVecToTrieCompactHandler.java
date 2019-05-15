package com.ibm.icu.impl;



public class PVecToTrieCompactHandler
  implements PropsVectors.CompactHandler
{
  public IntTrieBuilder builder;
  

  public int initialValue;
  


  public PVecToTrieCompactHandler() {}
  


  public void setRowIndexForErrorValue(int rowIndex) {}
  

  public void setRowIndexForInitialValue(int rowIndex)
  {
    initialValue = rowIndex;
  }
  
  public void setRowIndexForRange(int start, int end, int rowIndex) {
    builder.setRange(start, end + 1, rowIndex, true);
  }
  
  public void startRealValues(int rowIndex) {
    if (rowIndex > 65535)
    {
      throw new IndexOutOfBoundsException();
    }
    builder = new IntTrieBuilder(null, 100000, initialValue, initialValue, false);
  }
}
