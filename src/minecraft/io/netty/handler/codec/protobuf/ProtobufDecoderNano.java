package io.netty.handler.codec.protobuf;

import com.google.protobuf.nano.MessageNano;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;


















































@ChannelHandler.Sharable
public class ProtobufDecoderNano
  extends MessageToMessageDecoder<ByteBuf>
{
  private final Class<? extends MessageNano> clazz;
  
  public ProtobufDecoderNano(Class<? extends MessageNano> clazz)
  {
    this.clazz = ((Class)ObjectUtil.checkNotNull(clazz, "You must provide a Class"));
  }
  


  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
    throws Exception
  {
    int length = msg.readableBytes();
    int offset; byte[] array; int offset; if (msg.hasArray()) {
      byte[] array = msg.array();
      offset = msg.arrayOffset() + msg.readerIndex();
    } else {
      array = new byte[length];
      msg.getBytes(msg.readerIndex(), array, 0, length);
      offset = 0;
    }
    MessageNano prototype = (MessageNano)clazz.newInstance();
    out.add(MessageNano.mergeFrom(prototype, array, offset, length));
  }
}
