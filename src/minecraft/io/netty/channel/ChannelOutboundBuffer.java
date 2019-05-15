package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;













































public final class ChannelOutboundBuffer
{
  private static final InternalLogger logger;
  private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS;
  private final Channel channel;
  private Entry flushedEntry;
  private Entry unflushedEntry;
  private Entry tailEntry;
  private int flushed;
  private int nioBufferCount;
  private long nioBufferSize;
  private boolean inFail;
  private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER;
  private volatile long totalPendingSize;
  private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> WRITABLE_UPDATER;
  private volatile int writable = 1;
  
  static
  {
    logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
    
    NIO_BUFFERS = new FastThreadLocal()
    {
      protected ByteBuffer[] initialValue() throws Exception {
        return new ByteBuffer['Ѐ'];














      }
      















    };
    AtomicIntegerFieldUpdater<ChannelOutboundBuffer> writableUpdater = PlatformDependent.newAtomicIntegerFieldUpdater(ChannelOutboundBuffer.class, "writable");
    
    if (writableUpdater == null) {
      writableUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "writable");
    }
    WRITABLE_UPDATER = writableUpdater;
    
    AtomicLongFieldUpdater<ChannelOutboundBuffer> pendingSizeUpdater = PlatformDependent.newAtomicLongFieldUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
    
