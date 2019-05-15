package io.netty.util.internal;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;






















public final class PriorityQueue<T extends PriorityQueueNode<T>>
  extends AbstractQueue<T>
  implements Queue<T>
{
  private static final PriorityQueueNode[] EMPTY_QUEUE = new PriorityQueueNode[0];
  private T[] queue;
  private int size;
  
  public PriorityQueue() {
    this(8);
  }
  
  public PriorityQueue(int initialSize)
  {
    queue = ((PriorityQueueNode[])(initialSize != 0 ? new PriorityQueueNode[initialSize] : EMPTY_QUEUE));
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean isEmpty()
  {
    return size == 0;
  }
  
  public boolean contains(Object o)
  {
    if (!(o instanceof PriorityQueueNode)) {
      return false;
    }
    PriorityQueueNode<?> node = (PriorityQueueNode)o;
    int i = node.priorityQueueIndex();
    return (i >= 0) && (i < size) && (node.equals(queue[i]));
  }
  
  public void clear()
  {
    for (int i = 0; i < size; i++) {
      T node = queue[i];
      if (node != null) {
        node.priorityQueueIndex(-1);
        queue[i] = null;
      }
    }
    size = 0;
  }
  
  public boolean offer(T e)
  {
    ObjectUtil.checkNotNull(e, "e");
    if (e.priorityQueueIndex() != -1) {
      throw new IllegalArgumentException("e.priorityQueueIndex(): " + e.priorityQueueIndex() + " (expected: " + -1 + ")");
    }
    


    if (size >= queue.length)
    {

      queue = ((PriorityQueueNode[])Arrays.copyOf(queue, queue.length + (queue.length < 64 ? queue.length + 2 : queue.length >>> 1)));
    }
    


    bubbleUp(size++, e);
    return true;
  }
  
  public T poll()
  {
    if (size == 0) {
      return null;
    }
    T result = queue[0];
    result.priorityQueueIndex(-1);
    
    T last = queue[(--size)];
    queue[size] = null;
    if (size != 0) {
      bubbleDown(0, last);
    }
    
    return result;
  }
  
  public T peek()
  {
    return size == 0 ? null : queue[0];
  }
  
  public boolean remove(Object o)
  {
    if (!contains(o)) {
      return false;
    }
    
    T node = (PriorityQueueNode)o;
    int i = node.priorityQueueIndex();
    
    node.priorityQueueIndex(-1);
    if ((--size == 0) || (size == i))
    {
      queue[i] = null;
      return true;
    }
    

    T moved = queue[i] =  = queue[size];
    queue[size] = null;
    


    if (node.compareTo(moved) < 0) {
      bubbleDown(i, moved);
    } else {
      bubbleUp(i, moved);
    }
    return true;
  }
  
  public Object[] toArray()
  {
    return Arrays.copyOf(queue, size);
  }
  

  public <X> X[] toArray(X[] a)
  {
    if (a.length < size) {
      return (Object[])Arrays.copyOf(queue, size, a.getClass());
    }
    System.arraycopy(queue, 0, a, 0, size);
    if (a.length > size) {
      a[size] = null;
    }
    return a;
  }
  



  public Iterator<T> iterator()
  {
    return new PriorityQueueIterator(null);
  }
  
  private final class PriorityQueueIterator implements Iterator<T> {
    private int index;
    
    private PriorityQueueIterator() {}
    
    public boolean hasNext() { return index < size; }
    

    public T next()
    {
      if (index >= size) {
        throw new NoSuchElementException();
      }
      
      return queue[(index++)];
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("remove");
    }
  }
  
  private void bubbleDown(int k, T node) {
    int half = size >>> 1;
    while (k < half)
    {
      int iChild = (k << 1) + 1;
      T child = queue[iChild];
      

      int rightChild = iChild + 1;
      if ((rightChild < size) && (child.compareTo(queue[rightChild]) > 0)) {
        child = queue[(iChild = rightChild)];
      }
      

      if (node.compareTo(child) <= 0) {
        break;
      }
      

      queue[k] = child;
      child.priorityQueueIndex(k);
      

      k = iChild;
    }
    

    queue[k] = node;
    node.priorityQueueIndex(k);
  }
  
  private void bubbleUp(int k, T node) {
    while (k > 0) {
      int iParent = k - 1 >>> 1;
      T parent = queue[iParent];
      


      if (node.compareTo(parent) >= 0) {
        break;
      }
      

      queue[k] = parent;
      parent.priorityQueueIndex(k);
      

      k = iParent;
    }
    

    queue[k] = node;
    node.priorityQueueIndex(k);
  }
}
