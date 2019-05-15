package io.netty.handler.codec.http2.internal.hpack;

import io.netty.util.internal.ObjectUtil;

































class HeaderField
{
  static final int HEADER_ENTRY_OVERHEAD = 32;
  final CharSequence name;
  final CharSequence value;
  
  static long sizeOf(CharSequence name, CharSequence value)
  {
    return name.length() + value.length() + 32;
  }
  



  HeaderField(CharSequence name, CharSequence value)
  {
    this.name = ((CharSequence)ObjectUtil.checkNotNull(name, "name"));
    this.value = ((CharSequence)ObjectUtil.checkNotNull(value, "value"));
  }
  
  int size() {
    return name.length() + value.length() + 32;
  }
  

  public int hashCode()
  {
    return super.hashCode();
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof HeaderField)) {
      return false;
    }
    HeaderField other = (HeaderField)obj;
    
    return (HpackUtil.equalsConstantTime(name, name) & HpackUtil.equalsConstantTime(value, value)) != 0;
  }
  
  public String toString()
  {
    return name + ": " + value;
  }
}
