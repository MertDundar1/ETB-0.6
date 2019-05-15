package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public abstract class Request<T extends Request>
{
  protected HttpURLConnection connection;
  private boolean connected;
  protected String url;
  private static final int DEFAULT_READ_TIMEOUT = 60000;
  private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
  
  public Request(String url, int connectTimeout, int readTimeout)
  {
    try
    {
      this.url = url;
      Proxy proxy = RealmsClientConfig.getProxy();
      
      if (proxy != null) {
        connection = ((HttpURLConnection)new URL(url).openConnection(proxy));
      } else {
        connection = ((HttpURLConnection)new URL(url).openConnection());
      }
      
      connection.setConnectTimeout(connectTimeout);
      connection.setReadTimeout(readTimeout);
    } catch (MalformedURLException e) {
      throw new RealmsHttpException(e.getMessage(), e);
    } catch (IOException e) {
      throw new RealmsHttpException(e.getMessage(), e);
    }
  }
  
  public void cookie(String key, String value) {
    cookie(connection, key, value);
  }
  
  public static void cookie(HttpURLConnection connection, String key, String value) {
    String cookie = connection.getRequestProperty("Cookie");
    if (cookie == null) {
      connection.setRequestProperty("Cookie", key + "=" + value);
    } else {
      connection.setRequestProperty("Cookie", cookie + ";" + key + "=" + value);
    }
  }
  
  public T header(String name, String value)
  {
    connection.addRequestProperty(name, value);
    return this;
  }
  
  public int getRetryAfterHeader() {
    return getRetryAfterHeader(connection);
  }
  
  public static int getRetryAfterHeader(HttpURLConnection connection) {
    String pauseTime = connection.getHeaderField("Retry-After");
    try {
      return Integer.valueOf(pauseTime).intValue();
    } catch (Exception e) {}
    return 5;
  }
  
  public int responseCode()
  {
    try {
      connect();
      return connection.getResponseCode();
    } catch (Exception e) {
      throw new RealmsHttpException(e.getMessage(), e);
    }
  }
  
  public String text() {
    try {
      connect();
      String result = null;
      
      if (responseCode() >= 400) {
        result = read(connection.getErrorStream());
      } else {
        result = read(connection.getInputStream());
      }
      
      dispose();
      return result;
    } catch (IOException e) {
      throw new RealmsHttpException(e.getMessage(), e);
    }
  }
  
  private String read(InputStream in) throws IOException {
    if (in == null) return "";
    InputStreamReader streamReader = new InputStreamReader(in, "UTF-8");
    StringBuilder sb = new StringBuilder();
    for (int x = streamReader.read(); x != -1; x = streamReader.read()) { sb.append((char)x);
    }
    return sb.toString();
  }
  
  private void dispose() {
    byte[] bytes = new byte['Ѐ'];
    try {
      int count = 0;
      InputStream in = connection.getInputStream();
      while ((count = in.read(bytes)) > 0) {}
      

      in.close();
    } catch (Exception ignore) {
      try {
        InputStream errorStream = connection.getErrorStream();
        int ret = 0;
        
        if (errorStream == null) {
          return;
        }
        
        while ((ret = errorStream.read(bytes)) > 0) {}
        

        errorStream.close();
      }
      catch (IOException ignoreToo) {}
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
  
  protected T connect()
  {
    if (!connected) {
      T t = doConnect();
      connected = true;
      return t;
    }
    return this;
  }
  
  protected abstract T doConnect();
  
  public static Request<?> get(String url)
  {
    return new Get(url, 5000, 60000);
  }
  
  public static Request<?> get(String url, int connectTimeoutMillis, int readTimeoutMillis) {
    return new Get(url, connectTimeoutMillis, readTimeoutMillis);
  }
  
  public static Request<?> post(String uri, String content) {
    return new Post(uri, content.getBytes(), 5000, 60000);
  }
  
  public static Request<?> post(String uri, String content, int connectTimeoutMillis, int readTimeoutMillis) {
    return new Post(uri, content.getBytes(), connectTimeoutMillis, readTimeoutMillis);
  }
  
  public static Request<?> delete(String url) {
    return new Delete(url, 5000, 60000);
  }
  
  public static Request<?> put(String url, String content) {
    return new Put(url, content.getBytes(), 5000, 60000);
  }
  
  public static Request<?> put(String url, String content, int connectTimeoutMillis, int readTimeoutMillis) {
    return new Put(url, content.getBytes(), connectTimeoutMillis, readTimeoutMillis);
  }
  
  public String getHeader(String header) {
    return getHeader(connection, header);
  }
  
  public static String getHeader(HttpURLConnection connection, String header) {
    try {
      return connection.getHeaderField(header);
    } catch (Exception e) {}
    return "";
  }
  
  public static class Delete extends Request<Delete>
  {
    public Delete(String uri, int connectTimeout, int readTimeout)
    {
      super(connectTimeout, readTimeout);
    }
    
    public Delete doConnect()
    {
      try {
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.connect();
        return this;
      } catch (Exception e) {
        throw new RealmsHttpException(e.getMessage(), e);
      }
    }
  }
  
  public static class Get extends Request<Get>
  {
    public Get(String uri, int connectTimeout, int readTimeout)
    {
      super(connectTimeout, readTimeout);
    }
    
    public Get doConnect()
    {
      try {
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
        return this;
      } catch (Exception e) {
        throw new RealmsHttpException(e.getMessage(), e);
      }
    }
  }
  
  public static class Put extends Request<Put>
  {
    private byte[] content;
    
    public Put(String uri, byte[] content, int connectTimeout, int readTimeout)
    {
      super(connectTimeout, readTimeout);
      this.content = content;
    }
    
    public Put doConnect()
    {
      try {
        if (content != null) {
          connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        }
        
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("PUT");
        OutputStream os = connection.getOutputStream();
        os.write(content);
        os.flush();
        return this;
      } catch (Exception e) {
        throw new RealmsHttpException(e.getMessage(), e);
      }
    }
  }
  
  public static class Post extends Request<Post>
  {
    private byte[] content;
    
    public Post(String uri, byte[] content, int connectTimeout, int readTimeout)
    {
      super(connectTimeout, readTimeout);
      this.content = content;
    }
    
    public Post doConnect()
    {
      try {
        if (content != null) {
          connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        }
        
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        OutputStream out = connection.getOutputStream();
        out.write(content);
        out.flush();
        return this;
      } catch (Exception e) {
        throw new RealmsHttpException(e.getMessage(), e);
      }
    }
  }
}
