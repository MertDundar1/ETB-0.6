package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantmentTableRenderer
  extends TileEntitySpecialRenderer
{
  private static final ResourceLocation field_147540_b = new ResourceLocation("textures/entity/enchanting_table_book.png");
  private ModelBook field_147541_c = new ModelBook();
  private static final String __OBFID = "CL_00002470";
  
  public TileEntityEnchantmentTableRenderer() {}
  
  public void func_180537_a(TileEntityEnchantmentTable p_180537_1_, double p_180537_2_, double p_180537_4_, double p_180537_6_, float p_180537_8_, int p_180537_9_) { GlStateManager.pushMatrix();
    GlStateManager.translate((float)p_180537_2_ + 0.5F, (float)p_180537_4_ + 0.75F, (float)p_180537_6_ + 0.5F);
    float var10 = tickCount + p_180537_8_;
    GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(var10 * 0.1F) * 0.01F, 0.0F);
    

    for (float var11 = bookRotation - bookRotationPrev; var11 >= 3.1415927F; var11 -= 6.2831855F) {}
    



    while (var11 < -3.1415927F)
    {
      var11 += 6.2831855F;
    }
    
    float var12 = bookRotationPrev + var11 * p_180537_8_;
    GlStateManager.rotate(-var12 * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
    bindTexture(field_147540_b);
    float var13 = pageFlipPrev + (pageFlip - pageFlipPrev) * p_180537_8_ + 0.25F;
    float var14 = pageFlipPrev + (pageFlip - pageFlipPrev) * p_180537_8_ + 0.75F;
    var13 = (var13 - MathHelper.truncateDoubleToInt(var13)) * 1.6F - 0.3F;
    var14 = (var14 - MathHelper.truncateDoubleToInt(var14)) * 1.6F - 0.3F;
    
    if (var13 < 0.0F)
    {
      var13 = 0.0F;
    }
    
    if (var14 < 0.0F)
    {
      var14 = 0.0F;
    }
    
    if (var13 > 1.0F)
    {
      var13 = 1.0F;
    }
    
    if (var14 > 1.0F)
    {
      var14 = 1.0F;
    }
    
    float var15 = bookSpreadPrev + (bookSpread - bookSpreadPrev) * p_180537_8_;
    GlStateManager.enableCull();
    field_147541_c.render(null, var10, var13, var14, var15, 0.0F, 0.0625F);
    GlStateManager.popMatrix();
  }
  
  public void renderTileEntityAt(TileEntity p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_)
  {
    func_180537_a((TileEntityEnchantmentTable)p_180535_1_, p_180535_2_, p_180535_4_, p_180535_6_, p_180535_8_, p_180535_9_);
  }
}
