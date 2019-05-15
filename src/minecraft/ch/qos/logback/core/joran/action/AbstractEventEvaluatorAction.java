package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.xml.sax.Attributes;















public abstract class AbstractEventEvaluatorAction
  extends Action
{
  EventEvaluator<?> evaluator;
  boolean inError = false;
  

  public AbstractEventEvaluatorAction() {}
  
  public void begin(InterpretationContext ec, String name, Attributes attributes)
  {
    inError = false;
    evaluator = null;
    
    String className = attributes.getValue("class");
    if (OptionHelper.isEmpty(className)) {
      className = defaultClassName();
      addInfo("Assuming default evaluator class [" + className + "]");
    }
    
    if (OptionHelper.isEmpty(className)) {
      className = defaultClassName();
      inError = true;
      addError("Mandatory \"class\" attribute not set for <evaluator>");
      
      return;
    }
    
    String evaluatorName = attributes.getValue("name");
    if (OptionHelper.isEmpty(evaluatorName)) {
      inError = true;
      addError("Mandatory \"name\" attribute not set for <evaluator>");
      
      return;
    }
    try {
      evaluator = ((EventEvaluator)OptionHelper.instantiateByClassName(className, EventEvaluator.class, context));
      

      evaluator.setContext(context);
      evaluator.setName(evaluatorName);
      
      ec.pushObject(evaluator);
      addInfo("Adding evaluator named [" + evaluatorName + "] to the object stack");
    }
    catch (Exception oops)
    {
      inError = true;
      addError("Could not create evaluator of type " + className + "].", oops);
    }
  }
  





  protected abstract String defaultClassName();
  




  public void end(InterpretationContext ec, String e)
  {
    if (inError) {
      return;
    }
    
    if ((evaluator instanceof LifeCycle)) {
      evaluator.start();
      addInfo("Starting evaluator named [" + evaluator.getName() + "]");
    }
    
    Object o = ec.peekObject();
    
    if (o != evaluator) {
      addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
    } else {
      ec.popObject();
      try
      {
        Map<String, EventEvaluator<?>> evaluatorMap = (Map)context.getObject("EVALUATOR_MAP");
        
        if (evaluatorMap == null) {
          addError("Could not find EvaluatorMap");
        } else {
          evaluatorMap.put(evaluator.getName(), evaluator);
        }
      } catch (Exception ex) {
        addError("Could not set evaluator named [" + evaluator + "].", ex);
      }
    }
  }
  
  public void finish(InterpretationContext ec) {}
}
