package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.util.RangeValueIterator;
import com.ibm.icu.util.RangeValueIterator.Element;





































































public class TrieIterator
  implements RangeValueIterator
{
  private static final int BMP_INDEX_LENGTH_ = 2048;
  private static final int LEAD_SURROGATE_MIN_VALUE_ = 55296;
  private static final int TRAIL_SURROGATE_MIN_VALUE_ = 56320;
  private static final int TRAIL_SURROGATE_COUNT_ = 1024;
  private static final int TRAIL_SURROGATE_INDEX_BLOCK_LENGTH_ = 32;
  private static final int DATA_BLOCK_LENGTH_ = 32;
  private Trie m_trie_;
  private int m_initialValue_;
  private int m_currentCodepoint_;
  private int m_nextCodepoint_;
  private int m_nextValue_;
  private int m_nextIndex_;
  private int m_nextBlock_;
  private int m_nextBlockIndex_;
  private int m_nextTrailIndexOffset_;
  
  public TrieIterator(Trie trie)
  {
    if (trie == null) {
      throw new IllegalArgumentException("Argument trie cannot be null");
    }
    
    m_trie_ = trie;
    
    m_initialValue_ = extract(m_trie_.getInitialValue());
    reset();
  }
  












  public final boolean next(RangeValueIterator.Element element)
  {
    if (m_nextCodepoint_ > 1114111) {
      return false;
    }
    if ((m_nextCodepoint_ < 65536) && (calculateNextBMPElement(element)))
    {
      return true;
    }
    calculateNextSupplementaryElement(element);
    return true;
  }
  



  public final void reset()
  {
    m_currentCodepoint_ = 0;
    m_nextCodepoint_ = 0;
    m_nextIndex_ = 0;
    m_nextBlock_ = (m_trie_.m_index_[0] << '\002');
    if (m_nextBlock_ == m_trie_.m_dataOffset_) {
      m_nextValue_ = m_initialValue_;
    }
    else {
      m_nextValue_ = extract(m_trie_.getValue(m_nextBlock_));
    }
    m_nextBlockIndex_ = 0;
    m_nextTrailIndexOffset_ = 32;
  }
  











  protected int extract(int value)
  {
    return value;
  }
  










  private final void setResult(RangeValueIterator.Element element, int start, int limit, int value)
  {
    start = start;
    limit = limit;
    value = value;
  }
  











  private final boolean calculateNextBMPElement(RangeValueIterator.Element element)
  {
    int currentValue = m_nextValue_;
    m_currentCodepoint_ = m_nextCodepoint_;
    m_nextCodepoint_ += 1;
    m_nextBlockIndex_ += 1;
    if (!checkBlockDetail(currentValue)) {
      setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
      
      return true;
    }
    

    while (m_nextCodepoint_ < 65536)
    {


      if (m_nextCodepoint_ == 55296)
      {

        m_nextIndex_ = 2048;
      }
      else if (m_nextCodepoint_ == 56320)
      {
        m_nextIndex_ = (m_nextCodepoint_ >> 5);
      } else {
        m_nextIndex_ += 1;
      }
      
      m_nextBlockIndex_ = 0;
      if (!checkBlock(currentValue)) {
        setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
        
        return true;
      }
    }
    m_nextCodepoint_ -= 1;
    m_nextBlockIndex_ -= 1;
    return false;
  }
  

















  private final void calculateNextSupplementaryElement(RangeValueIterator.Element element)
  {
    int currentValue = m_nextValue_;
    m_nextCodepoint_ += 1;
    m_nextBlockIndex_ += 1;
    
    if (UTF16.getTrailSurrogate(m_nextCodepoint_) != 56320)
    {


      if ((!checkNullNextTrailIndex()) && (!checkBlockDetail(currentValue))) {
        setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
        
        m_currentCodepoint_ = m_nextCodepoint_;
        return;
      }
      
      m_nextIndex_ += 1;
      m_nextTrailIndexOffset_ += 1;
      if (!checkTrailBlock(currentValue)) {
        setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
        
        m_currentCodepoint_ = m_nextCodepoint_;
        return;
      }
    }
    int nextLead = UTF16.getLeadSurrogate(m_nextCodepoint_);
    
    while (nextLead < 56320)
    {
      int leadBlock = m_trie_.m_index_[(nextLead >> 5)] << '\002';
      

      if (leadBlock == m_trie_.m_dataOffset_)
      {
        if (currentValue != m_initialValue_) {
          m_nextValue_ = m_initialValue_;
          m_nextBlock_ = leadBlock;
          m_nextBlockIndex_ = 0;
          setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
          
          m_currentCodepoint_ = m_nextCodepoint_;
          return;
        }
        
        nextLead += 32;
        




        m_nextCodepoint_ = UCharacterProperty.getRawSupplementary((char)nextLead, 56320);

      }
      else
      {
        if (m_trie_.m_dataManipulate_ == null) {
          throw new NullPointerException("The field DataManipulate in this Trie is null");
        }
        

        m_nextIndex_ = m_trie_.m_dataManipulate_.getFoldingOffset(m_trie_.getValue(leadBlock + (nextLead & 0x1F)));
        

        if (m_nextIndex_ <= 0)
        {
          if (currentValue != m_initialValue_) {
            m_nextValue_ = m_initialValue_;
            m_nextBlock_ = m_trie_.m_dataOffset_;
            m_nextBlockIndex_ = 0;
            setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
            
            m_currentCodepoint_ = m_nextCodepoint_;
            return;
          }
          m_nextCodepoint_ += 1024;
        } else {
          m_nextTrailIndexOffset_ = 0;
          if (!checkTrailBlock(currentValue)) {
            setResult(element, m_currentCodepoint_, m_nextCodepoint_, currentValue);
            
            m_currentCodepoint_ = m_nextCodepoint_;
            return;
          }
        }
        nextLead++;
      }
    }
    
    setResult(element, m_currentCodepoint_, 1114112, currentValue);
  }
  













  private final boolean checkBlockDetail(int currentValue)
  {
    while (m_nextBlockIndex_ < 32) {
      m_nextValue_ = extract(m_trie_.getValue(m_nextBlock_ + m_nextBlockIndex_));
      
      if (m_nextValue_ != currentValue) {
        return false;
      }
      m_nextBlockIndex_ += 1;
      m_nextCodepoint_ += 1;
    }
    return true;
  }
  












  private final boolean checkBlock(int currentValue)
  {
    int currentBlock = m_nextBlock_;
    m_nextBlock_ = (m_trie_.m_index_[m_nextIndex_] << '\002');
    
    if ((m_nextBlock_ == currentBlock) && (m_nextCodepoint_ - m_currentCodepoint_ >= 32))
    {


      m_nextCodepoint_ += 32;
    }
    else if (m_nextBlock_ == m_trie_.m_dataOffset_)
    {
      if (currentValue != m_initialValue_) {
        m_nextValue_ = m_initialValue_;
        m_nextBlockIndex_ = 0;
        return false;
      }
      m_nextCodepoint_ += 32;

    }
    else if (!checkBlockDetail(currentValue)) {
      return false;
    }
    
    return true;
  }
  












  private final boolean checkTrailBlock(int currentValue)
  {
    while (m_nextTrailIndexOffset_ < 32)
    {

      m_nextBlockIndex_ = 0;
      
      if (!checkBlock(currentValue)) {
        return false;
      }
      m_nextTrailIndexOffset_ += 1;
      m_nextIndex_ += 1;
    }
    return true;
  }
  









  private final boolean checkNullNextTrailIndex()
  {
    if (m_nextIndex_ <= 0) {
      m_nextCodepoint_ += 1023;
      int nextLead = UTF16.getLeadSurrogate(m_nextCodepoint_);
      int leadBlock = m_trie_.m_index_[(nextLead >> 5)] << '\002';
      

      if (m_trie_.m_dataManipulate_ == null) {
        throw new NullPointerException("The field DataManipulate in this Trie is null");
      }
      
      m_nextIndex_ = m_trie_.m_dataManipulate_.getFoldingOffset(m_trie_.getValue(leadBlock + (nextLead & 0x1F)));
      

      m_nextIndex_ -= 1;
      m_nextBlockIndex_ = 32;
      return true;
    }
    return false;
  }
}
