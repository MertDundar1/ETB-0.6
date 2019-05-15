package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.PlatformDependent;


















































public class DefaultHttp2FrameReader
  implements Http2FrameReader, Http2FrameSizePolicy, Http2FrameReader.Configuration
{
  private final Http2HeadersDecoder headersDecoder;
  private boolean readingHeaders = true;
  
  private boolean readError;
  
  private byte frameType;
  
  private int streamId;
  
  private Http2Flags flags;
  
  private int payloadLength;
  
  private HeadersContinuation headersContinuation;
  
  private int maxFrameSize;
  

  public DefaultHttp2FrameReader()
  {
    this(true);
  }
  




  public DefaultHttp2FrameReader(boolean validateHeaders)
  {
    this(new DefaultHttp2HeadersDecoder(validateHeaders));
  }
  
  public DefaultHttp2FrameReader(Http2HeadersDecoder headersDecoder) {
    this.headersDecoder = headersDecoder;
    maxFrameSize = 16384;
  }
  
  public Http2HeaderTable headerTable()
  {
    return headersDecoder.configuration().headerTable();
  }
  
  public Http2FrameReader.Configuration configuration()
  {
    return this;
  }
  
  public Http2FrameSizePolicy frameSizePolicy()
  {
    return this;
  }
  
  public void maxFrameSize(int max) throws Http2Exception
  {
    if (!Http2CodecUtil.isMaxFrameSizeValid(max)) {
      throw Http2Exception.streamError(streamId, Http2Error.FRAME_SIZE_ERROR, "Invalid MAX_FRAME_SIZE specified in sent settings: %d", new Object[] { Integer.valueOf(max) });
    }
    
    maxFrameSize = max;
  }
  
  public int maxFrameSize()
  {
    return maxFrameSize;
  }
  
  public void close()
  {
    if (headersContinuation != null) {
      headersContinuation.close();
    }
  }
  
  public void readFrame(ChannelHandlerContext ctx, ByteBuf input, Http2FrameListener listener)
    throws Http2Exception
  {
    if (readError) {
      input.skipBytes(input.readableBytes());
      return;
    }
    try {
      do {
        if (readingHeaders) {
          processHeaderState(input);
          if (readingHeaders)
          {
            return;
          }
        }
        





        processPayloadState(ctx, input, listener);
        if (!readingHeaders)
        {
          return;
        }
      } while (input.isReadable());
    } catch (Http2Exception e) {
      readError = (!Http2Exception.isStreamError(e));
      throw e;
    } catch (RuntimeException e) {
      readError = true;
      throw e;
    } catch (Throwable cause) {
      readError = true;
      PlatformDependent.throwException(cause);
    }
  }
  
  private void processHeaderState(ByteBuf in) throws Http2Exception {
    if (in.readableBytes() < 9)
    {
      return;
    }
    

    payloadLength = in.readUnsignedMedium();
    if (payloadLength > maxFrameSize) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length: %d exceeds maximum: %d", new Object[] { Integer.valueOf(payloadLength), Integer.valueOf(maxFrameSize) });
    }
    
    frameType = in.readByte();
    flags = new Http2Flags(in.readUnsignedByte());
    streamId = Http2CodecUtil.readUnsignedInt(in);
    

    readingHeaders = false;
    
    switch (frameType) {
    case 0: 
      verifyDataFrame();
      break;
    case 1: 
      verifyHeadersFrame();
      break;
    case 2: 
      verifyPriorityFrame();
      break;
    case 3: 
      verifyRstStreamFrame();
      break;
    case 4: 
      verifySettingsFrame();
      break;
    case 5: 
      verifyPushPromiseFrame();
      break;
    case 6: 
      verifyPingFrame();
      break;
    case 7: 
      verifyGoAwayFrame();
      break;
    case 8: 
      verifyWindowUpdateFrame();
      break;
    case 9: 
      verifyContinuationFrame();
      break;
    }
    
  }
  

  private void processPayloadState(ChannelHandlerContext ctx, ByteBuf in, Http2FrameListener listener)
    throws Http2Exception
  {
    if (in.readableBytes() < payloadLength)
    {
      return;
    }
    

    ByteBuf payload = in.readSlice(payloadLength);
    

    readingHeaders = true;
    

    switch (frameType) {
    case 0: 
      readDataFrame(ctx, payload, listener);
      break;
    case 1: 
      readHeadersFrame(ctx, payload, listener);
      break;
    case 2: 
      readPriorityFrame(ctx, payload, listener);
      break;
    case 3: 
      readRstStreamFrame(ctx, payload, listener);
      break;
    case 4: 
      readSettingsFrame(ctx, payload, listener);
      break;
    case 5: 
      readPushPromiseFrame(ctx, payload, listener);
      break;
    case 6: 
      readPingFrame(ctx, payload, listener);
      break;
    case 7: 
      readGoAwayFrame(ctx, payload, listener);
      break;
    case 8: 
      readWindowUpdateFrame(ctx, payload, listener);
      break;
    case 9: 
      readContinuationFrame(payload, listener);
      break;
    default: 
      readUnknownFrame(ctx, payload, listener);
    }
  }
  
  private void verifyDataFrame() throws Http2Exception
  {
    verifyAssociatedWithAStream();
    verifyNotProcessingHeaders();
    verifyPayloadLength(payloadLength);
    
    if (payloadLength < flags.getPaddingPresenceFieldLength()) {
      throw Http2Exception.streamError(streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyHeadersFrame() throws Http2Exception
  {
    verifyAssociatedWithAStream();
    verifyNotProcessingHeaders();
    verifyPayloadLength(payloadLength);
    
    int requiredLength = flags.getPaddingPresenceFieldLength() + flags.getNumPriorityBytes();
    if (payloadLength < requiredLength) {
      throw Http2Exception.streamError(streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length too small." + payloadLength, new Object[0]);
    }
  }
  
  private void verifyPriorityFrame() throws Http2Exception
  {
    verifyAssociatedWithAStream();
    verifyNotProcessingHeaders();
    
    if (payloadLength != 5) {
      throw Http2Exception.streamError(streamId, Http2Error.FRAME_SIZE_ERROR, "Invalid frame length %d.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyRstStreamFrame() throws Http2Exception
  {
    verifyAssociatedWithAStream();
    verifyNotProcessingHeaders();
    
    if (payloadLength != 4) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Invalid frame length %d.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifySettingsFrame() throws Http2Exception {
    verifyNotProcessingHeaders();
    verifyPayloadLength(payloadLength);
    if (streamId != 0) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A stream ID must be zero.", new Object[0]);
    }
    if ((flags.ack()) && (payloadLength > 0)) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Ack settings frame must have an empty payload.", new Object[0]);
    }
    if (payloadLength % 6 > 0) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length %d invalid.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyPushPromiseFrame() throws Http2Exception {
    verifyNotProcessingHeaders();
    verifyPayloadLength(payloadLength);
    


    int minLength = flags.getPaddingPresenceFieldLength() + 4;
    if (payloadLength < minLength) {
      throw Http2Exception.streamError(streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyPingFrame() throws Http2Exception
  {
    verifyNotProcessingHeaders();
    if (streamId != 0) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A stream ID must be zero.", new Object[0]);
    }
    if (payloadLength != 8) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length %d incorrect size for ping.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyGoAwayFrame() throws Http2Exception
  {
    verifyNotProcessingHeaders();
    verifyPayloadLength(payloadLength);
    
    if (streamId != 0) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A stream ID must be zero.", new Object[0]);
    }
    if (payloadLength < 8) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyWindowUpdateFrame() throws Http2Exception {
    verifyNotProcessingHeaders();
    verifyStreamOrConnectionId(streamId, "Stream ID");
    
    if (payloadLength != 4) {
      throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Invalid frame length %d.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyContinuationFrame() throws Http2Exception {
    verifyAssociatedWithAStream();
    verifyPayloadLength(payloadLength);
    
    if (headersContinuation == null) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Received %s frame but not currently processing headers.", new Object[] { Byte.valueOf(frameType) });
    }
    

    if (streamId != headersContinuation.getStreamId()) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Continuation stream ID does not match pending headers. Expected %d, but received %d.", new Object[] { Integer.valueOf(headersContinuation.getStreamId()), Integer.valueOf(streamId) });
    }
    

    if (payloadLength < flags.getPaddingPresenceFieldLength()) {
      throw Http2Exception.streamError(streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small for padding.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void readDataFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener)
    throws Http2Exception
  {
    int padding = readPadding(payload);
    verifyPadding(padding);
    


    int dataLength = lengthWithoutTrailingPadding(payload.readableBytes(), padding);
    
    ByteBuf data = payload.readSlice(dataLength);
    listener.onDataRead(ctx, streamId, data, padding, flags.endOfStream());
    payload.skipBytes(payload.readableBytes());
  }
  
  private void readHeadersFrame(final ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    final int headersStreamId = streamId;
    final Http2Flags headersFlags = flags;
    final int padding = readPadding(payload);
    verifyPadding(padding);
    


    if (flags.priorityPresent()) {
      long word1 = payload.readUnsignedInt();
      final boolean exclusive = (word1 & 0x80000000) != 0L;
      final int streamDependency = (int)(word1 & 0x7FFFFFFF);
      if (streamDependency == streamId) {
        throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, "A stream cannot depend on itself.", new Object[0]);
      }
      final short weight = (short)(payload.readUnsignedByte() + 1);
      ByteBuf fragment = payload.readSlice(lengthWithoutTrailingPadding(payload.readableBytes(), padding));
      

      headersContinuation = new HeadersContinuation(headersStreamId, ctx)
      {
        public int getStreamId() {
          return headersStreamId;
        }
        
        public void processFragment(boolean endOfHeaders, ByteBuf fragment, Http2FrameListener listener)
          throws Http2Exception
        {
          DefaultHttp2FrameReader.HeadersBlockBuilder hdrBlockBuilder = headersBlockBuilder();
          hdrBlockBuilder.addFragment(fragment, ctx.alloc(), endOfHeaders);
          if (endOfHeaders) {
            listener.onHeadersRead(ctx, headersStreamId, hdrBlockBuilder.headers(), streamDependency, weight, exclusive, padding, headersFlags.endOfStream());
          }
          
        }
        

      };
      headersContinuation.processFragment(flags.endOfHeaders(), fragment, listener);
      return;
    }
    


    headersContinuation = new HeadersContinuation(headersStreamId, ctx)
    {
      public int getStreamId() {
        return headersStreamId;
      }
      
      public void processFragment(boolean endOfHeaders, ByteBuf fragment, Http2FrameListener listener)
        throws Http2Exception
      {
        DefaultHttp2FrameReader.HeadersBlockBuilder hdrBlockBuilder = headersBlockBuilder();
        hdrBlockBuilder.addFragment(fragment, ctx.alloc(), endOfHeaders);
        if (endOfHeaders) {
          listener.onHeadersRead(ctx, headersStreamId, hdrBlockBuilder.headers(), padding, headersFlags.endOfStream());
        }
        
      }
      

    };
    ByteBuf fragment = payload.readSlice(lengthWithoutTrailingPadding(payload.readableBytes(), padding));
    headersContinuation.processFragment(flags.endOfHeaders(), fragment, listener);
  }
  
  private void readPriorityFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    long word1 = payload.readUnsignedInt();
    boolean exclusive = (word1 & 0x80000000) != 0L;
    int streamDependency = (int)(word1 & 0x7FFFFFFF);
    if (streamDependency == streamId) {
      throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, "A stream cannot depend on itself.", new Object[0]);
    }
    short weight = (short)(payload.readUnsignedByte() + 1);
    listener.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
  }
  
  private void readRstStreamFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    long errorCode = payload.readUnsignedInt();
    listener.onRstStreamRead(ctx, streamId, errorCode);
  }
  
  private void readSettingsFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    if (flags.ack()) {
      listener.onSettingsAckRead(ctx);
    } else {
      int numSettings = payloadLength / 6;
      Http2Settings settings = new Http2Settings();
      for (int index = 0; index < numSettings; index++) {
        char id = (char)payload.readUnsignedShort();
        long value = payload.readUnsignedInt();
        try {
          settings.put(id, Long.valueOf(value));
        } catch (IllegalArgumentException e) {
          switch (id) {
          case '\005': 
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, e, e.getMessage(), new Object[0]); }
        }
        throw Http2Exception.connectionError(Http2Error.FLOW_CONTROL_ERROR, e, e.getMessage(), new Object[0]);
        
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, e, e.getMessage(), new Object[0]);
      }
      

      listener.onSettingsRead(ctx, settings);
    }
  }
  
  private void readPushPromiseFrame(final ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    final int pushPromiseStreamId = streamId;
    final int padding = readPadding(payload);
    verifyPadding(padding);
    final int promisedStreamId = Http2CodecUtil.readUnsignedInt(payload);
    

    headersContinuation = new HeadersContinuation(pushPromiseStreamId, ctx)
    {
      public int getStreamId() {
        return pushPromiseStreamId;
      }
      
      public void processFragment(boolean endOfHeaders, ByteBuf fragment, Http2FrameListener listener)
        throws Http2Exception
      {
        headersBlockBuilder().addFragment(fragment, ctx.alloc(), endOfHeaders);
        if (endOfHeaders) {
          listener.onPushPromiseRead(ctx, pushPromiseStreamId, promisedStreamId, headersBlockBuilder().headers(), padding);
        }
        
      }
      

    };
    ByteBuf fragment = payload.readSlice(lengthWithoutTrailingPadding(payload.readableBytes(), padding));
    headersContinuation.processFragment(flags.endOfHeaders(), fragment, listener);
  }
  
  private void readPingFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    ByteBuf data = payload.readSlice(payload.readableBytes());
    if (flags.ack()) {
      listener.onPingAckRead(ctx, data);
    } else {
      listener.onPingRead(ctx, data);
    }
  }
  
  private static void readGoAwayFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    int lastStreamId = Http2CodecUtil.readUnsignedInt(payload);
    long errorCode = payload.readUnsignedInt();
    ByteBuf debugData = payload.readSlice(payload.readableBytes());
    listener.onGoAwayRead(ctx, lastStreamId, errorCode, debugData);
  }
  
  private void readWindowUpdateFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener) throws Http2Exception
  {
    int windowSizeIncrement = Http2CodecUtil.readUnsignedInt(payload);
    if (windowSizeIncrement == 0) {
      throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, "Received WINDOW_UPDATE with delta 0 for stream: %d", new Object[] { Integer.valueOf(streamId) });
    }
    
    listener.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
  }
  
  private void readContinuationFrame(ByteBuf payload, Http2FrameListener listener)
    throws Http2Exception
  {
    ByteBuf continuationFragment = payload.readSlice(payload.readableBytes());
    headersContinuation.processFragment(flags.endOfHeaders(), continuationFragment, listener);
  }
  
  private void readUnknownFrame(ChannelHandlerContext ctx, ByteBuf payload, Http2FrameListener listener)
    throws Http2Exception
  {
    payload = payload.readSlice(payload.readableBytes());
    listener.onUnknownFrame(ctx, frameType, streamId, flags, payload);
  }
  



  private int readPadding(ByteBuf payload)
  {
    if (!flags.paddingPresent()) {
      return 0;
    }
    return payload.readUnsignedByte() + 1;
  }
  
  private void verifyPadding(int padding) throws Http2Exception {
    int len = lengthWithoutTrailingPadding(payloadLength, padding);
    if (len < 0) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Frame payload too small for padding.", new Object[0]);
    }
  }
  



  private static int lengthWithoutTrailingPadding(int readableBytes, int padding)
  {
    return padding == 0 ? readableBytes : readableBytes - (padding - 1);
  }
  






  private abstract class HeadersContinuation
  {
    private final DefaultHttp2FrameReader.HeadersBlockBuilder builder = new DefaultHttp2FrameReader.HeadersBlockBuilder(DefaultHttp2FrameReader.this);
    


    private HeadersContinuation() {}
    


    abstract int getStreamId();
    


    abstract void processFragment(boolean paramBoolean, ByteBuf paramByteBuf, Http2FrameListener paramHttp2FrameListener)
      throws Http2Exception;
    

    final DefaultHttp2FrameReader.HeadersBlockBuilder headersBlockBuilder()
    {
      return builder;
    }
    


    final void close()
    {
      builder.close();
    }
  }
  

  protected class HeadersBlockBuilder
  {
    private ByteBuf headerBlock;
    

    protected HeadersBlockBuilder() {}
    

    private void headerSizeExceeded()
      throws Http2Exception
    {
      close();
      Http2CodecUtil.headerListSizeExceeded(streamId, headersDecoder.configuration().headerTable().maxHeaderListSize());
    }
    







    final void addFragment(ByteBuf fragment, ByteBufAllocator alloc, boolean endOfHeaders)
      throws Http2Exception
    {
      if (headerBlock == null) {
        if (fragment.readableBytes() > headersDecoder.configuration().headerTable().maxHeaderListSize()) {
          headerSizeExceeded();
        }
        if (endOfHeaders)
        {

          headerBlock = fragment.retain();
        } else {
          headerBlock = alloc.buffer(fragment.readableBytes());
          headerBlock.writeBytes(fragment);
        }
        return;
      }
      if (headersDecoder.configuration().headerTable().maxHeaderListSize() - fragment.readableBytes() < headerBlock.readableBytes())
      {
        headerSizeExceeded();
      }
      if (headerBlock.isWritable(fragment.readableBytes()))
      {
        headerBlock.writeBytes(fragment);
      }
      else {
        ByteBuf buf = alloc.buffer(headerBlock.readableBytes() + fragment.readableBytes());
        buf.writeBytes(headerBlock);
        buf.writeBytes(fragment);
        headerBlock.release();
        headerBlock = buf;
      }
    }
    

    Http2Headers headers()
      throws Http2Exception
    {
      try
      {
        return headersDecoder.decodeHeaders(streamId, headerBlock);
      } finally {
        close();
      }
    }
    


    void close()
    {
      if (headerBlock != null) {
        headerBlock.release();
        headerBlock = null;
      }
      

      headersContinuation = null;
    }
  }
  
  private void verifyNotProcessingHeaders() throws Http2Exception {
    if (headersContinuation != null) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Received frame of type %s while processing headers.", new Object[] { Byte.valueOf(frameType) });
    }
  }
  
  private void verifyPayloadLength(int payloadLength) throws Http2Exception {
    if (payloadLength > maxFrameSize) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Total payload length %d exceeds max frame length.", new Object[] { Integer.valueOf(payloadLength) });
    }
  }
  
  private void verifyAssociatedWithAStream() throws Http2Exception {
    if (streamId == 0) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Frame of type %s must be associated with a stream.", new Object[] { Byte.valueOf(frameType) });
    }
  }
  
  private static void verifyStreamOrConnectionId(int streamId, String argumentName) throws Http2Exception
  {
    if (streamId < 0) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "%s must be >= 0", new Object[] { argumentName });
    }
  }
}
