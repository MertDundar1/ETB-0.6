package io.netty.handler.codec.stomp;

import io.netty.handler.codec.DecoderResult;


















public class DefaultStompHeadersSubframe
  implements StompHeadersSubframe
{
  protected final StompCommand command;
  protected DecoderResult decoderResult = DecoderResult.SUCCESS;
  protected final StompHeaders headers = new DefaultStompHeaders();
  
  public DefaultStompHeadersSubframe(StompCommand command) {
    if (command == null) {
      throw new NullPointerException("command");
    }
    this.command = command;
  }
  
  public StompCommand command()
  {
    return command;
  }
  
  public StompHeaders headers()
  {
    return headers;
  }
  
  public DecoderResult decoderResult()
  {
    return decoderResult;
  }
  
  public void setDecoderResult(DecoderResult decoderResult)
  {
    this.decoderResult = decoderResult;
  }
  
  public String toString()
  {
    return "StompFrame{command=" + command + ", headers=" + headers + '}';
  }
}
