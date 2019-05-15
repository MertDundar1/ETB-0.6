package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.tomcat.jni.SSLContext;


























public final class ReferenceCountedOpenSslServerContext
  extends ReferenceCountedOpenSslContext
{
  private static final byte[] ID = { 110, 101, 116, 116, 121 };
  
  private final OpenSslServerSessionContext sessionContext;
  
  private final OpenSslKeyMaterialManager keyMaterialManager;
  
  ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, boolean startTls)
    throws SSLException
  {
    this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, toNegotiator(apn), sessionCacheSize, sessionTimeout, clientAuth, startTls);
  }
  



  private ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, boolean startTls)
    throws SSLException
  {
    super(ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 1, keyCertChain, clientAuth, startTls, true);
    

    boolean success = false;
    try {
      ServerContext context = newSessionContext(this, ctx, engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory);
      
      sessionContext = sessionContext;
      keyMaterialManager = keyMaterialManager;
      success = true;
    } finally {
      if (!success) {
        release();
      }
    }
  }
  
  public OpenSslServerSessionContext sessionContext()
  {
    return sessionContext;
  }
  
  OpenSslKeyMaterialManager keyMaterialManager()
  {
    return keyMaterialManager;
  }
  








  static ServerContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory)
    throws SSLException
  {
    ServerContext result = new ServerContext();
    synchronized (ReferenceCountedOpenSslContext.class) {
      try {
        SSLContext.setVerify(ctx, 0, 10);
        if (!OpenSsl.useKeyManagerFactory()) {
          if (keyManagerFactory != null) {
            throw new IllegalArgumentException("KeyManagerFactory not supported");
          }
          
          ObjectUtil.checkNotNull(keyCertChain, "keyCertChain");
          

          SSLContext.setVerify(ctx, 0, 10);
          
          setKeyMaterial(ctx, keyCertChain, key, keyPassword);
        }
        else
        {
          if (keyManagerFactory == null) {
            keyManagerFactory = buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory);
          }
          
          X509KeyManager keyManager = chooseX509KeyManager(keyManagerFactory.getKeyManagers());
          keyMaterialManager = (useExtendedKeyManager(keyManager) ? new OpenSslExtendedKeyMaterialManager((X509ExtendedKeyManager)keyManager, keyPassword) : new OpenSslKeyMaterialManager(keyManager, keyPassword));
        }
        
      }
      catch (Exception e)
      {
        throw new SSLException("failed to set certificate and key", e);
      }
      try {
        if (trustCertCollection != null) {
          trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory);
        } else if (trustManagerFactory == null)
        {
          trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
          
          trustManagerFactory.init((KeyStore)null);
        }
        
        X509TrustManager manager = chooseTrustManager(trustManagerFactory.getTrustManagers());
        







        if (useExtendedTrustManager(manager)) {
          SSLContext.setCertVerifyCallback(ctx, new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
        }
        else {
          SSLContext.setCertVerifyCallback(ctx, new TrustManagerVerifyCallback(engineMap, manager));
        }
      } catch (Exception e) {
        throw new SSLException("unable to setup trustmanager", e);
      }
    }
    
    sessionContext = new OpenSslServerSessionContext(thiz);
    sessionContext.setSessionIdContext(ID);
    return result; }
  
  static final class ServerContext { OpenSslServerSessionContext sessionContext;
    OpenSslKeyMaterialManager keyMaterialManager;
    
    ServerContext() {} }
  
  private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier { TrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509TrustManager manager) { super();
      this.manager = manager;
    }
    
    private final X509TrustManager manager;
    void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception
    {
      manager.checkClientTrusted(peerCerts, auth);
    }
  }
  
  private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
    private final X509ExtendedTrustManager manager;
    
    ExtendedTrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509ExtendedTrustManager manager) {
      super();
      this.manager = manager;
    }
    
    void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth)
      throws Exception
    {
      manager.checkClientTrusted(peerCerts, auth, engine);
    }
  }
}
