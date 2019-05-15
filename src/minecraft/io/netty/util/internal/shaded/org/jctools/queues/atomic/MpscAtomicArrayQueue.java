package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
























public final class MpscAtomicArrayQueue<E>
  extends AtomicReferenceArrayQueue<E>
  implements QueueProgressIndicators
{
  private final AtomicLong consumerIndex;
  private final AtomicLong producerIndex;
  private volatile long headCache;
  
  public MpscAtomicArrayQueue(int capacity)
  {
    super(capacity);
    consumerIndex = new AtomicLong();
    producerIndex = new AtomicLong();
  }
  









  public boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    

    int mask = this.mask;
    long capacity = mask + 1;
    long consumerIndexCache = lvConsumerIndexCache();
    long currentProducerIndex;
    do {
      currentProducerIndex = lvProducerIndex();
      long wrapPoint = currentProducerIndex - capacity;
      if (consumerIndexCache <= wrapPoint) {
        long currHead = lvConsumerIndex();
        if (currHead <= wrapPoint) {
          return false;
        }
        
        svConsumerIndexCache(currHead);
        
        consumerIndexCache = currHead;
      }
      
    } while (!casProducerIndex(currentProducerIndex, currentProducerIndex + 1L));
    





    int offset = calcElementOffset(currentProducerIndex, mask);
    soElement(offset, e);
    return true;
  }
  





  public final int weakOffer(E e)
  {
    if (null == e) {
      throw new NullPointerException("Null is not a valid element");
    }
    int mask = this.mask;
    long capacity = mask + 1;
    long currentTail = lvProducerIndex();
    long consumerIndexCache = lvConsumerIndexCache();
    long wrapPoint = currentTail - capacity;
    if (consumerIndexCache <= wrapPoint) {
      long currHead = lvConsumerIndex();
      if (currHead <= wrapPoint) {
        return 1;
      }
      svConsumerIndexCache(currHead);
    }
    


    if (!casProducerIndex(currentTail, currentTail + 1L)) {
      return -1;
    }
    

    int offset = calcElementOffset(currentTail, mask);
    soElement(offset, e);
    return 0;
  }
  









  public E poll()
  {
    long consumerIndex = lvConsumerIndex();
    int offset = calcElementOffset(consumerIndex);
    
    AtomicReferenceArray<E> buffer = this.buffer;
    

    E e = lvElement(buffer, offset);
    if (null == e)
    {




      if (consumerIndex != lvProducerIndex()) {
        do {
          e = lvElement(buffer, offset);
        } while (e == null);
      } else {
        return null;
      }
    }
    
    spElement(buffer, offset, null);
    soConsumerIndex(consumerIndex + 1L);
    return e;
  }
  










  public E peek()
  {
    AtomicReferenceArray<E> buffer = this.buffer;
    
    long consumerIndex = lvConsumerIndex();
    int offset = calcElementOffset(consumerIndex);
    E e = lvElement(buffer, offset);
    if (null == e)
    {




      if (consumerIndex != lvProducerIndex()) {
        do {
          e = lvElement(buffer, offset);
        } while (e == null);
      } else {
        return null;
      }
    }
    return e;
  }
  











  public int size()
  {
    long after = lvConsumerIndex();
    for (;;) {
      long before = after;
      long currentProducerIndex = lvProducerIndex();
      after = lvConsumerIndex();
      if (before == after) {
        return (int)(currentProducerIndex - after);
      }
    }
  }
  





  public boolean isEmpty()
  {
    return lvConsumerIndex() == lvProducerIndex();
  }
  
  public long currentProducerIndex()
  {
    return lvProducerIndex();
  }
  
  public long currentConsumerIndex()
  {
    return lvConsumerIndex();
  }
  
  private long lvConsumerIndex() { return consumerIndex.get(); }
  
  private long lvProducerIndex() {
    return producerIndex.get();
  }
  
  protected final long lvConsumerIndexCache() { return headCache; }
  
  protected final void svConsumerIndexCache(long v)
  {
    headCache = v;
  }
  
  protected final boolean casProducerIndex(long expect, long newValue) { return producerIndex.compareAndSet(expect, newValue); }
  
  protected void soConsumerIndex(long l) {
    consumerIndex.lazySet(l);
  }
}
