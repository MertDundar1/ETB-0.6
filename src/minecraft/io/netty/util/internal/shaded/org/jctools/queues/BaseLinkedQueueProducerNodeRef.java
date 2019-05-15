package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

















abstract class BaseLinkedQueueProducerNodeRef<E>
  extends BaseLinkedQueuePad0<E>
{
  protected static final long P_NODE_OFFSET;
  protected LinkedQueueNode<E> producerNode;
  
  static
  {
    try
    {
      P_NODE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(BaseLinkedQueueProducerNodeRef.class.getDeclaredField("producerNode"));
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  

  protected final void spProducerNode(LinkedQueueNode<E> node)
  {
    producerNode = node;
  }
  
  protected final LinkedQueueNode<E> lvProducerNode()
  {
    return (LinkedQueueNode)UnsafeAccess.UNSAFE.getObjectVolatile(this, P_NODE_OFFSET);
  }
  
  protected final LinkedQueueNode<E> lpProducerNode() {
    return producerNode;
  }
  
  BaseLinkedQueueProducerNodeRef() {}
}
