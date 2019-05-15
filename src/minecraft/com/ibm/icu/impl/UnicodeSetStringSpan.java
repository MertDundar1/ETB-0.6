package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.text.UnicodeSet.SpanCondition;
import java.util.ArrayList;









































public class UnicodeSetStringSpan
{
  public static final int FWD = 32;
  public static final int BACK = 16;
  public static final int UTF16 = 8;
  public static final int CONTAINED = 2;
  public static final int NOT_CONTAINED = 1;
  public static final int ALL = 63;
  public static final int FWD_UTF16_CONTAINED = 42;
  public static final int FWD_UTF16_NOT_CONTAINED = 41;
  public static final int BACK_UTF16_CONTAINED = 26;
  public static final int BACK_UTF16_NOT_CONTAINED = 25;
  static final short ALL_CP_CONTAINED = 255;
  static final short LONG_SPAN = 254;
  private UnicodeSet spanSet;
  private UnicodeSet spanNotSet;
  private ArrayList<String> strings;
  private short[] spanLengths;
  private int maxLength16;
  private boolean all;
  private OffsetList offsets;
  
  public UnicodeSetStringSpan(UnicodeSet set, ArrayList<String> setStrings, int which)
  {
    spanSet = new UnicodeSet(0, 1114111);
    strings = setStrings;
    all = (which == 63);
    spanSet.retainAll(set);
    if (0 != (which & 0x1))
    {

      spanNotSet = spanSet;
    }
    offsets = new OffsetList();
    







    int stringsLength = strings.size();
    

    boolean someRelevant = false;
    for (int i = 0; i < stringsLength; i++) {
      String string = (String)strings.get(i);
      int length16 = string.length();
      int spanLength = spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
      if (spanLength < length16) {
        someRelevant = true;
      }
      if ((0 != (which & 0x8)) && (length16 > maxLength16)) {
        maxLength16 = length16;
      }
    }
    if (!someRelevant) {
      maxLength16 = 0;
      return;
    }
    


    if (all) {
      spanSet.freeze();
    }
    
    int allocSize;
    
    int allocSize;
    
    if (all)
    {
      allocSize = stringsLength * 2;
    } else {
      allocSize = stringsLength;
    }
    spanLengths = new short[allocSize];
    int spanBackLengthsOffset;
    int spanBackLengthsOffset; if (all)
    {
      spanBackLengthsOffset = stringsLength;
    }
    else {
      spanBackLengthsOffset = 0;
    }
    


    for (i = 0; i < stringsLength; i++) {
      String string = (String)strings.get(i);
      int length16 = string.length();
      int spanLength = spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
      if (spanLength < length16) {
        if (0 != (which & 0x8)) {
          if (0 != (which & 0x2)) {
            if (0 != (which & 0x20)) {
              spanLengths[i] = makeSpanLengthByte(spanLength);
            }
            if (0 != (which & 0x10)) {
              spanLength = length16 - spanSet.spanBack(string, length16, UnicodeSet.SpanCondition.CONTAINED);
              
              spanLengths[(spanBackLengthsOffset + i)] = makeSpanLengthByte(spanLength);
            }
          } else {
            int tmp388_387 = 0;spanLengths[(spanBackLengthsOffset + i)] = tmp388_387;spanLengths[i] = tmp388_387;
          }
        }
        
        if (0 != (which & 0x1))
        {


          if (0 != (which & 0x20)) {
            int c = string.codePointAt(0);
            addToSpanNotSet(c);
          }
          if (0 != (which & 0x10)) {
            int c = string.codePointBefore(length16);
            addToSpanNotSet(c);
          }
        }
      }
      else if (all) {
        spanLengths[(spanBackLengthsOffset + i)] = 'ÿ';spanLengths[i] = 'ÿ';
      }
      else {
        spanLengths[i] = 255;
      }
    }
    


    if (all) {
      spanNotSet.freeze();
    }
  }
  



  public UnicodeSetStringSpan(UnicodeSetStringSpan otherStringSpan, ArrayList<String> newParentSetStrings)
  {
    spanSet = spanSet;
    strings = newParentSetStrings;
    maxLength16 = maxLength16;
    all = true;
    if (spanNotSet == spanSet) {
      spanNotSet = spanSet;
    } else {
      spanNotSet = ((UnicodeSet)spanNotSet.clone());
    }
    offsets = new OffsetList();
    
    spanLengths = ((short[])spanLengths.clone());
  }
  




