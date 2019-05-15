package com.ibm.icu.util;

import com.ibm.icu.impl.Utility;



















/**
 * @deprecated
 */
public final class CompactByteArray
  implements Cloneable
{
  /**
   * @deprecated
   */
  public static final int UNICODECOUNT = 65536;
  private static final int BLOCKSHIFT = 7;
  private static final int BLOCKCOUNT = 128;
  private static final int INDEXSHIFT = 9;
  private static final int INDEXCOUNT = 512;
  private static final int BLOCKMASK = 127;
  private byte[] values;
  private char[] indices;
  private int[] hashes;
  private boolean isCompact;
  byte defaultValue;
  
  /**
   * @deprecated
   */
  public CompactByteArray()
  {
    this((byte)0);
  }
  




  /**
   * @deprecated
   */
  public CompactByteArray(byte defaultValue)
  {
    values = new byte[65536];
    indices = new char['Ȁ'];
    hashes = new int['Ȁ'];
    for (int i = 0; i < 65536; i++) {
      values[i] = defaultValue;
    }
    for (i = 0; i < 512; i++) {
      indices[i] = ((char)(i << 7));
      hashes[i] = 0;
    }
    isCompact = false;
    
    this.defaultValue = defaultValue;
  }
  







  /**
   * @deprecated
   */
  public CompactByteArray(char[] indexArray, byte[] newValues)
  {
    if (indexArray.length != 512)
      throw new IllegalArgumentException("Index out of bounds.");
    for (int i = 0; i < 512; i++) {
      char index = indexArray[i];
      if ((index < 0) || (index >= newValues.length + 128))
        throw new IllegalArgumentException("Index out of bounds.");
    }
    indices = indexArray;
    values = newValues;
    isCompact = true;
  }
  









  /**
   * @deprecated
   */
  public CompactByteArray(String indexArray, String valueArray)
  {
    this(Utility.RLEStringToCharArray(indexArray), Utility.RLEStringToByteArray(valueArray));
  }
  





  /**
   * @deprecated
   */
  public byte elementAt(char index)
  {
    return values[((indices[(index >> '\007')] & 0xFFFF) + (index & 0x7F))];
  }
  






  /**
   * @deprecated
   */
  public void setElementAt(char index, byte value)
  {
    if (isCompact)
      expand();
    values[index] = value;
    touchBlock(index >> '\007', value);
  }
  







  /**
   * @deprecated
   */
  public void setElementAt(char start, char end, byte value)
  {
    if (isCompact) {
      expand();
    }
    for (int i = start; i <= end; i++) {
      values[i] = value;
      touchBlock(i >> 7, value);
    }
  }
  
  /**
   * @deprecated
   */
  public void compact()
  {
    compact(false);
  }
  


  /**
   * @deprecated
   */
  public void compact(boolean exhaustive)
  {
    if (!isCompact) {
      int limitCompacted = 0;
      int iBlockStart = 0;
      char iUntouched = 65535;
      
      for (int i = 0; i < indices.length; iBlockStart += 128) {
        indices[i] = 65535;
        boolean touched = blockTouched(i);
        if ((!touched) && (iUntouched != 65535))
        {


          indices[i] = iUntouched;
        } else {
          int jBlockStart = 0;
          int j = 0;
          for (j = 0; j < limitCompacted; 
              jBlockStart += 128) {
            if ((hashes[i] == hashes[j]) && (arrayRegionMatches(values, iBlockStart, values, jBlockStart, 128)))
            {

              indices[i] = ((char)jBlockStart);
              break;
            }
            j++;
          }
          





          if (indices[i] == 65535)
          {
            System.arraycopy(values, iBlockStart, values, jBlockStart, 128);
            
            indices[i] = ((char)jBlockStart);
            hashes[j] = hashes[i];
            limitCompacted++;
            
            if (!touched)
            {

              iUntouched = (char)jBlockStart;
            }
          }
        }
        i++;
      }
      


































      int newSize = limitCompacted * 128;
      byte[] result = new byte[newSize];
      System.arraycopy(values, 0, result, 0, newSize);
      values = result;
      isCompact = true;
      hashes = null;
    }
  }
  







  static final boolean arrayRegionMatches(byte[] source, int sourceStart, byte[] target, int targetStart, int len)
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
  public byte[] getValueArray()
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
      CompactByteArray other = (CompactByteArray)super.clone();
      values = ((byte[])values.clone());
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
    CompactByteArray other = (CompactByteArray)obj;
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
      hashes = new int['Ȁ'];
      byte[] tempArray = new byte[65536];
      for (int i = 0; i < 65536; i++) {
        byte value = elementAt((char)i);
        tempArray[i] = value;
        touchBlock(i >> 7, value);
      }
      for (i = 0; i < 512; i++) {
        indices[i] = ((char)(i << 7));
      }
      values = null;
      values = tempArray;
      isCompact = false;
    }
  }
}
