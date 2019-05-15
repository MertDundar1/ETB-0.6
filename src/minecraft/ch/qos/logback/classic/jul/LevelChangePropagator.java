package ch.qos.logback.classic.jul;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;

















public class LevelChangePropagator
  extends ContextAwareBase
  implements LoggerContextListener, LifeCycle
{
  public LevelChangePropagator() {}
  
  private Set julLoggerSet = new HashSet();
  boolean isStarted = false;
  boolean resetJUL = false;
  
  public void setResetJUL(boolean resetJUL) {
    this.resetJUL = resetJUL;
  }
  
  public boolean isResetResistant() {
    return false;
  }
  

  public void onStart(LoggerContext context) {}
  

  public void onReset(LoggerContext context) {}
  
  public void onStop(LoggerContext context) {}
  
  public void onLevelChange(ch.qos.logback.classic.Logger logger, ch.qos.logback.classic.Level level)
  {
    propagate(logger, level);
  }
  
  private void propagate(ch.qos.logback.classic.Logger logger, ch.qos.logback.classic.Level level) {
    addInfo("Propagating " + level + " level on " + logger + " onto the JUL framework");
    java.util.logging.Logger julLogger = JULHelper.asJULLogger(logger);
    

    julLoggerSet.add(julLogger);
    java.util.logging.Level julLevel = JULHelper.asJULLevel(level);
    julLogger.setLevel(julLevel);
  }
  
  public void resetJULLevels() {
    LogManager lm = LogManager.getLogManager();
    
    Enumeration e = lm.getLoggerNames();
    while (e.hasMoreElements()) {
      String loggerName = (String)e.nextElement();
      java.util.logging.Logger julLogger = lm.getLogger(loggerName);
      if ((JULHelper.isRegularNonRootLogger(julLogger)) && (julLogger.getLevel() != null)) {
        addInfo("Setting level of jul logger [" + loggerName + "] to null");
        julLogger.setLevel(null);
      }
    }
  }
  
  private void propagateExistingLoggerLevels() {
    LoggerContext loggerContext = (LoggerContext)context;
    List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
    for (ch.qos.logback.classic.Logger l : loggerList) {
      if (l.getLevel() != null) {
        propagate(l, l.getLevel());
      }
    }
  }
  
  public void start() {
    if (resetJUL) {
      resetJULLevels();
    }
    propagateExistingLoggerLevels();
    
    isStarted = true;
  }
  
  public void stop() {
    isStarted = false;
  }
  
  public boolean isStarted() {
    return isStarted;
  }
}
