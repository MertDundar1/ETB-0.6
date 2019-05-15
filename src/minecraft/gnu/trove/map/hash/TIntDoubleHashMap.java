package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntDoubleHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;



































public class TIntDoubleHashMap
  extends TIntDoubleHash
  implements TIntDoubleMap, Externalizable
{
  static final long serialVersionUID = 1L;
  protected transient double[] _values;
  
  public TIntDoubleHashMap() {}
  
  public TIntDoubleHashMap(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  public TIntDoubleHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  













  public TIntDoubleHashMap(int initialCapacity, float loadFactor, int noEntryKey, double noEntryValue)
  {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  







  public TIntDoubleHashMap(int[] keys, double[] values)
  {
    super(Math.max(keys.length, values.length));
    
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++) {
      put(keys[i], values[i]);
    }
  }
  






  public TIntDoubleHashMap(TIntDoubleMap map)
  {
    super(map.size());
    if ((map instanceof TIntDoubleHashMap)) {
      TIntDoubleHashMap hashmap = (TIntDoubleHashMap)map;
      _loadFactor = _loadFactor;
      no_entry_key = no_entry_key;
      no_entry_value = no_entry_value;
      
      if (no_entry_key != 0) {
        Arrays.fill(_set, no_entry_key);
      }
      
      if (no_entry_value != 0.0D) {
        Arrays.fill(_values, no_entry_value);
      }
      setUp((int)Math.ceil(10.0F / _loadFactor));
    }
    putAll(map);
  }
  









  protected int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _values = new double[capacity];
    return capacity;
  }
  






  protected void rehash(int newCapacity)
  {
    int oldCapacity = _set.length;
    int[] oldKeys = _set;
    double[] oldVals = _values;
    byte[] oldStates = _states;
    
    _set = new int[newCapacity];
    _values = new double[newCapacity];
    _states = new byte[newCapacity];
    
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        int o = oldKeys[i];
        int index = insertionIndex(o);
        _set[index] = o;
        _values[index] = oldVals[i];
        _states[index] = 1;
      }
    }
  }
  

  public double put(int key, double value)
  {
    int index = insertionIndex(key);
    return doPut(key, value, index);
  }
  

  public double putIfAbsent(int key, double value)
  {
    int index = insertionIndex(key);
    if (index < 0)
      return _values[(-index - 1)];
    return doPut(key, value, index);
  }
  

  private double doPut(int key, double value, int index)
  {
    double previous = no_entry_value;
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
  

  public void putAll(Map<? extends Integer, ? extends Double> map)
  {
    ensureCapacity(map.size());
    
    for (Map.Entry<? extends Integer, ? extends Double> entry : map.entrySet()) {
      put(((Integer)entry.getKey()).intValue(), ((Double)entry.getValue()).doubleValue());
    }
  }
  

  public void putAll(TIntDoubleMap map)
  {
    ensureCapacity(map.size());
    TIntDoubleIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    }
  }
  

  public double get(int key)
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
  

  public double remove(int key)
  {
    double prev = no_entry_value;
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
  

  public TIntSet keySet()
  {
    return new TKeyView();
  }
  

  public int[] keys()
  {
    int[] keys = new int[size()];
    int[] k = _set;
    byte[] states = _states;
    
    int i = k.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        keys[(j++)] = k[i];
      }
    }
    return keys;
  }
  

  public int[] keys(int[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new int[size];
    }
    
    int[] keys = _set;
    byte[] states = _states;
    
    int i = keys.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        array[(j++)] = keys[i];
      }
    }
    return array;
  }
  

  public TDoubleCollection valueCollection()
  {
    return new TValueView();
  }
  

  public double[] values()
  {
    double[] vals = new double[size()];
    double[] v = _values;
    byte[] states = _states;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        vals[(j++)] = v[i];
      }
    }
    return vals;
  }
  

  public double[] values(double[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new double[size];
    }
    
    double[] v = _values;
    byte[] states = _states;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if (states[i] == 1) {
        array[(j++)] = v[i];
      }
    }
    return array;
  }
  

  public boolean containsValue(double val)
  {
    byte[] states = _states;
    double[] vals = _values;
    
    for (int i = vals.length; i-- > 0;) {
      if ((states[i] == 1) && (val == vals[i])) {
        return true;
      }
    }
    return false;
  }
  

  public boolean containsKey(int key)
  {
    return contains(key);
  }
  

  public TIntDoubleIterator iterator()
  {
    return new TIntDoubleHashIterator(this);
  }
  

  public boolean forEachKey(TIntProcedure procedure)
  {
    return forEach(procedure);
  }
  

  public boolean forEachValue(TDoubleProcedure procedure)
  {
    byte[] states = _states;
    double[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(values[i]))) {
        return false;
      }
    }
    return true;
  }
  

  public boolean forEachEntry(TIntDoubleProcedure procedure)
  {
    byte[] states = _states;
    int[] keys = _set;
    double[] values = _values;
    for (int i = keys.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(keys[i], values[i]))) {
        return false;
      }
    }
    return true;
  }
  

  public void transformValues(TDoubleFunction function)
  {
    byte[] states = _states;
    double[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        values[i] = function.execute(values[i]);
      }
    }
  }
  

  public boolean retainEntries(TIntDoubleProcedure procedure)
  {
    boolean modified = false;
    byte[] states = _states;
    int[] keys = _set;
    double[] values = _values;
    


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
  

  public boolean increment(int key)
  {
    return adjustValue(key, 1.0D);
  }
  

  public boolean adjustValue(int key, double amount)
  {
    int index = index(key);
    if (index < 0) {
      return false;
    }
    _values[index] += amount;
    return true;
  }
  


  public double adjustOrPutValue(int key, double adjust_amount, double put_amount)
  {
    int index = insertionIndex(key);
    boolean isNewMapping;
    double newValue;
    boolean isNewMapping; if (index < 0) {
      index = -index - 1;
      double newValue = _values[index] += adjust_amount;
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
  
  protected class TKeyView implements TIntSet
  {
    protected TKeyView() {}
    
    public TIntIterator iterator()
    {
      return new TIntDoubleHashMap.TIntDoubleKeyHashIterator(TIntDoubleHashMap.this, TIntDoubleHashMap.this);
    }
    

    public int getNoEntryValue()
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
    

    public boolean contains(int entry)
    {
      return TIntDoubleHashMap.this.contains(entry);
    }
    

    public int[] toArray()
    {
      return keys();
    }
    

    public int[] toArray(int[] dest)
    {
      return keys(dest);
    }
    





    public boolean add(int entry)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean remove(int entry)
    {
      return no_entry_value != remove(entry);
    }
    

    public boolean containsAll(Collection<?> collection)
    {
      for (Object element : collection) {
        if ((element instanceof Integer)) {
          int ele = ((Integer)element).intValue();
          if (!containsKey(ele)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(TIntCollection collection)
    {
      TIntIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!containsKey(iter.next())) {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(int[] array)
    {
      for (int element : array) {
        if (!TIntDoubleHashMap.this.contains(element)) {
          return false;
        }
      }
      return true;
    }
    





    public boolean addAll(Collection<? extends Integer> collection)
    {
      throw new UnsupportedOperationException();
    }
    





    public boolean addAll(TIntCollection collection)
    {
      throw new UnsupportedOperationException();
    }
    





    public boolean addAll(int[] array)
    {
      throw new UnsupportedOperationException();
    }
    


    public boolean retainAll(Collection<?> collection)
    {
      boolean modified = false;
      TIntIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Integer.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(TIntCollection collection)
    {
      if (this == collection) {
        return false;
      }
      boolean modified = false;
      TIntIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    

    public boolean retainAll(int[] array)
    {
      boolean changed = false;
      Arrays.sort(array);
      int[] set = _set;
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
        if ((element instanceof Integer)) {
          int c = ((Integer)element).intValue();
          if (remove(c)) {
            changed = true;
          }
        }
      }
      return changed;
    }
    

    public boolean removeAll(TIntCollection collection)
    {
      if (this == collection) {
        clear();
        return true;
      }
      boolean changed = false;
      TIntIterator iter = collection.iterator();
      while (iter.hasNext()) {
        int element = iter.next();
        if (remove(element)) {
          changed = true;
        }
      }
      return changed;
    }
    

    public boolean removeAll(int[] array)
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
      TIntDoubleHashMap.this.clear();
    }
    

    public boolean forEach(TIntProcedure procedure)
    {
      return forEachKey(procedure);
    }
    

    public boolean equals(Object other)
    {
      if (!(other instanceof TIntSet)) {
        return false;
      }
      TIntSet that = (TIntSet)other;
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
      forEachKey(new TIntProcedure() {
        private boolean first = true;
        
        public boolean execute(int key)
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
  
  protected class TValueView implements TDoubleCollection
  {
    protected TValueView() {}
    
    public TDoubleIterator iterator()
    {
      return new TIntDoubleHashMap.TIntDoubleValueHashIterator(TIntDoubleHashMap.this, TIntDoubleHashMap.this);
    }
    

    public double getNoEntryValue()
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
    

    public boolean contains(double entry)
    {
      return containsValue(entry);
    }
    

    public double[] toArray()
    {
      return values();
    }
    

    public double[] toArray(double[] dest)
    {
      return values(dest);
    }
    

    public boolean add(double entry)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean remove(double entry)
    {
      double[] values = _values;
      int[] set = _set;
      
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
        if ((element instanceof Double)) {
          double ele = ((Double)element).doubleValue();
          if (!containsValue(ele)) {
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
        if (!containsValue(iter.next())) {
          return false;
        }
      }
      return true;
    }
    

    public boolean containsAll(double[] array)
    {
      for (double element : array) {
        if (!containsValue(element)) {
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
      double[] values = _values;
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
      TIntDoubleHashMap.this.clear();
    }
    

    public boolean forEach(TDoubleProcedure procedure)
    {
      return forEachValue(procedure);
    }
    


    public String toString()
    {
      final StringBuilder buf = new StringBuilder("{");
      forEachValue(new TDoubleProcedure() {
        private boolean first = true;
        
        public boolean execute(double value) {
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
  



  class TIntDoubleKeyHashIterator
    extends THashPrimitiveIterator
    implements TIntIterator
  {
    TIntDoubleKeyHashIterator(TPrimitiveHash hash)
    {
      super();
    }
    
    public int next()
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
  




  class TIntDoubleValueHashIterator
    extends THashPrimitiveIterator
    implements TDoubleIterator
  {
    TIntDoubleValueHashIterator(TPrimitiveHash hash)
    {
      super();
    }
    
    public double next()
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
  



  class TIntDoubleHashIterator
    extends THashPrimitiveIterator
    implements TIntDoubleIterator
  {
    TIntDoubleHashIterator(TIntDoubleHashMap map)
    {
      super();
    }
    
    public void advance()
    {
      moveToNextIndex();
    }
    
    public int key()
    {
      return _set[_index];
    }
    
    public double value()
    {
      return _values[_index];
    }
    
    public double setValue(double val)
    {
      double old = value();
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
    if (!(other instanceof TIntDoubleMap)) {
      return false;
    }
    TIntDoubleMap that = (TIntDoubleMap)other;
    if (that.size() != size()) {
      return false;
    }
    double[] values = _values;
    byte[] states = _states;
    double this_no_entry_value = getNoEntryValue();
    double that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        int key = _set[i];
        double that_value = that.get(key);
        double this_value = values[i];
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
    forEachEntry(new TIntDoubleProcedure() {
      private boolean first = true;
      
      public boolean execute(int key, double value) { if (first) first = false; else {
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
        out.writeInt(_set[i]);
        out.writeDouble(_values[i]);
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
      int key = in.readInt();
      double val = in.readDouble();
      put(key, val);
    }
  }
}
