package gnu.trove.decorator;

import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
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












































public class TShortFloatMapDecorator
  extends AbstractMap<Short, Float>
  implements Map<Short, Float>, Externalizable, Cloneable
{
  protected TShortFloatMap _map;
  
  public TShortFloatMapDecorator() {}
  
  public TShortFloatMapDecorator(TShortFloatMap map)
  {
    _map = map;
  }
  





  public TShortFloatMap getMap()
  {
    return _map;
  }
  



  public Float put(Short key, Float value)
  {
    short k;
    


    short k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    float v;
    float v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    float retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Float get(Object key)
  {
    short k;
    


    if (key != null) { short k;
      if ((key instanceof Short)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    float v = _map.get(k);
    


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
    short k;
    


    if (key != null) { short k;
      if ((key instanceof Short)) {
        k = unwrapKey(key);
      } else {
        return null;
      }
    } else {
      k = _map.getNoEntryKey();
    }
    float v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Short, Float>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TShortFloatMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Short, Float>> iterator()
      {
        new Iterator() {
          private final TShortFloatIterator it = _map.iterator();
          
          public Map.Entry<Short, Float> next() {
            it.advance();
            final Short key = wrapKey(it.key());
            final Float v = wrapValue(it.value());
            new Map.Entry() {
              private Float val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Short getKey()
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
      
      public boolean add(Map.Entry<Short, Float> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Short key = (Short)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Short, Float>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TShortFloatMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Float)) && (_map.containsValue(unwrapValue(val)));
  }
  






  public boolean containsKey(Object key)
  {
    if (key == null) return _map.containsKey(_map.getNoEntryKey());
    return ((key instanceof Short)) && (_map.containsKey(unwrapKey(key)));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Short, ? extends Float> map)
  {
    Iterator<? extends Map.Entry<? extends Short, ? extends Float>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Short, ? extends Float> e = (Map.Entry)it.next();
      put((Short)e.getKey(), (Float)e.getValue());
    }
  }
  






  protected Short wrapKey(short k)
  {
    return Short.valueOf(k);
  }
  






  protected short unwrapKey(Object key)
  {
    return ((Short)key).shortValue();
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
    

    _map = ((TShortFloatMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
