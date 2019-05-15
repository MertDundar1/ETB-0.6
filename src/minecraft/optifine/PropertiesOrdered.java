package optifine;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class PropertiesOrdered extends Properties
{
  private Set<Object> keysOrdered = new LinkedHashSet();
  
  public PropertiesOrdered() {}
  
  public synchronized Object put(Object key, Object value) { keysOrdered.add(key);
    return super.put(key, value);
  }
  
  public Set<Object> keySet()
  {
    Set keysParent = super.keySet();
    keysOrdered.retainAll(keysParent);
    return Collections.unmodifiableSet(keysOrdered);
  }
  
  public synchronized Enumeration<Object> keys()
  {
    return Collections.enumeration(keySet());
  }
}
