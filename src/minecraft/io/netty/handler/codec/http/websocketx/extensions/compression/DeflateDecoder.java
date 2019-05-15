package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import java.util.List;


















abstract class DeflateDecoder
  extends WebSocketExtensionDecoder
{
  static final byte[] FRAME_TAIL = { 0, 0, -1, -1 };
  

  private final boolean noContext;
  

  private EmbeddedChannel decoder;
  

  public DeflateDecoder(boolean noContext)
  {
    this.noContext = noContext;
  }
  
  protected abstract boolean appendFrameTail(WebSocketFrame paramWebSocketFrame);
  
  protected abstract int newRsv(WebSocketFrame paramWebSocketFrame);
  
  protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception
  {
    if (decoder == null) {
      if ((!(msg instanceof TextWebSocketFrame)) && (!(msg instanceof BinaryWebSocketFrame))) {
        throw new CodecException("unexpected initial frame type: " + msg.getClass().getName());
      }
      decoder = new EmbeddedChannel(new ChannelHandler[] { ZlibCodecFactory.newZlibDecoder(ZlibWrapper.NONE) });
    }
    
    boolean readable = msg.content().isReadable();
    decoder.writeInbound(new Object[] { msg.content().retain() });
    if (appendFrameTail(msg)) {
      decoder.writeInbound(new Object[] { Unpooled.wrappedBuffer(FRAME_TAIL) });
    }
    
    CompositeByteBuf compositeUncompressedContent = ctx.alloc().compositeBuffer();
    for (;;) {
      ByteBuf partUncompressedContent = (ByteBuf)decoder.readInbound();
      if (partUncompressedContent == null) {
        break;
      }
      if (!partUncompressedContent.isReadable()) {
        partUncompressedContent.release();
      }
      else {
        compositeUncompressedContent.addComponent(true, partUncompressedContent);
      }
    }
    
    if ((readable) && (compositeUncompressedContent.numComponents() <= 0)) {
      compositeUncompressedContent.release();
      throw new CodecException("cannot read uncompressed buffer");
    }
    
    if ((msg.isFinalFragment()) && (noContext)) {
      cleanup();
    }
    
    WebSocketFrame outMsg;
    if ((msg instanceof TextWebSocketFrame)) {
      outMsg = new TextWebSocketFrame(msg.isFinalFragment(), newRsv(msg), compositeUncompressedContent); } else { WebSocketFrame outMsg;
      if ((msg instanceof BinaryWebSocketFrame)) {
        outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), newRsv(msg), compositeUncompressedContent); } else { WebSocketFrame outMsg;
        if ((msg instanceof ContinuationWebSocketFrame)) {
          outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), newRsv(msg), compositeUncompressedContent);
        }
        else
          throw new CodecException("unexpected frame type: " + msg.getClass().getName()); } }
    WebSocketFrame outMsg;
    out.add(outMsg);
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
  {
    cleanup();
    super.handlerRemoved(ctx);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    cleanup();
    super.channelInactive(ctx);
  }
  
  private void cleanup() {
    if (decoder != null)
    {
      if (decoder.finish()) {
        for (;;) {
          ByteBuf buf = (ByteBuf)decoder.readOutbound();
          if (buf == null) {
            break;
          }
          
          buf.release();
        }
      }
      decoder = null;
    }
  }
}
