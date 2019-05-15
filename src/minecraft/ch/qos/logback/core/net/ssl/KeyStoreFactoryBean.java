package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.util.LocationUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


































public class KeyStoreFactoryBean
{
  private String location;
  private String provider;
  private String type;
  private String password;
  
  public KeyStoreFactoryBean() {}
  
  public KeyStore createKeyStore()
    throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException
  {
    if (getLocation() == null) {
      throw new IllegalArgumentException("location is required");
    }
    
    InputStream inputStream = null;
    try {
      URL url = LocationUtil.urlForResource(getLocation());
      inputStream = url.openStream();
      KeyStore keyStore = newKeyStore();
      keyStore.load(inputStream, getPassword().toCharArray());
      return keyStore;
    }
    catch (NoSuchProviderException ex) {
      throw new NoSuchProviderException("no such keystore provider: " + getProvider());
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new NoSuchAlgorithmException("no such keystore type: " + getType());
    }
    catch (FileNotFoundException ex)
    {
      throw new KeyStoreException(getLocation() + ": file not found");
    }
    catch (Exception ex) {
      throw new KeyStoreException(getLocation() + ": " + ex.getMessage(), ex);
    }
    finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      }
      catch (IOException ex) {
        ex.printStackTrace(System.err);
      }
    }
  }
  




  private KeyStore newKeyStore()
    throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException
  {
    return getProvider() != null ? KeyStore.getInstance(getType(), getProvider()) : KeyStore.getInstance(getType());
  }
  





  public String getLocation()
  {
    return location;
  }
  





  public void setLocation(String location)
  {
    this.location = location;
  }
  




  public String getType()
  {
    if (type == null) {
      return "JKS";
    }
    return type;
  }
  






  public void setType(String type)
  {
    this.type = type;
  }
  



  public String getProvider()
  {
    return provider;
  }
  




  public void setProvider(String provider)
  {
    this.provider = provider;
  }
  




  public String getPassword()
  {
    if (password == null) {
      return "changeit";
    }
    return password;
  }
  



  public void setPassword(String password)
  {
    this.password = password;
  }
}
