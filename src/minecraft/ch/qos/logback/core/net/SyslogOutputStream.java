package ch.qos.logback.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;






















public class SyslogOutputStream
  extends OutputStream
{
  private static final int MAX_LEN = 1024;
  private InetAddress address;
  private DatagramSocket ds;
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private final int port;
  
  public SyslogOutputStream(String syslogHost, int port) throws UnknownHostException, SocketException
  {
    address = InetAddress.getByName(syslogHost);
    this.port = port;
    ds = new DatagramSocket();
  }
  
  public void write(byte[] byteArray, int offset, int len) throws IOException {
    baos.write(byteArray, offset, len);
  }
  
  public void flush() throws IOException {
    byte[] bytes = baos.toByteArray();
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
    


    if (baos.size() > 1024) {
      baos = new ByteArrayOutputStream();
    } else {
      baos.reset();
    }
    


    if (bytes.length == 0) {
      return;
    }
    if (ds != null) {
      ds.send(packet);
    }
  }
  
  public void close()
  {
    address = null;
    ds = null;
  }
  
  public int getPort() {
    return port;
  }
  
  public void write(int b) throws IOException
  {
    baos.write(b);
  }
  
  int getSendBufferSize() throws SocketException {
    return ds.getSendBufferSize();
  }
}
