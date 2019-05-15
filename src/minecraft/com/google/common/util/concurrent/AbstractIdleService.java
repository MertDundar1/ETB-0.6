package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;




























@Beta
public abstract class AbstractIdleService
  implements Service
{
  private final Supplier<String> threadNameSupplier = new Supplier() {
    public String get() {
      return serviceName() + " " + state();
    }
  };
  

  private final Service delegate = new AbstractService() {
    protected final void doStart() {
      MoreExecutors.renamingDecorator(executor(), threadNameSupplier).execute(new Runnable()
      {
        public void run() {
          try {
            startUp();
            notifyStarted();
          } catch (Throwable t) {
            notifyFailed(t);
            throw Throwables.propagate(t);
          }
        }
      });
    }
    
    protected final void doStop() {
      MoreExecutors.renamingDecorator(executor(), threadNameSupplier).execute(new Runnable()
      {
        public void run() {
          try {
            shutDown();
            notifyStopped();
          } catch (Throwable t) {
            notifyFailed(t);
            throw Throwables.propagate(t);
          }
        }
      });
    }
  };
  


  protected AbstractIdleService() {}
  


  protected abstract void startUp()
    throws Exception;
  


  protected abstract void shutDown()
    throws Exception;
  


  protected Executor executor()
  {
    new Executor() {
      public void execute(Runnable command) {
        MoreExecutors.newThread((String)threadNameSupplier.get(), command).start();
      }
    };
  }
  
  public String toString() {
    return serviceName() + " [" + state() + "]";
  }
  
  public final boolean isRunning() {
    return delegate.isRunning();
  }
  
  public final Service.State state() {
    return delegate.state();
  }
  


  public final void addListener(Service.Listener listener, Executor executor)
  {
    delegate.addListener(listener, executor);
  }
  


  public final Throwable failureCause()
  {
    return delegate.failureCause();
  }
  


  public final Service startAsync()
  {
    delegate.startAsync();
    return this;
  }
  


  public final Service stopAsync()
  {
    delegate.stopAsync();
    return this;
  }
  


  public final void awaitRunning()
  {
    delegate.awaitRunning();
  }
  

  public final void awaitRunning(long timeout, TimeUnit unit)
    throws TimeoutException
  {
    delegate.awaitRunning(timeout, unit);
  }
  


  public final void awaitTerminated()
  {
    delegate.awaitTerminated();
  }
  

  public final void awaitTerminated(long timeout, TimeUnit unit)
    throws TimeoutException
  {
    delegate.awaitTerminated(timeout, unit);
  }
  





  protected String serviceName()
  {
    return getClass().getSimpleName();
  }
}
