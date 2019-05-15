package io.netty.handler.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
















public final class HttpProxyHandler
  extends ProxyHandler
{
  private static final String PROTOCOL = "http";
  private static final String AUTH_BASIC = "basic";
  private final HttpClientCodec codec = new HttpClientCodec();
  private final String username;
  private final String password;
  private final CharSequence authorization;
  private HttpResponseStatus status;
  
  public HttpProxyHandler(SocketAddress proxyAddress) {
    super(proxyAddress);
    username = null;
    password = null;
    authorization = null;
  }
  
  public HttpProxyHandler(SocketAddress proxyAddress, String username, String password) {
    super(proxyAddress);
    if (username == null) {
      throw new NullPointerException("username");
    }
    if (password == null) {
      throw new NullPointerException("password");
    }
    this.username = username;
    this.password = password;
    
    ByteBuf authz = Unpooled.copiedBuffer(username + ':' + password, CharsetUtil.UTF_8);
    ByteBuf authzBase64 = Base64.encode(authz, false);
    
    authorization = new AsciiString("Basic " + authzBase64.toString(CharsetUtil.US_ASCII));
    
    authz.release();
    authzBase64.release();
  }
  
  public String protocol()
  {
    return "http";
  }
  
  public String authScheme()
  {
    return authorization != null ? "basic" : "none";
  }
  
  public String username() {
    return username;
  }
  
  public String password() {
    return password;
  }
  
  protected void addCodec(ChannelHandlerContext ctx) throws Exception
  {
    ChannelPipeline p = ctx.pipeline();
    String name = ctx.name();
    p.addBefore(name, null, codec);
  }
  
  protected void removeEncoder(ChannelHandlerContext ctx) throws Exception
  {
    codec.removeOutboundHandler();
  }
  
  protected void removeDecoder(ChannelHandlerContext ctx) throws Exception
  {
    codec.removeInboundHandler();
  }
  
  protected Object newInitialMessage(ChannelHandlerContext ctx) throws Exception
  {
    InetSocketAddress raddr = (InetSocketAddress)destinationAddress();
    String rhost;
    String rhost; if (raddr.isUnresolved()) {
      rhost = raddr.getHostString();
    } else {
      rhost = raddr.getAddress().getHostAddress();
    }
    
    String host = rhost + ':' + raddr.getPort();
    FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, host, Unpooled.EMPTY_BUFFER, false);
    



    req.headers().set(HttpHeaderNames.HOST, host);
    
    if (authorization != null) {
      req.headers().set(HttpHeaderNames.PROXY_AUTHORIZATION, authorization);
    }
    
    return req;
  }
  
  protected boolean handleResponse(ChannelHandlerContext ctx, Object response) throws Exception
  {
    if ((response instanceof HttpResponse)) {
      if (status != null) {
        throw new ProxyConnectException(exceptionMessage("too many responses"));
      }
      status = ((HttpResponse)response).status();
    }
    
    boolean finished = response instanceof LastHttpContent;
    if (finished) {
      if (status == null) {
        throw new ProxyConnectException(exceptionMessage("missing response"));
      }
      if (status.code() != 200) {
        throw new ProxyConnectException(exceptionMessage("status: " + status));
      }
    }
    
    return finished;
  }
}
