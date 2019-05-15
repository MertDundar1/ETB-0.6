package io.netty.resolver.dns;

import java.net.InetSocketAddress;

















final class SingletonDnsServerAddresses
  extends DnsServerAddresses
{
  private final InetSocketAddress address;
  private final String strVal;
  private final DnsServerAddressStream stream = new DnsServerAddressStream()
  {
    public InetSocketAddress next() {
      return address;
    }
    
    public String toString()
    {
      return SingletonDnsServerAddresses.this.toString();
    }
  };
  
  SingletonDnsServerAddresses(InetSocketAddress address) {
    this.address = address;
    strVal = (32 + "singleton(" + address + ')');
  }
  
  public DnsServerAddressStream stream()
  {
    return stream;
  }
  
  public String toString()
  {
    return strVal;
  }
}
