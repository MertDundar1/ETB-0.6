package gnu.trove.impl.hash;

import gnu.trove.iterator.TPrimitiveIterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;



















































public abstract class THashPrimitiveIterator
  implements TPrimitiveIterator
{
  protected final TPrimitiveHash _hash;
  protected int _expectedSize;
  protected int _index;
  
  public THashPrimitiveIterator(TPrimitiveHash hash)
  {
    _hash = hash;
    _expectedSize = _hash.size();
    _index = _hash.capacity();
  }
  









  protected final int nextIndex()
  {
    if (_expectedSize != _hash.size()) {
      throw new ConcurrentModificationException();
    }
    
    byte[] states = _hash._states;
    int i = _index;
    while ((i-- > 0) && (states[i] != 1)) {}
    

    return i;
  }
  






  public boolean hasNext()
  {
    return nextIndex() >= 0;
  }
  






  public void remove()
  {
    if (_expectedSize != _hash.size()) {
      throw new ConcurrentModificationException();
    }
    
    try
    {
      _hash.tempDisableAutoCompaction();
      _hash.removeAt(_index);
    }
    finally {
      _hash.reenableAutoCompaction(false);
    }
    
    _expectedSize -= 1;
  }
  






  protected final void moveToNextIndex()
  {
    if ((this._index = nextIndex()) < 0) {
      throw new NoSuchElementException();
    }
  }
}
