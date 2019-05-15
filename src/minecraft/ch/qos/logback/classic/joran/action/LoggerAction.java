package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;




















public class LoggerAction
  extends Action
{
  public static final String LEVEL_ATTRIBUTE = "level";
  boolean inError = false;
  
  public LoggerAction() {}
  
  public void begin(InterpretationContext ec, String name, Attributes attributes) { inError = false;
    logger = null;
    
    LoggerContext loggerContext = (LoggerContext)context;
    
    String loggerName = ec.subst(attributes.getValue("name"));
    
    if (OptionHelper.isEmpty(loggerName)) {
      inError = true;
      String aroundLine = getLineColStr(ec);
      String errorMsg = "No 'name' attribute in element " + name + ", around " + aroundLine;
      addError(errorMsg);
      return;
    }
    
    logger = loggerContext.getLogger(loggerName);
    
    String levelStr = ec.subst(attributes.getValue("level"));
    
    if (!OptionHelper.isEmpty(levelStr)) {
      if (("INHERITED".equalsIgnoreCase(levelStr)) || ("NULL".equalsIgnoreCase(levelStr)))
      {
        addInfo("Setting level of logger [" + loggerName + "] to null, i.e. INHERITED");
        
        logger.setLevel(null);
      } else {
        Level level = Level.toLevel(levelStr);
        addInfo("Setting level of logger [" + loggerName + "] to " + level);
        logger.setLevel(level);
      }
    }
    
    String additivityStr = ec.subst(attributes.getValue("additivity"));
    if (!OptionHelper.isEmpty(additivityStr)) {
      boolean additive = OptionHelper.toBoolean(additivityStr, true);
      addInfo("Setting additivity of logger [" + loggerName + "] to " + additive);
      
      logger.setAdditive(additive);
    }
    ec.pushObject(logger);
  }
  
  Logger logger;
  public void end(InterpretationContext ec, String e) { if (inError) {
      return;
    }
    Object o = ec.peekObject();
    if (o != logger) {
      addWarn("The object on the top the of the stack is not " + logger + " pushed earlier");
      addWarn("It is: " + o);
    } else {
      ec.popObject();
    }
  }
  
  public void finish(InterpretationContext ec) {}
}
