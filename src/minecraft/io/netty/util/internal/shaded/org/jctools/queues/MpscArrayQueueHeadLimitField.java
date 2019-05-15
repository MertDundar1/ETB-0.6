package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;


























































abstract class MpscArrayQueueHeadLimitField<E>
  extends MpscArrayQueueMidPad<E>
{
  private static final long P_LIMIT_OFFSET;
  private volatile long producerLimit;
  
  static
  {
    try
    {
      P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpscArrayQueueHeadLimitField.class.getDeclaredField("producerLimit"));
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  

  public MpscArrayQueueHeadLimitField(int capacity)
  {
    super(capacity);
    producerLimit = capacity;
  }
  
  protected final long lvProducerLimit() {
    return producerLimit;
  }
  
  protected final void soProducerLimit(long v) {
    UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, v);
  }
}
