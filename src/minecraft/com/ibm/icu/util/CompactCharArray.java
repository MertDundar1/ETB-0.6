package com.ibm.icu.util;

import com.ibm.icu.impl.Utility;

















/**
 * @deprecated
 */
public final class CompactCharArray
  implements Cloneable
{
  /**
   * @deprecated
   */
  public static final int UNICODECOUNT = 65536;
  /**
   * @deprecated
   */
  public static final int BLOCKSHIFT = 5;
  static final int BLOCKCOUNT = 32;
  static final int INDEXSHIFT = 11;
  static final int INDEXCOUNT = 2048;
  static final int BLOCKMASK = 31;
  private char[] values;
  private char[] indices;
  private int[] hashes;
  private boolean isCompact;
  char defaultValue;
  
  /**
   * @deprecated
   */
  public CompactCharArray()
  {
    this('\000');
  }
  




  /**
   * @deprecated
   */
  public CompactCharArray(char defaultValue)
  {
    values = new char[65536];
    indices = new char['ࠀ'];
    hashes = new int['ࠀ'];
    for (int i = 0; i < 65536; i++) {
      values[i] = defaultValue;
    }
    for (i = 0; i < 2048; i++) {
      indices[i] = ((char)(i << 5));
      hashes[i] = 0;
    }
    isCompact = false;
    
    this.defaultValue = defaultValue;
  }
  







  /**
   * @deprecated
   */
  public CompactCharArray(char[] indexArray, char[] newValues)
  {
    if (indexArray.length != 2048)
      throw new IllegalArgumentException("Index out of bounds.");
    for (int i = 0; i < 2048; i++) {
      char index = indexArray[i];
      if ((index < 0) || (index >= newValues.length + 32))
        throw new IllegalArgumentException("Index out of bounds.");
    }
    indices = indexArray;
    values = newValues;
    isCompact = true;
  }
  









  /**
   * @deprecated
   */
  public CompactCharArray(String indexArray, String valueArray)
  {
    this(Utility.RLEStringToCharArray(indexArray), Utility.RLEStringToCharArray(valueArray));
  }
  





  /**
   * @deprecated
   */
  public char elementAt(char index)
  {
    int ix = (indices[(index >> '\005')] & 0xFFFF) + (index & 0x1F);
    
    return ix >= values.length ? defaultValue : values[ix];
  }
  





  /**
   * @deprecated
   */
  public void setElementAt(char index, char value)
  {
    if (isCompact)
      expand();
    values[index] = value;
    touchBlock(index >> '\005', value);
  }
  







  /**
   * @deprecated
   */
  public void setElementAt(char start, char end, char value)
  {
    if (isCompact) {
      expand();
    }
    for (int i = start; i <= end; i++) {
      values[i] = value;
      touchBlock(i >> 5, value);
    }
  }
  
  /**
   * @deprecated
   */
  public void compact()
  {
    compact(true);
  }
  


  /**
   * @deprecated
   */
  public void compact(boolean exhaustive)
  {
    if (!isCompact) {
      int iBlockStart = 0;
      char iUntouched = 65535;
      int newSize = 0;
      
      char[] target = exhaustive ? new char[65536] : values;
      
      for (int i = 0; i < indices.length; iBlockStart += 32) {
        indices[i] = 65535;
        boolean touched = blockTouched(i);
        if ((!touched) && (iUntouched != 65535))
        {


          indices[i] = iUntouched;
        } else {
          int jBlockStart = 0;
          
          for (int j = 0; j < i; jBlockStart += 32) {
            if ((hashes[i] == hashes[j]) && (arrayRegionMatches(values, iBlockStart, values, jBlockStart, 32)))
            {

              indices[i] = indices[j];
            }
            j++;
          }
          




          if (indices[i] == 65535) { int dest;
            int dest;
            if (exhaustive)
            {
              dest = FindOverlappingPosition(iBlockStart, target, newSize);
            }
            else
            {
              dest = newSize;
            }
            int limit = dest + 32;
            if (limit > newSize) {
              for (int j = newSize; j < limit; j++) {
                target[j] = values[(iBlockStart + j - dest)];
              }
              newSize = limit;
            }
            indices[i] = ((char)dest);
            if (!touched)
            {

              iUntouched = (char)jBlockStart;
            }
          }
        }
        i++;
      }
      










































      char[] result = new char[newSize];
      System.arraycopy(target, 0, result, 0, newSize);
      values = result;
      isCompact = true;
      hashes = null;
    }
  }
  
  private int FindOverlappingPosition(int start, char[] tempValues, int tempCount)
  {
    for (int i = 0; i < tempCount; i++) {
      int currentCount = 32;
      if (i + 32 > tempCount) {
        currentCount = tempCount - i;
      }
      if (arrayRegionMatches(values, start, tempValues, i, currentCount))
        return i;
    }
    return tempCount;
  }
  







  static final boolean arrayRegionMatches(char[] source, int sourceStart, char[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; i++) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }
  



  private final void touchBlock(int i, int value)
  {
    hashes[i] = (hashes[i] + (value << 1) | 0x1);
  }
  



  private final boolean blockTouched(int i)
  {
    return hashes[i] != 0;
  }
  



  /**
   * @deprecated
   */
  public char[] getIndexArray()
  {
    return indices;
  }
  



  /**
   * @deprecated
   */
  public char[] getValueArray()
  {
    return values;
  }
  

  /**
   * @deprecated
   */
  public Object clone()
  {
    try
    {
      CompactCharArray other = (CompactCharArray)super.clone();
      values = ((char[])values.clone());
      indices = ((char[])indices.clone());
      if (hashes != null) hashes = ((int[])hashes.clone());
      return other;
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException();
    }
  }
  




  /**
   * @deprecated
   */
  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;
    CompactCharArray other = (CompactCharArray)obj;
    for (int i = 0; i < 65536; i++)
    {
      if (elementAt((char)i) != other.elementAt((char)i))
        return false;
    }
    return true;
  }
  

  /**
   * @deprecated
   */
  public int hashCode()
  {
    int result = 0;
    int increment = Math.min(3, values.length / 16);
    for (int i = 0; i < values.length; i += increment) {
      result = result * 37 + values[i];
    }
    return result;
  }
  









  private void expand()
  {
    if (isCompact)
    {
      hashes = new int['ࠀ'];
      char[] tempArray = new char[65536];
      for (int i = 0; i < 65536; i++) {
        tempArray[i] = elementAt((char)i);
      }
      for (i = 0; i < 2048; i++) {
        indices[i] = ((char)(i << 5));
      }
      values = null;
      values = tempArray;
      isCompact = false;
    }
  }
}
