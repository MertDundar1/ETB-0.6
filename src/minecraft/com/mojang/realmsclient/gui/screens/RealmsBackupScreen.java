package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.RealmsOptions;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class RealmsBackupScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private static final String PLUS_ICON_LOCATION = "realms:textures/gui/realms/plus_icon.png";
  
  private static final String RESTORE_ICON_LOCATION = "realms:textures/gui/realms/restore_icon.png";
  private static int lastScrollPosition = -1;
  
  private final RealmsConfigureWorldScreen lastScreen;
  
  private List<Backup> backups = java.util.Collections.emptyList();
  private String toolTip = null;
  
  private BackupSelectionList backupSelectionList;
  
  private int selectedBackup = -1;
  
  private static final int BACK_BUTTON_ID = 0;
  
  private static final int RESTORE_BUTTON_ID = 1;
  
  private static final int DOWNLOAD_BUTTON_ID = 2;
  private RealmsButton downloadButton;
  private Boolean noBackups = Boolean.valueOf(false);
  
  private RealmsServer serverData;
  private static final String UPLOADED_KEY = "Uploaded";
  
  public RealmsBackupScreen(RealmsConfigureWorldScreen lastscreen, RealmsServer serverData)
  {
    lastScreen = lastscreen;
    this.serverData = serverData;
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    backupSelectionList.mouseEvent();
  }
  
  public void init()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    backupSelectionList = new BackupSelectionList();
    
    if (lastScrollPosition != -1) {
      backupSelectionList.scroll(lastScrollPosition);
    }
    
    new Thread("Realms-fetch-backups")
    {
      public void run() {
        RealmsClient client = RealmsClient.createRealmsClient();
        try {
          backups = backupsForserverData.id).backups;
          noBackups = Boolean.valueOf(backups.size() == 0);
          RealmsBackupScreen.this.generateChangeList();
        } catch (RealmsServiceException e) {
          RealmsBackupScreen.LOGGER.error("Couldn't request backups", e);
        }
        
      }
    }.start();
    postInit();
  }
  
  private void generateChangeList() {
    if (backups.size() <= 1)
      return;
    Backup backup;
    Backup olderBackup;
    for (int i = 0; i < backups.size() - 1; i++) {
      backup = (Backup)backups.get(i);
      olderBackup = (Backup)backups.get(i + 1);
      
      if ((!metadata.isEmpty()) && (!metadata.isEmpty()))
      {


        for (String key : metadata.keySet()) {
          if ((!key.contains("Uploaded")) && (metadata.containsKey(key))) {
            if (!((String)metadata.get(key)).equals(metadata.get(key))) {
              addToChangeList(backup, key);
            }
          } else
            addToChangeList(backup, key);
        }
      }
    }
  }
  
  private void addToChangeList(Backup backup, String key) {
    if (key.contains("Uploaded")) {
      String uploadedTime = DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
      changeList.put(key, uploadedTime);
      backup.setUploadedVersion(true);
    } else {
      changeList.put(key, metadata.get(key));
    }
  }
  
  private void postInit() {
    buttonsAdd(this.downloadButton = newButton(2, width() - 125, 32, 100, 20, getLocalizedString("mco.backup.button.download")));
    buttonsAdd(newButton(0, width() - 125, height() - 35, 85, 20, getLocalizedString("gui.back")));
  }
  
  public void tick()
  {
    super.tick();
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    if (button.id() == 0) {
      Realms.setScreen(lastScreen);
    } else if (button.id() == 2) {
      downloadClicked();
    }
  }
  
  public void keyPressed(char eventCharacter, int eventKey)
  {
    if (eventKey == 1) {
      Realms.setScreen(lastScreen);
    }
  }
  
  private void restoreClicked(int selectedBackup) {
    if ((selectedBackup >= 0) && (selectedBackup < backups.size()) && (!serverData.expired)) {
      this.selectedBackup = selectedBackup;
      Date backupDate = backups.get(selectedBackup)).lastModifiedDate;
      String datePresentation = DateFormat.getDateTimeInstance(3, 3).format(backupDate);
      String age = RealmsUtil.convertToAgePresentation(Long.valueOf(System.currentTimeMillis() - backupDate.getTime()));
      String line2 = getLocalizedString("mco.configure.world.restore.question.line1", new Object[] { datePresentation, age });
      String line3 = getLocalizedString("mco.configure.world.restore.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, line2, line3, true, 1));
    }
  }
  
  private void downloadClicked() {
    String line2 = getLocalizedString("mco.configure.world.restore.download.question.line1");
    String line3 = getLocalizedString("mco.configure.world.restore.download.question.line2");
    Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 2));
  }
  
  private void downloadWorldData() {
    RealmsClient client = RealmsClient.createRealmsClient();
    try {
      String downloadLink = client.download(serverData.id);
      Realms.setScreen(new RealmsDownloadLatestWorldScreen(this, downloadLink, serverData.name + " (" + ((RealmsOptions)serverData.slots.get(Integer.valueOf(serverData.activeSlot))).getSlotName(serverData.activeSlot) + ")"));
    } catch (RealmsServiceException e) {
      LOGGER.error("Couldn't download world data");
      Realms.setScreen(new RealmsGenericErrorScreen(e, this));
    }
  }
  
  public void confirmResult(boolean result, int id)
  {
    if ((result) && (id == 1)) {
      restore();
    } else if ((result) && (id == 2)) {
      downloadWorldData();
    } else {
      Realms.setScreen(this);
    }
  }
  
  private void restore() {
    Backup backup = (Backup)backups.get(selectedBackup);
    RestoreTask restoreTask = new RestoreTask(backup, null);
    RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(lastScreen.getNewScreen(), restoreTask);
    longRunningMcoTaskScreen.start();
    Realms.setScreen(longRunningMcoTaskScreen);
  }
  
  public void render(int xm, int ym, float a)
  {
    toolTip = null;
    
    renderBackground();
    
    backupSelectionList.render(xm, ym, a);
    drawCenteredString(getLocalizedString("mco.configure.world.backup"), width() / 2, 12, 16777215);
    drawString(getLocalizedString("mco.configure.world.backup"), (width() - 150) / 2 - 90, 20, 10526880);
    
    if (noBackups.booleanValue()) {
      drawString(getLocalizedString("mco.backup.nobackups"), 20, height() / 2 - 10, 16777215);
    }
    
    downloadButton.active(!noBackups.booleanValue());
    
    super.render(xm, ym, a);
    if (toolTip != null) {
      renderMousehoverTooltip(toolTip, xm, ym);
    }
  }
  

  private class RestoreTask
    extends LongRunningTask
  {
    private final Backup backup;
    

    private RestoreTask(Backup backup)
    {
      this.backup = backup;
    }
    
    public void run()
    {
      setTitle(RealmsScreen.getLocalizedString("mco.backup.restoring"));
      
      for (int i = 0; i < 6; i++) {
        try {
          if (aborted()) return;
          RealmsClient client = RealmsClient.createRealmsClient();
          client.restoreWorld(serverData.id, backup.backupId);
          pause(1);
          if (aborted()) return;
          Realms.setScreen(lastScreen.getNewScreen());
          return;
        } catch (RetryCallException e) {
          if (aborted()) return;
          pause(delaySeconds);
        } catch (RealmsServiceException e) {
          if (aborted()) return;
          RealmsBackupScreen.LOGGER.error("Couldn't restore backup");
          Realms.setScreen(new RealmsGenericErrorScreen(e, lastScreen));
          return;
        } catch (Exception e) {
          if (aborted()) return;
          RealmsBackupScreen.LOGGER.error("Couldn't restore backup");
          error(e.getLocalizedMessage());
          return;
        }
      }
    }
    
    private void pause(int pauseSeconds) {
      try {
        Thread.sleep(pauseSeconds * 1000);
      } catch (InterruptedException e) {
        RealmsBackupScreen.LOGGER.error(e);
      }
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
  


  private class BackupSelectionList
    extends RealmsClickableScrolledSelectionList
  {
    public BackupSelectionList()
    {
      super(height(), 32, height() - 15, 36);
    }
    
    public int getItemCount()
    {
      return backups.size() + 1;
    }
    
    public int getMaxPosition()
    {
      return getItemCount() * 36;
    }
    
    public void renderBackground()
    {
      RealmsBackupScreen.this.renderBackground();
    }
    
    public void customMouseEvent(int y0, int y1, int headerHeight, float yo, int itemHeight)
    {
      if ((org.lwjgl.input.Mouse.isButtonDown(0)) && 
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
    

    public void renderItem(int i, int x, int y, int h, int mouseX, int mouseY)
    {
      
      if (i < backups.size()) {
        renderBackupItem(i, x, y, h, width);
      }
    }
    
    public int getScrollbarPosition()
    {
      return width() - 5;
    }
    
    public void itemClicked(int clickSlotPos, int slot, int xm, int ym, int width)
    {
      int infox = width() - 40;
      int infoy = clickSlotPos + 30 - getScroll();
      
      int mx = infox + 10;
      int my = infoy - 3;
      
      if ((xm >= infox) && (xm <= infox + 9) && (ym >= infoy) && (ym <= infoy + 9)) {
        if (!backups.get(slot)).changeList.isEmpty()) {
          RealmsBackupScreen.access$702(getScroll());
          Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)backups.get(slot)));
        }
      } else if ((xm >= mx) && (xm <= mx + 9) && (ym >= my) && (ym <= my + 9)) {
        RealmsBackupScreen.access$702(getScroll());
        RealmsBackupScreen.this.restoreClicked(slot);
      }
    }
    
    private void renderBackupItem(int i, int x, int y, int h, int width) {
      Backup backup = (Backup)backups.get(i);
      
      int color = backup.isUploadedVersion() ? -8388737 : 16777215;
      drawString("Backup (" + RealmsUtil.convertToAgePresentation(Long.valueOf(System.currentTimeMillis() - lastModifiedDate.getTime())) + ")", x + 2, y + 1, color);
      drawString(getMediumDatePresentation(lastModifiedDate), x + 2, y + 12, 5000268);
      
      int dx = width() - 30;
      int dy = -3;
      
      int infox = dx - 10;
      int infoy = dy + 3;
      
      if (!serverData.expired) {
        drawRestore(dx, y + dy, xm(), ym());
      }
      
      if (!changeList.isEmpty()) {
        drawInfo(infox, y + infoy, xm(), ym());
      }
    }
    
    private String getMediumDatePresentation(Date lastModifiedDate)
    {
      return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
    }
    
    private void drawRestore(int x, int y, int xm, int ym) {
      boolean hovered = (xm >= x) && (xm <= x + 12) && (ym >= y) && (ym <= y + 14) && (ym < height() - 15) && (ym > 32);
      
      RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, hovered ? 28.0F : 0.0F, 23, 28, 23.0F, 56.0F);
      GL11.glPopMatrix();
      
      if (hovered) {
        toolTip = RealmsScreen.getLocalizedString("mco.backup.button.restore");
      }
    }
    
    private void drawInfo(int x, int y, int xm, int ym) {
      boolean hovered = (xm >= x) && (xm <= x + 8) && (ym >= y) && (ym <= y + 8) && (ym < height() - 15) && (ym > 32);
      
      RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, hovered ? 15.0F : 0.0F, 15, 15, 15.0F, 30.0F);
      GL11.glPopMatrix();
      
      if (hovered) {
        toolTip = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
      }
    }
  }
}
