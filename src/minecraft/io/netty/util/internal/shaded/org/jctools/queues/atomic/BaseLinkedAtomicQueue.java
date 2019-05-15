package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;












abstract class BaseLinkedAtomicQueue<E>
  extends AbstractQueue<E>
{
  private final AtomicReference<LinkedQueueAtomicNode<E>> producerNode;
  private final AtomicReference<LinkedQueueAtomicNode<E>> consumerNode;
  
  public BaseLinkedAtomicQueue()
  {
    producerNode = new AtomicReference();
    consumerNode = new AtomicReference();
  }
  
  protected final LinkedQueueAtomicNode<E> lvProducerNode() { return (LinkedQueueAtomicNode)producerNode.get(); }
  
  protected final LinkedQueueAtomicNode<E> lpProducerNode() {
    return (LinkedQueueAtomicNode)producerNode.get();
  }
  
  protected final void spProducerNode(LinkedQueueAtomicNode<E> node) { producerNode.lazySet(node); }
  
  protected final LinkedQueueAtomicNode<E> xchgProducerNode(LinkedQueueAtomicNode<E> node) {
    return (LinkedQueueAtomicNode)producerNode.getAndSet(node);
  }
  
  protected final LinkedQueueAtomicNode<E> lvConsumerNode() { return (LinkedQueueAtomicNode)consumerNode.get(); }
  
  protected final LinkedQueueAtomicNode<E> lpConsumerNode()
  {
    return (LinkedQueueAtomicNode)consumerNode.get();
  }
  
  protected final void spConsumerNode(LinkedQueueAtomicNode<E> node) { consumerNode.lazySet(node); }
  
  public final Iterator<E> iterator()
  {
    throw new UnsupportedOperationException();
  }
  










  public final int size()
  {
    LinkedQueueAtomicNode<E> chaserNode = lvConsumerNode();
    LinkedQueueAtomicNode<E> producerNode = lvProducerNode();
    int size = 0;
    
    while ((chaserNode != producerNode) && (chaserNode != null) && (size < Integer.MAX_VALUE))
    {
      LinkedQueueAtomicNode<E> next = chaserNode.lvNext();
      
      if (next == chaserNode) {
        next = lvConsumerNode();
      }
      chaserNode = next;
      size++;
    }
    return size;
  }
  









  public final boolean isEmpty()
  {
    return lvConsumerNode() == lvProducerNode();
  }
  
  protected E getSingleConsumerNodeValue(LinkedQueueAtomicNode<E> currConsumerNode, LinkedQueueAtomicNode<E> nextNode)
  {
    E nextValue = nextNode.getAndNullValue();
    
    currConsumerNode.soNext(currConsumerNode);
    spConsumerNode(nextNode);
    return nextValue;
  }
}
