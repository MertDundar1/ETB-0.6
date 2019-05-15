package gnu.trove.decorator;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
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














































public class TObjectDoubleMapDecorator<K>
  extends AbstractMap<K, Double>
  implements Map<K, Double>, Externalizable, Cloneable
{
  protected TObjectDoubleMap<K> _map;
  
  public TObjectDoubleMapDecorator() {}
  
  public TObjectDoubleMapDecorator(TObjectDoubleMap<K> map)
  {
    _map = map;
  }
  





  public TObjectDoubleMap<K> getMap()
  {
    return _map;
  }
  








  public Double put(K key, Double value)
  {
    if (value == null) return wrapValue(_map.put(key, _map.getNoEntryValue()));
    return wrapValue(_map.put(key, unwrapValue(value)));
  }
  






  public Double get(Object key)
  {
    double v = _map.get(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  






  public Double remove(Object key)
  {
    double v = _map.remove(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<K, Double>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TObjectDoubleMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<K, Double>> iterator()
      {
        new Iterator() {
          private final TObjectDoubleIterator<K> it = _map.iterator();
          
          public Map.Entry<K, Double> next() {
            it.advance();
            final K key = it.key();
            final Double v = wrapValue(it.value());
            new Map.Entry() {
              private Double val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public K getKey()
              {
                return key;
              }
              
              public Double getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Double setValue(Double value) {
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
      
      public boolean add(Map.Entry<K, Double> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<K, Double>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TObjectDoubleMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Double)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends K, ? extends Double> map)
  {
    Iterator<? extends Map.Entry<? extends K, ? extends Double>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends K, ? extends Double> e = (Map.Entry)it.next();
      put(e.getKey(), (Double)e.getValue());
    }
  }
  






  protected Double wrapValue(double k)
  {
    return Double.valueOf(k);
  }
  






  protected double unwrapValue(Object value)
  {
    return ((Double)value).doubleValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    


    _map = ((TObjectDoubleMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
