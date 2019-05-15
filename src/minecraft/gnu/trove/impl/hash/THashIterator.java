package gnu.trove.impl.hash;

import gnu.trove.iterator.TIterator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
























































public abstract class THashIterator<V>
  implements TIterator, Iterator<V>
{
  private final TObjectHash<V> _object_hash;
  protected final THash _hash;
  protected int _expectedSize;
  protected int _index;
  
  protected THashIterator(TObjectHash<V> hash)
  {
    _hash = hash;
    _expectedSize = _hash.size();
    _index = _hash.capacity();
    _object_hash = hash;
  }
  










  public V next()
  {
    moveToNextIndex();
    return objectAtIndex(_index);
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
  










  protected final int nextIndex()
  {
    if (_expectedSize != _hash.size()) {
      throw new ConcurrentModificationException();
    }
    
    Object[] set = _object_hash._set;
    int i = _index;
    while ((i-- > 0) && ((set[i] == TObjectHash.FREE) || (set[i] == TObjectHash.REMOVED))) {}
    

    return i;
  }
  
  protected abstract V objectAtIndex(int paramInt);
}
