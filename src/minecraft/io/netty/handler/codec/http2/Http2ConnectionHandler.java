package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;








































public class Http2ConnectionHandler
  extends ByteToMessageDecoder
  implements Http2LifecycleManager, ChannelOutboundHandler
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2ConnectionHandler.class);
  
  private final Http2ConnectionDecoder decoder;
  private final Http2ConnectionEncoder encoder;
  private final Http2Settings initialSettings;
  private ChannelFutureListener closeListener;
  private BaseDecoder byteDecoder;
  private long gracefulShutdownTimeoutMillis;
  
  protected Http2ConnectionHandler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings)
  {
    this.initialSettings = ((Http2Settings)ObjectUtil.checkNotNull(initialSettings, "initialSettings"));
    this.decoder = ((Http2ConnectionDecoder)ObjectUtil.checkNotNull(decoder, "decoder"));
    this.encoder = ((Http2ConnectionEncoder)ObjectUtil.checkNotNull(encoder, "encoder"));
    if (encoder.connection() != decoder.connection()) {
      throw new IllegalArgumentException("Encoder and Decoder do not share the same connection object");
    }
  }
  



  public long gracefulShutdownTimeoutMillis()
  {
    return gracefulShutdownTimeoutMillis;
  }
  





  public void gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis)
  {
    if (gracefulShutdownTimeoutMillis < 0L) {
      throw new IllegalArgumentException("gracefulShutdownTimeoutMillis: " + gracefulShutdownTimeoutMillis + " (expected: >= 0)");
    }
    
    this.gracefulShutdownTimeoutMillis = gracefulShutdownTimeoutMillis;
  }
  
  public Http2Connection connection() {
    return encoder.connection();
  }
  
  public Http2ConnectionDecoder decoder() {
    return decoder;
  }
  
  public Http2ConnectionEncoder encoder() {
    return encoder;
  }
  
  private boolean prefaceSent() {
    return (byteDecoder != null) && (byteDecoder.prefaceSent());
  }
  


  public void onHttpClientUpgrade()
    throws Http2Exception
  {
    if (connection().isServer()) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Client-side HTTP upgrade requested for a server", new Object[0]);
    }
    if ((prefaceSent()) || (decoder.prefaceReceived())) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP upgrade must occur before HTTP/2 preface is sent or received", new Object[0]);
    }
    

    connection().local().createStream(1, true);
  }
  


  public void onHttpServerUpgrade(Http2Settings settings)
    throws Http2Exception
  {
    if (!connection().isServer()) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server-side HTTP upgrade requested for a client", new Object[0]);
    }
    if ((prefaceSent()) || (decoder.prefaceReceived())) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP upgrade must occur before HTTP/2 preface is sent or received", new Object[0]);
    }
    

    encoder.remoteSettings(settings);
    

    connection().remote().createStream(1, true);
  }
  
  public void flush(ChannelHandlerContext ctx)
    throws Http2Exception
  {
    encoder.flowController().writePendingBytes();
    try {
      ctx.flush();
    } catch (Throwable t) {
      throw new Http2Exception(Http2Error.INTERNAL_ERROR, "Error flushing", t); } }
  
  private abstract class BaseDecoder { private BaseDecoder() {}
    
    public abstract void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf, List<Object> paramList) throws Exception;
    
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
    {}
    
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {}
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { encoder().close();
      decoder().close();
      


      connection().close(ctx.voidPromise());
    }
    




    public boolean prefaceSent() { return true; }
  }
  
  private final class PrefaceDecoder extends Http2ConnectionHandler.BaseDecoder {
    private ByteBuf clientPrefaceString;
    private boolean prefaceSent;
    
    public PrefaceDecoder(ChannelHandlerContext ctx) {
      super(null);
      clientPrefaceString = Http2ConnectionHandler.clientPrefaceString(encoder.connection());
      

      sendPreface(ctx);
    }
    
    public boolean prefaceSent()
    {
      return prefaceSent;
    }
    
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
      try {
        if ((ctx.channel().isActive()) && (readClientPrefaceString(in)) && (verifyFirstFrameIsSettings(in)))
        {
          byteDecoder = new Http2ConnectionHandler.FrameDecoder(Http2ConnectionHandler.this, null);
          byteDecoder.decode(ctx, in, out);
        }
      } catch (Throwable e) {
        onError(ctx, e);
      }
    }
    
    public void channelActive(ChannelHandlerContext ctx)
      throws Exception
    {
      sendPreface(ctx);
    }
    
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
      cleanup();
      super.channelInactive(ctx);
    }
    


    public void handlerRemoved(ChannelHandlerContext ctx)
      throws Exception
    {
      cleanup();
    }
    


    private void cleanup()
    {
      if (clientPrefaceString != null) {
        clientPrefaceString.release();
        clientPrefaceString = null;
      }
    }
    




    private boolean readClientPrefaceString(ByteBuf in)
      throws Http2Exception
    {
      if (clientPrefaceString == null) {
        return true;
      }
      
      int prefaceRemaining = clientPrefaceString.readableBytes();
      int bytesRead = Math.min(in.readableBytes(), prefaceRemaining);
      

      if ((bytesRead == 0) || (!ByteBufUtil.equals(in, in.readerIndex(), clientPrefaceString, clientPrefaceString.readerIndex(), bytesRead)))
      {

        String receivedBytes = ByteBufUtil.hexDump(in, in.readerIndex(), Math.min(in.readableBytes(), clientPrefaceString.readableBytes()));
        
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP/2 client preface string missing or corrupt. Hex dump for received bytes: %s", new Object[] { receivedBytes });
      }
      
      in.skipBytes(bytesRead);
      clientPrefaceString.skipBytes(bytesRead);
      
      if (!clientPrefaceString.isReadable())
      {
        clientPrefaceString.release();
        clientPrefaceString = null;
        return true;
      }
      return false;
    }
    






    private boolean verifyFirstFrameIsSettings(ByteBuf in)
      throws Http2Exception
    {
      if (in.readableBytes() < 5)
      {
        return false;
      }
      
      short frameType = in.getUnsignedByte(in.readerIndex() + 3);
      short flags = in.getUnsignedByte(in.readerIndex() + 4);
      if ((frameType != 4) || ((flags & 0x1) != 0)) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "First received frame was not SETTINGS. Hex dump for first 5 bytes: %s", new Object[] { ByteBufUtil.hexDump(in, in.readerIndex(), 5) });
      }
      

      return true;
    }
    


    private void sendPreface(ChannelHandlerContext ctx)
    {
      if ((prefaceSent) || (!ctx.channel().isActive())) {
        return;
      }
      
      prefaceSent = true;
      
      if (!connection().isServer())
      {
        ctx.write(Http2CodecUtil.connectionPrefaceBuf()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      }
      

      encoder.writeSettings(ctx, initialSettings, ctx.newPromise()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
  }
  
  private final class FrameDecoder extends Http2ConnectionHandler.BaseDecoder {
    private FrameDecoder() { super(null); }
    
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      try {
        decoder.decodeFrame(ctx, in, out);
      } catch (Throwable e) {
        onError(ctx, e);
      }
    }
  }
  
  public void handlerAdded(ChannelHandlerContext ctx)
    throws Exception
  {
    encoder.lifecycleManager(this);
    decoder.lifecycleManager(this);
    encoder.flowController().channelHandlerContext(ctx);
    decoder.flowController().channelHandlerContext(ctx);
    byteDecoder = new PrefaceDecoder(ctx);
  }
  
  protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception
  {
    if (byteDecoder != null) {
      byteDecoder.handlerRemoved(ctx);
      byteDecoder = null;
    }
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception
  {
    if (byteDecoder == null) {
      byteDecoder = new PrefaceDecoder(ctx);
    }
    byteDecoder.channelActive(ctx);
    super.channelActive(ctx);
  }
  
  public void channelInactive(ChannelHandlerContext ctx)
    throws Exception
  {
    super.channelInactive(ctx);
    if (byteDecoder != null) {
      byteDecoder.channelInactive(ctx);
      byteDecoder = null;
    }
  }
  
  public void channelWritabilityChanged(ChannelHandlerContext ctx)
    throws Exception
  {
    try
    {
      if (ctx.channel().isWritable()) {
        flush(ctx);
      }
      encoder.flowController().channelWritabilityChanged();
    } finally {
      super.channelWritabilityChanged(ctx);
    }
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    byteDecoder.decode(ctx, in, out);
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
  
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    ctx.disconnect(promise);
  }
  
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    promise = promise.unvoid();
    
    if (!ctx.channel().isActive()) {
      ctx.close(promise);
      return;
    }
    





    ChannelFuture future = connection().goAwaySent() ? ctx.write(Unpooled.EMPTY_BUFFER) : goAway(ctx, null);
    ctx.flush();
    doGracefulShutdown(ctx, future, promise);
  }
  
  private void doGracefulShutdown(ChannelHandlerContext ctx, ChannelFuture future, ChannelPromise promise) {
    if (isGracefulShutdownComplete())
    {
      future.addListener(new ClosingChannelFutureListener(ctx, promise));
    }
    else {
      closeListener = new ClosingChannelFutureListener(ctx, promise, gracefulShutdownTimeoutMillis, TimeUnit.MILLISECONDS);
    }
  }
  
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise)
    throws Exception
  {
    ctx.deregister(promise);
  }
  
  public void read(ChannelHandlerContext ctx) throws Exception
  {
    ctx.read();
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
  {
    ctx.write(msg, promise);
  }
  
  public void channelReadComplete(ChannelHandlerContext ctx)
    throws Exception
  {
    try
    {
      flush(ctx);
    } finally {
      super.channelReadComplete(ctx);
    }
  }
  


  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    throws Exception
  {
    if (Http2CodecUtil.getEmbeddedHttp2Exception(cause) != null)
    {
      onError(ctx, cause);
    } else {
      super.exceptionCaught(ctx, cause);
    }
  }
  







  public void closeStreamLocal(Http2Stream stream, ChannelFuture future)
  {
    switch (stream.state()) {
    case HALF_CLOSED_LOCAL: 
    case OPEN: 
      stream.closeLocalSide();
      break;
    default: 
      closeStream(stream, future);
    }
    
  }
  







  public void closeStreamRemote(Http2Stream stream, ChannelFuture future)
  {
    switch (stream.state()) {
    case OPEN: 
    case HALF_CLOSED_REMOTE: 
      stream.closeRemoteSide();
      break;
    default: 
      closeStream(stream, future);
    }
    
  }
  
  public void closeStream(Http2Stream stream, ChannelFuture future)
  {
    stream.close();
    
    if (future.isDone()) {
      checkCloseConnection(future);
    } else {
      future.addListener(new ChannelFutureListener()
      {
        public void operationComplete(ChannelFuture future) throws Exception {
          Http2ConnectionHandler.this.checkCloseConnection(future);
        }
      });
    }
  }
  



  public void onError(ChannelHandlerContext ctx, Throwable cause)
  {
    Http2Exception embedded = Http2CodecUtil.getEmbeddedHttp2Exception(cause);
    if (Http2Exception.isStreamError(embedded)) {
      onStreamError(ctx, cause, (Http2Exception.StreamException)embedded);
    } else if ((embedded instanceof Http2Exception.CompositeStreamException)) {
      Http2Exception.CompositeStreamException compositException = (Http2Exception.CompositeStreamException)embedded;
      for (Http2Exception.StreamException streamException : compositException) {
        onStreamError(ctx, cause, streamException);
      }
    } else {
      onConnectionError(ctx, cause, embedded);
    }
    ctx.flush();
  }
  




  protected boolean isGracefulShutdownComplete()
  {
    return connection().numActiveStreams() == 0;
  }
  








  protected void onConnectionError(ChannelHandlerContext ctx, Throwable cause, Http2Exception http2Ex)
  {
    if (http2Ex == null) {
      http2Ex = new Http2Exception(Http2Error.INTERNAL_ERROR, cause.getMessage(), cause);
    }
    
    ChannelPromise promise = ctx.newPromise();
    ChannelFuture future = goAway(ctx, http2Ex);
    switch (http2Ex.shutdownHint()) {
    case GRACEFUL_SHUTDOWN: 
      doGracefulShutdown(ctx, future, promise);
      break;
    default: 
      future.addListener(new ClosingChannelFutureListener(ctx, promise));
    }
    
  }
  








  protected void onStreamError(ChannelHandlerContext ctx, Throwable cause, Http2Exception.StreamException http2Ex)
  {
    resetStream(ctx, http2Ex.streamId(), http2Ex.error().code(), ctx.newPromise());
  }
  
  protected Http2FrameWriter frameWriter() {
    return encoder().frameWriter();
  }
  





  private ChannelFuture resetUnknownStream(final ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise)
  {
    ChannelFuture future = frameWriter().writeRstStream(ctx, streamId, errorCode, promise);
    if (future.isDone()) {
      closeConnectionOnError(ctx, future);
    } else {
      future.addListener(new ChannelFutureListener()
      {
        public void operationComplete(ChannelFuture future) throws Exception {
          Http2ConnectionHandler.this.closeConnectionOnError(ctx, future);
        }
      });
    }
    return future;
  }
  

  public ChannelFuture resetStream(final ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise)
  {
    promise = promise.unvoid();
    final Http2Stream stream = connection().stream(streamId);
    if (stream == null) {
      return resetUnknownStream(ctx, streamId, errorCode, promise);
    }
    
    if (stream.isResetSent())
    {
      return promise.setSuccess(); }
    ChannelFuture future;
    ChannelFuture future;
    if (stream.state() == Http2Stream.State.IDLE)
    {
      future = promise.setSuccess();
    } else {
      future = frameWriter().writeRstStream(ctx, streamId, errorCode, promise);
    }
    


    stream.resetSent();
    
    if (future.isDone()) {
      processRstStreamWriteResult(ctx, stream, future);
    } else {
      future.addListener(new ChannelFutureListener()
      {
        public void operationComplete(ChannelFuture future) throws Exception {
          Http2ConnectionHandler.this.processRstStreamWriteResult(ctx, stream, future);
        }
      });
    }
    
    return future;
  }
  
  public ChannelFuture goAway(final ChannelHandlerContext ctx, final int lastStreamId, final long errorCode, ByteBuf debugData, ChannelPromise promise)
  {
    try
    {
      promise = promise.unvoid();
      Http2Connection connection = connection();
      if (connection().goAwaySent())
      {

        if (lastStreamId == connection().remote().lastStreamKnownByPeer())
        {
          debugData.release();
          return promise.setSuccess();
        }
        if (lastStreamId > connection.remote().lastStreamKnownByPeer()) {
          throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Last stream identifier must not increase between sending multiple GOAWAY frames (was '%d', is '%d').", new Object[] { Integer.valueOf(connection.remote().lastStreamKnownByPeer()), Integer.valueOf(lastStreamId) });
        }
      }
      


      connection.goAwaySent(lastStreamId, errorCode, debugData);
      


      debugData.retain();
      ChannelFuture future = frameWriter().writeGoAway(ctx, lastStreamId, errorCode, debugData, promise);
      
      if (future.isDone()) {
        processGoAwayWriteResult(ctx, lastStreamId, errorCode, debugData, future);
      } else {
        future.addListener(new ChannelFutureListener()
        {
          public void operationComplete(ChannelFuture future) throws Exception {
            Http2ConnectionHandler.processGoAwayWriteResult(ctx, lastStreamId, errorCode, val$debugData, future);
          }
        });
      }
      
      return future;
    } catch (Throwable cause) {
      debugData.release();
      return promise.setFailure(cause);
    }
  }
  





  private void checkCloseConnection(ChannelFuture future)
  {
    if ((this.closeListener != null) && (isGracefulShutdownComplete())) {
      ChannelFutureListener closeListener = this.closeListener;
      

      this.closeListener = null;
      try {
        closeListener.operationComplete(future);
      } catch (Exception e) {
        throw new IllegalStateException("Close listener threw an unexpected exception", e);
      }
    }
  }
  



  private ChannelFuture goAway(ChannelHandlerContext ctx, Http2Exception cause)
  {
    long errorCode = cause != null ? cause.error().code() : Http2Error.NO_ERROR.code();
    int lastKnownStream = connection().remote().lastStreamCreated();
    return goAway(ctx, lastKnownStream, errorCode, Http2CodecUtil.toByteBuf(ctx, cause), ctx.newPromise());
  }
  
  private void processRstStreamWriteResult(ChannelHandlerContext ctx, Http2Stream stream, ChannelFuture future) {
    if (future.isSuccess()) {
      closeStream(stream, future);
    }
    else {
      onConnectionError(ctx, future.cause(), null);
    }
  }
  
  private void closeConnectionOnError(ChannelHandlerContext ctx, ChannelFuture future) {
    if (!future.isSuccess()) {
      onConnectionError(ctx, future.cause(), null);
    }
  }
  


  private static ByteBuf clientPrefaceString(Http2Connection connection)
  {
    return connection.isServer() ? Http2CodecUtil.connectionPrefaceBuf() : null;
  }
  
  private static void processGoAwayWriteResult(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelFuture future)
  {
    try {
      if (future.isSuccess()) {
        if (errorCode != Http2Error.NO_ERROR.code()) {
          if (logger.isDebugEnabled()) {
            logger.debug("{} Sent GOAWAY: lastStreamId '{}', errorCode '{}', debugData '{}'. Forcing shutdown of the connection.", new Object[] { ctx.channel(), Integer.valueOf(lastStreamId), Long.valueOf(errorCode), debugData.toString(CharsetUtil.UTF_8), future.cause() });
          }
          

          ctx.close();
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug("{} Sending GOAWAY failed: lastStreamId '{}', errorCode '{}', debugData '{}'. Forcing shutdown of the connection.", new Object[] { ctx.channel(), Integer.valueOf(lastStreamId), Long.valueOf(errorCode), debugData.toString(CharsetUtil.UTF_8), future.cause() });
        }
        

        ctx.close();
      }
    }
    finally {
      debugData.release();
    }
  }
  
  private static final class ClosingChannelFutureListener
    implements ChannelFutureListener
  {
    private final ChannelHandlerContext ctx;
    private final ChannelPromise promise;
    private final ScheduledFuture<?> timeoutTask;
    
    ClosingChannelFutureListener(ChannelHandlerContext ctx, ChannelPromise promise)
    {
      this.ctx = ctx;
      this.promise = promise;
      timeoutTask = null;
    }
    
    ClosingChannelFutureListener(final ChannelHandlerContext ctx, final ChannelPromise promise, long timeout, TimeUnit unit)
    {
      this.ctx = ctx;
      this.promise = promise;
      timeoutTask = ctx.executor().schedule(new Runnable()
      {

        public void run() { ctx.close(promise); } }, timeout, unit);
    }
    

    public void operationComplete(ChannelFuture sentGoAwayFuture)
      throws Exception
    {
      if (timeoutTask != null) {
        timeoutTask.cancel(false);
      }
      ctx.close(promise);
    }
  }
}
