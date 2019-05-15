package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.dns.AbstractDnsOptPseudoRrRecord;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
















final class DnsQueryContext
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsQueryContext.class);
  
  private final DnsNameResolver parent;
  
  private final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
  
  private final int id;
  
  private final DnsQuestion question;
  
  private final DnsRecord[] additionals;
  
  private final DnsRecord optResource;
  private final InetSocketAddress nameServerAddr;
  private final boolean recursionDesired;
  private volatile ScheduledFuture<?> timeoutFuture;
  
  DnsQueryContext(DnsNameResolver parent, InetSocketAddress nameServerAddr, DnsQuestion question, DnsRecord[] additionals, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise)
  {
    this.parent = ((DnsNameResolver)ObjectUtil.checkNotNull(parent, "parent"));
    this.nameServerAddr = ((InetSocketAddress)ObjectUtil.checkNotNull(nameServerAddr, "nameServerAddr"));
    this.question = ((DnsQuestion)ObjectUtil.checkNotNull(question, "question"));
    this.additionals = ((DnsRecord[])ObjectUtil.checkNotNull(additionals, "additionals"));
    this.promise = ((Promise)ObjectUtil.checkNotNull(promise, "promise"));
    recursionDesired = parent.isRecursionDesired();
    id = queryContextManager.add(this);
    
    if (parent.isOptResourceEnabled()) {
      optResource = new AbstractDnsOptPseudoRrRecord(parent.maxPayloadSize(), 0, 0) {};
    }
    else
    {
      optResource = null;
    }
  }
  
  InetSocketAddress nameServerAddr() {
    return nameServerAddr;
  }
  
  DnsQuestion question() {
    return question;
  }
  
  void query() {
    DnsQuestion question = question();
    InetSocketAddress nameServerAddr = nameServerAddr();
    DatagramDnsQuery query = new DatagramDnsQuery(null, nameServerAddr, id);
    
    query.setRecursionDesired(recursionDesired);
    
    query.addRecord(DnsSection.QUESTION, question);
    
    for (DnsRecord record : additionals) {
      query.addRecord(DnsSection.ADDITIONAL, record);
    }
    
    if (optResource != null) {
      query.addRecord(DnsSection.ADDITIONAL, optResource);
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("{} WRITE: [{}: {}], {}", new Object[] { parent.ch, Integer.valueOf(id), nameServerAddr, question });
    }
    
    sendQuery(query);
  }
  
  private void sendQuery(final DnsQuery query) {
    if (parent.channelFuture.isDone()) {
      writeQuery(query);
    } else {
      parent.channelFuture.addListener(new GenericFutureListener()
      {
        public void operationComplete(Future<? super Channel> future) throws Exception {
          if (future.isSuccess()) {
            DnsQueryContext.this.writeQuery(query);
          } else {
            promise.tryFailure(future.cause());
          }
        }
      });
    }
  }
  
  private void writeQuery(DnsQuery query) {
    final ChannelFuture writeFuture = parent.ch.writeAndFlush(query);
    if (writeFuture.isDone()) {
      onQueryWriteCompletion(writeFuture);
    } else {
      writeFuture.addListener(new ChannelFutureListener()
      {
        public void operationComplete(ChannelFuture future) throws Exception {
          DnsQueryContext.this.onQueryWriteCompletion(writeFuture);
        }
      });
    }
  }
  
  private void onQueryWriteCompletion(ChannelFuture writeFuture) {
    if (!writeFuture.isSuccess()) {
      setFailure("failed to send a query", writeFuture.cause());
      return;
    }
    

    final long queryTimeoutMillis = parent.queryTimeoutMillis();
    if (queryTimeoutMillis > 0L) {
      timeoutFuture = parent.ch.eventLoop().schedule(new Runnable()
      {
        public void run() {
          if (promise.isDone())
          {
            return;
          }
          
          DnsQueryContext.this.setFailure("query timed out after " + queryTimeoutMillis + " milliseconds", null); } }, queryTimeoutMillis, TimeUnit.MILLISECONDS);
    }
  }
  

  void finish(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope)
  {
    DnsResponse res = (DnsResponse)envelope.content();
    if (res.count(DnsSection.QUESTION) != 1) {
      logger.warn("Received a DNS response with invalid number of questions: {}", envelope);
      return;
    }
    
    if (!question().equals(res.recordAt(DnsSection.QUESTION))) {
      logger.warn("Received a mismatching DNS response: {}", envelope);
      return;
    }
    
    setSuccess(envelope);
  }
  
  private void setSuccess(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
    parent.queryContextManager.remove(nameServerAddr(), id);
    

    ScheduledFuture<?> timeoutFuture = this.timeoutFuture;
    if (timeoutFuture != null) {
      timeoutFuture.cancel(false);
    }
    
    Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise = this.promise;
    if (promise.setUncancellable())
    {
      AddressedEnvelope<DnsResponse, InetSocketAddress> castResponse = envelope.retain();
      
      promise.setSuccess(castResponse);
    }
  }
  
  private void setFailure(String message, Throwable cause) {
    InetSocketAddress nameServerAddr = nameServerAddr();
    parent.queryContextManager.remove(nameServerAddr, id);
    
    StringBuilder buf = new StringBuilder(message.length() + 64);
    buf.append('[').append(nameServerAddr).append("] ").append(message).append(" (no stack trace available)");
    

    DnsNameResolverException e;
    
    DnsNameResolverException e;
    
    if (cause != null) {
      e = new DnsNameResolverException(nameServerAddr, question(), buf.toString(), cause);
    } else {
      e = new DnsNameResolverException(nameServerAddr, question(), buf.toString());
    }
    
    promise.tryFailure(e);
  }
}
