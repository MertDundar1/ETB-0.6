package ch.qos.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;






























public class SocketNode
  implements Runnable
{
  Socket socket;
  LoggerContext context;
  ObjectInputStream ois;
  SocketAddress remoteSocketAddress;
  Logger logger;
  boolean closed = false;
  SimpleSocketServer socketServer;
  
  public SocketNode(SimpleSocketServer socketServer, Socket socket, LoggerContext context) {
    this.socketServer = socketServer;
    this.socket = socket;
    remoteSocketAddress = socket.getRemoteSocketAddress();
    this.context = context;
    logger = context.getLogger(SocketNode.class);
  }
  





  public void run()
  {
    try
    {
      ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    }
    catch (Exception e) {
      logger.error("Could not open ObjectInputStream to " + socket, e);
      closed = true;
    }
    


    try
    {
      while (!closed)
      {
        ILoggingEvent event = (ILoggingEvent)ois.readObject();
        

        Logger remoteLogger = context.getLogger(event.getLoggerName());
        
        if (remoteLogger.isEnabledFor(event.getLevel()))
        {
          remoteLogger.callAppenders(event);
        }
      }
    } catch (EOFException e) {
      logger.info("Caught java.io.EOFException closing connection.");
    } catch (SocketException e) {
      logger.info("Caught java.net.SocketException closing connection.");
    } catch (IOException e) {
      logger.info("Caught java.io.IOException: " + e);
      logger.info("Closing connection.");
    } catch (Exception e) {
      logger.error("Unexpected exception. Closing connection.", e);
    }
    
    socketServer.socketNodeClosing(this);
    close();
  }
  
  void close() {
    if (closed) {
      return;
    }
    closed = true;
    if (ois != null) {
      try {
        ois.close();
      } catch (IOException e) {
        logger.warn("Could not close connection.", e);
      } finally {
        ois = null;
      }
    }
  }
  
  public String toString()
  {
    return getClass().getName() + remoteSocketAddress.toString();
  }
}
