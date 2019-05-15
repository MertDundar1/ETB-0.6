package io.netty.channel.epoll;

import io.netty.util.internal.PlatformDependent;


































final class EpollEventArray
{
  private static final int EPOLL_EVENT_SIZE = ;
  
  private static final int EPOLL_DATA_OFFSET = Native.offsetofEpollData();
  private long memoryAddress;
  private int length;
  
  EpollEventArray(int length)
  {
    if (length < 1) {
      throw new IllegalArgumentException("length must be >= 1 but was " + length);
    }
    this.length = length;
    memoryAddress = allocate(length);
  }
  
  private static long allocate(int length) {
    return PlatformDependent.allocateMemory(length * EPOLL_EVENT_SIZE);
  }
  


  long memoryAddress()
  {
    return memoryAddress;
  }
  



  int length()
  {
    return length;
  }
  



  void increase()
  {
    length <<= 1;
    free();
    memoryAddress = allocate(length);
  }
  


  void free()
  {
    PlatformDependent.freeMemory(memoryAddress);
  }
  


  int events(int index)
  {
    return PlatformDependent.getInt(memoryAddress + index * EPOLL_EVENT_SIZE);
  }
  


  int fd(int index)
  {
    return PlatformDependent.getInt(memoryAddress + index * EPOLL_EVENT_SIZE + EPOLL_DATA_OFFSET);
  }
}
