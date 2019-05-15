package ch.qos.logback.core.read;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.helpers.CyclicBuffer;

















public class CyclicBufferAppender<E>
  extends AppenderBase<E>
{
  CyclicBuffer<E> cb;
  
  public CyclicBufferAppender() {}
  
  int maxSize = 512;
  
  public void start() {
    cb = new CyclicBuffer(maxSize);
    super.start();
  }
  
  public void stop() {
    cb = null;
    super.stop();
  }
  
  protected void append(E eventObject)
  {
    if (!isStarted()) {
      return;
    }
    cb.add(eventObject);
  }
  
  public int getLength() {
    if (isStarted()) {
      return cb.length();
    }
    return 0;
  }
  
  public E get(int i)
  {
    if (isStarted()) {
      return cb.get(i);
    }
    return null;
  }
  
  public void reset()
  {
    cb.clear();
  }
  


  public int getMaxSize()
  {
    return maxSize;
  }
  
  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }
}
