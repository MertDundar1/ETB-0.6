package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.StringUtil;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

















abstract class DnsNameResolverContext<T>
{
  private static final int INADDRSZ4 = 4;
  private static final int INADDRSZ6 = 16;
  private static final FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>> RELEASE_RESPONSE = new FutureListener()
  {
    public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future)
    {
      if (future.isSuccess()) {
        ((AddressedEnvelope)future.getNow()).release();
      }
    }
  };
  
  private final DnsNameResolver parent;
  
  private final DnsServerAddressStream nameServerAddrs;
  private final String hostname;
  protected String pristineHostname;
  private final DnsCache resolveCache;
  private final boolean traceEnabled;
  private final int maxAllowedQueries;
  private final InternetProtocolFamily[] resolveAddressTypes;
  private final DnsRecord[] additionals;
  private final Set<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> queriesInProgress = Collections.newSetFromMap(new IdentityHashMap());
  
  private List<DnsCacheEntry> resolvedEntries;
  
  private StringBuilder trace;
  
  private int allowedQueries;
  
  private boolean triedCNAME;
  

  protected DnsNameResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache)
  {
    this.parent = parent;
    this.hostname = hostname;
    this.additionals = additionals;
    this.resolveCache = resolveCache;
    
    nameServerAddrs = nameServerAddresses.stream();
    maxAllowedQueries = parent.maxQueriesPerResolve();
    resolveAddressTypes = parent.resolveAddressTypesUnsafe();
    traceEnabled = parent.isTraceEnabled();
    allowedQueries = maxAllowedQueries;
  }
  
  void resolve(Promise<T> promise) {
    boolean directSearch = (parent.searchDomains().length == 0) || (StringUtil.endsWith(hostname, '.'));
    if (directSearch) {
      internalResolve(promise);
    } else {
      final Promise<T> original = promise;
      promise = parent.executor().newPromise();
      promise.addListener(new FutureListener() {
        int count;
        
        public void operationComplete(Future<T> future) throws Exception {
          if (future.isSuccess()) {
            original.trySuccess(future.getNow());
          } else if (count < parent.searchDomains().length) {
            String searchDomain = parent.searchDomains()[(count++)];
            Promise<T> nextPromise = parent.executor().newPromise();
            String nextHostname = hostname + '.' + searchDomain;
            DnsNameResolverContext<T> nextContext = newResolverContext(parent, nextHostname, additionals, resolveCache);
            
            pristineHostname = hostname;
            nextContext.internalResolve(nextPromise);
            nextPromise.addListener(this);
          } else {
            original.tryFailure(future.cause());
          }
        }
      });
      if (parent.ndots() == 0) {
        internalResolve(promise);
      } else {
        int dots = 0;
        for (int idx = hostname.length() - 1; idx >= 0; idx--)
          if (hostname.charAt(idx) == '.') { dots++; if (dots >= parent.ndots()) {
              internalResolve(promise);
              return;
            }
          }
        promise.tryFailure(new UnknownHostException(hostname));
      }
    }
  }
  
  private void internalResolve(Promise<T> promise) {
    InetSocketAddress nameServerAddrToTry = nameServerAddrs.next();
    for (InternetProtocolFamily f : resolveAddressTypes) { DnsRecordType type;
      DnsRecordType type;
      switch (4.$SwitchMap$io$netty$channel$socket$InternetProtocolFamily[f.ordinal()]) {
      case 1: 
        type = DnsRecordType.A;
        break;
      case 2: 
        type = DnsRecordType.AAAA;
        break;
      default: 
        throw new Error();
      }
      DnsRecordType type;
      query(nameServerAddrToTry, new DefaultDnsQuestion(hostname, type), promise);
    }
  }
  
  private void query(InetSocketAddress nameServerAddr, final DnsQuestion question, final Promise<T> promise) {
    if ((allowedQueries == 0) || (promise.isCancelled())) {
      tryToFinishResolve(promise);
      return;
    }
    
    allowedQueries -= 1;
    
    Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = parent.query0(nameServerAddr, question, additionals, parent.ch.eventLoop().newPromise());
    

    queriesInProgress.add(f);
    
    f.addListener(new FutureListener()
    {
      public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
        queriesInProgress.remove(future);
        
        if ((promise.isDone()) || (future.isCancelled())) {
          return;
        }
        try
        {
          if (future.isSuccess()) {
            onResponse(question, (AddressedEnvelope)future.getNow(), promise);
          }
          else {
            if (traceEnabled) {
              DnsNameResolverContext.this.addTrace(future.cause());
            }
            DnsNameResolverContext.this.query(nameServerAddrs.next(), question, promise);
          }
        } finally {
          tryToFinishResolve(promise);
        }
      }
    });
  }
  
  void onResponse(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, Promise<T> promise)
  {
    try {
      DnsResponse res = (DnsResponse)envelope.content();
      DnsResponseCode code = res.code();
      if (code == DnsResponseCode.NOERROR) {
        DnsRecordType type = question.type();
        if ((type == DnsRecordType.A) || (type == DnsRecordType.AAAA)) {
          onResponseAorAAAA(type, question, envelope, promise);
        } else if (type == DnsRecordType.CNAME) {
          onResponseCNAME(question, envelope, promise);
        }
      }
      else
      {
        if (traceEnabled) {
          addTrace((InetSocketAddress)envelope.sender(), "response code: " + code + " with " + res.count(DnsSection.ANSWER) + " answer(s) and " + res.count(DnsSection.AUTHORITY) + " authority resource(s)");
        }
        



        if (code != DnsResponseCode.NXDOMAIN)
          query(nameServerAddrs.next(), question, promise);
      }
    } finally {
      ReferenceCountUtil.safeRelease(envelope);
    }
  }
  



  private void onResponseAorAAAA(DnsRecordType qType, DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, Promise<T> promise)
  {
    DnsResponse response = (DnsResponse)envelope.content();
    Map<String, String> cnames = buildAliasMap(response);
    int answerCount = response.count(DnsSection.ANSWER);
    
    boolean found = false;
    for (int i = 0; i < answerCount; i++) {
      DnsRecord r = response.recordAt(DnsSection.ANSWER, i);
      DnsRecordType type = r.type();
      if ((type == DnsRecordType.A) || (type == DnsRecordType.AAAA))
      {


        String qName = question.name().toLowerCase(Locale.US);
        String rName = r.name().toLowerCase(Locale.US);
        

        if (!rName.equals(qName))
        {
          String resolved = qName;
          do {
            resolved = (String)cnames.get(resolved);
          } while ((!rName.equals(resolved)) && 
          

            (resolved != null));
          
          if (resolved == null) {}



        }
        else if ((r instanceof DnsRawRecord))
        {


          ByteBuf content = ((ByteBufHolder)r).content();
          int contentLen = content.readableBytes();
          if ((contentLen == 4) || (contentLen == 16))
          {


            byte[] addrBytes = new byte[contentLen];
            content.getBytes(content.readerIndex(), addrBytes);
            
            try
            {
              resolved = InetAddress.getByAddress(hostname, addrBytes);
            } catch (UnknownHostException e) {
              InetAddress resolved;
              throw new Error(e);
            }
            InetAddress resolved;
            if (resolvedEntries == null) {
              resolvedEntries = new ArrayList(8);
            }
            
            DnsCacheEntry e = new DnsCacheEntry(hostname, resolved);
            resolveCache.cache(hostname, additionals, resolved, r.timeToLive(), parent.ch.eventLoop());
            resolvedEntries.add(e);
            found = true;
          }
        }
      }
    }
    if (found) {
      return;
    }
    
    if (traceEnabled) {
      addTrace((InetSocketAddress)envelope.sender(), "no matching " + qType + " record found");
    }
    

    if (!cnames.isEmpty()) {
      onResponseCNAME(question, envelope, cnames, false, promise);
    }
  }
  
  private void onResponseCNAME(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, Promise<T> promise)
  {
    onResponseCNAME(question, envelope, buildAliasMap((DnsResponse)envelope.content()), true, promise);
  }
  



  private void onResponseCNAME(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> response, Map<String, String> cnames, boolean trace, Promise<T> promise)
  {
    String name = question.name().toLowerCase(Locale.US);
    String resolved = name;
    boolean found = false;
    while (!cnames.isEmpty())
    {

      String next = (String)cnames.remove(resolved);
      if (next == null) break;
      found = true;
      resolved = next;
    }
    



    if (found) {
      followCname((InetSocketAddress)response.sender(), name, resolved, promise);
    } else if ((trace) && (traceEnabled)) {
      addTrace((InetSocketAddress)response.sender(), "no matching CNAME record found");
    }
  }
  
  private static Map<String, String> buildAliasMap(DnsResponse response) {
    int answerCount = response.count(DnsSection.ANSWER);
    Map<String, String> cnames = null;
    for (int i = 0; i < answerCount; i++) {
      DnsRecord r = response.recordAt(DnsSection.ANSWER, i);
      DnsRecordType type = r.type();
      if (type == DnsRecordType.CNAME)
      {


        if ((r instanceof DnsRawRecord))
        {


          ByteBuf recordContent = ((ByteBufHolder)r).content();
          String domainName = decodeDomainName(recordContent);
          if (domainName != null)
          {


            if (cnames == null) {
              cnames = new HashMap();
            }
            
            cnames.put(r.name().toLowerCase(Locale.US), domainName.toLowerCase(Locale.US));
          }
        } } }
    return cnames != null ? cnames : Collections.emptyMap();
  }
  
  void tryToFinishResolve(Promise<T> promise) {
    if (!queriesInProgress.isEmpty())
    {
      if (gotPreferredAddress())
      {
        finishResolve(promise);
      }
      

      return;
    }
    

    if (resolvedEntries == null)
    {
      if (!triedCNAME)
      {
        triedCNAME = true;
        query(nameServerAddrs.next(), new DefaultDnsQuestion(hostname, DnsRecordType.CNAME), promise);
        return;
      }
    }
    

    finishResolve(promise);
  }
  
  private boolean gotPreferredAddress() {
    if (resolvedEntries == null) {
      return false;
    }
    
    int size = resolvedEntries.size();
    switch (4.$SwitchMap$io$netty$channel$socket$InternetProtocolFamily[resolveAddressTypes[0].ordinal()]) {
    case 1: 
      for (int i = 0; i < size; i++) {
        if ((((DnsCacheEntry)resolvedEntries.get(i)).address() instanceof Inet4Address)) {
          return true;
        }
      }
      break;
    case 2: 
      for (int i = 0; i < size; i++) {
        if ((((DnsCacheEntry)resolvedEntries.get(i)).address() instanceof Inet6Address)) {
          return true;
        }
      }
    }
    
    
    return false;
  }
  
  private void finishResolve(Promise<T> promise) {
    if (!queriesInProgress.isEmpty())
    {
      Iterator<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> i = queriesInProgress.iterator();
      while (i.hasNext()) {
        Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = (Future)i.next();
        i.remove();
        
        if (!f.cancel(false)) {
          f.addListener(RELEASE_RESPONSE);
        }
      }
    }
    
    if (resolvedEntries != null)
    {
      for (InternetProtocolFamily f : resolveAddressTypes) {
        if (finishResolve(f.addressType(), resolvedEntries, promise)) {
          return;
        }
      }
    }
    

    int tries = maxAllowedQueries - allowedQueries;
    StringBuilder buf = new StringBuilder(64);
    
    buf.append("failed to resolve '");
    if (pristineHostname != null) {
      buf.append(pristineHostname);
    } else {
      buf.append(hostname);
    }
    buf.append('\'');
    if (tries > 1) {
      if (tries < maxAllowedQueries) {
        buf.append(" after ").append(tries).append(" queries ");
      }
      else
      {
        buf.append(". Exceeded max queries per resolve ").append(maxAllowedQueries).append(' ');
      }
    }
    

    if (trace != null) {
      buf.append(':').append(trace);
    }
    
    UnknownHostException cause = new UnknownHostException(buf.toString());
    
    resolveCache.cache(hostname, additionals, cause, parent.ch.eventLoop());
    promise.tryFailure(cause);
  }
  

  abstract boolean finishResolve(Class<? extends InetAddress> paramClass, List<DnsCacheEntry> paramList, Promise<T> paramPromise);
  
  abstract DnsNameResolverContext<T> newResolverContext(DnsNameResolver paramDnsNameResolver, String paramString, DnsRecord[] paramArrayOfDnsRecord, DnsCache paramDnsCache);
  
  static String decodeDomainName(ByteBuf in)
  {
    in.markReaderIndex();
    try {
      return DefaultDnsRecordDecoder.decodeName(in);
    }
    catch (CorruptedFrameException e) {
      return null;
    } finally {
      in.resetReaderIndex();
    }
  }
  
  private void followCname(InetSocketAddress nameServerAddr, String name, String cname, Promise<T> promise)
  {
    if (traceEnabled) {
      if (trace == null) {
        trace = new StringBuilder(128);
      }
      
      trace.append(StringUtil.NEWLINE);
      trace.append("\tfrom ");
      trace.append(nameServerAddr);
      trace.append(": ");
      trace.append(name);
      trace.append(" CNAME ");
      trace.append(cname);
    }
    
    InetSocketAddress nextAddr = nameServerAddrs.next();
    query(nextAddr, new DefaultDnsQuestion(cname, DnsRecordType.A), promise);
    query(nextAddr, new DefaultDnsQuestion(cname, DnsRecordType.AAAA), promise);
  }
  
  private void addTrace(InetSocketAddress nameServerAddr, String msg) {
    assert (traceEnabled);
    
    if (trace == null) {
      trace = new StringBuilder(128);
    }
    
    trace.append(StringUtil.NEWLINE);
    trace.append("\tfrom ");
    trace.append(nameServerAddr);
    trace.append(": ");
    trace.append(msg);
  }
  
  private void addTrace(Throwable cause) {
    assert (traceEnabled);
    
    if (trace == null) {
      trace = new StringBuilder(128);
    }
    
    trace.append(StringUtil.NEWLINE);
    trace.append("Caused by: ");
    trace.append(cause);
  }
}
