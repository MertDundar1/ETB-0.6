package com.ibm.icu.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;































public class CharsetMatch
  implements Comparable<CharsetMatch>
{
  private int fConfidence;
  
  public Reader getReader()
  {
    InputStream inputStream = fInputStream;
    
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(fRawInput, 0, fRawLength);
    }
    try
    {
      inputStream.reset();
      return new InputStreamReader(inputStream, getName());
    } catch (IOException e) {}
    return null;
  }
  







  public String getString()
    throws IOException
  {
    return getString(-1);
  }
  













  public String getString(int maxLength)
    throws IOException
  {
    String result = null;
    if (fInputStream != null) {
      StringBuilder sb = new StringBuilder();
      char[] buffer = new char['Ѐ'];
      Reader reader = getReader();
      int max = maxLength < 0 ? Integer.MAX_VALUE : maxLength;
      int bytesRead = 0;
      
      while ((bytesRead = reader.read(buffer, 0, Math.min(max, 1024))) >= 0) {
        sb.append(buffer, 0, bytesRead);
        max -= bytesRead;
      }
      
      reader.close();
      
      return sb.toString();
    }
    String name = getName();
    




    int startSuffix = name.indexOf("_rtl") < 0 ? name.indexOf("_ltr") : name.indexOf("_rtl");
    if (startSuffix > 0) {
      name = name.substring(0, startSuffix);
    }
    result = new String(fRawInput, name);
    
    return result;
  }
  










  public int getConfidence()
  {
    return fConfidence;
  }
  














  public String getName()
  {
    return fCharsetName;
  }
  






  public String getLanguage()
  {
    return fLang;
  }
  












  public int compareTo(CharsetMatch other)
  {
    int compareResult = 0;
    if (fConfidence > fConfidence) {
      compareResult = 1;
    } else if (fConfidence < fConfidence) {
      compareResult = -1;
    }
    return compareResult;
  }
  


  CharsetMatch(CharsetDetector det, CharsetRecognizer rec, int conf)
  {
    fConfidence = conf;
    



    if (fInputStream == null)
    {

      fRawInput = fRawInput;
      fRawLength = fRawLength;
    }
    fInputStream = fInputStream;
    fCharsetName = rec.getName();
    fLang = rec.getLanguage();
  }
  


  CharsetMatch(CharsetDetector det, CharsetRecognizer rec, int conf, String csName, String lang)
  {
    fConfidence = conf;
    



    if (fInputStream == null)
    {

      fRawInput = fRawInput;
      fRawLength = fRawLength;
    }
    fInputStream = fInputStream;
    fCharsetName = csName;
    fLang = lang;
  }
  





  private byte[] fRawInput = null;
  
  private int fRawLength;
  
  private InputStream fInputStream = null;
  private String fCharsetName;
  private String fLang;
}
