package com.ibm.icu.impl;

import com.ibm.icu.text.UnicodeSet;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;















public final class UBiDiProps
{
  private int[] indexes;
  private int[] mirrors;
  private byte[] jgArray;
  private Trie2_16 trie;
  private static final String DATA_NAME = "ubidi";
  private static final String DATA_TYPE = "icu";
  private static final String DATA_FILE_NAME = "ubidi.icu";
  
  private UBiDiProps()
    throws IOException
  {
    InputStream is = ICUData.getStream("data/icudt51b/ubidi.icu");
    BufferedInputStream b = new BufferedInputStream(is, 4096);
    readData(b);
    b.close();
    is.close();
  }
  
  private void readData(InputStream is) throws IOException {
    DataInputStream inputStream = new DataInputStream(is);
    

    ICUBinary.readHeader(inputStream, FMT, new IsAcceptable(null));
    


    int count = inputStream.readInt();
    if (count < 16) {
      throw new IOException("indexes[0] too small in ubidi.icu");
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
      throw new IOException("ubidi.icu: not enough bytes for the trie");
    }
    
    inputStream.skipBytes(expectedTrieLength - trieLength);
    

    count = indexes[3];
    if (count > 0) {
      mirrors = new int[count];
      for (i = 0; i < count; i++) {
        mirrors[i] = inputStream.readInt();
      }
    }
    

    count = indexes[5] - indexes[4];
    jgArray = new byte[count];
    for (i = 0; i < count; i++)
      jgArray[i] = inputStream.readByte();
  }
  
  private static final class IsAcceptable implements ICUBinary.Authenticate {
    private IsAcceptable() {}
    
    public boolean isDataVersionAcceptable(byte[] version) {
      return version[0] == 2;
    }
  }
  







  public final void addPropertyStarts(UnicodeSet set)
  {
    Iterator<Trie2.Range> trieIterator = trie.iterator();
    Trie2.Range range;
    while ((trieIterator.hasNext()) && (!nextleadSurrogate)) {
      set.add(startCodePoint);
    }
    

    int length = indexes[3];
    for (int i = 0; i < length; i++) {
      int c = getMirrorCodePoint(mirrors[i]);
      set.add(c, c + 1);
    }
    

    int start = indexes[4];
    int limit = indexes[5];
    length = limit - start;
    byte prev = 0;
    for (i = 0; i < length; i++) {
      byte jg = jgArray[i];
      if (jg != prev) {
        set.add(start);
        prev = jg;
      }
      start++;
    }
    if (prev != 0)
    {
      set.add(limit);
    }
  }
  







  public final int getMaxValue(int which)
  {
    int max = indexes[15];
    switch (which) {
    case 4096: 
      return max & 0x1F;
    case 4102: 
      return (max & 0xFF0000) >> 16;
    case 4103: 
      return (max & 0xE0) >> 5;
    }
    return -1;
  }
  
  public final int getClass(int c)
  {
    return getClassFromProps(trie.get(c));
  }
  
  public final boolean isMirrored(int c) {
    return getFlagFromProps(trie.get(c), 12);
  }
  


  public final int getMirror(int c)
  {
    int props = trie.get(c);
    int delta = (short)props >> 13;
    if (delta != -4) {
      return c + delta;
    }
    




    int length = indexes[3];
    

    for (int i = 0; i < length; i++) {
      int m = mirrors[i];
      int c2 = getMirrorCodePoint(m);
      if (c == c2)
      {
        return getMirrorCodePoint(mirrors[getMirrorIndex(m)]); }
      if (c < c2) {
        break;
      }
    }
    

    return c;
  }
  
  public final boolean isBidiControl(int c)
  {
    return getFlagFromProps(trie.get(c), 11);
  }
  
  public final boolean isJoinControl(int c) {
    return getFlagFromProps(trie.get(c), 10);
  }
  
  public final int getJoiningType(int c) {
    return (trie.get(c) & 0xE0) >> 5;
  }
  

  public final int getJoiningGroup(int c)
  {
    int start = indexes[4];
    int limit = indexes[5];
    if ((start <= c) && (c < limit)) {
      return jgArray[(c - start)] & 0xFF;
    }
    return 0;
  }
  














  private static final byte[] FMT = { 66, 105, 68, 105 };
  
  private static final int IX_TRIE_SIZE = 2;
  
  private static final int IX_MIRROR_LENGTH = 3;
  
  private static final int IX_JG_START = 4;
  
  private static final int IX_JG_LIMIT = 5;
  
  private static final int IX_MAX_VALUES = 15;
  
  private static final int IX_TOP = 16;
  
  private static final int JT_SHIFT = 5;
  
  private static final int JOIN_CONTROL_SHIFT = 10;
  
  private static final int BIDI_CONTROL_SHIFT = 11;
  
  private static final int IS_MIRRORED_SHIFT = 12;
  
  private static final int MIRROR_DELTA_SHIFT = 13;
  
  private static final int MAX_JG_SHIFT = 16;
  
  private static final int CLASS_MASK = 31;
  private static final int JT_MASK = 224;
  private static final int MAX_JG_MASK = 16711680;
  private static final int ESC_MIRROR_DELTA = -4;
  private static final int MIRROR_INDEX_SHIFT = 21;
  public static final UBiDiProps INSTANCE;
  
  private static final int getClassFromProps(int props)
  {
    return props & 0x1F;
  }
  
  private static final boolean getFlagFromProps(int props, int shift) { return (props >> shift & 0x1) != 0; }
  










  private static final int getMirrorCodePoint(int m)
  {
    return m & 0x1FFFFF;
  }
  
  private static final int getMirrorIndex(int m) { return m >>> 21; }
  







  static
  {
    try
    {
      INSTANCE = new UBiDiProps();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
