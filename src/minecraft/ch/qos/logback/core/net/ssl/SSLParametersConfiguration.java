package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StringCollectionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

























public class SSLParametersConfiguration
  extends ContextAwareBase
{
  private String includedProtocols;
  private String excludedProtocols;
  private String includedCipherSuites;
  private String excludedCipherSuites;
  private Boolean needClientAuth;
  private Boolean wantClientAuth;
  private String[] enabledProtocols;
  private String[] enabledCipherSuites;
  
  public SSLParametersConfiguration() {}
  
  public void configure(SSLConfigurable socket)
  {
    socket.setEnabledProtocols(enabledProtocols(socket.getSupportedProtocols(), socket.getDefaultProtocols()));
    
    socket.setEnabledCipherSuites(enabledCipherSuites(socket.getSupportedCipherSuites(), socket.getDefaultCipherSuites()));
    
    if (isNeedClientAuth() != null) {
      socket.setNeedClientAuth(isNeedClientAuth().booleanValue());
    }
    if (isWantClientAuth() != null) {
      socket.setWantClientAuth(isWantClientAuth().booleanValue());
    }
  }
  






  private String[] enabledProtocols(String[] supportedProtocols, String[] defaultProtocols)
  {
    if (enabledProtocols == null)
    {

      if ((OptionHelper.isEmpty(getIncludedProtocols())) && (OptionHelper.isEmpty(getExcludedProtocols())))
      {
        enabledProtocols = ((String[])Arrays.copyOf(defaultProtocols, defaultProtocols.length));
      }
      else
      {
        enabledProtocols = includedStrings(supportedProtocols, getIncludedProtocols(), getExcludedProtocols());
      }
      
      for (String protocol : enabledProtocols) {
        addInfo("enabled protocol: " + protocol);
      }
    }
    return enabledProtocols;
  }
  






  private String[] enabledCipherSuites(String[] supportedCipherSuites, String[] defaultCipherSuites)
  {
    if (enabledCipherSuites == null)
    {

      if ((OptionHelper.isEmpty(getIncludedCipherSuites())) && (OptionHelper.isEmpty(getExcludedCipherSuites())))
      {
        enabledCipherSuites = ((String[])Arrays.copyOf(defaultCipherSuites, defaultCipherSuites.length));
      }
      else
      {
        enabledCipherSuites = includedStrings(supportedCipherSuites, getIncludedCipherSuites(), getExcludedCipherSuites());
      }
      
      for (String cipherSuite : enabledCipherSuites) {
        addInfo("enabled cipher suite: " + cipherSuite);
      }
    }
    return enabledCipherSuites;
  }
  










  private String[] includedStrings(String[] defaults, String included, String excluded)
  {
    List<String> values = new ArrayList(defaults.length);
    values.addAll(Arrays.asList(defaults));
    if (included != null) {
      StringCollectionUtil.retainMatching(values, stringToArray(included));
    }
    if (excluded != null) {
      StringCollectionUtil.removeMatching(values, stringToArray(excluded));
    }
    return (String[])values.toArray(new String[values.size()]);
  }
  




  private String[] stringToArray(String s)
  {
    return s.split("\\s*,\\s*");
  }
  




  public String getIncludedProtocols()
  {
    return includedProtocols;
  }
  





  public void setIncludedProtocols(String protocols)
  {
    includedProtocols = protocols;
  }
  




  public String getExcludedProtocols()
  {
    return excludedProtocols;
  }
  





  public void setExcludedProtocols(String protocols)
  {
    excludedProtocols = protocols;
  }
  




  public String getIncludedCipherSuites()
  {
    return includedCipherSuites;
  }
  





  public void setIncludedCipherSuites(String cipherSuites)
  {
    includedCipherSuites = cipherSuites;
  }
  




  public String getExcludedCipherSuites()
  {
    return excludedCipherSuites;
  }
  





  public void setExcludedCipherSuites(String cipherSuites)
  {
    excludedCipherSuites = cipherSuites;
  }
  



  public Boolean isNeedClientAuth()
  {
    return needClientAuth;
  }
  



  public void setNeedClientAuth(Boolean needClientAuth)
  {
    this.needClientAuth = needClientAuth;
  }
  



  public Boolean isWantClientAuth()
  {
    return wantClientAuth;
  }
  



  public void setWantClientAuth(Boolean wantClientAuth)
  {
    this.wantClientAuth = wantClientAuth;
  }
}
