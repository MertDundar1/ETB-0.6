package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;




















































public class TDoubleSetDecorator
  extends AbstractSet<Double>
  implements Set<Double>, Externalizable
{
  protected TDoubleSet _set;
  
  public TDoubleSetDecorator() {}
  
  public TDoubleSetDecorator(TDoubleSet set)
  {
    _set = set;
  }
  





  public TDoubleSet getSet()
  {
    return _set;
  }
  





  public boolean add(Double value)
  {
    return (value != null) && (_set.add(value.doubleValue()));
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
        if ((val instanceof Double)) {
          double v = ((Double)val).doubleValue();
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
    return ((value instanceof Double)) && (_set.remove(((Double)value).doubleValue()));
  }
  





  public Iterator<Double> iterator()
  {
    new Iterator() {
      private final TDoubleIterator it = _set.iterator();
      
      public Double next() {
        return Double.valueOf(it.next());
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
    

    _set = ((TDoubleSet)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(_set);
  }
}
