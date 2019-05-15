package gnu.trove.decorator;

import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
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












































public class TIntShortMapDecorator
  extends AbstractMap<Integer, Short>
  implements Map<Integer, Short>, Externalizable, Cloneable
{
  protected TIntShortMap _map;
  
  public TIntShortMapDecorator() {}
  
  public TIntShortMapDecorator(TIntShortMap map)
  {
    _map = map;
  }
  





  public TIntShortMap getMap()
  {
    return _map;
  }
  



  public Short put(Integer key, Short value)
  {
    int k;
    


    int k;
    

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
    short v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Integer, Short>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TIntShortMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Integer, Short>> iterator()
      {
        new Iterator() {
          private final TIntShortIterator it = _map.iterator();
          
          public Map.Entry<Integer, Short> next() {
            it.advance();
            final Integer key = wrapKey(it.key());
            final Short v = wrapValue(it.value());
            new Map.Entry() {
              private Short val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Integer getKey()
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
      
      public boolean add(Map.Entry<Integer, Short> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Integer, Short>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TIntShortMapDecorator.this.clear();
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
  







  public void putAll(Map<? extends Integer, ? extends Short> map)
  {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Short>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Integer, ? extends Short> e = (Map.Entry)it.next();
      put((Integer)e.getKey(), (Short)e.getValue());
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
    

    _map = ((TIntShortMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
