package io.netty.channel.epoll;

import io.netty.channel.ChannelConfig;
import io.netty.channel.RecvByteBufAllocator.DelegatingHandle;
import io.netty.channel.RecvByteBufAllocator.Handle;












class EpollRecvByteAllocatorHandle
  extends RecvByteBufAllocator.DelegatingHandle
{
  private boolean isEdgeTriggered;
  private final ChannelConfig config;
  private boolean receivedRdHup;
  
  EpollRecvByteAllocatorHandle(RecvByteBufAllocator.Handle handle, ChannelConfig config)
  {
    super(handle);
    this.config = config;
  }
  
  final void receivedRdHup() {
    receivedRdHup = true;
  }
  
  boolean maybeMoreDataToRead() {
    return (isEdgeTriggered) && (lastBytesRead() > 0);
  }
  
  final void edgeTriggered(boolean edgeTriggered) {
    isEdgeTriggered = edgeTriggered;
  }
  
  final boolean isEdgeTriggered() {
    return isEdgeTriggered;
  }
  










  public final boolean continueReading()
  {
    return (receivedRdHup) || ((maybeMoreDataToRead()) && (config.isAutoRead())) || (super.continueReading());
  }
}
