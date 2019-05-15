package com.ibm.icu.util;

import com.ibm.icu.text.UTF16;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;


































public final class CharsTrie
  implements Cloneable, Iterable<Entry>
{
  public CharsTrie(CharSequence trieChars, int offset)
  {
    chars_ = trieChars;
    pos_ = (this.root_ = offset);
    remainingMatchLength_ = -1;
  }
  





  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  




  public CharsTrie reset()
  {
    pos_ = root_;
    remainingMatchLength_ = -1;
    return this;
  }
  























  public CharsTrie saveState(State state)
  {
    chars = chars_;
    root = root_;
    pos = pos_;
    remainingMatchLength = remainingMatchLength_;
    return this;
  }
  









  public CharsTrie resetToState(State state)
  {
    if ((chars_ == chars) && (chars_ != null) && (root_ == root)) {
      pos_ = pos;
      remainingMatchLength_ = remainingMatchLength;
    } else {
      throw new IllegalArgumentException("incompatible trie state");
    }
    return this;
  }
  





  public BytesTrie.Result current()
  {
    int pos = pos_;
    if (pos < 0) {
      return BytesTrie.Result.NO_MATCH;
    }
    int node;
    return (remainingMatchLength_ < 0) && ((node = chars_.charAt(pos)) >= '@') ? valueResults_[(node >> 15)] : BytesTrie.Result.NO_VALUE;
  }
  








  public BytesTrie.Result first(int inUnit)
  {
    remainingMatchLength_ = -1;
    return nextImpl(root_, inUnit);
  }
  







  public BytesTrie.Result firstForCodePoint(int cp)
  {
    return first(UTF16.getLeadSurrogate(cp)).hasNext() ? next(UTF16.getTrailSurrogate(cp)) : cp <= 65535 ? first(cp) : BytesTrie.Result.NO_MATCH;
  }
  









  public BytesTrie.Result next(int inUnit)
  {
    int pos = pos_;
    if (pos < 0) {
      return BytesTrie.Result.NO_MATCH;
    }
    int length = remainingMatchLength_;
    if (length >= 0)
    {
      if (inUnit == chars_.charAt(pos++)) {
        remainingMatchLength_ = (--length);
        pos_ = pos;
        int node;
        return (length < 0) && ((node = chars_.charAt(pos)) >= '@') ? valueResults_[(node >> 15)] : BytesTrie.Result.NO_VALUE;
      }
      
      stop();
      return BytesTrie.Result.NO_MATCH;
    }
    
    return nextImpl(pos, inUnit);
  }
  






  public BytesTrie.Result nextForCodePoint(int cp)
  {
    return next(UTF16.getLeadSurrogate(cp)).hasNext() ? next(UTF16.getTrailSurrogate(cp)) : cp <= 65535 ? next(cp) : BytesTrie.Result.NO_MATCH;
  }
  



















  public BytesTrie.Result next(CharSequence s, int sIndex, int sLimit)
  {
    if (sIndex >= sLimit)
    {
      return current();
    }
    int pos = pos_;
    if (pos < 0) {
      return BytesTrie.Result.NO_MATCH;
    }
    int length = remainingMatchLength_;
    


    for (;;)
    {
      if (sIndex == sLimit) {
        remainingMatchLength_ = length;
        pos_ = pos;
        int node;
        return (length < 0) && ((node = chars_.charAt(pos)) >= '@') ? valueResults_[(node >> 15)] : BytesTrie.Result.NO_VALUE;
      }
      
      char inUnit = s.charAt(sIndex++);
      if (length < 0) {
        remainingMatchLength_ = length;
      }
      else {
        if (inUnit != chars_.charAt(pos)) {
          stop();
          return BytesTrie.Result.NO_MATCH;
        }
        pos++;
        length--; continue;
      }
      int node = chars_.charAt(pos++);
      for (;;) {
        if (node < 48) {
          BytesTrie.Result result = branchNext(pos, node, inUnit);
          if (result == BytesTrie.Result.NO_MATCH) {
            return BytesTrie.Result.NO_MATCH;
          }
          
          if (sIndex == sLimit) {
            return result;
          }
          if (result == BytesTrie.Result.FINAL_VALUE)
          {
            stop();
            return BytesTrie.Result.NO_MATCH;
          }
          inUnit = s.charAt(sIndex++);
          pos = pos_;
          node = chars_.charAt(pos++);
        } else { if (node < 64)
          {
            length = node - 48;
            if (inUnit != chars_.charAt(pos)) {
              stop();
              return BytesTrie.Result.NO_MATCH;
            }
            pos++;
            length--;
            break; }
          if ((node & 0x8000) != 0)
          {
            stop();
            return BytesTrie.Result.NO_MATCH;
          }
          
          pos = skipNodeValue(pos, node);
          node &= 0x3F;
        }
      }
    }
  }
  








  public int getValue()
  {
    int pos = pos_;
    int leadUnit = chars_.charAt(pos++);
    assert (leadUnit >= 64);
    return (leadUnit & 0x8000) != 0 ? readValue(chars_, pos, leadUnit & 0x7FFF) : readNodeValue(chars_, pos, leadUnit);
  }
  








  public long getUniqueValue()
  {
    int pos = pos_;
    if (pos < 0) {
      return 0L;
    }
    
    long uniqueValue = findUniqueValue(chars_, pos + remainingMatchLength_ + 1, 0L);
    
    return uniqueValue << 31 >> 31;
  }
  







  public int getNextChars(Appendable out)
  {
    int pos = pos_;
    if (pos < 0) {
      return 0;
    }
    if (remainingMatchLength_ >= 0) {
      append(out, chars_.charAt(pos));
      return 1;
    }
    int node = chars_.charAt(pos++);
    if (node >= 64) {
      if ((node & 0x8000) != 0) {
        return 0;
      }
      pos = skipNodeValue(pos, node);
      node &= 0x3F;
    }
    
    if (node < 48) {
      if (node == 0) {
        node = chars_.charAt(pos++);
      }
      getNextBranchChars(chars_, pos, ++node, out);
      return node;
    }
    
    append(out, chars_.charAt(pos));
    return 1;
  }
  





  public Iterator iterator()
  {
    return new Iterator(chars_, pos_, remainingMatchLength_, 0, null);
  }
  






  public Iterator iterator(int maxStringLength)
  {
    return new Iterator(chars_, pos_, remainingMatchLength_, maxStringLength, null);
  }
  








  public static Iterator iterator(CharSequence trieChars, int offset, int maxStringLength)
  {
    return new Iterator(trieChars, offset, -1, maxStringLength, null);
  }
  


  public static final class Iterator
    implements Iterator<CharsTrie.Entry>
  {
    private CharSequence chars_;
    

    private int pos_;
    

    private int initialPos_;
    

    private int remainingMatchLength_;
    

    private int initialRemainingMatchLength_;
    

    private boolean skipValue_;
    


    private Iterator(CharSequence trieChars, int offset, int remainingMatchLength, int maxStringLength)
    {
      chars_ = trieChars;
      pos_ = (this.initialPos_ = offset);
      remainingMatchLength_ = (this.initialRemainingMatchLength_ = remainingMatchLength);
      maxLength_ = maxStringLength;
      int length = remainingMatchLength_;
      if (length >= 0)
      {
        length++;
        if ((maxLength_ > 0) && (length > maxLength_)) {
          length = maxLength_;
        }
        str_.append(chars_, pos_, pos_ + length);
        pos_ += length;
        remainingMatchLength_ -= length;
      }
    }
    




    public Iterator reset()
    {
      pos_ = initialPos_;
      remainingMatchLength_ = initialRemainingMatchLength_;
      skipValue_ = false;
      int length = remainingMatchLength_ + 1;
      if ((maxLength_ > 0) && (length > maxLength_)) {
        length = maxLength_;
      }
      str_.setLength(length);
      pos_ += length;
      remainingMatchLength_ -= length;
      stack_.clear();
      return this;
    }
    


    public boolean hasNext()
    {
      return (pos_ >= 0) || (!stack_.isEmpty());
    }
    









    public CharsTrie.Entry next()
    {
      int pos = pos_;
      if (pos < 0) {
        if (stack_.isEmpty()) {
          throw new NoSuchElementException();
        }
        

        long top = ((Long)stack_.remove(stack_.size() - 1)).longValue();
        int length = (int)top;
        pos = (int)(top >> 32);
        str_.setLength(length & 0xFFFF);
        length >>>= 16;
        if (length > 1) {
          pos = branchNext(pos, length);
          if (pos < 0) {
            return entry_;
          }
        } else {
          str_.append(chars_.charAt(pos++));
        }
      }
      if (remainingMatchLength_ >= 0)
      {

        return truncateAndStop();
      }
      for (;;) {
        int node = chars_.charAt(pos++);
        if (node >= 64) {
          if (skipValue_) {
            pos = CharsTrie.skipNodeValue(pos, node);
            node &= 0x3F;
            skipValue_ = false;
          }
          else {
            boolean isFinal = (node & 0x8000) != 0;
            if (isFinal) {
              entry_.value = CharsTrie.readValue(chars_, pos, node & 0x7FFF);
            } else {
              entry_.value = CharsTrie.readNodeValue(chars_, pos, node);
            }
            if ((isFinal) || ((maxLength_ > 0) && (str_.length() == maxLength_))) {
              pos_ = -1;

            }
            else
            {

              pos_ = (pos - 1);
              skipValue_ = true;
            }
            entry_.chars = str_;
            return entry_;
          }
        }
        if ((maxLength_ > 0) && (str_.length() == maxLength_)) {
          return truncateAndStop();
        }
        if (node < 48) {
          if (node == 0) {
            node = chars_.charAt(pos++);
          }
          pos = branchNext(pos, node + 1);
          if (pos < 0) {
            return entry_;
          }
        }
        else {
          int length = node - 48 + 1;
          if ((maxLength_ > 0) && (str_.length() + length > maxLength_)) {
            str_.append(chars_, pos, pos + maxLength_ - str_.length());
            return truncateAndStop();
          }
          str_.append(chars_, pos, pos + length);
          pos += length;
        }
      }
    }
    




    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    private CharsTrie.Entry truncateAndStop() {
      pos_ = -1;
      

      entry_.chars = str_;
      entry_.value = -1;
      return entry_;
    }
    
    private int branchNext(int pos, int length) {
      while (length > 5) {
        pos++;
        
        stack_.add(Long.valueOf(CharsTrie.skipDelta(chars_, pos) << 32 | length - (length >> 1) << 16 | str_.length()));
        
        length >>= 1;
        pos = CharsTrie.jumpByDelta(chars_, pos);
      }
      

      char trieUnit = chars_.charAt(pos++);
      int node = chars_.charAt(pos++);
      boolean isFinal = (node & 0x8000) != 0;
      int value = CharsTrie.readValue(chars_, pos, node &= 0x7FFF);
      pos = CharsTrie.skipValue(pos, node);
      stack_.add(Long.valueOf(pos << 32 | length - 1 << 16 | str_.length()));
      str_.append(trieUnit);
      if (isFinal) {
        pos_ = -1;
        entry_.chars = str_;
        entry_.value = value;
        return -1;
      }
      return pos + value;
    }
    








    private StringBuilder str_ = new StringBuilder();
    private int maxLength_;
    private CharsTrie.Entry entry_ = new CharsTrie.Entry(null);
    







    private ArrayList<Long> stack_ = new ArrayList();
  }
  
  private void stop() {
    pos_ = -1;
  }
  
  private static int readValue(CharSequence chars, int pos, int leadUnit)
  {
    int value;
    int value;
    if (leadUnit < 16384) {
      value = leadUnit; } else { int value;
      if (leadUnit < 32767) {
        value = leadUnit - 16384 << 16 | chars.charAt(pos);
      } else
        value = chars.charAt(pos) << '\020' | chars.charAt(pos + 1);
    }
    return value;
  }
  
  private static int skipValue(int pos, int leadUnit) { if (leadUnit >= 16384) {
      if (leadUnit < 32767) {
        pos++;
      } else {
        pos += 2;
      }
    }
    return pos;
  }
  
  private static int skipValue(CharSequence chars, int pos) { int leadUnit = chars.charAt(pos++);
    return skipValue(pos, leadUnit & 0x7FFF);
  }
  
  private static int readNodeValue(CharSequence chars, int pos, int leadUnit) {
    assert ((64 <= leadUnit) && (leadUnit < 32768));
    int value;
    int value; if (leadUnit < 16448) {
      value = (leadUnit >> 6) - 1; } else { int value;
      if (leadUnit < 32704) {
        value = (leadUnit & 0x7FC0) - 16448 << 10 | chars.charAt(pos);
      } else
        value = chars.charAt(pos) << '\020' | chars.charAt(pos + 1);
    }
    return value;
  }
  
  private static int skipNodeValue(int pos, int leadUnit) { assert ((64 <= leadUnit) && (leadUnit < 32768));
    if (leadUnit >= 16448) {
      if (leadUnit < 32704) {
        pos++;
      } else {
        pos += 2;
      }
    }
    return pos;
  }
  
  private static int jumpByDelta(CharSequence chars, int pos) {
    int delta = chars.charAt(pos++);
    if (delta >= 64512) {
      if (delta == 65535) {
        delta = chars.charAt(pos) << '\020' | chars.charAt(pos + 1);
        pos += 2;
      } else {
        delta = delta - 64512 << 16 | chars.charAt(pos++);
      }
    }
    return pos + delta;
  }
  
  private static int skipDelta(CharSequence chars, int pos) {
    int delta = chars.charAt(pos++);
    if (delta >= 64512) {
      if (delta == 65535) {
        pos += 2;
      } else {
        pos++;
      }
    }
    return pos;
  }
  
  private static BytesTrie.Result[] valueResults_ = { BytesTrie.Result.INTERMEDIATE_VALUE, BytesTrie.Result.FINAL_VALUE };
  static final int kMaxBranchLinearSubNodeLength = 5;
  static final int kMinLinearMatch = 48;
  
  private BytesTrie.Result branchNext(int pos, int length, int inUnit) {
    if (length == 0) {
      length = chars_.charAt(pos++);
    }
    length++;
    

    while (length > 5) {
      if (inUnit < chars_.charAt(pos++)) {
        length >>= 1;
        pos = jumpByDelta(chars_, pos);
      } else {
        length -= (length >> 1);
        pos = skipDelta(chars_, pos);
      }
    }
    

    do
    {
      if (inUnit == chars_.charAt(pos++))
      {
        int node = chars_.charAt(pos);
        BytesTrie.Result result; BytesTrie.Result result; if ((node & 0x8000) != 0)
        {
          result = BytesTrie.Result.FINAL_VALUE;
        }
        else {
          pos++;
          int delta;
          int delta;
          if (node < 16384) {
            delta = node; } else { int delta;
            if (node < 32767) {
              delta = node - 16384 << 16 | chars_.charAt(pos++);
            } else {
              delta = chars_.charAt(pos) << '\020' | chars_.charAt(pos + 1);
              pos += 2;
            }
          }
          pos += delta;
          node = chars_.charAt(pos);
          result = node >= 64 ? valueResults_[(node >> 15)] : BytesTrie.Result.NO_VALUE;
        }
        pos_ = pos;
        return result;
      }
      length--;
      pos = skipValue(chars_, pos);
    } while (length > 1);
    if (inUnit == chars_.charAt(pos++)) {
      pos_ = pos;
      int node = chars_.charAt(pos);
      return node >= 64 ? valueResults_[(node >> 15)] : BytesTrie.Result.NO_VALUE;
    }
    stop();
    return BytesTrie.Result.NO_MATCH; }
  
  static final int kMaxLinearMatchLength = 16;
  static final int kMinValueLead = 64;
  static final int kNodeTypeMask = 63;
  
  private BytesTrie.Result nextImpl(int pos, int inUnit) { int node = chars_.charAt(pos++);
    for (;;) {
      if (node < 48)
        return branchNext(pos, node, inUnit);
      if (node < 64)
      {
        int length = node - 48;
        if (inUnit != chars_.charAt(pos++)) break;
        remainingMatchLength_ = (--length);
        pos_ = pos;
        return (length < 0) && ((node = chars_.charAt(pos)) >= '@') ? valueResults_[(node >> 15)] : BytesTrie.Result.NO_VALUE;
      }
      



      if ((node & 0x8000) != 0) {
        break;
      }
      

      pos = skipNodeValue(pos, node);
      node &= 0x3F;
    }
    
    stop();
    return BytesTrie.Result.NO_MATCH;
  }
  
  static final int kValueIsFinal = 32768;
  static final int kMaxOneUnitValue = 16383;
  static final int kMinTwoUnitValueLead = 16384;
  static final int kThreeUnitValueLead = 32767;
  static final int kMaxTwoUnitValue = 1073676287;
  
  private static long findUniqueValueFromBranch(CharSequence chars, int pos, int length, long uniqueValue) {
    while (length > 5) {
      pos++;
      uniqueValue = findUniqueValueFromBranch(chars, jumpByDelta(chars, pos), length >> 1, uniqueValue);
      if (uniqueValue == 0L) {
        return 0L;
      }
      length -= (length >> 1);
      pos = skipDelta(chars, pos);
    }
    do {
      pos++;
      
      int node = chars.charAt(pos++);
      boolean isFinal = (node & 0x8000) != 0;
      node &= 0x7FFF;
      int value = readValue(chars, pos, node);
      pos = skipValue(pos, node);
      if (isFinal) {
        if (uniqueValue != 0L) {
          if (value != (int)(uniqueValue >> 1)) {
            return 0L;
          }
        } else {
          uniqueValue = value << 1 | 1L;
        }
      } else {
        uniqueValue = findUniqueValue(chars, pos + value, uniqueValue);
        if (uniqueValue == 0L) {
          return 0L;
        }
      }
      length--; } while (length > 1);
    
    return pos + 1 << 33 | uniqueValue & 0x1FFFFFFFF;
  }
  
  static final int kMaxOneUnitNodeValue = 255;
  static final int kMinTwoUnitNodeValueLead = 16448;
  static final int kThreeUnitNodeValueLead = 32704;
  
  private static long findUniqueValue(CharSequence chars, int pos, long uniqueValue) { int node = chars.charAt(pos++);
    for (;;) {
      if (node < 48) {
        if (node == 0) {
          node = chars.charAt(pos++);
        }
        uniqueValue = findUniqueValueFromBranch(chars, pos, node + 1, uniqueValue);
        if (uniqueValue == 0L) {
          return 0L;
        }
        pos = (int)(uniqueValue >>> 33);
        node = chars.charAt(pos++);
      } else if (node < 64)
      {
        pos += node - 48 + 1;
        node = chars.charAt(pos++);
      } else {
        boolean isFinal = (node & 0x8000) != 0;
        int value;
        int value; if (isFinal) {
          value = readValue(chars, pos, node & 0x7FFF);
        } else {
          value = readNodeValue(chars, pos, node);
        }
        if (uniqueValue != 0L) {
          if (value != (int)(uniqueValue >> 1)) {
            return 0L;
          }
        } else {
          uniqueValue = value << 1 | 1L;
        }
        if (isFinal) {
          return uniqueValue;
        }
        pos = skipNodeValue(pos, node);
        node &= 0x3F;
      } } }
  
  static final int kMaxTwoUnitNodeValue = 16646143;
  static final int kMaxOneUnitDelta = 64511;
  static final int kMinTwoUnitDeltaLead = 64512;
  static final int kThreeUnitDeltaLead = 65535;
  
  private static void getNextBranchChars(CharSequence chars, int pos, int length, Appendable out) { while (length > 5) {
      pos++;
      getNextBranchChars(chars, jumpByDelta(chars, pos), length >> 1, out);
      length -= (length >> 1);
      pos = skipDelta(chars, pos);
    }
    do {
      append(out, chars.charAt(pos++));
      pos = skipValue(chars, pos);
      length--; } while (length > 1);
    append(out, chars.charAt(pos)); }
  
  static final int kMaxTwoUnitDelta = 67043327;
  
  private static void append(Appendable out, int c) { try { out.append((char)c);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private CharSequence chars_;
  private int root_;
  private int pos_;
  private int remainingMatchLength_;
  public static final class Entry
  {
    public CharSequence chars;
    public int value;
    
    private Entry() {}
  }
  
  public static final class State
  {
    private CharSequence chars;
    private int root;
    private int pos;
    private int remainingMatchLength;
    
    public State() {}
  }
}
