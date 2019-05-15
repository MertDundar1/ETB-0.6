package io.netty.util.internal.shaded.org.jctools.queues;

import java.util.Iterator;












































































abstract class BaseLinkedQueue<E>
  extends BaseLinkedQueueConsumerNodeRef<E>
{
  long p01;
  long p02;
  long p03;
  long p04;
  long p05;
  long p06;
  long p07;
  long p10;
  long p11;
  long p12;
  long p13;
  long p14;
  long p15;
  long p16;
  long p17;
  
  BaseLinkedQueue() {}
  
  public final Iterator<E> iterator()
  {
    throw new UnsupportedOperationException();
  }
  











  public final int size()
  {
    LinkedQueueNode<E> chaserNode = lvConsumerNode();
    LinkedQueueNode<E> producerNode = lvProducerNode();
    int size = 0;
    

    while ((chaserNode != producerNode) && (chaserNode != null) && (size < Integer.MAX_VALUE))
    {
      LinkedQueueNode<E> next = chaserNode.lvNext();
      
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
  
  public int capacity()
  {
    return -1;
  }
  
  protected E getSingleConsumerNodeValue(LinkedQueueNode<E> currConsumerNode, LinkedQueueNode<E> nextNode)
  {
    E nextValue = nextNode.getAndNullValue();
    



    currConsumerNode.soNext(currConsumerNode);
    spConsumerNode(nextNode);
    
    return nextValue;
  }
  
  public E relaxedPoll()
  {
    LinkedQueueNode<E> currConsumerNode = lpConsumerNode();
    LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
    if (nextNode != null) {
      return getSingleConsumerNodeValue(currConsumerNode, nextNode);
    }
    return null;
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c)
  {
    long result = 0L;
    int drained;
    do {
      drained = drain(c, 4096);
      result += drained;
    } while ((drained == 4096) && (result <= 2147479551L));
    return (int)result;
  }
  
  public int drain(MessagePassingQueue.Consumer<E> c, int limit)
  {
    LinkedQueueNode<E> chaserNode = consumerNode;
    for (int i = 0; i < limit; i++) {
      LinkedQueueNode<E> nextNode = chaserNode.lvNext();
      
      if (nextNode == null) {
        return i;
      }
      
      E nextValue = getSingleConsumerNodeValue(chaserNode, nextNode);
      chaserNode = nextNode;
      c.accept(nextValue);
    }
    return limit;
  }
  
  public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit)
  {
    LinkedQueueNode<E> chaserNode = consumerNode;
    int idleCounter = 0;
    while (exit.keepRunning()) {
      for (int i = 0; i < 4096; i++) {
        LinkedQueueNode<E> nextNode = chaserNode.lvNext();
        if (nextNode == null) {
          idleCounter = wait.idle(idleCounter);
        }
        else
        {
          idleCounter = 0;
          
          E nextValue = getSingleConsumerNodeValue(chaserNode, nextNode);
          chaserNode = nextNode;
          c.accept(nextValue);
        }
      }
    }
  }
  
  public E relaxedPeek() {
    LinkedQueueNode<E> currConsumerNode = consumerNode;
    LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
    if (nextNode != null) {
      return nextNode.lpValue();
    }
    return null;
  }
  
  public boolean relaxedOffer(E e)
  {
    return offer(e);
  }
}
