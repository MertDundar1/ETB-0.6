package gnu.trove.stack;

import java.io.Serializable;

public abstract interface TDoubleStack
  extends Serializable
{
  public abstract double getNoEntryValue();
  
  public abstract void push(double paramDouble);
  
  public abstract double pop();
  
  public abstract double peek();
  
  public abstract int size();
  
  public abstract void clear();
  
  public abstract double[] toArray();
  
  public abstract void toArray(double[] paramArrayOfDouble);
}
