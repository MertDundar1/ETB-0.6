package io.netty.channel.epoll;

import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.ServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


















public final class EpollServerSocketChannel
  extends AbstractEpollChannel
  implements ServerSocketChannel
{
  private final EpollServerSocketChannelConfig config;
  private volatile InetSocketAddress local;
  
  public EpollServerSocketChannel()
  {
    super(Native.socketStreamFd(), 4);
    config = new EpollServerSocketChannelConfig(this);
  }
  
  protected boolean isCompatible(EventLoop loop)
  {
    return loop instanceof EpollEventLoop;
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception
  {
    InetSocketAddress addr = (InetSocketAddress)localAddress;
    checkResolvable(addr);
    Native.bind(fd, addr.getAddress(), addr.getPort());
    local = Native.localAddress(fd);
    Native.listen(fd, config.getBacklog());
    active = true;
  }
  
  public EpollServerSocketChannelConfig config()
  {
    return config;
  }
  
  protected InetSocketAddress localAddress0()
  {
    return local;
  }
  
  protected InetSocketAddress remoteAddress0()
  {
    return null;
  }
  
  protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe()
  {
    return new EpollServerSocketUnsafe();
  }
  
  protected void doWrite(ChannelOutboundBuffer in) throws Exception
  {
    throw new UnsupportedOperationException();
  }
  


  protected Object filterOutboundMessage(Object msg) throws Exception { throw new UnsupportedOperationException(); }
  
  final class EpollServerSocketUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
    EpollServerSocketUnsafe() { super(); }
    

    public void connect(SocketAddress socketAddress, SocketAddress socketAddress2, ChannelPromise channelPromise)
    {
      channelPromise.setFailure(new UnsupportedOperationException());
    }
    
    void epollInReady()
    {
      assert (eventLoop().inEventLoop());
      ChannelPipeline pipeline = pipeline();
      Throwable exception = null;
      try {
        try {
          for (;;) {
            int socketFd = Native.accept(fd);
            if (socketFd == -1) {
              break;
            }
            try
            {
              readPending = false;
              pipeline.fireChannelRead(new EpollSocketChannel(EpollServerSocketChannel.this, socketFd));
            }
            catch (Throwable t) {
              pipeline.fireChannelReadComplete();
              pipeline.fireExceptionCaught(t);
            }
          }
        } catch (Throwable t) {
          exception = t;
        }
        pipeline.fireChannelReadComplete();
        
        if (exception != null) {
          pipeline.fireExceptionCaught(exception);

        }
        

      }
      finally
      {

        if ((!config.isAutoRead()) && (!readPending)) {
          clearEpollIn0();
        }
      }
    }
  }
}
