package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.JvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;





























































































public class MpmcArrayQueue<E>
  extends MpmcArrayQueueConsumerField<E>
  implements QueueProgressIndicators
{
  long p01;
  long p02;
  long p03;
  long p04;
  long p05;
  long p06;
  long p07;
  long p10;
  long p11;
  long p12;
  long p13;
  long p14;
  long p15;
  long p16;
  long p17;
  static final int RECOMENDED_POLL_BATCH = JvmInfo.CPUs * 4;
  static final int RECOMENDED_OFFER_BATCH = JvmInfo.CPUs * 4;
  
  public MpmcArrayQueue(int capacity) { super(validateCapacity(capacity)); }
  
  private static int validateCapacity(int capacity)
  {
    if (capacity < 2)
      throw new IllegalArgumentException("Minimum size is 2");
    return capacity;
  }
  
  public boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    long mask = this.mask;
    long capacity = mask + 1L;
    long[] sBuffer = sequenceBuffer;
    



    long cIndex = Long.MAX_VALUE;
    long pIndex;
    long seqOffset; long seq; do { pIndex = lvProducerIndex();
      seqOffset = calcSequenceOffset(pIndex, mask);
      seq = lvSequence(sBuffer, seqOffset);
      if (seq < pIndex) {
        if ((pIndex - capacity <= cIndex) && 
          (pIndex - capacity <= (cIndex = lvConsumerIndex())))
        {
          return false;
        }
        seq = pIndex + 1L;
      }
      
    } while ((seq > pIndex) || 
      (!casProducerIndex(pIndex, pIndex + 1L)));
    
    assert (null == UnsafeRefArrayAccess.lpElement(buffer, calcElementOffset(pIndex, mask)));
    UnsafeRefArrayAccess.soElement(buffer, calcElementOffset(pIndex, mask), e);
    soSequence(sBuffer, seqOffset, pIndex + 1L);
    return true;
  }
  








  public E poll()
  {
    long[] sBuffer = sequenceBuffer;
    long mask = this.mask;
    




    long pIndex = -1L;
    long cIndex;
    long seqOffset; long seq; long expectedSeq; do { cIndex = lvConsumerIndex();
      seqOffset = calcSequenceOffset(cIndex, mask);
      seq = lvSequence(sBuffer, seqOffset);
      expectedSeq = cIndex + 1L;
      if (seq < expectedSeq) {
        if ((cIndex >= pIndex) && 
          (cIndex == (pIndex = lvProducerIndex())))
        {
          return null;
        }
        seq = expectedSeq + 1L;
      }
      
    } while ((seq > expectedSeq) || 
      (!casConsumerIndex(cIndex, cIndex + 1L)));
    
    long offset = calcElementOffset(cIndex, mask);
    E e = UnsafeRefArrayAccess.lpElement(buffer, offset);
    assert (e != null);
    UnsafeRefArrayAccess.soElement(buffer, offset, null);
    soSequence(sBuffer, seqOffset, cIndex + mask + 1L);
    return e;
  }
  
  public E peek()
  {
    long cIndex;
    E e;
    do {
      cIndex = lvConsumerIndex();
      
      e = UnsafeRefArrayAccess.lpElement(buffer, calcElementOffset(cIndex));
    }
    while ((e == null) && (cIndex != lvProducerIndex()));
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
  
  public boolean relaxedOffer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    long mask = this.mask;
    long[] sBuffer = sequenceBuffer;
    long pIndex;
    long seqOffset;
    long seq;
    do
    {
      pIndex = lvProducerIndex();
      seqOffset = calcSequenceOffset(pIndex, mask);
      seq = lvSequence(sBuffer, seqOffset);
      if (seq < pIndex) {
        return false;
      }
    } while ((seq > pIndex) || 
      (!casProducerIndex(pIndex, pIndex + 1L)));
    
    UnsafeRefArrayAccess.soElement(buffer, calcElementOffset(pIndex, mask), e);
    soSequence(sBuffer, seqOffset, pIndex + 1L);
    return true;
  }
  
  public E relaxedPoll()
  {
    long[] sBuffer = sequenceBuffer;
    long mask = this.mask;
    long cIndex;
    long seqOffset;
    long seq;
    long expectedSeq;
    do
    {
      cIndex = lvConsumerIndex();
      seqOffset = calcSequenceOffset(cIndex, mask);
      seq = lvSequence(sBuffer, seqOffset);
      expectedSeq = cIndex + 1L;
      if (seq < expectedSeq) {
        return null;
      }
    } while ((seq > expectedSeq) || 
      (!casConsumerIndex(cIndex, cIndex + 1L)));
    
    long offset = calcElementOffset(cIndex, mask);
    E e = UnsafeRefArrayAccess.lpElement(buffer, offset);
    UnsafeRefArrayAccess.soElement(buffer, offset, null);
    soSequence(sBuffer, seqOffset, cIndex + mask + 1L);
    return e;
  }
  
  public E relaxedPeek()
  {
    long currConsumerIndex = lvConsumerIndex();
    return UnsafeRefArrayAccess.lpElement(buffer, calcElementOffset(currConsumerIndex));
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c)
  {
    int capacity = capacity();
    int sum = 0;
    while (sum < capacity) {
      int drained = 0;
      if ((drained = drain(c, RECOMENDED_POLL_BATCH)) == 0) {
        break;
      }
      sum += drained;
    }
    return sum;
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s)
  {
    long result = 0L;
    int capacity = capacity();
    do {
      int filled = fill(s, RECOMENDED_OFFER_BATCH);
      if (filled == 0) {
        return (int)result;
      }
      result += filled;
    } while (result <= capacity);
    return (int)result;
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c, int limit)
  {
    long[] sBuffer = sequenceBuffer;
    long mask = this.mask;
    E[] buffer = this.buffer;
    




    for (int i = 0; i < limit; i++) { long cIndex;
      long seqOffset;
      long seq; long expectedSeq; do { cIndex = lvConsumerIndex();
        seqOffset = calcSequenceOffset(cIndex, mask);
        seq = lvSequence(sBuffer, seqOffset);
        expectedSeq = cIndex + 1L;
        if (seq < expectedSeq) {
          return i;
        }
      } while ((seq > expectedSeq) || 
        (!casConsumerIndex(cIndex, cIndex + 1L)));
      
      long offset = calcElementOffset(cIndex, mask);
      E e = UnsafeRefArrayAccess.lpElement(buffer, offset);
      UnsafeRefArrayAccess.soElement(buffer, offset, null);
      soSequence(sBuffer, seqOffset, cIndex + mask + 1L);
      c.accept(e);
    }
    return limit;
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s, int limit)
  {
    long[] sBuffer = sequenceBuffer;
    long mask = this.mask;
    E[] buffer = this.buffer;
    



    for (int i = 0; i < limit; i++) { long pIndex;
      long seqOffset;
      long seq; do { pIndex = lvProducerIndex();
        seqOffset = calcSequenceOffset(pIndex, mask);
        seq = lvSequence(sBuffer, seqOffset);
        if (seq < pIndex) {
          return i;
        }
      } while ((seq > pIndex) || 
        (!casProducerIndex(pIndex, pIndex + 1L)));
      
      UnsafeRefArrayAccess.soElement(buffer, calcElementOffset(pIndex, mask), s.get());
      soSequence(sBuffer, seqOffset, pIndex + 1L);
    }
    return limit;
  }
  


  public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit)
  {
    int idleCounter = 0;
    while (exit.keepRunning()) {
      if (drain(c, RECOMENDED_POLL_BATCH) == 0) {
        idleCounter = w.idle(idleCounter);
      }
      else {
        idleCounter = 0;
      }
    }
  }
  

  public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit)
  {
    int idleCounter = 0;
    while (exit.keepRunning()) {
      if (fill(s, RECOMENDED_OFFER_BATCH) == 0) {
        idleCounter = w.idle(idleCounter);
      }
      else {
        idleCounter = 0;
      }
    }
  }
}
