package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ServerChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;














public abstract class AbstractEpollServerChannel
  extends AbstractEpollChannel
  implements ServerChannel
{
  private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
  


  @Deprecated
  protected AbstractEpollServerChannel(int fd)
  {
    this(new Socket(fd), false);
  }
  


  @Deprecated
  protected AbstractEpollServerChannel(FileDescriptor fd)
  {
    this(new Socket(fd.intValue()));
  }
  


  @Deprecated
  protected AbstractEpollServerChannel(Socket fd)
  {
    this(fd, isSoErrorZero(fd));
  }
  
  protected AbstractEpollServerChannel(Socket fd, boolean active) {
    super(null, fd, Native.EPOLLIN, active);
  }
  
  public ChannelMetadata metadata()
  {
    return METADATA;
  }
  
  protected boolean isCompatible(EventLoop loop)
  {
    return loop instanceof EpollEventLoop;
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
  
  abstract Channel newChildChannel(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws Exception;
  
  final class EpollServerSocketUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
    EpollServerSocketUnsafe() { super(); }
    


    private final byte[] acceptedAddress = new byte[26];
    

    public void connect(SocketAddress socketAddress, SocketAddress socketAddress2, ChannelPromise channelPromise)
    {
      channelPromise.setFailure(new UnsupportedOperationException());
    }
    
    void epollInReady()
    {
      assert (eventLoop().inEventLoop());
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
      
      Throwable exception = null;
      try
      {
        try
        {
          do
          {
            allocHandle.lastBytesRead(fd().accept(acceptedAddress));
            if (allocHandle.lastBytesRead() == -1) {
              break;
            }
            
            allocHandle.incMessagesRead(1);
            
            int len = acceptedAddress[0];
            readPending = false;
            pipeline.fireChannelRead(newChildChannel(allocHandle.lastBytesRead(), acceptedAddress, 1, len));
          } while (allocHandle.continueReading());
        } catch (Throwable t) {
          exception = t;
        }
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        
        if (exception != null) {
          pipeline.fireExceptionCaught(exception);
        }
      } finally {
        epollInFinally(config);
      }
    }
  }
}
