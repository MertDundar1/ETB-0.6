package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;

















public abstract class ThenOrElseActionBase
  extends Action
{
  Stack<ThenActionState> stateStack = new Stack();
  
  public ThenOrElseActionBase() {}
  
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException
  {
    if (!weAreActive(ic)) { return;
    }
    ThenActionState state = new ThenActionState();
    if (ic.isListenerListEmpty()) {
      ic.addInPlayListener(state);
      isRegistered = true;
    }
    stateStack.push(state);
  }
  
  boolean weAreActive(InterpretationContext ic) {
    Object o = ic.peekObject();
    if (!(o instanceof IfAction)) return false;
    IfAction ifAction = (IfAction)o;
    return ifAction.isActive();
  }
  
  public void end(InterpretationContext ic, String name) throws ActionException
  {
    if (!weAreActive(ic)) { return;
    }
    ThenActionState state = (ThenActionState)stateStack.pop();
    if (isRegistered) {
      ic.removeInPlayListener(state);
      Object o = ic.peekObject();
      if ((o instanceof IfAction)) {
        IfAction ifAction = (IfAction)o;
        removeFirstAndLastFromList(eventList);
        registerEventList(ifAction, eventList);
      } else {
        throw new IllegalStateException("Missing IfAction on top of stack");
      }
    }
  }
  
  abstract void registerEventList(IfAction paramIfAction, List<SaxEvent> paramList);
  
  void removeFirstAndLastFromList(List<SaxEvent> eventList) {
    eventList.remove(0);
    eventList.remove(eventList.size() - 1);
  }
}
