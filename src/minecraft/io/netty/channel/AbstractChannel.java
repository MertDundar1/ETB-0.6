package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.OneTimeTask;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.RejectedExecutionException;

















public abstract class AbstractChannel
  extends DefaultAttributeMap
  implements Channel
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
  
  static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = new ClosedChannelException();
  static final NotYetConnectedException NOT_YET_CONNECTED_EXCEPTION = new NotYetConnectedException();
  private MessageSizeEstimator.Handle estimatorHandle;
  
  static { CLOSED_CHANNEL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    NOT_YET_CONNECTED_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
  }
  

  private final Channel parent;
  
  private final long hashCode = ThreadLocalRandom.current().nextLong();
  private final Channel.Unsafe unsafe;
  private final DefaultChannelPipeline pipeline;
  private final ChannelFuture succeededFuture = new SucceededChannelFuture(this, null);
  private final VoidChannelPromise voidPromise = new VoidChannelPromise(this, true);
  private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
  private final CloseFuture closeFuture = new CloseFuture(this);
  

  private volatile SocketAddress localAddress;
  
  private volatile SocketAddress remoteAddress;
  
  private volatile EventLoop eventLoop;
  
  private volatile boolean registered;
  
  private boolean strValActive;
  
  private String strVal;
  

  protected AbstractChannel(Channel parent)
  {
    this.parent = parent;
    unsafe = newUnsafe();
    pipeline = new DefaultChannelPipeline(this);
  }
  
  public boolean isWritable()
  {
    ChannelOutboundBuffer buf = unsafe.outboundBuffer();
    return (buf != null) && (buf.isWritable());
  }
  
  public Channel parent()
  {
    return parent;
  }
  
  public ChannelPipeline pipeline()
  {
    return pipeline;
  }
  
  public ByteBufAllocator alloc()
  {
    return config().getAllocator();
  }
  
  public EventLoop eventLoop()
  {
    EventLoop eventLoop = this.eventLoop;
    if (eventLoop == null) {
      throw new IllegalStateException("channel not registered to an event loop");
    }
    return eventLoop;
  }
  
  public SocketAddress localAddress()
  {
    SocketAddress localAddress = this.localAddress;
    if (localAddress == null) {
      try {
        this.localAddress = (localAddress = unsafe().localAddress());
      }
      catch (Throwable t) {
        return null;
      }
    }
    return localAddress;
  }
  
  protected void invalidateLocalAddress() {
    localAddress = null;
  }
  
  public SocketAddress remoteAddress()
  {
    SocketAddress remoteAddress = this.remoteAddress;
    if (remoteAddress == null) {
      try {
        this.remoteAddress = (remoteAddress = unsafe().remoteAddress());
      }
      catch (Throwable t) {
        return null;
      }
    }
    return remoteAddress;
  }
  


  protected void invalidateRemoteAddress()
  {
    remoteAddress = null;
  }
  
  public boolean isRegistered()
  {
    return registered;
  }
  
  public ChannelFuture bind(SocketAddress localAddress)
  {
    return pipeline.bind(localAddress);
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress)
  {
    return pipeline.connect(remoteAddress);
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress)
  {
    return pipeline.connect(remoteAddress, localAddress);
  }
  
  public ChannelFuture disconnect()
  {
    return pipeline.disconnect();
  }
  
  public ChannelFuture close()
  {
    return pipeline.close();
  }
  
  public ChannelFuture deregister()
  {
    return pipeline.deregister();
  }
  
  public Channel flush()
  {
    pipeline.flush();
    return this;
  }
  
  public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise)
  {
    return pipeline.bind(localAddress, promise);
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise)
  {
    return pipeline.connect(remoteAddress, promise);
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
  {
    return pipeline.connect(remoteAddress, localAddress, promise);
  }
  
  public ChannelFuture disconnect(ChannelPromise promise)
  {
    return pipeline.disconnect(promise);
  }
  
  public ChannelFuture close(ChannelPromise promise)
  {
    return pipeline.close(promise);
  }
  
  public ChannelFuture deregister(ChannelPromise promise)
  {
    return pipeline.deregister(promise);
  }
  
  public Channel read()
  {
    pipeline.read();
    return this;
  }
  
  public ChannelFuture write(Object msg)
  {
    return pipeline.write(msg);
  }
  
  public ChannelFuture write(Object msg, ChannelPromise promise)
  {
    return pipeline.write(msg, promise);
  }
  
  public ChannelFuture writeAndFlush(Object msg)
  {
    return pipeline.writeAndFlush(msg);
  }
  
  public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise)
  {
    return pipeline.writeAndFlush(msg, promise);
  }
  
  public ChannelPromise newPromise()
  {
    return new DefaultChannelPromise(this);
  }
  
  public ChannelProgressivePromise newProgressivePromise()
  {
    return new DefaultChannelProgressivePromise(this);
  }
  
  public ChannelFuture newSucceededFuture()
  {
    return succeededFuture;
  }
  
  public ChannelFuture newFailedFuture(Throwable cause)
  {
    return new FailedChannelFuture(this, null, cause);
  }
  
  public ChannelFuture closeFuture()
  {
    return closeFuture;
  }
  
  public Channel.Unsafe unsafe()
  {
    return unsafe;
  }
  








  public final int hashCode()
  {
    return (int)hashCode;
  }
  




  public final boolean equals(Object o)
  {
    return this == o;
  }
  
  public final int compareTo(Channel o)
  {
    if (this == o) {
      return 0;
    }
    
    long ret = hashCode - o.hashCode();
    if (ret > 0L) {
      return 1;
    }
    if (ret < 0L) {
      return -1;
    }
    
    ret = System.identityHashCode(this) - System.identityHashCode(o);
    if (ret != 0L) {
      return (int)ret;
    }
    

    throw new Error();
  }
  






  public String toString()
  {
    boolean active = isActive();
    if ((strValActive == active) && (strVal != null)) {
      return strVal;
    }
    
    SocketAddress remoteAddr = remoteAddress();
    SocketAddress localAddr = localAddress();
    if (remoteAddr != null) { SocketAddress dstAddr;
      SocketAddress srcAddr;
      SocketAddress dstAddr;
      if (parent == null) {
        SocketAddress srcAddr = localAddr;
        dstAddr = remoteAddr;
      } else {
        srcAddr = remoteAddr;
        dstAddr = localAddr;
      }
      strVal = String.format("[id: 0x%08x, %s %s %s]", new Object[] { Integer.valueOf((int)hashCode), srcAddr, active ? "=>" : ":>", dstAddr });
    } else if (localAddr != null) {
      strVal = String.format("[id: 0x%08x, %s]", new Object[] { Integer.valueOf((int)hashCode), localAddr });
    } else {
      strVal = String.format("[id: 0x%08x]", new Object[] { Integer.valueOf((int)hashCode) });
    }
    
    strValActive = active;
    return strVal;
  }
  
  public final ChannelPromise voidPromise()
  {
    return voidPromise;
  }
  
  final MessageSizeEstimator.Handle estimatorHandle() {
    if (estimatorHandle == null) {
      estimatorHandle = config().getMessageSizeEstimator().newHandle();
    }
    return estimatorHandle;
  }
  


  protected abstract class AbstractUnsafe
    implements Channel.Unsafe
  {
    private ChannelOutboundBuffer outboundBuffer = new ChannelOutboundBuffer(AbstractChannel.this);
    private boolean inFlush0;
    
    protected AbstractUnsafe() {}
    
    public final ChannelOutboundBuffer outboundBuffer() { return outboundBuffer; }
    

    public final SocketAddress localAddress()
    {
      return localAddress0();
    }
    
    public final SocketAddress remoteAddress()
    {
      return remoteAddress0();
    }
    
    public final void register(EventLoop eventLoop, final ChannelPromise promise)
    {
      if (eventLoop == null) {
        throw new NullPointerException("eventLoop");
      }
      if (isRegistered()) {
        promise.setFailure(new IllegalStateException("registered to an event loop already"));
        return;
      }
      if (!isCompatible(eventLoop)) {
        promise.setFailure(new IllegalStateException("incompatible event loop type: " + eventLoop.getClass().getName()));
        
        return;
      }
      
      AbstractChannel.this.eventLoop = eventLoop;
      
      if (eventLoop.inEventLoop()) {
        register0(promise);
      } else {
        try {
          eventLoop.execute(new OneTimeTask()
          {
            public void run() {
              AbstractChannel.AbstractUnsafe.this.register0(promise);
            }
          });
        } catch (Throwable t) {
          AbstractChannel.logger.warn("Force-closing a channel whose registration task was not accepted by an event loop: {}", AbstractChannel.this, t);
          

          closeForcibly();
          closeFuture.setClosed();
          safeSetFailure(promise, t);
        }
      }
    }
    
    private void register0(ChannelPromise promise)
    {
      try
      {
        if ((!promise.setUncancellable()) || (!ensureOpen(promise))) {
          return;
        }
        doRegister();
        registered = true;
        safeSetSuccess(promise);
        pipeline.fireChannelRegistered();
        if (isActive()) {
          pipeline.fireChannelActive();
        }
      }
      catch (Throwable t) {
        closeForcibly();
        closeFuture.setClosed();
        safeSetFailure(promise, t);
      }
    }
    
    public final void bind(SocketAddress localAddress, ChannelPromise promise)
    {
      if ((!promise.setUncancellable()) || (!ensureOpen(promise))) {
        return;
      }
      

      if ((!PlatformDependent.isWindows()) && (!PlatformDependent.isRoot()) && (Boolean.TRUE.equals(config().getOption(ChannelOption.SO_BROADCAST))) && ((localAddress instanceof InetSocketAddress)) && (!((InetSocketAddress)localAddress).getAddress().isAnyLocalAddress()))
      {




        AbstractChannel.logger.warn("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; binding to a non-wildcard address (" + localAddress + ") anyway as requested.");
      }
      



      boolean wasActive = isActive();
      try {
        doBind(localAddress);
      } catch (Throwable t) {
        safeSetFailure(promise, t);
        closeIfClosed();
        return;
      }
      
      if ((!wasActive) && (isActive())) {
        invokeLater(new OneTimeTask()
        {
          public void run() {
            pipeline.fireChannelActive();
          }
        });
      }
      
      safeSetSuccess(promise);
    }
    
    public final void disconnect(ChannelPromise promise)
    {
      if (!promise.setUncancellable()) {
        return;
      }
      
      boolean wasActive = isActive();
      try {
        doDisconnect();
      } catch (Throwable t) {
        safeSetFailure(promise, t);
        closeIfClosed();
        return;
      }
      
      if ((wasActive) && (!isActive())) {
        invokeLater(new OneTimeTask()
        {
          public void run() {
            pipeline.fireChannelInactive();
          }
        });
      }
      
      safeSetSuccess(promise);
      closeIfClosed();
    }
    
    public final void close(final ChannelPromise promise)
    {
      if (!promise.setUncancellable()) {
        return;
      }
      
      if (inFlush0) {
        invokeLater(new OneTimeTask()
        {
          public void run() {
            close(promise);
          }
        });
        return;
      }
      
      if (closeFuture.isDone())
      {
        safeSetSuccess(promise);
        return;
      }
      
      boolean wasActive = isActive();
      ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
      this.outboundBuffer = null;
      try
      {
        doClose();
        closeFuture.setClosed();
        safeSetSuccess(promise);
      } catch (Throwable t) {
        closeFuture.setClosed();
        safeSetFailure(promise, t);
      }
      
      try
      {
        outboundBuffer.failFlushed(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
        outboundBuffer.close(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
      }
      finally {
        if ((wasActive) && (!isActive())) {
          invokeLater(new OneTimeTask()
          {
            public void run() {
              pipeline.fireChannelInactive();
            }
          });
        }
        
        deregister(voidPromise());
      }
    }
    
    public final void closeForcibly()
    {
      try {
        doClose();
      } catch (Exception e) {
        AbstractChannel.logger.warn("Failed to close a channel.", e);
      }
    }
    
    public final void deregister(ChannelPromise promise)
    {
      if (!promise.setUncancellable()) {
        return;
      }
      
      if (!registered) {
        safeSetSuccess(promise);
        return;
      }
      try
      {
        doDeregister();
      } catch (Throwable t) {
        AbstractChannel.logger.warn("Unexpected exception occurred while deregistering a channel.", t);
      } finally {
        if (registered) {
          registered = false;
          invokeLater(new OneTimeTask()
          {
            public void run() {
              pipeline.fireChannelUnregistered();
            }
          });
          safeSetSuccess(promise);

        }
        else
        {
          safeSetSuccess(promise);
        }
      }
    }
    
    public final void beginRead()
    {
      if (!isActive()) {
        return;
      }
      try
      {
        doBeginRead();
      } catch (Exception e) {
        invokeLater(new OneTimeTask()
        {
          public void run() {
            pipeline.fireExceptionCaught(e);
          }
        });
        close(voidPromise());
      }
    }
    
    public final void write(Object msg, ChannelPromise promise)
    {
      ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
      if (outboundBuffer == null)
      {



        safeSetFailure(promise, AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
        
        ReferenceCountUtil.release(msg); return;
      }
      
      int size;
      try
      {
        msg = filterOutboundMessage(msg);
        size = estimatorHandle().size(msg);
        if (size < 0) {
          size = 0;
        }
      } catch (Throwable t) {
        safeSetFailure(promise, t);
        ReferenceCountUtil.release(msg);
        return;
      }
      
      outboundBuffer.addMessage(msg, size, promise);
    }
    
    public final void flush()
    {
      ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
      if (outboundBuffer == null) {
        return;
      }
      
      outboundBuffer.addFlush();
      flush0();
    }
    
    protected void flush0() {
      if (inFlush0)
      {
        return;
      }
      
      ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
      if ((outboundBuffer == null) || (outboundBuffer.isEmpty())) {
        return;
      }
      
      inFlush0 = true;
      

      if (!isActive()) {
        try {
          if (isOpen()) {
            outboundBuffer.failFlushed(AbstractChannel.NOT_YET_CONNECTED_EXCEPTION);
          } else {
            outboundBuffer.failFlushed(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
          }
        } finally {
          inFlush0 = false;
        }
        return;
      }
      try
      {
        doWrite(outboundBuffer);
      } catch (Throwable t) {
        outboundBuffer.failFlushed(t);
        if (((t instanceof IOException)) && (config().isAutoClose())) {
          close(voidPromise());
        }
      } finally {
        inFlush0 = false;
      }
    }
    
    public final ChannelPromise voidPromise()
    {
      return unsafeVoidPromise;
    }
    
    protected final boolean ensureOpen(ChannelPromise promise) {
      if (isOpen()) {
        return true;
      }
      
      safeSetFailure(promise, AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
      return false;
    }
    


    protected final void safeSetSuccess(ChannelPromise promise)
    {
      if ((!(promise instanceof VoidChannelPromise)) && (!promise.trySuccess())) {
        AbstractChannel.logger.warn("Failed to mark a promise as success because it is done already: {}", promise);
      }
    }
    


    protected final void safeSetFailure(ChannelPromise promise, Throwable cause)
    {
      if ((!(promise instanceof VoidChannelPromise)) && (!promise.tryFailure(cause))) {
        AbstractChannel.logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
      }
    }
    
    protected final void closeIfClosed() {
      if (isOpen()) {
        return;
      }
      close(voidPromise());
    }
    









    private void invokeLater(Runnable task)
    {
      try
      {
        eventLoop().execute(task);
      } catch (RejectedExecutionException e) {
        AbstractChannel.logger.warn("Can't invoke task later as EventLoop rejected it", e);
      }
    }
  }
  






























































  protected Object filterOutboundMessage(Object msg)
    throws Exception { return msg; }
  
  protected abstract AbstractUnsafe newUnsafe();
  
  protected abstract boolean isCompatible(EventLoop paramEventLoop);
  
  static final class CloseFuture extends DefaultChannelPromise { CloseFuture(AbstractChannel ch) { super(); }
    

    public ChannelPromise setSuccess()
    {
      throw new IllegalStateException();
    }
    
    public ChannelPromise setFailure(Throwable cause)
    {
      throw new IllegalStateException();
    }
    
    public boolean trySuccess()
    {
      throw new IllegalStateException();
    }
    
    public boolean tryFailure(Throwable cause)
    {
      throw new IllegalStateException();
    }
    
    boolean setClosed() {
      return super.trySuccess();
    }
  }
  
  protected abstract SocketAddress localAddress0();
  
  protected abstract SocketAddress remoteAddress0();
  
  protected void doRegister()
    throws Exception
  {}
  
  protected abstract void doBind(SocketAddress paramSocketAddress)
    throws Exception;
  
  protected abstract void doDisconnect()
    throws Exception;
  
  protected abstract void doClose()
    throws Exception;
  
  protected void doDeregister()
    throws Exception
  {}
  
  protected abstract void doBeginRead()
    throws Exception;
  
  protected abstract void doWrite(ChannelOutboundBuffer paramChannelOutboundBuffer)
    throws Exception;
}
