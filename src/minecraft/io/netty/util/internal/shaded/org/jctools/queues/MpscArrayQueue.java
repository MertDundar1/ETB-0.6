package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

































































































































public class MpscArrayQueue<E>
  extends MpscArrayQueueConsumerField<E>
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
  
  public MpscArrayQueue(int capacity)
  {
    super(capacity);
  }
  







  public boolean offerIfBelowThreshold(E e, int threshold)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    long mask = this.mask;
    long capacity = mask + 1L;
    
    long producerLimit = lvProducerLimit();
    long pIndex;
    do {
      pIndex = lvProducerIndex();
      long available = producerLimit - pIndex;
      long size = capacity - available;
      if (size >= threshold) {
        long cIndex = lvConsumerIndex();
        size = pIndex - cIndex;
        if (size >= threshold) {
          return false;
        }
        

        producerLimit = cIndex + capacity;
        

        soProducerLimit(producerLimit);
      }
      
    } while (!casProducerIndex(pIndex, pIndex + 1L));
    





    long offset = calcElementOffset(pIndex, mask);
    UnsafeRefArrayAccess.soElement(buffer, offset, e);
    return true;
  }
  










  public boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    

    long mask = this.mask;
    long producerLimit = lvProducerLimit();
    long pIndex;
    do {
      pIndex = lvProducerIndex();
      if (pIndex >= producerLimit) {
        long cIndex = lvConsumerIndex();
        producerLimit = cIndex + mask + 1L;
        
        if (pIndex >= producerLimit) {
          return false;
        }
        


        soProducerLimit(producerLimit);
      }
      
    } while (!casProducerIndex(pIndex, pIndex + 1L));
    





    long offset = calcElementOffset(pIndex, mask);
    UnsafeRefArrayAccess.soElement(buffer, offset, e);
    return true;
  }
  





  public final int failFastOffer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    long mask = this.mask;
    long capacity = mask + 1L;
    long pIndex = lvProducerIndex();
    long producerLimit = lvProducerLimit();
    if (pIndex >= producerLimit) {
      long cIndex = lvConsumerIndex();
      producerLimit = cIndex + capacity;
      if (pIndex >= producerLimit) {
        return 1;
      }
      

      soProducerLimit(producerLimit);
    }
    


    if (!casProducerIndex(pIndex, pIndex + 1L)) {
      return -1;
    }
    

    long offset = calcElementOffset(pIndex, mask);
    UnsafeRefArrayAccess.soElement(buffer, offset, e);
    return 0;
  }
  









  public E poll()
  {
    long cIndex = lpConsumerIndex();
    long offset = calcElementOffset(cIndex);
    
    E[] buffer = this.buffer;
    

    E e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    if (null == e)
    {




      if (cIndex != lvProducerIndex()) {
        do {
          e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        } while (e == null);
      }
      else {
        return null;
      }
    }
    
    UnsafeRefArrayAccess.spElement(buffer, offset, null);
    soConsumerIndex(cIndex + 1L);
    return e;
  }
  










  public E peek()
  {
    E[] buffer = this.buffer;
    
    long cIndex = lpConsumerIndex();
    long offset = calcElementOffset(cIndex);
    E e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    if (null == e)
    {




      if (cIndex != lvProducerIndex()) {
        do {
          e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        } while (e == null);
      }
      else {
        return null;
      }
    }
    return e;
  }
  











  public int size()
  {
    long afterCIndex = lvConsumerIndex();
    for (;;) {
      long beforeCIndex = afterCIndex;
      long currentProducerIndex = lvProducerIndex();
      afterCIndex = lvConsumerIndex();
      if (beforeCIndex == afterCIndex) {
        return (int)(currentProducerIndex - afterCIndex);
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
    return offer(e);
  }
  
  public E relaxedPoll()
  {
    E[] buffer = this.buffer;
    long cIndex = lpConsumerIndex();
    long offset = calcElementOffset(cIndex);
    

    E e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    if (null == e) {
      return null;
    }
    
    UnsafeRefArrayAccess.spElement(buffer, offset, null);
    soConsumerIndex(cIndex + 1L);
    return e;
  }
  
  public E relaxedPeek()
  {
    E[] buffer = this.buffer;
    long mask = this.mask;
    long cIndex = lpConsumerIndex();
    return UnsafeRefArrayAccess.lvElement(buffer, calcElementOffset(cIndex, mask));
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c)
  {
    return drain(c, capacity());
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s)
  {
    long result = 0L;
    int capacity = capacity();
    do {
      int filled = fill(s, MpmcArrayQueue.RECOMENDED_OFFER_BATCH);
      if (filled == 0) {
        return (int)result;
      }
      result += filled;
    } while (result <= capacity);
    return (int)result;
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c, int limit)
  {
    E[] buffer = this.buffer;
    long mask = this.mask;
    long cIndex = lpConsumerIndex();
    
    for (int i = 0; i < limit; i++) {
      long index = cIndex + i;
      long offset = calcElementOffset(index, mask);
      E e = UnsafeRefArrayAccess.lvElement(buffer, offset);
      if (null == e) {
        return i;
      }
      UnsafeRefArrayAccess.soElement(buffer, offset, null);
      soConsumerIndex(index + 1L);
      c.accept(e);
    }
    return limit;
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s, int limit)
  {
    long mask = this.mask;
    long capacity = mask + 1L;
    long producerLimit = lvProducerLimit();
    
    int actualLimit = 0;
    long pIndex;
    do { pIndex = lvProducerIndex();
      long available = producerLimit - pIndex;
      if (available <= 0L) {
        long cIndex = lvConsumerIndex();
        producerLimit = cIndex + capacity;
        available = producerLimit - pIndex;
        if (available <= 0L) {
          return 0;
        }
        

        soProducerLimit(producerLimit);
      }
      
      actualLimit = Math.min((int)available, limit);
    } while (!casProducerIndex(pIndex, pIndex + actualLimit));
    
    E[] buffer = this.buffer;
    for (int i = 0; i < actualLimit; i++)
    {
      long offset = calcElementOffset(pIndex + i, mask);
      UnsafeRefArrayAccess.soElement(buffer, offset, s.get());
    }
    return actualLimit;
  }
  
  public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit)
  {
    E[] buffer = this.buffer;
    long mask = this.mask;
    long cIndex = lpConsumerIndex();
    
    int counter = 0;
    while (exit.keepRunning()) {
      for (int i = 0; i < 4096; i++) {
        long offset = calcElementOffset(cIndex, mask);
        E e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (null == e) {
          counter = w.idle(counter);
        }
        else {
          cIndex += 1L;
          counter = 0;
          UnsafeRefArrayAccess.soElement(buffer, offset, null);
          soConsumerIndex(cIndex);
          c.accept(e);
        }
      }
    }
  }
  
  public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
    int idleCounter = 0;
    while (exit.keepRunning()) {
      if (fill(s, MpmcArrayQueue.RECOMENDED_OFFER_BATCH) == 0) {
        idleCounter = w.idle(idleCounter);
      }
      else {
        idleCounter = 0;
      }
    }
  }
}
