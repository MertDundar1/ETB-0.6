package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http2.internal.hpack.Encoder;
import io.netty.util.internal.ObjectUtil;



















public class DefaultHttp2HeadersEncoder
  implements Http2HeadersEncoder, Http2HeadersEncoder.Configuration
{
  private final Encoder encoder;
  private final Http2HeadersEncoder.SensitivityDetector sensitivityDetector;
  private final Http2HeaderTable headerTable;
  private final ByteBuf tableSizeChangeOutput = Unpooled.buffer();
  
  public DefaultHttp2HeadersEncoder() {
    this(NEVER_SENSITIVE);
  }
  
  public DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector sensitivityDetector) {
    this(sensitivityDetector, new Encoder());
  }
  
  public DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector sensitivityDetector, boolean ignoreMaxHeaderListSize) {
    this(sensitivityDetector, new Encoder(ignoreMaxHeaderListSize));
  }
  
  public DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector sensitivityDetector, boolean ignoreMaxHeaderListSize, int dynamicTableArraySizeHint)
  {
    this(sensitivityDetector, new Encoder(ignoreMaxHeaderListSize, dynamicTableArraySizeHint));
  }
  



  DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector sensitivityDetector, Encoder encoder)
  {
    this.sensitivityDetector = ((Http2HeadersEncoder.SensitivityDetector)ObjectUtil.checkNotNull(sensitivityDetector, "sensitiveDetector"));
    this.encoder = ((Encoder)ObjectUtil.checkNotNull(encoder, "encoder"));
    headerTable = new Http2HeaderTableEncoder(null);
  }
  
  public void encodeHeaders(Http2Headers headers, ByteBuf buffer)
    throws Http2Exception
  {
    try
    {
      if (tableSizeChangeOutput.isReadable()) {
        buffer.writeBytes(tableSizeChangeOutput);
        tableSizeChangeOutput.clear();
      }
      
      encoder.encodeHeaders(buffer, headers, sensitivityDetector);
    } catch (Http2Exception e) {
      throw e;
    } catch (Throwable t) {
      throw Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, t, "Failed encoding headers block: %s", new Object[] { t.getMessage() });
    }
  }
  
  public Http2HeaderTable headerTable()
  {
    return headerTable;
  }
  
  public Http2HeadersEncoder.Configuration configuration()
  {
    return this;
  }
  
  private final class Http2HeaderTableEncoder implements Http2HeaderTable
  {
    private Http2HeaderTableEncoder() {}
    
    public void maxHeaderTableSize(long max) throws Http2Exception
    {
      encoder.setMaxHeaderTableSize(tableSizeChangeOutput, max);
    }
    
    public long maxHeaderTableSize()
    {
      return encoder.getMaxHeaderTableSize();
    }
    
    public void maxHeaderListSize(long max) throws Http2Exception
    {
      encoder.setMaxHeaderListSize(max);
    }
    
    public long maxHeaderListSize()
    {
      return encoder.getMaxHeaderListSize();
    }
  }
}
