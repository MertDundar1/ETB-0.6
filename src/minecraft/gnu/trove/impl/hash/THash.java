package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.PrimeFinder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;











































































public abstract class THash
  implements Externalizable
{
  static final long serialVersionUID = -1792948471915530295L;
  protected static final float DEFAULT_LOAD_FACTOR = 0.5F;
  protected static final int DEFAULT_CAPACITY = 10;
  protected transient int _size;
  protected transient int _free;
  protected float _loadFactor;
  protected int _maxSize;
  protected int _autoCompactRemovesRemaining;
  protected float _autoCompactionFactor;
  protected transient boolean _autoCompactTemporaryDisable = false;
  




  public THash()
  {
    this(10, 0.5F);
  }
  







  public THash(int initialCapacity)
  {
    this(initialCapacity, 0.5F);
  }
  










  public THash(int initialCapacity, float loadFactor)
  {
    _loadFactor = loadFactor;
    


    _autoCompactionFactor = loadFactor;
    
    setUp(HashFunctions.fastCeil(initialCapacity / loadFactor));
  }
  





  public boolean isEmpty()
  {
    return 0 == _size;
  }
  





  public int size()
  {
    return _size;
  }
  





  public abstract int capacity();
  





  public void ensureCapacity(int desiredCapacity)
  {
    if (desiredCapacity > _maxSize - size()) {
      rehash(PrimeFinder.nextPrime(HashFunctions.fastCeil((desiredCapacity + size()) / _loadFactor) + 1));
      
      computeMaxSize(capacity());
    }
  }
  


















  public void compact()
  {
    rehash(PrimeFinder.nextPrime(HashFunctions.fastCeil(size() / _loadFactor) + 1));
    computeMaxSize(capacity());
    

    if (_autoCompactionFactor != 0.0F) {
      computeNextAutoCompactionAmount(size());
    }
  }
  











  public void setAutoCompactionFactor(float factor)
  {
    if (factor < 0.0F) {
      throw new IllegalArgumentException("Factor must be >= 0: " + factor);
    }
    
    _autoCompactionFactor = factor;
  }
  





  public float getAutoCompactionFactor()
  {
    return _autoCompactionFactor;
  }
  









  public final void trimToSize()
  {
    compact();
  }
  






  protected void removeAt(int index)
  {
    _size -= 1;
    

    if (_autoCompactionFactor != 0.0F) {
      _autoCompactRemovesRemaining -= 1;
      
      if ((!_autoCompactTemporaryDisable) && (_autoCompactRemovesRemaining <= 0))
      {

        compact();
      }
    }
  }
  

  public void clear()
  {
    _size = 0;
    _free = capacity();
  }
  









  protected int setUp(int initialCapacity)
  {
    int capacity = PrimeFinder.nextPrime(initialCapacity);
    computeMaxSize(capacity);
    computeNextAutoCompactionAmount(initialCapacity);
    
    return capacity;
  }
  





  protected abstract void rehash(int paramInt);
  





  public void tempDisableAutoCompaction()
  {
    _autoCompactTemporaryDisable = true;
  }
  








  public void reenableAutoCompaction(boolean check_for_compaction)
  {
    _autoCompactTemporaryDisable = false;
    
    if ((check_for_compaction) && (_autoCompactRemovesRemaining <= 0) && (_autoCompactionFactor != 0.0F))
    {



      compact();
    }
  }
  







  protected void computeMaxSize(int capacity)
  {
    _maxSize = Math.min(capacity - 1, (int)(capacity * _loadFactor));
    _free = (capacity - _size);
  }
  






  protected void computeNextAutoCompactionAmount(int size)
  {
    if (_autoCompactionFactor != 0.0F)
    {

      _autoCompactRemovesRemaining = ((int)(size * _autoCompactionFactor + 0.5F));
    }
  }
  







  protected final void postInsertHook(boolean usedFreeSlot)
  {
    if (usedFreeSlot) {
      _free -= 1;
    }
    

    if ((++_size > _maxSize) || (_free == 0))
    {



      int newCapacity = _size > _maxSize ? PrimeFinder.nextPrime(capacity() << 1) : capacity();
      rehash(newCapacity);
      computeMaxSize(capacity());
    }
  }
  
  protected int calculateGrownCapacity()
  {
    return capacity() << 1;
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeFloat(_loadFactor);
    

    out.writeFloat(_autoCompactionFactor);
  }
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    float old_factor = _loadFactor;
    _loadFactor = in.readFloat();
    

    _autoCompactionFactor = in.readFloat();
    

    if (old_factor != _loadFactor) {
      setUp((int)Math.ceil(10.0F / _loadFactor));
    }
  }
}
