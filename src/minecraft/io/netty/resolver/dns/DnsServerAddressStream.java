package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public abstract interface DnsServerAddressStream
{
  public abstract InetSocketAddress next();
}
