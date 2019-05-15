package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.AbstractChannel.AbstractUnsafe;
import io.netty.channel.Channel;
import io.netty.channel.Channel.Unsafe;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator.Handle;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;





















abstract class AbstractHttp2StreamChannel
  extends AbstractChannel
{
  protected static final Object CLOSE_MESSAGE = new Object();
  private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
  private static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractHttp2StreamChannel.class, "doWrite(...)");
  


  private static final int ARBITRARY_MESSAGE_SIZE = 9;
  


  private final ChannelConfig config = new DefaultChannelConfig(this);
  private final Queue<Object> inboundBuffer = new ArrayDeque(4);
  private final Runnable fireChildReadCompleteTask = new Runnable()
  {
    public void run() {
      if (readInProgress) {
        readInProgress = false;
        unsafe().recvBufAllocHandle().readComplete();
        pipeline().fireChannelReadComplete();
      }
    }
  };
  

  private volatile int streamId = -1;
  private boolean closed;
  private boolean readInProgress;
  
  protected AbstractHttp2StreamChannel(Channel parent) {
    super(parent);
  }
  
  public ChannelMetadata metadata()
  {
    return METADATA;
  }
  
  public ChannelConfig config()
  {
    return config;
  }
  
  public boolean isOpen()
  {
    return !closed;
  }
  
  public boolean isActive()
  {
    return isOpen();
  }
  
  protected AbstractChannel.AbstractUnsafe newUnsafe()
  {
    return new Unsafe(null);
  }
  
  protected boolean isCompatible(EventLoop loop)
  {
    return true;
  }
  
  protected SocketAddress localAddress0()
  {
    return parent().localAddress();
  }
  
  protected SocketAddress remoteAddress0()
  {
    return parent().remoteAddress();
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception
  {
    throw new UnsupportedOperationException();
  }
  
  protected void doDisconnect() throws Exception
  {
    throw new UnsupportedOperationException();
  }
  
  protected void doClose() throws Exception
  {
    closed = true;
    while (!inboundBuffer.isEmpty()) {
      ReferenceCountUtil.release(inboundBuffer.poll());
    }
  }
  
  protected void doBeginRead()
  {
    if (readInProgress) {
      return;
    }
    
    RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
    allocHandle.reset(config());
    if (inboundBuffer.isEmpty()) {
      readInProgress = true;
    }
    else
    {
      do {
        Object m = inboundBuffer.poll();
        if (m == null) {
          break;
        }
        if (!doRead0(m, allocHandle))
        {
          return;
        }
      } while (allocHandle.continueReading());
      
      allocHandle.readComplete();
      pipeline().fireChannelReadComplete();
    }
  }
  
  protected final void doWrite(ChannelOutboundBuffer in) throws Exception {
    if (closed) {
      throw CLOSED_CHANNEL_EXCEPTION;
    }
    
    EventExecutor preferredExecutor = preferredEventExecutor();
    





    if (preferredExecutor.inEventLoop()) {
      for (;;) {
        Object msg = in.current();
        if (msg == null) {
          break;
        }
        try {
          doWrite(ReferenceCountUtil.retain(msg));
        }
        catch (Throwable t)
        {
          pipeline().fireExceptionCaught(t);
        }
        in.remove();
      }
      doWriteComplete();
    }
    else {
      final Object[] msgsCopy = new Object[in.size()];
      for (int i = 0; i < msgsCopy.length; i++) {
        msgsCopy[i] = ReferenceCountUtil.retain(in.current());
        in.remove();
      }
      
      preferredExecutor.execute(new Runnable()
      {
        public void run() {
          for (Object msg : msgsCopy) {
            try {
              doWrite(msg);
            } catch (Throwable t) {
              pipeline().fireExceptionCaught(t);
            }
          }
          doWriteComplete();
        }
      });
    }
  }
  




  protected abstract void doWrite(Object paramObject)
    throws Exception;
  




  protected abstract void doWriteComplete();
  



  protected abstract EventExecutor preferredEventExecutor();
  



  protected abstract void bytesConsumed(int paramInt);
  



  protected void fireChildRead(final Object msg)
  {
    if (eventLoop().inEventLoop()) {
      fireChildRead0(msg);
    } else {
      eventLoop().execute(new Runnable()
      {
        public void run() {
          AbstractHttp2StreamChannel.this.fireChildRead0(msg);
        }
      });
    }
  }
  
  private void fireChildRead0(Object msg) {
    if (closed) {
      ReferenceCountUtil.release(msg);
      return;
    }
    if (readInProgress) {
      assert (inboundBuffer.isEmpty());
      

      RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
      readInProgress = doRead0(ObjectUtil.checkNotNull(msg, "msg"), allocHandle);
      if (!allocHandle.continueReading()) {
        fireChildReadCompleteTask.run();
      }
    } else {
      inboundBuffer.add(msg);
    }
  }
  
  protected void fireChildReadComplete() {
    if (eventLoop().inEventLoop()) {
      fireChildReadCompleteTask.run();
    } else {
      eventLoop().execute(fireChildReadCompleteTask);
    }
  }
  


  protected void streamId(int streamId)
  {
    if (this.streamId != -1) {
      throw new IllegalStateException("Stream identifier may only be set once.");
    }
    this.streamId = ObjectUtil.checkPositiveOrZero(streamId, "streamId");
  }
  
  protected int streamId() {
    return streamId;
  }
  



  private boolean doRead0(Object msg, RecvByteBufAllocator.Handle allocHandle)
  {
    if (msg == CLOSE_MESSAGE) {
      allocHandle.readComplete();
      pipeline().fireChannelReadComplete();
      unsafe().close(voidPromise());
      return false;
    }
    int numBytesToBeConsumed = 0;
    if ((msg instanceof Http2DataFrame)) {
      Http2DataFrame data = (Http2DataFrame)msg;
      numBytesToBeConsumed = data.content().readableBytes() + data.padding();
      allocHandle.lastBytesRead(numBytesToBeConsumed);
    } else {
      allocHandle.lastBytesRead(9);
    }
    allocHandle.incMessagesRead(1);
    pipeline().fireChannelRead(msg);
    if (numBytesToBeConsumed != 0) {
      bytesConsumed(numBytesToBeConsumed);
    }
    return true;
  }
  
  private final class Unsafe extends AbstractChannel.AbstractUnsafe { private Unsafe() { super(); }
    
    public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    {
      promise.setFailure(new UnsupportedOperationException());
    }
  }
}
