package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;




































































public abstract class AbstractHttp2ConnectionHandlerBuilder<T extends Http2ConnectionHandler, B extends AbstractHttp2ConnectionHandlerBuilder<T, B>>
{
  private static final long DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS = TimeUnit.MILLISECONDS.convert(30L, TimeUnit.SECONDS);
  private static final Http2HeadersEncoder.SensitivityDetector DEFAULT_HEADER_SENSITIVITY_DETECTOR = Http2HeadersEncoder.NEVER_SENSITIVE;
  

  private Http2Settings initialSettings = new Http2Settings();
  private Http2FrameListener frameListener;
  private long gracefulShutdownTimeoutMillis = DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS;
  
  private Boolean isServer;
  
  private Http2Connection connection;
  
  private Http2ConnectionDecoder decoder;
  
  private Http2ConnectionEncoder encoder;
  
  private Boolean validateHeaders;
  
  private Http2FrameLogger frameLogger;
  
  private Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector;
  
  private Boolean encoderEnforceMaxConcurrentStreams;
  
  private Boolean encoderIgnoreMaxHeaderListSize;
  

  public AbstractHttp2ConnectionHandlerBuilder() {}
  

  protected Http2Settings initialSettings()
  {
    return initialSettings;
  }
  


  protected B initialSettings(Http2Settings settings)
  {
    initialSettings = ((Http2Settings)ObjectUtil.checkNotNull(settings, "settings"));
    return self();
  }
  




  protected Http2FrameListener frameListener()
  {
    return frameListener;
  }
  



  protected B frameListener(Http2FrameListener frameListener)
  {
    this.frameListener = ((Http2FrameListener)ObjectUtil.checkNotNull(frameListener, "frameListener"));
    return self();
  }
  


  protected long gracefulShutdownTimeoutMillis()
  {
    return gracefulShutdownTimeoutMillis;
  }
  


