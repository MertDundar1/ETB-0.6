package com.enjoytheban.ui.login;

import com.enjoytheban.Client;
import com.enjoytheban.management.FileManager;
import com.enjoytheban.utils.Helper;
import com.enjoytheban.utils.render.RenderUtil;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAltManager extends GuiScreen
{
  private static Minecraft mc = ;
  private GuiButton login;
  private GuiButton remove;
  private GuiButton rename;
  private AltLoginThread loginThread;
  private int offset;
  public Alt selectedAlt = null;
  private String status = "§eWaiting...";
  


  public GuiAltManager()
  {
    FileManager.saveAlts();
  }
  
  public void actionPerformed(GuiButton button)
  {
    switch (id)
    {
    case 0: 
      if (loginThread == null)
      {
        mc.displayGuiScreen(null);


      }
      else if ((!loginThread.getStatus().equals("Logging in...")) && 
        (!loginThread.getStatus().equals("Do not hit back! Logging in...")))
      {
        mc.displayGuiScreen(null);
      }
      else
      {
        loginThread.setStatus("Do not hit back! Logging in...");
      }
      break;
    case 1: 
      String user = selectedAlt.getUsername();
      String pass = selectedAlt.getPassword();
      loginThread = new AltLoginThread(user, pass);
      loginThread.start();
      break;
    case 2: 
      if (loginThread != null) {
        loginThread = null;
      }
      Client.instance.getAltManager();AltManager.getAlts().remove(selectedAlt);
      status = "§cRemoved.";
      selectedAlt = null;
      FileManager.saveAlts();
      break;
    case 3: 
      mc.displayGuiScreen(new GuiAddAlt(this));
      break;
    case 4: 
      mc.displayGuiScreen(new GuiAltLogin(this));
      break;
    case 5: 
      Client.instance.getAltManager();
      Client.instance.getAltManager();Alt randomAlt = (Alt)AltManager.alts.get(new Random().nextInt(AltManager.alts.size()));
      String user1 = randomAlt.getUsername();
      String pass1 = randomAlt.getPassword();
      loginThread = new AltLoginThread(user1, pass1);
      loginThread.start();
      break;
    case 6: 
      mc.displayGuiScreen(new GuiRenameAlt(this));
      break;
    case 7: 
      Client.instance.getAltManager();Alt lastAlt = AltManager.lastAlt;
      if (lastAlt == null)
      {
        if (loginThread == null) {
          status = "?cThere is no last used alt!";
        } else {
          loginThread.setStatus("?cThere is no last used alt!");
        }
      }
      else
      {
        String user2 = lastAlt.getUsername();
        String pass2 = lastAlt.getPassword();
        loginThread = new AltLoginThread(user2, pass2);
        loginThread.start();
      }
      break;
    }
    
  }
  
  public void drawScreen(int par1, int par2, float par3)
  {
    drawDefaultBackground();
    if (Mouse.hasWheel())
    {
      int wheel = Mouse.getDWheel();
      if (wheel < 0)
      {
        offset += 26;
        if (offset < 0) {
          offset = 0;
        }
      }
      else if (wheel > 0)
      {
        offset -= 26;
        if (offset < 0) {
          offset = 0;
        }
      }
    }
    drawDefaultBackground();
    mcfontRendererObj.drawStringWithShadow(mcsession.getUsername(), 10.0F, 10.0F, 
      -7829368);
    
    Client.instance.getAltManager();mcfontRendererObj.drawCenteredString("Account Manager - " + AltManager.getAlts().size() + " alts", 
      width / 2, 10, -1);
    mcfontRendererObj.drawCenteredString(loginThread == null ? status : 
      loginThread.getStatus(), width / 2, 20, -1);
    
    GL11.glPushMatrix();
    prepareScissorBox(0.0F, 33.0F, width, height - 50);
    GL11.glEnable(3089);
    int y = 38;
    Client.instance.getAltManager(); for (Alt alt : AltManager.getAlts()) {
      if (isAltInArea(y)) {
        String name;
        String name;
        if (alt.getMask().equals("")) {
          name = alt.getUsername();
        } else {
          name = alt.getMask();
        }
        String pass;
        String pass;
        if (alt.getPassword().equals("")) {
          pass = "§cCracked";
        } else {
          pass = alt.getPassword().replaceAll(".", "*");
        }
        
        if (alt == selectedAlt)
        {
          if ((isMouseOverAlt(par1, par2, y - offset)) && 
            (Mouse.isButtonDown(0))) {
            RenderUtil.drawBorderedRect(52.0F, y - offset - 4, 
              width - 52, y - offset + 20, 1.0F, -16777216, 
              -2142943931);
          } else if (isMouseOverAlt(par1, par2, y - offset)) {
            RenderUtil.drawBorderedRect(52.0F, y - offset - 4, 
              width - 52, y - offset + 20, 1.0F, -16777216, 
              -2142088622);
          } else {
            RenderUtil.drawBorderedRect(52.0F, y - offset - 4, 
              width - 52, y - offset + 20, 1.0F, -16777216, 
              -2144259791);
          }
        }
        else if ((isMouseOverAlt(par1, par2, y - offset)) && 
          (Mouse.isButtonDown(0))) {
          RenderUtil.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - 
            offset + 20, 1.0F, -16777216, -2146101995);
        } else if (isMouseOverAlt(par1, par2, y - offset)) {
          RenderUtil.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - 
            offset + 20, 1.0F, -16777216, -2145180893);
        }
        mcfontRendererObj.drawCenteredString(name, width / 2, y - offset, 
          -1);
        mcfontRendererObj.drawCenteredString(pass, width / 2, y - offset + 
          10, 5592405);
        y += 26;
      }
    }
    GL11.glDisable(3089);
    GL11.glPopMatrix();
    super.drawScreen(par1, par2, par3);
    if (selectedAlt == null)
    {
      login.enabled = false;
      remove.enabled = false;
      rename.enabled = false;
    }
    else
    {
      login.enabled = true;
      remove.enabled = true;
      rename.enabled = true;
    }
    if (Keyboard.isKeyDown(200))
    {
      offset -= 26;
      if (offset < 0) {
        offset = 0;
      }
    }
    else if (Keyboard.isKeyDown(208))
    {
      offset += 26;
      if (offset < 0) {
        offset = 0;
      }
    }
  }
  
  public void initGui()
  {
    buttonList.add(new GuiButton(0, width / 2 + 4 + 76, 
      height - 24, 75, 20, "Cancel"));
    buttonList.add(this.login = new GuiButton(1, width / 2 - 154, 
      height - 48, 70, 20, "Login"));
    buttonList.add(this.remove = new GuiButton(2, width / 2 - 74, 
      height - 24, 70, 20, "Remove"));
    buttonList.add(new GuiButton(3, width / 2 + 4 + 76, 
      height - 48, 75, 20, "Add"));
    buttonList.add(new GuiButton(4, width / 2 - 74, 
      height - 48, 70, 20, "Direct Login"));
    buttonList.add(new GuiButton(5, width / 2 + 4, 
      height - 48, 70, 20, "Random"));
    buttonList.add(this.rename = new GuiButton(6, width / 2 + 4, 
      height - 24, 70, 20, "Edit"));
    buttonList.add(this.rename = new GuiButton(7, width / 2 - 154, 
      height - 24, 70, 20, "Last Alt"));
    

    login.enabled = false;
    remove.enabled = false;
    rename.enabled = false;
  }
  
  private boolean isAltInArea(int y)
  {
    return y - offset <= height - 50;
  }
  
  private boolean isMouseOverAlt(int x, int y, int y1)
  {
    return (x >= 52) && (y >= y1 - 4) && (x <= width - 52) && (y <= y1 + 20) && 
      (x >= 0) && (y >= 33) && (x <= width) && (y <= height - 50);
  }
  
  protected void mouseClicked(int par1, int par2, int par3)
  {
    if (offset < 0) {
      offset = 0;
    }
    int y = 38 - offset;
    Client.instance.getAltManager(); for (Alt alt : AltManager.getAlts())
    {
      if (isMouseOverAlt(par1, par2, y))
      {
        if (alt == selectedAlt)
        {
          actionPerformed((GuiButton)buttonList.get(1));
          return;
        }
        selectedAlt = alt;
      }
      y += 26;
    }
    try
    {
      super.mouseClicked(par1, par2, par3);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public void prepareScissorBox(float x, float y, float x2, float y2)
  {
    int factor = new ScaledResolution(mc).getScaleFactor();
    GL11.glScissor((int)(x * factor), 
      (int)((new ScaledResolution(mc).getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), 
      (int)((y2 - y) * factor));
  }
  
  public void renderBackground(int par1, int par2)
  {
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(3008);
    drawDefaultBackground();
    Tessellator var3 = Tessellator.instance;
    var3.getWorldRenderer().startDrawingQuads();
    var3.getWorldRenderer().addVertexWithUV(0.0D, par2, -90.0D, 0.0D, 1.0D);
    var3.getWorldRenderer().addVertexWithUV(par1, par2, -90.0D, 1.0D, 1.0D);
    var3.getWorldRenderer().addVertexWithUV(par1, 0.0D, -90.0D, 1.0D, 0.0D);
    var3.getWorldRenderer().addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
    var3.draw();
    GL11.glDepthMask(true);
    GL11.glEnable(2929);
    GL11.glEnable(3008);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }
}
