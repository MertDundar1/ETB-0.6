package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;











abstract class AtomicReferenceArrayQueue<E>
  extends AbstractQueue<E>
{
  protected final AtomicReferenceArray<E> buffer;
  protected final int mask;
  
  public AtomicReferenceArrayQueue(int capacity)
  {
    int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
    mask = (actualCapacity - 1);
    buffer = new AtomicReferenceArray(actualCapacity);
  }
  
  public Iterator<E> iterator() {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    while ((poll() != null) || (!isEmpty())) {}
  }
  
  protected final int calcElementOffset(long index, int mask) {
    return (int)index & mask;
  }
  
  protected final int calcElementOffset(long index) { return (int)index & mask; }
  
  protected final E lvElement(AtomicReferenceArray<E> buffer, int offset) {
    return buffer.get(offset);
  }
  
  protected final E lpElement(AtomicReferenceArray<E> buffer, int offset) { return buffer.get(offset); }
  
  protected final E lpElement(int offset) {
    return buffer.get(offset);
  }
  
  protected final void spElement(AtomicReferenceArray<E> buffer, int offset, E value) { buffer.lazySet(offset, value); }
  
  protected final void spElement(int offset, E value) {
    buffer.lazySet(offset, value);
  }
  
  protected final void soElement(AtomicReferenceArray<E> buffer, int offset, E value) { buffer.lazySet(offset, value); }
  
  protected final void soElement(int offset, E value) {
    buffer.lazySet(offset, value);
  }
  
  protected final void svElement(AtomicReferenceArray<E> buffer, int offset, E value) { buffer.set(offset, value); }
  
  protected final E lvElement(int offset) {
    return lvElement(buffer, offset);
  }
}
