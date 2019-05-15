package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import ch.qos.logback.core.util.CloseUtil;
import ch.qos.logback.core.util.Duration;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
















































public abstract class AbstractSocketAppender<E>
  extends AppenderBase<E>
  implements SocketConnector.ExceptionHandler
{
  public static final int DEFAULT_PORT = 4560;
  public static final int DEFAULT_RECONNECTION_DELAY = 30000;
  public static final int DEFAULT_QUEUE_SIZE = 128;
  private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
  private static final int DEFAULT_EVENT_DELAY_TIMEOUT = 100;
  private final ObjectWriterFactory objectWriterFactory;
  private final QueueFactory queueFactory;
  private String remoteHost;
  private int port = 4560;
  private InetAddress address;
  private Duration reconnectionDelay = new Duration(30000L);
  private int queueSize = 128;
  private int acceptConnectionTimeout = 5000;
  private Duration eventDelayLimit = new Duration(100L);
  
  private BlockingDeque<E> deque;
  
  private String peerId;
  
  private SocketConnector connector;
  
  private Future<?> task;
  private volatile Socket socket;
  
  protected AbstractSocketAppender()
  {
    this(new QueueFactory(), new ObjectWriterFactory());
  }
  


  AbstractSocketAppender(QueueFactory queueFactory, ObjectWriterFactory objectWriterFactory)
  {
    this.objectWriterFactory = objectWriterFactory;
    this.queueFactory = queueFactory;
  }
  


  public void start()
  {
    if (isStarted()) return;
    int errorCount = 0;
    if (port <= 0) {
      errorCount++;
      addError("No port was configured for appender" + name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
    }
    


    if (remoteHost == null) {
      errorCount++;
      addError("No remote host was configured for appender" + name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
    }
    


    if (queueSize == 0) {
      addWarn("Queue size of zero is deprecated, use a size of one to indicate synchronous processing");
    }
    
    if (queueSize < 0) {
      errorCount++;
      addError("Queue size must be greater than zero");
    }
    
    if (errorCount == 0) {
      try {
        address = InetAddress.getByName(remoteHost);
      } catch (UnknownHostException ex) {
        addError("unknown host: " + remoteHost);
        errorCount++;
      }
    }
    
    if (errorCount == 0) {
      deque = queueFactory.newLinkedBlockingDeque(queueSize);
      peerId = ("remote peer " + remoteHost + ":" + port + ": ");
      connector = createConnector(address, port, 0, reconnectionDelay.getMilliseconds());
      task = getContext().getExecutorService().submit(new Runnable()
      {
        public void run() {
          AbstractSocketAppender.this.connectSocketAndDispatchEvents();
        }
      });
      super.start();
    }
  }
  



  public void stop()
  {
    if (!isStarted()) return;
    CloseUtil.closeQuietly(socket);
    task.cancel(true);
    super.stop();
  }
  



  protected void append(E event)
  {
    if ((event == null) || (!isStarted())) return;
    try
    {
      boolean inserted = deque.offer(event, eventDelayLimit.getMilliseconds(), TimeUnit.MILLISECONDS);
      if (!inserted) {
        addInfo("Dropping event due to timeout limit of [" + eventDelayLimit + "] being exceeded");
      }
    } catch (InterruptedException e) {
      addError("Interrupted while appending event to SocketAppender", e);
    }
  }
  
  private void connectSocketAndDispatchEvents() {
    try {
      while (socketConnectionCouldBeEstablished()) {
        try {
          ObjectWriter objectWriter = createObjectWriterForSocket();
          addInfo(peerId + "connection established");
          dispatchEvents(objectWriter);
        } catch (IOException ex) {
          addInfo(peerId + "connection failed: " + ex);
        } finally {
          CloseUtil.closeQuietly(socket);
          socket = null;
          addInfo(peerId + "connection closed");
        }
      }
    }
    catch (InterruptedException ex) {}
    
    addInfo("shutting down");
  }
  
  private boolean socketConnectionCouldBeEstablished() throws InterruptedException {
    return (this.socket = connector.call()) != null;
  }
  
  private ObjectWriter createObjectWriterForSocket() throws IOException {
    socket.setSoTimeout(acceptConnectionTimeout);
    ObjectWriter objectWriter = objectWriterFactory.newAutoFlushingObjectWriter(socket.getOutputStream());
    socket.setSoTimeout(0);
    return objectWriter;
  }
  
  private SocketConnector createConnector(InetAddress address, int port, int initialDelay, long retryDelay) {
    SocketConnector connector = newConnector(address, port, initialDelay, retryDelay);
    connector.setExceptionHandler(this);
    connector.setSocketFactory(getSocketFactory());
    return connector;
  }
  
  private void dispatchEvents(ObjectWriter objectWriter) throws InterruptedException, IOException {
    for (;;) {
      E event = deque.takeFirst();
      postProcessEvent(event);
      Serializable serializableEvent = getPST().transform(event);
      try {
        objectWriter.write(serializableEvent);
      } catch (IOException e) {
        tryReAddingEventToFrontOfQueue(event);
        throw e;
      }
    }
  }
  
  private void tryReAddingEventToFrontOfQueue(E event) {
    boolean wasInserted = deque.offerFirst(event);
    if (!wasInserted) {
      addInfo("Dropping event due to socket connection error and maxed out deque capacity");
    }
  }
  


  public void connectionFailed(SocketConnector connector, Exception ex)
  {
    if ((ex instanceof InterruptedException)) {
      addInfo("connector interrupted");
    } else if ((ex instanceof ConnectException)) {
      addInfo(peerId + "connection refused");
    } else {
      addInfo(peerId + ex);
    }
  }
  












  protected SocketConnector newConnector(InetAddress address, int port, long initialDelay, long retryDelay)
  {
    return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
  }
  




  protected SocketFactory getSocketFactory()
  {
    return SocketFactory.getDefault();
  }
  





  protected abstract void postProcessEvent(E paramE);
  




  protected abstract PreSerializationTransformer<E> getPST();
  




  public void setRemoteHost(String host)
  {
    remoteHost = host;
  }
  


  public String getRemoteHost()
  {
    return remoteHost;
  }
  



  public void setPort(int port)
  {
    this.port = port;
  }
  


  public int getPort()
  {
    return port;
  }
  







  public void setReconnectionDelay(Duration delay)
  {
    reconnectionDelay = delay;
  }
  


  public Duration getReconnectionDelay()
  {
    return reconnectionDelay;
  }
  











  public void setQueueSize(int queueSize)
  {
    this.queueSize = queueSize;
  }
  


  public int getQueueSize()
  {
    return queueSize;
  }
  






  public void setEventDelayLimit(Duration eventDelayLimit)
  {
    this.eventDelayLimit = eventDelayLimit;
  }
  


  public Duration getEventDelayLimit()
  {
    return eventDelayLimit;
  }
  








  void setAcceptConnectionTimeout(int acceptConnectionTimeout)
  {
    this.acceptConnectionTimeout = acceptConnectionTimeout;
  }
}
