package gnu.trove.decorator;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;




















































public class TByteSetDecorator
  extends AbstractSet<Byte>
  implements Set<Byte>, Externalizable
{
  protected TByteSet _set;
  
  public TByteSetDecorator() {}
  
  public TByteSetDecorator(TByteSet set)
  {
    _set = set;
  }
  





  public TByteSet getSet()
  {
    return _set;
  }
  





  public boolean add(Byte value)
  {
    return (value != null) && (_set.add(value.byteValue()));
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
        if ((val instanceof Byte)) {
          byte v = ((Byte)val).byteValue();
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
    return ((value instanceof Byte)) && (_set.remove(((Byte)value).byteValue()));
  }
  





  public Iterator<Byte> iterator()
  {
    new Iterator() {
      private final TByteIterator it = _set.iterator();
      
      public Byte next() {
        return Byte.valueOf(it.next());
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
    

    _set = ((TByteSet)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_set);
  }
}
