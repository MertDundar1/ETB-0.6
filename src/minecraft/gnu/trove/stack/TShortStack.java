package gnu.trove.stack;

import java.io.Serializable;

public abstract interface TShortStack
  extends Serializable
{
  public abstract short getNoEntryValue();
  
  public abstract void push(short paramShort);
  
  public abstract short pop();
  
  public abstract short peek();
  
  public abstract int size();
  
  public abstract void clear();
  
  public abstract short[] toArray();
  
  public abstract void toArray(short[] paramArrayOfShort);
}
