package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.collection.CharObjectMap.PrimitiveEntry;
import io.netty.util.internal.ObjectUtil;
































































public class DefaultHttp2FrameWriter
  implements Http2FrameWriter, Http2FrameSizePolicy, Http2FrameWriter.Configuration
{
  private static final String STREAM_ID = "Stream ID";
  private static final String STREAM_DEPENDENCY = "Stream Dependency";
  private static final ByteBuf ZERO_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer(255).writeZero(255)).asReadOnly();
  
  private final Http2HeadersEncoder headersEncoder;
  private int maxFrameSize;
  
  public DefaultHttp2FrameWriter()
  {
    this(new DefaultHttp2HeadersEncoder());
  }
  
  public DefaultHttp2FrameWriter(Http2HeadersEncoder.SensitivityDetector headersSensativityDetector) {
    this(new DefaultHttp2HeadersEncoder(headersSensativityDetector));
  }
  
  public DefaultHttp2FrameWriter(Http2HeadersEncoder.SensitivityDetector headersSensativityDetector, boolean ignoreMaxHeaderListSize) {
    this(new DefaultHttp2HeadersEncoder(headersSensativityDetector, ignoreMaxHeaderListSize));
  }
  
  public DefaultHttp2FrameWriter(Http2HeadersEncoder headersEncoder) {
    this.headersEncoder = headersEncoder;
    maxFrameSize = 16384;
  }
  
  public Http2FrameWriter.Configuration configuration()
  {
    return this;
  }
  
  public Http2HeaderTable headerTable()
  {
    return headersEncoder.configuration().headerTable();
  }
  
  public Http2FrameSizePolicy frameSizePolicy()
  {
    return this;
  }
  
  public void maxFrameSize(int max) throws Http2Exception
  {
    if (!Http2CodecUtil.isMaxFrameSizeValid(max)) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Invalid MAX_FRAME_SIZE specified in sent settings: %d", new Object[] { Integer.valueOf(max) });
    }
    maxFrameSize = max;
  }
  
  public int maxFrameSize()
  {
    return maxFrameSize;
  }
  

  public void close() {}
  

  public ChannelFuture writeData(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endStream, ChannelPromise promise)
  {
    Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator = new Http2CodecUtil.SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
    
    DataFrameHeader header = new DataFrameHeader(ctx, streamId);
    boolean needToReleaseHeaders = true;
    boolean needToReleaseData = true;
    try {
      verifyStreamId(streamId, "Stream ID");
      Http2CodecUtil.verifyPadding(padding);
      

      int remainingData = data.readableBytes();
      boolean lastFrame;
      do {
        int frameDataBytes = Math.min(remainingData, maxFrameSize);
        int framePaddingBytes = Math.min(padding, Math.max(0, maxFrameSize - 1 - frameDataBytes));
        

        padding -= framePaddingBytes;
        remainingData -= frameDataBytes;
        

        lastFrame = (remainingData == 0) && (padding == 0);
        

        ByteBuf frameHeader = header.slice(frameDataBytes, framePaddingBytes, (lastFrame) && (endStream));
        needToReleaseHeaders = !lastFrame;
        ctx.write(lastFrame ? frameHeader : frameHeader.retain(), promiseAggregator.newPromise());
        

        ByteBuf frameData = data.readSlice(frameDataBytes);
        
        needToReleaseData = !lastFrame;
        ctx.write(lastFrame ? frameData : frameData.retain(), promiseAggregator.newPromise());
        

        if (paddingBytes(framePaddingBytes) > 0) {
          ctx.write(ZERO_BUFFER.slice(0, paddingBytes(framePaddingBytes)), promiseAggregator.newPromise());
        }
      } while (!lastFrame);
    } catch (Throwable t) {
      if (needToReleaseHeaders) {
        header.release();
      }
      if (needToReleaseData) {
        data.release();
      }
      promiseAggregator.setFailure(t);
    }
    return promiseAggregator.doneAllocatingPromises();
  }
  

  public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, ChannelPromise promise)
  {
    return writeHeadersInternal(ctx, streamId, headers, padding, endStream, false, 0, (short)0, false, promise);
  }
  



  public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream, ChannelPromise promise)
  {
    return writeHeadersInternal(ctx, streamId, headers, padding, endStream, true, streamDependency, weight, exclusive, promise);
  }
  

  public ChannelFuture writePriority(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive, ChannelPromise promise)
  {
    try
    {
      verifyStreamId(streamId, "Stream ID");
      verifyStreamId(streamDependency, "Stream Dependency");
      verifyWeight(weight);
      
      ByteBuf buf = ctx.alloc().buffer(14);
      Http2CodecUtil.writeFrameHeaderInternal(buf, 5, (byte)2, new Http2Flags(), streamId);
      long word1 = exclusive ? 0x80000000 | streamDependency : streamDependency;
      Http2CodecUtil.writeUnsignedInt(word1, buf);
      
      buf.writeByte(weight - 1);
      return ctx.write(buf, promise);
    } catch (Throwable t) {
      return promise.setFailure(t);
    }
  }
  
  public ChannelFuture writeRstStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise)
  {
    try
    {
      verifyStreamId(streamId, "Stream ID");
      verifyErrorCode(errorCode);
      
      ByteBuf buf = ctx.alloc().buffer(13);
      Http2CodecUtil.writeFrameHeaderInternal(buf, 4, (byte)3, new Http2Flags(), streamId);
      Http2CodecUtil.writeUnsignedInt(errorCode, buf);
      return ctx.write(buf, promise);
    } catch (Throwable t) {
      return promise.setFailure(t);
    }
  }
  
  public ChannelFuture writeSettings(ChannelHandlerContext ctx, Http2Settings settings, ChannelPromise promise)
  {
    try
    {
      ObjectUtil.checkNotNull(settings, "settings");
      int payloadLength = 6 * settings.size();
      ByteBuf buf = ctx.alloc().buffer(9 + settings.size() * 6);
      Http2CodecUtil.writeFrameHeaderInternal(buf, payloadLength, (byte)4, new Http2Flags(), 0);
      for (CharObjectMap.PrimitiveEntry<Long> entry : settings.entries()) {
        Http2CodecUtil.writeUnsignedShort(entry.key(), buf);
        Http2CodecUtil.writeUnsignedInt(((Long)entry.value()).longValue(), buf);
      }
      return ctx.write(buf, promise);
    } catch (Throwable t) {
      return promise.setFailure(t);
    }
  }
  
  public ChannelFuture writeSettingsAck(ChannelHandlerContext ctx, ChannelPromise promise)
  {
    try {
      ByteBuf buf = ctx.alloc().buffer(9);
      Http2CodecUtil.writeFrameHeaderInternal(buf, 0, (byte)4, new Http2Flags().ack(true), 0);
      return ctx.write(buf, promise);
    } catch (Throwable t) {
      return promise.setFailure(t);
    }
  }
  

  public ChannelFuture writePing(ChannelHandlerContext ctx, boolean ack, ByteBuf data, ChannelPromise promise)
  {
    boolean releaseData = true;
    Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator = new Http2CodecUtil.SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
    try
    {
      verifyPingPayload(data);
      Http2Flags flags = ack ? new Http2Flags().ack(true) : new Http2Flags();
      ByteBuf buf = ctx.alloc().buffer(9);
      Http2CodecUtil.writeFrameHeaderInternal(buf, data.readableBytes(), (byte)6, flags, 0);
      ctx.write(buf, promiseAggregator.newPromise());
      

      releaseData = false;
      ctx.write(data, promiseAggregator.newPromise());
    } catch (Throwable t) {
      if (releaseData) {
        data.release();
      }
      promiseAggregator.setFailure(t);
    }
    return promiseAggregator.doneAllocatingPromises();
  }
  

  public ChannelFuture writePushPromise(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding, ChannelPromise promise)
  {
    ByteBuf headerBlock = null;
    Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator = new Http2CodecUtil.SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
    try
    {
      verifyStreamId(streamId, "Stream ID");
      verifyStreamId(promisedStreamId, "Promised Stream ID");
      Http2CodecUtil.verifyPadding(padding);
      

      headerBlock = ctx.alloc().buffer();
      headersEncoder.encodeHeaders(headers, headerBlock);
      

      Http2Flags flags = new Http2Flags().paddingPresent(padding > 0);
      
      int nonFragmentLength = 4 + padding;
      int maxFragmentLength = maxFrameSize - nonFragmentLength;
      ByteBuf fragment = headerBlock.readRetainedSlice(Math.min(headerBlock.readableBytes(), maxFragmentLength));
      
      flags.endOfHeaders(!headerBlock.isReadable());
      
      int payloadLength = fragment.readableBytes() + nonFragmentLength;
      ByteBuf buf = ctx.alloc().buffer(14);
      Http2CodecUtil.writeFrameHeaderInternal(buf, payloadLength, (byte)5, flags, streamId);
      writePaddingLength(buf, padding);
      

      buf.writeInt(promisedStreamId);
      ctx.write(buf, promiseAggregator.newPromise());
      

      ctx.write(fragment, promiseAggregator.newPromise());
      

      if (paddingBytes(padding) > 0) {
        ctx.write(ZERO_BUFFER.slice(0, paddingBytes(padding)), promiseAggregator.newPromise());
      }
      
      if (!flags.endOfHeaders()) {
        writeContinuationFrames(ctx, streamId, headerBlock, padding, promiseAggregator);
      }
    } catch (Throwable t) {
      promiseAggregator.setFailure(t);
    } finally {
      if (headerBlock != null) {
        headerBlock.release();
      }
    }
    return promiseAggregator.doneAllocatingPromises();
  }
  

  public ChannelFuture writeGoAway(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelPromise promise)
  {
    boolean releaseData = true;
    Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator = new Http2CodecUtil.SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
    try
    {
      verifyStreamOrConnectionId(lastStreamId, "Last Stream ID");
      verifyErrorCode(errorCode);
      
      int payloadLength = 8 + debugData.readableBytes();
      ByteBuf buf = ctx.alloc().buffer(17);
      Http2CodecUtil.writeFrameHeaderInternal(buf, payloadLength, (byte)7, new Http2Flags(), 0);
      buf.writeInt(lastStreamId);
      Http2CodecUtil.writeUnsignedInt(errorCode, buf);
      ctx.write(buf, promiseAggregator.newPromise());
      
      releaseData = false;
      ctx.write(debugData, promiseAggregator.newPromise());
    } catch (Throwable t) {
      if (releaseData) {
        debugData.release();
      }
      promiseAggregator.setFailure(t);
    }
    return promiseAggregator.doneAllocatingPromises();
  }
  
  public ChannelFuture writeWindowUpdate(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement, ChannelPromise promise)
  {
    try
    {
      verifyStreamOrConnectionId(streamId, "Stream ID");
      verifyWindowSizeIncrement(windowSizeIncrement);
      
      ByteBuf buf = ctx.alloc().buffer(13);
      Http2CodecUtil.writeFrameHeaderInternal(buf, 4, (byte)8, new Http2Flags(), streamId);
      buf.writeInt(windowSizeIncrement);
      return ctx.write(buf, promise);
    } catch (Throwable t) {
      return promise.setFailure(t);
    }
  }
  

  public ChannelFuture writeFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload, ChannelPromise promise)
  {
    boolean releaseData = true;
    Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator = new Http2CodecUtil.SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
    try
    {
      verifyStreamOrConnectionId(streamId, "Stream ID");
      ByteBuf buf = ctx.alloc().buffer(9);
      Http2CodecUtil.writeFrameHeaderInternal(buf, payload.readableBytes(), frameType, flags, streamId);
      ctx.write(buf, promiseAggregator.newPromise());
      
      releaseData = false;
      ctx.write(payload, promiseAggregator.newPromise());
    } catch (Throwable t) {
      if (releaseData) {
        payload.release();
      }
      promiseAggregator.setFailure(t);
    }
    return promiseAggregator.doneAllocatingPromises();
  }
  

  private ChannelFuture writeHeadersInternal(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, boolean hasPriority, int streamDependency, short weight, boolean exclusive, ChannelPromise promise)
  {
    ByteBuf headerBlock = null;
    Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator = new Http2CodecUtil.SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
    try
    {
      verifyStreamId(streamId, "Stream ID");
      if (hasPriority) {
        verifyStreamOrConnectionId(streamDependency, "Stream Dependency");
        Http2CodecUtil.verifyPadding(padding);
        verifyWeight(weight);
      }
      

      headerBlock = ctx.alloc().buffer();
      headersEncoder.encodeHeaders(headers, headerBlock);
      
      Http2Flags flags = new Http2Flags().endOfStream(endStream).priorityPresent(hasPriority).paddingPresent(padding > 0);
      


      int nonFragmentBytes = padding + flags.getNumPriorityBytes();
      int maxFragmentLength = maxFrameSize - nonFragmentBytes;
      ByteBuf fragment = headerBlock.readRetainedSlice(Math.min(headerBlock.readableBytes(), maxFragmentLength));
      

      flags.endOfHeaders(!headerBlock.isReadable());
      
      int payloadLength = fragment.readableBytes() + nonFragmentBytes;
      ByteBuf buf = ctx.alloc().buffer(15);
      Http2CodecUtil.writeFrameHeaderInternal(buf, payloadLength, (byte)1, flags, streamId);
      writePaddingLength(buf, padding);
      
      if (hasPriority) {
        long word1 = exclusive ? 0x80000000 | streamDependency : streamDependency;
        Http2CodecUtil.writeUnsignedInt(word1, buf);
        

        buf.writeByte(weight - 1);
      }
      ctx.write(buf, promiseAggregator.newPromise());
      

      ctx.write(fragment, promiseAggregator.newPromise());
      

      if (paddingBytes(padding) > 0) {
        ctx.write(ZERO_BUFFER.slice(0, paddingBytes(padding)), promiseAggregator.newPromise());
      }
      
      if (!flags.endOfHeaders()) {
        writeContinuationFrames(ctx, streamId, headerBlock, padding, promiseAggregator);
      }
    } catch (Throwable t) {
      promiseAggregator.setFailure(t);
    } finally {
      if (headerBlock != null) {
        headerBlock.release();
      }
    }
    return promiseAggregator.doneAllocatingPromises();
  }
  



  private ChannelFuture writeContinuationFrames(ChannelHandlerContext ctx, int streamId, ByteBuf headerBlock, int padding, Http2CodecUtil.SimpleChannelPromiseAggregator promiseAggregator)
  {
    Http2Flags flags = new Http2Flags().paddingPresent(padding > 0);
    int maxFragmentLength = maxFrameSize - padding;
    
    if (maxFragmentLength <= 0) {
      return promiseAggregator.setFailure(new IllegalArgumentException("Padding [" + padding + "] is too large for max frame size [" + maxFrameSize + "]"));
    }
    

    if (headerBlock.isReadable())
    {
      int fragmentReadableBytes = Math.min(headerBlock.readableBytes(), maxFragmentLength);
      int payloadLength = fragmentReadableBytes + padding;
      ByteBuf buf = ctx.alloc().buffer(10);
      Http2CodecUtil.writeFrameHeaderInternal(buf, payloadLength, (byte)9, flags, streamId);
      writePaddingLength(buf, padding);
      do
      {
        fragmentReadableBytes = Math.min(headerBlock.readableBytes(), maxFragmentLength);
        ByteBuf fragment = headerBlock.readRetainedSlice(fragmentReadableBytes);
        
        payloadLength = fragmentReadableBytes + padding;
        if (headerBlock.isReadable()) {
          ctx.write(buf.retain(), promiseAggregator.newPromise());
        }
        else {
          flags = flags.endOfHeaders(true);
          buf.release();
          buf = ctx.alloc().buffer(10);
          Http2CodecUtil.writeFrameHeaderInternal(buf, payloadLength, (byte)9, flags, streamId);
          writePaddingLength(buf, padding);
          ctx.write(buf, promiseAggregator.newPromise());
        }
        
        ctx.write(fragment, promiseAggregator.newPromise());
        

        if (paddingBytes(padding) > 0) {
          ctx.write(ZERO_BUFFER.slice(0, paddingBytes(padding)), promiseAggregator.newPromise());
        }
      } while (headerBlock.isReadable());
    }
    return promiseAggregator;
  }
  




  private static int paddingBytes(int padding)
  {
    return padding - 1;
  }
  
  private static void writePaddingLength(ByteBuf buf, int padding) {
    if (padding > 0)
    {

      buf.writeByte(padding - 1);
    }
  }
  
  private static void verifyStreamId(int streamId, String argumentName) {
    if (streamId <= 0) {
      throw new IllegalArgumentException(argumentName + " must be > 0");
    }
  }
  
  private static void verifyStreamOrConnectionId(int streamId, String argumentName) {
    if (streamId < 0) {
      throw new IllegalArgumentException(argumentName + " must be >= 0");
    }
  }
  
  private static void verifyWeight(short weight) {
    if ((weight < 1) || (weight > 256)) {
      throw new IllegalArgumentException("Invalid weight: " + weight);
    }
  }
  
  private static void verifyErrorCode(long errorCode) {
    if ((errorCode < 0L) || (errorCode > 4294967295L)) {
      throw new IllegalArgumentException("Invalid errorCode: " + errorCode);
    }
  }
  
  private static void verifyWindowSizeIncrement(int windowSizeIncrement) {
    if (windowSizeIncrement < 0) {
      throw new IllegalArgumentException("WindowSizeIncrement must be >= 0");
    }
  }
  
  private static void verifyPingPayload(ByteBuf data) {
    if ((data == null) || (data.readableBytes() != 8)) {
      throw new IllegalArgumentException("Opaque data must be 8 bytes");
    }
  }
  

  private static final class DataFrameHeader
  {
    private final int streamId;
    
    private final ByteBuf buffer;
    
    private final Http2Flags flags = new Http2Flags();
    
    private int prevData;
    
    private int prevPadding;
    private ByteBuf frameHeader;
    
    DataFrameHeader(ChannelHandlerContext ctx, int streamId)
    {
      buffer = ctx.alloc().buffer(30);
      this.streamId = streamId;
    }
    




    ByteBuf slice(int data, int padding, boolean endOfStream)
    {
      if ((data != prevData) || (padding != prevPadding) || (endOfStream != flags.endOfStream()) || (frameHeader == null))
      {

        prevData = data;
        prevPadding = padding;
        flags.paddingPresent(padding > 0);
        flags.endOfStream(endOfStream);
        frameHeader = buffer.readSlice(10).writerIndex(0);
        
        int payloadLength = data + padding;
        Http2CodecUtil.writeFrameHeaderInternal(frameHeader, payloadLength, (byte)0, flags, streamId);
        DefaultHttp2FrameWriter.writePaddingLength(frameHeader, padding);
      }
      return frameHeader.slice();
    }
    
    void release() {
      buffer.release();
    }
  }
}
