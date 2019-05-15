package io.netty.resolver.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.InetNameResolver;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.IDN;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;










public class DnsNameResolver
  extends InetNameResolver
{
  private static final InternalLogger logger;
  private static final String LOCALHOST = "localhost";
  private static final InetAddress LOCALHOST_ADDRESS;
  private static final DnsRecord[] EMTPY_ADDITIONALS;
  static final InternetProtocolFamily[] DEFAULT_RESOLVE_ADDRESS_TYPES;
  static final String[] DEFAULT_SEACH_DOMAINS;
  
  static
  {
    logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);
    

    EMTPY_ADDITIONALS = new DnsRecord[0];
    




    if (NetUtil.isIpV4StackPreferred()) {
      DEFAULT_RESOLVE_ADDRESS_TYPES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv4 };
      LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
    } else {
      DEFAULT_RESOLVE_ADDRESS_TYPES = new InternetProtocolFamily[2];
      if (NetUtil.isIpV6AddressesPreferred()) {
        DEFAULT_RESOLVE_ADDRESS_TYPES[0] = InternetProtocolFamily.IPv6;
        DEFAULT_RESOLVE_ADDRESS_TYPES[1] = InternetProtocolFamily.IPv4;
        LOCALHOST_ADDRESS = NetUtil.LOCALHOST6;
      } else {
        DEFAULT_RESOLVE_ADDRESS_TYPES[0] = InternetProtocolFamily.IPv4;
        DEFAULT_RESOLVE_ADDRESS_TYPES[1] = InternetProtocolFamily.IPv6;
        LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
      }
    }
    
    String[] searchDomains;
    
    try
    {
      Class<?> configClass = Class.forName("sun.net.dns.ResolverConfiguration");
      Method open = configClass.getMethod("open", new Class[0]);
      Method nameservers = configClass.getMethod("searchlist", new Class[0]);
      Object instance = open.invoke(null, new Object[0]);
      

      List<String> list = (List)nameservers.invoke(instance, new Object[0]);
      searchDomains = (String[])list.toArray(new String[list.size()]);
    } catch (Exception ignore) {
      String[] searchDomains;
      searchDomains = EmptyArrays.EMPTY_STRINGS;
    }
    DEFAULT_SEACH_DOMAINS = searchDomains;
  }
  
  private static final DatagramDnsResponseDecoder DECODER = new DatagramDnsResponseDecoder();
  private static final DatagramDnsQueryEncoder ENCODER = new DatagramDnsQueryEncoder();
  

  final DnsServerAddresses nameServerAddresses;
  
  final Future<Channel> channelFuture;
  
  final DatagramChannel ch;
  
  final DnsQueryContextManager queryContextManager = new DnsQueryContextManager();
  


  private final DnsCache resolveCache;
  

  private final FastThreadLocal<DnsServerAddressStream> nameServerAddrStream = new FastThreadLocal()
  {
    protected DnsServerAddressStream initialValue() throws Exception
    {
      return nameServerAddresses.stream();
    }
  };
  



  private final long queryTimeoutMillis;
  



  private final int maxQueriesPerResolve;
  


  private final boolean traceEnabled;
  


  private final InternetProtocolFamily[] resolvedAddressTypes;
  


  private final boolean recursionDesired;
  


  private final int maxPayloadSize;
  


  private final boolean optResourceEnabled;
  


  private final HostsFileEntriesResolver hostsFileEntriesResolver;
  


  private final String[] searchDomains;
  


  private final int ndots;
  



  public DnsNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, DnsServerAddresses nameServerAddresses, final DnsCache resolveCache, long queryTimeoutMillis, InternetProtocolFamily[] resolvedAddressTypes, boolean recursionDesired, int maxQueriesPerResolve, boolean traceEnabled, int maxPayloadSize, boolean optResourceEnabled, HostsFileEntriesResolver hostsFileEntriesResolver, String[] searchDomains, int ndots)
  {
    super(eventLoop);
    ObjectUtil.checkNotNull(channelFactory, "channelFactory");
    this.nameServerAddresses = ((DnsServerAddresses)ObjectUtil.checkNotNull(nameServerAddresses, "nameServerAddresses"));
    this.queryTimeoutMillis = ObjectUtil.checkPositive(queryTimeoutMillis, "queryTimeoutMillis");
    this.resolvedAddressTypes = ((InternetProtocolFamily[])ObjectUtil.checkNonEmpty(resolvedAddressTypes, "resolvedAddressTypes"));
    this.recursionDesired = recursionDesired;
    this.maxQueriesPerResolve = ObjectUtil.checkPositive(maxQueriesPerResolve, "maxQueriesPerResolve");
    this.traceEnabled = traceEnabled;
    this.maxPayloadSize = ObjectUtil.checkPositive(maxPayloadSize, "maxPayloadSize");
    this.optResourceEnabled = optResourceEnabled;
    this.hostsFileEntriesResolver = ((HostsFileEntriesResolver)ObjectUtil.checkNotNull(hostsFileEntriesResolver, "hostsFileEntriesResolver"));
    this.resolveCache = resolveCache;
    this.searchDomains = ((String[])((String[])ObjectUtil.checkNotNull(searchDomains, "searchDomains")).clone());
    this.ndots = ObjectUtil.checkPositiveOrZero(ndots, "ndots");
    
    Bootstrap b = new Bootstrap();
    b.group(executor());
    b.channelFactory(channelFactory);
    b.option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, Boolean.valueOf(true));
    final DnsResponseHandler responseHandler = new DnsResponseHandler(executor().newPromise());
    b.handler(new ChannelInitializer()
    {
      protected void initChannel(DatagramChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelHandler[] { DnsNameResolver.DECODER, DnsNameResolver.ENCODER, responseHandler });
      }
      
    });
    channelFuture = channelActivePromise;
    ch = ((DatagramChannel)b.register().channel());
    ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(maxPayloadSize));
    
    ch.closeFuture().addListener(new ChannelFutureListener()
    {
      public void operationComplete(ChannelFuture future) throws Exception {
        resolveCache.clear();
      }
    });
  }
  


  public DnsCache resolveCache()
  {
    return resolveCache;
  }
  



  public long queryTimeoutMillis()
  {
    return queryTimeoutMillis;
  }
  




  public List<InternetProtocolFamily> resolvedAddressTypes()
  {
    return Arrays.asList(resolvedAddressTypes);
  }
  
  InternetProtocolFamily[] resolveAddressTypesUnsafe() {
    return resolvedAddressTypes;
  }
  
  final String[] searchDomains() {
    return searchDomains;
  }
  
  final int ndots() {
    return ndots;
  }
  



  public boolean isRecursionDesired()
  {
    return recursionDesired;
  }
  



  public int maxQueriesPerResolve()
  {
    return maxQueriesPerResolve;
  }
  



  public boolean isTraceEnabled()
  {
    return traceEnabled;
  }
  


  public int maxPayloadSize()
  {
    return maxPayloadSize;
  }
  



  public boolean isOptResourceEnabled()
  {
    return optResourceEnabled;
  }
  



  public HostsFileEntriesResolver hostsFileEntriesResolver()
  {
    return hostsFileEntriesResolver;
  }
  





  public void close()
  {
    if (ch.isOpen()) {
      ch.close();
    }
  }
  
  protected EventLoop executor()
  {
    return (EventLoop)super.executor();
  }
  
  private InetAddress resolveHostsFileEntry(String hostname) {
    if (hostsFileEntriesResolver == null) {
      return null;
    }
    InetAddress address = hostsFileEntriesResolver.address(hostname);
    if ((address == null) && (PlatformDependent.isWindows()) && ("localhost".equalsIgnoreCase(hostname)))
    {


      return LOCALHOST_ADDRESS;
    }
    return address;
  }
  








  public final Future<InetAddress> resolve(String inetHost, Iterable<DnsRecord> additionals)
  {
    return resolve(inetHost, additionals, executor().newPromise());
  }
  









  public final Future<InetAddress> resolve(String inetHost, Iterable<DnsRecord> additionals, Promise<InetAddress> promise)
  {
    ObjectUtil.checkNotNull(inetHost, "inetHost");
    ObjectUtil.checkNotNull(promise, "promise");
    DnsRecord[] additionalsArray = toArray(additionals, true);
    try {
      doResolve(inetHost, additionalsArray, promise, resolveCache);
      return promise;
    } catch (Exception e) {
      return promise.setFailure(e);
    }
  }
  







  public final Future<List<InetAddress>> resolveAll(String inetHost, Iterable<DnsRecord> additionals)
  {
    return resolveAll(inetHost, additionals, executor().newPromise());
  }
  









  public final Future<List<InetAddress>> resolveAll(String inetHost, Iterable<DnsRecord> additionals, Promise<List<InetAddress>> promise)
  {
    ObjectUtil.checkNotNull(inetHost, "inetHost");
    ObjectUtil.checkNotNull(promise, "promise");
    DnsRecord[] additionalsArray = toArray(additionals, true);
    try {
      doResolveAll(inetHost, additionalsArray, promise, resolveCache);
      return promise;
    } catch (Exception e) {
      return promise.setFailure(e);
    }
  }
  
  protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception
  {
    doResolve(inetHost, EMTPY_ADDITIONALS, promise, resolveCache);
  }
  
  private static DnsRecord[] toArray(Iterable<DnsRecord> additionals, boolean validateType) {
    ObjectUtil.checkNotNull(additionals, "additionals");
    if ((additionals instanceof Collection)) {
      Collection<DnsRecord> records = (Collection)additionals;
      for (DnsRecord r : additionals) {
        validateAdditional(r, validateType);
      }
      return (DnsRecord[])records.toArray(new DnsRecord[records.size()]);
    }
    
    Iterator<DnsRecord> additionalsIt = additionals.iterator();
    if (!additionalsIt.hasNext()) {
      return EMTPY_ADDITIONALS;
    }
    List<DnsRecord> records = new ArrayList();
    do {
      DnsRecord r = (DnsRecord)additionalsIt.next();
      validateAdditional(r, validateType);
      records.add(r);
    } while (additionalsIt.hasNext());
    
    return (DnsRecord[])records.toArray(new DnsRecord[records.size()]);
  }
  
  private static void validateAdditional(DnsRecord record, boolean validateType) {
    ObjectUtil.checkNotNull(record, "record");
    if ((validateType) && ((record instanceof DnsRawRecord))) {
      throw new IllegalArgumentException("DnsRawRecord implementations not allowed: " + record);
    }
  }
  





  protected void doResolve(String inetHost, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache)
    throws Exception
  {
    byte[] bytes = NetUtil.createByteArrayFromIpAddressString(inetHost);
    if (bytes != null)
    {
      promise.setSuccess(InetAddress.getByAddress(bytes));
      return;
    }
    
    String hostname = hostname(inetHost);
    
    InetAddress hostsFileEntry = resolveHostsFileEntry(hostname);
    if (hostsFileEntry != null) {
      promise.setSuccess(hostsFileEntry);
      return;
    }
    
    if (!doResolveCached(hostname, additionals, promise, resolveCache)) {
      doResolveUncached(hostname, additionals, promise, resolveCache);
    }
  }
  


  private boolean doResolveCached(String hostname, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache)
  {
    List<DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
    if ((cachedEntries == null) || (cachedEntries.isEmpty())) {
      return false;
    }
    
    InetAddress address = null;
    Throwable cause = null;
    synchronized (cachedEntries) {
      int numEntries = cachedEntries.size();
      assert (numEntries > 0);
      
      if (((DnsCacheEntry)cachedEntries.get(0)).cause() != null) {
        cause = ((DnsCacheEntry)cachedEntries.get(0)).cause();
      }
      else {
        for (InternetProtocolFamily f : resolvedAddressTypes) {
          for (int i = 0; i < numEntries; i++) {
            DnsCacheEntry e = (DnsCacheEntry)cachedEntries.get(i);
            if (f.addressType().isInstance(e.address())) {
              address = e.address();
              break;
            }
          }
        }
      }
    }
    
    if (address != null) {
      trySuccess(promise, address);
      return true;
    }
    if (cause != null) {
      tryFailure(promise, cause);
      return true;
    }
    return false;
  }
  
  private static <T> void trySuccess(Promise<T> promise, T result) {
    if (!promise.trySuccess(result)) {
      logger.warn("Failed to notify success ({}) to a promise: {}", result, promise);
    }
  }
  
  private static void tryFailure(Promise<?> promise, Throwable cause) {
    if (!promise.tryFailure(cause)) {
      logger.warn("Failed to notify failure to a promise: {}", promise, cause);
    }
  }
  


  private void doResolveUncached(String hostname, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache)
  {
    SingleResolverContext ctx = new SingleResolverContext(this, hostname, additionals, resolveCache);
    ctx.resolve(promise);
  }
  
  static final class SingleResolverContext extends DnsNameResolverContext<InetAddress>
  {
    SingleResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache)
    {
      super(hostname, additionals, resolveCache);
    }
    

    DnsNameResolverContext<InetAddress> newResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache)
    {
      return new SingleResolverContext(parent, hostname, additionals, resolveCache);
    }
    



    boolean finishResolve(Class<? extends InetAddress> addressType, List<DnsCacheEntry> resolvedEntries, Promise<InetAddress> promise)
    {
      int numEntries = resolvedEntries.size();
      for (int i = 0; i < numEntries; i++) {
        InetAddress a = ((DnsCacheEntry)resolvedEntries.get(i)).address();
        if (addressType.isInstance(a)) {
          DnsNameResolver.trySuccess(promise, a);
          return true;
        }
      }
      return false;
    }
  }
  
  protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception
  {
    doResolveAll(inetHost, EMTPY_ADDITIONALS, promise, resolveCache);
  }
  





  protected void doResolveAll(String inetHost, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache)
    throws Exception
  {
    byte[] bytes = NetUtil.createByteArrayFromIpAddressString(inetHost);
    if (bytes != null)
    {
      promise.setSuccess(Collections.singletonList(InetAddress.getByAddress(bytes)));
      return;
    }
    
    String hostname = hostname(inetHost);
    
    InetAddress hostsFileEntry = resolveHostsFileEntry(hostname);
    if (hostsFileEntry != null) {
      promise.setSuccess(Collections.singletonList(hostsFileEntry));
      return;
    }
    
    if (!doResolveAllCached(hostname, additionals, promise, resolveCache)) {
      doResolveAllUncached(hostname, additionals, promise, resolveCache);
    }
  }
  


  private boolean doResolveAllCached(String hostname, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache)
  {
    List<DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
    if ((cachedEntries == null) || (cachedEntries.isEmpty())) {
      return false;
    }
    
    List<InetAddress> result = null;
    Throwable cause = null;
    synchronized (cachedEntries) {
      int numEntries = cachedEntries.size();
      assert (numEntries > 0);
      
      if (((DnsCacheEntry)cachedEntries.get(0)).cause() != null) {
        cause = ((DnsCacheEntry)cachedEntries.get(0)).cause();
      } else {
        for (InternetProtocolFamily f : resolvedAddressTypes) {
          for (int i = 0; i < numEntries; i++) {
            DnsCacheEntry e = (DnsCacheEntry)cachedEntries.get(i);
            if (f.addressType().isInstance(e.address())) {
              if (result == null) {
                result = new ArrayList(numEntries);
              }
              result.add(e.address());
            }
          }
        }
      }
    }
    
    if (result != null) {
      trySuccess(promise, result);
      return true;
    }
    if (cause != null) {
      tryFailure(promise, cause);
      return true;
    }
    return false;
  }
  
  static final class ListResolverContext extends DnsNameResolverContext<List<InetAddress>>
  {
    ListResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache) {
      super(hostname, additionals, resolveCache);
    }
    

    DnsNameResolverContext<List<InetAddress>> newResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache)
    {
      return new ListResolverContext(parent, hostname, additionals, resolveCache);
    }
    



    boolean finishResolve(Class<? extends InetAddress> addressType, List<DnsCacheEntry> resolvedEntries, Promise<List<InetAddress>> promise)
    {
      List<InetAddress> result = null;
      int numEntries = resolvedEntries.size();
      for (int i = 0; i < numEntries; i++) {
        InetAddress a = ((DnsCacheEntry)resolvedEntries.get(i)).address();
        if (addressType.isInstance(a)) {
          if (result == null) {
            result = new ArrayList(numEntries);
          }
          result.add(a);
        }
      }
      
      if (result != null) {
        promise.trySuccess(result);
        return true;
      }
      return false;
    }
  }
  


  private void doResolveAllUncached(String hostname, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache)
  {
    DnsNameResolverContext<List<InetAddress>> ctx = new ListResolverContext(this, hostname, additionals, resolveCache);
    
    ctx.resolve(promise);
  }
  
  private static String hostname(String inetHost) {
    String hostname = IDN.toASCII(inetHost);
    
    if ((StringUtil.endsWith(inetHost, '.')) && (!StringUtil.endsWith(hostname, '.'))) {
      hostname = hostname + ".";
    }
    return hostname;
  }
  


  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question)
  {
    return query(nextNameServerAddress(), question);
  }
  



  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question, Iterable<DnsRecord> additionals)
  {
    return query(nextNameServerAddress(), question, additionals);
  }
  



  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise)
  {
    return query(nextNameServerAddress(), question, Collections.emptyList(), promise);
  }
  
  private InetSocketAddress nextNameServerAddress() {
    return ((DnsServerAddressStream)nameServerAddrStream.get()).next();
  }
  




  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question)
  {
    return query0(nameServerAddr, question, EMTPY_ADDITIONALS, ch.eventLoop().newPromise());
  }
  





  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Iterable<DnsRecord> additionals)
  {
    return query0(nameServerAddr, question, toArray(additionals, false), ch.eventLoop().newPromise());
  }
  






  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise)
  {
    return query0(nameServerAddr, question, EMTPY_ADDITIONALS, promise);
  }
  






  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Iterable<DnsRecord> additionals, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise)
  {
    return query0(nameServerAddr, question, toArray(additionals, false), promise);
  }
  



  Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(InetSocketAddress nameServerAddr, DnsQuestion question, DnsRecord[] additionals, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise)
  {
    Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> castPromise = cast((Promise)ObjectUtil.checkNotNull(promise, "promise"));
    try
    {
      new DnsQueryContext(this, nameServerAddr, question, additionals, castPromise).query();
      return castPromise;
    } catch (Exception e) {
      return castPromise.setFailure(e);
    }
  }
  
  private static Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> cast(Promise<?> promise)
  {
    return promise;
  }
  
  private final class DnsResponseHandler extends ChannelInboundHandlerAdapter
  {
    private final Promise<Channel> channelActivePromise;
    
    DnsResponseHandler() {
      this.channelActivePromise = channelActivePromise;
    }
    
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
      try {
        DatagramDnsResponse res = (DatagramDnsResponse)msg;
        int queryId = res.id();
        
        if (DnsNameResolver.logger.isDebugEnabled()) {
          DnsNameResolver.logger.debug("{} RECEIVED: [{}: {}], {}", new Object[] { ch, Integer.valueOf(queryId), res.sender(), res });
        }
        
        DnsQueryContext qCtx = queryContextManager.get(res.sender(), queryId);
        if (qCtx == null) {
          DnsNameResolver.logger.warn("{} Received a DNS response with an unknown ID: {}", ch, Integer.valueOf(queryId));
        }
        else
        {
          qCtx.finish(res); }
      } finally {
        ReferenceCountUtil.safeRelease(msg);
      }
    }
    
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
      super.channelActive(ctx);
      channelActivePromise.setSuccess(ctx.channel());
    }
    
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
      DnsNameResolver.logger.warn("{} Unexpected exception: ", ch, cause);
    }
  }
}
