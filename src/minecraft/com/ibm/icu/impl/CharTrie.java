package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;



























public class CharTrie
  extends Trie
{
  private char m_initialValue_;
  private char[] m_data_;
  
  public CharTrie(InputStream inputStream, Trie.DataManipulate dataManipulate)
    throws IOException
  {
    super(inputStream, dataManipulate);
    
    if (!isCharTrie()) {
      throw new IllegalArgumentException("Data given does not belong to a char trie.");
    }
  }
  















  public CharTrie(int initialValue, int leadUnitValue, Trie.DataManipulate dataManipulate)
  {
    super(new char['ࠠ'], 512, dataManipulate);
    


    int latin1Length;
    


    int dataLength = latin1Length = 'Ā';
    if (leadUnitValue != initialValue) {
      dataLength += 32;
    }
    m_data_ = new char[dataLength];
    m_dataLength_ = dataLength;
    
    m_initialValue_ = ((char)initialValue);
    





    for (int i = 0; i < latin1Length; i++) {
      m_data_[i] = ((char)initialValue);
    }
    
    if (leadUnitValue != initialValue)
    {
      char block = (char)(latin1Length >> 2);
      i = 1728;
      int limit = 1760;
      for (; i < limit; i++) {
        m_index_[i] = block;
      }
      

      limit = latin1Length + 32;
      for (i = latin1Length; i < limit; i++) {
        m_data_[i] = ((char)leadUnitValue);
      }
    }
  }
  












  public final char getCodePointValue(int ch)
  {
    if ((0 <= ch) && (ch < 55296))
    {
      int offset = (m_index_[(ch >> 5)] << '\002') + (ch & 0x1F);
      
      return m_data_[offset];
    }
    

    int offset = getCodePointOffset(ch);
    


    return offset >= 0 ? m_data_[offset] : m_initialValue_;
  }
  









  public final char getLeadValue(char ch)
  {
    return m_data_[getLeadOffset(ch)];
  }
  







  public final char getBMPValue(char ch)
  {
    return m_data_[getBMPOffset(ch)];
  }
  





  public final char getSurrogateValue(char lead, char trail)
  {
    int offset = getSurrogateOffset(lead, trail);
    if (offset > 0) {
      return m_data_[offset];
    }
    return m_initialValue_;
  }
  









  public final char getTrailValue(int leadvalue, char trail)
  {
    if (m_dataManipulate_ == null) {
      throw new NullPointerException("The field DataManipulate in this Trie is null");
    }
    
    int offset = m_dataManipulate_.getFoldingOffset(leadvalue);
    if (offset > 0) {
      return m_data_[getRawOffset(offset, (char)(trail & 0x3FF))];
    }
    
    return m_initialValue_;
  }
  







  public final char getLatin1LinearValue(char ch)
  {
    return m_data_[(32 + m_dataOffset_ + ch)];
  }
  







  public boolean equals(Object other)
  {
    boolean result = super.equals(other);
    if ((result) && ((other instanceof CharTrie))) {
      CharTrie othertrie = (CharTrie)other;
      return m_initialValue_ == m_initialValue_;
    }
    return false;
  }
  
  public int hashCode() {
    if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
    return 42;
  }
  









  protected final void unserialize(InputStream inputStream)
    throws IOException
  {
    DataInputStream input = new DataInputStream(inputStream);
    int indexDataLength = m_dataOffset_ + m_dataLength_;
    m_index_ = new char[indexDataLength];
    for (int i = 0; i < indexDataLength; i++) {
      m_index_[i] = input.readChar();
    }
    m_data_ = m_index_;
    m_initialValue_ = m_data_[m_dataOffset_];
  }
  






  protected final int getSurrogateOffset(char lead, char trail)
  {
    if (m_dataManipulate_ == null) {
      throw new NullPointerException("The field DataManipulate in this Trie is null");
    }
    


    int offset = m_dataManipulate_.getFoldingOffset(getLeadValue(lead));
    

    if (offset > 0) {
      return getRawOffset(offset, (char)(trail & 0x3FF));
    }
    


    return -1;
  }
  







  protected final int getValue(int index)
  {
    return m_data_[index];
  }
  




  protected final int getInitialValue()
  {
    return m_initialValue_;
  }
}
