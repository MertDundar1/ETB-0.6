package ch.qos.logback.classic.joran.action;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;












public class ContextNameAction
  extends Action
{
  public ContextNameAction() {}
  
  public void begin(InterpretationContext ec, String name, Attributes attributes) {}
  
  public void body(InterpretationContext ec, String body)
  {
    String finalBody = ec.subst(body);
    addInfo("Setting logger context name as [" + finalBody + "]");
    try {
      context.setName(finalBody);
    } catch (IllegalStateException e) {
      addError("Failed to rename context [" + context.getName() + "] as [" + finalBody + "]", e);
    }
  }
  
  public void end(InterpretationContext ec, String name) {}
}
