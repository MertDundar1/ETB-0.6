package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
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












































public class TDoubleShortMapDecorator
  extends AbstractMap<Double, Short>
  implements Map<Double, Short>, Externalizable, Cloneable
{
  protected TDoubleShortMap _map;
  
  public TDoubleShortMapDecorator() {}
  
  public TDoubleShortMapDecorator(TDoubleShortMap map)
  {
    _map = map;
  }
  





  public TDoubleShortMap getMap()
  {
    return _map;
  }
  



  public Short put(Double key, Short value)
  {
    double k;
    


    double k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    short v;
    short v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    short retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Short get(Object key)
  {
    double k;
    


    if (key != null) { double k;
      if ((key instanceof Double)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    short v = _map.get(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  



  public Short remove(Object key)
  {
    double k;
    


    if (key != null) { double k;
      if ((key instanceof Double)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    short v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Double, Short>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TDoubleShortMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Double, Short>> iterator()
      {
        new Iterator() {
          private final TDoubleShortIterator it = _map.iterator();
          
          public Map.Entry<Double, Short> next() {
            it.advance();
            final Double key = wrapKey(it.key());
            final Short v = wrapValue(it.value());
            new Map.Entry() {
              private Short val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Double getKey()
              {
                return key;
              }
              
              public Short getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Short setValue(Short value) {
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
      
      public boolean add(Map.Entry<Double, Short> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Double, Short>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TDoubleShortMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Short)) && (_map.containsValue(unwrapValue(val)));
  }
  






  public boolean containsKey(Object key)
  {
    if (key == null) return _map.containsKey(_map.getNoEntryKey());
    return ((key instanceof Double)) && (_map.containsKey(unwrapKey(key)));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Double, ? extends Short> map)
  {
    Iterator<? extends Map.Entry<? extends Double, ? extends Short>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Double, ? extends Short> e = (Map.Entry)it.next();
      put((Double)e.getKey(), (Short)e.getValue());
    }
  }
  






  protected Double wrapKey(double k)
  {
    return Double.valueOf(k);
  }
  






  protected double unwrapKey(Object key)
  {
    return ((Double)key).doubleValue();
  }
  






  protected Short wrapValue(short k)
  {
    return Short.valueOf(k);
  }
  






  protected short unwrapValue(Object value)
  {
    return ((Short)value).shortValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _map = ((TDoubleShortMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
