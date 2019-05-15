package gnu.trove.decorator;

import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
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












































public class TLongLongMapDecorator
  extends AbstractMap<Long, Long>
  implements Map<Long, Long>, Externalizable, Cloneable
{
  protected TLongLongMap _map;
  
  public TLongLongMapDecorator() {}
  
  public TLongLongMapDecorator(TLongLongMap map)
  {
    _map = map;
  }
  





  public TLongLongMap getMap()
  {
    return _map;
  }
  



  public Long put(Long key, Long value)
  {
    long k;
    


    long k;
    

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
    long k;
    


    if (key != null) { long k;
      if ((key instanceof Long)) {
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
    long k;
    


    if (key != null) { long k;
      if ((key instanceof Long)) {
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
  






  public Set<Map.Entry<Long, Long>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TLongLongMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Long, Long>> iterator()
      {
        new Iterator() {
          private final TLongLongIterator it = _map.iterator();
          
          public Map.Entry<Long, Long> next() {
            it.advance();
            final Long key = wrapKey(it.key());
            final Long v = wrapValue(it.value());
            new Map.Entry() {
              private Long val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Long getKey()
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
      
      public boolean add(Map.Entry<Long, Long> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Long key = (Long)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Long, Long>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TLongLongMapDecorator.this.clear();
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
    return ((key instanceof Long)) && (_map.containsKey(unwrapKey(key)));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Long, ? extends Long> map)
  {
    Iterator<? extends Map.Entry<? extends Long, ? extends Long>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Long, ? extends Long> e = (Map.Entry)it.next();
      put((Long)e.getKey(), (Long)e.getValue());
    }
  }
  






  protected Long wrapKey(long k)
  {
    return Long.valueOf(k);
  }
  






  protected long unwrapKey(Object key)
  {
    return ((Long)key).longValue();
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
    

    _map = ((TLongLongMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
