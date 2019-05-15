package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.AsciiHeadersEncoder;
import io.netty.handler.codec.AsciiHeadersEncoder.NewlineType;
import io.netty.handler.codec.AsciiHeadersEncoder.SeparatorType;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map.Entry;















public class StompSubframeEncoder
  extends MessageToMessageEncoder<StompSubframe>
{
  public StompSubframeEncoder() {}
  
  protected void encode(ChannelHandlerContext ctx, StompSubframe msg, List<Object> out)
    throws Exception
  {
    if ((msg instanceof StompFrame)) {
      StompFrame frame = (StompFrame)msg;
      ByteBuf frameBuf = encodeFrame(frame, ctx);
      out.add(frameBuf);
      ByteBuf contentBuf = encodeContent(frame, ctx);
      out.add(contentBuf);
    } else if ((msg instanceof StompHeadersSubframe)) {
      StompHeadersSubframe frame = (StompHeadersSubframe)msg;
      ByteBuf buf = encodeFrame(frame, ctx);
      out.add(buf);
    } else if ((msg instanceof StompContentSubframe)) {
      StompContentSubframe stompContentSubframe = (StompContentSubframe)msg;
      ByteBuf buf = encodeContent(stompContentSubframe, ctx);
      out.add(buf);
    }
  }
  
  private static ByteBuf encodeContent(StompContentSubframe content, ChannelHandlerContext ctx) {
    if ((content instanceof LastStompContentSubframe)) {
      ByteBuf buf = ctx.alloc().buffer(content.content().readableBytes() + 1);
      buf.writeBytes(content.content());
      buf.writeByte(0);
      return buf;
    }
    return content.content().retain();
  }
  
  private static ByteBuf encodeFrame(StompHeadersSubframe frame, ChannelHandlerContext ctx)
  {
    ByteBuf buf = ctx.alloc().buffer();
    
    buf.writeBytes(frame.command().toString().getBytes(CharsetUtil.US_ASCII));
    buf.writeByte(10);
    AsciiHeadersEncoder headersEncoder = new AsciiHeadersEncoder(buf, AsciiHeadersEncoder.SeparatorType.COLON, AsciiHeadersEncoder.NewlineType.LF);
    for (Map.Entry<CharSequence, CharSequence> entry : frame.headers()) {
      headersEncoder.encode(entry);
    }
    buf.writeByte(10);
    return buf;
  }
}
