package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.Channel.Unsafe;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator.Handle;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.WritableByteChannel;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;















public abstract class AbstractEpollStreamChannel
  extends AbstractEpollChannel
  implements DuplexChannel
{
  private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
  private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
  

  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEpollStreamChannel.class);
  private static final ClosedChannelException DO_CLOSE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "doClose()");
  
  private static final ClosedChannelException CLEAR_SPLICE_QUEUE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "clearSpliceQueue()");
  

  private static final ClosedChannelException SPLICE_TO_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "spliceTo(...)");
  

  private static final ClosedChannelException FAIL_SPLICE_IF_CLOSED_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "failSpliceIfClosed(...)");
  

  private ChannelPromise connectPromise;
  

  private ScheduledFuture<?> connectTimeoutFuture;
  

  private SocketAddress requestedRemoteAddress;
  

  private Queue<SpliceInTask> spliceQueue;
  
  private FileDescriptor pipeIn;
  
  private FileDescriptor pipeOut;
  
  private WritableByteChannel byteChannel;
  

  @Deprecated
  protected AbstractEpollStreamChannel(Channel parent, int fd)
  {
    this(parent, new Socket(fd));
  }
  


  @Deprecated
  protected AbstractEpollStreamChannel(int fd)
  {
    this(new Socket(fd));
  }
  


  @Deprecated
  protected AbstractEpollStreamChannel(FileDescriptor fd)
  {
    this(new Socket(fd.intValue()));
  }
  


  @Deprecated
  protected AbstractEpollStreamChannel(Socket fd)
  {
    this(fd, isSoErrorZero(fd));
  }
  
  protected AbstractEpollStreamChannel(Channel parent, Socket fd) {
    super(parent, fd, Native.EPOLLIN, true);
    
    flags |= Native.EPOLLRDHUP;
  }
  
  protected AbstractEpollStreamChannel(Socket fd, boolean active) {
    super(null, fd, Native.EPOLLIN, active);
    
    flags |= Native.EPOLLRDHUP;
  }
  
  protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe()
  {
    return new EpollStreamUnsafe();
  }
  
  public ChannelMetadata metadata()
  {
    return METADATA;
  }
  













  public final ChannelFuture spliceTo(AbstractEpollStreamChannel ch, int len)
  {
    return spliceTo(ch, len, newPromise());
  }
  














  public final ChannelFuture spliceTo(AbstractEpollStreamChannel ch, int len, ChannelPromise promise)
  {
    if (ch.eventLoop() != eventLoop()) {
      throw new IllegalArgumentException("EventLoops are not the same.");
    }
    if (len < 0) {
      throw new IllegalArgumentException("len: " + len + " (expected: >= 0)");
    }
    if ((ch.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) || (config().getEpollMode() != EpollMode.LEVEL_TRIGGERED))
    {
      throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
    }
    ObjectUtil.checkNotNull(promise, "promise");
    if (!isOpen()) {
      promise.tryFailure(SPLICE_TO_CLOSED_CHANNEL_EXCEPTION);
    } else {
      addToSpliceQueue(new SpliceInChannelTask(ch, len, promise));
      failSpliceIfClosed(promise);
    }
    return promise;
  }
  













  public final ChannelFuture spliceTo(FileDescriptor ch, int offset, int len)
  {
    return spliceTo(ch, offset, len, newPromise());
  }
  














  public final ChannelFuture spliceTo(FileDescriptor ch, int offset, int len, ChannelPromise promise)
  {
    if (len < 0) {
      throw new IllegalArgumentException("len: " + len + " (expected: >= 0)");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("offset must be >= 0 but was " + offset);
    }
    if (config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
      throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
    }
    ObjectUtil.checkNotNull(promise, "promise");
    if (!isOpen()) {
      promise.tryFailure(SPLICE_TO_CLOSED_CHANNEL_EXCEPTION);
    } else {
      addToSpliceQueue(new SpliceFdTask(ch, offset, len, promise));
      failSpliceIfClosed(promise);
    }
    return promise;
  }
  
  private void failSpliceIfClosed(ChannelPromise promise) {
    if (!isOpen())
    {

      if (promise.tryFailure(FAIL_SPLICE_IF_CLOSED_CLOSED_CHANNEL_EXCEPTION)) {
        eventLoop().execute(new Runnable()
        {
          public void run()
          {
            AbstractEpollStreamChannel.this.clearSpliceQueue();
          }
        });
      }
    }
  }
  


  private boolean writeBytes(ChannelOutboundBuffer in, ByteBuf buf, int writeSpinCount)
    throws Exception
  {
    int readableBytes = buf.readableBytes();
    if (readableBytes == 0) {
      in.remove();
      return true;
    }
    
    if ((buf.hasMemoryAddress()) || (buf.nioBufferCount() == 1)) {
      int writtenBytes = doWriteBytes(buf, writeSpinCount);
      in.removeBytes(writtenBytes);
      return writtenBytes == readableBytes;
    }
    ByteBuffer[] nioBuffers = buf.nioBuffers();
    return writeBytesMultiple(in, nioBuffers, nioBuffers.length, readableBytes, writeSpinCount);
  }
  

  private boolean writeBytesMultiple(ChannelOutboundBuffer in, IovArray array, int writeSpinCount)
    throws IOException
  {
    long expectedWrittenBytes = array.size();
    long initialExpectedWrittenBytes = expectedWrittenBytes;
    
    int cnt = array.count();
    
    assert (expectedWrittenBytes != 0L);
    assert (cnt != 0);
    
    boolean done = false;
    int offset = 0;
    int end = offset + cnt;
    for (int i = writeSpinCount - 1; i >= 0; i--) {
      long localWrittenBytes = fd().writevAddresses(array.memoryAddress(offset), cnt);
      if (localWrittenBytes == 0L) {
        break;
      }
      expectedWrittenBytes -= localWrittenBytes;
      
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
    in.removeBytes(initialExpectedWrittenBytes - expectedWrittenBytes);
    return done;
  }
  

  private boolean writeBytesMultiple(ChannelOutboundBuffer in, ByteBuffer[] nioBuffers, int nioBufferCnt, long expectedWrittenBytes, int writeSpinCount)
    throws IOException
  {
    assert (expectedWrittenBytes != 0L);
    long initialExpectedWrittenBytes = expectedWrittenBytes;
    
    boolean done = false;
    int offset = 0;
    int end = offset + nioBufferCnt;
    for (int i = writeSpinCount - 1; i >= 0; i--) {
      long localWrittenBytes = fd().writev(nioBuffers, offset, nioBufferCnt);
      if (localWrittenBytes == 0L) {
        break;
      }
      expectedWrittenBytes -= localWrittenBytes;
      
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
    
    in.removeBytes(initialExpectedWrittenBytes - expectedWrittenBytes);
    return done;
  }
  





  private boolean writeDefaultFileRegion(ChannelOutboundBuffer in, DefaultFileRegion region, int writeSpinCount)
    throws Exception
  {
    long regionCount = region.count();
    if (region.transferred() >= regionCount) {
      in.remove();
      return true;
    }
    
    long baseOffset = region.position();
    boolean done = false;
    long flushedAmount = 0L;
    
    for (int i = writeSpinCount - 1; i >= 0; i--) {
      long offset = region.transferred();
      long localFlushedAmount = Native.sendfile(fd().intValue(), region, baseOffset, offset, regionCount - offset);
      
      if (localFlushedAmount == 0L) {
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
  
  private boolean writeFileRegion(ChannelOutboundBuffer in, FileRegion region, int writeSpinCount) throws Exception
  {
    if (region.transferred() >= region.count()) {
      in.remove();
      return true;
    }
    
    boolean done = false;
    long flushedAmount = 0L;
    
    if (byteChannel == null) {
      byteChannel = new SocketWritableByteChannel(null);
    }
    for (int i = writeSpinCount - 1; i >= 0; i--) {
      long localFlushedAmount = region.transferTo(byteChannel, region.transferred());
      if (localFlushedAmount == 0L) {
        break;
      }
      
      flushedAmount += localFlushedAmount;
      if (region.transferred() >= region.count()) {
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
    int writeSpinCount = config().getWriteSpinCount();
    for (;;) {
      int msgCount = in.size();
      
      if (msgCount == 0)
      {
        clearFlag(Native.EPOLLOUT);
        
        return;
      }
      

      if ((msgCount > 1) && ((in.current() instanceof ByteBuf)) ? 
        !doWriteMultiple(in, writeSpinCount) : 
        







        !doWriteSingle(in, writeSpinCount)) {
        break;
      }
    }
    



    setFlag(Native.EPOLLOUT);
  }
  
  protected boolean doWriteSingle(ChannelOutboundBuffer in, int writeSpinCount) throws Exception
  {
    Object msg = in.current();
    if ((msg instanceof ByteBuf)) {
      if (!writeBytes(in, (ByteBuf)msg, writeSpinCount))
      {

        return false;
      }
    } else if ((msg instanceof DefaultFileRegion)) {
      if (!writeDefaultFileRegion(in, (DefaultFileRegion)msg, writeSpinCount))
      {

        return false;
      }
    } else if ((msg instanceof FileRegion)) {
      if (!writeFileRegion(in, (FileRegion)msg, writeSpinCount))
      {

        return false;
      }
    } else if ((msg instanceof SpliceOutTask)) {
      if (!((SpliceOutTask)msg).spliceOut()) {
        return false;
      }
      in.remove();
    }
    else {
      throw new Error();
    }
    
    return true;
  }
  
  private boolean doWriteMultiple(ChannelOutboundBuffer in, int writeSpinCount) throws Exception {
    if (PlatformDependent.hasUnsafe())
    {
      IovArray array = ((EpollEventLoop)eventLoop()).cleanArray();
      in.forEachFlushedMessage(array);
      
      int cnt = array.count();
      if (cnt >= 1)
      {
        if (!writeBytesMultiple(in, array, writeSpinCount))
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
        if (!writeBytesMultiple(in, buffers, cnt, in.nioBufferSize(), writeSpinCount))
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
      if ((!buf.hasMemoryAddress()) && ((PlatformDependent.hasUnsafe()) || (!buf.isDirect()))) {
        if ((buf instanceof CompositeByteBuf))
        {

          CompositeByteBuf comp = (CompositeByteBuf)buf;
          if ((!comp.isDirect()) || (comp.nioBufferCount() > Native.IOV_MAX))
          {
            buf = newDirectBuffer(buf);
            assert (buf.hasMemoryAddress());
          }
        }
        else
        {
          buf = newDirectBuffer(buf);
          assert (buf.hasMemoryAddress());
        }
      }
      return buf;
    }
    
    if (((msg instanceof FileRegion)) || ((msg instanceof SpliceOutTask))) {
      return msg;
    }
    
    throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
  }
  
  private void shutdownOutput0(ChannelPromise promise)
  {
    try {
      fd().shutdown(false, true);
      promise.setSuccess();
    } catch (Throwable cause) {
      promise.setFailure(cause);
    }
  }
  
  private void shutdownInput0(ChannelPromise promise) {
    try {
      fd().shutdown(true, false);
      promise.setSuccess();
    } catch (Throwable cause) {
      promise.setFailure(cause);
    }
  }
  
  private void shutdown0(ChannelPromise promise) {
    try {
      fd().shutdown(true, true);
      promise.setSuccess();
    } catch (Throwable cause) {
      promise.setFailure(cause);
    }
  }
  
  public boolean isOutputShutdown()
  {
    return fd().isOutputShutdown();
  }
  
  public boolean isInputShutdown()
  {
    return fd().isInputShutdown();
  }
  
  public boolean isShutdown()
  {
    return fd().isShutdown();
  }
  
  public ChannelFuture shutdownOutput()
  {
    return shutdownOutput(newPromise());
  }
  
  public ChannelFuture shutdownOutput(final ChannelPromise promise)
  {
    Executor closeExecutor = ((EpollStreamUnsafe)unsafe()).prepareToClose();
    if (closeExecutor != null) {
      closeExecutor.execute(new Runnable()
      {
        public void run() {
          AbstractEpollStreamChannel.this.shutdownOutput0(promise);
        }
      });
    } else {
      EventLoop loop = eventLoop();
      if (loop.inEventLoop()) {
        shutdownOutput0(promise);
      } else {
        loop.execute(new Runnable()
        {
          public void run() {
            AbstractEpollStreamChannel.this.shutdownOutput0(promise);
          }
        });
      }
    }
    return promise;
  }
  
  public ChannelFuture shutdownInput()
  {
    return shutdownInput(newPromise());
  }
  
  public ChannelFuture shutdownInput(final ChannelPromise promise)
  {
    Executor closeExecutor = ((EpollStreamUnsafe)unsafe()).prepareToClose();
    if (closeExecutor != null) {
      closeExecutor.execute(new Runnable()
      {
        public void run() {
          AbstractEpollStreamChannel.this.shutdownInput0(promise);
        }
      });
    } else {
      EventLoop loop = eventLoop();
      if (loop.inEventLoop()) {
        shutdownInput0(promise);
      } else {
        loop.execute(new Runnable()
        {
          public void run() {
            AbstractEpollStreamChannel.this.shutdownInput0(promise);
          }
        });
      }
    }
    return promise;
  }
  
  public ChannelFuture shutdown()
  {
    return shutdown(newPromise());
  }
  
  public ChannelFuture shutdown(final ChannelPromise promise)
  {
    Executor closeExecutor = ((EpollStreamUnsafe)unsafe()).prepareToClose();
    if (closeExecutor != null) {
      closeExecutor.execute(new Runnable()
      {
        public void run() {
          AbstractEpollStreamChannel.this.shutdown0(promise);
        }
      });
    } else {
      EventLoop loop = eventLoop();
      if (loop.inEventLoop()) {
        shutdown0(promise);
      } else {
        loop.execute(new Runnable()
        {
          public void run() {
            AbstractEpollStreamChannel.this.shutdown0(promise);
          }
        });
      }
    }
    return promise;
  }
  
  protected void doClose() throws Exception
  {
    try {
      ChannelPromise promise = connectPromise;
      if (promise != null)
      {
        promise.tryFailure(DO_CLOSE_CLOSED_CHANNEL_EXCEPTION);
        connectPromise = null;
      }
      
      ScheduledFuture<?> future = connectTimeoutFuture;
      if (future != null) {
        future.cancel(false);
        connectTimeoutFuture = null;
      }
      
      super.doClose();
    } finally {
      safeClosePipe(pipeIn);
      safeClosePipe(pipeOut);
      clearSpliceQueue();
    }
  }
  
  private void clearSpliceQueue() {
    if (spliceQueue == null) {
      return;
    }
    for (;;) {
      SpliceInTask task = (SpliceInTask)spliceQueue.poll();
      if (task == null) {
        break;
      }
      promise.tryFailure(CLEAR_SPLICE_QUEUE_CLOSED_CHANNEL_EXCEPTION);
    }
  }
  

  protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress)
    throws Exception
  {
    if (localAddress != null) {
      fd().bind(localAddress);
    }
    
    boolean success = false;
    try {
      boolean connected = fd().connect(remoteAddress);
      if (!connected) {
        setFlag(Native.EPOLLOUT);
      }
      success = true;
      return connected;
    } finally {
      if (!success) {
        doClose();
      }
    }
  }
  
  private static void safeClosePipe(FileDescriptor fd) {
    if (fd != null)
      try {
        fd.close();
      } catch (IOException e) {
        if (logger.isWarnEnabled())
          logger.warn("Error while closing a pipe", e);
      }
  }
  
  class EpollStreamUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
    EpollStreamUnsafe() {
      super();
    }
    
    protected Executor prepareToClose()
    {
      return super.prepareToClose();
    }
    
    private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, EpollRecvByteAllocatorHandle allocHandle)
    {
      if (byteBuf != null) {
        if (byteBuf.isReadable()) {
          readPending = false;
          pipeline.fireChannelRead(byteBuf);
        } else {
          byteBuf.release();
        }
      }
      allocHandle.readComplete();
      pipeline.fireChannelReadComplete();
      pipeline.fireExceptionCaught(cause);
      if ((close) || ((cause instanceof IOException))) {
        shutdownInput();
      }
    }
    

    public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    {
      if ((!promise.setUncancellable()) || (!ensureOpen(promise))) {
        return;
      }
      try
      {
        if (connectPromise != null) {
          throw new ConnectionPendingException();
        }
        
        boolean wasActive = isActive();
        if (doConnect(remoteAddress, localAddress)) {
          fulfillConnectPromise(promise, wasActive);
        } else {
          connectPromise = promise;
          requestedRemoteAddress = remoteAddress;
          

          int connectTimeoutMillis = config().getConnectTimeoutMillis();
          if (connectTimeoutMillis > 0) {
            connectTimeoutFuture = eventLoop().schedule(new Runnable()
            {
              public void run() {
                ChannelPromise connectPromise = AbstractEpollStreamChannel.this.connectPromise;
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
        closeIfClosed();
        promise.tryFailure(annotateConnectException(t, remoteAddress));
      }
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
      if (promise == null)
      {
        return;
      }
      AbstractEpollStreamChannel.this.active = true;
      


      boolean active = isActive();
      

      boolean promiseSet = promise.trySuccess();
      


      if ((!wasActive) && (active)) {
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
        fulfillConnectPromise(connectPromise, annotateConnectException(t, requestedRemoteAddress));
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
    

    boolean doFinishConnect()
      throws Exception
    {
      if (fd().finishConnect()) {
        clearFlag(Native.EPOLLOUT);
        return true;
      }
      setFlag(Native.EPOLLOUT);
      return false;
    }
    

    EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.Handle handle)
    {
      return new EpollRecvByteAllocatorStreamingHandle(handle, config());
    }
    
    void epollInReady()
    {
      if (fd().isInputShutdown()) {
        clearEpollIn0();
        return;
      }
      ChannelConfig config = config();
      EpollRecvByteAllocatorHandle allocHandle = recvBufAllocHandle();
      allocHandle.edgeTriggered(isFlagSet(Native.EPOLLET));
      
      ChannelPipeline pipeline = pipeline();
      ByteBufAllocator allocator = config.getAllocator();
      allocHandle.reset(config);
      epollInBefore();
      
      ByteBuf byteBuf = null;
      boolean close = false;
      try {
        do {
          if (spliceQueue != null) {
            AbstractEpollStreamChannel.SpliceInTask spliceTask = (AbstractEpollStreamChannel.SpliceInTask)spliceQueue.peek();
            if (spliceTask != null) {
              if (!spliceTask.spliceIn(allocHandle)) {
                break;
              }
              if (!isActive()) continue;
              spliceQueue.remove(); continue;
            }
          }
          







          byteBuf = allocHandle.allocate(allocator);
          allocHandle.lastBytesRead(doReadBytes(byteBuf));
          if (allocHandle.lastBytesRead() <= 0)
          {
            byteBuf.release();
            byteBuf = null;
            close = allocHandle.lastBytesRead() < 0;
          }
          else {
            allocHandle.incMessagesRead(1);
            readPending = false;
            pipeline.fireChannelRead(byteBuf);
            byteBuf = null;
            
            if (fd().isInputShutdown())
            {

              break;


            }
            


          }
          

        }
        while (allocHandle.continueReading());
        
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        
        if (close) {
          shutdownInput();
        }
      } catch (Throwable t) {
        handleReadException(pipeline, byteBuf, t, close, allocHandle);
      } finally {
        epollInFinally(config);
      }
    }
  }
  
  private void addToSpliceQueue(final SpliceInTask task) {
    EventLoop eventLoop = eventLoop();
    if (eventLoop.inEventLoop()) {
      addToSpliceQueue0(task);
    } else {
      eventLoop.execute(new Runnable()
      {
        public void run() {
          AbstractEpollStreamChannel.this.addToSpliceQueue0(task);
        }
      });
    }
  }
  
  private void addToSpliceQueue0(SpliceInTask task) {
    if (spliceQueue == null) {
      spliceQueue = PlatformDependent.newMpscQueue();
    }
    spliceQueue.add(task);
  }
  
  protected abstract class SpliceInTask {
    final ChannelPromise promise;
    int len;
    
    protected SpliceInTask(int len, ChannelPromise promise) {
      this.promise = promise;
      this.len = len;
    }
    
    abstract boolean spliceIn(RecvByteBufAllocator.Handle paramHandle);
    
    protected final int spliceIn(FileDescriptor pipeOut, RecvByteBufAllocator.Handle handle) throws IOException
    {
      int length = Math.min(handle.guess(), len);
      int splicedIn = 0;
      for (;;)
      {
        int localSplicedIn = Native.splice(fd().intValue(), -1L, pipeOut.intValue(), -1L, length);
        if (localSplicedIn == 0) {
          break;
        }
        splicedIn += localSplicedIn;
        length -= localSplicedIn;
      }
      
      return splicedIn;
    }
  }
  
  private final class SpliceInChannelTask extends AbstractEpollStreamChannel.SpliceInTask implements ChannelFutureListener
  {
    private final AbstractEpollStreamChannel ch;
    
    SpliceInChannelTask(AbstractEpollStreamChannel ch, int len, ChannelPromise promise) {
      super(len, promise);
      this.ch = ch;
    }
    
    public void operationComplete(ChannelFuture future) throws Exception
    {
      if (!future.isSuccess()) {
        promise.setFailure(future.cause());
      }
    }
    
    public boolean spliceIn(RecvByteBufAllocator.Handle handle)
    {
      assert (ch.eventLoop().inEventLoop());
      if (len == 0) {
        promise.setSuccess();
        return true;
      }
      

      try
      {
        FileDescriptor pipeOut = ch.pipeOut;
        if (pipeOut == null)
        {
          FileDescriptor[] pipe = FileDescriptor.pipe();
          ch.pipeIn = pipe[0];
          pipeOut = ch.pipeOut = pipe[1];
        }
        
        int splicedIn = spliceIn(pipeOut, handle);
        if (splicedIn > 0)
        {
          if (len != Integer.MAX_VALUE) {
            len -= splicedIn;
          }
          
          ChannelPromise splicePromise;
          
          ChannelPromise splicePromise;
          if (len == 0) {
            splicePromise = promise;
          } else {
            splicePromise = ch.newPromise().addListener(this);
          }
          
          boolean autoRead = config().isAutoRead();
          


          ch.unsafe().write(new AbstractEpollStreamChannel.SpliceOutTask(AbstractEpollStreamChannel.this, ch, splicedIn, autoRead), splicePromise);
          ch.unsafe().flush();
          if ((autoRead) && (!splicePromise.isDone()))
          {



            config().setAutoRead(false);
          }
        }
        
        return len == 0;
      } catch (Throwable cause) {
        promise.setFailure(cause); }
      return true;
    }
  }
  
  private final class SpliceOutTask
  {
    private final AbstractEpollStreamChannel ch;
    private final boolean autoRead;
    private int len;
    
    SpliceOutTask(AbstractEpollStreamChannel ch, int len, boolean autoRead) {
      this.ch = ch;
      this.len = len;
      this.autoRead = autoRead;
    }
    
    public boolean spliceOut() throws Exception {
      assert (ch.eventLoop().inEventLoop());
      try {
        int splicedOut = Native.splice(ch.pipeIn.intValue(), -1L, ch.fd().intValue(), -1L, len);
        len -= splicedOut;
        if (len == 0) {
          if (autoRead)
          {
            config().setAutoRead(true);
          }
          return true;
        }
        return false;
      } catch (IOException e) {
        if (autoRead)
        {
          config().setAutoRead(true);
        }
        throw e;
      }
    }
  }
  
  private final class SpliceFdTask extends AbstractEpollStreamChannel.SpliceInTask {
    private final FileDescriptor fd;
    private final ChannelPromise promise;
    private final int offset;
    
    SpliceFdTask(FileDescriptor fd, int offset, int len, ChannelPromise promise) {
      super(len, promise);
      this.fd = fd;
      this.promise = promise;
      this.offset = offset;
    }
    
    public boolean spliceIn(RecvByteBufAllocator.Handle handle)
    {
      assert (eventLoop().inEventLoop());
      if (len == 0) {
        promise.setSuccess();
        return true;
      }
      try
      {
        FileDescriptor[] pipe = FileDescriptor.pipe();
        FileDescriptor pipeIn = pipe[0];
        FileDescriptor pipeOut = pipe[1];
        try {
          int splicedIn = spliceIn(pipeOut, handle);
          int splicedOut; if (splicedIn > 0)
          {
            if (len != Integer.MAX_VALUE) {
              len -= splicedIn;
            }
            do {
              splicedOut = Native.splice(pipeIn.intValue(), -1L, fd.intValue(), offset, splicedIn);
              splicedIn -= splicedOut;
            } while (splicedIn > 0);
            if (len == 0) {
              promise.setSuccess();
              return 1;
            }
          }
          return 0;
        } finally {
          AbstractEpollStreamChannel.safeClosePipe(pipeIn);
          AbstractEpollStreamChannel.safeClosePipe(pipeOut);
        }
        

        return true;
      }
      catch (Throwable cause)
      {
        promise.setFailure(cause);
      }
    }
  }
  
  private final class SocketWritableByteChannel implements WritableByteChannel
  {
    private SocketWritableByteChannel() {}
    
    public int write(ByteBuffer src) throws IOException
    {
      int position = src.position();
      int limit = src.limit();
      int written; if (src.isDirect()) {
        written = fd().write(src, position, src.limit());
      } else {
        int readableBytes = limit - position;
        ByteBuf buffer = null;
        try {
          if (readableBytes == 0) {
            buffer = Unpooled.EMPTY_BUFFER;
          } else {
            ByteBufAllocator alloc = alloc();
            if (alloc.isDirectBufferPooled()) {
              buffer = alloc.directBuffer(readableBytes);
            } else {
              buffer = ByteBufUtil.threadLocalDirectBuffer();
              if (buffer == null) {
                buffer = Unpooled.directBuffer(readableBytes);
              }
            }
          }
          buffer.writeBytes(src.duplicate());
          ByteBuffer nioBuffer = buffer.internalNioBuffer(buffer.readerIndex(), readableBytes);
          written = fd().write(nioBuffer, nioBuffer.position(), nioBuffer.limit());
        } finally { int written;
          if (buffer != null)
            buffer.release();
        }
      }
      int written;
      if (written > 0) {
        src.position(position + written);
      }
      return written;
    }
    
    public boolean isOpen()
    {
      return fd().isOpen();
    }
    
    public void close() throws IOException
    {
      fd().close();
    }
  }
}
