package gnu.trove.stack.array;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.stack.TShortStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;






































public class TShortArrayStack
  implements TShortStack, Externalizable
{
  protected TShortArrayList _list;
  public static final int DEFAULT_CAPACITY = 10;
  
  public TShortArrayStack()
  {
    this(10);
  }
  






  public TShortArrayStack(int capacity)
  {
    _list = new TShortArrayList(capacity);
  }
  







  public TShortArrayStack(int capacity, short no_entry_value)
  {
    _list = new TShortArrayList(capacity, no_entry_value);
  }
  






  public TShortArrayStack(TShortStack stack)
  {
    if ((stack instanceof TShortArrayStack)) {
      TShortArrayStack array_stack = (TShortArrayStack)stack;
      _list = new TShortArrayList(_list);
    } else {
      throw new UnsupportedOperationException("Only support TShortArrayStack");
    }
  }
  







  public short getNoEntryValue()
  {
    return _list.getNoEntryValue();
  }
  





  public void push(short val)
  {
    _list.add(val);
  }
  





  public short pop()
  {
    return _list.removeAt(_list.size() - 1);
  }
  





  public short peek()
  {
    return _list.get(_list.size() - 1);
  }
  



  public int size()
  {
    return _list.size();
  }
  



  public void clear()
  {
    _list.clear();
  }
  






  public short[] toArray()
  {
    short[] retval = _list.toArray();
    reverse(retval, 0, size());
    return retval;
  }
  











  public void toArray(short[] dest)
  {
    int size = size();
    int start = size - dest.length;
    if (start < 0) {
      start = 0;
    }
    
    int length = Math.min(size, dest.length);
    _list.toArray(dest, start, length);
    reverse(dest, 0, length);
    if (dest.length > size) {
      dest[size] = _list.getNoEntryValue();
    }
  }
  







  private void reverse(short[] dest, int from, int to)
  {
    if (from == to) {
      return;
    }
    if (from > to) {
      throw new IllegalArgumentException("from cannot be greater than to");
    }
    int i = from; for (int j = to - 1; i < j; j--) {
      swap(dest, i, j);i++;
    }
  }
  







  private void swap(short[] dest, int i, int j)
  {
    short tmp = dest[i];
    dest[i] = dest[j];
    dest[j] = tmp;
  }
  





  public String toString()
  {
    StringBuilder buf = new StringBuilder("{");
    for (int i = _list.size() - 1; i > 0; i--) {
      buf.append(_list.get(i));
      buf.append(", ");
    }
    if (size() > 0) {
      buf.append(_list.get(0));
    }
    buf.append("}");
    return buf.toString();
  }
  
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    
    TShortArrayStack that = (TShortArrayStack)o;
    
    return _list.equals(_list);
  }
  
  public int hashCode()
  {
    return _list.hashCode();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_list);
  }
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _list = ((TShortArrayList)in.readObject());
  }
}
