package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;













public class LoggerContextListenerAction
  extends Action
{
  boolean inError = false;
  LoggerContextListener lcl;
  
  public LoggerContextListenerAction() {}
  
  public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException
  {
    inError = false;
    
    String className = attributes.getValue("class");
    if (OptionHelper.isEmpty(className)) {
      addError("Mandatory \"class\" attribute not set for <loggerContextListener> element");
      
      inError = true;
      return;
    }
    try
    {
      lcl = ((LoggerContextListener)OptionHelper.instantiateByClassName(className, LoggerContextListener.class, context));
      

      if ((lcl instanceof ContextAware)) {
        ((ContextAware)lcl).setContext(context);
      }
      
      ec.pushObject(lcl);
      addInfo("Adding LoggerContextListener of type [" + className + "] to the object stack");
    }
    catch (Exception oops)
    {
      inError = true;
      addError("Could not create LoggerContextListener of type " + className + "].", oops);
    }
  }
  
  public void end(InterpretationContext ec, String name) throws ActionException
  {
    if (inError) {
      return;
    }
    Object o = ec.peekObject();
    
    if (o != lcl) {
      addWarn("The object on the top the of the stack is not the LoggerContextListener pushed earlier.");
    } else {
      if ((lcl instanceof LifeCycle)) {
        ((LifeCycle)lcl).start();
        addInfo("Starting LoggerContextListener");
      }
      ((LoggerContext)context).addListener(lcl);
      ec.popObject();
    }
  }
}
