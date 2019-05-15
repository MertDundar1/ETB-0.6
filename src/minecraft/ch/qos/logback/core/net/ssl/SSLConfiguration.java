package ch.qos.logback.core.net.ssl;









public class SSLConfiguration
  extends SSLContextFactoryBean
{
  private SSLParametersConfiguration parameters;
  







  public SSLConfiguration() {}
  







  public SSLParametersConfiguration getParameters()
  {
    if (parameters == null) {
      parameters = new SSLParametersConfiguration();
    }
    return parameters;
  }
  



  public void setParameters(SSLParametersConfiguration parameters)
  {
    this.parameters = parameters;
  }
}
