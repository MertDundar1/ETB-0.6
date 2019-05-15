package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeak;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import org.apache.tomcat.jni.Buffer;
import org.apache.tomcat.jni.SSL;










public class ReferenceCountedOpenSslEngine
  extends SSLEngine
  implements ReferenceCounted
{
  private static final InternalLogger logger;
  private static final SSLException BEGIN_HANDSHAKE_ENGINE_CLOSED;
  private static final SSLException HANDSHAKE_ENGINE_CLOSED;
  private static final SSLException RENEGOTIATION_UNSUPPORTED;
  private static final SSLException ENCRYPTED_PACKET_OVERSIZED;
  private static final Class<?> SNI_HOSTNAME_CLASS;
  private static final Method GET_SERVER_NAMES_METHOD;
  private static final Method SET_SERVER_NAMES_METHOD;
  private static final Method GET_ASCII_NAME_METHOD;
  private static final Method GET_USE_CIPHER_SUITES_ORDER_METHOD;
  private static final Method SET_USE_CIPHER_SUITES_ORDER_METHOD;
  private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector;
  private static final int MAX_PLAINTEXT_LENGTH = 16384;
  private static final int MAX_COMPRESSED_LENGTH = 17408;
  private static final int MAX_CIPHERTEXT_LENGTH = 18432;
  static final int MAX_ENCRYPTED_PACKET_LENGTH = 18713;
  static final int MAX_ENCRYPTION_OVERHEAD_LENGTH = 2329;
  private static final AtomicIntegerFieldUpdater<ReferenceCountedOpenSslEngine> DESTROYED_UPDATER;
  private static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
  
  static
  {
    logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
    
    BEGIN_HANDSHAKE_ENGINE_CLOSED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("engine closed"), ReferenceCountedOpenSslEngine.class, "beginHandshake()");
    
    HANDSHAKE_ENGINE_CLOSED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("engine closed"), ReferenceCountedOpenSslEngine.class, "handshake()");
    
    RENEGOTIATION_UNSUPPORTED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("renegotiation unsupported"), ReferenceCountedOpenSslEngine.class, "beginHandshake()");
    
    ENCRYPTED_PACKET_OVERSIZED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("encrypted packet oversized"), ReferenceCountedOpenSslEngine.class, "unwrap(...)");
    






    leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
    


    AtomicIntegerFieldUpdater<ReferenceCountedOpenSslEngine> destroyedUpdater = PlatformDependent.newAtomicIntegerFieldUpdater(ReferenceCountedOpenSslEngine.class, "destroyed");
    
    if (destroyedUpdater == null) {
      destroyedUpdater = AtomicIntegerFieldUpdater.newUpdater(ReferenceCountedOpenSslEngine.class, "destroyed");
    }
    DESTROYED_UPDATER = destroyedUpdater;
    
    Method getUseCipherSuitesOrderMethod = null;
    Method setUseCipherSuitesOrderMethod = null;
    Class<?> sniHostNameClass = null;
    Method getAsciiNameMethod = null;
    Method getServerNamesMethod = null;
    Method setServerNamesMethod = null;
    if (PlatformDependent.javaVersion() >= 8) {
      try {
        getUseCipherSuitesOrderMethod = SSLParameters.class.getDeclaredMethod("getUseCipherSuitesOrder", new Class[0]);
        SSLParameters parameters = new SSLParameters();
        
        Boolean order = (Boolean)getUseCipherSuitesOrderMethod.invoke(parameters, new Object[0]);
        setUseCipherSuitesOrderMethod = SSLParameters.class.getDeclaredMethod("setUseCipherSuitesOrder", new Class[] { Boolean.TYPE });
        
        setUseCipherSuitesOrderMethod.invoke(parameters, new Object[] { Boolean.valueOf(true) });
      } catch (Throwable ignore) {
        getUseCipherSuitesOrderMethod = null;
        setUseCipherSuitesOrderMethod = null;
      }
      try {
        sniHostNameClass = Class.forName("javax.net.ssl.SNIHostName", false, PlatformDependent.getClassLoader(ReferenceCountedOpenSslEngine.class));
        
        Object sniHostName = sniHostNameClass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { "netty.io" });
        getAsciiNameMethod = sniHostNameClass.getDeclaredMethod("getAsciiName", new Class[0]);
        
        String name = (String)getAsciiNameMethod.invoke(sniHostName, new Object[0]);
        
        getServerNamesMethod = SSLParameters.class.getDeclaredMethod("getServerNames", new Class[0]);
        setServerNamesMethod = SSLParameters.class.getDeclaredMethod("setServerNames", new Class[] { List.class });
        SSLParameters parameters = new SSLParameters();
        
        List serverNames = (List)getServerNamesMethod.invoke(parameters, new Object[0]);
        setServerNamesMethod.invoke(parameters, new Object[] { Collections.emptyList() });
      } catch (Throwable ingore) {
        sniHostNameClass = null;
        getAsciiNameMethod = null;
        getServerNamesMethod = null;
        setServerNamesMethod = null;
      }
    }
    GET_USE_CIPHER_SUITES_ORDER_METHOD = getUseCipherSuitesOrderMethod;
    SET_USE_CIPHER_SUITES_ORDER_METHOD = setUseCipherSuitesOrderMethod;
    SNI_HOSTNAME_CLASS = sniHostNameClass;
    GET_ASCII_NAME_METHOD = getAsciiNameMethod;
    GET_SERVER_NAMES_METHOD = getServerNamesMethod;
    SET_SERVER_NAMES_METHOD = setServerNamesMethod;
  }
  













  private static final long EMPTY_ADDR = Buffer.address(Unpooled.EMPTY_BUFFER.nioBuffer());
  
  private static final SSLEngineResult NEED_UNWRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
  private static final SSLEngineResult NEED_UNWRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
  private static final SSLEngineResult NEED_WRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
  private static final SSLEngineResult NEED_WRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
  private static final SSLEngineResult CLOSED_NOT_HANDSHAKING = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
  
  private long ssl;
  
  private long networkBIO;
  
  private boolean certificateSet;
  

  private static enum HandshakeState
  {
    NOT_STARTED, 
    


    STARTED_IMPLICITLY, 
    


    STARTED_EXPLICITLY, 
    



    FINISHED;
    
    private HandshakeState() {} }
  private HandshakeState handshakeState = HandshakeState.NOT_STARTED;
  
  private boolean receivedShutdown;
  
  private volatile int destroyed;
  private final ResourceLeak leak;
  private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted()
  {
    public ReferenceCounted touch(Object hint) {
      if (leak != null) {
        leak.record(hint);
      }
      
      return ReferenceCountedOpenSslEngine.this;
    }
    
    protected void deallocate()
    {
      shutdown();
      if (leak != null) {
        leak.close();
      }
    }
  };
  
  private volatile ClientAuth clientAuth = ClientAuth.NONE;
  

  private volatile long lastAccessed = -1L;
  
  private String endPointIdentificationAlgorithm;
  
  private Object algorithmConstraints;
  
  private List<?> sniHostNames;
  
  private boolean isInboundDone;
  
  private boolean isOutboundDone;
  private boolean engineClosed;
  private final boolean clientMode;
  private final ByteBufAllocator alloc;
  private final OpenSslEngineMap engineMap;
  private final OpenSslApplicationProtocolNegotiator apn;
  private final boolean rejectRemoteInitiatedRenegation;
  private final OpenSslSession session;
  private final Certificate[] localCerts;
  private final ByteBuffer[] singleSrcBuffer = new ByteBuffer[1];
  private final ByteBuffer[] singleDstBuffer = new ByteBuffer[1];
  



  private final OpenSslKeyMaterialManager keyMaterialManager;
  



  SSLHandshakeException handshakeException;
  



  ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean leakDetection)
  {
    super(peerHost, peerPort);
    OpenSsl.ensureAvailability();
    leak = (leakDetection ? leakDetector.open(this) : null);
    this.alloc = ((ByteBufAllocator)ObjectUtil.checkNotNull(alloc, "alloc"));
    apn = ((OpenSslApplicationProtocolNegotiator)context.applicationProtocolNegotiator());
    ssl = SSL.newSSL(ctx, !context.isClient());
    session = new OpenSslSession(context.sessionContext());
    networkBIO = SSL.makeNetworkBIO(ssl);
    clientMode = context.isClient();
    engineMap = engineMap;
    rejectRemoteInitiatedRenegation = rejectRemoteInitiatedRenegotiation;
    localCerts = keyCertChain;
    


    setClientAuth(clientMode ? ClientAuth.NONE : clientAuth);
    


    if ((clientMode) && (peerHost != null)) {
      SSL.setTlsExtHostName(ssl, peerHost);
    }
    keyMaterialManager = context.keyMaterialManager();
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
  




  public final synchronized SSLSession getHandshakeSession()
  {
    switch (handshakeState) {
    case NOT_STARTED: 
    case FINISHED: 
      return null;
    }
    return session;
  }
  





  public final synchronized long sslPointer()
  {
    return ssl;
  }
  


  public final synchronized void shutdown()
  {
    if (DESTROYED_UPDATER.compareAndSet(this, 0, 1)) {
      engineMap.remove(ssl);
      SSL.freeSSL(ssl);
      SSL.freeBIO(networkBIO);
      ssl = (this.networkBIO = 0L);
      

      isInboundDone = (this.isOutboundDone = this.engineClosed = 1);
    }
    

    SSL.clearError();
  }
  




  private int writePlaintextData(ByteBuffer src)
  {
    int pos = src.position();
    int limit = src.limit();
    int len = Math.min(limit - pos, 16384);
    

    if (src.isDirect()) {
      long addr = Buffer.address(src) + pos;
      int sslWrote = SSL.writeToSSL(ssl, addr, len);
      if (sslWrote > 0) {
        src.position(pos + sslWrote);
      }
    } else {
      ByteBuf buf = alloc.directBuffer(len);
      try {
        long addr = OpenSsl.memoryAddress(buf);
        
        src.limit(pos + len);
        
        buf.setBytes(0, src);
        src.limit(limit);
        
        int sslWrote = SSL.writeToSSL(ssl, addr, len);
        if (sslWrote > 0) {
          src.position(pos + sslWrote);
        } else {
          src.position(pos);
        }
      } finally {
        buf.release();
      } }
    int sslWrote;
    return sslWrote;
  }
  


  private int writeEncryptedData(ByteBuffer src)
  {
    int pos = src.position();
    int len = src.remaining();
    
    if (src.isDirect()) {
      long addr = Buffer.address(src) + pos;
      int netWrote = SSL.writeToBIO(networkBIO, addr, len);
      if (netWrote >= 0) {
        src.position(pos + netWrote);
      }
    } else {
      ByteBuf buf = alloc.directBuffer(len);
      try {
        long addr = OpenSsl.memoryAddress(buf);
        
        buf.setBytes(0, src);
        
        int netWrote = SSL.writeToBIO(networkBIO, addr, len);
        if (netWrote >= 0) {
          src.position(pos + netWrote);
        } else {
          src.position(pos);
        }
      } finally {
        buf.release();
      }
    }
    int netWrote;
    return netWrote;
  }
  



  private int readPlaintextData(ByteBuffer dst)
  {
    if (dst.isDirect()) {
      int pos = dst.position();
      long addr = Buffer.address(dst) + pos;
      int len = dst.limit() - pos;
      int sslRead = SSL.readFromSSL(ssl, addr, len);
      if (sslRead > 0) {
        dst.position(pos + sslRead);
      }
    } else {
      int pos = dst.position();
      int limit = dst.limit();
      int len = Math.min(18713, limit - pos);
      ByteBuf buf = alloc.directBuffer(len);
      try {
        long addr = OpenSsl.memoryAddress(buf);
        
        int sslRead = SSL.readFromSSL(ssl, addr, len);
        if (sslRead > 0) {
          dst.limit(pos + sslRead);
          buf.getBytes(0, dst);
          dst.limit(limit);
        }
      } finally {
        buf.release();
      }
    }
    int sslRead;
    return sslRead;
  }
  




  private int readEncryptedData(ByteBuffer dst, int pending)
  {
    if ((dst.isDirect()) && (dst.remaining() >= pending)) {
      int pos = dst.position();
      long addr = Buffer.address(dst) + pos;
      int bioRead = SSL.readFromBIO(networkBIO, addr, pending);
      if (bioRead > 0) {
        dst.position(pos + bioRead);
        return bioRead;
      }
    } else {
      ByteBuf buf = alloc.directBuffer(pending);
      try {
        long addr = OpenSsl.memoryAddress(buf);
        
        int bioRead = SSL.readFromBIO(networkBIO, addr, pending);
        if (bioRead > 0) {
          int oldLimit = dst.limit();
          dst.limit(dst.position() + bioRead);
          buf.getBytes(0, dst);
          dst.limit(oldLimit);
          return bioRead;
        }
      } finally {
        buf.release();
      }
    }
    int bioRead;
    return bioRead;
  }
  
  private SSLEngineResult readPendingBytesFromBIO(ByteBuffer dst, int bytesConsumed, int bytesProduced, SSLEngineResult.HandshakeStatus status)
    throws SSLException
  {
    int pendingNet = SSL.pendingWrittenBytesInBIO(networkBIO);
    if (pendingNet > 0)
    {

      int capacity = dst.remaining();
      if (capacity < pendingNet) {
        return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, mayFinishHandshake(status != SSLEngineResult.HandshakeStatus.FINISHED ? getHandshakeStatus(pendingNet) : status), bytesConsumed, bytesProduced);
      }
      



      int produced = readEncryptedData(dst, pendingNet);
      
      if (produced <= 0)
      {

        SSL.clearError();
      } else {
        bytesProduced += produced;
        pendingNet -= produced;
      }
      


      if (isOutboundDone) {
        shutdown();
      }
      
      return new SSLEngineResult(getEngineStatus(), mayFinishHandshake(status != SSLEngineResult.HandshakeStatus.FINISHED ? getHandshakeStatus(pendingNet) : status), bytesConsumed, bytesProduced);
    }
    

    return null;
  }
  

  public final SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst)
    throws SSLException
  {
    if (srcs == null) {
      throw new IllegalArgumentException("srcs is null");
    }
    if (dst == null) {
      throw new IllegalArgumentException("dst is null");
    }
    
    if ((offset >= srcs.length) || (offset + length > srcs.length)) {
      throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
    }
    


    if (dst.isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    
    synchronized (this)
    {
      if (isDestroyed()) {
        return CLOSED_NOT_HANDSHAKING;
      }
      
      SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
      
      if (handshakeState != HandshakeState.FINISHED) {
        if (handshakeState != HandshakeState.STARTED_EXPLICITLY)
        {
          handshakeState = HandshakeState.STARTED_IMPLICITLY;
        }
        
        status = handshake();
        if (status == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
          return NEED_UNWRAP_OK;
        }
        
        if (engineClosed) {
          return NEED_UNWRAP_CLOSED;
        }
      }
      

      int bytesProduced = 0;
      int bytesConsumed = 0;
      int endOffset = offset + length;
      for (int i = offset; i < endOffset; i++) {
        ByteBuffer src = srcs[i];
        if (src == null) {
          throw new IllegalArgumentException("srcs[" + i + "] is null");
        }
        while (src.hasRemaining())
        {

          int result = writePlaintextData(src);
          if (result > 0) {
            bytesConsumed += result;
            
            SSLEngineResult pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
            if (pendingNetResult != null) {
              if (pendingNetResult.getStatus() != SSLEngineResult.Status.OK) {
                return pendingNetResult;
              }
              bytesProduced = pendingNetResult.bytesProduced();
            }
          } else {
            int sslError = SSL.getError(ssl, result);
            switch (sslError)
            {
            case 6: 
              if (!receivedShutdown) {
                closeAll();
              }
              SSLEngineResult pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
              return pendingNetResult != null ? pendingNetResult : CLOSED_NOT_HANDSHAKING;
            


            case 2: 
              SSLEngineResult pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
              return pendingNetResult != null ? pendingNetResult : new SSLEngineResult(getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_UNWRAP, bytesConsumed, bytesProduced);
            













            case 3: 
              SSLEngineResult pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
              return pendingNetResult != null ? pendingNetResult : NEED_WRAP_CLOSED;
            }
            
            throw shutdownWithError("SSL_write");
          }
        }
      }
      


      if (bytesConsumed == 0) {
        SSLEngineResult pendingNetResult = readPendingBytesFromBIO(dst, 0, bytesProduced, status);
        if (pendingNetResult != null) {
          return pendingNetResult;
        }
      }
      
      return newResult(bytesConsumed, bytesProduced, status);
    }
  }
  


  private SSLException shutdownWithError(String operations)
  {
    String err = SSL.getLastError();
    return shutdownWithError(operations, err);
  }
  
  private SSLException shutdownWithError(String operation, String err) {
    if (logger.isDebugEnabled()) {
      logger.debug("{} failed: OpenSSL error: {}", operation, err);
    }
    

    shutdown();
    if (handshakeState == HandshakeState.FINISHED) {
      return new SSLException(err);
    }
    return new SSLHandshakeException(err);
  }
  


  public final SSLEngineResult unwrap(ByteBuffer[] srcs, int srcsOffset, int srcsLength, ByteBuffer[] dsts, int dstsOffset, int dstsLength)
    throws SSLException
  {
    if (srcs == null) {
      throw new NullPointerException("srcs");
    }
    if ((srcsOffset >= srcs.length) || (srcsOffset + srcsLength > srcs.length))
    {
      throw new IndexOutOfBoundsException("offset: " + srcsOffset + ", length: " + srcsLength + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
    }
    

    if (dsts == null) {
      throw new IllegalArgumentException("dsts is null");
    }
    if ((dstsOffset >= dsts.length) || (dstsOffset + dstsLength > dsts.length)) {
      throw new IndexOutOfBoundsException("offset: " + dstsOffset + ", length: " + dstsLength + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
    }
    

    long capacity = 0L;
    int endOffset = dstsOffset + dstsLength;
    for (int i = dstsOffset; i < endOffset; i++) {
      ByteBuffer dst = dsts[i];
      if (dst == null) {
        throw new IllegalArgumentException("dsts[" + i + "] is null");
      }
      if (dst.isReadOnly()) {
        throw new ReadOnlyBufferException();
      }
      capacity += dst.remaining();
    }
    
    int srcsEndOffset = srcsOffset + srcsLength;
    long len = 0L;
    for (int i = srcsOffset; i < srcsEndOffset; i++) {
      ByteBuffer src = srcs[i];
      if (src == null) {
        throw new IllegalArgumentException("srcs[" + i + "] is null");
      }
      len += src.remaining();
    }
    
    synchronized (this)
    {
      if (isDestroyed()) {
        return CLOSED_NOT_HANDSHAKING;
      }
      

      if (len > 18713L) {
        isInboundDone = true;
        isOutboundDone = true;
        engineClosed = true;
        shutdown();
        throw ENCRYPTED_PACKET_OVERSIZED;
      }
      
      SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
      
      if (handshakeState != HandshakeState.FINISHED) {
        if (handshakeState != HandshakeState.STARTED_EXPLICITLY)
        {
          handshakeState = HandshakeState.STARTED_IMPLICITLY;
        }
        
        status = handshake();
        if (status == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
          return NEED_WRAP_OK;
        }
        if (engineClosed) {
          return NEED_WRAP_CLOSED;
        }
      }
      

      int bytesConsumed = 0;
      if (srcsOffset < srcsEndOffset) {
        do {
          ByteBuffer src = srcs[srcsOffset];
          int remaining = src.remaining();
          if (remaining == 0)
          {

            srcsOffset++;
          }
          else {
            int written = writeEncryptedData(src);
            if (written > 0) {
              bytesConsumed += written;
              
              if (written != remaining) break;
              srcsOffset++;




            }
            else
            {




              SSL.clearError();
              break;
            }
          } } while (srcsOffset < srcsEndOffset);
      }
      

      int bytesProduced = 0;
      
      if (capacity > 0L)
      {
        int idx = dstsOffset;
        while (idx < endOffset) {
          ByteBuffer dst = dsts[idx];
          if (!dst.hasRemaining()) {
            idx++;
          }
          else
          {
            int bytesRead = readPlaintextData(dst);
            


            rejectRemoteInitiatedRenegation();
            
            if (bytesRead > 0) {
              bytesProduced += bytesRead;
              
              if (!dst.hasRemaining()) {
                idx++;
              }
              else {
                return newResult(bytesConsumed, bytesProduced, status);
              }
            } else {
              int sslError = SSL.getError(ssl, bytesRead);
              switch (sslError)
              {
              case 6: 
                if (!receivedShutdown) {
                  closeAll();
                }
              

              case 2: 
              case 3: 
                return newResult(bytesConsumed, bytesProduced, status);
              }
              return sslReadErrorResult(SSL.getLastErrorNumber(), bytesConsumed, bytesProduced);
            }
            
          }
          
        }
      }
      else if (SSL.readFromSSL(ssl, EMPTY_ADDR, 0) <= 0)
      {
        int err = SSL.getLastErrorNumber();
        if (OpenSsl.isError(err)) {
          return sslReadErrorResult(err, bytesConsumed, bytesProduced);
        }
      }
      
      if (pendingAppData() > 0)
      {
        return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, mayFinishHandshake(status != SSLEngineResult.HandshakeStatus.FINISHED ? getHandshakeStatus() : status), bytesConsumed, bytesProduced);
      }
      



      if ((!receivedShutdown) && ((SSL.getShutdown(ssl) & 0x2) == 2)) {
        closeAll();
      }
      
      return newResult(bytesConsumed, bytesProduced, status);
    }
  }
  
  private SSLEngineResult sslReadErrorResult(int err, int bytesConsumed, int bytesProduced) throws SSLException {
    String errStr = SSL.getErrorString(err);
    




    if (SSL.pendingWrittenBytesInBIO(networkBIO) > 0) {
      if ((handshakeException == null) && (handshakeState != HandshakeState.FINISHED))
      {

        handshakeException = new SSLHandshakeException(errStr);
      }
      return new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced);
    }
    throw shutdownWithError("SSL_read", errStr);
  }
  

  private int pendingAppData()
  {
    return handshakeState == HandshakeState.FINISHED ? SSL.pendingReadableBytesInSSL(ssl) : 0;
  }
  
  private SSLEngineResult newResult(int bytesConsumed, int bytesProduced, SSLEngineResult.HandshakeStatus status) throws SSLException
  {
    return new SSLEngineResult(getEngineStatus(), mayFinishHandshake(status != SSLEngineResult.HandshakeStatus.FINISHED ? getHandshakeStatus() : status), bytesConsumed, bytesProduced);
  }
  
  private void closeAll()
    throws SSLException
  {
    receivedShutdown = true;
    closeOutbound();
    closeInbound();
  }
  
  private void rejectRemoteInitiatedRenegation() throws SSLHandshakeException {
    if ((rejectRemoteInitiatedRenegation) && (SSL.getHandshakeCount(ssl) > 1))
    {

      shutdown();
      throw new SSLHandshakeException("remote-initiated renegotation not allowed");
    }
  }
  
  public final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dsts) throws SSLException {
    return unwrap(srcs, 0, srcs.length, dsts, 0, dsts.length);
  }
  
  private ByteBuffer[] singleSrcBuffer(ByteBuffer src) {
    singleSrcBuffer[0] = src;
    return singleSrcBuffer;
  }
  
  private void resetSingleSrcBuffer() {
    singleSrcBuffer[0] = null;
  }
  
  private ByteBuffer[] singleDstBuffer(ByteBuffer src) {
    singleDstBuffer[0] = src;
    return singleDstBuffer;
  }
  
  private void resetSingleDstBuffer() {
    singleDstBuffer[0] = null;
  }
  
  public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException
  {
    try
    {
      return unwrap(singleSrcBuffer(src), 0, 1, dsts, offset, length);
    } finally {
      resetSingleSrcBuffer();
    }
  }
  
  public final synchronized SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException
  {
    try {
      return wrap(singleSrcBuffer(src), dst);
    } finally {
      resetSingleSrcBuffer();
    }
  }
  
  public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException
  {
    try {
      return unwrap(singleSrcBuffer(src), singleDstBuffer(dst));
    } finally {
      resetSingleSrcBuffer();
      resetSingleDstBuffer();
    }
  }
  
  public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException
  {
    try {
      return unwrap(singleSrcBuffer(src), dsts);
    } finally {
      resetSingleSrcBuffer();
    }
  }
  



  public final Runnable getDelegatedTask()
  {
    return null;
  }
  
  public final synchronized void closeInbound() throws SSLException
  {
    if (isInboundDone) {
      return;
    }
    
    isInboundDone = true;
    engineClosed = true;
    
    shutdown();
    
    if ((handshakeState != HandshakeState.NOT_STARTED) && (!receivedShutdown)) {
      throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?");
    }
  }
  

  public final synchronized boolean isInboundDone()
  {
    return (isInboundDone) || (engineClosed);
  }
  
  public final synchronized void closeOutbound()
  {
    if (isOutboundDone) {
      return;
    }
    
    isOutboundDone = true;
    engineClosed = true;
    
    if ((handshakeState != HandshakeState.NOT_STARTED) && (!isDestroyed())) {
      int mode = SSL.getShutdown(ssl);
      if ((mode & 0x1) != 1) {
        int err = SSL.shutdownSSL(ssl);
        if (err < 0) {
          int sslErr = SSL.getError(ssl, err);
          switch (sslErr)
          {
          case 0: 
          case 2: 
          case 3: 
          case 4: 
          case 6: 
          case 7: 
          case 8: 
            break;
          case 1: 
          case 5: 
            if (logger.isDebugEnabled()) {
              logger.debug("SSL_shutdown failed: OpenSSL error: {}", SSL.getLastError());
            }
            
            shutdown();
            break;
          default: 
            SSL.clearError();
          }
        }
      }
    }
    else
    {
      shutdown();
    }
  }
  
  public final synchronized boolean isOutboundDone()
  {
    return isOutboundDone;
  }
  
  public final String[] getSupportedCipherSuites()
  {
    return (String[])OpenSsl.AVAILABLE_CIPHER_SUITES.toArray(new String[OpenSsl.AVAILABLE_CIPHER_SUITES.size()]);
  }
  
  public final String[] getEnabledCipherSuites()
  {
    String[] enabled;
    synchronized (this) { String[] enabled;
      if (!isDestroyed()) {
        enabled = SSL.getCiphers(ssl);
      } else
        return EmptyArrays.EMPTY_STRINGS;
    }
    String[] enabled;
    if (enabled == null) {
      return EmptyArrays.EMPTY_STRINGS;
    }
    synchronized (this) {
      for (int i = 0; i < enabled.length; i++) {
        String mapped = toJavaCipherSuite(enabled[i]);
        if (mapped != null) {
          enabled[i] = mapped;
        }
      }
    }
    return enabled;
  }
  

  public final void setEnabledCipherSuites(String[] cipherSuites)
  {
    ObjectUtil.checkNotNull(cipherSuites, "cipherSuites");
    
    StringBuilder buf = new StringBuilder();
    for (String c : cipherSuites) {
      if (c == null) {
        break;
      }
      
      String converted = CipherSuiteConverter.toOpenSsl(c);
      if (converted == null) {
        converted = c;
      }
      
      if (!OpenSsl.isCipherSuiteAvailable(converted)) {
        throw new IllegalArgumentException("unsupported cipher suite: " + c + '(' + converted + ')');
      }
      
      buf.append(converted);
      buf.append(':');
    }
    
    if (buf.length() == 0) {
      throw new IllegalArgumentException("empty cipher suites");
    }
    buf.setLength(buf.length() - 1);
    
    String cipherSuiteSpec = buf.toString();
    
    synchronized (this) {
      if (!isDestroyed()) {
        try {
          SSL.setCipherSuites(ssl, cipherSuiteSpec);
        } catch (Exception e) {
          throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec, e);
        }
      } else {
        throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec);
      }
    }
  }
  
  public final String[] getSupportedProtocols()
  {
    return (String[])OpenSsl.SUPPORTED_PROTOCOLS_SET.toArray(new String[OpenSsl.SUPPORTED_PROTOCOLS_SET.size()]);
  }
  
  public final String[] getEnabledProtocols()
  {
    List<String> enabled = InternalThreadLocalMap.get().arrayList();
    
    enabled.add("SSLv2Hello");
    
    int opts;
    synchronized (this) { int opts;
      if (!isDestroyed()) {
        opts = SSL.getOptions(ssl);
      } else
        return (String[])enabled.toArray(new String[1]);
    }
    int opts;
    if ((opts & 0x4000000) == 0) {
      enabled.add("TLSv1");
    }
    if ((opts & 0x10000000) == 0) {
      enabled.add("TLSv1.1");
    }
    if ((opts & 0x8000000) == 0) {
      enabled.add("TLSv1.2");
    }
    if ((opts & 0x1000000) == 0) {
      enabled.add("SSLv2");
    }
    if ((opts & 0x2000000) == 0) {
      enabled.add("SSLv3");
    }
    return (String[])enabled.toArray(new String[enabled.size()]);
  }
  
  public final void setEnabledProtocols(String[] protocols)
  {
    if (protocols == null)
    {
      throw new IllegalArgumentException();
    }
    boolean sslv2 = false;
    boolean sslv3 = false;
    boolean tlsv1 = false;
    boolean tlsv1_1 = false;
    boolean tlsv1_2 = false;
    for (String p : protocols) {
      if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(p)) {
        throw new IllegalArgumentException("Protocol " + p + " is not supported.");
      }
      if (p.equals("SSLv2")) {
        sslv2 = true;
      } else if (p.equals("SSLv3")) {
        sslv3 = true;
      } else if (p.equals("TLSv1")) {
        tlsv1 = true;
      } else if (p.equals("TLSv1.1")) {
        tlsv1_1 = true;
      } else if (p.equals("TLSv1.2")) {
        tlsv1_2 = true;
      }
    }
    synchronized (this) {
      if (!isDestroyed())
      {
        SSL.setOptions(ssl, 4095);
        

        SSL.clearOptions(ssl, 520093696);
        

        int opts = 0;
        if (!sslv2) {
          opts |= 0x1000000;
        }
        if (!sslv3) {
          opts |= 0x2000000;
        }
        if (!tlsv1) {
          opts |= 0x4000000;
        }
        if (!tlsv1_1) {
          opts |= 0x10000000;
        }
        if (!tlsv1_2) {
          opts |= 0x8000000;
        }
        

        SSL.setOptions(ssl, opts);
      } else {
        throw new IllegalStateException("failed to enable protocols: " + Arrays.asList(protocols));
      }
    }
  }
  
  public final SSLSession getSession()
  {
    return session;
  }
  
  public final synchronized void beginHandshake() throws SSLException
  {
    switch (2.$SwitchMap$io$netty$handler$ssl$ReferenceCountedOpenSslEngine$HandshakeState[handshakeState.ordinal()]) {
    case 3: 
      checkEngineClosed(BEGIN_HANDSHAKE_ENGINE_CLOSED);
      






      handshakeState = HandshakeState.STARTED_EXPLICITLY;
      
      break;
    case 4: 
      break;
    
    case 2: 
      if (clientMode)
      {
        throw RENEGOTIATION_UNSUPPORTED;
      }
      












      if ((SSL.renegotiate(ssl) != 1) || (SSL.doHandshake(ssl) != 1)) {
        throw shutdownWithError("renegotiation failed");
      }
      
      SSL.setState(ssl, 8192);
      
      lastAccessed = System.currentTimeMillis();
    

    case 1: 
      handshakeState = HandshakeState.STARTED_EXPLICITLY;
      handshake();
      break;
    default: 
      throw new Error();
    }
  }
  
  private void checkEngineClosed(SSLException cause) throws SSLException {
    if ((engineClosed) || (isDestroyed())) {
      throw cause;
    }
  }
  
  private static SSLEngineResult.HandshakeStatus pendingStatus(int pendingStatus)
  {
    return pendingStatus > 0 ? SSLEngineResult.HandshakeStatus.NEED_WRAP : SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
  }
  
  private SSLEngineResult.HandshakeStatus handshake() throws SSLException {
    if (handshakeState == HandshakeState.FINISHED) {
      return SSLEngineResult.HandshakeStatus.FINISHED;
    }
    checkEngineClosed(HANDSHAKE_ENGINE_CLOSED);
    




    SSLHandshakeException exception = handshakeException;
    if (exception != null) {
      if (SSL.pendingWrittenBytesInBIO(networkBIO) > 0)
      {
        return SSLEngineResult.HandshakeStatus.NEED_WRAP;
      }
      

      handshakeException = null;
      shutdown();
      throw exception;
    }
    

    engineMap.add(this);
    if (lastAccessed == -1L) {
      lastAccessed = System.currentTimeMillis();
    }
    
    if ((!certificateSet) && (keyMaterialManager != null)) {
      certificateSet = true;
      keyMaterialManager.setKeyMaterial(this);
    }
    
    int code = SSL.doHandshake(ssl);
    if (code <= 0)
    {

      if (handshakeException != null) {
        exception = handshakeException;
        handshakeException = null;
        shutdown();
        throw exception;
      }
      
      int sslError = SSL.getError(ssl, code);
      
      switch (sslError) {
      case 2: 
      case 3: 
        return pendingStatus(SSL.pendingWrittenBytesInBIO(networkBIO));
      }
      
      throw shutdownWithError("SSL_do_handshake");
    }
    

    session.handshakeFinished();
    engineMap.remove(ssl);
    return SSLEngineResult.HandshakeStatus.FINISHED;
  }
  
  private SSLEngineResult.Status getEngineStatus() {
    return engineClosed ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK;
  }
  
  private SSLEngineResult.HandshakeStatus mayFinishHandshake(SSLEngineResult.HandshakeStatus status) throws SSLException
  {
    if ((status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) && (handshakeState != HandshakeState.FINISHED))
    {

      return handshake();
    }
    return status;
  }
  

  public final synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus()
  {
    return needPendingStatus() ? pendingStatus(SSL.pendingWrittenBytesInBIO(networkBIO)) : SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
  }
  
  private SSLEngineResult.HandshakeStatus getHandshakeStatus(int pending)
  {
    return needPendingStatus() ? pendingStatus(pending) : SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
  }
  
  private boolean needPendingStatus() {
    return (handshakeState != HandshakeState.NOT_STARTED) && (!isDestroyed()) && ((handshakeState != HandshakeState.FINISHED) || (engineClosed));
  }
  



  private String toJavaCipherSuite(String openSslCipherSuite)
  {
    if (openSslCipherSuite == null) {
      return null;
    }
    
    String prefix = toJavaCipherSuitePrefix(SSL.getVersion(ssl));
    return CipherSuiteConverter.toJava(openSslCipherSuite, prefix);
  }
  

  private static String toJavaCipherSuitePrefix(String protocolVersion)
  {
    char c;
    char c;
    if ((protocolVersion == null) || (protocolVersion.length() == 0)) {
      c = '\000';
    } else {
      c = protocolVersion.charAt(0);
    }
    
    switch (c) {
    case 'T': 
      return "TLS";
    case 'S': 
      return "SSL";
    }
    return "UNKNOWN";
  }
  

  public final void setUseClientMode(boolean clientMode)
  {
    if (clientMode != this.clientMode) {
      throw new UnsupportedOperationException();
    }
  }
  
  public final boolean getUseClientMode()
  {
    return clientMode;
  }
  
  public final void setNeedClientAuth(boolean b)
  {
    setClientAuth(b ? ClientAuth.REQUIRE : ClientAuth.NONE);
  }
  
  public final boolean getNeedClientAuth()
  {
    return clientAuth == ClientAuth.REQUIRE;
  }
  
  public final void setWantClientAuth(boolean b)
  {
    setClientAuth(b ? ClientAuth.OPTIONAL : ClientAuth.NONE);
  }
  
  public final boolean getWantClientAuth()
  {
    return clientAuth == ClientAuth.OPTIONAL;
  }
  
  private void setClientAuth(ClientAuth mode) {
    if (clientMode) {
      return;
    }
    synchronized (this) {
      if (clientAuth == mode)
      {
        return;
      }
      switch (2.$SwitchMap$io$netty$handler$ssl$ClientAuth[mode.ordinal()]) {
      case 1: 
        SSL.setVerify(ssl, 0, 10);
        break;
      case 2: 
        SSL.setVerify(ssl, 2, 10);
        break;
      case 3: 
        SSL.setVerify(ssl, 1, 10);
        break;
      default: 
        throw new Error(mode.toString());
      }
      clientAuth = mode;
    }
  }
  
  public final void setEnableSessionCreation(boolean b)
  {
    if (b) {
      throw new UnsupportedOperationException();
    }
  }
  
  public final boolean getEnableSessionCreation()
  {
    return false;
  }
  
  public final synchronized SSLParameters getSSLParameters()
  {
    SSLParameters sslParameters = super.getSSLParameters();
    
    int version = PlatformDependent.javaVersion();
    if (version >= 7) {
      sslParameters.setEndpointIdentificationAlgorithm(endPointIdentificationAlgorithm);
      SslParametersUtils.setAlgorithmConstraints(sslParameters, algorithmConstraints);
      if (version >= 8) {
        if ((SET_SERVER_NAMES_METHOD != null) && (sniHostNames != null)) {
          try {
            SET_SERVER_NAMES_METHOD.invoke(sslParameters, new Object[] { sniHostNames });
          } catch (IllegalAccessException e) {
            throw new Error(e);
          } catch (InvocationTargetException e) {
            throw new Error(e);
          }
        }
        if ((SET_USE_CIPHER_SUITES_ORDER_METHOD != null) && (!isDestroyed())) {
          try {
            SET_USE_CIPHER_SUITES_ORDER_METHOD.invoke(sslParameters, new Object[] { Boolean.valueOf((SSL.getOptions(ssl) & 0x400000) != 0 ? 1 : false) });
          }
          catch (IllegalAccessException e) {
            throw new Error(e);
          } catch (InvocationTargetException e) {
            throw new Error(e);
          }
        }
      }
    }
    return sslParameters;
  }
  
  public final synchronized void setSSLParameters(SSLParameters sslParameters)
  {
    super.setSSLParameters(sslParameters);
    
    int version = PlatformDependent.javaVersion();
    if (version >= 7) {
      endPointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm();
      algorithmConstraints = sslParameters.getAlgorithmConstraints();
      if (version >= 8) {
        if ((SNI_HOSTNAME_CLASS != null) && (clientMode) && (!isDestroyed())) {
          assert (GET_SERVER_NAMES_METHOD != null);
          assert (GET_ASCII_NAME_METHOD != null);
          try {
            List<?> servernames = (List)GET_SERVER_NAMES_METHOD.invoke(sslParameters, new Object[0]);
            if (servernames != null) {
              for (Object serverName : servernames) {
                if (SNI_HOSTNAME_CLASS.isInstance(serverName)) {
                  SSL.setTlsExtHostName(ssl, (String)GET_ASCII_NAME_METHOD.invoke(serverName, new Object[0]));
                } else {
                  throw new IllegalArgumentException("Only " + SNI_HOSTNAME_CLASS.getName() + " instances are supported, but found: " + serverName);
                }
              }
            }
            

            sniHostNames = servernames;
          } catch (IllegalAccessException e) {
            throw new Error(e);
          } catch (InvocationTargetException e) {
            throw new Error(e);
          }
        }
        if ((GET_USE_CIPHER_SUITES_ORDER_METHOD != null) && (!isDestroyed())) {
          try {
            if (((Boolean)GET_USE_CIPHER_SUITES_ORDER_METHOD.invoke(sslParameters, new Object[0])).booleanValue()) {
              SSL.setOptions(ssl, 4194304);
            } else {
              SSL.clearOptions(ssl, 4194304);
            }
          } catch (IllegalAccessException e) {
            throw new Error(e);
          } catch (InvocationTargetException e) {
            throw new Error(e);
          }
        }
      }
    }
  }
  
  private boolean isDestroyed() {
    return destroyed != 0;
  }
  

  private final class OpenSslSession
    implements SSLSession, ApplicationProtocolAccessor
  {
    private final OpenSslSessionContext sessionContext;
    
    private javax.security.cert.X509Certificate[] x509PeerCerts;
    private String protocol;
    private String applicationProtocol;
    private Certificate[] peerCerts;
    private String cipher;
    private byte[] id;
    private long creationTime;
    private Map<String, Object> values;
    
    OpenSslSession(OpenSslSessionContext sessionContext)
    {
      this.sessionContext = sessionContext;
    }
    
    public byte[] getId()
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (id == null) {
          return EmptyArrays.EMPTY_BYTES;
        }
        return (byte[])id.clone();
      }
    }
    
    public SSLSessionContext getSessionContext()
    {
      return sessionContext;
    }
    
    public long getCreationTime()
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if ((creationTime == 0L) && (!ReferenceCountedOpenSslEngine.this.isDestroyed())) {
          creationTime = (SSL.getTime(ssl) * 1000L);
        }
      }
      return creationTime;
    }
    
    public long getLastAccessedTime()
    {
      long lastAccessed = ReferenceCountedOpenSslEngine.this.lastAccessed;
      
      return lastAccessed == -1L ? getCreationTime() : lastAccessed;
    }
    
    public void invalidate()
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
          SSL.setTimeout(ssl, 0L);
        }
      }
    }
    
    public boolean isValid()
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
          return System.currentTimeMillis() - SSL.getTimeout(ssl) * 1000L < SSL.getTime(ssl) * 1000L;
        }
      }
      return false;
    }
    
    public void putValue(String name, Object value)
    {
      if (name == null) {
        throw new NullPointerException("name");
      }
      if (value == null) {
        throw new NullPointerException("value");
      }
      Map<String, Object> values = this.values;
      if (values == null)
      {
        values = this.values = new HashMap(2);
      }
      Object old = values.put(name, value);
      if ((value instanceof SSLSessionBindingListener)) {
        ((SSLSessionBindingListener)value).valueBound(new SSLSessionBindingEvent(this, name));
      }
      notifyUnbound(old, name);
    }
    
    public Object getValue(String name)
    {
      if (name == null) {
        throw new NullPointerException("name");
      }
      if (values == null) {
        return null;
      }
      return values.get(name);
    }
    
    public void removeValue(String name)
    {
      if (name == null) {
        throw new NullPointerException("name");
      }
      Map<String, Object> values = this.values;
      if (values == null) {
        return;
      }
      Object old = values.remove(name);
      notifyUnbound(old, name);
    }
    
    public String[] getValueNames()
    {
      Map<String, Object> values = this.values;
      if ((values == null) || (values.isEmpty())) {
        return EmptyArrays.EMPTY_STRINGS;
      }
      return (String[])values.keySet().toArray(new String[values.size()]);
    }
    
    private void notifyUnbound(Object value, String name) {
      if ((value instanceof SSLSessionBindingListener)) {
        ((SSLSessionBindingListener)value).valueUnbound(new SSLSessionBindingEvent(this, name));
      }
    }
    


    void handshakeFinished()
      throws SSLException
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
          id = SSL.getSessionId(ssl);
          cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(SSL.getCipherForSSL(ssl));
          protocol = SSL.getVersion(ssl);
          
          initPeerCerts();
          selectApplicationProtocol();
          
          handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.FINISHED;
        } else {
          throw new SSLException("Already closed");
        }
      }
    }
    




    private void initPeerCerts()
    {
      byte[][] chain = SSL.getPeerCertChain(ssl);
      byte[] clientCert;
      byte[] clientCert; if (!clientMode)
      {




        clientCert = SSL.getPeerCertificate(ssl);
      } else {
        clientCert = null;
      }
      
      if ((chain == null) || (chain.length == 0)) {
        if ((clientCert == null) || (clientCert.length == 0)) {
          peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
          x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
        } else {
          peerCerts = new Certificate[1];
          x509PeerCerts = new javax.security.cert.X509Certificate[1];
          
          peerCerts[0] = new OpenSslX509Certificate(clientCert);
          x509PeerCerts[0] = new OpenSslJavaxX509Certificate(clientCert);
        }
      } else if ((clientCert == null) || (clientCert.length == 0)) {
        peerCerts = new Certificate[chain.length];
        x509PeerCerts = new javax.security.cert.X509Certificate[chain.length];
        
        for (int a = 0; a < chain.length; a++) {
          byte[] bytes = chain[a];
          peerCerts[a] = new OpenSslX509Certificate(bytes);
          x509PeerCerts[a] = new OpenSslJavaxX509Certificate(bytes);
        }
      } else {
        int len = clientCert.length + 1;
        peerCerts = new Certificate[len];
        x509PeerCerts = new javax.security.cert.X509Certificate[len];
        
        peerCerts[0] = new OpenSslX509Certificate(clientCert);
        x509PeerCerts[0] = new OpenSslJavaxX509Certificate(clientCert);
        
        int a = 0; for (int i = 1; a < chain.length; i++) {
          byte[] bytes = chain[a];
          peerCerts[i] = new OpenSslX509Certificate(bytes);
          x509PeerCerts[i] = new OpenSslJavaxX509Certificate(bytes);a++;
        }
      }
    }
    



    private void selectApplicationProtocol()
      throws SSLException
    {
      ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior = apn.selectedListenerFailureBehavior();
      List<String> protocols = apn.protocols();
      
      switch (ReferenceCountedOpenSslEngine.2.$SwitchMap$io$netty$handler$ssl$ApplicationProtocolConfig$Protocol[apn.protocol().ordinal()])
      {
      case 1: 
        break;
      
      case 2: 
        String applicationProtocol = SSL.getAlpnSelected(ssl);
        if (applicationProtocol != null) {
          this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol);
        }
        
        break;
      case 3: 
        String applicationProtocol = SSL.getNextProtoNegotiated(ssl);
        if (applicationProtocol != null) {
          this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol);
        }
        
        break;
      case 4: 
        String applicationProtocol = SSL.getAlpnSelected(ssl);
        if (applicationProtocol == null) {
          applicationProtocol = SSL.getNextProtoNegotiated(ssl);
        }
        if (applicationProtocol != null) {
          this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol);
        }
        
        break;
      default: 
        throw new Error();
      }
    }
    
    private String selectApplicationProtocol(List<String> protocols, ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior, String applicationProtocol)
      throws SSLException
    {
      if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT) {
        return applicationProtocol;
      }
      int size = protocols.size();
      assert (size > 0);
      if (protocols.contains(applicationProtocol)) {
        return applicationProtocol;
      }
      if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL) {
        return (String)protocols.get(size - 1);
      }
      throw new SSLException("unknown protocol " + applicationProtocol);
    }
    


    public Certificate[] getPeerCertificates()
      throws SSLPeerUnverifiedException
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if ((peerCerts == null) || (peerCerts.length == 0)) {
          throw new SSLPeerUnverifiedException("peer not verified");
        }
        return (Certificate[])peerCerts.clone();
      }
    }
    
    public Certificate[] getLocalCertificates()
    {
      if (localCerts == null) {
        return null;
      }
      return (Certificate[])localCerts.clone();
    }
    
    public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if ((x509PeerCerts == null) || (x509PeerCerts.length == 0)) {
          throw new SSLPeerUnverifiedException("peer not verified");
        }
        return (javax.security.cert.X509Certificate[])x509PeerCerts.clone();
      }
    }
    
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException
    {
      Certificate[] peer = getPeerCertificates();
      

      return ((java.security.cert.X509Certificate)peer[0]).getSubjectX500Principal();
    }
    
    public Principal getLocalPrincipal()
    {
      Certificate[] local = localCerts;
      if ((local == null) || (local.length == 0)) {
        return null;
      }
      return ((java.security.cert.X509Certificate)local[0]).getIssuerX500Principal();
    }
    
    public String getCipherSuite()
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (cipher == null) {
          return "SSL_NULL_WITH_NULL_NULL";
        }
        return cipher;
      }
    }
    
    public String getProtocol()
    {
      String protocol = this.protocol;
      if (protocol == null) {
        synchronized (ReferenceCountedOpenSslEngine.this) {
          if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
            protocol = SSL.getVersion(ssl);
          } else {
            protocol = "";
          }
        }
      }
      return protocol;
    }
    
    public String getApplicationProtocol()
    {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        return applicationProtocol;
      }
    }
    
    public String getPeerHost()
    {
      return ReferenceCountedOpenSslEngine.this.getPeerHost();
    }
    
    public int getPeerPort()
    {
      return ReferenceCountedOpenSslEngine.this.getPeerPort();
    }
    
    public int getPacketBufferSize()
    {
      return 18713;
    }
    
    public int getApplicationBufferSize()
    {
      return 16384;
    }
  }
}
