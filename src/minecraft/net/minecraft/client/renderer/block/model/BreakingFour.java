package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BreakingFour extends BakedQuad
{
  private final TextureAtlasSprite texture;
  private static final String __OBFID = "CL_00002492";
  
  public BreakingFour(BakedQuad p_i46217_1_, TextureAtlasSprite p_i46217_2_)
  {
    super(Arrays.copyOf(p_i46217_1_.func_178209_a(), p_i46217_1_.func_178209_a().length), field_178213_b, FaceBakery.func_178410_a(p_i46217_1_.func_178209_a()));
    texture = p_i46217_2_;
    func_178217_e();
  }
  
  private void func_178217_e()
  {
    for (int var1 = 0; var1 < 4; var1++)
    {
      func_178216_a(var1);
    }
  }
  
  private void func_178216_a(int p_178216_1_)
  {
    int step = field_178215_a.length / 4;
    int var2 = step * p_178216_1_;
    float var3 = Float.intBitsToFloat(field_178215_a[var2]);
    float var4 = Float.intBitsToFloat(field_178215_a[(var2 + 1)]);
    float var5 = Float.intBitsToFloat(field_178215_a[(var2 + 2)]);
    float var6 = 0.0F;
    float var7 = 0.0F;
    
    switch (SwitchEnumFacing.field_178419_a[face.ordinal()])
    {
    case 1: 
      var6 = var3 * 16.0F;
      var7 = (1.0F - var5) * 16.0F;
      break;
    
    case 2: 
      var6 = var3 * 16.0F;
      var7 = var5 * 16.0F;
      break;
    
    case 3: 
      var6 = (1.0F - var3) * 16.0F;
      var7 = (1.0F - var4) * 16.0F;
      break;
    
    case 4: 
      var6 = var3 * 16.0F;
      var7 = (1.0F - var4) * 16.0F;
      break;
    
    case 5: 
      var6 = var5 * 16.0F;
      var7 = (1.0F - var4) * 16.0F;
      break;
    
    case 6: 
      var6 = (1.0F - var5) * 16.0F;
      var7 = (1.0F - var4) * 16.0F;
    }
    
    field_178215_a[(var2 + 4)] = Float.floatToRawIntBits(texture.getInterpolatedU(var6));
    field_178215_a[(var2 + 4 + 1)] = Float.floatToRawIntBits(texture.getInterpolatedV(var7));
  }
  
  static final class SwitchEnumFacing
  {
    static final int[] field_178419_a = new int[EnumFacing.values().length];
    private static final String __OBFID = "CL_00002491";
    
    static
    {
      try
      {
        field_178419_a[EnumFacing.DOWN.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        field_178419_a[EnumFacing.UP.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        field_178419_a[EnumFacing.NORTH.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      try
      {
        field_178419_a[EnumFacing.SOUTH.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
      



      try
      {
        field_178419_a[EnumFacing.WEST.ordinal()] = 5;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {}
      



      try
      {
        field_178419_a[EnumFacing.EAST.ordinal()] = 6;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {}
    }
    
    SwitchEnumFacing() {}
  }
}
