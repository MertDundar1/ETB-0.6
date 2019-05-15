package net.minecraft.world.chunk.storage;

public class NibbleArrayReader
{
  public final byte[] data;
  private final int depthBits;
  private final int depthBitsPlusFour;
  private static final String __OBFID = "CL_00000376";
  
  public NibbleArrayReader(byte[] dataIn, int depthBitsIn)
  {
    data = dataIn;
    depthBits = depthBitsIn;
    depthBitsPlusFour = (depthBitsIn + 4);
  }
  
  public int get(int p_76686_1_, int p_76686_2_, int p_76686_3_)
  {
    int var4 = p_76686_1_ << depthBitsPlusFour | p_76686_3_ << depthBits | p_76686_2_;
    int var5 = var4 >> 1;
    int var6 = var4 & 0x1;
    return var6 == 0 ? data[var5] & 0xF : data[var5] >> 4 & 0xF;
  }
}
