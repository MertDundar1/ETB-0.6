package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AsyncMapping;
import io.netty.util.CharsetUtil;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.IDN;
import java.net.SocketAddress;
import java.util.List;
import java.util.Locale;






















public class SniHandler
  extends ByteToMessageDecoder
  implements ChannelOutboundHandler
{
  private static final int MAX_SSL_RECORDS = 4;
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(SniHandler.class);
  
  private static final Selection EMPTY_SELECTION = new Selection(null, null);
  
  protected final AsyncMapping<String, SslContext> mapping;
  
  private boolean handshakeFailed;
  private boolean suppressRead;
  private boolean readPending;
  private volatile Selection selection = EMPTY_SELECTION;
  





  public SniHandler(Mapping<? super String, ? extends SslContext> mapping)
  {
    this(new AsyncMappingAdapter(mapping, null));
  }
  





  public SniHandler(DomainNameMapping<? extends SslContext> mapping)
  {
    this(mapping);
  }
  






  public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping)
  {
    this.mapping = ((AsyncMapping)ObjectUtil.checkNotNull(mapping, "mapping"));
  }
  


  public String hostname()
  {
    return selection.hostname;
  }
  


  public SslContext sslContext()
  {
    return selection.context;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    if ((!suppressRead) && (!handshakeFailed)) {
      int writerIndex = in.writerIndex();
      try
      {
        for (int i = 0; i < 4; i++) {
          int readerIndex = in.readerIndex();
          int readableBytes = writerIndex - readerIndex;
          if (readableBytes < 5)
          {
            return;
          }
          
          int command = in.getUnsignedByte(readerIndex);
          

          switch (command) {
          case 20: 
          case 21: 
            int len = SslUtils.getEncryptedPacketLength(in, readerIndex);
            

            if (len == -1) {
              handshakeFailed = true;
              NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
              
              in.skipBytes(in.readableBytes());
              ctx.fireExceptionCaught(e);
              
              SslUtils.notifyHandshakeFailure(ctx, e);
              return;
            }
            if (writerIndex - readerIndex - 5 < len)
            {
              return;
            }
            
            in.skipBytes(len);
            break;
          case 22: 
            int majorVersion = in.getUnsignedByte(readerIndex + 1);
            

            if (majorVersion == 3) {
              int packetLength = in.getUnsignedShort(readerIndex + 3) + 5;
              

              if (readableBytes < packetLength)
              {
                return;
              }
              




















              int endOffset = readerIndex + packetLength;
              int offset = readerIndex + 43;
              
              if (endOffset - offset < 6) {
                break;
              }
              
              int sessionIdLength = in.getUnsignedByte(offset);
              offset += sessionIdLength + 1;
              
              int cipherSuitesLength = in.getUnsignedShort(offset);
              offset += cipherSuitesLength + 2;
              
              int compressionMethodLength = in.getUnsignedByte(offset);
              offset += compressionMethodLength + 1;
              
              int extensionsLength = in.getUnsignedShort(offset);
              offset += 2;
              int extensionsLimit = offset + extensionsLength;
              
              if (extensionsLimit > endOffset) {
                break;
              }
              


              while (extensionsLimit - offset >= 4)
              {


                int extensionType = in.getUnsignedShort(offset);
                offset += 2;
                
                int extensionLength = in.getUnsignedShort(offset);
                offset += 2;
                
                if (extensionsLimit - offset < extensionLength) {
                  break;
                }
                


                if (extensionType == 0) {
                  offset += 2;
                  if (extensionsLimit - offset < 3) {
                    break;
                  }
                  
                  int serverNameType = in.getUnsignedByte(offset);
                  offset++;
                  
                  if (serverNameType != 0) break;
                  int serverNameLength = in.getUnsignedShort(offset);
                  offset += 2;
                  
                  if (extensionsLimit - offset < serverNameLength) {
                    break;
                  }
                  
                  String hostname = in.toString(offset, serverNameLength, CharsetUtil.UTF_8);
                  
                  try
                  {
                    select(ctx, IDN.toASCII(hostname, 1).toLowerCase(Locale.US));
                  }
                  catch (Throwable t) {
                    ctx.fireExceptionCaught(t);
                  }
                  return;
                }
                




                offset += extensionLength;
              }
            }
            break;
          }
          
          break;
        }
      }
      catch (Throwable e)
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(in), e);
        }
      }
      
      select(ctx, null);
    }
  }
  
  private void select(final ChannelHandlerContext ctx, final String hostname) throws Exception {
    Future<SslContext> future = lookup(ctx, hostname);
    if (future.isDone()) {
      if (future.isSuccess()) {
        onSslContext(ctx, hostname, (SslContext)future.getNow());
      } else {
        throw new DecoderException("failed to get the SslContext for " + hostname, future.cause());
      }
    } else {
      suppressRead = true;
      future.addListener(new FutureListener()
      {
        public void operationComplete(Future<SslContext> future) throws Exception {
          try {
            suppressRead = false;
            if (future.isSuccess()) {
              SniHandler.this.onSslContext(ctx, hostname, (SslContext)future.getNow());
            } else {
              ctx.fireExceptionCaught(new DecoderException("failed to get the SslContext for " + hostname, future.cause()));
            }
          }
          finally {
            if (readPending) {
              readPending = false;
              ctx.read();
            }
          }
        }
      });
    }
  }
  




  protected Future<SslContext> lookup(ChannelHandlerContext ctx, String hostname)
    throws Exception
  {
    return mapping.map(hostname, ctx.executor().newPromise());
  }
  




  private void onSslContext(ChannelHandlerContext ctx, String hostname, SslContext sslContext)
  {
    selection = new Selection(sslContext, hostname);
    try {
      replaceHandler(ctx, hostname, sslContext);
    } catch (Throwable cause) {
      selection = EMPTY_SELECTION;
      ctx.fireExceptionCaught(cause);
    }
  }
  







  protected void replaceHandler(ChannelHandlerContext ctx, String hostname, SslContext sslContext)
    throws Exception
  {
    SslHandler sslHandler = null;
    try {
      sslHandler = sslContext.newHandler(ctx.alloc());
      ctx.pipeline().replace(this, SslHandler.class.getName(), sslHandler);
      sslHandler = null;

    }
    finally
    {
      if (sslHandler != null) {
        ReferenceCountUtil.safeRelease(sslHandler.engine());
      }
    }
  }
  
  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception
  {
    ctx.bind(localAddress, promise);
  }
  
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    throws Exception
  {
    ctx.connect(remoteAddress, localAddress, promise);
  }
  
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    ctx.disconnect(promise);
  }
  
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    ctx.close(promise);
  }
  
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
  {
    ctx.deregister(promise);
  }
  
  public void read(ChannelHandlerContext ctx) throws Exception
  {
    if (suppressRead) {
      readPending = true;
    } else {
      ctx.read();
    }
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
  {
    ctx.write(msg, promise);
  }
  
  public void flush(ChannelHandlerContext ctx) throws Exception
  {
    ctx.flush();
  }
  
  private static final class AsyncMappingAdapter implements AsyncMapping<String, SslContext> {
    private final Mapping<? super String, ? extends SslContext> mapping;
    
    private AsyncMappingAdapter(Mapping<? super String, ? extends SslContext> mapping) {
      this.mapping = ((Mapping)ObjectUtil.checkNotNull(mapping, "mapping"));
    }
    
    public Future<SslContext> map(String input, Promise<SslContext> promise)
    {
      try
      {
        context = (SslContext)mapping.map(input);
      } catch (Throwable cause) { SslContext context;
        return promise.setFailure(cause); }
      SslContext context;
      return promise.setSuccess(context);
    }
  }
  
  private static final class Selection {
    final SslContext context;
    final String hostname;
    
    Selection(SslContext context, String hostname) {
      this.context = context;
      this.hostname = hostname;
    }
  }
}
