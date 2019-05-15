package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
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












































public class TDoubleCharMapDecorator
  extends AbstractMap<Double, Character>
  implements Map<Double, Character>, Externalizable, Cloneable
{
  protected TDoubleCharMap _map;
  
  public TDoubleCharMapDecorator() {}
  
  public TDoubleCharMapDecorator(TDoubleCharMap map)
  {
    _map = map;
  }
  





  public TDoubleCharMap getMap()
  {
    return _map;
  }
  



  public Character put(Double key, Character value)
  {
    double k;
    


    double k;
    

    if (key == null) {
      k = _map.getNoEntryKey();
    } else
      k = unwrapKey(key);
    char v;
    char v; if (value == null) {
      v = _map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    }
    char retval = _map.put(k, v);
    if (retval == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(retval);
  }
  



  public Character get(Object key)
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
    char v = _map.get(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  




  public void clear()
  {
    _map.clear();
  }
  



  public Character remove(Object key)
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
    char v = _map.remove(k);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<Double, Character>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TDoubleCharMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<Double, Character>> iterator()
      {
        new Iterator() {
          private final TDoubleCharIterator it = _map.iterator();
          
          public Map.Entry<Double, Character> next() {
            it.advance();
            final Double key = wrapKey(it.key());
            final Character v = wrapValue(it.value());
            new Map.Entry() {
              private Character val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public Double getKey()
              {
                return key;
              }
              
              public Character getValue() {
                return val;
              }
              
              public int hashCode() {
                return key.hashCode() + val.hashCode();
              }
              
              public Character setValue(Character value) {
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
      
      public boolean add(Map.Entry<Double, Character> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<Double, Character>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TDoubleCharMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Character)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends Double, ? extends Character> map)
  {
    Iterator<? extends Map.Entry<? extends Double, ? extends Character>> it = map.entrySet().iterator();
    
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends Double, ? extends Character> e = (Map.Entry)it.next();
      put((Double)e.getKey(), (Character)e.getValue());
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
  






  protected Character wrapValue(char k)
  {
    return Character.valueOf(k);
  }
  






  protected char unwrapValue(Object value)
  {
    return ((Character)value).charValue();
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _map = ((TDoubleCharMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
