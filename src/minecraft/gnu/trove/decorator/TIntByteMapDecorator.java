package gnu.trove.decorator;

import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.map.TIntByteMap;
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












































public class TIntByteMapDecorator
  extends AbstractMap<Integer, Byte>
  implements Map<Integer, Byte>, Externalizable, Cloneable
{
  protected TIntByteMap _map;
  
  public TIntByteMapDecorator() {}
  
  public TIntByteMapDecorator(TIntByteMap map)
  {
    _map = map;
  }
  





  public TIntByteMap getMap()
  {
    return _map;
  }
  



  public Byte put(Integer key, Byte value)
  {
    int k;
    


    int k;
    

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
    int k;
    


    if (key != null) { int k;
      if ((key instanceof Integer)) {
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
    int k;
    


    if (key != null) { int k;
      if ((key instanceof Integer)) {
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
  






  public Set<Map.Entry<Integer, Byte>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TIntByteMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Integer, Byte>> iterator()
      {
        new Iterator() {
          private final TIntByteIterator it = _map.iterator();
          
          public Map.Entry<Integer, Byte> next() {
            it.advance();
            final Integer key = wrapKey(it.key());
            final Byte v = wrapValue(it.value());
            new Map.Entry() {
              private Byte val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Integer getKey()
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
      
      public boolean add(Map.Entry<Integer, Byte> o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        boolean modified = false;
        if (contains(o))
        {
          Integer key = (Integer)((Map.Entry)o).getKey();
          _map.remove(unwrapKey(key));
          modified = true;
        }
        return modified;
      }
      
      public boolean addAll(Collection<? extends Map.Entry<Integer, Byte>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TIntByteMapDecorator.this.clear();
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
    return ((key instanceof Integer)) && (_map.containsKey(unwrapKey(key)));
  }
  





  public int size()
  {
    return _map.size();
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public void putAll(Map<? extends Integer, ? extends Byte> map)
  {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Byte>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Integer, ? extends Byte> e = (Map.Entry)it.next();
      put((Integer)e.getKey(), (Byte)e.getValue());
    }
  }
  






  protected Integer wrapKey(int k)
  {
    return Integer.valueOf(k);
  }
  






  protected int unwrapKey(Object key)
  {
    return ((Integer)key).intValue();
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
    

    _map = ((TIntByteMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
