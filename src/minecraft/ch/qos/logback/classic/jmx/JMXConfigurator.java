package ch.qos.logback.classic.jmx;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusListenerAsList;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
























public class JMXConfigurator
  extends ContextAwareBase
  implements JMXConfiguratorMBean, LoggerContextListener
{
  private static String EMPTY = "";
  
  LoggerContext loggerContext;
  
  MBeanServer mbs;
  
  ObjectName objectName;
  
  String objectNameAsString;
  boolean debug = true;
  
  boolean started;
  
  public JMXConfigurator(LoggerContext loggerContext, MBeanServer mbs, ObjectName objectName)
  {
    started = true;
    context = loggerContext;
    this.loggerContext = loggerContext;
    this.mbs = mbs;
    this.objectName = objectName;
    objectNameAsString = objectName.toString();
    if (previouslyRegisteredListenerWithSameObjectName()) {
      addError("Previously registered JMXConfigurator named [" + objectNameAsString + "] in the logger context named [" + loggerContext.getName() + "]");

    }
    else
    {
      loggerContext.addListener(this);
    }
  }
  
  private boolean previouslyRegisteredListenerWithSameObjectName() {
    List<LoggerContextListener> lcll = loggerContext.getCopyOfListenerList();
    for (LoggerContextListener lcl : lcll) {
      if ((lcl instanceof JMXConfigurator)) {
        JMXConfigurator jmxConfigurator = (JMXConfigurator)lcl;
        if (objectName.equals(objectName)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void reloadDefaultConfiguration() throws JoranException {
    ContextInitializer ci = new ContextInitializer(loggerContext);
    URL url = ci.findURLOfDefaultConfigurationFile(true);
    reloadByURL(url);
  }
  
  public void reloadByFileName(String fileName) throws JoranException, FileNotFoundException
  {
    File f = new File(fileName);
    if ((f.exists()) && (f.isFile()))
    {
      try {
        URL url = f.toURI().toURL();
        reloadByURL(url);
      } catch (MalformedURLException e) {
        throw new RuntimeException("Unexpected MalformedURLException occured. See nexted cause.", e);
      }
    }
    else
    {
      String errMsg = "Could not find [" + fileName + "]";
      addInfo(errMsg);
      throw new FileNotFoundException(errMsg);
    }
  }
  
  void addStatusListener(StatusListener statusListener) {
    StatusManager sm = loggerContext.getStatusManager();
    sm.add(statusListener);
  }
  
  void removeStatusListener(StatusListener statusListener) {
    StatusManager sm = loggerContext.getStatusManager();
    sm.remove(statusListener);
  }
  
  public void reloadByURL(URL url) throws JoranException {
    StatusListenerAsList statusListenerAsList = new StatusListenerAsList();
    
    addStatusListener(statusListenerAsList);
    addInfo("Resetting context: " + loggerContext.getName());
    loggerContext.reset();
    
    addStatusListener(statusListenerAsList);
    try
    {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(loggerContext);
      configurator.doConfigure(url);
      addInfo("Context: " + loggerContext.getName() + " reloaded.");
    } finally {
      removeStatusListener(statusListenerAsList);
      if (debug) {
        StatusPrinter.print(statusListenerAsList.getStatusList());
      }
    }
  }
  
  public void setLoggerLevel(String loggerName, String levelStr) {
    if (loggerName == null) {
      return;
    }
    if (levelStr == null) {
      return;
    }
    loggerName = loggerName.trim();
    levelStr = levelStr.trim();
    
    addInfo("Trying to set level " + levelStr + " to logger " + loggerName);
    LoggerContext lc = (LoggerContext)context;
    
    Logger logger = lc.getLogger(loggerName);
    if ("null".equalsIgnoreCase(levelStr)) {
      logger.setLevel(null);
    } else {
      Level level = Level.toLevel(levelStr, null);
      if (level != null) {
        logger.setLevel(level);
      }
    }
  }
  
  public String getLoggerLevel(String loggerName) {
    if (loggerName == null) {
      return EMPTY;
    }
    
    loggerName = loggerName.trim();
    
    LoggerContext lc = (LoggerContext)context;
    Logger logger = lc.exists(loggerName);
    if ((logger != null) && (logger.getLevel() != null)) {
      return logger.getLevel().toString();
    }
    return EMPTY;
  }
  
  public String getLoggerEffectiveLevel(String loggerName)
  {
    if (loggerName == null) {
      return EMPTY;
    }
    
    loggerName = loggerName.trim();
    
    LoggerContext lc = (LoggerContext)context;
    Logger logger = lc.exists(loggerName);
    if (logger != null) {
      return logger.getEffectiveLevel().toString();
    }
    return EMPTY;
  }
  
  public List<String> getLoggerList()
  {
    LoggerContext lc = (LoggerContext)context;
    List<String> strList = new ArrayList();
    Iterator<Logger> it = lc.getLoggerList().iterator();
    while (it.hasNext()) {
      Logger log = (Logger)it.next();
      strList.add(log.getName());
    }
    return strList;
  }
  
  public List<String> getStatuses() {
    List<String> list = new ArrayList();
    Iterator<Status> it = context.getStatusManager().getCopyOfStatusList().iterator();
    
    while (it.hasNext()) {
      list.add(((Status)it.next()).toString());
    }
    return list;
  }
  



  public void onStop(LoggerContext context)
  {
    if (!started) {
      addInfo("onStop() method called on a stopped JMXActivator [" + objectNameAsString + "]");
      
      return;
    }
    if (mbs.isRegistered(objectName)) {
      try {
        addInfo("Unregistering mbean [" + objectNameAsString + "]");
        mbs.unregisterMBean(objectName);
      }
      catch (InstanceNotFoundException e) {
        addError("Unable to find a verifiably registered mbean [" + objectNameAsString + "]", e);
      }
      catch (MBeanRegistrationException e) {
        addError("Failed to unregister [" + objectNameAsString + "]", e);
      }
    } else {
      addInfo("mbean [" + objectNameAsString + "] was not in the mbean registry. This is OK.");
    }
    
    stop();
  }
  

  public void onLevelChange(Logger logger, Level level) {}
  
  public void onReset(LoggerContext context)
  {
    addInfo("onReset() method called JMXActivator [" + objectNameAsString + "]");
  }
  




  public boolean isResetResistant()
  {
    return true;
  }
  
  private void clearFields() {
    mbs = null;
    objectName = null;
    loggerContext = null;
  }
  
  private void stop() {
    started = false;
    clearFields();
  }
  

  public void onStart(LoggerContext context) {}
  

  public String toString()
  {
    return getClass().getName() + "(" + context.getName() + ")";
  }
}
