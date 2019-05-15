package ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.net.ssl.KeyManagerFactory;






























public class KeyManagerFactoryFactoryBean
{
  private String algorithm;
  private String provider;
  
  public KeyManagerFactoryFactoryBean() {}
  
  public KeyManagerFactory createKeyManagerFactory()
    throws NoSuchProviderException, NoSuchAlgorithmException
  {
    return getProvider() != null ? KeyManagerFactory.getInstance(getAlgorithm(), getProvider()) : KeyManagerFactory.getInstance(getAlgorithm());
  }
  







  public String getAlgorithm()
  {
    if (algorithm == null) {
      return KeyManagerFactory.getDefaultAlgorithm();
    }
    return algorithm;
  }
  





  public void setAlgorithm(String algorithm)
  {
    this.algorithm = algorithm;
  }
  



  public String getProvider()
  {
    return provider;
  }
  




  public void setProvider(String provider)
  {
    this.provider = provider;
  }
}
