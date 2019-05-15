package ch.qos.logback.classic.util;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferUtil;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;






















public class ContextInitializer
{
  public static final String GROOVY_AUTOCONFIG_FILE = "logback.groovy";
  public static final String AUTOCONFIG_FILE = "logback.xml";
  public static final String TEST_AUTOCONFIG_FILE = "logback-test.xml";
  public static final String CONFIG_FILE_PROPERTY = "logback.configurationFile";
  public static final String STATUS_LISTENER_CLASS = "logback.statusListenerClass";
  public static final String SYSOUT = "SYSOUT";
  final LoggerContext loggerContext;
  
  public ContextInitializer(LoggerContext loggerContext)
  {
    this.loggerContext = loggerContext;
  }
  
  public void configureByResource(URL url) throws JoranException {
    if (url == null) {
      throw new IllegalArgumentException("URL argument cannot be null");
    }
    String urlString = url.toString();
    if (urlString.endsWith("groovy")) {
      if (EnvUtil.isGroovyAvailable())
      {

        GafferUtil.runGafferConfiguratorOn(loggerContext, this, url);
      } else {
        StatusManager sm = loggerContext.getStatusManager();
        sm.add(new ErrorStatus("Groovy classes are not available on the class path. ABORTING INITIALIZATION.", loggerContext));
      }
    }
    else if (urlString.endsWith("xml")) {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(loggerContext);
      configurator.doConfigure(url);
    } else {
      throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be either .groovy or .xml");
    }
  }
  
  void joranConfigureByResource(URL url) throws JoranException {
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(loggerContext);
    configurator.doConfigure(url);
  }
  
  private URL findConfigFileURLFromSystemProperties(ClassLoader classLoader, boolean updateStatus) {
    String logbackConfigFile = OptionHelper.getSystemProperty("logback.configurationFile");
    if (logbackConfigFile != null) {
      URL result = null;
      try {
        result = new URL(logbackConfigFile);
        return result;
      }
      catch (MalformedURLException e)
      {
        result = Loader.getResource(logbackConfigFile, classLoader);
        if (result != null) {
          return result;
        }
        File f = new File(logbackConfigFile);
        if ((f.exists()) && (f.isFile())) {
          try {
            result = f.toURI().toURL();
            return result;
          }
          catch (MalformedURLException e1) {}
        }
      } finally {
        if (updateStatus) {
          statusOnResourceSearch(logbackConfigFile, classLoader, result);
        }
      }
    }
    return null;
  }
  
  public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {
    ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
    URL url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
    if (url != null) {
      return url;
    }
    
    url = getResource("logback.groovy", myClassLoader, updateStatus);
    if (url != null) {
      return url;
    }
    
    url = getResource("logback-test.xml", myClassLoader, updateStatus);
    if (url != null) {
      return url;
    }
    
    return getResource("logback.xml", myClassLoader, updateStatus);
  }
  
  private URL getResource(String filename, ClassLoader myClassLoader, boolean updateStatus) {
    URL url = Loader.getResource(filename, myClassLoader);
    if (updateStatus) {
      statusOnResourceSearch(filename, myClassLoader, url);
    }
    return url;
  }
  
  public void autoConfig() throws JoranException {
    StatusListenerConfigHelper.installIfAsked(loggerContext);
    URL url = findURLOfDefaultConfigurationFile(true);
    if (url != null) {
      configureByResource(url);
    } else {
      Configurator c = (Configurator)EnvUtil.loadFromServiceLoader(Configurator.class);
      if (c != null) {
        try {
          c.setContext(loggerContext);
          c.configure(loggerContext);
        } catch (Exception e) {
          throw new LogbackException(String.format("Failed to initialize Configurator: %s using ServiceLoader", new Object[] { c != null ? c.getClass().getCanonicalName() : "null" }), e);
        }
        
      } else {
        BasicConfigurator.configure(loggerContext);
      }
    }
  }
  
  private void multiplicityWarning(String resourceName, ClassLoader classLoader) {
    Set<URL> urlSet = null;
    StatusManager sm = loggerContext.getStatusManager();
    try {
      urlSet = Loader.getResourceOccurrenceCount(resourceName, classLoader);
    } catch (IOException e) {
      sm.add(new ErrorStatus("Failed to get url list for resource [" + resourceName + "]", loggerContext, e));
    }
    
    if ((urlSet != null) && (urlSet.size() > 1)) {
      sm.add(new WarnStatus("Resource [" + resourceName + "] occurs multiple times on the classpath.", loggerContext));
      
      for (URL url : urlSet) {
        sm.add(new WarnStatus("Resource [" + resourceName + "] occurs at [" + url.toString() + "]", loggerContext));
      }
    }
  }
  
  private void statusOnResourceSearch(String resourceName, ClassLoader classLoader, URL url)
  {
    StatusManager sm = loggerContext.getStatusManager();
    if (url == null) {
      sm.add(new InfoStatus("Could NOT find resource [" + resourceName + "]", loggerContext));
    }
    else {
      sm.add(new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", loggerContext));
      
      multiplicityWarning(resourceName, classLoader);
    }
  }
}
