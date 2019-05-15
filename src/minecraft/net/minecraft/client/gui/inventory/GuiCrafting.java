package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiCrafting extends GuiContainer
{
  private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");
  private static final String __OBFID = "CL_00000750";
  
  public GuiCrafting(InventoryPlayer p_i45504_1_, World worldIn)
  {
    this(p_i45504_1_, worldIn, BlockPos.ORIGIN);
  }
  
  public GuiCrafting(InventoryPlayer p_i45505_1_, World worldIn, BlockPos p_i45505_3_)
  {
    super(new ContainerWorkbench(p_i45505_1_, worldIn, p_i45505_3_));
  }
  



  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
  {
    fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 28, 6, 4210752);
    fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
  }
  



  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
  {
    net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(craftingTableGuiTextures);
    int var4 = (width - xSize) / 2;
    int var5 = (height - ySize) / 2;
    drawTexturedModalRect(var4, var5, 0, 0, xSize, ySize);
  }
}