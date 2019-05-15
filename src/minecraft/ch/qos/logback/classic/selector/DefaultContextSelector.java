package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import java.util.Arrays;
import java.util.List;













public class DefaultContextSelector
  implements ContextSelector
{
  private LoggerContext defaultLoggerContext;
  
  public DefaultContextSelector(LoggerContext context)
  {
    defaultLoggerContext = context;
  }
  
  public LoggerContext getLoggerContext() {
    return getDefaultLoggerContext();
  }
  
  public LoggerContext getDefaultLoggerContext() {
    return defaultLoggerContext;
  }
  
  public LoggerContext detachLoggerContext(String loggerContextName) {
    return defaultLoggerContext;
  }
  
  public List<String> getContextNames() {
    return Arrays.asList(new String[] { defaultLoggerContext.getName() });
  }
  
  public LoggerContext getLoggerContext(String name) {
    if (defaultLoggerContext.getName().equals(name)) {
      return defaultLoggerContext;
    }
    return null;
  }
}
