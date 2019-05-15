package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;














final class HttpHeaderEntity
  implements CharSequence
{
  private final String name;
  private final int hash;
  private final byte[] bytes;
  private final int separatorLen;
  
  public HttpHeaderEntity(String name)
  {
    this(name, null);
  }
  
  public HttpHeaderEntity(String name, byte[] separator) {
    this.name = name;
    hash = HttpHeaders.hash(name);
    byte[] nameBytes = name.getBytes(CharsetUtil.US_ASCII);
    if (separator == null) {
      bytes = nameBytes;
      separatorLen = 0;
    } else {
      separatorLen = separator.length;
      bytes = new byte[nameBytes.length + separator.length];
      System.arraycopy(nameBytes, 0, bytes, 0, nameBytes.length);
      System.arraycopy(separator, 0, bytes, nameBytes.length, separator.length);
    }
  }
  
  int hash() {
    return hash;
  }
  
  public int length()
  {
    return bytes.length - separatorLen;
  }
  
  public char charAt(int index)
  {
    if (bytes.length - separatorLen <= index) {
      throw new IndexOutOfBoundsException();
    }
    return (char)bytes[index];
  }
  
  public CharSequence subSequence(int start, int end)
  {
    return new HttpHeaderEntity(name.substring(start, end));
  }
  
  public String toString()
  {
    return name;
  }
  
  boolean encode(ByteBuf buf) {
    buf.writeBytes(bytes);
    return separatorLen > 0;
  }
}
