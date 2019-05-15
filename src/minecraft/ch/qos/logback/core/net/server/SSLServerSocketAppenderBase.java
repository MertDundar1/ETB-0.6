package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;



















public abstract class SSLServerSocketAppenderBase<E>
  extends AbstractServerSocketAppender<E>
  implements SSLComponent
{
  private SSLConfiguration ssl;
  private ServerSocketFactory socketFactory;
  
  public SSLServerSocketAppenderBase() {}
  
  protected ServerSocketFactory getServerSocketFactory()
  {
    return socketFactory;
  }
  


  public void start()
  {
    try
    {
      SSLContext sslContext = getSsl().createContext(this);
      SSLParametersConfiguration parameters = getSsl().getParameters();
      parameters.setContext(getContext());
      socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
      
      super.start();
    }
    catch (Exception ex) {
      addError(ex.getMessage(), ex);
    }
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
