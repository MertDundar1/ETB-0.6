package net.minecraft.client.gui;

import com.enjoytheban.api.EventBus;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.WorldInfo;
import optifine.Config;
import optifine.CustomColors;

public class GuiIngame extends Gui
{
  private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
  private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
  private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
  private final Random rand = new Random();
  
  private final Minecraft mc;
  
  private final RenderItem itemRenderer;
  
  private final GuiNewChat persistantChatGUI;
  
  private final GuiStreamIndicator streamIndicator;
  private int updateCounter;
  private String recordPlaying = "";
  

  private int recordPlayingUpFor;
  
  private boolean recordIsPlaying;
  
  public float prevVignetteBrightness = 1.0F;
  
  private int remainingHighlightTicks;
  
  private ItemStack highlightingItemStack;
  
  private final GuiOverlayDebug overlayDebug;
  
  private final GuiSpectator field_175197_u;
  private final GuiPlayerTabOverlay overlayPlayerList;
  private int field_175195_w;
  private String field_175201_x = "";
  private String field_175200_y = "";
  private int field_175199_z;
  private int field_175192_A;
  private int field_175193_B;
  private int field_175194_C = 0;
  private int field_175189_D = 0;
  private long field_175190_E = 0L;
  private long field_175191_F = 0L;
  private static final String __OBFID = "CL_00000661";
  
  public GuiIngame(Minecraft mcIn)
  {
    mc = mcIn;
    itemRenderer = mcIn.getRenderItem();
    overlayDebug = new GuiOverlayDebug(mcIn);
    field_175197_u = new GuiSpectator(mcIn);
    persistantChatGUI = new GuiNewChat(mcIn);
    streamIndicator = new GuiStreamIndicator(mcIn);
    overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
    func_175177_a();
  }
  
  public void func_175177_a()
  {
    field_175199_z = 10;
    field_175192_A = 70;
    field_175193_B = 20;
  }
  
