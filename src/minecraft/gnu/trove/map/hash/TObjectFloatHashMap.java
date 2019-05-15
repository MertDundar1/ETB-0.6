package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
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


























public class TObjectFloatHashMap<K>
  extends TObjectHash<K>
  implements TObjectFloatMap<K>, Externalizable
{
  static final long serialVersionUID = 1L;
  private final TObjectFloatProcedure<K> PUT_ALL_PROC = new TObjectFloatProcedure() {
    public boolean execute(K key, float value) {
      put(key, value);
      return true;
    }
  };
  




  protected transient float[] _values;
  




  protected float no_entry_value;
  




  public TObjectFloatHashMap() {}
  




  public TObjectFloatHashMap(int initialCapacity)
  {
    super(initialCapacity);
    no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
  }
  








  public TObjectFloatHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
    no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
  }
  









  public TObjectFloatHashMap(int initialCapacity, float loadFactor, float noEntryValue)
  {
    super(initialCapacity, loadFactor);
    no_entry_value = noEntryValue;
    
    if (no_entry_value != 0.0F) {
      Arrays.fill(_values, no_entry_value);
    }
  }
  






  public TObjectFloatHashMap(TObjectFloatMap<K> map)
  {
    this(map.size(), 0.5F, map.getNoEntryValue());
    if ((map instanceof TObjectFloatHashMap)) {
      TObjectFloatHashMap hashmap = (TObjectFloatHashMap)map;
      _loadFactor = _loadFactor;
      no_entry_value = no_entry_value;
      
      if (no_entry_value != 0.0F) {
        Arrays.fill(_values, no_entry_value);
      }
      setUp((int)Math.ceil(10.0F / _loadFactor));
    }
    putAll(map);
  }
  









  public int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _values = new float[capacity];
    return capacity;
  }
  





  protected void rehash(int newCapacity)
  {
    int oldCapacity = _set.length;
    
    K[] oldKeys = (Object[])_set;
    float[] oldVals = _values;
    
    _set = new Object[newCapacity];
    Arrays.fill(_set, FREE);
    _values = new float[newCapacity];
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
  



  public float getNoEntryValue()
  {
    return no_entry_value;
  }
  

  public boolean containsKey(Object key)
  {
    return contains(key);
  }
  

  public boolean containsValue(float val)
  {
    Object[] keys = _set;
    float[] vals = _values;
    
    for (int i = vals.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED) && (val == vals[i])) {
        return true;
      }
    }
    return false;
  }
  

  public float get(Object key)
  {
    int index = index(key);
    return index < 0 ? no_entry_value : _values[index];
  }
  



  public float put(K key, float value)
  {
    int index = insertionIndex(key);
    return doPut(key, value, index);
  }
  

  public float putIfAbsent(K key, float value)
  {
    int index = insertionIndex(key);
    if (index < 0)
      return _values[(-index - 1)];
    return doPut(key, value, index);
  }
  
  private float doPut(K key, float value, int index)
  {
    float previous = no_entry_value;
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
  

  public float remove(Object key)
  {
    float prev = no_entry_value;
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
  



  public void putAll(Map<? extends K, ? extends Float> map)
  {
    Set<? extends Map.Entry<? extends K, ? extends Float>> set = map.entrySet();
    for (Map.Entry<? extends K, ? extends Float> entry : set) {
      put(entry.getKey(), ((Float)entry.getValue()).floatValue());
    }
  }
  

  public void putAll(TObjectFloatMap<K> map)
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
  

  public TFloatCollection valueCollection()
  {
    return new TFloatValueCollection();
  }
  

  public float[] values()
  {
    float[] vals = new float[size()];
    float[] v = _values;
    Object[] keys = _set;
    
    int i = v.length; for (int j = 0; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED)) {
        vals[(j++)] = v[i];
      }
    }
    return vals;
  }
  

  public float[] values(float[] array)
  {
    int size = size();
    if (array.length < size) {
      array = new float[size];
    }
    
    float[] v = _values;
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
  



  public TObjectFloatIterator<K> iterator()
  {
    return new TObjectFloatHashIterator(this);
  }
  



  public boolean increment(K key)
  {
    return adjustValue(key, 1.0F);
  }
  

  public boolean adjustValue(K key, float amount)
  {
    int index = index(key);
    if (index < 0) {
      return false;
    }
    _values[index] += amount;
    return true;
  }
  


  public float adjustOrPutValue(K key, float adjust_amount, float put_amount)
  {
    int index = insertionIndex(key);
    boolean isNewMapping;
    float newValue;
    boolean isNewMapping; if (index < 0) {
      index = -index - 1;
      float newValue = _values[index] += adjust_amount;
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
  







  public boolean forEachValue(TFloatProcedure procedure)
  {
    Object[] keys = _set;
    float[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED) && (!procedure.execute(values[i])))
      {
        return false;
      }
    }
    return true;
  }
  









  public boolean forEachEntry(TObjectFloatProcedure<K> procedure)
  {
    Object[] keys = _set;
    float[] values = _values;
    for (int i = keys.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED) && (!procedure.execute(keys[i], values[i])))
      {

        return false;
      }
    }
    return true;
  }
  







  public boolean retainEntries(TObjectFloatProcedure<K> procedure)
  {
    boolean modified = false;
    
    K[] keys = (Object[])_set;
    float[] values = _values;
    

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
  





  public void transformValues(TFloatFunction function)
  {
    Object[] keys = _set;
    float[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((keys[i] != null) && (keys[i] != REMOVED)) {
        values[i] = function.execute(values[i]);
      }
    }
  }
  









  public boolean equals(Object other)
  {
    if (!(other instanceof TObjectFloatMap)) {
      return false;
    }
    TObjectFloatMap that = (TObjectFloatMap)other;
    if (that.size() != size()) {
      return false;
    }
    try {
      TObjectFloatIterator iter = iterator();
      while (iter.hasNext()) {
        iter.advance();
        Object key = iter.key();
        float value = iter.value();
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
    float[] values = _values;
    for (int i = values.length; i-- > 0;) {
      if ((keys[i] != FREE) && (keys[i] != REMOVED)) {
        hashcode += (HashFunctions.hash(values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode()));
      }
    }
    
    return hashcode;
  }
  
  protected class KeyView extends TObjectFloatHashMap<K>.MapBackedView<K> {
    protected KeyView() {
      super(null);
    }
    
    public Iterator<K> iterator() {
      return new TObjectHashIterator(TObjectFloatHashMap.this);
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
      TObjectFloatHashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return TObjectFloatHashMap.this.size();
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
      return TObjectFloatHashMap.this.isEmpty();
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
  
  class TFloatValueCollection implements TFloatCollection
  {
    TFloatValueCollection() {}
    
    public TFloatIterator iterator() {
      return new TObjectFloatValueHashIterator();
    }
    
    public float getNoEntryValue()
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
    
    public boolean contains(float entry)
    {
      return containsValue(entry);
    }
    
    public float[] toArray()
    {
      return values();
    }
    
    public float[] toArray(float[] dest)
    {
      return values(dest);
    }
    
    public boolean add(float entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(float entry)
    {
      float[] values = _values;
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
        if ((element instanceof Float)) {
          float ele = ((Float)element).floatValue();
          if (!containsValue(ele)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
    
    public boolean containsAll(TFloatCollection collection)
    {
      TFloatIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!containsValue(iter.next())) {
          return false;
        }
      }
      return true;
    }
    
    public boolean containsAll(float[] array)
    {
      for (float element : array) {
        if (!containsValue(element)) {
          return false;
        }
      }
      return true;
    }
    
    public boolean addAll(Collection<? extends Float> collection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TFloatCollection collection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(float[] array)
    {
      throw new UnsupportedOperationException();
    }
    

    public boolean retainAll(Collection<?> collection)
    {
      boolean modified = false;
      TFloatIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Float.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    
    public boolean retainAll(TFloatCollection collection)
    {
      if (this == collection) {
        return false;
      }
      boolean modified = false;
      TFloatIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        }
      }
      return modified;
    }
    
    public boolean retainAll(float[] array)
    {
      boolean changed = false;
      Arrays.sort(array);
      float[] values = _values;
      
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
        if ((element instanceof Float)) {
          float c = ((Float)element).floatValue();
          if (remove(c)) {
            changed = true;
          }
        }
      }
      return changed;
    }
    
    public boolean removeAll(TFloatCollection collection)
    {
      if (this == collection) {
        clear();
        return true;
      }
      boolean changed = false;
      TFloatIterator iter = collection.iterator();
      while (iter.hasNext()) {
        float element = iter.next();
        if (remove(element)) {
          changed = true;
        }
      }
      return changed;
    }
    
    public boolean removeAll(float[] array)
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
      TObjectFloatHashMap.this.clear();
    }
    
    public boolean forEach(TFloatProcedure procedure)
    {
      return forEachValue(procedure);
    }
    

    public String toString()
    {
      final StringBuilder buf = new StringBuilder("{");
      forEachValue(new TFloatProcedure() {
        private boolean first = true;
        
        public boolean execute(float value) {
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
    
    class TObjectFloatValueHashIterator
      implements TFloatIterator
    {
      protected THash _hash = TObjectFloatHashMap.this;
      


      protected int _expectedSize;
      

      protected int _index;
      


      TObjectFloatValueHashIterator()
      {
        _expectedSize = _hash.size();
        _index = _hash.capacity();
      }
      
      public boolean hasNext()
      {
        return nextIndex() >= 0;
      }
      
      public float next()
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
  
  class TObjectFloatHashIterator<K>
    extends TObjectHashIterator<K>
    implements TObjectFloatIterator<K>
  {
    private final TObjectFloatHashMap<K> _map;
    
    public TObjectFloatHashIterator()
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
    
    public float value()
    {
      return _map._values[_index];
    }
    
    public float setValue(float val)
    {
      float old = value();
      _map._values[_index] = val;
      return old;
    }
  }
  


  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    super.writeExternal(out);
    

    out.writeFloat(no_entry_value);
    

    out.writeInt(_size);
    

    for (int i = _set.length; i-- > 0;) {
      if ((_set[i] != REMOVED) && (_set[i] != FREE)) {
        out.writeObject(_set[i]);
        out.writeFloat(_values[i]);
      }
    }
  }
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    super.readExternal(in);
    

    no_entry_value = in.readFloat();
    

    int size = in.readInt();
    setUp(size);
    

    while (size-- > 0)
    {
      K key = in.readObject();
      float val = in.readFloat();
      put(key, val);
    }
  }
  

  public String toString()
  {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TObjectFloatProcedure() {
      private boolean first = true;
      
      public boolean execute(K key, float value) { if (first) first = false; else {
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
