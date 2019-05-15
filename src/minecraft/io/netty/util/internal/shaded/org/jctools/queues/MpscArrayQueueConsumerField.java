package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;
































































































abstract class MpscArrayQueueConsumerField<E>
  extends MpscArrayQueueL2Pad<E>
{
  private static final long C_INDEX_OFFSET;
  protected long consumerIndex;
  
  static
  {
    try
    {
      C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpscArrayQueueConsumerField.class.getDeclaredField("consumerIndex"));
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  

  public MpscArrayQueueConsumerField(int capacity)
  {
    super(capacity);
  }
  
  protected final long lpConsumerIndex() {
    return consumerIndex;
  }
  
  protected final long lvConsumerIndex() {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
  }
  
  protected void soConsumerIndex(long l) {
    UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, l);
  }
}
