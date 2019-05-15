package io.netty.channel.epoll;

import io.netty.channel.Channel.Unsafe;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.collection.IntObjectMap.Entry;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;











final class EpollEventLoop
  extends SingleThreadEventLoop
{
  private static final InternalLogger logger;
  private static final AtomicIntegerFieldUpdater<EpollEventLoop> WAKEN_UP_UPDATER;
  private final int epollFd;
  private final int eventFd;
  
  static
  {
    logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
    


    AtomicIntegerFieldUpdater<EpollEventLoop> updater = PlatformDependent.newAtomicIntegerFieldUpdater(EpollEventLoop.class, "wakenUp");
    
    if (updater == null) {
      updater = AtomicIntegerFieldUpdater.newUpdater(EpollEventLoop.class, "wakenUp");
    }
    WAKEN_UP_UPDATER = updater;
  }
  


  private final IntObjectMap<AbstractEpollChannel> ids = new IntObjectHashMap();
  
  private final long[] events;
  
  private int id;
  
  private boolean overflown;
  private volatile int wakenUp;
  private volatile int ioRatio = 50;
  
  EpollEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, int maxEvents) {
    super(parent, threadFactory, false);
    events = new long[maxEvents];
    boolean success = false;
    int epollFd = -1;
    int eventFd = -1;
    try {
      this.epollFd = (epollFd = Native.epollCreate());
      this.eventFd = (eventFd = Native.eventFd());
      Native.epollCtlAdd(epollFd, eventFd, 1, 0);
      success = true; return;
    } finally {
      if (!success) {
        if (epollFd != -1) {
          try {
            Native.close(epollFd);
          }
          catch (Exception e) {}
        }
        
        if (eventFd != -1) {
          try {
            Native.close(eventFd);
          }
          catch (Exception e) {}
        }
      }
    }
  }
  
  private int nextId()
  {
    int id = this.id;
    if (id == Integer.MAX_VALUE) {
      overflown = true;
      id = 0;
    }
    if (overflown)
    {


      while (ids.containsKey(++id)) {}
      this.id = id;

    }
    else
    {
      this.id = (++id);
    }
    return id;
  }
  
  protected void wakeup(boolean inEventLoop)
  {
    if ((!inEventLoop) && (WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)))
    {
      Native.eventFdWrite(eventFd, 1L);
    }
  }
  


  void add(AbstractEpollChannel ch)
  {
    assert (inEventLoop());
    int id = nextId();
    Native.epollCtlAdd(epollFd, fd, flags, id);
    id = id;
    ids.put(id, ch);
  }
  


  void modify(AbstractEpollChannel ch)
  {
    assert (inEventLoop());
    Native.epollCtlMod(epollFd, fd, flags, id);
  }
  


  void remove(AbstractEpollChannel ch)
  {
    assert (inEventLoop());
    if ((ids.remove(id) != null) && (ch.isOpen()))
    {

      Native.epollCtlDel(epollFd, fd);
    }
  }
  

  protected Queue<Runnable> newTaskQueue()
  {
    return PlatformDependent.newMpscQueue();
  }
  


  public int getIoRatio()
  {
    return ioRatio;
  }
  



  public void setIoRatio(int ioRatio)
  {
    if ((ioRatio <= 0) || (ioRatio > 100)) {
      throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
    }
    this.ioRatio = ioRatio;
  }
  
  private int epollWait(boolean oldWakenUp) {
    int selectCnt = 0;
    long currentTimeNanos = System.nanoTime();
    long selectDeadLineNanos = currentTimeNanos + delayNanos(currentTimeNanos);
    for (;;) {
      long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
      if (timeoutMillis <= 0L) {
        if (selectCnt != 0) break;
        int ready = Native.epollWait(epollFd, events, 0);
        if (ready > 0) {
          return ready;
        }
        break;
      }
      

      int selectedKeys = Native.epollWait(epollFd, events, (int)timeoutMillis);
      selectCnt++;
      
      if ((selectedKeys != 0) || (oldWakenUp) || (wakenUp == 1) || (hasTasks()) || (hasScheduledTasks()))
      {



        return selectedKeys;
      }
      currentTimeNanos = System.nanoTime();
    }
    return 0;
  }
  
  protected void run()
  {
    for (;;) {
      boolean oldWakenUp = WAKEN_UP_UPDATER.getAndSet(this, 0) == 1;
      try { int ready;
        int ready;
        if (hasTasks())
        {
          ready = Native.epollWait(epollFd, events, 0);
        } else {
          ready = epollWait(oldWakenUp);
          




























          if (wakenUp == 1) {
            Native.eventFdWrite(eventFd, 1L);
          }
        }
        
        int ioRatio = this.ioRatio;
        if (ioRatio == 100) {
          if (ready > 0) {
            processReady(events, ready);
          }
          runAllTasks();
        } else {
          long ioStartTime = System.nanoTime();
          
          if (ready > 0) {
            processReady(events, ready);
          }
          
          long ioTime = System.nanoTime() - ioStartTime;
          runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
        }
        
        if (isShuttingDown()) {
          closeAll();
          if (confirmShutdown()) {
            break;
          }
        }
      } catch (Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);
        

        try
        {
          Thread.sleep(1000L);
        }
        catch (InterruptedException e) {}
      }
    }
  }
  
  private void closeAll()
  {
    Native.epollWait(epollFd, events, 0);
    Collection<AbstractEpollChannel> channels = new ArrayList(ids.size());
    
    for (IntObjectMap.Entry<AbstractEpollChannel> entry : ids.entries()) {
      channels.add(entry.value());
    }
    
    for (AbstractEpollChannel ch : channels) {
      ch.unsafe().close(ch.unsafe().voidPromise());
    }
  }
  
  private void processReady(long[] events, int ready) {
    for (int i = 0; i < ready; i++) {
      long ev = events[i];
      
      int id = (int)(ev >> 32);
      if (id == 0)
      {
        Native.eventFdRead(eventFd);
      } else {
        boolean read = (ev & 1L) != 0L;
        boolean write = (ev & 0x2) != 0L;
        boolean close = (ev & 0x8) != 0L;
        
        AbstractEpollChannel ch = (AbstractEpollChannel)ids.get(id);
        if (ch != null) {
          AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
          if ((write) && (ch.isOpen()))
          {
            unsafe.epollOutReady();
          }
          if ((read) && (ch.isOpen()))
          {
            unsafe.epollInReady();
          }
          if ((close) && (ch.isOpen())) {
            unsafe.epollRdHupReady();
          }
        }
      }
    }
  }
  
  protected void cleanup()
  {
    try {
      Native.close(epollFd);
    } catch (IOException e) {
      logger.warn("Failed to close the epoll fd.", e);
    }
    try {
      Native.close(eventFd);
    } catch (IOException e) {
      logger.warn("Failed to close the event fd.", e);
    }
  }
}
