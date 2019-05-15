package io.netty.util.internal.shaded.org.jctools.queues;




























public class SpscLinkedQueue<E>
  extends BaseLinkedQueue<E>
{
  public SpscLinkedQueue()
  {
    spProducerNode(new LinkedQueueNode());
    spConsumerNode(producerNode);
    consumerNode.soNext(null);
  }
  















  public boolean offer(E e)
  {
    if (null == e) {
      throw new NullPointerException();
    }
    LinkedQueueNode<E> nextNode = new LinkedQueueNode(e);
    LinkedQueueNode<E> producerNode = lpProducerNode();
    producerNode.soNext(nextNode);
    spProducerNode(nextNode);
    return true;
  }
  















  public E poll()
  {
    return relaxedPoll();
  }
  
  public E peek()
  {
    return relaxedPeek();
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s)
  {
    long result = 0L;
    do {
      fill(s, 4096);
      result += 4096L;
    } while (result <= 2147479551L);
    return (int)result;
  }
  
  public int fill(MessagePassingQueue.Supplier<E> s, int limit)
  {
    if (limit == 0) return 0;
    LinkedQueueNode<E> tail = new LinkedQueueNode(s.get());
    LinkedQueueNode<E> head = tail;
    for (int i = 1; i < limit; i++) {
      LinkedQueueNode<E> temp = new LinkedQueueNode(s.get());
      tail.soNext(temp);
      tail = temp;
    }
    LinkedQueueNode<E> oldPNode = lpProducerNode();
    oldPNode.soNext(head);
    spProducerNode(tail);
    return limit;
  }
  

  public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit)
  {
    LinkedQueueNode<E> chaserNode = producerNode;
    while (exit.keepRunning()) {
      for (int i = 0; i < 4096; i++) {
        LinkedQueueNode<E> nextNode = new LinkedQueueNode(s.get());
        chaserNode.soNext(nextNode);
        chaserNode = nextNode;
        producerNode = chaserNode;
      }
    }
  }
}
