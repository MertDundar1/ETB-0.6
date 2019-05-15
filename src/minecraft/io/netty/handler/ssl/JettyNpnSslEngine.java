package io.netty.handler.ssl;

import java.nio.ByteBuffer;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.npn.NextProtoNego;
import org.eclipse.jetty.npn.NextProtoNego.ClientProvider;
import org.eclipse.jetty.npn.NextProtoNego.ServerProvider;














final class JettyNpnSslEngine
  extends SSLEngine
{
  private static boolean available;
  private final SSLEngine engine;
  private final JettyNpnSslSession session;
  
  static boolean isAvailable()
  {
    updateAvailability();
    return available;
  }
  
  private static void updateAvailability() {
    if (available) {
      return;
    }
    try
    {
      ClassLoader bootloader = ClassLoader.getSystemClassLoader().getParent();
      if (bootloader == null)
      {

        bootloader = ClassLoader.getSystemClassLoader();
      }
      Class.forName("sun.security.ssl.NextProtoNegoExtension", true, bootloader);
      available = true;
    }
    catch (Exception ignore) {}
  }
  



  JettyNpnSslEngine(SSLEngine engine, final List<String> nextProtocols, boolean server)
  {
    assert (!nextProtocols.isEmpty());
    
    this.engine = engine;
    session = new JettyNpnSslSession(engine);
    
    if (server) {
      NextProtoNego.put(engine, new NextProtoNego.ServerProvider()
      {
        public void unsupported() {
          getSession().setApplicationProtocol((String)nextProtocols.get(nextProtocols.size() - 1));
        }
        
        public List<String> protocols()
        {
          return nextProtocols;
        }
        
        public void protocolSelected(String protocol)
        {
          getSession().setApplicationProtocol(protocol);
        }
      });
    } else {
      final String[] list = (String[])nextProtocols.toArray(new String[nextProtocols.size()]);
      final String fallback = list[(list.length - 1)];
      
      NextProtoNego.put(engine, new NextProtoNego.ClientProvider()
      {
        public boolean supports() {
          return true;
        }
        
        public void unsupported()
        {
          session.setApplicationProtocol(null);
        }
        
        public String selectProtocol(List<String> protocols)
        {
          for (String p : list) {
            if (protocols.contains(p)) {
              return p;
            }
          }
          return fallback;
        }
      });
    }
  }
  
  public JettyNpnSslSession getSession()
  {
    return session;
  }
  
  public void closeInbound() throws SSLException
  {
    NextProtoNego.remove(engine);
    engine.closeInbound();
  }
  
  public void closeOutbound()
  {
    NextProtoNego.remove(engine);
    engine.closeOutbound();
  }
  
  public String getPeerHost()
  {
    return engine.getPeerHost();
  }
  
  public int getPeerPort()
  {
    return engine.getPeerPort();
  }
  
  public SSLEngineResult wrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException
  {
    return engine.wrap(byteBuffer, byteBuffer2);
  }
  
  public SSLEngineResult wrap(ByteBuffer[] byteBuffers, ByteBuffer byteBuffer) throws SSLException
  {
    return engine.wrap(byteBuffers, byteBuffer);
  }
  
  public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i2, ByteBuffer byteBuffer) throws SSLException
  {
    return engine.wrap(byteBuffers, i, i2, byteBuffer);
  }
  
  public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException
  {
    return engine.unwrap(byteBuffer, byteBuffer2);
  }
  
  public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers) throws SSLException
  {
    return engine.unwrap(byteBuffer, byteBuffers);
  }
  
  public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i2) throws SSLException
  {
    return engine.unwrap(byteBuffer, byteBuffers, i, i2);
  }
  
  public Runnable getDelegatedTask()
  {
    return engine.getDelegatedTask();
  }
  
  public boolean isInboundDone()
  {
    return engine.isInboundDone();
  }
  
  public boolean isOutboundDone()
  {
    return engine.isOutboundDone();
  }
  
  public String[] getSupportedCipherSuites()
  {
    return engine.getSupportedCipherSuites();
  }
  
  public String[] getEnabledCipherSuites()
  {
    return engine.getEnabledCipherSuites();
  }
  
  public void setEnabledCipherSuites(String[] strings)
  {
    engine.setEnabledCipherSuites(strings);
  }
  
  public String[] getSupportedProtocols()
  {
    return engine.getSupportedProtocols();
  }
  
  public String[] getEnabledProtocols()
  {
    return engine.getEnabledProtocols();
  }
  
  public void setEnabledProtocols(String[] strings)
  {
    engine.setEnabledProtocols(strings);
  }
  
  public SSLSession getHandshakeSession()
  {
    return engine.getHandshakeSession();
  }
  
  public void beginHandshake() throws SSLException
  {
    engine.beginHandshake();
  }
  
  public SSLEngineResult.HandshakeStatus getHandshakeStatus()
  {
    return engine.getHandshakeStatus();
  }
  
  public void setUseClientMode(boolean b)
  {
    engine.setUseClientMode(b);
  }
  
  public boolean getUseClientMode()
  {
    return engine.getUseClientMode();
  }
  
  public void setNeedClientAuth(boolean b)
  {
    engine.setNeedClientAuth(b);
  }
  
  public boolean getNeedClientAuth()
  {
    return engine.getNeedClientAuth();
  }
  
  public void setWantClientAuth(boolean b)
  {
    engine.setWantClientAuth(b);
  }
  
  public boolean getWantClientAuth()
  {
    return engine.getWantClientAuth();
  }
  
  public void setEnableSessionCreation(boolean b)
  {
    engine.setEnableSessionCreation(b);
  }
  
  public boolean getEnableSessionCreation()
  {
    return engine.getEnableSessionCreation();
  }
  
  public SSLParameters getSSLParameters()
  {
    return engine.getSSLParameters();
  }
  
  public void setSSLParameters(SSLParameters sslParameters)
  {
    engine.setSSLParameters(sslParameters);
  }
}
