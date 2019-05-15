package com.ibm.icu.impl;

import java.io.PrintStream;








public class Trie2Writable
  extends Trie2
{
  private static final int UTRIE2_MAX_INDEX_LENGTH = 65535;
  private static final int UTRIE2_MAX_DATA_LENGTH = 262140;
  private static final int UNEWTRIE2_INITIAL_DATA_LENGTH = 16384;
  private static final int UNEWTRIE2_MEDIUM_DATA_LENGTH = 131072;
  private static final int UNEWTRIE2_INDEX_2_NULL_OFFSET = 2656;
  private static final int UNEWTRIE2_INDEX_2_START_OFFSET = 2720;
  private static final int UNEWTRIE2_DATA_NULL_OFFSET = 192;
  private static final int UNEWTRIE2_DATA_START_OFFSET = 256;
  private static final int UNEWTRIE2_DATA_0800_OFFSET = 2176;
  
  public Trie2Writable(int initialValueP, int errorValueP)
  {
    init(initialValueP, errorValueP);
  }
  
  private void init(int initialValueP, int errorValueP)
  {
    initialValue = initialValueP;
    errorValue = errorValueP;
    highStart = 1114112;
    
    data = new int['䀀'];
    dataCapacity = 16384;
    initialValue = initialValueP;
    errorValue = errorValueP;
    highStart = 1114112;
    firstFreeBlock = 0;
    isCompacted = false;
    







    for (int i = 0; i < 128; i++) {
      data[i] = initialValue;
    }
    for (; i < 192; i++) {
      data[i] = errorValue;
    }
    for (i = 192; i < 256; i++) {
      data[i] = initialValue;
    }
    dataNullOffset = 192;
    dataLength = 256;
    

    i = 0; for (int j = 0; j < 128; j += 32) {
      index2[i] = j;
      map[i] = 1;i++;
    }
    for (; 
        

        j < 192; j += 32) {
      map[i] = 0;i++;
    }
    






    map[(i++)] = 34845;
    



    j += 32;
    for (; j < 256; j += 32) {
      map[i] = 0;i++;
    }
    




    for (i = 4; i < 2080; i++) {
      index2[i] = 192;
    }
    




    for (i = 0; i < 576; i++) {
      index2[(2080 + i)] = -1;
    }
    

    for (i = 0; i < 64; i++) {
      index2[(2656 + i)] = 192;
    }
    index2NullOffset = 2656;
    index2Length = 2720;
    

    i = 0; for (j = 0; 
        i < 32; 
        j += 64)
    {
      index1[i] = j;i++;
    }
    for (; 
        
        i < 544; i++) {
      index1[i] = 2656;
    }
    





    for (i = 128; i < 2048; i += 32) {
      set(i, initialValue);
    }
  }
  






  public Trie2Writable(Trie2 source)
  {
    init(initialValue, errorValue);
    
    for (Trie2.Range r : source) {
      setRange(r, true);
    }
  }
  
  private boolean isInNullBlock(int c, boolean forLSCP)
  {
    int i2;
    int i2;
    if ((Character.isHighSurrogate((char)c)) && (forLSCP)) {
      i2 = 320 + (c >> 5);
    }
    else {
      i2 = index1[(c >> 11)] + (c >> 5 & 0x3F);
    }
    
    int block = index2[i2];
    return block == dataNullOffset;
  }
  

  private int allocIndex2Block()
  {
    int newBlock = index2Length;
    int newTop = newBlock + 64;
    if (newTop > index2.length) {
      throw new IllegalStateException("Internal error in Trie2 creation.");
    }
    




    index2Length = newTop;
    System.arraycopy(index2, index2NullOffset, index2, newBlock, 64);
    return newBlock;
  }
  

  private int getIndex2Block(int c, boolean forLSCP)
  {
    if ((c >= 55296) && (c < 56320) && (forLSCP)) {
      return 2048;
    }
    
    int i1 = c >> 11;
    int i2 = index1[i1];
    if (i2 == index2NullOffset) {
      i2 = allocIndex2Block();
      index1[i1] = i2;
    }
    return i2;
  }
  
  private int allocDataBlock(int copyBlock)
  {
    int newBlock;
    if (firstFreeBlock != 0)
    {
      int newBlock = firstFreeBlock;
      firstFreeBlock = (-map[(newBlock >> 5)]);
    }
    else {
      newBlock = dataLength;
      int newTop = newBlock + 32;
      if (newTop > dataCapacity)
      {
        int capacity;
        

        if (dataCapacity < 131072) {
          capacity = 131072; } else { int capacity;
          if (dataCapacity < 1115264) {
            capacity = 1115264;


          }
          else
          {

            throw new IllegalStateException("Internal error in Trie2 creation."); } }
        int capacity;
        int[] newData = new int[capacity];
        System.arraycopy(data, 0, newData, 0, dataLength);
        data = newData;
        dataCapacity = capacity;
      }
      dataLength = newTop;
    }
    System.arraycopy(data, copyBlock, data, newBlock, 32);
    map[(newBlock >> 5)] = 0;
    return newBlock;
  }
  


  private void releaseDataBlock(int block)
  {
    map[(block >> 5)] = (-firstFreeBlock);
    firstFreeBlock = block;
  }
  
  private boolean isWritableBlock(int block)
  {
    return (block != dataNullOffset) && (1 == map[(block >> 5)]);
  }
  
  private void setIndex2Entry(int i2, int block)
  {
    map[(block >> 5)] += 1;
    int oldBlock = index2[i2];
    if (0 == map[(oldBlock >> 5)] -= 1) {
      releaseDataBlock(oldBlock);
    }
    index2[i2] = block;
  }
  







  private int getDataBlock(int c, boolean forLSCP)
  {
    int i2 = getIndex2Block(c, forLSCP);
    
    i2 += (c >> 5 & 0x3F);
    int oldBlock = index2[i2];
    if (isWritableBlock(oldBlock)) {
      return oldBlock;
    }
    

    int newBlock = allocDataBlock(oldBlock);
    setIndex2Entry(i2, newBlock);
    return newBlock;
  }
  




  public Trie2Writable set(int c, int value)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point.");
    }
    set(c, true, value);
    fHash = 0;
    return this;
  }
  
  private Trie2Writable set(int c, boolean forLSCP, int value)
  {
    if (isCompacted) {
      uncompact();
    }
    int block = getDataBlock(c, forLSCP);
    data[(block + (c & 0x1F))] = value;
    return this;
  }
  











  private void uncompact()
  {
    Trie2Writable tempTrie = new Trie2Writable(this);
    

    index1 = index1;
    index2 = index2;
    data = data;
    index2Length = index2Length;
    dataCapacity = dataCapacity;
    isCompacted = isCompacted;
    

    header = header;
    index = index;
    data16 = data16;
    data32 = data32;
    indexLength = indexLength;
    dataLength = dataLength;
    index2NullOffset = index2NullOffset;
    initialValue = initialValue;
    errorValue = errorValue;
    highStart = highStart;
    highValueIndex = highValueIndex;
    dataNullOffset = dataNullOffset;
  }
  
  private void writeBlock(int block, int value)
  {
    int limit = block + 32;
    while (block < limit) {
      data[(block++)] = value;
    }
  }
  





  private void fillBlock(int block, int start, int limit, int value, int initialValue, boolean overwrite)
  {
    int pLimit = block + limit;
    if (overwrite) {
      for (int i = block + start; i < pLimit; i++) {
        data[i] = value;
      }
    }
    for (int i = block + start; i < pLimit; i++) {
      if (data[i] == initialValue) {
        data[i] = value;
      }
    }
  }
  



















  public Trie2Writable setRange(int start, int end, int value, boolean overwrite)
  {
    if ((start > 1114111) || (start < 0) || (end > 1114111) || (end < 0) || (start > end)) {
      throw new IllegalArgumentException("Invalid code point range.");
    }
    if ((!overwrite) && (value == initialValue)) {
      return this;
    }
    fHash = 0;
    if (isCompacted) {
      uncompact();
    }
    
    int limit = end + 1;
    if ((start & 0x1F) != 0)
    {


      int block = getDataBlock(start, true);
      
      int nextStart = start + 32 & 0xFFFFFFE0;
      if (nextStart <= limit) {
        fillBlock(block, start & 0x1F, 32, value, initialValue, overwrite);
        
        start = nextStart;
      } else {
        fillBlock(block, start & 0x1F, limit & 0x1F, value, initialValue, overwrite);
        
        return this;
      }
    }
    

    int rest = limit & 0x1F;
    

    limit &= 0xFFFFFFE0;
    int repeatBlock;
    int repeatBlock;
    if (value == initialValue) {
      repeatBlock = dataNullOffset;
    } else {
      repeatBlock = -1;
    }
    
    while (start < limit)
    {
      boolean setRepeatBlock = false;
      
      if ((value == initialValue) && (isInNullBlock(start, true))) {
        start += 32;

      }
      else
      {
        int i2 = getIndex2Block(start, true);
        i2 += (start >> 5 & 0x3F);
        int block = index2[i2];
        if (isWritableBlock(block))
        {
          if ((overwrite) && (block >= 2176))
          {




            setRepeatBlock = true;
          }
          else {
            fillBlock(block, 0, 32, value, initialValue, overwrite);
          }
          
        }
        else if ((data[block] != value) && ((overwrite) || (block == dataNullOffset)))
        {















          setRepeatBlock = true;
        }
        if (setRepeatBlock) {
          if (repeatBlock >= 0) {
            setIndex2Entry(i2, repeatBlock);
          }
          else {
            repeatBlock = getDataBlock(start, true);
            writeBlock(repeatBlock, value);
          }
        }
        
        start += 32;
      }
    }
    if (rest > 0)
    {
      int block = getDataBlock(start, true);
      fillBlock(block, 0, rest, value, initialValue, overwrite);
    }
    
    return this;
  }
  














  public Trie2Writable setRange(Trie2.Range range, boolean overwrite)
  {
    fHash = 0;
    if (leadSurrogate) {
      for (int c = startCodePoint; c <= endCodePoint; c++) {
        if ((overwrite) || (getFromU16SingleLead((char)c) == initialValue)) {
          setForLeadSurrogateCodeUnit((char)c, value);
        }
      }
    } else {
      setRange(startCodePoint, endCodePoint, value, overwrite);
    }
    return this;
  }
  















  public Trie2Writable setForLeadSurrogateCodeUnit(char codeUnit, int value)
  {
    fHash = 0;
    set(codeUnit, false, value);
    return this;
  }
  







  public int get(int codePoint)
  {
    if ((codePoint < 0) || (codePoint > 1114111)) {
      return errorValue;
    }
    return get(codePoint, true);
  }
  



  private int get(int c, boolean fromLSCP)
  {
    if ((c >= highStart) && ((c < 55296) || (c >= 56320) || (fromLSCP)))
      return data[(dataLength - 4)];
    int i2;
    int i2;
    if ((c >= 55296) && (c < 56320) && (fromLSCP)) {
      i2 = 320 + (c >> 5);
    }
    else {
      i2 = index1[(c >> 11)] + (c >> 5 & 0x3F);
    }
    
    int block = index2[i2];
    return data[(block + (c & 0x1F))];
  }
  













  public int getFromU16SingleLead(char c)
  {
    return get(c, false);
  }
  

  private boolean equal_int(int[] a, int s, int t, int length)
  {
    for (int i = 0; i < length; i++) {
      if (a[(s + i)] != a[(t + i)]) {
        return false;
      }
    }
    return true;
  }
  


  private int findSameIndex2Block(int index2Length, int otherBlock)
  {
    
    

    for (int block = 0; block <= index2Length; block++) {
      if (equal_int(index2, block, otherBlock, 64)) {
        return block;
      }
    }
    return -1;
  }
  



  private int findSameDataBlock(int dataLength, int otherBlock, int blockLength)
  {
    dataLength -= blockLength;
    
    for (int block = 0; block <= dataLength; block += 4) {
      if (equal_int(data, block, otherBlock, blockLength)) {
        return block;
      }
    }
    return -1;
  }
  


  private int findHighStart(int highValue)
  {
    int prevBlock;
    

    int prevI2Block;
    

    int prevBlock;
    
    if (highValue == initialValue) {
      int prevI2Block = index2NullOffset;
      prevBlock = dataNullOffset;
    } else {
      prevI2Block = -1;
      prevBlock = -1;
    }
    int prev = 1114112;
    

    int i1 = 544;
    int c = prev;
    while (c > 0) {
      int i2Block = index1[(--i1)];
      if (i2Block == prevI2Block)
      {
        c -= 2048;
      }
      else {
        prevI2Block = i2Block;
        if (i2Block == index2NullOffset)
        {
          if (highValue != initialValue) {
            return c;
          }
          c -= 2048;
        }
        else {
          int i2 = 64; int block; int j; for (;;) { if (i2 > 0) {
              block = index2[(i2Block + --i2)];
              if (block == prevBlock)
              {
                c -= 32;
                continue;
              }
              prevBlock = block;
              if (block == dataNullOffset)
              {
                if (highValue != initialValue) {
                  return c;
                }
                c -= 32;
              } } else { break; }
            for (j = 32; j > 0;) {
              int value = data[(block + --j)];
              if (value != highValue) {
                return c;
              }
              c--;
            }
          }
        }
      }
    }
    

    return 0;
  }
  
















  private void compactData()
  {
    int newStart = 192;
    int start = 0; for (int i = 0; start < newStart; i++) {
      map[i] = start;start += 32;
    }
    




    int blockLength = 64;
    int blockCount = blockLength >> 5;
    for (start = newStart; start < dataLength;)
    {




      if (start == 2176) {
        blockLength = 32;
        blockCount = 1;
      }
      

      if (map[(start >> 5)] <= 0)
      {
        start += blockLength;


      }
      else
      {

        int movedStart = findSameDataBlock(newStart, start, blockLength);
        if (movedStart >= 0)
        {
          i = blockCount; for (int mapIndex = start >> 5; i > 0; i--) {
            map[(mapIndex++)] = movedStart;
            movedStart += 32;
          }
          

          start += blockLength;


        }
        else
        {


          int overlap = blockLength - 4;
          while ((overlap > 0) && (!equal_int(data, newStart - overlap, start, overlap))) {
            overlap -= 4;
          }
          if ((overlap > 0) || (newStart < start))
          {
            movedStart = newStart - overlap;
            i = blockCount; for (int mapIndex = start >> 5; i > 0; i--) {
              map[(mapIndex++)] = movedStart;
              movedStart += 32;
            }
            

            start += overlap;
            for (i = blockLength - overlap; i > 0; i--) {
              data[(newStart++)] = data[(start++)];
            }
          } else {
            i = blockCount; for (int mapIndex = start >> 5; i > 0; i--) {
              map[(mapIndex++)] = start;
              start += 32;
            }
            newStart = start;
          }
        }
      }
    }
    for (i = 0; i < index2Length; i++) {
      if (i == 2080)
      {
        i += 576;
      }
      index2[i] = map[(index2[i] >> 5)];
    }
    dataNullOffset = map[(dataNullOffset >> 5)];
    

    while ((newStart & 0x3) != 0) {
      data[(newStart++)] = initialValue;
    }
    
    if (UTRIE2_DEBUG)
    {
      System.out.printf("compacting UTrie2: count of 32-bit data words %d->%d\n", new Object[] { Integer.valueOf(dataLength), Integer.valueOf(newStart) });
    }
    

    dataLength = newStart;
  }
  


  private void compactIndex2()
  {
    int newStart = 2080;
    int start = 0; for (int i = 0; start < newStart; i++) {
      map[i] = start;start += 64;
    }
    

    newStart += 32 + (highStart - 65536 >> 11);
    
    for (start = 2656; start < index2Length;)
    {
      int movedStart;
      




      if ((movedStart = findSameIndex2Block(newStart, start)) >= 0)
      {


        map[(start >> 6)] = movedStart;
        

        start += 64;


      }
      else
      {


        int overlap = 63;
        while ((overlap > 0) && (!equal_int(index2, newStart - overlap, start, overlap))) {
          overlap--;
        }
        if ((overlap > 0) || (newStart < start))
        {
          map[(start >> 6)] = (newStart - overlap);
          

          start += overlap;
          for (i = 64 - overlap; i > 0; i--) {
            index2[(newStart++)] = index2[(start++)];
          }
        } else {
          map[(start >> 6)] = start;
          start += 64;
          newStart = start;
        }
      }
    }
    
    for (i = 0; i < 544; i++) {
      index1[i] = map[(index1[i] >> 6)];
    }
    index2NullOffset = map[(index2NullOffset >> 6)];
    






    while ((newStart & 0x3) != 0)
    {
      index2[(newStart++)] = 262140;
    }
    
    if (UTRIE2_DEBUG)
    {
      System.out.printf("compacting UTrie2: count of 16-bit index-2 words %d->%d\n", new Object[] { Integer.valueOf(index2Length), Integer.valueOf(newStart) });
    }
    

    index2Length = newStart;
  }
  




  private void compactTrie()
  {
    int highValue = get(1114111);
    int localHighStart = findHighStart(highValue);
    localHighStart = localHighStart + 2047 & 0xF800;
    if (localHighStart == 1114112) {
      highValue = errorValue;
    }
    




    highStart = localHighStart;
    
    if (UTRIE2_DEBUG) {
      System.out.printf("UTrie2: highStart U+%04x  highValue 0x%x  initialValue 0x%x\n", new Object[] { Integer.valueOf(highStart), Integer.valueOf(highValue), Integer.valueOf(initialValue) });
    }
    

    if (highStart < 1114112)
    {
      int suppHighStart = highStart <= 65536 ? 65536 : highStart;
      setRange(suppHighStart, 1114111, initialValue, true);
    }
    
    compactData();
    if (highStart > 65536) {
      compactIndex2();
    }
    else if (UTRIE2_DEBUG) {
      System.out.printf("UTrie2: highStart U+%04x  count of 16-bit index-2 words %d->%d\n", new Object[] { Integer.valueOf(highStart), Integer.valueOf(index2Length), Integer.valueOf(2112) });
    }
    







    data[(dataLength++)] = highValue;
    while ((dataLength & 0x3) != 0) {
      data[(dataLength++)] = initialValue;
    }
    
    isCompacted = true;
  }
  





  public Trie2_16 toTrie2_16()
  {
    Trie2_16 frozenTrie = new Trie2_16();
    freeze(frozenTrie, Trie2.ValueWidth.BITS_16);
    return frozenTrie;
  }
  




  public Trie2_32 toTrie2_32()
  {
    Trie2_32 frozenTrie = new Trie2_32();
    freeze(frozenTrie, Trie2.ValueWidth.BITS_32);
    return frozenTrie;
  }
  






















  private void freeze(Trie2 dest, Trie2.ValueWidth valueBits)
  {
    if (!isCompacted)
      compactTrie();
    int allIndexesLength;
    int allIndexesLength;
    if (highStart <= 65536) {
      allIndexesLength = 2112;
    } else
      allIndexesLength = index2Length;
    int dataMove;
    int dataMove; if (valueBits == Trie2.ValueWidth.BITS_16) {
      dataMove = allIndexesLength;
    } else {
      dataMove = 0;
    }
    

    if ((allIndexesLength > 65535) || (dataMove + dataNullOffset > 65535) || (dataMove + 2176 > 65535) || (dataMove + dataLength > 262140))
    {






      throw new UnsupportedOperationException("Trie2 data is too large.");
    }
    

    int indexLength = allIndexesLength;
    if (valueBits == Trie2.ValueWidth.BITS_16) {
      indexLength += dataLength;
    } else {
      data32 = new int[dataLength];
    }
    index = new char[indexLength];
    
    indexLength = allIndexesLength;
    dataLength = dataLength;
    if (highStart <= 65536) {
      index2NullOffset = 65535;
    } else {
      index2NullOffset = (0 + index2NullOffset);
    }
    initialValue = initialValue;
    errorValue = errorValue;
    highStart = highStart;
    highValueIndex = (dataMove + dataLength - 4);
    dataNullOffset = (dataMove + dataNullOffset);
    



    header = new Trie2.UTrie2Header();
    header.signature = 1416784178;
    header.options = (valueBits == Trie2.ValueWidth.BITS_16 ? 0 : 1);
    header.indexLength = indexLength;
    header.shiftedDataLength = (dataLength >> 2);
    header.index2NullOffset = index2NullOffset;
    header.dataNullOffset = dataNullOffset;
    header.shiftedHighStart = (highStart >> 11);
    



    int destIdx = 0;
    for (int i = 0; i < 2080; i++) {
      index[(destIdx++)] = ((char)(index2[i] + dataMove >> 2));
    }
    if (UTRIE2_DEBUG) {
      System.out.println("\n\nIndex2 for BMP limit is " + Integer.toHexString(destIdx));
    }
    

    for (i = 0; i < 2; i++) {
      index[(destIdx++)] = ((char)(dataMove + 128));
    }
    for (; i < 32; i++) {
      index[(destIdx++)] = ((char)(dataMove + index2[(i << 1)]));
    }
    if (UTRIE2_DEBUG) {
      System.out.println("Index2 for UTF-8 2byte values limit is " + Integer.toHexString(destIdx));
    }
    
    if (highStart > 65536) {
      int index1Length = highStart - 65536 >> 11;
      int index2Offset = 2112 + index1Length;
      


      for (i = 0; i < index1Length; i++)
      {
        index[(destIdx++)] = ((char)(0 + index1[(i + 32)]));
      }
      if (UTRIE2_DEBUG) {
        System.out.println("Index 1 for supplementals, limit is " + Integer.toHexString(destIdx));
      }
      




      for (i = 0; i < index2Length - index2Offset; i++) {
        index[(destIdx++)] = ((char)(dataMove + index2[(index2Offset + i)] >> 2));
      }
      if (UTRIE2_DEBUG) {
        System.out.println("Index 2 for supplementals, limit is " + Integer.toHexString(destIdx));
      }
    }
    

    switch (1.$SwitchMap$com$ibm$icu$impl$Trie2$ValueWidth[valueBits.ordinal()])
    {
    case 1: 
      assert (destIdx == dataMove);
      data16 = destIdx;
      for (i = 0; i < dataLength;) {
        index[(destIdx++)] = ((char)data[i]);i++; continue;
        



        for (i = 0; i < dataLength; i++) {
          data32[i] = data[i];
        }
      }
    }
    
  }
  


































  private int[] index1 = new int['Ƞ'];
  private int[] index2 = new int[35488];
  


  private int[] data;
  


  private int index2Length;
  


  private int dataCapacity;
  


  private int firstFreeBlock;
  

  private int index2NullOffset;
  

  private boolean isCompacted;
  

  private int[] map = new int[34852];
  

  private boolean UTRIE2_DEBUG = false;
}
