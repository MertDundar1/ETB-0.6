package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ThreadLocalRandom;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;


























public class RoundRobinInetSocketAddressResolver
  extends InetSocketAddressResolver
{
  public RoundRobinInetSocketAddressResolver(EventExecutor executor, NameResolver<InetAddress> nameResolver)
  {
    super(executor, nameResolver);
  }
  



  protected void doResolve(final InetSocketAddress unresolvedAddress, final Promise<InetSocketAddress> promise)
    throws Exception
  {
    nameResolver.resolveAll(unresolvedAddress.getHostName()).addListener(new FutureListener()
    {
      public void operationComplete(Future<List<InetAddress>> future) throws Exception
      {
        if (future.isSuccess()) {
          List<InetAddress> inetAddresses = (List)future.getNow();
          int numAddresses = inetAddresses.size();
          if (numAddresses == 0) {
            promise.setFailure(new UnknownHostException(unresolvedAddress.getHostName()));
          }
          else
          {
            int index = numAddresses == 1 ? 0 : ThreadLocalRandom.current().nextInt(numAddresses);
            
            promise.setSuccess(new InetSocketAddress((InetAddress)inetAddresses.get(index), unresolvedAddress.getPort()));
          }
        }
        else {
          promise.setFailure(future.cause());
        }
      }
    });
  }
}
