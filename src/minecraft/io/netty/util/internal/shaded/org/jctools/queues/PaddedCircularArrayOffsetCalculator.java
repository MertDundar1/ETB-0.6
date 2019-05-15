package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.JvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;




public final class PaddedCircularArrayOffsetCalculator
{
  static final int REF_BUFFER_PAD = JvmInfo.CACHE_LINE_SIZE * 2 >> UnsafeRefArrayAccess.REF_ELEMENT_SHIFT;
  
  static { int paddingOffset = REF_BUFFER_PAD << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT;
    REF_ARRAY_BASE = UnsafeRefArrayAccess.REF_ARRAY_BASE + paddingOffset;
  }
  



  public static <E> E[] allocate(int capacity)
  {
    return (Object[])new Object[capacity + REF_BUFFER_PAD * 2];
  }
  


  static final long REF_ARRAY_BASE;
  
  protected static long calcElementOffset(long index, long mask)
  {
    return REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
  }
  
  private PaddedCircularArrayOffsetCalculator() {}
}
