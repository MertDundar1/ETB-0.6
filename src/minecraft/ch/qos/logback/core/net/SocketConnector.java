package ch.qos.logback.core.net;

import java.net.Socket;
import java.util.concurrent.Callable;
import javax.net.SocketFactory;

public abstract interface SocketConnector
  extends Callable<Socket>
{
  public abstract Socket call()
    throws InterruptedException;
  
  public abstract void setExceptionHandler(ExceptionHandler paramExceptionHandler);
  
  public abstract void setSocketFactory(SocketFactory paramSocketFactory);
  
  public static abstract interface ExceptionHandler
  {
    public abstract void connectionFailed(SocketConnector paramSocketConnector, Exception paramException);
  }
}
