package shadersmod.client;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiSlot;
import optifine.Lang;

class GuiSlotShaders extends GuiSlot
{
  private ArrayList shaderslist;
  private int selectedIndex;
  private long lastClickedCached = 0L;
  final GuiShaders shadersGui;
  
  public GuiSlotShaders(GuiShaders par1GuiShaders, int width, int height, int top, int bottom, int slotHeight)
  {
    super(par1GuiShaders.getMc(), width, height, top, bottom, slotHeight);
    shadersGui = par1GuiShaders;
    updateList();
    amountScrolled = 0.0F;
    int posYSelected = selectedIndex * slotHeight;
    int wMid = (bottom - top) / 2;
    
    if (posYSelected > wMid)
    {
      scrollBy(posYSelected - wMid);
    }
  }
  



  public int getListWidth()
  {
    return width - 20;
  }
  
  public void updateList()
  {
    shaderslist = Shaders.listOfShaders();
    selectedIndex = 0;
    int i = 0;
    
    for (int n = shaderslist.size(); i < n; i++)
    {
      if (((String)shaderslist.get(i)).equals(Shaders.currentshadername))
      {
        selectedIndex = i;
        break;
      }
    }
  }
  
  protected int getSize()
  {
    return shaderslist.size();
  }
  



  protected void elementClicked(int index, boolean doubleClicked, int mouseX, int mouseY)
  {
    if ((index != selectedIndex) || (lastClicked != lastClickedCached))
    {
      selectedIndex = index;
      lastClickedCached = lastClicked;
      Shaders.setShaderPack((String)shaderslist.get(index));
      Shaders.uninit();
      shadersGui.updateButtons();
    }
  }
  



  protected boolean isSelected(int index)
  {
    return index == selectedIndex;
  }
  
  protected int getScrollBarX()
  {
    return width - 6;
  }
  



  protected int getContentHeight()
  {
    return getSize() * 18;
  }
  
  protected void drawBackground() {}
  
  protected void drawSlot(int index, int posX, int posY, int contentY, int mouseX, int mouseY)
  {
    String label = (String)shaderslist.get(index);
    
    if (label.equals(Shaders.packNameNone))
    {
      label = Lang.get("of.options.shaders.packNone");
    }
    else if (label.equals(Shaders.packNameDefault))
    {
      label = Lang.get("of.options.shaders.packDefault");
    }
    
    shadersGui.drawCenteredString(label, width / 2, posY + 1, 16777215);
  }
  
  public int getSelectedIndex()
  {
    return selectedIndex;
  }
}
