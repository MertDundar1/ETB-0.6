package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TByteProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;





























































public abstract class TByteCharHash
  extends TPrimitiveHash
{
  public transient byte[] _set;
  protected byte no_entry_key;
  protected char no_entry_value;
  
  public TByteCharHash()
  {
    no_entry_key = 0;
    no_entry_value = '\000';
  }
  







  public TByteCharHash(int initialCapacity)
  {
    super(initialCapacity);
    no_entry_key = 0;
    no_entry_value = '\000';
  }
  








  public TByteCharHash(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
    no_entry_key = 0;
    no_entry_value = '\000';
  }
  










  public TByteCharHash(int initialCapacity, float loadFactor, byte no_entry_key, char no_entry_value)
  {
    super(initialCapacity, loadFactor);
    this.no_entry_key = no_entry_key;
    this.no_entry_value = no_entry_value;
  }
  







  public byte getNoEntryKey()
  {
    return no_entry_key;
  }
  







  public char getNoEntryValue()
  {
    return no_entry_value;
  }
  









  protected int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _set = new byte[capacity];
    return capacity;
  }
  






  public boolean contains(byte val)
  {
    return index(val) >= 0;
  }
  







  public boolean forEach(TByteProcedure procedure)
  {
    byte[] states = _states;
    byte[] set = _set;
    for (int i = set.length; i-- > 0;) {
      if ((states[i] == 1) && (!procedure.execute(set[i]))) {
        return false;
      }
    }
    return true;
  }
  





  public void removeAt(int index)
  {
    _set[index] = no_entry_key;
    super.removeAt(index);
  }
  








  protected int index(byte key)
  {
    byte[] states = _states;
    byte[] set = _set;
    int length = states.length;
    int hash = HashFunctions.hash(key) & 0x7FFFFFFF;
    int index = hash % length;
    
    if ((states[index] != 0) && ((states[index] == 2) || (set[index] != key)))
    {

      int probe = 1 + hash % (length - 2);
      do
      {
        index -= probe;
        if (index < 0) {
          index += length;
        }
      } while ((states[index] != 0) && ((states[index] == 2) || (set[index] != key)));
    }
    

    return states[index] == 0 ? -1 : index;
  }
  










  protected int insertionIndex(byte key)
  {
    byte[] states = _states;
    byte[] set = _set;
    int length = states.length;
    int hash = HashFunctions.hash(key) & 0x7FFFFFFF;
    int index = hash % length;
    
    if (states[index] == 0)
      return index;
    if ((states[index] == 1) && (set[index] == key)) {
      return -index - 1;
    }
    
    int probe = 1 + hash % (length - 2);
    












    if (states[index] != 2)
    {
      do
      {
        index -= probe;
        if (index < 0) {
          index += length;
        }
      } while ((states[index] == 1) && (set[index] != key));
    }
    



    if (states[index] == 2) {
      int firstRemoved = index;
      while ((states[index] != 0) && ((states[index] == 2) || (set[index] != key)))
      {
        index -= probe;
        if (index < 0) {
          index += length;
        }
      }
      return states[index] == 1 ? -index - 1 : firstRemoved;
    }
    
    return states[index] == 1 ? -index - 1 : index;
  }
  


  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    super.writeExternal(out);
    

    out.writeByte(no_entry_key);
    

    out.writeChar(no_entry_value);
  }
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    super.readExternal(in);
    

    no_entry_key = in.readByte();
    

    no_entry_value = in.readChar();
  }
}
