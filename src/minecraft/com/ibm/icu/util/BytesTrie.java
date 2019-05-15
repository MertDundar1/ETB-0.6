package com.ibm.icu.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;































public final class BytesTrie
  implements Cloneable, Iterable<Entry>
{
  public BytesTrie(byte[] trieBytes, int offset)
  {
    bytes_ = trieBytes;
    pos_ = (this.root_ = offset);
    remainingMatchLength_ = -1;
  }
  





  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  




  public BytesTrie reset()
  {
    pos_ = root_;
    remainingMatchLength_ = -1;
    return this;
  }
  























  public BytesTrie saveState(State state)
  {
    bytes = bytes_;
    root = root_;
    pos = pos_;
    remainingMatchLength = remainingMatchLength_;
    return this;
  }
  









  public BytesTrie resetToState(State state)
  {
    if ((bytes_ == bytes) && (bytes_ != null) && (root_ == root)) {
      pos_ = pos;
      remainingMatchLength_ = remainingMatchLength;
    } else {
      throw new IllegalArgumentException("incompatible trie state");
    }
    return this;
  }
  
  public static final class State
  {
    private byte[] bytes;
    private int root;
    private int pos;
    private int remainingMatchLength;
    
    public State() {}
  }
  
  public static enum Result
  {
    NO_MATCH, 
    





    NO_VALUE, 
    






    FINAL_VALUE, 
    






    INTERMEDIATE_VALUE;
    



    private Result() {}
    


    public boolean matches()
    {
      return this != NO_MATCH;
    }
    



    public boolean hasValue()
    {
      return ordinal() >= 2;
    }
    


    public boolean hasNext()
    {
      return (ordinal() & 0x1) != 0;
    }
  }
  




  public Result current()
  {
    int pos = pos_;
    if (pos < 0) {
      return Result.NO_MATCH;
    }
    int node;
    return (remainingMatchLength_ < 0) && ((node = bytes_[pos] & 0xFF) >= 32) ? valueResults_[(node & 0x1)] : Result.NO_VALUE;
  }
  









  public Result first(int inByte)
  {
    remainingMatchLength_ = -1;
    if (inByte < 0) {
      inByte += 256;
    }
    return nextImpl(root_, inByte);
  }
  






  public Result next(int inByte)
  {
    int pos = pos_;
    if (pos < 0) {
      return Result.NO_MATCH;
    }
    if (inByte < 0) {
      inByte += 256;
    }
    int length = remainingMatchLength_;
    if (length >= 0)
    {
      if (inByte == (bytes_[(pos++)] & 0xFF)) {
        remainingMatchLength_ = (--length);
        pos_ = pos;
        int node;
        return (length < 0) && ((node = bytes_[pos] & 0xFF) >= 32) ? valueResults_[(node & 0x1)] : Result.NO_VALUE;
      }
      
      stop();
      return Result.NO_MATCH;
    }
    
    return nextImpl(pos, inByte);
  }
  















  public Result next(byte[] s, int sIndex, int sLimit)
  {
    if (sIndex >= sLimit)
    {
      return current();
    }
    int pos = pos_;
    if (pos < 0) {
      return Result.NO_MATCH;
    }
    int length = remainingMatchLength_;
    


    for (;;)
    {
      if (sIndex == sLimit) {
        remainingMatchLength_ = length;
        pos_ = pos;
        int node;
        return (length < 0) && ((node = bytes_[pos] & 0xFF) >= 32) ? valueResults_[(node & 0x1)] : Result.NO_VALUE;
      }
      
      byte inByte = s[(sIndex++)];
      if (length < 0) {
        remainingMatchLength_ = length;
      }
      else {
        if (inByte != bytes_[pos]) {
          stop();
          return Result.NO_MATCH;
        }
        pos++;
        length--; continue;
      }
      for (;;) {
        int node = bytes_[(pos++)] & 0xFF;
        if (node < 16) {
          Result result = branchNext(pos, node, inByte & 0xFF);
          if (result == Result.NO_MATCH) {
            return Result.NO_MATCH;
          }
          
          if (sIndex == sLimit) {
            return result;
          }
          if (result == Result.FINAL_VALUE)
          {
            stop();
            return Result.NO_MATCH;
          }
          inByte = s[(sIndex++)];
          pos = pos_;
        } else { if (node < 32)
          {
            length = node - 16;
            if (inByte != bytes_[pos]) {
              stop();
              return Result.NO_MATCH;
            }
            pos++;
            length--;
            break; }
          if ((node & 0x1) != 0)
          {
            stop();
            return Result.NO_MATCH;
          }
          
          pos = skipValue(pos, node);
          
          assert ((bytes_[pos] & 0xFF) < 32);
        }
      }
    }
  }
  








  public int getValue()
  {
    int pos = pos_;
    int leadByte = bytes_[(pos++)] & 0xFF;
    assert (leadByte >= 32);
    return readValue(bytes_, pos, leadByte >> 1);
  }
  







  public long getUniqueValue()
  {
    int pos = pos_;
    if (pos < 0) {
      return 0L;
    }
    
    long uniqueValue = findUniqueValue(bytes_, pos + remainingMatchLength_ + 1, 0L);
    
    return uniqueValue << 31 >> 31;
  }
  







  public int getNextBytes(Appendable out)
  {
    int pos = pos_;
    if (pos < 0) {
      return 0;
    }
    if (remainingMatchLength_ >= 0) {
      append(out, bytes_[pos] & 0xFF);
      return 1;
    }
    int node = bytes_[(pos++)] & 0xFF;
    if (node >= 32) {
      if ((node & 0x1) != 0) {
        return 0;
      }
      pos = skipValue(pos, node);
      node = bytes_[(pos++)] & 0xFF;
      assert (node < 32);
    }
    
    if (node < 16) {
      if (node == 0) {
        node = bytes_[(pos++)] & 0xFF;
      }
      getNextBranchBytes(bytes_, pos, ++node, out);
      return node;
    }
    
    append(out, bytes_[pos] & 0xFF);
    return 1;
  }
  





  public Iterator iterator()
  {
    return new Iterator(bytes_, pos_, remainingMatchLength_, 0, null);
  }
  






  public Iterator iterator(int maxStringLength)
  {
    return new Iterator(bytes_, pos_, remainingMatchLength_, maxStringLength, null);
  }
  








  public static Iterator iterator(byte[] trieBytes, int offset, int maxStringLength)
  {
    return new Iterator(trieBytes, offset, -1, maxStringLength, null);
  }
  
  public static final class Entry {
    public int value;
    private byte[] bytes;
    private int length;
    
    private Entry(int capacity) {
      bytes = new byte[capacity];
    }
    


    public int bytesLength()
    {
      return length;
    }
    


    public byte byteAt(int index)
    {
      return bytes[index];
    }
    



    public void copyBytesTo(byte[] dest, int destOffset)
    {
      System.arraycopy(bytes, 0, dest, destOffset, length);
    }
    


    public ByteBuffer bytesAsByteBuffer()
    {
      return ByteBuffer.wrap(bytes, 0, length).asReadOnlyBuffer();
    }
    





    private void ensureCapacity(int len)
    {
      if (bytes.length < len) {
        byte[] newBytes = new byte[Math.min(2 * bytes.length, 2 * len)];
        System.arraycopy(bytes, 0, newBytes, 0, length);
        bytes = newBytes;
      }
    }
    
    private void append(byte b) { ensureCapacity(length + 1);
      bytes[(length++)] = b;
    }
    
    private void append(byte[] b, int off, int len) { ensureCapacity(length + len);
      System.arraycopy(b, off, bytes, length, len);
      length += len; }
    
    private void truncateString(int newLength) { length = newLength; }
  }
  
  public static final class Iterator implements Iterator<BytesTrie.Entry> {
    private byte[] bytes_;
    private int pos_;
    private int initialPos_;
    private int remainingMatchLength_;
    private int initialRemainingMatchLength_;
    private int maxLength_;
    private BytesTrie.Entry entry_;
    
    private Iterator(byte[] trieBytes, int offset, int remainingMatchLength, int maxStringLength) { bytes_ = trieBytes;
      pos_ = (this.initialPos_ = offset);
      remainingMatchLength_ = (this.initialRemainingMatchLength_ = remainingMatchLength);
      maxLength_ = maxStringLength;
      entry_ = new BytesTrie.Entry(maxLength_ != 0 ? maxLength_ : 32, null);
      int length = remainingMatchLength_;
      if (length >= 0)
      {
        length++;
        if ((maxLength_ > 0) && (length > maxLength_)) {
          length = maxLength_;
        }
        BytesTrie.Entry.access$600(entry_, bytes_, pos_, length);
        pos_ += length;
        remainingMatchLength_ -= length;
      }
    }
    




    public Iterator reset()
    {
      pos_ = initialPos_;
      remainingMatchLength_ = initialRemainingMatchLength_;
      int length = remainingMatchLength_ + 1;
      if ((maxLength_ > 0) && (length > maxLength_)) {
        length = maxLength_;
      }
      BytesTrie.Entry.access$700(entry_, length);
      pos_ += length;
      remainingMatchLength_ -= length;
      stack_.clear();
      return this;
    }
    


    public boolean hasNext()
    {
      return (pos_ >= 0) || (!stack_.isEmpty());
    }
    









    public BytesTrie.Entry next()
    {
      int pos = pos_;
      if (pos < 0) {
        if (stack_.isEmpty()) {
          throw new NoSuchElementException();
        }
        

        long top = ((Long)stack_.remove(stack_.size() - 1)).longValue();
        int length = (int)top;
        pos = (int)(top >> 32);
        BytesTrie.Entry.access$700(entry_, length & 0xFFFF);
        length >>>= 16;
        if (length > 1) {
          pos = branchNext(pos, length);
          if (pos < 0) {
            return entry_;
          }
        } else {
          BytesTrie.Entry.access$800(entry_, bytes_[(pos++)]);
        }
      }
      if (remainingMatchLength_ >= 0)
      {

        return truncateAndStop();
      }
      for (;;) {
        int node = bytes_[(pos++)] & 0xFF;
        if (node >= 32)
        {
          boolean isFinal = (node & 0x1) != 0;
          entry_.value = BytesTrie.readValue(bytes_, pos, node >> 1);
          if ((isFinal) || ((maxLength_ > 0) && (BytesTrie.Entry.access$1000(entry_) == maxLength_))) {
            pos_ = -1;
          } else {
            pos_ = BytesTrie.skipValue(pos, node);
          }
          return entry_;
        }
        if ((maxLength_ > 0) && (BytesTrie.Entry.access$1000(entry_) == maxLength_)) {
          return truncateAndStop();
        }
        if (node < 16) {
          if (node == 0) {
            node = bytes_[(pos++)] & 0xFF;
          }
          pos = branchNext(pos, node + 1);
          if (pos < 0) {
            return entry_;
          }
        }
        else {
          int length = node - 16 + 1;
          if ((maxLength_ > 0) && (BytesTrie.Entry.access$1000(entry_) + length > maxLength_)) {
            BytesTrie.Entry.access$600(entry_, bytes_, pos, maxLength_ - BytesTrie.Entry.access$1000(entry_));
            return truncateAndStop();
          }
          BytesTrie.Entry.access$600(entry_, bytes_, pos, length);
          pos += length;
        }
      }
    }
    




    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    private BytesTrie.Entry truncateAndStop() {
      pos_ = -1;
      entry_.value = -1;
      return entry_;
    }
    
    private int branchNext(int pos, int length) {
      while (length > 5) {
        pos++;
        
        stack_.add(Long.valueOf(BytesTrie.skipDelta(bytes_, pos) << 32 | length - (length >> 1) << 16 | BytesTrie.Entry.access$1000(entry_)));
        
        length >>= 1;
        pos = BytesTrie.jumpByDelta(bytes_, pos);
      }
      

      byte trieByte = bytes_[(pos++)];
      int node = bytes_[(pos++)] & 0xFF;
      boolean isFinal = (node & 0x1) != 0;
      int value = BytesTrie.readValue(bytes_, pos, node >> 1);
      pos = BytesTrie.skipValue(pos, node);
      stack_.add(Long.valueOf(pos << 32 | length - 1 << 16 | BytesTrie.Entry.access$1000(entry_)));
      BytesTrie.Entry.access$800(entry_, trieByte);
      if (isFinal) {
        pos_ = -1;
        entry_.value = value;
        return -1;
      }
      return pos + value;
    }
    

















    private ArrayList<Long> stack_ = new ArrayList();
  }
  
  private void stop() {
    pos_ = -1;
  }
  
  private static int readValue(byte[] bytes, int pos, int leadByte)
  {
    int value;
    int value;
    if (leadByte < 81) {
      value = leadByte - 16; } else { int value;
      if (leadByte < 108) {
        value = leadByte - 81 << 8 | bytes[pos] & 0xFF; } else { int value;
        if (leadByte < 126) {
          value = leadByte - 108 << 16 | (bytes[pos] & 0xFF) << 8 | bytes[(pos + 1)] & 0xFF; } else { int value;
          if (leadByte == 126) {
            value = (bytes[pos] & 0xFF) << 16 | (bytes[(pos + 1)] & 0xFF) << 8 | bytes[(pos + 2)] & 0xFF;
          } else
            value = bytes[pos] << 24 | (bytes[(pos + 1)] & 0xFF) << 16 | (bytes[(pos + 2)] & 0xFF) << 8 | bytes[(pos + 3)] & 0xFF;
        } } }
    return value;
  }
  
  private static int skipValue(int pos, int leadByte) { assert (leadByte >= 32);
    if (leadByte >= 162) {
      if (leadByte < 216) {
        pos++;
      } else if (leadByte < 252) {
        pos += 2;
      } else {
        pos += 3 + (leadByte >> 1 & 0x1);
      }
    }
    return pos;
  }
  
  private static int skipValue(byte[] bytes, int pos) { int leadByte = bytes[(pos++)] & 0xFF;
    return skipValue(pos, leadByte);
  }
  
  private static int jumpByDelta(byte[] bytes, int pos)
  {
    int delta = bytes[(pos++)] & 0xFF;
    if (delta >= 192)
    {
      if (delta < 240) {
        delta = delta - 192 << 8 | bytes[(pos++)] & 0xFF;
      } else if (delta < 254) {
        delta = delta - 240 << 16 | (bytes[pos] & 0xFF) << 8 | bytes[(pos + 1)] & 0xFF;
        pos += 2;
      } else if (delta == 254) {
        delta = (bytes[pos] & 0xFF) << 16 | (bytes[(pos + 1)] & 0xFF) << 8 | bytes[(pos + 2)] & 0xFF;
        pos += 3;
      } else {
        delta = bytes[pos] << 24 | (bytes[(pos + 1)] & 0xFF) << 16 | (bytes[(pos + 2)] & 0xFF) << 8 | bytes[(pos + 3)] & 0xFF;
        pos += 4;
      } }
    return pos + delta;
  }
  
  private static int skipDelta(byte[] bytes, int pos) {
    int delta = bytes[(pos++)] & 0xFF;
    if (delta >= 192) {
      if (delta < 240) {
        pos++;
      } else if (delta < 254) {
        pos += 2;
      } else {
        pos += 3 + (delta & 0x1);
      }
    }
    return pos;
  }
  
  private static Result[] valueResults_ = { Result.INTERMEDIATE_VALUE, Result.FINAL_VALUE };
  static final int kMaxBranchLinearSubNodeLength = 5;
  static final int kMinLinearMatch = 16;
  static final int kMaxLinearMatchLength = 16;
  
  private Result branchNext(int pos, int length, int inByte) { if (length == 0) {
      length = bytes_[(pos++)] & 0xFF;
    }
    length++;
    

    while (length > 5) {
      if (inByte < (bytes_[(pos++)] & 0xFF)) {
        length >>= 1;
        pos = jumpByDelta(bytes_, pos);
      } else {
        length -= (length >> 1);
        pos = skipDelta(bytes_, pos);
      }
    }
    

    do
    {
      if (inByte == (bytes_[(pos++)] & 0xFF))
      {
        int node = bytes_[pos] & 0xFF;
        assert (node >= 32);
        Result result; Result result; if ((node & 0x1) != 0)
        {
          result = Result.FINAL_VALUE;
        }
        else {
          pos++;
          
          node >>= 1;
          int delta;
          int delta; if (node < 81) {
            delta = node - 16; } else { int delta;
            if (node < 108) {
              delta = node - 81 << 8 | bytes_[(pos++)] & 0xFF;
            } else if (node < 126) {
              int delta = node - 108 << 16 | (bytes_[pos] & 0xFF) << 8 | bytes_[(pos + 1)] & 0xFF;
              pos += 2;
            } else if (node == 126) {
              int delta = (bytes_[pos] & 0xFF) << 16 | (bytes_[(pos + 1)] & 0xFF) << 8 | bytes_[(pos + 2)] & 0xFF;
              pos += 3;
            } else {
              delta = bytes_[pos] << 24 | (bytes_[(pos + 1)] & 0xFF) << 16 | (bytes_[(pos + 2)] & 0xFF) << 8 | bytes_[(pos + 3)] & 0xFF;
              pos += 4;
            }
          }
          pos += delta;
          node = bytes_[pos] & 0xFF;
          result = node >= 32 ? valueResults_[(node & 0x1)] : Result.NO_VALUE;
        }
        pos_ = pos;
        return result;
      }
      length--;
      pos = skipValue(bytes_, pos);
    } while (length > 1);
    if (inByte == (bytes_[(pos++)] & 0xFF)) {
      pos_ = pos;
      int node = bytes_[pos] & 0xFF;
      return node >= 32 ? valueResults_[(node & 0x1)] : Result.NO_VALUE;
    }
    stop();
    return Result.NO_MATCH; }
  
  static final int kMinValueLead = 32;
  private static final int kValueIsFinal = 1;
  static final int kMinOneByteValueLead = 16;
  static final int kMaxOneByteValue = 64;
  
  private Result nextImpl(int pos, int inByte) { for (;;) { int node = bytes_[(pos++)] & 0xFF;
      if (node < 16)
        return branchNext(pos, node, inByte);
      if (node < 32)
      {
        int length = node - 16;
        if (inByte != (bytes_[(pos++)] & 0xFF)) break;
        remainingMatchLength_ = (--length);
        pos_ = pos;
        return (length < 0) && ((node = bytes_[pos] & 0xFF) >= 32) ? valueResults_[(node & 0x1)] : Result.NO_VALUE;
      }
      



      if ((node & 0x1) != 0) {
        break;
      }
      

      pos = skipValue(pos, node);
      
      assert ((bytes_[pos] & 0xFF) < 32);
    }
    
    stop();
    return Result.NO_MATCH;
  }
  
  static final int kMinTwoByteValueLead = 81;
  static final int kMaxTwoByteValue = 6911;
  static final int kMinThreeByteValueLead = 108;
  static final int kFourByteValueLead = 126;
  static final int kMaxThreeByteValue = 1179647;
  
  private static long findUniqueValueFromBranch(byte[] bytes, int pos, int length, long uniqueValue) {
    while (length > 5) {
      pos++;
      uniqueValue = findUniqueValueFromBranch(bytes, jumpByDelta(bytes, pos), length >> 1, uniqueValue);
      if (uniqueValue == 0L) {
        return 0L;
      }
      length -= (length >> 1);
      pos = skipDelta(bytes, pos);
    }
    do {
      pos++;
      
      int node = bytes[(pos++)] & 0xFF;
      boolean isFinal = (node & 0x1) != 0;
      int value = readValue(bytes, pos, node >> 1);
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
        uniqueValue = findUniqueValue(bytes, pos + value, uniqueValue);
        if (uniqueValue == 0L) {
          return 0L;
        }
      }
      length--; } while (length > 1);
    
    return pos + 1 << 33 | uniqueValue & 0x1FFFFFFFF;
  }
  
  static final int kFiveByteValueLead = 127;
  static final int kMaxOneByteDelta = 191;
  static final int kMinTwoByteDeltaLead = 192;
  
  private static long findUniqueValue(byte[] bytes, int pos, long uniqueValue) {
    for (;;) { int node = bytes[(pos++)] & 0xFF;
      if (node < 16) {
        if (node == 0) {
          node = bytes[(pos++)] & 0xFF;
        }
        uniqueValue = findUniqueValueFromBranch(bytes, pos, node + 1, uniqueValue);
        if (uniqueValue == 0L) {
          return 0L;
        }
        pos = (int)(uniqueValue >>> 33);
      } else if (node < 32)
      {
        pos += node - 16 + 1;
      } else {
        boolean isFinal = (node & 0x1) != 0;
        int value = readValue(bytes, pos, node >> 1);
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
        pos = skipValue(pos, node);
      } } }
  
  static final int kMinThreeByteDeltaLead = 240;
  static final int kFourByteDeltaLead = 254;
  static final int kFiveByteDeltaLead = 255;
  static final int kMaxTwoByteDelta = 12287;
  
  private static void getNextBranchBytes(byte[] bytes, int pos, int length, Appendable out) { while (length > 5) {
      pos++;
      getNextBranchBytes(bytes, jumpByDelta(bytes, pos), length >> 1, out);
      length -= (length >> 1);
      pos = skipDelta(bytes, pos);
    }
    do {
      append(out, bytes[(pos++)] & 0xFF);
      pos = skipValue(bytes, pos);
      length--; } while (length > 1);
    append(out, bytes[pos] & 0xFF); }
  
  static final int kMaxThreeByteDelta = 917503;
  
  private static void append(Appendable out, int c) { try { out.append((char)c);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private byte[] bytes_;
  private int root_;
  private int pos_;
  private int remainingMatchLength_;
}
