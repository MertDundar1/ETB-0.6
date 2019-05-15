package net.minecraft.client.renderer;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer
{
  private boolean hasActivePotionEffects;
  private static final String __OBFID = "CL_00000755";
  
  public InventoryEffectRenderer(net.minecraft.inventory.Container p_i1089_1_)
  {
    super(p_i1089_1_);
  }
  



  public void initGui()
  {
    super.initGui();
    func_175378_g();
  }
  
  protected void func_175378_g()
  {
    if (!mc.thePlayer.getActivePotionEffects().isEmpty())
    {
      guiLeft = (160 + (width - xSize - 200) / 2);
      hasActivePotionEffects = true;
    }
    else
    {
      guiLeft = ((width - xSize) / 2);
      hasActivePotionEffects = false;
    }
  }
  



  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    super.drawScreen(mouseX, mouseY, partialTicks);
    
    if (hasActivePotionEffects)
    {
      drawActivePotionEffects();
    }
  }
  



  private void drawActivePotionEffects()
  {
    int var1 = guiLeft - 124;
    int var2 = guiTop;
    boolean var3 = true;
    Collection var4 = mc.thePlayer.getActivePotionEffects();
    
    if (!var4.isEmpty())
    {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      int var5 = 33;
      
      if (var4.size() > 5)
      {
        var5 = 132 / (var4.size() - 1);
      }
      
      for (Iterator var6 = mc.thePlayer.getActivePotionEffects().iterator(); var6.hasNext(); var2 += var5)
      {
        PotionEffect var7 = (PotionEffect)var6.next();
        Potion var8 = Potion.potionTypes[var7.getPotionID()];
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(inventoryBackground);
        drawTexturedModalRect(var1, var2, 0, 166, 140, 32);
        
        if (var8.hasStatusIcon())
        {
          int var9 = var8.getStatusIconIndex();
          drawTexturedModalRect(var1 + 6, var2 + 7, 0 + var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
        }
        
        String var11 = I18n.format(var8.getName(), new Object[0]);
        
        if (var7.getAmplifier() == 1)
        {
          var11 = var11 + " " + I18n.format("enchantment.level.2", new Object[0]);
        }
        else if (var7.getAmplifier() == 2)
        {
          var11 = var11 + " " + I18n.format("enchantment.level.3", new Object[0]);
        }
        else if (var7.getAmplifier() == 3)
        {
          var11 = var11 + " " + I18n.format("enchantment.level.4", new Object[0]);
        }
        
        fontRendererObj.drawStringWithShadow(var11, var1 + 10 + 18, var2 + 6, 16777215);
        String var10 = Potion.getDurationString(var7);
        fontRendererObj.drawStringWithShadow(var10, var1 + 10 + 18, var2 + 6 + 10, 8355711);
      }
    }
  }
}