  public void func_175180_a(float p_175180_1_)
  {
    ScaledResolution var2 = new ScaledResolution(mc);
    int var3 = var2.getScaledWidth();
    int var4 = var2.getScaledHeight();
    mc.entityRenderer.setupOverlayRendering();
    GlStateManager.enableBlend();
    
    if (Config.isVignetteEnabled())
    {
      func_180480_a(mc.thePlayer.getBrightness(p_175180_1_), var2);
    }
    else
    {
      GlStateManager.enableDepth();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }
    
    ItemStack var5 = mc.thePlayer.inventory.armorItemInSlot(3);
    
    if ((mc.gameSettings.thirdPersonView == 0) && (var5 != null) && (var5.getItem() == Item.getItemFromBlock(Blocks.pumpkin)))
    {
      func_180476_e(var2);
    }
    


    if (!mc.thePlayer.isPotionActive(Potion.confusion))
    {
      float var7 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * p_175180_1_;
      
      if (var7 > 0.0F)
      {
        func_180474_b(var7, var2);
      }
    }
    
    if (mc.playerController.enableEverythingIsScrewedUpMode())
    {
      field_175197_u.func_175264_a(var2, p_175180_1_);
    }
    else
    {
      func_180479_a(var2, p_175180_1_);
    }
    
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(icons);
    GlStateManager.enableBlend();
    
    if ((func_175183_b()) && (mc.gameSettings.thirdPersonView < 1))
    {
      GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
      GlStateManager.enableAlpha();
      drawTexturedModalRect(var3 / 2 - 7, var4 / 2 - 7, 0, 0, 16, 16);
    }
    
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    mc.mcProfiler.startSection("bossHealth");
    renderBossHealth();
    mc.mcProfiler.endSection();
    
    if (mc.playerController.shouldDrawHUD())
    {
      func_180477_d(var2);
    }
    
    GlStateManager.disableBlend();
    


    if (mc.thePlayer.getSleepTimer() > 0)
    {
      mc.mcProfiler.startSection("sleep");
      GlStateManager.disableDepth();
      GlStateManager.disableAlpha();
      int var11 = mc.thePlayer.getSleepTimer();
      float var7 = var11 / 100.0F;
      
      if (var7 > 1.0F)
      {
        var7 = 1.0F - (var11 - 100) / 10.0F;
      }
      
      int var8 = (int)(220.0F * var7) << 24 | 0x101020;
      drawRect(0.0D, 0.0D, var3, var4, var8);
      GlStateManager.enableAlpha();
      GlStateManager.enableDepth();
      mc.mcProfiler.endSection();
    }
    
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    int var11 = var3 / 2 - 91;
    
    if (mc.thePlayer.isRidingHorse())
    {
      func_175186_a(var2, var11);
    }
    else if (mc.playerController.gameIsSurvivalOrAdventure())
    {
      func_175176_b(var2, var11);
    }
    
    if ((mc.gameSettings.heldItemTooltips) && (!mc.playerController.enableEverythingIsScrewedUpMode()))
    {
      func_175182_a(var2);
    }
    else if (mc.thePlayer.func_175149_v())
    {
      field_175197_u.func_175263_a(var2);
    }
    
    if (mc.isDemo())
    {
      func_175185_b(var2);
    }
    
    if (mc.gameSettings.showDebugInfo)
    {
      overlayDebug.func_175237_a(var2);
    }
    


    if (recordPlayingUpFor > 0)
    {
      mc.mcProfiler.startSection("overlayMessage");
      float var7 = recordPlayingUpFor - p_175180_1_;
      int var8 = (int)(var7 * 255.0F / 20.0F);
      
      if (var8 > 255)
      {
        var8 = 255;
      }
      
      if (var8 > 8)
      {
        GlStateManager.pushMatrix();
        GlStateManager.translate(var3 / 2, var4 - 68, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int var9 = 16777215;
        
        if (recordIsPlaying)
        {
          var9 = java.awt.Color.HSBtoRGB(var7 / 50.0F, 0.7F, 0.6F) & 0xFFFFFF;
        }
        
        func_175179_f().drawString(recordPlaying, -func_175179_f().getStringWidth(recordPlaying) / 2, -4, var9 + (var8 << 24 & 0xFF000000));
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      }
      
      mc.mcProfiler.endSection();
    }
    
    if (field_175195_w > 0)
    {
      mc.mcProfiler.startSection("titleAndSubtitle");
      float var7 = field_175195_w - p_175180_1_;
      int var8 = 255;
      
      if (field_175195_w > field_175193_B + field_175192_A)
      {
        float var12 = field_175199_z + field_175192_A + field_175193_B - var7;
        var8 = (int)(var12 * 255.0F / field_175199_z);
      }
      
      if (field_175195_w <= field_175193_B)
      {
        var8 = (int)(var7 * 255.0F / field_175193_B);
      }
      
      var8 = MathHelper.clamp_int(var8, 0, 255);
      
      if (var8 > 8)
      {
        GlStateManager.pushMatrix();
        GlStateManager.translate(var3 / 2, var4 / 2, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.scale(4.0F, 4.0F, 4.0F);
        int var9 = var8 << 24 & 0xFF000000;
        func_175179_f().func_175065_a(field_175201_x, -func_175179_f().getStringWidth(field_175201_x) / 2, -10.0F, 0xFFFFFF | var9, true);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        func_175179_f().func_175065_a(field_175200_y, -func_175179_f().getStringWidth(field_175200_y) / 2, 5.0F, 0xFFFFFF | var9, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      }
      
      mc.mcProfiler.endSection();
    }
    
    Scoreboard var121 = mc.theWorld.getScoreboard();
    ScoreObjective var13 = null;
    ScorePlayerTeam var15 = var121.getPlayersTeam(mc.thePlayer.getName());
    
    if (var15 != null)
    {
      int var16 = var15.func_178775_l().func_175746_b();
      
      if (var16 >= 0)
      {
        var13 = var121.getObjectiveInDisplaySlot(3 + var16);
      }
    }
    
    ScoreObjective var161 = var13 != null ? var13 : var121.getObjectiveInDisplaySlot(1);
    
    if (var161 != null)
    {
      func_180475_a(var161, var2);
    }
    
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.disableAlpha();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, var4 - 48, 0.0F);
    mc.mcProfiler.startSection("chat");
    persistantChatGUI.drawChat(updateCounter);
    mc.mcProfiler.endSection();
    GlStateManager.popMatrix();
    var161 = var121.getObjectiveInDisplaySlot(0);
    
    if ((mc.gameSettings.keyBindPlayerList.getIsKeyPressed()) && ((!mc.isIntegratedServerRunning()) || (mc.thePlayer.sendQueue.func_175106_d().size() > 1) || (var161 != null)))
    {
      overlayPlayerList.func_175246_a(true);
      overlayPlayerList.func_175249_a(var3, var121, var161);
    }
    else
    {
      overlayPlayerList.func_175246_a(false);
    }
    
    EventBus.getInstance().call(new com.enjoytheban.api.events.rendering.EventRender2D(p_175180_1_));
    
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableLighting();
    GlStateManager.enableAlpha();
  }
  
  protected void func_180479_a(ScaledResolution p_180479_1_, float p_180479_2_)
  {
    if ((mc.func_175606_aa() instanceof EntityPlayer))
    {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      mc.getTextureManager().bindTexture(widgetsTexPath);
      EntityPlayer var3 = (EntityPlayer)mc.func_175606_aa();
      int var4 = p_180479_1_.getScaledWidth() / 2;
      float var5 = zLevel;
      zLevel = -90.0F;
      drawTexturedModalRect(var4 - 91, p_180479_1_.getScaledHeight() - 22, 0, 0, 182, 22);
      drawTexturedModalRect(var4 - 91 - 1 + inventory.currentItem * 20, p_180479_1_.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
      zLevel = var5;
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.enableGUIStandardItemLighting();
      
      for (int var6 = 0; var6 < 9; var6++)
      {
        int var7 = p_180479_1_.getScaledWidth() / 2 - 90 + var6 * 20 + 2;
        int var8 = p_180479_1_.getScaledHeight() - 16 - 3;
        func_175184_a(var6, var7, var8, p_180479_2_, var3);
      }
      
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
    }
  }
  
  public void func_175186_a(ScaledResolution p_175186_1_, int p_175186_2_)
  {
    mc.mcProfiler.startSection("jumpBar");
    mc.getTextureManager().bindTexture(Gui.icons);
    float var3 = mc.thePlayer.getHorseJumpPower();
    short var4 = 182;
    int var5 = (int)(var3 * (var4 + 1));
    int var6 = p_175186_1_.getScaledHeight() - 32 + 3;
    drawTexturedModalRect(p_175186_2_, var6, 0, 84, var4, 5);
    
    if (var5 > 0)
    {
      drawTexturedModalRect(p_175186_2_, var6, 0, 89, var5, 5);
    }
    
    mc.mcProfiler.endSection();
  }
  
  public void func_175176_b(ScaledResolution p_175176_1_, int p_175176_2_)
  {
    mc.mcProfiler.startSection("expBar");
    mc.getTextureManager().bindTexture(Gui.icons);
    int var3 = mc.thePlayer.xpBarCap();
    

    if (var3 > 0)
    {
      short var9 = 182;
      int var10 = (int)(mc.thePlayer.experience * (var9 + 1));
      int var6 = p_175176_1_.getScaledHeight() - 32 + 3;
      drawTexturedModalRect(p_175176_2_, var6, 0, 64, var9, 5);
      
      if (var10 > 0)
      {
        drawTexturedModalRect(p_175176_2_, var6, 0, 69, var10, 5);
      }
    }
    
    mc.mcProfiler.endSection();
    
    if (mc.thePlayer.experienceLevel > 0)
    {
      mc.mcProfiler.startSection("expLevel");
      int var91 = 8453920;
      
      if (Config.isCustomColors())
      {
        var91 = CustomColors.getExpBarTextColor(var91);
      }
      
      String var101 = mc.thePlayer.experienceLevel;
      int var6 = (p_175176_1_.getScaledWidth() - func_175179_f().getStringWidth(var101)) / 2;
      int var7 = p_175176_1_.getScaledHeight() - 31 - 4;
      boolean var8 = false;
      func_175179_f().drawString(var101, var6 + 1, var7, 0);
      func_175179_f().drawString(var101, var6 - 1, var7, 0);
      func_175179_f().drawString(var101, var6, var7 + 1, 0);
      func_175179_f().drawString(var101, var6, var7 - 1, 0);
      func_175179_f().drawString(var101, var6, var7, var91);
      mc.mcProfiler.endSection();
    }
  }
  
  public void func_175182_a(ScaledResolution p_175182_1_)
  {
    mc.mcProfiler.startSection("toolHighlight");
    
    if ((remainingHighlightTicks > 0) && (highlightingItemStack != null))
    {
      String var2 = highlightingItemStack.getDisplayName();
      
      if (highlightingItemStack.hasDisplayName())
      {
        var2 = EnumChatFormatting.ITALIC + var2;
      }
      
      int var3 = (p_175182_1_.getScaledWidth() - func_175179_f().getStringWidth(var2)) / 2;
      int var4 = p_175182_1_.getScaledHeight() - 59;
      
      if (!mc.playerController.shouldDrawHUD())
      {
        var4 += 14;
      }
      
      int var5 = (int)(remainingHighlightTicks * 256.0F / 10.0F);
      
      if (var5 > 255)
      {
        var5 = 255;
      }
      
      if (var5 > 0)
      {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        func_175179_f().drawStringWithShadow(var2, var3, var4, 16777215 + (var5 << 24));
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      }
    }
    
    mc.mcProfiler.endSection();
  }
  
  public void func_175185_b(ScaledResolution p_175185_1_)
  {
    mc.mcProfiler.startSection("demo");
    String var2 = "";
    
    if (mc.theWorld.getTotalWorldTime() >= 120500L)
    {
      var2 = I18n.format("demo.demoExpired", new Object[0]);
    }
    else
    {
      var2 = I18n.format("demo.remainingTime", new Object[] { net.minecraft.util.StringUtils.ticksToElapsedTime((int)(120500L - mc.theWorld.getTotalWorldTime())) });
    }
    
    int var3 = func_175179_f().getStringWidth(var2);
    func_175179_f().drawStringWithShadow(var2, p_175185_1_.getScaledWidth() - var3 - 10, 5.0F, 16777215);
    mc.mcProfiler.endSection();
  }
  
  protected boolean func_175183_b()
  {
    if ((mc.gameSettings.showDebugInfo) && (!mc.thePlayer.func_175140_cp()) && (!mc.gameSettings.field_178879_v))
    {
      return false;
    }
    if (mc.playerController.enableEverythingIsScrewedUpMode())
    {
      if (mc.pointedEntity != null)
      {
        return true;
      }
      

      if ((mc.objectMouseOver != null) && (mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK))
      {
        net.minecraft.util.BlockPos var1 = mc.objectMouseOver.func_178782_a();
        
        if ((mc.theWorld.getTileEntity(var1) instanceof IInventory))
        {
          return true;
        }
      }
      
      return false;
    }
    


    return true;
  }
  

  public void func_180478_c(ScaledResolution p_180478_1_)
  {
    streamIndicator.render(p_180478_1_.getScaledWidth() - 10, 10);
  }
  
  private void func_180475_a(ScoreObjective p_180475_1_, ScaledResolution p_180475_2_)
  {
    Scoreboard var3 = p_180475_1_.getScoreboard();
    Collection var4 = var3.getSortedScores(p_180475_1_);
    ArrayList var5 = Lists.newArrayList(Iterables.filter(var4, new com.google.common.base.Predicate()
    {
      private static final String __OBFID = "CL_00001958";
      
      public boolean func_178903_a(Score p_178903_1_) {
        return (p_178903_1_.getPlayerName() != null) && (!p_178903_1_.getPlayerName().startsWith("#"));
      }
      
      public boolean apply(Object p_apply_1_) {
        return func_178903_a((Score)p_apply_1_);
      }
    }));
    ArrayList var21;
    ArrayList var21;
    if (var5.size() > 15)
    {
      var21 = Lists.newArrayList(Iterables.skip(var5, var4.size() - 15));
    }
    else
    {
      var21 = var5;
    }
    
    int var6 = func_175179_f().getStringWidth(p_180475_1_.getDisplayName());
    
    String var10;
    for (Iterator var22 = var21.iterator(); var22.hasNext(); var6 = Math.max(var6, func_175179_f().getStringWidth(var10)))
    {
      Score var23 = (Score)var22.next();
      ScorePlayerTeam var24 = var3.getPlayersTeam(var23.getPlayerName());
      var10 = ScorePlayerTeam.formatPlayerName(var24, var23.getPlayerName()) + ": " + EnumChatFormatting.RED + var23.getScorePoints();
    }
    
    int var221 = var21.size() * func_175179_fFONT_HEIGHT;
    int var231 = p_180475_2_.getScaledHeight() / 2 + var221 / 3;
    byte var241 = 3;
    int var25 = p_180475_2_.getScaledWidth() - var6 - var241;
    int var11 = 0;
    Iterator var12 = var21.iterator();
    
    while (var12.hasNext())
    {
      Score var13 = (Score)var12.next();
      var11++;
      ScorePlayerTeam var14 = var3.getPlayersTeam(var13.getPlayerName());
      String var15 = ScorePlayerTeam.formatPlayerName(var14, var13.getPlayerName());
      String var16 = EnumChatFormatting.RED + var13.getScorePoints();
      int var18 = var231 - var11 * func_175179_fFONT_HEIGHT;
      int var19 = p_180475_2_.getScaledWidth() - var241 + 2;
      drawRect(var25 - 2, var18, var19, var18 + func_175179_fFONT_HEIGHT, 1342177280);
      func_175179_f().drawString(var15, var25, var18, 553648127);
      func_175179_f().drawString(var16, var19 - func_175179_f().getStringWidth(var16), var18, 553648127);
      
      if (var11 == var21.size())
      {
        String var20 = p_180475_1_.getDisplayName();
        drawRect(var25 - 2, var18 - func_175179_fFONT_HEIGHT - 1, var19, var18 - 1, 1610612736);
        drawRect(var25 - 2, var18 - 1, var19, var18, 1342177280);
        func_175179_f().drawString(var20, var25 + var6 / 2 - func_175179_f().getStringWidth(var20) / 2, var18 - func_175179_fFONT_HEIGHT, 553648127);
      }
    }
  }
  
  private void func_180477_d(ScaledResolution p_180477_1_)
  {
    if ((mc.func_175606_aa() instanceof EntityPlayer))
    {
      EntityPlayer var2 = (EntityPlayer)mc.func_175606_aa();
      int var3 = MathHelper.ceiling_float_int(var2.getHealth());
      boolean var4 = (field_175191_F > updateCounter) && ((field_175191_F - updateCounter) / 3L % 2L == 1L);
      
      if ((var3 < field_175194_C) && (hurtResistantTime > 0))
      {
        field_175190_E = Minecraft.getSystemTime();
        field_175191_F = (updateCounter + 20);
      }
      else if ((var3 > field_175194_C) && (hurtResistantTime > 0))
      {
        field_175190_E = Minecraft.getSystemTime();
        field_175191_F = (updateCounter + 10);
      }
      
      if (Minecraft.getSystemTime() - field_175190_E > 1000L)
      {
        field_175194_C = var3;
        field_175189_D = var3;
        field_175190_E = Minecraft.getSystemTime();
      }
      
      field_175194_C = var3;
      int var5 = field_175189_D;
      rand.setSeed(updateCounter * 312871);
      boolean var6 = false;
      FoodStats var7 = var2.getFoodStats();
      int var8 = var7.getFoodLevel();
      int var9 = var7.getPrevFoodLevel();
      IAttributeInstance var10 = var2.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.maxHealth);
      int var11 = p_180477_1_.getScaledWidth() / 2 - 91;
      int var12 = p_180477_1_.getScaledWidth() / 2 + 91;
      int var13 = p_180477_1_.getScaledHeight() - 39;
      float var14 = (float)var10.getAttributeValue();
      float var15 = var2.getAbsorptionAmount();
      int var16 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F / 10.0F);
      int var17 = Math.max(10 - (var16 - 2), 3);
      int var18 = var13 - (var16 - 1) * var17 - 10;
      float var19 = var15;
      int var20 = var2.getTotalArmorValue();
      int var21 = -1;
      
      if (var2.isPotionActive(Potion.regeneration))
      {
        var21 = updateCounter % MathHelper.ceiling_float_int(var14 + 5.0F);
      }
      
      mc.mcProfiler.startSection("armor");
      


      for (int var22 = 0; var22 < 10; var22++)
      {
        if (var20 > 0)
        {
          int var23 = var11 + var22 * 8;
          
          if (var22 * 2 + 1 < var20)
          {
            drawTexturedModalRect(var23, var18, 34, 9, 9, 9);
          }
          
          if (var22 * 2 + 1 == var20)
          {
            drawTexturedModalRect(var23, var18, 25, 9, 9, 9);
          }
          
          if (var22 * 2 + 1 > var20)
          {
            drawTexturedModalRect(var23, var18, 16, 9, 9, 9);
          }
        }
      }
      
      mc.mcProfiler.endStartSection("health");
      



      for (var22 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F) - 1; var22 >= 0; var22--)
      {
        int var23 = 16;
        
        if (var2.isPotionActive(Potion.poison))
        {
          var23 += 36;
        }
        else if (var2.isPotionActive(Potion.wither))
        {
          var23 += 72;
        }
        
        byte var34 = 0;
        
        if (var4)
        {
          var34 = 1;
        }
        
        int var25 = MathHelper.ceiling_float_int((var22 + 1) / 10.0F) - 1;
        int var26 = var11 + var22 % 10 * 8;
        int var27 = var13 - var25 * var17;
        
        if (var3 <= 4)
        {
          var27 += rand.nextInt(2);
        }
        
        if (var22 == var21)
        {
          var27 -= 2;
        }
        
        byte var36 = 0;
        
        if (worldObj.getWorldInfo().isHardcoreModeEnabled())
        {
          var36 = 5;
        }
        
        drawTexturedModalRect(var26, var27, 16 + var34 * 9, 9 * var36, 9, 9);
        
        if (var4)
        {
          if (var22 * 2 + 1 < var5)
          {
            drawTexturedModalRect(var26, var27, var23 + 54, 9 * var36, 9, 9);
          }
          
          if (var22 * 2 + 1 == var5)
          {
            drawTexturedModalRect(var26, var27, var23 + 63, 9 * var36, 9, 9);
          }
        }
        
        if (var19 > 0.0F)
        {
          if ((var19 == var15) && (var15 % 2.0F == 1.0F))
          {
            drawTexturedModalRect(var26, var27, var23 + 153, 9 * var36, 9, 9);
          }
          else
          {
            drawTexturedModalRect(var26, var27, var23 + 144, 9 * var36, 9, 9);
          }
          
          var19 -= 2.0F;
        }
        else
        {
          if (var22 * 2 + 1 < var3)
          {
            drawTexturedModalRect(var26, var27, var23 + 36, 9 * var36, 9, 9);
          }
          
          if (var22 * 2 + 1 == var3)
          {
            drawTexturedModalRect(var26, var27, var23 + 45, 9 * var36, 9, 9);
          }
        }
      }
      
      Entity var371 = ridingEntity;
      

      if (var371 == null)
      {
        mc.mcProfiler.endStartSection("food");
        
        for (int var23 = 0; var23 < 10; var23++)
        {
          int var38 = var13;
          int var25 = 16;
          byte var35 = 0;
          
          if (var2.isPotionActive(Potion.hunger))
          {
            var25 += 36;
            var35 = 13;
          }
          
          if ((var2.getFoodStats().getSaturationLevel() <= 0.0F) && (updateCounter % (var8 * 3 + 1) == 0))
          {
            var38 = var13 + (rand.nextInt(3) - 1);
          }
          
          if (var6)
          {
            var35 = 1;
          }
          
          int var27 = var12 - var23 * 8 - 9;
          drawTexturedModalRect(var27, var38, 16 + var35 * 9, 27, 9, 9);
          
          if (var6)
          {
            if (var23 * 2 + 1 < var9)
            {
              drawTexturedModalRect(var27, var38, var25 + 54, 27, 9, 9);
            }
            
            if (var23 * 2 + 1 == var9)
            {
              drawTexturedModalRect(var27, var38, var25 + 63, 27, 9, 9);
            }
          }
          
          if (var23 * 2 + 1 < var8)
          {
            drawTexturedModalRect(var27, var38, var25 + 36, 27, 9, 9);
          }
          
          if (var23 * 2 + 1 == var8)
          {
            drawTexturedModalRect(var27, var38, var25 + 45, 27, 9, 9);
          }
        }
      }
      else if ((var371 instanceof EntityLivingBase))
      {
        mc.mcProfiler.endStartSection("mountHealth");
        EntityLivingBase var391 = (EntityLivingBase)var371;
        int var38 = (int)Math.ceil(var391.getHealth());
        float var37 = var391.getMaxHealth();
        int var26 = (int)(var37 + 0.5F) / 2;
        
        if (var26 > 30)
        {
          var26 = 30;
        }
        
        int var27 = var13;
        
        for (int var39 = 0; var26 > 0; var39 += 20)
        {
          int var29 = Math.min(var26, 10);
          var26 -= var29;
          
          for (int var30 = 0; var30 < var29; var30++)
          {
            byte var31 = 52;
            byte var32 = 0;
            
            if (var6)
            {
              var32 = 1;
            }
            
            int var33 = var12 - var30 * 8 - 9;
            drawTexturedModalRect(var33, var27, var31 + var32 * 9, 9, 9, 9);
            
            if (var30 * 2 + 1 + var39 < var38)
            {
              drawTexturedModalRect(var33, var27, var31 + 36, 9, 9, 9);
            }
            
            if (var30 * 2 + 1 + var39 == var38)
            {
              drawTexturedModalRect(var33, var27, var31 + 45, 9, 9, 9);
            }
          }
          
          var27 -= 10;
        }
      }
      
      mc.mcProfiler.endStartSection("air");
      
      if (var2.isInsideOfMaterial(net.minecraft.block.material.Material.water))
      {
        int var23 = mc.thePlayer.getAir();
        int var38 = MathHelper.ceiling_double_int((var23 - 2) * 10.0D / 300.0D);
        int var25 = MathHelper.ceiling_double_int(var23 * 10.0D / 300.0D) - var38;
        
        for (int var26 = 0; var26 < var38 + var25; var26++)
        {
          if (var26 < var38)
          {
            drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 16, 18, 9, 9);
          }
          else
          {
            drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 25, 18, 9, 9);
          }
        }
      }
      
