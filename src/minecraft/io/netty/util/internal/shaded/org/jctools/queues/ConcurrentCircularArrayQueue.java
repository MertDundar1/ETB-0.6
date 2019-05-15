package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.Iterator;



































public abstract class ConcurrentCircularArrayQueue<E>
  extends ConcurrentCircularArrayQueueL0Pad<E>
{
  protected final long mask;
  protected final E[] buffer;
  
  public ConcurrentCircularArrayQueue(int capacity)
  {
    int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
    mask = (actualCapacity - 1);
    buffer = SparsePaddedCircularArrayOffsetCalculator.allocate(actualCapacity);
  }
  



  protected final long calcElementOffset(long index)
  {
    return calcElementOffset(index, mask);
  }
  




  protected static long calcElementOffset(long index, long mask)
  {
    return SparsePaddedCircularArrayOffsetCalculator.calcElementOffset(index, mask);
  }
  
  public Iterator<E> iterator()
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    while ((poll() != null) || (!isEmpty())) {}
  }
  

  public int capacity()
  {
    return (int)(mask + 1L);
  }
}
