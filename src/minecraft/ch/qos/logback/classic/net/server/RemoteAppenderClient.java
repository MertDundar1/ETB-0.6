package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.server.Client;

abstract interface RemoteAppenderClient
  extends Client
{
  public abstract void setLoggerContext(LoggerContext paramLoggerContext);
}
