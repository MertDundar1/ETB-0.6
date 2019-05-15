package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import java.security.PrivateKey;































public final class PemPrivateKey
  extends AbstractReferenceCounted
  implements PrivateKey, PemEncoded
{
  private static final byte[] BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
  private static final byte[] END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
  private static final String PKCS8_FORMAT = "PKCS#8";
  private final ByteBuf content;
  
  /* Error */
  static PemEncoded toPEM(io.netty.buffer.ByteBufAllocator allocator, boolean useDirect, PrivateKey key)
  {
    // Byte code:
    //   0: aload_2
    //   1: instanceof 1
    //   4: ifeq +13 -> 17
    //   7: aload_2
    //   8: checkcast 1	io/netty/handler/ssl/PemEncoded
    //   11: invokeinterface 2 1 0
    //   16: areturn
    //   17: aload_2
    //   18: invokeinterface 3 1 0
    //   23: invokestatic 4	io/netty/buffer/Unpooled:wrappedBuffer	([B)Lio/netty/buffer/ByteBuf;
    //   26: astore_3
    //   27: aload_0
    //   28: aload_3
    //   29: invokestatic 5	io/netty/handler/ssl/SslUtils:toBase64	(Lio/netty/buffer/ByteBufAllocator;Lio/netty/buffer/ByteBuf;)Lio/netty/buffer/ByteBuf;
    //   32: astore 4
    //   34: getstatic 6	io/netty/handler/ssl/PemPrivateKey:BEGIN_PRIVATE_KEY	[B
    //   37: arraylength
    //   38: aload 4
    //   40: invokevirtual 7	io/netty/buffer/ByteBuf:readableBytes	()I
    //   43: iadd
    //   44: getstatic 8	io/netty/handler/ssl/PemPrivateKey:END_PRIVATE_KEY	[B
    //   47: arraylength
    //   48: iadd
    //   49: istore 5
    //   51: iconst_0
    //   52: istore 6
    //   54: iload_1
    //   55: ifeq +14 -> 69
    //   58: aload_0
    //   59: iload 5
    //   61: invokeinterface 9 2 0
    //   66: goto +11 -> 77
    //   69: aload_0
    //   70: iload 5
    //   72: invokeinterface 10 2 0
    //   77: astore 7
    //   79: aload 7
    //   81: getstatic 6	io/netty/handler/ssl/PemPrivateKey:BEGIN_PRIVATE_KEY	[B
    //   84: invokevirtual 11	io/netty/buffer/ByteBuf:writeBytes	([B)Lio/netty/buffer/ByteBuf;
    //   87: pop
    //   88: aload 7
    //   90: aload 4
    //   92: invokevirtual 12	io/netty/buffer/ByteBuf:writeBytes	(Lio/netty/buffer/ByteBuf;)Lio/netty/buffer/ByteBuf;
    //   95: pop
    //   96: aload 7
    //   98: getstatic 8	io/netty/handler/ssl/PemPrivateKey:END_PRIVATE_KEY	[B
    //   101: invokevirtual 11	io/netty/buffer/ByteBuf:writeBytes	([B)Lio/netty/buffer/ByteBuf;
    //   104: pop
    //   105: new 13	io/netty/handler/ssl/PemValue
    //   108: dup
    //   109: aload 7
    //   111: iconst_1
    //   112: invokespecial 14	io/netty/handler/ssl/PemValue:<init>	(Lio/netty/buffer/ByteBuf;Z)V
    //   115: astore 8
    //   117: iconst_1
    //   118: istore 6
    //   120: aload 8
    //   122: astore 9
    //   124: iload 6
    //   126: ifne +8 -> 134
    //   129: aload 7
    //   131: invokestatic 15	io/netty/handler/ssl/SslUtils:zerooutAndRelease	(Lio/netty/buffer/ByteBuf;)V
    //   134: aload 4
    //   136: invokestatic 15	io/netty/handler/ssl/SslUtils:zerooutAndRelease	(Lio/netty/buffer/ByteBuf;)V
    //   139: aload_3
    //   140: invokestatic 15	io/netty/handler/ssl/SslUtils:zerooutAndRelease	(Lio/netty/buffer/ByteBuf;)V
    //   143: aload 9
    //   145: areturn
    //   146: astore 10
    //   148: iload 6
    //   150: ifne +8 -> 158
    //   153: aload 7
    //   155: invokestatic 15	io/netty/handler/ssl/SslUtils:zerooutAndRelease	(Lio/netty/buffer/ByteBuf;)V
    //   158: aload 10
    //   160: athrow
    //   161: astore 11
    //   163: aload 4
    //   165: invokestatic 15	io/netty/handler/ssl/SslUtils:zerooutAndRelease	(Lio/netty/buffer/ByteBuf;)V
    //   168: aload 11
    //   170: athrow
    //   171: astore 12
    //   173: aload_3
    //   174: invokestatic 15	io/netty/handler/ssl/SslUtils:zerooutAndRelease	(Lio/netty/buffer/ByteBuf;)V
    //   177: aload 12
    //   179: athrow
    // Line number table:
    //   Java source line #58	-> byte code offset #0
    //   Java source line #59	-> byte code offset #7
    //   Java source line #62	-> byte code offset #17
    //   Java source line #64	-> byte code offset #27
    //   Java source line #66	-> byte code offset #34
    //   Java source line #68	-> byte code offset #51
    //   Java source line #69	-> byte code offset #54
    //   Java source line #71	-> byte code offset #79
    //   Java source line #72	-> byte code offset #88
    //   Java source line #73	-> byte code offset #96
    //   Java source line #75	-> byte code offset #105
    //   Java source line #76	-> byte code offset #117
    //   Java source line #77	-> byte code offset #120
    //   Java source line #80	-> byte code offset #124
    //   Java source line #81	-> byte code offset #129
    //   Java source line #85	-> byte code offset #134
    //   Java source line #88	-> byte code offset #139
    //   Java source line #80	-> byte code offset #146
    //   Java source line #81	-> byte code offset #153
    //   Java source line #85	-> byte code offset #161
    //   Java source line #88	-> byte code offset #171
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	allocator	io.netty.buffer.ByteBufAllocator
    //   0	180	1	useDirect	boolean
    //   0	180	2	key	PrivateKey
    //   26	148	3	encoded	ByteBuf
    //   32	132	4	base64	ByteBuf
    //   49	22	5	size	int
    //   52	97	6	success	boolean
    //   77	77	7	pem	ByteBuf
    //   115	6	8	value	PemValue
    //   122	22	9	localPemValue1	PemValue
    //   146	13	10	localObject1	Object
    //   161	8	11	localObject2	Object
    //   171	7	12	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   79	124	146	finally
    //   146	148	146	finally
    //   34	134	161	finally
    //   146	163	161	finally
    //   27	139	171	finally
    //   146	173	171	finally
  }
  
  public static PemPrivateKey valueOf(byte[] key)
  {
    return valueOf(Unpooled.wrappedBuffer(key));
  }
  





  public static PemPrivateKey valueOf(ByteBuf key)
  {
    return new PemPrivateKey(key);
  }
  

  private PemPrivateKey(ByteBuf content)
  {
    this.content = ((ByteBuf)ObjectUtil.checkNotNull(content, "content"));
  }
  
  public boolean isSensitive()
  {
    return true;
  }
  
  public ByteBuf content()
  {
    int count = refCnt();
    if (count <= 0) {
      throw new IllegalReferenceCountException(count);
    }
    
    return content;
  }
  
  public PemPrivateKey copy()
  {
    return replace(content.copy());
  }
  
  public PemPrivateKey duplicate()
  {
    return replace(content.duplicate());
  }
  
  public PemPrivateKey retainedDuplicate()
  {
    return replace(content.retainedDuplicate());
  }
  
  public PemPrivateKey replace(ByteBuf content)
  {
    return new PemPrivateKey(content);
  }
  
  public PemPrivateKey touch()
  {
    content.touch();
    return this;
  }
  
  public PemPrivateKey touch(Object hint)
  {
    content.touch(hint);
    return this;
  }
  
  public PemPrivateKey retain()
  {
    return (PemPrivateKey)super.retain();
  }
  
  public PemPrivateKey retain(int increment)
  {
    return (PemPrivateKey)super.retain(increment);
  }
  


  protected void deallocate()
  {
    SslUtils.zerooutAndRelease(content);
  }
  
  public byte[] getEncoded()
  {
    throw new UnsupportedOperationException();
  }
  
  public String getAlgorithm()
  {
    throw new UnsupportedOperationException();
  }
  
  public String getFormat()
  {
    return "PKCS#8";
  }
  






  public void destroy()
  {
    release(refCnt());
  }
  






  public boolean isDestroyed()
  {
    return refCnt() == 0;
  }
}
