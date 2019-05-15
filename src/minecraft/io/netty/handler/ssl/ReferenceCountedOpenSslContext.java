package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeak;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.tomcat.jni.CertificateVerifier;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;


























public abstract class ReferenceCountedOpenSslContext
  extends SslContext
  implements ReferenceCounted
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);
  








  private static final boolean JDK_REJECT_CLIENT_INITIATED_RENEGOTIATION = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(SystemPropertyUtil.getBoolean("jdk.tls.rejectClientInitiatedRenegotiation", false));
    }
  })).booleanValue();
  

  private static final List<String> DEFAULT_CIPHERS;
  

  private static final Integer DH_KEY_LENGTH;
  

  private static final ResourceLeakDetector<ReferenceCountedOpenSslContext> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslContext.class);
  
  protected static final int VERIFY_DEPTH = 10;
  
  protected volatile long ctx;
  
  long aprPool;
  
  private volatile int aprPoolDestroyed;
  
  private final List<String> unmodifiableCiphers;
  
  private final long sessionCacheSize;
  
  private final long sessionTimeout;
  
  private final OpenSslApplicationProtocolNegotiator apn;
  
  private final int mode;
  
  private final ResourceLeak leak;
  private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted()
  {
    public ReferenceCounted touch(Object hint) {
      if (leak != null) {
        leak.record(hint);
      }
      
      return ReferenceCountedOpenSslContext.this;
    }
    
    protected void deallocate()
    {
      destroy();
      if (leak != null) {
        leak.close();
      }
    }
  };
  
  final Certificate[] keyCertChain;
  final ClientAuth clientAuth;
  final OpenSslEngineMap engineMap = new DefaultOpenSslEngineMap(null);
  
  volatile boolean rejectRemoteInitiatedRenegotiation;
  static final OpenSslApplicationProtocolNegotiator NONE_PROTOCOL_NEGOTIATOR = new OpenSslApplicationProtocolNegotiator()
  {
    public ApplicationProtocolConfig.Protocol protocol()
    {
      return ApplicationProtocolConfig.Protocol.NONE;
    }
    
    public List<String> protocols()
    {
      return Collections.emptyList();
    }
    
    public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior()
    {
      return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
    }
    
    public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior()
    {
      return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
    }
  };
  
  static {
    List<String> ciphers = new ArrayList();
    
    Collections.addAll(ciphers, new String[] { "ECDHE-ECDSA-AES256-GCM-SHA384", "ECDHE-ECDSA-AES128-GCM-SHA256", "ECDHE-RSA-AES128-GCM-SHA256", "ECDHE-RSA-AES128-SHA", "ECDHE-RSA-AES256-SHA", "AES128-GCM-SHA256", "AES128-SHA", "AES256-SHA" });
    








    DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
    
    if (logger.isDebugEnabled()) {
      logger.debug("Default cipher suite (OpenSSL): " + ciphers);
    }
    
    Integer dhLen = null;
    try
    {
      String dhKeySize = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run() {
          return SystemPropertyUtil.get("jdk.tls.ephemeralDHKeySize");
        }
      });
      if (dhKeySize != null) {
        try {
          dhLen = Integer.valueOf(dhKeySize);
        } catch (NumberFormatException e) {
          logger.debug("ReferenceCountedOpenSslContext supports -Djdk.tls.ephemeralDHKeySize={int}, but got: " + dhKeySize);
        }
      }
    }
    catch (Throwable localThrowable) {}
    

    DH_KEY_LENGTH = dhLen;
  }
  

  ReferenceCountedOpenSslContext(Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apnCfg, long sessionCacheSize, long sessionTimeout, int mode, Certificate[] keyCertChain, ClientAuth clientAuth, boolean startTls, boolean leakDetection)
    throws SSLException
  {
    this(ciphers, cipherFilter, toNegotiator(apnCfg), sessionCacheSize, sessionTimeout, mode, keyCertChain, clientAuth, startTls, leakDetection);
  }
  


  ReferenceCountedOpenSslContext(Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, int mode, Certificate[] keyCertChain, ClientAuth clientAuth, boolean startTls, boolean leakDetection)
    throws SSLException
  {
    super(startTls);
    
    OpenSsl.ensureAvailability();
    
    if ((mode != 1) && (mode != 0)) {
      throw new IllegalArgumentException("mode most be either SSL.SSL_MODE_SERVER or SSL.SSL_MODE_CLIENT");
    }
    leak = (leakDetection ? leakDetector.open(this) : null);
    this.mode = mode;
    this.clientAuth = (isServer() ? (ClientAuth)ObjectUtil.checkNotNull(clientAuth, "clientAuth") : ClientAuth.NONE);
    
    if (mode == 1) {
      rejectRemoteInitiatedRenegotiation = JDK_REJECT_CLIENT_INITIATED_RENEGOTIATION;
    }
    
    this.keyCertChain = (keyCertChain == null ? null : (Certificate[])keyCertChain.clone());
    List<String> convertedCiphers;
    List<String> convertedCiphers; if (ciphers == null) {
      convertedCiphers = null;
    } else {
      convertedCiphers = new ArrayList();
      for (String c : ciphers) {
        if (c == null) {
          break;
        }
        
        String converted = CipherSuiteConverter.toOpenSsl(c);
        if (converted != null) {
          c = converted;
        }
        convertedCiphers.add(c);
      }
    }
    
    unmodifiableCiphers = Arrays.asList(((CipherSuiteFilter)ObjectUtil.checkNotNull(cipherFilter, "cipherFilter")).filterCipherSuites(convertedCiphers, DEFAULT_CIPHERS, OpenSsl.availableCipherSuites()));
    

    this.apn = ((OpenSslApplicationProtocolNegotiator)ObjectUtil.checkNotNull(apn, "apn"));
    

    aprPool = Pool.create(0L);
    

    boolean success = false;
    try {
      synchronized (ReferenceCountedOpenSslContext.class) {
        try {
          ctx = SSLContext.make(aprPool, 31, mode);
        } catch (Exception e) {
          throw new SSLException("failed to create an SSL_CTX", e);
        }
        
        SSLContext.setOptions(ctx, 4095);
        SSLContext.setOptions(ctx, 16777216);
        SSLContext.setOptions(ctx, 33554432);
        SSLContext.setOptions(ctx, 4194304);
        SSLContext.setOptions(ctx, 524288);
        SSLContext.setOptions(ctx, 1048576);
        SSLContext.setOptions(ctx, 65536);
        



        SSLContext.setOptions(ctx, 16384);
        



        SSLContext.setMode(ctx, SSLContext.getMode(ctx) | 0x2);
        
        if (DH_KEY_LENGTH != null) {
          SSLContext.setTmpDHLength(ctx, DH_KEY_LENGTH.intValue());
        }
        
        try
        {
          SSLContext.setCipherSuite(ctx, CipherSuiteConverter.toOpenSsl(unmodifiableCiphers));
        } catch (SSLException e) {
          throw e;
        } catch (Exception e) {
          throw new SSLException("failed to set cipher suite: " + unmodifiableCiphers, e);
        }
        
        List<String> nextProtoList = apn.protocols();
        
        if (!nextProtoList.isEmpty()) {
          String[] protocols = (String[])nextProtoList.toArray(new String[nextProtoList.size()]);
          int selectorBehavior = opensslSelectorFailureBehavior(apn.selectorFailureBehavior());
          
          switch (5.$SwitchMap$io$netty$handler$ssl$ApplicationProtocolConfig$Protocol[apn.protocol().ordinal()]) {
          case 1: 
            SSLContext.setNpnProtos(ctx, protocols, selectorBehavior);
            break;
          case 2: 
            SSLContext.setAlpnProtos(ctx, protocols, selectorBehavior);
            break;
          case 3: 
            SSLContext.setNpnProtos(ctx, protocols, selectorBehavior);
            SSLContext.setAlpnProtos(ctx, protocols, selectorBehavior);
            break;
          default: 
            throw new Error();
          }
          
        }
        
        if (sessionCacheSize > 0L) {
          this.sessionCacheSize = sessionCacheSize;
          SSLContext.setSessionCacheSize(ctx, sessionCacheSize);
        }
        else {
          this.sessionCacheSize = (sessionCacheSize = SSLContext.setSessionCacheSize(ctx, 20480L));
          
          SSLContext.setSessionCacheSize(ctx, sessionCacheSize);
        }
        

        if (sessionTimeout > 0L) {
          this.sessionTimeout = sessionTimeout;
          SSLContext.setSessionCacheTimeout(ctx, sessionTimeout);
        }
        else {
          this.sessionTimeout = (sessionTimeout = SSLContext.setSessionCacheTimeout(ctx, 300L));
          
          SSLContext.setSessionCacheTimeout(ctx, sessionTimeout);
        }
      }
      success = true;
    } finally {
      if (!success) {
        release();
      }
    }
  }
  
  private static int opensslSelectorFailureBehavior(ApplicationProtocolConfig.SelectorFailureBehavior behavior) {
    switch (behavior) {
    case NO_ADVERTISE: 
      return 0;
    case CHOOSE_MY_LAST_PROTOCOL: 
      return 1;
    }
    throw new Error();
  }
  

  public final List<String> cipherSuites()
  {
    return unmodifiableCiphers;
  }
  
  public final long sessionCacheSize()
  {
    return sessionCacheSize;
  }
  
  public final long sessionTimeout()
  {
    return sessionTimeout;
  }
  
  public ApplicationProtocolNegotiator applicationProtocolNegotiator()
  {
    return apn;
  }
  
  public final boolean isClient()
  {
    return mode == 0;
  }
  
  public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort)
  {
    return newEngine0(alloc, peerHost, peerPort);
  }
  
  SSLEngine newEngine0(ByteBufAllocator alloc, String peerHost, int peerPort) {
    return new ReferenceCountedOpenSslEngine(this, alloc, peerHost, peerPort, true);
  }
  





  public final SSLEngine newEngine(ByteBufAllocator alloc)
  {
    return newEngine(alloc, null, -1);
  }
  






  @Deprecated
  public final long context()
  {
    return ctx;
  }
  




  @Deprecated
  public final OpenSslSessionStats stats()
  {
    return sessionContext().stats();
  }
  



  public void setRejectRemoteInitiatedRenegotiation(boolean rejectRemoteInitiatedRenegotiation)
  {
    this.rejectRemoteInitiatedRenegotiation = rejectRemoteInitiatedRenegotiation;
  }
  




  @Deprecated
  public final void setTicketKeys(byte[] keys)
  {
    sessionContext().setTicketKeys(keys);
  }
  







  public final long sslCtxPointer()
  {
    return ctx;
  }
  


  final void destroy()
  {
    synchronized (ReferenceCountedOpenSslContext.class) {
      if (ctx != 0L) {
        SSLContext.free(ctx);
        ctx = 0L;
      }
      

      if (aprPool != 0L) {
        Pool.destroy(aprPool);
        aprPool = 0L;
      }
    }
  }
  
  protected static X509Certificate[] certificates(byte[][] chain) {
    X509Certificate[] peerCerts = new X509Certificate[chain.length];
    for (int i = 0; i < peerCerts.length; i++) {
      peerCerts[i] = new OpenSslX509Certificate(chain[i]);
    }
    return peerCerts;
  }
  
  protected static X509TrustManager chooseTrustManager(TrustManager[] managers) {
    for (TrustManager m : managers) {
      if ((m instanceof X509TrustManager)) {
        return (X509TrustManager)m;
      }
    }
    throw new IllegalStateException("no X509TrustManager found");
  }
  
  protected static X509KeyManager chooseX509KeyManager(KeyManager[] kms) {
    for (KeyManager km : kms) {
      if ((km instanceof X509KeyManager)) {
        return (X509KeyManager)km;
      }
    }
    throw new IllegalStateException("no X509KeyManager found");
  }
  






  static OpenSslApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config)
  {
    if (config == null) {
      return NONE_PROTOCOL_NEGOTIATOR;
    }
    
    switch (5.$SwitchMap$io$netty$handler$ssl$ApplicationProtocolConfig$Protocol[config.protocol().ordinal()]) {
    case 4: 
      return NONE_PROTOCOL_NEGOTIATOR;
    case 1: 
    case 2: 
    case 3: 
      switch (config.selectedListenerFailureBehavior()) {
      case CHOOSE_MY_LAST_PROTOCOL: 
      case ACCEPT: 
        switch (config.selectorFailureBehavior()) {
        case NO_ADVERTISE: 
        case CHOOSE_MY_LAST_PROTOCOL: 
          return new OpenSslDefaultApplicationProtocolNegotiator(config);
        }
        
        throw new UnsupportedOperationException("OpenSSL provider does not support " + config.selectorFailureBehavior() + " behavior");
      }
      
      


      throw new UnsupportedOperationException("OpenSSL provider does not support " + config.selectedListenerFailureBehavior() + " behavior");
    }
    
    


    throw new Error();
  }
  
  static boolean useExtendedTrustManager(X509TrustManager trustManager)
  {
    return (PlatformDependent.javaVersion() >= 7) && ((trustManager instanceof X509ExtendedTrustManager));
  }
  
  static boolean useExtendedKeyManager(X509KeyManager keyManager) {
    return (PlatformDependent.javaVersion() >= 7) && ((keyManager instanceof X509ExtendedKeyManager));
  }
  
  public final int refCnt()
  {
    return refCnt.refCnt();
  }
  
  public final ReferenceCounted retain()
  {
    refCnt.retain();
    return this;
  }
  
  public final ReferenceCounted retain(int increment)
  {
    refCnt.retain(increment);
    return this;
  }
  
  public final ReferenceCounted touch()
  {
    refCnt.touch();
    return this;
  }
  
  public final ReferenceCounted touch(Object hint)
  {
    refCnt.touch(hint);
    return this;
  }
  
  public final boolean release()
  {
    return refCnt.release();
  }
  
  public final boolean release(int decrement)
  {
    return refCnt.release(decrement);
  }
  
  static abstract class AbstractCertificateVerifier implements CertificateVerifier {
    private final OpenSslEngineMap engineMap;
    
    AbstractCertificateVerifier(OpenSslEngineMap engineMap) {
      this.engineMap = engineMap;
    }
    
    public final int verify(long ssl, byte[][] chain, String auth)
    {
      X509Certificate[] peerCerts = ReferenceCountedOpenSslContext.certificates(chain);
      ReferenceCountedOpenSslEngine engine = engineMap.get(ssl);
      try {
        verify(engine, peerCerts, auth);
        return 0;
      } catch (Throwable cause) {
        ReferenceCountedOpenSslContext.logger.debug("verification of certificate failed", cause);
        SSLHandshakeException e = new SSLHandshakeException("General OpenSslEngine problem");
        e.initCause(cause);
        handshakeException = e;
        
        if ((cause instanceof OpenSslCertificateException)) {
          return ((OpenSslCertificateException)cause).errorCode();
        }
        if ((cause instanceof CertificateExpiredException)) {
          return 10;
        }
        if ((cause instanceof CertificateNotYetValidException)) {
          return 9;
        }
        if ((PlatformDependent.javaVersion() >= 7) && ((cause instanceof CertificateRevokedException)))
          return 23;
      }
      return 1;
    }
    
    abstract void verify(ReferenceCountedOpenSslEngine paramReferenceCountedOpenSslEngine, X509Certificate[] paramArrayOfX509Certificate, String paramString)
      throws Exception;
  }
  
  private static final class DefaultOpenSslEngineMap implements OpenSslEngineMap
  {
    private final Map<Long, ReferenceCountedOpenSslEngine> engines = PlatformDependent.newConcurrentHashMap();
    
    private DefaultOpenSslEngineMap() {}
    
    public ReferenceCountedOpenSslEngine remove(long ssl) { return (ReferenceCountedOpenSslEngine)engines.remove(Long.valueOf(ssl)); }
    

    public void add(ReferenceCountedOpenSslEngine engine)
    {
      engines.put(Long.valueOf(engine.sslPointer()), engine);
    }
    
    public ReferenceCountedOpenSslEngine get(long ssl)
    {
      return (ReferenceCountedOpenSslEngine)engines.get(Long.valueOf(ssl));
    }
  }
  
  static void setKeyMaterial(long ctx, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword)
    throws SSLException
  {
    long keyBio = 0L;
    long keyCertChainBio = 0L;
    long keyCertChainBio2 = 0L;
    PemEncoded encoded = null;
    try
    {
      encoded = PemX509Certificate.toPEM(ByteBufAllocator.DEFAULT, true, keyCertChain);
      keyCertChainBio = toBIO(ByteBufAllocator.DEFAULT, encoded.retain());
      keyCertChainBio2 = toBIO(ByteBufAllocator.DEFAULT, encoded.retain());
      
      if (key != null) {
        keyBio = toBIO(key);
      }
      
      SSLContext.setCertificateBio(ctx, keyCertChainBio, keyBio, keyPassword == null ? "" : keyPassword);
      


      SSLContext.setCertificateChainBio(ctx, keyCertChainBio2, true);
    } catch (SSLException e) {
      throw e;
    } catch (Exception e) {
      throw new SSLException("failed to set certificate and key", e);
    } finally {
      freeBio(keyBio);
      freeBio(keyCertChainBio);
      freeBio(keyCertChainBio2);
      if (encoded != null) {
        encoded.release();
      }
    }
  }
  
  static void freeBio(long bio) {
    if (bio != 0L) {
      SSL.freeBIO(bio);
    }
  }
  


  static long toBIO(PrivateKey key)
    throws Exception
  {
    if (key == null) {
      return 0L;
    }
    
    ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    PemEncoded pem = PemPrivateKey.toPEM(allocator, true, key);
    try {
      return toBIO(allocator, pem.retain());
    } finally {
      pem.release();
    }
  }
  


  static long toBIO(X509Certificate... certChain)
    throws Exception
  {
    if (certChain == null) {
      return 0L;
    }
    
    if (certChain.length == 0) {
      throw new IllegalArgumentException("certChain can't be empty");
    }
    
    ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    PemEncoded pem = PemX509Certificate.toPEM(allocator, true, certChain);
    try {
      return toBIO(allocator, pem.retain());
    } finally {
      pem.release();
    }
  }
  



























  private static long newBIO(ByteBuf buffer)
    throws Exception
  {
    try
    {
      long bio = SSL.newMemBIO();
      int readable = buffer.readableBytes();
      if (SSL.writeToBIO(bio, OpenSsl.memoryAddress(buffer) + buffer.readerIndex(), readable) != readable) {
        SSL.freeBIO(bio);
        throw new IllegalStateException("Could not write data to memory BIO");
      }
      return bio;
    } finally {
      buffer.release();
    }
  }
  
  abstract OpenSslKeyMaterialManager keyMaterialManager();
  
  public abstract OpenSslSessionContext sessionContext();
  
  /* Error */
  static long toBIO(ByteBufAllocator allocator, PemEncoded pem)
    throws Exception
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 157 1 0
    //   6: astore_2
    //   7: aload_2
    //   8: invokevirtual 158	io/netty/buffer/ByteBuf:isDirect	()Z
    //   11: ifeq +20 -> 31
    //   14: aload_2
    //   15: invokevirtual 159	io/netty/buffer/ByteBuf:retainedSlice	()Lio/netty/buffer/ByteBuf;
    //   18: invokestatic 160	io/netty/handler/ssl/ReferenceCountedOpenSslContext:newBIO	(Lio/netty/buffer/ByteBuf;)J
    //   21: lstore_3
    //   22: aload_1
    //   23: invokeinterface 152 1 0
    //   28: pop
    //   29: lload_3
    //   30: lreturn
    //   31: aload_0
    //   32: aload_2
    //   33: invokevirtual 161	io/netty/buffer/ByteBuf:readableBytes	()I
    //   36: invokeinterface 162 2 0
    //   41: astore_3
    //   42: aload_3
    //   43: aload_2
    //   44: aload_2
    //   45: invokevirtual 163	io/netty/buffer/ByteBuf:readerIndex	()I
    //   48: aload_2
    //   49: invokevirtual 161	io/netty/buffer/ByteBuf:readableBytes	()I
    //   52: invokevirtual 164	io/netty/buffer/ByteBuf:writeBytes	(Lio/netty/buffer/ByteBuf;II)Lio/netty/buffer/ByteBuf;
    //   55: pop
    //   56: aload_3
    //   57: invokevirtual 159	io/netty/buffer/ByteBuf:retainedSlice	()Lio/netty/buffer/ByteBuf;
    //   60: invokestatic 160	io/netty/handler/ssl/ReferenceCountedOpenSslContext:newBIO	(Lio/netty/buffer/ByteBuf;)J
    //   63: lstore 4
    //   65: aload_1
    //   66: invokeinterface 165 1 0
    //   71: ifeq +7 -> 78
    //   74: aload_3
    //   75: invokestatic 166	io/netty/handler/ssl/SslUtils:zeroout	(Lio/netty/buffer/ByteBuf;)V
    //   78: aload_3
    //   79: invokevirtual 167	io/netty/buffer/ByteBuf:release	()Z
    //   82: pop
    //   83: goto +13 -> 96
    //   86: astore 6
    //   88: aload_3
    //   89: invokevirtual 167	io/netty/buffer/ByteBuf:release	()Z
    //   92: pop
    //   93: aload 6
    //   95: athrow
    //   96: aload_1
    //   97: invokeinterface 152 1 0
    //   102: pop
    //   103: lload 4
    //   105: lreturn
    //   106: astore 7
    //   108: aload_1
    //   109: invokeinterface 165 1 0
    //   114: ifeq +7 -> 121
    //   117: aload_3
    //   118: invokestatic 166	io/netty/handler/ssl/SslUtils:zeroout	(Lio/netty/buffer/ByteBuf;)V
    //   121: aload_3
    //   122: invokevirtual 167	io/netty/buffer/ByteBuf:release	()Z
    //   125: pop
    //   126: goto +13 -> 139
    //   129: astore 8
    //   131: aload_3
    //   132: invokevirtual 167	io/netty/buffer/ByteBuf:release	()Z
    //   135: pop
    //   136: aload 8
    //   138: athrow
    //   139: aload 7
    //   141: athrow
    //   142: astore 9
    //   144: aload_1
    //   145: invokeinterface 152 1 0
    //   150: pop
    //   151: aload 9
    //   153: athrow
    // Line number table:
    //   Java source line #733	-> byte code offset #0
    //   Java source line #735	-> byte code offset #7
    //   Java source line #736	-> byte code offset #14
    //   Java source line #755	-> byte code offset #22
    //   Java source line #739	-> byte code offset #31
    //   Java source line #741	-> byte code offset #42
    //   Java source line #742	-> byte code offset #56
    //   Java source line #747	-> byte code offset #65
    //   Java source line #748	-> byte code offset #74
    //   Java source line #751	-> byte code offset #78
    //   Java source line #752	-> byte code offset #83
    //   Java source line #751	-> byte code offset #86
    //   Java source line #755	-> byte code offset #96
    //   Java source line #744	-> byte code offset #106
    //   Java source line #747	-> byte code offset #108
    //   Java source line #748	-> byte code offset #117
    //   Java source line #751	-> byte code offset #121
    //   Java source line #752	-> byte code offset #126
    //   Java source line #751	-> byte code offset #129
    //   Java source line #755	-> byte code offset #142
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	154	0	allocator	ByteBufAllocator
    //   0	154	1	pem	PemEncoded
    //   6	43	2	content	ByteBuf
    //   21	9	3	l1	long
    //   41	91	3	buffer	ByteBuf
    //   63	41	4	l2	long
    //   86	8	6	localObject1	Object
    //   106	34	7	localObject2	Object
    //   129	8	8	localObject3	Object
    //   142	10	9	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   65	78	86	finally
    //   86	88	86	finally
    //   42	65	106	finally
    //   106	108	106	finally
    //   108	121	129	finally
    //   129	131	129	finally
    //   0	22	142	finally
    //   31	96	142	finally
    //   106	144	142	finally
  }
}
