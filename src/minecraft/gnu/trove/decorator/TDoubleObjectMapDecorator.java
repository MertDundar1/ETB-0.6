package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
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












































public class TDoubleObjectMapDecorator<V>
  extends AbstractMap<Double, V>
  implements Map<Double, V>, Externalizable, Cloneable
{
  protected TDoubleObjectMap<V> _map;
  
  public TDoubleObjectMapDecorator() {}
  
  public TDoubleObjectMapDecorator(TDoubleObjectMap<V> map)
  {
    _map = map;
  }
  





  public TDoubleObjectMap<V> getMap()
  {
    return _map;
  }
  



  public V put(Double key, V value)
  {
    double k;
    

    double k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    }
    return _map.put(k, value);
  }
  



  public V get(Object key)
  {
    double k;
    


    if (key != null) { double k;
      if ((key instanceof Double)) {
        k = unwrapKey((Double)key);
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
    double k;
    


    if (key != null) { double k;
      if ((key instanceof Double)) {
        k = unwrapKey((Double)key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    return _map.remove(k);
  }
  





  public Set<Map.Entry<Double, V>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TDoubleObjectMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Double, V>> iterator()
      {
        new Iterator() {
          private final TDoubleObjectIterator<V> it = _map.iterator();
          
          public Map.Entry<Double, V> next() {
            it.advance();
            final Double key = wrapKey(it.key());
            final V v = it.value();
            new Map.Entry() {
              private V val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Double getKey()
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
      
      public boolean add(Map.Entry<Double, V> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Double key = (Double)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Double, V>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TDoubleObjectMapDecorator.this.clear();
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
    return ((key instanceof Double)) && (_map.containsKey(((Double)key).doubleValue()));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Double, ? extends V> map)
  {
    Iterator<? extends Map.Entry<? extends Double, ? extends V>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Double, ? extends V> e = (Map.Entry)it.next();
      put((Double)e.getKey(), e.getValue());
    }
  }
  






  protected Double wrapKey(double k)
  {
    return Double.valueOf(k);
  }
  






  protected double unwrapKey(Double key)
  {
    return key.doubleValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TDoubleObjectMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
