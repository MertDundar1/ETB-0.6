package io.netty.util.internal.shaded.org.jctools.queues;

abstract class MpscChunkedArrayQueueProducerFields<E>
  extends MpscChunkedArrayQueuePad1<E>
{
  protected long producerIndex;
  
  MpscChunkedArrayQueueProducerFields() {}
}
