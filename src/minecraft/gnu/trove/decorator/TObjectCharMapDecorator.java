package gnu.trove.decorator;

import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
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














































public class TObjectCharMapDecorator<K>
  extends AbstractMap<K, Character>
  implements Map<K, Character>, Externalizable, Cloneable
{
  protected TObjectCharMap<K> _map;
  
  public TObjectCharMapDecorator() {}
  
  public TObjectCharMapDecorator(TObjectCharMap<K> map)
  {
    _map = map;
  }
  





  public TObjectCharMap<K> getMap()
  {
    return _map;
  }
  








  public Character put(K key, Character value)
  {
    if (value == null) return wrapValue(_map.put(key, _map.getNoEntryValue()));
    return wrapValue(_map.put(key, unwrapValue(value)));
  }
  






  public Character get(Object key)
  {
    char v = _map.get(key);
    


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
    char v = _map.remove(key);
    


    if (v == _map.getNoEntryValue()) {
      return null;
    }
    return wrapValue(v);
  }
  






  public Set<Map.Entry<K, Character>> entrySet()
  {
    new AbstractSet() {
      public int size() {
        return _map.size();
      }
      
      public boolean isEmpty() {
        return TObjectCharMapDecorator.this.isEmpty();
      }
      
      public boolean contains(Object o) {
        if ((o instanceof Map.Entry)) {
          Object k = ((Map.Entry)o).getKey();
          Object v = ((Map.Entry)o).getValue();
          return (containsKey(k)) && (get(k).equals(v));
        }
        
        return false;
      }
      
      public Iterator<Map.Entry<K, Character>> iterator()
      {
        new Iterator() {
          private final TObjectCharIterator<K> it = _map.iterator();
          
          public Map.Entry<K, Character> next() {
            it.advance();
            final K key = it.key();
            final Character v = wrapValue(it.value());
            new Map.Entry() {
              private Character val = v;
              
              public boolean equals(Object o) {
                return ((o instanceof Map.Entry)) && (((Map.Entry)o).getKey().equals(key)) && (((Map.Entry)o).getValue().equals(val));
              }
              

              public K getKey()
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
      
      public boolean add(Map.Entry<K, Character> o) {
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
      
      public boolean addAll(Collection<? extends Map.Entry<K, Character>> c) {
        throw new UnsupportedOperationException();
      }
      
      public void clear() {
        TObjectCharMapDecorator.this.clear();
      }
    };
  }
  






  public boolean containsValue(Object val)
  {
    return ((val instanceof Character)) && (_map.containsValue(unwrapValue(val)));
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
  







  public void putAll(Map<? extends K, ? extends Character> map)
  {
    Iterator<? extends Map.Entry<? extends K, ? extends Character>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0;) {
      Map.Entry<? extends K, ? extends Character> e = (Map.Entry)it.next();
      put(e.getKey(), (Character)e.getValue());
    }
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
    


    _map = ((TObjectCharMap)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_map);
  }
}
