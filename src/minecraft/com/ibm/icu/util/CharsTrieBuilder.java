package com.ibm.icu.util;

import java.nio.CharBuffer;






























public final class CharsTrieBuilder
  extends StringTrieBuilder
{
  public CharsTrieBuilder() {}
  
  public CharsTrieBuilder add(CharSequence s, int value)
  {
    addImpl(s, value);
    return this;
  }
  










  public CharsTrie build(StringTrieBuilder.Option buildOption)
  {
    return new CharsTrie(buildCharSequence(buildOption), 0);
  }
  










  public CharSequence buildCharSequence(StringTrieBuilder.Option buildOption)
  {
    buildChars(buildOption);
    return CharBuffer.wrap(chars, chars.length - charsLength, charsLength);
  }
  
  private void buildChars(StringTrieBuilder.Option buildOption)
  {
    if (chars == null) {
      chars = new char['Ѐ'];
    }
    buildImpl(buildOption);
  }
  





  public CharsTrieBuilder clear()
  {
    clearImpl();
    chars = null;
    charsLength = 0;
    return this;
  }
  

  /**
   * @deprecated
   */
  protected boolean matchNodesCanHaveValues()
  {
    return true;
  }
  
  /**
   * @deprecated
   */
  protected int getMaxBranchLinearSubNodeLength()
  {
    return 5;
  }
  
  /**
   * @deprecated
   */
  protected int getMinLinearMatch() {
    return 48;
  }
  

  /**
   * @deprecated
   */
  protected int getMaxLinearMatchLength() { return 16; }
  
  private void ensureCapacity(int length) {
    if (length > chars.length) {
      int newCapacity = chars.length;
      do {
        newCapacity *= 2;
      } while (newCapacity <= length);
      char[] newChars = new char[newCapacity];
      System.arraycopy(chars, chars.length - charsLength, newChars, newChars.length - charsLength, charsLength);
      
      chars = newChars;
    }
  }
  

  /**
   * @deprecated
   */
  protected int write(int unit)
  {
    int newLength = charsLength + 1;
    ensureCapacity(newLength);
    charsLength = newLength;
    chars[(chars.length - charsLength)] = ((char)unit);
    return charsLength;
  }
  

  /**
   * @deprecated
   */
  protected int write(int offset, int length)
  {
    int newLength = charsLength + length;
    ensureCapacity(newLength);
    charsLength = newLength;
    int charsOffset = chars.length - charsLength;
    while (length > 0) {
      chars[(charsOffset++)] = strings.charAt(offset++);
      length--;
    }
    return charsLength;
  }
  
  private int write(char[] s, int length) { int newLength = charsLength + length;
    ensureCapacity(newLength);
    charsLength = newLength;
    System.arraycopy(s, 0, chars, chars.length - charsLength, length);
    return charsLength;
  }
  

  private final char[] intUnits = new char[3];
  private char[] chars;
  private int charsLength;
  
  /**
   * @deprecated
   */
  protected int writeValueAndFinal(int i, boolean isFinal)
  {
    if ((0 <= i) && (i <= 16383))
      return write(i | (isFinal ? 32768 : 0));
    int length;
    int length;
    if ((i < 0) || (i > 1073676287)) {
      intUnits[0] = '翿';
      intUnits[1] = ((char)(i >> 16));
      intUnits[2] = ((char)i);
      length = 3;

    }
    else
    {
      intUnits[0] = ((char)(16384 + (i >> 16)));
      intUnits[1] = ((char)i);
      length = 2;
    }
    intUnits[0] = ((char)(intUnits[0] | (isFinal ? 32768 : '\000')));
    return write(intUnits, length);
  }
  

  /**
   * @deprecated
   */
  protected int writeValueAndType(boolean hasValue, int value, int node)
  {
    if (!hasValue)
      return write(node);
    int length;
    int length;
    if ((value < 0) || (value > 16646143)) {
      intUnits[0] = '翀';
      intUnits[1] = ((char)(value >> 16));
      intUnits[2] = ((char)value);
      length = 3; } else { int length;
      if (value <= 255) {
        intUnits[0] = ((char)(value + 1 << 6));
        length = 1;
      } else {
        intUnits[0] = ((char)(16448 + (value >> 10 & 0x7FC0)));
        intUnits[1] = ((char)value);
        length = 2;
      } }
    int tmp115_114 = 0; char[] tmp115_111 = intUnits;tmp115_111[tmp115_114] = ((char)(tmp115_111[tmp115_114] | (char)node));
    return write(intUnits, length);
  }
  

  /**
   * @deprecated
   */
  protected int writeDeltaTo(int jumpTarget)
  {
    int i = charsLength - jumpTarget;
    assert (i >= 0);
    if (i <= 64511)
      return write(i);
    int length;
    int length;
    if (i <= 67043327) {
      intUnits[0] = ((char)(64512 + (i >> 16)));
      length = 1;
    } else {
      intUnits[0] = 65535;
      intUnits[1] = ((char)(i >> 16));
      length = 2;
    }
    intUnits[(length++)] = ((char)i);
    return write(intUnits, length);
  }
}
