package ch.qos.logback.core.net;

import ch.qos.logback.core.util.DelayStrategy;
import ch.qos.logback.core.util.FixedDelay;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;





























public class DefaultSocketConnector
  implements SocketConnector
{
  private final InetAddress address;
  private final int port;
  private final DelayStrategy delayStrategy;
  private SocketConnector.ExceptionHandler exceptionHandler;
  private SocketFactory socketFactory;
  
  public DefaultSocketConnector(InetAddress address, int port, long initialDelay, long retryDelay)
  {
    this(address, port, new FixedDelay(initialDelay, retryDelay));
  }
  








  public DefaultSocketConnector(InetAddress address, int port, DelayStrategy delayStrategy)
  {
    this.address = address;
    this.port = port;
    this.delayStrategy = delayStrategy;
  }
  

  public Socket call()
    throws InterruptedException
  {
    useDefaultsForMissingFields();
    Socket socket = createSocket();
    while ((socket == null) && (!Thread.currentThread().isInterrupted())) {
      Thread.sleep(delayStrategy.nextDelay());
      socket = createSocket();
    }
    return socket;
  }
  
  private Socket createSocket() {
    Socket newSocket = null;
    try {
      newSocket = socketFactory.createSocket(address, port);
    } catch (IOException ioex) {
      exceptionHandler.connectionFailed(this, ioex);
    }
    return newSocket;
  }
  
  private void useDefaultsForMissingFields() {
    if (exceptionHandler == null) {
      exceptionHandler = new ConsoleExceptionHandler(null);
    }
    if (socketFactory == null) {
      socketFactory = SocketFactory.getDefault();
    }
  }
  


  public void setExceptionHandler(SocketConnector.ExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }
  


  public void setSocketFactory(SocketFactory socketFactory)
  {
    this.socketFactory = socketFactory;
  }
  
  private static class ConsoleExceptionHandler implements SocketConnector.ExceptionHandler
  {
    private ConsoleExceptionHandler() {}
    
    public void connectionFailed(SocketConnector connector, Exception ex)
    {
      System.out.println(ex);
    }
  }
}
