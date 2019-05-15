package io.netty.handler.codec.redis;






















public enum RedisMessageType
{
  SIMPLE_STRING((byte)43, true), 
  ERROR((byte)45, true), 
  INTEGER((byte)58, true), 
  BULK_STRING((byte)36, false), 
  ARRAY_HEADER((byte)42, false), 
  ARRAY((byte)42, false);
  
  private final byte value;
  private final boolean inline;
  
  private RedisMessageType(byte value, boolean inline) {
    this.value = value;
    this.inline = inline;
  }
  


  public byte value()
  {
    return value;
  }
  



  public boolean isInline()
  {
    return inline;
  }
  


  public static RedisMessageType valueOf(byte value)
  {
    switch (value) {
    case 43: 
      return SIMPLE_STRING;
    case 45: 
      return ERROR;
    case 58: 
      return INTEGER;
    case 36: 
      return BULK_STRING;
    case 42: 
      return ARRAY_HEADER;
    }
    throw new RedisCodecException("Unknown RedisMessageType: " + value);
  }
}
