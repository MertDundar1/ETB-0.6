package io.netty.util.internal.shaded.org.jctools.queues.atomic;




























public final class SpscLinkedAtomicQueue<E>
  extends BaseLinkedAtomicQueue<E>
{
  public SpscLinkedAtomicQueue()
  {
    LinkedQueueAtomicNode<E> node = new LinkedQueueAtomicNode();
    spProducerNode(node);
    spConsumerNode(node);
    node.soNext(null);
  }
  















  public boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    LinkedQueueAtomicNode<E> nextNode = new LinkedQueueAtomicNode(e);
    lpProducerNode().soNext(nextNode);
    spProducerNode(nextNode);
    return true;
  }
  














  public E poll()
  {
    LinkedQueueAtomicNode<E> currConsumerNode = lpConsumerNode();
    LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
    if (nextNode != null) {
      return getSingleConsumerNodeValue(currConsumerNode, nextNode);
    }
    return null;
  }
  
  public E peek()
  {
    LinkedQueueAtomicNode<E> nextNode = lpConsumerNode().lvNext();
    if (nextNode != null) {
      return nextNode.lpValue();
    }
    return null;
  }
}
