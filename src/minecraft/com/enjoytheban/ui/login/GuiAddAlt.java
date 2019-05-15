package com.enjoytheban.ui.login;

import com.enjoytheban.Client;
import com.enjoytheban.management.FileManager;
import com.enjoytheban.utils.Helper;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;



public class GuiAddAlt
  extends GuiScreen
{
  private final GuiAltManager manager;
  private GuiPasswordField password;
  
  private class AddAltThread
    extends Thread
  {
    private final String password;
    private final String username;
    
    public AddAltThread(String username, String password)
    {
      this.username = username;
      this.password = password;
      status = "§7Waiting...";
    }
    
    private final void checkAndAddAlt(String username, String password)
    {
      YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(
        Proxy.NO_PROXY, "");
      YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service
        .createUserAuthentication(Agent.MINECRAFT);
      auth.setUsername(username);
      auth.setPassword(password);
      try
      {
        auth.logIn();
        Client.instance.getAltManager();AltManager.getAlts()
          .add(new Alt(username, password));
        FileManager.saveAlts();
        status = ("§aAlt added. (" + username + ")");
      }
      catch (AuthenticationException e)
      {
        status = "§cAlt failed!";
        e.printStackTrace();
      }
    }
    
    public void run()
    {
      if (password.equals(""))
      {
        Client.instance.getAltManager();AltManager.getAlts().add(new Alt(username, ""));
        FileManager.saveAlts();
        status = ("§aAlt added. (" + username + " - offline name)");
        return;
      }
      status = "§eTrying alt...";
      checkAndAddAlt(username, password);
    }
  }
  
  private String status = "§eWaiting...";
  private GuiTextField username;
  private GuiTextField combined;
  
  public GuiAddAlt(GuiAltManager manager)
  {
    this.manager = manager;
  }
  
  protected void actionPerformed(GuiButton button)
  {
    switch (id) {
    case 0:  AddAltThread login;
      AddAltThread login;
      if (combined.getText().isEmpty()) {
        login = new AddAltThread(username.getText(), password.getText()); } else { AddAltThread login;
        if ((!combined.getText().isEmpty()) && (combined.getText().contains(":"))) {
          String u = combined.getText().split(":")[0];
          String p = combined.getText().split(":")[1];
          login = new AddAltThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
        }
        else {
          login = new AddAltThread(username.getText(), password.getText());
        } }
      login.start();
      break;
    case 1: 
      mc.displayGuiScreen(manager);
    }
  }
  
  public void drawScreen(int i, int j, float f)
  {
    drawDefaultBackground();
    

    mcfontRendererObj.drawCenteredString("Add Alt", width / 2, 20, 
      -1);
    username.drawTextBox();
    password.drawTextBox();
    combined.drawTextBox();
    if (username.getText().isEmpty()) {
      mcfontRendererObj.drawStringWithShadow("Username / E-Mail", width / 2 - 96, 
        66.0F, -7829368);
    }
    if (password.getText().isEmpty()) {
      mcfontRendererObj.drawStringWithShadow("Password", width / 2 - 96, 106.0F, 
        -7829368);
    }
    if (combined.getText().isEmpty()) {
      mcfontRendererObj.drawStringWithShadow("Email:Password", width / 2 - 96, 146.0F, -7829368);
    }
    mcfontRendererObj.drawCenteredString(status, width / 2, 30, -1);
    
    super.drawScreen(i, j, f);
  }
  
  public void initGui()
  {
    Keyboard.enableRepeatEvents(true);
    buttonList.clear();
    buttonList.add(new GuiButton(0, width / 2 - 100, 
      height / 4 + 92 + 12, "Login"));
    buttonList.add(new GuiButton(1, width / 2 - 100, 
      height / 4 + 116 + 12, "Back"));
    username = new GuiTextField(1, mc.fontRendererObj, width / 2 - 100, 60, 200, 
      20);
    password = new GuiPasswordField(mc.fontRendererObj, width / 2 - 100, 100, 
      200, 20);
    combined = new GuiTextField(eventButton, mc.fontRendererObj, width / 2 - 100, 140, 200, 20);
    combined.setMaxStringLength(200);
  }
  
  protected void keyTyped(char par1, int par2)
  {
    username.textboxKeyTyped(par1, par2);
    password.textboxKeyTyped(par1, par2);
    combined.textboxKeyTyped(par1, par2);
    if ((par1 == '\t') && ((username.isFocused()) || (combined.isFocused()) || (password.isFocused())))
    {
      username.setFocused(!username.isFocused());
      password.setFocused(!password.isFocused());
      combined.setFocused(!combined.isFocused());
    }
    if (par1 == '\r') {
      actionPerformed((GuiButton)buttonList.get(0));
    }
  }
  
  protected void mouseClicked(int par1, int par2, int par3)
  {
    try
    {
      super.mouseClicked(par1, par2, par3);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    username.mouseClicked(par1, par2, par3);
    password.mouseClicked(par1, par2, par3);
    combined.mouseClicked(par1, par2, par3);
  }
}
