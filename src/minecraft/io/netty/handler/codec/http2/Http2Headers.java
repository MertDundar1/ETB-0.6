package io.netty.handler.codec.http2;

import io.netty.handler.codec.Headers;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.Map.Entry;




public abstract interface Http2Headers
  extends Headers<CharSequence, CharSequence, Http2Headers>
{
  public abstract Iterator<Map.Entry<CharSequence, CharSequence>> iterator();
  
  public abstract Http2Headers method(CharSequence paramCharSequence);
  
  public abstract Http2Headers scheme(CharSequence paramCharSequence);
  
  public abstract Http2Headers authority(CharSequence paramCharSequence);
  
  public abstract Http2Headers path(CharSequence paramCharSequence);
  
  public abstract Http2Headers status(CharSequence paramCharSequence);
  
  public abstract CharSequence method();
  
  public abstract CharSequence scheme();
  
  public abstract CharSequence authority();
  
  public abstract CharSequence path();
  
  public abstract CharSequence status();
  
  public static enum PseudoHeaderName
  {
    METHOD(":method"), 
    



    SCHEME(":scheme"), 
    



    AUTHORITY(":authority"), 
    



    PATH(":path"), 
    



    STATUS(":status");
    
    static {
      PSEUDO_HEADERS = new CharSequenceMap();
      
      for (PseudoHeaderName pseudoHeader : values()) {
        PSEUDO_HEADERS.add(pseudoHeader.value(), AsciiString.EMPTY_STRING);
      }
    }
    
    private PseudoHeaderName(String value) {
      this.value = new AsciiString(value);
    }
    
    public AsciiString value()
    {
      return value;
    }
    
    private final AsciiString value;
    private static final CharSequenceMap<AsciiString> PSEUDO_HEADERS;
    public static boolean isPseudoHeader(CharSequence header)
    {
      return PSEUDO_HEADERS.contains(header);
    }
  }
}
