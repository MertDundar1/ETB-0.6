package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.internal.hpack.Decoder;
import io.netty.util.internal.ObjectUtil;
























public class DefaultHttp2HeadersDecoder
  implements Http2HeadersDecoder, Http2HeadersDecoder.Configuration
{
  private static final float HEADERS_COUNT_WEIGHT_NEW = 0.2F;
  private static final float HEADERS_COUNT_WEIGHT_HISTORICAL = 0.8F;
  private final Decoder decoder;
  private final Http2HeaderTable headerTable;
  private final boolean validateHeaders;
  private float headerArraySizeAccumulator = 8.0F;
  
  public DefaultHttp2HeadersDecoder() {
    this(true);
  }
  
  public DefaultHttp2HeadersDecoder(boolean validateHeaders) {
    this(validateHeaders, new Decoder());
  }
  
  public DefaultHttp2HeadersDecoder(boolean validateHeaders, int initialHuffmanDecodeCapacity) {
    this(validateHeaders, new Decoder(initialHuffmanDecodeCapacity));
  }
  



  DefaultHttp2HeadersDecoder(boolean validateHeaders, Decoder decoder)
  {
    this.decoder = ((Decoder)ObjectUtil.checkNotNull(decoder, "decoder"));
    headerTable = new Http2HeaderTableDecoder(null);
    this.validateHeaders = validateHeaders;
  }
  
  public Http2HeaderTable headerTable()
  {
    return headerTable;
  }
  
  public Http2HeadersDecoder.Configuration configuration()
  {
    return this;
  }
  
  public Http2Headers decodeHeaders(int streamId, ByteBuf headerBlock) throws Http2Exception
  {
    try {
      Http2Headers headers = new DefaultHttp2Headers(validateHeaders, (int)headerArraySizeAccumulator);
      decoder.decode(streamId, headerBlock, headers);
      headerArraySizeAccumulator = (0.2F * headers.size() + 0.8F * headerArraySizeAccumulator);
      
      return headers;
    } catch (Http2Exception e) {
      throw e;

    }
    catch (Throwable e)
    {
      throw Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, e, e.getMessage(), new Object[0]);
    }
  }
  
  private final class Http2HeaderTableDecoder implements Http2HeaderTable
  {
    private Http2HeaderTableDecoder() {}
    
    public void maxHeaderTableSize(long max) throws Http2Exception
    {
      decoder.setMaxHeaderTableSize(max);
    }
    
    public long maxHeaderTableSize()
    {
      return decoder.getMaxHeaderTableSize();
    }
    
    public void maxHeaderListSize(long max) throws Http2Exception
    {
      decoder.setMaxHeaderListSize(max);
    }
    
    public long maxHeaderListSize()
    {
      return decoder.getMaxHeaderListSize();
    }
  }
}
