package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;




















































public class TFloatSetDecorator
  extends AbstractSet<Float>
  implements Set<Float>, Externalizable
{
  protected TFloatSet _set;
  
  public TFloatSetDecorator() {}
  
  public TFloatSetDecorator(TFloatSet set)
  {
    _set = set;
  }
  





  public TFloatSet getSet()
  {
    return _set;
  }
  





  public boolean add(Float value)
  {
    return (value != null) && (_set.add(value.floatValue()));
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
        if ((val instanceof Float)) {
          float v = ((Float)val).floatValue();
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
    return ((value instanceof Float)) && (_set.remove(((Float)value).floatValue()));
  }
  





  public Iterator<Float> iterator()
  {
    new Iterator() {
      private final TFloatIterator it = _set.iterator();
      
      public Float next() {
        return Float.valueOf(it.next());
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
    

    _set = ((TFloatSet)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_set);
  }
}
