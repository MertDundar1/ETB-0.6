package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;














public class SocksCmdRequestDecoder
  extends ReplayingDecoder<State>
{
  private static final String name = "SOCKS_CMD_REQUEST_DECODER";
  private SocksProtocolVersion version;
  private int fieldLength;
  private SocksCmdType cmdType;
  private SocksAddressType addressType;
  private byte reserved;
  private String host;
  private int port;
  
  @Deprecated
  public static String getName()
  {
    return "SOCKS_CMD_REQUEST_DECODER";
  }
  








  private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
  
  public SocksCmdRequestDecoder() {
    super(State.CHECK_PROTOCOL_VERSION);
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception
  {
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksCmdRequestDecoder$State[((State)state()).ordinal()]) {
    case 1: 
      version = SocksProtocolVersion.valueOf(byteBuf.readByte());
      if (version == SocksProtocolVersion.SOCKS5)
      {

        checkpoint(State.READ_CMD_HEADER); }
      break;
    case 2: 
      cmdType = SocksCmdType.valueOf(byteBuf.readByte());
      reserved = byteBuf.readByte();
      addressType = SocksAddressType.valueOf(byteBuf.readByte());
      checkpoint(State.READ_CMD_ADDRESS);
    
    case 3: 
      switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksAddressType[addressType.ordinal()]) {
      case 1: 
        host = SocksCommonUtils.intToIp(byteBuf.readInt());
        port = byteBuf.readUnsignedShort();
        msg = new SocksCmdRequest(cmdType, addressType, host, port);
        break;
      
      case 2: 
        fieldLength = byteBuf.readByte();
        host = byteBuf.readBytes(fieldLength).toString(CharsetUtil.US_ASCII);
        port = byteBuf.readUnsignedShort();
        msg = new SocksCmdRequest(cmdType, addressType, host, port);
        break;
      
      case 3: 
        host = SocksCommonUtils.ipv6toStr(byteBuf.readBytes(16).array());
        port = byteBuf.readUnsignedShort();
        msg = new SocksCmdRequest(cmdType, addressType, host, port); }
      break;
    }
    
    



    ctx.pipeline().remove(this);
    out.add(msg);
  }
  
  static enum State {
    CHECK_PROTOCOL_VERSION, 
    READ_CMD_HEADER, 
    READ_CMD_ADDRESS;
    
    private State() {}
  }
}
