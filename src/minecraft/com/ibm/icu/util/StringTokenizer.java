package com.ibm.icu.util;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import java.util.Enumeration;
import java.util.NoSuchElementException;





































































































public final class StringTokenizer
  implements Enumeration<Object>
{
  private int m_tokenOffset_;
  private int m_tokenSize_;
  private int[] m_tokenStart_;
  private int[] m_tokenLimit_;
  private UnicodeSet m_delimiters_;
  private String m_source_;
  private int m_length_;
  private int m_nextOffset_;
  private boolean m_returnDelimiters_;
  private boolean m_coalesceDelimiters_;
  
  public StringTokenizer(String str, UnicodeSet delim, boolean returndelims)
  {
    this(str, delim, returndelims, false);
  }
  



















  /**
   * @deprecated
   */
  public StringTokenizer(String str, UnicodeSet delim, boolean returndelims, boolean coalescedelims)
  {
    m_source_ = str;
    m_length_ = str.length();
    if (delim == null) {
      m_delimiters_ = EMPTY_DELIMITER_;
    }
    else {
      m_delimiters_ = delim;
    }
    m_returnDelimiters_ = returndelims;
    m_coalesceDelimiters_ = coalescedelims;
    m_tokenOffset_ = -1;
    m_tokenSize_ = -1;
    if (m_length_ == 0)
    {
      m_nextOffset_ = -1;
    }
    else {
      m_nextOffset_ = 0;
      if (!returndelims) {
        m_nextOffset_ = getNextNonDelimiter(0);
      }
    }
  }
  










  public StringTokenizer(String str, UnicodeSet delim)
  {
    this(str, delim, false, false);
  }
  















  public StringTokenizer(String str, String delim, boolean returndelims)
  {
    this(str, delim, returndelims, false);
  }
  




















  /**
   * @deprecated
   */
  public StringTokenizer(String str, String delim, boolean returndelims, boolean coalescedelims)
  {
    m_delimiters_ = EMPTY_DELIMITER_;
    if ((delim != null) && (delim.length() > 0)) {
      m_delimiters_ = new UnicodeSet();
      m_delimiters_.addAll(delim);
      checkDelimiters();
    }
    m_coalesceDelimiters_ = coalescedelims;
    m_source_ = str;
    m_length_ = str.length();
    m_returnDelimiters_ = returndelims;
    m_tokenOffset_ = -1;
    m_tokenSize_ = -1;
    if (m_length_ == 0)
    {
      m_nextOffset_ = -1;
    }
    else {
      m_nextOffset_ = 0;
      if (!returndelims) {
        m_nextOffset_ = getNextNonDelimiter(0);
      }
    }
  }
  











  public StringTokenizer(String str, String delim)
  {
    this(str, delim, false, false);
  }
  











  public StringTokenizer(String str)
  {
    this(str, DEFAULT_DELIMITERS_, false, false);
  }
  












  public boolean hasMoreTokens()
  {
    return m_nextOffset_ >= 0;
  }
  







  public String nextToken()
  {
    if (m_tokenOffset_ < 0) {
      if (m_nextOffset_ < 0) {
        throw new NoSuchElementException("No more tokens in String");
      }
      
      if (m_returnDelimiters_) {
        int tokenlimit = 0;
        int c = UTF16.charAt(m_source_, m_nextOffset_);
        boolean contains = (c < delims.length) && (delims[c] != 0) ? true : delims == null ? m_delimiters_.contains(c) : false;
        

        if (contains) {
          if (m_coalesceDelimiters_) {
            tokenlimit = getNextNonDelimiter(m_nextOffset_);
          } else {
            tokenlimit = m_nextOffset_ + UTF16.getCharCount(c);
            if (tokenlimit == m_length_) {
              tokenlimit = -1;
            }
          }
        }
        else
          tokenlimit = getNextDelimiter(m_nextOffset_);
        String result;
        String result;
        if (tokenlimit < 0) {
          result = m_source_.substring(m_nextOffset_);
        }
        else {
          result = m_source_.substring(m_nextOffset_, tokenlimit);
        }
        m_nextOffset_ = tokenlimit;
        return result;
      }
      
      int tokenlimit = getNextDelimiter(m_nextOffset_);
      String result;
      if (tokenlimit < 0) {
        String result = m_source_.substring(m_nextOffset_);
        m_nextOffset_ = tokenlimit;
      }
      else {
        result = m_source_.substring(m_nextOffset_, tokenlimit);
        m_nextOffset_ = getNextNonDelimiter(tokenlimit);
      }
      
      return result;
    }
    

    if (m_tokenOffset_ >= m_tokenSize_)
      throw new NoSuchElementException("No more tokens in String");
    String result;
    String result;
    if (m_tokenLimit_[m_tokenOffset_] >= 0) {
      result = m_source_.substring(m_tokenStart_[m_tokenOffset_], m_tokenLimit_[m_tokenOffset_]);
    }
    else
    {
      result = m_source_.substring(m_tokenStart_[m_tokenOffset_]);
    }
    m_tokenOffset_ += 1;
    m_nextOffset_ = -1;
    if (m_tokenOffset_ < m_tokenSize_) {
      m_nextOffset_ = m_tokenStart_[m_tokenOffset_];
    }
    return result;
  }
  














  public String nextToken(String delim)
  {
    m_delimiters_ = EMPTY_DELIMITER_;
    if ((delim != null) && (delim.length() > 0)) {
      m_delimiters_ = new UnicodeSet();
      m_delimiters_.addAll(delim);
    }
    return nextToken(m_delimiters_);
  }
  














  public String nextToken(UnicodeSet delim)
  {
    m_delimiters_ = delim;
    checkDelimiters();
    m_tokenOffset_ = -1;
    m_tokenSize_ = -1;
    if (!m_returnDelimiters_) {
      m_nextOffset_ = getNextNonDelimiter(m_nextOffset_);
    }
    return nextToken();
  }
  









  public boolean hasMoreElements()
  {
    return hasMoreTokens();
  }
  











  public Object nextElement()
  {
    return nextToken();
  }
  









  public int countTokens()
  {
    int result = 0;
    if (hasMoreTokens()) {
      if (m_tokenOffset_ >= 0) {
        return m_tokenSize_ - m_tokenOffset_;
      }
      if (m_tokenStart_ == null) {
        m_tokenStart_ = new int[100];
        m_tokenLimit_ = new int[100];
      }
      do {
        if (m_tokenStart_.length == result) {
          int[] temptokenindex = m_tokenStart_;
          int[] temptokensize = m_tokenLimit_;
          int originalsize = temptokenindex.length;
          int newsize = originalsize + 100;
          m_tokenStart_ = new int[newsize];
          m_tokenLimit_ = new int[newsize];
          System.arraycopy(temptokenindex, 0, m_tokenStart_, 0, originalsize);
          
          System.arraycopy(temptokensize, 0, m_tokenLimit_, 0, originalsize);
        }
        
        m_tokenStart_[result] = m_nextOffset_;
        if (m_returnDelimiters_) {
          int c = UTF16.charAt(m_source_, m_nextOffset_);
          boolean contains = (c < delims.length) && (delims[c] != 0) ? true : delims == null ? m_delimiters_.contains(c) : false;
          

          if (contains) {
            if (m_coalesceDelimiters_) {
              m_tokenLimit_[result] = getNextNonDelimiter(m_nextOffset_);
            }
            else {
              int p = m_nextOffset_ + 1;
              if (p == m_length_) {
                p = -1;
              }
              m_tokenLimit_[result] = p;
            }
            
          }
          else {
            m_tokenLimit_[result] = getNextDelimiter(m_nextOffset_);
          }
          m_nextOffset_ = m_tokenLimit_[result];
        }
        else {
          m_tokenLimit_[result] = getNextDelimiter(m_nextOffset_);
          m_nextOffset_ = getNextNonDelimiter(m_tokenLimit_[result]);
        }
        result++;
      } while (m_nextOffset_ >= 0);
      m_tokenOffset_ = 0;
      m_tokenSize_ = result;
      m_nextOffset_ = m_tokenStart_[0];
    }
    return result;
  }
  






















































  private static final UnicodeSet DEFAULT_DELIMITERS_ = new UnicodeSet(new int[] { 9, 10, 12, 13, 32, 32 });
  



  private static final int TOKEN_SIZE_ = 100;
  


  private static final UnicodeSet EMPTY_DELIMITER_ = UnicodeSet.EMPTY;
  



  private boolean[] delims;
  




  private int getNextDelimiter(int offset)
  {
    if (offset >= 0) {
      int result = offset;
      int c = 0;
      if (delims == null) {
        do {
          c = UTF16.charAt(m_source_, result);
          if (m_delimiters_.contains(c)) {
            break;
          }
          result++;
        } while (result < m_length_);
      } else {
        do {
          c = UTF16.charAt(m_source_, result);
          if ((c < delims.length) && (delims[c] != 0)) {
            break;
          }
          result++;
        } while (result < m_length_);
      }
      if (result < m_length_) {
        return result;
      }
    }
    return -1 - m_length_;
  }
  







  private int getNextNonDelimiter(int offset)
  {
    if (offset >= 0) {
      int result = offset;
      int c = 0;
      if (delims == null) {
        do {
          c = UTF16.charAt(m_source_, result);
          if (!m_delimiters_.contains(c)) {
            break;
          }
          result++;
        } while (result < m_length_);
      } else {
        do {
          c = UTF16.charAt(m_source_, result);
          if ((c >= delims.length) || (delims[c] == 0)) {
            break;
          }
          result++;
        } while (result < m_length_);
      }
      if (result < m_length_) {
        return result;
      }
    }
    return -1 - m_length_;
  }
  
  void checkDelimiters() {
    if ((m_delimiters_ == null) || (m_delimiters_.size() == 0)) {
      delims = new boolean[0];
    } else {
      int maxChar = m_delimiters_.getRangeEnd(m_delimiters_.getRangeCount() - 1);
      if (maxChar < 127) {
        delims = new boolean[maxChar + 1];
        int ch; for (int i = 0; -1 != (ch = m_delimiters_.charAt(i)); i++) {
          delims[ch] = true;
        }
      } else {
        delims = null;
      }
    }
  }
}
