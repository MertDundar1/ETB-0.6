package io.netty.util.internal;











public abstract class OneTimeTask
  extends MpscLinkedQueueNode<Runnable>
  implements Runnable
{
  public OneTimeTask() {}
  









  public Runnable value()
  {
    return this;
  }
}
