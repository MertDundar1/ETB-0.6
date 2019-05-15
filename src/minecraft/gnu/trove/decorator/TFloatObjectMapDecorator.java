package gnu.trove.decorator;

import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
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












































public class TFloatObjectMapDecorator<V>
  extends AbstractMap<Float, V>
  implements Map<Float, V>, Externalizable, Cloneable
{
  protected TFloatObjectMap<V> _map;
  
  public TFloatObjectMapDecorator() {}
  
  public TFloatObjectMapDecorator(TFloatObjectMap<V> map)
  {
    _map = map;
  }
  





  public TFloatObjectMap<V> getMap()
  {
    return _map;
  }
  



  public V put(Float key, V value)
  {
    float k;
    

    float k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    }
    return _map.put(k, value);
  }
  



  public V get(Object key)
  {
    float k;
    


    if (key != null) { float k;
      if ((key instanceof Float)) {
        k = unwrapKey((Float)key);
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
    float k;
    


    if (key != null) { float k;
      if ((key instanceof Float)) {
        k = unwrapKey((Float)key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    return _map.remove(k);
  }
  





  public Set<Map.Entry<Float, V>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TFloatObjectMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Float, V>> iterator()
      {
        new Iterator() {
          private final TFloatObjectIterator<V> it = _map.iterator();
          
          public Map.Entry<Float, V> next() {
            it.advance();
            final Float key = wrapKey(it.key());
            final V v = it.value();
            new Map.Entry() {
              private V val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Float getKey()
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
      
      public boolean add(Map.Entry<Float, V> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Float key = (Float)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Float, V>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TFloatObjectMapDecorator.this.clear();
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
    return ((key instanceof Float)) && (_map.containsKey(((Float)key).floatValue()));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Float, ? extends V> map)
  {
    Iterator<? extends Map.Entry<? extends Float, ? extends V>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Float, ? extends V> e = (Map.Entry)it.next();
      put((Float)e.getKey(), e.getValue());
    }
  }
  






  protected Float wrapKey(float k)
  {
    return Float.valueOf(k);
  }
  






  protected float unwrapKey(Float key)
  {
    return key.floatValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TFloatObjectMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