      mc.mcProfiler.endSection();
    }
  }
  



  private void renderBossHealth()
  {
    if ((BossStatus.bossName != null) && (BossStatus.statusBarTime > 0))
    {
      BossStatus.statusBarTime -= 1;
      FontRenderer var1 = mc.fontRendererObj;
      ScaledResolution var2 = new ScaledResolution(mc);
      int var3 = var2.getScaledWidth();
      short var4 = 182;
      int var5 = var3 / 2 - var4 / 2;
      int var6 = (int)(BossStatus.healthScale * (var4 + 1));
      byte var7 = 12;
      drawTexturedModalRect(var5, var7, 0, 74, var4, 5);
      drawTexturedModalRect(var5, var7, 0, 74, var4, 5);
      
      if (var6 > 0)
      {
        drawTexturedModalRect(var5, var7, 0, 79, var6, 5);
      }
      
      String var8 = BossStatus.bossName;
      int bossTextColor = 16777215;
      
      if (Config.isCustomColors())
      {
        bossTextColor = CustomColors.getBossTextColor(bossTextColor);
      }
      
      func_175179_f().drawStringWithShadow(var8, var3 / 2 - func_175179_f().getStringWidth(var8) / 2, var7 - 10, bossTextColor);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      mc.getTextureManager().bindTexture(icons);
    }
  }
  
  private void func_180476_e(ScaledResolution p_180476_1_)
  {
    GlStateManager.disableDepth();
    GlStateManager.depthMask(false);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableAlpha();
    mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
    Tessellator var2 = Tessellator.getInstance();
    WorldRenderer var3 = var2.getWorldRenderer();
    var3.startDrawingQuads();
    var3.addVertexWithUV(0.0D, p_180476_1_.getScaledHeight(), -90.0D, 0.0D, 1.0D);
    var3.addVertexWithUV(p_180476_1_.getScaledWidth(), p_180476_1_.getScaledHeight(), -90.0D, 1.0D, 1.0D);
    var3.addVertexWithUV(p_180476_1_.getScaledWidth(), 0.0D, -90.0D, 1.0D, 0.0D);
    var3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
    var2.draw();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableAlpha();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  private void func_180480_a(float p_180480_1_, ScaledResolution p_180480_2_)
  {
    if (Config.isVignetteEnabled())
    {
      p_180480_1_ = 1.0F - p_180480_1_;
      p_180480_1_ = MathHelper.clamp_float(p_180480_1_, 0.0F, 1.0F);
      WorldBorder var3 = mc.theWorld.getWorldBorder();
      float var4 = (float)var3.getClosestDistance(mc.thePlayer);
      double var5 = Math.min(var3.func_177749_o() * var3.getWarningTime() * 1000.0D, Math.abs(var3.getTargetSize() - var3.getDiameter()));
      double var7 = Math.max(var3.getWarningDistance(), var5);
      
      if (var4 < var7)
      {
        var4 = 1.0F - (float)(var4 / var7);
      }
      else
      {
        var4 = 0.0F;
      }
      
      prevVignetteBrightness = ((float)(prevVignetteBrightness + (p_180480_1_ - prevVignetteBrightness) * 0.01D));
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
      
      if (var4 > 0.0F)
      {
        GlStateManager.color(0.0F, var4, var4, 1.0F);
      }
      else
      {
        GlStateManager.color(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);
      }
      
      mc.getTextureManager().bindTexture(vignetteTexPath);
      Tessellator var9 = Tessellator.getInstance();
      WorldRenderer var10 = var9.getWorldRenderer();
      var10.startDrawingQuads();
      var10.addVertexWithUV(0.0D, p_180480_2_.getScaledHeight(), -90.0D, 0.0D, 1.0D);
      var10.addVertexWithUV(p_180480_2_.getScaledWidth(), p_180480_2_.getScaledHeight(), -90.0D, 1.0D, 1.0D);
      var10.addVertexWithUV(p_180480_2_.getScaledWidth(), 0.0D, -90.0D, 1.0D, 0.0D);
      var10.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
      var9.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }
  }
  
  private void func_180474_b(float p_180474_1_, ScaledResolution p_180474_2_)
  {
    if (p_180474_1_ < 1.0F)
    {
      p_180474_1_ *= p_180474_1_;
      p_180474_1_ *= p_180474_1_;
      p_180474_1_ = p_180474_1_ * 0.8F + 0.2F;
    }
    
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    GlStateManager.depthMask(false);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(1.0F, 1.0F, 1.0F, p_180474_1_);
    mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    TextureAtlasSprite var3 = mc.getBlockRendererDispatcher().func_175023_a().func_178122_a(Blocks.portal.getDefaultState());
    float var4 = var3.getMinU();
    float var5 = var3.getMinV();
    float var6 = var3.getMaxU();
    float var7 = var3.getMaxV();
    Tessellator var8 = Tessellator.getInstance();
    WorldRenderer var9 = var8.getWorldRenderer();
    var9.startDrawingQuads();
    var9.addVertexWithUV(0.0D, p_180474_2_.getScaledHeight(), -90.0D, var4, var7);
    var9.addVertexWithUV(p_180474_2_.getScaledWidth(), p_180474_2_.getScaledHeight(), -90.0D, var6, var7);
    var9.addVertexWithUV(p_180474_2_.getScaledWidth(), 0.0D, -90.0D, var6, var5);
    var9.addVertexWithUV(0.0D, 0.0D, -90.0D, var4, var5);
    var8.draw();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableAlpha();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  private void func_175184_a(int p_175184_1_, int p_175184_2_, int p_175184_3_, float p_175184_4_, EntityPlayer p_175184_5_)
  {
    ItemStack var6 = inventory.mainInventory[p_175184_1_];
    
    if (var6 != null)
    {
      float var7 = animationsToGo - p_175184_4_;
      
      if (var7 > 0.0F)
      {
        GlStateManager.pushMatrix();
        float var8 = 1.0F + var7 / 5.0F;
        GlStateManager.translate(p_175184_2_ + 8, p_175184_3_ + 12, 0.0F);
        GlStateManager.scale(1.0F / var8, (var8 + 1.0F) / 2.0F, 1.0F);
        GlStateManager.translate(-(p_175184_2_ + 8), -(p_175184_3_ + 12), 0.0F);
      }
      
      itemRenderer.func_180450_b(var6, p_175184_2_, p_175184_3_);
      
      if (var7 > 0.0F)
      {
        GlStateManager.popMatrix();
      }
      
      itemRenderer.func_175030_a(mc.fontRendererObj, var6, p_175184_2_, p_175184_3_);
    }
  }
  



  public void updateTick()
  {
    if (recordPlayingUpFor > 0)
    {
      recordPlayingUpFor -= 1;
    }
    
    if (field_175195_w > 0)
    {
      field_175195_w -= 1;
      
      if (field_175195_w <= 0)
      {
        field_175201_x = "";
        field_175200_y = "";
      }
    }
    
    updateCounter += 1;
    streamIndicator.func_152439_a();
    
    if (mc.thePlayer != null)
    {
      ItemStack var1 = mc.thePlayer.inventory.getCurrentItem();
      
      if (var1 == null)
      {
        remainingHighlightTicks = 0;
      }
      else if ((highlightingItemStack != null) && (var1.getItem() == highlightingItemStack.getItem()) && (ItemStack.areItemStackTagsEqual(var1, highlightingItemStack)) && ((var1.isItemStackDamageable()) || (var1.getMetadata() == highlightingItemStack.getMetadata())))
      {
        if (remainingHighlightTicks > 0)
        {
          remainingHighlightTicks -= 1;
        }
        
      }
      else {
        remainingHighlightTicks = 40;
      }
      
      highlightingItemStack = var1;
    }
  }
  
  public void setRecordPlayingMessage(String p_73833_1_)
  {
    setRecordPlaying(I18n.format("record.nowPlaying", new Object[] { p_73833_1_ }), true);
  }
  
  public void setRecordPlaying(String p_110326_1_, boolean p_110326_2_)
  {
    recordPlaying = p_110326_1_;
    recordPlayingUpFor = 60;
    recordIsPlaying = p_110326_2_;
  }
  
  public void func_175178_a(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_)
  {
    if ((p_175178_1_ == null) && (p_175178_2_ == null) && (p_175178_3_ < 0) && (p_175178_4_ < 0) && (p_175178_5_ < 0))
    {
      field_175201_x = "";
      field_175200_y = "";
      field_175195_w = 0;
    }
    else if (p_175178_1_ != null)
    {
      field_175201_x = p_175178_1_;
      field_175195_w = (field_175199_z + field_175192_A + field_175193_B);
    }
    else if (p_175178_2_ != null)
    {
      field_175200_y = p_175178_2_;
    }
    else
    {
      if (p_175178_3_ >= 0)
      {
        field_175199_z = p_175178_3_;
      }
      
      if (p_175178_4_ >= 0)
      {
        field_175192_A = p_175178_4_;
      }
      
      if (p_175178_5_ >= 0)
      {
        field_175193_B = p_175178_5_;
      }
      
      if (field_175195_w > 0)
      {
        field_175195_w = (field_175199_z + field_175192_A + field_175193_B);
      }
    }
  }
  
  public void func_175188_a(IChatComponent p_175188_1_, boolean p_175188_2_)
  {
    setRecordPlaying(p_175188_1_.getUnformattedText(), p_175188_2_);
  }
  



  public GuiNewChat getChatGUI()
  {
    return persistantChatGUI;
  }
  
  public int getUpdateCounter()
  {
    return updateCounter;
  }
  
  public FontRenderer func_175179_f()
  {
    return mc.fontRendererObj;
  }
  
  public GuiSpectator func_175187_g()
  {
    return field_175197_u;
  }
  
  public GuiPlayerTabOverlay getTabList()
  {
    return overlayPlayerList;
  }
}
