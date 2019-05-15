package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;























public final class PerMessageDeflateClientExtensionHandshaker
  implements WebSocketClientExtensionHandshaker
{
  private final int compressionLevel;
  private final boolean allowClientWindowSize;
  private final int requestedServerWindowSize;
  private final boolean allowClientNoContext;
  private final boolean requestedServerNoContext;
  
  public PerMessageDeflateClientExtensionHandshaker()
  {
    this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false);
  }
  


















  public PerMessageDeflateClientExtensionHandshaker(int compressionLevel, boolean allowClientWindowSize, int requestedServerWindowSize, boolean allowClientNoContext, boolean requestedServerNoContext)
  {
    if ((requestedServerWindowSize > 15) || (requestedServerWindowSize < 8)) {
      throw new IllegalArgumentException("requestedServerWindowSize: " + requestedServerWindowSize + " (expected: 8-15)");
    }
    
    if ((compressionLevel < 0) || (compressionLevel > 9)) {
      throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
    }
    
    this.compressionLevel = compressionLevel;
    this.allowClientWindowSize = allowClientWindowSize;
    this.requestedServerWindowSize = requestedServerWindowSize;
    this.allowClientNoContext = allowClientNoContext;
    this.requestedServerNoContext = requestedServerNoContext;
  }
  
  public WebSocketExtensionData newRequestData()
  {
    HashMap<String, String> parameters = new HashMap(4);
    if (requestedServerWindowSize != 15) {
      parameters.put("server_no_context_takeover", null);
    }
    if (allowClientNoContext) {
      parameters.put("client_no_context_takeover", null);
    }
    if (requestedServerWindowSize != 15) {
      parameters.put("server_max_window_bits", Integer.toString(requestedServerWindowSize));
    }
    if (allowClientWindowSize) {
      parameters.put("client_max_window_bits", null);
    }
    return new WebSocketExtensionData("permessage-deflate", parameters);
  }
  
  public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData)
  {
    if (!"permessage-deflate".equals(extensionData.name())) {
      return null;
    }
    
    boolean succeed = true;
    int clientWindowSize = 15;
    int serverWindowSize = 15;
    boolean serverNoContext = false;
    boolean clientNoContext = false;
    
    Iterator<Map.Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();
    
    while ((succeed) && (parametersIterator.hasNext())) {
      Map.Entry<String, String> parameter = (Map.Entry)parametersIterator.next();
      
      if ("client_max_window_bits".equalsIgnoreCase((String)parameter.getKey()))
      {
        if (allowClientWindowSize) {
          clientWindowSize = Integer.parseInt((String)parameter.getValue());
        } else {
          succeed = false;
        }
      } else if ("server_max_window_bits".equalsIgnoreCase((String)parameter.getKey()))
      {
        serverWindowSize = Integer.parseInt((String)parameter.getValue());
        if ((clientWindowSize > 15) || (clientWindowSize < 8)) {
          succeed = false;
        }
      } else if ("client_no_context_takeover".equalsIgnoreCase((String)parameter.getKey()))
      {
        if (allowClientNoContext) {
          clientNoContext = true;
        } else {
          succeed = false;
        }
      } else if ("server_no_context_takeover".equalsIgnoreCase((String)parameter.getKey()))
      {
        if (requestedServerNoContext) {
          serverNoContext = true;
        } else {
          succeed = false;
        }
      }
      else {
        succeed = false;
      }
    }
    
    if (((requestedServerNoContext) && (!serverNoContext)) || (requestedServerWindowSize != serverWindowSize))
    {
      succeed = false;
    }
    
    if (succeed) {
      return new PermessageDeflateExtension(serverNoContext, serverWindowSize, clientNoContext, clientWindowSize);
    }
    
    return null;
  }
  
  private final class PermessageDeflateExtension
    implements WebSocketClientExtension
  {
    private final boolean serverNoContext;
    private final int serverWindowSize;
    private final boolean clientNoContext;
    private final int clientWindowSize;
    
    public int rsv()
    {
      return 4;
    }
    
    public PermessageDeflateExtension(boolean serverNoContext, int serverWindowSize, boolean clientNoContext, int clientWindowSize)
    {
      this.serverNoContext = serverNoContext;
      this.serverWindowSize = serverWindowSize;
      this.clientNoContext = clientNoContext;
      this.clientWindowSize = clientWindowSize;
    }
    
    public WebSocketExtensionEncoder newExtensionEncoder()
    {
      return new PerMessageDeflateEncoder(compressionLevel, serverWindowSize, serverNoContext);
    }
    
    public WebSocketExtensionDecoder newExtensionDecoder()
    {
      return new PerMessageDeflateDecoder(clientNoContext);
    }
  }
}
