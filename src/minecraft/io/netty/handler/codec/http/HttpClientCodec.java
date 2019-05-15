package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

































public final class HttpClientCodec
  extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder>
{
  private final Queue<HttpMethod> queue = new ArrayDeque();
  

  private boolean done;
  
  private final AtomicLong requestResponseCounter = new AtomicLong();
  

  private final boolean failOnMissingResponse;
  


  public HttpClientCodec()
  {
    this(4096, 8192, 8192, false);
  }
  
  public void setSingleDecode(boolean singleDecode) {
    ((HttpResponseDecoder)inboundHandler()).setSingleDecode(singleDecode);
  }
  
  public boolean isSingleDecode() {
    return ((HttpResponseDecoder)inboundHandler()).isSingleDecode();
  }
  


  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize)
  {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, false);
  }
  



  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse)
  {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, true);
  }
  




  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders)
  {
    init(new Decoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), new Encoder(null));
    this.failOnMissingResponse = failOnMissingResponse;
  }
  
  private final class Encoder extends HttpRequestEncoder
  {
    private Encoder() {}
    
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
      if (((msg instanceof HttpRequest)) && (!done)) {
        queue.offer(((HttpRequest)msg).getMethod());
      }
      
      super.encode(ctx, msg, out);
      
      if (failOnMissingResponse)
      {
        if ((msg instanceof LastHttpContent))
        {
          requestResponseCounter.incrementAndGet();
        }
      }
    }
  }
  
  private final class Decoder extends HttpResponseDecoder {
    Decoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
      super(maxHeaderSize, maxChunkSize, validateHeaders);
    }
    
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out)
      throws Exception
    {
      if (done) {
        int readable = actualReadableBytes();
        if (readable == 0)
        {

          return;
        }
        out.add(buffer.readBytes(readable));
      } else {
        int oldSize = out.size();
        super.decode(ctx, buffer, out);
        if (failOnMissingResponse) {
          int size = out.size();
          for (int i = oldSize; i < size; i++) {
            decrement(out.get(i));
          }
        }
      }
    }
    
    private void decrement(Object msg) {
      if (msg == null) {
        return;
      }
      

      if ((msg instanceof LastHttpContent)) {
        requestResponseCounter.decrementAndGet();
      }
    }
    
    protected boolean isContentAlwaysEmpty(HttpMessage msg)
    {
      int statusCode = ((HttpResponse)msg).getStatus().code();
      if (statusCode == 100)
      {
        return true;
      }
      


      HttpMethod method = (HttpMethod)queue.poll();
      
      char firstChar = method.name().charAt(0);
      switch (firstChar)
      {



      case 'H': 
        if (HttpMethod.HEAD.equals(method)) {
          return true;
        }
        













        break;
      case 'C': 
        if ((statusCode == 200) && 
          (HttpMethod.CONNECT.equals(method)))
        {
          done = true;
          queue.clear();
          return true;
        }
        
        break;
      }
      
      return super.isContentAlwaysEmpty(msg);
    }
    
    public void channelInactive(ChannelHandlerContext ctx)
      throws Exception
    {
      super.channelInactive(ctx);
      
      if (failOnMissingResponse) {
        long missingResponses = requestResponseCounter.get();
        if (missingResponses > 0L) {
          ctx.fireExceptionCaught(new PrematureChannelClosureException("channel gone inactive with " + missingResponses + " missing response(s)"));
        }
      }
    }
  }
}
