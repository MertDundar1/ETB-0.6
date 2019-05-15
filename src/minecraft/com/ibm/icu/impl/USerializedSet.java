package com.ibm.icu.impl;












public final class USerializedSet
{
  public USerializedSet() {}
  










  public final boolean getSet(char[] src, int srcStart)
  {
    array = null;
    arrayOffset = (this.bmpLength = this.length = 0);
    
    length = src[(srcStart++)];
    

    if ((length & 0x8000) > 0)
    {
      length &= 0x7FFF;
      if (src.length < srcStart + 1 + length) {
        length = 0;
        throw new IndexOutOfBoundsException();
      }
      bmpLength = src[(srcStart++)];
    }
    else {
      if (src.length < srcStart + length) {
        length = 0;
        throw new IndexOutOfBoundsException();
      }
      bmpLength = length;
    }
    array = new char[length];
    System.arraycopy(src, srcStart, array, 0, length);
    
    return true;
  }
  



  public final void setToOne(int c)
  {
    if (1114111 < c) {
      return;
    }
    
    if (c < 65535) {
      bmpLength = (this.length = 2);
      array[0] = ((char)c);
      array[1] = ((char)(c + 1));
    } else if (c == 65535) {
      bmpLength = 1;
      length = 3;
      array[0] = 65535;
      array[1] = '\001';
      array[2] = '\000';
    } else if (c < 1114111) {
      bmpLength = 0;
      length = 4;
      array[0] = ((char)(c >> 16));
      array[1] = ((char)c);
      c++;
      array[2] = ((char)(c >> 16));
      array[3] = ((char)c);
    } else {
      bmpLength = 0;
      length = 2;
      array[0] = '\020';
      array[1] = 65535;
    }
  }
  







  public final boolean getRange(int rangeIndex, int[] range)
  {
    if (rangeIndex < 0) {
      return false;
    }
    if (array == null) {
      array = new char[8];
    }
    if ((range == null) || (range.length < 2)) {
      throw new IllegalArgumentException();
    }
    rangeIndex *= 2;
    if (rangeIndex < bmpLength) {
      range[0] = array[(rangeIndex++)];
      if (rangeIndex < bmpLength) {
        range[1] = (array[rangeIndex] - '\001');
      } else if (rangeIndex < length) {
        range[1] = ((array[rangeIndex] << '\020' | array[(rangeIndex + 1)]) - 1);
      } else {
        range[1] = 1114111;
      }
      return true;
    }
    rangeIndex -= bmpLength;
    rangeIndex *= 2;
    int suppLength = length - bmpLength;
    if (rangeIndex < suppLength) {
      int offset = arrayOffset + bmpLength;
      range[0] = (array[(offset + rangeIndex)] << '\020' | array[(offset + rangeIndex + 1)]);
      rangeIndex += 2;
      if (rangeIndex < suppLength) {
        range[1] = ((array[(offset + rangeIndex)] << '\020' | array[(offset + rangeIndex + 1)]) - 1);
      } else {
        range[1] = 1114111;
      }
      return true;
    }
    return false;
  }
  








  public final boolean contains(int c)
  {
    if (c > 1114111) {
      return false;
    }
    
    if (c <= 65535)
    {

      for (int i = 0; (i < bmpLength) && ((char)c >= array[i]); i++) {}
      return (i & 0x1) != 0;
    }
    

    char high = (char)(c >> 16);char low = (char)c;
    int i = bmpLength;
    while ((i < length) && ((high > array[i]) || ((high == array[i]) && (low >= array[(i + 1)])))) {
      i += 2;
    }
    
    return (i + bmpLength & 0x2) != 0;
  }
  







  public final int countRanges()
  {
    return (bmpLength + (length - bmpLength) / 2 + 1) / 2;
  }
  
  private char[] array = new char[8];
  private int arrayOffset;
  private int bmpLength;
  private int length;
}
