package com.enjoytheban.ui.login;

import com.enjoytheban.utils.Helper;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiAltLogin extends GuiScreen
{
  private GuiPasswordField password;
  private final GuiScreen previousScreen;
  private AltLoginThread thread;
  private GuiTextField username;
  private GuiTextField combined;
  
  public GuiAltLogin(GuiScreen previousScreen)
  {
    this.previousScreen = previousScreen;
  }
  
  protected void actionPerformed(GuiButton button) {
    switch (id) {
    case 1: 
      mc.displayGuiScreen(previousScreen);
      break;
    case 0: 
      if (combined.getText().isEmpty()) {
        thread = new AltLoginThread(username.getText(), password.getText());
      } else if ((!combined.getText().isEmpty()) && (combined.getText().contains(":"))) {
        String u = combined.getText().split(":")[0];
        String p = combined.getText().split(":")[1];
        thread = new AltLoginThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
      } else {
        thread = new AltLoginThread(username.getText(), password.getText()); }
      thread.start();
    }
  }
  
  public void drawScreen(int x, int y, float z) {
    drawDefaultBackground();
    username.drawTextBox();
    password.drawTextBox();
    combined.drawTextBox();
    mcfontRendererObj.drawCenteredString("Alt Login", width / 2, 20, 
      -1);
    mcfontRendererObj.drawCenteredString(thread == null ? "§eWaiting..." : 
      thread.getStatus(), width / 2, 29, -1);
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
    super.drawScreen(x, y, z);
  }
  
  public void initGui() {
    int var3 = height / 4 + 24;
    buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
    buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
    username = new GuiTextField(1, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
    password = new GuiPasswordField(mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
    combined = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 140, 200, 20);
    username.setFocused(true);
    username.setMaxStringLength(200);
    password.func_146203_f(200);
    combined.setMaxStringLength(200);
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
  }
  
  protected void keyTyped(char character, int key)
  {
    try
    {
      super.keyTyped(character, key);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    if ((character == '\t') && ((username.isFocused()) || (combined.isFocused()) || (password.isFocused())))
    {
      username.setFocused(!username.isFocused());
      password.setFocused(!password.isFocused());
      combined.setFocused(!combined.isFocused());
    }
    if (character == '\r') {
      actionPerformed((GuiButton)buttonList.get(0));
    }
    username.textboxKeyTyped(character, key);
    password.textboxKeyTyped(character, key);
    combined.textboxKeyTyped(character, key);
  }
  
  protected void mouseClicked(int x, int y, int button) {
    try {
      super.mouseClicked(x, y, button);
    } catch (IOException e) {
      e.printStackTrace();
    }
    username.mouseClicked(x, y, button);
    password.mouseClicked(x, y, button);
    combined.mouseClicked(x, y, button);
  }
  
  public void onGuiClosed() {
    org.lwjgl.input.Keyboard.enableRepeatEvents(false);
  }
  
  public void updateScreen() {
    username.updateCursorCounter();
    password.updateCursorCounter();
    combined.updateCursorCounter();
  }
}
