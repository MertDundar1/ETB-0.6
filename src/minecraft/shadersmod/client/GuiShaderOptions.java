package shadersmod.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import optifine.Config;
import optifine.Lang;
import optifine.StrUtils;

public class GuiShaderOptions extends GuiScreen
{
  private GuiScreen prevScreen;
  protected String title;
  private GameSettings settings;
  private int lastMouseX;
  private int lastMouseY;
  private long mouseStillTime;
  private String screenName;
  private String screenText;
  private boolean changed;
  public static final String OPTION_PROFILE = "<profile>";
  public static final String OPTION_EMPTY = "<empty>";
  public static final String OPTION_REST = "*";
  
  public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings)
  {
    lastMouseX = 0;
    lastMouseY = 0;
    mouseStillTime = 0L;
    screenName = null;
    screenText = null;
    changed = false;
    title = "Shader Options";
    prevScreen = guiscreen;
    settings = gamesettings;
  }
  
  public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings, String screenName)
  {
    this(guiscreen, gamesettings);
    this.screenName = screenName;
    
    if (screenName != null)
    {
      screenText = Shaders.translate("screen." + screenName, screenName);
    }
  }
  



  public void initGui()
  {
    title = I18n.format("of.options.shaderOptionsTitle", new Object[0]);
    byte baseId = 100;
    boolean baseX = false;
    byte baseY = 30;
    byte stepY = 20;
    int btnX = width - 130;
    byte btnWidth = 120;
    byte btnHeight = 20;
    int columns = 2;
    ShaderOption[] ops = Shaders.getShaderPackOptions(screenName);
    
    if (ops != null)
    {
      if (ops.length > 18)
      {
        columns = ops.length / 9 + 1;
      }
      
      for (int i = 0; i < ops.length; i++)
      {
        ShaderOption so = ops[i];
        
        if ((so != null) && (so.isVisible()))
        {
          int col = i % columns;
          int row = i / columns;
          int colWidth = Math.min(width / columns, 200);
          int var21 = (width - colWidth * columns) / 2;
          int x = col * colWidth + 5 + var21;
          int y = baseY + row * stepY;
          int w = colWidth - 10;
          String text = getButtonText(so, w);
          GuiButtonShaderOption btn = new GuiButtonShaderOption(baseId + i, x, y, w, btnHeight, so, text);
          enabled = so.isEnabled();
          buttonList.add(btn);
        }
      }
    }
    
    buttonList.add(new GuiButton(201, width / 2 - btnWidth - 20, height / 6 + 168 + 11, btnWidth, btnHeight, I18n.format("controls.reset", new Object[0])));
    buttonList.add(new GuiButton(200, width / 2 + 20, height / 6 + 168 + 11, btnWidth, btnHeight, I18n.format("gui.done", new Object[0])));
  }
  
  private String getButtonText(ShaderOption so, int btnWidth)
  {
    String labelName = so.getNameText();
    
    if ((so instanceof ShaderOptionScreen))
    {
      ShaderOptionScreen fr1 = (ShaderOptionScreen)so;
      return labelName + "...";
    }
    

    FontRenderer fr = getMinecraftfontRendererObj;
    
    for (int lenSuffix = fr.getStringWidth(": " + Lang.getOff()) + 5; (fr.getStringWidth(labelName) + lenSuffix >= btnWidth) && (labelName.length() > 0); labelName = labelName.substring(0, labelName.length() - 1)) {}
    



    String col = so.isChanged() ? so.getValueColor(so.getValue()) : "";
    String labelValue = so.getValueText(so.getValue());
    return labelName + ": " + col + labelValue;
  }
  

  protected void actionPerformed(GuiButton guibutton)
  {
    if (enabled)
    {
      if ((id < 200) && ((guibutton instanceof GuiButtonShaderOption)))
      {
        GuiButtonShaderOption opts = (GuiButtonShaderOption)guibutton;
        ShaderOption i = opts.getShaderOption();
        
        if ((i instanceof ShaderOptionScreen))
        {
          String var8 = i.getName();
          GuiShaderOptions scr = new GuiShaderOptions(this, settings, var8);
          mc.displayGuiScreen(scr);
          return;
        }
        
        i.nextValue();
        updateAllButtons();
        changed = true;
      }
      
      if (id == 201)
      {
        ShaderOption[] var6 = Shaders.getChangedOptions(Shaders.getShaderPackOptions());
        
        for (int var7 = 0; var7 < var6.length; var7++)
        {
          ShaderOption opt = var6[var7];
          opt.resetValue();
          changed = true;
        }
        
        updateAllButtons();
      }
      
      if (id == 200)
      {
        if (changed)
        {
          Shaders.saveShaderPackOptions();
          Shaders.uninit();
        }
        
        mc.displayGuiScreen(prevScreen);
      }
    }
  }
  


  protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    throws java.io.IOException
  {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    
    if (mouseButton == 1)
    {
      GuiButton btn = getSelectedButton(mouseX, mouseY);
      
      if ((btn instanceof GuiButtonShaderOption))
      {
        GuiButtonShaderOption btnSo = (GuiButtonShaderOption)btn;
        ShaderOption so = btnSo.getShaderOption();
        
        if (so.isChanged())
        {
          btnSo.playPressSound(mc.getSoundHandler());
          so.resetValue();
          changed = true;
          updateAllButtons();
        }
      }
    }
  }
  
  private void updateAllButtons()
  {
    Iterator it = buttonList.iterator();
    
    while (it.hasNext())
    {
      GuiButton btn = (GuiButton)it.next();
      
      if ((btn instanceof GuiButtonShaderOption))
      {
        GuiButtonShaderOption gbso = (GuiButtonShaderOption)btn;
        ShaderOption opt = gbso.getShaderOption();
        
        if ((opt instanceof ShaderOptionProfile))
        {
          ShaderOptionProfile optProf = (ShaderOptionProfile)opt;
          optProf.updateProfile();
        }
        
        displayString = getButtonText(opt, gbso.getButtonWidth());
      }
    }
  }
  



  public void drawScreen(int x, int y, float f)
  {
    drawDefaultBackground();
    
    if (screenText != null)
    {
      drawCenteredString(fontRendererObj, screenText, width / 2, 15, 16777215);
    }
    else
    {
      drawCenteredString(fontRendererObj, title, width / 2, 15, 16777215);
    }
    
    super.drawScreen(x, y, f);
    
    if ((Math.abs(x - lastMouseX) <= 5) && (Math.abs(y - lastMouseY) <= 5))
    {
      drawTooltips(x, y, buttonList);
    }
    else
    {
      lastMouseX = x;
      lastMouseY = y;
      mouseStillTime = System.currentTimeMillis();
    }
  }
  
  private void drawTooltips(int x, int y, List buttonList)
  {
    short activateDelay = 700;
    
    if (System.currentTimeMillis() >= mouseStillTime + activateDelay)
    {
      int x1 = width / 2 - 150;
      int y1 = height / 6 - 7;
      
      if (y <= y1 + 98)
      {
        y1 += 105;
      }
      
      int x2 = x1 + 150 + 150;
      int y2 = y1 + 84 + 10;
      GuiButton btn = getSelectedButton(x, y);
      
      if ((btn instanceof GuiButtonShaderOption))
      {
        GuiButtonShaderOption btnSo = (GuiButtonShaderOption)btn;
        ShaderOption so = btnSo.getShaderOption();
        String[] lines = makeTooltipLines(so, x2 - x1);
        
        if (lines == null)
        {
          return;
        }
        
        drawGradientRect(x1, y1, x2, y2, -536870912, -536870912);
        
        for (int i = 0; i < lines.length; i++)
        {
          String line = lines[i];
          int col = 14540253;
          
          if (line.endsWith("!"))
          {
            col = 16719904;
          }
          
          fontRendererObj.drawStringWithShadow(line, x1 + 5, y1 + 5 + i * 11, col);
        }
      }
    }
  }
  
  private String[] makeTooltipLines(ShaderOption so, int width)
  {
    if ((so instanceof ShaderOptionProfile))
    {
      return null;
    }
    

    String name = so.getNameText();
    String desc = Config.normalize(so.getDescriptionText()).trim();
    String[] descs = splitDescription(desc);
    String id = null;
    
    if (!name.equals(so.getName()))
    {
      id = Lang.get("of.general.id") + ": " + so.getName();
    }
    
    String source = null;
    
    if (so.getPaths() != null)
    {
      source = Lang.get("of.general.from") + ": " + Config.arrayToString(so.getPaths());
    }
    
    String def = null;
    
    if (so.getValueDefault() != null)
    {
      String list = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
      def = Lang.getDefault() + ": " + list;
    }
    
    ArrayList list1 = new ArrayList();
    list1.add(name);
    list1.addAll(Arrays.asList(descs));
    
    if (id != null)
    {
      list1.add(id);
    }
    
    if (source != null)
    {
      list1.add(source);
    }
    
    if (def != null)
    {
      list1.add(def);
    }
    
    String[] lines = makeTooltipLines(width, list1);
    return lines;
  }
  

  private String[] splitDescription(String desc)
  {
    if (desc.length() <= 0)
    {
      return new String[0];
    }
    

    desc = StrUtils.removePrefix(desc, "//");
    String[] descs = desc.split("\\. ");
    
    for (int i = 0; i < descs.length; i++)
    {
      descs[i] = ("- " + descs[i].trim());
      descs[i] = StrUtils.removeSuffix(descs[i], ".");
    }
    
    return descs;
  }
  

  private String[] makeTooltipLines(int width, List<String> args)
  {
    FontRenderer fr = getMinecraftfontRendererObj;
    ArrayList list = new ArrayList();
    
    for (int lines = 0; lines < args.size(); lines++)
    {
      String arg = (String)args.get(lines);
      
      if ((arg != null) && (arg.length() > 0))
      {
        List parts = fr.listFormattedStringToWidth(arg, width);
        Iterator it = parts.iterator();
        
        while (it.hasNext())
        {
          String part = (String)it.next();
          list.add(part);
        }
      }
    }
    
    String[] var10 = (String[])list.toArray(new String[list.size()]);
    return var10;
  }
  
  private GuiButton getSelectedButton(int x, int y)
  {
    for (int i = 0; i < buttonList.size(); i++)
    {
      GuiButton btn = (GuiButton)buttonList.get(i);
      int btnWidth = GuiVideoSettings.getButtonWidth(btn);
      int btnHeight = GuiVideoSettings.getButtonHeight(btn);
      
      if ((x >= xPosition) && (y >= yPosition) && (x < xPosition + btnWidth) && (y < yPosition + btnHeight))
      {
        return btn;
      }
    }
    
    return null;
  }
}
