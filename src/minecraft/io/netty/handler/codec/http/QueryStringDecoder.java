package io.netty.handler.codec.http;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


















































public class QueryStringDecoder
{
  private static final int DEFAULT_MAX_PARAMS = 1024;
  private final Charset charset;
  private final String uri;
  private final boolean hasPath;
  private final int maxParams;
  private String path;
  private Map<String, List<String>> params;
  private int nParams;
  
  public QueryStringDecoder(String uri)
  {
    this(uri, HttpConstants.DEFAULT_CHARSET);
  }
  



  public QueryStringDecoder(String uri, boolean hasPath)
  {
    this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
  }
  



  public QueryStringDecoder(String uri, Charset charset)
  {
    this(uri, charset, true);
  }
  



  public QueryStringDecoder(String uri, Charset charset, boolean hasPath)
  {
    this(uri, charset, hasPath, 1024);
  }
  



  public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams)
  {
    if (uri == null) {
      throw new NullPointerException("getUri");
    }
    if (charset == null) {
      throw new NullPointerException("charset");
    }
    if (maxParams <= 0) {
      throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
    }
    

    this.uri = uri;
    this.charset = charset;
    this.maxParams = maxParams;
    this.hasPath = hasPath;
  }
  



  public QueryStringDecoder(URI uri)
  {
    this(uri, HttpConstants.DEFAULT_CHARSET);
  }
  



  public QueryStringDecoder(URI uri, Charset charset)
  {
    this(uri, charset, 1024);
  }
  



  public QueryStringDecoder(URI uri, Charset charset, int maxParams)
  {
    if (uri == null) {
      throw new NullPointerException("getUri");
    }
    if (charset == null) {
      throw new NullPointerException("charset");
    }
    if (maxParams <= 0) {
      throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
    }
    

    String rawPath = uri.getRawPath();
    if (rawPath != null) {
      hasPath = true;
    } else {
      rawPath = "";
      hasPath = false;
    }
    
    this.uri = (rawPath + '?' + uri.getRawQuery());
    
    this.charset = charset;
    this.maxParams = maxParams;
  }
  


  public String path()
  {
    if (path == null) {
      if (!hasPath) {
        return this.path = "";
      }
      
      int pathEndPos = uri.indexOf('?');
      if (pathEndPos < 0) {
        path = uri;
      } else {
        return this.path = uri.substring(0, pathEndPos);
      }
    }
    return path;
  }
  


  public Map<String, List<String>> parameters()
  {
    if (params == null) {
      if (hasPath) {
        int pathLength = path().length();
        if (uri.length() == pathLength) {
          return Collections.emptyMap();
        }
        decodeParams(uri.substring(pathLength + 1));
      } else {
        if (uri.isEmpty()) {
          return Collections.emptyMap();
        }
        decodeParams(uri);
      }
    }
    return params;
  }
  
  private void decodeParams(String s) {
    Map<String, List<String>> params = this.params = new LinkedHashMap();
    nParams = 0;
    String name = null;
    int pos = 0;
    

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c == '=') && (name == null)) {
        if (pos != i) {
          name = decodeComponent(s.substring(pos, i), charset);
        }
        pos = i + 1;
      }
      else if ((c == '&') || (c == ';')) {
        if ((name == null) && (pos != i))
        {


          if (addParam(params, decodeComponent(s.substring(pos, i), charset), "")) {}

        }
        else if (name != null) {
          if (!addParam(params, name, decodeComponent(s.substring(pos, i), charset))) {
            return;
          }
          name = null;
        }
        pos = i + 1;
      }
    }
    
    if (pos != i) {
      if (name == null) {
        addParam(params, decodeComponent(s.substring(pos, i), charset), "");
      } else {
        addParam(params, name, decodeComponent(s.substring(pos, i), charset));
      }
    } else if (name != null) {
      addParam(params, name, "");
    }
  }
  
  private boolean addParam(Map<String, List<String>> params, String name, String value) {
    if (nParams >= maxParams) {
      return false;
    }
    
    List<String> values = (List)params.get(name);
    if (values == null) {
      values = new ArrayList(1);
      params.put(name, values);
    }
    values.add(value);
    nParams += 1;
    return true;
  }
  










  public static String decodeComponent(String s)
  {
    return decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
  }
  





















  public static String decodeComponent(String s, Charset charset)
  {
    if (s == null) {
      return "";
    }
    int size = s.length();
    boolean modified = false;
    for (int i = 0; i < size; i++) {
      char c = s.charAt(i);
      if ((c == '%') || (c == '+')) {
        modified = true;
        break;
      }
    }
    if (!modified) {
      return s;
    }
    byte[] buf = new byte[size];
    int pos = 0;
    for (int i = 0; i < size; i++) {
      char c = s.charAt(i);
      switch (c) {
      case '+': 
        buf[(pos++)] = 32;
        break;
      case '%': 
        if (i == size - 1) {
          throw new IllegalArgumentException("unterminated escape sequence at end of string: " + s);
        }
        
        c = s.charAt(++i);
        if (c == '%') {
          buf[(pos++)] = 37;
        }
        else {
          if (i == size - 1) {
            throw new IllegalArgumentException("partial escape sequence at end of string: " + s);
          }
          
          c = decodeHexNibble(c);
          char c2 = decodeHexNibble(s.charAt(++i));
          if ((c == 65535) || (c2 == 65535)) {
            throw new IllegalArgumentException("invalid escape sequence `%" + s.charAt(i - 1) + s.charAt(i) + "' at index " + (i - 2) + " of: " + s);
          }
          


          c = (char)(c * '\020' + c2);
        }
        break;
      default:  buf[(pos++)] = ((byte)c);
      }
      
    }
    return new String(buf, 0, pos, charset);
  }
  






  private static char decodeHexNibble(char c)
  {
    if (('0' <= c) && (c <= '9'))
      return (char)(c - '0');
    if (('a' <= c) && (c <= 'f'))
      return (char)(c - 'a' + 10);
    if (('A' <= c) && (c <= 'F')) {
      return (char)(c - 'A' + 10);
    }
    return 65535;
  }
}
