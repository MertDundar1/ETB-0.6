package gnu.trove.decorator;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
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












































public class TIntObjectMapDecorator<V>
  extends AbstractMap<Integer, V>
  implements Map<Integer, V>, Externalizable, Cloneable
{
  protected TIntObjectMap<V> _map;
  
  public TIntObjectMapDecorator() {}
  
  public TIntObjectMapDecorator(TIntObjectMap<V> map)
  {
    _map = map;
  }
  





  public TIntObjectMap<V> getMap()
  {
    return _map;
  }
  



  public V put(Integer key, V value)
  {
    int k;
    

    int k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    }
    return _map.put(k, value);
  }
  



  public V get(Object key)
  {
    int k;
    


    if (key != null) { int k;
      if ((key instanceof Integer)) {
        k = unwrapKey((Integer)key);
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
    int k;
    


    if (key != null) { int k;
      if ((key instanceof Integer)) {
        k = unwrapKey((Integer)key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    return _map.remove(k);
  }
  





  public Set<Map.Entry<Integer, V>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TIntObjectMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Integer, V>> iterator()
      {
        new Iterator() {
          private final TIntObjectIterator<V> it = _map.iterator();
          
          public Map.Entry<Integer, V> next() {
            it.advance();
            final Integer key = wrapKey(it.key());
            final V v = it.value();
            new Map.Entry() {
              private V val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Integer getKey()
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
      
      public boolean add(Map.Entry<Integer, V> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Integer key = (Integer)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Integer, V>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TIntObjectMapDecorator.this.clear();
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
    return ((key instanceof Integer)) && (_map.containsKey(((Integer)key).intValue()));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Integer, ? extends V> map)
  {
    Iterator<? extends Map.Entry<? extends Integer, ? extends V>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Integer, ? extends V> e = (Map.Entry)it.next();
      put((Integer)e.getKey(), e.getValue());
    }
  }
  






  protected Integer wrapKey(int k)
  {
    return Integer.valueOf(k);
  }
  






  protected int unwrapKey(Integer key)
  {
    return key.intValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TIntObjectMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
