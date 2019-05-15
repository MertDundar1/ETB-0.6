package ch.qos.logback.classic.jul;








public class JULHelper
{
  public JULHelper() {}
  







  public static final boolean isRegularNonRootLogger(java.util.logging.Logger julLogger)
  {
    if (julLogger == null)
      return false;
    return !julLogger.getName().equals("");
  }
  
  public static final boolean isRoot(java.util.logging.Logger julLogger) {
    if (julLogger == null)
      return false;
    return julLogger.getName().equals("");
  }
  
  public static java.util.logging.Level asJULLevel(ch.qos.logback.classic.Level lbLevel) {
    if (lbLevel == null) {
      throw new IllegalArgumentException("Unexpected level [null]");
    }
    switch (levelInt) {
    case -2147483648: 
      return java.util.logging.Level.ALL;
    case 5000: 
      return java.util.logging.Level.FINEST;
    case 10000: 
      return java.util.logging.Level.FINE;
    case 20000: 
      return java.util.logging.Level.INFO;
    case 30000: 
      return java.util.logging.Level.WARNING;
    case 40000: 
      return java.util.logging.Level.SEVERE;
    case 2147483647: 
      return java.util.logging.Level.OFF;
    }
    throw new IllegalArgumentException("Unexpected level [" + lbLevel + "]");
  }
  
  public static String asJULLoggerName(String loggerName)
  {
    if ("ROOT".equals(loggerName)) {
      return "";
    }
    return loggerName;
  }
  
  public static java.util.logging.Logger asJULLogger(String loggerName) {
    String julLoggerName = asJULLoggerName(loggerName);
    return java.util.logging.Logger.getLogger(julLoggerName);
  }
  
  public static java.util.logging.Logger asJULLogger(ch.qos.logback.classic.Logger logger) {
    return asJULLogger(logger.getName());
  }
}
