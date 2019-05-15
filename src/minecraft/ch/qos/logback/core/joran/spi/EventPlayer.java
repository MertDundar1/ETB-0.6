package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import java.util.ArrayList;
import java.util.List;














public class EventPlayer
{
  final Interpreter interpreter;
  List<SaxEvent> eventList;
  int currentIndex;
  
  public EventPlayer(Interpreter interpreter)
  {
    this.interpreter = interpreter;
  }
  




  public List<SaxEvent> getCopyOfPlayerEventList()
  {
    return new ArrayList(eventList);
  }
  
  public void play(List<SaxEvent> aSaxEventList) {
    eventList = aSaxEventList;
    
    for (currentIndex = 0; currentIndex < eventList.size(); currentIndex += 1) {
      SaxEvent se = (SaxEvent)eventList.get(currentIndex);
      
      if ((se instanceof StartEvent)) {
        interpreter.startElement((StartEvent)se);
        
        interpreter.getInterpretationContext().fireInPlay(se);
      }
      if ((se instanceof BodyEvent))
      {
        interpreter.getInterpretationContext().fireInPlay(se);
        interpreter.characters((BodyEvent)se);
      }
      if ((se instanceof EndEvent))
      {
        interpreter.getInterpretationContext().fireInPlay(se);
        interpreter.endElement((EndEvent)se);
      }
    }
  }
  
  public void addEventsDynamically(List<SaxEvent> eventList, int offset)
  {
    this.eventList.addAll(currentIndex + offset, eventList);
  }
}
