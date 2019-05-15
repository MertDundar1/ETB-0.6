package io.netty.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;





















public final class DefaultEventExecutorChooserFactory
  implements EventExecutorChooserFactory
{
  public static final DefaultEventExecutorChooserFactory INSTANCE = new DefaultEventExecutorChooserFactory();
  

  private DefaultEventExecutorChooserFactory() {}
  
  public EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] executors)
  {
    if (isPowerOfTwo(executors.length)) {
      return new PowerOfTowEventExecutorChooser(executors);
    }
    return new GenericEventExecutorChooser(executors);
  }
  
  private static boolean isPowerOfTwo(int val)
  {
    return (val & -val) == val;
  }
  
  private static final class PowerOfTowEventExecutorChooser implements EventExecutorChooserFactory.EventExecutorChooser {
    private final AtomicInteger idx = new AtomicInteger();
    private final EventExecutor[] executors;
    
    PowerOfTowEventExecutorChooser(EventExecutor[] executors) {
      this.executors = executors;
    }
    
    public EventExecutor next()
    {
      return executors[(idx.getAndIncrement() & executors.length - 1)];
    }
  }
  
  private static final class GenericEventExecutorChooser implements EventExecutorChooserFactory.EventExecutorChooser {
    private final AtomicInteger idx = new AtomicInteger();
    private final EventExecutor[] executors;
    
    GenericEventExecutorChooser(EventExecutor[] executors) {
      this.executors = executors;
    }
    
    public EventExecutor next()
    {
      return executors[Math.abs(idx.getAndIncrement() % executors.length)];
    }
  }
}
