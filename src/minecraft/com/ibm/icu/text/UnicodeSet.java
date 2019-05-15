package com.ibm.icu.text;

import com.ibm.icu.impl.BMPSet;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.RuleCharacterIterator;
import com.ibm.icu.impl.SortedSetRelation;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.UPropertyAliases;
import com.ibm.icu.impl.UnicodeSetStringSpan;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.CharSequences;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.VersionInfo;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

























































































































































































































































public class UnicodeSet
  extends UnicodeFilter
  implements Iterable<String>, Comparable<UnicodeSet>, Freezable<UnicodeSet>
{
  public static final UnicodeSet EMPTY = new UnicodeSet().freeze();
  



  public static final UnicodeSet ALL_CODE_POINTS = new UnicodeSet(0, 1114111).freeze();
  
  private static XSymbolTable XSYMBOL_TABLE = null;
  

  private static final int LOW = 0;
  

  private static final int HIGH = 1114112;
  

  public static final int MIN_VALUE = 0;
  

  public static final int MAX_VALUE = 1114111;
  

  private int len;
  

  private int[] list;
  

  private int[] rangeList;
  
  private int[] buffer;
  
  TreeSet<String> strings = new TreeSet();
  









  private String pat = null;
  

  private static final int START_EXTRA = 16;
  

  private static final int GROW_EXTRA = 16;
  

  private static final String ANY_ID = "ANY";
  

  private static final String ASCII_ID = "ASCII";
  
  private static final String ASSIGNED = "Assigned";
  
  private static UnicodeSet[] INCLUSIONS = null;
  


  private BMPSet bmpSet;
  

  private UnicodeSetStringSpan stringSpan;
  


  public UnicodeSet()
  {
    list = new int[17];
    list[(len++)] = 1114112;
  }
  



  public UnicodeSet(UnicodeSet other)
  {
    set(other);
  }
  







  public UnicodeSet(int start, int end)
  {
    this();
    complement(start, end);
  }
  








  public UnicodeSet(int... pairs)
  {
    if ((pairs.length & 0x1) != 0) {
      throw new IllegalArgumentException("Must have even number of integers");
    }
    list = new int[pairs.length + 1];
    len = list.length;
    int last = -1;
    int i = 0;
    for (; i < pairs.length; 
        










        list[(i++)] = tmp133_131)
    {
      int start = pairs[i];
      if (last >= start) {
        throw new IllegalArgumentException("Must be monotonically increasing.");
      }
      int tmp97_95 = start;last = tmp97_95;list[(i++)] = tmp97_95;
      
      int end = pairs[i] + 1;
      if (last >= end) {
        throw new IllegalArgumentException("Must be monotonically increasing.");
      }
      last = end;
    }
    list[i] = 1114112;
  }
  







  public UnicodeSet(String pattern)
  {
    this();
    applyPattern(pattern, null, null, 1);
  }
  








  public UnicodeSet(String pattern, boolean ignoreWhitespace)
  {
    this();
    applyPattern(pattern, null, null, ignoreWhitespace ? 1 : 0);
  }
  









  public UnicodeSet(String pattern, int options)
  {
    this();
    applyPattern(pattern, null, null, options);
  }
  











  public UnicodeSet(String pattern, ParsePosition pos, SymbolTable symbols)
  {
    this();
    applyPattern(pattern, pos, symbols, 1);
  }
  













  public UnicodeSet(String pattern, ParsePosition pos, SymbolTable symbols, int options)
  {
    this();
    applyPattern(pattern, pos, symbols, options);
  }
  




  public Object clone()
  {
    UnicodeSet result = new UnicodeSet(this);
    bmpSet = bmpSet;
    stringSpan = stringSpan;
    return result;
  }
  








  public UnicodeSet set(int start, int end)
  {
    checkFrozen();
    clear();
    complement(start, end);
    return this;
  }
  





  public UnicodeSet set(UnicodeSet other)
  {
    checkFrozen();
    list = ((int[])list.clone());
    len = len;
    pat = pat;
    strings = new TreeSet(strings);
    return this;
  }
  








  public final UnicodeSet applyPattern(String pattern)
  {
    checkFrozen();
    return applyPattern(pattern, null, null, 1);
  }
  









  public UnicodeSet applyPattern(String pattern, boolean ignoreWhitespace)
  {
    checkFrozen();
    return applyPattern(pattern, null, null, ignoreWhitespace ? 1 : 0);
  }
  










  public UnicodeSet applyPattern(String pattern, int options)
  {
    checkFrozen();
    return applyPattern(pattern, null, null, options);
  }
  




  public static boolean resemblesPattern(String pattern, int pos)
  {
    return ((pos + 1 < pattern.length()) && (pattern.charAt(pos) == '[')) || (resemblesPropertyPattern(pattern, pos));
  }
  



  private static void _appendToPat(StringBuffer buf, String s, boolean escapeUnprintable)
  {
    int cp;
    

    for (int i = 0; i < s.length(); i += Character.charCount(cp)) {
      cp = s.codePointAt(i);
      _appendToPat(buf, cp, escapeUnprintable);
    }
  }
  





  private static void _appendToPat(StringBuffer buf, int c, boolean escapeUnprintable)
  {
    if ((escapeUnprintable) && (Utility.isUnprintable(c)))
    {

      if (Utility.escapeUnprintable(buf, c)) {
        return;
      }
    }
    
    switch (c) {
    case 36: 
    case 38: 
    case 45: 
    case 58: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
    case 123: 
    case 125: 
      buf.append('\\');
      break;
    
    default: 
      if (PatternProps.isWhiteSpace(c)) {
        buf.append('\\');
      }
      break;
    }
    UTF16.append(buf, c);
  }
  





  public String toPattern(boolean escapeUnprintable)
  {
    StringBuffer result = new StringBuffer();
    return _toPattern(result, escapeUnprintable).toString();
  }
  





  private StringBuffer _toPattern(StringBuffer result, boolean escapeUnprintable)
  {
    if (pat != null)
    {
      int backslashCount = 0;
      for (int i = 0; i < pat.length();) {
        int c = UTF16.charAt(pat, i);
        i += UTF16.getCharCount(c);
        if ((escapeUnprintable) && (Utility.isUnprintable(c)))
        {



          if (backslashCount % 2 != 0) {
            result.setLength(result.length() - 1);
          }
          Utility.escapeUnprintable(result, c);
          backslashCount = 0;
        } else {
          UTF16.append(result, c);
          if (c == 92) {
            backslashCount++;
          } else {
            backslashCount = 0;
          }
        }
      }
      return result;
    }
    
    return _generatePattern(result, escapeUnprintable, true);
  }
  







  public StringBuffer _generatePattern(StringBuffer result, boolean escapeUnprintable)
  {
    return _generatePattern(result, escapeUnprintable, true);
  }
  







  public StringBuffer _generatePattern(StringBuffer result, boolean escapeUnprintable, boolean includeStrings)
  {
    result.append('[');
    










    int count = getRangeCount();
    



    if ((count > 1) && (getRangeStart(0) == 0) && (getRangeEnd(count - 1) == 1114111))
    {



      result.append('^');
      
      for (int i = 1; i < count; i++) {
        int start = getRangeEnd(i - 1) + 1;
        int end = getRangeStart(i) - 1;
        _appendToPat(result, start, escapeUnprintable);
        if (start != end) {
          if (start + 1 != end) {
            result.append('-');
          }
          _appendToPat(result, end, escapeUnprintable);
        }
        
      }
    }
    else
    {
      for (int i = 0; i < count; i++) {
        int start = getRangeStart(i);
        int end = getRangeEnd(i);
        _appendToPat(result, start, escapeUnprintable);
        if (start != end) {
          if (start + 1 != end) {
            result.append('-');
          }
          _appendToPat(result, end, escapeUnprintable);
        }
      }
    }
    
    if ((includeStrings) && (strings.size() > 0)) {
      for (String s : strings) {
        result.append('{');
        _appendToPat(result, s, escapeUnprintable);
        result.append('}');
      }
    }
    return result.append(']');
  }
  







  public int size()
  {
    int n = 0;
    int count = getRangeCount();
    for (int i = 0; i < count; i++) {
      n += getRangeEnd(i) - getRangeStart(i) + 1;
    }
    return n + strings.size();
  }
  





  public boolean isEmpty()
  {
    return (len == 1) && (strings.size() == 0);
  }
  














  public boolean matchesIndexValue(int v)
  {
    for (int i = 0; i < getRangeCount(); i++) {
      int low = getRangeStart(i);
      int high = getRangeEnd(i);
      if ((low & 0xFF00) == (high & 0xFF00)) {
        if (((low & 0xFF) <= v) && (v <= (high & 0xFF))) {
          return true;
        }
      } else if (((low & 0xFF) <= v) || (v <= (high & 0xFF))) {
        return true;
      }
    }
    if (strings.size() != 0) {
      for (String s : strings)
      {




        int c = UTF16.charAt(s, 0);
        if ((c & 0xFF) == v) {
          return true;
        }
      }
    }
    return false;
  }
  








  public int matches(Replaceable text, int[] offset, int limit, boolean incremental)
  {
    if (offset[0] == limit)
    {


      if (contains(65535)) {
        return incremental ? 1 : 2;
      }
      return 0;
    }
    
    if (strings.size() != 0)
    {







      boolean forward = offset[0] < limit;
      



      char firstChar = text.charAt(offset[0]);
      


      int highWaterLength = 0;
      
      for (String trial : strings)
      {




        char c = trial.charAt(forward ? 0 : trial.length() - 1);
        


        if ((forward) && (c > firstChar)) break;
        if (c == firstChar)
        {
          int length = matchRest(text, offset[0], limit, trial);
          
          if (incremental) {
            int maxLen = forward ? limit - offset[0] : offset[0] - limit;
            if (length == maxLen)
            {
              return 1;
            }
          }
          
          if (length == trial.length())
          {
            if (length > highWaterLength) {
              highWaterLength = length;
            }
            

            if ((forward) && (length < highWaterLength)) {
              break;
            }
          }
        }
      }
      


      if (highWaterLength != 0) {
        offset[0] += (forward ? highWaterLength : -highWaterLength);
        return 2;
      }
    }
    return super.matches(text, offset, limit, incremental);
  }
  






















  private static int matchRest(Replaceable text, int start, int limit, String s)
  {
    int slen = s.length();
    int maxLen; if (start < limit) {
      int maxLen = limit - start;
      if (maxLen > slen) maxLen = slen;
      for (int i = 1; i < maxLen; i++) {
        if (text.charAt(start + i) != s.charAt(i)) return 0;
      }
    } else {
      maxLen = start - limit;
      if (maxLen > slen) maxLen = slen;
      slen--;
      for (int i = 1; i < maxLen; i++) {
        if (text.charAt(start - i) != s.charAt(slen - i)) return 0;
      }
    }
    return maxLen;
  }
  

  /**
   * @deprecated
   */
  public int matchesAt(CharSequence text, int offset)
  {
    int lastLen = -1;
    
    if (strings.size() != 0) {
      char firstChar = text.charAt(offset);
      String trial = null;
      
      Iterator<String> it = strings.iterator();
      while (it.hasNext()) {
        trial = (String)it.next();
        char firstStringChar = trial.charAt(0);
        if (firstStringChar >= firstChar) {
          if (firstStringChar > firstChar)
            break label135;
        }
      }
      for (;;) {
        int tempLen = matchesAt(text, offset, trial);
        if (lastLen > tempLen) break;
        lastLen = tempLen;
        if (!it.hasNext()) break;
        trial = (String)it.next();
      }
    }
    label135:
    if (lastLen < 2) {
      int cp = UTF16.charAt(text, offset);
      if (contains(cp)) { lastLen = UTF16.getCharCount(cp);
      }
    }
    return offset + lastLen;
  }
  







  private static int matchesAt(CharSequence text, int offsetInText, CharSequence substring)
  {
    int len = substring.length();
    int textLength = text.length();
    if (textLength + offsetInText > len) {
      return -1;
    }
    int i = 0;
    for (int j = offsetInText; i < len; j++) {
      char pc = substring.charAt(i);
      char tc = text.charAt(j);
      if (pc != tc) { return -1;
      }
      i++;
    }
    


    return i;
  }
  






  public void addMatchSetTo(UnicodeSet toUnionTo)
  {
    toUnionTo.addAll(this);
  }
  







  public int indexOf(int c)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c, 6));
    }
    int i = 0;
    int n = 0;
    for (;;) {
      int start = list[(i++)];
      if (c < start) {
        return -1;
      }
      int limit = list[(i++)];
      if (c < limit) {
        return n + c - start;
      }
      n += limit - start;
    }
  }
  




  public int charAt(int index)
  {
    int i;
    


    if (index >= 0)
    {


      int len2 = len & 0xFFFFFFFE;
      for (i = 0; i < len2;) {
        int start = list[(i++)];
        int count = list[(i++)] - start;
        if (index < count) {
          return start + index;
        }
        index -= count;
      }
    }
    return -1;
  }
  











  public UnicodeSet add(int start, int end)
  {
    checkFrozen();
    return add_unchecked(start, end);
  }
  






  public UnicodeSet addAll(int start, int end)
  {
    checkFrozen();
    return add_unchecked(start, end);
  }
  
  private UnicodeSet add_unchecked(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start < end) {
      add(range(start, end), 2, 0);
    } else if (start == end) {
      add(start);
    }
    return this;
  }
  
























  public final UnicodeSet add(int c)
  {
    checkFrozen();
    return add_unchecked(c);
  }
  
  private final UnicodeSet add_unchecked(int c)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c, 6));
    }
    



    int i = findCodePoint(c);
    

    if ((i & 0x1) != 0) { return this;
    }
    














    if (c == list[i] - 1)
    {
      list[i] = c;
      
      if (c == 1114111) {
        ensureCapacity(len + 1);
        list[(len++)] = 1114112;
      }
      if ((i > 0) && (c == list[(i - 1)]))
      {




        System.arraycopy(list, i + 1, list, i - 1, len - i - 1);
        len -= 2;
      }
      
    }
    else if ((i > 0) && (c == list[(i - 1)]))
    {
      list[(i - 1)] += 1;








    }
    else
    {








      if (len + 2 > list.length) {
        int[] temp = new int[len + 2 + 16];
        if (i != 0) System.arraycopy(list, 0, temp, 0, i);
        System.arraycopy(list, i, temp, i + 2, len - i);
        list = temp;
      } else {
        System.arraycopy(list, i, list, i + 2, len - i);
      }
      
      list[i] = c;
      list[(i + 1)] = (c + 1);
      len += 2;
    }
    
    pat = null;
    return this;
  }
  









  public final UnicodeSet add(CharSequence s)
  {
    checkFrozen();
    int cp = getSingleCP(s);
    if (cp < 0) {
      strings.add(s.toString());
      pat = null;
    } else {
      add_unchecked(cp, cp);
    }
    return this;
  }
  




  private static int getSingleCP(CharSequence s)
  {
    if (s.length() < 1) {
      throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
    }
    if (s.length() > 2) return -1;
    if (s.length() == 1) { return s.charAt(0);
    }
    
    int cp = UTF16.charAt(s, 0);
    if (cp > 65535) {
      return cp;
    }
    return -1;
  }
  






  public final UnicodeSet addAll(CharSequence s)
  {
    checkFrozen();
    int cp;
    for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(s, i);
      add_unchecked(cp, cp);
    }
    return this;
  }
  






  public final UnicodeSet retainAll(String s)
  {
    return retainAll(fromAll(s));
  }
  






  public final UnicodeSet complementAll(String s)
  {
    return complementAll(fromAll(s));
  }
  






  public final UnicodeSet removeAll(String s)
  {
    return removeAll(fromAll(s));
  }
  




  public final UnicodeSet removeAllStrings()
  {
    checkFrozen();
    if (strings.size() != 0) {
      strings.clear();
      pat = null;
    }
    return this;
  }
  






  public static UnicodeSet from(String s)
  {
    return new UnicodeSet().add(s);
  }
  






  public static UnicodeSet fromAll(String s)
  {
    return new UnicodeSet().addAll(s);
  }
  











  public UnicodeSet retain(int start, int end)
  {
    checkFrozen();
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start <= end) {
      retain(range(start, end), 2, 0);
    } else {
      clear();
    }
    return this;
  }
  







  public final UnicodeSet retain(int c)
  {
    return retain(c, c);
  }
  







  public final UnicodeSet retain(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      boolean isIn = strings.contains(s);
      if ((isIn) && (size() == 1)) {
        return this;
      }
      clear();
      strings.add(s);
      pat = null;
    } else {
      retain(cp, cp);
    }
    return this;
  }
  











  public UnicodeSet remove(int start, int end)
  {
    checkFrozen();
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start <= end) {
      retain(range(start, end), 2, 2);
    }
    return this;
  }
  







  public final UnicodeSet remove(int c)
  {
    return remove(c, c);
  }
  







  public final UnicodeSet remove(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      strings.remove(s);
      pat = null;
    } else {
      remove(cp, cp);
    }
    return this;
  }
  











  public UnicodeSet complement(int start, int end)
  {
    checkFrozen();
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start <= end) {
      xor(range(start, end), 2, 0);
    }
    pat = null;
    return this;
  }
  





  public final UnicodeSet complement(int c)
  {
    return complement(c, c);
  }
  




  public UnicodeSet complement()
  {
    checkFrozen();
    if (list[0] == 0) {
      System.arraycopy(list, 1, list, 0, len - 1);
      len -= 1;
    } else {
      ensureCapacity(len + 1);
      System.arraycopy(list, 0, list, 1, len);
      list[0] = 0;
      len += 1;
    }
    pat = null;
    return this;
  }
  








  public final UnicodeSet complement(String s)
  {
    checkFrozen();
    int cp = getSingleCP(s);
    if (cp < 0) {
      if (strings.contains(s)) {
        strings.remove(s);
      } else {
        strings.add(s);
      }
      pat = null;
    } else {
      complement(cp, cp);
    }
    return this;
  }
  





  public boolean contains(int c)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c, 6));
    }
    if (bmpSet != null) {
      return bmpSet.contains(c);
    }
    if (stringSpan != null) {
      return stringSpan.contains(c);
    }
    









    int i = findCodePoint(c);
    
    return (i & 0x1) != 0;
  }
  




















  private final int findCodePoint(int c)
  {
    if (c < list[0]) { return 0;
    }
    
    if ((len >= 2) && (c >= list[(len - 2)])) return len - 1;
    int lo = 0;
    int hi = len - 1;
    
    for (;;)
    {
      int i = lo + hi >>> 1;
      if (i == lo) return hi;
      if (c < list[i]) {
        hi = i;
      } else {
        lo = i;
      }
    }
  }
  


























































































































  public boolean contains(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    



    int i = findCodePoint(start);
    return ((i & 0x1) != 0) && (end < list[i]);
  }
  







  public final boolean contains(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      return strings.contains(s);
    }
    return contains(cp);
  }
  











  public boolean containsAll(UnicodeSet b)
  {
    int[] listB = list;
    boolean needA = true;
    boolean needB = true;
    int aPtr = 0;
    int bPtr = 0;
    int aLen = len - 1;
    int bLen = len - 1;
    int startA = 0;int startB = 0;int limitA = 0;int limitB = 0;
    for (;;)
    {
      if (needA) {
        if (aPtr >= aLen)
        {
          if ((needB) && (bPtr >= bLen)) {
            break label168;
          }
          return false;
        }
        startA = list[(aPtr++)];
        limitA = list[(aPtr++)];
      }
      if (needB) {
        if (bPtr >= bLen) {
          break label168;
        }
        
        startB = listB[(bPtr++)];
        limitB = listB[(bPtr++)];
      }
      
      if (startB >= limitA) {
        needA = true;
        needB = false;
      }
      else
      {
        if ((startB < startA) || (limitB > limitA)) break;
        needA = false;
        needB = true;
      }
    }
    
    return false;
    
    label168:
    if (!strings.containsAll(strings)) return false;
    return true;
  }
  















  public boolean containsAll(String s)
  {
    int cp;
    













    for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(s, i);
      if (!contains(cp)) {
        if (strings.size() == 0) {
          return false;
        }
        return containsAll(s, 0);
      }
    }
    return true;
  }
  





  private boolean containsAll(String s, int i)
  {
    if (i >= s.length()) {
      return true;
    }
    int cp = UTF16.charAt(s, i);
    if ((contains(cp)) && (containsAll(s, i + UTF16.getCharCount(cp)))) {
      return true;
    }
    for (String setStr : strings) {
      if ((s.startsWith(setStr, i)) && (containsAll(s, i + setStr.length()))) {
        return true;
      }
    }
    return false;
  }
  



  /**
   * @deprecated
   */
  public String getRegexEquivalent()
  {
    if (strings.size() == 0) {
      return toString();
    }
    StringBuffer result = new StringBuffer("(?:");
    _generatePattern(result, true, false);
    for (String s : strings) {
      result.append('|');
      _appendToPat(result, s, true);
    }
    return ")";
  }
  







  public boolean containsNone(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    int i = -1;
    for (;;) {
      if (start < list[(++i)]) break;
    }
    return ((i & 0x1) == 0) && (end < list[i]);
  }
  










  public boolean containsNone(UnicodeSet b)
  {
    int[] listB = list;
    boolean needA = true;
    boolean needB = true;
    int aPtr = 0;
    int bPtr = 0;
    int aLen = len - 1;
    int bLen = len - 1;
    int startA = 0;int startB = 0;int limitA = 0;int limitB = 0;
    for (;;)
    {
      if (needA) {
        if (aPtr >= aLen) {
          break label147;
        }
        
        startA = list[(aPtr++)];
        limitA = list[(aPtr++)];
      }
      if (needB) {
        if (bPtr >= bLen) {
          break label147;
        }
        
        startB = listB[(bPtr++)];
        limitB = listB[(bPtr++)];
      }
      
      if (startB >= limitA) {
        needA = true;
        needB = false;
      }
      else
      {
        if (startA < limitB) break;
        needA = false;
        needB = true;
      }
    }
    
    return false;
    
    label147:
    if (!SortedSetRelation.hasRelation(strings, 5, strings)) return false;
    return true;
  }
  





























  public boolean containsNone(String s)
  {
    return span(s, SpanCondition.NOT_CONTAINED) == s.length();
  }
  







  public final boolean containsSome(int start, int end)
  {
    return !containsNone(start, end);
  }
  






  public final boolean containsSome(UnicodeSet s)
  {
    return !containsNone(s);
  }
  






  public final boolean containsSome(String s)
  {
    return !containsNone(s);
  }
  










  public UnicodeSet addAll(UnicodeSet c)
  {
    checkFrozen();
    add(list, len, 0);
    strings.addAll(strings);
    return this;
  }
  









  public UnicodeSet retainAll(UnicodeSet c)
  {
    checkFrozen();
    retain(list, len, 0);
    strings.retainAll(strings);
    return this;
  }
  









  public UnicodeSet removeAll(UnicodeSet c)
  {
    checkFrozen();
    retain(list, len, 2);
    strings.removeAll(strings);
    return this;
  }
  








  public UnicodeSet complementAll(UnicodeSet c)
  {
    checkFrozen();
    xor(list, len, 0);
    SortedSetRelation.doOperation(strings, 5, strings);
    return this;
  }
  




  public UnicodeSet clear()
  {
    checkFrozen();
    list[0] = 1114112;
    len = 1;
    pat = null;
    strings.clear();
    return this;
  }
  






  public int getRangeCount()
  {
    return len / 2;
  }
  








  public int getRangeStart(int index)
  {
    return list[(index * 2)];
  }
  








  public int getRangeEnd(int index)
  {
    return list[(index * 2 + 1)] - 1;
  }
  




  public UnicodeSet compact()
  {
    checkFrozen();
    if (len != list.length) {
      int[] temp = new int[len];
      System.arraycopy(list, 0, temp, 0, len);
      list = temp;
    }
    rangeList = null;
    buffer = null;
    return this;
  }
  










  public boolean equals(Object o)
  {
    if (o == null) {
      return false;
    }
    if (this == o) {
      return true;
    }
    try {
      UnicodeSet that = (UnicodeSet)o;
      if (len != len) return false;
      for (int i = 0; i < len; i++) {
        if (list[i] != list[i]) return false;
      }
      if (!strings.equals(strings)) return false;
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  






  public int hashCode()
  {
    int result = len;
    for (int i = 0; i < len; i++) {
      result *= 1000003;
      result += list[i];
    }
    return result;
  }
  



  public String toString()
  {
    return toPattern(true);
  }
  






























  /**
   * @deprecated
   */
  public UnicodeSet applyPattern(String pattern, ParsePosition pos, SymbolTable symbols, int options)
  {
    boolean parsePositionWasNull = pos == null;
    if (parsePositionWasNull) {
      pos = new ParsePosition(0);
    }
    
    StringBuffer rebuiltPat = new StringBuffer();
    RuleCharacterIterator chars = new RuleCharacterIterator(pattern, symbols, pos);
    
    applyPattern(chars, symbols, rebuiltPat, options);
    if (chars.inVariable()) {
      syntaxError(chars, "Extra chars in variable value");
    }
    pat = rebuiltPat.toString();
    if (parsePositionWasNull) {
      int i = pos.getIndex();
      

      if ((options & 0x1) != 0) {
        i = PatternProps.skipWhiteSpace(pattern, i);
      }
      
      if (i != pattern.length()) {
        throw new IllegalArgumentException("Parse of \"" + pattern + "\" failed at " + i);
      }
    }
    
    return this;
  }
  



















  void applyPattern(RuleCharacterIterator chars, SymbolTable symbols, StringBuffer rebuiltPat, int options)
  {
    int opts = 3;
    
    if ((options & 0x1) != 0) {
      opts |= 0x4;
    }
    
    StringBuffer patBuf = new StringBuffer();StringBuffer buf = null;
    boolean usePat = false;
    UnicodeSet scratch = null;
    Object backup = null;
    


    int lastItem = 0;int lastChar = 0;int mode = 0;
    char op = '\000';
    
    boolean invert = false;
    
    clear();
    
    while ((mode != 2) && (!chars.atEnd()))
    {










      int c = 0;
      boolean literal = false;
      UnicodeSet nested = null;
      



      int setMode = 0;
      if (resemblesPropertyPattern(chars, opts)) {
        setMode = 2;





      }
      else
      {




        backup = chars.getPos(backup);
        c = chars.next(opts);
        literal = chars.isEscaped();
        
        if ((c == 91) && (!literal)) {
          if (mode == 1) {
            chars.setPos(backup);
            setMode = 1;
          }
          else {
            mode = 1;
            patBuf.append('[');
            backup = chars.getPos(backup);
            c = chars.next(opts);
            literal = chars.isEscaped();
            if ((c == 94) && (!literal)) {
              invert = true;
              patBuf.append('^');
              backup = chars.getPos(backup);
              c = chars.next(opts);
              literal = chars.isEscaped();
            }
            

            if (c == 45) {
              literal = true;
            }
            else {
              chars.setPos(backup);
            }
          }
        }
        else if (symbols != null) {
          UnicodeMatcher m = symbols.lookupMatcher(c);
          if (m != null) {
            try {
              nested = (UnicodeSet)m;
              setMode = 3;
            } catch (ClassCastException e) {
              syntaxError(chars, "Syntax error");
            }
          }
        }
      }
      





      if (setMode != 0) {
        if (lastItem == 1) {
          if (op != 0) {
            syntaxError(chars, "Char expected after operator");
          }
          add_unchecked(lastChar, lastChar);
          _appendToPat(patBuf, lastChar, false);
          lastItem = op = 0;
        }
        
        if ((op == '-') || (op == '&')) {
          patBuf.append(op);
        }
        
        if (nested == null) {
          if (scratch == null) scratch = new UnicodeSet();
          nested = scratch;
        }
        switch (setMode) {
        case 1: 
          nested.applyPattern(chars, symbols, patBuf, options);
          break;
        case 2: 
          chars.skipIgnored(opts);
          nested.applyPropertyPattern(chars, patBuf, symbols);
          break;
        case 3: 
          nested._toPattern(patBuf, false);
        }
        
        
        usePat = true;
        
        if (mode == 0)
        {
          set(nested);
          mode = 2;
          break;
        }
        
        switch (op) {
        case '-': 
          removeAll(nested);
          break;
        case '&': 
          retainAll(nested);
          break;
        case '\000': 
          addAll(nested);
        }
        
        
        op = '\000';
        lastItem = 2;

      }
      else
      {
        if (mode == 0) {
          syntaxError(chars, "Missing '['");
        }
        




        if (!literal) {
          switch (c) {
          case 93: 
            if (lastItem == 1) {
              add_unchecked(lastChar, lastChar);
              _appendToPat(patBuf, lastChar, false);
            }
            
            if (op == '-') {
              add_unchecked(op, op);
              patBuf.append(op);
            } else if (op == '&') {
              syntaxError(chars, "Trailing '&'");
            }
            patBuf.append(']');
            mode = 2;
            break;
          case 45: 
            if (op == 0) {
              if (lastItem != 0) {
                op = (char)c;
                continue;
              }
              
              add_unchecked(c, c);
              c = chars.next(opts);
              literal = chars.isEscaped();
              if ((c == 93) && (!literal)) {
                patBuf.append("-]");
                mode = 2;
                continue;
              }
            }
            
            syntaxError(chars, "'-' not after char or set");
            break;
          case 38: 
            if ((lastItem == 2) && (op == 0)) {
              op = (char)c;
              continue;
            }
            syntaxError(chars, "'&' not after set");
            break;
          case 94: 
            syntaxError(chars, "'^' not after '['");
            break;
          case 123: 
            if (op != 0) {
              syntaxError(chars, "Missing operand after operator");
            }
            if (lastItem == 1) {
              add_unchecked(lastChar, lastChar);
              _appendToPat(patBuf, lastChar, false);
            }
            lastItem = 0;
            if (buf == null) {
              buf = new StringBuffer();
            } else {
              buf.setLength(0);
            }
            boolean ok = false;
            while (!chars.atEnd()) {
              c = chars.next(opts);
              literal = chars.isEscaped();
              if ((c == 125) && (!literal)) {
                ok = true;
                break;
              }
              UTF16.append(buf, c);
            }
            if ((buf.length() < 1) || (!ok)) {
              syntaxError(chars, "Invalid multicharacter string");
            }
            


            add(buf.toString());
            patBuf.append('{');
            _appendToPat(patBuf, buf.toString(), false);
            patBuf.append('}');
            break;
          





          case 36: 
            backup = chars.getPos(backup);
            c = chars.next(opts);
            literal = chars.isEscaped();
            boolean anchor = (c == 93) && (!literal);
            if ((symbols == null) && (!anchor)) {
              c = 36;
              chars.setPos(backup);
            }
            else {
              if ((anchor) && (op == 0)) {
                if (lastItem == 1) {
                  add_unchecked(lastChar, lastChar);
                  _appendToPat(patBuf, lastChar, false);
                }
                add_unchecked(65535);
                usePat = true;
                patBuf.append('$').append(']');
                mode = 2;
                continue;
              }
              syntaxError(chars, "Unquoted '$'"); }
            break;
          





          }
          
        } else {
          switch (lastItem) {
          case 0: 
            lastItem = 1;
            lastChar = c;
            break;
          case 1: 
            if (op == '-') {
              if (lastChar >= c)
              {

                syntaxError(chars, "Invalid range");
              }
              add_unchecked(lastChar, c);
              _appendToPat(patBuf, lastChar, false);
              patBuf.append(op);
              _appendToPat(patBuf, c, false);
              lastItem = op = 0;
            } else {
              add_unchecked(lastChar, lastChar);
              _appendToPat(patBuf, lastChar, false);
              lastChar = c;
            }
            break;
          case 2: 
            if (op != 0) {
              syntaxError(chars, "Set expected after operator");
            }
            lastChar = c;
            lastItem = 1;
          }
        }
      }
    }
    if (mode != 2) {
      syntaxError(chars, "Missing ']'");
    }
    
    chars.skipIgnored(opts);
    






    if ((options & 0x2) != 0) {
      closeOver(2);
    }
    if (invert) {
      complement();
    }
    


    if (usePat) {
      rebuiltPat.append(patBuf.toString());
    } else {
      _generatePattern(rebuiltPat, false, true);
    }
  }
  
  private static void syntaxError(RuleCharacterIterator chars, String msg) {
    throw new IllegalArgumentException("Error: " + msg + " at \"" + Utility.escape(chars.toString()) + '"');
  }
  






  public <T extends Collection<String>> T addAllTo(T target)
  {
    return addAllTo(this, target);
  }
  





  public String[] addAllTo(String[] target)
  {
    return (String[])addAllTo(this, target);
  }
  



  public static String[] toArray(UnicodeSet set)
  {
    return (String[])addAllTo(set, new String[set.size()]);
  }
  





  public UnicodeSet add(Collection<?> source)
  {
    return addAll(source);
  }
  






  public UnicodeSet addAll(Collection<?> source)
  {
    checkFrozen();
    for (Object o : source) {
      add(o.toString());
    }
    return this;
  }
  



  private void ensureCapacity(int newLen)
  {
    if (newLen <= list.length) return;
    int[] temp = new int[newLen + 16];
    System.arraycopy(list, 0, temp, 0, len);
    list = temp;
  }
  
  private void ensureBufferCapacity(int newLen) {
    if ((buffer != null) && (newLen <= buffer.length)) return;
    buffer = new int[newLen + 16];
  }
  


  private int[] range(int start, int end)
  {
    if (rangeList == null) {
      rangeList = new int[] { start, end + 1, 1114112 };
    } else {
      rangeList[0] = start;
      rangeList[1] = (end + 1);
    }
    return rangeList;
  }
  






  private UnicodeSet xor(int[] other, int otherLen, int polarity)
  {
    ensureBufferCapacity(len + otherLen);
    int i = 0;int j = 0;int k = 0;
    int a = list[(i++)];
    

    int b;
    
    if ((polarity == 1) || (polarity == 2)) {
      int b = 0;
      if (other[j] == 0) {
        j++;
        b = other[j];
      }
    }
    else {
      b = other[(j++)];
    }
    
    for (;;)
    {
      if (a < b) {
        buffer[(k++)] = a;
        a = list[(i++)];
      } else if (b < a) {
        buffer[(k++)] = b;
        b = other[(j++)];
      } else { if (a == 1114112)
          break;
        a = list[(i++)];
        b = other[(j++)];
      } }
    buffer[(k++)] = 1114112;
    len = k;
    



    int[] temp = list;
    list = buffer;
    buffer = temp;
    pat = null;
    return this;
  }
  




  private UnicodeSet add(int[] other, int otherLen, int polarity)
  {
    ensureBufferCapacity(len + otherLen);
    int i = 0;int j = 0;int k = 0;
    int a = list[(i++)];
    int b = other[(j++)];
    

    for (;;)
    {
      switch (polarity) {
      case 0: 
        if (a < b)
        {
          if ((k > 0) && (a <= buffer[(k - 1)]))
          {
            a = max(list[i], buffer[(--k)]);
          }
          else {
            buffer[(k++)] = a;
            a = list[i];
          }
          i++;
          polarity ^= 0x1;
        } else if (b < a) {
          if ((k > 0) && (b <= buffer[(k - 1)])) {
            b = max(other[j], buffer[(--k)]);
          } else {
            buffer[(k++)] = b;
            b = other[j];
          }
          j++;
          polarity ^= 0x2;
        } else {
          if (a == 1114112) {
            break label620;
          }
          if ((k > 0) && (a <= buffer[(k - 1)])) {
            a = max(list[i], buffer[(--k)]);
          }
          else {
            buffer[(k++)] = a;
            a = list[i];
          }
          i++;
          polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 3: 
        if (b <= a) {
          if (a == 1114112) break label620;
          buffer[(k++)] = a;
        } else {
          if (b == 1114112) break label620;
          buffer[(k++)] = b;
        }
        a = list[(i++)];polarity ^= 0x1;
        b = other[(j++)];polarity ^= 0x2;
        break;
      case 1: 
        if (a < b) {
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label620;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 2: 
        if (b < a) {
          buffer[(k++)] = b;b = other[(j++)];polarity ^= 0x2;
        } else if (a < b) {
          a = list[(i++)];polarity ^= 0x1;
        } else {
          if (a == 1114112) break label620;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break; }
    }
    label620:
    buffer[(k++)] = 1114112;
    len = k;
    
    int[] temp = list;
    list = buffer;
    buffer = temp;
    pat = null;
    return this;
  }
  




  private UnicodeSet retain(int[] other, int otherLen, int polarity)
  {
    ensureBufferCapacity(len + otherLen);
    int i = 0;int j = 0;int k = 0;
    int a = list[(i++)];
    int b = other[(j++)];
    

    for (;;)
    {
      switch (polarity) {
      case 0: 
        if (a < b) {
          a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label508;
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 3: 
        if (a < b) {
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          buffer[(k++)] = b;b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label508;
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 1: 
        if (a < b) {
          a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          buffer[(k++)] = b;b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label508;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 2: 
        if (b < a) {
          b = other[(j++)];polarity ^= 0x2;
        } else if (a < b) {
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
        } else {
          if (a == 1114112) break label508;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break; }
    }
    label508:
    buffer[(k++)] = 1114112;
    len = k;
    
    int[] temp = list;
    list = buffer;
    buffer = temp;
    pat = null;
    return this;
  }
  
  private static final int max(int a, int b) {
    return a > b ? a : b;
  }
  
  private static abstract interface Filter
  {
    public abstract boolean contains(int paramInt);
  }
  
  private static class NumericValueFilter
    implements UnicodeSet.Filter
  {
    double value;
    
    NumericValueFilter(double value) { this.value = value; }
    
    public boolean contains(int ch) { return UCharacter.getUnicodeNumericValue(ch) == value; }
  }
  
  private static class GeneralCategoryMaskFilter implements UnicodeSet.Filter {
    int mask;
    
    GeneralCategoryMaskFilter(int mask) { this.mask = mask; }
    
    public boolean contains(int ch) { return (1 << UCharacter.getType(ch) & mask) != 0; }
  }
  
  private static class IntPropertyFilter implements UnicodeSet.Filter {
    int prop;
    int value;
    
    IntPropertyFilter(int prop, int value) {
      this.prop = prop;
      this.value = value;
    }
    
    public boolean contains(int ch) { return UCharacter.getIntPropertyValue(ch, prop) == value; }
  }
  
  private static class ScriptExtensionsFilter implements UnicodeSet.Filter {
    int script;
    
    ScriptExtensionsFilter(int script) { this.script = script; }
    
    public boolean contains(int c) { return UScript.hasScript(c, script); }
  }
  


  private static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
  public static final int IGNORE_SPACE = 1;
  public static final int CASE = 2;
  
  private static class VersionFilter implements UnicodeSet.Filter { VersionFilter(VersionInfo version) { this.version = version; }
    
    public boolean contains(int ch) { VersionInfo v = UCharacter.getAge(ch);
      

      return (v != UnicodeSet.NO_VERSION) && (v.compareTo(version) <= 0);
    }
    
    VersionInfo version;
  }
  
  private static synchronized UnicodeSet getInclusions(int src) { if (INCLUSIONS == null) {
      INCLUSIONS = new UnicodeSet[12];
    }
    if (INCLUSIONS[src] == null) {
      UnicodeSet incl = new UnicodeSet();
      switch (src) {
      case 1: 
        UCharacterProperty.INSTANCE.addPropertyStarts(incl);
        break;
      case 2: 
        UCharacterProperty.INSTANCE.upropsvec_addPropertyStarts(incl);
        break;
      case 6: 
        UCharacterProperty.INSTANCE.addPropertyStarts(incl);
        UCharacterProperty.INSTANCE.upropsvec_addPropertyStarts(incl);
        break;
      case 7: 
        getNFCInstanceimpl.addPropertyStarts(incl);
        UCaseProps.INSTANCE.addPropertyStarts(incl);
        break;
      case 8: 
        getNFCInstanceimpl.addPropertyStarts(incl);
        break;
      case 9: 
        getNFKCInstanceimpl.addPropertyStarts(incl);
        break;
      case 10: 
        getNFKC_CFInstanceimpl.addPropertyStarts(incl);
        break;
      case 11: 
        getNFCInstanceimpl.addCanonIterPropertyStarts(incl);
        break;
      case 4: 
        UCaseProps.INSTANCE.addPropertyStarts(incl);
        break;
      case 5: 
        UBiDiProps.INSTANCE.addPropertyStarts(incl);
        break;
      case 3: default: 
        throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + src + ")");
      }
      INCLUSIONS[src] = incl;
    }
    return INCLUSIONS[src];
  }
  












  private UnicodeSet applyFilter(Filter filter, int src)
  {
    clear();
    
    int startHasProperty = -1;
    UnicodeSet inclusions = getInclusions(src);
    int limitRange = inclusions.getRangeCount();
    
    for (int j = 0; j < limitRange; j++)
    {
      int start = inclusions.getRangeStart(j);
      int end = inclusions.getRangeEnd(j);
      

      for (int ch = start; ch <= end; ch++)
      {

        if (filter.contains(ch)) {
          if (startHasProperty < 0) {
            startHasProperty = ch;
          }
        } else if (startHasProperty >= 0) {
          add_unchecked(startHasProperty, ch - 1);
          startHasProperty = -1;
        }
      }
    }
    if (startHasProperty >= 0) {
      add_unchecked(startHasProperty, 1114111);
    }
    
    return this;
  }
  




  private static String mungeCharName(String source)
  {
    source = PatternProps.trimWhiteSpace(source);
    StringBuilder buf = null;
    for (int i = 0; i < source.length(); i++) {
      char ch = source.charAt(i);
      if (PatternProps.isWhiteSpace(ch)) {
        if (buf == null)
          buf = new StringBuilder().append(source, 0, i); else {
          if (buf.charAt(buf.length() - 1) == ' ')
            continue;
        }
        ch = ' ';
      }
      else if (buf != null) {
        buf.append(ch);
      }
    }
    return buf == null ? source : buf.toString();
  }
  


























  public UnicodeSet applyIntPropertyValue(int prop, int value)
  {
    checkFrozen();
    if (prop == 8192) {
      applyFilter(new GeneralCategoryMaskFilter(value), 1);
    } else if (prop == 28672) {
      applyFilter(new ScriptExtensionsFilter(value), 2);
    } else {
      applyFilter(new IntPropertyFilter(prop, value), UCharacterProperty.INSTANCE.getSource(prop));
    }
    return this;
  }
  




























  public UnicodeSet applyPropertyAlias(String propertyAlias, String valueAlias)
  {
    return applyPropertyAlias(propertyAlias, valueAlias, null);
  }
  











  public UnicodeSet applyPropertyAlias(String propertyAlias, String valueAlias, SymbolTable symbols)
  {
    checkFrozen();
    

    boolean mustNotBeEmpty = false;boolean invert = false;
    
    if ((symbols != null) && ((symbols instanceof XSymbolTable)) && (((XSymbolTable)symbols).applyPropertyAlias(propertyAlias, valueAlias, this)))
    {

      return this;
    }
    
    if ((XSYMBOL_TABLE != null) && 
      (XSYMBOL_TABLE.applyPropertyAlias(propertyAlias, valueAlias, this))) {
      return this;
    }
    int v;
    int p;
    if (valueAlias.length() > 0) {
      int p = UCharacter.getPropertyEnum(propertyAlias);
      

      if (p == 4101) {
        p = 8192;
      }
      int v;
      if (((p >= 0) && (p < 57)) || ((p >= 4096) && (p < 4117)) || ((p >= 8192) && (p < 8193)))
      {
        try
        {
          v = UCharacter.getPropertyValueEnum(p, valueAlias);
        } catch (IllegalArgumentException e) {
          int v;
          if ((p == 4098) || (p == 4112) || (p == 4113))
          {

            v = Integer.parseInt(PatternProps.trimWhiteSpace(valueAlias));
            



            if ((v < 0) || (v > 255)) throw e;
          } else {
            throw e;
          }
          
        }
        
      } else {
        switch (p)
        {
        case 12288: 
          double value = Double.parseDouble(PatternProps.trimWhiteSpace(valueAlias));
          applyFilter(new NumericValueFilter(value), 1);
          return this;
        




        case 16389: 
          String buf = mungeCharName(valueAlias);
          int ch = UCharacter.getCharFromExtendedName(buf);
          if (ch == -1) {
            throw new IllegalArgumentException("Invalid character name");
          }
          clear();
          add_unchecked(ch);
          return this;
        

        case 16395: 
          throw new IllegalArgumentException("Unicode_1_Name (na1) not supported");
        



        case 16384: 
          VersionInfo version = VersionInfo.getInstance(mungeCharName(valueAlias));
          applyFilter(new VersionFilter(version), 2);
          return this;
        
        case 28672: 
          v = UCharacter.getPropertyValueEnum(4106, valueAlias);
          
          break;
        

        default: 
          throw new IllegalArgumentException("Unsupported property");
        

        }
        
      }
    }
    else
    {
      UPropertyAliases pnames = UPropertyAliases.INSTANCE;
      p = 8192;
      v = pnames.getPropertyValueEnum(p, propertyAlias);
      if (v == -1) {
        p = 4106;
        v = pnames.getPropertyValueEnum(p, propertyAlias);
        if (v == -1) {
          p = pnames.getPropertyEnum(propertyAlias);
          if (p == -1) {
            p = -1;
          }
          if ((p >= 0) && (p < 57)) {
            v = 1;
          } else if (p == -1) {
            if (0 == UPropertyAliases.compare("ANY", propertyAlias)) {
              set(0, 1114111);
              return this; }
            if (0 == UPropertyAliases.compare("ASCII", propertyAlias)) {
              set(0, 127);
              return this; }
            if (0 == UPropertyAliases.compare("Assigned", propertyAlias))
            {
              p = 8192;
              v = 1;
              invert = true;
            }
            else {
              throw new IllegalArgumentException("Invalid property alias: " + propertyAlias + "=" + valueAlias);
            }
          }
          else
          {
            throw new IllegalArgumentException("Missing property value");
          }
        }
      }
    }
    
    applyIntPropertyValue(p, v);
    if (invert) {
      complement();
    }
    
    if ((mustNotBeEmpty) && (isEmpty()))
    {

      throw new IllegalArgumentException("Invalid property value");
    }
    
    return this;
  }
  








  private static boolean resemblesPropertyPattern(String pattern, int pos)
  {
    if (pos + 5 > pattern.length()) {
      return false;
    }
    

    return (pattern.regionMatches(pos, "[:", 0, 2)) || (pattern.regionMatches(true, pos, "\\p", 0, 2)) || (pattern.regionMatches(pos, "\\N", 0, 2));
  }
  










  private static boolean resemblesPropertyPattern(RuleCharacterIterator chars, int iterOpts)
  {
    boolean result = false;
    iterOpts &= 0xFFFFFFFD;
    Object pos = chars.getPos(null);
    int c = chars.next(iterOpts);
    if ((c == 91) || (c == 92)) {
      int d = chars.next(iterOpts & 0xFFFFFFFB);
      result = d == 58;
    }
    
    chars.setPos(pos);
    return result;
  }
  



  private UnicodeSet applyPropertyPattern(String pattern, ParsePosition ppos, SymbolTable symbols)
  {
    int pos = ppos.getIndex();
    



    if (pos + 5 > pattern.length()) {
      return null;
    }
    
    boolean posix = false;
    boolean isName = false;
    boolean invert = false;
    

    if (pattern.regionMatches(pos, "[:", 0, 2)) {
      posix = true;
      pos = PatternProps.skipWhiteSpace(pattern, pos + 2);
      if ((pos < pattern.length()) && (pattern.charAt(pos) == '^')) {
        pos++;
        invert = true;
      }
    } else if ((pattern.regionMatches(true, pos, "\\p", 0, 2)) || (pattern.regionMatches(pos, "\\N", 0, 2)))
    {
      char c = pattern.charAt(pos + 1);
      invert = c == 'P';
      isName = c == 'N';
      pos = PatternProps.skipWhiteSpace(pattern, pos + 2);
      if ((pos == pattern.length()) || (pattern.charAt(pos++) != '{'))
      {
        return null;
      }
    }
    else {
      return null;
    }
    

    int close = pattern.indexOf(posix ? ":]" : "}", pos);
    if (close < 0)
    {
      return null;
    }
    



    int equals = pattern.indexOf('=', pos);
    String valueName;
    String propName; String valueName; if ((equals >= 0) && (equals < close) && (!isName))
    {
      String propName = pattern.substring(pos, equals);
      valueName = pattern.substring(equals + 1, close);

    }
    else
    {
      propName = pattern.substring(pos, close);
      valueName = "";
      

      if (isName)
      {




        valueName = propName;
        propName = "na";
      }
    }
    
    applyPropertyAlias(propName, valueName, symbols);
    
    if (invert) {
      complement();
    }
    

    ppos.setIndex(close + (posix ? 2 : 1));
    
    return this;
  }
  










  private void applyPropertyPattern(RuleCharacterIterator chars, StringBuffer rebuiltPat, SymbolTable symbols)
  {
    String patStr = chars.lookahead();
    ParsePosition pos = new ParsePosition(0);
    applyPropertyPattern(patStr, pos, symbols);
    if (pos.getIndex() == 0) {
      syntaxError(chars, "Invalid property pattern");
    }
    chars.jumpahead(pos.getIndex());
    rebuiltPat.append(patStr.substring(0, pos.getIndex()));
  }
  






















  public static final int CASE_INSENSITIVE = 2;
  





















  public static final int ADD_CASE_MAPPINGS = 4;
  




















  private static final void addCaseMapping(UnicodeSet set, int result, StringBuilder full)
  {
    if (result >= 0) {
      if (result > 31)
      {
        set.add(result);
      }
      else {
        set.add(full.toString());
        full.setLength(0);
      }
    }
  }
  

























  public UnicodeSet closeOver(int attribute)
  {
    checkFrozen();
    if ((attribute & 0x6) != 0) {
      UCaseProps csp = UCaseProps.INSTANCE;
      UnicodeSet foldSet = new UnicodeSet(this);
      ULocale root = ULocale.ROOT;
      



      if ((attribute & 0x2) != 0) {
        strings.clear();
      }
      
      int n = getRangeCount();
      
      StringBuilder full = new StringBuilder();
      int[] locCache = new int[1];
      
      for (int i = 0; i < n; i++) {
        int start = getRangeStart(i);
        int end = getRangeEnd(i);
        
        if ((attribute & 0x2) != 0)
        {
          for (int cp = start; cp <= end; cp++) {
            csp.addCaseClosure(cp, foldSet);
          }
          
        }
        else
          for (int cp = start; cp <= end; cp++) {
            int result = csp.toFullLower(cp, null, full, root, locCache);
            addCaseMapping(foldSet, result, full);
            
            result = csp.toFullTitle(cp, null, full, root, locCache);
            addCaseMapping(foldSet, result, full);
            
            result = csp.toFullUpper(cp, null, full, root, locCache);
            addCaseMapping(foldSet, result, full);
            
            result = csp.toFullFolding(cp, full, 0);
            addCaseMapping(foldSet, result, full);
          }
      }
      BreakIterator bi;
      if (!strings.isEmpty()) {
        if ((attribute & 0x2) != 0) {
          for (String s : strings) {
            String str = UCharacter.foldCase(s, 0);
            if (!csp.addStringCaseClosure(str, foldSet)) {
              foldSet.add(str);
            }
          }
        } else {
          bi = BreakIterator.getWordInstance(root);
          for (String str : strings) {
            foldSet.add(UCharacter.toLowerCase(root, str));
            foldSet.add(UCharacter.toTitleCase(root, str, bi));
            foldSet.add(UCharacter.toUpperCase(root, str));
            foldSet.add(UCharacter.foldCase(str, 0));
          }
        }
      }
      set(foldSet);
    }
    return this;
  }
  







  public static abstract class XSymbolTable
    implements SymbolTable
  {
    public XSymbolTable() {}
    






    public UnicodeMatcher lookupMatcher(int i)
    {
      return null;
    }
    







    public boolean applyPropertyAlias(String propertyName, String propertyValue, UnicodeSet result)
    {
      return false;
    }
    



    public char[] lookup(String s)
    {
      return null;
    }
    



    public String parseReference(String text, ParsePosition pos, int limit)
    {
      return null;
    }
  }
  





  public boolean isFrozen()
  {
    return (bmpSet != null) || (stringSpan != null);
  }
  





  public UnicodeSet freeze()
  {
    if (!isFrozen())
    {




      buffer = null;
      int[] oldList; int i; if (list.length > len + 16)
      {

        int capacity = len == 0 ? 1 : len;
        oldList = list;
        list = new int[capacity];
        for (i = capacity; i-- > 0;) {
          list[i] = oldList[i];
        }
      }
      

      if (!strings.isEmpty()) {
        stringSpan = new UnicodeSetStringSpan(this, new ArrayList(strings), 63);
        if (!stringSpan.needsStringSpanUTF16())
        {




          stringSpan = null;
        }
      }
      if (stringSpan == null)
      {
        bmpSet = new BMPSet(list, len);
      }
    }
    return this;
  }
  







  public int span(CharSequence s, SpanCondition spanCondition)
  {
    return span(s, 0, spanCondition);
  }
  










  public int span(CharSequence s, int start, SpanCondition spanCondition)
  {
    int end = s.length();
    if (start < 0) {
      start = 0;
    } else if (start >= end) {
      return end;
    }
    if (bmpSet != null) {
      return start + bmpSet.span(s, start, end, spanCondition);
    }
    int len = end - start;
    if (stringSpan != null)
      return start + stringSpan.span(s, start, len, spanCondition);
    if (!strings.isEmpty()) {
      int which = spanCondition == SpanCondition.NOT_CONTAINED ? 41 : 42;
      
      UnicodeSetStringSpan strSpan = new UnicodeSetStringSpan(this, new ArrayList(strings), which);
      if (strSpan.needsStringSpanUTF16()) {
        return start + strSpan.span(s, start, len, spanCondition);
      }
    }
    

    boolean spanContained = spanCondition != SpanCondition.NOT_CONTAINED;
    

    int next = start;
    do {
      int c = Character.codePointAt(s, next);
      if (spanContained != contains(c)) {
        break;
      }
      next = Character.offsetByCodePoints(s, next, 1);
    } while (next < end);
    return next;
  }
  







  public int spanBack(CharSequence s, SpanCondition spanCondition)
  {
    return spanBack(s, s.length(), spanCondition);
  }
  










  public int spanBack(CharSequence s, int fromIndex, SpanCondition spanCondition)
  {
    if (fromIndex <= 0) {
      return 0;
    }
    if (fromIndex > s.length()) {
      fromIndex = s.length();
    }
    if (bmpSet != null) {
      return bmpSet.spanBack(s, fromIndex, spanCondition);
    }
    if (stringSpan != null)
      return stringSpan.spanBack(s, fromIndex, spanCondition);
    if (!strings.isEmpty()) {
      int which = spanCondition == SpanCondition.NOT_CONTAINED ? 25 : 26;
      

      UnicodeSetStringSpan strSpan = new UnicodeSetStringSpan(this, new ArrayList(strings), which);
      if (strSpan.needsStringSpanUTF16()) {
        return strSpan.spanBack(s, fromIndex, spanCondition);
      }
    }
    

    boolean spanContained = spanCondition != SpanCondition.NOT_CONTAINED;
    

    int prev = fromIndex;
    do {
      int c = Character.codePointBefore(s, prev);
      if (spanContained != contains(c)) {
        break;
      }
      prev = Character.offsetByCodePoints(s, prev, -1);
    } while (prev > 0);
    return prev;
  }
  




  public UnicodeSet cloneAsThawed()
  {
    UnicodeSet result = (UnicodeSet)clone();
    bmpSet = null;
    stringSpan = null;
    return result;
  }
  
  private void checkFrozen()
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify frozen object");
    }
  }
  








  public Iterator<String> iterator()
  {
    return new UnicodeSetIterator2(this);
  }
  

  private static class UnicodeSetIterator2
    implements Iterator<String>
  {
    private int[] sourceList;
    
    private int len;
    private int item;
    private int current;
    private int limit;
    private TreeSet<String> sourceStrings;
    private Iterator<String> stringIterator;
    private char[] buffer;
    
    UnicodeSetIterator2(UnicodeSet source)
    {
      len = (len - 1);
      if (item >= len) {
        stringIterator = strings.iterator();
        sourceList = null;
      } else {
        sourceStrings = strings;
        sourceList = list;
        current = sourceList[(item++)];
        limit = sourceList[(item++)];
      }
    }
    


    public boolean hasNext()
    {
      return (sourceList != null) || (stringIterator.hasNext());
    }
    


    public String next()
    {
      if (sourceList == null) {
        return (String)stringIterator.next();
      }
      int codepoint = current++;
      
      if (current >= limit) {
        if (item >= len) {
          stringIterator = sourceStrings.iterator();
          sourceList = null;
        } else {
          current = sourceList[(item++)];
          limit = sourceList[(item++)];
        }
      }
      
      if (codepoint <= 65535) {
        return String.valueOf((char)codepoint);
      }
      

      if (buffer == null) {
        buffer = new char[2];
      }
      
      int offset = codepoint - 65536;
      buffer[0] = ((char)((offset >>> 10) + 55296));
      buffer[1] = ((char)((offset & 0x3FF) + 56320));
      return String.valueOf(buffer);
    }
    


    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  



  public boolean containsAll(Collection<String> collection)
  {
    for (String o : collection) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }
  



  public boolean containsNone(Collection<String> collection)
  {
    for (String o : collection) {
      if (contains(o)) {
        return false;
      }
    }
    return true;
  }
  



  public final boolean containsSome(Collection<String> collection)
  {
    return !containsNone(collection);
  }
  



  public UnicodeSet addAll(String... collection)
  {
    checkFrozen();
    for (String str : collection) {
      add(str);
    }
    return this;
  }
  




  public UnicodeSet removeAll(Collection<String> collection)
  {
    checkFrozen();
    for (String o : collection) {
      remove(o);
    }
    return this;
  }
  



  public UnicodeSet retainAll(Collection<String> collection)
  {
    checkFrozen();
    
    UnicodeSet toRetain = new UnicodeSet();
    toRetain.addAll(collection);
    retainAll(toRetain);
    return this;
  }
  






  public static enum ComparisonStyle
  {
    SHORTER_FIRST, 
    


    LEXICOGRAPHIC, 
    


    LONGER_FIRST;
    


    private ComparisonStyle() {}
  }
  

  public int compareTo(UnicodeSet o)
  {
    return compareTo(o, ComparisonStyle.SHORTER_FIRST);
  }
  



  public int compareTo(UnicodeSet o, ComparisonStyle style)
  {
    if (style != ComparisonStyle.LEXICOGRAPHIC) {
      int diff = size() - o.size();
      if (diff != 0) {
        return (diff < 0 ? 1 : 0) == (style == ComparisonStyle.SHORTER_FIRST ? 1 : 0) ? -1 : 1;
      }
    }
    
    for (int i = 0;; i++) { int result;
      if (0 != (result = list[i] - list[i]))
      {
        if (list[i] == 1114112) {
          if (strings.isEmpty()) return 1;
          String item = (String)strings.first();
          return compare(item, list[i]);
        }
        if (list[i] == 1114112) {
          if (strings.isEmpty()) return -1;
          String item = (String)strings.first();
          return -compare(item, list[i]);
        }
        
        return (i & 0x1) == 0 ? result : -result;
      }
      if (list[i] == 1114112) {
        break;
      }
    }
    return compare(strings, strings);
  }
  


  public int compareTo(Iterable<String> other)
  {
    return compare(this, other);
  }
  








  public static int compare(String string, int codePoint)
  {
    return CharSequences.compare(string, codePoint);
  }
  






  public static int compare(int codePoint, String string)
  {
    return -CharSequences.compare(string, codePoint);
  }
  








  public static <T extends Comparable<T>> int compare(Iterable<T> collection1, Iterable<T> collection2)
  {
    return compare(collection1.iterator(), collection2.iterator());
  }
  




  /**
   * @deprecated
   */
  public static <T extends Comparable<T>> int compare(Iterator<T> first, Iterator<T> other)
  {
    for (;;)
    {
      if (!first.hasNext())
        return other.hasNext() ? -1 : 0;
      if (!other.hasNext()) {
        return 1;
      }
      T item1 = (Comparable)first.next();
      T item2 = (Comparable)other.next();
      int result = item1.compareTo(item2);
      if (result != 0) {
        return result;
      }
    }
  }
  




  public static <T extends Comparable<T>> int compare(Collection<T> collection1, Collection<T> collection2, ComparisonStyle style)
  {
    if (style != ComparisonStyle.LEXICOGRAPHIC) {
      int diff = collection1.size() - collection2.size();
      if (diff != 0) {
        return (diff < 0 ? 1 : 0) == (style == ComparisonStyle.SHORTER_FIRST ? 1 : 0) ? -1 : 1;
      }
    }
    return compare(collection1, collection2);
  }
  



  public static <T, U extends Collection<T>> U addAllTo(Iterable<T> source, U target)
  {
    for (T item : source) {
      target.add(item);
    }
    return target;
  }
  



  public static <T> T[] addAllTo(Iterable<T> source, T[] target)
  {
    int i = 0;
    for (T item : source) {
      target[(i++)] = item;
    }
    return target;
  }
  








  public Iterable<String> strings()
  {
    return Collections.unmodifiableSortedSet(strings);
  }
  

  /**
   * @deprecated
   */
  public static int getSingleCodePoint(CharSequence s)
  {
    return CharSequences.getSingleCodePoint(s);
  }
  





  /**
   * @deprecated
   */
  public UnicodeSet addBridges(UnicodeSet dontCare)
  {
    UnicodeSet notInInput = new UnicodeSet(this).complement();
    for (UnicodeSetIterator it = new UnicodeSetIterator(notInInput); it.nextRange();) {
      if ((codepoint != 0) && (codepoint != UnicodeSetIterator.IS_STRING) && (codepointEnd != 1114111) && (dontCare.contains(codepoint, codepointEnd))) {
        add(codepoint, codepointEnd);
      }
    }
    return this;
  }
  
  /**
   * @deprecated
   */
  public int findIn(CharSequence value, int fromIndex, boolean findNot)
  {
    int cp;
    for (; 
        


        fromIndex < value.length(); fromIndex += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(value, fromIndex);
      if (contains(cp) != findNot) {
        break;
      }
    }
    return fromIndex;
  }
  
  /**
   * @deprecated
   */
  public int findLastIn(CharSequence value, int fromIndex, boolean findNot)
  {
    
    int cp;
    for (; 
        



        fromIndex >= 0; fromIndex -= UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(value, fromIndex);
      if (contains(cp) != findNot) {
        break;
      }
    }
    return fromIndex < 0 ? -1 : fromIndex;
  }
  




  /**
   * @deprecated
   */
  public String stripFrom(CharSequence source, boolean matches)
  {
    StringBuilder result = new StringBuilder();
    for (int pos = 0; pos < source.length();) {
      int inside = findIn(source, pos, !matches);
      result.append(source.subSequence(pos, inside));
      pos = findIn(source, inside, matches);
    }
    return result.toString();
  }
  















































  public static enum SpanCondition
  {
    NOT_CONTAINED, 
    















    CONTAINED, 
    

















    SIMPLE, 
    




    CONDITION_COUNT;
    

    private SpanCondition() {}
  }
  

  public static XSymbolTable getDefaultXSymbolTable()
  {
    return XSYMBOL_TABLE;
  }
  











  public static void setDefaultXSymbolTable(XSymbolTable xSymbolTable)
  {
    XSYMBOL_TABLE = xSymbolTable;
  }
}
