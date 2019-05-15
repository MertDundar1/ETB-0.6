package io.netty.handler.codec.http.cors;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Set;





















public class CorsHandler
  extends ChannelDuplexHandler
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
  private final CorsConfig config;
  private HttpRequest request;
  
  public CorsHandler(CorsConfig config)
  {
    this.config = config;
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
  {
    if ((config.isCorsSupportEnabled()) && ((msg instanceof HttpRequest))) {
      request = ((HttpRequest)msg);
      if (isPreflightRequest(request)) {
        handlePreflight(ctx, request);
        return;
      }
      if ((config.isShortCurcuit()) && (!validateOrigin())) {
        forbidden(ctx, request);
        return;
      }
    }
    ctx.fireChannelRead(msg);
  }
  
  private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
    HttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
    if (setOrigin(response)) {
      setAllowMethods(response);
      setAllowHeaders(response);
      setAllowCredentials(response);
      setMaxAge(response);
      setPreflightHeaders(response);
    }
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }
  





  private void setPreflightHeaders(HttpResponse response)
  {
    response.headers().add(config.preflightResponseHeaders());
  }
  
  private boolean setOrigin(HttpResponse response) {
    String origin = request.headers().get("Origin");
    if (origin != null) {
      if (("null".equals(origin)) && (config.isNullOriginAllowed())) {
        setAnyOrigin(response);
        return true;
      }
      if (config.isAnyOriginSupported()) {
        if (config.isCredentialsAllowed()) {
          echoRequestOrigin(response);
          setVaryHeader(response);
        } else {
          setAnyOrigin(response);
        }
        return true;
      }
      if (config.origins().contains(origin)) {
        setOrigin(response, origin);
        setVaryHeader(response);
        return true;
      }
      logger.debug("Request origin [" + origin + "] was not among the configured origins " + config.origins());
    }
    return false;
  }
  
  private boolean validateOrigin() {
    if (config.isAnyOriginSupported()) {
      return true;
    }
    
    String origin = request.headers().get("Origin");
    if (origin == null)
    {
      return true;
    }
    
    if (("null".equals(origin)) && (config.isNullOriginAllowed())) {
      return true;
    }
    
    return config.origins().contains(origin);
  }
  
  private void echoRequestOrigin(HttpResponse response) {
    setOrigin(response, request.headers().get("Origin"));
  }
  
  private static void setVaryHeader(HttpResponse response) {
    response.headers().set("Vary", "Origin");
  }
  
  private static void setAnyOrigin(HttpResponse response) {
    setOrigin(response, "*");
  }
  
  private static void setOrigin(HttpResponse response, String origin) {
    response.headers().set("Access-Control-Allow-Origin", origin);
  }
  
  private void setAllowCredentials(HttpResponse response) {
    if (config.isCredentialsAllowed()) {
      response.headers().set("Access-Control-Allow-Credentials", "true");
    }
  }
  
  private static boolean isPreflightRequest(HttpRequest request) {
    HttpHeaders headers = request.headers();
    return (request.getMethod().equals(HttpMethod.OPTIONS)) && (headers.contains("Origin")) && (headers.contains("Access-Control-Request-Method"));
  }
  

  private void setExposeHeaders(HttpResponse response)
  {
    if (!config.exposedHeaders().isEmpty()) {
      response.headers().set("Access-Control-Expose-Headers", config.exposedHeaders());
    }
  }
  
  private void setAllowMethods(HttpResponse response) {
    response.headers().set("Access-Control-Allow-Methods", config.allowedRequestMethods());
  }
  
  private void setAllowHeaders(HttpResponse response) {
    response.headers().set("Access-Control-Allow-Headers", config.allowedRequestHeaders());
  }
  
  private void setMaxAge(HttpResponse response) {
    response.headers().set("Access-Control-Max-Age", Long.valueOf(config.maxAge()));
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
    throws Exception
  {
    if ((config.isCorsSupportEnabled()) && ((msg instanceof HttpResponse))) {
      HttpResponse response = (HttpResponse)msg;
      if (setOrigin(response)) {
        setAllowCredentials(response);
        setAllowHeaders(response);
        setExposeHeaders(response);
      }
    }
    ctx.writeAndFlush(msg, promise);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
  {
    logger.error("Caught error in CorsHandler", cause);
    ctx.fireExceptionCaught(cause);
  }
  
  private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
    ctx.writeAndFlush(new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
  }
}
