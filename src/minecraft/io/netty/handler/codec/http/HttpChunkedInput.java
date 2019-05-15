package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;






































public class HttpChunkedInput
  implements ChunkedInput<HttpContent>
{
  private final ChunkedInput<ByteBuf> input;
  private final LastHttpContent lastHttpContent;
  private boolean sentLastChunk;
  
  public HttpChunkedInput(ChunkedInput<ByteBuf> input)
  {
    this.input = input;
    lastHttpContent = LastHttpContent.EMPTY_LAST_CONTENT;
  }
  






  public HttpChunkedInput(ChunkedInput<ByteBuf> input, LastHttpContent lastHttpContent)
  {
    this.input = input;
    this.lastHttpContent = lastHttpContent;
  }
  
  public boolean isEndOfInput() throws Exception
  {
    if (input.isEndOfInput())
    {
      return sentLastChunk;
    }
    return false;
  }
  
  public void close()
    throws Exception
  {
    input.close();
  }
  
  public HttpContent readChunk(ChannelHandlerContext ctx) throws Exception
  {
    if (input.isEndOfInput()) {
      if (sentLastChunk) {
        return null;
      }
      
      sentLastChunk = true;
      return lastHttpContent;
    }
    
    ByteBuf buf = (ByteBuf)input.readChunk(ctx);
    return new DefaultHttpContent(buf);
  }
}
