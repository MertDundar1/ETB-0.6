package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;



























public class IntTrieBuilder
  extends TrieBuilder
{
  protected int[] m_data_;
  protected int m_initialValue_;
  private int m_leadUnitValue_;
  
  public IntTrieBuilder(IntTrieBuilder table)
  {
    super(table);
    m_data_ = new int[m_dataCapacity_];
    System.arraycopy(m_data_, 0, m_data_, 0, m_dataLength_);
    m_initialValue_ = m_initialValue_;
    m_leadUnitValue_ = m_leadUnitValue_;
  }
  










  public IntTrieBuilder(int[] aliasdata, int maxdatalength, int initialvalue, int leadunitvalue, boolean latin1linear)
  {
    if ((maxdatalength < 32) || ((latin1linear) && (maxdatalength < 1024)))
    {
      throw new IllegalArgumentException("Argument maxdatalength is too small");
    }
    

    if (aliasdata != null) {
      m_data_ = aliasdata;
    }
    else {
      m_data_ = new int[maxdatalength];
    }
    

    int j = 32;
    
    if (latin1linear)
    {



      int i = 0;
      
      do
      {
        m_index_[(i++)] = j;
        j += 32;
      } while (i < 8);
    }
    
    m_dataLength_ = j;
    
    Arrays.fill(m_data_, 0, m_dataLength_, initialvalue);
    m_initialValue_ = initialvalue;
    m_leadUnitValue_ = leadunitvalue;
    m_dataCapacity_ = maxdatalength;
    m_isLatin1Linear_ = latin1linear;
    m_isCompacted_ = false;
  }
  


































































  public int getValue(int ch)
  {
    if ((m_isCompacted_) || (ch > 1114111) || (ch < 0)) {
      return 0;
    }
    
    int block = m_index_[(ch >> 5)];
    return m_data_[(Math.abs(block) + (ch & 0x1F))];
  }
  








  public int getValue(int ch, boolean[] inBlockZero)
  {
    if ((m_isCompacted_) || (ch > 1114111) || (ch < 0)) {
      if (inBlockZero != null) {
        inBlockZero[0] = true;
      }
      return 0;
    }
    
    int block = m_index_[(ch >> 5)];
    if (inBlockZero != null) {
      inBlockZero[0] = (block == 0 ? 1 : false);
    }
    return m_data_[(Math.abs(block) + (ch & 0x1F))];
  }
  









  public boolean setValue(int ch, int value)
  {
    if ((m_isCompacted_) || (ch > 1114111) || (ch < 0)) {
      return false;
    }
    
    int block = getDataBlock(ch);
    if (block < 0) {
      return false;
    }
    
    m_data_[(block + (ch & 0x1F))] = value;
    return true;
  }
  







  public IntTrie serialize(TrieBuilder.DataManipulate datamanipulate, Trie.DataManipulate triedatamanipulate)
  {
    if (datamanipulate == null) {
      throw new IllegalArgumentException("Parameters can not be null");
    }
    

    if (!m_isCompacted_)
    {
      compact(false);
      
      fold(datamanipulate);
      
      compact(true);
      m_isCompacted_ = true;
    }
    
    if (m_dataLength_ >= 262144) {
      throw new ArrayIndexOutOfBoundsException("Data length too small");
    }
    
    char[] index = new char[m_indexLength_];
    int[] data = new int[m_dataLength_];
    

    for (int i = 0; i < m_indexLength_; i++) {
      index[i] = ((char)(m_index_[i] >>> 2));
    }
    
    System.arraycopy(m_data_, 0, data, 0, m_dataLength_);
    
    int options = 37;
    options |= 0x100;
    if (m_isLatin1Linear_) {
      options |= 0x200;
    }
    return new IntTrie(index, data, m_initialValue_, options, triedatamanipulate);
  }
  



















  public int serialize(OutputStream os, boolean reduceTo16Bits, TrieBuilder.DataManipulate datamanipulate)
    throws IOException
  {
    if (datamanipulate == null) {
      throw new IllegalArgumentException("Parameters can not be null");
    }
    


    if (!m_isCompacted_)
    {
      compact(false);
      
      fold(datamanipulate);
      
      compact(true);
      m_isCompacted_ = true;
    }
    
    int length;
    
    if (reduceTo16Bits) {
      length = m_dataLength_ + m_indexLength_;
    } else {
      length = m_dataLength_;
    }
    if (length >= 262144) {
      throw new ArrayIndexOutOfBoundsException("Data length too small");
    }
    





    int length = 16 + 2 * m_indexLength_;
    if (reduceTo16Bits) {
      length += 2 * m_dataLength_;
    } else {
      length += 4 * m_dataLength_;
    }
    
    if (os == null)
    {
      return length;
    }
    
    DataOutputStream dos = new DataOutputStream(os);
    dos.writeInt(1416784229);
    
    int options = 37;
    if (!reduceTo16Bits) {
      options |= 0x100;
    }
    if (m_isLatin1Linear_) {
      options |= 0x200;
    }
    dos.writeInt(options);
    
    dos.writeInt(m_indexLength_);
    dos.writeInt(m_dataLength_);
    

    if (reduceTo16Bits)
    {
      for (int i = 0; i < m_indexLength_; i++) {
        int v = m_index_[i] + m_indexLength_ >>> 2;
        dos.writeChar(v);
      }
      

      for (int i = 0; i < m_dataLength_; i++) {
        int v = m_data_[i] & 0xFFFF;
        dos.writeChar(v);
      }
    }
    else {
      for (int i = 0; i < m_indexLength_; i++) {
        int v = m_index_[i] >>> 2;
        dos.writeChar(v);
      }
      

      for (int i = 0; i < m_dataLength_; i++) {
        dos.writeInt(m_data_[i]);
      }
    }
    
    return length;
  }
  




















  public boolean setRange(int start, int limit, int value, boolean overwrite)
  {
    if ((m_isCompacted_) || (start < 0) || (start > 1114111) || (limit < 0) || (limit > 1114112) || (start > limit))
    {

      return false;
    }
    
    if (start == limit) {
      return true;
    }
    
    if ((start & 0x1F) != 0)
    {
      int block = getDataBlock(start);
      if (block < 0) {
        return false;
      }
      
      int nextStart = start + 32 & 0xFFFFFFE0;
      if (nextStart <= limit) {
        fillBlock(block, start & 0x1F, 32, value, overwrite);
        
        start = nextStart;
      }
      else {
        fillBlock(block, start & 0x1F, limit & 0x1F, value, overwrite);
        
        return true;
      }
    }
    

    int rest = limit & 0x1F;
    

    limit &= 0xFFFFFFE0;
    

    int repeatBlock = 0;
    if (value != m_initialValue_)
    {


      repeatBlock = -1;
    }
    while (start < limit)
    {
      int block = m_index_[(start >> 5)];
      if (block > 0)
      {
        fillBlock(block, 0, 32, value, overwrite);
      }
      else if ((m_data_[(-block)] != value) && ((block == 0) || (overwrite)))
      {

        if (repeatBlock >= 0) {
          m_index_[(start >> 5)] = (-repeatBlock);
        }
        else
        {
          repeatBlock = getDataBlock(start);
          if (repeatBlock < 0) {
            return false;
          }
          


          m_index_[(start >> 5)] = (-repeatBlock);
          fillBlock(repeatBlock, 0, 32, value, true);
        }
      }
      
      start += 32;
    }
    
    if (rest > 0)
    {
      int block = getDataBlock(start);
      if (block < 0) {
        return false;
      }
      fillBlock(block, 0, rest, value, overwrite);
    }
    
    return true;
  }
  











  private int allocDataBlock()
  {
    int newBlock = m_dataLength_;
    int newTop = newBlock + 32;
    if (newTop > m_dataCapacity_)
    {
      return -1;
    }
    m_dataLength_ = newTop;
    return newBlock;
  }
  





  private int getDataBlock(int ch)
  {
    ch >>= 5;
    int indexValue = m_index_[ch];
    if (indexValue > 0) {
      return indexValue;
    }
    

    int newBlock = allocDataBlock();
    if (newBlock < 0)
    {
      return -1;
    }
    m_index_[ch] = newBlock;
    

    System.arraycopy(m_data_, Math.abs(indexValue), m_data_, newBlock, 128);
    
    return newBlock;
  }
  












  private void compact(boolean overlap)
  {
    if (m_isCompacted_) {
      return;
    }
    


    findUnusedBlocks();
    


    int overlapStart = 32;
    if (m_isLatin1Linear_) {
      overlapStart += 256;
    }
    
    int newStart = 32;
    
    for (int start = newStart; start < m_dataLength_;)
    {



      if (m_map_[(start >>> 5)] < 0)
      {
        start += 32;

      }
      else
      {
        if (start >= overlapStart) {
          int i = findSameDataBlock(m_data_, newStart, start, overlap ? 4 : 32);
          
          if (i >= 0)
          {

            m_map_[(start >>> 5)] = i;
            
            start += 32;
            
            continue;
          }
        }
        
        int i;
        if ((overlap) && (start >= overlapStart))
        {
          i = 28; }
        int i; while ((i > 0) && (!equal_int(m_data_, newStart - i, start, i))) {
          i -= 4; continue;
          
          i = 0;
        }
        if (i > 0)
        {
          m_map_[(start >>> 5)] = (newStart - i);
          
          start += i;
          for (i = 32 - i; i > 0; i--) {
            m_data_[(newStart++)] = m_data_[(start++)];
          }
        }
        else if (newStart < start)
        {
          m_map_[(start >>> 5)] = newStart;
          for (i = 32; i > 0; i--) {
            m_data_[(newStart++)] = m_data_[(start++)];
          }
        }
        else {
          m_map_[(start >>> 5)] = start;
          newStart += 32;
          start = newStart;
        }
      }
    }
    for (int i = 0; i < m_indexLength_; i++) {
      m_index_[i] = m_map_[(Math.abs(m_index_[i]) >>> 5)];
    }
    m_dataLength_ = newStart;
  }
  





  private static final int findSameDataBlock(int[] data, int dataLength, int otherBlock, int step)
  {
    
    




    for (int block = 0; block <= dataLength; block += step) {
      if (equal_int(data, block, otherBlock, 32)) {
        return block;
      }
    }
    return -1;
  }
  











  private final void fold(TrieBuilder.DataManipulate manipulate)
  {
    int[] leadIndexes = new int[32];
    int[] index = m_index_;
    
    System.arraycopy(index, 1728, leadIndexes, 0, 32);
    








    int block = 0;
    if (m_leadUnitValue_ != m_initialValue_)
    {




      block = allocDataBlock();
      if (block < 0)
      {
        throw new IllegalStateException("Internal error: Out of memory space");
      }
      fillBlock(block, 0, 32, m_leadUnitValue_, true);
      
      block = -block;
    }
    for (int c = 1728; c < 1760; c++) {
      m_index_[c] = block;
    }
    







    int indexLength = 2048;
    
    for (int c = 65536; c < 1114112;) {
      if (index[(c >> 5)] != 0)
      {
        c &= 0xFC00;
        
        block = findSameIndexBlock(index, indexLength, c >> 5);
        




        int value = manipulate.getFoldedValue(c, block + 32);
        
        if (value != getValue(UTF16.getLeadSurrogate(c))) {
          if (!setValue(UTF16.getLeadSurrogate(c), value))
          {
            throw new ArrayIndexOutOfBoundsException("Data table overflow");
          }
          

          if (block == indexLength)
          {

            System.arraycopy(index, c >> 5, index, indexLength, 32);
            
            indexLength += 32;
          }
        }
        c += 1024;
      }
      else {
        c += 32;
      }
    }
    








    if (indexLength >= 34816) {
      throw new ArrayIndexOutOfBoundsException("Index table overflow");
    }
    

    System.arraycopy(index, 2048, index, 2080, indexLength - 2048);
    

    System.arraycopy(leadIndexes, 0, index, 2048, 32);
    
    indexLength += 32;
    m_indexLength_ = indexLength;
  }
  




  private void fillBlock(int block, int start, int limit, int value, boolean overwrite)
  {
    limit += block;
    block += start;
    if (overwrite) {
      while (block < limit) {
        m_data_[(block++)] = value;
      }
    }
    
    while (block < limit) {
      if (m_data_[block] == m_initialValue_) {
        m_data_[block] = value;
      }
      block++;
    }
  }
}
