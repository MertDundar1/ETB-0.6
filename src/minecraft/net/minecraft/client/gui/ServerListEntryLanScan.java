package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry
{
  private final Minecraft field_148288_a = Minecraft.getMinecraft();
  private static final String __OBFID = "CL_00000815";
  
  public ServerListEntryLanScan() {}
  
  public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) { int var9 = y + slotHeight / 2 - field_148288_a.fontRendererObj.FONT_HEIGHT / 2;
    field_148288_a.fontRendererObj.drawString(I18n.format("lanServer.scanning", new Object[0]), field_148288_a.currentScreen.width / 2 - field_148288_a.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning", new Object[0])) / 2, var9, 16777215);
    String var10;
    String var10;
    String var10; switch ((int)(Minecraft.getSystemTime() / 300L % 4L))
    {
    case 0: 
    default: 
      var10 = "O o o";
      break;
    
    case 1: 
    case 3: 
      var10 = "o O o";
      break;
    
    case 2: 
      var10 = "o o O";
    }
    
    field_148288_a.fontRendererObj.drawString(var10, field_148288_a.currentScreen.width / 2 - field_148288_a.fontRendererObj.getStringWidth(var10) / 2, var9 + field_148288_a.fontRendererObj.FONT_HEIGHT, 8421504);
  }
  


  public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}
  

  public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
  {
    return false;
  }
  
  public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}
}
