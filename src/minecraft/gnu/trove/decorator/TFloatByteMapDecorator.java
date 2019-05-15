package gnu.trove.decorator;

import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
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












































public class TFloatByteMapDecorator
  extends AbstractMap<Float, Byte>
  implements Map<Float, Byte>, Externalizable, Cloneable
{
  protected TFloatByteMap _map;
  
  public TFloatByteMapDecorator() {}
  
  public TFloatByteMapDecorator(TFloatByteMap map)
  {
    _map = map;
  }
  





  public TFloatByteMap getMap()
  {
    return _map;
  }
  



  public Byte put(Float key, Byte value)
  {
    float k;
    


    float k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    byte v;
    byte v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    byte retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Byte get(Object key)
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
    byte v = _map.get(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  



  public Byte remove(Object key)
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
    byte v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Float, Byte>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TFloatByteMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Float, Byte>> iterator()
      {
        new Iterator() {
          private final TFloatByteIterator it = _map.iterator();
          
          public Map.Entry<Float, Byte> next() {
            it.advance();
            final Float key = wrapKey(it.key());
            final Byte v = wrapValue(it.value());
            new Map.Entry() {
              private Byte val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Float getKey()
              {
                return key;
              }
              
              public Byte getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Byte setValue(Byte value) {
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
      
      public boolean add(Map.Entry<Float, Byte> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Float, Byte>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TFloatByteMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Byte)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends Float, ? extends Byte> map)
  {
    Iterator<? extends Map.Entry<? extends Float, ? extends Byte>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Float, ? extends Byte> e = (Map.Entry)it.next();
      put((Float)e.getKey(), (Byte)e.getValue());
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
  






  protected Byte wrapValue(byte k)
  {
    return Byte.valueOf(k);
  }
  






  protected byte unwrapValue(Object value)
  {
    return ((Byte)value).byteValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _map = ((TFloatByteMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
