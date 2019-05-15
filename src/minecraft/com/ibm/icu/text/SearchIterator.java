package com.ibm.icu.text;

import java.text.CharacterIterator;










































































































































public abstract class SearchIterator
{
  public static final int DONE = -1;
  protected BreakIterator breakIterator;
  protected CharacterIterator targetText;
  protected int matchLength;
  private boolean m_isForwardSearching_;
  private boolean m_isOverlap_;
  private boolean m_reset_;
  private int m_setOffset_;
  private int m_lastMatchStart_;
  
  public void setIndex(int position)
  {
    if ((position < targetText.getBeginIndex()) || (position > targetText.getEndIndex()))
    {
      throw new IndexOutOfBoundsException("setIndex(int) expected position to be between " + targetText.getBeginIndex() + " and " + targetText.getEndIndex());
    }
    

    m_setOffset_ = position;
    m_reset_ = false;
    matchLength = 0;
  }
  












  public void setOverlapping(boolean allowOverlap)
  {
    m_isOverlap_ = allowOverlap;
  }
  












  public void setBreakIterator(BreakIterator breakiter)
  {
    breakIterator = breakiter;
    if (breakIterator != null) {
      breakIterator.setText(targetText);
    }
  }
  










  public void setTarget(CharacterIterator text)
  {
    if ((text == null) || (text.getEndIndex() == text.getIndex())) {
      throw new IllegalArgumentException("Illegal null or empty text");
    }
    
    targetText = text;
    targetText.setIndex(targetText.getBeginIndex());
    matchLength = 0;
    m_reset_ = true;
    m_isForwardSearching_ = true;
    if (breakIterator != null) {
      breakIterator.setText(targetText);
    }
  }
  


























  public int getMatchStart()
  {
    return m_lastMatchStart_;
  }
  


















  public abstract int getIndex();
  

















  public int getMatchLength()
  {
    return matchLength;
  }
  











  public BreakIterator getBreakIterator()
  {
    return breakIterator;
  }
  






  public CharacterIterator getTarget()
  {
    return targetText;
  }
  
















  public String getMatchedText()
  {
    if (matchLength > 0) {
      int limit = m_lastMatchStart_ + matchLength;
      StringBuilder result = new StringBuilder(matchLength);
      result.append(targetText.current());
      targetText.next();
      while (targetText.getIndex() < limit) {
        result.append(targetText.current());
        targetText.next();
      }
      targetText.setIndex(m_lastMatchStart_);
      return result.toString();
    }
    return null;
  }
  
























  public int next()
  {
    int start = targetText.getIndex();
    if (m_setOffset_ != -1) {
      start = m_setOffset_;
      m_setOffset_ = -1;
    }
    if (m_isForwardSearching_) {
      if ((!m_reset_) && (start + matchLength >= targetText.getEndIndex()))
      {

        matchLength = 0;
        targetText.setIndex(targetText.getEndIndex());
        m_lastMatchStart_ = -1;
        return -1;
      }
      m_reset_ = false;


    }
    else
    {


      m_isForwardSearching_ = true;
      if (start != -1)
      {

        return start;
      }
    }
    
    if (start == -1) {
      start = targetText.getBeginIndex();
    }
    if (matchLength > 0)
    {
      if (m_isOverlap_) {
        start++;
      }
      else {
        start += matchLength;
      }
    }
    m_lastMatchStart_ = handleNext(start);
    return m_lastMatchStart_;
  }
  






















  public int previous()
  {
    int start = targetText.getIndex();
    if (m_setOffset_ != -1) {
      start = m_setOffset_;
      m_setOffset_ = -1;
    }
    if (m_reset_) {
      m_isForwardSearching_ = false;
      m_reset_ = false;
      start = targetText.getEndIndex();
    }
    
    if (m_isForwardSearching_ == true)
    {




      m_isForwardSearching_ = false;
      if (start != targetText.getEndIndex()) {
        return start;
      }
      
    }
    else if (start == targetText.getBeginIndex())
    {
      matchLength = 0;
      targetText.setIndex(targetText.getBeginIndex());
      m_lastMatchStart_ = -1;
      return -1;
    }
    

    m_lastMatchStart_ = handlePrevious(start);
    return m_lastMatchStart_;
  }
  







  public boolean isOverlapping()
  {
    return m_isOverlap_;
  }
  













  public void reset()
  {
    matchLength = 0;
    setIndex(targetText.getBeginIndex());
    m_isOverlap_ = false;
    m_isForwardSearching_ = true;
    m_reset_ = true;
    m_setOffset_ = -1;
  }
  

















  public final int first()
  {
    m_isForwardSearching_ = true;
    setIndex(targetText.getBeginIndex());
    return next();
  }
  


















  public final int following(int position)
  {
    m_isForwardSearching_ = true;
    
    setIndex(position);
    return next();
  }
  

















  public final int last()
  {
    m_isForwardSearching_ = false;
    setIndex(targetText.getEndIndex());
    return previous();
  }
  



















  public final int preceding(int position)
  {
    m_isForwardSearching_ = false;
    
    setIndex(position);
    return previous();
  }
  













































  protected SearchIterator(CharacterIterator target, BreakIterator breaker)
  {
    if ((target == null) || (target.getEndIndex() - target.getBeginIndex() == 0))
    {
      throw new IllegalArgumentException("Illegal argument target.  Argument can not be null or of length 0");
    }
    

    targetText = target;
    breakIterator = breaker;
    if (breakIterator != null) {
      breakIterator.setText(target);
    }
    matchLength = 0;
    m_lastMatchStart_ = -1;
    m_isOverlap_ = false;
    m_isForwardSearching_ = true;
    m_reset_ = true;
    m_setOffset_ = -1;
  }
  












  protected void setMatchLength(int length)
  {
    matchLength = length;
  }
  
  protected abstract int handleNext(int paramInt);
  
  protected abstract int handlePrevious(int paramInt);
}
