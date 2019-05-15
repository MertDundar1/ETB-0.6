package io.netty.util.internal;

public abstract interface PriorityQueueNode<T>
  extends Comparable<T>
{
  public static final int INDEX_NOT_IN_QUEUE = -1;
  
  public abstract int priorityQueueIndex();
  
  public abstract void priorityQueueIndex(int paramInt);
}
