package gnu.trove.stack.array;

import gnu.trove.list.array.TCharArrayList;
import gnu.trove.stack.TCharStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;






































public class TCharArrayStack
  implements TCharStack, Externalizable
{
  protected TCharArrayList _list;
  public static final int DEFAULT_CAPACITY = 10;
  
  public TCharArrayStack()
  {
    this(10);
  }
  






  public TCharArrayStack(int capacity)
  {
    _list = new TCharArrayList(capacity);
  }
  







  public TCharArrayStack(int capacity, char no_entry_value)
  {
    _list = new TCharArrayList(capacity, no_entry_value);
  }
  






  public TCharArrayStack(TCharStack stack)
  {
    if ((stack instanceof TCharArrayStack)) {
      TCharArrayStack array_stack = (TCharArrayStack)stack;
      _list = new TCharArrayList(_list);
    } else {
      throw new UnsupportedOperationException("Only support TCharArrayStack");
    }
  }
  







  public char getNoEntryValue()
  {
    return _list.getNoEntryValue();
  }
  





  public void push(char val)
  {
    _list.add(val);
  }
  





  public char pop()
  {
    return _list.removeAt(_list.size() - 1);
  }
  





  public char peek()
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
  






  public char[] toArray()
  {
    char[] retval = _list.toArray();
    reverse(retval, 0, size());
    return retval;
  }
  











  public void toArray(char[] dest)
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
  







  private void reverse(char[] dest, int from, int to)
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
  







  private void swap(char[] dest, int i, int j)
  {
    char tmp = dest[i];
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
    
    TCharArrayStack that = (TCharArrayStack)o;
    
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
    

    _list = ((TCharArrayList)in.readObject());
  }
}
