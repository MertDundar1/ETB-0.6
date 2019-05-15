package org.slf4j.impl;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.spi.LoggerFactoryBinder;



























public class StaticLoggerBinder
  implements LoggerFactoryBinder
{
  public static String REQUESTED_API_VERSION = "1.6";
  


  static final String NULL_CS_URL = "http://logback.qos.ch/codes.html#null_CS";
  

  private static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
  
  private static Object KEY = new Object();
  
  static {
    SINGLETON.init();
  }
  
  private boolean initialized = false;
  private LoggerContext defaultLoggerContext = new LoggerContext();
  private final ContextSelectorStaticBinder contextSelectorBinder = ContextSelectorStaticBinder.getSingleton();
  
  private StaticLoggerBinder()
  {
    defaultLoggerContext.setName("default");
  }
  
  public static StaticLoggerBinder getSingleton() {
    return SINGLETON;
  }
  


  static void reset()
  {
    SINGLETON = new StaticLoggerBinder();
    SINGLETON.init();
  }
  
  void init()
  {
    try
    {
      try
      {
        new ContextInitializer(defaultLoggerContext).autoConfig();
      } catch (JoranException je) {
        Util.report("Failed to auto configure default logger context", je);
      }
      
      if (!StatusUtil.contextHasStatusListener(defaultLoggerContext)) {
        StatusPrinter.printInCaseOfErrorsOrWarnings(defaultLoggerContext);
      }
      contextSelectorBinder.init(defaultLoggerContext, KEY);
      initialized = true;
    }
    catch (Throwable t) {
      Util.report("Failed to instantiate [" + LoggerContext.class.getName() + "]", t);
    }
  }
  
  public ILoggerFactory getLoggerFactory()
  {
    if (!initialized) {
      return defaultLoggerContext;
    }
    
    if (contextSelectorBinder.getContextSelector() == null) {
      throw new IllegalStateException("contextSelector cannot be null. See also http://logback.qos.ch/codes.html#null_CS");
    }
    
    return contextSelectorBinder.getContextSelector().getLoggerContext();
  }
  
  public String getLoggerFactoryClassStr() {
    return contextSelectorBinder.getClass().getName();
  }
}
