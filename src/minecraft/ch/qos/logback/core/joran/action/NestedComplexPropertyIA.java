package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Stack;
import org.xml.sax.Attributes;




























public class NestedComplexPropertyIA
  extends ImplicitAction
{
  Stack<IADataForComplexProperty> actionDataStack = new Stack();
  
  public NestedComplexPropertyIA() {}
  
  public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext ic) {
    String nestedElementTagName = elementPath.peekLast();
    

    if (ic.isEmpty()) {
      return false;
    }
    
    Object o = ic.peekObject();
    PropertySetter parentBean = new PropertySetter(o);
    parentBean.setContext(context);
    
    AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
    

    switch (1.$SwitchMap$ch$qos$logback$core$util$AggregationType[aggregationType.ordinal()]) {
    case 1: 
    case 2: 
    case 3: 
      return false;
    

    case 4: 
    case 5: 
      IADataForComplexProperty ad = new IADataForComplexProperty(parentBean, aggregationType, nestedElementTagName);
      
      actionDataStack.push(ad);
      
      return true;
    }
    addError("PropertySetter.computeAggregationType returned " + aggregationType);
    
    return false;
  }
  



  public void begin(InterpretationContext ec, String localName, Attributes attributes)
  {
    IADataForComplexProperty actionData = (IADataForComplexProperty)actionDataStack.peek();
    

    String className = attributes.getValue("class");
    
    className = ec.subst(className);
    
    Class<?> componentClass = null;
    try
    {
      if (!OptionHelper.isEmpty(className)) {
        componentClass = Loader.loadClass(className, context);
      }
      else {
        PropertySetter parentBean = parentBean;
        componentClass = parentBean.getClassNameViaImplicitRules(actionData.getComplexPropertyName(), actionData.getAggregationType(), ec.getDefaultNestedComponentRegistry());
      }
      


      if (componentClass == null) {
        inError = true;
        String errMsg = "Could not find an appropriate class for property [" + localName + "]";
        
        addError(errMsg);
        return;
      }
      
      if (OptionHelper.isEmpty(className)) {
        addInfo("Assuming default type [" + componentClass.getName() + "] for [" + localName + "] property");
      }
      

      actionData.setNestedComplexProperty(componentClass.newInstance());
      

      if ((actionData.getNestedComplexProperty() instanceof ContextAware)) {
        ((ContextAware)actionData.getNestedComplexProperty()).setContext(context);
      }
      


      ec.pushObject(actionData.getNestedComplexProperty());
    }
    catch (Exception oops) {
      inError = true;
      String msg = "Could not create component [" + localName + "] of type [" + className + "]";
      
      addError(msg, oops);
    }
  }
  



  public void end(InterpretationContext ec, String tagName)
  {
    IADataForComplexProperty actionData = (IADataForComplexProperty)actionDataStack.pop();
    

    if (inError) {
      return;
    }
    
    PropertySetter nestedBean = new PropertySetter(actionData.getNestedComplexProperty());
    
    nestedBean.setContext(context);
    

    if (nestedBean.computeAggregationType("parent") == AggregationType.AS_COMPLEX_PROPERTY) {
      nestedBean.setComplexProperty("parent", parentBean.getObj());
    }
    


    Object nestedComplexProperty = actionData.getNestedComplexProperty();
    if (((nestedComplexProperty instanceof LifeCycle)) && (NoAutoStartUtil.notMarkedWithNoAutoStart(nestedComplexProperty)))
    {
      ((LifeCycle)nestedComplexProperty).start();
    }
    
    Object o = ec.peekObject();
    
    if (o != actionData.getNestedComplexProperty()) {
      addError("The object on the top the of the stack is not the component pushed earlier.");
    } else {
      ec.popObject();
      
      switch (1.$SwitchMap$ch$qos$logback$core$util$AggregationType[aggregationType.ordinal()]) {
      case 5: 
        parentBean.setComplexProperty(tagName, actionData.getNestedComplexProperty());
        

        break;
      case 4: 
        parentBean.addComplexProperty(tagName, actionData.getNestedComplexProperty());
      }
    }
  }
}
