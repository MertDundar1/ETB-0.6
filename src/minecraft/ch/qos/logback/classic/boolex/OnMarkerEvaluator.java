package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Marker;



















public class OnMarkerEvaluator
  extends EventEvaluatorBase<ILoggingEvent>
{
  public OnMarkerEvaluator() {}
  
  List<String> markerList = new ArrayList();
  
  public void addMarker(String markerStr) {
    markerList.add(markerStr);
  }
  




  public boolean evaluate(ILoggingEvent event)
    throws NullPointerException, EvaluationException
  {
    Marker eventsMarker = event.getMarker();
    if (eventsMarker == null) {
      return false;
    }
    
    for (String markerStr : markerList) {
      if (eventsMarker.contains(markerStr)) {
        return true;
      }
    }
    return false;
  }
}
