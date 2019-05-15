package ch.qos.logback.core;

import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
















public class AsyncAppenderBase<E>
  extends UnsynchronizedAppenderBase<E>
  implements AppenderAttachable<E>
{
  AppenderAttachableImpl<E> aai;
  BlockingQueue<E> blockingQueue;
  public static final int DEFAULT_QUEUE_SIZE = 256;
  int queueSize;
  int appenderCount;
  static final int UNDEFINED = -1;
  int discardingThreshold;
  AsyncAppenderBase<E>.Worker worker;
  public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
  int maxFlushTime;
  
  public AsyncAppenderBase()
  {
    aai = new AppenderAttachableImpl();
    





    queueSize = 256;
    
    appenderCount = 0;
    

    discardingThreshold = -1;
    
    worker = new Worker();
    






    maxFlushTime = 1000;
  }
  








  protected boolean isDiscardable(E eventObject)
  {
    return false;
  }
  





  protected void preprocess(E eventObject) {}
  




  public void start()
  {
    if (appenderCount == 0) {
      addError("No attached appenders found.");
      return;
    }
    if (queueSize < 1) {
      addError("Invalid queue size [" + queueSize + "]");
      return;
    }
    blockingQueue = new ArrayBlockingQueue(queueSize);
    
    if (discardingThreshold == -1)
      discardingThreshold = (queueSize / 5);
    addInfo("Setting discardingThreshold to " + discardingThreshold);
    worker.setDaemon(true);
    worker.setName("AsyncAppender-Worker-" + getName());
    
    super.start();
    worker.start();
  }
  
  public void stop()
  {
    if (!isStarted()) {
      return;
    }
    

    super.stop();
    


    worker.interrupt();
    try {
      worker.join(maxFlushTime);
      

      if (worker.isAlive()) {
        addWarn("Max queue flush timeout (" + maxFlushTime + " ms) exceeded. Approximately " + blockingQueue.size() + " queued events were possibly discarded.");
      }
      else {
        addInfo("Queue flush finished successfully within timeout.");
      }
    }
    catch (InterruptedException e) {
      addError("Failed to join worker thread. " + blockingQueue.size() + " queued events may be discarded.", e);
    }
  }
  

  protected void append(E eventObject)
  {
    if ((isQueueBelowDiscardingThreshold()) && (isDiscardable(eventObject))) {
      return;
    }
    preprocess(eventObject);
    put(eventObject);
  }
  
  private boolean isQueueBelowDiscardingThreshold() {
    return blockingQueue.remainingCapacity() < discardingThreshold;
  }
  
  private void put(E eventObject) {
    try {
      blockingQueue.put(eventObject);
    }
    catch (InterruptedException e) {}
  }
  
  public int getQueueSize() {
    return queueSize;
  }
  
  public void setQueueSize(int queueSize) {
    this.queueSize = queueSize;
  }
  
  public int getDiscardingThreshold() {
    return discardingThreshold;
  }
  
  public void setDiscardingThreshold(int discardingThreshold) {
    this.discardingThreshold = discardingThreshold;
  }
  
  public int getMaxFlushTime() {
    return maxFlushTime;
  }
  
  public void setMaxFlushTime(int maxFlushTime) {
    this.maxFlushTime = maxFlushTime;
  }
  




  public int getNumberOfElementsInQueue()
  {
    return blockingQueue.size();
  }
  





  public int getRemainingCapacity()
  {
    return blockingQueue.remainingCapacity();
  }
  

  public void addAppender(Appender<E> newAppender)
  {
    if (appenderCount == 0) {
      appenderCount += 1;
      addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
      aai.addAppender(newAppender);
    } else {
      addWarn("One and only one appender may be attached to AsyncAppender.");
      addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
    }
  }
  
  public Iterator<Appender<E>> iteratorForAppenders() {
    return aai.iteratorForAppenders();
  }
  
  public Appender<E> getAppender(String name) {
    return aai.getAppender(name);
  }
  
  public boolean isAttached(Appender<E> eAppender) {
    return aai.isAttached(eAppender);
  }
  
  public void detachAndStopAllAppenders() {
    aai.detachAndStopAllAppenders();
  }
  
  public boolean detachAppender(Appender<E> eAppender) {
    return aai.detachAppender(eAppender);
  }
  

  public boolean detachAppender(String name) { return aai.detachAppender(name); }
  
  class Worker extends Thread {
    Worker() {}
    
    public void run() {
      AsyncAppenderBase<E> parent = AsyncAppenderBase.this;
      AppenderAttachableImpl<E> aai = aai;
      for (;;)
      {
        if (parent.isStarted()) {
          try {
            E e = blockingQueue.take();
            aai.appendLoopOnAppenders(e);
          }
          catch (InterruptedException ie) {}
        }
      }
      
      addInfo("Worker thread will flush remaining events before exiting. ");
      
      for (E e : blockingQueue) {
        aai.appendLoopOnAppenders(e);
        blockingQueue.remove(e);
      }
      

      aai.detachAndStopAllAppenders();
    }
  }
}
