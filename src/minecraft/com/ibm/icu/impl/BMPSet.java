package com.ibm.icu.impl;

import com.ibm.icu.text.UnicodeSet.SpanCondition;
















public final class BMPSet
{
  public static int U16_SURROGATE_OFFSET = 56613888;
  




  private boolean[] latin1Contains;
  




  private int[] table7FF;
  




  private int[] bmpBlockBits;
  




  private int[] list4kStarts;
  




  private final int[] list;
  




  private final int listLength;
  




  public BMPSet(int[] parentList, int parentListLength)
  {
    list = parentList;
    listLength = parentListLength;
    latin1Contains = new boolean['Ā'];
    table7FF = new int[64];
    bmpBlockBits = new int[64];
    list4kStarts = new int[18];
    





    list4kStarts[0] = findCodePoint(2048, 0, listLength - 1);
    
    for (int i = 1; i <= 16; i++) {
      list4kStarts[i] = findCodePoint(i << 12, list4kStarts[(i - 1)], listLength - 1);
    }
    list4kStarts[17] = (listLength - 1);
    
    initBits();
  }
  
  public BMPSet(BMPSet otherBMPSet, int[] newParentList, int newParentListLength) {
    list = newParentList;
    listLength = newParentListLength;
    latin1Contains = ((boolean[])latin1Contains.clone());
    table7FF = ((int[])table7FF.clone());
    bmpBlockBits = ((int[])bmpBlockBits.clone());
    list4kStarts = ((int[])list4kStarts.clone());
  }
  
  public boolean contains(int c) {
    if (c <= 255)
      return latin1Contains[c];
    if (c <= 2047)
      return (table7FF[(c & 0x3F)] & 1 << (c >> 6)) != 0;
    if ((c < 55296) || ((c >= 57344) && (c <= 65535))) {
      int lead = c >> 12;
      int twoBits = bmpBlockBits[(c >> 6 & 0x3F)] >> lead & 0x10001;
      if (twoBits <= 1)
      {

        return 0 != twoBits;
      }
      
      return containsSlow(c, list4kStarts[lead], list4kStarts[(lead + 1)]);
    }
    if (c <= 1114111)
    {
      return containsSlow(c, list4kStarts[13], list4kStarts[17]);
    }
    

    return false;
  }
  













  public final int span(CharSequence s, int start, int end, UnicodeSet.SpanCondition spanCondition)
  {
    int i = start;
    int limit = Math.min(s.length(), end);
    if (UnicodeSet.SpanCondition.NOT_CONTAINED != spanCondition)
    {
      while (i < limit) {
        char c = s.charAt(i);
        if (c <= 'ÿ') {
          if (latin1Contains[c] == 0) {
            break;
          }
        } else if (c <= '߿') {
          if ((table7FF[(c & 0x3F)] & 1 << (c >> '\006')) == 0)
            break;
        } else { char c2;
          if ((c < 55296) || (c >= 56320) || (i + 1 == limit) || ((c2 = s.charAt(i + 1)) < 56320) || (c2 >= 57344))
          {
            int lead = c >> '\f';
            int twoBits = bmpBlockBits[(c >> '\006' & 0x3F)] >> lead & 0x10001;
            if (twoBits <= 1 ? 
            

              twoBits == 0 : 
              



              !containsSlow(c, list4kStarts[lead], list4kStarts[(lead + 1)])) {
              break;
            }
          }
          else {
            char c2;
            int supplementary = UCharacterProperty.getRawSupplementary(c, c2);
            if (!containsSlow(supplementary, list4kStarts[16], list4kStarts[17])) {
              break;
            }
            i++;
          } }
        i++;
      }
    }
    
    while (i < limit) {
      char c = s.charAt(i);
      if (c <= 'ÿ') {
        if (latin1Contains[c] != 0) {
          break;
        }
      } else if (c <= '߿') {
        if ((table7FF[(c & 0x3F)] & 1 << (c >> '\006')) != 0)
          break;
      } else { char c2;
        if ((c < 55296) || (c >= 56320) || (i + 1 == limit) || ((c2 = s.charAt(i + 1)) < 56320) || (c2 >= 57344))
        {
          int lead = c >> '\f';
          int twoBits = bmpBlockBits[(c >> '\006' & 0x3F)] >> lead & 0x10001;
          if (twoBits <= 1 ? 
          

            twoBits != 0 : 
            



            containsSlow(c, list4kStarts[lead], list4kStarts[(lead + 1)])) {
            break;
          }
        }
        else {
          char c2;
          int supplementary = UCharacterProperty.getRawSupplementary(c, c2);
          if (containsSlow(supplementary, list4kStarts[16], list4kStarts[17])) {
            break;
          }
          i++;
        } }
      i++;
    }
    
    return i - start;
  }
  








