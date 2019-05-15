package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;











class Quantifier
  implements UnicodeMatcher
{
  private UnicodeMatcher matcher;
  private int minCount;
  private int maxCount;
  public static final int MAX = Integer.MAX_VALUE;
  
  public Quantifier(UnicodeMatcher theMatcher, int theMinCount, int theMaxCount)
  {
    if ((theMatcher == null) || (theMinCount < 0) || (theMaxCount < 0) || (theMinCount > theMaxCount)) {
      throw new IllegalArgumentException();
    }
    matcher = theMatcher;
    minCount = theMinCount;
    maxCount = theMaxCount;
  }
  





  public int matches(Replaceable text, int[] offset, int limit, boolean incremental)
  {
    int start = offset[0];
    int count = 0;
    while (count < maxCount) {
      int pos = offset[0];
      int m = matcher.matches(text, offset, limit, incremental);
      if (m == 2) {
        count++;
        if (pos == offset[0]) {
          break;
        }
      }
      else {
        if ((!incremental) || (m != 1)) break;
        return 1;
      }
    }
    

    if ((incremental) && (offset[0] == limit)) {
      return 1;
    }
    if (count >= minCount) {
      return 2;
    }
    offset[0] = start;
    return 0;
  }
  


  public String toPattern(boolean escapeUnprintable)
  {
    StringBuilder result = new StringBuilder();
    result.append(matcher.toPattern(escapeUnprintable));
    if (minCount == 0) {
      if (maxCount == 1)
        return '?';
      if (maxCount == Integer.MAX_VALUE) {
        return '*';
      }
    }
    else if ((minCount == 1) && (maxCount == Integer.MAX_VALUE)) {
      return '+';
    }
    result.append('{');
    result.append(Utility.hex(minCount, 1));
    result.append(',');
    if (maxCount != Integer.MAX_VALUE) {
      result.append(Utility.hex(maxCount, 1));
    }
    result.append('}');
    return result.toString();
  }
  


  public boolean matchesIndexValue(int v)
  {
    return (minCount == 0) || (matcher.matchesIndexValue(v));
  }
  






  public void addMatchSetTo(UnicodeSet toUnionTo)
  {
    if (maxCount > 0) {
      matcher.addMatchSetTo(toUnionTo);
    }
  }
}
