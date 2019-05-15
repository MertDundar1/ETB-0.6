package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


















































public class HttpPostRequestDecoder
{
  private static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
  private final HttpDataFactory factory;
  private final HttpRequest request;
  private final Charset charset;
  private boolean bodyToDecode;
  private boolean isLastChunk;
  private final List<InterfaceHttpData> bodyListHttpData = new ArrayList();
  



  private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
  




  private ByteBuf undecodedChunk;
  




  private boolean isMultipart;
  



  private int bodyListHttpDataRank;
  



  private String multipartDataBoundary;
  



  private String multipartMixedBoundary;
  



  private MultiPartStatus currentStatus = MultiPartStatus.NOTSTARTED;
  


  private Map<String, Attribute> currentFieldAttributes;
  


  private FileUpload currentFileUpload;
  


  private Attribute currentAttribute;
  


  private boolean destroyed;
  

  private int discardThreshold = 10485760;
  











  public HttpPostRequestDecoder(HttpRequest request)
    throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException
  {
    this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
  }
  













  public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request)
    throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException
  {
    this(factory, request, HttpConstants.DEFAULT_CHARSET);
  }
  















  public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset)
    throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException
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
    HttpMethod method = request.getMethod();
    if ((method.equals(HttpMethod.POST)) || (method.equals(HttpMethod.PUT)) || (method.equals(HttpMethod.PATCH))) {
      bodyToDecode = true;
    }
    this.charset = charset;
    this.factory = factory;
    

    String contentType = this.request.headers().get("Content-Type");
    if (contentType != null) {
      checkMultipart(contentType);
    } else {
      isMultipart = false;
    }
    if (!bodyToDecode) {
      throw new IncompatibleDataDecoderException("No Body to decode");
    }
    if ((request instanceof HttpContent))
    {

      offer((HttpContent)request);
    } else {
      undecodedChunk = Unpooled.buffer();
      parseBody();
    }
  }
  





























  private static enum MultiPartStatus
  {
    NOTSTARTED,  PREAMBLE,  HEADERDELIMITER,  DISPOSITION,  FIELD,  FILEUPLOAD,  MIXEDPREAMBLE,  MIXEDDELIMITER, 
    MIXEDDISPOSITION,  MIXEDFILEUPLOAD,  MIXEDCLOSEDELIMITER,  CLOSEDELIMITER,  PREEPILOGUE,  EPILOGUE;
    

    private MultiPartStatus() {}
  }
  
  private void checkMultipart(String contentType)
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    String[] headerContentType = splitHeaderContentType(contentType);
    if ((headerContentType[0].toLowerCase().startsWith("multipart/form-data")) && (headerContentType[1].toLowerCase().startsWith("boundary")))
    {
      String[] boundary = StringUtil.split(headerContentType[1], '=');
      if (boundary.length != 2) {
        throw new ErrorDataDecoderException("Needs a boundary value");
      }
      if (boundary[1].charAt(0) == '"') {
        String bound = boundary[1].trim();
        int index = bound.length() - 1;
        if (bound.charAt(index) == '"') {
          boundary[1] = bound.substring(1, index);
        }
      }
      multipartDataBoundary = ("--" + boundary[1]);
      isMultipart = true;
      currentStatus = MultiPartStatus.HEADERDELIMITER;
    } else {
      isMultipart = false;
    }
  }
  
  private void checkDestroyed() {
    if (destroyed) {
      throw new IllegalStateException(HttpPostRequestDecoder.class.getSimpleName() + " was destroyed already");
    }
  }
  




  public boolean isMultipart()
  {
    checkDestroyed();
    return isMultipart;
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
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    checkDestroyed();
    
    if (!isLastChunk) {
      throw new NotEnoughDataDecoderException();
    }
    return bodyListHttpData;
  }
  









  public List<InterfaceHttpData> getBodyHttpDatas(String name)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    checkDestroyed();
    
    if (!isLastChunk) {
      throw new NotEnoughDataDecoderException();
    }
    return (List)bodyMapHttpData.get(name);
  }
  










  public InterfaceHttpData getBodyHttpData(String name)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    checkDestroyed();
    
    if (!isLastChunk) {
      throw new NotEnoughDataDecoderException();
    }
    List<InterfaceHttpData> list = (List)bodyMapHttpData.get(name);
    if (list != null) {
      return (InterfaceHttpData)list.get(0);
    }
    return null;
  }
  







  public HttpPostRequestDecoder offer(HttpContent content)
    throws HttpPostRequestDecoder.ErrorDataDecoderException
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
    throws HttpPostRequestDecoder.EndOfDataDecoderException
  {
    checkDestroyed();
    
    if (currentStatus == MultiPartStatus.EPILOGUE)
    {
      if (bodyListHttpDataRank >= bodyListHttpData.size()) {
        throw new EndOfDataDecoderException();
      }
    }
    return (!bodyListHttpData.isEmpty()) && (bodyListHttpDataRank < bodyListHttpData.size());
  }
  










  public InterfaceHttpData next()
    throws HttpPostRequestDecoder.EndOfDataDecoderException
  {
    checkDestroyed();
    
    if (hasNext()) {
      return (InterfaceHttpData)bodyListHttpData.get(bodyListHttpDataRank++);
    }
    return null;
  }
  





  private void parseBody()
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    if ((currentStatus == MultiPartStatus.PREEPILOGUE) || (currentStatus == MultiPartStatus.EPILOGUE)) {
      if (isLastChunk) {
        currentStatus = MultiPartStatus.EPILOGUE;
      }
      return;
    }
    if (isMultipart) {
      parseBodyMultipart();
    } else {
      parseBodyAttributes();
    }
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
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    int firstpos = undecodedChunk.readerIndex();
    int currentpos = firstpos;
    

    if (currentStatus == MultiPartStatus.NOTSTARTED) {
      currentStatus = MultiPartStatus.DISPOSITION;
    }
    boolean contRead = true;
    try { int ampersandpos;
      while ((undecodedChunk.isReadable()) && (contRead)) {
        char read = (char)undecodedChunk.readUnsignedByte();
        currentpos++;
        int ampersandpos; switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[currentStatus.ordinal()]) {
        case 1: 
          if (read == '=') {
            currentStatus = MultiPartStatus.FIELD;
            int equalpos = currentpos - 1;
            String key = decodeAttribute(undecodedChunk.toString(firstpos, equalpos - firstpos, charset), charset);
            
            currentAttribute = factory.createAttribute(request, key);
            firstpos = currentpos;
          } else if (read == '&') {
            currentStatus = MultiPartStatus.DISPOSITION;
            ampersandpos = currentpos - 1;
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
            currentStatus = MultiPartStatus.DISPOSITION;
            ampersandpos = currentpos - 1;
            setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
            firstpos = currentpos;
            contRead = true;
          } else if (read == '\r') {
            if (undecodedChunk.isReadable()) {
              read = (char)undecodedChunk.readUnsignedByte();
              currentpos++;
              if (read == '\n') {
                currentStatus = MultiPartStatus.PREEPILOGUE;
                int ampersandpos = currentpos - 2;
                setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                firstpos = currentpos;
                contRead = false;
              }
              else {
                throw new ErrorDataDecoderException("Bad end of line");
              }
            } else {
              currentpos--;
            }
          } else if (read == '\n') {
            currentStatus = MultiPartStatus.PREEPILOGUE;
            ampersandpos = currentpos - 1;
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
        ampersandpos = currentpos;
        if (ampersandpos > firstpos) {
          setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
        } else if (!currentAttribute.isCompleted()) {
          setFinalBuffer(Unpooled.EMPTY_BUFFER);
        }
        firstpos = currentpos;
        currentStatus = MultiPartStatus.EPILOGUE;
        undecodedChunk.readerIndex(firstpos);
        return;
      }
      if ((contRead) && (currentAttribute != null))
      {
        if (currentStatus == MultiPartStatus.FIELD) {
          currentAttribute.addContent(undecodedChunk.copy(firstpos, currentpos - firstpos), false);
          
          firstpos = currentpos;
        }
        undecodedChunk.readerIndex(firstpos);
      }
      else {
        undecodedChunk.readerIndex(firstpos);
      }
    }
    catch (ErrorDataDecoderException e) {
      undecodedChunk.readerIndex(firstpos);
      throw e;
    }
    catch (IOException e) {
      undecodedChunk.readerIndex(firstpos);
      throw new ErrorDataDecoderException(e);
    }
  }
  



  private void parseBodyAttributes()
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    HttpPostBodyUtil.SeekAheadOptimize sao;
    

    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
      parseBodyAttributesStandard();
      return;
    }
    int firstpos = undecodedChunk.readerIndex();
    int currentpos = firstpos;
    

    if (currentStatus == MultiPartStatus.NOTSTARTED) {
      currentStatus = MultiPartStatus.DISPOSITION;
    }
    boolean contRead = true;
    try { int ampersandpos;
      while (pos < limit) {
        char read = (char)(bytes[(pos++)] & 0xFF);
        currentpos++;
        int ampersandpos; switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[currentStatus.ordinal()]) {
        case 1: 
          if (read == '=') {
            currentStatus = MultiPartStatus.FIELD;
            int equalpos = currentpos - 1;
            String key = decodeAttribute(undecodedChunk.toString(firstpos, equalpos - firstpos, charset), charset);
            
            currentAttribute = factory.createAttribute(request, key);
            firstpos = currentpos;
          } else if (read == '&') {
            currentStatus = MultiPartStatus.DISPOSITION;
            ampersandpos = currentpos - 1;
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
            currentStatus = MultiPartStatus.DISPOSITION;
            ampersandpos = currentpos - 1;
            setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
            firstpos = currentpos;
            contRead = true;
          } else if (read == '\r') {
            if (pos < limit) {
              read = (char)(bytes[(pos++)] & 0xFF);
              currentpos++;
              if (read == '\n') {
                currentStatus = MultiPartStatus.PREEPILOGUE;
                int ampersandpos = currentpos - 2;
                sao.setReadPosition(0);
                setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                firstpos = currentpos;
                contRead = false;
                
                break label512;
              }
              sao.setReadPosition(0);
              throw new ErrorDataDecoderException("Bad end of line");
            }
            
            if (limit > 0) {
              currentpos--;
            }
          }
          else if (read == '\n') {
            currentStatus = MultiPartStatus.PREEPILOGUE;
            ampersandpos = currentpos - 1;
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
        ampersandpos = currentpos;
        if (ampersandpos > firstpos) {
          setFinalBuffer(undecodedChunk.copy(firstpos, ampersandpos - firstpos));
        } else if (!currentAttribute.isCompleted()) {
          setFinalBuffer(Unpooled.EMPTY_BUFFER);
        }
        firstpos = currentpos;
        currentStatus = MultiPartStatus.EPILOGUE;
        undecodedChunk.readerIndex(firstpos);
        return;
      }
      if ((contRead) && (currentAttribute != null))
      {
        if (currentStatus == MultiPartStatus.FIELD) {
          currentAttribute.addContent(undecodedChunk.copy(firstpos, currentpos - firstpos), false);
          
          firstpos = currentpos;
        }
        undecodedChunk.readerIndex(firstpos);
      }
      else {
        undecodedChunk.readerIndex(firstpos);
      }
    }
    catch (ErrorDataDecoderException e) {
      undecodedChunk.readerIndex(firstpos);
      throw e;
    }
    catch (IOException e) {
      undecodedChunk.readerIndex(firstpos);
      throw new ErrorDataDecoderException(e);
    }
  }
  
  private void setFinalBuffer(ByteBuf buffer) throws HttpPostRequestDecoder.ErrorDataDecoderException, IOException {
    currentAttribute.addContent(buffer, true);
    String value = decodeAttribute(currentAttribute.getByteBuf().toString(charset), charset);
    currentAttribute.setValue(value);
    addHttpData(currentAttribute);
    currentAttribute = null;
  }
  


  private static String decodeAttribute(String s, Charset charset)
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    try
    {
      return QueryStringDecoder.decodeComponent(s, charset);
    } catch (IllegalArgumentException e) {
      throw new ErrorDataDecoderException("Bad string: '" + s + '\'', e);
    }
  }
  





  private void parseBodyMultipart()
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    if ((undecodedChunk == null) || (undecodedChunk.readableBytes() == 0))
    {
      return;
    }
    InterfaceHttpData data = decodeMultipart(currentStatus);
    while (data != null) {
      addHttpData(data);
      if ((currentStatus == MultiPartStatus.PREEPILOGUE) || (currentStatus == MultiPartStatus.EPILOGUE)) {
        break;
      }
      data = decodeMultipart(currentStatus);
    }
  }
  














  private InterfaceHttpData decodeMultipart(MultiPartStatus state)
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[state.ordinal()]) {
    case 3: 
      throw new ErrorDataDecoderException("Should not be called with the current getStatus");
    
    case 4: 
      throw new ErrorDataDecoderException("Should not be called with the current getStatus");
    
    case 5: 
      return findMultipartDelimiter(multipartDataBoundary, MultiPartStatus.DISPOSITION, MultiPartStatus.PREEPILOGUE);
    










    case 1: 
      return findMultipartDisposition();
    

    case 2: 
      Charset localCharset = null;
      Attribute charsetAttribute = (Attribute)currentFieldAttributes.get("charset");
      if (charsetAttribute != null) {
        try {
          localCharset = Charset.forName(charsetAttribute.getValue());
        } catch (IOException e) {
          throw new ErrorDataDecoderException(e);
        }
      }
      Attribute nameAttribute = (Attribute)currentFieldAttributes.get("name");
      if (currentAttribute == null) {
        try {
          currentAttribute = factory.createAttribute(request, cleanString(nameAttribute.getValue()));
        }
        catch (NullPointerException e) {
          throw new ErrorDataDecoderException(e);
        } catch (IllegalArgumentException e) {
          throw new ErrorDataDecoderException(e);
        } catch (IOException e) {
          throw new ErrorDataDecoderException(e);
        }
        if (localCharset != null) {
          currentAttribute.setCharset(localCharset);
        }
      }
      try
      {
        loadFieldMultipart(multipartDataBoundary);
      } catch (NotEnoughDataDecoderException e) {
        return null;
      }
      Attribute finalAttribute = currentAttribute;
      currentAttribute = null;
      currentFieldAttributes = null;
      
      currentStatus = MultiPartStatus.HEADERDELIMITER;
      return finalAttribute;
    

    case 6: 
      return getFileUpload(multipartDataBoundary);
    


    case 7: 
      return findMultipartDelimiter(multipartMixedBoundary, MultiPartStatus.MIXEDDISPOSITION, MultiPartStatus.HEADERDELIMITER);
    

    case 8: 
      return findMultipartDisposition();
    

    case 9: 
      return getFileUpload(multipartMixedBoundary);
    
    case 10: 
      return null;
    case 11: 
      return null;
    }
    throw new ErrorDataDecoderException("Shouldn't reach here.");
  }
  


  void skipControlCharacters()
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    HttpPostBodyUtil.SeekAheadOptimize sao;
    
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e) {
      try {
        skipControlCharactersStandard();
      } catch (IndexOutOfBoundsException e1) {
        throw new NotEnoughDataDecoderException(e1);
      }
      return;
    }
    
    while (pos < limit) {
      char c = (char)(bytes[(pos++)] & 0xFF);
      if ((!Character.isISOControl(c)) && (!Character.isWhitespace(c))) {
        sao.setReadPosition(1);
        return;
      }
    }
    throw new NotEnoughDataDecoderException("Access out of bounds");
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
  












  private InterfaceHttpData findMultipartDelimiter(String delimiter, MultiPartStatus dispositionStatus, MultiPartStatus closeDelimiterStatus)
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    int readerIndex = undecodedChunk.readerIndex();
    try {
      skipControlCharacters();
    } catch (NotEnoughDataDecoderException e1) {
      undecodedChunk.readerIndex(readerIndex);
      return null;
    }
    skipOneLine();
    String newline;
    try {
      newline = readDelimiter(delimiter);
    } catch (NotEnoughDataDecoderException e) {
      undecodedChunk.readerIndex(readerIndex);
      return null;
    }
    if (newline.equals(delimiter)) {
      currentStatus = dispositionStatus;
      return decodeMultipart(dispositionStatus);
    }
    if (newline.equals(delimiter + "--"))
    {
      currentStatus = closeDelimiterStatus;
      if (currentStatus == MultiPartStatus.HEADERDELIMITER)
      {

        currentFieldAttributes = null;
        return decodeMultipart(MultiPartStatus.HEADERDELIMITER);
      }
      return null;
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new ErrorDataDecoderException("No Multipart delimiter found");
  }
  




  private InterfaceHttpData findMultipartDisposition()
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    int readerIndex = undecodedChunk.readerIndex();
    if (currentStatus == MultiPartStatus.DISPOSITION) {
      currentFieldAttributes = new TreeMap(CaseIgnoringComparator.INSTANCE);
    }
    
    while (!skipOneLine()) {
      String newline;
      try {
        skipControlCharacters();
        newline = readLine();
      } catch (NotEnoughDataDecoderException e) {
        undecodedChunk.readerIndex(readerIndex);
        return null;
      }
      String[] contents = splitMultipartHeader(newline);
      if (contents[0].equalsIgnoreCase("Content-Disposition")) { boolean checkSecondArg;
        boolean checkSecondArg;
        if (currentStatus == MultiPartStatus.DISPOSITION) {
          checkSecondArg = contents[1].equalsIgnoreCase("form-data");
        } else {
          checkSecondArg = (contents[1].equalsIgnoreCase("attachment")) || (contents[1].equalsIgnoreCase("file"));
        }
        
        if (checkSecondArg)
        {
          for (int i = 2; i < contents.length; i++) {
            String[] values = StringUtil.split(contents[i], '=');
            Attribute attribute;
            try {
              String name = cleanString(values[0]);
              String value = values[1];
              

              if ("filename".equals(name))
              {
                value = value.substring(1, value.length() - 1);
              }
              else {
                value = cleanString(value);
              }
              attribute = factory.createAttribute(request, name, value);
            } catch (NullPointerException e) {
              throw new ErrorDataDecoderException(e);
            } catch (IllegalArgumentException e) {
              throw new ErrorDataDecoderException(e);
            }
            currentFieldAttributes.put(attribute.getName(), attribute);
          }
        }
      } else if (contents[0].equalsIgnoreCase("Content-Transfer-Encoding")) {
        Attribute attribute;
        try {
          attribute = factory.createAttribute(request, "Content-Transfer-Encoding", cleanString(contents[1]));
        }
        catch (NullPointerException e) {
          throw new ErrorDataDecoderException(e);
        } catch (IllegalArgumentException e) {
          throw new ErrorDataDecoderException(e);
        }
        currentFieldAttributes.put("Content-Transfer-Encoding", attribute);
      } else if (contents[0].equalsIgnoreCase("Content-Length")) {
        Attribute attribute;
        try {
          attribute = factory.createAttribute(request, "Content-Length", cleanString(contents[1]));
        }
        catch (NullPointerException e) {
          throw new ErrorDataDecoderException(e);
        } catch (IllegalArgumentException e) {
          throw new ErrorDataDecoderException(e);
        }
        currentFieldAttributes.put("Content-Length", attribute);
      } else if (contents[0].equalsIgnoreCase("Content-Type"))
      {
        if (contents[1].equalsIgnoreCase("multipart/mixed")) {
          if (currentStatus == MultiPartStatus.DISPOSITION) {
            String[] values = StringUtil.split(contents[2], '=');
            multipartMixedBoundary = ("--" + values[1]);
            currentStatus = MultiPartStatus.MIXEDDELIMITER;
            return decodeMultipart(MultiPartStatus.MIXEDDELIMITER);
          }
          throw new ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
        }
        
        for (int i = 1; i < contents.length; i++) {
          if (contents[i].toLowerCase().startsWith("charset")) {
            String[] values = StringUtil.split(contents[i], '=');
            Attribute attribute;
            try {
              attribute = factory.createAttribute(request, "charset", cleanString(values[1]));
            }
            catch (NullPointerException e) {
              throw new ErrorDataDecoderException(e);
            } catch (IllegalArgumentException e) {
              throw new ErrorDataDecoderException(e);
            }
            currentFieldAttributes.put("charset", attribute);
          } else {
            Attribute attribute;
            try {
              attribute = factory.createAttribute(request, cleanString(contents[0]), contents[i]);
            }
            catch (NullPointerException e) {
              throw new ErrorDataDecoderException(e);
            } catch (IllegalArgumentException e) {
              throw new ErrorDataDecoderException(e);
            }
            currentFieldAttributes.put(attribute.getName(), attribute);
          }
        }
      }
      else {
        throw new ErrorDataDecoderException("Unknown Params: " + newline);
      }
    }
    
    Attribute filenameAttribute = (Attribute)currentFieldAttributes.get("filename");
    if (currentStatus == MultiPartStatus.DISPOSITION) {
      if (filenameAttribute != null)
      {
        currentStatus = MultiPartStatus.FILEUPLOAD;
        
        return decodeMultipart(MultiPartStatus.FILEUPLOAD);
      }
      
      currentStatus = MultiPartStatus.FIELD;
      
      return decodeMultipart(MultiPartStatus.FIELD);
    }
    
    if (filenameAttribute != null)
    {
      currentStatus = MultiPartStatus.MIXEDFILEUPLOAD;
      
      return decodeMultipart(MultiPartStatus.MIXEDFILEUPLOAD);
    }
    
    throw new ErrorDataDecoderException("Filename not found");
  }
  










  protected InterfaceHttpData getFileUpload(String delimiter)
    throws HttpPostRequestDecoder.ErrorDataDecoderException
  {
    Attribute encoding = (Attribute)currentFieldAttributes.get("Content-Transfer-Encoding");
    Charset localCharset = charset;
    
    HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
    if (encoding != null) {
      String code;
      try {
        code = encoding.getValue().toLowerCase();
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
      if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
        localCharset = HttpPostBodyUtil.US_ASCII;
      } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
        localCharset = HttpPostBodyUtil.ISO_8859_1;
        mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
      } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value()))
      {
        mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
      } else {
        throw new ErrorDataDecoderException("TransferEncoding Unknown: " + code);
      }
    }
    Attribute charsetAttribute = (Attribute)currentFieldAttributes.get("charset");
    if (charsetAttribute != null) {
      try {
        localCharset = Charset.forName(charsetAttribute.getValue());
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
    }
    if (currentFileUpload == null) {
      Attribute filenameAttribute = (Attribute)currentFieldAttributes.get("filename");
      Attribute nameAttribute = (Attribute)currentFieldAttributes.get("name");
      Attribute contentTypeAttribute = (Attribute)currentFieldAttributes.get("Content-Type");
      if (contentTypeAttribute == null) {
        throw new ErrorDataDecoderException("Content-Type is absent but required");
      }
      Attribute lengthAttribute = (Attribute)currentFieldAttributes.get("Content-Length");
      long size;
      try {
        size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      } catch (NumberFormatException e) {
        size = 0L;
      }
      try {
        currentFileUpload = factory.createFileUpload(request, cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentTypeAttribute.getValue(), mechanism.value(), localCharset, size);

      }
      catch (NullPointerException e)
      {
        throw new ErrorDataDecoderException(e);
      } catch (IllegalArgumentException e) {
        throw new ErrorDataDecoderException(e);
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
    }
    try
    {
      readFileUploadByteMultipart(delimiter);

    }
    catch (NotEnoughDataDecoderException e)
    {
      return null;
    }
    if (currentFileUpload.isCompleted())
    {
      if (currentStatus == MultiPartStatus.FILEUPLOAD) {
        currentStatus = MultiPartStatus.HEADERDELIMITER;
        currentFieldAttributes = null;
      } else {
        currentStatus = MultiPartStatus.MIXEDDELIMITER;
        cleanMixedAttributes();
      }
      FileUpload fileUpload = currentFileUpload;
      currentFileUpload = null;
      return fileUpload;
    }
    


    return null;
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
    
    factory.cleanRequestHttpDatas(request);
  }
  


  public void removeHttpDataFromClean(InterfaceHttpData data)
  {
    checkDestroyed();
    
    factory.removeHttpDataFromClean(request, data);
  }
  



  private void cleanMixedAttributes()
  {
    currentFieldAttributes.remove("charset");
    currentFieldAttributes.remove("Content-Length");
    currentFieldAttributes.remove("Content-Transfer-Encoding");
    currentFieldAttributes.remove("Content-Type");
    currentFieldAttributes.remove("filename");
  }
  






  private String readLineStandard()
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    int readerIndex = undecodedChunk.readerIndex();
    try {
      ByteBuf line = Unpooled.buffer(64);
      
      while (undecodedChunk.isReadable()) {
        byte nextByte = undecodedChunk.readByte();
        if (nextByte == 13)
        {
          nextByte = undecodedChunk.getByte(undecodedChunk.readerIndex());
          if (nextByte == 10)
          {
            undecodedChunk.skipBytes(1);
            return line.toString(charset);
          }
          
          line.writeByte(13);
        } else {
          if (nextByte == 10) {
            return line.toString(charset);
          }
          line.writeByte(nextByte);
        }
      }
    } catch (IndexOutOfBoundsException e) {
      undecodedChunk.readerIndex(readerIndex);
      throw new NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new NotEnoughDataDecoderException();
  }
  



  private String readLine()
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    HttpPostBodyUtil.SeekAheadOptimize sao;
    

    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
      return readLineStandard();
    }
    int readerIndex = undecodedChunk.readerIndex();
    try {
      ByteBuf line = Unpooled.buffer(64);
      
      while (pos < limit) {
        byte nextByte = bytes[(pos++)];
        if (nextByte == 13) {
          if (pos < limit) {
            nextByte = bytes[(pos++)];
            if (nextByte == 10) {
              sao.setReadPosition(0);
              return line.toString(charset);
            }
            
            pos -= 1;
            line.writeByte(13);
          }
          else {
            line.writeByte(nextByte);
          }
        } else { if (nextByte == 10) {
            sao.setReadPosition(0);
            return line.toString(charset);
          }
          line.writeByte(nextByte);
        }
      }
    } catch (IndexOutOfBoundsException e) {
      undecodedChunk.readerIndex(readerIndex);
      throw new NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new NotEnoughDataDecoderException();
  }
  













  private String readDelimiterStandard(String delimiter)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    int readerIndex = undecodedChunk.readerIndex();
    try {
      StringBuilder sb = new StringBuilder(64);
      int delimiterPos = 0;
      int len = delimiter.length();
      while ((undecodedChunk.isReadable()) && (delimiterPos < len)) {
        byte nextByte = undecodedChunk.readByte();
        if (nextByte == delimiter.charAt(delimiterPos)) {
          delimiterPos++;
          sb.append((char)nextByte);
        }
        else {
          undecodedChunk.readerIndex(readerIndex);
          throw new NotEnoughDataDecoderException();
        }
      }
      
      if (undecodedChunk.isReadable()) {
        byte nextByte = undecodedChunk.readByte();
        
        if (nextByte == 13) {
          nextByte = undecodedChunk.readByte();
          if (nextByte == 10) {
            return sb.toString();
          }
          

          undecodedChunk.readerIndex(readerIndex);
          throw new NotEnoughDataDecoderException();
        }
        if (nextByte == 10)
          return sb.toString();
        if (nextByte == 45) {
          sb.append('-');
          
          nextByte = undecodedChunk.readByte();
          if (nextByte == 45) {
            sb.append('-');
            
            if (undecodedChunk.isReadable()) {
              nextByte = undecodedChunk.readByte();
              if (nextByte == 13) {
                nextByte = undecodedChunk.readByte();
                if (nextByte == 10) {
                  return sb.toString();
                }
                

                undecodedChunk.readerIndex(readerIndex);
                throw new NotEnoughDataDecoderException();
              }
              if (nextByte == 10) {
                return sb.toString();
              }
              


              undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
              return sb.toString();
            }
            




            return sb.toString();
          }
        }
      }
    }
    catch (IndexOutOfBoundsException e)
    {
      undecodedChunk.readerIndex(readerIndex);
      throw new NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new NotEnoughDataDecoderException();
  }
  






  private String readDelimiter(String delimiter)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException
  {
    HttpPostBodyUtil.SeekAheadOptimize sao;
    




    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
      return readDelimiterStandard(delimiter);
    }
    int readerIndex = undecodedChunk.readerIndex();
    int delimiterPos = 0;
    int len = delimiter.length();
    try {
      StringBuilder sb = new StringBuilder(64);
      
      while ((pos < limit) && (delimiterPos < len)) {
        byte nextByte = bytes[(pos++)];
        if (nextByte == delimiter.charAt(delimiterPos)) {
          delimiterPos++;
          sb.append((char)nextByte);
        }
        else {
          undecodedChunk.readerIndex(readerIndex);
          throw new NotEnoughDataDecoderException();
        }
      }
      
      if (pos < limit) {
        byte nextByte = bytes[(pos++)];
        if (nextByte == 13)
        {
          if (pos < limit) {
            nextByte = bytes[(pos++)];
            if (nextByte == 10) {
              sao.setReadPosition(0);
              return sb.toString();
            }
            

            undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException();
          }
          


          undecodedChunk.readerIndex(readerIndex);
          throw new NotEnoughDataDecoderException();
        }
        if (nextByte == 10)
        {

          sao.setReadPosition(0);
          return sb.toString(); }
        if (nextByte == 45) {
          sb.append('-');
          
          if (pos < limit) {
            nextByte = bytes[(pos++)];
            if (nextByte == 45) {
              sb.append('-');
              
              if (pos < limit) {
                nextByte = bytes[(pos++)];
                if (nextByte == 13) {
                  if (pos < limit) {
                    nextByte = bytes[(pos++)];
                    if (nextByte == 10) {
                      sao.setReadPosition(0);
                      return sb.toString();
                    }
                    

                    undecodedChunk.readerIndex(readerIndex);
                    throw new NotEnoughDataDecoderException();
                  }
                  


                  undecodedChunk.readerIndex(readerIndex);
                  throw new NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                  sao.setReadPosition(0);
                  return sb.toString();
                }
                



                sao.setReadPosition(1);
                return sb.toString();
              }
              




              sao.setReadPosition(0);
              return sb.toString();
            }
            
          }
        }
      }
    }
    catch (IndexOutOfBoundsException e)
    {
      undecodedChunk.readerIndex(readerIndex);
      throw new NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new NotEnoughDataDecoderException();
  }
  









  private void readFileUploadByteMultipartStandard(String delimiter)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException
  {
    int readerIndex = undecodedChunk.readerIndex();
    
    boolean newLine = true;
    int index = 0;
    int lastPosition = undecodedChunk.readerIndex();
    boolean found = false;
    while (undecodedChunk.isReadable()) {
      byte nextByte = undecodedChunk.readByte();
      if (newLine)
      {
        if (nextByte == delimiter.codePointAt(index)) {
          index++;
          if (delimiter.length() == index) {
            found = true;
            break;
          }
        }
        else {
          newLine = false;
          index = 0;
          
          if (nextByte == 13) {
            if (undecodedChunk.isReadable()) {
              nextByte = undecodedChunk.readByte();
              if (nextByte == 10) {
                newLine = true;
                index = 0;
                lastPosition = undecodedChunk.readerIndex() - 2;
              }
              else {
                lastPosition = undecodedChunk.readerIndex() - 1;
                

                undecodedChunk.readerIndex(lastPosition);
              }
            }
          } else if (nextByte == 10) {
            newLine = true;
            index = 0;
            lastPosition = undecodedChunk.readerIndex() - 1;
          }
          else {
            lastPosition = undecodedChunk.readerIndex();
          }
          
        }
      }
      else if (nextByte == 13) {
        if (undecodedChunk.isReadable()) {
          nextByte = undecodedChunk.readByte();
          if (nextByte == 10) {
            newLine = true;
            index = 0;
            lastPosition = undecodedChunk.readerIndex() - 2;
          }
          else {
            lastPosition = undecodedChunk.readerIndex() - 1;
            

            undecodedChunk.readerIndex(lastPosition);
          }
        }
      } else if (nextByte == 10) {
        newLine = true;
        index = 0;
        lastPosition = undecodedChunk.readerIndex() - 1;
      }
      else {
        lastPosition = undecodedChunk.readerIndex();
      }
    }
    
    ByteBuf buffer = undecodedChunk.copy(readerIndex, lastPosition - readerIndex);
    if (found) {
      try
      {
        currentFileUpload.addContent(buffer, true);
        
        undecodedChunk.readerIndex(lastPosition);
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
      
    } else {
      try
      {
        currentFileUpload.addContent(buffer, false);
        
        undecodedChunk.readerIndex(lastPosition);
        throw new NotEnoughDataDecoderException();
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
    }
  }
  




  private void readFileUploadByteMultipart(String delimiter)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException
  {
    HttpPostBodyUtil.SeekAheadOptimize sao;
    



    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
      readFileUploadByteMultipartStandard(delimiter);
      return;
    }
    int readerIndex = undecodedChunk.readerIndex();
    
    boolean newLine = true;
    int index = 0;
    int lastrealpos = pos;
    
    boolean found = false;
    
    while (pos < limit) {
      byte nextByte = bytes[(pos++)];
      if (newLine)
      {
        if (nextByte == delimiter.codePointAt(index)) {
          index++;
          if (delimiter.length() == index) {
            found = true;
            break;
          }
        }
        else {
          newLine = false;
          index = 0;
          
          if (nextByte == 13) {
            if (pos < limit) {
              nextByte = bytes[(pos++)];
              if (nextByte == 10) {
                newLine = true;
                index = 0;
                lastrealpos = pos - 2;
              }
              else {
                pos -= 1;
                

                lastrealpos = pos;
              }
            }
          } else if (nextByte == 10) {
            newLine = true;
            index = 0;
            lastrealpos = pos - 1;
          }
          else {
            lastrealpos = pos;
          }
          
        }
      }
      else if (nextByte == 13) {
        if (pos < limit) {
          nextByte = bytes[(pos++)];
          if (nextByte == 10) {
            newLine = true;
            index = 0;
            lastrealpos = pos - 2;
          }
          else {
            pos -= 1;
            

            lastrealpos = pos;
          }
        }
      } else if (nextByte == 10) {
        newLine = true;
        index = 0;
        lastrealpos = pos - 1;
      }
      else {
        lastrealpos = pos;
      }
    }
    
    int lastPosition = sao.getReadPosition(lastrealpos);
    ByteBuf buffer = undecodedChunk.copy(readerIndex, lastPosition - readerIndex);
    if (found) {
      try
      {
        currentFileUpload.addContent(buffer, true);
        
        undecodedChunk.readerIndex(lastPosition);
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
      
    } else {
      try
      {
        currentFileUpload.addContent(buffer, false);
        
        undecodedChunk.readerIndex(lastPosition);
        throw new NotEnoughDataDecoderException();
      } catch (IOException e) {
        throw new ErrorDataDecoderException(e);
      }
    }
  }
  






  private void loadFieldMultipartStandard(String delimiter)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException
  {
    int readerIndex = undecodedChunk.readerIndex();
    try
    {
      boolean newLine = true;
      int index = 0;
      int lastPosition = undecodedChunk.readerIndex();
      boolean found = false;
      while (undecodedChunk.isReadable()) {
        byte nextByte = undecodedChunk.readByte();
        if (newLine)
        {
          if (nextByte == delimiter.codePointAt(index)) {
            index++;
            if (delimiter.length() == index) {
              found = true;
              break;
            }
          }
          else {
            newLine = false;
            index = 0;
            
            if (nextByte == 13) {
              if (undecodedChunk.isReadable()) {
                nextByte = undecodedChunk.readByte();
                if (nextByte == 10) {
                  newLine = true;
                  index = 0;
                  lastPosition = undecodedChunk.readerIndex() - 2;
                }
                else {
                  lastPosition = undecodedChunk.readerIndex() - 1;
                  undecodedChunk.readerIndex(lastPosition);
                }
              } else {
                lastPosition = undecodedChunk.readerIndex() - 1;
              }
            } else if (nextByte == 10) {
              newLine = true;
              index = 0;
              lastPosition = undecodedChunk.readerIndex() - 1;
            } else {
              lastPosition = undecodedChunk.readerIndex();
            }
            
          }
        }
        else if (nextByte == 13) {
          if (undecodedChunk.isReadable()) {
            nextByte = undecodedChunk.readByte();
            if (nextByte == 10) {
              newLine = true;
              index = 0;
              lastPosition = undecodedChunk.readerIndex() - 2;
            }
            else {
              lastPosition = undecodedChunk.readerIndex() - 1;
              undecodedChunk.readerIndex(lastPosition);
            }
          } else {
            lastPosition = undecodedChunk.readerIndex() - 1;
          }
        } else if (nextByte == 10) {
          newLine = true;
          index = 0;
          lastPosition = undecodedChunk.readerIndex() - 1;
        } else {
          lastPosition = undecodedChunk.readerIndex();
        }
      }
      
      if (found)
      {

        try
        {

          currentAttribute.addContent(undecodedChunk.copy(readerIndex, lastPosition - readerIndex), true);
        }
        catch (IOException e) {
          throw new ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
      } else {
        try {
          currentAttribute.addContent(undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
        }
        catch (IOException e) {
          throw new ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
        throw new NotEnoughDataDecoderException();
      }
    } catch (IndexOutOfBoundsException e) {
      undecodedChunk.readerIndex(readerIndex);
      throw new NotEnoughDataDecoderException(e);
    }
  }
  


  private void loadFieldMultipart(String delimiter)
    throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException
  {
    HttpPostBodyUtil.SeekAheadOptimize sao;
    

    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
      loadFieldMultipartStandard(delimiter);
      return;
    }
    int readerIndex = undecodedChunk.readerIndex();
    try
    {
      boolean newLine = true;
      int index = 0;
      
      int lastrealpos = pos;
      boolean found = false;
      
      while (pos < limit) {
        byte nextByte = bytes[(pos++)];
        if (newLine)
        {
          if (nextByte == delimiter.codePointAt(index)) {
            index++;
            if (delimiter.length() == index) {
              found = true;
              break;
            }
          }
          else {
            newLine = false;
            index = 0;
            
            if (nextByte == 13) {
              if (pos < limit) {
                nextByte = bytes[(pos++)];
                if (nextByte == 10) {
                  newLine = true;
                  index = 0;
                  lastrealpos = pos - 2;
                }
                else {
                  pos -= 1;
                  lastrealpos = pos;
                }
              }
            } else if (nextByte == 10) {
              newLine = true;
              index = 0;
              lastrealpos = pos - 1;
            } else {
              lastrealpos = pos;
            }
            
          }
        }
        else if (nextByte == 13) {
          if (pos < limit) {
            nextByte = bytes[(pos++)];
            if (nextByte == 10) {
              newLine = true;
              index = 0;
              lastrealpos = pos - 2;
            }
            else {
              pos -= 1;
              lastrealpos = pos;
            }
          }
        } else if (nextByte == 10) {
          newLine = true;
          index = 0;
          lastrealpos = pos - 1;
        } else {
          lastrealpos = pos;
        }
      }
      
      int lastPosition = sao.getReadPosition(lastrealpos);
      if (found)
      {

        try
        {

          currentAttribute.addContent(undecodedChunk.copy(readerIndex, lastPosition - readerIndex), true);
        }
        catch (IOException e) {
          throw new ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
      } else {
        try {
          currentAttribute.addContent(undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
        }
        catch (IOException e) {
          throw new ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
        throw new NotEnoughDataDecoderException();
      }
    } catch (IndexOutOfBoundsException e) {
      undecodedChunk.readerIndex(readerIndex);
      throw new NotEnoughDataDecoderException(e);
    }
  }
  




  private static String cleanString(String field)
  {
    StringBuilder sb = new StringBuilder(field.length());
    for (int i = 0; i < field.length(); i++) {
      char nextChar = field.charAt(i);
      if (nextChar == ':') {
        sb.append(32);
      } else if (nextChar == ',') {
        sb.append(32);
      } else if (nextChar == '=') {
        sb.append(32);
      } else if (nextChar == ';') {
        sb.append(32);
      } else if (nextChar == '\t') {
        sb.append(32);
      } else if (nextChar != '"')
      {

        sb.append(nextChar);
      }
    }
    return sb.toString().trim();
  }
  




  private boolean skipOneLine()
  {
    if (!undecodedChunk.isReadable()) {
      return false;
    }
    byte nextByte = undecodedChunk.readByte();
    if (nextByte == 13) {
      if (!undecodedChunk.isReadable()) {
        undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
        return false;
      }
      nextByte = undecodedChunk.readByte();
      if (nextByte == 10) {
        return true;
      }
      undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 2);
      return false;
    }
    if (nextByte == 10) {
      return true;
    }
    undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
    return false;
  }
  








  private static String[] splitHeaderContentType(String sb)
  {
    int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
    int aEnd = sb.indexOf(';');
    if (aEnd == -1) {
      return new String[] { sb, "" };
    }
    if (sb.charAt(aEnd - 1) == ' ') {
      aEnd--;
    }
    int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
    int bEnd = HttpPostBodyUtil.findEndOfString(sb);
    return new String[] { sb.substring(aStart, aEnd), sb.substring(bStart, bEnd) };
  }
  





  private static String[] splitMultipartHeader(String sb)
  {
    ArrayList<String> headers = new ArrayList(1);
    




    int nameStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
    for (int nameEnd = nameStart; nameEnd < sb.length(); nameEnd++) {
      char ch = sb.charAt(nameEnd);
      if ((ch == ':') || (Character.isWhitespace(ch))) {
        break;
      }
    }
    for (int colonEnd = nameEnd; colonEnd < sb.length(); colonEnd++) {
      if (sb.charAt(colonEnd) == ':') {
        colonEnd++;
        break;
      }
    }
    int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
    int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
    headers.add(sb.substring(nameStart, nameEnd));
    String svalue = sb.substring(valueStart, valueEnd);
    String[] values;
    String[] values; if (svalue.indexOf(';') >= 0) {
      values = StringUtil.split(svalue, ';');
    } else {
      values = StringUtil.split(svalue, ',');
    }
    for (String value : values) {
      headers.add(value.trim());
    }
    String[] array = new String[headers.size()];
    for (int i = 0; i < headers.size(); i++) {
      array[i] = ((String)headers.get(i));
    }
    return array;
  }
  

  public static class NotEnoughDataDecoderException
    extends DecoderException
  {
    private static final long serialVersionUID = -7846841864603865638L;
    

    public NotEnoughDataDecoderException() {}
    
    public NotEnoughDataDecoderException(String msg)
    {
      super();
    }
    
    public NotEnoughDataDecoderException(Throwable cause) {
      super();
    }
    
    public NotEnoughDataDecoderException(String msg, Throwable cause) {
      super(cause);
    }
  }
  
  public static class EndOfDataDecoderException
    extends DecoderException
  {
    private static final long serialVersionUID = 1336267941020800769L;
    
    public EndOfDataDecoderException() {}
  }
  
  public static class ErrorDataDecoderException
    extends DecoderException
  {
    private static final long serialVersionUID = 5020247425493164465L;
    
    public ErrorDataDecoderException() {}
    
    public ErrorDataDecoderException(String msg)
    {
      super();
    }
    
    public ErrorDataDecoderException(Throwable cause) {
      super();
    }
    
    public ErrorDataDecoderException(String msg, Throwable cause) {
      super(cause);
    }
  }
  

  public static class IncompatibleDataDecoderException
    extends DecoderException
  {
    private static final long serialVersionUID = -953268047926250267L;
    
    public IncompatibleDataDecoderException() {}
    
    public IncompatibleDataDecoderException(String msg)
    {
      super();
    }
    
    public IncompatibleDataDecoderException(Throwable cause) {
      super();
    }
    
    public IncompatibleDataDecoderException(String msg, Throwable cause) {
      super(cause);
    }
  }
}