    if (pendingSizeUpdater == null) {
      pendingSizeUpdater = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
    }
    TOTAL_PENDING_SIZE_UPDATER = pendingSizeUpdater;
  }
  
  ChannelOutboundBuffer(AbstractChannel channel) {
    this.channel = channel;
  }
  



  public void addMessage(Object msg, int size, ChannelPromise promise)
  {
    Entry entry = Entry.newInstance(msg, size, total(msg), promise);
    if (tailEntry == null) {
      flushedEntry = null;
      tailEntry = entry;
    } else {
      Entry tail = tailEntry;
      next = entry;
      tailEntry = entry;
    }
    if (unflushedEntry == null) {
      unflushedEntry = entry;
    }
    


    incrementPendingOutboundBytes(size);
  }
  







  public void addFlush()
  {
    Entry entry = unflushedEntry;
    if (entry != null) {
      if (flushedEntry == null)
      {
        flushedEntry = entry;
      }
      do {
        flushed += 1;
        if (!promise.setUncancellable())
        {
          int pending = entry.cancel();
          decrementPendingOutboundBytes(pending);
        }
        entry = next;
      } while (entry != null);
      

      unflushedEntry = null;
    }
  }
  



  void incrementPendingOutboundBytes(long size)
  {
    if (size == 0L) {
      return;
    }
    
    long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
    if ((newWriteBufferSize > channel.config().getWriteBufferHighWaterMark()) && 
      (WRITABLE_UPDATER.compareAndSet(this, 1, 0))) {
      channel.pipeline().fireChannelWritabilityChanged();
    }
  }
  




  void decrementPendingOutboundBytes(long size)
  {
    if (size == 0L) {
      return;
    }
    
    long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
    if (((newWriteBufferSize == 0L) || (newWriteBufferSize < channel.config().getWriteBufferLowWaterMark())) && 
      (WRITABLE_UPDATER.compareAndSet(this, 0, 1))) {
      channel.pipeline().fireChannelWritabilityChanged();
    }
  }
  
  private static long total(Object msg)
  {
    if ((msg instanceof ByteBuf)) {
      return ((ByteBuf)msg).readableBytes();
    }
    if ((msg instanceof FileRegion)) {
      return ((FileRegion)msg).count();
    }
    if ((msg instanceof ByteBufHolder)) {
      return ((ByteBufHolder)msg).content().readableBytes();
    }
    return -1L;
  }
  


  public Object current()
  {
    Entry entry = flushedEntry;
    if (entry == null) {
      return null;
    }
    
    return msg;
  }
  


  public void progress(long amount)
  {
    Entry e = flushedEntry;
    assert (e != null);
    ChannelPromise p = promise;
    if ((p instanceof ChannelProgressivePromise)) {
      long progress = progress + amount;
      progress = progress;
      ((ChannelProgressivePromise)p).tryProgress(progress, total);
    }
  }
  




  public boolean remove()
  {
    Entry e = flushedEntry;
    if (e == null) {
      return false;
    }
    Object msg = msg;
    
    ChannelPromise promise = promise;
    int size = pendingSize;
    
    removeEntry(e);
    
    if (!cancelled)
    {
      ReferenceCountUtil.safeRelease(msg);
      safeSuccess(promise);
      decrementPendingOutboundBytes(size);
    }
    

    e.recycle();
    
    return true;
  }
  




  public boolean remove(Throwable cause)
  {
    Entry e = flushedEntry;
    if (e == null) {
      return false;
    }
    Object msg = msg;
    
    ChannelPromise promise = promise;
    int size = pendingSize;
    
    removeEntry(e);
    
    if (!cancelled)
    {
      ReferenceCountUtil.safeRelease(msg);
      
      safeFail(promise, cause);
      decrementPendingOutboundBytes(size);
    }
    

    e.recycle();
    
    return true;
  }
  
  private void removeEntry(Entry e) {
    if (--flushed == 0)
    {
      flushedEntry = null;
      if (e == tailEntry) {
        tailEntry = null;
        unflushedEntry = null;
      }
    } else {
      flushedEntry = next;
    }
  }
  


  public void removeBytes(long writtenBytes)
  {
    for (;;)
    {
      Object msg = current();
      if (!(msg instanceof ByteBuf)) {
        if (($assertionsDisabled) || (writtenBytes == 0L)) break; throw new AssertionError();
      }
      

      ByteBuf buf = (ByteBuf)msg;
      int readerIndex = buf.readerIndex();
      int readableBytes = buf.writerIndex() - readerIndex;
      
      if (readableBytes <= writtenBytes) {
        if (writtenBytes != 0L) {
          progress(readableBytes);
          writtenBytes -= readableBytes;
        }
        remove();
      } else {
        if (writtenBytes == 0L) break;
        buf.readerIndex(readerIndex + (int)writtenBytes);
        progress(writtenBytes); break;
      }
    }
  }
  











  public ByteBuffer[] nioBuffers()
  {
    long nioBufferSize = 0L;
    int nioBufferCount = 0;
    InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
    ByteBuffer[] nioBuffers = (ByteBuffer[])NIO_BUFFERS.get(threadLocalMap);
    Entry entry = flushedEntry;
    while ((isFlushedEntry(entry)) && ((msg instanceof ByteBuf))) {
      if (!cancelled) {
        ByteBuf buf = (ByteBuf)msg;
        int readerIndex = buf.readerIndex();
        int readableBytes = buf.writerIndex() - readerIndex;
        
        if (readableBytes > 0) {
          nioBufferSize += readableBytes;
          int count = count;
          if (count == -1)
          {
            count = (count = buf.nioBufferCount());
          }
          int neededSpace = nioBufferCount + count;
          if (neededSpace > nioBuffers.length) {
            nioBuffers = expandNioBufferArray(nioBuffers, neededSpace, nioBufferCount);
            NIO_BUFFERS.set(threadLocalMap, nioBuffers);
          }
          if (count == 1) {
            ByteBuffer nioBuf = buf;
            if (nioBuf == null)
            {

              buf = (nioBuf = buf.internalNioBuffer(readerIndex, readableBytes));
            }
            nioBuffers[(nioBufferCount++)] = nioBuf;
          } else {
            ByteBuffer[] nioBufs = bufs;
            if (nioBufs == null)
            {

              bufs = (nioBufs = buf.nioBuffers());
            }
            nioBufferCount = fillBufferArray(nioBufs, nioBuffers, nioBufferCount);
          }
        }
      }
      entry = next;
    }
    this.nioBufferCount = nioBufferCount;
    this.nioBufferSize = nioBufferSize;
    
    return nioBuffers;
  }
  
  private static int fillBufferArray(ByteBuffer[] nioBufs, ByteBuffer[] nioBuffers, int nioBufferCount) {
    for (ByteBuffer nioBuf : nioBufs) {
      if (nioBuf == null) {
        break;
      }
      nioBuffers[(nioBufferCount++)] = nioBuf;
    }
    return nioBufferCount;
  }
  
  private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] array, int neededSpace, int size) {
    int newCapacity = array.length;
    
    do
    {
      newCapacity <<= 1;
      
      if (newCapacity < 0) {
        throw new IllegalStateException();
      }
      
    } while (neededSpace > newCapacity);
    
    ByteBuffer[] newArray = new ByteBuffer[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    
    return newArray;
  }
  




  public int nioBufferCount()
  {
    return nioBufferCount;
  }
  




  public long nioBufferSize()
  {
    return nioBufferSize;
  }
  
  boolean isWritable() {
    return writable != 0;
  }
  


  public int size()
  {
    return flushed;
  }
  



  public boolean isEmpty()
  {
    return flushed == 0;
  }
  




  void failFlushed(Throwable cause)
  {
    if (inFail) {
      return;
    }
    try
    {
      inFail = true;
      for (;;) {
        if (!remove(cause)) {
          break;
        }
      }
    } finally {
      inFail = false;
    }
  }
  
  void close(final ClosedChannelException cause) {
    if (inFail) {
      channel.eventLoop().execute(new Runnable()
      {
        public void run() {
          close(cause);
        }
      });
      return;
    }
    
    inFail = true;
    
    if (channel.isOpen()) {
      throw new IllegalStateException("close() must be invoked after the channel is closed.");
    }
    
    if (!isEmpty()) {
      throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
    }
    
    try
    {
      Entry e = unflushedEntry;
      while (e != null)
      {
        int size = pendingSize;
        TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
        
        if (!cancelled) {
          ReferenceCountUtil.safeRelease(msg);
          safeFail(promise, cause);
        }
        e = e.recycleAndGetNext();
      }
    } finally {
      inFail = false;
    }
  }
  
  private static void safeSuccess(ChannelPromise promise) {
    if ((!(promise instanceof VoidChannelPromise)) && (!promise.trySuccess())) {
      logger.warn("Failed to mark a promise as success because it is done already: {}", promise);
    }
  }
  
  private static void safeFail(ChannelPromise promise, Throwable cause) {
    if ((!(promise instanceof VoidChannelPromise)) && (!promise.tryFailure(cause))) {
      logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
    }
  }
  




  public long totalPendingWriteBytes()
  {
    return totalPendingSize;
  }
  



  public void forEachFlushedMessage(MessageProcessor processor)
    throws Exception
  {
    if (processor == null) {
      throw new NullPointerException("processor");
    }
    
    Entry entry = flushedEntry;
    if (entry == null) {
      return;
    }
    do
    {
      if ((!cancelled) && 
        (!processor.processMessage(msg))) {
        return;
      }
      
      entry = next;
    } while (isFlushedEntry(entry));
  }
  
  private boolean isFlushedEntry(Entry e) {
    return (e != null) && (e != unflushedEntry);
  }
  


  @Deprecated
  public void recycle() {}
  


  static final class Entry
  {
    private static final Recycler<Entry> RECYCLER = new Recycler()
    {
      protected ChannelOutboundBuffer.Entry newObject(Recycler.Handle handle) {
        return new ChannelOutboundBuffer.Entry(handle, null);
      }
    };
    
    private final Recycler.Handle handle;
    Entry next;
    Object msg;
    ByteBuffer[] bufs;
    ByteBuffer buf;
    ChannelPromise promise;
    long progress;
    long total;
    int pendingSize;
    int count = -1;
    boolean cancelled;
    
    private Entry(Recycler.Handle handle) {
      this.handle = handle;
    }
    
    static Entry newInstance(Object msg, int size, long total, ChannelPromise promise) {
      Entry entry = (Entry)RECYCLER.get();
      msg = msg;
      pendingSize = size;
      total = total;
      promise = promise;
      return entry;
    }
    
    int cancel() {
      if (!cancelled) {
        cancelled = true;
        int pSize = pendingSize;
        

        ReferenceCountUtil.safeRelease(msg);
        msg = Unpooled.EMPTY_BUFFER;
        
        pendingSize = 0;
        total = 0L;
        progress = 0L;
        bufs = null;
        buf = null;
        return pSize;
      }
      return 0;
    }
    
    void recycle() {
      next = null;
      bufs = null;
      buf = null;
      msg = null;
      promise = null;
      progress = 0L;
      total = 0L;
      pendingSize = 0;
      count = -1;
      cancelled = false;
      RECYCLER.recycle(this, handle);
    }
    
    Entry recycleAndGetNext() {
      Entry next = this.next;
      recycle();
      return next;
    }
  }
  
  public static abstract interface MessageProcessor
  {
    public abstract boolean processMessage(Object paramObject)
      throws Exception;
  }
}
