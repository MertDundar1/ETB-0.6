package io.netty.channel;

import java.util.Queue;
import java.util.Set;
















public class ThreadPerChannelEventLoop
  extends SingleThreadEventLoop
{
  private final ThreadPerChannelEventLoopGroup parent;
  private Channel ch;
  
  public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup parent)
  {
    super(parent, threadFactory, true);
    this.parent = parent;
  }
  
  public ChannelFuture register(Channel channel, ChannelPromise promise)
  {
    super.register(channel, promise).addListener(new ChannelFutureListener()
    {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          ch = future.channel();
        } else {
          deregister();
        }
      }
    });
  }
  
  protected void run()
  {
    for (;;) {
      Runnable task = takeTask();
      if (task != null) {
        task.run();
        updateLastExecutionTime();
      }
      
      Channel ch = this.ch;
      if (isShuttingDown()) {
        if (ch != null) {
          ch.unsafe().close(ch.unsafe().voidPromise());
        }
        if (confirmShutdown()) {
          break;
        }
      }
      else if (ch != null)
      {
        if (!ch.isRegistered()) {
          runAllTasks();
          deregister();
        }
      }
    }
  }
  
  protected void deregister()
  {
    ch = null;
    parent.activeChildren.remove(this);
    parent.idleChildren.add(this);
  }
}
