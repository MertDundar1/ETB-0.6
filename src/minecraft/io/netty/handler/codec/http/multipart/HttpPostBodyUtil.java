package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;













































final class HttpPostBodyUtil
{
  public static final int chunkSize = 8096;
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String NAME = "name";
  public static final String FILENAME = "filename";
  public static final String FORM_DATA = "form-data";
  public static final String ATTACHMENT = "attachment";
  public static final String FILE = "file";
  public static final String MULTIPART_MIXED = "multipart/mixed";
  public static final Charset ISO_8859_1 = CharsetUtil.ISO_8859_1;
  



  public static final Charset US_ASCII = CharsetUtil.US_ASCII;
  




  public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
  



  public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";
  



  private HttpPostBodyUtil() {}
  




  public static enum TransferEncodingMechanism
  {
    BIT7("7bit"), 
    


    BIT8("8bit"), 
    


    BINARY("binary");
    
    private final String value;
    
    private TransferEncodingMechanism(String value) {
      this.value = value;
    }
    
    private TransferEncodingMechanism() {
      value = name();
    }
    
    public String value() {
      return value;
    }
    
    public String toString()
    {
      return value;
    }
  }
  

  static class SeekAheadNoBackArrayException
    extends Exception
  {
    private static final long serialVersionUID = -630418804938699495L;
    

    SeekAheadNoBackArrayException() {}
  }
  

  static class SeekAheadOptimize
  {
    byte[] bytes;
    int readerIndex;
    int pos;
    int origPos;
    int limit;
    ByteBuf buffer;
    
    SeekAheadOptimize(ByteBuf buffer)
      throws HttpPostBodyUtil.SeekAheadNoBackArrayException
    {
      if (!buffer.hasArray()) {
        throw new HttpPostBodyUtil.SeekAheadNoBackArrayException();
      }
      this.buffer = buffer;
      bytes = buffer.array();
      readerIndex = buffer.readerIndex();
      origPos = (this.pos = buffer.arrayOffset() + readerIndex);
      limit = (buffer.arrayOffset() + buffer.writerIndex());
    }
    




    void setReadPosition(int minus)
    {
      pos -= minus;
      readerIndex = getReadPosition(pos);
      buffer.readerIndex(readerIndex);
    }
    




    int getReadPosition(int index)
    {
      return index - origPos + readerIndex;
    }
    
    void clear() {
      buffer = null;
      bytes = null;
      limit = 0;
      pos = 0;
      readerIndex = 0;
    }
  }
  




  static int findNonWhitespace(String sb, int offset)
  {
    for (int result = offset; result < sb.length(); result++) {
      if (!Character.isWhitespace(sb.charAt(result))) {
        break;
      }
    }
    return result;
  }
  




  static int findWhitespace(String sb, int offset)
  {
    for (int result = offset; result < sb.length(); result++) {
      if (Character.isWhitespace(sb.charAt(result))) {
        break;
      }
    }
    return result;
  }
  




  static int findEndOfString(String sb)
  {
    for (int result = sb.length(); result > 0; result--) {
      if (!Character.isWhitespace(sb.charAt(result - 1))) {
        break;
      }
    }
    return result;
  }
}
