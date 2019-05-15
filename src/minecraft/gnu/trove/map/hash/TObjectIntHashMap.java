package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;


























public class TObjectIntHashMap<K>
  extends TObjectHash<K>
  implements TObjectIntMap<K>, Externalizable
{
  static final long serialVersionUID = 1L;
  private final TObjectIntProcedure<K> PUT_ALL_PROC = new TObjectIntProcedure() {
    public boolean execute(K key, int value) {
      put(key, value);
      return true;
    }
  };
  




  protected transient int[] _values;
  




  protected int no_entry_value;
  




  public TObjectIntHashMap() {}
  




  public TObjectIntHashMap(int initialCapacity)
  {
    super(initialCapacity);
    no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
  }
  








  public TObjectIntHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
    no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
  }
  









  public TObjectIntHashMap(int initialCapacity, float loadFactor, int noEntryValue)
  {
    super(initialCapacity, loadFactor);
    no_entry_value = noEntryValue;
    
    if (no_entry_value != 0) {
      Arrays.fill(_values, no_entry_value);
    }
  }
  






  public TObjectIntHashMap(TObjectIntMap<K> map)
  {
    this(map.size(), 0.5F, map.getNoEntryValue());
    if ((map instanceof TObjectIntHashMap)) {
      TObjectIntHashMap hashmap = (TObjectIntHashMap)map;
      _loadFactor = _loadFactor;
      no_entry_value = no_entry_value;
      
      if (no_entry_value != 0) {
        Arrays.fill(_values, no_entry_value);
      }
      setUp((int)Math.ceil(10.0F / _loadFactor));
    }
    putAll(map);
  }
  









  public int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _values = new int[capacity];
    return capacity;
  }
  





  protected void rehash(int newCapacity)
  {
    int oldCapacity = _set.length;
    
    K[] oldKeys = (Object[])_set;
    int[] oldVals = _values;
    
    _set = new Object[newCapacity];
    Arrays.fill(_set, FREE);
    _values = new int[newCapacity];
    Arrays.fill(_values, no_entry_value);
    
    for (int i = oldCapacity; i-- > 0;) {
      if ((oldKeys[i] != FREE) && (oldKeys[i] != REMOVED)) {
        K o = oldKeys[i];
        int index = insertionIndex(o);
        if (index < 0) {
          throwObjectContractViolation(_set[(-index - 1)], o);
        }
        _set[index] = o;
        _values[index] = oldVals[i];
      }
    }
  }
  



  public int getNoEntryValue()
  {
    return no_entry_value;
  }
  

  public boolean containsKey(Object key)
  {
    return contains(key);
  }
  

  public boolean containsValue(int val)
  {
    Object[] keys = _set;
    int[] vals = _values;
    
    for (int i = vals.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED) && (val == vals[i])) {
        return true;
      }
    }
    return false;
  }
  

  public int get(Object key)
  {
    int index = index(key);
    return index < 0 ? no_entry_value : _values[index];
  }
  



  public int put(K key, int value)
  {
    int index = insertionIndex(key);
    return doPut(key, value, index);
  }
  

  public int putIfAbsent(K key, int value)
  {
    int index = insertionIndex(key);
    if (index < 0)
      return _values[(-index - 1)];
    return doPut(key, value, index);
  }
  
  private int doPut(K key, int value, int index)
  {
    int previous = no_entry_value;
    boolean isNewMapping = true;
    if (index < 0) {
      index = -index - 1;
      previous = _values[index];
      isNewMapping = false;
    }
    
    K oldKey = _set[index];
    _set[index] = key;
    _values[index] = value;
    
    if (isNewMapping) {
      postInsertHook(oldKey == FREE);
    }
    return previous;
  }
  

  public int remove(Object key)
  {
    int prev = no_entry_value;
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
  



  public void putAll(Map<? extends K, ? extends Integer> map)
  {
    Set<? extends Map.Entry<? extends K, ? extends Integer>> set = map.entrySet();
    for (Map.Entry<? extends K, ? extends Integer> entry : set) {
      put(entry.getKey(), ((Integer)entry.getValue()).intValue());
    }
  }
  

  public void putAll(TObjectIntMap<K> map)
  {
    map.forEachEntry(PUT_ALL_PROC);
  }
  

  public void clear()
  {
    super.clear();
    Arrays.fill(_set, 0, _set.length, FREE);
    Arrays.fill(_values, 0, _values.length, no_entry_value);
  }
  



  public Set<K> keySet()
  {
    return new KeyView();
  }
  


  public Object[] keys()
  {
    K[] keys = (Object[])new Object[size()];
    Object[] k = _set;
    
    int i = k.length; for (int j = 0; i-- > 0;) {
      if ((k[i] != FREE) && (k[i] != REMOVED))
      {
        keys[(j++)] = k[i];
      }
    }
    return keys;
  }
  

  public K[] keys(K[] a)
  {
    int size = size();
    if (a.length < size)
    {
      a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
    }
    

    Object[] k = _set;
    
    int i = k.length; for (int j = 0; i-- > 0;) {
      if ((k[i] != FREE) && (k[i] != REMOVED))
      {
        a[(j++)] = k[i];
      }
    }
    return a;
  }
  

  public TIntCollection valueCollection()
  {
    return new TIntValueCollection();
  }
  

  public int[] values()
  {
    int[] vals = new int[size()];
    int[] v = _values;
    Object[] keys = _set;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED)) {
        vals[(j++)] = v[i];
      }
    }
    return vals;
  }
  

  public int[] values(int[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new int[size];
    }
    
    int[] v = _values;
    Object[] keys = _set;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED)) {
        array[(j++)] = v[i];
      }
    }
    if (array.length > size) {
      array[size] = no_entry_value;
    }
    return array;
  }
  



  public TObjectIntIterator<K> iterator()
  {
    return new TObjectIntHashIterator(this);
  }
  



  public boolean increment(K key)
  {
    return adjustValue(key, 1);
  }
  

  public boolean adjustValue(K key, int amount)
  {
    int index = index(key);
    if (index < 0) {
      return false;
    }
    _values[index] += amount;
    return true;
  }
  


  public int adjustOrPutValue(K key, int adjust_amount, int put_amount)
  {
    int index = insertionIndex(key);
    boolean isNewMapping;
    int newValue;
    boolean isNewMapping; if (index < 0) {
      index = -index - 1;
      int newValue = _values[index] += adjust_amount;
      isNewMapping = false;
    } else {
      newValue = _values[index] = put_amount;
      isNewMapping = true;
    }
    

    K oldKey = _set[index];
    _set[index] = key;
    
    if (isNewMapping) {
      postInsertHook(oldKey == FREE);
    }
    
    return newValue;
  }
  







  public boolean forEachKey(TObjectProcedure<K> procedure)
  {
    return forEach(procedure);
  }
  







  public boolean forEachValue(TIntProcedure procedure)
  {
    Object[] keys = _set;
    int[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED) && (!procedure.execute(values[i])))
      {
        return false;
      }
    }
    return true;
  }
  









  public boolean forEachEntry(TObjectIntProcedure<K> procedure)
  {
    Object[] keys = _set;
    int[] values = _values;
    for (int i = keys.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED) && (!procedure.execute(keys[i], values[i])))
      {

        return false;
      }
    }
    return true;
  }
  







  public boolean retainEntries(TObjectIntProcedure<K> procedure)
  {
    boolean modified = false;
    
    K[] keys = (Object[])_set;
    int[] values = _values;
    

    tempDisableAutoCompaction();
    try {
      for (i = keys.length; i-- > 0;) {
        if ((keys[i] != FREE) && (keys[i] != REMOVED) && (!procedure.execute(keys[i], values[i])))
        {

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
  





  public void transformValues(TIntFunction function)
  {
    Object[] keys = _set;
    int[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((keys[i] != null) && (keys[i] != REMOVED)) {
        values[i] = function.execute(values[i]);
      }
    }
  }
  









  public boolean equals(Object other)
  {
    if (!(other instanceof TObjectIntMap)) {
      return false;
    }
    TObjectIntMap that = (TObjectIntMap)other;
    if (that.size() != size()) {
      return false;
    }
    try {
      TObjectIntIterator iter = iterator();
      while (iter.hasNext()) {
        iter.advance();
        Object key = iter.key();
        int value = iter.value();
        if (value == no_entry_value) {
          if ((that.get(key) != that.getNoEntryValue()) || (!that.containsKey(key))) {
            return false;
          }
        }
        else if (value != that.get(key)) {
          return false;
        }
      }
    }
    catch (ClassCastException ex) {}
    

    return true;
  }
  

  public int hashCode()
  {
    int hashcode = 0;
    Object[] keys = _set;
    int[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED)) {
        hashcode += (HashFunctions.hash(values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode()));
      }
    }
    
    return hashcode;
  }
  
  protected class KeyView extends TObjectIntHashMap<K>.MapBackedView<K> {
    protected KeyView() {
      super(null);
    }
    
    public Iterator<K> iterator() {
      return new TObjectHashIterator(TObjectIntHashMap.this);
    }
    
    public boolean removeElement(K key) {
      return no_entry_value != remove(key);
    }
    
    public boolean containsElement(K key) {
      return contains(key);
    }
  }
  
  private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E>
  {
    private MapBackedView() {}
    
    public abstract boolean removeElement(E paramE);
    
    public abstract boolean containsElement(E paramE);
    
    public boolean contains(Object key)
    {
      return containsElement(key);
    }
    
    public boolean remove(Object o)
    {
      return removeElement(o);
    }
    
    public void clear() {
      TObjectIntHashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return TObjectIntHashMap.this.size();
    }
    
    public Object[] toArray() {
      Object[] result = new Object[size()];
      Iterator<E> e = iterator();
      for (int i = 0; e.hasNext(); i++) {
        result[i] = e.next();
      }
      return result;
    }
    
    public <T> T[] toArray(T[] a) {
      int size = size();
      if (a.length < size)
      {
        a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
      }
      
      Iterator<E> it = iterator();
      Object[] result = a;
      for (int i = 0; i < size; i++) {
        result[i] = it.next();
      }
      
      if (a.length > size) {
        a[size] = null;
      }
      
      return a;
    }
    
    public boolean isEmpty() {
      return TObjectIntHashMap.this.isEmpty();
    }
    
    public boolean addAll(Collection<? extends E> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection)
    {
      boolean changed = false;
      Iterator<E> i = iterator();
      while (i.hasNext()) {
        if (!collection.contains(i.next())) {
          i.remove();
          changed = true;
        }
      }
      return changed;
    }
  }
  
  class TIntValueCollection implements TIntCollection
  {
    TIntValueCollection() {}
    
    public TIntIterator iterator() {
      return new TObjectIntValueHashIterator();
    }
    
    public int getNoEntryValue()
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
    
    public boolean contains(int entry)
    {
      return containsValue(entry);
    }
    
    public int[] toArray()
    {
      return values();
    }
    
    public int[] toArray(int[] dest)
    {
      return values(dest);
    }
    
    public boolean add(int entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(int entry)
    {
      int[] values = _values;
      Object[] set = _set;
      
      for (int i = values.length; i-- > 0;) {
        if ((set[i] != TObjectHash.FREE) && (set[i] != TObjectHash.REMOVED) && (entry == values[i])) {
          removeAt(i);
          return true;
        }
      }
      return false;
    }
    
    public boolean containsAll(Collection<?> collection)
    {
      for (Object element : collection) {
        if ((element instanceof Integer)) {
          int ele = ((Integer)element).intValue();
          if (!containsValue(ele)) {
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
        if (!containsValue(iter.next())) {
          return false;
        }
      }
      return true;
    }
    
    public boolean containsAll(int[] array)
    {
      for (int element : array) {
        if (!containsValue(element)) {
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
      int[] values = _values;
      
      Object[] set = _set;
      for (int i = set.length; i-- > 0;) {
        if ((set[i] != TObjectHash.FREE) && (set[i] != TObjectHash.REMOVED) && (Arrays.binarySearch(array, values[i]) < 0))
        {

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
      TObjectIntHashMap.this.clear();
    }
    
    public boolean forEach(TIntProcedure procedure)
    {
      return forEachValue(procedure);
    }
    

    public String toString()
    {
      final StringBuilder buf = new StringBuilder("{");
      forEachValue(new TIntProcedure() {
        private boolean first = true;
        
        public boolean execute(int value) {
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
    
    class TObjectIntValueHashIterator
      implements TIntIterator
    {
      protected THash _hash = TObjectIntHashMap.this;
      


      protected int _expectedSize;
      

      protected int _index;
      


      TObjectIntValueHashIterator()
      {
        _expectedSize = _hash.size();
        _index = _hash.capacity();
      }
      
      public boolean hasNext()
      {
        return nextIndex() >= 0;
      }
      
      public int next()
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
      





      protected final void moveToNextIndex()
      {
        if ((this._index = nextIndex()) < 0) {
          throw new NoSuchElementException();
        }
      }
      









      protected final int nextIndex()
      {
        if (_expectedSize != _hash.size()) {
          throw new ConcurrentModificationException();
        }
        
        Object[] set = _set;
        int i = _index;
        while ((i-- > 0) && ((set[i] == TObjectHash.FREE) || (set[i] == TObjectHash.REMOVED))) {}
        

        return i;
      }
    }
  }
  
  class TObjectIntHashIterator<K>
    extends TObjectHashIterator<K>
    implements TObjectIntIterator<K>
  {
    private final TObjectIntHashMap<K> _map;
    
    public TObjectIntHashIterator()
    {
      super();
      _map = map;
    }
    
    public void advance()
    {
      moveToNextIndex();
    }
    

    public K key()
    {
      return _map._set[_index];
    }
    
    public int value()
    {
      return _map._values[_index];
    }
    
    public int setValue(int val)
    {
      int old = value();
      _map._values[_index] = val;
      return old;
    }
  }
  


  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    super.writeExternal(out);
    

    out.writeInt(no_entry_value);
    

    out.writeInt(_size);
    

    for (int i = _set.length; i-- > 0;) {
      if ((_set[i] != REMOVED) && (_set[i] != FREE)) {
        out.writeObject(_set[i]);
        out.writeInt(_values[i]);
      }
    }
  }
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    super.readExternal(in);
    

    no_entry_value = in.readInt();
    

    int size = in.readInt();
    setUp(size);
    

    while (size-- > 0)
    {
      K key = in.readObject();
      int val = in.readInt();
      put(key, val);
    }
  }
  

  public String toString()
  {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TObjectIntProcedure() {
      private boolean first = true;
      
      public boolean execute(K key, int value) { if (first) first = false; else {
          buf.append(",");
        }
        buf.append(key).append("=").append(value);
        return true;
      }
    });
    buf.append("}");
    return buf.toString();
  }
}
