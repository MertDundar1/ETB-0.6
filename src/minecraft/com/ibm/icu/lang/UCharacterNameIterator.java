package com.ibm.icu.lang;

import com.ibm.icu.impl.UCharacterName;
import com.ibm.icu.util.ValueIterator;
import com.ibm.icu.util.ValueIterator.Element;




















class UCharacterNameIterator
  implements ValueIterator
{
  private UCharacterName m_name_;
  private int m_choice_;
  private int m_start_;
  private int m_limit_;
  private int m_current_;
  
  public boolean next(ValueIterator.Element element)
  {
    if (m_current_ >= m_limit_) {
      return false;
    }
    
    if ((m_choice_ == 0) || (m_choice_ == 2))
    {

      int length = m_name_.getAlgorithmLength();
      if (m_algorithmIndex_ < length) {
        while (m_algorithmIndex_ < length)
        {
          if ((m_algorithmIndex_ >= 0) && (m_name_.getAlgorithmEnd(m_algorithmIndex_) >= m_current_)) {
            break;
          }
          m_algorithmIndex_ += 1;
        }
        




        if (m_algorithmIndex_ < length)
        {


          int start = m_name_.getAlgorithmStart(m_algorithmIndex_);
          if (m_current_ < start)
          {

            int end = start;
            if (m_limit_ <= start) {
              end = m_limit_;
            }
            if (!iterateGroup(element, end)) {
              m_current_ += 1;
              return true;
            }
          }
          





          if (m_current_ >= m_limit_)
          {

            return false;
          }
          
          integer = m_current_;
          value = m_name_.getAlgorithmName(m_algorithmIndex_, m_current_);
          

          m_groupIndex_ = -1;
          m_current_ += 1;
          return true;
        }
      }
    }
    
    if (!iterateGroup(element, m_limit_)) {
      m_current_ += 1;
      return true;
    }
    if ((m_choice_ == 2) && 
      (!iterateExtended(element, m_limit_))) {
      m_current_ += 1;
      return true;
    }
    

    return false;
  }
  





  public void reset()
  {
    m_current_ = m_start_;
    m_groupIndex_ = -1;
    m_algorithmIndex_ = -1;
  }
  















  public void setRange(int start, int limit)
  {
    if (start >= limit) {
      throw new IllegalArgumentException("start or limit has to be valid Unicode codepoints and start < limit");
    }
    
    if (start < 0) {
      m_start_ = 0;
    }
    else {
      m_start_ = start;
    }
    
    if (limit > 1114112) {
      m_limit_ = 1114112;
    }
    else {
      m_limit_ = limit;
    }
    m_current_ = m_start_;
  }
  








  protected UCharacterNameIterator(UCharacterName name, int choice)
  {
    if (name == null) {
      throw new IllegalArgumentException("UCharacterName name argument cannot be null. Missing unames.icu?");
    }
    m_name_ = name;
    
    m_choice_ = choice;
    m_start_ = 0;
    m_limit_ = 1114112;
    m_current_ = m_start_;
  }
  

























  private int m_groupIndex_ = -1;
  


  private int m_algorithmIndex_ = -1;
  


  private static char[] GROUP_OFFSETS_ = new char[33];
  
  private static char[] GROUP_LENGTHS_ = new char[33];
  












  private boolean iterateSingleGroup(ValueIterator.Element result, int limit)
  {
    synchronized (GROUP_OFFSETS_) {
      synchronized (GROUP_LENGTHS_) {
        int index = m_name_.getGroupLengths(m_groupIndex_, GROUP_OFFSETS_, GROUP_LENGTHS_);
        
        while (m_current_ < limit) {
          int offset = UCharacterName.getGroupOffset(m_current_);
          String name = m_name_.getGroupName(index + GROUP_OFFSETS_[offset], GROUP_LENGTHS_[offset], m_choice_);
          

          if (((name == null) || (name.length() == 0)) && (m_choice_ == 2))
          {
            name = m_name_.getExtendedName(m_current_);
          }
          if ((name != null) && (name.length() > 0)) {
            integer = m_current_;
            value = name;
            return false;
          }
          m_current_ += 1;
        }
      }
    }
    return true;
  }
  









  private boolean iterateGroup(ValueIterator.Element result, int limit)
  {
    if (m_groupIndex_ < 0) {
      m_groupIndex_ = m_name_.getGroup(m_current_);
    }
    

    while ((m_groupIndex_ < m_name_.m_groupcount_) && (m_current_ < limit))
    {
      int startMSB = UCharacterName.getCodepointMSB(m_current_);
      int gMSB = m_name_.getGroupMSB(m_groupIndex_);
      if (startMSB == gMSB) {
        if (startMSB == UCharacterName.getCodepointMSB(limit - 1))
        {

          return iterateSingleGroup(result, limit);
        }
        

        if (!iterateSingleGroup(result, UCharacterName.getGroupLimit(gMSB)))
        {
          return false;
        }
        m_groupIndex_ += 1;
      }
      else if (startMSB > gMSB)
      {

        m_groupIndex_ += 1;
      }
      else {
        int gMIN = UCharacterName.getGroupMin(gMSB);
        if (gMIN > limit) {
          gMIN = limit;
        }
        if ((m_choice_ == 2) && 
          (!iterateExtended(result, gMIN))) {
          return false;
        }
        
        m_current_ = gMIN;
      }
    }
    
    return true;
  }
  









  private boolean iterateExtended(ValueIterator.Element result, int limit)
  {
    while (m_current_ < limit) {
      String name = m_name_.getExtendedOr10Name(m_current_);
      if ((name != null) && (name.length() > 0)) {
        integer = m_current_;
        value = name;
        return false;
      }
      m_current_ += 1;
    }
    return true;
  }
}
