package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class RealmsPlayerScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private static final String OP_ICON_LOCATION = "realms:textures/gui/realms/op_icon.png";
  
  private static final String USER_ICON_LOCATION = "realms:textures/gui/realms/user_icon.png";
  
  private static final String CROSS_ICON_LOCATION = "realms:textures/gui/realms/cross_icon.png";
  
  private String toolTip;
  
  private final RealmsConfigureWorldScreen lastScreen;
  
  private RealmsServer serverData;
  
  private InvitedSelectionList invitedSelectionList;
  
  private int column1_x;
  
  private int column_width;
  private int column2_x;
  private static final int BUTTON_BACK_ID = 0;
  private static final int BUTTON_INVITE_ID = 1;
  private static final int BUTTON_UNINVITE_ID = 2;
  private static final int BUTTON_ACTIVITY_ID = 3;
  private RealmsButton inviteButton;
  private RealmsButton activityButton;
  private int selectedInvitedIndex = -1;
  private String selectedInvited;
  private boolean stateChanged;
  
  public RealmsPlayerScreen(RealmsConfigureWorldScreen lastScreen, RealmsServer serverData)
  {
    this.lastScreen = lastScreen;
    this.serverData = serverData;
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    
    if (invitedSelectionList != null) {
      invitedSelectionList.mouseEvent();
    }
  }
  
  public void tick()
  {
    super.tick();
  }
  
  public void init()
  {
    column1_x = (width() / 2 - 160);
    column_width = 150;
    column2_x = (width() / 2 + 12);
    
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    buttonsAdd(this.inviteButton = newButton(1, column2_x, RealmsConstants.row(1), column_width + 10, 20, getLocalizedString("mco.configure.world.buttons.invite")));
    
    buttonsAdd(this.activityButton = newButton(3, column2_x, RealmsConstants.row(3), column_width + 10, 20, getLocalizedString("mco.configure.world.buttons.activity")));
    
    buttonsAdd(newButton(0, column2_x + column_width / 2 + 2, RealmsConstants.row(12), column_width / 2 + 10 - 2, 20, getLocalizedString("gui.back")));
    
    invitedSelectionList = new InvitedSelectionList();
    invitedSelectionList.setLeftPos(column1_x);
    
    inviteButton.active(false);
  }
  

  public void removed()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    switch (button.id()) {
    case 0: 
      backButtonClicked();
      break;
    case 1: 
      Realms.setScreen(new RealmsInviteScreen(lastScreen, this, serverData));
      break;
    case 3: 
      Realms.setScreen(new RealmsActivityScreen(this, serverData));
      break;
    }
    
  }
  

  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      backButtonClicked();
    }
  }
  
  private void backButtonClicked() {
    if (stateChanged) {
      Realms.setScreen(lastScreen.getNewScreen());
    } else {
      Realms.setScreen(lastScreen);
    }
  }
  
  private void op(int index) {
    RealmsClient client = RealmsClient.createRealmsClient();
    String selectedInvite = ((PlayerInfo)serverData.players.get(index)).getName();
    try
    {
      updateOps(client.op(serverData.id, selectedInvite));
    } catch (RealmsServiceException e) {
      LOGGER.error("Couldn't op the user");
    }
  }
  
  private void deop(int index) {
    RealmsClient client = RealmsClient.createRealmsClient();
    String selectedInvite = ((PlayerInfo)serverData.players.get(index)).getName();
    try
    {
      updateOps(client.deop(serverData.id, selectedInvite));
    } catch (RealmsServiceException e) {
      LOGGER.error("Couldn't deop the user");
    }
  }
  
  private void updateOps(com.mojang.realmsclient.dto.Ops ops) {
    for (PlayerInfo playerInfo : serverData.players) {
      playerInfo.setOperator(ops.contains(playerInfo.getName()));
    }
  }
  
  private void uninvite(int index) {
    if ((index >= 0) && (index < serverData.players.size())) {
      PlayerInfo playerInfo = (PlayerInfo)serverData.players.get(index);
      selectedInvited = playerInfo.getUuid();
      selectedInvitedIndex = index;
      RealmsConfirmScreen confirmScreen = new RealmsConfirmScreen(this, "Question", getLocalizedString("mco.configure.world.uninvite.question") + " '" + playerInfo.getName() + "' ?", 2);
      Realms.setScreen(confirmScreen);
    }
  }
  
  public void confirmResult(boolean result, int id)
  {
    if (id == 2) {
      if (result) {
        RealmsClient client = RealmsClient.createRealmsClient();
        try {
          client.uninvite(serverData.id, selectedInvited);
        } catch (RealmsServiceException e) {
          LOGGER.error("Couldn't uninvite user");
        }
        deleteFromInvitedList(selectedInvitedIndex);
      }
      stateChanged = true;
      Realms.setScreen(this);
    }
  }
  
  private void deleteFromInvitedList(int selectedInvitedIndex) {
    serverData.players.remove(selectedInvitedIndex);
  }
  
  public void render(int xm, int ym, float a)
  {
    toolTip = null;
    renderBackground();
    
    if (invitedSelectionList != null) {
      invitedSelectionList.render(xm, ym, a);
    }
    
    int bottom_border = RealmsConstants.row(12) + 20;
    
    GL11.glDisable(2896);
    GL11.glDisable(2912);
    Tezzelator t = Tezzelator.instance;
    bind("textures/gui/options_background.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    float s = 32.0F;
    t.begin(7, net.minecraft.realms.RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
    t.vertex(0.0D, height(), 0.0D).tex(0.0D, (height() - bottom_border) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
    t.vertex(width(), height(), 0.0D).tex(width() / 32.0F, (height() - bottom_border) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
    t.vertex(width(), bottom_border, 0.0D).tex(width() / 32.0F, 0.0D).color(64, 64, 64, 255).endVertex();
    t.vertex(0.0D, bottom_border, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
    t.end();
    
    drawCenteredString(getLocalizedString("mco.configure.world.players.title"), width() / 2, 17, 16777215);
    
    if ((serverData != null) && (serverData.players != null)) {
      drawString(getLocalizedString("mco.configure.world.invited") + " (" + serverData.players.size() + ")", column1_x, RealmsConstants.row(0), 10526880);
      inviteButton.active(serverData.players.size() < 200);
    } else {
      drawString(getLocalizedString("mco.configure.world.invited"), column1_x, RealmsConstants.row(0), 10526880);
      inviteButton.active(false);
    }
    
    super.render(xm, ym, a);
    
    if (serverData == null) {
      return;
    }
    
    if (toolTip != null) {
      renderMousehoverTooltip(toolTip, xm, ym);
    }
  }
  
  protected void renderMousehoverTooltip(String msg, int x, int y) {
    if (msg == null) {
      return;
    }
    
    int rx = x + 12;
    int ry = y - 12;
    int width = fontWidth(msg);
    fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, -1073741824, -1073741824);
    
    fontDrawShadow(msg, rx, ry, 16777215);
  }
  
  private class InvitedSelectionList extends net.minecraft.realms.RealmsClickableScrolledSelectionList
  {
    public InvitedSelectionList() {
      super(RealmsConstants.row(12) + 20, RealmsConstants.row(1), RealmsConstants.row(12) + 20, 13);
    }
    
    public void customMouseEvent(int y0, int y1, int headerHeight, float yo, int itemHeight)
    {
      if ((org.lwjgl.input.Mouse.isButtonDown(0)) && 
        (ym() >= y0) && (ym() <= y1)) {
        int x0 = column1_x;
        int x1 = column1_x + column_width;
        
        int clickSlotPos = ym() - y0 - headerHeight + (int)yo - 4;
        int slot = clickSlotPos / itemHeight;
        if ((xm() >= x0) && (xm() <= x1) && (slot >= 0) && (clickSlotPos >= 0) && (slot < getItemCount())) {
          itemClicked(clickSlotPos, slot, xm(), ym(), width());
        }
      }
    }
    

    public void itemClicked(int clickSlotPos, int slot, int xm, int ym, int width)
    {
      if ((slot < 0) || (slot > serverData.players.size()) || (toolTip == null)) {
        return;
      }
      
      if ((toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip"))) || (toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip")))) {
        if (((PlayerInfo)serverData.players.get(slot)).isOperator()) {
          RealmsPlayerScreen.this.deop(slot);
        } else {
          RealmsPlayerScreen.this.op(slot);
        }
      } else if (toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
        RealmsPlayerScreen.this.uninvite(slot);
      }
    }
    
    public void renderBackground()
    {
      RealmsPlayerScreen.this.renderBackground();
    }
    

    public int getScrollbarPosition()
    {
      return column1_x + width() - 5;
    }
    
    public int getItemCount()
    {
      return serverData == null ? 1 : serverData.players.size();
    }
    
    public int getMaxPosition()
    {
      return getItemCount() * 13;
    }
    
    protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY)
    {
      if (serverData == null) {
        return;
      }
      
      if (i < serverData.players.size()) {
        renderInvitedItem(i, x, y, h);
      }
    }
    
    private void renderInvitedItem(int i, int x, int y, int h) {
      PlayerInfo invited = (PlayerInfo)serverData.players.get(i);
      
      drawString(invited.getName(), column1_x + 3 + 12, y + 1, invited.getAccepted() ? 16777215 : 10526880);
      
      if (invited.isOperator()) {
        RealmsPlayerScreen.this.drawOpped(column1_x + column_width - 10, y + 1, xm(), ym());
      } else {
        RealmsPlayerScreen.this.drawNormal(column1_x + column_width - 10, y + 1, xm(), ym());
      }
      
      RealmsPlayerScreen.this.drawRemoveIcon(column1_x + column_width - 22, y + 2, xm(), ym());
      


      RealmsScreen.bindFace(invited.getUuid(), invited.getName());
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.blit(column1_x + 2 + 2, y + 1, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
      RealmsScreen.blit(column1_x + 2 + 2, y + 1, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
    }
  }
  
  private void drawRemoveIcon(int x, int y, int xm, int ym) {
    boolean hovered = (xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 25) && (ym > RealmsConstants.row(1));
    
    bind("realms:textures/gui/realms/cross_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    RealmsScreen.blit(x, y, 0.0F, hovered ? 7.0F : 0.0F, 8, 7, 8.0F, 14.0F);
    GL11.glPopMatrix();
    
    if (hovered) {
      toolTip = getLocalizedString("mco.configure.world.invites.remove.tooltip");
    }
  }
  
  private void drawOpped(int x, int y, int xm, int ym) {
    boolean hovered = (xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 25) && (ym > RealmsConstants.row(1));
    
    bind("realms:textures/gui/realms/op_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    RealmsScreen.blit(x, y, 0.0F, hovered ? 8.0F : 0.0F, 8, 8, 8.0F, 16.0F);
    GL11.glPopMatrix();
    
    if (hovered) {
      toolTip = getLocalizedString("mco.configure.world.invites.ops.tooltip");
    }
  }
  
  private void drawNormal(int x, int y, int xm, int ym) {
    boolean hovered = (xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9);
    
    bind("realms:textures/gui/realms/user_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    RealmsScreen.blit(x, y, 0.0F, hovered ? 8.0F : 0.0F, 8, 8, 8.0F, 16.0F);
    GL11.glPopMatrix();
    
    if (hovered) {
      toolTip = getLocalizedString("mco.configure.world.invites.normal.tooltip");
    }
  }
}
