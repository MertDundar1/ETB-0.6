package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback
{
  private static final Logger field_175287_a = ;
  private static final Set field_175284_f = com.google.common.collect.Sets.newHashSet(new String[] { "http", "https" });
  private static final Splitter field_175285_g = Splitter.on('\n');
  


  protected Minecraft mc;
  


  protected net.minecraft.client.renderer.entity.RenderItem itemRender;
  

  public int width;
  

  public int height;
  

  protected List buttonList = Lists.newArrayList();
  

  protected List labelList = Lists.newArrayList();
  

  public boolean allowUserInput;
  

  protected FontRenderer fontRendererObj;
  
  private GuiButton selectedButton;
  
  protected int eventButton;
  
  private long lastMouseEvent;
  
  private int touchValue;
  
  private URI field_175286_t;
  
  private static final String __OBFID = "CL_00000710";
  

  public GuiScreen() {}
  

  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    for (int var4 = 0; var4 < buttonList.size(); var4++)
    {
      ((GuiButton)buttonList.get(var4)).drawButton(mc, mouseX, mouseY);
    }
    
    for (var4 = 0; var4 < labelList.size(); var4++)
    {
      ((GuiLabel)labelList.get(var4)).drawLabel(mc, mouseX, mouseY);
    }
  }
  



  protected void keyTyped(char typedChar, int keyCode)
    throws IOException
  {
    if (keyCode == 1)
    {
      mc.displayGuiScreen(null);
      
      if (mc.currentScreen == null)
      {
        mc.setIngameFocus();
      }
    }
  }
  



  public static String getClipboardString()
  {
    try
    {
      Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
      
      if ((var0 != null) && (var0.isDataFlavorSupported(DataFlavor.stringFlavor)))
      {
        return (String)var0.getTransferData(DataFlavor.stringFlavor);
      }
    }
    catch (Exception localException) {}
    



    return "";
  }
  



  public static void setClipboardString(String copyText)
  {
    if (!org.apache.commons.lang3.StringUtils.isEmpty(copyText))
    {
      try
      {
        StringSelection var1 = new StringSelection(copyText);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, null);
      }
      catch (Exception localException) {}
    }
  }
  



  protected void renderToolTip(ItemStack itemIn, int x, int y)
  {
    List var4 = itemIn.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
    
    for (int var5 = 0; var5 < var4.size(); var5++)
    {
      if (var5 == 0)
      {
        var4.set(var5, getRarityrarityColor + (String)var4.get(var5));
      }
      else
      {
        var4.set(var5, EnumChatFormatting.GRAY + (String)var4.get(var5));
      }
    }
    
    drawHoveringText(var4, x, y);
  }
  




  protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY)
  {
    drawHoveringText(Arrays.asList(new String[] { tabName }), mouseX, mouseY);
  }
  
  protected void drawHoveringText(List textLines, int x, int y)
  {
    if (!textLines.isEmpty())
    {
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      int var4 = 0;
      Iterator var5 = textLines.iterator();
      
      while (var5.hasNext())
      {
        String var6 = (String)var5.next();
        int var7 = fontRendererObj.getStringWidth(var6);
        
        if (var7 > var4)
        {
          var4 = var7;
        }
      }
      
      int var14 = x + 12;
      int var15 = y - 12;
      int var8 = 8;
      
      if (textLines.size() > 1)
      {
        var8 += 2 + (textLines.size() - 1) * 10;
      }
      
      if (var14 + var4 > width)
      {
        var14 -= 28 + var4;
      }
      
      if (var15 + var8 + 6 > height)
      {
        var15 = height - var8 - 6;
      }
      
      zLevel = 300.0F;
      itemRender.zLevel = 300.0F;
      int var9 = -267386864;
      drawGradientRect(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
      drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
      drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
      drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
      drawGradientRect(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
      int var10 = 1347420415;
      int var11 = (var10 & 0xFEFEFE) >> 1 | var10 & 0xFF000000;
      drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
      drawGradientRect(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
      drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
      drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);
      
      for (int var12 = 0; var12 < textLines.size(); var12++)
      {
        String var13 = (String)textLines.get(var12);
        fontRendererObj.drawStringWithShadow(var13, var14, var15, -1);
        
        if (var12 == 0)
        {
          var15 += 2;
        }
        
        var15 += 10;
      }
      
      zLevel = 0.0F;
      itemRender.zLevel = 0.0F;
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.enableRescaleNormal();
    }
  }
  
  protected void func_175272_a(IChatComponent p_175272_1_, int p_175272_2_, int p_175272_3_)
  {
    if ((p_175272_1_ != null) && (p_175272_1_.getChatStyle().getChatHoverEvent() != null))
    {
      HoverEvent var4 = p_175272_1_.getChatStyle().getChatHoverEvent();
      
      if (var4.getAction() == HoverEvent.Action.SHOW_ITEM)
      {
        ItemStack var5 = null;
        
        try
        {
          NBTTagCompound var6 = JsonToNBT.func_180713_a(var4.getValue().getUnformattedText());
          
          if ((var6 instanceof NBTTagCompound))
          {
            var5 = ItemStack.loadItemStackFromNBT(var6);
          }
        }
        catch (NBTException localNBTException1) {}
        



        if (var5 != null)
        {
          renderToolTip(var5, p_175272_2_, p_175272_3_);
        }
        else
        {
          drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", p_175272_2_, p_175272_3_);

        }
        


      }
      else if (var4.getAction() == HoverEvent.Action.SHOW_ENTITY)
      {
        if (mc.gameSettings.advancedItemTooltips)
        {
          try
          {
            NBTTagCompound var12 = JsonToNBT.func_180713_a(var4.getValue().getUnformattedText());
            
            if ((var12 instanceof NBTTagCompound))
            {
              ArrayList var14 = Lists.newArrayList();
              NBTTagCompound var7 = var12;
              var14.add(var7.getString("name"));
              
              if (var7.hasKey("type", 8))
              {
                String var8 = var7.getString("type");
                var14.add("Type: " + var8 + " (" + net.minecraft.entity.EntityList.func_180122_a(var8) + ")");
              }
              
              var14.add(var7.getString("id"));
              drawHoveringText(var14, p_175272_2_, p_175272_3_);
            }
            else
            {
              drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
            }
          }
          catch (NBTException var10)
          {
            drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
          }
        }
      }
      else if (var4.getAction() == HoverEvent.Action.SHOW_TEXT)
      {
        drawHoveringText(field_175285_g.splitToList(var4.getValue().getFormattedText()), p_175272_2_, p_175272_3_);
      }
      else if (var4.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT)
      {
        StatBase var13 = net.minecraft.stats.StatList.getOneShotStat(var4.getValue().getUnformattedText());
        
        if (var13 != null)
        {
          IChatComponent var15 = var13.getStatName();
          ChatComponentTranslation var16 = new ChatComponentTranslation("stats.tooltip.type." + (var13.isAchievement() ? "achievement" : "statistic"), new Object[0]);
          var16.getChatStyle().setItalic(Boolean.valueOf(true));
          String var8 = (var13 instanceof Achievement) ? ((Achievement)var13).getDescription() : null;
          ArrayList var9 = Lists.newArrayList(new String[] { var15.getFormattedText(), var16.getFormattedText() });
          
          if (var8 != null)
          {
            var9.addAll(fontRendererObj.listFormattedStringToWidth(var8, 150));
          }
          
          drawHoveringText(var9, p_175272_2_, p_175272_3_);
        }
        else
        {
          drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", p_175272_2_, p_175272_3_);
        }
      }
      

      GlStateManager.disableLighting();
    }
  }
  
  protected void func_175274_a(String p_175274_1_, boolean p_175274_2_) {}
  
  protected boolean func_175276_a(IChatComponent p_175276_1_)
  {
    if (p_175276_1_ == null)
    {
      return false;
    }
    

    ClickEvent var2 = p_175276_1_.getChatStyle().getChatClickEvent();
    
    if (isShiftKeyDown())
    {
      if (p_175276_1_.getChatStyle().getInsertion() != null)
      {
        func_175274_a(p_175276_1_.getChatStyle().getInsertion(), false);
      }
    }
    else if (var2 != null)
    {


      if (var2.getAction() == ClickEvent.Action.OPEN_URL)
      {
        if (!mc.gameSettings.chatLinks)
        {
          return false;
        }
        
        try
        {
          URI var3 = new URI(var2.getValue());
          
          if (!field_175284_f.contains(var3.getScheme().toLowerCase()))
          {
            throw new URISyntaxException(var2.getValue(), "Unsupported protocol: " + var3.getScheme().toLowerCase());
          }
          
          if (mc.gameSettings.chatLinksPrompt)
          {
            field_175286_t = var3;
            mc.displayGuiScreen(new GuiConfirmOpenLink(this, var2.getValue(), 31102009, false));
          }
          else
          {
            func_175282_a(var3);
          }
        }
        catch (URISyntaxException var4)
        {
          field_175287_a.error("Can't open url for " + var2, var4);
        }
      }
      else if (var2.getAction() == ClickEvent.Action.OPEN_FILE)
      {
        URI var3 = new File(var2.getValue()).toURI();
        func_175282_a(var3);
      }
      else if (var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
      {
        func_175274_a(var2.getValue(), true);
      }
      else if (var2.getAction() == ClickEvent.Action.RUN_COMMAND)
      {
        func_175281_b(var2.getValue(), false);
      }
      else if (var2.getAction() == ClickEvent.Action.TWITCH_USER_INFO)
      {
        tv.twitch.chat.ChatUserInfo var5 = mc.getTwitchStream().func_152926_a(var2.getValue());
        
        if (var5 != null)
        {
          mc.displayGuiScreen(new net.minecraft.client.gui.stream.GuiTwitchUserMode(mc.getTwitchStream(), var5));
        }
        else
        {
          field_175287_a.error("Tried to handle twitch user but couldn't find them!");
        }
      }
      else
      {
        field_175287_a.error("Don't know how to handle " + var2);
      }
      
      return true;
    }
    
    return false;
  }
  

  public void func_175275_f(String p_175275_1_)
  {
    func_175281_b(p_175275_1_, true);
  }
  
  public void func_175281_b(String p_175281_1_, boolean p_175281_2_)
  {
    if (p_175281_2_)
    {
      mc.ingameGUI.getChatGUI().addToSentMessages(p_175281_1_);
    }
    
    mc.thePlayer.sendChatMessage(p_175281_1_);
  }
  


  protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    throws IOException
  {
    if (mouseButton == 0)
    {
      for (int var4 = 0; var4 < buttonList.size(); var4++)
      {
        GuiButton var5 = (GuiButton)buttonList.get(var4);
        
        if (var5.mousePressed(mc, mouseX, mouseY))
        {
          selectedButton = var5;
          var5.playPressSound(mc.getSoundHandler());
          actionPerformed(var5);
        }
      }
    }
  }
  



  protected void mouseReleased(int mouseX, int mouseY, int state)
  {
    if ((selectedButton != null) && (state == 0))
    {
      selectedButton.mouseReleased(mouseX, mouseY);
      selectedButton = null;
    }
  }
  


  protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}
  


  protected void actionPerformed(GuiButton button)
    throws IOException
  {}
  


  public void setWorldAndResolution(Minecraft mc, int width, int height)
  {
    this.mc = mc;
    itemRender = mc.getRenderItem();
    fontRendererObj = fontRendererObj;
    this.width = width;
    this.height = height;
    buttonList.clear();
    initGui();
  }
  



  public void initGui() {}
  


  public void handleInput()
    throws IOException
  {
    if (Mouse.isCreated())
    {
      while (Mouse.next())
      {
        handleMouseInput();
      }
    }
    
    if (Keyboard.isCreated())
    {
      while (Keyboard.next())
      {
        handleKeyboardInput();
      }
    }
  }
  


  public void handleMouseInput()
    throws IOException
  {
    int var1 = Mouse.getEventX() * width / mc.displayWidth;
    int var2 = height - Mouse.getEventY() * height / mc.displayHeight - 1;
    int var3 = Mouse.getEventButton();
    
    if (Mouse.getEventButtonState())
    {
      if ((mc.gameSettings.touchscreen) && (touchValue++ > 0))
      {
        return;
      }
      
      eventButton = var3;
      lastMouseEvent = Minecraft.getSystemTime();
      mouseClicked(var1, var2, eventButton);
    }
    else if (var3 != -1)
    {
      if ((mc.gameSettings.touchscreen) && (--touchValue > 0))
      {
        return;
      }
      
      eventButton = -1;
      mouseReleased(var1, var2, var3);
    }
    else if ((eventButton != -1) && (lastMouseEvent > 0L))
    {
      long var4 = Minecraft.getSystemTime() - lastMouseEvent;
      mouseClickMove(var1, var2, eventButton, var4);
    }
  }
  


  public void handleKeyboardInput()
    throws IOException
  {
    if (Keyboard.getEventKeyState())
    {
      keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
    }
    
    mc.dispatchKeypresses();
  }
  



  public void updateScreen() {}
  



  public void onGuiClosed() {}
  



  public void drawDefaultBackground()
  {
    drawWorldBackground(0);
  }
  
  public void drawWorldBackground(int tint)
  {
    if (mc.theWorld != null)
    {
      drawGradientRect(0, 0, width, height, -1072689136, -804253680);
    }
    else
    {
      drawBackground(tint);
    }
  }
  



  public void drawBackground(int tint)
  {
    GlStateManager.disableLighting();
    GlStateManager.disableFog();
    Tessellator var2 = Tessellator.getInstance();
    WorldRenderer var3 = var2.getWorldRenderer();
    mc.getTextureManager().bindTexture(optionsBackground);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    float var4 = 32.0F;
    var3.startDrawingQuads();
    var3.func_178991_c(4210752);
    var3.addVertexWithUV(0.0D, height, 0.0D, 0.0D, height / var4 + tint);
    var3.addVertexWithUV(width, height, 0.0D, width / var4, height / var4 + tint);
    var3.addVertexWithUV(width, 0.0D, 0.0D, width / var4, tint);
    var3.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, tint);
    var2.draw();
  }
  



  public boolean doesGuiPauseGame()
  {
    return true;
  }
  
  public void confirmClicked(boolean result, int id)
  {
    if (id == 31102009)
    {
      if (result)
      {
        func_175282_a(field_175286_t);
      }
      
      field_175286_t = null;
      mc.displayGuiScreen(this);
    }
  }
  
  private void func_175282_a(URI p_175282_1_)
  {
    try
    {
      Class var2 = Class.forName("java.awt.Desktop");
      Object var3 = var2.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      var2.getMethod("browse", new Class[] { URI.class }).invoke(var3, new Object[] { p_175282_1_ });
    }
    catch (Throwable var4)
    {
      field_175287_a.error("Couldn't open link", var4);
    }
  }
  



  public static boolean isCtrlKeyDown()
  {
    return (Keyboard.isKeyDown(219)) || (Keyboard.isKeyDown(220));
  }
  



  public static boolean isShiftKeyDown()
  {
    return (Keyboard.isKeyDown(42)) || (Keyboard.isKeyDown(54));
  }
  
  public static boolean func_175283_s()
  {
    return (Keyboard.isKeyDown(56)) || (Keyboard.isKeyDown(184));
  }
  
  public static boolean func_175277_d(int p_175277_0_)
  {
    return (p_175277_0_ == 45) && (isCtrlKeyDown());
  }
  
  public static boolean func_175279_e(int p_175279_0_)
  {
    return (p_175279_0_ == 47) && (isCtrlKeyDown());
  }
  
  public static boolean func_175280_f(int p_175280_0_)
  {
    return (p_175280_0_ == 46) && (isCtrlKeyDown());
  }
  
  public static boolean func_175278_g(int p_175278_0_)
  {
    return (p_175278_0_ == 30) && (isCtrlKeyDown());
  }
  
  public void func_175273_b(Minecraft mcIn, int p_175273_2_, int p_175273_3_)
  {
    setWorldAndResolution(mcIn, p_175273_2_, p_175273_3_);
  }
}
