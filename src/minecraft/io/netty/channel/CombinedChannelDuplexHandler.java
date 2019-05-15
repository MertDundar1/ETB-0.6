package io.netty.channel;

import java.net.SocketAddress;



























public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler>
  extends ChannelDuplexHandler
{
  private I inboundHandler;
  private O outboundHandler;
  
  protected CombinedChannelDuplexHandler() {}
  
  public CombinedChannelDuplexHandler(I inboundHandler, O outboundHandler)
  {
    init(inboundHandler, outboundHandler);
  }
  







  protected final void init(I inboundHandler, O outboundHandler)
  {
    validate(inboundHandler, outboundHandler);
    this.inboundHandler = inboundHandler;
    this.outboundHandler = outboundHandler;
  }
  
  private void validate(I inboundHandler, O outboundHandler) {
    if (this.inboundHandler != null) {
      throw new IllegalStateException("init() can not be invoked if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with non-default constructor.");
    }
    


    if (inboundHandler == null) {
      throw new NullPointerException("inboundHandler");
    }
    if (outboundHandler == null) {
      throw new NullPointerException("outboundHandler");
    }
    if ((inboundHandler instanceof ChannelOutboundHandler)) {
      throw new IllegalArgumentException("inboundHandler must not implement " + ChannelOutboundHandler.class.getSimpleName() + " to get combined.");
    }
    

    if ((outboundHandler instanceof ChannelInboundHandler)) {
      throw new IllegalArgumentException("outboundHandler must not implement " + ChannelInboundHandler.class.getSimpleName() + " to get combined.");
    }
  }
  

  protected final I inboundHandler()
  {
    return inboundHandler;
  }
  
  protected final O outboundHandler() {
    return outboundHandler;
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    if (inboundHandler == null) {
      throw new IllegalStateException("init() must be invoked before being added to a " + ChannelPipeline.class.getSimpleName() + " if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with the default constructor.");
    }
    

    try
    {
      inboundHandler.handlerAdded(ctx);
    } finally {
      outboundHandler.handlerAdded(ctx);
    }
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
  {
    try {
      inboundHandler.handlerRemoved(ctx);
    } finally {
      outboundHandler.handlerRemoved(ctx);
    }
  }
  
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception
  {
    inboundHandler.channelRegistered(ctx);
  }
  
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception
  {
    inboundHandler.channelUnregistered(ctx);
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception
  {
    inboundHandler.channelActive(ctx);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    inboundHandler.channelInactive(ctx);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
  {
    inboundHandler.exceptionCaught(ctx, cause);
  }
  
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
  {
    inboundHandler.userEventTriggered(ctx, evt);
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
  {
    inboundHandler.channelRead(ctx, msg);
  }
  
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
  {
    inboundHandler.channelReadComplete(ctx);
  }
  

  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
    throws Exception
  {
    outboundHandler.bind(ctx, localAddress, promise);
  }
  


  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    throws Exception
  {
    outboundHandler.connect(ctx, remoteAddress, localAddress, promise);
  }
  
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    outboundHandler.disconnect(ctx, promise);
  }
  
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    outboundHandler.close(ctx, promise);
  }
  
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    outboundHandler.deregister(ctx, promise);
  }
  
  public void read(ChannelHandlerContext ctx) throws Exception
  {
    outboundHandler.read(ctx);
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
  {
    outboundHandler.write(ctx, msg, promise);
  }
  
  public void flush(ChannelHandlerContext ctx) throws Exception
  {
    outboundHandler.flush(ctx);
  }
  
  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
  {
    inboundHandler.channelWritabilityChanged(ctx);
  }
}
