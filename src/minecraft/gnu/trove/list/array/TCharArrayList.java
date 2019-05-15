package gnu.trove.list.array;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;






































public class TCharArrayList
  implements TCharList, Externalizable
{
  static final long serialVersionUID = 1L;
  protected char[] _data;
  protected int _pos;
  protected static final int DEFAULT_CAPACITY = 10;
  protected char no_entry_value;
  
  public TCharArrayList()
  {
    this(10, '\000');
  }
  







  public TCharArrayList(int capacity)
  {
    this(capacity, '\000');
  }
  







  public TCharArrayList(int capacity, char no_entry_value)
  {
    _data = new char[capacity];
    _pos = 0;
    this.no_entry_value = no_entry_value;
  }
  





  public TCharArrayList(TCharCollection collection)
  {
    this(collection.size());
    addAll(collection);
  }
  







  public TCharArrayList(char[] values)
  {
    this(values.length);
    add(values);
  }
  

  public char getNoEntryValue()
  {
    return no_entry_value;
  }
  







  public void ensureCapacity(int capacity)
  {
    if (capacity > _data.length) {
      int newCap = Math.max(_data.length << 1, capacity);
      char[] tmp = new char[newCap];
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
      char[] tmp = new char[size()];
      toArray(tmp, 0, tmp.length);
      _data = tmp;
    }
  }
  



  public boolean add(char val)
  {
    ensureCapacity(_pos + 1);
    _data[(_pos++)] = val;
    return true;
  }
  

  public void add(char[] vals)
  {
    add(vals, 0, vals.length);
  }
  

  public void add(char[] vals, int offset, int length)
  {
    ensureCapacity(_pos + length);
    System.arraycopy(vals, offset, _data, _pos, length);
    _pos += length;
  }
  

  public void insert(int offset, char value)
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
  

  public void insert(int offset, char[] values)
  {
    insert(offset, values, 0, values.length);
  }
  

  public void insert(int offset, char[] values, int valOffset, int len)
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
  

  public char get(int offset)
  {
    if (offset >= _pos) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    return _data[offset];
  }
  



  public char getQuick(int offset)
  {
    return _data[offset];
  }
  

  public void set(int offset, char val)
  {
    if (offset >= _pos) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    _data[offset] = val;
  }
  

  public char replace(int offset, char val)
  {
    if (offset >= _pos) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    char old = _data[offset];
    _data[offset] = val;
    return old;
  }
  

  public void set(int offset, char[] values)
  {
    set(offset, values, 0, values.length);
  }
  

  public void set(int offset, char[] values, int valOffset, int length)
  {
    if ((offset < 0) || (offset + length > _pos)) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    System.arraycopy(values, valOffset, _data, offset, length);
  }
  



  public void setQuick(int offset, char val)
  {
    _data[offset] = val;
  }
  

  public void clear()
  {
    clear(10);
  }
  




  public void clear(int capacity)
  {
    _data = new char[capacity];
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
  

  public boolean remove(char value)
  {
    for (int index = 0; index < _pos; index++) {
      if (value == _data[index]) {
        remove(index, 1);
        return true;
      }
    }
    return false;
  }
  

  public char removeAt(int offset)
  {
    char old = get(offset);
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
  




  public TCharIterator iterator()
  {
    return new TCharArrayIterator(0);
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
    if (this == collection) {
      return true;
    }
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
    for (char element : array) {
      if (add(element)) {
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
    char[] data = _data;
    
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
    if (collection == this) {
      clear();
      return true;
    }
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
  

  public void transformValues(TCharFunction function)
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
    char tmp = _data[i];
    _data[i] = _data[j];
    _data[j] = tmp;
  }
  



  public TCharList subList(int begin, int end)
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
    TCharArrayList list = new TCharArrayList(end - begin);
    for (int i = begin; i < end; i++) {
      list.add(_data[i]);
    }
    return list;
  }
  

  public char[] toArray()
  {
    return toArray(0, _pos);
  }
  

  public char[] toArray(int offset, int len)
  {
    char[] rv = new char[len];
    toArray(rv, offset, len);
    return rv;
  }
  

  public char[] toArray(char[] dest)
  {
    int len = dest.length;
    if (dest.length > _pos) {
      len = _pos;
      dest[len] = no_entry_value;
    }
    toArray(dest, 0, len);
    return dest;
  }
  

  public char[] toArray(char[] dest, int offset, int len)
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
  

  public char[] toArray(char[] dest, int source_pos, int dest_pos, int len)
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
    if ((other instanceof TCharArrayList)) {
      TCharArrayList that = (TCharArrayList)other;
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
  



  public boolean forEach(TCharProcedure procedure)
  {
    for (int i = 0; i < _pos; i++) {
      if (!procedure.execute(_data[i])) {
        return false;
      }
    }
    return true;
  }
  

  public boolean forEachDescending(TCharProcedure procedure)
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
  



  public void fill(char val)
  {
    Arrays.fill(_data, 0, _pos, val);
  }
  

  public void fill(int fromIndex, int toIndex, char val)
  {
    if (toIndex > _pos) {
      ensureCapacity(toIndex);
      _pos = toIndex;
    }
    Arrays.fill(_data, fromIndex, toIndex, val);
  }
  



  public int binarySearch(char value)
  {
    return binarySearch(value, 0, _pos);
  }
  

  public int binarySearch(char value, int fromIndex, int toIndex)
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
      char midVal = _data[mid];
      
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
  

  public int indexOf(char value)
  {
    return indexOf(0, value);
  }
  

  public int indexOf(int offset, char value)
  {
    for (int i = offset; i < _pos; i++) {
      if (_data[i] == value) {
        return i;
      }
    }
    return -1;
  }
  

  public int lastIndexOf(char value)
  {
    return lastIndexOf(_pos, value);
  }
  

  public int lastIndexOf(int offset, char value)
  {
    for (int i = offset; i-- > 0;) {
      if (_data[i] == value) {
        return i;
      }
    }
    return -1;
  }
  

  public boolean contains(char value)
  {
    return lastIndexOf(value) >= 0;
  }
  

  public TCharList grep(TCharProcedure condition)
  {
    TCharArrayList list = new TCharArrayList();
    for (int i = 0; i < _pos; i++) {
      if (condition.execute(_data[i])) {
        list.add(_data[i]);
      }
    }
    return list;
  }
  

  public TCharList inverseGrep(TCharProcedure condition)
  {
    TCharArrayList list = new TCharArrayList();
    for (int i = 0; i < _pos; i++) {
      if (!condition.execute(_data[i])) {
        list.add(_data[i]);
      }
    }
    return list;
  }
  

  public char max()
  {
    if (size() == 0) {
      throw new IllegalStateException("cannot find maximum of an empty list");
    }
    char max = '\000';
    for (int i = 0; i < _pos; i++) {
      if (_data[i] > max) {
        max = _data[i];
      }
    }
    return max;
  }
  

  public char min()
  {
    if (size() == 0) {
      throw new IllegalStateException("cannot find minimum of an empty list");
    }
    char min = 65535;
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
  


  class TCharArrayIterator
    implements TCharIterator
  {
    private int cursor = 0;
    





    int lastRet = -1;
    
    TCharArrayIterator(int index)
    {
      cursor = index;
    }
    

    public boolean hasNext()
    {
      return cursor < size();
    }
    
    public char next()
    {
      try
      {
        char next = get(cursor);
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
    

    out.writeChar(no_entry_value);
    

    int len = _data.length;
    out.writeInt(len);
    for (int i = 0; i < len; i++) {
      out.writeChar(_data[i]);
    }
  }
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _pos = in.readInt();
    

    no_entry_value = in.readChar();
    

    int len = in.readInt();
    _data = new char[len];
    for (int i = 0; i < len; i++) {
      _data[i] = in.readChar();
    }
  }
}
