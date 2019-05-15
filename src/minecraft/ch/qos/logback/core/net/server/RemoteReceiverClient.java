package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAware;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

abstract interface RemoteReceiverClient
  extends Client, ContextAware
{
  public abstract void setQueue(BlockingQueue<Serializable> paramBlockingQueue);
  
  public abstract boolean offer(Serializable paramSerializable);
}
