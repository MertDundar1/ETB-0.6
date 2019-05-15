package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;



















public class DefinePropertyAction
  extends Action
{
  String scopeStr;
  ActionUtil.Scope scope;
  String propertyName;
  PropertyDefiner definer;
  boolean inError;
  
  public DefinePropertyAction() {}
  
  public void begin(InterpretationContext ec, String localName, Attributes attributes)
    throws ActionException
  {
    scopeStr = null;
    scope = null;
    propertyName = null;
    definer = null;
    inError = false;
    

    propertyName = attributes.getValue("name");
    scopeStr = attributes.getValue("scope");
    
    scope = ActionUtil.stringToScope(scopeStr);
    if (OptionHelper.isEmpty(propertyName)) {
      addError("Missing property name for property definer. Near [" + localName + "] line " + getLineNumber(ec));
      
      inError = true;
      return;
    }
    

    String className = attributes.getValue("class");
    if (OptionHelper.isEmpty(className)) {
      addError("Missing class name for property definer. Near [" + localName + "] line " + getLineNumber(ec));
      
      inError = true;
      return;
    }
    
    try
    {
      addInfo("About to instantiate property definer of type [" + className + "]");
      
      definer = ((PropertyDefiner)OptionHelper.instantiateByClassName(className, PropertyDefiner.class, context));
      
      definer.setContext(context);
      if ((definer instanceof LifeCycle)) {
        ((LifeCycle)definer).start();
      }
      ec.pushObject(definer);
    } catch (Exception oops) {
      inError = true;
      addError("Could not create an PropertyDefiner of type [" + className + "].", oops);
      
      throw new ActionException(oops);
    }
  }
  



  public void end(InterpretationContext ec, String name)
  {
    if (inError) {
      return;
    }
    
    Object o = ec.peekObject();
    
    if (o != definer) {
      addWarn("The object at the of the stack is not the property definer for property named [" + propertyName + "] pushed earlier.");
    }
    else {
      addInfo("Popping property definer for property named [" + propertyName + "] from the object stack");
      
      ec.popObject();
      

      String propertyValue = definer.getPropertyValue();
      if (propertyValue != null) {
        ActionUtil.setProperty(ec, propertyName, propertyValue, scope);
      }
    }
  }
}
