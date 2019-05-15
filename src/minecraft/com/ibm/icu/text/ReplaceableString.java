package com.ibm.icu.text;












public class ReplaceableString
  implements Replaceable
{
  private StringBuffer buf;
  











  public ReplaceableString(String str)
  {
    buf = new StringBuffer(str);
  }
  








  public ReplaceableString(StringBuffer buf)
  {
    this.buf = buf;
  }
  



  public ReplaceableString()
  {
    buf = new StringBuffer();
  }
  




  public String toString()
  {
    return buf.toString();
  }
  



  public String substring(int start, int limit)
  {
    return buf.substring(start, limit);
  }
  




  public int length()
  {
    return buf.length();
  }
  






  public char charAt(int offset)
  {
    return buf.charAt(offset);
  }
  










  public int char32At(int offset)
  {
    return UTF16.charAt(buf, offset);
  }
  

















  public void getChars(int srcStart, int srcLimit, char[] dst, int dstStart)
  {
    if (srcStart != srcLimit) {
      buf.getChars(srcStart, srcLimit, dst, dstStart);
    }
  }
  










  public void replace(int start, int limit, String text)
  {
    buf.replace(start, limit, text);
  }
  













  public void replace(int start, int limit, char[] chars, int charsStart, int charsLen)
  {
    buf.delete(start, limit);
    buf.insert(start, chars, charsStart, charsLen);
  }
  














  public void copy(int start, int limit, int dest)
  {
    if ((start == limit) && (start >= 0) && (start <= buf.length())) {
      return;
    }
    char[] text = new char[limit - start];
    getChars(start, limit, text, 0);
    replace(dest, dest, text, 0, limit - start);
  }
  



  public boolean hasMetaData()
  {
    return false;
  }
}
