package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import java.util.List;

























public final class RedisDecoder
  extends ByteToMessageDecoder
{
  private final ToPositiveLongProcessor toPositiveLongProcessor = new ToPositiveLongProcessor(null);
  
  private final int maxInlineMessageLength;
  
  private final RedisMessagePool messagePool;
  
  private State state = State.DECODE_TYPE;
  private RedisMessageType type;
  private int remainingBulkLength;
  
  private static enum State {
    DECODE_TYPE, 
    DECODE_INLINE, 
    DECODE_LENGTH, 
    DECODE_BULK_STRING_EOL, 
    DECODE_BULK_STRING_CONTENT;
    

    private State() {}
  }
  
  public RedisDecoder()
  {
    this(65536, FixedRedisMessagePool.INSTANCE);
  }
  




  public RedisDecoder(int maxInlineMessageLength, RedisMessagePool messagePool)
  {
    if ((maxInlineMessageLength <= 0) || (maxInlineMessageLength > 536870912)) {
      throw new RedisCodecException("maxInlineMessageLength: " + maxInlineMessageLength + " (expected: <= " + 536870912 + ")");
    }
    
    this.maxInlineMessageLength = maxInlineMessageLength;
    this.messagePool = messagePool;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    try {
      do {
        do {
          do { do { do { switch (1.$SwitchMap$io$netty$handler$codec$redis$RedisDecoder$State[state.ordinal()]) {
                }
              } while (decodeType(in));
              return;


            }
            while (decodeInline(in, out));
            return;


          }
          while (decodeLength(in, out));
          return;


        }
        while (decodeBulkStringEndOfLine(in, out));
        return;


      }
      while (decodeBulkStringContent(in, out));
      return;
      


      throw new RedisCodecException("Unknown state: " + state);
    }
    catch (RedisCodecException e)
    {
      resetDecoder();
      throw e;
    } catch (Exception e) {
      resetDecoder();
      throw new RedisCodecException(e);
    }
  }
  
  private void resetDecoder() {
    state = State.DECODE_TYPE;
    remainingBulkLength = 0;
  }
  
  private boolean decodeType(ByteBuf in) throws Exception {
    if (!in.isReadable()) {
      return false;
    }
    type = RedisMessageType.valueOf(in.readByte());
    state = (type.isInline() ? State.DECODE_INLINE : State.DECODE_LENGTH);
    return true;
  }
  
  private boolean decodeInline(ByteBuf in, List<Object> out) throws Exception {
    ByteBuf lineBytes = readLine(in);
    if (lineBytes == null) {
      if (in.readableBytes() > maxInlineMessageLength) {
        throw new RedisCodecException("length: " + in.readableBytes() + " (expected: <= " + maxInlineMessageLength + ")");
      }
      
      return false;
    }
    out.add(newInlineRedisMessage(type, lineBytes));
    resetDecoder();
    return true;
  }
  
  private boolean decodeLength(ByteBuf in, List<Object> out) throws Exception {
    ByteBuf lineByteBuf = readLine(in);
    if (lineByteBuf == null) {
      return false;
    }
    long length = parseRedisNumber(lineByteBuf);
    if (length < -1L) {
      throw new RedisCodecException("length: " + length + " (expected: >= " + -1 + ")");
    }
    switch (type) {
    case ARRAY_HEADER: 
      out.add(new ArrayHeaderRedisMessage(length));
      resetDecoder();
      return true;
    case BULK_STRING: 
      if (length > 536870912L) {
        throw new RedisCodecException("length: " + length + " (expected: <= " + 536870912 + ")");
      }
      
      remainingBulkLength = ((int)length);
      return decodeBulkString(in, out);
    }
    throw new RedisCodecException("bad type: " + type);
  }
  
  private boolean decodeBulkString(ByteBuf in, List<Object> out) throws Exception
  {
    switch (remainingBulkLength) {
    case -1: 
      out.add(FullBulkStringRedisMessage.NULL_INSTANCE);
      resetDecoder();
      return true;
    case 0: 
      state = State.DECODE_BULK_STRING_EOL;
      return decodeBulkStringEndOfLine(in, out);
    }
    out.add(new BulkStringHeaderRedisMessage(remainingBulkLength));
    state = State.DECODE_BULK_STRING_CONTENT;
    return decodeBulkStringContent(in, out);
  }
  
  private boolean decodeBulkStringEndOfLine(ByteBuf in, List<Object> out)
    throws Exception
  {
    if (in.readableBytes() < 2) {
      return false;
    }
    readEndOfLine(in);
    out.add(FullBulkStringRedisMessage.EMPTY_INSTANCE);
    resetDecoder();
    return true;
  }
  
  private boolean decodeBulkStringContent(ByteBuf in, List<Object> out) throws Exception
  {
    int readableBytes = in.readableBytes();
    if (readableBytes == 0) {
      return false;
    }
    

    if (readableBytes >= remainingBulkLength + 2) {
      ByteBuf content = in.readSlice(remainingBulkLength);
      readEndOfLine(in);
      
      out.add(new DefaultLastBulkStringRedisContent(content.retain()));
      resetDecoder();
      return true;
    }
    

    int toRead = Math.min(remainingBulkLength, readableBytes);
    remainingBulkLength -= toRead;
    out.add(new DefaultBulkStringRedisContent(in.readSlice(toRead).retain()));
    return true;
  }
  
  private static void readEndOfLine(ByteBuf in) {
    short delim = in.readShort();
    if (RedisConstants.EOL_SHORT == delim) {
      return;
    }
    byte[] bytes = RedisCodecUtil.shortToBytes(delim);
    throw new RedisCodecException("delimiter: [" + bytes[0] + "," + bytes[1] + "] (expected: \\r\\n)");
  }
  
  private RedisMessage newInlineRedisMessage(RedisMessageType messageType, ByteBuf content) {
    switch (1.$SwitchMap$io$netty$handler$codec$redis$RedisMessageType[messageType.ordinal()]) {
    case 3: 
      SimpleStringRedisMessage cached = messagePool.getSimpleString(content);
      return cached != null ? cached : new SimpleStringRedisMessage(content.toString(CharsetUtil.UTF_8));
    
    case 4: 
      ErrorRedisMessage cached = messagePool.getError(content);
      return cached != null ? cached : new ErrorRedisMessage(content.toString(CharsetUtil.UTF_8));
    
    case 5: 
      IntegerRedisMessage cached = messagePool.getInteger(content);
      return cached != null ? cached : new IntegerRedisMessage(parseRedisNumber(content));
    }
    
    throw new RedisCodecException("bad type: " + messageType);
  }
  
  private static ByteBuf readLine(ByteBuf in)
  {
    if (!in.isReadable(2)) {
      return null;
    }
    int lfIndex = in.forEachByte(ByteProcessor.FIND_LF);
    if (lfIndex < 0) {
      return null;
    }
    ByteBuf data = in.readSlice(lfIndex - in.readerIndex() - 1);
    readEndOfLine(in);
    return data;
  }
  
  private long parseRedisNumber(ByteBuf byteBuf) {
    int readableBytes = byteBuf.readableBytes();
    boolean negative = (readableBytes > 0) && (byteBuf.getByte(byteBuf.readerIndex()) == 45);
    int extraOneByteForNegative = negative ? 1 : 0;
    if (readableBytes <= extraOneByteForNegative) {
      throw new RedisCodecException("no number to parse: " + byteBuf.toString(CharsetUtil.US_ASCII));
    }
    if (readableBytes > 19 + extraOneByteForNegative) {
      throw new RedisCodecException("too many characters to be a valid RESP Integer: " + byteBuf.toString(CharsetUtil.US_ASCII));
    }
    
    if (negative) {
      return -parsePositiveNumber(byteBuf.skipBytes(extraOneByteForNegative));
    }
    return parsePositiveNumber(byteBuf);
  }
  
  private long parsePositiveNumber(ByteBuf byteBuf) {
    toPositiveLongProcessor.reset();
    byteBuf.forEachByte(toPositiveLongProcessor);
    return toPositiveLongProcessor.content();
  }
  
  private static final class ToPositiveLongProcessor implements ByteProcessor {
    private long result;
    
    private ToPositiveLongProcessor() {}
    
    public boolean process(byte value) throws Exception { if ((value < 48) || (value > 57)) {
        throw new RedisCodecException("bad byte in number: " + value);
      }
      result = (result * 10L + (value - 48));
      return true;
    }
    
    public long content() {
      return result;
    }
    
    public void reset() {
      result = 0L;
    }
  }
}
