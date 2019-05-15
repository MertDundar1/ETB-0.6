package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
















































public class HttpPostStandardRequestDecoder
  implements InterfaceHttpPostRequestDecoder
{
  private final HttpDataFactory factory;
  private final HttpRequest request;
  private final Charset charset;
  private boolean isLastChunk;
  private final List<InterfaceHttpData> bodyListHttpData = new ArrayList();
  



  private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
  




  private ByteBuf undecodedChunk;
  



  private int bodyListHttpDataRank;
  



  private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
  

  private Attribute currentAttribute;
  

  private boolean destroyed;
  

  private int discardThreshold = 10485760;
  









  public HttpPostStandardRequestDecoder(HttpRequest request)
  {
    this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
  }
  











  public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request)
  {
    this(factory, request, HttpConstants.DEFAULT_CHARSET);
  }
  













  public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset)
  {
    if (factory == null) {
      throw new NullPointerException("factory");
    }
    if (request == null) {
      throw new NullPointerException("request");
    }
    if (charset == null) {
      throw new NullPointerException("charset");
    }
    this.request = request;
    this.charset = charset;
    this.factory = factory;
    if ((request instanceof HttpContent))
    {

      offer((HttpContent)request);
    } else {
      undecodedChunk = Unpooled.buffer();
      parseBody();
    }
  }
  
  private void checkDestroyed() {
    if (destroyed) {
      throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already");
    }
  }
  






  public boolean isMultipart()
  {
    checkDestroyed();
    return false;
  }
  





  public void setDiscardThreshold(int discardThreshold)
  {
    if (discardThreshold < 0) {
      throw new IllegalArgumentException("discardThreshold must be >= 0");
    }
    this.discardThreshold = discardThreshold;
  }
  



  public int getDiscardThreshold()
  {
    return discardThreshold;
  }
  










  public List<InterfaceHttpData> getBodyHttpDatas()
  {
    checkDestroyed();
    
    if (!isLastChunk) {
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    return bodyListHttpData;
  }
  











  public List<InterfaceHttpData> getBodyHttpDatas(String name)
  {
    checkDestroyed();
    
    if (!isLastChunk) {
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    return (List)bodyMapHttpData.get(name);
  }
  












  public InterfaceHttpData getBodyHttpData(String name)
  {
    checkDestroyed();
    
    if (!isLastChunk) {
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    List<InterfaceHttpData> list = (List)bodyMapHttpData.get(name);
    if (list != null) {
      return (InterfaceHttpData)list.get(0);
    }
    return null;
  }
  









  public HttpPostStandardRequestDecoder offer(HttpContent content)
  {
    checkDestroyed();
    



    ByteBuf buf = content.content();
    if (undecodedChunk == null) {
      undecodedChunk = buf.copy();
    } else {
      undecodedChunk.writeBytes(buf);
    }
    if ((content instanceof LastHttpContent)) {
      isLastChunk = true;
    }
    parseBody();
    if ((undecodedChunk != null) && (undecodedChunk.writerIndex() > discardThreshold)) {
      undecodedChunk.discardReadBytes();
    }
    return this;
  }
  










  public boolean hasNext()
  {
    checkDestroyed();
    
    if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE)
    {
      if (bodyListHttpDataRank >= bodyListHttpData.size()) {
        throw new HttpPostRequestDecoder.EndOfDataDecoderException();
      }
    }
    return (!bodyListHttpData.isEmpty()) && (bodyListHttpDataRank < bodyListHttpData.size());
  }
  












  public InterfaceHttpData next()
  {
    checkDestroyed();
    
    if (hasNext()) {
      return (InterfaceHttpData)bodyListHttpData.get(bodyListHttpDataRank++);
    }
    return null;
  }
  
  public InterfaceHttpData currentPartialHttpData()
  {
    return currentAttribute;
  }
  






  private void parseBody()
  {
    if ((currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE) || (currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE)) {
      if (isLastChunk) {
        currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
      }
      return;
    }
    parseBodyAttributes();
  }
  


  protected void addHttpData(InterfaceHttpData data)
  {
    if (data == null) {
      return;
    }
    List<InterfaceHttpData> datas = (List)bodyMapHttpData.get(data.getName());
    if (datas == null) {
      datas = new ArrayList(1);
      bodyMapHttpData.put(data.getName(), datas);
    }
    datas.add(data);
    bodyListHttpData.add(data);
  }
  







  private void parseBodyAttributesStandard()
  {
    int firstpos = undecodedChunk.readerIndex();
    int currentpos = firstpos;
    

    if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
      currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
    }
    boolean contRead = true;
    try {
      while ((undecodedChunk.isReadable()) && (contRead)) {
        char read = (char)undecodedChunk.readUnsignedByte();
        currentpos++;
        switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[currentStatus.ordinal()]) {
        case 1: 
          if (read == '=') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            int equalpos = currentpos - 1;
            String key = decodeAttribute(undecodedChunk.toString(firstpos, equalpos - firstpos, charset), charset);
            
            currentAttribute = factory.createAttribute(request, key);
            firstpos = currentpos;
          } else if (read == '&') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
            int ampersandpos = currentpos - 1;
            String key = decodeAttribute(undecodedChunk.toString(firstpos, ampersandpos - firstpos, charset), charset);
            
            currentAttribute = factory.createAttribute(request, key);
            currentAttribute.setValue("");
            addHttpData(currentAttribute);
            currentAttribute = null;
            firstpos = currentpos;
            contRead = true; }
          break;
        
        case 2: 
          if (read == '&') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
            int ampersandpos = currentpos - 1;
            setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
            firstpos = currentpos;
            contRead = true;
          } else if (read == '\r') {
            if (undecodedChunk.isReadable()) {
              read = (char)undecodedChunk.readUnsignedByte();
              currentpos++;
              if (read == '\n') {
                currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                int ampersandpos = currentpos - 2;
                setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                firstpos = currentpos;
                contRead = false;
              }
              else {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
              }
            } else {
              currentpos--;
            }
          } else if (read == '\n') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
            int ampersandpos = currentpos - 1;
            setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
            firstpos = currentpos;
            contRead = false;
          }
          
          break;
        default: 
          contRead = false;
        }
      }
      if ((isLastChunk) && (currentAttribute != null))
      {
        int ampersandpos = currentpos;
        if (ampersandpos > firstpos) {
          setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
        } else if (!currentAttribute.isCompleted()) {
          setFinalBuffer(Unpooled.EMPTY_BUFFER);
        }
        firstpos = currentpos;
        currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
        undecodedChunk.readerIndex(firstpos);
        return;
      }
      if ((contRead) && (currentAttribute != null))
      {
        if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
          currentAttribute.addContent(undecodedChunk.copy(firstpos, currentpos - firstpos), false);
          
          firstpos = currentpos;
        }
        undecodedChunk.readerIndex(firstpos);
      }
      else {
        undecodedChunk.readerIndex(firstpos);
      }
    }
    catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
      undecodedChunk.readerIndex(firstpos);
      throw e;
    }
    catch (IOException e) {
      undecodedChunk.readerIndex(firstpos);
      throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
    }
  }
  







  private void parseBodyAttributes()
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) { HttpPostBodyUtil.SeekAheadOptimize sao;
      parseBodyAttributesStandard(); return;
    }
    HttpPostBodyUtil.SeekAheadOptimize sao;
    int firstpos = undecodedChunk.readerIndex();
    int currentpos = firstpos;
    

    if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
      currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
    }
    boolean contRead = true;
    try {
      while (pos < limit) {
        char read = (char)(bytes[(pos++)] & 0xFF);
        currentpos++;
        switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[currentStatus.ordinal()]) {
        case 1: 
          if (read == '=') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            int equalpos = currentpos - 1;
            String key = decodeAttribute(undecodedChunk.toString(firstpos, equalpos - firstpos, charset), charset);
            
            currentAttribute = factory.createAttribute(request, key);
            firstpos = currentpos;
          } else if (read == '&') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
            int ampersandpos = currentpos - 1;
            String key = decodeAttribute(undecodedChunk.toString(firstpos, ampersandpos - firstpos, charset), charset);
            
            currentAttribute = factory.createAttribute(request, key);
            currentAttribute.setValue("");
            addHttpData(currentAttribute);
            currentAttribute = null;
            firstpos = currentpos;
            contRead = true; }
          break;
        
        case 2: 
          if (read == '&') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
            int ampersandpos = currentpos - 1;
            setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
            firstpos = currentpos;
            contRead = true;
          } else if (read == '\r') {
            if (pos < limit) {
              read = (char)(bytes[(pos++)] & 0xFF);
              currentpos++;
              if (read == '\n') {
                currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                int ampersandpos = currentpos - 2;
                sao.setReadPosition(0);
                setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                firstpos = currentpos;
                contRead = false;
                
                break label512;
              }
              sao.setReadPosition(0);
              throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
            }
            
            if (limit > 0) {
              currentpos--;
            }
          }
          else if (read == '\n') {
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
            int ampersandpos = currentpos - 1;
            sao.setReadPosition(0);
            setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
            firstpos = currentpos;
            contRead = false; }
          break;
        


        default: 
          sao.setReadPosition(0);
          contRead = false;
          break label512; }
      }
      label512:
      if ((isLastChunk) && (currentAttribute != null))
      {
        int ampersandpos = currentpos;
        if (ampersandpos > firstpos) {
          setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
        } else if (!currentAttribute.isCompleted()) {
          setFinalBuffer(Unpooled.EMPTY_BUFFER);
        }
        firstpos = currentpos;
        currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
        undecodedChunk.readerIndex(firstpos);
        return;
      }
      if ((contRead) && (currentAttribute != null))
      {
        if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
          currentAttribute.addContent(undecodedChunk.copy(firstpos, currentpos - firstpos), false);
          
          firstpos = currentpos;
        }
        undecodedChunk.readerIndex(firstpos);
      }
      else {
        undecodedChunk.readerIndex(firstpos);
      }
    }
    catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
      undecodedChunk.readerIndex(firstpos);
      throw e;
    }
    catch (IOException e) {
      undecodedChunk.readerIndex(firstpos);
      throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
    }
    catch (IllegalArgumentException e) {
      undecodedChunk.readerIndex(firstpos);
      throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
    }
  }
  
  private void setFinalBuffer(ByteBuf buffer) throws IOException {
    currentAttribute.addContent(buffer, true);
    String value = decodeAttribute(currentAttribute.getByteBuf().toString(charset), charset);
    currentAttribute.setValue(value);
    addHttpData(currentAttribute);
    currentAttribute = null;
  }
  



  private static String decodeAttribute(String s, Charset charset)
  {
    try
    {
      return QueryStringDecoder.decodeComponent(s, charset);
    } catch (IllegalArgumentException e) {
      throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', e);
    }
  }
  


  void skipControlCharacters()
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) {
      try { HttpPostBodyUtil.SeekAheadOptimize sao;
        skipControlCharactersStandard();
      } catch (IndexOutOfBoundsException e) {
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
      }
      return;
    }
    HttpPostBodyUtil.SeekAheadOptimize sao;
    while (pos < limit) {
      char c = (char)(bytes[(pos++)] & 0xFF);
      if ((!Character.isISOControl(c)) && (!Character.isWhitespace(c))) {
        sao.setReadPosition(1);
        return;
      }
    }
    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
  }
  
  void skipControlCharactersStandard() {
    for (;;) {
      char c = (char)undecodedChunk.readUnsignedByte();
      if ((!Character.isISOControl(c)) && (!Character.isWhitespace(c))) {
        undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
        break;
      }
    }
  }
  




  public void destroy()
  {
    checkDestroyed();
    cleanFiles();
    destroyed = true;
    
    if ((undecodedChunk != null) && (undecodedChunk.refCnt() > 0)) {
      undecodedChunk.release();
      undecodedChunk = null;
    }
    

    for (int i = bodyListHttpDataRank; i < bodyListHttpData.size(); i++) {
      ((InterfaceHttpData)bodyListHttpData.get(i)).release();
    }
  }
  



  public void cleanFiles()
  {
    checkDestroyed();
    
    factory.cleanRequestHttpData(request);
  }
  



  public void removeHttpDataFromClean(InterfaceHttpData data)
  {
    checkDestroyed();
    
    factory.removeHttpDataFromClean(request, data);
  }
}
