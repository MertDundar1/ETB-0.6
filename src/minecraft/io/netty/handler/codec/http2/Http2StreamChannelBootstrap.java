package io.netty.handler.codec.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;






































public class Http2StreamChannelBootstrap
{
  private volatile ParentChannelAndMultiplexCodec channelAndCodec;
  private volatile ChannelHandler handler;
  private volatile EventLoopGroup group;
  private final Map<ChannelOption<?>, Object> options;
  private final Map<AttributeKey<?>, Object> attributes;
  
  public Http2StreamChannelBootstrap()
  {
    options = Collections.synchronizedMap(new LinkedHashMap());
    attributes = Collections.synchronizedMap(new LinkedHashMap());
  }
  
  Http2StreamChannelBootstrap(Http2StreamChannelBootstrap bootstrap0)
  {
    ObjectUtil.checkNotNull(bootstrap0, "bootstrap must not be null");
    channelAndCodec = channelAndCodec;
    handler = handler;
    group = group;
    options = Collections.synchronizedMap(new LinkedHashMap(options));
    attributes = Collections.synchronizedMap(new LinkedHashMap(attributes));
  }
  


  public ChannelFuture connect()
  {
    return connect(-1);
  }
  


  ChannelFuture connect(int streamId)
  {
    validateState();
    
    ParentChannelAndMultiplexCodec channelAndCodec0 = channelAndCodec;
    Channel parentChannel = parentChannel;
    Http2MultiplexCodec multiplexCodec = multiplexCodec;
    
    EventLoopGroup group0 = group;
    group0 = group0 == null ? parentChannel.eventLoop() : group0;
    
    return multiplexCodec.createStreamChannel(parentChannel, group0, handler, options, attributes, streamId);
  }
  






  public Http2StreamChannelBootstrap parentChannel(Channel parent)
  {
    channelAndCodec = new ParentChannelAndMultiplexCodec(parent);
    return this;
  }
  






  public Http2StreamChannelBootstrap handler(ChannelHandler handler)
  {
    this.handler = checkSharable((ChannelHandler)ObjectUtil.checkNotNull(handler, "handler"));
    return this;
  }
  





  public Http2StreamChannelBootstrap group(EventLoopGroup group)
  {
    this.group = group;
    return this;
  }
  



  public <T> Http2StreamChannelBootstrap option(ChannelOption<T> option, T value)
  {
    ObjectUtil.checkNotNull(option, "option must not be null");
    if (value == null) {
      options.remove(option);
    } else {
      options.put(option, value);
    }
    return this;
  }
  



  public <T> Http2StreamChannelBootstrap attr(AttributeKey<T> key, T value)
  {
    ObjectUtil.checkNotNull(key, "key must not be null");
    if (value == null) {
      attributes.remove(key);
    } else {
      attributes.put(key, value);
    }
    return this;
  }
  
  public Channel parentChannel() {
    ParentChannelAndMultiplexCodec channelAndCodec0 = channelAndCodec;
    if (channelAndCodec0 != null) {
      return parentChannel;
    }
    return null;
  }
  
  public ChannelHandler handler() {
    return handler;
  }
  
  public EventLoopGroup group() {
    return group;
  }
  
  public Map<ChannelOption<?>, Object> options() {
    return Collections.unmodifiableMap(new LinkedHashMap(options));
  }
  
  public Map<AttributeKey<?>, Object> attributes() {
    return Collections.unmodifiableMap(new LinkedHashMap(attributes));
  }
  
  private void validateState() {
    ObjectUtil.checkNotNull(handler, "handler must be set");
    ObjectUtil.checkNotNull(channelAndCodec, "parent channel must be set");
  }
  
  private static ChannelHandler checkSharable(ChannelHandler handler) {
    if (!handler.getClass().isAnnotationPresent(ChannelHandler.Sharable.class)) {
      throw new IllegalArgumentException("The handler must be Sharable");
    }
    return handler;
  }
  
  private static class ParentChannelAndMultiplexCodec {
    final Channel parentChannel;
    final Http2MultiplexCodec multiplexCodec;
    
    ParentChannelAndMultiplexCodec(Channel parentChannel) {
      this.parentChannel = checkRegistered((Channel)ObjectUtil.checkNotNull(parentChannel, "parentChannel"));
      multiplexCodec = requireMultiplexCodec(parentChannel.pipeline());
    }
    
    private static Http2MultiplexCodec requireMultiplexCodec(ChannelPipeline pipeline) {
      ChannelHandlerContext ctx = pipeline.context(Http2MultiplexCodec.class);
      if (ctx == null) {
        throw new IllegalArgumentException(Http2MultiplexCodec.class.getSimpleName() + " was not found in the channel pipeline.");
      }
      
      return (Http2MultiplexCodec)ctx.handler();
    }
    
    private static Channel checkRegistered(Channel channel) {
      if (!channel.isRegistered()) {
        throw new IllegalArgumentException("The channel must be registered to an eventloop.");
      }
      return channel;
    }
  }
}
