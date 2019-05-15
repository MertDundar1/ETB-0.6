package ch.qos.logback.core.net.server;

import java.io.Closeable;
import java.io.IOException;

public abstract interface ServerListener<T extends Client>
  extends Closeable
{
  public abstract T acceptClient()
    throws IOException, InterruptedException;
  
  public abstract void close();
}
