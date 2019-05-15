package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator.Handle;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


















public final class EpollSocketChannel
  extends AbstractEpollChannel
  implements SocketChannel
{
  private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
  
  private final EpollSocketChannelConfig config;
  
  private ChannelPromise connectPromise;
  
  private ScheduledFuture<?> connectTimeoutFuture;
  
  private SocketAddress requestedRemoteAddress;
  
  private volatile InetSocketAddress local;
  
  private volatile InetSocketAddress remote;
  
  private volatile boolean inputShutdown;
  
  private volatile boolean outputShutdown;
  
  EpollSocketChannel(Channel parent, int fd)
  {
    super(parent, fd, 1, true);
    config = new EpollSocketChannelConfig(this);
    

    remote = Native.remoteAddress(fd);
    local = Native.localAddress(fd);
  }
  
  public EpollSocketChannel() {
    super(Native.socketStreamFd(), 1);
    config = new EpollSocketChannelConfig(this);
  }
  
  protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe()
  {
    return new EpollSocketUnsafe();
  }
  
  protected SocketAddress localAddress0()
  {
    return local;
  }
  
  protected SocketAddress remoteAddress0()
  {
    return remote;
  }
  
  protected void doBind(SocketAddress local) throws Exception
  {
    InetSocketAddress localAddress = (InetSocketAddress)local;
    Native.bind(fd, localAddress.getAddress(), localAddress.getPort());
    this.local = Native.localAddress(fd);
  }
  


  private boolean writeBytes(ChannelOutboundBuffer in, ByteBuf buf)
    throws Exception
  {
    int readableBytes = buf.readableBytes();
    if (readableBytes == 0) {
      in.remove();
      return true;
    }
    
    boolean done = false;
    long writtenBytes = 0L;
    if (buf.hasMemoryAddress()) {
      long memoryAddress = buf.memoryAddress();
      int readerIndex = buf.readerIndex();
      int writerIndex = buf.writerIndex();
      for (;;) {
        int localFlushedAmount = Native.writeAddress(fd, memoryAddress, readerIndex, writerIndex);
        if (localFlushedAmount > 0) {
          writtenBytes += localFlushedAmount;
          if (writtenBytes == readableBytes) {
            done = true;
            break;
          }
          readerIndex += localFlushedAmount;
        }
        else {
          setEpollOut();
          break;
        }
      }
      
      in.removeBytes(writtenBytes);
      return done; }
    if (buf.nioBufferCount() == 1) {
      int readerIndex = buf.readerIndex();
      ByteBuffer nioBuf = buf.internalNioBuffer(readerIndex, buf.readableBytes());
      for (;;) {
        int pos = nioBuf.position();
        int limit = nioBuf.limit();
        int localFlushedAmount = Native.write(fd, nioBuf, pos, limit);
        if (localFlushedAmount > 0) {
          nioBuf.position(pos + localFlushedAmount);
          writtenBytes += localFlushedAmount;
          if (writtenBytes == readableBytes) {
            done = true;
            break;
          }
        }
        else {
          setEpollOut();
          break;
        }
      }
      
      in.removeBytes(writtenBytes);
      return done;
    }
    ByteBuffer[] nioBuffers = buf.nioBuffers();
    return writeBytesMultiple(in, nioBuffers, nioBuffers.length, readableBytes);
  }
  
  private boolean writeBytesMultiple(ChannelOutboundBuffer in, IovArray array)
    throws IOException
  {
    long expectedWrittenBytes = array.size();
    int cnt = array.count();
    
    assert (expectedWrittenBytes != 0L);
    assert (cnt != 0);
    
    boolean done = false;
    long writtenBytes = 0L;
    int offset = 0;
    int end = offset + cnt;
    for (;;) {
      long localWrittenBytes = Native.writevAddresses(fd, array.memoryAddress(offset), cnt);
      if (localWrittenBytes == 0L)
      {
        setEpollOut();
        break;
      }
      expectedWrittenBytes -= localWrittenBytes;
      writtenBytes += localWrittenBytes;
      
      if (expectedWrittenBytes == 0L)
      {
        done = true;
        break;
      }
      do
      {
        long bytes = array.processWritten(offset, localWrittenBytes);
        if (bytes == -1L) {
          break;
        }
        
        offset++;
        cnt--;
        localWrittenBytes -= bytes;
      }
      while ((offset < end) && (localWrittenBytes > 0L));
    }
    
    in.removeBytes(writtenBytes);
    return done;
  }
  

  private boolean writeBytesMultiple(ChannelOutboundBuffer in, ByteBuffer[] nioBuffers, int nioBufferCnt, long expectedWrittenBytes)
    throws IOException
  {
    assert (expectedWrittenBytes != 0L);
    
    boolean done = false;
    long writtenBytes = 0L;
    int offset = 0;
    int end = offset + nioBufferCnt;
    for (;;) {
      long localWrittenBytes = Native.writev(fd, nioBuffers, offset, nioBufferCnt);
      if (localWrittenBytes == 0L)
      {
        setEpollOut();
        break;
      }
      expectedWrittenBytes -= localWrittenBytes;
      writtenBytes += localWrittenBytes;
      
      if (expectedWrittenBytes == 0L)
      {
        done = true;
        break;
      }
      do {
        ByteBuffer buffer = nioBuffers[offset];
        int pos = buffer.position();
        int bytes = buffer.limit() - pos;
        if (bytes > localWrittenBytes) {
          buffer.position(pos + (int)localWrittenBytes);
          
          break;
        }
        offset++;
        nioBufferCnt--;
        localWrittenBytes -= bytes;
      }
      while ((offset < end) && (localWrittenBytes > 0L));
    }
    
    in.removeBytes(writtenBytes);
    return done;
  }
  




  private boolean writeFileRegion(ChannelOutboundBuffer in, DefaultFileRegion region)
    throws Exception
  {
    long regionCount = region.count();
    if (region.transfered() >= regionCount) {
      in.remove();
      return true;
    }
    
    long baseOffset = region.position();
    boolean done = false;
    long flushedAmount = 0L;
    
    for (int i = config().getWriteSpinCount() - 1; i >= 0; i--) {
      long offset = region.transfered();
      long localFlushedAmount = Native.sendfile(fd, region, baseOffset, offset, regionCount - offset);
      if (localFlushedAmount == 0L)
      {
        setEpollOut();
        break;
      }
      
      flushedAmount += localFlushedAmount;
      if (region.transfered() >= regionCount) {
        done = true;
        break;
      }
    }
    
    if (flushedAmount > 0L) {
      in.progress(flushedAmount);
    }
    
    if (done) {
      in.remove();
    }
    return done;
  }
  
  protected void doWrite(ChannelOutboundBuffer in) throws Exception
  {
    for (;;) {
      int msgCount = in.size();
      
      if (msgCount == 0)
      {
        clearEpollOut();

      }
      else
      {
        if ((msgCount > 1) && ((in.current() instanceof ByteBuf)) ? 
          !doWriteMultiple(in) : 
          






          !doWriteSingle(in)) {
          break;
        }
      }
    }
  }
  
  private boolean doWriteSingle(ChannelOutboundBuffer in) throws Exception
  {
    Object msg = in.current();
    if ((msg instanceof ByteBuf)) {
      ByteBuf buf = (ByteBuf)msg;
      if (!writeBytes(in, buf))
      {

        return false;
      }
    } else if ((msg instanceof DefaultFileRegion)) {
      DefaultFileRegion region = (DefaultFileRegion)msg;
      if (!writeFileRegion(in, region))
      {

        return false;
      }
    }
    else {
      throw new Error();
    }
    
    return true;
  }
  
  private boolean doWriteMultiple(ChannelOutboundBuffer in) throws Exception {
    if (PlatformDependent.hasUnsafe())
    {
      IovArray array = IovArray.get(in);
      int cnt = array.count();
      if (cnt >= 1)
      {
        if (!writeBytesMultiple(in, array))
        {

          return false;
        }
      } else {
        in.removeBytes(0L);
      }
    } else {
      ByteBuffer[] buffers = in.nioBuffers();
      int cnt = in.nioBufferCount();
      if (cnt >= 1)
      {
        if (!writeBytesMultiple(in, buffers, cnt, in.nioBufferSize()))
        {

          return false;
        }
      } else {
        in.removeBytes(0L);
      }
    }
    
    return true;
  }
  
  protected Object filterOutboundMessage(Object msg)
  {
    if ((msg instanceof ByteBuf)) {
      ByteBuf buf = (ByteBuf)msg;
      if ((!buf.hasMemoryAddress()) && ((PlatformDependent.hasUnsafe()) || (!buf.isDirect())))
      {

        buf = newDirectBuffer(buf);
        assert (buf.hasMemoryAddress());
      }
      return buf;
    }
    
    if ((msg instanceof DefaultFileRegion)) {
      return msg;
    }
    
    throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
  }
  

  public EpollSocketChannelConfig config()
  {
    return config;
  }
  
  public boolean isInputShutdown()
  {
    return inputShutdown;
  }
  
  public boolean isOutputShutdown()
  {
    return (outputShutdown) || (!isActive());
  }
  
  public ChannelFuture shutdownOutput()
  {
    return shutdownOutput(newPromise());
  }
  
  public ChannelFuture shutdownOutput(final ChannelPromise promise)
  {
    EventLoop loop = eventLoop();
    if (loop.inEventLoop()) {
      try {
        Native.shutdown(fd, false, true);
        outputShutdown = true;
        promise.setSuccess();
      } catch (Throwable t) {
        promise.setFailure(t);
      }
    } else {
      loop.execute(new Runnable()
      {
        public void run() {
          shutdownOutput(promise);
        }
      });
    }
    return promise;
  }
  


  public ServerSocketChannel parent() { return (ServerSocketChannel)super.parent(); }
  
  final class EpollSocketUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
    EpollSocketUnsafe() { super(); }
    
    private void closeOnRead(ChannelPipeline pipeline)
    {
      inputShutdown = true;
      if (isOpen()) {
        if (Boolean.TRUE.equals(config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
          clearEpollIn0();
          pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
        } else {
          close(voidPromise());
        }
      }
    }
    
    private boolean handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close) {
      if (byteBuf != null) {
        if (byteBuf.isReadable()) {
          readPending = false;
          pipeline.fireChannelRead(byteBuf);
        } else {
          byteBuf.release();
        }
      }
      pipeline.fireChannelReadComplete();
      pipeline.fireExceptionCaught(cause);
      if ((close) || ((cause instanceof IOException))) {
        closeOnRead(pipeline);
        return true;
      }
      return false;
    }
    

    public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    {
      if ((!promise.setUncancellable()) || (!ensureOpen(promise))) {
        return;
      }
      try
      {
        if (connectPromise != null) {
          throw new IllegalStateException("connection attempt already made");
        }
        
        boolean wasActive = isActive();
        if (doConnect((InetSocketAddress)remoteAddress, (InetSocketAddress)localAddress)) {
          fulfillConnectPromise(promise, wasActive);
        } else {
          connectPromise = promise;
          requestedRemoteAddress = remoteAddress;
          

          int connectTimeoutMillis = config().getConnectTimeoutMillis();
          if (connectTimeoutMillis > 0) {
            connectTimeoutFuture = eventLoop().schedule(new Runnable()
            {
              public void run() {
                ChannelPromise connectPromise = EpollSocketChannel.this.connectPromise;
                ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                
                if ((connectPromise != null) && (connectPromise.tryFailure(cause)))
                  close(voidPromise()); } }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
          }
          



          promise.addListener(new ChannelFutureListener()
          {
            public void operationComplete(ChannelFuture future) throws Exception {
              if (future.isCancelled()) {
                if (connectTimeoutFuture != null) {
                  connectTimeoutFuture.cancel(false);
                }
                connectPromise = null;
                close(voidPromise());
              }
            }
          });
        }
      } catch (Throwable t) {
        if ((t instanceof ConnectException)) {
          Throwable newT = new ConnectException(t.getMessage() + ": " + remoteAddress);
          newT.setStackTrace(t.getStackTrace());
          t = newT;
        }
        closeIfClosed();
        promise.tryFailure(t);
      }
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
      if (promise == null)
      {
        return;
      }
      active = true;
      

      boolean promiseSet = promise.trySuccess();
      


      if ((!wasActive) && (isActive())) {
        pipeline().fireChannelActive();
      }
      

      if (!promiseSet) {
        close(voidPromise());
      }
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
      if (promise == null)
      {
        return;
      }
      

      promise.tryFailure(cause);
      closeIfClosed();
    }
    

    private RecvByteBufAllocator.Handle allocHandle;
    private void finishConnect()
    {
      assert (eventLoop().inEventLoop());
      
      boolean connectStillInProgress = false;
      try {
        boolean wasActive = isActive();
        if (!doFinishConnect()) {
          connectStillInProgress = true;
        }
        else
          fulfillConnectPromise(connectPromise, wasActive);
      } catch (Throwable t) {
        if ((t instanceof ConnectException)) {
          Throwable newT = new ConnectException(t.getMessage() + ": " + requestedRemoteAddress);
          newT.setStackTrace(t.getStackTrace());
          t = newT;
        }
        
        fulfillConnectPromise(connectPromise, t);
      } finally {
        if (!connectStillInProgress)
        {

          if (connectTimeoutFuture != null) {
            connectTimeoutFuture.cancel(false);
          }
          connectPromise = null;
        }
      }
    }
    
    void epollOutReady()
    {
      if (connectPromise != null)
      {
        finishConnect();
      } else {
        super.epollOutReady();
      }
    }
    

    private boolean doConnect(InetSocketAddress remoteAddress, InetSocketAddress localAddress)
      throws Exception
    {
      if (localAddress != null) {
        AbstractEpollChannel.checkResolvable(localAddress);
        Native.bind(fd, localAddress.getAddress(), localAddress.getPort());
      }
      
      boolean success = false;
      try {
        AbstractEpollChannel.checkResolvable(remoteAddress);
        boolean connected = Native.connect(fd, remoteAddress.getAddress(), remoteAddress.getPort());
        
        remote = remoteAddress;
        local = Native.localAddress(fd);
        if (!connected) {
          setEpollOut();
        }
        success = true;
        return connected;
      } finally {
        if (!success) {
          doClose();
        }
      }
    }
    

    private boolean doFinishConnect()
      throws Exception
    {
      if (Native.finishConnect(fd)) {
        clearEpollOut();
        return true;
      }
      setEpollOut();
      return false;
    }
    


    private int doReadBytes(ByteBuf byteBuf)
      throws Exception
    {
      int writerIndex = byteBuf.writerIndex();
      int localReadAmount;
      int localReadAmount; if (byteBuf.hasMemoryAddress()) {
        localReadAmount = Native.readAddress(fd, byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
      } else {
        ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
        localReadAmount = Native.read(fd, buf, buf.position(), buf.limit());
      }
      if (localReadAmount > 0) {
        byteBuf.writerIndex(writerIndex + localReadAmount);
      }
      return localReadAmount;
    }
    
    void epollRdHupReady()
    {
      if (isActive()) {
        epollInReady();
      } else {
        closeOnRead(pipeline());
      }
    }
    
    void epollInReady()
    {
      ChannelConfig config = config();
      ChannelPipeline pipeline = pipeline();
      ByteBufAllocator allocator = config.getAllocator();
      RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
      if (allocHandle == null) {
        this.allocHandle = (allocHandle = config.getRecvByteBufAllocator().newHandle());
      }
      
      ByteBuf byteBuf = null;
      boolean close = false;
      try {
        int totalReadAmount = 0;
        
        for (;;)
        {
          byteBuf = allocHandle.allocate(allocator);
          int writable = byteBuf.writableBytes();
          int localReadAmount = doReadBytes(byteBuf);
          if (localReadAmount <= 0)
          {
            byteBuf.release();
            close = localReadAmount < 0;
          }
          else {
            readPending = false;
            pipeline.fireChannelRead(byteBuf);
            byteBuf = null;
            
            if (totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
              allocHandle.record(totalReadAmount);
              

              totalReadAmount = localReadAmount;
            } else {
              totalReadAmount += localReadAmount;
            }
            
            if (localReadAmount < writable) {
              break;
            }
          }
        }
        
        pipeline.fireChannelReadComplete();
        allocHandle.record(totalReadAmount);
        
        if (close) {
          closeOnRead(pipeline);
          close = false;
        }
      } catch (Throwable t) {
        boolean closed = handleReadException(pipeline, byteBuf, t, close);
        if (!closed)
        {

          eventLoop().execute(new Runnable()
          {
            public void run() {
              epollInReady();
            }
            

          });
        }
        

      }
      finally
      {
        if ((!config.isAutoRead()) && (!readPending)) {
          clearEpollIn0();
        }
      }
    }
  }
}
