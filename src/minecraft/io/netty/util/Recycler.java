package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;























public abstract class Recycler<T>
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
  
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(Integer.MIN_VALUE);
  private static final int OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
  

  private static final int DEFAULT_MAX_CAPACITY;
  

  static
  {
    int maxCapacity = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity.default", 0);
    if (maxCapacity <= 0)
    {
      maxCapacity = 262144;
    }
    
    DEFAULT_MAX_CAPACITY = maxCapacity;
    if (logger.isDebugEnabled())
      logger.debug("-Dio.netty.recycler.maxCapacity.default: {}", Integer.valueOf(DEFAULT_MAX_CAPACITY));
  }
  
  private static final int INITIAL_CAPACITY = Math.min(DEFAULT_MAX_CAPACITY, 256);
  
  private final int maxCapacity;
  
  private final FastThreadLocal<Stack<T>> threadLocal = new FastThreadLocal()
  {
    protected Recycler.Stack<T> initialValue() {
      return new Recycler.Stack(Recycler.this, Thread.currentThread(), maxCapacity);
    }
  };
  
  protected Recycler() {
    this(DEFAULT_MAX_CAPACITY);
  }
  
  protected Recycler(int maxCapacity) {
    this.maxCapacity = Math.max(0, maxCapacity);
  }
  
  public final T get()
  {
    Stack<T> stack = (Stack)threadLocal.get();
    DefaultHandle handle = stack.pop();
    if (handle == null) {
      handle = stack.newHandle();
      value = newObject(handle);
    }
    return value;
  }
  
  public final boolean recycle(T o, Handle handle) {
    DefaultHandle h = (DefaultHandle)handle;
    if (stack.parent != this) {
      return false;
    }
    if (o != value) {
      throw new IllegalArgumentException("o does not belong to handle");
    }
    h.recycle();
    return true;
  }
  
  protected abstract T newObject(Handle paramHandle);
  
  public static abstract interface Handle {}
  
  static final class DefaultHandle implements Recycler.Handle
  {
    private int lastRecycledId;
    private int recycleId;
    private Recycler.Stack<?> stack;
    private Object value;
    
    DefaultHandle(Recycler.Stack<?> stack) {
      this.stack = stack;
    }
    
    public void recycle() {
      Thread thread = Thread.currentThread();
      if (thread == stack.thread) {
        stack.push(this);
        return;
      }
      


      Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> delayedRecycled = (Map)Recycler.DELAYED_RECYCLED.get();
      Recycler.WeakOrderQueue queue = (Recycler.WeakOrderQueue)delayedRecycled.get(stack);
      if (queue == null) {
        delayedRecycled.put(stack, queue = new Recycler.WeakOrderQueue(stack, thread));
      }
      queue.add(this);
    }
  }
  
  private static final FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED = new FastThreadLocal()
  {
    protected Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> initialValue()
    {
      return new WeakHashMap();
    }
  };
  
  private static final class WeakOrderQueue {
    private static final int LINK_CAPACITY = 16;
    private Link head;
    private Link tail;
    private WeakOrderQueue next;
    private final WeakReference<Thread> owner;
    
    private static final class Link extends AtomicInteger {
      private final Recycler.DefaultHandle[] elements = new Recycler.DefaultHandle[16];
      
      private int readIndex;
      
      private Link next;
      

      private Link() {}
    }
    

    private final int id = Recycler.ID_GENERATOR.getAndIncrement();
    
    WeakOrderQueue(Recycler.Stack<?> stack, Thread thread) {
      head = (this.tail = new Link(null));
      owner = new WeakReference(thread);
      synchronized (stack) {
        next = head;
        head = this;
      }
    }
    
    void add(Recycler.DefaultHandle handle) {
      Recycler.DefaultHandle.access$702(handle, id);
      
      Link tail = this.tail;
      int writeIndex;
      if ((writeIndex = tail.get()) == 16) {
        this.tail = (tail = next = new Link(null));
        writeIndex = tail.get();
      }
      elements[writeIndex] = handle;
      Recycler.DefaultHandle.access$202(handle, null);
      

      tail.lazySet(writeIndex + 1);
    }
    
    boolean hasFinalData() {
      return tail.readIndex != tail.get();
    }
    


    boolean transfer(Recycler.Stack<?> to)
    {
      Link head = this.head;
      if (head == null) {
        return false;
      }
      
      if (readIndex == 16) {
        if (next == null) {
          return false;
        }
        this.head = (head = next);
      }
      
      int start = readIndex;
      int end = head.get();
      if (start == end) {
        return false;
      }
      
      int count = end - start;
      if (size + count > elements.length) {
        elements = ((Recycler.DefaultHandle[])Arrays.copyOf(elements, (size + count) * 2));
      }
      
      Recycler.DefaultHandle[] src = elements;
      Recycler.DefaultHandle[] trg = elements;
      int size = size;
      while (start < end) {
        Recycler.DefaultHandle element = src[start];
        if (Recycler.DefaultHandle.access$1300(element) == 0) {
          Recycler.DefaultHandle.access$1302(element, Recycler.DefaultHandle.access$700(element));
        } else if (Recycler.DefaultHandle.access$1300(element) != Recycler.DefaultHandle.access$700(element)) {
          throw new IllegalStateException("recycled already");
        }
        Recycler.DefaultHandle.access$202(element, to);
        trg[(size++)] = element;
        src[(start++)] = null;
      }
      size = size;
      
      if ((end == 16) && (next != null)) {
        this.head = next;
      }
      
      readIndex = end;
      return true;
    }
  }
  

  static final class Stack<T>
  {
    final Recycler<T> parent;
    
    final Thread thread;
    
    private Recycler.DefaultHandle[] elements;
    private final int maxCapacity;
    private int size;
    private volatile Recycler.WeakOrderQueue head;
    private Recycler.WeakOrderQueue cursor;
    private Recycler.WeakOrderQueue prev;
    
    Stack(Recycler<T> parent, Thread thread, int maxCapacity)
    {
      this.parent = parent;
      this.thread = thread;
      this.maxCapacity = maxCapacity;
      elements = new Recycler.DefaultHandle[Recycler.INITIAL_CAPACITY];
    }
    
    Recycler.DefaultHandle pop() {
      int size = this.size;
      if (size == 0) {
        if (!scavenge()) {
          return null;
        }
        size = this.size;
      }
      size--;
      Recycler.DefaultHandle ret = elements[size];
      if (Recycler.DefaultHandle.access$700(ret) != Recycler.DefaultHandle.access$1300(ret)) {
        throw new IllegalStateException("recycled multiple times");
      }
      Recycler.DefaultHandle.access$1302(ret, 0);
      Recycler.DefaultHandle.access$702(ret, 0);
      this.size = size;
      return ret;
    }
    
    boolean scavenge()
    {
      if (scavengeSome()) {
        return true;
      }
      

      prev = null;
      cursor = head;
      return false;
    }
    
    boolean scavengeSome() {
      boolean success = false;
      Recycler.WeakOrderQueue cursor = this.cursor;Recycler.WeakOrderQueue prev = this.prev;
      while (cursor != null) {
        if (cursor.transfer(this)) {
          success = true;
          break;
        }
        Recycler.WeakOrderQueue next = Recycler.WeakOrderQueue.access$1500(cursor);
        if (Recycler.WeakOrderQueue.access$1600(cursor).get() == null)
        {


          if (cursor.hasFinalData()) {
            for (;;) {
              if (!cursor.transfer(this)) {
                break;
              }
            }
          }
          if (prev != null) {
            Recycler.WeakOrderQueue.access$1502(prev, next);
          }
        } else {
          prev = cursor;
        }
        cursor = next;
      }
      this.prev = prev;
      this.cursor = cursor;
      return success;
    }
    
    void push(Recycler.DefaultHandle item) {
      if ((Recycler.DefaultHandle.access$1300(item) | Recycler.DefaultHandle.access$700(item)) != 0) {
        throw new IllegalStateException("recycled already");
      }
      Recycler.DefaultHandle.access$1302(item, Recycler.DefaultHandle.access$702(item, Recycler.OWN_THREAD_ID));
      
      int size = this.size;
      if (size == elements.length) {
        if (size == maxCapacity)
        {
          return;
        }
        elements = ((Recycler.DefaultHandle[])Arrays.copyOf(elements, size << 1));
      }
      
      elements[size] = item;
      this.size = (size + 1);
    }
    
    Recycler.DefaultHandle newHandle() {
      return new Recycler.DefaultHandle(this);
    }
  }
}
