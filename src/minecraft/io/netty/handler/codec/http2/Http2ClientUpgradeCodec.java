package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientUpgradeHandler.UpgradeCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.CharObjectMap.PrimitiveEntry;
import io.netty.util.internal.ObjectUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;























public class Http2ClientUpgradeCodec
  implements HttpClientUpgradeHandler.UpgradeCodec
{
  private static final List<CharSequence> UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
  

  private final String handlerName;
  

  private final Http2ConnectionHandler connectionHandler;
  


  public Http2ClientUpgradeCodec(Http2ConnectionHandler connectionHandler)
  {
    this(null, connectionHandler);
  }
  






  public Http2ClientUpgradeCodec(String handlerName, Http2ConnectionHandler connectionHandler)
  {
    this.handlerName = handlerName;
    this.connectionHandler = ((Http2ConnectionHandler)ObjectUtil.checkNotNull(connectionHandler, "connectionHandler"));
  }
  
  public CharSequence protocol()
  {
    return Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME;
  }
  

  public Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext ctx, HttpRequest upgradeRequest)
  {
    CharSequence settingsValue = getSettingsHeaderValue(ctx);
    upgradeRequest.headers().set(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER, settingsValue);
    return UPGRADE_HEADERS;
  }
  

  public void upgradeTo(ChannelHandlerContext ctx, FullHttpResponse upgradeResponse)
    throws Exception
  {
    connectionHandler.onHttpClientUpgrade();
    

    ctx.pipeline().addAfter(ctx.name(), handlerName, connectionHandler);
  }
  



  private CharSequence getSettingsHeaderValue(ChannelHandlerContext ctx)
  {
    ByteBuf buf = null;
    ByteBuf encodedBuf = null;
    try
    {
      Http2Settings settings = connectionHandler.decoder().localSettings();
      

      int payloadLength = 6 * settings.size();
      buf = ctx.alloc().buffer(payloadLength);
      for (CharObjectMap.PrimitiveEntry<Long> entry : settings.entries()) {
        Http2CodecUtil.writeUnsignedShort(entry.key(), buf);
        Http2CodecUtil.writeUnsignedInt(((Long)entry.value()).longValue(), buf);
      }
      

      encodedBuf = Base64.encode(buf, Base64Dialect.URL_SAFE);
      return encodedBuf.toString(CharsetUtil.UTF_8);
    } finally {
      ReferenceCountUtil.release(buf);
      ReferenceCountUtil.release(encodedBuf);
    }
  }
}
