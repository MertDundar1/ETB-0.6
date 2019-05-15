package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

















public abstract class DefaultMaxMessagesRecvByteBufAllocator
  implements MaxMessagesRecvByteBufAllocator
{
  private volatile int maxMessagesPerRead;
  
  public DefaultMaxMessagesRecvByteBufAllocator()
  {
    this(1);
  }
  
  public DefaultMaxMessagesRecvByteBufAllocator(int maxMessagesPerRead) {
    maxMessagesPerRead(maxMessagesPerRead);
  }
  
  public int maxMessagesPerRead()
  {
    return maxMessagesPerRead;
  }
  
  public MaxMessagesRecvByteBufAllocator maxMessagesPerRead(int maxMessagesPerRead)
  {
    if (maxMessagesPerRead <= 0) {
      throw new IllegalArgumentException("maxMessagesPerRead: " + maxMessagesPerRead + " (expected: > 0)");
    }
    this.maxMessagesPerRead = maxMessagesPerRead;
    return this;
  }
  

  public abstract class MaxMessageHandle
    implements RecvByteBufAllocator.Handle
  {
    private ChannelConfig config;
    
    private int maxMessagePerRead;
    private int totalMessages;
    private int totalBytesRead;
    private int attemptedBytesRead;
    private int lastBytesRead;
    
    public MaxMessageHandle() {}
    
    public void reset(ChannelConfig config)
    {
      this.config = config;
      maxMessagePerRead = maxMessagesPerRead();
      totalMessages = (this.totalBytesRead = 0);
    }
    
    public ByteBuf allocate(ByteBufAllocator alloc)
    {
      return alloc.ioBuffer(guess());
    }
    
    public final void incMessagesRead(int amt)
    {
      totalMessages += amt;
    }
    
    public final void lastBytesRead(int bytes)
    {
      lastBytesRead = bytes;
      

      totalBytesRead += bytes;
      if (totalBytesRead < 0) {
        totalBytesRead = Integer.MAX_VALUE;
      }
    }
    
    public final int lastBytesRead()
    {
      return lastBytesRead;
    }
    
    public boolean continueReading()
    {
      return (config.isAutoRead()) && (attemptedBytesRead == lastBytesRead) && (totalMessages < maxMessagePerRead) && (totalBytesRead < Integer.MAX_VALUE);
    }
    



    public void readComplete() {}
    


    public int attemptedBytesRead()
    {
      return attemptedBytesRead;
    }
    
    public void attemptedBytesRead(int bytes)
    {
      attemptedBytesRead = bytes;
    }
    
    protected final int totalBytesRead() {
      return totalBytesRead;
    }
  }
}
