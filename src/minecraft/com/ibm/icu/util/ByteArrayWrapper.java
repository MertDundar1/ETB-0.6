package com.ibm.icu.util;

import com.ibm.icu.impl.Utility;
import java.nio.ByteBuffer;
















































public class ByteArrayWrapper
  implements Comparable<ByteArrayWrapper>
{
  public byte[] bytes;
  public int size;
  
  public ByteArrayWrapper() {}
  
  public ByteArrayWrapper(byte[] bytesToAdopt, int size)
  {
    if (((bytesToAdopt == null) && (size != 0)) || (size < 0) || (size > bytesToAdopt.length)) {
      throw new IndexOutOfBoundsException("illegal size: " + size);
    }
    bytes = bytesToAdopt;
    this.size = size;
  }
  




  public ByteArrayWrapper(ByteBuffer source)
  {
    size = source.limit();
    bytes = new byte[size];
    source.get(bytes, 0, size);
  }
  

































  public ByteArrayWrapper ensureCapacity(int capacity)
  {
    if ((bytes == null) || (bytes.length < capacity)) {
      byte[] newbytes = new byte[capacity];
      copyBytes(bytes, 0, newbytes, 0, size);
      bytes = newbytes;
    }
    return this;
  }
  











  public final ByteArrayWrapper set(byte[] src, int start, int limit)
  {
    size = 0;
    append(src, start, limit);
    return this;
  }
  




















  public final ByteArrayWrapper append(byte[] src, int start, int limit)
  {
    int len = limit - start;
    ensureCapacity(size + len);
    copyBytes(src, start, bytes, size, len);
    size += len;
    return this;
  }
  













  public final byte[] releaseBytes()
  {
    byte[] result = bytes;
    bytes = null;
    size = 0;
    return result;
  }
  





  public String toString()
  {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < size; i++) {
      if (i != 0) result.append(" ");
      result.append(Utility.hex(bytes[i] & 0xFF, 2));
    }
    return result.toString();
  }
  





  public boolean equals(Object other)
  {
    if (this == other) return true;
    if (other == null) return false;
    try {
      ByteArrayWrapper that = (ByteArrayWrapper)other;
      if (size != size) return false;
      for (int i = 0; i < size; i++) {
        if (bytes[i] != bytes[i]) return false;
      }
      return true;
    }
    catch (ClassCastException e) {}
    
    return false;
  }
  




  public int hashCode()
  {
    int result = bytes.length;
    for (int i = 0; i < size; i++) {
      result = 37 * result + bytes[i];
    }
    return result;
  }
  







  public int compareTo(ByteArrayWrapper other)
  {
    if (this == other) return 0;
    int minSize = size < size ? size : size;
    for (int i = 0; i < minSize; i++) {
      if (bytes[i] != bytes[i]) {
        return (bytes[i] & 0xFF) - (bytes[i] & 0xFF);
      }
    }
    return size - size;
  }
  











  private static final void copyBytes(byte[] src, int srcoff, byte[] tgt, int tgtoff, int length)
  {
    if (length < 64) {
      int i = srcoff; for (int n = tgtoff;; n++) { length--; if (length < 0) break;
        tgt[n] = src[i];i++;
      }
    }
    else {
      System.arraycopy(src, srcoff, tgt, tgtoff, length);
    }
  }
}
