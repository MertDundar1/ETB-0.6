package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundBuffer.MessageProcessor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;

































final class IovArray
  implements ChannelOutboundBuffer.MessageProcessor
{
  private static final int ADDRESS_SIZE = PlatformDependent.addressSize();
  




  private static final int IOV_SIZE = 2 * ADDRESS_SIZE;
  



  private static final int CAPACITY = Native.IOV_MAX * IOV_SIZE;
  
  private static final FastThreadLocal<IovArray> ARRAY = new FastThreadLocal()
  {
    protected IovArray initialValue() throws Exception {
      return new IovArray(null);
    }
    
    protected void onRemoval(IovArray value)
      throws Exception
    {
      PlatformDependent.freeMemory(memoryAddress);
    }
  };
  private final long memoryAddress;
  private int count;
  private long size;
  
  private IovArray()
  {
    memoryAddress = PlatformDependent.allocateMemory(CAPACITY);
  }
  



  private boolean add(ByteBuf buf)
  {
    if (count == Native.IOV_MAX)
    {
      return false;
    }
    
    int len = buf.readableBytes();
    if (len == 0)
    {


      return true;
    }
    
    long addr = buf.memoryAddress();
    int offset = buf.readerIndex();
    
    long baseOffset = memoryAddress(count++);
    long lengthOffset = baseOffset + ADDRESS_SIZE;
    
    if (ADDRESS_SIZE == 8)
    {
      PlatformDependent.putLong(baseOffset, addr + offset);
      PlatformDependent.putLong(lengthOffset, len);
    } else {
      assert (ADDRESS_SIZE == 4);
      PlatformDependent.putInt(baseOffset, (int)addr + offset);
      PlatformDependent.putInt(lengthOffset, len);
    }
    
    size += len;
    return true;
  }
  



  long processWritten(int index, long written)
  {
    long baseOffset = memoryAddress(index);
    long lengthOffset = baseOffset + ADDRESS_SIZE;
    if (ADDRESS_SIZE == 8)
    {
      long len = PlatformDependent.getLong(lengthOffset);
      if (len > written) {
        long offset = PlatformDependent.getLong(baseOffset);
        PlatformDependent.putLong(baseOffset, offset + written);
        PlatformDependent.putLong(lengthOffset, len - written);
        return -1L;
      }
      return len;
    }
    assert (ADDRESS_SIZE == 4);
    long len = PlatformDependent.getInt(lengthOffset);
    if (len > written) {
      int offset = PlatformDependent.getInt(baseOffset);
      PlatformDependent.putInt(baseOffset, (int)(offset + written));
      PlatformDependent.putInt(lengthOffset, (int)(len - written));
      return -1L;
    }
    return len;
  }
  



  int count()
  {
    return count;
  }
  


  long size()
  {
    return size;
  }
  


  long memoryAddress(int offset)
  {
    return memoryAddress + IOV_SIZE * offset;
  }
  
  public boolean processMessage(Object msg) throws Exception
  {
    return ((msg instanceof ByteBuf)) && (add((ByteBuf)msg));
  }
  

  static IovArray get(ChannelOutboundBuffer buffer)
    throws Exception
  {
    IovArray array = (IovArray)ARRAY.get();
    size = 0L;
    count = 0;
    buffer.forEachFlushedMessage(array);
    return array;
  }
}
