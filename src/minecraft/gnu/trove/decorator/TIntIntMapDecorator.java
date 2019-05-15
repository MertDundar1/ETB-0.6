package gnu.trove.decorator;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
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












































public class TIntIntMapDecorator
  extends AbstractMap<Integer, Integer>
  implements Map<Integer, Integer>, Externalizable, Cloneable
{
  protected TIntIntMap _map;
  
  public TIntIntMapDecorator() {}
  
  public TIntIntMapDecorator(TIntIntMap map)
  {
    _map = map;
  }
  





  public TIntIntMap getMap()
  {
    return _map;
  }
  



  public Integer put(Integer key, Integer value)
  {
    int k;
    


    int k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    int v;
    int v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    int retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Integer get(Object key)
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
    int v = _map.get(k);
    


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
    int v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Integer, Integer>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TIntIntMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Integer, Integer>> iterator()
      {
        new Iterator() {
          private final TIntIntIterator it = _map.iterator();
          
          public Map.Entry<Integer, Integer> next() {
            it.advance();
            final Integer key = wrapKey(it.key());
            final Integer v = wrapValue(it.value());
            new Map.Entry() {
              private Integer val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Integer getKey()
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
      
      public boolean add(Map.Entry<Integer, Integer> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Integer, Integer>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TIntIntMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Integer)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends Integer, ? extends Integer> map)
  {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Integer>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Integer, ? extends Integer> e = (Map.Entry)it.next();
      put((Integer)e.getKey(), (Integer)e.getValue());
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
    

    _map = ((TIntIntMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
