package ch.qos.logback.core.spi;

import ch.qos.logback.core.helpers.CyclicBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


















public class CyclicBufferTracker<E>
  extends AbstractComponentTracker<CyclicBuffer<E>>
{
  static final int DEFAULT_NUMBER_OF_BUFFERS = 64;
  static final int DEFAULT_BUFFER_SIZE = 256;
  int bufferSize = 256;
  

  public CyclicBufferTracker()
  {
    setMaxComponents(64);
  }
  
  public int getBufferSize() {
    return bufferSize;
  }
  
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }
  
  protected void processPriorToRemoval(CyclicBuffer<E> component)
  {
    component.clear();
  }
  
  protected CyclicBuffer<E> buildComponent(String key)
  {
    return new CyclicBuffer(bufferSize);
  }
  
  protected boolean isComponentStale(CyclicBuffer<E> eCyclicBuffer)
  {
    return false;
  }
  
  List<String> liveKeysAsOrderedList()
  {
    return new ArrayList(liveMap.keySet());
  }
  
  List<String> lingererKeysAsOrderedList() {
    return new ArrayList(lingerersMap.keySet());
  }
}
