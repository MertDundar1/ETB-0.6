package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.CloseUtil;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;


























class RemoteReceiverStreamClient
  extends ContextAwareBase
  implements RemoteReceiverClient
{
  private final String clientId;
  private final Socket socket;
  private final OutputStream outputStream;
  private BlockingQueue<Serializable> queue;
  
  public RemoteReceiverStreamClient(String id, Socket socket)
  {
    clientId = ("client " + id + ": ");
    this.socket = socket;
    outputStream = null;
  }
  








  RemoteReceiverStreamClient(String id, OutputStream outputStream)
  {
    clientId = ("client " + id + ": ");
    socket = null;
    this.outputStream = outputStream;
  }
  


  public void setQueue(BlockingQueue<Serializable> queue)
  {
    this.queue = queue;
  }
  


  public boolean offer(Serializable event)
  {
    if (queue == null) {
      throw new IllegalStateException("client has no event queue");
    }
    return queue.offer(event);
  }
  


  public void close()
  {
    if (socket == null) return;
    CloseUtil.closeQuietly(socket);
  }
  


  public void run()
  {
    addInfo(clientId + "connected");
    
    ObjectOutputStream oos = null;
    try {
      int counter = 0;
      oos = createObjectOutputStream();
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Serializable event = (Serializable)queue.take();
          oos.writeObject(event);
          oos.flush();
          counter++; if (counter >= 70)
          {

            counter = 0;
            oos.reset();
          }
        }
        catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
    }
    catch (SocketException ex) {
      addInfo(clientId + ex);
    }
    catch (IOException ex) {
      addError(clientId + ex);
    }
    catch (RuntimeException ex) {
      addError(clientId + ex);
    }
    finally {
      if (oos != null) {
        CloseUtil.closeQuietly(oos);
      }
      close();
      addInfo(clientId + "connection closed");
    }
  }
  
  private ObjectOutputStream createObjectOutputStream() throws IOException {
    if (socket == null) {
      return new ObjectOutputStream(outputStream);
    }
    return new ObjectOutputStream(socket.getOutputStream());
  }
}
