package ch.qos.logback.classic.jmx;

import ch.qos.logback.core.joran.spi.JoranException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

public abstract interface JMXConfiguratorMBean
{
  public abstract void reloadDefaultConfiguration()
    throws JoranException;
  
  public abstract void reloadByFileName(String paramString)
    throws JoranException, FileNotFoundException;
  
  public abstract void reloadByURL(URL paramURL)
    throws JoranException;
  
  public abstract void setLoggerLevel(String paramString1, String paramString2);
  
  public abstract String getLoggerLevel(String paramString);
  
  public abstract String getLoggerEffectiveLevel(String paramString);
  
  public abstract List<String> getLoggerList();
  
  public abstract List<String> getStatuses();
}
