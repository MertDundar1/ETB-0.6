package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.Stitcher.Holder;

public class StitcherException extends RuntimeException
{
  private final Stitcher.Holder field_98149_a;
  private static final String __OBFID = "CL_00001057";
  
  public StitcherException(Stitcher.Holder p_i2344_1_, String p_i2344_2_)
  {
    super(p_i2344_2_);
    field_98149_a = p_i2344_1_;
  }
}
