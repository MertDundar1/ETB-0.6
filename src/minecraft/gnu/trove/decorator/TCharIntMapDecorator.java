package gnu.trove.decorator;

import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;












































public class TCharIntMapDecorator
  extends AbstractMap<Character, Integer>
  implements Map<Character, Integer>, Externalizable, Cloneable
{
  protected TCharIntMap _map;
  
  public TCharIntMapDecorator() {}
  
  public TCharIntMapDecorator(TCharIntMap map)
  {
    _map = map;
  }
  





  public TCharIntMap getMap()
  {
    return _map;
  }
  



  public Integer put(Character key, Integer value)
  {
    char k;
    


    char k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    int v;
    int v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    int retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Integer get(Object key)
  {
    char k;
    


    if (key != null) { char k;
      if ((key instanceof Character)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    int v = _map.get(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  



  public Integer remove(Object key)
  {
    char k;
    


    if (key != null) { char k;
      if ((key instanceof Character)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    int v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Character, Integer>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TCharIntMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Character, Integer>> iterator()
      {
        new Iterator() {
          private final TCharIntIterator it = _map.iterator();
          
          public Map.Entry<Character, Integer> next() {
            it.advance();
            final Character key = wrapKey(it.key());
            final Integer v = wrapValue(it.value());
            new Map.Entry() {
              private Integer val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Character getKey()
              {
                return key;
              }
              
              public Integer getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Integer setValue(Integer value) {
                val = value;
                return put(key, value);
              }
            };
          }
          
          public boolean hasNext() {
            return it.hasNext();
          }
          
          public void remove() {
            it.remove();
          }
        };
      }
      
      public boolean add(Map.Entry<Character, Integer> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Character key = (Character)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Character, Integer>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TCharIntMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Integer)) && (_map.containsValue(unwrapValue(val)));
  }
  






  public boolean containsKey(Object key)
  {
    if (key == null) return _map.containsKey(_map.getNoEntryKey());
    return ((key instanceof Character)) && (_map.containsKey(unwrapKey(key)));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Character, ? extends Integer> map)
  {
    Iterator<? extends Map.Entry<? extends Character, ? extends Integer>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Character, ? extends Integer> e = (Map.Entry)it.next();
      put((Character)e.getKey(), (Integer)e.getValue());
    }
  }
  






  protected Character wrapKey(char k)
  {
    return Character.valueOf(k);
  }
  






  protected char unwrapKey(Object key)
  {
    return ((Character)key).charValue();
  }
  






  protected Integer wrapValue(int k)
  {
    return Integer.valueOf(k);
  }
  






  protected int unwrapValue(Object value)
  {
    return ((Integer)value).intValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _map = ((TCharIntMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
