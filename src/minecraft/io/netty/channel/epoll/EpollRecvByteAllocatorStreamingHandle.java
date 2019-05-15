package io.netty.channel.epoll;

import io.netty.channel.ChannelConfig;
import io.netty.channel.RecvByteBufAllocator.Handle;













final class EpollRecvByteAllocatorStreamingHandle
  extends EpollRecvByteAllocatorHandle
{
  public EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.Handle handle, ChannelConfig config)
  {
    super(handle, config);
  }
  




  boolean maybeMoreDataToRead()
  {
    return (isEdgeTriggered()) && (lastBytesRead() == attemptedBytesRead());
  }
}
