package gnu.trove.decorator;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;




















































public class TShortSetDecorator
  extends AbstractSet<Short>
  implements Set<Short>, Externalizable
{
  protected TShortSet _set;
  
  public TShortSetDecorator() {}
  
  public TShortSetDecorator(TShortSet set)
  {
    _set = set;
  }
  





  public TShortSet getSet()
  {
    return _set;
  }
  





  public boolean add(Short value)
  {
    return (value != null) && (_set.add(value.shortValue()));
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
        if ((val instanceof Short)) {
          short v = ((Short)val).shortValue();
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
    return ((value instanceof Short)) && (_set.remove(((Short)value).shortValue()));
  }
  





  public Iterator<Short> iterator()
  {
    new Iterator() {
      private final TShortIterator it = _set.iterator();
      
      public Short next() {
        return Short.valueOf(it.next());
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
    

    _set = ((TShortSet)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_set);
  }
}
