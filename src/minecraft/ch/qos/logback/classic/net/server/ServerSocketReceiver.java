package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.net.ReceiverBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.net.server.ServerListener;
import ch.qos.logback.core.net.server.ServerRunner;
import ch.qos.logback.core.util.CloseUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import javax.net.ServerSocketFactory;
























public class ServerSocketReceiver
  extends ReceiverBase
{
  public static final int DEFAULT_BACKLOG = 50;
  private int port = 4560;
  private int backlog = 50;
  
  private String address;
  private ServerSocket serverSocket;
  private ServerRunner runner;
  
  public ServerSocketReceiver() {}
  
  protected boolean shouldStart()
  {
    try
    {
      ServerSocket serverSocket = getServerSocketFactory().createServerSocket(getPort(), getBacklog(), getInetAddress());
      

      ServerListener<RemoteAppenderClient> listener = createServerListener(serverSocket);
      

      runner = createServerRunner(listener, getContext().getExecutorService());
      runner.setContext(getContext());
      return true;
    }
    catch (Exception ex) {
      addError("server startup error: " + ex, ex);
      CloseUtil.closeQuietly(this.serverSocket); }
    return false;
  }
  

  protected ServerListener<RemoteAppenderClient> createServerListener(ServerSocket socket)
  {
    return new RemoteAppenderServerListener(socket);
  }
  

  protected ServerRunner createServerRunner(ServerListener<RemoteAppenderClient> listener, Executor executor)
  {
    return new RemoteAppenderServerRunner(listener, executor);
  }
  
  protected Runnable getRunnableTask()
  {
    return runner;
  }
  

  protected void onStop()
  {
    try
    {
      if (runner == null) return;
      runner.stop();
    }
    catch (IOException ex) {
      addError("server shutdown error: " + ex, ex);
    }
  }
  





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
}
