package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.EventPlayer;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
















public class IfAction
  extends Action
{
  private static final String CONDITION_ATTR = "condition";
  public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
  public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
  Stack<IfState> stack = new Stack();
  
  public IfAction() {}
  
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException
  {
    IfState state = new IfState();
    boolean emptyStack = stack.isEmpty();
    stack.push(state);
    
    if (!emptyStack) {
      return;
    }
    
    ic.pushObject(this);
    if (!EnvUtil.isJaninoAvailable()) {
      addError("Could not find Janino library on the class path. Skipping conditional processing.");
      addError("See also http://logback.qos.ch/codes.html#ifJanino");
      return;
    }
    
    active = true;
    Condition condition = null;
    String conditionAttribute = attributes.getValue("condition");
    

    if (!OptionHelper.isEmpty(conditionAttribute)) {
      conditionAttribute = OptionHelper.substVars(conditionAttribute, ic, context);
      PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(ic);
      pesb.setContext(context);
      try {
        condition = pesb.build(conditionAttribute);
      } catch (Exception e) {
        addError("Failed to parse condition [" + conditionAttribute + "]", e);
      }
      
      if (condition != null) {
        boolResult = Boolean.valueOf(condition.evaluate());
      }
    }
  }
  


  public void end(InterpretationContext ic, String name)
    throws ActionException
  {
    IfState state = (IfState)stack.pop();
    if (!active) {
      return;
    }
    

    Object o = ic.peekObject();
    if (o == null) {
      throw new IllegalStateException("Unexpected null object on stack");
    }
    if (!(o instanceof IfAction)) {
      throw new IllegalStateException("Unexpected object of type [" + o.getClass() + "] on stack");
    }
    

    if (o != this) {
      throw new IllegalStateException("IfAction different then current one on stack");
    }
    
    ic.popObject();
    
    if (boolResult == null) {
      addError("Failed to determine \"if then else\" result");
      return;
    }
    
    Interpreter interpreter = ic.getJoranInterpreter();
    List<SaxEvent> listToPlay = thenSaxEventList;
    if (!boolResult.booleanValue()) {
      listToPlay = elseSaxEventList;
    }
    

    if (listToPlay != null)
    {
      interpreter.getEventPlayer().addEventsDynamically(listToPlay, 1);
    }
  }
  

  public void setThenSaxEventList(List<SaxEvent> thenSaxEventList)
  {
    IfState state = (IfState)stack.firstElement();
    if (active) {
      thenSaxEventList = thenSaxEventList;
    } else {
      throw new IllegalStateException("setThenSaxEventList() invoked on inactive IfAction");
    }
  }
  
  public void setElseSaxEventList(List<SaxEvent> elseSaxEventList) {
    IfState state = (IfState)stack.firstElement();
    if (active) {
      elseSaxEventList = elseSaxEventList;
    } else {
      throw new IllegalStateException("setElseSaxEventList() invoked on inactive IfAction");
    }
  }
  
  public boolean isActive()
  {
    if (stack == null) return false;
    if (stack.isEmpty()) return false;
    return stack.peek()).active;
  }
}
