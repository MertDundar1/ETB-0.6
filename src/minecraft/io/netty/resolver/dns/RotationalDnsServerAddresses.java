package io.netty.resolver.dns;

import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;















final class RotationalDnsServerAddresses
  extends DefaultDnsServerAddresses
{
  private static final AtomicIntegerFieldUpdater<RotationalDnsServerAddresses> startIdxUpdater;
  private volatile int startIdx;
  
  static
  {
    AtomicIntegerFieldUpdater<RotationalDnsServerAddresses> updater = PlatformDependent.newAtomicIntegerFieldUpdater(RotationalDnsServerAddresses.class, "startIdx");
    

    if (updater == null) {
      updater = AtomicIntegerFieldUpdater.newUpdater(RotationalDnsServerAddresses.class, "startIdx");
    }
    
    startIdxUpdater = updater;
  }
  


  RotationalDnsServerAddresses(InetSocketAddress[] addresses)
  {
    super("rotational", addresses);
  }
  
  public DnsServerAddressStream stream()
  {
    for (;;) {
      int curStartIdx = startIdx;
      int nextStartIdx = curStartIdx + 1;
      if (nextStartIdx >= addresses.length) {
        nextStartIdx = 0;
      }
      if (startIdxUpdater.compareAndSet(this, curStartIdx, nextStartIdx)) {
        return new SequentialDnsServerAddressStream(addresses, curStartIdx);
      }
    }
  }
}
