package org.apache.log4j;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.spi.LoggerFactory;





























public class LogManager
{
  public LogManager() {}
  
  public static Logger getRootLogger()
  {
    return Log4jLoggerFactory.getLogger("ROOT");
  }
  
  public static Logger getLogger(String name) {
    return Log4jLoggerFactory.getLogger(name);
  }
  
  public static Logger getLogger(Class clazz) {
    return Log4jLoggerFactory.getLogger(clazz.getName());
  }
  










  public static Logger getLogger(String name, LoggerFactory loggerFactory)
  {
    return loggerFactory.makeNewLoggerInstance(name);
  }
  




  public static Enumeration getCurrentLoggers()
  {
    return new Vector().elements();
  }
  
  public static void shutdown() {}
  
  public static void resetConfiguration() {}
}
