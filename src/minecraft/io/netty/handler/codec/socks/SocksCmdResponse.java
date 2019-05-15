package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.IDN;
























public final class SocksCmdResponse
  extends SocksResponse
{
  private final SocksCmdStatus cmdStatus;
  private final SocksAddressType addressType;
  private final String host;
  private final int port;
  private static final byte[] DOMAIN_ZEROED = { 0 };
  private static final byte[] IPv4_HOSTNAME_ZEROED = { 0, 0, 0, 0 };
  private static final byte[] IPv6_HOSTNAME_ZEROED = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  


  public SocksCmdResponse(SocksCmdStatus cmdStatus, SocksAddressType addressType)
  {
    this(cmdStatus, addressType, null, 0);
  }
  












  public SocksCmdResponse(SocksCmdStatus cmdStatus, SocksAddressType addressType, String host, int port)
  {
    super(SocksResponseType.CMD);
    if (cmdStatus == null) {
      throw new NullPointerException("cmdStatus");
    }
    if (addressType == null) {
      throw new NullPointerException("addressType");
    }
    if (host != null) {
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
      
      host = IDN.toASCII(host);
    }
    if ((port < 0) || (port > 65535)) {
      throw new IllegalArgumentException(port + " is not in bounds 0 <= x <= 65535");
    }
    this.cmdStatus = cmdStatus;
    this.addressType = addressType;
    this.host = host;
    this.port = port;
  }
  




  public SocksCmdStatus cmdStatus()
  {
    return cmdStatus;
  }
  




  public SocksAddressType addressType()
  {
    return addressType;
  }
  







  public String host()
  {
    if (host != null) {
      return IDN.toUnicode(host);
    }
    return null;
  }
  






  public int port()
  {
    return port;
  }
  
  public void encodeAsByteBuf(ByteBuf byteBuf)
  {
    byteBuf.writeByte(protocolVersion().byteValue());
    byteBuf.writeByte(cmdStatus.byteValue());
    byteBuf.writeByte(0);
    byteBuf.writeByte(addressType.byteValue());
    switch (1.$SwitchMap$io$netty$handler$codec$socks$SocksAddressType[addressType.ordinal()]) {
    case 1: 
      byte[] hostContent = host == null ? IPv4_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(host);
      
      byteBuf.writeBytes(hostContent);
      byteBuf.writeShort(port);
      break;
    
    case 2: 
      byte[] hostContent = host == null ? DOMAIN_ZEROED : host.getBytes(CharsetUtil.US_ASCII);
      
      byteBuf.writeByte(hostContent.length);
      byteBuf.writeBytes(hostContent);
      byteBuf.writeShort(port);
      break;
    
    case 3: 
      byte[] hostContent = host == null ? IPv6_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(host);
      
      byteBuf.writeBytes(hostContent);
      byteBuf.writeShort(port);
      break;
    }
  }
}
