package ch.qos.logback.core.util;

import ch.qos.logback.core.CoreConstants;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;





















public class ExecutorServiceUtil
{
  private static final ThreadFactory THREAD_FACTORY = new ThreadFactory()
  {
    private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    
    public Thread newThread(Runnable r) {
      Thread thread = defaultFactory.newThread(r);
      if (!thread.isDaemon()) {
        thread.setDaemon(true);
      }
      thread.setName("logback-" + threadNumber.getAndIncrement());
      return thread;
    }
  };
  

  public ExecutorServiceUtil() {}
  
  public static ExecutorService newExecutorService()
  {
    return new ThreadPoolExecutor(CoreConstants.CORE_POOL_SIZE, 32, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue(), THREAD_FACTORY);
  }
  








  public static void shutdown(ExecutorService executorService)
  {
    executorService.shutdownNow();
  }
}
