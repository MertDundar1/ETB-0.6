package shadersmod.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import optifine.Config;
import optifine.Lang;
import org.lwjgl.Sys;

public class GuiShaders extends GuiScreen
{
  protected GuiScreen parentGui;
  protected String screenTitle = "Shaders";
  private int updateTimer = -1;
  private GuiSlotShaders shaderList;
  private static float[] QUALITY_MULTIPLIERS = { 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F };
  private static String[] QUALITY_MULTIPLIER_NAMES = { "0.5x", "0.7x", "1x", "1.5x", "2x" };
  private static float[] HAND_DEPTH_VALUES = { 0.0625F, 0.125F, 0.25F };
  private static String[] HAND_DEPTH_NAMES = { "0.5x", "1x", "2x" };
  public static final int EnumOS_UNKNOWN = 0;
  public static final int EnumOS_WINDOWS = 1;
  public static final int EnumOS_OSX = 2;
  public static final int EnumOS_SOLARIS = 3;
  public static final int EnumOS_LINUX = 4;
  
  public GuiShaders(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
  {
    parentGui = par1GuiScreen;
  }
  



  public void initGui()
  {
    screenTitle = I18n.format("of.options.shadersTitle", new Object[0]);
    
    if (Shaders.shadersConfig == null)
    {
      Shaders.loadConfig();
    }
    
    byte btnWidth = 120;
    byte btnHeight = 20;
    int btnX = width - btnWidth - 10;
    byte baseY = 30;
    byte stepY = 20;
    int shaderListWidth = width - btnWidth - 20;
    shaderList = new GuiSlotShaders(this, shaderListWidth, height, baseY, height - 50, 16);
    shaderList.registerScrollButtons(7, 8);
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, btnX, 0 * stepY + baseY, btnWidth, btnHeight));
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, btnX, 1 * stepY + baseY, btnWidth, btnHeight));
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, btnX, 2 * stepY + baseY, btnWidth, btnHeight));
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, btnX, 3 * stepY + baseY, btnWidth, btnHeight));
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, btnX, 4 * stepY + baseY, btnWidth, btnHeight));
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, btnX, 5 * stepY + baseY, btnWidth, btnHeight));
    buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, btnX, 6 * stepY + baseY, btnWidth, btnHeight));
    int btnFolderWidth = Math.min(150, shaderListWidth / 2 - 10);
    buttonList.add(new GuiButton(201, shaderListWidth / 4 - btnFolderWidth / 2, height - 25, btnFolderWidth, btnHeight, Lang.get("of.options.shaders.shadersFolder")));
    buttonList.add(new GuiButton(202, shaderListWidth / 4 * 3 - btnFolderWidth / 2, height - 25, btnFolderWidth, btnHeight, I18n.format("gui.done", new Object[0])));
    buttonList.add(new GuiButton(203, btnX, height - 25, btnWidth, btnHeight, Lang.get("of.options.shaders.shaderOptions")));
    updateButtons();
  }
  
  public void updateButtons()
  {
    boolean shaderActive = Config.isShaders();
    Iterator it = buttonList.iterator();
    
    while (it.hasNext())
    {
      GuiButton button = (GuiButton)it.next();
      
      if ((id != 201) && (id != 202) && (id != EnumShaderOption.ANTIALIASING.ordinal()))
      {
        enabled = shaderActive;
      }
    }
  }
  


  public void handleMouseInput()
    throws IOException
  {
    super.handleMouseInput();
    shaderList.func_178039_p();
  }
  
  protected void actionPerformed(GuiButton button)
  {
    if (enabled)
    {
      if ((button instanceof GuiButtonEnumShaderOption))
      {
        GuiButtonEnumShaderOption var12 = (GuiButtonEnumShaderOption)button;
        




        switch (NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[var12.getEnumShaderOption().ordinal()])
        {
        case 1: 
          Shaders.nextAntialiasingLevel();
          Shaders.uninit();
          break;
        
        case 2: 
          Shaders.configNormalMap = !Shaders.configNormalMap;
          mc.func_175603_A();
          break;
        
        case 3: 
          Shaders.configSpecularMap = !Shaders.configSpecularMap;
          mc.func_175603_A();
          break;
        
        case 4: 
          float var13 = Shaders.configRenderResMul;
          float[] var15 = QUALITY_MULTIPLIERS;
          String[] names = QUALITY_MULTIPLIER_NAMES;
          int index = getValueIndex(var13, var15);
          
          if (isShiftKeyDown())
          {
            index--;
            
            if (index < 0)
            {
              index = var15.length - 1;
            }
          }
          else
          {
            index++;
            
            if (index >= var15.length)
            {
              index = 0;
            }
          }
          
          Shaders.configRenderResMul = var15[index];
          Shaders.scheduleResize();
          break;
        
        case 5: 
          float var13 = Shaders.configShadowResMul;
          float[] var15 = QUALITY_MULTIPLIERS;
          String[] names = QUALITY_MULTIPLIER_NAMES;
          int index = getValueIndex(var13, var15);
          
          if (isShiftKeyDown())
          {
            index--;
            
            if (index < 0)
            {
              index = var15.length - 1;
            }
          }
          else
          {
            index++;
            
            if (index >= var15.length)
            {
              index = 0;
            }
          }
          
          Shaders.configShadowResMul = var15[index];
          Shaders.scheduleResizeShadow();
          break;
        
        case 6: 
          float var13 = Shaders.configHandDepthMul;
          float[] var15 = HAND_DEPTH_VALUES;
          String[] names = HAND_DEPTH_NAMES;
          int index = getValueIndex(var13, var15);
          
          if (isShiftKeyDown())
          {
            index--;
            
            if (index < 0)
            {
              index = var15.length - 1;
            }
          }
          else
          {
            index++;
            
            if (index >= var15.length)
            {
              index = 0;
            }
          }
          
          Shaders.configHandDepthMul = var15[index];
          break;
        
        case 7: 
          Shaders.configCloudShadow = !Shaders.configCloudShadow;
          break;
        
        case 8: 
          Shaders.configOldLighting.nextValue();
          Shaders.updateBlockLightLevel();
          mc.func_175603_A();
          break;
        
        case 9: 
          Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
          break;
        
        case 10: 
          Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
          Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
          displayString = ("Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB]);
          ShadersTex.updateTextureMinMagFilter();
          break;
        
        case 11: 
          Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
          displayString = ("Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN]);
          ShadersTex.updateTextureMinMagFilter();
          break;
        
        case 12: 
          Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
          displayString = ("Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS]);
          ShadersTex.updateTextureMinMagFilter();
          break;
        
        case 13: 
          Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
          displayString = ("ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum));
          ShadersTex.updateTextureMinMagFilter();
        }
        
        var12.updateButtonText();
      }
      else
      {
        switch (id)
        {
        case 201: 
          switch (getOSType())
          {
          case 1: 
            String gbeso = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] { Shaders.shaderpacksdir.getAbsolutePath() });
            
            try
            {
              Runtime.getRuntime().exec(gbeso);
              return;
            }
            catch (IOException var9)
            {
              var9.printStackTrace();
            }
          

          case 2: 
            try
            {
              Runtime.getRuntime().exec(new String[] { "/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath() });
              return;
            }
            catch (IOException var10)
            {
              var10.printStackTrace();
            }
          }
          
          boolean var11 = false;
          
          try
          {
            Class val = Class.forName("java.awt.Desktop");
            Object var14 = val.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            val.getMethod("browse", new Class[] { java.net.URI.class }).invoke(var14, new Object[] { new File(mc.mcDataDir, Shaders.shaderpacksdirname).toURI() });
          }
          catch (Throwable var8)
          {
            var8.printStackTrace();
            var11 = true;
          }
          
          if (var11)
          {
            Config.dbg("Opening via system class!");
            Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
          }
          
          break;
        
        case 202: 
          new File(Shaders.shadersdir, "current.cfg");
          
          try
          {
            Shaders.storeConfig();
          }
          catch (Exception localException) {}
          



          mc.displayGuiScreen(parentGui);
          break;
        
        case 203: 
          GuiShaderOptions values = new GuiShaderOptions(this, Config.getGameSettings());
          Config.getMinecraft().displayGuiScreen(values);
          break;
        
        default: 
          shaderList.actionPerformed(button);
        }
        
      }
    }
  }
  


  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    drawDefaultBackground();
    shaderList.drawScreen(mouseX, mouseY, partialTicks);
    
    if (updateTimer <= 0)
    {
      shaderList.updateList();
      updateTimer += 20;
    }
    
    drawCenteredString(fontRendererObj, screenTitle + " ", width / 2, 15, 16777215);
    String info = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
    int infoWidth = fontRendererObj.getStringWidth(info);
    
    if (infoWidth < width - 5)
    {
      drawCenteredString(fontRendererObj, info, width / 2, height - 40, 8421504);
    }
    else
    {
      drawString(fontRendererObj, info, 5, height - 40, 8421504);
    }
    
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  



  public void updateScreen()
  {
    super.updateScreen();
    updateTimer -= 1;
  }
  
  public Minecraft getMc()
  {
    return mc;
  }
  
  public void drawCenteredString(String text, int x, int y, int color)
  {
    drawCenteredString(fontRendererObj, text, x, y, color);
  }
  
  public static String toStringOnOff(boolean value)
  {
    String on = Lang.getOn();
    String off = Lang.getOff();
    return value ? on : off;
  }
  
  public static String toStringAa(int value)
  {
    return value == 4 ? "FXAA 4x" : value == 2 ? "FXAA 2x" : Lang.getOff();
  }
  
  public static String toStringValue(float val, float[] values, String[] names)
  {
    int index = getValueIndex(val, values);
    return names[index];
  }
  
  public static int getValueIndex(float val, float[] values)
  {
    for (int i = 0; i < values.length; i++)
    {
      float value = values[i];
      
      if (value >= val)
      {
        return i;
      }
    }
    
    return values.length - 1;
  }
  
  public static String toStringQuality(float val)
  {
    return toStringValue(val, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
  }
  
  public static String toStringHandDepth(float val)
  {
    return toStringValue(val, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
  }
  
  public static int getOSType()
  {
    String osName = System.getProperty("os.name").toLowerCase();
    return osName.contains("unix") ? 4 : osName.contains("linux") ? 4 : osName.contains("sunos") ? 3 : osName.contains("solaris") ? 3 : osName.contains("mac") ? 2 : osName.contains("win") ? 1 : 0;
  }
  
  static class NamelessClass1647571870
  {
    static final int[] $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];
    
    static
    {
      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
      }
      catch (NoSuchFieldError localNoSuchFieldError7) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
      }
      catch (NoSuchFieldError localNoSuchFieldError8) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 9;
      }
      catch (NoSuchFieldError localNoSuchFieldError9) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 10;
      }
      catch (NoSuchFieldError localNoSuchFieldError10) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 11;
      }
      catch (NoSuchFieldError localNoSuchFieldError11) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 12;
      }
      catch (NoSuchFieldError localNoSuchFieldError12) {}
      



      try
      {
        $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 13;
      }
      catch (NoSuchFieldError localNoSuchFieldError13) {}
    }
    
    NamelessClass1647571870() {}
  }
}
