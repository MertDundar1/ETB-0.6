package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



















public abstract class AbstractScheduledEventExecutor
  extends AbstractEventExecutor
{
  Queue<ScheduledFutureTask<?>> scheduledTaskQueue;
  
  protected AbstractScheduledEventExecutor() {}
  
  protected AbstractScheduledEventExecutor(EventExecutorGroup parent)
  {
    super(parent);
  }
  
  protected static long nanoTime() {
    return ScheduledFutureTask.nanoTime();
  }
  
  Queue<ScheduledFutureTask<?>> scheduledTaskQueue() {
    if (scheduledTaskQueue == null) {
      scheduledTaskQueue = new PriorityQueue();
    }
    return scheduledTaskQueue;
  }
  
  private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue) {
    return (queue == null) || (queue.isEmpty());
  }
  




  protected void cancelScheduledTasks()
  {
    assert (inEventLoop());
    Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
    if (isNullOrEmpty(scheduledTaskQueue)) {
      return;
    }
    
    ScheduledFutureTask<?>[] scheduledTasks = (ScheduledFutureTask[])scheduledTaskQueue.toArray(new ScheduledFutureTask[scheduledTaskQueue.size()]);
    

    for (ScheduledFutureTask<?> task : scheduledTasks) {
      task.cancelWithoutRemove(false);
    }
    
    scheduledTaskQueue.clear();
  }
  


  protected final Runnable pollScheduledTask()
  {
    return pollScheduledTask(nanoTime());
  }
  



  protected final Runnable pollScheduledTask(long nanoTime)
  {
    assert (inEventLoop());
    
    Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
    ScheduledFutureTask<?> scheduledTask = scheduledTaskQueue == null ? null : (ScheduledFutureTask)scheduledTaskQueue.peek();
    if (scheduledTask == null) {
      return null;
    }
    
    if (scheduledTask.deadlineNanos() <= nanoTime) {
      scheduledTaskQueue.remove();
      return scheduledTask;
    }
    return null;
  }
  


  protected final long nextScheduledTaskNano()
  {
    Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
    ScheduledFutureTask<?> scheduledTask = scheduledTaskQueue == null ? null : (ScheduledFutureTask)scheduledTaskQueue.peek();
    if (scheduledTask == null) {
      return -1L;
    }
    return Math.max(0L, scheduledTask.deadlineNanos() - nanoTime());
  }
  
  final ScheduledFutureTask<?> peekScheduledTask() {
    Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
    if (scheduledTaskQueue == null) {
      return null;
    }
    return (ScheduledFutureTask)scheduledTaskQueue.peek();
  }
  


  protected final boolean hasScheduledTasks()
  {
    Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
    ScheduledFutureTask<?> scheduledTask = scheduledTaskQueue == null ? null : (ScheduledFutureTask)scheduledTaskQueue.peek();
    return (scheduledTask != null) && (scheduledTask.deadlineNanos() <= nanoTime());
  }
  
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
  {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(unit, "unit");
    if (delay < 0L) {
      throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", new Object[] { Long.valueOf(delay) }));
    }
    
    return schedule(new ScheduledFutureTask(this, command, null, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
  }
  

  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
  {
    ObjectUtil.checkNotNull(callable, "callable");
    ObjectUtil.checkNotNull(unit, "unit");
    if (delay < 0L) {
      throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", new Object[] { Long.valueOf(delay) }));
    }
    
    return schedule(new ScheduledFutureTask(this, callable, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
  }
  

  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
  {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(unit, "unit");
    if (initialDelay < 0L) {
      throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", new Object[] { Long.valueOf(initialDelay) }));
    }
    
    if (period <= 0L) {
      throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", new Object[] { Long.valueOf(period) }));
    }
    

    return schedule(new ScheduledFutureTask(this, Executors.callable(command, null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
  }
  


  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
  {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(unit, "unit");
    if (initialDelay < 0L) {
      throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", new Object[] { Long.valueOf(initialDelay) }));
    }
    
    if (delay <= 0L) {
      throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", new Object[] { Long.valueOf(delay) }));
    }
    

    return schedule(new ScheduledFutureTask(this, Executors.callable(command, null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
  }
  

  <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> task)
  {
    if (inEventLoop()) {
      scheduledTaskQueue().add(task);
    } else {
      execute(new Runnable()
      {
        public void run() {
          scheduledTaskQueue().add(task);
        }
      });
    }
    
    return task;
  }
  
  final void removeScheduled(final ScheduledFutureTask<?> task) {
    if (inEventLoop()) {
      scheduledTaskQueue().remove(task);
    } else {
      execute(new Runnable()
      {
        public void run() {
          removeScheduled(task);
        }
      });
    }
  }
}
