package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.IDN;




















public final class SocksCmdRequest
  extends SocksRequest
{
  private final SocksCmdType cmdType;
  private final SocksAddressType addressType;
  private final String host;
  private final int port;
  
  public SocksCmdRequest(SocksCmdType cmdType, SocksAddressType addressType, String host, int port)
  {
    super(SocksRequestType.CMD);
    if (cmdType == null) {
      throw new NullPointerException("cmdType");
    }
    if (addressType == null) {
      throw new NullPointerException("addressType");
    }
    if (host == null) {
      throw new NullPointerException("host");
    }
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksAddressType[addressType.ordinal()]) {
    case 1: 
      if (!NetUtil.isValidIpV4Address(host)) {
        throw new IllegalArgumentException(host + " is not a valid IPv4 address");
      }
      break;
    case 2: 
      if (IDN.toASCII(host).length() > 255) {
        throw new IllegalArgumentException(host + " IDN: " + IDN.toASCII(host) + " exceeds 255 char limit");
      }
      break;
    case 3: 
      if (!NetUtil.isValidIpV6Address(host)) {
        throw new IllegalArgumentException(host + " is not a valid IPv6 address");
      }
      
      break;
    }
    
    if ((port <= 0) || (port >= 65536)) {
      throw new IllegalArgumentException(port + " is not in bounds 0 < x < 65536");
    }
    this.cmdType = cmdType;
    this.addressType = addressType;
    this.host = IDN.toASCII(host);
    this.port = port;
  }
  




  public SocksCmdType cmdType()
  {
    return cmdType;
  }
  




  public SocksAddressType addressType()
  {
    return addressType;
  }
  




  public String host()
  {
    return IDN.toUnicode(host);
  }
  




  public int port()
  {
    return port;
  }
  
  public void encodeAsByteBuf(ByteBuf byteBuf)
  {
    byteBuf.writeByte(protocolVersion().byteValue());
    byteBuf.writeByte(cmdType.byteValue());
    byteBuf.writeByte(0);
    byteBuf.writeByte(addressType.byteValue());
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksAddressType[addressType.ordinal()]) {
    case 1: 
      byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(host));
      byteBuf.writeShort(port);
      break;
    

    case 2: 
      byteBuf.writeByte(host.length());
      byteBuf.writeBytes(host.getBytes(CharsetUtil.US_ASCII));
      byteBuf.writeShort(port);
      break;
    

    case 3: 
      byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(host));
      byteBuf.writeShort(port);
    }
  }
}
