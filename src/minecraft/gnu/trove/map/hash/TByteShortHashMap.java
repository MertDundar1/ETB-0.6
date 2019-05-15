package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;



































public class TByteShortHashMap
  extends TByteShortHash
  implements TByteShortMap, Externalizable
{
  static final long serialVersionUID = 1L;
  protected transient short[] _values;
  
  public TByteShortHashMap() {}
  
  public TByteShortHashMap(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  public TByteShortHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  













  public TByteShortHashMap(int initialCapacity, float loadFactor, byte noEntryKey, short noEntryValue)
  {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  







  public TByteShortHashMap(byte[] keys, short[] values)
  {
    super(Math.max(keys.length, values.length));
    
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++) {
      put(keys[i], values[i]);
    }
  }
  






  public TByteShortHashMap(TByteShortMap map)
  {
    super(map.size());
    if ((map instanceof TByteShortHashMap)) {
      TByteShortHashMap hashmap = (TByteShortHashMap)map;
      _loadFactor = _loadFactor;
      no_entry_key = no_entry_key;
      no_entry_value = no_entry_value;
      
      if (no_entry_key != 0) {
        Arrays.fill(_set, no_entry_key);
      }
      
      if (no_entry_value != 0) {
        Arrays.fill(_values, no_entry_value);
      }
      setUp((int)Math.ceil(10.0F / _loadFactor));
    }
    putAll(map);
  }
  









  protected int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _values = new short[capacity];
    return capacity;
  }
  






  protected void rehash(int newCapacity)
  {
    int oldCapacity = _set.length;
    byte[] oldKeys = _set;
    short[] oldVals = _values;
    byte[] oldStates = _states;
    
    _set = new byte[newCapacity];
    _values = new short[newCapacity];
    _states = new byte[newCapacity];
    
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        byte o = oldKeys[i];
        int index = insertionIndex(o);
        _set[index] = o;
        _values[index] = oldVals[i];
        _states[index] = 1;
      }
    }
  }
  

  public short put(byte key, short value)
  {
    int index = insertionIndex(key);
    return doPut(key, value, index);
  }
  

  public short putIfAbsent(byte key, short value)
  {
    int index = insertionIndex(key);
    if (index < 0)
      return _values[(-index - 1)];
    return doPut(key, value, index);
  }
  

  private short doPut(byte key, short value, int index)
  {
    short previous = no_entry_value;
    boolean isNewMapping = true;
    if (index < 0) {
      index = -index - 1;
      previous = _values[index];
      isNewMapping = false;
    }
    byte previousState = _states[index];
    _set[index] = key;
    _states[index] = 1;
    _values[index] = value;
    if (isNewMapping) {
      postInsertHook(previousState == 0);
    }
    
    return previous;
  }
  

  public void putAll(Map<? extends Byte, ? extends Short> map)
  {
    ensureCapacity(map.size());
    
    for (Map.Entry<? extends Byte, ? extends Short> entry : map.entrySet()) {
      put(((Byte)entry.getKey()).byteValue(), ((Short)entry.getValue()).shortValue());
    }
  }
  

  public void putAll(TByteShortMap map)
  {
    ensureCapacity(map.size());
    TByteShortIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    }
  }
  

  public short get(byte key)
  {
    int index = index(key);
    return index < 0 ? no_entry_value : _values[index];
  }
  

  public void clear()
  {
    super.clear();
    Arrays.fill(_set, 0, _set.length, no_entry_key);
    Arrays.fill(_values, 0, _values.length, no_entry_value);
    Arrays.fill(_states, 0, _states.length, (byte)0);
  }
  

  public boolean isEmpty()
  {
    return 0 == _size;
  }
  

  public short remove(byte key)
  {
    short prev = no_entry_value;
    int index = index(key);
    if (index >= 0) {
      prev = _values[index];
      removeAt(index);
    }
    return prev;
  }
  

  public void removeAt(int index)
  {
    _values[index] = no_entry_value;
    super.removeAt(index);
  }
  

  public TByteSet keySet()
  {
    return new TKeyView();
  }
  

  public byte[] keys()
  {
    byte[] keys = new byte[size()];
    byte[] k = _set;
    byte[] states = _states;
    
    int i = k.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        keys[(j++)] = k[i];
      }
    }
    return keys;
  }
  

  public byte[] keys(byte[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new byte[size];
    }
    
    byte[] keys = _set;
    byte[] states = _states;
    
    int i = keys.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        array[(j++)] = keys[i];
      }
    }
    return array;
  }
  

  public TShortCollection valueCollection()
  {
    return new TValueView();
  }
  

  public short[] values()
  {
    short[] vals = new short[size()];
    short[] v = _values;
    byte[] states = _states;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        vals[(j++)] = v[i];
      }
    }
    return vals;
  }
  

  public short[] values(short[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new short[size];
    }
    
    short[] v = _values;
    byte[] states = _states;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        array[(j++)] = v[i];
      }
    }
    return array;
  }
  

  public boolean containsValue(short val)
  {
    byte[] states = _states;
    short[] vals = _values;
    
    for (int i = vals.length; i-- > 0;) {
      if ((states[i] == 1) && (val == vals[i])) {
        return true;
      }
    }
    return false;
  }
  

  public boolean containsKey(byte key)
  {
    return contains(key);
  }
  

  public TByteShortIterator iterator()
  {
    return new TByteShortHashIterator(this);
  }
  

  public boolean forEachKey(TByteProcedure procedure)
  {
    return forEach(procedure);
  }
  

  public boolean forEachValue(TShortProcedure procedure)
  {
    byte[] states = _states;
    short[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(values[i]))) {
        return false;
      }
    }
    return true;
  }
  

  public boolean forEachEntry(TByteShortProcedure procedure)
  {
    byte[] states = _states;
    byte[] keys = _set;
    short[] values = _values;
    for (int i = keys.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(keys[i], values[i]))) {
        return false;
      }
    }
    return true;
  }
  

  public void transformValues(TShortFunction function)
  {
    byte[] states = _states;
    short[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        values[i] = function.execute(values[i]);
      }
    }
  }
  

  public boolean retainEntries(TByteShortProcedure procedure)
  {
    boolean modified = false;
    byte[] states = _states;
    byte[] keys = _set;
    short[] values = _values;
    


    tempDisableAutoCompaction();
    try {
      for (i = keys.length; i-- > 0;) {
        if ((states[i] == 1) && (!procedure.execute(keys[i], values[i]))) {
          removeAt(i);
          modified = true;
        }
      }
    } finally {
      int i;
      reenableAutoCompaction(true);
    }
    
    return modified;
  }
  

  public boolean increment(byte key)
  {
    return adjustValue(key, (short)1);
  }
  

  public boolean adjustValue(byte key, short amount)
  {
    int index = index(key);
    if (index < 0) {
      return false;
    }
    int tmp17_16 = index; short[] tmp17_13 = _values;tmp17_13[tmp17_16] = ((short)(tmp17_13[tmp17_16] + amount));
    return true;
  }
  


  public short adjustOrPutValue(byte key, short adjust_amount, short put_amount)
  {
    int index = insertionIndex(key);
    boolean isNewMapping;
    short newValue;
    boolean isNewMapping; if (index < 0) {
      index = -index - 1; int 
        tmp25_23 = index; short[] tmp25_20 = _values;short newValue = tmp25_20[tmp25_23] = (short)(tmp25_20[tmp25_23] + adjust_amount);
      isNewMapping = false;
    } else {
      newValue = _values[index] = put_amount;
      isNewMapping = true;
    }
    
    byte previousState = _states[index];
    _set[index] = key;
    _states[index] = 1;
    
    if (isNewMapping) {
      postInsertHook(previousState == 0);
    }
    
    return newValue;
  }
  
  protected class TKeyView implements TByteSet
  {
    protected TKeyView() {}
    
    public TByteIterator iterator()
    {
      return new TByteShortHashMap.TByteShortKeyHashIterator(TByteShortHashMap.this, TByteShortHashMap.this);
    }
    

    public byte getNoEntryValue()
    {
      return no_entry_key;
    }
    

    public int size()
    {
      return _size;
    }
    

    public boolean isEmpty()
    {
      return 0 == _size;
    }
    

    public boolean contains(byte entry)
    {
      return TByteShortHashMap.this.contains(entry);
    }
    

    public byte[] toArray()
    {
      return keys();
    }
    

    public byte[] toArray(byte[] dest)
    {
      return keys(dest);
    }
    





    public boolean add(byte entry)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean remove(byte entry)
    {
      return no_entry_value != remove(entry);
    }
    

    public boolean containsAll(Collection<?> collection)
    {
      for (Object element : collection) {
        if ((element instanceof Byte)) {
          byte ele = ((Byte)element).byteValue();
          if (!containsKey(ele)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(TByteCollection collection)
    {
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!containsKey(iter.next())) {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(byte[] array)
    {
      for (byte element : array) {
        if (!TByteShortHashMap.this.contains(element)) {
          return false;
        }
      }
      return true;
    }
    





    public boolean addAll(Collection<? extends Byte> collection)
    {
      throw new UnsupportedOperationException();
    }
    





    public boolean addAll(TByteCollection collection)
    {
      throw new UnsupportedOperationException();
    }
    





    public boolean addAll(byte[] array)
    {
      throw new UnsupportedOperationException();
    }
    


    public boolean retainAll(Collection<?> collection)
    {
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Byte.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(TByteCollection collection)
    {
      if (this == collection) {
        return false;
      }
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(byte[] array)
    {
      boolean changed = false;
      Arrays.sort(array);
      byte[] set = _set;
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
        if ((element instanceof Byte)) {
          byte c = ((Byte)element).byteValue();
          if (remove(c)) {
            changed = true;
          }
        }
      }
      return changed;
    }
    

    public boolean removeAll(TByteCollection collection)
    {
      if (this == collection) {
        clear();
        return true;
      }
      boolean changed = false;
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        byte element = iter.next();
        if (remove(element)) {
          changed = true;
        }
      }
      return changed;
    }
    

    public boolean removeAll(byte[] array)
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
      TByteShortHashMap.this.clear();
    }
    

    public boolean forEach(TByteProcedure procedure)
    {
      return forEachKey(procedure);
    }
    

    public boolean equals(Object other)
    {
      if (!(other instanceof TByteSet)) {
        return false;
      }
      TByteSet that = (TByteSet)other;
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
      final StringBuilder buf = new StringBuilder("{");
      forEachKey(new TByteProcedure() {
        private boolean first = true;
        
        public boolean execute(byte key)
        {
          if (first) {
            first = false;
          } else {
            buf.append(",");
          }
          
          buf.append(key);
          return true;
        }
      });
      buf.append("}");
      return buf.toString();
    }
  }
  
  protected class TValueView implements TShortCollection
  {
    protected TValueView() {}
    
    public TShortIterator iterator()
    {
      return new TByteShortHashMap.TByteShortValueHashIterator(TByteShortHashMap.this, TByteShortHashMap.this);
    }
    

    public short getNoEntryValue()
    {
      return no_entry_value;
    }
    

    public int size()
    {
      return _size;
    }
    

    public boolean isEmpty()
    {
      return 0 == _size;
    }
    

    public boolean contains(short entry)
    {
      return containsValue(entry);
    }
    

    public short[] toArray()
    {
      return values();
    }
    

    public short[] toArray(short[] dest)
    {
      return values(dest);
    }
    

    public boolean add(short entry)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean remove(short entry)
    {
      short[] values = _values;
      byte[] set = _set;
      
      for (int i = values.length; i-- > 0;) {
        if ((set[i] != 0) && (set[i] != 2) && (entry == values[i])) {
          removeAt(i);
          return true;
        }
      }
      return false;
    }
    

    public boolean containsAll(Collection<?> collection)
    {
      for (Object element : collection) {
        if ((element instanceof Short)) {
          short ele = ((Short)element).shortValue();
          if (!containsValue(ele)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(TShortCollection collection)
    {
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!containsValue(iter.next())) {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(short[] array)
    {
      for (short element : array) {
        if (!containsValue(element)) {
          return false;
        }
      }
      return true;
    }
    

    public boolean addAll(Collection<? extends Short> collection)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean addAll(TShortCollection collection)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean addAll(short[] array)
    {
      throw new UnsupportedOperationException();
    }
    


    public boolean retainAll(Collection<?> collection)
    {
      boolean modified = false;
      TShortIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Short.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(TShortCollection collection)
    {
      if (this == collection) {
        return false;
      }
      boolean modified = false;
      TShortIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(short[] array)
    {
      boolean changed = false;
      Arrays.sort(array);
      short[] values = _values;
      byte[] states = _states;
      
      for (int i = values.length; i-- > 0;) {
        if ((states[i] == 1) && (Arrays.binarySearch(array, values[i]) < 0)) {
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
        if ((element instanceof Short)) {
          short c = ((Short)element).shortValue();
          if (remove(c)) {
            changed = true;
          }
        }
      }
      return changed;
    }
    

    public boolean removeAll(TShortCollection collection)
    {
      if (this == collection) {
        clear();
        return true;
      }
      boolean changed = false;
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        short element = iter.next();
        if (remove(element)) {
          changed = true;
        }
      }
      return changed;
    }
    

    public boolean removeAll(short[] array)
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
      TByteShortHashMap.this.clear();
    }
    

    public boolean forEach(TShortProcedure procedure)
    {
      return forEachValue(procedure);
    }
    


    public String toString()
    {
      final StringBuilder buf = new StringBuilder("{");
      forEachValue(new TShortProcedure() {
        private boolean first = true;
        
        public boolean execute(short value) {
          if (first) {
            first = false;
          } else {
            buf.append(",");
          }
          
          buf.append(value);
          return true;
        }
      });
      buf.append("}");
      return buf.toString();
    }
  }
  



  class TByteShortKeyHashIterator
    extends THashPrimitiveIterator
    implements TByteIterator
  {
    TByteShortKeyHashIterator(TPrimitiveHash hash)
    {
      super();
    }
    
    public byte next()
    {
      moveToNextIndex();
      return _set[_index];
    }
    
    public void remove()
    {
      if (_expectedSize != _hash.size()) {
        throw new ConcurrentModificationException();
      }
      
      try
      {
        _hash.tempDisableAutoCompaction();
        removeAt(_index);
      }
      finally {
        _hash.reenableAutoCompaction(false);
      }
      
      _expectedSize -= 1;
    }
  }
  




  class TByteShortValueHashIterator
    extends THashPrimitiveIterator
    implements TShortIterator
  {
    TByteShortValueHashIterator(TPrimitiveHash hash)
    {
      super();
    }
    
    public short next()
    {
      moveToNextIndex();
      return _values[_index];
    }
    
    public void remove()
    {
      if (_expectedSize != _hash.size()) {
        throw new ConcurrentModificationException();
      }
      
      try
      {
        _hash.tempDisableAutoCompaction();
        removeAt(_index);
      }
      finally {
        _hash.reenableAutoCompaction(false);
      }
      
      _expectedSize -= 1;
    }
  }
  



  class TByteShortHashIterator
    extends THashPrimitiveIterator
    implements TByteShortIterator
  {
    TByteShortHashIterator(TByteShortHashMap map)
    {
      super();
    }
    
    public void advance()
    {
      moveToNextIndex();
    }
    
    public byte key()
    {
      return _set[_index];
    }
    
    public short value()
    {
      return _values[_index];
    }
    
    public short setValue(short val)
    {
      short old = value();
      _values[_index] = val;
      return old;
    }
    
    public void remove()
    {
      if (_expectedSize != _hash.size()) {
        throw new ConcurrentModificationException();
      }
      try
      {
        _hash.tempDisableAutoCompaction();
        removeAt(_index);
      }
      finally {
        _hash.reenableAutoCompaction(false);
      }
      _expectedSize -= 1;
    }
  }
  


  public boolean equals(Object other)
  {
    if (!(other instanceof TByteShortMap)) {
      return false;
    }
    TByteShortMap that = (TByteShortMap)other;
    if (that.size() != size()) {
      return false;
    }
    short[] values = _values;
    byte[] states = _states;
    short this_no_entry_value = getNoEntryValue();
    short that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        byte key = _set[i];
        short that_value = that.get(key);
        short this_value = values[i];
        if ((this_value != that_value) && (this_value != this_no_entry_value) && (that_value != that_no_entry_value))
        {

          return false;
        }
      }
    }
    return true;
  }
  


  public int hashCode()
  {
    int hashcode = 0;
    byte[] states = _states;
    for (int i = _values.length; i-- > 0;) {
      if (states[i] == 1) {
        hashcode += (HashFunctions.hash(_set[i]) ^ HashFunctions.hash(_values[i]));
      }
    }
    
    return hashcode;
  }
  


  public String toString()
  {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TByteShortProcedure() {
      private boolean first = true;
      
      public boolean execute(byte key, short value) { if (first) first = false; else {
          buf.append(",");
        }
        buf.append(key);
        buf.append("=");
        buf.append(value);
        return true;
      }
    });
    buf.append("}");
    return buf.toString();
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    super.writeExternal(out);
    

    out.writeInt(_size);
    

    for (int i = _states.length; i-- > 0;) {
      if (_states[i] == 1) {
        out.writeByte(_set[i]);
        out.writeShort(_values[i]);
      }
    }
  }
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    super.readExternal(in);
    

    int size = in.readInt();
    setUp(size);
    

    while (size-- > 0) {
      byte key = in.readByte();
      short val = in.readShort();
      put(key, val);
    }
  }
}
