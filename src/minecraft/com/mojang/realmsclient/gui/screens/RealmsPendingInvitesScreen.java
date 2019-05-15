package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Date;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsBufferBuilder;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RealmsPendingInvitesScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private static final int BUTTON_BACK_ID = 0;
  
  private static final String ACCEPT_ICON_LOCATION = "realms:textures/gui/realms/accept_icon.png";
  
  private static final String REJECT_ICON_LOCATION = "realms:textures/gui/realms/reject_icon.png";
  
  private final RealmsScreen lastScreen;
  private String toolTip = null;
  
  private boolean loaded = false;
  
  private PendingInvitationList pendingList;
  private List<PendingInvite> pendingInvites = com.google.common.collect.Lists.newArrayList();
  
  public RealmsPendingInvitesScreen(RealmsScreen lastScreen)
  {
    this.lastScreen = lastScreen;
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    pendingList.mouseEvent();
  }
  
  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    pendingList = new PendingInvitationList();
    
    new Thread("Realms-pending-invitations-fetcher")
    {
      public void run() {
        RealmsClient client = RealmsClient.createRealmsClient();
        try {
          pendingInvites = pendingInvitespendingInvites;
        } catch (RealmsServiceException e) {
          RealmsPendingInvitesScreen.LOGGER.error("Couldn't list invites");
        } finally {
          loaded = true;
        }
        
      }
    }.start();
    buttonsAdd(newButton(0, width() / 2 - 75, height() - 32, 153, 20, getLocalizedString("gui.done")));
  }
  

  public void tick()
  {
    super.tick();
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    switch (button.id()) {
    case 0: 
      Realms.setScreen(new RealmsMainScreen(lastScreen));
    }
    
  }
  

  public void keyPressed(char eventCharacter, int eventKey)
  {
    if (eventKey == 1) {
      Realms.setScreen(new RealmsMainScreen(lastScreen));
    }
  }
  
  private void updateList(int slot) {
    pendingInvites.remove(slot);
  }
  
  private void reject(final int slot) {
    if (slot < pendingInvites.size()) {
      new Thread("Realms-reject-invitation")
      {
        public void run() {
          try {
            RealmsClient client = RealmsClient.createRealmsClient();
            client.rejectInvitation(pendingInvites.get(slot)).invitationId);
            RealmsPendingInvitesScreen.this.updateList(slot);
          } catch (RealmsServiceException e) {
            RealmsPendingInvitesScreen.LOGGER.error("Couldn't reject invite");
          }
        }
      }.start();
    }
  }
  
  private void accept(final int slot) {
    if (slot < pendingInvites.size()) {
      new Thread("Realms-accept-invitation")
      {
        public void run() {
          try {
            RealmsClient client = RealmsClient.createRealmsClient();
            client.acceptInvitation(pendingInvites.get(slot)).invitationId);
            RealmsPendingInvitesScreen.this.updateList(slot);
          } catch (RealmsServiceException e) {
            RealmsPendingInvitesScreen.LOGGER.error("Couldn't accept invite");
          }
        }
      }.start();
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    toolTip = null;
    
    renderBackground();
    
    pendingList.render(xm, ym, a);
    drawCenteredString(getLocalizedString("mco.invites.title"), width() / 2, 12, 16777215);
    
    if (toolTip != null) {
      renderMousehoverTooltip(toolTip, xm, ym);
    }
    
    if ((pendingInvites.size() == 0) && (loaded)) {
      drawCenteredString(getLocalizedString("mco.invites.nopending"), width() / 2, height() / 2 - 20, 16777215);
    }
    
    super.render(xm, ym, a);
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
  
  private class PendingInvitationList extends RealmsClickableScrolledSelectionList
  {
    public PendingInvitationList() {
      super(height(), 32, height() - 40, 36);
    }
    
    public int getItemCount()
    {
      return pendingInvites.size();
    }
    
    public int getMaxPosition()
    {
      return getItemCount() * 36;
    }
    
    public void renderBackground()
    {
      RealmsPendingInvitesScreen.this.renderBackground();
    }
    
    public void renderSelected(int width, int y, int h, Tezzelator t)
    {
      int x0 = getScrollbarPosition() - 290;
      int x1 = getScrollbarPosition() - 10;
      
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3553);
      t.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
      t.vertex(x0, y + h + 2, 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(x1, y + h + 2, 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(x1, y - 2, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(x0, y - 2, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      
      t.vertex(x0 + 1, y + h + 1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      t.vertex(x1 - 1, y + h + 1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      t.vertex(x1 - 1, y - 1, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      t.vertex(x0 + 1, y - 1, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      
      t.end();
      GL11.glEnable(3553);
    }
    
    public void renderItem(int i, int x, int y, int h, int mouseX, int mouseY)
    {
      if (i < pendingInvites.size()) {
        renderPendingInvitationItem(i, x, y, h);
      }
    }
    
    private void renderPendingInvitationItem(int i, int x, int y, int h) {
      PendingInvite invite = (PendingInvite)pendingInvites.get(i);
      
      drawString(worldName, x + 2, y + 1, 16777215);
      drawString(worldOwnerName, x + 2, y + 12, 7105644);
      drawString(RealmsUtil.convertToAgePresentation(Long.valueOf(System.currentTimeMillis() - date.getTime())), x + 2, y + 24, 7105644);
      
      int dx = getScrollbarPosition() - 50;
      
      drawAccept(dx, y, xm(), ym());
      drawReject(dx + 20, y, xm(), ym());
      
      RealmsScreen.bindFace(worldOwnerUuid, worldOwnerName);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.blit(x - 36, y, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
      RealmsScreen.blit(x - 36, y, 40.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
    }
    
    private void drawAccept(int x, int y, int xm, int ym) {
      boolean hovered = false;
      
      if ((xm >= x) && (xm <= x + 15) && (ym >= y) && (ym <= y + 15) && (ym < height() - 40) && (ym > 32)) {
        hovered = true;
      }
      
      RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(x, y, hovered ? 19.0F : 0.0F, 0.0F, 18, 18, 37.0F, 18.0F);
      
      GL11.glPopMatrix();
      
      if (hovered) {
        toolTip = RealmsScreen.getLocalizedString("mco.invites.button.accept");
      }
    }
    
    private void drawReject(int x, int y, int xm, int ym) {
      boolean hovered = false;
      
      if ((xm >= x) && (xm <= x + 15) && (ym >= y) && (ym <= y + 15) && (ym < height() - 40) && (ym > 32)) {
        hovered = true;
      }
      
      RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(x, y, hovered ? 19.0F : 0.0F, 0.0F, 18, 18, 37.0F, 18.0F);
      GL11.glPopMatrix();
      
      if (hovered) {
        toolTip = RealmsScreen.getLocalizedString("mco.invites.button.reject");
      }
    }
    
    public void itemClicked(int clickSlotPos, int slot, int xm, int ym, int width)
    {
      int x = getScrollbarPosition() - 50;
      int y = clickSlotPos + 30 - getScroll();
      
      if ((xm >= x) && (xm <= x + 15) && (ym >= y) && (ym <= y + 15)) {
        RealmsPendingInvitesScreen.this.accept(slot);
      } else if ((xm >= x + 20) && (xm <= x + 20 + 15) && (ym >= y) && (ym <= y + 15)) {
        RealmsPendingInvitesScreen.this.reject(slot);
      }
    }
    
    public void customMouseEvent(int y0, int y1, int headerHeight, float yo, int itemHeight)
    {
      if ((Mouse.isButtonDown(0)) && 
        (ym() >= y0) && (ym() <= y1)) {
        int x0 = width() / 2 - 92;
        int x1 = width();
        
        int clickSlotPos = ym() - y0 - headerHeight + (int)yo - 4;
        int slot = clickSlotPos / itemHeight;
        if ((xm() >= x0) && (xm() <= x1) && (slot >= 0) && (clickSlotPos >= 0) && (slot < getItemCount())) {
          itemClicked(clickSlotPos, slot, xm(), ym(), width());
        }
      }
    }
  }
}
