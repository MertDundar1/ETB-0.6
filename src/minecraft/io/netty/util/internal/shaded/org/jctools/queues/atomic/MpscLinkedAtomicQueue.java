package io.netty.util.internal.shaded.org.jctools.queues.atomic;




























public final class MpscLinkedAtomicQueue<E>
  extends BaseLinkedAtomicQueue<E>
{
  public MpscLinkedAtomicQueue()
  {
    LinkedQueueAtomicNode<E> node = new LinkedQueueAtomicNode();
    spConsumerNode(node);
    xchgProducerNode(node);
  }
  















  public final boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    LinkedQueueAtomicNode<E> nextNode = new LinkedQueueAtomicNode(e);
    LinkedQueueAtomicNode<E> prevProducerNode = xchgProducerNode(nextNode);
    

    prevProducerNode.soNext(nextNode);
    return true;
  }
  
















  public final E poll()
  {
    LinkedQueueAtomicNode<E> currConsumerNode = lpConsumerNode();
    LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
    if (nextNode != null) {
      return getSingleConsumerNodeValue(currConsumerNode, nextNode);
    }
    if (currConsumerNode != lvProducerNode())
    {
      while ((nextNode = currConsumerNode.lvNext()) == null) {}
      

      return getSingleConsumerNodeValue(currConsumerNode, nextNode);
    }
    return null;
  }
  
  public final E peek()
  {
    LinkedQueueAtomicNode<E> currConsumerNode = lpConsumerNode();
    LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
    if (nextNode != null) {
      return nextNode.lpValue();
    }
    if (currConsumerNode != lvProducerNode())
    {
      while ((nextNode = currConsumerNode.lvNext()) == null) {}
      
      return nextNode.lpValue();
    }
    return null;
  }
}
