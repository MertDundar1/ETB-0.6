package ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.List;

















public class StatusListenerAsList
  implements StatusListener
{
  public StatusListenerAsList() {}
  
  List<Status> statusList = new ArrayList();
  
  public void addStatusEvent(Status status) {
    statusList.add(status);
  }
  
  public List<Status> getStatusList() {
    return statusList;
  }
}
