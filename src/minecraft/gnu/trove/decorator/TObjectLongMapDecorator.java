package gnu.trove.decorator;

import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
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














































public class TObjectLongMapDecorator<K>
  extends AbstractMap<K, Long>
  implements Map<K, Long>, Externalizable, Cloneable
{
  protected TObjectLongMap<K> _map;
  
  public TObjectLongMapDecorator() {}
  
  public TObjectLongMapDecorator(TObjectLongMap<K> map)
  {
    _map = map;
  }
  





  public TObjectLongMap<K> getMap()
  {
    return _map;
  }
  








  public Long put(K key, Long value)
  {
    if (value == null) return wrapValue(_map.put(key, _map.getNoEntryValue()));
    return wrapValue(_map.put(key, unwrapValue(value)));
  }
  






  public Long get(Object key)
  {
    long v = _map.get(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  






  public Long remove(Object key)
  {
    long v = _map.remove(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<K, Long>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TObjectLongMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<K, Long>> iterator()
      {
        new Iterator() {
          private final TObjectLongIterator<K> it = _map.iterator();
          
          public Map.Entry<K, Long> next() {
            it.advance();
            final K key = it.key();
            final Long v = wrapValue(it.value());
            new Map.Entry() {
              private Long val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public K getKey()
              {
                return key;
              }
              
              public Long getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Long setValue(Long value) {
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
      
      public boolean add(Map.Entry<K, Long> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<K, Long>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TObjectLongMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Long)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends K, ? extends Long> map)
  {
    Iterator<? extends Map.Entry<? extends K, ? extends Long>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends K, ? extends Long> e = (Map.Entry)it.next();
      put(e.getKey(), (Long)e.getValue());
    }
  }
  






  protected Long wrapValue(long k)
  {
    return Long.valueOf(k);
  }
  






  protected long unwrapValue(Object value)
  {
    return ((Long)value).longValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TObjectLongMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
