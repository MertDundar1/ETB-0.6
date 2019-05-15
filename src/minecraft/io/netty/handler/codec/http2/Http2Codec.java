package io.netty.handler.codec.http2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;




















public final class Http2Codec
  extends ChannelDuplexHandler
{
  private static final Http2FrameLogger HTTP2_FRAME_LOGGER = new Http2FrameLogger(LogLevel.INFO, Http2Codec.class);
  


  private final Http2FrameCodec frameCodec;
  

  private final Http2MultiplexCodec multiplexCodec;
  


  public Http2Codec(boolean server, ChannelHandler streamHandler)
  {
    this(server, new Http2StreamChannelBootstrap().handler(streamHandler), HTTP2_FRAME_LOGGER);
  }
  





  public Http2Codec(boolean server, Http2StreamChannelBootstrap bootstrap, Http2FrameLogger frameLogger)
  {
    this(server, bootstrap, new DefaultHttp2FrameWriter(), frameLogger);
  }
  

  Http2Codec(boolean server, Http2StreamChannelBootstrap bootstrap, Http2FrameWriter frameWriter, Http2FrameLogger frameLogger)
  {
    frameCodec = new Http2FrameCodec(server, frameWriter, frameLogger);
    multiplexCodec = new Http2MultiplexCodec(server, bootstrap);
  }
  
  Http2FrameCodec frameCodec() {
    return frameCodec;
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    ctx.pipeline().addBefore(ctx.executor(), ctx.name(), null, frameCodec);
    ctx.pipeline().addBefore(ctx.executor(), ctx.name(), null, multiplexCodec);
    
    ctx.pipeline().remove(this);
  }
}
