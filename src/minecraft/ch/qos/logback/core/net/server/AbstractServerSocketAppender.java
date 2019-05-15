package ch.qos.logback.core.net.server;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import javax.net.ServerSocketFactory;





























public abstract class AbstractServerSocketAppender<E>
  extends AppenderBase<E>
{
  public static final int DEFAULT_BACKLOG = 50;
  public static final int DEFAULT_CLIENT_QUEUE_SIZE = 100;
  private int port = 4560;
  private int backlog = 50;
  private int clientQueueSize = 100;
  private String address;
  private ServerRunner<RemoteReceiverClient> runner;
  
  public AbstractServerSocketAppender() {}
  
  public void start()
  {
    if (isStarted()) return;
    try {
      ServerSocket socket = getServerSocketFactory().createServerSocket(getPort(), getBacklog(), getInetAddress());
      
      ServerListener<RemoteReceiverClient> listener = createServerListener(socket);
      
      runner = createServerRunner(listener, getContext().getExecutorService());
      runner.setContext(getContext());
      getContext().getExecutorService().execute(runner);
      super.start();
    } catch (Exception ex) {
      addError("server startup error: " + ex, ex);
    }
  }
  
  protected ServerListener<RemoteReceiverClient> createServerListener(ServerSocket socket)
  {
    return new RemoteReceiverServerListener(socket);
  }
  

  protected ServerRunner<RemoteReceiverClient> createServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor)
  {
    return new RemoteReceiverServerRunner(listener, executor, getClientQueueSize());
  }
  

  public void stop()
  {
    if (!isStarted()) return;
    try {
      runner.stop();
      super.stop();
    }
    catch (IOException ex) {
      addError("server shutdown error: " + ex, ex);
    }
  }
  
  protected void append(E event)
  {
    if (event == null) return;
    postProcessEvent(event);
    final Serializable serEvent = getPST().transform(event);
    runner.accept(new ClientVisitor() {
      public void visit(RemoteReceiverClient client) {
        client.offer(serEvent);
      }
    });
  }
  






  protected abstract void postProcessEvent(E paramE);
  





  protected abstract PreSerializationTransformer<E> getPST();
  





  protected ServerSocketFactory getServerSocketFactory()
    throws Exception
  {
    return ServerSocketFactory.getDefault();
  }
  



  protected InetAddress getInetAddress()
    throws UnknownHostException
  {
    if (getAddress() == null) return null;
    return InetAddress.getByName(getAddress());
  }
  



  public int getPort()
  {
    return port;
  }
  



  public void setPort(int port)
  {
    this.port = port;
  }
  







  public int getBacklog()
  {
    return backlog;
  }
  







  public void setBacklog(int backlog)
  {
    this.backlog = backlog;
  }
  



  public String getAddress()
  {
    return address;
  }
  



  public void setAddress(String address)
  {
    this.address = address;
  }
  



  public int getClientQueueSize()
  {
    return clientQueueSize;
  }
  



  public void setClientQueueSize(int clientQueueSize)
  {
    this.clientQueueSize = clientQueueSize;
  }
}
