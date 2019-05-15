package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.ULocale;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
























public final class UCaseProps
{
  private UCaseProps()
    throws IOException
  {
    InputStream is = ICUData.getRequiredStream("data/icudt51b/ucase.icu");
    BufferedInputStream b = new BufferedInputStream(is, 4096);
    readData(b);
    b.close();
    is.close();
  }
  
  private final void readData(InputStream is) throws IOException {
    DataInputStream inputStream = new DataInputStream(is);
    

    ICUBinary.readHeader(inputStream, FMT, new IsAcceptable(null));
    


    int count = inputStream.readInt();
    if (count < 16) {
      throw new IOException("indexes[0] too small in ucase.icu");
    }
    indexes = new int[count];
    
    indexes[0] = count;
    for (int i = 1; i < count; i++) {
      indexes[i] = inputStream.readInt();
    }
    

    trie = Trie2_16.createFromSerialized(inputStream);
    int expectedTrieLength = indexes[2];
    int trieLength = trie.getSerializedLength();
    if (trieLength > expectedTrieLength) {
      throw new IOException("ucase.icu: not enough bytes for the trie");
    }
    
    inputStream.skipBytes(expectedTrieLength - trieLength);
    

    count = indexes[3];
    if (count > 0) {
      exceptions = new char[count];
      for (i = 0; i < count; i++) {
        exceptions[i] = inputStream.readChar();
      }
    }
    

    count = indexes[4];
    if (count > 0) {
      unfold = new char[count];
      for (i = 0; i < count; i++)
        unfold[i] = inputStream.readChar();
    } }
  
  public static abstract interface ContextIterator { public abstract void reset(int paramInt);
    
    public abstract int next(); }
  
  private static final class IsAcceptable implements ICUBinary.Authenticate { private IsAcceptable() {}
    
    public boolean isDataVersionAcceptable(byte[] version) { return version[0] == 3; }
  }
  



  public final void addPropertyStarts(UnicodeSet set)
  {
    Iterator<Trie2.Range> trieIterator = trie.iterator();
    Trie2.Range range;
    while ((trieIterator.hasNext()) && (!nextleadSurrogate)) {
      set.add(startCodePoint);
    }
  }
  









  private static final int getExceptionsOffset(int props)
  {
    return props >> 5;
  }
  
  private static final boolean propsHasException(int props) {
    return (props & 0x10) != 0;
  }
  

