package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;
import java.util.Stack;
import org.xml.sax.Attributes;





























public class NestedBasicPropertyIA
  extends ImplicitAction
{
  Stack<IADataForBasicProperty> actionDataStack = new Stack();
  
  public NestedBasicPropertyIA() {}
  
  public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext ec)
  {
    String nestedElementTagName = elementPath.peekLast();
    

    if (ec.isEmpty()) {
      return false;
    }
    
    Object o = ec.peekObject();
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
      IADataForBasicProperty ad = new IADataForBasicProperty(parentBean, aggregationType, nestedElementTagName);
      
      actionDataStack.push(ad);
      
      return true;
    }
    addError("PropertySetter.canContainComponent returned " + aggregationType);
    return false;
  }
  


  public void begin(InterpretationContext ec, String localName, Attributes attributes) {}
  


  public void body(InterpretationContext ec, String body)
  {
    String finalBody = ec.subst(body);
    
    IADataForBasicProperty actionData = (IADataForBasicProperty)actionDataStack.peek();
    switch (1.$SwitchMap$ch$qos$logback$core$util$AggregationType[aggregationType.ordinal()]) {
    case 4: 
      parentBean.setProperty(propertyName, finalBody);
      break;
    case 5: 
      parentBean.addBasicProperty(propertyName, finalBody);
    }
    
  }
  
  public void end(InterpretationContext ec, String tagName)
  {
    actionDataStack.pop();
  }
}
