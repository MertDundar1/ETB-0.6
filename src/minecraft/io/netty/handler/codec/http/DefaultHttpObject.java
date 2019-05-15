package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;















public class DefaultHttpObject
  implements HttpObject
{
  private DecoderResult decoderResult = DecoderResult.SUCCESS;
  

  protected DefaultHttpObject() {}
  

  public DecoderResult getDecoderResult()
  {
    return decoderResult;
  }
  
  public void setDecoderResult(DecoderResult decoderResult)
  {
    if (decoderResult == null) {
      throw new NullPointerException("decoderResult");
    }
    this.decoderResult = decoderResult;
  }
}
