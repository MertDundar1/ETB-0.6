package ch.qos.logback.core.net.server;

import java.io.Closeable;

public abstract interface Client
  extends Runnable, Closeable
{
  public abstract void close();
}
