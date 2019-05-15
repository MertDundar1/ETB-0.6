package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






















public class SctpMessageCompletionHandler
  extends MessageToMessageDecoder<SctpMessage>
{
  private final Map<Integer, ByteBuf> fragments = new HashMap();
  
  public SctpMessageCompletionHandler() {}
  
  protected void decode(ChannelHandlerContext ctx, SctpMessage msg, List<Object> out) throws Exception { ByteBuf byteBuf = msg.content();
    int protocolIdentifier = msg.protocolIdentifier();
    int streamIdentifier = msg.streamIdentifier();
    boolean isComplete = msg.isComplete();
    ByteBuf frag;
    ByteBuf frag;
    if (fragments.containsKey(Integer.valueOf(streamIdentifier))) {
      frag = (ByteBuf)fragments.remove(Integer.valueOf(streamIdentifier));
    } else {
      frag = Unpooled.EMPTY_BUFFER;
    }
    
    if ((isComplete) && (!frag.isReadable()))
    {
      out.add(msg);
    } else if ((!isComplete) && (frag.isReadable()))
    {
      fragments.put(Integer.valueOf(streamIdentifier), Unpooled.wrappedBuffer(new ByteBuf[] { frag, byteBuf }));
    } else if ((isComplete) && (frag.isReadable()))
    {
      fragments.remove(Integer.valueOf(streamIdentifier));
      SctpMessage assembledMsg = new SctpMessage(protocolIdentifier, streamIdentifier, Unpooled.wrappedBuffer(new ByteBuf[] { frag, byteBuf }));
      


      out.add(assembledMsg);
    }
    else {
      fragments.put(Integer.valueOf(streamIdentifier), byteBuf);
    }
    byteBuf.retain();
  }
}
