package net.minecraft.world.chunk;



public class NibbleArray
{
  private final byte[] data;
  
  private static final String __OBFID = "CL_00000371";
  

  public NibbleArray()
  {
    data = new byte['ࠀ'];
  }
  
  public NibbleArray(byte[] storageArray)
  {
    data = storageArray;
    
    if (storageArray.length != 2048)
    {
      throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + storageArray.length);
    }
  }
  



  public int get(int x, int y, int z)
  {
    return getFromIndex(getCoordinateIndex(x, y, z));
  }
  



  public void set(int x, int y, int z, int value)
  {
    setIndex(getCoordinateIndex(x, y, z), value);
  }
  
  private int getCoordinateIndex(int x, int y, int z)
  {
    return y << 8 | z << 4 | x;
  }
  
  public int getFromIndex(int index)
  {
    int var2 = func_177478_c(index);
    return func_177479_b(index) ? data[var2] & 0xF : data[var2] >> 4 & 0xF;
  }
  
  public void setIndex(int index, int value)
  {
    int var3 = func_177478_c(index);
    
    if (func_177479_b(index))
    {
      data[var3] = ((byte)(data[var3] & 0xF0 | value & 0xF));
    }
    else
    {
      data[var3] = ((byte)(data[var3] & 0xF | (value & 0xF) << 4));
    }
  }
  
  private boolean func_177479_b(int p_177479_1_)
  {
    return (p_177479_1_ & 0x1) == 0;
  }
  
  private int func_177478_c(int p_177478_1_)
  {
    return p_177478_1_ >> 1;
  }
  
  public byte[] getData()
  {
    return data;
  }
}
