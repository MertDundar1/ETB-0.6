package com.ibm.icu.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;








public abstract class URLHandler
{
  public static final String PROPNAME = "urlhandler.props";
  private static final Map<String, Method> handlers;
  private static final boolean DEBUG = ICUDebug.enabled("URLHandler");
  
  static {
    Map<String, Method> h = null;
    try
    {
      InputStream is = URLHandler.class.getResourceAsStream("urlhandler.props");
      if (is == null) {
        ClassLoader loader = Utility.getFallbackClassLoader();
        is = loader.getResourceAsStream("urlhandler.props");
      }
      
      if (is != null) {
        Class<?>[] params = { URL.class };
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          line = line.trim();
          
          if ((line.length() != 0) && (line.charAt(0) != '#'))
          {


            int ix = line.indexOf('=');
            
            if (ix == -1) {
              if (!DEBUG) break; System.err.println("bad urlhandler line: '" + line + "'"); break;
            }
            

            String key = line.substring(0, ix).trim();
            String value = line.substring(ix + 1).trim();
            try
            {
              Class<?> cl = Class.forName(value);
              Method m = cl.getDeclaredMethod("get", params);
              
              if (h == null) {
                h = new HashMap();
              }
              
              h.put(key, m);
            }
            catch (ClassNotFoundException e) {
              if (DEBUG) System.err.println(e);
            }
            catch (NoSuchMethodException e) {
              if (DEBUG) System.err.println(e);
            }
            catch (SecurityException e) {
              if (DEBUG) System.err.println(e);
            }
          } }
        br.close();
      }
    } catch (Throwable t) {
      if (DEBUG) { System.err.println(t);
      }
    }
    handlers = h;
  }
  
  public static URLHandler get(URL url) {
    if (url == null) {
      return null;
    }
    
    String protocol = url.getProtocol();
    
    if (handlers != null) {
      Method m = (Method)handlers.get(protocol);
      
      if (m != null) {
        try {
          URLHandler handler = (URLHandler)m.invoke(null, new Object[] { url });
          
          if (handler != null) {
            return handler;
          }
        }
        catch (IllegalAccessException e) {
          if (DEBUG) System.err.println(e);
        }
        catch (IllegalArgumentException e) {
          if (DEBUG) System.err.println(e);
        }
        catch (InvocationTargetException e) {
          if (DEBUG) { System.err.println(e);
          }
        }
      }
    }
    return getDefault(url);
  }
  
  protected static URLHandler getDefault(URL url) {
    URLHandler handler = null;
    
    String protocol = url.getProtocol();
    try {
      if (protocol.equals("file")) {
        handler = new FileURLHandler(url);
      } else if ((protocol.equals("jar")) || (protocol.equals("wsjar"))) {
        handler = new JarURLHandler(url);
      }
    }
    catch (Exception e) {}
    
    return handler;
  }
  
  private static class FileURLHandler extends URLHandler {
    File file;
    
    FileURLHandler(URL url) {
      try {
        file = new File(url.toURI());
      }
      catch (URISyntaxException use) {}
      
      if ((file == null) || (!file.exists())) {
        if (URLHandler.DEBUG) System.err.println("file does not exist - " + url.toString());
        throw new IllegalArgumentException();
      }
    }
    
    public void guide(URLHandler.URLVisitor v, boolean recurse, boolean strip) {
      if (file.isDirectory()) {
        process(v, recurse, strip, "/", file.listFiles());
      } else {
        v.visit(file.getName());
      }
    }
    
    private void process(URLHandler.URLVisitor v, boolean recurse, boolean strip, String path, File[] files) {
      for (int i = 0; i < files.length; i++) {
        File f = files[i];
        
        if (f.isDirectory()) {
          if (recurse) {
            process(v, recurse, strip, path + f.getName() + '/', f.listFiles());
          }
        } else {
          v.visit(path + f.getName());
        }
      }
    }
  }
  
  private static class JarURLHandler extends URLHandler {
    JarFile jarFile;
    String prefix;
    
    JarURLHandler(URL url) {
      try {
        prefix = url.getPath();
        
        int ix = prefix.lastIndexOf("!/");
        
        if (ix >= 0) {
          prefix = prefix.substring(ix + 2);
        }
        
        String protocol = url.getProtocol();
        if (!protocol.equals("jar"))
        {

          String urlStr = url.toString();
          int idx = urlStr.indexOf(":");
          if (idx != -1) {
            url = new URL("jar" + urlStr.substring(idx));
          }
        }
        
        JarURLConnection conn = (JarURLConnection)url.openConnection();
        jarFile = conn.getJarFile();
      }
      catch (Exception e) {
        if (URLHandler.DEBUG) System.err.println("icurb jar error: " + e);
        throw new IllegalArgumentException("jar error: " + e.getMessage());
      }
    }
    
    public void guide(URLHandler.URLVisitor v, boolean recurse, boolean strip) {
      try {
        Enumeration<JarEntry> entries = jarFile.entries();
        
        while (entries.hasMoreElements()) {
          JarEntry entry = (JarEntry)entries.nextElement();
          
          if (!entry.isDirectory()) {
            String name = entry.getName();
            
            if (name.startsWith(prefix)) {
              name = name.substring(prefix.length());
              
              int ix = name.lastIndexOf('/');
              
              if (ix != -1) {
                if (!recurse) {
                  continue;
                }
                
                if (strip) {
                  name = name.substring(ix + 1);
                }
              }
              
              v.visit(name);
            }
          }
        }
      }
      catch (Exception e) {
        if (URLHandler.DEBUG) System.err.println("icurb jar error: " + e);
      }
    }
  }
  
  public void guide(URLVisitor visitor, boolean recurse)
  {
    guide(visitor, recurse, true);
  }
  
  public URLHandler() {}
  
  public abstract void guide(URLVisitor paramURLVisitor, boolean paramBoolean1, boolean paramBoolean2);
  
  public static abstract interface URLVisitor
  {
    public abstract void visit(String paramString);
  }
}
