package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;



















public class DefaultHttpContent
  extends DefaultHttpObject
  implements HttpContent
{
  private final ByteBuf content;
  
  public DefaultHttpContent(ByteBuf content)
  {
    if (content == null) {
      throw new NullPointerException("content");
    }
    this.content = content;
  }
  
  public ByteBuf content()
  {
    return content;
  }
  
  public HttpContent copy()
  {
    return new DefaultHttpContent(content.copy());
  }
  
  public HttpContent duplicate()
  {
    return new DefaultHttpContent(content.duplicate());
  }
  
  public int refCnt()
  {
    return content.refCnt();
  }
  
  public HttpContent retain()
  {
    content.retain();
    return this;
  }
  
  public HttpContent retain(int increment)
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
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + "(data: " + content() + ", decoderResult: " + getDecoderResult() + ')';
  }
}
