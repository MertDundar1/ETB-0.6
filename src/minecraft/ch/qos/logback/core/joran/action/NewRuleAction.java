package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;












public class NewRuleAction
  extends Action
{
  boolean inError = false;
  

  public NewRuleAction() {}
  
  public void begin(InterpretationContext ec, String localName, Attributes attributes)
  {
    inError = false;
    
    String pattern = attributes.getValue("pattern");
    String actionClass = attributes.getValue("actionClass");
    
    if (OptionHelper.isEmpty(pattern)) {
      inError = true;
      String errorMsg = "No 'pattern' attribute in <newRule>";
      addError(errorMsg);
      return;
    }
    
    if (OptionHelper.isEmpty(actionClass)) {
      inError = true;
      String errorMsg = "No 'actionClass' attribute in <newRule>";
      addError(errorMsg);
      return;
    }
    try
    {
      addInfo("About to add new Joran parsing rule [" + pattern + "," + actionClass + "].");
      
      ec.getJoranInterpreter().getRuleStore().addRule(new ElementSelector(pattern), actionClass);
    }
    catch (Exception oops) {
      inError = true;
      String errorMsg = "Could not add new Joran parsing rule [" + pattern + "," + actionClass + "]";
      
      addError(errorMsg);
    }
  }
  
  public void end(InterpretationContext ec, String n) {}
  
  public void finish(InterpretationContext ec) {}
}
