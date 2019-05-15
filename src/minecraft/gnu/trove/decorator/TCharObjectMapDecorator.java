package gnu.trove.decorator;

import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
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












































public class TCharObjectMapDecorator<V>
  extends AbstractMap<Character, V>
  implements Map<Character, V>, Externalizable, Cloneable
{
  protected TCharObjectMap<V> _map;
  
  public TCharObjectMapDecorator() {}
  
  public TCharObjectMapDecorator(TCharObjectMap<V> map)
  {
    _map = map;
  }
  





  public TCharObjectMap<V> getMap()
  {
    return _map;
  }
  



  public V put(Character key, V value)
  {
    char k;
    

    char k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    }
    return _map.put(k, value);
  }
  



  public V get(Object key)
  {
    char k;
    


    if (key != null) { char k;
      if ((key instanceof Character)) {
        k = unwrapKey((Character)key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    return _map.get(k);
  }
  



  public void clear()
  {
    _map.clear();
  }
  



  public V remove(Object key)
  {
    char k;
    


    if (key != null) { char k;
      if ((key instanceof Character)) {
        k = unwrapKey((Character)key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    return _map.remove(k);
  }
  





  public Set<Map.Entry<Character, V>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TCharObjectMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Character, V>> iterator()
      {
        new Iterator() {
          private final TCharObjectIterator<V> it = _map.iterator();
          
          public Map.Entry<Character, V> next() {
            it.advance();
            final Character key = wrapKey(it.key());
            final V v = it.value();
            new Map.Entry() {
              private V val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Character getKey()
              {
                return key;
              }
              
              public V getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public V setValue(V value) {
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
      
      public boolean add(Map.Entry<Character, V> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Character, V>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TCharObjectMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return _map.containsValue(val);
  }
  






  public boolean containsKey(Object key)
  {
    if (key == null) return _map.containsKey(_map.getNoEntryKey());
    return ((key instanceof Character)) && (_map.containsKey(((Character)key).charValue()));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Character, ? extends V> map)
  {
    Iterator<? extends Map.Entry<? extends Character, ? extends V>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Character, ? extends V> e = (Map.Entry)it.next();
      put((Character)e.getKey(), e.getValue());
    }
  }
  






  protected Character wrapKey(char k)
  {
    return Character.valueOf(k);
  }
  






  protected char unwrapKey(Character key)
  {
    return key.charValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TCharObjectMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
