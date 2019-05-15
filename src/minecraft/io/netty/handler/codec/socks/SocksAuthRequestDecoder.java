package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

















public class SocksAuthRequestDecoder
  extends ReplayingDecoder<State>
{
  private static final String name = "SOCKS_AUTH_REQUEST_DECODER";
  private SocksSubnegotiationVersion version;
  private int fieldLength;
  private String username;
  private String password;
  
  @Deprecated
  public static String getName()
  {
    return "SOCKS_AUTH_REQUEST_DECODER";
  }
  




  private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
  
  public SocksAuthRequestDecoder() {
    super(State.CHECK_PROTOCOL_VERSION);
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception
  {
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksAuthRequestDecoder$State[((State)state()).ordinal()]) {
    case 1: 
      version = SocksSubnegotiationVersion.valueOf(byteBuf.readByte());
      if (version == SocksSubnegotiationVersion.AUTH_PASSWORD)
      {

        checkpoint(State.READ_USERNAME); }
      break;
    case 2: 
      fieldLength = byteBuf.readByte();
      username = byteBuf.readBytes(fieldLength).toString(CharsetUtil.US_ASCII);
      checkpoint(State.READ_PASSWORD);
    
    case 3: 
      fieldLength = byteBuf.readByte();
      password = byteBuf.readBytes(fieldLength).toString(CharsetUtil.US_ASCII);
      msg = new SocksAuthRequest(username, password);
    }
    
    ctx.pipeline().remove(this);
    out.add(msg);
  }
  
  static enum State {
    CHECK_PROTOCOL_VERSION, 
    READ_USERNAME, 
    READ_PASSWORD;
    
    private State() {}
  }
}
