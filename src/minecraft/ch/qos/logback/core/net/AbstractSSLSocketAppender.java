package ch.qos.logback.core.net;

import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;





























public abstract class AbstractSSLSocketAppender<E>
  extends AbstractSocketAppender<E>
  implements SSLComponent
{
  private SSLConfiguration ssl;
  private SocketFactory socketFactory;
  
  protected AbstractSSLSocketAppender() {}
  
  protected SocketFactory getSocketFactory()
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
      socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
      
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
