package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;



































public class TDoubleByteHashMap
  extends TDoubleByteHash
  implements TDoubleByteMap, Externalizable
{
  static final long serialVersionUID = 1L;
  protected transient byte[] _values;
  
  public TDoubleByteHashMap() {}
  
  public TDoubleByteHashMap(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  public TDoubleByteHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  













  public TDoubleByteHashMap(int initialCapacity, float loadFactor, double noEntryKey, byte noEntryValue)
  {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  







  public TDoubleByteHashMap(double[] keys, byte[] values)
  {
    super(Math.max(keys.length, values.length));
    
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++) {
      put(keys[i], values[i]);
    }
  }
  






  public TDoubleByteHashMap(TDoubleByteMap map)
  {
    super(map.size());
    if ((map instanceof TDoubleByteHashMap)) {
      TDoubleByteHashMap hashmap = (TDoubleByteHashMap)map;
      _loadFactor = _loadFactor;
      no_entry_key = no_entry_key;
      no_entry_value = no_entry_value;
      
      if (no_entry_key != 0.0D) {
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
    _values = new byte[capacity];
    return capacity;
  }
  






  protected void rehash(int newCapacity)
  {
    int oldCapacity = _set.length;
    double[] oldKeys = _set;
    byte[] oldVals = _values;
    byte[] oldStates = _states;
    
    _set = new double[newCapacity];
    _values = new byte[newCapacity];
    _states = new byte[newCapacity];
    
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        double o = oldKeys[i];
        int index = insertionIndex(o);
        _set[index] = o;
        _values[index] = oldVals[i];
        _states[index] = 1;
      }
    }
  }
  

  public byte put(double key, byte value)
  {
    int index = insertionIndex(key);
    return doPut(key, value, index);
  }
  

  public byte putIfAbsent(double key, byte value)
  {
    int index = insertionIndex(key);
    if (index < 0)
      return _values[(-index - 1)];
    return doPut(key, value, index);
  }
  

  private byte doPut(double key, byte value, int index)
  {
    byte previous = no_entry_value;
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
  

  public void putAll(Map<? extends Double, ? extends Byte> map)
  {
    ensureCapacity(map.size());
    
    for (Map.Entry<? extends Double, ? extends Byte> entry : map.entrySet()) {
      put(((Double)entry.getKey()).doubleValue(), ((Byte)entry.getValue()).byteValue());
    }
  }
  

  public void putAll(TDoubleByteMap map)
  {
    ensureCapacity(map.size());
    TDoubleByteIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    }
  }
  

  public byte get(double key)
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
  

  public byte remove(double key)
  {
    byte prev = no_entry_value;
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
  

  public TDoubleSet keySet()
  {
    return new TKeyView();
  }
  

  public double[] keys()
  {
    double[] keys = new double[size()];
    double[] k = _set;
    byte[] states = _states;
    
    int i = k.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        keys[(j++)] = k[i];
      }
    }
    return keys;
  }
  

  public double[] keys(double[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new double[size];
    }
    
    double[] keys = _set;
    byte[] states = _states;
    
    int i = keys.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        array[(j++)] = keys[i];
      }
    }
    return array;
  }
  

  public TByteCollection valueCollection()
  {
    return new TValueView();
  }
  

  public byte[] values()
  {
    byte[] vals = new byte[size()];
    byte[] v = _values;
    byte[] states = _states;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        vals[(j++)] = v[i];
      }
    }
    return vals;
  }
  

  public byte[] values(byte[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new byte[size];
    }
    
    byte[] v = _values;
    byte[] states = _states;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        array[(j++)] = v[i];
      }
    }
    return array;
  }
  

  public boolean containsValue(byte val)
  {
    byte[] states = _states;
    byte[] vals = _values;
    
    for (int i = vals.length; i-- > 0;) {
      if ((states[i] == 1) && (val == vals[i])) {
        return true;
      }
    }
    return false;
  }
  

  public boolean containsKey(double key)
  {
    return contains(key);
  }
  

  public TDoubleByteIterator iterator()
  {
    return new TDoubleByteHashIterator(this);
  }
  

  public boolean forEachKey(TDoubleProcedure procedure)
  {
    return forEach(procedure);
  }
  

  public boolean forEachValue(TByteProcedure procedure)
  {
    byte[] states = _states;
    byte[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(values[i]))) {
        return false;
      }
    }
    return true;
  }
  

  public boolean forEachEntry(TDoubleByteProcedure procedure)
  {
    byte[] states = _states;
    double[] keys = _set;
    byte[] values = _values;
    for (int i = keys.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(keys[i], values[i]))) {
        return false;
      }
    }
    return true;
  }
  

  public void transformValues(TByteFunction function)
  {
    byte[] states = _states;
    byte[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        values[i] = function.execute(values[i]);
      }
    }
  }
  

  public boolean retainEntries(TDoubleByteProcedure procedure)
  {
    boolean modified = false;
    byte[] states = _states;
    double[] keys = _set;
    byte[] values = _values;
    


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
  

  public boolean increment(double key)
  {
    return adjustValue(key, (byte)1);
  }
  

  public boolean adjustValue(double key, byte amount)
  {
    int index = index(key);
    if (index < 0) {
      return false;
    }
    int tmp20_18 = index; byte[] tmp20_15 = _values;tmp20_15[tmp20_18] = ((byte)(tmp20_15[tmp20_18] + amount));
    return true;
  }
  


  public byte adjustOrPutValue(double key, byte adjust_amount, byte put_amount)
  {
    int index = insertionIndex(key);
    boolean isNewMapping;
    byte newValue;
    boolean isNewMapping; if (index < 0) {
      index = -index - 1; int 
        tmp25_23 = index; byte[] tmp25_20 = _values;byte newValue = tmp25_20[tmp25_23] = (byte)(tmp25_20[tmp25_23] + adjust_amount);
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
  
  protected class TKeyView implements TDoubleSet
  {
    protected TKeyView() {}
    
    public TDoubleIterator iterator()
    {
      return new TDoubleByteHashMap.TDoubleByteKeyHashIterator(TDoubleByteHashMap.this, TDoubleByteHashMap.this);
    }
    

    public double getNoEntryValue()
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
    

    public boolean contains(double entry)
    {
      return TDoubleByteHashMap.this.contains(entry);
    }
    

    public double[] toArray()
    {
      return keys();
    }
    

    public double[] toArray(double[] dest)
    {
      return keys(dest);
    }
    





    public boolean add(double entry)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean remove(double entry)
    {
      return no_entry_value != remove(entry);
    }
    

    public boolean containsAll(Collection<?> collection)
    {
      for (Object element : collection) {
        if ((element instanceof Double)) {
          double ele = ((Double)element).doubleValue();
          if (!containsKey(ele)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(TDoubleCollection collection)
    {
      TDoubleIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!containsKey(iter.next())) {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(double[] array)
    {
      for (double element : array) {
        if (!TDoubleByteHashMap.this.contains(element)) {
          return false;
        }
      }
      return true;
    }
    





    public boolean addAll(Collection<? extends Double> collection)
    {
      throw new UnsupportedOperationException();
    }
    





    public boolean addAll(TDoubleCollection collection)
    {
      throw new UnsupportedOperationException();
    }
    





    public boolean addAll(double[] array)
    {
      throw new UnsupportedOperationException();
    }
    


    public boolean retainAll(Collection<?> collection)
    {
      boolean modified = false;
      TDoubleIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Double.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(TDoubleCollection collection)
    {
      if (this == collection) {
        return false;
      }
      boolean modified = false;
      TDoubleIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(double[] array)
    {
      boolean changed = false;
      Arrays.sort(array);
      double[] set = _set;
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
        if ((element instanceof Double)) {
          double c = ((Double)element).doubleValue();
          if (remove(c)) {
            changed = true;
          }
        }
      }
      return changed;
    }
    

    public boolean removeAll(TDoubleCollection collection)
    {
      if (this == collection) {
        clear();
        return true;
      }
      boolean changed = false;
      TDoubleIterator iter = collection.iterator();
      while (iter.hasNext()) {
        double element = iter.next();
        if (remove(element)) {
          changed = true;
        }
      }
      return changed;
    }
    

    public boolean removeAll(double[] array)
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
      TDoubleByteHashMap.this.clear();
    }
    

    public boolean forEach(TDoubleProcedure procedure)
    {
      return forEachKey(procedure);
    }
    

    public boolean equals(Object other)
    {
      if (!(other instanceof TDoubleSet)) {
        return false;
      }
      TDoubleSet that = (TDoubleSet)other;
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
      forEachKey(new TDoubleProcedure() {
        private boolean first = true;
        
        public boolean execute(double key)
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
  
  protected class TValueView implements TByteCollection
  {
    protected TValueView() {}
    
    public TByteIterator iterator()
    {
      return new TDoubleByteHashMap.TDoubleByteValueHashIterator(TDoubleByteHashMap.this, TDoubleByteHashMap.this);
    }
    

    public byte getNoEntryValue()
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
    

    public boolean contains(byte entry)
    {
      return containsValue(entry);
    }
    

    public byte[] toArray()
    {
      return values();
    }
    

    public byte[] toArray(byte[] dest)
    {
      return values(dest);
    }
    

    public boolean add(byte entry)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean remove(byte entry)
    {
      byte[] values = _values;
      double[] set = _set;
      
      for (int i = values.length; i-- > 0;) {
        if ((set[i] != 0.0D) && (set[i] != 2.0D) && (entry == values[i])) {
          removeAt(i);
          return true;
        }
      }
      return false;
    }
    

    public boolean containsAll(Collection<?> collection)
    {
      for (Object element : collection) {
        if ((element instanceof Byte)) {
          byte ele = ((Byte)element).byteValue();
          if (!containsValue(ele)) {
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
        if (!containsValue(iter.next())) {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(byte[] array)
    {
      for (byte element : array) {
        if (!containsValue(element)) {
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
      byte[] values = _values;
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
      TDoubleByteHashMap.this.clear();
    }
    

    public boolean forEach(TByteProcedure procedure)
    {
      return forEachValue(procedure);
    }
    


    public String toString()
    {
      final StringBuilder buf = new StringBuilder("{");
      forEachValue(new TByteProcedure() {
        private boolean first = true;
        
        public boolean execute(byte value) {
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
  



  class TDoubleByteKeyHashIterator
    extends THashPrimitiveIterator
    implements TDoubleIterator
  {
    TDoubleByteKeyHashIterator(TPrimitiveHash hash)
    {
      super();
    }
    
    public double next()
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
  




  class TDoubleByteValueHashIterator
    extends THashPrimitiveIterator
    implements TByteIterator
  {
    TDoubleByteValueHashIterator(TPrimitiveHash hash)
    {
      super();
    }
    
    public byte next()
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
  



  class TDoubleByteHashIterator
    extends THashPrimitiveIterator
    implements TDoubleByteIterator
  {
    TDoubleByteHashIterator(TDoubleByteHashMap map)
    {
      super();
    }
    
    public void advance()
    {
      moveToNextIndex();
    }
    
    public double key()
    {
      return _set[_index];
    }
    
    public byte value()
    {
      return _values[_index];
    }
    
    public byte setValue(byte val)
    {
      byte old = value();
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
    if (!(other instanceof TDoubleByteMap)) {
      return false;
    }
    TDoubleByteMap that = (TDoubleByteMap)other;
    if (that.size() != size()) {
      return false;
    }
    byte[] values = _values;
    byte[] states = _states;
    byte this_no_entry_value = getNoEntryValue();
    byte that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        double key = _set[i];
        byte that_value = that.get(key);
        byte this_value = values[i];
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
    forEachEntry(new TDoubleByteProcedure() {
      private boolean first = true;
      
      public boolean execute(double key, byte value) { if (first) first = false; else {
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
        out.writeDouble(_set[i]);
        out.writeByte(_values[i]);
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
      double key = in.readDouble();
      byte val = in.readByte();
      put(key, val);
    }
  }
}
