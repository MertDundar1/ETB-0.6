package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;







































































public abstract class Trie2
  implements Iterable<Range>
{
  public Trie2() {}
  
  public static Trie2 createFromSerialized(InputStream is)
    throws IOException
  {
    DataInputStream dis = new DataInputStream(is);
    boolean needByteSwap = false;
    
    UTrie2Header header = new UTrie2Header();
    

    signature = dis.readInt();
    switch (signature) {
    case 1416784178: 
      needByteSwap = false;
      break;
    case 845771348: 
      needByteSwap = true;
      signature = Integer.reverseBytes(signature);
      break;
    default: 
      throw new IllegalArgumentException("Stream does not contain a serialized UTrie2");
    }
    
    options = swapShort(needByteSwap, dis.readUnsignedShort());
    indexLength = swapShort(needByteSwap, dis.readUnsignedShort());
    shiftedDataLength = swapShort(needByteSwap, dis.readUnsignedShort());
    index2NullOffset = swapShort(needByteSwap, dis.readUnsignedShort());
    dataNullOffset = swapShort(needByteSwap, dis.readUnsignedShort());
    shiftedHighStart = swapShort(needByteSwap, dis.readUnsignedShort());
    


    if ((options & 0xF) > 1)
      throw new IllegalArgumentException("UTrie2 serialized format error.");
    Trie2 This;
    ValueWidth width;
    Trie2 This;
    if ((options & 0xF) == 0) {
      ValueWidth width = ValueWidth.BITS_16;
      This = new Trie2_16();
    } else {
      width = ValueWidth.BITS_32;
      This = new Trie2_32();
    }
    header = header;
    

    indexLength = indexLength;
    dataLength = (shiftedDataLength << 2);
    index2NullOffset = index2NullOffset;
    dataNullOffset = dataNullOffset;
    highStart = (shiftedHighStart << 11);
    highValueIndex = (dataLength - 4);
    if (width == ValueWidth.BITS_16) {
      highValueIndex += indexLength;
    }
    



    int indexArraySize = indexLength;
    if (width == ValueWidth.BITS_16) {
      indexArraySize += dataLength;
    }
    index = new char[indexArraySize];
    


    for (int i = 0; i < indexLength; i++) {
      index[i] = swapChar(needByteSwap, dis.readChar());
    }
    



    if (width == ValueWidth.BITS_16) {
      data16 = indexLength;
      for (i = 0; i < dataLength; i++) {
        index[(data16 + i)] = swapChar(needByteSwap, dis.readChar());
      }
    }
    data32 = new int[dataLength];
    for (i = 0; i < dataLength; i++) {
      data32[i] = swapInt(needByteSwap, dis.readInt());
    }
    

    switch (2.$SwitchMap$com$ibm$icu$impl$Trie2$ValueWidth[width.ordinal()]) {
    case 1: 
      data32 = null;
      initialValue = index[dataNullOffset];
      errorValue = index[(data16 + 128)];
      break;
    case 2: 
      data16 = 0;
      initialValue = data32[dataNullOffset];
      errorValue = data32[''];
      break;
    default: 
      throw new IllegalArgumentException("UTrie2 serialized format error.");
    }
    
    return This;
  }
  
  private static int swapShort(boolean needSwap, int value)
  {
    return needSwap ? Short.reverseBytes((short)value) & 0xFFFF : value;
  }
  
  private static char swapChar(boolean needSwap, char value) {
    return needSwap ? (char)Short.reverseBytes((short)value) : value;
  }
  
  private static int swapInt(boolean needSwap, int value) {
    return needSwap ? Integer.reverseBytes(value) : value;
  }
  












  public static int getVersion(InputStream is, boolean littleEndianOk)
    throws IOException
  {
    if (!is.markSupported()) {
      throw new IllegalArgumentException("Input stream must support mark().");
    }
    is.mark(4);
    byte[] sig = new byte[4];
    int read = is.read(sig);
    is.reset();
    
    if (read != sig.length) {
      return 0;
    }
    
    if ((sig[0] == 84) && (sig[1] == 114) && (sig[2] == 105) && (sig[3] == 101)) {
      return 1;
    }
    if ((sig[0] == 84) && (sig[1] == 114) && (sig[2] == 105) && (sig[3] == 50)) {
      return 2;
    }
    if (littleEndianOk) {
      if ((sig[0] == 101) && (sig[1] == 105) && (sig[2] == 114) && (sig[3] == 84)) {
        return 1;
      }
      if ((sig[0] == 50) && (sig[1] == 105) && (sig[2] == 114) && (sig[3] == 84)) {
        return 2;
      }
    }
    return 0;
  }
  

















  public abstract int get(int paramInt);
  
















  public abstract int getFromU16SingleLead(char paramChar);
  
















  public final boolean equals(Object other)
  {
    if (!(other instanceof Trie2)) {
      return false;
    }
    Trie2 OtherTrie = (Trie2)other;
    

    Iterator<Range> otherIter = OtherTrie.iterator();
    for (Range rangeFromThis : this) {
      if (!otherIter.hasNext()) {
        return false;
      }
      Range rangeFromOther = (Range)otherIter.next();
      if (!rangeFromThis.equals(rangeFromOther)) {
        return false;
      }
    }
    if (otherIter.hasNext()) {
      return false;
    }
    
    if ((errorValue != errorValue) || (initialValue != initialValue))
    {
      return false;
    }
    
    return true;
  }
  
  public int hashCode()
  {
    if (fHash == 0) {
      int hash = initHash();
      for (Range r : this) {
        hash = hashInt(hash, r.hashCode());
      }
      if (hash == 0) {
        hash = 1;
      }
      fHash = hash;
    }
    return fHash;
  }
  

  public static class Range
  {
    public int startCodePoint;
    
    public int endCodePoint;
    
    public int value;
    
    public boolean leadSurrogate;
    
    public Range() {}
    
    public boolean equals(Object other)
    {
      if ((other == null) || (!other.getClass().equals(getClass()))) {
        return false;
      }
      Range tother = (Range)other;
      return (startCodePoint == startCodePoint) && (endCodePoint == endCodePoint) && (value == value) && (leadSurrogate == leadSurrogate);
    }
    



    public int hashCode()
    {
      int h = Trie2.access$000();
      h = Trie2.hashUChar32(h, startCodePoint);
      h = Trie2.hashUChar32(h, endCodePoint);
      h = Trie2.hashInt(h, value);
      h = Trie2.hashByte(h, leadSurrogate ? 1 : 0);
      return h;
    }
  }
  







  public Iterator<Range> iterator()
  {
    return iterator(defaultValueMapper);
  }
  
  private static ValueMapper defaultValueMapper = new ValueMapper() {
    public int map(int in) {
      return in;
    }
  };
  UTrie2Header header;
  char[] index;
  int data16;
  int[] data32;
  int indexLength;
  int dataLength;
  int index2NullOffset;
  int initialValue;
  int errorValue;
  int highStart;
  
  public Iterator<Range> iterator(ValueMapper mapper) {
    return new Trie2Iterator(mapper);
  }
  













  public Iterator<Range> iteratorForLeadSurrogate(char lead, ValueMapper mapper)
  {
    return new Trie2Iterator(lead, mapper);
  }
  












  public Iterator<Range> iteratorForLeadSurrogate(char lead)
  {
    return new Trie2Iterator(lead, defaultValueMapper);
  }
  





























  protected int serializeHeader(DataOutputStream dos)
    throws IOException
  {
    int bytesWritten = 0;
    
    dos.writeInt(header.signature);
    dos.writeShort(header.options);
    dos.writeShort(header.indexLength);
    dos.writeShort(header.shiftedDataLength);
    dos.writeShort(header.index2NullOffset);
    dos.writeShort(header.dataNullOffset);
    dos.writeShort(header.shiftedHighStart);
    bytesWritten += 16;
    


    for (int i = 0; i < header.indexLength; i++) {
      dos.writeChar(index[i]);
    }
    bytesWritten += header.indexLength;
    return bytesWritten;
  }
  























  public CharSequenceIterator charSequenceIterator(CharSequence text, int index)
  {
    return new CharSequenceIterator(text, index);
  }
  
  public static abstract interface ValueMapper
  {
    public abstract int map(int paramInt);
  }
  
  public static class CharSequenceValues {
    public int index;
    public int codePoint;
    public int value;
    
    public CharSequenceValues() {}
  }
  
  public class CharSequenceIterator implements Iterator<Trie2.CharSequenceValues> {
    private CharSequence text;
    private int textLength;
    private int index;
    
    CharSequenceIterator(CharSequence t, int index) {
      text = t;
      textLength = text.length();
      set(index);
    }
    



    private Trie2.CharSequenceValues fResults = new Trie2.CharSequenceValues();
    
    public void set(int i)
    {
      if ((i < 0) || (i > textLength)) {
        throw new IndexOutOfBoundsException();
      }
      index = i;
    }
    
    public final boolean hasNext()
    {
      return index < textLength;
    }
    
    public final boolean hasPrevious()
    {
      return index > 0;
    }
    
    public Trie2.CharSequenceValues next()
    {
      int c = Character.codePointAt(text, index);
      int val = get(c);
      
      fResults.index = index;
      fResults.codePoint = c;
      fResults.value = val;
      index += 1;
      if (c >= 65536) {
        index += 1;
      }
      return fResults;
    }
    
    public Trie2.CharSequenceValues previous()
    {
      int c = Character.codePointBefore(text, index);
      int val = get(c);
      index -= 1;
      if (c >= 65536) {
        index -= 1;
      }
      fResults.index = index;
      fResults.codePoint = c;
      fResults.value = val;
      return fResults;
    }
    




    public void remove()
    {
      throw new UnsupportedOperationException("Trie2.CharSequenceIterator does not support remove().");
    }
  }
  










  static enum ValueWidth
  {
    BITS_16, 
    BITS_32;
    





    private ValueWidth() {}
  }
  





  int highValueIndex;
  




  int dataNullOffset;
  




  int fHash;
  




  static final int UTRIE2_OPTIONS_VALUE_BITS_MASK = 15;
  




  static final int UTRIE2_SHIFT_1 = 11;
  




  static final int UTRIE2_SHIFT_2 = 5;
  




  static final int UTRIE2_SHIFT_1_2 = 6;
  




  static final int UTRIE2_OMITTED_BMP_INDEX_1_LENGTH = 32;
  




  static final int UTRIE2_CP_PER_INDEX_1_ENTRY = 2048;
  




  static final int UTRIE2_INDEX_2_BLOCK_LENGTH = 64;
  




  static final int UTRIE2_INDEX_2_MASK = 63;
  




  static final int UTRIE2_DATA_BLOCK_LENGTH = 32;
  




  static final int UTRIE2_DATA_MASK = 31;
  




  static final int UTRIE2_INDEX_SHIFT = 2;
  




  static final int UTRIE2_DATA_GRANULARITY = 4;
  




  static final int UTRIE2_INDEX_2_OFFSET = 0;
  



  static final int UTRIE2_LSCP_INDEX_2_OFFSET = 2048;
  



  static final int UTRIE2_LSCP_INDEX_2_LENGTH = 32;
  



  static final int UTRIE2_INDEX_2_BMP_LENGTH = 2080;
  



  static final int UTRIE2_UTF8_2B_INDEX_2_OFFSET = 2080;
  



  static final int UTRIE2_UTF8_2B_INDEX_2_LENGTH = 32;
  



  static final int UTRIE2_INDEX_1_OFFSET = 2112;
  



  static final int UTRIE2_MAX_INDEX_1_LENGTH = 512;
  



  static final int UTRIE2_BAD_UTF8_DATA_OFFSET = 128;
  



  static final int UTRIE2_DATA_START_OFFSET = 192;
  



  static final int UNEWTRIE2_INDEX_GAP_OFFSET = 2080;
  



  static final int UNEWTRIE2_INDEX_GAP_LENGTH = 576;
  



  static final int UNEWTRIE2_MAX_INDEX_2_LENGTH = 35488;
  



  static final int UNEWTRIE2_INDEX_1_LENGTH = 544;
  



  static final int UNEWTRIE2_MAX_DATA_LENGTH = 1115264;
  



  static class UTrie2Header
  {
    int signature;
    



    int options;
    



    int indexLength;
    



    int shiftedDataLength;
    



    int index2NullOffset;
    



    int dataNullOffset;
    



    int shiftedHighStart;
    




    UTrie2Header() {}
  }
  




  class Trie2Iterator
    implements Iterator<Trie2.Range>
  {
    private Trie2.ValueMapper mapper;
    




    Trie2Iterator(Trie2.ValueMapper vm)
    {
      mapper = vm;
      nextStart = 0;
      limitCP = 1114112;
      doLeadSurrogates = true;
    }
    

    Trie2Iterator(char leadSurrogate, Trie2.ValueMapper vm)
    {
      if ((leadSurrogate < 55296) || (leadSurrogate > 56319)) {
        throw new IllegalArgumentException("Bad lead surrogate value.");
      }
      mapper = vm;
      nextStart = (leadSurrogate - 55232 << 10);
      limitCP = (nextStart + 1024);
      doLeadSurrogates = false;
    }
    




    public Trie2.Range next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      if (nextStart >= limitCP)
      {

        doingCodePoints = false;
        nextStart = 55296;
      }
      int endOfRange = 0;
      int val = 0;
      int mappedVal = 0;
      
      if (doingCodePoints)
      {
        val = get(nextStart);
        mappedVal = mapper.map(val);
        endOfRange = rangeEnd(nextStart, limitCP, val);
        


        while (endOfRange < limitCP - 1)
        {

          val = get(endOfRange + 1);
          if (mapper.map(val) != mappedVal) {
            break;
          }
          endOfRange = rangeEnd(endOfRange + 1, limitCP, val);
        }
      }
      
      val = getFromU16SingleLead((char)nextStart);
      mappedVal = mapper.map(val);
      endOfRange = rangeEndLS((char)nextStart);
      


      while (endOfRange < 56319)
      {

        val = getFromU16SingleLead((char)(endOfRange + 1));
        if (mapper.map(val) != mappedVal) {
          break;
        }
        endOfRange = rangeEndLS((char)(endOfRange + 1));
      }
      
      returnValue.startCodePoint = nextStart;
      returnValue.endCodePoint = endOfRange;
      returnValue.value = mappedVal;
      returnValue.leadSurrogate = (!doingCodePoints);
      nextStart = (endOfRange + 1);
      return returnValue;
    }
    


    public boolean hasNext()
    {
      return ((doingCodePoints) && ((doLeadSurrogates) || (nextStart < limitCP))) || (nextStart < 56320);
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
    















    private int rangeEndLS(char startingLS)
    {
      if (startingLS >= 56319) {
        return 56319;
      }
      

      int val = getFromU16SingleLead(startingLS);
      for (int c = startingLS + '\001'; c <= 56319; c++) {
        if (getFromU16SingleLead((char)c) != val) {
          break;
        }
      }
      return c - 1;
    }
    




    private Trie2.Range returnValue = new Trie2.Range();
    

    private int nextStart;
    

    private int limitCP;
    

    private boolean doingCodePoints = true;
    


    private boolean doLeadSurrogates = true;
  }
  







  int rangeEnd(int start, int limitp, int val)
  {
    int limit = Math.min(highStart, limitp);
    
    for (int c = start + 1; c < limit; c++) {
      if (get(c) != val) {
        break;
      }
    }
    if (c >= highStart) {
      c = limitp;
    }
    return c - 1;
  }
  



  private static int initHash()
  {
    return -2128831035;
  }
  
  private static int hashByte(int h, int b) {
    h *= 16777619;
    h ^= b;
    return h;
  }
  
  private static int hashUChar32(int h, int c) {
    h = hashByte(h, c & 0xFF);
    h = hashByte(h, c >> 8 & 0xFF);
    h = hashByte(h, c >> 16);
    return h;
  }
  
  private static int hashInt(int h, int i) {
    h = hashByte(h, i & 0xFF);
    h = hashByte(h, i >> 8 & 0xFF);
    h = hashByte(h, i >> 16 & 0xFF);
    h = hashByte(h, i >> 24 & 0xFF);
    return h;
  }
}
