package io.netty.handler.codec.socksx;

import io.netty.handler.codec.DecoderResult;



















public abstract class AbstractSocksMessage
  implements SocksMessage
{
  private DecoderResult decoderResult = DecoderResult.SUCCESS;
  
  public AbstractSocksMessage() {}
  
  public DecoderResult decoderResult() { return decoderResult; }
  

  public void setDecoderResult(DecoderResult decoderResult)
  {
    if (decoderResult == null) {
      throw new NullPointerException("decoderResult");
    }
    this.decoderResult = decoderResult;
  }
}
