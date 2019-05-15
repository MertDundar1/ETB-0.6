package com.mojang.realmsclient.client;

import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen.DownloadStatus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

public class FileDownload
{
  private static final Logger LOGGER = ;
  
  public FileDownload() { cancelled = false;
    finished = false;
    error = false;
    extracting = false;
    





    requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build(); }
  
  private volatile boolean cancelled;
  private volatile boolean finished;
  private volatile boolean error;
  
  public long contentLength(String downloadLink) { CloseableHttpClient client = null;
    HttpGet httpGet = null;
    try {
      httpGet = new HttpGet(downloadLink);
      client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
      

      CloseableHttpResponse response = client.execute(httpGet);
      return Long.parseLong(response.getFirstHeader("Content-Length").getValue());
    } catch (Throwable t) { long l;
      LOGGER.error("Unable to get content length for download");
      return 0L;
    } finally {
      if (httpGet != null) {
        httpGet.releaseConnection();
      }
      if (client != null)
        try {
          client.close();
        } catch (IOException e) {
          LOGGER.error("Could not close http client", e); } } }
  
  private volatile boolean extracting;
  private volatile File tempFile;
  private volatile HttpGet request;
  private Thread currentThread;
  private RequestConfig requestConfig;
  public void download(final String downloadLink, final String worldName, final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, final RealmsAnvilLevelStorageSource levelStorageSource) { if (currentThread != null) {
      return;
    }
    
    currentThread = new Thread()
    {
      public void run() {
        CloseableHttpClient client = null;
        try {
          tempFile = File.createTempFile("backup", ".tar.gz");
          
          request = new HttpGet(downloadLink);
          client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
          


          HttpResponse response = client.execute(request);
          downloadStatustotalBytes = Long.valueOf(Long.parseLong(response.getFirstHeader("Content-Length").getValue()));
          
          if (response.getStatusLine().getStatusCode() != 200) {
            error = true;
            request.abort();
          }
          else
          {
            OutputStream os = new FileOutputStream(tempFile);
            FileDownload.ProgressListener progressListener = new FileDownload.ProgressListener(FileDownload.this, worldName.trim(), tempFile, levelStorageSource, downloadStatus, null);
            FileDownload.DownloadCountingOutputStream dcount = new FileDownload.DownloadCountingOutputStream(FileDownload.this, os);
            dcount.setListener(progressListener);
            
            org.apache.commons.io.IOUtils.copy(response.getEntity().getContent(), dcount);
          }
          return;
        } catch (Exception e) { FileDownload.LOGGER.error("Caught exception while downloading: " + e.getMessage());
          error = true;
        } finally {
          request.releaseConnection();
          if (tempFile != null) {
            tempFile.delete();
          }
          if (client != null) {
            try {
              client.close();
            } catch (IOException e) {
              FileDownload.LOGGER.error("Failed to close Realms download client");
            }
          }
        }
      }
    };
    currentThread.start();
  }
  
  public void cancel() {
    if (request != null) {
      request.abort();
    }
    if (tempFile != null) {
      tempFile.delete();
    }
    cancelled = true;
  }
  
  public boolean isFinished() {
    return finished;
  }
  
  public boolean isError() {
    return error;
  }
  
  public boolean isExtracting() {
    return extracting;
  }
  
  private static final String[] INVALID_FILE_NAMES = { "CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9" };
  

  public static String findAvailableFolderName(String folder)
  {
    folder = folder.replaceAll("[\\./\"]", "_");
    for (String invalidName : INVALID_FILE_NAMES) {
      if (folder.equalsIgnoreCase(invalidName)) {
        folder = "_" + folder + "_";
      }
    }
    return folder;
  }
  
