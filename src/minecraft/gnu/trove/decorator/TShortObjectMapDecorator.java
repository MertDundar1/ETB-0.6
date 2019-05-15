package gnu.trove.decorator;

import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
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












































public class TShortObjectMapDecorator<V>
  extends AbstractMap<Short, V>
  implements Map<Short, V>, Externalizable, Cloneable
{
  protected TShortObjectMap<V> _map;
  
  public TShortObjectMapDecorator() {}
  
  public TShortObjectMapDecorator(TShortObjectMap<V> map)
  {
    _map = map;
  }
  





  public TShortObjectMap<V> getMap()
  {
    return _map;
  }
  



  public V put(Short key, V value)
  {
    short k;
    

    short k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    }
    return _map.put(k, value);
  }
  



  public V get(Object key)
  {
    short k;
    


    if (key != null) { short k;
      if ((key instanceof Short)) {
        k = unwrapKey((Short)key);
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
    short k;
    


    if (key != null) { short k;
      if ((key instanceof Short)) {
        k = unwrapKey((Short)key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    return _map.remove(k);
  }
  





  public Set<Map.Entry<Short, V>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TShortObjectMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Short, V>> iterator()
      {
        new Iterator() {
          private final TShortObjectIterator<V> it = _map.iterator();
          
          public Map.Entry<Short, V> next() {
            it.advance();
            final Short key = wrapKey(it.key());
            final V v = it.value();
            new Map.Entry() {
              private V val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Short getKey()
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
      
      public boolean add(Map.Entry<Short, V> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Short key = (Short)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Short, V>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TShortObjectMapDecorator.this.clear();
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
    return ((key instanceof Short)) && (_map.containsKey(((Short)key).shortValue()));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Short, ? extends V> map)
  {
    Iterator<? extends Map.Entry<? extends Short, ? extends V>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Short, ? extends V> e = (Map.Entry)it.next();
      put((Short)e.getKey(), e.getValue());
    }
  }
  






  protected Short wrapKey(short k)
  {
    return Short.valueOf(k);
  }
  






  protected short unwrapKey(Short key)
  {
    return key.shortValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TShortObjectMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
