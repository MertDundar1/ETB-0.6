package ch.qos.logback.classic.net;

import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;






















public class SSLSocketReceiver
  extends SocketReceiver
  implements SSLComponent
{
  private SSLConfiguration ssl;
  private SocketFactory socketFactory;
  
  public SSLSocketReceiver() {}
  
  protected SocketFactory getSocketFactory()
  {
    return socketFactory;
  }
  


  protected boolean shouldStart()
  {
    try
    {
      SSLContext sslContext = getSsl().createContext(this);
      SSLParametersConfiguration parameters = getSsl().getParameters();
      parameters.setContext(getContext());
      socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
      
      return super.shouldStart();
    }
    catch (Exception ex) {
      addError(ex.getMessage(), ex); }
    return false;
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
