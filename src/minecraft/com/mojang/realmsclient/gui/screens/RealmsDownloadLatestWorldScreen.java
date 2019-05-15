package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.FileDownload;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsBufferBuilder;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private final RealmsScreen lastScreen;
  
  private final String downloadLink;
  private RealmsButton cancelButton;
  private final String worldName;
  private final DownloadStatus downloadStatus;
  private volatile String errorMessage = null;
  private volatile String status = null;
  private volatile String progress = null;
  private volatile boolean cancelled = false;
  private volatile boolean showDots = true;
  private volatile boolean finished = false;
  private volatile boolean extracting = false;
  
  private Long previousWrittenBytes = null;
  private Long previousTimeSnapshot = null;
  private long bytesPersSecond = 0L;
  
  private int animTick = 0;
  private static final String[] DOTS = { "", ".", ". .", ". . ." };
  private int dotIndex = 0;
  
  private final int WARNING_ID = 100;
  
  private boolean checked = false;
  private static final ReentrantLock downloadLock = new ReentrantLock();
  
  public RealmsDownloadLatestWorldScreen(RealmsScreen lastScreen, String downloadLink, String worldName) {
    this.lastScreen = lastScreen;
    this.worldName = worldName;
    this.downloadLink = downloadLink;
    downloadStatus = new DownloadStatus();
  }
  
  public void init()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    buttonsClear();
    buttonsAdd(this.cancelButton = newButton(0, width() / 2 - 100, height() - 42, 200, 20, getLocalizedString("gui.cancel")));
    checkDownloadSize();
  }
  
  private void checkDownloadSize() {
    if (finished) {
      return;
    }
    
    if ((!checked) && (getContentLength(downloadLink) >= 1048576000L)) {
      String line1 = getLocalizedString("mco.download.confirmation.line1", new Object[] { humanReadableSize(1048576000L) });
      String line2 = getLocalizedString("mco.download.confirmation.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, line1, line2, false, 100));
    } else {
      downloadSave();
    }
  }
  
  public void confirmResult(boolean result, int id)
  {
    checked = true;
    Realms.setScreen(this);
    downloadSave();
  }
  
  private long getContentLength(String downloadLink) {
    FileDownload fileDownload = new FileDownload();
    return fileDownload.contentLength(downloadLink);
  }
  

  public void tick()
  {
    super.tick();
    
    animTick += 1;
  }
  

  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) {
      return;
    }
    
    if (button.id() == 0) {
      cancelled = true;
      backButtonClicked();
    }
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      cancelled = true;
      backButtonClicked();
    }
  }
  
  private void backButtonClicked() {
    Realms.setScreen(lastScreen);
  }
  

  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    if ((extracting) && (!finished)) {
      status = getLocalizedString("mco.download.extracting");
    }
    
    drawCenteredString(getLocalizedString("mco.download.title"), width() / 2, 20, 16777215);
    
    drawCenteredString(status, width() / 2, 50, 16777215);
    
    if (showDots) {
      drawDots();
    }
    
    if ((downloadStatus.bytesWritten.longValue() != 0L) && (!cancelled)) {
      drawProgressBar();
      drawDownloadSpeed();
    }
    
    if (errorMessage != null) {
      drawCenteredString(errorMessage, width() / 2, 110, 16711680);
    }
    
    super.render(xm, ym, a);
  }
  
  private void drawDots() {
    int statusWidth = fontWidth(status);
    if (animTick % 10 == 0) {
      dotIndex += 1;
    }
    drawString(DOTS[(dotIndex % DOTS.length)], width() / 2 + statusWidth / 2 + 5, 50, 16777215);
  }
  
  private void drawProgressBar() {
    double percentage = downloadStatus.bytesWritten.doubleValue() / downloadStatus.totalBytes.doubleValue() * 100.0D;
    progress = String.format("%.1f", new Object[] { Double.valueOf(percentage) });
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(3553);
    Tezzelator t = Tezzelator.instance;
    t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
    
    double base = width() / 2 - 100;
    double diff = 0.5D;
    
    t.vertex(base - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
    t.vertex(base + 200.0D * percentage / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
    t.vertex(base + 200.0D * percentage / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
    t.vertex(base - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
    
    t.vertex(base, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
    t.vertex(base + 200.0D * percentage / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
    t.vertex(base + 200.0D * percentage / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
    t.vertex(base, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
    
    t.end();
    GL11.glEnable(3553);
    
    drawCenteredString(progress + " %", width() / 2, 84, 16777215);
  }
  
  private void drawDownloadSpeed() {
    if (animTick % RealmsSharedConstants.TICKS_PER_SECOND == 0) {
      if (previousWrittenBytes != null) {
        long timeElapsed = System.currentTimeMillis() - previousTimeSnapshot.longValue();
        if (timeElapsed == 0L) {
          timeElapsed = 1L;
        }
        bytesPersSecond = (1000L * (downloadStatus.bytesWritten.longValue() - previousWrittenBytes.longValue()) / timeElapsed);
        drawDownloadSpeed0(bytesPersSecond);
      }
      previousWrittenBytes = downloadStatus.bytesWritten;
      previousTimeSnapshot = Long.valueOf(System.currentTimeMillis());
    } else {
      drawDownloadSpeed0(bytesPersSecond);
    }
  }
  
  private void drawDownloadSpeed0(long bytesPersSecond) {
    if (bytesPersSecond > 0L) {
      int progressLength = fontWidth(progress);
      String stringPresentation = "(" + humanReadableSpeed(bytesPersSecond) + ")";
      drawString(stringPresentation, width() / 2 + progressLength / 2 + 15, 84, 16777215);
    }
  }
  
  public static String humanReadableSpeed(long bytes) {
    int unit = 1024;
    if (bytes < unit) return bytes + " B";
    int exp = (int)(Math.log(bytes) / Math.log(unit));
    String pre = "KMGTPE".charAt(exp - 1) + "";
    return String.format("%.1f %sB/s", new Object[] { Double.valueOf(bytes / Math.pow(unit, exp)), pre });
  }
  
  public static String humanReadableSize(long bytes) {
    int unit = 1024;
    if (bytes < unit) return bytes + " B";
    int exp = (int)(Math.log(bytes) / Math.log(unit));
    String pre = "KMGTPE".charAt(exp - 1) + "";
    return String.format("%.0f %sB", new Object[] { Double.valueOf(bytes / Math.pow(unit, exp)), pre });
  }
  

  public void mouseEvent()
  {
    super.mouseEvent();
  }
  
  private void downloadSave() {
    new Thread()
    {
      public void run() {
        try {
          if (!RealmsDownloadLatestWorldScreen.downloadLock.tryLock(1L, java.util.concurrent.TimeUnit.SECONDS)) {
            return;
          }
          
          status = RealmsScreen.getLocalizedString("mco.download.preparing");
          
          if (cancelled) {
            RealmsDownloadLatestWorldScreen.this.downloadCancelled();
          }
          else
          {
            status = RealmsScreen.getLocalizedString("mco.download.downloading", new Object[] { worldName });
            
            FileDownload fileDownload = new FileDownload();
            fileDownload.contentLength(downloadLink);
            fileDownload.download(downloadLink, worldName, downloadStatus, getLevelStorageSource());
            
            while (!fileDownload.isFinished()) {
              if (fileDownload.isError()) {
                fileDownload.cancel();
                errorMessage = RealmsScreen.getLocalizedString("mco.download.failed");
                cancelButton.msg(RealmsScreen.getLocalizedString("gui.done")); return;
              }
              
              if (fileDownload.isExtracting()) {
                extracting = true;
              }
              if (cancelled) {
                fileDownload.cancel();
                RealmsDownloadLatestWorldScreen.this.downloadCancelled(); return;
              }
              try
              {
                Thread.sleep(500L);
              } catch (InterruptedException e) {
                RealmsDownloadLatestWorldScreen.LOGGER.error("Failed to check Realms backup download status");
              }
            }
            
            finished = true;
            status = RealmsScreen.getLocalizedString("mco.download.done");
            cancelButton.msg(RealmsScreen.getLocalizedString("gui.done"));
          }
        } catch (InterruptedException e) {
          RealmsDownloadLatestWorldScreen.LOGGER.error("Could not acquire upload lock");
        } catch (Exception e) {
          errorMessage = RealmsScreen.getLocalizedString("mco.download.failed");
          e.printStackTrace();
        } finally {
          if (!RealmsDownloadLatestWorldScreen.downloadLock.isHeldByCurrentThread()) {
            return;
          }
          RealmsDownloadLatestWorldScreen.downloadLock.unlock();
          

          showDots = false;
          buttonsRemove(cancelButton);
          finished = true;
        }
      }
    }.start();
  }
  
  private void downloadCancelled() {
    status = getLocalizedString("mco.download.cancelled");
  }
  
  public class DownloadStatus
  {
    public volatile Long bytesWritten = Long.valueOf(0L);
    public volatile Long totalBytes = Long.valueOf(0L);
    
    public DownloadStatus() {}
  }
}
