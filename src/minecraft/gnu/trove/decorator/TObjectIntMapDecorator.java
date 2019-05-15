package gnu.trove.decorator;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
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














































public class TObjectIntMapDecorator<K>
  extends AbstractMap<K, Integer>
  implements Map<K, Integer>, Externalizable, Cloneable
{
  protected TObjectIntMap<K> _map;
  
  public TObjectIntMapDecorator() {}
  
  public TObjectIntMapDecorator(TObjectIntMap<K> map)
  {
    _map = map;
  }
  





  public TObjectIntMap<K> getMap()
  {
    return _map;
  }
  








  public Integer put(K key, Integer value)
  {
    if (value == null) return wrapValue(_map.put(key, _map.getNoEntryValue()));
    return wrapValue(_map.put(key, unwrapValue(value)));
  }
  






  public Integer get(Object key)
  {
    int v = _map.get(key);
    


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
    int v = _map.remove(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<K, Integer>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TObjectIntMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<K, Integer>> iterator()
      {
        new Iterator() {
          private final TObjectIntIterator<K> it = _map.iterator();
          
          public Map.Entry<K, Integer> next() {
            it.advance();
            final K key = it.key();
            final Integer v = wrapValue(it.value());
            new Map.Entry() {
              private Integer val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public K getKey()
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
      
      public boolean add(Map.Entry<K, Integer> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<K, Integer>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TObjectIntMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Integer)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends K, ? extends Integer> map)
  {
    Iterator<? extends Map.Entry<? extends K, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends K, ? extends Integer> e = (Map.Entry)it.next();
      put(e.getKey(), (Integer)e.getValue());
    }
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
    


    _map = ((TObjectIntMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
