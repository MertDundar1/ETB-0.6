package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;



















public class SocksAuthResponseDecoder
  extends ReplayingDecoder<State>
{
  private static final String name = "SOCKS_AUTH_RESPONSE_DECODER";
  private SocksSubnegotiationVersion version;
  private SocksAuthStatus authStatus;
  
  @Deprecated
  public static String getName()
  {
    return "SOCKS_AUTH_RESPONSE_DECODER";
  }
  


  private SocksResponse msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
  
  public SocksAuthResponseDecoder() {
    super(State.CHECK_PROTOCOL_VERSION);
  }
  
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out)
    throws Exception
  {
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksAuthResponseDecoder$State[((State)state()).ordinal()]) {
    case 1: 
      version = SocksSubnegotiationVersion.valueOf(byteBuf.readByte());
      if (version == SocksSubnegotiationVersion.AUTH_PASSWORD)
      {

        checkpoint(State.READ_AUTH_RESPONSE); }
      break;
    case 2: 
      authStatus = SocksAuthStatus.valueOf(byteBuf.readByte());
      msg = new SocksAuthResponse(authStatus);
    }
    
    channelHandlerContext.pipeline().remove(this);
    out.add(msg);
  }
  
  static enum State {
    CHECK_PROTOCOL_VERSION, 
    READ_AUTH_RESPONSE;
    
    private State() {}
  }
}
