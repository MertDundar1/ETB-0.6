package ch.qos.logback.core.net.ssl;

import javax.net.ssl.SSLSocket;

















public class SSLConfigurableSocket
  implements SSLConfigurable
{
  private final SSLSocket delegate;
  
  public SSLConfigurableSocket(SSLSocket delegate)
  {
    this.delegate = delegate;
  }
  
  public String[] getDefaultProtocols() {
    return delegate.getEnabledProtocols();
  }
  
  public String[] getSupportedProtocols() {
    return delegate.getSupportedProtocols();
  }
  
  public void setEnabledProtocols(String[] protocols) {
    delegate.setEnabledProtocols(protocols);
  }
  
  public String[] getDefaultCipherSuites() {
    return delegate.getEnabledCipherSuites();
  }
  
  public String[] getSupportedCipherSuites() {
    return delegate.getSupportedCipherSuites();
  }
  
  public void setEnabledCipherSuites(String[] suites) {
    delegate.setEnabledCipherSuites(suites);
  }
  
  public void setNeedClientAuth(boolean state) {
    delegate.setNeedClientAuth(state);
  }
  
  public void setWantClientAuth(boolean state) {
    delegate.setWantClientAuth(state);
  }
}
