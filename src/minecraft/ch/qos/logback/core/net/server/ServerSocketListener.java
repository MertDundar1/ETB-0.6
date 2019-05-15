package ch.qos.logback.core.net.server;

import ch.qos.logback.core.util.CloseUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;























public abstract class ServerSocketListener<T extends Client>
  implements ServerListener<T>
{
  private final ServerSocket serverSocket;
  
  public ServerSocketListener(ServerSocket serverSocket)
  {
    this.serverSocket = serverSocket;
  }
  

  public T acceptClient()
    throws IOException
  {
    Socket socket = serverSocket.accept();
    return createClient(socketAddressToString(socket.getRemoteSocketAddress()), socket);
  }
  





  protected abstract T createClient(String paramString, Socket paramSocket)
    throws IOException;
  





  public void close()
  {
    CloseUtil.closeQuietly(serverSocket);
  }
  



  public String toString()
  {
    return socketAddressToString(serverSocket.getLocalSocketAddress());
  }
  




  private String socketAddressToString(SocketAddress address)
  {
    String addr = address.toString();
    int i = addr.indexOf("/");
    if (i >= 0) {
      addr = addr.substring(i + 1);
    }
    return addr;
  }
}
