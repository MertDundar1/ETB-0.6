package ch.qos.logback.classic.net;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.net.ServerSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


































public class SimpleSocketServer
  extends Thread
{
  Logger logger = LoggerFactory.getLogger(SimpleSocketServer.class);
  
  private final int port;
  private final LoggerContext lc;
  private boolean closed = false;
  private ServerSocket serverSocket;
  private List<SocketNode> socketNodeList = new ArrayList();
  private CountDownLatch latch;
  
  public static void main(String[] argv)
    throws Exception
  {
    doMain(SimpleSocketServer.class, argv);
  }
  
  protected static void doMain(Class<? extends SimpleSocketServer> serverClass, String[] argv) throws Exception
  {
    int port = -1;
    if (argv.length == 2) {
      port = parsePortNumber(argv[0]);
    } else {
      usage("Wrong number of arguments.");
    }
    
    String configFile = argv[1];
    LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
    configureLC(lc, configFile);
    
    SimpleSocketServer sss = new SimpleSocketServer(lc, port);
    sss.start();
  }
  
  public SimpleSocketServer(LoggerContext lc, int port) {
    this.lc = lc;
    this.port = port;
  }
  

  public void run()
  {
    String oldThreadName = Thread.currentThread().getName();
    
    try
    {
      String newThreadName = getServerThreadName();
      Thread.currentThread().setName(newThreadName);
      
      logger.info("Listening on port " + port);
      serverSocket = getServerSocketFactory().createServerSocket(port);
      while (!closed) {
        logger.info("Waiting to accept a new client.");
        signalAlmostReadiness();
        Socket socket = serverSocket.accept();
        logger.info("Connected to client at " + socket.getInetAddress());
        logger.info("Starting new socket node.");
        SocketNode newSocketNode = new SocketNode(this, socket, lc);
        synchronized (socketNodeList) {
          socketNodeList.add(newSocketNode);
        }
        String clientThreadName = getClientThreadName(socket);
        new Thread(newSocketNode, clientThreadName).start();
      }
    } catch (Exception e) {
      if (closed) {
        logger.info("Exception in run method for a closed server. This is normal.");
      } else {
        logger.error("Unexpected failure in run method", e);
      }
    }
    finally
    {
      Thread.currentThread().setName(oldThreadName);
    }
  }
  


  protected String getServerThreadName()
  {
    return String.format("Logback %s (port %d)", new Object[] { getClass().getSimpleName(), Integer.valueOf(port) });
  }
  


  protected String getClientThreadName(Socket socket)
  {
    return String.format("Logback SocketNode (client: %s)", new Object[] { socket.getRemoteSocketAddress() });
  }
  




  protected ServerSocketFactory getServerSocketFactory()
  {
    return ServerSocketFactory.getDefault();
  }
  



  void signalAlmostReadiness()
  {
    if ((latch != null) && (latch.getCount() != 0L))
    {
      latch.countDown();
    }
  }
  



  void setLatch(CountDownLatch latch)
  {
    this.latch = latch;
  }
  

  public CountDownLatch getLatch()
  {
    return latch;
  }
  
  public boolean isClosed() { return closed; }
  
  public void close()
  {
    closed = true;
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        logger.error("Failed to close serverSocket", e);
      } finally {
        serverSocket = null;
      }
    }
    
    logger.info("closing this server");
    synchronized (socketNodeList) {
      for (SocketNode sn : socketNodeList) {
        sn.close();
      }
    }
    if (socketNodeList.size() != 0) {
      logger.warn("Was expecting a 0-sized socketNodeList after server shutdown");
    }
  }
  
  public void socketNodeClosing(SocketNode sn)
  {
    logger.debug("Removing {}", sn);
    



    synchronized (socketNodeList) {
      socketNodeList.remove(sn);
    }
  }
  
  static void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
    
    System.exit(1);
  }
  
  static int parsePortNumber(String portStr) {
    try {
      return Integer.parseInt(portStr);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number [" + portStr + "].");
    }
    return -1;
  }
  
  public static void configureLC(LoggerContext lc, String configFile)
    throws JoranException
  {
    JoranConfigurator configurator = new JoranConfigurator();
    lc.reset();
    configurator.setContext(lc);
    configurator.doConfigure(configFile);
  }
}
