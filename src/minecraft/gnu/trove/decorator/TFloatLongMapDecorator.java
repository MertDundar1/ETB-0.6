package gnu.trove.decorator;

import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
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












































public class TFloatLongMapDecorator
  extends AbstractMap<Float, Long>
  implements Map<Float, Long>, Externalizable, Cloneable
{
  protected TFloatLongMap _map;
  
  public TFloatLongMapDecorator() {}
  
  public TFloatLongMapDecorator(TFloatLongMap map)
  {
    _map = map;
  }
  





  public TFloatLongMap getMap()
  {
    return _map;
  }
  



  public Long put(Float key, Long value)
  {
    float k;
    


    float k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    long v;
    long v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    long retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Long get(Object key)
  {
    float k;
    


    if (key != null) { float k;
      if ((key instanceof Float)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    long v = _map.get(k);
    


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
    float k;
    


    if (key != null) { float k;
      if ((key instanceof Float)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    long v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Float, Long>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TFloatLongMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Float, Long>> iterator()
      {
        new Iterator() {
          private final TFloatLongIterator it = _map.iterator();
          
          public Map.Entry<Float, Long> next() {
            it.advance();
            final Float key = wrapKey(it.key());
            final Long v = wrapValue(it.value());
            new Map.Entry() {
              private Long val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Float getKey()
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
      
      public boolean add(Map.Entry<Float, Long> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Float, Long>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TFloatLongMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Long)) && (_map.containsValue(unwrapValue(val)));
  }
  






  public boolean containsKey(Object key)
  {
    if (key == null) return _map.containsKey(_map.getNoEntryKey());
    return ((key instanceof Float)) && (_map.containsKey(unwrapKey(key)));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Float, ? extends Long> map)
  {
    Iterator<? extends Map.Entry<? extends Float, ? extends Long>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Float, ? extends Long> e = (Map.Entry)it.next();
      put((Float)e.getKey(), (Long)e.getValue());
    }
  }
  






  protected Float wrapKey(float k)
  {
    return Float.valueOf(k);
  }
  






  protected float unwrapKey(Object key)
  {
    return ((Float)key).floatValue();
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
    

    _map = ((TFloatLongMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
