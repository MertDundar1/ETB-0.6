package com.enjoytheban.utils.proxy;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;


public class GuiProxy
  extends GuiScreen
{
  private GuiMultiplayer prevMenu;
  private GuiTextField proxyBox;
  private String error = "";
  
  public static boolean connected = false;
  
  public GuiProxy(GuiMultiplayer guiMultiplayer) {
    prevMenu = guiMultiplayer;
  }
  
  public void updateScreen()
  {
    proxyBox.updateCursorCounter();
  }
  
  public void initGui()
  {
    Keyboard.enableRepeatEvents(true);
    buttonList.clear();
    buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, "Back"));
    buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 72 + 12, "Connect"));
    buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 + 12, "Disconnect"));
    proxyBox = new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
    proxyBox.setFocused(true);
  }
  
  public void onGuiClosed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  protected void actionPerformed(GuiButton clickedButton)
  {
    if (enabled) {
      if (id == 0) {
        mc.displayGuiScreen(prevMenu);
      } else if (id == 1) {
        if ((!proxyBox.getText().contains(":")) || (proxyBox.getText().split(":").length != 2)) {
          error = "Not a proxy!";
          return;
        }
        String[] parts = proxyBox.getText().split(":");
        
        if ((isInteger(parts[1])) && (Integer.parseInt(parts[1]) <= 65536)) Integer.parseInt(parts[1]);
        try
        {
          ProxyConfig.proxyAddr = new ConnectionInfo();
          proxyAddrip = parts[0];
          proxyAddrport = Integer.parseInt(parts[1]);
          connected = true;
        } catch (Exception e) {
          error = e.toString();
          return;
        }
        if (error.isEmpty()) {}


      }
      else if (id == 2) {
        ProxyConfig.stop();
        connected = false;
      }
    }
  }
  
  protected void keyTyped(char par1, int par2)
  {
    proxyBox.textboxKeyTyped(par1, par2);
    
    if ((par2 == 28) || (par2 == 156)) {
      actionPerformed((GuiButton)buttonList.get(1));
    }
  }
  
  protected void mouseClicked(int par1, int par2, int par3) throws IOException {
    super.mouseClicked(par1, par2, par3);
    proxyBox.mouseClicked(par1, par2, par3);
    if (proxyBox.isFocused()) {
      error = "";
    }
  }
  
  public void drawScreen(int par1, int par2, float par3) {
    drawDefaultBackground();
    mc.fontRendererObj.drawCenteredString("Proxies Reloaded", width / 2, 20, 16777215);
    mc.fontRendererObj.drawCenteredString("(SOCKS5 Proxies only)", width / 2, 30, 16777215);
    mc.fontRendererObj.drawStringWithShadow("IP:Port", width / 2 - 100, 47.0F, 10526880);
    mc.fontRendererObj.drawCenteredString(error, width / 2, 95, 16711680);
    String currentProxy = "";
    if (connected) {
      currentProxy = "§a" + proxyAddrip + ":" + proxyAddrport;
    }
    if (!connected) {
      currentProxy = "§cN/A";
    }
    mc.fontRendererObj.drawStringWithShadow("Current proxy: " + currentProxy, 1.0F, 3.0F, -1);
    proxyBox.drawTextBox();
    super.drawScreen(par1, par2, par3);
  }
  
  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}
