package gnu.trove.decorator;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;




















































public class TLongSetDecorator
  extends AbstractSet<Long>
  implements Set<Long>, Externalizable
{
  protected TLongSet _set;
  
  public TLongSetDecorator() {}
  
  public TLongSetDecorator(TLongSet set)
  {
    _set = set;
  }
  





  public TLongSet getSet()
  {
    return _set;
  }
  





  public boolean add(Long value)
  {
    return (value != null) && (_set.add(value.longValue()));
  }
  







  public boolean equals(Object other)
  {
    if (_set.equals(other))
      return true;
    if ((other instanceof Set)) {
      Set that = (Set)other;
      if (that.size() != _set.size()) {
        return false;
      }
      Iterator it = that.iterator();
      for (int i = that.size(); i-- > 0;) {
        Object val = it.next();
        if ((val instanceof Long)) {
          long v = ((Long)val).longValue();
          if (!_set.contains(v))
          {

            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
    
    return false;
  }
  




  public void clear()
  {
    _set.clear();
  }
  






  public boolean remove(Object value)
  {
    return ((value instanceof Long)) && (_set.remove(((Long)value).longValue()));
  }
  





  public Iterator<Long> iterator()
  {
    new Iterator() {
      private final TLongIterator it = _set.iterator();
      
      public Long next() {
        return Long.valueOf(it.next());
      }
      
      public boolean hasNext() {
        return it.hasNext();
      }
      
      public void remove() {
        it.remove();
      }
    };
  }
  





  public int size()
  {
    return _set.size();
  }
  





  public boolean isEmpty()
  {
    return _set.size() == 0;
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _set = ((TLongSet)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_set);
  }
}
