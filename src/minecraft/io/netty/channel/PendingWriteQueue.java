package io.netty.channel;

import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;


















public final class PendingWriteQueue
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
  
  private final ChannelHandlerContext ctx;
  
  private final ChannelOutboundBuffer buffer;
  private final MessageSizeEstimator.Handle estimatorHandle;
  private PendingWrite head;
  private PendingWrite tail;
  private int size;
  
  public PendingWriteQueue(ChannelHandlerContext ctx)
  {
    if (ctx == null) {
      throw new NullPointerException("ctx");
    }
    this.ctx = ctx;
    buffer = ctx.channel().unsafe().outboundBuffer();
    estimatorHandle = ctx.channel().config().getMessageSizeEstimator().newHandle();
  }
  


  public boolean isEmpty()
  {
    assert (ctx.executor().inEventLoop());
    return head == null;
  }
  


  public int size()
  {
    assert (ctx.executor().inEventLoop());
    return size;
  }
  


  public void add(Object msg, ChannelPromise promise)
  {
    assert (ctx.executor().inEventLoop());
    if (msg == null) {
      throw new NullPointerException("msg");
    }
    if (promise == null) {
      throw new NullPointerException("promise");
    }
    int messageSize = estimatorHandle.size(msg);
    if (messageSize < 0)
    {
      messageSize = 0;
    }
    PendingWrite write = PendingWrite.newInstance(msg, messageSize, promise);
    PendingWrite currentTail = tail;
    if (currentTail == null) {
      tail = (this.head = write);
    } else {
      next = write;
      tail = write;
    }
    size += 1;
    buffer.incrementPendingOutboundBytes(size);
  }
  



  public void removeAndFailAll(Throwable cause)
  {
    assert (ctx.executor().inEventLoop());
    if (cause == null) {
      throw new NullPointerException("cause");
    }
    PendingWrite write = head;
    while (write != null) {
      PendingWrite next = next;
      ReferenceCountUtil.safeRelease(msg);
      ChannelPromise promise = promise;
      recycle(write);
      safeFail(promise, cause);
      write = next;
    }
    assertEmpty();
  }
  



  public void removeAndFail(Throwable cause)
  {
    assert (ctx.executor().inEventLoop());
    if (cause == null) {
      throw new NullPointerException("cause");
    }
    PendingWrite write = head;
    if (write == null) {
      return;
    }
    ReferenceCountUtil.safeRelease(msg);
    ChannelPromise promise = promise;
    safeFail(promise, cause);
    recycle(write);
  }
  






  public ChannelFuture removeAndWriteAll()
  {
    assert (ctx.executor().inEventLoop());
    PendingWrite write = head;
    if (write == null)
    {
      return null;
    }
    if (size == 1)
    {
      return removeAndWrite();
    }
    ChannelPromise p = ctx.newPromise();
    ChannelPromiseAggregator aggregator = new ChannelPromiseAggregator(p);
    while (write != null) {
      PendingWrite next = next;
      Object msg = msg;
      ChannelPromise promise = promise;
      recycle(write);
      ctx.write(msg, promise);
      aggregator.add(new ChannelPromise[] { promise });
      write = next;
    }
    assertEmpty();
    return p;
  }
  
  private void assertEmpty() {
    assert ((tail == null) && (head == null) && (size == 0));
  }
  






  public ChannelFuture removeAndWrite()
  {
    assert (ctx.executor().inEventLoop());
    PendingWrite write = head;
    if (write == null) {
      return null;
    }
    Object msg = msg;
    ChannelPromise promise = promise;
    recycle(write);
    return ctx.write(msg, promise);
  }
  





  public ChannelPromise remove()
  {
    assert (ctx.executor().inEventLoop());
    PendingWrite write = head;
    if (write == null) {
      return null;
    }
    ChannelPromise promise = promise;
    ReferenceCountUtil.safeRelease(msg);
    recycle(write);
    return promise;
  }
  


  public Object current()
  {
    assert (ctx.executor().inEventLoop());
    PendingWrite write = head;
    if (write == null) {
      return null;
    }
    return msg;
  }
  
  private void recycle(PendingWrite write) {
    PendingWrite next = next;
    
    buffer.decrementPendingOutboundBytes(size);
    write.recycle();
    size -= 1;
    if (next == null)
    {
      head = (this.tail = null);
      if ((!$assertionsDisabled) && (size != 0)) throw new AssertionError();
    } else {
      head = next;
      assert (size > 0);
    }
  }
  
  private static void safeFail(ChannelPromise promise, Throwable cause) {
    if ((!(promise instanceof VoidChannelPromise)) && (!promise.tryFailure(cause))) {
      logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
    }
  }
  


  static final class PendingWrite
  {
    private static final Recycler<PendingWrite> RECYCLER = new Recycler()
    {
      protected PendingWriteQueue.PendingWrite newObject(Recycler.Handle handle) {
        return new PendingWriteQueue.PendingWrite(handle, null);
      }
    };
    private final Recycler.Handle handle;
    private PendingWrite next;
    private long size;
    private ChannelPromise promise;
    private Object msg;
    
    private PendingWrite(Recycler.Handle handle)
    {
      this.handle = handle;
    }
    
    static PendingWrite newInstance(Object msg, int size, ChannelPromise promise) {
      PendingWrite write = (PendingWrite)RECYCLER.get();
      size = size;
      msg = msg;
      promise = promise;
      return write;
    }
    
    private void recycle() {
      size = 0L;
      next = null;
      msg = null;
      promise = null;
      RECYCLER.recycle(this, handle);
    }
  }
}
