package io.netty.util.internal.chmv8;




















public class ForkJoinWorkerThread
  extends Thread
{
  final ForkJoinPool pool;
  

















  final ForkJoinPool.WorkQueue workQueue;
  


















  protected ForkJoinWorkerThread(ForkJoinPool pool)
  {
    super("aForkJoinWorkerThread");
    this.pool = pool;
    workQueue = pool.registerWorker(this);
  }
  




  public ForkJoinPool getPool()
  {
    return pool;
  }
  









  public int getPoolIndex()
  {
    return workQueue.poolIndex >>> 1;
  }
  








  protected void onStart() {}
  








  protected void onTermination(Throwable exception) {}
  







  public void run()
  {
    Throwable exception = null;
    try {
      onStart();
      pool.runWorker(workQueue);
    } catch (Throwable ex) {
      exception = ex;
    } finally {
      try {
        onTermination(exception);
      } catch (Throwable ex) {
        if (exception == null)
          exception = ex;
      } finally {
        pool.deregisterWorker(this, exception);
      }
    }
  }
}
