package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.HttpHeaderDateFormat;
import io.netty.util.internal.ObjectUtil;
import java.text.ParsePosition;
import java.util.Date;





























public final class ClientCookieDecoder
  extends CookieDecoder
{
  public static final ClientCookieDecoder STRICT = new ClientCookieDecoder(true);
  



  public static final ClientCookieDecoder LAX = new ClientCookieDecoder(false);
  
  private ClientCookieDecoder(boolean strict) {
    super(strict);
  }
  




  public Cookie decode(String header)
  {
    int headerLen = ((String)ObjectUtil.checkNotNull(header, "header")).length();
    
    if (headerLen == 0) {
      return null;
    }
    
    CookieBuilder cookieBuilder = null;
    
    int i = 0;
    


    while (i != headerLen)
    {

      char c = header.charAt(i);
      if (c == ',') {
        break;
      }
      

      if ((c == '\t') || (c == '\n') || (c == '\013') || (c == '\f') || (c == '\r') || (c == ' ') || (c == ';'))
      {
        i++;

      }
      else
      {

        int nameBegin = i;
        int nameEnd = i;
        int valueBegin = -1;
        int valueEnd = -1;
        
        if (i != headerLen) {
          for (;;)
          {
            char curChar = header.charAt(i);
            if (curChar == ';')
            {
              nameEnd = i;
              valueBegin = valueEnd = -1;
              break;
            }
            if (curChar == '=')
            {
              nameEnd = i;
              i++;
              if (i == headerLen)
              {
                valueBegin = valueEnd = 0;
                break;
              }
              
              valueBegin = i;
              
              int semiPos = header.indexOf(';', i);
              valueEnd = i = semiPos > 0 ? semiPos : headerLen;
              break;
            }
            i++;
            

            if (i == headerLen)
            {
              nameEnd = headerLen;
              valueBegin = valueEnd = -1;
              break;
            }
          }
        }
        
        if ((valueEnd > 0) && (header.charAt(valueEnd - 1) == ','))
        {
          valueEnd--;
        }
        
        if (cookieBuilder == null)
        {
          DefaultCookie cookie = initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
          
          if (cookie == null) {
            return null;
          }
          
          cookieBuilder = new CookieBuilder(cookie, header);
        }
        else {
          cookieBuilder.appendAttribute(nameBegin, nameEnd, valueBegin, valueEnd);
        }
      } }
    return cookieBuilder.cookie();
  }
  
  private static class CookieBuilder
  {
    private final String header;
    private final DefaultCookie cookie;
    private String domain;
    private String path;
    private long maxAge = Long.MIN_VALUE;
    private int expiresStart;
    private int expiresEnd;
    private boolean secure;
    private boolean httpOnly;
    
    public CookieBuilder(DefaultCookie cookie, String header) {
      this.cookie = cookie;
      this.header = header;
    }
    
    private long mergeMaxAgeAndExpires()
    {
      if (maxAge != Long.MIN_VALUE) {
        return maxAge;
      }
      String expires = computeValue(expiresStart, expiresEnd);
      if (expires != null) {
        Date expiresDate = HttpHeaderDateFormat.get().parse(expires, new ParsePosition(0));
        if (expiresDate != null) {
          long maxAgeMillis = expiresDate.getTime() - System.currentTimeMillis();
          return maxAgeMillis / 1000L + (maxAgeMillis % 1000L != 0L ? 1 : 0);
        }
      }
      
      return Long.MIN_VALUE;
    }
    
    public Cookie cookie() {
      cookie.setDomain(domain);
      cookie.setPath(path);
      cookie.setMaxAge(mergeMaxAgeAndExpires());
      cookie.setSecure(secure);
      cookie.setHttpOnly(httpOnly);
      return cookie;
    }
    












    public void appendAttribute(int keyStart, int keyEnd, int valueStart, int valueEnd)
    {
      int length = keyEnd - keyStart;
      
      if (length == 4) {
        parse4(keyStart, valueStart, valueEnd);
      } else if (length == 6) {
        parse6(keyStart, valueStart, valueEnd);
      } else if (length == 7) {
        parse7(keyStart, valueStart, valueEnd);
      } else if (length == 8) {
        parse8(keyStart, valueStart, valueEnd);
      }
    }
    
    private void parse4(int nameStart, int valueStart, int valueEnd) {
      if (header.regionMatches(true, nameStart, "Path", 0, 4)) {
        path = computeValue(valueStart, valueEnd);
      }
    }
    
    private void parse6(int nameStart, int valueStart, int valueEnd) {
      if (header.regionMatches(true, nameStart, "Domain", 0, 5)) {
        domain = computeValue(valueStart, valueEnd);
      } else if (header.regionMatches(true, nameStart, "Secure", 0, 5)) {
        secure = true;
      }
    }
    
    private void setMaxAge(String value) {
      try {
        maxAge = Math.max(Long.parseLong(value), 0L);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    
    private void parse7(int nameStart, int valueStart, int valueEnd)
    {
      if (header.regionMatches(true, nameStart, "Expires", 0, 7)) {
        expiresStart = valueStart;
        expiresEnd = valueEnd;
      } else if (header.regionMatches(true, nameStart, "Max-Age", 0, 7)) {
        setMaxAge(computeValue(valueStart, valueEnd));
      }
    }
    
    private void parse8(int nameStart, int valueStart, int valueEnd) {
      if (header.regionMatches(true, nameStart, "HTTPOnly", 0, 8)) {
        httpOnly = true;
      }
    }
    
    private String computeValue(int valueStart, int valueEnd) {
      return (valueStart == -1) || (valueStart == valueEnd) ? null : header.substring(valueStart, valueEnd);
    }
  }
}
