package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer
{
  private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
  private final InventoryPlayer field_175383_v;
  private IInventory tileFurnace;
  private static final String __OBFID = "CL_00000758";
  
  public GuiFurnace(InventoryPlayer p_i45501_1_, IInventory p_i45501_2_)
  {
    super(new net.minecraft.inventory.ContainerFurnace(p_i45501_1_, p_i45501_2_));
    field_175383_v = p_i45501_1_;
    tileFurnace = p_i45501_2_;
  }
  



  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
  {
    String var3 = tileFurnace.getDisplayName().getUnformattedText();
    fontRendererObj.drawString(var3, xSize / 2 - fontRendererObj.getStringWidth(var3) / 2, 6, 4210752);
    fontRendererObj.drawString(field_175383_v.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
  }
  



  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
  {
    net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(furnaceGuiTextures);
    int var4 = (width - xSize) / 2;
    int var5 = (height - ySize) / 2;
    drawTexturedModalRect(var4, var5, 0, 0, xSize, ySize);
    

    if (TileEntityFurnace.func_174903_a(tileFurnace))
    {
      int var6 = func_175382_i(13);
      drawTexturedModalRect(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 1);
    }
    
    int var6 = func_175381_h(24);
    drawTexturedModalRect(var4 + 79, var5 + 34, 176, 14, var6 + 1, 16);
  }
  
  private int func_175381_h(int p_175381_1_)
  {
    int var2 = tileFurnace.getField(2);
    int var3 = tileFurnace.getField(3);
    return (var3 != 0) && (var2 != 0) ? var2 * p_175381_1_ / var3 : 0;
  }
  
  private int func_175382_i(int p_175382_1_)
  {
    int var2 = tileFurnace.getField(1);
    
    if (var2 == 0)
    {
      var2 = 200;
    }
    
    return tileFurnace.getField(0) * p_175382_1_ / var2;
  }
}
