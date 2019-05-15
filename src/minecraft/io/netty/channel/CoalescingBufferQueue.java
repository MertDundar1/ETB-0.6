package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;


























public final class CoalescingBufferQueue
{
  private final Channel channel;
  private final ArrayDeque<Object> bufAndListenerPairs;
  private int readableBytes;
  
  public CoalescingBufferQueue(Channel channel)
  {
    this(channel, 4);
  }
  
  public CoalescingBufferQueue(Channel channel, int initSize) {
    this.channel = ((Channel)ObjectUtil.checkNotNull(channel, "channel"));
    bufAndListenerPairs = new ArrayDeque(initSize);
  }
  


  public void add(ByteBuf buf)
  {
    add(buf, (ChannelFutureListener)null);
  }
  







  public void add(ByteBuf buf, ChannelPromise promise)
  {
    ObjectUtil.checkNotNull(promise, "promise");
    add(buf, promise.isVoid() ? null : new ChannelPromiseNotifier(new ChannelPromise[] { promise }));
  }
  







  public void add(ByteBuf buf, ChannelFutureListener listener)
  {
    ObjectUtil.checkNotNull(buf, "buf");
    if (readableBytes > Integer.MAX_VALUE - buf.readableBytes()) {
      throw new IllegalStateException("buffer queue length overflow: " + readableBytes + " + " + buf.readableBytes());
    }
    
    bufAndListenerPairs.add(buf);
    if (listener != null) {
      bufAndListenerPairs.add(listener);
    }
    readableBytes += buf.readableBytes();
  }
  









  public ByteBuf remove(int bytes, ChannelPromise aggregatePromise)
  {
    if (bytes < 0) {
      throw new IllegalArgumentException("bytes (expected >= 0): " + bytes);
    }
    ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
    

    if (bufAndListenerPairs.isEmpty()) {
      return Unpooled.EMPTY_BUFFER;
    }
    bytes = Math.min(bytes, readableBytes);
    
    ByteBuf toReturn = null;
    int originalBytes = bytes;
    for (;;) {
      Object entry = bufAndListenerPairs.poll();
      if (entry == null) {
        break;
      }
      if ((entry instanceof ChannelFutureListener)) {
        aggregatePromise.addListener((ChannelFutureListener)entry);
      }
      else {
        ByteBuf entryBuffer = (ByteBuf)entry;
        if (entryBuffer.readableBytes() > bytes)
        {
          bufAndListenerPairs.addFirst(entryBuffer);
          if (bytes <= 0)
            break;
          toReturn = compose(toReturn, entryBuffer.readRetainedSlice(bytes));
          bytes = 0; break;
        }
        

        toReturn = compose(toReturn, entryBuffer);
        bytes -= entryBuffer.readableBytes();
      }
    }
    readableBytes -= originalBytes - bytes;
    assert (readableBytes >= 0);
    return toReturn;
  }
  


  private ByteBuf compose(ByteBuf current, ByteBuf next)
  {
    if (current == null) {
      return next;
    }
    if ((current instanceof CompositeByteBuf)) {
      CompositeByteBuf composite = (CompositeByteBuf)current;
      composite.addComponent(true, next);
      return composite;
    }
    

    CompositeByteBuf composite = channel.alloc().compositeBuffer(bufAndListenerPairs.size() + 2);
    composite.addComponent(true, current);
    composite.addComponent(true, next);
    return composite;
  }
  


  public int readableBytes()
  {
    return readableBytes;
  }
  


  public boolean isEmpty()
  {
    return bufAndListenerPairs.isEmpty();
  }
  


  public void releaseAndFailAll(Throwable cause)
  {
    releaseAndCompleteAll(channel.newFailedFuture(cause));
  }
  
  private void releaseAndCompleteAll(ChannelFuture future) {
    readableBytes = 0;
    Throwable pending = null;
    for (;;) {
      Object entry = bufAndListenerPairs.poll();
      if (entry == null) {
        break;
      }
      try {
        if ((entry instanceof ByteBuf)) {
          ReferenceCountUtil.safeRelease(entry);
        } else {
          ((ChannelFutureListener)entry).operationComplete(future);
        }
      } catch (Throwable t) {
        pending = t;
      }
    }
    if (pending != null) {
      throw new IllegalStateException(pending);
    }
  }
  



  public void copyTo(CoalescingBufferQueue dest)
  {
    bufAndListenerPairs.addAll(bufAndListenerPairs);
    readableBytes += readableBytes;
  }
}
