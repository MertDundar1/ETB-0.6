package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;












public class RootLoggerAction
  extends Action
{
  Logger root;
  
  public RootLoggerAction() {}
  
  boolean inError = false;
  
  public void begin(InterpretationContext ec, String name, Attributes attributes) {
    inError = false;
    
    LoggerContext loggerContext = (LoggerContext)context;
    root = loggerContext.getLogger("ROOT");
    
    String levelStr = ec.subst(attributes.getValue("level"));
    if (!OptionHelper.isEmpty(levelStr)) {
      Level level = Level.toLevel(levelStr);
      addInfo("Setting level of ROOT logger to " + level);
      root.setLevel(level);
    }
    ec.pushObject(root);
  }
  
  public void end(InterpretationContext ec, String name) {
    if (inError) {
      return;
    }
    Object o = ec.peekObject();
    if (o != root) {
      addWarn("The object on the top the of the stack is not the root logger");
      addWarn("It is: " + o);
    } else {
      ec.popObject();
    }
  }
  
  public void finish(InterpretationContext ec) {}
}
