package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;


































public abstract class HttpContentEncoder
  extends MessageToMessageCodec<HttpRequest, HttpObject>
{
  private final Queue<String> acceptEncodingQueue;
  private String acceptEncoding;
  private EmbeddedChannel encoder;
  private State state;
  
  private static enum State
  {
    PASS_THROUGH, 
    AWAIT_HEADERS, 
    AWAIT_CONTENT;
    
    private State() {} }
  public HttpContentEncoder() { acceptEncodingQueue = new ArrayDeque();
    

    state = State.AWAIT_HEADERS;
  }
  
  public boolean acceptOutboundMessage(Object msg) throws Exception {
    return ((msg instanceof HttpContent)) || ((msg instanceof HttpResponse));
  }
  
  protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out)
    throws Exception
  {
    String acceptedEncoding = msg.headers().get("Accept-Encoding");
    if (acceptedEncoding == null) {
      acceptedEncoding = "identity";
    }
    acceptEncodingQueue.add(acceptedEncoding);
    out.add(ReferenceCountUtil.retain(msg));
  }
  
  protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception
  {
    boolean isFull = ((msg instanceof HttpResponse)) && ((msg instanceof LastHttpContent));
    switch (1.$SwitchMap$io$netty$handler$codec$http$HttpContentEncoder$State[state.ordinal()]) {
    case 1: 
      ensureHeaders(msg);
      assert (encoder == null);
      
      HttpResponse res = (HttpResponse)msg;
      
      if (res.getStatus().code() == 100) {
        if (isFull) {
          out.add(ReferenceCountUtil.retain(res));
        } else {
          out.add(res);
          
          state = State.PASS_THROUGH;
        }
        
      }
      else
      {
        acceptEncoding = ((String)acceptEncodingQueue.poll());
        if (acceptEncoding == null) {
          throw new IllegalStateException("cannot send more responses than requests");
        }
        
        if (isFull)
        {
          if (!((ByteBufHolder)res).content().isReadable()) {
            out.add(ReferenceCountUtil.retain(res));
            return;
          }
        }
        

        Result result = beginEncode(res, acceptEncoding);
        

        if (result == null) {
          if (isFull) {
            out.add(ReferenceCountUtil.retain(res));
          } else {
            out.add(res);
            
            state = State.PASS_THROUGH;
          }
        }
        else
        {
          encoder = result.contentEncoder();
          


          res.headers().set("Content-Encoding", result.targetContentEncoding());
          

          res.headers().remove("Content-Length");
          res.headers().set("Transfer-Encoding", "chunked");
          

          if (isFull)
          {
            HttpResponse newRes = new DefaultHttpResponse(res.getProtocolVersion(), res.getStatus());
            newRes.headers().set(res.headers());
            out.add(newRes);
          }
          else {
            out.add(res);
            state = State.AWAIT_CONTENT;
            if (!(msg instanceof HttpContent)) {
              return;
            }
          }
        }
      }
      
      break;
    case 2: 
      ensureContent(msg);
      if (encodeContent((HttpContent)msg, out)) {
        state = State.AWAIT_HEADERS;
      }
      
      break;
    case 3: 
      ensureContent(msg);
      out.add(ReferenceCountUtil.retain(msg));
      
      if ((msg instanceof LastHttpContent)) {
        state = State.AWAIT_HEADERS;
      }
      break;
    }
  }
  
  private static void ensureHeaders(HttpObject msg)
  {
    if (!(msg instanceof HttpResponse)) {
      throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
    }
  }
  

  private static void ensureContent(HttpObject msg)
  {
    if (!(msg instanceof HttpContent)) {
      throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
    }
  }
  

  private boolean encodeContent(HttpContent c, List<Object> out)
  {
    ByteBuf content = c.content();
    
    encode(content, out);
    
    if ((c instanceof LastHttpContent)) {
      finishEncode(out);
      LastHttpContent last = (LastHttpContent)c;
      


      HttpHeaders headers = last.trailingHeaders();
      if (headers.isEmpty()) {
        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
      } else {
        out.add(new ComposedLastHttpContent(headers));
      }
      return true;
    }
    return false;
  }
  






  protected abstract Result beginEncode(HttpResponse paramHttpResponse, String paramString)
    throws Exception;
  






  public void handlerRemoved(ChannelHandlerContext ctx)
    throws Exception
  {
    cleanup();
    super.handlerRemoved(ctx);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    cleanup();
    super.channelInactive(ctx);
  }
  
  private void cleanup() {
    if (encoder != null)
    {
      if (encoder.finish()) {
        for (;;) {
          ByteBuf buf = (ByteBuf)encoder.readOutbound();
          if (buf == null) {
            break;
          }
          

          buf.release();
        }
      }
      encoder = null;
    }
  }
  
  private void encode(ByteBuf in, List<Object> out)
  {
    encoder.writeOutbound(new Object[] { in.retain() });
    fetchEncoderOutput(out);
  }
  
  private void finishEncode(List<Object> out) {
    if (encoder.finish()) {
      fetchEncoderOutput(out);
    }
    encoder = null;
  }
  
  private void fetchEncoderOutput(List<Object> out) {
    for (;;) {
      ByteBuf buf = (ByteBuf)encoder.readOutbound();
      if (buf == null) {
        break;
      }
      if (!buf.isReadable()) {
        buf.release();
      }
      else
        out.add(new DefaultHttpContent(buf));
    }
  }
  
  public static final class Result {
    private final String targetContentEncoding;
    private final EmbeddedChannel contentEncoder;
    
    public Result(String targetContentEncoding, EmbeddedChannel contentEncoder) {
      if (targetContentEncoding == null) {
        throw new NullPointerException("targetContentEncoding");
      }
      if (contentEncoder == null) {
        throw new NullPointerException("contentEncoder");
      }
      
      this.targetContentEncoding = targetContentEncoding;
      this.contentEncoder = contentEncoder;
    }
    
    public String targetContentEncoding() {
      return targetContentEncoding;
    }
    
    public EmbeddedChannel contentEncoder() {
      return contentEncoder;
    }
  }
}
