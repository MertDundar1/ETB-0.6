package com.ibm.icu.util;

import java.nio.ByteBuffer;
















public final class BytesTrieBuilder
  extends StringTrieBuilder
{
  public BytesTrieBuilder() {}
  
  private static final class BytesAsCharSequence
    implements CharSequence
  {
    private byte[] s;
    private int len;
    
    public BytesAsCharSequence(byte[] sequence, int length)
    {
      s = sequence;
      len = length; }
    
    public char charAt(int i) { return (char)(s[i] & 0xFF); }
    public int length() { return len; }
    public CharSequence subSequence(int start, int end) { return null; }
  }
  













  public BytesTrieBuilder add(byte[] sequence, int length, int value)
  {
    addImpl(new BytesAsCharSequence(sequence, length), value);
    return this;
  }
  











  public BytesTrie build(StringTrieBuilder.Option buildOption)
  {
    buildBytes(buildOption);
    return new BytesTrie(bytes, bytes.length - bytesLength);
  }
  















  public ByteBuffer buildByteBuffer(StringTrieBuilder.Option buildOption)
  {
    buildBytes(buildOption);
    return ByteBuffer.wrap(bytes, bytes.length - bytesLength, bytesLength);
  }
  
  private void buildBytes(StringTrieBuilder.Option buildOption)
  {
    if (bytes == null) {
      bytes = new byte['Ѐ'];
    }
    buildImpl(buildOption);
  }
  





  public BytesTrieBuilder clear()
  {
    clearImpl();
    bytes = null;
    bytesLength = 0;
    return this;
  }
  

  /**
   * @deprecated
   */
  protected boolean matchNodesCanHaveValues()
  {
    return false;
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
    return 16;
  }
  

  /**
   * @deprecated
   */
  protected int getMaxLinearMatchLength() { return 16; }
  
  private void ensureCapacity(int length) {
    if (length > bytes.length) {
      int newCapacity = bytes.length;
      do {
        newCapacity *= 2;
      } while (newCapacity <= length);
      byte[] newBytes = new byte[newCapacity];
      System.arraycopy(bytes, bytes.length - bytesLength, newBytes, newBytes.length - bytesLength, bytesLength);
      
      bytes = newBytes;
    }
  }
  

  /**
   * @deprecated
   */
  protected int write(int b)
  {
    int newLength = bytesLength + 1;
    ensureCapacity(newLength);
    bytesLength = newLength;
    bytes[(bytes.length - bytesLength)] = ((byte)b);
    return bytesLength;
  }
  

  /**
   * @deprecated
   */
  protected int write(int offset, int length)
  {
    int newLength = bytesLength + length;
    ensureCapacity(newLength);
    bytesLength = newLength;
    int bytesOffset = bytes.length - bytesLength;
    while (length > 0) {
      bytes[(bytesOffset++)] = ((byte)strings.charAt(offset++));
      length--;
    }
    return bytesLength;
  }
  
  private int write(byte[] b, int length) { int newLength = bytesLength + length;
    ensureCapacity(newLength);
    bytesLength = newLength;
    System.arraycopy(b, 0, bytes, bytes.length - bytesLength, length);
    return bytesLength;
  }
  

  private final byte[] intBytes = new byte[5];
  private byte[] bytes;
  private int bytesLength;
  
  /**
   * @deprecated
   */
  protected int writeValueAndFinal(int i, boolean isFinal)
  {
    if ((0 <= i) && (i <= 64)) {
      return write(16 + i << 1 | (isFinal ? 1 : 0));
    }
    int length = 1;
    if ((i < 0) || (i > 16777215)) {
      intBytes[0] = Byte.MAX_VALUE;
      intBytes[1] = ((byte)(i >> 24));
      intBytes[2] = ((byte)(i >> 16));
      intBytes[3] = ((byte)(i >> 8));
      intBytes[4] = ((byte)i);
      length = 5;
    }
    else
    {
      if (i <= 6911) {
        intBytes[0] = ((byte)(81 + (i >> 8)));
      } else {
        if (i <= 1179647) {
          intBytes[0] = ((byte)(108 + (i >> 16)));
        } else {
          intBytes[0] = 126;
          intBytes[1] = ((byte)(i >> 16));
          length = 2;
        }
        intBytes[(length++)] = ((byte)(i >> 8));
      }
      intBytes[(length++)] = ((byte)i);
    }
    intBytes[0] = ((byte)(intBytes[0] << 1 | (isFinal ? 1 : 0)));
    return write(intBytes, length);
  }
  

  /**
   * @deprecated
   */
  protected int writeValueAndType(boolean hasValue, int value, int node)
  {
    int offset = write(node);
    if (hasValue) {
      offset = writeValueAndFinal(value, false);
    }
    return offset;
  }
  

  /**
   * @deprecated
   */
  protected int writeDeltaTo(int jumpTarget)
  {
    int i = bytesLength - jumpTarget;
    assert (i >= 0);
    if (i <= 191)
      return write(i);
    int length;
    int length;
    if (i <= 12287) {
      intBytes[0] = ((byte)(192 + (i >> 8)));
      length = 1;
    } else { int length;
      if (i <= 917503) {
        intBytes[0] = ((byte)(240 + (i >> 16)));
        length = 2;
      } else { int length;
        if (i <= 16777215) {
          intBytes[0] = -2;
          length = 3;
        } else {
          intBytes[0] = -1;
          intBytes[1] = ((byte)(i >> 24));
          length = 4;
        }
        intBytes[1] = ((byte)(i >> 16));
      }
      intBytes[1] = ((byte)(i >> 8));
    }
    intBytes[(length++)] = ((byte)i);
    return write(intBytes, length);
  }
}
