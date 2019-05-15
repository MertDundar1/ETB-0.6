package io.netty.channel.unix;

import java.io.File;
import java.net.SocketAddress;

















public final class DomainSocketAddress
  extends SocketAddress
{
  private static final long serialVersionUID = -6934618000832236893L;
  private final String socketPath;
  
  public DomainSocketAddress(String socketPath)
  {
    if (socketPath == null) {
      throw new NullPointerException("socketPath");
    }
    this.socketPath = socketPath;
  }
  
  public DomainSocketAddress(File file) {
    this(file.getPath());
  }
  


  public String path()
  {
    return socketPath;
  }
  
  public String toString()
  {
    return path();
  }
  
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DomainSocketAddress)) {
      return false;
    }
    
    return socketPath.equals(socketPath);
  }
  
  public int hashCode()
  {
    return socketPath.hashCode();
  }
}
