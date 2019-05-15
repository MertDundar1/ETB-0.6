package com.ibm.icu.impl.data;

import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.PatternProps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;



































public class ResourceReader
{
  private BufferedReader reader;
  private String resourceName;
  private String encoding;
  private Class<?> root;
  private int lineNo;
  
  public ResourceReader(String resourceName, String encoding)
    throws UnsupportedEncodingException
  {
    this(ICUData.class, "data/" + resourceName, encoding);
  }
  





  public ResourceReader(String resourceName)
  {
    this(ICUData.class, "data/" + resourceName);
  }
  









  public ResourceReader(Class<?> rootClass, String resourceName, String encoding)
    throws UnsupportedEncodingException
  {
    root = rootClass;
    this.resourceName = resourceName;
    this.encoding = encoding;
    lineNo = -1;
    _reset();
  }
  





  public ResourceReader(InputStream is, String resourceName, String encoding)
  {
    root = null;
    this.resourceName = resourceName;
    this.encoding = encoding;
    
    lineNo = -1;
    try {
      InputStreamReader isr = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
      


      reader = new BufferedReader(isr);
      lineNo = 0;
    }
    catch (UnsupportedEncodingException e) {}
  }
  






  public ResourceReader(InputStream is, String resourceName)
  {
    this(is, resourceName, null);
  }
  





  public ResourceReader(Class<?> rootClass, String resourceName)
  {
    root = rootClass;
    this.resourceName = resourceName;
    encoding = null;
    lineNo = -1;
    try {
      _reset();
    }
    catch (UnsupportedEncodingException e) {}
  }
  

  public String readLine()
    throws IOException
  {
    if (lineNo == 0)
    {
      lineNo += 1;
      String line = reader.readLine();
      if ((line.charAt(0) == 65519) || (line.charAt(0) == 65279))
      {
        line = line.substring(1);
      }
      return line;
    }
    lineNo += 1;
    return reader.readLine();
  }
  
  public String readLineSkippingComments(boolean trim)
    throws IOException
  {
    String line;
    int pos;
    do
    {
      line = readLine();
      if (line == null) {
        return line;
      }
      
      pos = PatternProps.skipWhiteSpace(line, 0);
    }
    while ((pos == line.length()) || (line.charAt(pos) == '#'));
    


    if (trim) line = line.substring(pos);
    return line;
  }
  




  public String readLineSkippingComments()
    throws IOException
  {
    return readLineSkippingComments(false);
  }
  





  public int getLineNumber()
  {
    return lineNo;
  }
  



  public String describePosition()
  {
    return resourceName + ':' + lineNo;
  }
  






  public void reset()
  {
    try
    {
      _reset();
    }
    catch (UnsupportedEncodingException e) {}
  }
  







  private void _reset()
    throws UnsupportedEncodingException
  {
    if (lineNo == 0) {
      return;
    }
    InputStream is = ICUData.getStream(root, resourceName);
    if (is == null) {
      throw new IllegalArgumentException("Can't open " + resourceName);
    }
    
    InputStreamReader isr = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
    

    reader = new BufferedReader(isr);
    lineNo = 0;
  }
}
