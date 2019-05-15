package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiCommandBlock extends GuiScreen
{
  private static final Logger field_146488_a = ;
  
  private GuiTextField commandTextField;
  
  private GuiTextField field_146486_g;
  
  private final CommandBlockLogic localCommandBlock;
  
  private GuiButton doneBtn;
  
  private GuiButton cancelBtn;
  
  private GuiButton field_175390_s;
  private boolean field_175389_t;
  private static final String __OBFID = "CL_00000748";
  
  public GuiCommandBlock(CommandBlockLogic p_i45032_1_)
  {
    localCommandBlock = p_i45032_1_;
  }
  



  public void updateScreen()
  {
    commandTextField.updateCursorCounter();
  }
  



  public void initGui()
  {
    Keyboard.enableRepeatEvents(true);
    buttonList.clear();
    buttonList.add(this.doneBtn = new GuiButton(0, width / 2 - 4 - 150, height / 4 + 120 + 12, 150, 20, I18n.format("gui.done", new Object[0])));
    buttonList.add(this.cancelBtn = new GuiButton(1, width / 2 + 4, height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel", new Object[0])));
    buttonList.add(this.field_175390_s = new GuiButton(4, width / 2 + 150 - 20, 150, 20, 20, "O"));
    commandTextField = new GuiTextField(2, fontRendererObj, width / 2 - 150, 50, 300, 20);
    commandTextField.setMaxStringLength(32767);
    commandTextField.setFocused(true);
    commandTextField.setText(localCommandBlock.getCustomName());
    field_146486_g = new GuiTextField(3, fontRendererObj, width / 2 - 150, 150, 276, 20);
    field_146486_g.setMaxStringLength(32767);
    field_146486_g.setEnabled(false);
    field_146486_g.setText("-");
    field_175389_t = localCommandBlock.func_175571_m();
    func_175388_a();
    doneBtn.enabled = (commandTextField.getText().trim().length() > 0);
  }
  



  public void onGuiClosed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  protected void actionPerformed(GuiButton button) throws IOException
  {
    if (enabled)
    {
      if (id == 1)
      {
        localCommandBlock.func_175573_a(field_175389_t);
        mc.displayGuiScreen(null);
      }
      else if (id == 0)
      {
        PacketBuffer var2 = new PacketBuffer(io.netty.buffer.Unpooled.buffer());
        var2.writeByte(localCommandBlock.func_145751_f());
        localCommandBlock.func_145757_a(var2);
        var2.writeString(commandTextField.getText());
        var2.writeBoolean(localCommandBlock.func_175571_m());
        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", var2));
        
        if (!localCommandBlock.func_175571_m())
        {
          localCommandBlock.func_145750_b(null);
        }
        
        mc.displayGuiScreen(null);
      }
      else if (id == 4)
      {
        localCommandBlock.func_175573_a(!localCommandBlock.func_175571_m());
        func_175388_a();
      }
    }
  }
  



  protected void keyTyped(char typedChar, int keyCode)
    throws IOException
  {
    commandTextField.textboxKeyTyped(typedChar, keyCode);
    field_146486_g.textboxKeyTyped(typedChar, keyCode);
    doneBtn.enabled = (commandTextField.getText().trim().length() > 0);
    
    if ((keyCode != 28) && (keyCode != 156))
    {
      if (keyCode == 1)
      {
        actionPerformed(cancelBtn);
      }
      
    }
    else {
      actionPerformed(doneBtn);
    }
  }
  


  protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    throws IOException
  {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    commandTextField.mouseClicked(mouseX, mouseY, mouseButton);
    field_146486_g.mouseClicked(mouseX, mouseY, mouseButton);
  }
  



  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    drawDefaultBackground();
    drawCenteredString(fontRendererObj, I18n.format("advMode.setCommand", new Object[0]), width / 2, 20, 16777215);
    drawString(fontRendererObj, I18n.format("advMode.command", new Object[0]), width / 2 - 150, 37, 10526880);
    commandTextField.drawTextBox();
    byte var4 = 75;
    byte var5 = 0;
    FontRenderer var10001 = fontRendererObj;
    String var10002 = I18n.format("advMode.nearestPlayer", new Object[0]);
    int var10003 = width / 2 - 150;
    int var7 = var5 + 1;
    drawString(var10001, var10002, var10003, var4 + var5 * fontRendererObj.FONT_HEIGHT, 10526880);
    drawString(fontRendererObj, I18n.format("advMode.randomPlayer", new Object[0]), width / 2 - 150, var4 + var7++ * fontRendererObj.FONT_HEIGHT, 10526880);
    drawString(fontRendererObj, I18n.format("advMode.allPlayers", new Object[0]), width / 2 - 150, var4 + var7++ * fontRendererObj.FONT_HEIGHT, 10526880);
    drawString(fontRendererObj, I18n.format("advMode.allEntities", new Object[0]), width / 2 - 150, var4 + var7++ * fontRendererObj.FONT_HEIGHT, 10526880);
    drawString(fontRendererObj, "", width / 2 - 150, var4 + var7++ * fontRendererObj.FONT_HEIGHT, 10526880);
    
    if (field_146486_g.getText().length() > 0)
    {
      int var6 = var4 + var7 * fontRendererObj.FONT_HEIGHT + 16;
      drawString(fontRendererObj, I18n.format("advMode.previousOutput", new Object[0]), width / 2 - 150, var6, 10526880);
      field_146486_g.drawTextBox();
    }
    
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  private void func_175388_a()
  {
    if (localCommandBlock.func_175571_m())
    {
      field_175390_s.displayString = "O";
      
      if (localCommandBlock.getLastOutput() != null)
      {
        field_146486_g.setText(localCommandBlock.getLastOutput().getUnformattedText());
      }
    }
    else
    {
      field_175390_s.displayString = "X";
      field_146486_g.setText("-");
    }
  }
}
