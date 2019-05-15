package ch.qos.logback.core.recovery;

import ch.qos.logback.core.net.SyslogOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;














public class ResilientSyslogOutputStream
  extends ResilientOutputStreamBase
{
  String syslogHost;
  int port;
  
  public ResilientSyslogOutputStream(String syslogHost, int port)
    throws UnknownHostException, SocketException
  {
    this.syslogHost = syslogHost;
    this.port = port;
    os = new SyslogOutputStream(syslogHost, port);
    presumedClean = true;
  }
  
  String getDescription()
  {
    return "syslog [" + syslogHost + ":" + port + "]";
  }
  
  OutputStream openNewOutputStream() throws IOException
  {
    return new SyslogOutputStream(syslogHost, port);
  }
  
  public String toString()
  {
    return "c.q.l.c.recovery.ResilientSyslogOutputStream@" + System.identityHashCode(this);
  }
}
