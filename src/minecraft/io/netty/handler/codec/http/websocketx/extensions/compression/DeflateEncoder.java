package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
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
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import java.util.List;

























abstract class DeflateEncoder
  extends WebSocketExtensionEncoder
{
  private final int compressionLevel;
  private final int windowSize;
  private final boolean noContext;
  private EmbeddedChannel encoder;
  
  public DeflateEncoder(int compressionLevel, int windowSize, boolean noContext)
  {
    this.compressionLevel = compressionLevel;
    this.windowSize = windowSize;
    this.noContext = noContext;
  }
  



  protected abstract int rsv(WebSocketFrame paramWebSocketFrame);
  



  protected abstract boolean removeFrameTail(WebSocketFrame paramWebSocketFrame);
  


  protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out)
    throws Exception
  {
    if (encoder == null) {
      encoder = new EmbeddedChannel(new ChannelHandler[] { ZlibCodecFactory.newZlibEncoder(ZlibWrapper.NONE, compressionLevel, windowSize, 8) });
    }
    

    encoder.writeOutbound(new Object[] { msg.content().retain() });
    
    CompositeByteBuf fullCompressedContent = ctx.alloc().compositeBuffer();
    for (;;) {
      ByteBuf partCompressedContent = (ByteBuf)encoder.readOutbound();
      if (partCompressedContent == null) {
        break;
      }
      if (!partCompressedContent.isReadable()) {
        partCompressedContent.release();
      }
      else
        fullCompressedContent.addComponent(true, partCompressedContent);
    }
    if (fullCompressedContent.numComponents() <= 0) {
      fullCompressedContent.release();
      throw new CodecException("cannot read compressed buffer");
    }
    
    if ((msg.isFinalFragment()) && (noContext)) {
      cleanup();
    }
    ByteBuf compressedContent;
    ByteBuf compressedContent;
    if (removeFrameTail(msg)) {
      int realLength = fullCompressedContent.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.length;
      compressedContent = fullCompressedContent.slice(0, realLength);
    } else {
      compressedContent = fullCompressedContent;
    }
    
    WebSocketFrame outMsg;
    if ((msg instanceof TextWebSocketFrame)) {
      outMsg = new TextWebSocketFrame(msg.isFinalFragment(), rsv(msg), compressedContent); } else { WebSocketFrame outMsg;
      if ((msg instanceof BinaryWebSocketFrame)) {
        outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), rsv(msg), compressedContent); } else { WebSocketFrame outMsg;
        if ((msg instanceof ContinuationWebSocketFrame)) {
          outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), rsv(msg), compressedContent);
        } else
          throw new CodecException("unexpected frame type: " + msg.getClass().getName()); } }
    WebSocketFrame outMsg;
    out.add(outMsg);
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
  {
    cleanup();
    super.handlerRemoved(ctx);
  }
  
  private void cleanup() {
    if (encoder != null)
    {
      if (encoder.finish()) {
        for (;;) {
          ByteBuf buf = (ByteBuf)encoder.readOutbound();
          if (buf == null) {
            break;
          }
          
          buf.release();
        }
      }
      encoder = null;
    }
  }
}
