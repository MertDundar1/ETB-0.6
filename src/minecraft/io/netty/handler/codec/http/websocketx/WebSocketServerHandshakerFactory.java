package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;


































public class WebSocketServerHandshakerFactory
{
  private final String webSocketURL;
  private final String subprotocols;
  private final boolean allowExtensions;
  private final int maxFramePayloadLength;
  
  public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions)
  {
    this(webSocketURL, subprotocols, allowExtensions, 65536);
  }
  















  public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength)
  {
    this.webSocketURL = webSocketURL;
    this.subprotocols = subprotocols;
    this.allowExtensions = allowExtensions;
    this.maxFramePayloadLength = maxFramePayloadLength;
  }
  






  public WebSocketServerHandshaker newHandshaker(HttpRequest req)
  {
    String version = req.headers().get("Sec-WebSocket-Version");
    if (version != null) {
      if (version.equals(WebSocketVersion.V13.toHttpHeaderValue()))
      {
        return new WebSocketServerHandshaker13(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength);
      }
      if (version.equals(WebSocketVersion.V08.toHttpHeaderValue()))
      {
        return new WebSocketServerHandshaker08(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength);
      }
      if (version.equals(WebSocketVersion.V07.toHttpHeaderValue()))
      {
        return new WebSocketServerHandshaker07(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength);
      }
      
      return null;
    }
    

    return new WebSocketServerHandshaker00(webSocketURL, subprotocols, maxFramePayloadLength);
  }
  



  @Deprecated
  public static void sendUnsupportedWebSocketVersionResponse(Channel channel)
  {
    sendUnsupportedVersionResponse(channel);
  }
  


  public static ChannelFuture sendUnsupportedVersionResponse(Channel channel)
  {
    return sendUnsupportedVersionResponse(channel, channel.newPromise());
  }
  


  public static ChannelFuture sendUnsupportedVersionResponse(Channel channel, ChannelPromise promise)
  {
    HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED);
    

    res.headers().set("Sec-WebSocket-Version", WebSocketVersion.V13.toHttpHeaderValue());
    return channel.write(res, promise);
  }
}