  private void untarGzipArchive(String name, File file, RealmsAnvilLevelStorageSource levelStorageSource) throws IOException {
    Pattern namePattern = Pattern.compile(".*-([0-9]+)$");
    

    int number = 1;
    
    for (char replacer : RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS) {
      name = name.replace(replacer, '_');
    }
    
    if (org.apache.commons.lang3.StringUtils.isEmpty(name)) {
      name = "Realm";
    }
    
    name = findAvailableFolderName(name);
    try
    {
      for (RealmsLevelSummary summary : levelStorageSource.getLevelList()) {
        if (summary.getLevelId().toLowerCase().startsWith(name.toLowerCase())) {
          Matcher matcher = namePattern.matcher(summary.getLevelId());
          if (matcher.matches()) {
            if (Integer.valueOf(matcher.group(1)).intValue() > number) {
              number = Integer.valueOf(matcher.group(1)).intValue();
            }
          } else {
            number++;
          }
        }
      }
    } catch (Exception e) {
      error = true; return;
    }
    
    String finalName;
    if ((!levelStorageSource.isNewLevelIdAcceptable(name)) || (number > 1)) {
      String finalName = name + (number == 1 ? "" : new StringBuilder().append("-").append(number).toString());
      
      if (!levelStorageSource.isNewLevelIdAcceptable(finalName)) {
        boolean foundName = false;
        
        while (!foundName) {
          number++;
          finalName = name + (number == 1 ? "" : new StringBuilder().append("-").append(number).toString());
          
          if (levelStorageSource.isNewLevelIdAcceptable(finalName)) {
            foundName = true;
          }
        }
      }
    } else {
      finalName = name;
    }
    
    TarArchiveInputStream tarIn = null;
    File saves = new File(net.minecraft.realms.Realms.getGameDirectoryPath(), "saves");
    try {
      saves.mkdir();
      
      tarIn = new TarArchiveInputStream(new org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file))));
      
      TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
      while (tarEntry != null) {
        File destPath = new File(saves, tarEntry.getName().replace("world", finalName));
        if (tarEntry.isDirectory()) {
          destPath.mkdirs();
        } else {
          destPath.createNewFile();
          byte[] btoRead = new byte['Ѐ'];
          BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
          int len = 0;
          
          while ((len = tarIn.read(btoRead)) != -1) {
            bout.write(btoRead, 0, len);
          }
          
          bout.close();
          btoRead = null;
        }
        
        tarEntry = tarIn.getNextTarEntry();
      }
    } catch (Exception e) { RealmsAnvilLevelStorageSource levelSource;
      error = true;
    } finally { RealmsAnvilLevelStorageSource levelSource;
      if (tarIn != null) {
        tarIn.close();
      }
      if (file != null) {
        file.delete();
      }
      RealmsAnvilLevelStorageSource levelSource = levelStorageSource;
      levelSource.renameLevel(finalName, finalName.trim());
      finished = true;
    }
  }
  

  private class ProgressListener
    implements ActionListener
  {
    private volatile String worldName;
    
    private volatile File tempFile;
    private volatile RealmsAnvilLevelStorageSource levelStorageSource;
    private volatile RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
    
    private ProgressListener(String worldName, File tempFile, RealmsAnvilLevelStorageSource levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus)
    {
      this.worldName = worldName;
      this.tempFile = tempFile;
      this.levelStorageSource = levelStorageSource;
      this.downloadStatus = downloadStatus;
    }
    
    public void actionPerformed(ActionEvent e)
    {
      downloadStatus.bytesWritten = Long.valueOf(((FileDownload.DownloadCountingOutputStream)e.getSource()).getByteCount());
      if ((downloadStatus.bytesWritten.longValue() >= downloadStatus.totalBytes.longValue()) && (!cancelled)) {
        try {
          extracting = true;
          FileDownload.this.untarGzipArchive(worldName, tempFile, levelStorageSource);
        } catch (IOException e1) {
          error = true;
        }
      }
    }
  }
  
  private class DownloadCountingOutputStream extends CountingOutputStream
  {
    private ActionListener listener = null;
    
    public DownloadCountingOutputStream(OutputStream out) {
      super();
    }
    
    public void setListener(ActionListener listener) {
      this.listener = listener;
    }
    
    protected void afterWrite(int n) throws IOException
    {
      super.afterWrite(n);
      if (listener != null) {
        listener.actionPerformed(new ActionEvent(this, 0, null));
      }
    }
  }
}