  private static final byte[] flagsOffset = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8 };
  
  public static final int MAX_STRING_LENGTH = 31;
  
  private static final int LOC_UNKNOWN = 0;
  
  private static final int LOC_ROOT = 1;
  
  private static final int LOC_TURKISH = 2;
  private static final int LOC_LITHUANIAN = 3;
  private static final String iDot = "i̇";
  private static final String jDot = "j̇";
  private static final String iOgonekDot = "į̇";
  private static final String iDotGrave = "i̇̀";
  private static final String iDotAcute = "i̇́";
  private static final String iDotTilde = "i̇̃";
  private static final int FOLD_CASE_OPTIONS_MASK = 255;
  
  private static final boolean hasSlot(int flags, int index)
  {
    return (flags & 1 << index) != 0;
  }
  
  private static final byte slotOffset(int flags, int index) { return flagsOffset[(flags & (1 << index) - 1)]; }
  



  private final long getSlotValueAndOffset(int excWord, int index, int excOffset)
  {
    long value;
    


    long value;
    

    if ((excWord & 0x100) == 0) {
      excOffset += slotOffset(excWord, index);
      value = exceptions[excOffset];
    } else {
      excOffset += 2 * slotOffset(excWord, index);
      value = exceptions[(excOffset++)];
      value = value << 16 | exceptions[excOffset];
    }
    return value | excOffset << 32;
  }
  
  private final int getSlotValue(int excWord, int index, int excOffset) {
    int value;
    int value;
    if ((excWord & 0x100) == 0) {
      excOffset += slotOffset(excWord, index);
      value = exceptions[excOffset];
    } else {
      excOffset += 2 * slotOffset(excWord, index);
      value = exceptions[(excOffset++)];
      value = value << 16 | exceptions[excOffset];
    }
    return value;
  }
  

  public final int tolower(int c)
  {
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) >= 2) {
        c += getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      if (hasSlot(excWord, 0)) {
        c = getSlotValue(excWord, 0, excOffset);
      }
    }
    return c;
  }
  
  public final int toupper(int c) {
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) == 1) {
        c += getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      if (hasSlot(excWord, 2)) {
        c = getSlotValue(excWord, 2, excOffset);
      }
    }
    return c;
  }
  
  public final int totitle(int c) {
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) == 1) {
        c += getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      int index;
      if (hasSlot(excWord, 3)) {
        index = 3; } else { int index;
        if (hasSlot(excWord, 2)) {
          index = 2;
        } else
          return c; }
      int index;
      c = getSlotValue(excWord, index, excOffset);
    }
    return c;
  }
  
















  public final void addCaseClosure(int c, UnicodeSet set)
  {
    switch (c)
    {
    case 73: 
      set.add(105);
      return;
    case 105: 
      set.add(73);
      return;
    
    case 304: 
      set.add("i̇");
      return;
    
    case 305: 
      return;
    }
    
    


    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) != 0)
      {
        int delta = getDelta(props);
        if (delta != 0) {
          set.add(c + delta);
        }
        
      }
      
    }
    else
    {
      int excOffset = getExceptionsOffset(props);
      
      int excWord = exceptions[(excOffset++)];
      

      int excOffset0 = excOffset;
      

      for (int index = 0; index <= 3; index++)
        if (hasSlot(excWord, index)) {
          excOffset = excOffset0;
          c = getSlotValue(excWord, index, excOffset);
          set.add(c);
        }
      int closureOffset;
      int closureLength;
      int closureOffset;
      if (hasSlot(excWord, 6)) {
        excOffset = excOffset0;
        long value = getSlotValueAndOffset(excWord, 6, excOffset);
        int closureLength = (int)value & 0xF;
        closureOffset = (int)(value >> 32) + 1;
      } else {
        closureLength = 0;
        closureOffset = 0;
      }
      

      if (hasSlot(excWord, 7)) {
        excOffset = excOffset0;
        long value = getSlotValueAndOffset(excWord, 7, excOffset);
        int fullLength = (int)value;
        

        excOffset = (int)(value >> 32) + 1;
        
        fullLength &= 0xFFFF;
        

        excOffset += (fullLength & 0xF);
        fullLength >>= 4;
        

        int length = fullLength & 0xF;
        if (length != 0) {
          set.add(new String(exceptions, excOffset, length));
          excOffset += length;
        }
        

        fullLength >>= 4;
        excOffset += (fullLength & 0xF);
        fullLength >>= 4;
        excOffset += fullLength;
        
        closureOffset = excOffset;
      }
      

      for (index = 0; index < closureLength; index += UTF16.getCharCount(c)) {
        c = UTF16.charAt(exceptions, closureOffset, exceptions.length, index);
        set.add(c);
      }
    }
  }
  





  private final int strcmpMax(String s, int unfoldOffset, int max)
  {
    int length = s.length();
    max -= length;
    int i1 = 0;
    do {
      int c1 = s.charAt(i1++);
      int c2 = unfold[(unfoldOffset++)];
      if (c2 == 0) {
        return 1;
      }
      c1 -= c2;
      if (c1 != 0) {
        return c1;
      }
      length--; } while (length > 0);
    

    if ((max == 0) || (unfold[unfoldOffset] == 0)) {
      return 0;
    }
    return -max;
  }
  













  public final boolean addStringCaseClosure(String s, UnicodeSet set)
  {
    if ((unfold == null) || (s == null)) {
      return false;
    }
    int length = s.length();
    if (length <= 1)
    {






      return false;
    }
    
    int unfoldRows = unfold[0];
    int unfoldRowWidth = unfold[1];
    int unfoldStringWidth = unfold[2];
    

    if (length > unfoldStringWidth)
    {
      return false;
    }
    

    int start = 0;
    int limit = unfoldRows;
    while (start < limit) {
      int i = (start + limit) / 2;
      int unfoldOffset = (i + 1) * unfoldRowWidth;
      int result = strcmpMax(s, unfoldOffset, unfoldStringWidth);
      
      if (result == 0)
      {
        int c;
        
        for (i = unfoldStringWidth; (i < unfoldRowWidth) && (unfold[(unfoldOffset + i)] != 0); i += UTF16.getCharCount(c)) {
          c = UTF16.charAt(unfold, unfoldOffset, unfold.length, i);
          set.add(c);
          addCaseClosure(c, set);
        }
        return true; }
      if (result < 0) {
        limit = i;
      } else {
        start = i + 1;
      }
    }
    
    return false;
  }
  
  public final int getType(int c)
  {
    return getTypeFromProps(trie.get(c));
  }
  
  public final int getTypeOrIgnorable(int c)
  {
    return getTypeAndIgnorableFromProps(trie.get(c));
  }
  
  public final int getDotType(int c)
  {
    int props = trie.get(c);
    if (!propsHasException(props)) {
      return props & 0x60;
    }
    return exceptions[getExceptionsOffset(props)] >> '\007' & 0x60;
  }
  
  public final boolean isSoftDotted(int c)
  {
    return getDotType(c) == 32;
  }
  
  public final boolean isCaseSensitive(int c) {
    return (trie.get(c) & 0x8) != 0;
  }
  



































































  private static final int getCaseLocale(ULocale locale, int[] locCache)
  {
    int result;
    


































































    if ((locCache != null) && ((result = locCache[0]) != 0)) {
      return result;
    }
    
    int result = 1;
    
    String language = locale.getLanguage();
    if ((language.equals("tr")) || (language.equals("tur")) || (language.equals("az")) || (language.equals("aze"))) {
      result = 2;
    } else if ((language.equals("lt")) || (language.equals("lit"))) {
      result = 3;
    }
    
    if (locCache != null) {
      locCache[0] = result;
    }
    return result;
  }
  


  private final boolean isFollowedByCasedLetter(ContextIterator iter, int dir)
  {
    if (iter == null) {
      return false;
    }
    int c;
    for (iter.reset(dir); (c = iter.next()) >= 0;) {
      int type = getTypeOrIgnorable(c);
      if ((type & 0x4) == 0)
      {
        if (type != 0) {
          return true;
        }
        return false;
      }
    }
    
    return false;
  }
  



  private final boolean isPrecededBySoftDotted(ContextIterator iter)
  {
    if (iter == null) {
      return false;
    }
    int c;
    for (iter.reset(-1); (c = iter.next()) >= 0;) {
      int dotType = getDotType(c);
      if (dotType == 32)
        return true;
      if (dotType != 96) {
        return false;
      }
    }
    
    return false;
  }
  





































  private final boolean isPrecededBy_I(ContextIterator iter)
  {
    if (iter == null) {
      return false;
    }
    int c;
    for (iter.reset(-1); (c = iter.next()) >= 0;) {
      if (c == 73) {
        return true;
      }
      int dotType = getDotType(c);
      if (dotType != 96) {
        return false;
      }
    }
    
    return false;
  }
  



  private final boolean isFollowedByMoreAbove(ContextIterator iter)
  {
    if (iter == null) {
      return false;
    }
    int c;
    for (iter.reset(1); (c = iter.next()) >= 0;) {
      int dotType = getDotType(c);
      if (dotType == 64)
        return true;
      if (dotType != 96) {
        return false;
      }
    }
    
    return false;
  }
  



  private final boolean isFollowedByDotAbove(ContextIterator iter)
  {
    if (iter == null) {
      return false;
    }
    int c;
    for (iter.reset(1); (c = iter.next()) >= 0;) {
      if (c == 775) {
        return true;
      }
      int dotType = getDotType(c);
      if (dotType != 96) {
        return false;
      }
    }
    
    return false;
  }
  





























  public final int toFullLower(int c, ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache)
  {
    int result = c;
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) >= 2) {
        result = c + getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      

      int excOffset2 = excOffset;
      
      if ((excWord & 0x4000) != 0)
      {
        int loc = getCaseLocale(locale, locCache);
        






        if ((loc == 3) && (((c != 73) && (c != 74) && (c != 302)) || ((isFollowedByMoreAbove(iter)) || (c == 204) || (c == 205) || (c == 296))))
        {





















          switch (c) {
          case 73: 
            out.append("i̇");
            return 2;
          case 74: 
            out.append("j̇");
            return 2;
          case 302: 
            out.append("į̇");
            return 2;
          case 204: 
            out.append("i̇̀");
            return 3;
          case 205: 
            out.append("i̇́");
            return 3;
          case 296: 
            out.append("i̇̃");
            return 3;
          }
          return 0;
        }
        
        if ((loc == 2) && (c == 304))
        {






          return 105; }
        if ((loc == 2) && (c == 775) && (isPrecededBy_I(iter)))
        {






          return 0; }
        if ((loc == 2) && (c == 73) && (!isFollowedByDotAbove(iter)))
        {





          return 305; }
        if (c == 304)
        {




          out.append("i̇");
          return 2; }
        if ((c == 931) && (!isFollowedByCasedLetter(iter, 1)) && (isFollowedByCasedLetter(iter, -1)))
        {








          return 962;
        }
        
      }
      else if (hasSlot(excWord, 7)) {
        long value = getSlotValueAndOffset(excWord, 7, excOffset);
        int full = (int)value & 0xF;
        if (full != 0)
        {
          excOffset = (int)(value >> 32) + 1;
          

          out.append(exceptions, excOffset, full);
          

          return full;
        }
      }
      
      if (hasSlot(excWord, 0)) {
        result = getSlotValue(excWord, 0, excOffset2);
      }
    }
    
    return result == c ? result ^ 0xFFFFFFFF : result;
  }
  






  private final int toUpperOrTitle(int c, ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache, boolean upperNotTitle)
  {
    int result = c;
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) == 1) {
        result = c + getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      

      int excOffset2 = excOffset;
      
      if ((excWord & 0x4000) != 0)
      {
        int loc = getCaseLocale(locale, locCache);
        
        if ((loc == 2) && (c == 105))
        {










          return 304; }
        if ((loc == 3) && (c == 775) && (isPrecededBySoftDotted(iter)))
        {








          return 0;
        }
        
      }
      else if (hasSlot(excWord, 7)) {
        long value = getSlotValueAndOffset(excWord, 7, excOffset);
        int full = (int)value & 0xFFFF;
        

        excOffset = (int)(value >> 32) + 1;
        

        excOffset += (full & 0xF);
        full >>= 4;
        excOffset += (full & 0xF);
        full >>= 4;
        
        if (upperNotTitle) {
          full &= 0xF;
        }
        else {
          excOffset += (full & 0xF);
          full = full >> 4 & 0xF;
        }
        
        if (full != 0)
        {
          out.append(exceptions, excOffset, full);
          

          return full;
        }
      }
      int index;
      if ((!upperNotTitle) && (hasSlot(excWord, 3))) {
        index = 3; } else { int index;
        if (hasSlot(excWord, 2))
        {
          index = 2;
        } else
          return c ^ 0xFFFFFFFF; }
      int index;
      result = getSlotValue(excWord, index, excOffset2);
    }
    
    return result == c ? result ^ 0xFFFFFFFF : result;
  }
  

  public final int toFullUpper(int c, ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache)
  {
    return toUpperOrTitle(c, iter, out, locale, locCache, true);
  }
  

  public final int toFullTitle(int c, ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache)
  {
    return toUpperOrTitle(c, iter, out, locale, locCache, false);
  }
  
















































  public final int fold(int c, int options)
  {
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) >= 2) {
        c += getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      
      if ((excWord & 0x8000) != 0)
      {
        if ((options & 0xFF) == 0)
        {
          if (c == 73)
          {
            return 105; }
          if (c == 304)
          {
            return c;
          }
        }
        else {
          if (c == 73)
          {
            return 305; }
          if (c == 304)
          {
            return 105; }
        }
      }
      int index;
      if (hasSlot(excWord, 1)) {
        index = 1; } else { int index;
        if (hasSlot(excWord, 0)) {
          index = 0;
        } else
          return c; }
      int index;
      c = getSlotValue(excWord, index, excOffset);
    }
    return c;
  }
  

















  public final int toFullFolding(int c, StringBuilder out, int options)
  {
    int result = c;
    int props = trie.get(c);
    if (!propsHasException(props)) {
      if (getTypeFromProps(props) >= 2) {
        result = c + getDelta(props);
      }
    } else {
      int excOffset = getExceptionsOffset(props);
      int excWord = exceptions[(excOffset++)];
      

      int excOffset2 = excOffset;
      
      if ((excWord & 0x8000) != 0)
      {
        if ((options & 0xFF) == 0)
        {
          if (c == 73)
          {
            return 105; }
          if (c == 304)
          {
            out.append("i̇");
            return 2;
          }
        }
        else {
          if (c == 73)
          {
            return 305; }
          if (c == 304)
          {
            return 105;
          }
        }
      } else if (hasSlot(excWord, 7)) {
        long value = getSlotValueAndOffset(excWord, 7, excOffset);
        int full = (int)value & 0xFFFF;
        

        excOffset = (int)(value >> 32) + 1;
        

        excOffset += (full & 0xF);
        full = full >> 4 & 0xF;
        
        if (full != 0)
        {
          out.append(exceptions, excOffset, full);
          

          return full;
        }
      }
      int index;
      if (hasSlot(excWord, 1)) {
        index = 1; } else { int index;
        if (hasSlot(excWord, 0)) {
          index = 0;
        } else
          return c ^ 0xFFFFFFFF; }
      int index;
      result = getSlotValue(excWord, index, excOffset2);
    }
    
    return result == c ? result ^ 0xFFFFFFFF : result;
  }
  


  private static final int[] rootLocCache = { 1 };
  








  public static final StringBuilder dummyStringBuilder = new StringBuilder();
  private int[] indexes;
  
  public final boolean hasBinaryProperty(int c, int which) { switch (which) {
    case 22: 
      return 1 == getType(c);
    case 30: 
      return 2 == getType(c);
    case 27: 
      return isSoftDotted(c);
    case 34: 
      return isCaseSensitive(c);
    case 49: 
      return 0 != getType(c);
    case 50: 
      return getTypeOrIgnorable(c) >> 2 != 0;
    











    case 51: 
      dummyStringBuilder.setLength(0);
      return toFullLower(c, null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
    case 52: 
      dummyStringBuilder.setLength(0);
      return toFullUpper(c, null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
    case 53: 
      dummyStringBuilder.setLength(0);
      return toFullTitle(c, null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
    
    case 55: 
      dummyStringBuilder.setLength(0);
      return (toFullLower(c, null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0) || (toFullUpper(c, null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0) || (toFullTitle(c, null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0);
    }
    
    

    return false;
  }
  


  private char[] exceptions;
  
  private char[] unfold;
  
  private Trie2_16 trie;
  
  private static final String DATA_NAME = "ucase";
  
  private static final String DATA_TYPE = "icu";
  
  private static final String DATA_FILE_NAME = "ucase.icu";
  
  private static final byte[] FMT = { 99, 65, 83, 69 };
  
  private static final int IX_TRIE_SIZE = 2;
  
  private static final int IX_EXC_LENGTH = 3;
  private static final int IX_UNFOLD_LENGTH = 4;
  private static final int IX_TOP = 16;
  public static final int TYPE_MASK = 3;
  public static final int NONE = 0;
  public static final int LOWER = 1;
  public static final int UPPER = 2;
  public static final int TITLE = 3;
  private static final int SENSITIVE = 8;
  private static final int EXCEPTION = 16;
  private static final int DOT_MASK = 96;
  private static final int SOFT_DOTTED = 32;
  private static final int ABOVE = 64;
  private static final int OTHER_ACCENT = 96;
  private static final int DELTA_SHIFT = 7;
  
  private static final int getTypeFromProps(int props)
  {
    return props & 0x3;
  }
  
  private static final int getTypeAndIgnorableFromProps(int props) {
    return props & 0x7;
  }
  















  private static final int getDelta(int props)
  {
    return (short)props >> 7;
  }
  



  private static final int EXC_SHIFT = 5;
  


  private static final int EXC_LOWER = 0;
  


  private static final int EXC_FOLD = 1;
  


  private static final int EXC_UPPER = 2;
  

  private static final int EXC_TITLE = 3;
  

  private static final int EXC_CLOSURE = 6;
  

  private static final int EXC_FULL_MAPPINGS = 7;
  

  private static final int EXC_DOUBLE_SLOTS = 256;
  

  private static final int EXC_DOT_SHIFT = 7;
  

  private static final int EXC_CONDITIONAL_SPECIAL = 16384;
  

  private static final int EXC_CONDITIONAL_FOLD = 32768;
  

  private static final int FULL_LOWER = 15;
  

  private static final int CLOSURE_MAX_LENGTH = 15;
  

  private static final int UNFOLD_ROWS = 0;
  

  private static final int UNFOLD_ROW_WIDTH = 1;
  

  private static final int UNFOLD_STRING_WIDTH = 2;
  

  public static final UCaseProps INSTANCE;
  

  static
  {
    try
    {
      INSTANCE = new UCaseProps();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
