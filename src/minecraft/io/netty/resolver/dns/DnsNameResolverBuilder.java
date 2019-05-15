package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

























public final class DnsNameResolverBuilder
{
  private final EventLoop eventLoop;
  private ChannelFactory<? extends DatagramChannel> channelFactory;
  private DnsServerAddresses nameServerAddresses = DnsServerAddresses.defaultAddresses();
  private DnsCache resolveCache;
  private Integer minTtl;
  private Integer maxTtl;
  private Integer negativeTtl;
  private long queryTimeoutMillis = 5000L;
  private InternetProtocolFamily[] resolvedAddressTypes = DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
  private boolean recursionDesired = true;
  private int maxQueriesPerResolve = 16;
  private boolean traceEnabled;
  private int maxPayloadSize = 4096;
  private boolean optResourceEnabled = true;
  private HostsFileEntriesResolver hostsFileEntriesResolver = HostsFileEntriesResolver.DEFAULT;
  private String[] searchDomains = DnsNameResolver.DEFAULT_SEACH_DOMAINS;
  private int ndots = 1;
  





  public DnsNameResolverBuilder(EventLoop eventLoop)
  {
    this.eventLoop = eventLoop;
  }
  





  public DnsNameResolverBuilder channelFactory(ChannelFactory<? extends DatagramChannel> channelFactory)
  {
    this.channelFactory = channelFactory;
    return this;
  }
  






  public DnsNameResolverBuilder channelType(Class<? extends DatagramChannel> channelType)
  {
    return channelFactory(new ReflectiveChannelFactory(channelType));
  }
  





  public DnsNameResolverBuilder nameServerAddresses(DnsServerAddresses nameServerAddresses)
  {
    this.nameServerAddresses = nameServerAddresses;
    return this;
  }
  





  public DnsNameResolverBuilder resolveCache(DnsCache resolveCache)
  {
    this.resolveCache = resolveCache;
    return this;
  }
  











  public DnsNameResolverBuilder ttl(int minTtl, int maxTtl)
  {
    this.maxTtl = Integer.valueOf(maxTtl);
    this.minTtl = Integer.valueOf(minTtl);
    return this;
  }
  





  public DnsNameResolverBuilder negativeTtl(int negativeTtl)
  {
    this.negativeTtl = Integer.valueOf(negativeTtl);
    return this;
  }
  





  public DnsNameResolverBuilder queryTimeoutMillis(long queryTimeoutMillis)
  {
    this.queryTimeoutMillis = queryTimeoutMillis;
    return this;
  }
  








  public DnsNameResolverBuilder resolvedAddressTypes(InternetProtocolFamily... resolvedAddressTypes)
  {
    ObjectUtil.checkNotNull(resolvedAddressTypes, "resolvedAddressTypes");
    
    List<InternetProtocolFamily> list = InternalThreadLocalMap.get().arrayList(InternetProtocolFamily.values().length);
    

    for (InternetProtocolFamily f : resolvedAddressTypes) {
      if (f == null) {
        break;
      }
      

      if (!list.contains(f))
      {


        list.add(f);
      }
    }
    if (list.isEmpty()) {
      throw new IllegalArgumentException("no protocol family specified");
    }
    
    this.resolvedAddressTypes = ((InternetProtocolFamily[])list.toArray(new InternetProtocolFamily[list.size()]));
    
    return this;
  }
  








  public DnsNameResolverBuilder resolvedAddressTypes(Iterable<InternetProtocolFamily> resolvedAddressTypes)
  {
    ObjectUtil.checkNotNull(resolvedAddressTypes, "resolveAddressTypes");
    
    List<InternetProtocolFamily> list = InternalThreadLocalMap.get().arrayList(InternetProtocolFamily.values().length);
    

    for (InternetProtocolFamily f : resolvedAddressTypes) {
      if (f == null) {
        break;
      }
      

      if (!list.contains(f))
      {


        list.add(f);
      }
    }
    if (list.isEmpty()) {
      throw new IllegalArgumentException("no protocol family specified");
    }
    
    this.resolvedAddressTypes = ((InternetProtocolFamily[])list.toArray(new InternetProtocolFamily[list.size()]));
    
    return this;
  }
  





  public DnsNameResolverBuilder recursionDesired(boolean recursionDesired)
  {
    this.recursionDesired = recursionDesired;
    return this;
  }
  





  public DnsNameResolverBuilder maxQueriesPerResolve(int maxQueriesPerResolve)
  {
    this.maxQueriesPerResolve = maxQueriesPerResolve;
    return this;
  }
  






  public DnsNameResolverBuilder traceEnabled(boolean traceEnabled)
  {
    this.traceEnabled = traceEnabled;
    return this;
  }
  





  public DnsNameResolverBuilder maxPayloadSize(int maxPayloadSize)
  {
    this.maxPayloadSize = maxPayloadSize;
    return this;
  }
  







  public DnsNameResolverBuilder optResourceEnabled(boolean optResourceEnabled)
  {
    this.optResourceEnabled = optResourceEnabled;
    return this;
  }
  




  public DnsNameResolverBuilder hostsFileEntriesResolver(HostsFileEntriesResolver hostsFileEntriesResolver)
  {
    this.hostsFileEntriesResolver = hostsFileEntriesResolver;
    return this;
  }
  





  public DnsNameResolverBuilder searchDomains(Iterable<String> searchDomains)
  {
    ObjectUtil.checkNotNull(searchDomains, "searchDomains");
    
    List<String> list = InternalThreadLocalMap.get().arrayList(4);
    

    for (String f : searchDomains) {
      if (f == null) {
        break;
      }
      

      if (!list.contains(f))
      {


        list.add(f);
      }
    }
    this.searchDomains = ((String[])list.toArray(new String[list.size()]));
    return this;
  }
  






  public DnsNameResolverBuilder ndots(int ndots)
  {
    this.ndots = ndots;
    return this;
  }
  





  public DnsNameResolver build()
  {
    if ((resolveCache != null) && ((minTtl != null) || (maxTtl != null) || (negativeTtl != null))) {
      throw new IllegalStateException("resolveCache and TTLs are mutually exclusive");
    }
    
    DnsCache cache = resolveCache != null ? resolveCache : new DefaultDnsCache(ObjectUtil.intValue(minTtl, 0), ObjectUtil.intValue(maxTtl, Integer.MAX_VALUE), ObjectUtil.intValue(negativeTtl, 0));
    

    return new DnsNameResolver(eventLoop, channelFactory, nameServerAddresses, cache, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, searchDomains, ndots);
  }
}
