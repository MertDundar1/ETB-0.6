package io.netty.util.internal.shaded.org.jctools.queues;









abstract class MpscChunkedArrayQueueColdProducerFields<E>
  extends MpscChunkedArrayQueuePad2<E>
{
  protected long maxQueueCapacity;
  







  protected long producerMask;
  







  protected E[] producerBuffer;
  






  protected volatile long producerLimit;
  






  protected boolean isFixedChunkSize = false;
  
  MpscChunkedArrayQueueColdProducerFields() {}
}
