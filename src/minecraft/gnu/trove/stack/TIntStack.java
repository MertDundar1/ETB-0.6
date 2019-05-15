package gnu.trove.stack;

import java.io.Serializable;

public abstract interface TIntStack
  extends Serializable
{
  public abstract int getNoEntryValue();
  
  public abstract void push(int paramInt);
  
  public abstract int pop();
  
  public abstract int peek();
  
  public abstract int size();
  
  public abstract void clear();
  
  public abstract int[] toArray();
  
  public abstract void toArray(int[] paramArrayOfInt);
}
