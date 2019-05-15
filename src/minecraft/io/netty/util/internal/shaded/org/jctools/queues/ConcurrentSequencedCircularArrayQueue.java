package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.JvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;










public abstract class ConcurrentSequencedCircularArrayQueue<E>
  extends ConcurrentCircularArrayQueue<E>
{
  private static final long ARRAY_BASE;
  private static final int ELEMENT_SHIFT;
  protected static final int SEQ_BUFFER_PAD;
  protected final long[] sequenceBuffer;
  
  static
  {
    int scale = UnsafeAccess.UNSAFE.arrayIndexScale([J.class);
    if (8 == scale) {
      ELEMENT_SHIFT = 3 + SparsePaddedCircularArrayOffsetCalculator.SPARSE_SHIFT;
    } else {
      throw new IllegalStateException("Unexpected long[] element size");
    }
    
    SEQ_BUFFER_PAD = JvmInfo.CACHE_LINE_SIZE * 2 / scale;
    
    ARRAY_BASE = UnsafeAccess.UNSAFE.arrayBaseOffset([J.class) + SEQ_BUFFER_PAD * scale;
  }
  
  public ConcurrentSequencedCircularArrayQueue(int capacity)
  {
    super(capacity);
    int actualCapacity = (int)(mask + 1L);
    
    sequenceBuffer = new long[(actualCapacity << SparsePaddedCircularArrayOffsetCalculator.SPARSE_SHIFT) + SEQ_BUFFER_PAD * 2];
    
    for (long i = 0L; i < actualCapacity; i += 1L) {
      soSequence(sequenceBuffer, calcSequenceOffset(i), i);
    }
  }
  
  protected final long calcSequenceOffset(long index) {
    return calcSequenceOffset(index, mask);
  }
  
  protected static long calcSequenceOffset(long index, long mask) { return ARRAY_BASE + ((index & mask) << ELEMENT_SHIFT); }
  
  protected final void soSequence(long[] buffer, long offset, long e) {
    UnsafeAccess.UNSAFE.putOrderedLong(buffer, offset, e);
  }
  
  protected final long lvSequence(long[] buffer, long offset) {
    return UnsafeAccess.UNSAFE.getLongVolatile(buffer, offset);
  }
}