  protected B gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis)
  {
    this.gracefulShutdownTimeoutMillis = gracefulShutdownTimeoutMillis;
    return self();
  }
  



  protected boolean isServer()
  {
    return isServer != null ? isServer.booleanValue() : true;
  }
  



  protected B server(boolean isServer)
  {
    enforceConstraint("server", "connection", connection);
    enforceConstraint("server", "codec", decoder);
    enforceConstraint("server", "codec", encoder);
    
    this.isServer = Boolean.valueOf(isServer);
    return self();
  }
  




  protected Http2Connection connection()
  {
    return connection;
  }
  


  protected B connection(Http2Connection connection)
  {
    enforceConstraint("connection", "server", isServer);
    enforceConstraint("connection", "codec", decoder);
    enforceConstraint("connection", "codec", encoder);
    
    this.connection = ((Http2Connection)ObjectUtil.checkNotNull(connection, "connection"));
    
    return self();
  }
  




  protected Http2ConnectionDecoder decoder()
  {
    return decoder;
  }
  




  protected Http2ConnectionEncoder encoder()
  {
    return encoder;
  }
  


  protected B codec(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder)
  {
    enforceConstraint("codec", "server", isServer);
    enforceConstraint("codec", "connection", connection);
    enforceConstraint("codec", "frameLogger", frameLogger);
    enforceConstraint("codec", "validateHeaders", validateHeaders);
    enforceConstraint("codec", "headerSensitivityDetector", headerSensitivityDetector);
    enforceConstraint("codec", "encoderEnforceMaxConcurrentStreams", encoderEnforceMaxConcurrentStreams);
    
    ObjectUtil.checkNotNull(decoder, "decoder");
    ObjectUtil.checkNotNull(encoder, "encoder");
    
    if (decoder.connection() != encoder.connection()) {
      throw new IllegalArgumentException("The specified encoder and decoder have different connections.");
    }
    
    this.decoder = decoder;
    this.encoder = encoder;
    
    return self();
  }
  



  protected boolean isValidateHeaders()
  {
    return validateHeaders != null ? validateHeaders.booleanValue() : true;
  }
  



  protected B validateHeaders(boolean validateHeaders)
  {
    enforceNonCodecConstraints("validateHeaders");
    this.validateHeaders = Boolean.valueOf(validateHeaders);
    return self();
  }
  




  protected Http2FrameLogger frameLogger()
  {
    return frameLogger;
  }
  


  protected B frameLogger(Http2FrameLogger frameLogger)
  {
    enforceNonCodecConstraints("frameLogger");
    this.frameLogger = ((Http2FrameLogger)ObjectUtil.checkNotNull(frameLogger, "frameLogger"));
    return self();
  }
  



  protected boolean encoderEnforceMaxConcurrentStreams()
  {
    return encoderEnforceMaxConcurrentStreams != null ? encoderEnforceMaxConcurrentStreams.booleanValue() : false;
  }
  



  protected B encoderEnforceMaxConcurrentStreams(boolean encoderEnforceMaxConcurrentStreams)
  {
    enforceNonCodecConstraints("encoderEnforceMaxConcurrentStreams");
    this.encoderEnforceMaxConcurrentStreams = Boolean.valueOf(encoderEnforceMaxConcurrentStreams);
    return self();
  }
  


  protected Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector()
  {
    return headerSensitivityDetector != null ? headerSensitivityDetector : DEFAULT_HEADER_SENSITIVITY_DETECTOR;
  }
  


  protected B headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector)
  {
    enforceNonCodecConstraints("headerSensitivityDetector");
    this.headerSensitivityDetector = ((Http2HeadersEncoder.SensitivityDetector)ObjectUtil.checkNotNull(headerSensitivityDetector, "headerSensitivityDetector"));
    return self();
  }
  






  protected B encoderIgnoreMaxHeaderListSize(boolean ignoreMaxHeaderListSize)
  {
    enforceNonCodecConstraints("encoderIgnoreMaxHeaderListSize");
    encoderIgnoreMaxHeaderListSize = Boolean.valueOf(ignoreMaxHeaderListSize);
    return self();
  }
  


  protected T build()
  {
    if (encoder != null) {
      assert (decoder != null);
      return buildFromCodec(decoder, encoder);
    }
    
    Http2Connection connection = this.connection;
    if (connection == null) {
      connection = new DefaultHttp2Connection(isServer());
    }
    
    return buildFromConnection(connection);
  }
  
  private T buildFromConnection(Http2Connection connection) {
    Http2FrameReader reader = new DefaultHttp2FrameReader(isValidateHeaders());
    Http2FrameWriter writer = encoderIgnoreMaxHeaderListSize == null ? new DefaultHttp2FrameWriter(headerSensitivityDetector()) : new DefaultHttp2FrameWriter(headerSensitivityDetector(), encoderIgnoreMaxHeaderListSize.booleanValue());
    


    if (frameLogger != null) {
      reader = new Http2InboundFrameLogger(reader, frameLogger);
      writer = new Http2OutboundFrameLogger(writer, frameLogger);
    }
    
    Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, writer);
    boolean encoderEnforceMaxConcurrentStreams = encoderEnforceMaxConcurrentStreams();
    
    if (encoderEnforceMaxConcurrentStreams) {
      if (connection.isServer()) {
        encoder.close();
        reader.close();
        throw new IllegalArgumentException("encoderEnforceMaxConcurrentStreams: " + encoderEnforceMaxConcurrentStreams + " not supported for server");
      }
      

      encoder = new StreamBufferingEncoder(encoder);
    }
    
    Http2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, reader);
    return buildFromCodec(decoder, encoder);
  }
  
  private T buildFromCodec(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder)
  {
    try
    {
      handler = build(decoder, encoder, initialSettings);
    } catch (Throwable t) { T handler;
      encoder.close();
      decoder.close();
      throw new IllegalStateException("failed to build a Http2ConnectionHandler", t);
    }
    
    T handler;
    handler.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
    if (handler.decoder().frameListener() == null) {
      handler.decoder().frameListener(frameListener);
    }
    return handler;
  }
  






  protected abstract T build(Http2ConnectionDecoder paramHttp2ConnectionDecoder, Http2ConnectionEncoder paramHttp2ConnectionEncoder, Http2Settings paramHttp2Settings)
    throws Exception;
  






  protected final B self()
  {
    return this;
  }
  
  private void enforceNonCodecConstraints(String rejectee) {
    enforceConstraint(rejectee, "server/connection", decoder);
    enforceConstraint(rejectee, "server/connection", encoder);
  }
  
  private static void enforceConstraint(String methodName, String rejectorName, Object value) {
    if (value != null) {
      throw new IllegalStateException(methodName + "() cannot be called because " + rejectorName + "() has been called already.");
    }
  }
}
