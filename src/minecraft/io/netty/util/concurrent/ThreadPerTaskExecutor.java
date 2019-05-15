package io.netty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;













public final class ThreadPerTaskExecutor
  implements Executor
{
  private final ThreadFactory threadFactory;
  
  public ThreadPerTaskExecutor(ThreadFactory threadFactory)
  {
    if (threadFactory == null) {
      throw new NullPointerException("threadFactory");
    }
    this.threadFactory = threadFactory;
  }
  
  public void execute(Runnable command)
  {
    threadFactory.newThread(command).start();
  }
}
