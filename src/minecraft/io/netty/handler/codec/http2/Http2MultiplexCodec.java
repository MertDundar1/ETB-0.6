package io.netty.handler.codec.http2;

import io.netty.channel.Channel;
import io.netty.channel.Channel.Unsafe;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.collection.IntObjectMap.PrimitiveEntry;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;











































public final class Http2MultiplexCodec
  extends ChannelDuplexHandler
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2MultiplexCodec.class);
  
  private final Http2StreamChannelBootstrap bootstrap;
  
  private final List<Http2StreamChannel> channelsToFireChildReadComplete = new ArrayList();
  
  private final boolean server;
  private ChannelHandlerContext ctx;
  private volatile Runnable flushTask;
  private final IntObjectMap<Http2StreamChannel> childChannels = new IntObjectHashMap();
  





  public Http2MultiplexCodec(boolean server, Http2StreamChannelBootstrap bootstrap)
  {
    if (bootstrap.parentChannel() != null) {
      throw new IllegalStateException("The parent channel must not be set on the bootstrap.");
    }
    this.server = server;
    this.bootstrap = new Http2StreamChannelBootstrap(bootstrap);
  }
  
  public void handlerAdded(ChannelHandlerContext ctx)
  {
    this.ctx = ctx;
    bootstrap.parentChannel(ctx.channel());
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
  {
    if (!(cause instanceof Http2Exception.StreamException)) {
      ctx.fireExceptionCaught(cause);
      return;
    }
    
    Http2Exception.StreamException streamEx = (Http2Exception.StreamException)cause;
    try {
      Http2StreamChannel childChannel = (Http2StreamChannel)childChannels.get(streamEx.streamId());
      if (childChannel != null) {
        childChannel.pipeline().fireExceptionCaught(streamEx);
      } else {
        logger.warn(String.format("Exception caught for unknown HTTP/2 stream '%d'", new Object[] { Integer.valueOf(streamEx.streamId()) }), streamEx);
      }
    }
    finally {
      onStreamClosed(streamEx.streamId());
    }
  }
  

  public void flush(ChannelHandlerContext ctx)
  {
    ctx.flush();
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
  {
    if (!(msg instanceof Http2Frame)) {
      ctx.fireChannelRead(msg);
      return;
    }
    if ((msg instanceof Http2StreamFrame)) {
      Http2StreamFrame frame = (Http2StreamFrame)msg;
      int streamId = frame.streamId();
      Http2StreamChannel childChannel = (Http2StreamChannel)childChannels.get(streamId);
      if (childChannel == null)
      {
        ReferenceCountUtil.release(msg);
        throw new Http2Exception.StreamException(streamId, Http2Error.STREAM_CLOSED, String.format("Received %s frame for an unknown stream %d", new Object[] { frame.name(), Integer.valueOf(streamId) }));
      }
      
      fireChildReadAndRegister(childChannel, frame);
    } else if ((msg instanceof Http2GoAwayFrame)) {
      Http2GoAwayFrame goAwayFrame = (Http2GoAwayFrame)msg;
      for (IntObjectMap.PrimitiveEntry<Http2StreamChannel> entry : childChannels.entries()) {
        Http2StreamChannel childChannel = (Http2StreamChannel)entry.value();
        int streamId = entry.key();
        if ((streamId > goAwayFrame.lastStreamId()) && (Http2CodecUtil.isOutboundStream(server, streamId))) {
          childChannel.pipeline().fireUserEventTriggered(goAwayFrame.retainedDuplicate());
        }
      }
      goAwayFrame.release();
    }
    else {
      ReferenceCountUtil.release(msg);
      throw new UnsupportedMessageTypeException(msg, new Class[0]);
    }
  }
  

  private void fireChildReadAndRegister(Http2StreamChannel childChannel, Http2StreamFrame frame)
  {
    childChannel.fireChildRead(frame);
    if (!inStreamsToFireChildReadComplete) {
      channelsToFireChildReadComplete.add(childChannel);
      inStreamsToFireChildReadComplete = true;
    }
  }
  
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
  {
    if ((evt instanceof Http2StreamActiveEvent)) {
      Http2StreamActiveEvent activeEvent = (Http2StreamActiveEvent)evt;
      onStreamActive(activeEvent.streamId(), activeEvent.headers());
    } else if ((evt instanceof Http2StreamClosedEvent)) {
      onStreamClosed(((Http2StreamClosedEvent)evt).streamId());
    } else {
      ctx.fireUserEventTriggered(evt);
    }
  }
  
  private void onStreamActive(int streamId, Http2HeadersFrame headersFrame) {
    Http2StreamChannel childChannel;
    if (Http2CodecUtil.isOutboundStream(server, streamId)) {
      if (!(headersFrame instanceof ChannelCarryingHeadersFrame)) {
        throw new IllegalArgumentException("needs to be wrapped");
      }
      Http2StreamChannel childChannel = ((ChannelCarryingHeadersFrame)headersFrame).channel();
      childChannel.streamId(streamId);
    } else {
      ChannelFuture future = bootstrap.connect(streamId);
      childChannel = (Http2StreamChannel)future.channel();
    }
    
    Http2StreamChannel existing = (Http2StreamChannel)childChannels.put(streamId, childChannel);
    assert (existing == null);
  }
  
  private void onStreamClosed(int streamId) {
    final Http2StreamChannel childChannel = (Http2StreamChannel)childChannels.remove(streamId);
    if (childChannel != null) {
      EventLoop eventLoop = childChannel.eventLoop();
      if (eventLoop.inEventLoop()) {
        onStreamClosed0(childChannel);
      } else {
        eventLoop.execute(new Runnable()
        {
          public void run() {
            Http2MultiplexCodec.this.onStreamClosed0(childChannel);
          }
        });
      }
    }
  }
  
  private void onStreamClosed0(Http2StreamChannel childChannel) {
    assert (childChannel.eventLoop().inEventLoop());
    
    onStreamClosedFired = true;
    childChannel.fireChildRead(AbstractHttp2StreamChannel.CLOSE_MESSAGE);
  }
  
  void flushFromStreamChannel() {
    EventExecutor executor = ctx.executor();
    if (executor.inEventLoop()) {
      flush(ctx);
    } else {
      Runnable task = flushTask;
      if (task == null) {
        task = this.flushTask = new Runnable()
        {
          public void run() {
            flush(ctx);
          }
        };
      }
      executor.execute(task);
    }
  }
  
  void writeFromStreamChannel(Object msg, boolean flush) {
    writeFromStreamChannel(msg, ctx.newPromise(), flush);
  }
  
  void writeFromStreamChannel(final Object msg, final ChannelPromise promise, final boolean flush) {
    EventExecutor executor = ctx.executor();
    if (executor.inEventLoop()) {
      writeFromStreamChannel0(msg, flush, promise);
    } else {
      try {
        executor.execute(new Runnable()
        {
          public void run() {
            Http2MultiplexCodec.this.writeFromStreamChannel0(msg, flush, promise);
          }
        });
      } catch (Throwable cause) {
        promise.setFailure(cause);
      }
    }
  }
  
  private void writeFromStreamChannel0(Object msg, boolean flush, ChannelPromise promise) {
    try {
      write(ctx, msg, promise);
    } catch (Throwable cause) {
      promise.tryFailure(cause);
    }
    if (flush) {
      flush(ctx);
    }
  }
  



  public void channelReadComplete(ChannelHandlerContext ctx)
  {
    for (int i = 0; i < channelsToFireChildReadComplete.size(); i++) {
      Http2StreamChannel childChannel = (Http2StreamChannel)channelsToFireChildReadComplete.get(i);
      
      inStreamsToFireChildReadComplete = false;
      childChannel.fireChildReadComplete();
    }
    channelsToFireChildReadComplete.clear();
  }
  


  ChannelFuture createStreamChannel(Channel parentChannel, EventLoopGroup group, ChannelHandler handler, Map<ChannelOption<?>, Object> options, Map<AttributeKey<?>, Object> attrs, int streamId)
  {
    Http2StreamChannel channel = new Http2StreamChannel(parentChannel);
    if (Http2CodecUtil.isStreamIdValid(streamId)) {
      assert (!Http2CodecUtil.isOutboundStream(server, streamId));
      assert (ctx.channel().eventLoop().inEventLoop());
      channel.streamId(streamId);
    }
    channel.pipeline().addLast(new ChannelHandler[] { handler });
    
    initOpts(channel, options);
    initAttrs(channel, attrs);
    
    ChannelFuture future = group.register(channel);
    


    if (future.cause() != null) {
      if (channel.isRegistered()) {
        channel.close();
      } else {
        channel.unsafe().closeForcibly();
      }
    }
    return future;
  }
  
  private static void initOpts(Channel channel, Map<ChannelOption<?>, Object> opts)
  {
    if (opts != null) {
      for (Map.Entry<ChannelOption<?>, Object> e : opts.entrySet()) {
        try {
          if (!channel.config().setOption((ChannelOption)e.getKey(), e.getValue())) {
            logger.warn("Unknown channel option: " + e);
          }
        } catch (Throwable t) {
          logger.warn("Failed to set a channel option: " + channel, t);
        }
      }
    }
  }
  
  private static void initAttrs(Channel channel, Map<AttributeKey<?>, Object> attrs)
  {
    if (attrs != null) {
      for (Map.Entry<AttributeKey<?>, Object> e : attrs.entrySet()) {
        channel.attr((AttributeKey)e.getKey()).set(e.getValue());
      }
    }
  }
  
  final class Http2StreamChannel
    extends AbstractHttp2StreamChannel
    implements ChannelFutureListener
  {
    boolean onStreamClosedFired;
    boolean inStreamsToFireChildReadComplete;
    
    Http2StreamChannel(Channel parentChannel)
    {
      super();
    }
    
    protected void doClose() throws Exception
    {
      if ((!onStreamClosedFired) && (Http2CodecUtil.isStreamIdValid(streamId()))) {
        Http2StreamFrame resetFrame = new DefaultHttp2ResetFrame(Http2Error.CANCEL).streamId(streamId());
        writeFromStreamChannel(resetFrame, true);
      }
      super.doClose();
    }
    
    protected void doWrite(Object msg)
    {
      if (!(msg instanceof Http2StreamFrame)) {
        ReferenceCountUtil.release(msg);
        throw new IllegalArgumentException("Message must be an Http2StreamFrame: " + msg);
      }
      Http2StreamFrame frame = (Http2StreamFrame)msg;
      ChannelPromise promise = ctx.newPromise();
      if (Http2CodecUtil.isStreamIdValid(frame.streamId())) {
        ReferenceCountUtil.release(frame);
        throw new IllegalArgumentException("Stream id must not be set on the frame. Was: " + frame.streamId());
      }
      if (!Http2CodecUtil.isStreamIdValid(streamId())) {
        if (!(frame instanceof Http2HeadersFrame)) {
          throw new IllegalArgumentException("The first frame must be a headers frame. Was: " + frame.name());
        }
        frame = new Http2MultiplexCodec.ChannelCarryingHeadersFrame((Http2HeadersFrame)frame, this);
        
        promise.addListener(this);
      } else {
        frame.streamId(streamId());
      }
      writeFromStreamChannel(frame, promise, false);
    }
    
    protected void doWriteComplete()
    {
      flushFromStreamChannel();
    }
    
    protected EventExecutor preferredEventExecutor()
    {
      return ctx.executor();
    }
    
    protected void bytesConsumed(int bytes)
    {
      ctx.write(new DefaultHttp2WindowUpdateFrame(bytes).streamId(streamId()));
    }
    
    public void operationComplete(ChannelFuture future) throws Exception
    {
      Throwable cause = future.cause();
      if (cause != null) {
        pipeline().fireExceptionCaught(cause);
        close();
      }
    }
  }
  

  private static final class ChannelCarryingHeadersFrame
    implements Http2HeadersFrame
  {
    private final Http2HeadersFrame frame;
    
    private final Http2MultiplexCodec.Http2StreamChannel childChannel;
    
    ChannelCarryingHeadersFrame(Http2HeadersFrame frame, Http2MultiplexCodec.Http2StreamChannel childChannel)
    {
      this.frame = frame;
      this.childChannel = childChannel;
    }
    
    public Http2Headers headers()
    {
      return frame.headers();
    }
    
    public boolean isEndStream()
    {
      return frame.isEndStream();
    }
    
    public int padding()
    {
      return frame.padding();
    }
    
    public Http2StreamFrame streamId(int streamId)
    {
      return frame.streamId(streamId);
    }
    
    public int streamId()
    {
      return frame.streamId();
    }
    
    public String name()
    {
      return frame.name();
    }
    
    Http2MultiplexCodec.Http2StreamChannel channel() {
      return childChannel;
    }
  }
}
