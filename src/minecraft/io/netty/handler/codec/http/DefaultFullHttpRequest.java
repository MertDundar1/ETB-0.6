package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;















public class DefaultFullHttpRequest
  extends DefaultHttpRequest
  implements FullHttpRequest
{
  private final ByteBuf content;
  private final HttpHeaders trailingHeader;
  private final boolean validateHeaders;
  
  public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri)
  {
    this(httpVersion, method, uri, Unpooled.buffer(0));
  }
  
  public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
    this(httpVersion, method, uri, content, true);
  }
  
  public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, boolean validateHeaders)
  {
    super(httpVersion, method, uri, validateHeaders);
    if (content == null) {
      throw new NullPointerException("content");
    }
    this.content = content;
    trailingHeader = new DefaultHttpHeaders(validateHeaders);
    this.validateHeaders = validateHeaders;
  }
  
  public HttpHeaders trailingHeaders()
  {
    return trailingHeader;
  }
  
  public ByteBuf content()
  {
    return content;
  }
  
  public int refCnt()
  {
    return content.refCnt();
  }
  
  public FullHttpRequest retain()
  {
    content.retain();
    return this;
  }
  
  public FullHttpRequest retain(int increment)
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
  
  public FullHttpRequest setProtocolVersion(HttpVersion version)
  {
    super.setProtocolVersion(version);
    return this;
  }
  
  public FullHttpRequest setMethod(HttpMethod method)
  {
    super.setMethod(method);
    return this;
  }
  
  public FullHttpRequest setUri(String uri)
  {
    super.setUri(uri);
    return this;
  }
  
  public FullHttpRequest copy()
  {
    DefaultFullHttpRequest copy = new DefaultFullHttpRequest(getProtocolVersion(), getMethod(), getUri(), content().copy(), validateHeaders);
    
    copy.headers().set(headers());
    copy.trailingHeaders().set(trailingHeaders());
    return copy;
  }
  
  public FullHttpRequest duplicate()
  {
    DefaultFullHttpRequest duplicate = new DefaultFullHttpRequest(getProtocolVersion(), getMethod(), getUri(), content().duplicate(), validateHeaders);
    
    duplicate.headers().set(headers());
    duplicate.trailingHeaders().set(trailingHeaders());
    return duplicate;
  }
}
