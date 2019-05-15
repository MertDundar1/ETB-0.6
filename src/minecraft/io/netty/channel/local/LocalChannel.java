package io.netty.channel.local;

import io.netty.channel.AbstractChannel;
import io.netty.channel.AbstractChannel.AbstractUnsafe;
import io.netty.channel.Channel;
import io.netty.channel.Channel.Unsafe;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

















public class LocalChannel
  extends AbstractChannel
{
  private static final ChannelMetadata METADATA = new ChannelMetadata(false);
  
  private static final int MAX_READER_STACK_DEPTH = 8;
  
  private final ChannelConfig config = new DefaultChannelConfig(this);
  private final Queue<Object> inboundBuffer = new ArrayDeque();
  private final Runnable readTask = new Runnable()
  {
    public void run() {
      ChannelPipeline pipeline = pipeline();
      for (;;) {
        Object m = inboundBuffer.poll();
        if (m == null) {
          break;
        }
        pipeline.fireChannelRead(m);
      }
      pipeline.fireChannelReadComplete();
    }
  };
  
  private final Runnable shutdownHook = new Runnable()
  {
    public void run() {
      unsafe().close(unsafe().voidPromise());
    }
  };
  private volatile int state;
  private volatile LocalChannel peer;
  private volatile LocalAddress localAddress;
  private volatile LocalAddress remoteAddress;
  private volatile ChannelPromise connectPromise;
  private volatile boolean readInProgress;
  private volatile boolean registerInProgress;
  
  public LocalChannel()
  {
    super(null);
  }
  
  LocalChannel(LocalServerChannel parent, LocalChannel peer) {
    super(parent);
    this.peer = peer;
    localAddress = parent.localAddress();
    remoteAddress = peer.localAddress();
  }
  
  public ChannelMetadata metadata()
  {
    return METADATA;
  }
  
  public ChannelConfig config()
  {
    return config;
  }
  
  public LocalServerChannel parent()
  {
    return (LocalServerChannel)super.parent();
  }
  
  public LocalAddress localAddress()
  {
    return (LocalAddress)super.localAddress();
  }
  
  public LocalAddress remoteAddress()
  {
    return (LocalAddress)super.remoteAddress();
  }
  
  public boolean isOpen()
  {
    return state < 3;
  }
  
  public boolean isActive()
  {
    return state == 2;
  }
  
  protected AbstractChannel.AbstractUnsafe newUnsafe()
  {
    return new LocalUnsafe(null);
  }
  
  protected boolean isCompatible(EventLoop loop)
  {
    return loop instanceof SingleThreadEventLoop;
  }
  
  protected SocketAddress localAddress0()
  {
    return localAddress;
  }
  
  protected SocketAddress remoteAddress0()
  {
    return remoteAddress;
  }
  




  protected void doRegister()
    throws Exception
  {
    if ((this.peer != null) && (parent() != null))
    {





      final LocalChannel peer = this.peer;
      registerInProgress = true;
      state = 2;
      
      remoteAddress = parent().localAddress();
      state = 2;
      




      peer.eventLoop().execute(new Runnable()
      {
        public void run() {
          registerInProgress = false;
          peer.pipeline().fireChannelActive();
          peerconnectPromise.setSuccess();
        }
      });
    }
    ((SingleThreadEventExecutor)eventLoop()).addShutdownHook(shutdownHook);
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception
  {
    this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
    

    state = 1;
  }
  
  protected void doDisconnect() throws Exception
  {
    doClose();
  }
  
  protected void doClose() throws Exception
  {
    if (state <= 2)
    {
      if (localAddress != null) {
        if (parent() == null) {
          LocalChannelRegistry.unregister(localAddress);
        }
        localAddress = null;
      }
      state = 3;
    }
    
    final LocalChannel peer = this.peer;
    if ((peer != null) && (peer.isActive()))
    {

      EventLoop eventLoop = peer.eventLoop();
      




      if ((eventLoop.inEventLoop()) && (!registerInProgress)) {
        peer.unsafe().close(unsafe().voidPromise());
      } else {
        peer.eventLoop().execute(new Runnable()
        {
          public void run() {
            peer.unsafe().close(unsafe().voidPromise());
          }
        });
      }
      this.peer = null;
    }
  }
  
  protected void doDeregister()
    throws Exception
  {
    ((SingleThreadEventExecutor)eventLoop()).removeShutdownHook(shutdownHook);
  }
  
  protected void doBeginRead() throws Exception
  {
    if (readInProgress) {
      return;
    }
    
    ChannelPipeline pipeline = pipeline();
    Queue<Object> inboundBuffer = this.inboundBuffer;
    if (inboundBuffer.isEmpty()) {
      readInProgress = true;
      return;
    }
    
    InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
    Integer stackDepth = Integer.valueOf(threadLocals.localChannelReaderStackDepth());
    if (stackDepth.intValue() < 8) {
      threadLocals.setLocalChannelReaderStackDepth(stackDepth.intValue() + 1);
      try {
        for (;;) {
          Object received = inboundBuffer.poll();
          if (received == null) {
            break;
          }
          pipeline.fireChannelRead(received);
        }
        pipeline.fireChannelReadComplete();
      } finally {
        threadLocals.setLocalChannelReaderStackDepth(stackDepth.intValue());
      }
    } else {
      eventLoop().execute(readTask);
    }
  }
  
  protected void doWrite(ChannelOutboundBuffer in) throws Exception
  {
    if (state < 2) {
      throw new NotYetConnectedException();
    }
    if (state > 2) {
      throw new ClosedChannelException();
    }
    
    final LocalChannel peer = this.peer;
    final ChannelPipeline peerPipeline = peer.pipeline();
    EventLoop peerLoop = peer.eventLoop();
    
    if (peerLoop == eventLoop()) {
      for (;;) {
        Object msg = in.current();
        if (msg == null) {
          break;
        }
        inboundBuffer.add(msg);
        ReferenceCountUtil.retain(msg);
        in.remove();
      }
      finishPeerRead(peer, peerPipeline);
    }
    else {
      final Object[] msgsCopy = new Object[in.size()];
      for (int i = 0; i < msgsCopy.length; i++) {
        msgsCopy[i] = ReferenceCountUtil.retain(in.current());
        in.remove();
      }
      
      peerLoop.execute(new Runnable()
      {
        public void run() {
          Collections.addAll(peerinboundBuffer, msgsCopy);
          LocalChannel.finishPeerRead(peer, peerPipeline);
        }
      });
    }
  }
  
  private static void finishPeerRead(LocalChannel peer, ChannelPipeline peerPipeline) {
    if (readInProgress) {
      readInProgress = false;
      for (;;) {
        Object received = inboundBuffer.poll();
        if (received == null) {
          break;
        }
        peerPipeline.fireChannelRead(received);
      }
      peerPipeline.fireChannelReadComplete();
    }
  }
  
  private class LocalUnsafe extends AbstractChannel.AbstractUnsafe { private LocalUnsafe() { super(); }
    

    public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    {
      if ((!promise.setUncancellable()) || (!ensureOpen(promise))) {
        return;
      }
      
      if (state == 2) {
        Exception cause = new AlreadyConnectedException();
        safeSetFailure(promise, cause);
        pipeline().fireExceptionCaught(cause);
        return;
      }
      
      if (connectPromise != null) {
        throw new ConnectionPendingException();
      }
      
      connectPromise = promise;
      
      if (state != 1)
      {
        if (localAddress == null) {
          localAddress = new LocalAddress(LocalChannel.this);
        }
      }
      
      if (localAddress != null) {
        try {
          doBind(localAddress);
        } catch (Throwable t) {
          safeSetFailure(promise, t);
          close(voidPromise());
          return;
        }
      }
      
      Channel boundChannel = LocalChannelRegistry.get(remoteAddress);
      if (!(boundChannel instanceof LocalServerChannel)) {
        Exception cause = new ChannelException("connection refused");
        safeSetFailure(promise, cause);
        close(voidPromise());
        return;
      }
      
      LocalServerChannel serverChannel = (LocalServerChannel)boundChannel;
      peer = serverChannel.serve(LocalChannel.this);
    }
  }
}
