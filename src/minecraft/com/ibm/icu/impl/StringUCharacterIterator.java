package com.ibm.icu.impl;

import com.ibm.icu.text.UCharacterIterator;

















public final class StringUCharacterIterator
  extends UCharacterIterator
{
  private String m_text_;
  private int m_currentIndex_;
  
  public StringUCharacterIterator(String str)
  {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    m_text_ = str;
    m_currentIndex_ = 0;
  }
  



  public StringUCharacterIterator()
  {
    m_text_ = "";
    m_currentIndex_ = 0;
  }
  







  public Object clone()
  {
    try
    {
      return super.clone();
    } catch (CloneNotSupportedException e) {}
    return null;
  }
  





  public int current()
  {
    if (m_currentIndex_ < m_text_.length()) {
      return m_text_.charAt(m_currentIndex_);
    }
    return -1;
  }
  





  public int getLength()
  {
    return m_text_.length();
  }
  




  public int getIndex()
  {
    return m_currentIndex_;
  }
  









  public int next()
  {
    if (m_currentIndex_ < m_text_.length())
    {
      return m_text_.charAt(m_currentIndex_++);
    }
    return -1;
  }
  









  public int previous()
  {
    if (m_currentIndex_ > 0) {
      return m_text_.charAt(--m_currentIndex_);
    }
    return -1;
  }
  







  public void setIndex(int currentIndex)
    throws IndexOutOfBoundsException
  {
    if ((currentIndex < 0) || (currentIndex > m_text_.length())) {
      throw new IndexOutOfBoundsException();
    }
    m_currentIndex_ = currentIndex;
  }
  



































  public int getText(char[] fillIn, int offset)
  {
    int length = m_text_.length();
    if ((offset < 0) || (offset + length > fillIn.length)) {
      throw new IndexOutOfBoundsException(Integer.toString(length));
    }
    m_text_.getChars(0, length, fillIn, offset);
    return length;
  }
  





  public String getText()
  {
    return m_text_;
  }
  







  public void setText(String text)
  {
    if (text == null) {
      throw new NullPointerException();
    }
    m_text_ = text;
    m_currentIndex_ = 0;
  }
}
