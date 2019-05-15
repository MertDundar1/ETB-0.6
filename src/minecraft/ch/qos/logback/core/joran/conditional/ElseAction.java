package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;












public class ElseAction
  extends ThenOrElseActionBase
{
  public ElseAction() {}
  
  void registerEventList(IfAction ifAction, List<SaxEvent> eventList)
  {
    ifAction.setElseSaxEventList(eventList);
  }
}
