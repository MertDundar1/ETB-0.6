package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.lang.reflect.Field;
import java.util.Iterator;
import sun.misc.Unsafe;











































public class MpscChunkedArrayQueue<E>
  extends MpscChunkedArrayQueueConsumerFields<E>
  implements MessagePassingQueue<E>, QueueProgressIndicators
{
  long p0;
  long p1;
  long p2;
  long p3;
  long p4;
  long p5;
  long p6;
  long p7;
  long p10;
  long p11;
  long p12;
  long p13;
  long p14;
  long p15;
  long p16;
  long p17;
  private static final long P_INDEX_OFFSET;
  private static final long C_INDEX_OFFSET;
  private static final long P_LIMIT_OFFSET;
  
  static
  {
    try
    {
      Field iField = MpscChunkedArrayQueueProducerFields.class.getDeclaredField("producerIndex");
      P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    try {
      Field iField = MpscChunkedArrayQueueConsumerFields.class.getDeclaredField("consumerIndex");
      C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    try {
      Field iField = MpscChunkedArrayQueueColdProducerFields.class.getDeclaredField("producerLimit");
      P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static final Object JUMP = new Object();
  
  public MpscChunkedArrayQueue(int maxCapacity) {
    this(Math.max(2, Pow2.roundToPowerOfTwo(maxCapacity / 8)), maxCapacity, false);
  }
  








  public MpscChunkedArrayQueue(int initialCapacity, int maxCapacity, boolean fixedChunkSize)
  {
    if (initialCapacity < 2) {
      throw new IllegalArgumentException("Initial capacity must be 2 or more");
    }
    if (maxCapacity < 4) {
      throw new IllegalArgumentException("Max capacity must be 4 or more");
    }
    if (Pow2.roundToPowerOfTwo(initialCapacity) >= Pow2.roundToPowerOfTwo(maxCapacity)) {
      throw new IllegalArgumentException("Initial capacity cannot exceed maximum capacity(both rounded up to a power of 2)");
    }
    

    int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
    
    long mask = p2capacity - 1 << 1;
    
    E[] buffer = CircularArrayOffsetCalculator.allocate(p2capacity + 1);
    producerBuffer = buffer;
    producerMask = mask;
    consumerBuffer = buffer;
    consumerMask = mask;
    maxQueueCapacity = (Pow2.roundToPowerOfTwo(maxCapacity) << 1);
    isFixedChunkSize = fixedChunkSize;
    soProducerLimit(mask);
  }
  
  public final Iterator<E> iterator()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    
    long pIndex;
    long mask;
    E[] buffer;
    for (;;)
    {
      long producerLimit = lvProducerLimit();
      pIndex = lvProducerIndex();
      
      if ((pIndex & 1L) != 1L)
      {




        mask = producerMask;
        buffer = producerBuffer;
        


        if (producerLimit <= pIndex) {
          int result = offerSlowPath(mask, buffer, pIndex, producerLimit);
          switch (result) {
          case 0: 
            break;
          case 1: 
            break;
          case 2: 
            return false;
          case 3: 
            resize(mask, buffer, pIndex, consumerIndex, maxQueueCapacity, e);
            return true;
          }
        }
        else {
          if (casProducerIndex(pIndex, pIndex + 2L))
            break;
        }
      }
    }
    long offset = modifiedCalcElementOffset(pIndex, mask);
    UnsafeRefArrayAccess.soElement(buffer, offset, e);
    return true;
  }
  
  private int offerSlowPath(long mask, E[] buffer, long pIndex, long producerLimit)
  {
    long consumerIndex = lvConsumerIndex();
    long maxQueueCapacity = this.maxQueueCapacity;
    long bufferCapacity = getCurrentBufferCapacity(mask, maxQueueCapacity);
    int result = 0;
    if (consumerIndex + bufferCapacity > pIndex) {
      if (!casProducerLimit(producerLimit, consumerIndex + bufferCapacity)) {
        result = 1;
      }
      
    }
    else if (consumerIndex == pIndex - maxQueueCapacity) {
      result = 2;

    }
    else if (casProducerIndex(pIndex, pIndex + 1L)) {
      result = 3;
    }
    else {
      result = 1;
    }
    return result;
  }
  



  private static long modifiedCalcElementOffset(long index, long mask)
  {
    return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT - 1);
  }
  






  public E poll()
  {
    E[] buffer = consumerBuffer;
    long index = consumerIndex;
    long mask = consumerMask;
    
    long offset = modifiedCalcElementOffset(index, mask);
    Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    if (e == null) {
      if (index != lvProducerIndex())
      {
        do
        {

          e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        } while (e == null);
      }
      else {
        return null;
      }
    }
    if (e == JUMP) {
      E[] nextBuffer = getNextBuffer(buffer, mask);
      return newBufferPoll(nextBuffer, index);
    }
    UnsafeRefArrayAccess.soElement(buffer, offset, null);
    soConsumerIndex(index + 2L);
    return e;
  }
  






  public E peek()
  {
    E[] buffer = consumerBuffer;
    long index = consumerIndex;
    long mask = consumerMask;
    
    long offset = modifiedCalcElementOffset(index, mask);
    Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    while ((e == null) && (index != lvProducerIndex()) && 
    

      ((e = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null)) {}
    

    if (e == JUMP) {
      return newBufferPeek(getNextBuffer(buffer, mask), index);
    }
    return e;
  }
  
  private E[] getNextBuffer(E[] buffer, long mask)
  {
    long nextArrayOffset = nextArrayOffset(mask);
    E[] nextBuffer = (Object[])UnsafeRefArrayAccess.lvElement(buffer, nextArrayOffset);
    UnsafeRefArrayAccess.soElement(buffer, nextArrayOffset, null);
    return nextBuffer;
  }
  
  private long nextArrayOffset(long mask) {
    return modifiedCalcElementOffset(mask + 2L, Long.MAX_VALUE);
  }
  
  private E newBufferPoll(E[] nextBuffer, long index) {
    long offsetInNew = newBufferAndOffset(nextBuffer, index);
    E n = UnsafeRefArrayAccess.lvElement(nextBuffer, offsetInNew);
    if (n == null) {
      throw new IllegalStateException("new buffer must have at least one element");
    }
    UnsafeRefArrayAccess.soElement(nextBuffer, offsetInNew, null);
    soConsumerIndex(index + 2L);
    return n;
  }
  
  private E newBufferPeek(E[] nextBuffer, long index) {
    long offsetInNew = newBufferAndOffset(nextBuffer, index);
    E n = UnsafeRefArrayAccess.lvElement(nextBuffer, offsetInNew);
    if (null == n) {
      throw new IllegalStateException("new buffer must have at least one element");
    }
    return n;
  }
  
  private long newBufferAndOffset(E[] nextBuffer, long index) {
    consumerBuffer = nextBuffer;
    consumerMask = (nextBuffer.length - 2 << 1);
    long offsetInNew = modifiedCalcElementOffset(index, consumerMask);
    return offsetInNew;
  }
  






  public final int size()
  {
    long after = lvConsumerIndex();
    for (;;) {
      long before = after;
      long currentProducerIndex = lvProducerIndex();
      after = lvConsumerIndex();
      if (before == after) {
        return (int)(currentProducerIndex - after) >> 1;
      }
    }
  }
  
  private long lvProducerIndex() {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
  }
  
  private long lvConsumerIndex() {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
  }
  
  private void soProducerIndex(long v) {
    UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, v);
  }
  
  private boolean casProducerIndex(long expect, long newValue) {
    return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
  }
  
  private void soConsumerIndex(long v) {
    UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, v);
  }
  
  private long lvProducerLimit() {
    return producerLimit;
  }
  
  private boolean casProducerLimit(long expect, long newValue) {
    return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, expect, newValue);
  }
  
  private void soProducerLimit(long v) {
    UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, v);
  }
  
  public long currentProducerIndex()
  {
    return lvProducerIndex();
  }
  
  public long currentConsumerIndex()
  {
    return lvConsumerIndex();
  }
  
  public int capacity()
  {
    return (int)(maxQueueCapacity / 2L);
  }
  
  public boolean relaxedOffer(E e)
  {
    return offer(e);
  }
  

  public E relaxedPoll()
  {
    E[] buffer = consumerBuffer;
    long index = consumerIndex;
    long mask = consumerMask;
    
    long offset = modifiedCalcElementOffset(index, mask);
    Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    if (e == null) {
      return null;
    }
    if (e == JUMP) {
      E[] nextBuffer = getNextBuffer(buffer, mask);
      return newBufferPoll(nextBuffer, index);
    }
    UnsafeRefArrayAccess.soElement(buffer, offset, null);
    soConsumerIndex(index + 2L);
    return e;
  }
  

  public E relaxedPeek()
  {
    E[] buffer = consumerBuffer;
    long index = consumerIndex;
    long mask = consumerMask;
    
    long offset = modifiedCalcElementOffset(index, mask);
    Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
    if (e == JUMP) {
      return newBufferPeek(getNextBuffer(buffer, mask), index);
    }
    return e;
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s, int batchSize)
  {
    long pIndex;
    long mask;
    E[] buffer;
    for (;;)
    {
      long producerLimit = lvProducerLimit();
      pIndex = lvProducerIndex();
      
      if ((pIndex & 1L) != 1L)
      {




        mask = producerMask;
        buffer = producerBuffer;
        


        long batchIndex = Math.min(producerLimit, pIndex + 2 * batchSize);
        int result;
        if ((pIndex == producerLimit) || (producerLimit < batchIndex))
          result = offerSlowPath(mask, buffer, pIndex, producerLimit);
        switch (result) {
        case 1: 
          break;
        case 2: 
          return 0;
        case 3: 
          resize(mask, buffer, pIndex, consumerIndex, maxQueueCapacity, s.get());
          return 1;
        


        default: 
          if (casProducerIndex(pIndex, batchIndex)) {
            int claimedSlots = (int)((batchIndex - pIndex) / 2L);
            break label164; }
          break; } } }
    label164:
    int claimedSlots;
    int i = 0;
    for (i = 0; i < claimedSlots; i++) {
      long offset = modifiedCalcElementOffset(pIndex + 2 * i, mask);
      UnsafeRefArrayAccess.soElement(buffer, offset, s.get());
    }
    return claimedSlots;
  }
  
  private void resize(long mask, E[] buffer, long pIndex, long consumerIndex, long maxQueueCapacity, E e)
  {
    int newBufferLength = getNextBufferCapacity(buffer, maxQueueCapacity);
    E[] newBuffer = CircularArrayOffsetCalculator.allocate(newBufferLength);
    
    producerBuffer = newBuffer;
    producerMask = (newBufferLength - 2 << 1);
    
    long offsetInOld = modifiedCalcElementOffset(pIndex, mask);
    long offsetInNew = modifiedCalcElementOffset(pIndex, producerMask);
    
    UnsafeRefArrayAccess.soElement(newBuffer, offsetInNew, e);
    UnsafeRefArrayAccess.soElement(buffer, nextArrayOffset(mask), newBuffer);
    long available = maxQueueCapacity - (pIndex - consumerIndex);
    
    if (available <= 0L) {
      throw new IllegalStateException();
    }
    
    soProducerLimit(pIndex + Math.min(mask, available));
    

    UnsafeRefArrayAccess.soElement(buffer, offsetInOld, JUMP);
    

    soProducerIndex(pIndex + 2L);
  }
  
  private int getNextBufferCapacity(E[] buffer, long maxQueueCapacity) {
    int newBufferLength = buffer.length;
    if (isFixedChunkSize) {
      newBufferLength = buffer.length;
    }
    else {
      if (buffer.length - 1 == maxQueueCapacity) {
        throw new IllegalStateException();
      }
      newBufferLength = 2 * buffer.length - 1;
    }
    return newBufferLength;
  }
  
  protected long getCurrentBufferCapacity(long mask, long maxQueueCapacity)
  {
    return (!isFixedChunkSize) && (mask + 2L == maxQueueCapacity) ? maxQueueCapacity : mask;
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
  



  public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit)
  {
    while (exit.keepRunning()) {
      while (fill(s, MpmcArrayQueue.RECOMENDED_OFFER_BATCH) != 0) {}
      

      int idleCounter = 0;
      while ((fill(s, MpmcArrayQueue.RECOMENDED_OFFER_BATCH) == 0) && (exit.keepRunning())) {
        idleCounter = w.idle(idleCounter);
      }
    }
  }
  

  public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit)
  {
    int idleCounter = 0;
    while (exit.keepRunning()) {
      E e = relaxedPoll();
      if (e == null) {
        idleCounter = w.idle(idleCounter);
      }
      else {
        idleCounter = 0;
        c.accept(e);
      }
    }
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c) {
    return drain(c, capacity());
  }
  


  public int drain(MessagePassingQueue.Consumer<E> c, int limit)
  {
    E m;
    
    for (int i = 0; 
        
        (i < limit) && ((m = relaxedPoll()) != null); i++) {
      c.accept(m);
    }
    return i;
  }
}
