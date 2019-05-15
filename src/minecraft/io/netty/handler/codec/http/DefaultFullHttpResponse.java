package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

















public class DefaultFullHttpResponse
  extends DefaultHttpResponse
  implements FullHttpResponse
{
  private final ByteBuf content;
  private final HttpHeaders trailingHeaders;
  private final boolean validateHeaders;
  
  public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status)
  {
    this(version, status, Unpooled.buffer(0));
  }
  
  public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
    this(version, status, content, true);
  }
  
  public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders)
  {
    super(version, status, validateHeaders);
    if (content == null) {
      throw new NullPointerException("content");
    }
    this.content = content;
    trailingHeaders = new DefaultHttpHeaders(validateHeaders);
    this.validateHeaders = validateHeaders;
  }
  
  public HttpHeaders trailingHeaders()
  {
    return trailingHeaders;
  }
  
  public ByteBuf content()
  {
    return content;
  }
  
  public int refCnt()
  {
    return content.refCnt();
  }
  
  public FullHttpResponse retain()
  {
    content.retain();
    return this;
  }
  
  public FullHttpResponse retain(int increment)
  {
    content.retain(increment);
    return this;
  }
  
  public boolean release()
  {
    return content.release();
  }
  
  public boolean release(int decrement)
  {
    return content.release(decrement);
  }
  
  public FullHttpResponse setProtocolVersion(HttpVersion version)
  {
    super.setProtocolVersion(version);
    return this;
  }
  
  public FullHttpResponse setStatus(HttpResponseStatus status)
  {
    super.setStatus(status);
    return this;
  }
  
  public FullHttpResponse copy()
  {
    DefaultFullHttpResponse copy = new DefaultFullHttpResponse(getProtocolVersion(), getStatus(), content().copy(), validateHeaders);
    
    copy.headers().set(headers());
    copy.trailingHeaders().set(trailingHeaders());
    return copy;
  }
  
  public FullHttpResponse duplicate()
  {
    DefaultFullHttpResponse duplicate = new DefaultFullHttpResponse(getProtocolVersion(), getStatus(), content().duplicate(), validateHeaders);
    
    duplicate.headers().set(headers());
    duplicate.trailingHeaders().set(trailingHeaders());
    return duplicate;
  }
}
