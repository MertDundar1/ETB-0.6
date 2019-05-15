package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;















public class DefaultMaxBytesRecvByteBufAllocator
  implements MaxBytesRecvByteBufAllocator
{
  private volatile int maxBytesPerRead;
  private volatile int maxBytesPerIndividualRead;
  
  private final class HandleImpl
    implements RecvByteBufAllocator.Handle
  {
    private int individualReadMax;
    private int bytesToRead;
    private int lastBytesRead;
    private int attemptBytesRead;
    
    private HandleImpl() {}
    
    public ByteBuf allocate(ByteBufAllocator alloc)
    {
      return alloc.ioBuffer(guess());
    }
    
    public int guess()
    {
      return Math.min(individualReadMax, bytesToRead);
    }
    
    public void reset(ChannelConfig config)
    {
      bytesToRead = maxBytesPerRead();
      individualReadMax = maxBytesPerIndividualRead();
    }
    

    public void incMessagesRead(int amt) {}
    

    public void lastBytesRead(int bytes)
    {
      lastBytesRead = bytes;
      

      bytesToRead -= bytes;
    }
    
    public int lastBytesRead()
    {
      return lastBytesRead;
    }
    

    public boolean continueReading()
    {
      return (bytesToRead > 0) && (attemptBytesRead == lastBytesRead);
    }
    

    public void readComplete() {}
    

    public void attemptedBytesRead(int bytes)
    {
      attemptBytesRead = bytes;
    }
    
    public int attemptedBytesRead()
    {
      return attemptBytesRead;
    }
  }
  
  public DefaultMaxBytesRecvByteBufAllocator() {
    this(65536, 65536);
  }
  
  public DefaultMaxBytesRecvByteBufAllocator(int maxBytesPerRead, int maxBytesPerIndividualRead) {
    checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
    this.maxBytesPerRead = maxBytesPerRead;
    this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
  }
  
  public RecvByteBufAllocator.Handle newHandle()
  {
    return new HandleImpl(null);
  }
  
  public int maxBytesPerRead()
  {
    return maxBytesPerRead;
  }
  
  public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int maxBytesPerRead)
  {
    if (maxBytesPerRead <= 0) {
      throw new IllegalArgumentException("maxBytesPerRead: " + maxBytesPerRead + " (expected: > 0)");
    }
    

    synchronized (this) {
      int maxBytesPerIndividualRead = maxBytesPerIndividualRead();
      if (maxBytesPerRead < maxBytesPerIndividualRead) {
        throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead);
      }
      


      this.maxBytesPerRead = maxBytesPerRead;
    }
    return this;
  }
  
  public int maxBytesPerIndividualRead()
  {
    return maxBytesPerIndividualRead;
  }
  
  public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int maxBytesPerIndividualRead)
  {
    if (maxBytesPerIndividualRead <= 0) {
      throw new IllegalArgumentException("maxBytesPerIndividualRead: " + maxBytesPerIndividualRead + " (expected: > 0)");
    }
    


    synchronized (this) {
      int maxBytesPerRead = maxBytesPerRead();
      if (maxBytesPerIndividualRead > maxBytesPerRead) {
        throw new IllegalArgumentException("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + maxBytesPerRead + "): " + maxBytesPerIndividualRead);
      }
      


      this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
    }
    return this;
  }
  
  public synchronized Map.Entry<Integer, Integer> maxBytesPerReadPair()
  {
    return new AbstractMap.SimpleEntry(Integer.valueOf(maxBytesPerRead), Integer.valueOf(maxBytesPerIndividualRead));
  }
  
  private void checkMaxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
    if (maxBytesPerRead <= 0) {
      throw new IllegalArgumentException("maxBytesPerRead: " + maxBytesPerRead + " (expected: > 0)");
    }
    if (maxBytesPerIndividualRead <= 0) {
      throw new IllegalArgumentException("maxBytesPerIndividualRead: " + maxBytesPerIndividualRead + " (expected: > 0)");
    }
    
    if (maxBytesPerRead < maxBytesPerIndividualRead) {
      throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead);
    }
  }
  



  public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead)
  {
    checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
    

    synchronized (this) {
      this.maxBytesPerRead = maxBytesPerRead;
      this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
    }
    return this;
  }
}
