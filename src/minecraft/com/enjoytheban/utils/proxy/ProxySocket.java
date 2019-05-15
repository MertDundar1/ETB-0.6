package com.enjoytheban.utils.proxy;

import java.net.Socket;

public class ProxySocket
{
  public ProxySocket() {}
  
  public static Socket connectOverProxy(String proxyAdress, int proxyPort, String destAddress, int destPort) throws Exception
  {
    java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, new java.net.InetSocketAddress(proxyAdress, proxyPort));
    Socket returnment = new Socket(proxy);
    returnment.setTcpNoDelay(true);
    returnment.connect(new java.net.InetSocketAddress(destAddress, destPort));
    return returnment;
  }
}
