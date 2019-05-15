package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;



























public class Logger
  extends Category
{
  private static final String LOGGER_FQCN = Logger.class.getName();
  
  protected Logger(String name) {
    super(name);
  }
  
  public static Logger getLogger(String name) {
    return Log4jLoggerFactory.getLogger(name);
  }
  
  public static Logger getLogger(String name, LoggerFactory loggerFactory) {
    return Log4jLoggerFactory.getLogger(name, loggerFactory);
  }
  
  public static Logger getLogger(Class clazz) {
    return getLogger(clazz.getName());
  }
  




  public static Logger getRootLogger()
  {
    return Log4jLoggerFactory.getLogger("ROOT");
  }
  



  public boolean isTraceEnabled()
  {
    return slf4jLogger.isTraceEnabled();
  }
  


  public void trace(Object message)
  {
    differentiatedLog(null, LOGGER_FQCN, 0, message, null);
  }
  



  public void trace(Object message, Throwable t)
  {
    differentiatedLog(null, LOGGER_FQCN, 0, message, null);
  }
}
