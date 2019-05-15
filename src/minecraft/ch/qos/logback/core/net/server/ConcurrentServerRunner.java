package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;




































public abstract class ConcurrentServerRunner<T extends Client>
  extends ContextAwareBase
  implements Runnable, ServerRunner<T>
{
  private final Lock clientsLock = new ReentrantLock();
  
  private final Collection<T> clients = new ArrayList();
  


  private final ServerListener<T> listener;
  


  private final Executor executor;
  

  private boolean running;
  


  public ConcurrentServerRunner(ServerListener<T> listener, Executor executor)
  {
    this.listener = listener;
    this.executor = executor;
  }
  


  public boolean isRunning()
  {
    return running;
  }
  
  protected void setRunning(boolean running) {
    this.running = running;
  }
  

  public void stop()
    throws IOException
  {
    listener.close();
    accept(new ClientVisitor() {
      public void visit(T client) {
        client.close();
      }
    });
  }
  


  public void accept(ClientVisitor<T> visitor)
  {
    Collection<T> clients = copyClients();
    for (T client : clients) {
      try {
        visitor.visit(client);
      }
      catch (RuntimeException ex) {
        addError(client + ": " + ex);
      }
    }
  }
  




  private Collection<T> copyClients()
  {
    clientsLock.lock();
    try {
      Collection<T> copy = new ArrayList(clients);
      return copy;
    }
    finally {
      clientsLock.unlock();
    }
  }
  


  public void run()
  {
    setRunning(true);
    try {
      addInfo("listening on " + listener);
      while (!Thread.currentThread().isInterrupted()) {
        T client = listener.acceptClient();
        if (!configureClient(client)) {
          addError(client + ": connection dropped");
          client.close();
        }
        else {
          try {
            executor.execute(new ClientWrapper(client));
          }
          catch (RejectedExecutionException ex) {
            addError(client + ": connection dropped");
            client.close();
          }
          
        }
      }
    }
    catch (InterruptedException ex) {}catch (Exception ex)
    {
      addError("listener: " + ex);
    }
    
    setRunning(false);
    addInfo("shutting down");
    listener.close();
  }
  







  protected abstract boolean configureClient(T paramT);
  






  private void addClient(T client)
  {
    clientsLock.lock();
    try {
      clients.add(client);
    }
    finally {
      clientsLock.unlock();
    }
  }
  



  private void removeClient(T client)
  {
    clientsLock.lock();
    try {
      clients.remove(client);
    }
    finally {
      clientsLock.unlock();
    }
  }
  

  private class ClientWrapper
    implements Client
  {
    private final T delegate;
    

    public ClientWrapper()
    {
      delegate = client;
    }
    
    public void run() {
      ConcurrentServerRunner.this.addClient(delegate);
      try {
        delegate.run();
      }
      finally {
        ConcurrentServerRunner.this.removeClient(delegate);
      }
    }
    
    public void close() {
      delegate.close();
    }
  }
}
