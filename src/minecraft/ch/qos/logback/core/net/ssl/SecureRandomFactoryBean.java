package ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;





























public class SecureRandomFactoryBean
{
  private String algorithm;
  private String provider;
  
  public SecureRandomFactoryBean() {}
  
  public SecureRandom createSecureRandom()
    throws NoSuchProviderException, NoSuchAlgorithmException
  {
    try
    {
      return getProvider() != null ? SecureRandom.getInstance(getAlgorithm(), getProvider()) : SecureRandom.getInstance(getAlgorithm());

    }
    catch (NoSuchProviderException ex)
    {
      throw new NoSuchProviderException("no such secure random provider: " + getProvider());
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new NoSuchAlgorithmException("no such secure random algorithm: " + getAlgorithm());
    }
  }
  






  public String getAlgorithm()
  {
    if (algorithm == null) {
      return "SHA1PRNG";
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
