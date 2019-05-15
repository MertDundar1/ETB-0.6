package net.minecraft.client.gui.achievement;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements extends GuiScreen implements net.minecraft.client.gui.IProgressMeter
{
  private static final int field_146572_y = AchievementList.minDisplayColumn * 24 - 112;
  private static final int field_146571_z = AchievementList.minDisplayRow * 24 - 112;
  private static final int field_146559_A = AchievementList.maxDisplayColumn * 24 - 77;
  private static final int field_146560_B = AchievementList.maxDisplayRow * 24 - 77;
  private static final ResourceLocation field_146561_C = new ResourceLocation("textures/gui/achievement/achievement_background.png");
  protected GuiScreen parentScreen;
  protected int field_146555_f = 256;
  protected int field_146557_g = 202;
  protected int field_146563_h;
  protected int field_146564_i;
  protected float field_146570_r = 1.0F;
  protected double field_146569_s;
  protected double field_146568_t;
  protected double field_146567_u;
  protected double field_146566_v;
  protected double field_146565_w;
  protected double field_146573_x;
  private int field_146554_D;
  private StatFileWriter statFileWriter;
  private boolean loadingAchievements = true;
  private static final String __OBFID = "CL_00000722";
  
  public GuiAchievements(GuiScreen p_i45026_1_, StatFileWriter p_i45026_2_)
  {
    parentScreen = p_i45026_1_;
    statFileWriter = p_i45026_2_;
    short var3 = 141;
    short var4 = 141;
    field_146569_s = (this.field_146567_u = this.field_146565_w = openInventorydisplayColumn * 24 - var3 / 2 - 12);
    field_146568_t = (this.field_146566_v = this.field_146573_x = openInventorydisplayRow * 24 - var4 / 2);
  }
  



  public void initGui()
  {
    mc.getNetHandler().addToSendQueue(new net.minecraft.network.play.client.C16PacketClientStatus(net.minecraft.network.play.client.C16PacketClientStatus.EnumState.REQUEST_STATS));
    buttonList.clear();
    buttonList.add(new net.minecraft.client.gui.GuiOptionButton(1, width / 2 + 24, height / 2 + 74, 80, 20, I18n.format("gui.done", new Object[0])));
  }
  
  protected void actionPerformed(GuiButton button) throws IOException
  {
    if (!loadingAchievements)
    {
      if (id == 1)
      {
        mc.displayGuiScreen(parentScreen);
      }
    }
  }
  



  protected void keyTyped(char typedChar, int keyCode)
    throws IOException
  {
    if (keyCode == mc.gameSettings.keyBindInventory.getKeyCode())
    {
      mc.displayGuiScreen(null);
      mc.setIngameFocus();
    }
    else
    {
      super.keyTyped(typedChar, keyCode);
    }
  }
  



  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    if (loadingAchievements)
    {
      drawDefaultBackground();
      drawCenteredString(fontRendererObj, I18n.format("multiplayer.downloadingStats", new Object[0]), width / 2, height / 2, 16777215);
      drawCenteredString(fontRendererObj, lanSearchStates[((int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length))], width / 2, height / 2 + fontRendererObj.FONT_HEIGHT * 2, 16777215);

    }
    else
    {

      if (Mouse.isButtonDown(0))
      {
        int var4 = (width - field_146555_f) / 2;
        int var5 = (height - field_146557_g) / 2;
        int var6 = var4 + 8;
        int var7 = var5 + 17;
        
        if (((field_146554_D == 0) || (field_146554_D == 1)) && (mouseX >= var6) && (mouseX < var6 + 224) && (mouseY >= var7) && (mouseY < var7 + 155))
        {
          if (field_146554_D == 0)
          {
            field_146554_D = 1;
          }
          else
          {
            field_146567_u -= (mouseX - field_146563_h) * field_146570_r;
            field_146566_v -= (mouseY - field_146564_i) * field_146570_r;
            field_146565_w = (this.field_146569_s = field_146567_u);
            field_146573_x = (this.field_146568_t = field_146566_v);
          }
          
          field_146563_h = mouseX;
          field_146564_i = mouseY;
        }
      }
      else
      {
        field_146554_D = 0;
      }
      
      int var4 = Mouse.getDWheel();
      float var11 = field_146570_r;
      
      if (var4 < 0)
      {
        field_146570_r += 0.25F;
      }
      else if (var4 > 0)
      {
        field_146570_r -= 0.25F;
      }
      
      field_146570_r = MathHelper.clamp_float(field_146570_r, 1.0F, 2.0F);
      
      if (field_146570_r != var11)
      {
        float var10000 = var11 - field_146570_r;
        float var12 = var11 * field_146555_f;
        float var8 = var11 * field_146557_g;
        float var9 = field_146570_r * field_146555_f;
        float var10 = field_146570_r * field_146557_g;
        field_146567_u -= (var9 - var12) * 0.5F;
        field_146566_v -= (var10 - var8) * 0.5F;
        field_146565_w = (this.field_146569_s = field_146567_u);
        field_146573_x = (this.field_146568_t = field_146566_v);
      }
      
      if (field_146565_w < field_146572_y)
      {
        field_146565_w = field_146572_y;
      }
      
      if (field_146573_x < field_146571_z)
      {
        field_146573_x = field_146571_z;
      }
      
      if (field_146565_w >= field_146559_A)
      {
        field_146565_w = (field_146559_A - 1);
      }
      
      if (field_146573_x >= field_146560_B)
      {
        field_146573_x = (field_146560_B - 1);
      }
      
      drawDefaultBackground();
      drawAchievementScreen(mouseX, mouseY, partialTicks);
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      drawTitle();
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
    }
  }
  
  public void doneLoading()
  {
    if (loadingAchievements)
    {
      loadingAchievements = false;
    }
  }
  



  public void updateScreen()
  {
    if (!loadingAchievements)
    {
      field_146569_s = field_146567_u;
      field_146568_t = field_146566_v;
      double var1 = field_146565_w - field_146567_u;
      double var3 = field_146573_x - field_146566_v;
      
      if (var1 * var1 + var3 * var3 < 4.0D)
      {
        field_146567_u += var1;
        field_146566_v += var3;
      }
      else
      {
        field_146567_u += var1 * 0.85D;
        field_146566_v += var3 * 0.85D;
      }
    }
  }
  
  protected void drawTitle()
  {
    int var1 = (width - field_146555_f) / 2;
    int var2 = (height - field_146557_g) / 2;
    fontRendererObj.drawString(I18n.format("gui.achievements", new Object[0]), var1 + 15, var2 + 5, 4210752);
  }
  
  protected void drawAchievementScreen(int p_146552_1_, int p_146552_2_, float p_146552_3_)
  {
    int var4 = MathHelper.floor_double(field_146569_s + (field_146567_u - field_146569_s) * p_146552_3_);
    int var5 = MathHelper.floor_double(field_146568_t + (field_146566_v - field_146568_t) * p_146552_3_);
    
    if (var4 < field_146572_y)
    {
      var4 = field_146572_y;
    }
    
    if (var5 < field_146571_z)
    {
      var5 = field_146571_z;
    }
    
    if (var4 >= field_146559_A)
    {
      var4 = field_146559_A - 1;
    }
    
    if (var5 >= field_146560_B)
    {
      var5 = field_146560_B - 1;
    }
    
    int var6 = (width - field_146555_f) / 2;
    int var7 = (height - field_146557_g) / 2;
    int var8 = var6 + 16;
    int var9 = var7 + 17;
    zLevel = 0.0F;
    GlStateManager.depthFunc(518);
    GlStateManager.pushMatrix();
    GlStateManager.translate(var8, var9, -200.0F);
    GlStateManager.scale(1.0F / field_146570_r, 1.0F / field_146570_r, 0.0F);
    GlStateManager.func_179098_w();
    GlStateManager.disableLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    int var10 = var4 + 288 >> 4;
    int var11 = var5 + 288 >> 4;
    int var12 = (var4 + 288) % 16;
    int var13 = (var5 + 288) % 16;
    boolean var14 = true;
    boolean var15 = true;
    boolean var16 = true;
    boolean var17 = true;
    boolean var18 = true;
    Random var19 = new Random();
    float var20 = 16.0F / field_146570_r;
    float var21 = 16.0F / field_146570_r;
    




    for (int var22 = 0; var22 * var20 - var13 < 155.0F; var22++)
    {
      float var23 = 0.6F - (var11 + var22) / 25.0F * 0.3F;
      GlStateManager.color(var23, var23, var23, 1.0F);
      
      for (int var24 = 0; var24 * var21 - var12 < 224.0F; var24++)
      {
        var19.setSeed(mc.getSession().getPlayerID().hashCode() + var10 + var24 + (var11 + var22) * 16);
        int var25 = var19.nextInt(1 + var11 + var22) + (var11 + var22) / 2;
        TextureAtlasSprite var26 = func_175371_a(Blocks.sand);
        
        if ((var25 <= 37) && (var11 + var22 != 35))
        {
          if (var25 == 22)
          {
            if (var19.nextInt(2) == 0)
            {
              var26 = func_175371_a(Blocks.diamond_ore);
            }
            else
            {
              var26 = func_175371_a(Blocks.redstone_ore);
            }
          }
          else if (var25 == 10)
          {
            var26 = func_175371_a(Blocks.iron_ore);
          }
          else if (var25 == 8)
          {
            var26 = func_175371_a(Blocks.coal_ore);
          }
          else if (var25 > 4)
          {
            var26 = func_175371_a(Blocks.stone);
          }
          else if (var25 > 0)
          {
            var26 = func_175371_a(Blocks.dirt);
          }
        }
        else
        {
          Block var27 = Blocks.bedrock;
          var26 = func_175371_a(var27);
        }
        
        mc.getTextureManager().bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);
        func_175175_a(var24 * 16 - var12, var22 * 16 - var13, var26, 16, 16);
      }
    }
    
    GlStateManager.enableDepth();
    GlStateManager.depthFunc(515);
    mc.getTextureManager().bindTexture(field_146561_C);
    



    for (var22 = 0; var22 < AchievementList.achievementList.size(); var22++)
    {
      Achievement var34 = (Achievement)AchievementList.achievementList.get(var22);
      
      if (parentAchievement != null)
      {
        int var24 = displayColumn * 24 - var4 + 11;
        int var25 = displayRow * 24 - var5 + 11;
        int var37 = parentAchievement.displayColumn * 24 - var4 + 11;
        int var40 = parentAchievement.displayRow * 24 - var5 + 11;
        boolean var28 = statFileWriter.hasAchievementUnlocked(var34);
        boolean var29 = statFileWriter.canUnlockAchievement(var34);
        int var30 = statFileWriter.func_150874_c(var34);
        
        if (var30 <= 4)
        {
          int var31 = -16777216;
          
          if (var28)
          {
            var31 = -6250336;
          }
          else if (var29)
          {
            var31 = -16711936;
          }
          
          drawHorizontalLine(var24, var37, var25, var31);
          drawVerticalLine(var37, var25, var40, var31);
          
          if (var24 > var37)
          {
            drawTexturedModalRect(var24 - 11 - 7, var25 - 5, 114, 234, 7, 11);
          }
          else if (var24 < var37)
          {
            drawTexturedModalRect(var24 + 11, var25 - 5, 107, 234, 7, 11);
          }
          else if (var25 > var40)
          {
            drawTexturedModalRect(var24 - 5, var25 - 11 - 7, 96, 234, 11, 7);
          }
          else if (var25 < var40)
          {
            drawTexturedModalRect(var24 - 5, var25 + 11, 96, 241, 11, 7);
          }
        }
      }
    }
    
    Achievement var33 = null;
    float var23 = (p_146552_1_ - var8) * field_146570_r;
    float var35 = (p_146552_2_ - var9) * field_146570_r;
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    


    for (int var25 = 0; var25 < AchievementList.achievementList.size(); var25++)
    {
      Achievement var38 = (Achievement)AchievementList.achievementList.get(var25);
      int var40 = displayColumn * 24 - var4;
      int var41 = displayRow * 24 - var5;
      
      if ((var40 >= -24) && (var41 >= -24) && (var40 <= 224.0F * field_146570_r) && (var41 <= 155.0F * field_146570_r))
      {
        int var42 = statFileWriter.func_150874_c(var38);
        
        float var43;
        if (statFileWriter.hasAchievementUnlocked(var38))
        {
          float var43 = 0.75F;
          GlStateManager.color(var43, var43, var43, 1.0F);
        }
        else if (statFileWriter.canUnlockAchievement(var38))
        {
          float var43 = 1.0F;
          GlStateManager.color(var43, var43, var43, 1.0F);
        }
        else if (var42 < 3)
        {
          float var43 = 0.3F;
          GlStateManager.color(var43, var43, var43, 1.0F);
        }
        else if (var42 == 3)
        {
          float var43 = 0.2F;
          GlStateManager.color(var43, var43, var43, 1.0F);
        }
        else
        {
          if (var42 != 4) {
            continue;
          }
          

          var43 = 0.1F;
          GlStateManager.color(var43, var43, var43, 1.0F);
        }
        
        mc.getTextureManager().bindTexture(field_146561_C);
        
        if (var38.getSpecial())
        {
          drawTexturedModalRect(var40 - 2, var41 - 2, 26, 202, 26, 26);
        }
        else
        {
          drawTexturedModalRect(var40 - 2, var41 - 2, 0, 202, 26, 26);
        }
        
        if (!statFileWriter.canUnlockAchievement(var38))
        {
          var43 = 0.1F;
          GlStateManager.color(var43, var43, var43, 1.0F);
          itemRender.func_175039_a(false);
        }
        
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        itemRender.func_180450_b(theItemStack, var40 + 3, var41 + 3);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableLighting();
        
        if (!statFileWriter.canUnlockAchievement(var38))
        {
          itemRender.func_175039_a(true);
        }
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        if ((var23 >= var40) && (var23 <= var40 + 22) && (var35 >= var41) && (var35 <= var41 + 22))
        {
          var33 = var38;
        }
      }
    }
    
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.popMatrix();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(field_146561_C);
    drawTexturedModalRect(var6, var7, 0, 0, field_146555_f, field_146557_g);
    zLevel = 0.0F;
    GlStateManager.depthFunc(515);
    GlStateManager.disableDepth();
    GlStateManager.func_179098_w();
    super.drawScreen(p_146552_1_, p_146552_2_, p_146552_3_);
    
    if (var33 != null)
    {
      String var36 = var33.getStatName().getUnformattedText();
      String var39 = var33.getDescription();
      int var40 = p_146552_1_ + 12;
      int var41 = p_146552_2_ - 4;
      int var42 = statFileWriter.func_150874_c(var33);
      
      if (statFileWriter.canUnlockAchievement(var33))
      {
        int var30 = Math.max(fontRendererObj.getStringWidth(var36), 120);
        int var31 = fontRendererObj.splitStringWidth(var39, var30);
        
        if (statFileWriter.hasAchievementUnlocked(var33))
        {
          var31 += 12;
        }
        
        drawGradientRect(var40 - 3, var41 - 3, var40 + var30 + 3, var41 + var31 + 3 + 12, -1073741824, -1073741824);
        fontRendererObj.drawSplitString(var39, var40, var41 + 12, var30, -6250336);
        
        if (statFileWriter.hasAchievementUnlocked(var33))
        {
          fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken", new Object[0]), var40, var41 + var31 + 4, -7302913);


        }
        


      }
      else if (var42 == 3)
      {
        var36 = I18n.format("achievement.unknown", new Object[0]);
        int var30 = Math.max(fontRendererObj.getStringWidth(var36), 120);
        String var44 = new ChatComponentTranslation("achievement.requires", new Object[] { parentAchievement.getStatName() }).getUnformattedText();
        int var32 = fontRendererObj.splitStringWidth(var44, var30);
        drawGradientRect(var40 - 3, var41 - 3, var40 + var30 + 3, var41 + var32 + 12 + 3, -1073741824, -1073741824);
        fontRendererObj.drawSplitString(var44, var40, var41 + 12, var30, -9416624);
      }
      else if (var42 < 3)
      {
        int var30 = Math.max(fontRendererObj.getStringWidth(var36), 120);
        String var44 = new ChatComponentTranslation("achievement.requires", new Object[] { parentAchievement.getStatName() }).getUnformattedText();
        int var32 = fontRendererObj.splitStringWidth(var44, var30);
        drawGradientRect(var40 - 3, var41 - 3, var40 + var30 + 3, var41 + var32 + 12 + 3, -1073741824, -1073741824);
        fontRendererObj.drawSplitString(var44, var40, var41 + 12, var30, -9416624);
      }
      else
      {
        var36 = null;
      }
      

      if (var36 != null)
      {
        fontRendererObj.drawStringWithShadow(var36, var40, var41, var33.getSpecial() ? -8355776 : statFileWriter.canUnlockAchievement(var33) ? -1 : var33.getSpecial() ? -128 : -8355712);
      }
    }
    
    GlStateManager.enableDepth();
    GlStateManager.enableLighting();
    RenderHelper.disableStandardItemLighting();
  }
  
  private TextureAtlasSprite func_175371_a(Block p_175371_1_)
  {
    return Minecraft.getMinecraft().getBlockRendererDispatcher().func_175023_a().func_178122_a(p_175371_1_.getDefaultState());
  }
  



  public boolean doesGuiPauseGame()
  {
    return !loadingAchievements;
  }
}
