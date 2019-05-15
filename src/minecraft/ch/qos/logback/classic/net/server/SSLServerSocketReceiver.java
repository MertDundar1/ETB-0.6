package ch.qos.logback.classic.net.server;

import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;



















public class SSLServerSocketReceiver
  extends ServerSocketReceiver
  implements SSLComponent
{
  private SSLConfiguration ssl;
  private ServerSocketFactory socketFactory;
  
  public SSLServerSocketReceiver() {}
  
  protected ServerSocketFactory getServerSocketFactory()
    throws Exception
  {
    if (socketFactory == null) {
      SSLContext sslContext = getSsl().createContext(this);
      SSLParametersConfiguration parameters = getSsl().getParameters();
      parameters.setContext(getContext());
      socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
    }
    
    return socketFactory;
  }
  




  public SSLConfiguration getSsl()
  {
    if (ssl == null) {
      ssl = new SSLConfiguration();
    }
    return ssl;
  }
  



  public void setSsl(SSLConfiguration ssl)
  {
    this.ssl = ssl;
  }
}
