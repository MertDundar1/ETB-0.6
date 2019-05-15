package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;





















































abstract class MpmcArrayQueueConsumerField<E>
  extends MpmcArrayQueueL2Pad<E>
{
  private static final long C_INDEX_OFFSET;
  private volatile long consumerIndex;
  
  static
  {
    try
    {
      C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpmcArrayQueueConsumerField.class
        .getDeclaredField("consumerIndex"));
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  
  public MpmcArrayQueueConsumerField(int capacity)
  {
    super(capacity);
  }
  
  protected final long lvConsumerIndex() {
    return consumerIndex;
  }
  
  protected final boolean casConsumerIndex(long expect, long newValue) {
    return UnsafeAccess.UNSAFE.compareAndSwapLong(this, C_INDEX_OFFSET, expect, newValue);
  }
}
