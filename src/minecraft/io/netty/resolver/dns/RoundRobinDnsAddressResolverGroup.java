package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.RoundRobinInetSocketAddressResolver;
import java.net.InetAddress;
import java.net.InetSocketAddress;


























public class RoundRobinDnsAddressResolverGroup
  extends DnsAddressResolverGroup
{
  public RoundRobinDnsAddressResolverGroup(Class<? extends DatagramChannel> channelType, DnsServerAddresses nameServerAddresses)
  {
    super(channelType, nameServerAddresses);
  }
  

  public RoundRobinDnsAddressResolverGroup(ChannelFactory<? extends DatagramChannel> channelFactory, DnsServerAddresses nameServerAddresses)
  {
    super(channelFactory, nameServerAddresses);
  }
  

  protected final AddressResolver<InetSocketAddress> newAddressResolver(EventLoop eventLoop, NameResolver<InetAddress> resolver)
    throws Exception
  {
    return new RoundRobinInetSocketAddressResolver(eventLoop, resolver);
  }
}
