package com.ibm.icu.lang;






































/**
 * @deprecated
 */
public class CharSequences
{
  /**
   * @deprecated
   */
  public static int matchAfter(CharSequence a, CharSequence b, int aIndex, int bIndex)
  {
    int i = aIndex;int j = bIndex;
    int alen = a.length();
    int blen = b.length();
    for (; (i < alen) && (j < blen); j++) {
      char ca = a.charAt(i);
      char cb = b.charAt(j);
      if (ca != cb) {
        break;
      }
      i++;
    }
    





    int result = i - aIndex;
    if ((result != 0) && (!onCharacterBoundary(a, i)) && (!onCharacterBoundary(b, j))) {
      result--;
    }
    return result;
  }
  

  /**
   * @deprecated
   */
  public int codePointLength(CharSequence s)
  {
    return Character.codePointCount(s, 0, s.length());
  }
  














  /**
   * @deprecated
   */
  public static final boolean equals(int codepoint, CharSequence other)
  {
    if (other == null) {
      return false;
    }
    switch (other.length()) {
    case 1:  return codepoint == other.charAt(0);
    case 2:  return (codepoint > 65535) && (codepoint == Character.codePointAt(other, 0)); }
    return false;
  }
  

  /**
   * @deprecated
   */
  public static final boolean equals(CharSequence other, int codepoint)
  {
    return equals(codepoint, other);
  }
  






  /**
   * @deprecated
   */
  public static int compare(CharSequence string, int codePoint)
  {
    if ((codePoint < 0) || (codePoint > 1114111)) {
      throw new IllegalArgumentException();
    }
    int stringLength = string.length();
    if (stringLength == 0) {
      return -1;
    }
    char firstChar = string.charAt(0);
    int offset = codePoint - 65536;
    
    if (offset < 0) {
      int result = firstChar - codePoint;
      if (result != 0) {
        return result;
      }
      return stringLength - 1;
    }
    
    char lead = (char)((offset >>> 10) + 55296);
    int result = firstChar - lead;
    if (result != 0) {
      return result;
    }
    if (stringLength > 1) {
      char trail = (char)((offset & 0x3FF) + 56320);
      result = string.charAt(1) - trail;
      if (result != 0) {
        return result;
      }
    }
    return stringLength - 2;
  }
  





  /**
   * @deprecated
   */
  public static int compare(int codepoint, CharSequence a)
  {
    return -compare(a, codepoint);
  }
  


  /**
   * @deprecated
   */
  public static int getSingleCodePoint(CharSequence s)
  {
    int length = s.length();
    if ((length < 1) || (length > 2)) {
      return Integer.MAX_VALUE;
    }
    int result = Character.codePointAt(s, 0);
    return (result < 65536 ? 1 : 0) == (length == 1 ? 1 : 0) ? result : Integer.MAX_VALUE;
  }
  



  /**
   * @deprecated
   */
  public static final <T> boolean equals(T a, T b)
  {
    return b == null ? false : a == null ? false : b == null ? true : a.equals(b);
  }
  




  /**
   * @deprecated
   */
  public static int compare(CharSequence a, CharSequence b)
  {
    int alength = a.length();
    int blength = b.length();
    int min = alength <= blength ? alength : blength;
    for (int i = 0; i < min; i++) {
      int diff = a.charAt(i) - b.charAt(i);
      if (diff != 0) {
        return diff;
      }
    }
    return alength - blength;
  }
  



  /**
   * @deprecated
   */
  public static boolean equalsChars(CharSequence a, CharSequence b)
  {
    return (a.length() == b.length()) && (compare(a, b) == 0);
  }
  


  /**
   * @deprecated
   */
  public static boolean onCharacterBoundary(CharSequence s, int i)
  {
    return (i <= 0) || (i >= s.length()) || (!Character.isHighSurrogate(s.charAt(i - 1))) || (!Character.isLowSurrogate(s.charAt(i)));
  }
  



  /**
   * @deprecated
   */
  public static int indexOf(CharSequence s, int codePoint)
  {
    int cp;
    

    for (int i = 0; i < s.length(); i += Character.charCount(cp)) {
      cp = Character.codePointAt(s, i);
      if (cp == codePoint) {
        return i;
      }
    }
    return -1;
  }
  







  /**
   * @deprecated
   */
  public static int[] codePoints(CharSequence s)
  {
    int[] result = new int[s.length()];
    int j = 0;
    for (int i = 0; i < s.length(); i++) {
      char cp = s.charAt(i);
      if ((cp >= 56320) && (cp <= 57343) && (i != 0)) {
        char last = (char)result[(j - 1)];
        if ((last >= 55296) && (last <= 56319))
        {
          result[(j - 1)] = Character.toCodePoint(last, cp);
          continue;
        }
      }
      result[(j++)] = cp;
    }
    if (j == result.length) {
      return result;
    }
    int[] shortResult = new int[j];
    System.arraycopy(result, 0, shortResult, 0, j);
    return shortResult;
  }
  
  private CharSequences() {}
}
