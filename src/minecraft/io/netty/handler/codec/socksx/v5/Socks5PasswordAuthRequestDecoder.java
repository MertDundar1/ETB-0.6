package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;























public class Socks5PasswordAuthRequestDecoder
  extends ReplayingDecoder<State>
{
  static enum State
  {
    INIT, 
    SUCCESS, 
    FAILURE;
    
    private State() {} }
  
  public Socks5PasswordAuthRequestDecoder() { super(State.INIT); }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    try
    {
      switch (1.$SwitchMap$io$netty$handler$codec$socksx$v5$Socks5PasswordAuthRequestDecoder$State[((State)state()).ordinal()]) {
      case 1: 
        int startOffset = in.readerIndex();
        byte version = in.getByte(startOffset);
        if (version != 1) {
          throw new DecoderException("unsupported subnegotiation version: " + version + " (expected: 1)");
        }
        
        int usernameLength = in.getUnsignedByte(startOffset + 1);
        int passwordLength = in.getUnsignedByte(startOffset + 2 + usernameLength);
        int totalLength = usernameLength + passwordLength + 3;
        
        in.skipBytes(totalLength);
        out.add(new DefaultSocks5PasswordAuthRequest(in.toString(startOffset + 2, usernameLength, CharsetUtil.US_ASCII), in.toString(startOffset + 3 + usernameLength, passwordLength, CharsetUtil.US_ASCII)));
        


        checkpoint(State.SUCCESS);
      
      case 2: 
        int readableBytes = actualReadableBytes();
        if (readableBytes > 0) {
          out.add(in.readRetainedSlice(readableBytes));
        }
        
        break;
      case 3: 
        in.skipBytes(actualReadableBytes());
      }
    }
    catch (Exception e)
    {
      fail(out, e);
    }
  }
  
  private void fail(List<Object> out, Throwable cause) {
    if (!(cause instanceof DecoderException)) {
      cause = new DecoderException(cause);
    }
    
    checkpoint(State.FAILURE);
    
    Socks5Message m = new DefaultSocks5PasswordAuthRequest("", "");
    m.setDecoderResult(DecoderResult.failure(cause));
    out.add(m);
  }
}
