package com.ibm.icu.text;

import java.text.CharacterIterator;






















/**
 * @deprecated
 */
public final class StringCharacterIterator
  implements CharacterIterator
{
  private String text;
  private int begin;
  private int end;
  private int pos;
  
  /**
   * @deprecated
   */
  public StringCharacterIterator(String text)
  {
    this(text, 0);
  }
  




  /**
   * @deprecated
   */
  public StringCharacterIterator(String text, int pos)
  {
    this(text, 0, text.length(), pos);
  }
  






  /**
   * @deprecated
   */
  public StringCharacterIterator(String text, int begin, int end, int pos)
  {
    if (text == null) {
      throw new NullPointerException();
    }
    this.text = text;
    
    if ((begin < 0) || (begin > end) || (end > text.length())) {
      throw new IllegalArgumentException("Invalid substring range");
    }
    
    if ((pos < begin) || (pos > end)) {
      throw new IllegalArgumentException("Invalid position");
    }
    
    this.begin = begin;
    this.end = end;
    this.pos = pos;
  }
  





  /**
   * @deprecated
   */
  public void setText(String text)
  {
    if (text == null) {
      throw new NullPointerException();
    }
    this.text = text;
    begin = 0;
    end = text.length();
    pos = 0;
  }
  


  /**
   * @deprecated
   */
  public char first()
  {
    pos = begin;
    return current();
  }
  


  /**
   * @deprecated
   */
  public char last()
  {
    if (end != begin) {
      pos = (end - 1);
    } else {
      pos = end;
    }
    return current();
  }
  


  /**
   * @deprecated
   */
  public char setIndex(int p)
  {
    if ((p < begin) || (p > end)) {
      throw new IllegalArgumentException("Invalid index");
    }
    pos = p;
    return current();
  }
  


  /**
   * @deprecated
   */
  public char current()
  {
    if ((pos >= begin) && (pos < end)) {
      return text.charAt(pos);
    }
    
    return 65535;
  }
  



  /**
   * @deprecated
   */
  public char next()
  {
    if (pos < end - 1) {
      pos += 1;
      return text.charAt(pos);
    }
    
    pos = end;
    return 65535;
  }
  



  /**
   * @deprecated
   */
  public char previous()
  {
    if (pos > begin) {
      pos -= 1;
      return text.charAt(pos);
    }
    
    return 65535;
  }
  



  /**
   * @deprecated
   */
  public int getBeginIndex()
  {
    return begin;
  }
  


  /**
   * @deprecated
   */
  public int getEndIndex()
  {
    return end;
  }
  


  /**
   * @deprecated
   */
  public int getIndex()
  {
    return pos;
  }
  




  /**
   * @deprecated
   */
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof StringCharacterIterator)) {
      return false;
    }
    
    StringCharacterIterator that = (StringCharacterIterator)obj;
    
    if (hashCode() != that.hashCode()) {
      return false;
    }
    if (!text.equals(text)) {
      return false;
    }
    if ((pos != pos) || (begin != begin) || (end != end)) {
      return false;
    }
    return true;
  }
  


  /**
   * @deprecated
   */
  public int hashCode()
  {
    return text.hashCode() ^ pos ^ begin ^ end;
  }
  

  /**
   * @deprecated
   */
  public Object clone()
  {
    try
    {
      return (StringCharacterIterator)super.clone();

    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }
}
