package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsBufferBuilder;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.realms.Tezzelator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsUploadScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private static final int CANCEL_BUTTON = 0;
  
  private static final int BACK_BUTTON = 1;
  
  private final RealmsResetWorldScreen lastScreen;
  private final RealmsLevelSummary selectedLevel;
  private final long worldId;
  private final int slotId;
  private final UploadStatus uploadStatus;
  private volatile String errorMessage = null;
  private volatile String status = null;
  private volatile String progress = null;
  private volatile boolean cancelled = false;
  private volatile boolean uploadFinished = false;
  private volatile boolean showDots = true;
  
  private volatile boolean uploadStarted = false;
  
  private RealmsButton backButton;
  
  private RealmsButton cancelButton;
  private int animTick = 0;
  private static final String[] DOTS = { "", ".", ". .", ". . ." };
  private int dotIndex = 0;
  
  private Long previousWrittenBytes = null;
  private Long previousTimeSnapshot = null;
  private long bytesPersSecond = 0L;
  
  private static final ReentrantLock uploadLock = new ReentrantLock();
  
  public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen, RealmsLevelSummary selectedLevel) {
    this.worldId = worldId;
    this.slotId = slotId;
    this.lastScreen = lastScreen;
    this.selectedLevel = selectedLevel;
    uploadStatus = new UploadStatus();
  }
  
  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    backButton = newButton(1, width() / 2 - 100, height() - 42, 200, 20, getLocalizedString("gui.back"));
    buttonsAdd(this.cancelButton = newButton(0, width() / 2 - 100, height() - 42, 200, 20, getLocalizedString("gui.cancel")));
    
    if (!uploadStarted) {
      if (lastScreen.slot != -1) {
        lastScreen.switchSlot(this);
      } else {
        upload();
      }
    }
  }
  
  public void confirmResult(boolean result, int buttonId)
  {
    if ((result) && (!uploadStarted)) {
      uploadStarted = true;
      Realms.setScreen(this);
      upload();
    }
  }
  
  public void removed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) {
      return;
    }
    
    if (button.id() == 1) {
      lastScreen.confirmResult(true, 0);
    } else if (button.id() == 0) {
      cancelled = true;
      Realms.setScreen(lastScreen);
    }
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      cancelled = true;
      Realms.setScreen(lastScreen);
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    if ((!uploadFinished) && (uploadStatus.bytesWritten.longValue() != 0L) && (uploadStatus.bytesWritten.longValue() == uploadStatus.totalBytes.longValue())) {
      status = getLocalizedString("mco.upload.verifying");
    }
    
    drawCenteredString(status, width() / 2, 50, 16777215);
    
    if (showDots) {
      drawDots();
    }
    
    if ((uploadStatus.bytesWritten.longValue() != 0L) && (!cancelled)) {
      drawProgressBar();
      drawUploadSpeed();
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
    double percentage = uploadStatus.bytesWritten.doubleValue() / uploadStatus.totalBytes.doubleValue() * 100.0D;
    
    if (percentage > 100.0D) {
      percentage = 100.0D;
    }
    
    progress = String.format("%.1f", new Object[] { Double.valueOf(percentage) });
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(3553);
    
    double base = width() / 2 - 100;
    double diff = 0.5D;
    
    Tezzelator t = Tezzelator.instance;
    t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
    
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
  
  private void drawUploadSpeed() {
    if (animTick % RealmsSharedConstants.TICKS_PER_SECOND == 0) {
      if (previousWrittenBytes != null) {
        long timeElapsed = System.currentTimeMillis() - previousTimeSnapshot.longValue();
        if (timeElapsed == 0L) {
          timeElapsed = 1L;
        }
        bytesPersSecond = (1000L * (uploadStatus.bytesWritten.longValue() - previousWrittenBytes.longValue()) / timeElapsed);
        drawUploadSpeed0(bytesPersSecond);
      }
      
      previousWrittenBytes = uploadStatus.bytesWritten;
      previousTimeSnapshot = Long.valueOf(System.currentTimeMillis());
    } else {
      drawUploadSpeed0(bytesPersSecond);
    }
  }
  
  private void drawUploadSpeed0(long bytesPersSecond) {
    if (bytesPersSecond > 0L) {
      int progressLength = fontWidth(progress);
      String stringPresentation = "(" + humanReadableByteCount(bytesPersSecond) + ")";
      drawString(stringPresentation, width() / 2 + progressLength / 2 + 15, 84, 16777215);
    }
  }
  
  public static String humanReadableByteCount(long bytes) {
    int unit = 1024;
    if (bytes < unit) return bytes + " B";
    int exp = (int)(Math.log(bytes) / Math.log(unit));
    String pre = "KMGTPE".charAt(exp - 1) + "";
    return String.format("%.1f %sB/s", new Object[] { Double.valueOf(bytes / Math.pow(unit, exp)), pre });
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
  }
  
  public void tick()
  {
    super.tick();
    
    animTick += 1;
  }
  
  private void upload() {
    uploadStarted = true;
    
    new Thread()
    {
      public void run() {
        File archive = null;
        RealmsClient client = RealmsClient.createRealmsClient();
        long wid = worldId;
        try
        {
          if (!RealmsUploadScreen.uploadLock.tryLock(1L, java.util.concurrent.TimeUnit.SECONDS))
          {
























































































            uploadFinished = true;
            
            if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
              RealmsUploadScreen.uploadLock.unlock();
            } else {
              return;
            }
            
            showDots = false;
            buttonsRemove(cancelButton);
            buttonsAdd(backButton);
            
            if (archive != null) {
              RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
              archive.delete();
            }
            
            if (cancelled) {
              return;
            }
            
            try
            {
              client.uploadFinished(wid);
            } catch (RealmsServiceException e) {
              RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
            }
            return;
          }
          status = RealmsScreen.getLocalizedString("mco.upload.preparing");
          
          UploadInfo uploadInfo = null;
          
          for (int i = 0; i < 20; i++) {
            try {
              if (cancelled) {
                RealmsUploadScreen.this.uploadCancelled(wid);
                













































































                uploadFinished = true;
                
                if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
                  RealmsUploadScreen.uploadLock.unlock();
                } else {
                  return;
                }
                
                showDots = false;
                buttonsRemove(cancelButton);
                buttonsAdd(backButton);
                
                if (archive != null) {
                  RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
                  archive.delete();
                }
                
                if (cancelled) {
                  return;
                }
                
                try
                {
                  client.uploadFinished(wid);
                } catch (RealmsServiceException e) {
                  RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
                }
                return;
              }
              uploadInfo = client.upload(wid, UploadTokenCache.get(wid));
            }
            catch (RetryCallException e) {
              Thread.sleep(delaySeconds * 1000);
            }
          }
          
          if (uploadInfo == null) {
            status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
            

































































            uploadFinished = true;
            
            if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
              RealmsUploadScreen.uploadLock.unlock();
            } else {
              return;
            }
            
            showDots = false;
            buttonsRemove(cancelButton);
            buttonsAdd(backButton);
            
            if (archive != null) {
              RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
              archive.delete();
            }
            
            if (cancelled) {
              return;
            }
            
            try
            {
              client.uploadFinished(wid);
            } catch (RealmsServiceException e) {
              RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
            }
            return;
          }
          UploadTokenCache.put(wid, uploadInfo.getToken());
          
          if (!uploadInfo.isWorldClosed()) {
            status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
            


























































            uploadFinished = true;
            
            if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
              RealmsUploadScreen.uploadLock.unlock();
            } else {
              return;
            }
            
            showDots = false;
            buttonsRemove(cancelButton);
            buttonsAdd(backButton);
            
            if (archive != null) {
              RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
              archive.delete();
            }
            
            if (cancelled) {
              return;
            }
            
            try
            {
              client.uploadFinished(wid);
            } catch (RealmsServiceException e) {
              RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
            }
            return;
          }
          if (cancelled) {
            RealmsUploadScreen.this.uploadCancelled(wid);
            





















































            uploadFinished = true;
            
            if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
              RealmsUploadScreen.uploadLock.unlock();
            } else {
              return;
            }
            
            showDots = false;
            buttonsRemove(cancelButton);
            buttonsAdd(backButton);
            
            if (archive != null) {
              RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
              archive.delete();
            }
            
            if (cancelled) {
              return;
            }
            
            try
            {
              client.uploadFinished(wid);
            } catch (RealmsServiceException e) {
              RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
            }
            return;
          }
          File saves = new File(Realms.getGameDirectoryPath(), "saves");
          archive = RealmsUploadScreen.this.tarGzipArchive(new File(saves, selectedLevel.getLevelId()));
          
          if (cancelled) {
            RealmsUploadScreen.this.uploadCancelled(wid);
            













































            uploadFinished = true;
            
            if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
              RealmsUploadScreen.uploadLock.unlock();
            } else {
              return;
            }
            
            showDots = false;
            buttonsRemove(cancelButton);
            buttonsAdd(backButton);
            
            if (archive != null) {
              RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
              archive.delete();
            }
            
            if (cancelled) {
              return;
            }
            
            try
            {
              client.uploadFinished(wid);
            } catch (RealmsServiceException e) {
              RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
            }
            return;
          }
          if (!RealmsUploadScreen.this.verify(archive)) {
            errorMessage = RealmsScreen.getLocalizedString("mco.upload.size.failure", new Object[] { selectedLevel.getLevelName() });
            








































            uploadFinished = true;
            
            if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
              RealmsUploadScreen.uploadLock.unlock();
            } else {
              return;
            }
            
            showDots = false;
            buttonsRemove(cancelButton);
            buttonsAdd(backButton);
            
            if (archive != null) {
              RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
              archive.delete();
            }
            
            if (cancelled) {
              return;
            }
            
            try
            {
              client.uploadFinished(wid);
            } catch (RealmsServiceException e) {
              RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
            }
            return;
          }
          status = RealmsScreen.getLocalizedString("mco.upload.uploading", new Object[] { selectedLevel.getLevelName() });
          
          FileUpload fileUpload = new FileUpload();
          fileUpload.upload(archive, worldId, slotId, uploadInfo, Realms.getSessionId(), Realms.getName(), RealmsSharedConstants.VERSION_STRING, uploadStatus);
          
          while (!fileUpload.isFinished()) {
            if (cancelled) {
              fileUpload.cancel();
              RealmsUploadScreen.this.uploadCancelled(wid);
              




























              uploadFinished = true;
              
              if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
                RealmsUploadScreen.uploadLock.unlock();
              } else {
                return;
              }
              
              showDots = false;
              buttonsRemove(cancelButton);
              buttonsAdd(backButton);
              
              if (archive != null) {
                RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
                archive.delete();
              }
              
              if (cancelled) {
                return;
              }
              
              try
              {
                client.uploadFinished(wid);
              } catch (RealmsServiceException e) {
                RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
              }
              return;
            }
            try
            {
              Thread.sleep(500L);
            } catch (InterruptedException e) {
              RealmsUploadScreen.LOGGER.error("Failed to check Realms file upload status");
            }
          }
          
          if ((fileUpload.getStatusCode() >= 200) && (fileUpload.getStatusCode() < 300)) {
            uploadFinished = true;
            status = RealmsScreen.getLocalizedString("mco.upload.done");
            backButton.msg(RealmsScreen.getLocalizedString("gui.done"));
            UploadTokenCache.invalidate(wid);
          }
          else if ((fileUpload.getStatusCode() == 400) && (fileUpload.getErrorMessage() != null)) {
            errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[] { fileUpload.getErrorMessage() });
          } else {
            errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[] { Integer.valueOf(fileUpload.getStatusCode()) });
          }
          







          uploadFinished = true;
          
          if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
            RealmsUploadScreen.uploadLock.unlock();
          } else {
            return;
          }
          
          showDots = false;
          buttonsRemove(cancelButton);
          buttonsAdd(backButton);
          
          if (archive != null) {
            RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
            archive.delete();
          }
          
          if (cancelled) {
            return;
          }
          
          try
          {
            client.uploadFinished(wid);
          } catch (RealmsServiceException e) {
            RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
          }
          showDots = false;
        }
        catch (IOException e)
        {
          errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[] { e.getMessage() });
          




          uploadFinished = true;
          
          if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
            RealmsUploadScreen.uploadLock.unlock();
          } else {
            return;
          }
          
          showDots = false;
          buttonsRemove(cancelButton);
          buttonsAdd(backButton);
          
          if (archive != null) {
            RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
            archive.delete();
          }
          
          if (cancelled) {
            return;
          }
          
          try
          {
            client.uploadFinished(wid);
          } catch (RealmsServiceException e) {
            RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
          }
        }
        catch (RealmsServiceException e)
        {
          errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[] { e.toString() });
          


          uploadFinished = true;
          
          if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
            RealmsUploadScreen.uploadLock.unlock();
          } else {
            return;
          }
          
          showDots = false;
          buttonsRemove(cancelButton);
          buttonsAdd(backButton);
          
          if (archive != null) {
            RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
            archive.delete();
          }
          
          if (cancelled) {
            return;
          }
          
          try
          {
            client.uploadFinished(wid);
          } catch (RealmsServiceException e) {
            RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
          }
        }
        catch (InterruptedException e)
        {
          RealmsUploadScreen.LOGGER.error("Could not acquire upload lock");
          
          uploadFinished = true;
          
          if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
            RealmsUploadScreen.uploadLock.unlock();
          } else {
            return;
          }
          
          showDots = false;
          buttonsRemove(cancelButton);
          buttonsAdd(backButton);
          
          if (archive != null) {
            RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
            archive.delete();
          }
          
          if (cancelled) {
            return;
          }
          
          try
          {
            client.uploadFinished(wid);
          } catch (RealmsServiceException e) {
            RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() });
          }
        }
        finally
        {
          uploadFinished = true;
          
          if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
            RealmsUploadScreen.uploadLock.unlock();
          } else {
            return;
          }
        }
        
        buttonsRemove(cancelButton);
        buttonsAdd(backButton);
        
        if (archive != null) {
          RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
          archive.delete();
        }
        
        if (cancelled)
        {
          return;
        }
        try
        {
          client.uploadFinished(wid);
        } catch (RealmsServiceException e) {
          RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[] { e.toString() }); }
        throw localObject;
      }
    }.start();
  }
  
  private void uploadCancelled(long worldId)
  {
    status = getLocalizedString("mco.upload.cancelled");
    String oldToken = UploadTokenCache.get(worldId);
    UploadTokenCache.invalidate(worldId);
    try
    {
      RealmsClient client = RealmsClient.createRealmsClient();
      client.uploadCancelled(worldId, oldToken);
    } catch (RealmsServiceException e) {
      LOGGER.error("Failed to cancel upload", e);
    }
  }
  
  private boolean verify(File archive) {
    return archive.length() < 1048576000L;
  }
  
  private File tarGzipArchive(File pathToDirectoryFile) throws IOException {
    TarArchiveOutputStream tar = null;
    try {
      File file = File.createTempFile("realms-upload-file", ".tar.gz");
      tar = new TarArchiveOutputStream(new java.util.zip.GZIPOutputStream(new FileOutputStream(file)));
      addFileToTarGz(tar, pathToDirectoryFile.getAbsolutePath(), "world", true);
      tar.finish();
      return file;
    } finally {
      if (tar != null) {
        tar.close();
      }
    }
  }
  
  private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base, boolean root) throws IOException {
    if (cancelled) {
      return;
    }
    
    File f = new File(path);
    String entryName = base + f.getName();
    TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
    tOut.putArchiveEntry(tarEntry);
    
    if (f.isFile()) {
      org.apache.commons.compress.utils.IOUtils.copy(new FileInputStream(f), tOut);
      tOut.closeArchiveEntry();
    } else {
      tOut.closeArchiveEntry();
      File[] children = f.listFiles();
      
      if (children != null) {
        for (File child : children) {
          addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/", false);
        }
      }
    }
  }
}
