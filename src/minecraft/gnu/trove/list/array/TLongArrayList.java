package gnu.trove.list.array;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;






































public class TLongArrayList
  implements TLongList, Externalizable
{
  static final long serialVersionUID = 1L;
  protected long[] _data;
  protected int _pos;
  protected static final int DEFAULT_CAPACITY = 10;
  protected long no_entry_value;
  
  public TLongArrayList()
  {
    this(10, 0L);
  }
  







  public TLongArrayList(int capacity)
  {
    this(capacity, 0L);
  }
  







  public TLongArrayList(int capacity, long no_entry_value)
  {
    _data = new long[capacity];
    _pos = 0;
    this.no_entry_value = no_entry_value;
  }
  





  public TLongArrayList(TLongCollection collection)
  {
    this(collection.size());
    addAll(collection);
  }
  







  public TLongArrayList(long[] values)
  {
    this(values.length);
    add(values);
  }
  

  public long getNoEntryValue()
  {
    return no_entry_value;
  }
  







  public void ensureCapacity(int capacity)
  {
    if (capacity > _data.length) {
      int newCap = Math.max(_data.length << 1, capacity);
      long[] tmp = new long[newCap];
      System.arraycopy(_data, 0, tmp, 0, _data.length);
      _data = tmp;
    }
  }
  

  public int size()
  {
    return _pos;
  }
  

  public boolean isEmpty()
  {
    return _pos == 0;
  }
  



  public void trimToSize()
  {
    if (_data.length > size()) {
      long[] tmp = new long[size()];
      toArray(tmp, 0, tmp.length);
      _data = tmp;
    }
  }
  



  public boolean add(long val)
  {
    ensureCapacity(_pos + 1);
    _data[(_pos++)] = val;
    return true;
  }
  

  public void add(long[] vals)
  {
    add(vals, 0, vals.length);
  }
  

  public void add(long[] vals, int offset, int length)
  {
    ensureCapacity(_pos + length);
    System.arraycopy(vals, offset, _data, _pos, length);
    _pos += length;
  }
  

  public void insert(int offset, long value)
  {
    if (offset == _pos) {
      add(value);
      return;
    }
    ensureCapacity(_pos + 1);
    
    System.arraycopy(_data, offset, _data, offset + 1, _pos - offset);
    
    _data[offset] = value;
    _pos += 1;
  }
  

  public void insert(int offset, long[] values)
  {
    insert(offset, values, 0, values.length);
  }
  

  public void insert(int offset, long[] values, int valOffset, int len)
  {
    if (offset == _pos) {
      add(values, valOffset, len);
      return;
    }
    
    ensureCapacity(_pos + len);
    
    System.arraycopy(_data, offset, _data, offset + len, _pos - offset);
    
    System.arraycopy(values, valOffset, _data, offset, len);
    _pos += len;
  }
  

  public long get(int offset)
  {
    if (offset >= _pos) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    return _data[offset];
  }
  



  public long getQuick(int offset)
  {
    return _data[offset];
  }
  

  public void set(int offset, long val)
  {
    if (offset >= _pos) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    _data[offset] = val;
  }
  

  public long replace(int offset, long val)
  {
    if (offset >= _pos) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    long old = _data[offset];
    _data[offset] = val;
    return old;
  }
  

  public void set(int offset, long[] values)
  {
    set(offset, values, 0, values.length);
  }
  

  public void set(int offset, long[] values, int valOffset, int length)
  {
    if ((offset < 0) || (offset + length > _pos)) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    System.arraycopy(values, valOffset, _data, offset, length);
  }
  



  public void setQuick(int offset, long val)
  {
    _data[offset] = val;
  }
  

  public void clear()
  {
    clear(10);
  }
  




  public void clear(int capacity)
  {
    _data = new long[capacity];
    _pos = 0;
  }
  





  public void reset()
  {
    _pos = 0;
    Arrays.fill(_data, no_entry_value);
  }
  








  public void resetQuick()
  {
    _pos = 0;
  }
  

  public boolean remove(long value)
  {
    for (int index = 0; index < _pos; index++) {
      if (value == _data[index]) {
        remove(index, 1);
        return true;
      }
    }
    return false;
  }
  

  public long removeAt(int offset)
  {
    long old = get(offset);
    remove(offset, 1);
    return old;
  }
  

  public void remove(int offset, int length)
  {
    if ((offset < 0) || (offset >= _pos)) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    
    if (offset == 0)
    {
      System.arraycopy(_data, length, _data, 0, _pos - length);
    }
    else if (_pos - length != offset)
    {




      System.arraycopy(_data, offset + length, _data, offset, _pos - (offset + length));
    }
    
    _pos -= length;
  }
  




  public TLongIterator iterator()
  {
    return new TLongArrayIterator(0);
  }
  

  public boolean containsAll(Collection<?> collection)
  {
    for (Object element : collection) {
      if ((element instanceof Long)) {
        long c = ((Long)element).longValue();
        if (!contains(c)) {
          return false;
        }
      } else {
        return false;
      }
    }
    
    return true;
  }
  

  public boolean containsAll(TLongCollection collection)
  {
    if (this == collection) {
      return true;
    }
    TLongIterator iter = collection.iterator();
    while (iter.hasNext()) {
      long element = iter.next();
      if (!contains(element)) {
        return false;
      }
    }
    return true;
  }
  

  public boolean containsAll(long[] array)
  {
    for (int i = array.length; i-- > 0;) {
      if (!contains(array[i])) {
        return false;
      }
    }
    return true;
  }
  

  public boolean addAll(Collection<? extends Long> collection)
  {
    boolean changed = false;
    for (Long element : collection) {
      long e = element.longValue();
      if (add(e)) {
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean addAll(TLongCollection collection)
  {
    boolean changed = false;
    TLongIterator iter = collection.iterator();
    while (iter.hasNext()) {
      long element = iter.next();
      if (add(element)) {
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean addAll(long[] array)
  {
    boolean changed = false;
    for (long element : array) {
      if (add(element)) {
        changed = true;
      }
    }
    return changed;
  }
  


  public boolean retainAll(Collection<?> collection)
  {
    boolean modified = false;
    TLongIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(Long.valueOf(iter.next()))) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  

  public boolean retainAll(TLongCollection collection)
  {
    if (this == collection) {
      return false;
    }
    boolean modified = false;
    TLongIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(iter.next())) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  

  public boolean retainAll(long[] array)
  {
    boolean changed = false;
    Arrays.sort(array);
    long[] data = _data;
    
    for (int i = data.length; i-- > 0;) {
      if (Arrays.binarySearch(array, data[i]) < 0) {
        remove(i, 1);
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean removeAll(Collection<?> collection)
  {
    boolean changed = false;
    for (Object element : collection) {
      if ((element instanceof Long)) {
        long c = ((Long)element).longValue();
        if (remove(c)) {
          changed = true;
        }
      }
    }
    return changed;
  }
  

  public boolean removeAll(TLongCollection collection)
  {
    if (collection == this) {
      clear();
      return true;
    }
    boolean changed = false;
    TLongIterator iter = collection.iterator();
    while (iter.hasNext()) {
      long element = iter.next();
      if (remove(element)) {
        changed = true;
      }
    }
    return changed;
  }
  

  public boolean removeAll(long[] array)
  {
    boolean changed = false;
    for (int i = array.length; i-- > 0;) {
      if (remove(array[i])) {
        changed = true;
      }
    }
    return changed;
  }
  

  public void transformValues(TLongFunction function)
  {
    for (int i = _pos; i-- > 0;) {
      _data[i] = function.execute(_data[i]);
    }
  }
  

  public void reverse()
  {
    reverse(0, _pos);
  }
  

  public void reverse(int from, int to)
  {
    if (from == to) {
      return;
    }
    if (from > to) {
      throw new IllegalArgumentException("from cannot be greater than to");
    }
    int i = from; for (int j = to - 1; i < j; j--) {
      swap(i, j);i++;
    }
  }
  

  public void shuffle(Random rand)
  {
    for (int i = _pos; i-- > 1;) {
      swap(i, rand.nextInt(i));
    }
  }
  






  private void swap(int i, int j)
  {
    long tmp = _data[i];
    _data[i] = _data[j];
    _data[j] = tmp;
  }
  



  public TLongList subList(int begin, int end)
  {
    if (end < begin) {
      throw new IllegalArgumentException("end index " + end + " greater than begin index " + begin);
    }
    
    if (begin < 0) {
      throw new IndexOutOfBoundsException("begin index can not be < 0");
    }
    if (end > _data.length) {
      throw new IndexOutOfBoundsException("end index < " + _data.length);
    }
    TLongArrayList list = new TLongArrayList(end - begin);
    for (int i = begin; i < end; i++) {
      list.add(_data[i]);
    }
    return list;
  }
  

  public long[] toArray()
  {
    return toArray(0, _pos);
  }
  

  public long[] toArray(int offset, int len)
  {
    long[] rv = new long[len];
    toArray(rv, offset, len);
    return rv;
  }
  

  public long[] toArray(long[] dest)
  {
    int len = dest.length;
    if (dest.length > _pos) {
      len = _pos;
      dest[len] = no_entry_value;
    }
    toArray(dest, 0, len);
    return dest;
  }
  

  public long[] toArray(long[] dest, int offset, int len)
  {
    if (len == 0) {
      return dest;
    }
    if ((offset < 0) || (offset >= _pos)) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    System.arraycopy(_data, offset, dest, 0, len);
    return dest;
  }
  

  public long[] toArray(long[] dest, int source_pos, int dest_pos, int len)
  {
    if (len == 0) {
      return dest;
    }
    if ((source_pos < 0) || (source_pos >= _pos)) {
      throw new ArrayIndexOutOfBoundsException(source_pos);
    }
    System.arraycopy(_data, source_pos, dest, dest_pos, len);
    return dest;
  }
  




  public boolean equals(Object other)
  {
    if (other == this) {
      return true;
    }
    if ((other instanceof TLongArrayList)) {
      TLongArrayList that = (TLongArrayList)other;
      if (that.size() != size()) { return false;
      }
      for (int i = _pos; i-- > 0;) {
        if (_data[i] != _data[i]) {
          return false;
        }
      }
      return true;
    }
    
    return false;
  }
  


  public int hashCode()
  {
    int h = 0;
    for (int i = _pos; i-- > 0;) {
      h += HashFunctions.hash(_data[i]);
    }
    return h;
  }
  



  public boolean forEach(TLongProcedure procedure)
  {
    for (int i = 0; i < _pos; i++) {
      if (!procedure.execute(_data[i])) {
        return false;
      }
    }
    return true;
  }
  

  public boolean forEachDescending(TLongProcedure procedure)
  {
    for (int i = _pos; i-- > 0;) {
      if (!procedure.execute(_data[i])) {
        return false;
      }
    }
    return true;
  }
  



  public void sort()
  {
    Arrays.sort(_data, 0, _pos);
  }
  

  public void sort(int fromIndex, int toIndex)
  {
    Arrays.sort(_data, fromIndex, toIndex);
  }
  



  public void fill(long val)
  {
    Arrays.fill(_data, 0, _pos, val);
  }
  

  public void fill(int fromIndex, int toIndex, long val)
  {
    if (toIndex > _pos) {
      ensureCapacity(toIndex);
      _pos = toIndex;
    }
    Arrays.fill(_data, fromIndex, toIndex, val);
  }
  



  public int binarySearch(long value)
  {
    return binarySearch(value, 0, _pos);
  }
  

  public int binarySearch(long value, int fromIndex, int toIndex)
  {
    if (fromIndex < 0) {
      throw new ArrayIndexOutOfBoundsException(fromIndex);
    }
    if (toIndex > _pos) {
      throw new ArrayIndexOutOfBoundsException(toIndex);
    }
    
    int low = fromIndex;
    int high = toIndex - 1;
    
    while (low <= high) {
      int mid = low + high >>> 1;
      long midVal = _data[mid];
      
      if (midVal < value) {
        low = mid + 1;
      }
      else if (midVal > value) {
        high = mid - 1;
      }
      else {
        return mid;
      }
    }
    return -(low + 1);
  }
  

  public int indexOf(long value)
  {
    return indexOf(0, value);
  }
  

  public int indexOf(int offset, long value)
  {
    for (int i = offset; i < _pos; i++) {
      if (_data[i] == value) {
        return i;
      }
    }
    return -1;
  }
  

  public int lastIndexOf(long value)
  {
    return lastIndexOf(_pos, value);
  }
  

  public int lastIndexOf(int offset, long value)
  {
    for (int i = offset; i-- > 0;) {
      if (_data[i] == value) {
        return i;
      }
    }
    return -1;
  }
  

  public boolean contains(long value)
  {
    return lastIndexOf(value) >= 0;
  }
  

  public TLongList grep(TLongProcedure condition)
  {
    TLongArrayList list = new TLongArrayList();
    for (int i = 0; i < _pos; i++) {
      if (condition.execute(_data[i])) {
        list.add(_data[i]);
      }
    }
    return list;
  }
  

  public TLongList inverseGrep(TLongProcedure condition)
  {
    TLongArrayList list = new TLongArrayList();
    for (int i = 0; i < _pos; i++) {
      if (!condition.execute(_data[i])) {
        list.add(_data[i]);
      }
    }
    return list;
  }
  

  public long max()
  {
    if (size() == 0) {
      throw new IllegalStateException("cannot find maximum of an empty list");
    }
    long max = Long.MIN_VALUE;
    for (int i = 0; i < _pos; i++) {
      if (_data[i] > max) {
        max = _data[i];
      }
    }
    return max;
  }
  

  public long min()
  {
    if (size() == 0) {
      throw new IllegalStateException("cannot find minimum of an empty list");
    }
    long min = Long.MAX_VALUE;
    for (int i = 0; i < _pos; i++) {
      if (_data[i] < min) {
        min = _data[i];
      }
    }
    return min;
  }
  




  public String toString()
  {
    StringBuilder buf = new StringBuilder("{");
    int i = 0; for (int end = _pos - 1; i < end; i++) {
      buf.append(_data[i]);
      buf.append(", ");
    }
    if (size() > 0) {
      buf.append(_data[(_pos - 1)]);
    }
    buf.append("}");
    return buf.toString();
  }
  


  class TLongArrayIterator
    implements TLongIterator
  {
    private int cursor = 0;
    





    int lastRet = -1;
    
    TLongArrayIterator(int index)
    {
      cursor = index;
    }
    

    public boolean hasNext()
    {
      return cursor < size();
    }
    
    public long next()
    {
      try
      {
        long next = get(cursor);
        lastRet = (cursor++);
        return next;
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }
    

    public void remove()
    {
      if (lastRet == -1) {
        throw new IllegalStateException();
      }
      try {
        remove(lastRet, 1);
        if (lastRet < cursor)
          cursor -= 1;
        lastRet = -1;
      } catch (IndexOutOfBoundsException e) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeInt(_pos);
    

    out.writeLong(no_entry_value);
    

    int len = _data.length;
    out.writeInt(len);
    for (int i = 0; i < len; i++) {
      out.writeLong(_data[i]);
    }
  }
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _pos = in.readInt();
    

    no_entry_value = in.readLong();
    

    int len = in.readInt();
    _data = new long[len];
    for (int i = 0; i < len; i++) {
      _data[i] = in.readLong();
    }
  }
}
