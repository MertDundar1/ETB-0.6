package io.netty.util.internal;

import java.util.Arrays;














public final class AppendableCharSequence
  implements CharSequence, Appendable
{
  private char[] chars;
  private int pos;
  
  public AppendableCharSequence(int length)
  {
    if (length < 1) {
      throw new IllegalArgumentException("length: " + length + " (length: >= 1)");
    }
    chars = new char[length];
  }
  
  private AppendableCharSequence(char[] chars) {
    this.chars = chars;
    pos = chars.length;
  }
  
  public int length()
  {
    return pos;
  }
  
  public char charAt(int index)
  {
    if (index > pos) {
      throw new IndexOutOfBoundsException();
    }
    return chars[index];
  }
  
  public AppendableCharSequence subSequence(int start, int end)
  {
    return new AppendableCharSequence(Arrays.copyOfRange(chars, start, end));
  }
  
  public AppendableCharSequence append(char c)
  {
    if (pos == chars.length) {
      char[] old = chars;
      
      int len = old.length << 1;
      if (len < 0) {
        throw new IllegalStateException();
      }
      chars = new char[len];
      System.arraycopy(old, 0, chars, 0, old.length);
    }
    chars[(pos++)] = c;
    return this;
  }
  
  public AppendableCharSequence append(CharSequence csq)
  {
    return append(csq, 0, csq.length());
  }
  
  public AppendableCharSequence append(CharSequence csq, int start, int end)
  {
    if (csq.length() < end) {
      throw new IndexOutOfBoundsException();
    }
    int length = end - start;
    if (length > chars.length - pos) {
      chars = expand(chars, pos + length, pos);
    }
    if ((csq instanceof AppendableCharSequence))
    {
      AppendableCharSequence seq = (AppendableCharSequence)csq;
      char[] src = chars;
      System.arraycopy(src, start, chars, pos, length);
      pos += length;
      return this;
    }
    for (int i = start; i < end; i++) {
      chars[(pos++)] = csq.charAt(i);
    }
    
    return this;
  }
  



  public void reset()
  {
    pos = 0;
  }
  
  public String toString()
  {
    return new String(chars, 0, pos);
  }
  


  public String substring(int start, int end)
  {
    int length = end - start;
    if ((start > pos) || (length > pos)) {
      throw new IndexOutOfBoundsException();
    }
    return new String(chars, start, length);
  }
  
  private static char[] expand(char[] array, int neededSpace, int size) {
    int newCapacity = array.length;
    do
    {
      newCapacity <<= 1;
      
      if (newCapacity < 0) {
        throw new IllegalStateException();
      }
      
    } while (neededSpace > newCapacity);
    
    char[] newArray = new char[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    
    return newArray;
  }
}
