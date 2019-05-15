package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TCharProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;





























































public abstract class TCharDoubleHash
  extends TPrimitiveHash
{
  public transient char[] _set;
  protected char no_entry_key;
  protected double no_entry_value;
  
  public TCharDoubleHash()
  {
    no_entry_key = '\000';
    no_entry_value = 0.0D;
  }
  







  public TCharDoubleHash(int initialCapacity)
  {
    super(initialCapacity);
    no_entry_key = '\000';
    no_entry_value = 0.0D;
  }
  








  public TCharDoubleHash(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
    no_entry_key = '\000';
    no_entry_value = 0.0D;
  }
  










  public TCharDoubleHash(int initialCapacity, float loadFactor, char no_entry_key, double no_entry_value)
  {
    super(initialCapacity, loadFactor);
    this.no_entry_key = no_entry_key;
    this.no_entry_value = no_entry_value;
  }
  







  public char getNoEntryKey()
  {
    return no_entry_key;
  }
  







  public double getNoEntryValue()
  {
    return no_entry_value;
  }
  









  protected int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _set = new char[capacity];
    return capacity;
  }
  






  public boolean contains(char val)
  {
    return index(val) >= 0;
  }
  







  public boolean forEach(TCharProcedure procedure)
  {
    byte[] states = _states;
    char[] set = _set;
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
  








  protected int index(char key)
  {
    byte[] states = _states;
    char[] set = _set;
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
  










  protected int insertionIndex(char key)
  {
    byte[] states = _states;
    char[] set = _set;
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
    

    out.writeChar(no_entry_key);
    

    out.writeDouble(no_entry_value);
  }
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    super.readExternal(in);
    

    no_entry_key = in.readChar();
    

    no_entry_value = in.readDouble();
  }
}
