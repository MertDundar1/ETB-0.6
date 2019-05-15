package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;


























































































public class SslHandler
  extends ByteToMessageDecoder
  implements ChannelOutboundHandler
{
  private static final InternalLogger logger;
  private static final Pattern IGNORABLE_CLASS_IN_STACK;
  private static final Pattern IGNORABLE_ERROR_MESSAGE;
  private static final SSLException SSLENGINE_CLOSED;
  private static final SSLException HANDSHAKE_TIMED_OUT;
  private static final ClosedChannelException CHANNEL_CLOSED;
  private volatile ChannelHandlerContext ctx;
  private final SSLEngine engine;
  private final int maxPacketBufferSize;
  private final Executor delegatedTaskExecutor;
  private final boolean wantsDirectBuffer;
  private final boolean wantsLargeOutboundNetworkBuffer;
  private boolean wantsInboundHeapBuffer;
  private final boolean startTls;
  private boolean sentFirstMessage;
  private boolean flushedBeforeHandshakeDone;
  private PendingWriteQueue pendingUnencryptedWrites;
  
  static
  {
    logger = InternalLoggerFactory.getInstance(SslHandler.class);
    

    IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
    
    IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
    

    SSLENGINE_CLOSED = new SSLException("SSLEngine closed already");
    HANDSHAKE_TIMED_OUT = new SSLException("handshake timed out");
    CHANNEL_CLOSED = new ClosedChannelException();
    

    SSLENGINE_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    HANDSHAKE_TIMED_OUT.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    CHANNEL_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
  }
  

































  private final LazyChannelPromise handshakePromise = new LazyChannelPromise(null);
  private final LazyChannelPromise sslCloseFuture = new LazyChannelPromise(null);
  


  private boolean needsFlush;
  

  private int packetLength;
  

  private volatile long handshakeTimeoutMillis = 10000L;
  private volatile long closeNotifyTimeoutMillis = 3000L;
  




  public SslHandler(SSLEngine engine)
  {
    this(engine, false);
  }
  







  public SslHandler(SSLEngine engine, boolean startTls)
  {
    this(engine, startTls, ImmediateExecutor.INSTANCE);
  }
  


  @Deprecated
  public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor)
  {
    this(engine, false, delegatedTaskExecutor);
  }
  


  @Deprecated
  public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor)
  {
    if (engine == null) {
      throw new NullPointerException("engine");
    }
    if (delegatedTaskExecutor == null) {
      throw new NullPointerException("delegatedTaskExecutor");
    }
    this.engine = engine;
    this.delegatedTaskExecutor = delegatedTaskExecutor;
    this.startTls = startTls;
    maxPacketBufferSize = engine.getSession().getPacketBufferSize();
    
    wantsDirectBuffer = (engine instanceof OpenSslEngine);
    wantsLargeOutboundNetworkBuffer = (!(engine instanceof OpenSslEngine));
  }
  
  public long getHandshakeTimeoutMillis() {
    return handshakeTimeoutMillis;
  }
  
  public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
    if (unit == null) {
      throw new NullPointerException("unit");
    }
    
    setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
  }
  
  public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
    if (handshakeTimeoutMillis < 0L) {
      throw new IllegalArgumentException("handshakeTimeoutMillis: " + handshakeTimeoutMillis + " (expected: >= 0)");
    }
    
    this.handshakeTimeoutMillis = handshakeTimeoutMillis;
  }
  
  public long getCloseNotifyTimeoutMillis() {
    return closeNotifyTimeoutMillis;
  }
  
  public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
    if (unit == null) {
      throw new NullPointerException("unit");
    }
    
    setCloseNotifyTimeoutMillis(unit.toMillis(closeNotifyTimeout));
  }
  
  public void setCloseNotifyTimeoutMillis(long closeNotifyTimeoutMillis) {
    if (closeNotifyTimeoutMillis < 0L) {
      throw new IllegalArgumentException("closeNotifyTimeoutMillis: " + closeNotifyTimeoutMillis + " (expected: >= 0)");
    }
    
    this.closeNotifyTimeoutMillis = closeNotifyTimeoutMillis;
  }
  


  public SSLEngine engine()
  {
    return engine;
  }
  


  public Future<Channel> handshakeFuture()
  {
    return handshakePromise;
  }
  



  public ChannelFuture close()
  {
    return close(ctx.newPromise());
  }
  


  public ChannelFuture close(final ChannelPromise future)
  {
    final ChannelHandlerContext ctx = this.ctx;
    ctx.executor().execute(new Runnable()
    {
      public void run() {
        engine.closeOutbound();
        try {
          write(ctx, Unpooled.EMPTY_BUFFER, future);
          flush(ctx);
        } catch (Exception e) {
          if (!future.tryFailure(e)) {
            SslHandler.logger.warn("flush() raised a masked exception.", e);
          }
          
        }
      }
    });
    return future;
  }
  







  public Future<Channel> sslCloseFuture()
  {
    return sslCloseFuture;
  }
  
  public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception
  {
    if (!pendingUnencryptedWrites.isEmpty())
    {
      pendingUnencryptedWrites.removeAndFailAll(new ChannelException("Pending write on removal of SslHandler"));
    }
  }
  
  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception
  {
    ctx.bind(localAddress, promise);
  }
  
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    throws Exception
  {
    ctx.connect(remoteAddress, localAddress, promise);
  }
  
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    ctx.deregister(promise);
  }
  
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
    throws Exception
  {
    closeOutboundAndChannel(ctx, promise, true);
  }
  
  public void close(ChannelHandlerContext ctx, ChannelPromise promise)
    throws Exception
  {
    closeOutboundAndChannel(ctx, promise, false);
  }
  
  public void read(ChannelHandlerContext ctx)
  {
    ctx.read();
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
  {
    pendingUnencryptedWrites.add(msg, promise);
  }
  

  public void flush(ChannelHandlerContext ctx)
    throws Exception
  {
    if ((startTls) && (!sentFirstMessage)) {
      sentFirstMessage = true;
      pendingUnencryptedWrites.removeAndWriteAll();
      ctx.flush();
      return;
    }
    if (pendingUnencryptedWrites.isEmpty()) {
      pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.voidPromise());
    }
    if (!handshakePromise.isDone()) {
      flushedBeforeHandshakeDone = true;
    }
    wrap(ctx, false);
    ctx.flush();
  }
  
  private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
    ByteBuf out = null;
    ChannelPromise promise = null;
    try {
      for (;;) {
        Object msg = pendingUnencryptedWrites.current();
        if (msg == null) {
          break;
        }
        
        if (!(msg instanceof ByteBuf)) {
          pendingUnencryptedWrites.removeAndWrite();
        }
        else
        {
          ByteBuf buf = (ByteBuf)msg;
          if (out == null) {
            out = allocateOutNetBuf(ctx, buf.readableBytes());
          }
          
          SSLEngineResult result = wrap(engine, buf, out);
          
          if (!buf.isReadable()) {
            promise = pendingUnencryptedWrites.remove();
          } else {
            promise = null;
          }
          
          if (result.getStatus() == SSLEngineResult.Status.CLOSED)
          {

            pendingUnencryptedWrites.removeAndFailAll(SSLENGINE_CLOSED); return;
          }
          
          switch (8.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[result.getHandshakeStatus().ordinal()]) {
          case 1: 
            runDelegatedTasks();
            break;
          case 2: 
            setHandshakeSuccess();
          
          case 3: 
            setHandshakeSuccessIfStillHandshaking();
          
          case 4: 
            finishWrap(ctx, out, promise, inUnwrap);
            promise = null;
            out = null;
            break;
          case 5: 
            return;
          default: 
            throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
          }
        }
      }
    }
    catch (SSLException e) {
      setHandshakeFailure(e);
      throw e;
    } finally {
      finishWrap(ctx, out, promise, inUnwrap);
    }
  }
  
  private void finishWrap(ChannelHandlerContext ctx, ByteBuf out, ChannelPromise promise, boolean inUnwrap) {
    if (out == null) {
      out = Unpooled.EMPTY_BUFFER;
    } else if (!out.isReadable()) {
      out.release();
      out = Unpooled.EMPTY_BUFFER;
    }
    
    if (promise != null) {
      ctx.write(out, promise);
    } else {
      ctx.write(out);
    }
    
    if (inUnwrap) {
      needsFlush = true;
    }
  }
  
  private void wrapNonAppData(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
    ByteBuf out = null;
    try {
      for (;;) {
        if (out == null) {
          out = allocateOutNetBuf(ctx, 0);
        }
        SSLEngineResult result = wrap(engine, Unpooled.EMPTY_BUFFER, out);
        
        if (result.bytesProduced() > 0) {
          ctx.write(out);
          if (inUnwrap) {
            needsFlush = true;
          }
          out = null;
        }
        
        switch (8.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[result.getHandshakeStatus().ordinal()]) {
        case 2: 
          setHandshakeSuccess();
          break;
        case 1: 
          runDelegatedTasks();
          break;
        case 5: 
          if (!inUnwrap) {
            unwrapNonAppData(ctx);
          }
          break;
        case 4: 
          break;
        case 3: 
          setHandshakeSuccessIfStillHandshaking();
          

          if (!inUnwrap) {
            unwrapNonAppData(ctx);
          }
          break;
        default: 
          throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
        }
        
        if (result.bytesProduced() == 0) {
          break;
        }
      }
    } catch (SSLException e) {
      setHandshakeFailure(e);
      throw e;
    } finally {
      if (out != null) {
        out.release();
      }
    }
  }
  
  private SSLEngineResult wrap(SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
    ByteBuffer in0 = in.nioBuffer();
    if (!in0.isDirect()) {
      ByteBuffer newIn0 = ByteBuffer.allocateDirect(in0.remaining());
      newIn0.put(in0).flip();
      in0 = newIn0;
    }
    for (;;)
    {
      ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
      SSLEngineResult result = engine.wrap(in0, out0);
      in.skipBytes(result.bytesConsumed());
      out.writerIndex(out.writerIndex() + result.bytesProduced());
      
      switch (result.getStatus()) {
      case BUFFER_OVERFLOW: 
        out.ensureWritable(maxPacketBufferSize);
        break;
      default: 
        return result;
      }
      
    }
  }
  
  public void channelInactive(ChannelHandlerContext ctx)
    throws Exception
  {
    setHandshakeFailure(CHANNEL_CLOSED);
    super.channelInactive(ctx);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
  {
    if (ignoreException(cause))
    {

      if (logger.isDebugEnabled()) {
        logger.debug("Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", cause);
      }
      




      if (ctx.channel().isActive()) {
        ctx.close();
      }
    } else {
      ctx.fireExceptionCaught(cause);
    }
  }
  








  private boolean ignoreException(Throwable t)
  {
    if ((!(t instanceof SSLException)) && ((t instanceof IOException)) && (sslCloseFuture.isDone())) {
      String message = String.valueOf(t.getMessage()).toLowerCase();
      


      if (IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
        return true;
      }
      

      StackTraceElement[] elements = t.getStackTrace();
      for (StackTraceElement element : elements) {
        String classname = element.getClassName();
        String methodname = element.getMethodName();
        

        if (!classname.startsWith("io.netty."))
        {



          if ("read".equals(methodname))
          {




            if (IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
              return true;
            }
            


            try
            {
              Class<?> clazz = PlatformDependent.getClassLoader(getClass()).loadClass(classname);
              
              if ((SocketChannel.class.isAssignableFrom(clazz)) || (DatagramChannel.class.isAssignableFrom(clazz)))
              {
                return true;
              }
              

              if ((PlatformDependent.javaVersion() >= 7) && ("com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())))
              {
                return true;
              }
            }
            catch (ClassNotFoundException e) {}
          }
        }
      }
    }
    return false;
  }
  











  public static boolean isEncrypted(ByteBuf buffer)
  {
    if (buffer.readableBytes() < 5) {
      throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
    }
    return getEncryptedPacketLength(buffer, buffer.readerIndex()) != -1;
  }
  












  private static int getEncryptedPacketLength(ByteBuf buffer, int offset)
  {
    int packetLength = 0;
    
    boolean tls;
    
    switch (buffer.getUnsignedByte(offset)) {
    case 20: 
    case 21: 
    case 22: 
    case 23: 
      tls = true;
      break;
    
    default: 
      tls = false;
    }
    
    if (tls)
    {
      int majorVersion = buffer.getUnsignedByte(offset + 1);
      if (majorVersion == 3)
      {
        packetLength = buffer.getUnsignedShort(offset + 3) + 5;
        if (packetLength <= 5)
        {
          tls = false;
        }
      }
      else {
        tls = false;
      }
    }
    
    if (!tls)
    {
      boolean sslv2 = true;
      int headerLength = (buffer.getUnsignedByte(offset) & 0x80) != 0 ? 2 : 3;
      int majorVersion = buffer.getUnsignedByte(offset + headerLength + 1);
      if ((majorVersion == 2) || (majorVersion == 3))
      {
        if (headerLength == 2) {
          packetLength = (buffer.getShort(offset) & 0x7FFF) + 2;
        } else {
          packetLength = (buffer.getShort(offset) & 0x3FFF) + 3;
        }
        if (packetLength <= headerLength) {
          sslv2 = false;
        }
      } else {
        sslv2 = false;
      }
      
      if (!sslv2) {
        return -1;
      }
    }
    return packetLength;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
    throws SSLException
  {
    int startOffset = in.readerIndex();
    int endOffset = in.writerIndex();
    int offset = startOffset;
    int totalLength = 0;
    

    if (this.packetLength > 0) {
      if (endOffset - startOffset < this.packetLength) {
        return;
      }
      offset += this.packetLength;
      totalLength = this.packetLength;
      this.packetLength = 0;
    }
    

    boolean nonSslRecord = false;
    
    while (totalLength < 18713) {
      int readableBytes = endOffset - offset;
      if (readableBytes < 5) {
        break;
      }
      
      int packetLength = getEncryptedPacketLength(in, offset);
      if (packetLength == -1) {
        nonSslRecord = true;
        break;
      }
      
      assert (packetLength > 0);
      
      if (packetLength > readableBytes)
      {
        this.packetLength = packetLength;
        break;
      }
      
      int newTotalLength = totalLength + packetLength;
      if (newTotalLength > 18713) {
        break;
      }
      



      offset += packetLength;
      totalLength = newTotalLength;
    }
    
    if (totalLength > 0)
    {










      in.skipBytes(totalLength);
      ByteBuffer inNetBuf = in.nioBuffer(startOffset, totalLength);
      unwrap(ctx, inNetBuf, totalLength);
      assert ((!inNetBuf.hasRemaining()) || (engine.isInboundDone()));
    }
    
    if (nonSslRecord)
    {
      NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
      
      in.skipBytes(in.readableBytes());
      ctx.fireExceptionCaught(e);
      setHandshakeFailure(e);
    }
  }
  
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
  {
    if (needsFlush) {
      needsFlush = false;
      ctx.flush();
    }
    super.channelReadComplete(ctx);
  }
  

  private void unwrapNonAppData(ChannelHandlerContext ctx)
    throws SSLException
  {
    unwrap(ctx, Unpooled.EMPTY_BUFFER.nioBuffer(), 0);
  }
  






  private void unwrap(ChannelHandlerContext ctx, ByteBuffer packet, int initialOutAppBufCapacity)
    throws SSLException
  {
    int oldPos = packet.position();
    ByteBuffer oldPacket; ByteBuf newPacket; if ((wantsInboundHeapBuffer) && (packet.isDirect())) {
      ByteBuf newPacket = ctx.alloc().heapBuffer(packet.limit() - oldPos);
      newPacket.writeBytes(packet);
      ByteBuffer oldPacket = packet;
      packet = newPacket.nioBuffer();
    } else {
      oldPacket = null;
      newPacket = null;
    }
    
    boolean wrapLater = false;
    ByteBuf decodeOut = allocate(ctx, initialOutAppBufCapacity);
    try {
      for (;;) {
        SSLEngineResult result = unwrap(engine, packet, decodeOut);
        SSLEngineResult.Status status = result.getStatus();
        SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
        int produced = result.bytesProduced();
        int consumed = result.bytesConsumed();
        
        if (status == SSLEngineResult.Status.CLOSED)
        {
          sslCloseFuture.trySuccess(ctx.channel());
        }
        else
        {
          switch (8.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[handshakeStatus.ordinal()]) {
          case 5: 
            break;
          case 4: 
            wrapNonAppData(ctx, true);
            break;
          case 1: 
            runDelegatedTasks();
            break;
          case 2: 
            setHandshakeSuccess();
            wrapLater = true;
            break;
          case 3: 
            if (setHandshakeSuccessIfStillHandshaking()) {
              wrapLater = true;

            }
            else if (flushedBeforeHandshakeDone)
            {


              flushedBeforeHandshakeDone = false;
              wrapLater = true;
            }
            
            break;
          default: 
            throw new IllegalStateException("Unknown handshake status: " + handshakeStatus);
            

            if ((status == SSLEngineResult.Status.BUFFER_UNDERFLOW) || ((consumed == 0) && (produced == 0)))
              break label296;
          } }
      }
      label296:
      if (wrapLater) {
        wrap(ctx, true);
      }
    } catch (SSLException e) {
      setHandshakeFailure(e);
      throw e;
    }
    finally
    {
      if (newPacket != null) {
        oldPacket.position(oldPos + packet.position());
        newPacket.release();
      }
      
      if (decodeOut.isReadable()) {
        ctx.fireChannelRead(decodeOut);
      } else {
        decodeOut.release();
      }
    }
  }
  
  private static SSLEngineResult unwrap(SSLEngine engine, ByteBuffer in, ByteBuf out) throws SSLException {
    int overflows = 0;
    for (;;) {
      ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
      SSLEngineResult result = engine.unwrap(in, out0);
      out.writerIndex(out.writerIndex() + result.bytesProduced());
      switch (result.getStatus()) {
      case BUFFER_OVERFLOW: 
        int max = engine.getSession().getApplicationBufferSize();
        switch (overflows++) {
        case 0: 
          out.ensureWritable(Math.min(max, in.remaining()));
          break;
        default: 
          out.ensureWritable(max);
        }
        break;
      default: 
        return result;
      }
      
    }
  }
  




  private void runDelegatedTasks()
  {
    if (delegatedTaskExecutor == ImmediateExecutor.INSTANCE) {
      for (;;) {
        Runnable task = engine.getDelegatedTask();
        if (task == null) {
          break;
        }
        
        task.run();
      }
    }
    final List<Runnable> tasks = new ArrayList(2);
    for (;;) {
      Runnable task = engine.getDelegatedTask();
      if (task == null) {
        break;
      }
      
      tasks.add(task);
    }
    
    if (tasks.isEmpty()) {
      return;
    }
    
    final CountDownLatch latch = new CountDownLatch(1);
    delegatedTaskExecutor.execute(new Runnable()
    {
      public void run() {
        try {
          for (Runnable task : tasks) {
            task.run();
          }
        } catch (Exception e) {
          ctx.fireExceptionCaught(e);
        } finally {
          latch.countDown();
        }
        
      }
    });
    boolean interrupted = false;
    while (latch.getCount() != 0L) {
      try {
        latch.await();
      }
      catch (InterruptedException e) {
        interrupted = true;
      }
    }
    
    if (interrupted) {
      Thread.currentThread().interrupt();
    }
  }
  







  private boolean setHandshakeSuccessIfStillHandshaking()
  {
    if (!handshakePromise.isDone()) {
      setHandshakeSuccess();
      return true;
    }
    return false;
  }
  



  private void setHandshakeSuccess()
  {
    String cipherSuite = String.valueOf(engine.getSession().getCipherSuite());
    if ((!wantsDirectBuffer) && ((cipherSuite.contains("_GCM_")) || (cipherSuite.contains("-GCM-")))) {
      wantsInboundHeapBuffer = true;
    }
    
    if (handshakePromise.trySuccess(ctx.channel())) {
      if (logger.isDebugEnabled()) {
        logger.debug(ctx.channel() + " HANDSHAKEN: " + engine.getSession().getCipherSuite());
      }
      ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
    }
  }
  




  private void setHandshakeFailure(Throwable cause)
  {
    engine.closeOutbound();
    try
    {
      engine.closeInbound();

    }
    catch (SSLException e)
    {

      String msg = e.getMessage();
      if ((msg == null) || (!msg.contains("possible truncation attack"))) {
        logger.debug("SSLEngine.closeInbound() raised an exception.", e);
      }
    }
    notifyHandshakeFailure(cause);
    pendingUnencryptedWrites.removeAndFailAll(cause);
  }
  
  private void notifyHandshakeFailure(Throwable cause) {
    if (handshakePromise.tryFailure(cause)) {
      ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
      ctx.close();
    }
  }
  
  private void closeOutboundAndChannel(ChannelHandlerContext ctx, ChannelPromise promise, boolean disconnect) throws Exception
  {
    if (!ctx.channel().isActive()) {
      if (disconnect) {
        ctx.disconnect(promise);
      } else {
        ctx.close(promise);
      }
      return;
    }
    
    engine.closeOutbound();
    
    ChannelPromise closeNotifyFuture = ctx.newPromise();
    write(ctx, Unpooled.EMPTY_BUFFER, closeNotifyFuture);
    flush(ctx);
    safeClose(ctx, closeNotifyFuture, promise);
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    this.ctx = ctx;
    pendingUnencryptedWrites = new PendingWriteQueue(ctx);
    
    if ((ctx.channel().isActive()) && (engine.getUseClientMode()))
    {

      handshake();
    }
  }
  

  private Future<Channel> handshake()
  {
    ScheduledFuture<?> timeoutFuture;
    final ScheduledFuture<?> timeoutFuture;
    if (handshakeTimeoutMillis > 0L) {
      timeoutFuture = ctx.executor().schedule(new Runnable()
      {
        public void run() {
          if (handshakePromise.isDone()) {
            return;
          }
          SslHandler.this.notifyHandshakeFailure(SslHandler.HANDSHAKE_TIMED_OUT); } }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
    }
    else
    {
      timeoutFuture = null;
    }
    
    handshakePromise.addListener(new GenericFutureListener()
    {
      public void operationComplete(Future<Channel> f) throws Exception {
        if (timeoutFuture != null) {
          timeoutFuture.cancel(false);
        }
      }
    });
    try {
      engine.beginHandshake();
      wrapNonAppData(ctx, false);
      ctx.flush();
    } catch (Exception e) {
      notifyHandshakeFailure(e);
    }
    return handshakePromise;
  }
  


  public void channelActive(final ChannelHandlerContext ctx)
    throws Exception
  {
    if ((!startTls) && (engine.getUseClientMode()))
    {

      handshake().addListener(new GenericFutureListener()
      {
        public void operationComplete(Future<Channel> future) throws Exception {
          if (!future.isSuccess()) {
            SslHandler.logger.debug("Failed to complete handshake", future.cause());
            ctx.close();
          }
        }
      });
    }
    ctx.fireChannelActive();
  }
  

  private void safeClose(final ChannelHandlerContext ctx, ChannelFuture flushFuture, final ChannelPromise promise)
  {
    if (!ctx.channel().isActive()) {
      ctx.close(promise); return;
    }
    
    ScheduledFuture<?> timeoutFuture;
    final ScheduledFuture<?> timeoutFuture;
    if (closeNotifyTimeoutMillis > 0L)
    {
      timeoutFuture = ctx.executor().schedule(new Runnable()
      {
        public void run() {
          SslHandler.logger.warn(ctx.channel() + " last write attempt timed out." + " Force-closing the connection.");
          

          ctx.close(promise); } }, closeNotifyTimeoutMillis, TimeUnit.MILLISECONDS);
    }
    else
    {
      timeoutFuture = null;
    }
    

    flushFuture.addListener(new ChannelFutureListener()
    {
      public void operationComplete(ChannelFuture f) throws Exception
      {
        if (timeoutFuture != null) {
          timeoutFuture.cancel(false);
        }
        

        ctx.close(promise);
      }
    });
  }
  



  private ByteBuf allocate(ChannelHandlerContext ctx, int capacity)
  {
    ByteBufAllocator alloc = ctx.alloc();
    if (wantsDirectBuffer) {
      return alloc.directBuffer(capacity);
    }
    return alloc.buffer(capacity);
  }
  




  private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes)
  {
    if (wantsLargeOutboundNetworkBuffer) {
      return allocate(ctx, maxPacketBufferSize);
    }
    return allocate(ctx, Math.min(pendingBytes + 2329, maxPacketBufferSize));
  }
  
  private final class LazyChannelPromise
    extends DefaultPromise<Channel>
  {
    private LazyChannelPromise() {}
    
    protected EventExecutor executor()
    {
      if (ctx == null) {
        throw new IllegalStateException();
      }
      return ctx.executor();
    }
  }
}
