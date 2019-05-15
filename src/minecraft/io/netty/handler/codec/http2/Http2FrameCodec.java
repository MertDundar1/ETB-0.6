package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeEvent;
import io.netty.handler.logging.LogLevel;
import io.netty.util.ReferenceCountUtil;


























































































public class Http2FrameCodec
  extends ChannelDuplexHandler
{
  private static final Http2FrameLogger HTTP2_FRAME_LOGGER = new Http2FrameLogger(LogLevel.INFO, Http2FrameCodec.class);
  

  private final Http2ConnectionHandler http2Handler;
  
  private final boolean server;
  
  private ChannelHandlerContext ctx;
  
  private ChannelHandlerContext http2HandlerCtx;
  

  public Http2FrameCodec(boolean server)
  {
    this(server, HTTP2_FRAME_LOGGER);
  }
  




  public Http2FrameCodec(boolean server, Http2FrameLogger frameLogger)
  {
    this(server, new DefaultHttp2FrameWriter(), frameLogger);
  }
  
  Http2FrameCodec(boolean server, Http2FrameWriter frameWriter, Http2FrameLogger frameLogger)
  {
    Http2Connection connection = new DefaultHttp2Connection(server);
    frameWriter = new Http2OutboundFrameLogger(frameWriter, frameLogger);
    Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, frameWriter);
    Http2FrameReader frameReader = new DefaultHttp2FrameReader();
    Http2FrameReader reader = new Http2InboundFrameLogger(frameReader, frameLogger);
    Http2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, reader);
    decoder.frameListener(new FrameListener(null));
    http2Handler = new InternalHttp2ConnectionHandler(decoder, encoder, new Http2Settings());
    http2Handler.connection().addListener(new ConnectionListener(null));
    this.server = server;
  }
  
  Http2ConnectionHandler connectionHandler() {
    return http2Handler;
  }
  


  public void handlerAdded(ChannelHandlerContext ctx)
    throws Exception
  {
    this.ctx = ctx;
    ctx.pipeline().addBefore(ctx.executor(), ctx.name(), null, http2Handler);
    http2HandlerCtx = ctx.pipeline().context(http2Handler);
  }
  


  public void handlerRemoved(ChannelHandlerContext ctx)
    throws Exception
  {
    ctx.pipeline().remove(http2Handler);
  }
  



  public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    throws Exception
  {
    if (!(evt instanceof HttpServerUpgradeHandler.UpgradeEvent)) {
      super.userEventTriggered(ctx, evt);
      return;
    }
    
    HttpServerUpgradeHandler.UpgradeEvent upgrade = (HttpServerUpgradeHandler.UpgradeEvent)evt;
    ctx.fireUserEventTriggered(upgrade.retain());
    try {
      Http2Stream stream = http2Handler.connection().stream(1);
      


      new ConnectionListener(null).onStreamActive(stream);
      upgrade.upgradeRequest().headers().setInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), 1);
      
      new InboundHttpToHttp2Adapter(http2Handler.connection(), http2Handler.decoder().frameListener()).channelRead(ctx, upgrade.upgradeRequest().retain());
    }
    finally {
      upgrade.release();
    }
  }
  

  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
  {
    ctx.fireExceptionCaught(cause);
  }
  



  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
  {
    try
    {
      if ((msg instanceof Http2WindowUpdateFrame)) {
        Http2WindowUpdateFrame frame = (Http2WindowUpdateFrame)msg;
        consumeBytes(frame.streamId(), frame.windowSizeIncrement(), promise);
      } else if ((msg instanceof Http2StreamFrame)) {
        writeStreamFrame((Http2StreamFrame)msg, promise);
      } else if ((msg instanceof Http2GoAwayFrame)) {
        writeGoAwayFrame((Http2GoAwayFrame)msg, promise);
      } else {
        throw new UnsupportedMessageTypeException(msg, new Class[0]);
      }
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }
  
  private void consumeBytes(int streamId, int bytes, ChannelPromise promise) {
    try {
      Http2Stream stream = http2Handler.connection().stream(streamId);
      ((Http2LocalFlowController)http2Handler.connection().local().flowController()).consumeBytes(stream, bytes);
      
      promise.setSuccess();
    } catch (Throwable t) {
      promise.setFailure(t);
    }
  }
  
  private void writeGoAwayFrame(Http2GoAwayFrame frame, ChannelPromise promise) {
    if (frame.lastStreamId() > -1) {
      throw new IllegalArgumentException("Last stream id must not be set on GOAWAY frame");
    }
    
    int lastStreamCreated = http2Handler.connection().remote().lastStreamCreated();
    int lastStreamId = lastStreamCreated + frame.extraStreamIds() * 2;
    
    if (lastStreamId < lastStreamCreated) {
      lastStreamId = Integer.MAX_VALUE;
    }
    http2Handler.goAway(http2HandlerCtx, lastStreamId, frame.errorCode(), frame.content().retain(), promise);
  }
  
  private void writeStreamFrame(Http2StreamFrame frame, ChannelPromise promise)
  {
    if ((frame instanceof Http2DataFrame)) {
      Http2DataFrame dataFrame = (Http2DataFrame)frame;
      http2Handler.encoder().writeData(http2HandlerCtx, frame.streamId(), dataFrame.content().retain(), dataFrame.padding(), dataFrame.isEndStream(), promise);
    }
    else if ((frame instanceof Http2HeadersFrame)) {
      writeHeadersFrame((Http2HeadersFrame)frame, promise);
    } else if ((frame instanceof Http2ResetFrame)) {
      Http2ResetFrame rstFrame = (Http2ResetFrame)frame;
      http2Handler.resetStream(http2HandlerCtx, frame.streamId(), rstFrame.errorCode(), promise);
    } else {
      throw new UnsupportedMessageTypeException(frame, new Class[0]);
    }
  }
  
  private void writeHeadersFrame(Http2HeadersFrame headersFrame, ChannelPromise promise) {
    int streamId = headersFrame.streamId();
    if (!Http2CodecUtil.isStreamIdValid(streamId)) {
      Http2Connection.Endpoint<Http2LocalFlowController> localEndpoint = http2Handler.connection().local();
      streamId = localEndpoint.incrementAndGetNextStreamId();
      
      try
      {
        localEndpoint.createStream(streamId, false);
      } catch (Http2Exception e) {
        promise.setFailure(e);
        return;
      }
      ctx.fireUserEventTriggered(new Http2StreamActiveEvent(streamId, headersFrame));
    }
    http2Handler.encoder().writeHeaders(http2HandlerCtx, streamId, headersFrame.headers(), headersFrame.padding(), headersFrame.isEndStream(), promise);
  }
  
  private final class ConnectionListener extends Http2ConnectionAdapter {
    private ConnectionListener() {}
    
    public void onStreamActive(Http2Stream stream) {
      if (ctx == null)
      {
        return;
      }
      if (Http2CodecUtil.isOutboundStream(server, stream.id()))
      {
        return;
      }
      ctx.fireUserEventTriggered(new Http2StreamActiveEvent(stream.id()));
    }
    
    public void onStreamClosed(Http2Stream stream)
    {
      ctx.fireUserEventTriggered(new Http2StreamClosedEvent(stream.id()));
    }
    
    public void onGoAwayReceived(int lastStreamId, long errorCode, ByteBuf debugData)
    {
      ctx.fireChannelRead(new DefaultHttp2GoAwayFrame(lastStreamId, errorCode, debugData.retain()));
    }
  }
  
  private static final class InternalHttp2ConnectionHandler extends Http2ConnectionHandler
  {
    InternalHttp2ConnectionHandler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
      super(encoder, initialSettings);
    }
    
    protected void onStreamError(ChannelHandlerContext ctx, Throwable cause, Http2Exception.StreamException http2Ex)
    {
      try
      {
        Http2Stream stream = connection().stream(http2Ex.streamId());
        if (stream == null) {
          return;
        }
        ctx.fireExceptionCaught(http2Ex);
      } finally {
        super.onStreamError(ctx, cause, http2Ex);
      }
    }
  }
  
  private static final class FrameListener extends Http2FrameAdapter {
    private FrameListener() {}
    
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) { Http2ResetFrame rstFrame = new DefaultHttp2ResetFrame(errorCode);
      rstFrame.streamId(streamId);
      ctx.fireChannelRead(rstFrame);
    }
    


    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream)
    {
      onHeadersRead(ctx, streamId, headers, padding, endStream);
    }
    

    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream)
    {
      Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers, endOfStream, padding);
      headersFrame.streamId(streamId);
      ctx.fireChannelRead(headersFrame);
    }
    

    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
    {
      Http2DataFrame dataFrame = new DefaultHttp2DataFrame(data.retain(), endOfStream, padding);
      dataFrame.streamId(streamId);
      ctx.fireChannelRead(dataFrame);
      

      return 0;
    }
  }
}