  public final int spanBack(CharSequence s, int limit, UnicodeSet.SpanCondition spanCondition)
  {
    limit = Math.min(s.length(), limit);
    if (UnicodeSet.SpanCondition.NOT_CONTAINED != spanCondition)
    {
      do {
        char c = s.charAt(--limit);
        if (c <= 'ÿ') {
          if (latin1Contains[c] == 0) {
            break;
          }
        } else if (c <= '߿') {
          if ((table7FF[(c & 0x3F)] & 1 << (c >> '\006')) == 0)
            break;
        } else { char c2;
          if ((c < 55296) || (c < 56320) || (0 == limit) || ((c2 = s.charAt(limit - 1)) < 55296) || (c2 >= 56320))
          {
            int lead = c >> '\f';
            int twoBits = bmpBlockBits[(c >> '\006' & 0x3F)] >> lead & 0x10001;
            if (twoBits <= 1 ? 
            

              twoBits == 0 : 
              



              !containsSlow(c, list4kStarts[lead], list4kStarts[(lead + 1)])) {
              break;
            }
          }
          else {
            char c2;
            int supplementary = UCharacterProperty.getRawSupplementary(c2, c);
            if (!containsSlow(supplementary, list4kStarts[16], list4kStarts[17])) {
              break;
            }
            limit--;
          }
        } } while (0 != limit);
      return 0;
    }
    

    do
    {
      char c = s.charAt(--limit);
      if (c <= 'ÿ') {
        if (latin1Contains[c] != 0) {
          break;
        }
      } else if (c <= '߿') {
        if ((table7FF[(c & 0x3F)] & 1 << (c >> '\006')) != 0)
          break;
      } else { char c2;
        if ((c < 55296) || (c < 56320) || (0 == limit) || ((c2 = s.charAt(limit - 1)) < 55296) || (c2 >= 56320))
        {
          int lead = c >> '\f';
          int twoBits = bmpBlockBits[(c >> '\006' & 0x3F)] >> lead & 0x10001;
          if (twoBits <= 1 ? 
          

            twoBits != 0 : 
            



            containsSlow(c, list4kStarts[lead], list4kStarts[(lead + 1)])) {
            break;
          }
        }
        else {
          char c2;
          int supplementary = UCharacterProperty.getRawSupplementary(c2, c);
          if (containsSlow(supplementary, list4kStarts[16], list4kStarts[17])) {
            break;
          }
          limit--;
        }
      } } while (0 != limit);
    return 0;
    


    return limit + 1;
  }
  


  private static void set32x64Bits(int[] table, int start, int limit)
  {
    assert (64 == table.length);
    int lead = start >> 6;
    int trail = start & 0x3F;
    

    int bits = 1 << lead;
    if (start + 1 == limit) {
      table[trail] |= bits;
      return;
    }
    
    int limitLead = limit >> 6;
    int limitTrail = limit & 0x3F;
    
    if (lead == limitLead)
    {
      while (trail < limitTrail) {
        table[(trail++)] |= bits;
      }
    }
    


    if (trail > 0) {
      do {
        table[(trail++)] |= bits;
      } while (trail < 64);
      lead++;
    }
    if (lead < limitLead) {
      bits = (1 << lead) - 1 ^ 0xFFFFFFFF;
      if (limitLead < 32) {
        bits &= (1 << limitLead) - 1;
      }
      for (trail = 0; trail < 64; trail++) {
        table[trail] |= bits;
      }
    }
    



    bits = 1 << limitLead;
    for (trail = 0; trail < limitTrail; trail++) {
      table[trail] |= bits;
    }
  }
  

  private void initBits()
  {
    int listIndex = 0;
    int start;
    int limit;
    do {
      start = list[(listIndex++)];
      int limit; if (listIndex < listLength) {
        limit = list[(listIndex++)];
      } else {
        limit = 1114112;
      }
      if (start >= 256) {
        break;
      }
      do {
        latin1Contains[(start++)] = true;
      } while ((start < limit) && (start < 256));
    } while (limit <= 256);
    

    while (start < 2048) {
      set32x64Bits(table7FF, start, limit <= 2048 ? limit : 2048);
      if (limit > 2048) {
        start = 2048;
        break;
      }
      
      start = list[(listIndex++)];
      if (listIndex < listLength) {
        limit = list[(listIndex++)];
      } else {
        limit = 1114112;
      }
    }
    

    int minStart = 2048;
    while (start < 65536) {
      if (limit > 65536) {
        limit = 65536;
      }
      
      if (start < minStart) {
        start = minStart;
      }
      if (start < limit) {
        if (0 != (start & 0x3F))
        {
          start >>= 6;
          bmpBlockBits[(start & 0x3F)] |= 65537 << (start >> 6);
          start = start + 1 << 6;
          minStart = start;
        }
        if (start < limit) {
          if (start < (limit & 0xFFFFFFC0))
          {
            set32x64Bits(bmpBlockBits, start >> 6, limit >> 6);
          }
          
          if (0 != (limit & 0x3F))
          {
            limit >>= 6;
            bmpBlockBits[(limit & 0x3F)] |= 65537 << (limit >> 6);
            limit = limit + 1 << 6;
            minStart = limit;
          }
        }
      }
      
      if (limit == 65536) {
        break;
      }
      
      start = list[(listIndex++)];
      if (listIndex < listLength) {
        limit = list[(listIndex++)];
      } else {
        limit = 1114112;
      }
    }
  }
  



























  private int findCodePoint(int c, int lo, int hi)
  {
    if (c < list[lo]) {
      return lo;
    }
    
    if ((lo >= hi) || (c >= list[(hi - 1)])) {
      return hi;
    }
    for (;;)
    {
      int i = lo + hi >>> 1;
      if (i == lo)
        break;
      if (c < list[i]) {
        hi = i;
      } else {
        lo = i;
      }
    }
    return hi;
  }
  
  private final boolean containsSlow(int c, int lo, int hi) {
    return 0 != (findCodePoint(c, lo, hi) & 0x1);
  }
}
