package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import java.util.List;

public abstract interface ContextSelector
{
  public abstract LoggerContext getLoggerContext();
  
  public abstract LoggerContext getLoggerContext(String paramString);
  
  public abstract LoggerContext getDefaultLoggerContext();
  
  public abstract LoggerContext detachLoggerContext(String paramString);
  
  public abstract List<String> getContextNames();
}
