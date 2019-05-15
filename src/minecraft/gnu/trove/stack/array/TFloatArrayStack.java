package gnu.trove.stack.array;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.stack.TFloatStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;






































public class TFloatArrayStack
  implements TFloatStack, Externalizable
{
  protected TFloatArrayList _list;
  public static final int DEFAULT_CAPACITY = 10;
  
  public TFloatArrayStack()
  {
    this(10);
  }
  






  public TFloatArrayStack(int capacity)
  {
    _list = new TFloatArrayList(capacity);
  }
  







  public TFloatArrayStack(int capacity, float no_entry_value)
  {
    _list = new TFloatArrayList(capacity, no_entry_value);
  }
  






  public TFloatArrayStack(TFloatStack stack)
  {
    if ((stack instanceof TFloatArrayStack)) {
      TFloatArrayStack array_stack = (TFloatArrayStack)stack;
      _list = new TFloatArrayList(_list);
    } else {
      throw new UnsupportedOperationException("Only support TFloatArrayStack");
    }
  }
  







  public float getNoEntryValue()
  {
    return _list.getNoEntryValue();
  }
  





  public void push(float val)
  {
    _list.add(val);
  }
  





  public float pop()
  {
    return _list.removeAt(_list.size() - 1);
  }
  





  public float peek()
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
  






  public float[] toArray()
  {
    float[] retval = _list.toArray();
    reverse(retval, 0, size());
    return retval;
  }
  











  public void toArray(float[] dest)
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
  







  private void reverse(float[] dest, int from, int to)
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
  







  private void swap(float[] dest, int i, int j)
  {
    float tmp = dest[i];
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
    
    TFloatArrayStack that = (TFloatArrayStack)o;
    
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
    

    _list = ((TFloatArrayList)in.readObject());
  }
}
