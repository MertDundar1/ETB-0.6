package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;




















abstract class MpmcArrayQueueProducerField<E>
  extends MpmcArrayQueueL1Pad<E>
{
  private static final long P_INDEX_OFFSET;
  private volatile long producerIndex;
  
  static
  {
    try
    {
      P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpmcArrayQueueProducerField.class
        .getDeclaredField("producerIndex"));
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  
  public MpmcArrayQueueProducerField(int capacity)
  {
    super(capacity);
  }
  
  protected final long lvProducerIndex() {
    return producerIndex;
  }
  
  protected final boolean casProducerIndex(long expect, long newValue) {
    return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
  }
}
