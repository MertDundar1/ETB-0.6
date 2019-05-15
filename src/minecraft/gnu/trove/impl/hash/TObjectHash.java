package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;







































public abstract class TObjectHash<T>
  extends THash
{
  public static final Object FREE = new Object(); public static final Object REMOVED = new Object();
  


  public transient Object[] _set;
  


  static final long serialVersionUID = -3461112548087185871L;
  



  public TObjectHash() {}
  



  public TObjectHash(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  public TObjectHash(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  
  public int capacity()
  {
    return _set.length;
  }
  
  public void removeAt(int index)
  {
    _set[index] = REMOVED;
    super.removeAt(index);
  }
  








  public int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _set = new Object[capacity];
    Arrays.fill(_set, FREE);
    return capacity;
  }
  








  public boolean forEach(TObjectProcedure<T> procedure)
  {
    Object[] set = _set;
    for (int i = set.length; i-- > 0;) {
      if ((set[i] != FREE) && (set[i] != REMOVED) && (!procedure.execute(set[i])))
      {

        return false;
      }
    }
    return true;
  }
  







  public boolean contains(Object obj)
  {
    return index(obj) >= 0;
  }
  







  protected int index(Object obj)
  {
    Object[] set = _set;
    int length = set.length;
    int hash = HashFunctions.hash(obj) & 0x7FFFFFFF;
    int index = hash % length;
    Object cur = set[index];
    
    if (cur == obj) {
      return index;
    }
    
    if (cur == FREE) {
      return -1;
    }
    

    if ((cur == REMOVED) || (!cur.equals(obj)))
    {
      int probe = 1 + hash % (length - 2);
      do
      {
        index -= probe;
        if (index < 0) {
          index += length;
        }
        cur = set[index];
      }
      while ((cur != FREE) && ((cur == REMOVED) || (!cur.equals(obj))));
    }
    
    return cur == FREE ? -1 : index;
  }
  











  protected int insertionIndex(T obj)
  {
    Object[] set = _set;
    int length = set.length;
    int hash = HashFunctions.hash(obj) & 0x7FFFFFFF;
    int index = hash % length;
    Object cur = set[index];
    
    if (cur == FREE)
      return index;
    if ((cur == obj) || ((cur != REMOVED) && (cur.equals(obj)))) {
      return -index - 1;
    }
    
    int probe = 1 + hash % (length - 2);
    











    if (cur != REMOVED)
    {
      do
      {
        index -= probe;
        if (index < 0) {
          index += length;
        }
        cur = set[index];


      }
      while ((cur != FREE) && (cur != REMOVED) && (cur != obj) && (!cur.equals(obj)));
    }
    



    if (cur == REMOVED) {
      int firstRemoved = index;
      
      while ((cur != FREE) && ((cur == REMOVED) || (cur != obj) || (!cur.equals(obj)))) {
        index -= probe;
        if (index < 0) {
          index += length;
        }
        cur = set[index];
      }
      
      return cur != FREE ? -index - 1 : firstRemoved;
    }
    

    return cur != FREE ? -index - 1 : index;
  }
  












  protected final void throwObjectContractViolation(Object o1, Object o2)
    throws IllegalArgumentException
  {
    throw new IllegalArgumentException("Equal objects must have equal hashcodes. During rehashing, Trove discovered that the following two objects claim to be equal (as in java.lang.Object.equals()) but their hashCodes (or those calculated by your TObjectHashingStrategy) are not equal.This violates the general contract of java.lang.Object.hashCode().  See bullet point two in that method's documentation. object #1 =" + o1 + "; object #2 =" + o2);
  }
  












  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    super.writeExternal(out);
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    super.readExternal(in);
  }
}
