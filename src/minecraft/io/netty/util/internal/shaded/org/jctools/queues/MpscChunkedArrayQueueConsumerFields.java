package io.netty.util.internal.shaded.org.jctools.queues;

abstract class MpscChunkedArrayQueueConsumerFields<E>
  extends MpscChunkedArrayQueuePad3<E>
{
  protected long consumerMask;
  protected E[] consumerBuffer;
  protected long consumerIndex;
  
  MpscChunkedArrayQueueConsumerFields() {}
}
