package ch.qos.logback.core.net.ssl;

public abstract interface SSLComponent
{
  public abstract SSLConfiguration getSsl();
  
  public abstract void setSsl(SSLConfiguration paramSSLConfiguration);
}
