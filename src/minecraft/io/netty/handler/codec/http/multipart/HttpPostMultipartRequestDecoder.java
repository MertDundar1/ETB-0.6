package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;















































public class HttpPostMultipartRequestDecoder
  implements InterfaceHttpPostRequestDecoder
{
  private final HttpDataFactory factory;
  private final HttpRequest request;
  private Charset charset;
  private boolean isLastChunk;
  private final List<InterfaceHttpData> bodyListHttpData = new ArrayList();
  



  private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
  




  private ByteBuf undecodedChunk;
  




  private int bodyListHttpDataRank;
  



  private String multipartDataBoundary;
  



  private String multipartMixedBoundary;
  



  private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
  


  private Map<CharSequence, Attribute> currentFieldAttributes;
  


  private FileUpload currentFileUpload;
  


  private Attribute currentAttribute;
  


  private boolean destroyed;
  

  private int discardThreshold = 10485760;
  









  public HttpPostMultipartRequestDecoder(HttpRequest request)
  {
    this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
  }
  











  public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request)
  {
    this(factory, request, HttpConstants.DEFAULT_CHARSET);
  }
  













  public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset)
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
    

    setMultipart(this.request.headers().get(HttpHeaderNames.CONTENT_TYPE));
    if ((request instanceof HttpContent))
    {

      offer((HttpContent)request);
    } else {
      undecodedChunk = Unpooled.buffer();
      parseBody();
    }
  }
  


  private void setMultipart(String contentType)
  {
    String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentType);
    if (dataBoundary != null) {
      multipartDataBoundary = dataBoundary[0];
      if ((dataBoundary.length > 1) && (dataBoundary[1] != null)) {
        charset = Charset.forName(dataBoundary[1]);
      }
    } else {
      multipartDataBoundary = null;
    }
    currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
  }
  
  private void checkDestroyed() {
    if (destroyed) {
      throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
    }
  }
  






  public boolean isMultipart()
  {
    checkDestroyed();
    return true;
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
  









  public HttpPostMultipartRequestDecoder offer(HttpContent content)
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
    if (currentFileUpload != null) {
      return currentFileUpload;
    }
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
    parseBodyMultipart();
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
  






  private void parseBodyMultipart()
  {
    if ((undecodedChunk == null) || (undecodedChunk.readableBytes() == 0))
    {
      return;
    }
    InterfaceHttpData data = decodeMultipart(currentStatus);
    while (data != null) {
      addHttpData(data);
      if ((currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE) || (currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE)) {
        break;
      }
      data = decodeMultipart(currentStatus);
    }
  }
  















  private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus state)
  {
    switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[state.ordinal()]) {
    case 1: 
      throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
    
    case 2: 
      throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
    
    case 3: 
      return findMultipartDelimiter(multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
    










    case 4: 
      return findMultipartDisposition();
    

    case 5: 
      Charset localCharset = null;
      Attribute charsetAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderValues.CHARSET);
      if (charsetAttribute != null) {
        try {
          localCharset = Charset.forName(charsetAttribute.getValue());
        } catch (IOException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        } catch (UnsupportedCharsetException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
      }
      Attribute nameAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderValues.NAME);
      if (currentAttribute == null) {
        Attribute lengthAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
        long size;
        try
        {
          size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
        } catch (IOException e) {
          long size;
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        } catch (NumberFormatException e) {
          size = 0L;
        }
        try {
          if (size > 0L) {
            currentAttribute = factory.createAttribute(request, cleanString(nameAttribute.getValue()), size);
          }
          else {
            currentAttribute = factory.createAttribute(request, cleanString(nameAttribute.getValue()));
          }
        }
        catch (NullPointerException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        } catch (IllegalArgumentException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        } catch (IOException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        if (localCharset != null) {
          currentAttribute.setCharset(localCharset);
        }
      }
      try
      {
        loadFieldMultipart(multipartDataBoundary);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
        return null;
      }
      Attribute finalAttribute = currentAttribute;
      currentAttribute = null;
      currentFieldAttributes = null;
      
      currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
      return finalAttribute;
    

    case 6: 
      return getFileUpload(multipartDataBoundary);
    


    case 7: 
      return findMultipartDelimiter(multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
    

    case 8: 
      return findMultipartDisposition();
    

    case 9: 
      return getFileUpload(multipartMixedBoundary);
    
    case 10: 
      return null;
    case 11: 
      return null;
    }
    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
  }
  





  void skipControlCharacters()
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) {
      try { HttpPostBodyUtil.SeekAheadOptimize sao;
        skipControlCharactersStandard();
      } catch (IndexOutOfBoundsException e1) {
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e1);
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
  













  private InterfaceHttpData findMultipartDelimiter(String delimiter, HttpPostRequestDecoder.MultiPartStatus dispositionStatus, HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus)
  {
    int readerIndex = undecodedChunk.readerIndex();
    try {
      skipControlCharacters();
    } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
      undecodedChunk.readerIndex(readerIndex);
      return null;
    }
    skipOneLine();
    try
    {
      newline = readDelimiter(delimiter);
    } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) { String newline;
      undecodedChunk.readerIndex(readerIndex);
      return null; }
    String newline;
    if (newline.equals(delimiter)) {
      currentStatus = dispositionStatus;
      return decodeMultipart(dispositionStatus);
    }
    if (newline.equals(delimiter + "--"))
    {
      currentStatus = closeDelimiterStatus;
      if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER)
      {

        currentFieldAttributes = null;
        return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
      }
      return null;
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
  }
  





  private InterfaceHttpData findMultipartDisposition()
  {
    int readerIndex = undecodedChunk.readerIndex();
    if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
      currentFieldAttributes = new TreeMap(CaseIgnoringComparator.INSTANCE);
    }
    
    while (!skipOneLine())
    {
      try {
        skipControlCharacters();
        newline = readLine();
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) { String newline;
        undecodedChunk.readerIndex(readerIndex);
        return null; }
      String newline;
      String[] contents = splitMultipartHeader(newline);
      if (HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(contents[0])) { boolean checkSecondArg;
        boolean checkSecondArg;
        if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
          checkSecondArg = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(contents[1]);
        } else {
          checkSecondArg = (HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(contents[1])) || (HttpHeaderValues.FILE.contentEqualsIgnoreCase(contents[1]));
        }
        
        if (checkSecondArg)
        {
          for (int i = 2; i < contents.length; i++) {
            String[] values = contents[i].split("=", 2);
            try
            {
              String name = cleanString(values[0]);
              String value = values[1];
              

              if (HttpHeaderValues.FILENAME.contentEquals(name))
              {
                value = value.substring(1, value.length() - 1);
              }
              else {
                value = cleanString(value);
              }
              attribute = factory.createAttribute(request, name, value);
            } catch (NullPointerException e) { Attribute attribute;
              throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            } catch (IllegalArgumentException e) {
              throw new HttpPostRequestDecoder.ErrorDataDecoderException(e); }
            Attribute attribute;
            currentFieldAttributes.put(attribute.getName(), attribute);
          }
        }
      } else if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(contents[0]))
      {
        try {
          attribute = factory.createAttribute(request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), cleanString(contents[1]));
        } catch (NullPointerException e) {
          Attribute attribute;
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        } catch (IllegalArgumentException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        Attribute attribute;
        currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, attribute);
      } else if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(contents[0]))
      {
        try {
          attribute = factory.createAttribute(request, HttpHeaderNames.CONTENT_LENGTH.toString(), cleanString(contents[1]));
        } catch (NullPointerException e) {
          Attribute attribute;
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        } catch (IllegalArgumentException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        Attribute attribute;
        currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, attribute);
      } else if (HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(contents[0]))
      {
        if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(contents[1])) {
          if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            String values = StringUtil.substringAfter(contents[2], '=');
            multipartMixedBoundary = ("--" + values);
            currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
            return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
          }
          throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
        }
        
        for (int i = 1; i < contents.length; i++) {
          if (contents[i].toLowerCase().startsWith(HttpHeaderValues.CHARSET.toString())) {
            String values = StringUtil.substringAfter(contents[i], '=');
            try
            {
              attribute = factory.createAttribute(request, HttpHeaderValues.CHARSET.toString(), cleanString(values));
            } catch (NullPointerException e) {
              Attribute attribute;
              throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            } catch (IllegalArgumentException e) {
              throw new HttpPostRequestDecoder.ErrorDataDecoderException(e); }
            Attribute attribute;
            currentFieldAttributes.put(HttpHeaderValues.CHARSET, attribute);
          }
          else {
            try {
              attribute = factory.createAttribute(request, cleanString(contents[0]), contents[i]);
            } catch (NullPointerException e) {
              Attribute attribute;
              throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            } catch (IllegalArgumentException e) {
              throw new HttpPostRequestDecoder.ErrorDataDecoderException(e); }
            Attribute attribute;
            currentFieldAttributes.put(attribute.getName(), attribute);
          }
        }
      }
      else {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Unknown Params: " + newline);
      }
    }
    
    Attribute filenameAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderValues.FILENAME);
    if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
      if (filenameAttribute != null)
      {
        currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
        
        return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
      }
      
      currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
      
      return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
    }
    
    if (filenameAttribute != null)
    {
      currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
      
      return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
    }
    
    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
  }
  











  protected InterfaceHttpData getFileUpload(String delimiter)
  {
    Attribute encoding = (Attribute)currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
    Charset localCharset = charset;
    
    HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
    if (encoding != null)
    {
      try {
        code = encoding.getValue().toLowerCase();
      } catch (IOException e) { String code;
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e); }
      String code;
      if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
        localCharset = CharsetUtil.US_ASCII;
      } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
        localCharset = CharsetUtil.ISO_8859_1;
        mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
      } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value()))
      {
        mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
      } else {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
      }
    }
    Attribute charsetAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderValues.CHARSET);
    if (charsetAttribute != null) {
      try {
        localCharset = Charset.forName(charsetAttribute.getValue());
      } catch (IOException e) {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      } catch (UnsupportedCharsetException e) {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      }
    }
    if (currentFileUpload == null) {
      Attribute filenameAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderValues.FILENAME);
      Attribute nameAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderValues.NAME);
      Attribute contentTypeAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
      Attribute lengthAttribute = (Attribute)currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
      long size;
      try {
        size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
      } catch (IOException e) { long size;
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      } catch (NumberFormatException ignored) {
        size = 0L;
      }
      try { String contentType;
        String contentType;
        if (contentTypeAttribute != null) {
          contentType = contentTypeAttribute.getValue();
        } else {
          contentType = "application/octet-stream";
        }
        currentFileUpload = factory.createFileUpload(request, cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentType, mechanism.value(), localCharset, size);

      }
      catch (NullPointerException e)
      {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      } catch (IllegalArgumentException e) {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      } catch (IOException e) {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      }
    }
    try
    {
      readFileUploadByteMultipart(delimiter);

    }
    catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e)
    {
      return null;
    }
    if (currentFileUpload.isCompleted())
    {
      if (currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
        currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
        currentFieldAttributes = null;
      } else {
        currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
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
    
    factory.cleanRequestHttpData(request);
  }
  



  public void removeHttpDataFromClean(InterfaceHttpData data)
  {
    checkDestroyed();
    
    factory.removeHttpDataFromClean(request, data);
  }
  



  private void cleanMixedAttributes()
  {
    currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
    currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
    currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
    currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
    currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
  }
  







  private String readLineStandard()
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
            undecodedChunk.readByte();
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
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
  }
  







  private String readLine()
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) { HttpPostBodyUtil.SeekAheadOptimize sao;
      return readLineStandard(); }
    HttpPostBodyUtil.SeekAheadOptimize sao;
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
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
  }
  














  private String readDelimiterStandard(String delimiter)
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
          throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
          throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
  }
  













  private String readDelimiter(String delimiter)
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) { HttpPostBodyUtil.SeekAheadOptimize sao;
      return readDelimiterStandard(delimiter); }
    HttpPostBodyUtil.SeekAheadOptimize sao;
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
          throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
          }
          


          undecodedChunk.readerIndex(readerIndex);
          throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                  }
                  


                  undecodedChunk.readerIndex(readerIndex);
                  throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
    }
    undecodedChunk.readerIndex(readerIndex);
    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
  }
  









  private void readFileUploadByteMultipartStandard(String delimiter)
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
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      }
      
    } else {
      try
      {
        currentFileUpload.addContent(buffer, false);
        
        undecodedChunk.readerIndex(lastPosition);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } catch (IOException e) {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      }
    }
  }
  









  private void readFileUploadByteMultipart(String delimiter)
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) { HttpPostBodyUtil.SeekAheadOptimize sao;
      readFileUploadByteMultipartStandard(delimiter); return;
    }
    HttpPostBodyUtil.SeekAheadOptimize sao;
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
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      }
      
    } else {
      try
      {
        currentFileUpload.addContent(buffer, false);
        
        undecodedChunk.readerIndex(lastPosition);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } catch (IOException e) {
        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
      }
    }
  }
  






  private void loadFieldMultipartStandard(String delimiter)
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
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
      } else {
        try {
          currentAttribute.addContent(undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
        }
        catch (IOException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      }
    } catch (IndexOutOfBoundsException e) {
      undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
    }
  }
  






  private void loadFieldMultipart(String delimiter)
  {
    try
    {
      sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
    } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException ignored) { HttpPostBodyUtil.SeekAheadOptimize sao;
      loadFieldMultipartStandard(delimiter); return;
    }
    HttpPostBodyUtil.SeekAheadOptimize sao;
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
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
      } else {
        try {
          currentAttribute.addContent(undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
        }
        catch (IOException e) {
          throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      }
    } catch (IndexOutOfBoundsException e) {
      undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
    }
  }
  





  private static String cleanString(String field)
  {
    StringBuilder sb = new StringBuilder(field.length());
    for (int i = 0; i < field.length(); i++) {
      char nextChar = field.charAt(i);
      if (nextChar == ':') {
        sb.append(' ');
      } else if (nextChar == ',') {
        sb.append(' ');
      } else if (nextChar == '=') {
        sb.append(' ');
      } else if (nextChar == ';') {
        sb.append(' ');
      } else if (nextChar == '\t') {
        sb.append(' ');
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
      values = splitMultipartHeaderValues(svalue);
    } else {
      values = svalue.split(",");
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
  



  private static String[] splitMultipartHeaderValues(String svalue)
  {
    List<String> values = InternalThreadLocalMap.get().arrayList(1);
    boolean inQuote = false;
    boolean escapeNext = false;
    int start = 0;
    for (int i = 0; i < svalue.length(); i++) {
      char c = svalue.charAt(i);
      if (inQuote) {
        if (escapeNext) {
          escapeNext = false;
        }
        else if (c == '\\') {
          escapeNext = true;
        } else if (c == '"') {
          inQuote = false;
        }
        
      }
      else if (c == '"') {
        inQuote = true;
      } else if (c == ';') {
        values.add(svalue.substring(start, i));
        start = i + 1;
      }
    }
    
    values.add(svalue.substring(start));
    return (String[])values.toArray(new String[values.size()]);
  }
}
