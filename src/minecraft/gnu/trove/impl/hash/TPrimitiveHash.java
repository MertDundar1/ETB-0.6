package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;






























































public abstract class TPrimitiveHash
  extends THash
{
  public transient byte[] _states;
  public static final byte FREE = 0;
  public static final byte FULL = 1;
  public static final byte REMOVED = 2;
  
  public TPrimitiveHash() {}
  
  public TPrimitiveHash(int initialCapacity)
  {
    this(initialCapacity, 0.5F);
  }
  










  public TPrimitiveHash(int initialCapacity, float loadFactor)
  {
    _loadFactor = loadFactor;
    setUp(HashFunctions.fastCeil(initialCapacity / loadFactor));
  }
  






  public int capacity()
  {
    return _states.length;
  }
  





  protected void removeAt(int index)
  {
    _states[index] = 2;
    super.removeAt(index);
  }
  









  protected int setUp(int initialCapacity)
  {
    int capacity = super.setUp(initialCapacity);
    _states = new byte[capacity];
    return capacity;
  }
}
