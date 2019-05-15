package net.minecraft.server.management;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LowerStringMap implements Map
{
  private final Map internalMap = Maps.newLinkedHashMap();
  private static final String __OBFID = "CL_00001488";
  
  public LowerStringMap() {}
  
  public int size() { return internalMap.size(); }
  

  public boolean isEmpty()
  {
    return internalMap.isEmpty();
  }
  
  public boolean containsKey(Object p_containsKey_1_)
  {
    return internalMap.containsKey(p_containsKey_1_.toString().toLowerCase());
  }
  
  public boolean containsValue(Object p_containsValue_1_)
  {
    return internalMap.containsKey(p_containsValue_1_);
  }
  
  public Object get(Object p_get_1_)
  {
    return internalMap.get(p_get_1_.toString().toLowerCase());
  }
  
  public Object put(String p_put_1_, Object p_put_2_)
  {
    return internalMap.put(p_put_1_.toLowerCase(), p_put_2_);
  }
  
  public Object remove(Object p_remove_1_)
  {
    return internalMap.remove(p_remove_1_.toString().toLowerCase());
  }
  
  public void putAll(Map p_putAll_1_)
  {
    Iterator var2 = p_putAll_1_.entrySet().iterator();
    
    while (var2.hasNext())
    {
      Map.Entry var3 = (Map.Entry)var2.next();
      put((String)var3.getKey(), var3.getValue());
    }
  }
  
  public void clear()
  {
    internalMap.clear();
  }
  
  public Set keySet()
  {
    return internalMap.keySet();
  }
  
  public Collection values()
  {
    return internalMap.values();
  }
  
  public Set entrySet()
  {
    return internalMap.entrySet();
  }
  
  public Object put(Object p_put_1_, Object p_put_2_)
  {
    return put((String)p_put_1_, p_put_2_);
  }
}
