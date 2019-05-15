package gnu.trove.decorator;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
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














































public class TObjectFloatMapDecorator<K>
  extends AbstractMap<K, Float>
  implements Map<K, Float>, Externalizable, Cloneable
{
  protected TObjectFloatMap<K> _map;
  
  public TObjectFloatMapDecorator() {}
  
  public TObjectFloatMapDecorator(TObjectFloatMap<K> map)
  {
    _map = map;
  }
  





  public TObjectFloatMap<K> getMap()
  {
    return _map;
  }
  








  public Float put(K key, Float value)
  {
    if (value == null) return wrapValue(_map.put(key, _map.getNoEntryValue()));
    return wrapValue(_map.put(key, unwrapValue(value)));
  }
  






  public Float get(Object key)
  {
    float v = _map.get(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  






  public Float remove(Object key)
  {
    float v = _map.remove(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<K, Float>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TObjectFloatMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<K, Float>> iterator()
      {
        new Iterator() {
          private final TObjectFloatIterator<K> it = _map.iterator();
          
          public Map.Entry<K, Float> next() {
            it.advance();
            final K key = it.key();
            final Float v = wrapValue(it.value());
            new Map.Entry() {
              private Float val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public K getKey()
              {
                return key;
              }
              
              public Float getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Float setValue(Float value) {
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
      
      public boolean add(Map.Entry<K, Float> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          K key = ((Map.Entry)o).getKey();
          _map.remove(key);
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<K, Float>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TObjectFloatMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Float)) && (_map.containsValue(unwrapValue(val)));
  }
  






  public boolean containsKey(Object key)
  {
    return _map.containsKey(key);
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return _map.size() == 0;
  }
  







  public void putAll(Map<? extends K, ? extends Float> map)
  {
    Iterator<? extends Map.Entry<? extends K, ? extends Float>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends K, ? extends Float> e = (Map.Entry)it.next();
      put(e.getKey(), (Float)e.getValue());
    }
  }
  






  protected Float wrapValue(float k)
  {
    return Float.valueOf(k);
  }
  






  protected float unwrapValue(Object value)
  {
    return ((Float)value).floatValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TObjectFloatMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
