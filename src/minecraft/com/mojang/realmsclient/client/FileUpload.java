package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.RealmsVersion;
import com.mojang.realmsclient.dto.UploadInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

public class FileUpload
{
  private static final Logger LOGGER = ;
  private static final String UPLOAD_PATH = "/upload";
  private static final String PORT = "8080";
  
  public FileUpload() { cancelled = false;
    finished = false;
    

    statusCode = -1;
    

    requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build(); }
  
  private volatile boolean cancelled;
  private volatile boolean finished;
  private HttpPost request;
  private int statusCode;
  private String errorMessage;
  private RequestConfig requestConfig;
  private Thread currentThread;
  public void upload(final File file, final long worldId, int slotId, final UploadInfo uploadInfo, final String sessionId, final String username, final String clientVersion, final UploadStatus uploadStatus) { if (currentThread != null) {
      return;
    }
    
    currentThread = new Thread()
    {
      public void run() {
        request = new HttpPost("http://" + uploadInfo.getUploadEndpoint() + ":" + "8080" + "/upload" + "/" + String.valueOf(worldId) + "/" + String.valueOf(sessionId));
        CloseableHttpClient client = null;
        try {
          client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
          


          String realmsVersion = RealmsVersion.getVersion();
          if (realmsVersion != null) {
            request.setHeader("Cookie", "sid=" + username + ";token=" + uploadInfo.getToken() + ";user=" + clientVersion + ";version=" + uploadStatus + ";realms_version=" + realmsVersion);
          } else {
            request.setHeader("Cookie", "sid=" + username + ";token=" + uploadInfo.getToken() + ";user=" + clientVersion + ";version=" + uploadStatus);
          }
          
          filetotalBytes = Long.valueOf(val$file.length());
          
          FileUpload.CustomInputStreamEntity entity = new FileUpload.CustomInputStreamEntity(new java.io.FileInputStream(val$file), val$file.length(), file);
          entity.setContentType("application/octet-stream");
          request.setEntity(entity);
          
          HttpResponse response = client.execute(request);
          
          int statusCode = response.getStatusLine().getStatusCode();
          
          if (statusCode == 401) {
            FileUpload.LOGGER.debug("Realms server returned 401: " + response.getFirstHeader("WWW-Authenticate"));
          }
          
          FileUpload.this.statusCode = statusCode;
          
          String json = org.apache.http.util.EntityUtils.toString(response.getEntity(), "UTF-8");
          
          if (json != null) {
            try {
              JsonParser parser = new JsonParser();
              errorMessage = parser.parse(json).getAsJsonObject().get("errorMsg").getAsString();
            }
            catch (Exception e) {}
          }
          return;
        } catch (Exception e) {
          FileUpload.LOGGER.error("Caught exception while uploading: " + e.getMessage());
        } finally {
          request.releaseConnection();
          finished = true;
          if (client != null) {
            try {
              client.close();
            } catch (IOException e) {
              FileUpload.LOGGER.error("Failed to close Realms upload client");
            }
          }
        }
      }
    };
    currentThread.start();
  }
  
  public void cancel() {
    cancelled = true;
    if (request != null) {
      request.abort();
    }
  }
  
  public boolean isFinished() {
    return finished;
  }
  
  public int getStatusCode() {
    return statusCode;
  }
  
  public String getErrorMessage() {
    return errorMessage;
  }
  
  private static class CustomInputStreamEntity extends InputStreamEntity
  {
    private final long length;
    private final InputStream content;
    private final UploadStatus uploadStatus;
    
    public CustomInputStreamEntity(InputStream instream, long length, UploadStatus uploadStatus) {
      super();
      content = instream;
      this.length = length;
      this.uploadStatus = uploadStatus;
    }
    
    public void writeTo(OutputStream outstream) throws IOException {
      org.apache.http.util.Args.notNull(outstream, "Output stream");
      InputStream instream = content;
      try {
        byte[] buffer = new byte['က'];
        
        if (length < 0L) { int l;
          UploadStatus localUploadStatus1;
          for (; (l = instream.read(buffer)) != -1; 
              
              (localUploadStatus1.bytesWritten = Long.valueOf(bytesWritten.longValue() + l)))
          {
            outstream.write(buffer, 0, l);
            localUploadStatus1 = uploadStatus;
          }
        }
        
        long remaining = length;
        while (remaining > 0L) {
          int l = instream.read(buffer, 0, (int)Math.min(4096L, remaining));
          if (l == -1) {
            break;
          }
          outstream.write(buffer, 0, l);
          UploadStatus localUploadStatus2 = uploadStatus;(localUploadStatus2.bytesWritten = Long.valueOf(bytesWritten.longValue() + l));
          remaining -= l;
          outstream.flush();
        }
      }
      finally {
        instream.close();
      }
    }
  }
}
