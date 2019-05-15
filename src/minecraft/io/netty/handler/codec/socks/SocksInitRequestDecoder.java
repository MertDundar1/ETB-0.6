package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.ArrayList;
import java.util.List;





















public class SocksInitRequestDecoder
  extends ReplayingDecoder<State>
{
  private static final String name = "SOCKS_INIT_REQUEST_DECODER";
  
  @Deprecated
  public static String getName()
  {
    return "SOCKS_INIT_REQUEST_DECODER";
  }
  
  private final List<SocksAuthScheme> authSchemes = new ArrayList();
  private SocksProtocolVersion version;
  private byte authSchemeNum;
  private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
  
  public SocksInitRequestDecoder() {
    super(State.CHECK_PROTOCOL_VERSION);
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception
  {
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksInitRequestDecoder$State[((State)state()).ordinal()]) {
    case 1: 
      version = SocksProtocolVersion.valueOf(byteBuf.readByte());
      if (version == SocksProtocolVersion.SOCKS5)
      {

        checkpoint(State.READ_AUTH_SCHEMES); }
      break;
    case 2: 
      authSchemes.clear();
      authSchemeNum = byteBuf.readByte();
      for (int i = 0; i < authSchemeNum; i++) {
        authSchemes.add(SocksAuthScheme.valueOf(byteBuf.readByte()));
      }
      msg = new SocksInitRequest(authSchemes);
    }
    
    
    ctx.pipeline().remove(this);
    out.add(msg);
  }
  
  static enum State {
    CHECK_PROTOCOL_VERSION, 
    READ_AUTH_SCHEMES;
    
    private State() {}
  }
}
