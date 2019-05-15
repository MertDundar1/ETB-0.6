package gnu.trove.set.hash;

import gnu.trove.TCharCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;














































public class TCharHashSet
  extends TCharHash
  implements TCharSet, Externalizable
{
  static final long serialVersionUID = 1L;
  
  public TCharHashSet() {}
  
  public TCharHashSet(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  public TCharHashSet(int initialCapacity, float load_factor)
  {
    super(initialCapacity, load_factor);
  }
  










  public TCharHashSet(int initial_capacity, float load_factor, char no_entry_value)
  {
    super(initial_capacity, load_factor, no_entry_value);
    
    if (no_entry_value != 0) {
      Arrays.fill(_set, no_entry_value);
    }
  }
  






  public TCharHashSet(Collection<? extends Character> collection)
  {
    this(Math.max(collection.size(), 10));
    addAll(collection);
  }
  






  public TCharHashSet(TCharCollection collection)
  {
    this(Math.max(collection.size(), 10));
    if ((collection instanceof TCharHashSet)) {
      TCharHashSet hashset = (TCharHashSet)collection;
      _loadFactor = _loadFactor;
      no_entry_value = no_entry_value;
      
      if (no_entry_value != 0) {
        Arrays.fill(_set, no_entry_value);
      }
      setUp((int)Math.ceil(10.0F / _loadFactor));
    }
    addAll(collection);
  }
  






  public TCharHashSet(char[] array)
  {
    this(Math.max(array.length, 10));
    addAll(array);
  }
  

  public TCharIterator iterator()
  {
    return new TCharHashIterator(this);
  }
  

  public char[] toArray()
  {
    char[] result = new char[size()];
    char[] set = _set;
    byte[] states = _states;
    
    int i = states.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        result[(j++)] = set[i];
      }
    }
    return result;
  }
  

  public char[] toArray(char[] dest)
  {
    char[] set = _set;
    byte[] states = _states;
    
    int i = states.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        dest[(j++)] = set[i];
      }
    }
    
    if (dest.length > _size) {
      dest[_size] = no_entry_value;
    }
    return dest;
  }
  

  public boolean add(char val)
  {
    int index = insertionIndex(val);
    
    if (index < 0) {
      return false;
    }
    
    byte previousState = _states[index];
    _set[index] = val;
    _states[index] = 1;
    postInsertHook(previousState == 0);
    
    return true;
  }
  

  public boolean remove(char val)
  {
    int index = index(val);
    if (index >= 0) {
      removeAt(index);
      return true;
    }
    return false;
  }
  

  public boolean containsAll(Collection<?> collection)
  {
    for (Object element : collection) {
      if ((element instanceof Character)) {
        char c = ((Character)element).charValue();
        if (!contains(c)) {
          return false;
        }
      } else {
        return false;
      }
    }
    
    return true;
  }
  

  public boolean containsAll(TCharCollection collection)
  {
    TCharIterator iter = collection.iterator();
    while (iter.hasNext()) {
      char element = iter.next();
      if (!contains(element)) {
        return false;
      }
    }
    return true;
  }
  

  public boolean containsAll(char[] array)
  {
    for (int i = array.length; i-- > 0;) {
      if (!contains(array[i])) {
        return false;
      }
    }
    return true;
  }
  

  public boolean addAll(Collection<? extends Character> collection)
  {
    boolean changed = false;
    for (Character element : collection) {
      char e = element.charValue();
      if (add(e)) {
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean addAll(TCharCollection collection)
  {
    boolean changed = false;
    TCharIterator iter = collection.iterator();
    while (iter.hasNext()) {
      char element = iter.next();
      if (add(element)) {
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean addAll(char[] array)
  {
    boolean changed = false;
    for (int i = array.length; i-- > 0;) {
      if (add(array[i])) {
        changed = true;
      }
    }
    return changed;
  }
  


  public boolean retainAll(Collection<?> collection)
  {
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(Character.valueOf(iter.next()))) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  

  public boolean retainAll(TCharCollection collection)
  {
    if (this == collection) {
      return false;
    }
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(iter.next())) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  

  public boolean retainAll(char[] array)
  {
    boolean changed = false;
    Arrays.sort(array);
    char[] set = _set;
    byte[] states = _states;
    
    for (int i = set.length; i-- > 0;) {
      if ((states[i] == 1) && (Arrays.binarySearch(array, set[i]) < 0)) {
        removeAt(i);
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean removeAll(Collection<?> collection)
  {
    boolean changed = false;
    for (Object element : collection) {
      if ((element instanceof Character)) {
        char c = ((Character)element).charValue();
        if (remove(c)) {
          changed = true;
        }
      }
    }
    return changed;
  }
  

  public boolean removeAll(TCharCollection collection)
  {
    boolean changed = false;
    TCharIterator iter = collection.iterator();
    while (iter.hasNext()) {
      char element = iter.next();
      if (remove(element)) {
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean removeAll(char[] array)
  {
    boolean changed = false;
    for (int i = array.length; i-- > 0;) {
      if (remove(array[i])) {
        changed = true;
      }
    }
    return changed;
  }
  

  public void clear()
  {
    super.clear();
    char[] set = _set;
    byte[] states = _states;
    
    for (int i = set.length; i-- > 0;) {
      set[i] = no_entry_value;
      states[i] = 0;
    }
  }
  

  protected void rehash(int newCapacity)
  {
    int oldCapacity = _set.length;
    char[] oldSet = _set;
    byte[] oldStates = _states;
    
    _set = new char[newCapacity];
    _states = new byte[newCapacity];
    
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        char o = oldSet[i];
        int index = insertionIndex(o);
        _set[index] = o;
        _states[index] = 1;
      }
    }
  }
  

  public boolean equals(Object other)
  {
    if (!(other instanceof TCharSet)) {
      return false;
    }
    TCharSet that = (TCharSet)other;
    if (that.size() != size()) {
      return false;
    }
    for (int i = _states.length; i-- > 0;) {
      if ((_states[i] == 1) && 
        (!that.contains(_set[i]))) {
        return false;
      }
    }
    
    return true;
  }
  

  public int hashCode()
  {
    int hashcode = 0;
    for (int i = _states.length; i-- > 0;) {
      if (_states[i] == 1) {
        hashcode += HashFunctions.hash(_set[i]);
      }
    }
    return hashcode;
  }
  

  public String toString()
  {
    StringBuilder buffy = new StringBuilder(_size * 2 + 2);
    buffy.append("{");
    int i = _states.length; for (int j = 1; i-- > 0;) {
      if (_states[i] == 1) {
        buffy.append(_set[i]);
        if (j++ < _size) {
          buffy.append(",");
        }
      }
    }
    buffy.append("}");
    return buffy.toString();
  }
  
  class TCharHashIterator
    extends THashPrimitiveIterator
    implements TCharIterator
  {
    private final TCharHash _hash;
    
    public TCharHashIterator(TCharHash hash)
    {
      super();
      _hash = hash;
    }
    
    public char next()
    {
      moveToNextIndex();
      return _hash._set[_index];
    }
  }
  


  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(1);
    

    super.writeExternal(out);
    

    out.writeInt(_size);
    

    out.writeFloat(_loadFactor);
    

    out.writeChar(no_entry_value);
    

    for (int i = _states.length; i-- > 0;) {
      if (_states[i] == 1) {
        out.writeChar(_set[i]);
      }
    }
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    int version = in.readByte();
    

    super.readExternal(in);
    

    int size = in.readInt();
    
    if (version >= 1)
    {
      _loadFactor = in.readFloat();
      

      no_entry_value = in.readChar();
      
      if (no_entry_value != 0) {
        Arrays.fill(_set, no_entry_value);
      }
    }
    

    setUp(size);
    while (size-- > 0) {
      char val = in.readChar();
      add(val);
    }
  }
}
