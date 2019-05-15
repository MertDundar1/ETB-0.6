package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.StringUtil;


















public class DefaultByteBufHolder
  implements ByteBufHolder
{
  private final ByteBuf data;
  
  public DefaultByteBufHolder(ByteBuf data)
  {
    if (data == null) {
      throw new NullPointerException("data");
    }
    this.data = data;
  }
  
  public ByteBuf content()
  {
    if (data.refCnt() <= 0) {
      throw new IllegalReferenceCountException(data.refCnt());
    }
    return data;
  }
  
  public ByteBufHolder copy()
  {
    return new DefaultByteBufHolder(data.copy());
  }
  
  public ByteBufHolder duplicate()
  {
    return new DefaultByteBufHolder(data.duplicate());
  }
  
  public int refCnt()
  {
    return data.refCnt();
  }
  
  public ByteBufHolder retain()
  {
    data.retain();
    return this;
  }
  
  public ByteBufHolder retain(int increment)
  {
    data.retain(increment);
    return this;
  }
  
  public boolean release()
  {
    return data.release();
  }
  
  public boolean release(int decrement)
  {
    return data.release(decrement);
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '(' + content().toString() + ')';
  }
}
