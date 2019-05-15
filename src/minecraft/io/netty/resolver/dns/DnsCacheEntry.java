package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;























public final class DnsCacheEntry
{
  private final String hostname;
  private final InetAddress address;
  private final Throwable cause;
  private volatile ScheduledFuture<?> expirationFuture;
  
  public DnsCacheEntry(String hostname, InetAddress address)
  {
    this.hostname = ((String)ObjectUtil.checkNotNull(hostname, "hostname"));
    this.address = ((InetAddress)ObjectUtil.checkNotNull(address, "address"));
    cause = null;
  }
  
  public DnsCacheEntry(String hostname, Throwable cause) {
    this.hostname = ((String)ObjectUtil.checkNotNull(hostname, "hostname"));
    this.cause = ((Throwable)ObjectUtil.checkNotNull(cause, "cause"));
    address = null;
  }
  
  public String hostname() {
    return hostname;
  }
  
  public InetAddress address() {
    return address;
  }
  
  public Throwable cause() {
    return cause;
  }
  
  void scheduleExpiration(EventLoop loop, Runnable task, long delay, TimeUnit unit) {
    assert (expirationFuture == null) : "expiration task scheduled already";
    expirationFuture = loop.schedule(task, delay, unit);
  }
  
  void cancelExpiration() {
    ScheduledFuture<?> expirationFuture = this.expirationFuture;
    if (expirationFuture != null) {
      expirationFuture.cancel(false);
    }
  }
  
  public String toString()
  {
    if (cause != null) {
      return hostname + '/' + cause;
    }
    return address.toString();
  }
}
