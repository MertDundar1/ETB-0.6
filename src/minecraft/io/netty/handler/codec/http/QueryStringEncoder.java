package io.netty.handler.codec.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;



























public class QueryStringEncoder
{
  private final Charset charset;
  private final String uri;
  private final List<Param> params = new ArrayList();
  



  public QueryStringEncoder(String uri)
  {
    this(uri, HttpConstants.DEFAULT_CHARSET);
  }
  



  public QueryStringEncoder(String uri, Charset charset)
  {
    if (uri == null) {
      throw new NullPointerException("getUri");
    }
    if (charset == null) {
      throw new NullPointerException("charset");
    }
    
    this.uri = uri;
    this.charset = charset;
  }
  


  public void addParam(String name, String value)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    params.add(new Param(name, value));
  }
  



  public URI toUri()
    throws URISyntaxException
  {
    return new URI(toString());
  }
  





  public String toString()
  {
    if (params.isEmpty()) {
      return uri;
    }
    StringBuilder sb = new StringBuilder(uri).append('?');
    for (int i = 0; i < params.size(); i++) {
      Param param = (Param)params.get(i);
      sb.append(encodeComponent(name, charset));
      if (value != null) {
        sb.append('=');
        sb.append(encodeComponent(value, charset));
      }
      if (i != params.size() - 1) {
        sb.append('&');
      }
    }
    return sb.toString();
  }
  
  private static String encodeComponent(String s, Charset charset)
  {
    try
    {
      return URLEncoder.encode(s, charset.name()).replace("+", "%20");
    } catch (UnsupportedEncodingException ignored) {
      throw new UnsupportedCharsetException(charset.name());
    }
  }
  
  private static final class Param
  {
    final String name;
    final String value;
    
    Param(String name, String value) {
      this.value = value;
      this.name = name;
    }
  }
}
