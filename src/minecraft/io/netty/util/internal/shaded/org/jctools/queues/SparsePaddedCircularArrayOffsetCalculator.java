package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public final class SparsePaddedCircularArrayOffsetCalculator {
  static final int SPARSE_SHIFT = Integer.getInteger("io.netty.util.internal.shaded.org.jctools.sparse.shift", 0).intValue();
  


  private static final int REF_ELEMENT_SHIFT = UnsafeRefArrayAccess.REF_ELEMENT_SHIFT + SPARSE_SHIFT;
  private static final long REF_ARRAY_BASE = PaddedCircularArrayOffsetCalculator.REF_ARRAY_BASE;
  

  private SparsePaddedCircularArrayOffsetCalculator() {}
  

  public static <E> E[] allocate(int capacity)
  {
    return (Object[])new Object[(capacity << SPARSE_SHIFT) + PaddedCircularArrayOffsetCalculator.REF_BUFFER_PAD * 2];
  }
  





  public static long calcElementOffset(long index, long mask)
  {
    return REF_ARRAY_BASE + ((index & mask) << REF_ELEMENT_SHIFT);
  }
}