  public boolean needsStringSpanUTF16()
  {
    return maxLength16 != 0;
  }
  
  public boolean contains(int c)
  {
    return spanSet.contains(c);
  }
  

  private void addToSpanNotSet(int c)
  {
    if ((spanNotSet == null) || (spanNotSet == spanSet)) {
      if (spanSet.contains(c)) {
        return;
      }
      spanNotSet = spanSet.cloneAsThawed();
    }
    spanNotSet.add(c);
  }
  



















































































  public synchronized int span(CharSequence s, int start, int length, UnicodeSet.SpanCondition spanCondition)
  {
    if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
      return spanNot(s, start, length);
    }
    int spanLength = spanSet.span(s.subSequence(start, start + length), UnicodeSet.SpanCondition.CONTAINED);
    if (spanLength == length) {
      return length;
    }
    

    int initSize = 0;
    if (spanCondition == UnicodeSet.SpanCondition.CONTAINED)
    {
      initSize = maxLength16;
    }
    offsets.setMaxLength(initSize);
    int pos = start + spanLength;int rest = length - spanLength;
    int stringsLength = strings.size();
    for (;;) {
      if (spanCondition == UnicodeSet.SpanCondition.CONTAINED)
        for (int i = 0; i < stringsLength; i++) {
          int overlap = spanLengths[i];
          if (overlap != 255)
          {

            String string = (String)strings.get(i);
            
            int length16 = string.length();
            

            if (overlap >= 254) {
              overlap = length16;
              
              overlap = string.offsetByCodePoints(overlap, -1);
            }
            
            if (overlap > spanLength) {
              overlap = spanLength;
            }
            int inc = length16 - overlap;
            
            while (inc <= rest)
            {


              if ((!offsets.containsOffset(inc)) && (matches16CPB(s, pos - overlap, length, string, length16))) {
                if (inc == rest) {
                  return length;
                }
                offsets.addOffset(inc);
              }
              if (overlap == 0) {
                break;
              }
              overlap--;
              inc++;
            }
          }
        }
      int maxInc = 0;int maxOverlap = 0;
      for (int i = 0; i < stringsLength; i++) {
        int overlap = spanLengths[i];
        


        String string = (String)strings.get(i);
        
        int length16 = string.length();
        

        if (overlap >= 254) {
          overlap = length16;
        }
        

        if (overlap > spanLength) {
          overlap = spanLength;
        }
        int inc = length16 - overlap;
        
        while ((inc <= rest) && (overlap >= maxOverlap))
        {


          if (((overlap > maxOverlap) || (inc > maxInc)) && (matches16CPB(s, pos - overlap, length, string, length16)))
          {
            maxInc = inc;
            maxOverlap = overlap;
            break;
          }
          overlap--;
          inc++;
        }
      }
      
      if ((maxInc != 0) || (maxOverlap != 0))
      {

        pos += maxInc;
        rest -= maxInc;
        if (rest == 0) {
          return length;
        }
        spanLength = 0;

      }
      else
      {

        if ((spanLength != 0) || (pos == 0))
        {




          if (offsets.isEmpty()) {
            return pos - start;
          }
        }
        else
        {
          if (offsets.isEmpty())
          {

            spanLength = spanSet.span(s.subSequence(pos, pos + rest), UnicodeSet.SpanCondition.CONTAINED);
            if ((spanLength == rest) || (spanLength == 0))
            {

              return pos + spanLength - start;
            }
            pos += spanLength;
            rest -= spanLength;
            continue;
          }
          


          spanLength = spanOne(spanSet, s, pos, rest);
          if (spanLength > 0) {
            if (spanLength == rest) {
              return length;
            }
            


            pos += spanLength;
            rest -= spanLength;
            offsets.shift(spanLength);
            spanLength = 0;
            continue;
          }
        }
        

        int minOffset = offsets.popMinimum();
        pos += minOffset;
        rest -= minOffset;
        spanLength = 0;
      }
    }
  }
  





  public synchronized int spanBack(CharSequence s, int length, UnicodeSet.SpanCondition spanCondition)
  {
    if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
      return spanNotBack(s, length);
    }
    int pos = spanSet.spanBack(s, length, UnicodeSet.SpanCondition.CONTAINED);
    if (pos == 0) {
      return 0;
    }
    int spanLength = length - pos;
    

    int initSize = 0;
    if (spanCondition == UnicodeSet.SpanCondition.CONTAINED)
    {
      initSize = maxLength16;
    }
    offsets.setMaxLength(initSize);
    int stringsLength = strings.size();
    int spanBackLengthsOffset = 0;
    if (all) {
      spanBackLengthsOffset = stringsLength;
    }
    for (;;) {
      if (spanCondition == UnicodeSet.SpanCondition.CONTAINED)
        for (int i = 0; i < stringsLength; i++) {
          int overlap = spanLengths[(spanBackLengthsOffset + i)];
          if (overlap != 255)
          {

            String string = (String)strings.get(i);
            
            int length16 = string.length();
            

            if (overlap >= 254) {
              overlap = length16;
              
              int len1 = 0;
              len1 = string.offsetByCodePoints(0, 1);
              overlap -= len1;
            }
            if (overlap > spanLength) {
              overlap = spanLength;
            }
            int dec = length16 - overlap;
            
            while (dec <= pos)
            {


              if ((!offsets.containsOffset(dec)) && (matches16CPB(s, pos - dec, length, string, length16))) {
                if (dec == pos) {
                  return 0;
                }
                offsets.addOffset(dec);
              }
              if (overlap == 0) {
                break;
              }
              overlap--;
              dec++;
            }
          }
        }
      int maxDec = 0;int maxOverlap = 0;
      for (int i = 0; i < stringsLength; i++) {
        int overlap = spanLengths[(spanBackLengthsOffset + i)];
        


        String string = (String)strings.get(i);
        
        int length16 = string.length();
        

        if (overlap >= 254) {
          overlap = length16;
        }
        

        if (overlap > spanLength) {
          overlap = spanLength;
        }
        int dec = length16 - overlap;
        
        while ((dec <= pos) && (overlap >= maxOverlap))
        {


          if (((overlap > maxOverlap) || (dec > maxDec)) && (matches16CPB(s, pos - dec, length, string, length16)))
          {
            maxDec = dec;
            maxOverlap = overlap;
            break;
          }
          overlap--;
          dec++;
        }
      }
      
      if ((maxDec != 0) || (maxOverlap != 0))
      {

        pos -= maxDec;
        if (pos == 0) {
          return 0;
        }
        spanLength = 0;

      }
      else
      {

        if ((spanLength != 0) || (pos == length))
        {




          if (offsets.isEmpty()) {
            return pos;
          }
        }
        else
        {
          if (offsets.isEmpty())
          {

            int oldPos = pos;
            pos = spanSet.spanBack(s, oldPos, UnicodeSet.SpanCondition.CONTAINED);
            spanLength = oldPos - pos;
            if ((pos != 0) && (spanLength != 0)) {
              continue;
            }
            return pos;
          }
          




          spanLength = spanOneBack(spanSet, s, pos);
          if (spanLength > 0) {
            if (spanLength == pos) {
              return 0;
            }
            


            pos -= spanLength;
            offsets.shift(spanLength);
            spanLength = 0;
            continue;
          }
        }
        

        pos -= offsets.popMinimum();
        spanLength = 0;
      }
    }
  }
  
























  private int spanNot(CharSequence s, int start, int length)
  {
    int pos = start;int rest = length;
    int stringsLength = strings.size();
    
    do
    {
      int i = spanNotSet.span(s.subSequence(pos, pos + rest), UnicodeSet.SpanCondition.NOT_CONTAINED);
      if (i == rest) {
        return length;
      }
      pos += i;
      rest -= i;
      


      int cpLength = spanOne(spanSet, s, pos, rest);
      if (cpLength > 0) {
        return pos - start;
      }
      

      for (i = 0; i < stringsLength; i++) {
        if (spanLengths[i] != 255)
        {

          String string = (String)strings.get(i);
          
          int length16 = string.length();
          if ((length16 <= rest) && (matches16CPB(s, pos, length, string, length16))) {
            return pos - start;
          }
        }
      }
      


      pos -= cpLength;
      rest += cpLength;
    } while (rest != 0);
    return length;
  }
  
  private int spanNotBack(CharSequence s, int length) {
    int pos = length;
    int stringsLength = strings.size();
    
    do
    {
      pos = spanNotSet.spanBack(s, pos, UnicodeSet.SpanCondition.NOT_CONTAINED);
      if (pos == 0) {
        return 0;
      }
      


      int cpLength = spanOneBack(spanSet, s, pos);
      if (cpLength > 0) {
        return pos;
      }
      

      for (int i = 0; i < stringsLength; i++)
      {


        if (spanLengths[i] != 255)
        {

          String string = (String)strings.get(i);
          
          int length16 = string.length();
          if ((length16 <= pos) && (matches16CPB(s, pos - length16, length, string, length16))) {
            return pos;
          }
        }
      }
      


      pos += cpLength;
    } while (pos != 0);
    return 0;
  }
  
  static short makeSpanLengthByte(int spanLength)
  {
    return spanLength < 254 ? (short)spanLength : 254;
  }
  
  private static boolean matches16(CharSequence s, int start, String t, int length)
  {
    int end = start + length;
    while (length-- > 0) {
      if (s.charAt(--end) != t.charAt(length)) {
        return false;
      }
    }
    return true;
  }
  







  static boolean matches16CPB(CharSequence s, int start, int slength, String t, int tlength)
  {
    return ((0 >= start) || (!UTF16.isLeadSurrogate(s.charAt(start - 1))) || (!UTF16.isTrailSurrogate(s.charAt(start + 0)))) && ((tlength >= slength) || (!UTF16.isLeadSurrogate(s.charAt(start + tlength - 1))) || (!UTF16.isTrailSurrogate(s.charAt(start + tlength)))) && (matches16(s, start, t, tlength));
  }
  





  static int spanOne(UnicodeSet set, CharSequence s, int start, int length)
  {
    char c = s.charAt(start);
    if ((c >= 55296) && (c <= 56319) && (length >= 2)) {
      char c2 = s.charAt(start + 1);
      if (UTF16.isTrailSurrogate(c2)) {
        int supplementary = UCharacterProperty.getRawSupplementary(c, c2);
        return set.contains(supplementary) ? 2 : -2;
      }
    }
    return set.contains(c) ? 1 : -1;
  }
  
  static int spanOneBack(UnicodeSet set, CharSequence s, int length) {
    char c = s.charAt(length - 1);
    if ((c >= 56320) && (c <= 57343) && (length >= 2)) {
      char c2 = s.charAt(length - 2);
      if (UTF16.isLeadSurrogate(c2)) {
        int supplementary = UCharacterProperty.getRawSupplementary(c2, c);
        return set.contains(supplementary) ? 2 : -2;
      }
    }
    return set.contains(c) ? 1 : -1;
  }
  





  static class OffsetList
  {
    private boolean[] list;
    




    private int length;
    




    private int start;
    





    public OffsetList()
    {
      list = new boolean[16];
    }
    
    public void setMaxLength(int maxLength) {
      if (maxLength > list.length) {
        list = new boolean[maxLength];
      }
      clear();
    }
    
    public void clear() {
      for (int i = list.length; i-- > 0;) {
        list[i] = false;
      }
      start = (this.length = 0);
    }
    
    public boolean isEmpty() {
      return length == 0;
    }
    




    public void shift(int delta)
    {
      int i = start + delta;
      if (i >= list.length) {
        i -= list.length;
      }
      if (list[i] != 0) {
        list[i] = false;
        length -= 1;
      }
      start = i;
    }
    

    public void addOffset(int offset)
    {
      int i = start + offset;
      if (i >= list.length) {
        i -= list.length;
      }
      list[i] = true;
      length += 1;
    }
    
    public boolean containsOffset(int offset)
    {
      int i = start + offset;
      if (i >= list.length) {
        i -= list.length;
      }
      return list[i];
    }
    



    public int popMinimum()
    {
      int i = start;
      do { i++; if (i >= list.length) break;
      } while (list[i] == 0);
      list[i] = false;
      length -= 1;
      int result = i - start;
      start = i;
      return result;
      





      int result = list.length - start;
      i = 0;
      while (list[i] == 0) {
        i++;
      }
      list[i] = false;
      length -= 1;
      start = i;
      return result + i;
    }
  }
}
