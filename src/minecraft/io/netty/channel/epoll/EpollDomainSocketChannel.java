package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import java.net.SocketAddress;
















public final class EpollDomainSocketChannel
  extends AbstractEpollStreamChannel
  implements DomainSocketChannel
{
  private final EpollDomainSocketChannelConfig config = new EpollDomainSocketChannelConfig(this);
  private volatile DomainSocketAddress local;
  private volatile DomainSocketAddress remote;
  
  public EpollDomainSocketChannel()
  {
    super(Socket.newSocketDomain(), false);
  }
  


  @Deprecated
  public EpollDomainSocketChannel(Channel parent, FileDescriptor fd)
  {
    super(parent, new Socket(fd.intValue()));
  }
  




  @Deprecated
  public EpollDomainSocketChannel(FileDescriptor fd)
  {
    super(fd);
  }
  
  public EpollDomainSocketChannel(Channel parent, Socket fd) {
    super(parent, fd);
  }
  


  public EpollDomainSocketChannel(Socket fd, boolean active)
  {
    super(fd, active);
  }
  
  protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe()
  {
    return new EpollDomainUnsafe(null);
  }
  
  protected DomainSocketAddress localAddress0()
  {
    return local;
  }
  
  protected DomainSocketAddress remoteAddress0()
  {
    return remote;
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception
  {
    fd().bind(localAddress);
    local = ((DomainSocketAddress)localAddress);
  }
  
  public EpollDomainSocketChannelConfig config()
  {
    return config;
  }
  
  protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception
  {
    if (super.doConnect(remoteAddress, localAddress)) {
      local = ((DomainSocketAddress)localAddress);
      remote = ((DomainSocketAddress)remoteAddress);
      return true;
    }
    return false;
  }
  
  public DomainSocketAddress remoteAddress()
  {
    return (DomainSocketAddress)super.remoteAddress();
  }
  
  public DomainSocketAddress localAddress()
  {
    return (DomainSocketAddress)super.localAddress();
  }
  
  protected boolean doWriteSingle(ChannelOutboundBuffer in, int writeSpinCount) throws Exception
  {
    Object msg = in.current();
    if (((msg instanceof FileDescriptor)) && (Native.sendFd(fd().intValue(), ((FileDescriptor)msg).intValue()) > 0))
    {
      in.remove();
      return true;
    }
    return super.doWriteSingle(in, writeSpinCount);
  }
  
  protected Object filterOutboundMessage(Object msg)
  {
    if ((msg instanceof FileDescriptor)) {
      return msg;
    }
    return super.filterOutboundMessage(msg);
  }
  
  private final class EpollDomainUnsafe extends AbstractEpollStreamChannel.EpollStreamUnsafe { private EpollDomainUnsafe() { super(); }
    
    void epollInReady() {
      switch (EpollDomainSocketChannel.1.$SwitchMap$io$netty$channel$unix$DomainSocketReadMode[config().getReadMode().ordinal()]) {
      case 1: 
        super.epollInReady();
        break;
      case 2: 
        epollInReadFd();
        break;
      default: 
        throw new Error();
      }
    }
    
    private void epollInReadFd() {
      if (fd().isInputShutdown()) {
        clearEpollIn0();
        return;
      }
      ChannelConfig config = config();
      EpollRecvByteAllocatorHandle allocHandle = recvBufAllocHandle();
      allocHandle.edgeTriggered(isFlagSet(Native.EPOLLET));
      
      ChannelPipeline pipeline = pipeline();
      allocHandle.reset(config);
      epollInBefore();
      

      try
      {
        do
        {
          allocHandle.lastBytesRead(Native.recvFd(fd().intValue()));
          switch (allocHandle.lastBytesRead()) {
          case 0: 
            break;
          case -1: 
            close(voidPromise()); return;
          
          default: 
            allocHandle.incMessagesRead(1);
            readPending = false;
            pipeline.fireChannelRead(new FileDescriptor(allocHandle.lastBytesRead()));
          }
          
        } while (allocHandle.continueReading());
        
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
      } catch (Throwable t) {
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        pipeline.fireExceptionCaught(t);
      } finally {
        epollInFinally(config);
      }
    }
  }
}
