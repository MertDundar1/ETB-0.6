package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.Arrays;
import java.util.List;


























public class SnappyFramedDecoder
  extends ByteToMessageDecoder
{
  private static enum ChunkType
  {
    STREAM_IDENTIFIER, 
    COMPRESSED_DATA, 
    UNCOMPRESSED_DATA, 
    RESERVED_UNSKIPPABLE, 
    RESERVED_SKIPPABLE;
    
    private ChunkType() {} }
  private static final byte[] SNAPPY = { 115, 78, 97, 80, 112, 89 };
  
  private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
  private final Snappy snappy = new Snappy();
  

  private final boolean validateChecksums;
  
  private boolean started;
  
  private boolean corrupted;
  

  public SnappyFramedDecoder()
  {
    this(false);
  }
  








  public SnappyFramedDecoder(boolean validateChecksums)
  {
    this.validateChecksums = validateChecksums;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    if (corrupted) {
      in.skipBytes(in.readableBytes());
      return;
    }
    try
    {
      int idx = in.readerIndex();
      int inSize = in.readableBytes();
      if (inSize < 4)
      {

        return;
      }
      
      int chunkTypeVal = in.getUnsignedByte(idx);
      ChunkType chunkType = mapChunkType((byte)chunkTypeVal);
      int chunkLength = ByteBufUtil.swapMedium(in.getUnsignedMedium(idx + 1));
      
      switch (1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[chunkType.ordinal()]) {
      case 1: 
        if (chunkLength != SNAPPY.length) {
          throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
        }
        
        if (inSize >= 4 + SNAPPY.length)
        {


          byte[] identifier = new byte[chunkLength];
          in.skipBytes(4).readBytes(identifier);
          
          if (!Arrays.equals(identifier, SNAPPY)) {
            throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
          }
          

          started = true; }
        break;
      case 2: 
        if (!started) {
          throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
        }
        
        if (inSize < 4 + chunkLength)
        {
          return;
        }
        
        in.skipBytes(4 + chunkLength);
        break;
      


      case 3: 
        throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(chunkTypeVal));
      
      case 4: 
        if (!started) {
          throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
        }
        if (chunkLength > 65540) {
          throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
        }
        
        if (inSize < 4 + chunkLength) {
          return;
        }
        
        in.skipBytes(4);
        if (validateChecksums) {
          int checksum = ByteBufUtil.swapInt(in.readInt());
          Snappy.validateChecksum(checksum, in, in.readerIndex(), chunkLength - 4);
        } else {
          in.skipBytes(4);
        }
        out.add(in.readSlice(chunkLength - 4).retain());
        break;
      case 5: 
        if (!started) {
          throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
        }
        
        if (inSize < 4 + chunkLength) {
          return;
        }
        
        in.skipBytes(4);
        int checksum = ByteBufUtil.swapInt(in.readInt());
        ByteBuf uncompressed = ctx.alloc().buffer(0);
        if (validateChecksums) {
          int oldWriterIndex = in.writerIndex();
          try {
            in.writerIndex(in.readerIndex() + chunkLength - 4);
            snappy.decode(in, uncompressed);
          } finally {
            in.writerIndex(oldWriterIndex);
          }
          Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
        } else {
          snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
        }
        out.add(uncompressed);
        snappy.reset();
      }
    }
    catch (Exception e) {
      corrupted = true;
      throw e;
    }
  }
  





  private static ChunkType mapChunkType(byte type)
  {
    if (type == 0)
      return ChunkType.COMPRESSED_DATA;
    if (type == 1)
      return ChunkType.UNCOMPRESSED_DATA;
    if (type == -1)
      return ChunkType.STREAM_IDENTIFIER;
    if ((type & 0x80) == 128) {
      return ChunkType.RESERVED_SKIPPABLE;
    }
    return ChunkType.RESERVED_UNSKIPPABLE;
  }
}
