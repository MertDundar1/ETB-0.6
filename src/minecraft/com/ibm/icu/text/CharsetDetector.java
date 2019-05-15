package com.ibm.icu.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

























































public class CharsetDetector
{
  private static final int kBufSize = 8000;
  
  public CharsetDetector() {}
  
  public CharsetDetector setDeclaredEncoding(String encoding)
  {
    fDeclaredEncoding = encoding;
    return this;
  }
  








  public CharsetDetector setText(byte[] in)
  {
    fRawInput = in;
    fRawLength = in.length;
    
    return this;
  }
  
















  public CharsetDetector setText(InputStream in)
    throws IOException
  {
    fInputStream = in;
    fInputStream.mark(8000);
    fRawInput = new byte['ὀ'];
    

    fRawLength = 0;
    int remainingLength = 8000;
    while (remainingLength > 0)
    {
      int bytesRead = fInputStream.read(fRawInput, fRawLength, remainingLength);
      if (bytesRead <= 0) {
        break;
      }
      fRawLength += bytesRead;
      remainingLength -= bytesRead;
    }
    fInputStream.reset();
    
    return this;
  }
  























  public CharsetMatch detect()
  {
    CharsetMatch[] matches = detectAll();
    
    if ((matches == null) || (matches.length == 0)) {
      return null;
    }
    
    return matches[0];
  }
  














  public CharsetMatch[] detectAll()
  {
    ArrayList<CharsetMatch> matches = new ArrayList();
    
    MungeInput();
    


    for (CharsetRecognizer csr : fCSRecognizers) {
      CharsetMatch m = csr.match(this);
      if (m != null) {
        matches.add(m);
      }
    }
    Collections.sort(matches);
    Collections.reverse(matches);
    CharsetMatch[] resultArray = new CharsetMatch[matches.size()];
    resultArray = (CharsetMatch[])matches.toArray(resultArray);
    return resultArray;
  }
  






















  public Reader getReader(InputStream in, String declaredEncoding)
  {
    fDeclaredEncoding = declaredEncoding;
    try
    {
      setText(in);
      
      CharsetMatch match = detect();
      
      if (match == null) {
        return null;
      }
      
      return match.getReader();
    } catch (IOException e) {}
    return null;
  }
  

















  public String getString(byte[] in, String declaredEncoding)
  {
    fDeclaredEncoding = declaredEncoding;
    try
    {
      setText(in);
      
      CharsetMatch match = detect();
      
      if (match == null) {
        return null;
      }
      
      return match.getString(-1);
    } catch (IOException e) {}
    return null;
  }
  









  public static String[] getAllDetectableCharsets()
  {
    return fCharsetNames;
  }
  









  public boolean inputFilterEnabled()
  {
    return fStripTags;
  }
  











  public boolean enableInputFilter(boolean filter)
  {
    boolean previous = fStripTags;
    
    fStripTags = filter;
    
    return previous;
  }
  



  private void MungeInput()
  {
    int srci = 0;
    int dsti = 0;
    
    boolean inMarkup = false;
    int openTags = 0;
    int badTags = 0;
    






    if (fStripTags) {
      for (srci = 0; (srci < fRawLength) && (dsti < fInputBytes.length); srci++) {
        byte b = fRawInput[srci];
        if (b == 60) {
          if (inMarkup) {
            badTags++;
          }
          inMarkup = true;
          openTags++;
        }
        
        if (!inMarkup) {
          fInputBytes[(dsti++)] = b;
        }
        
        if (b == 62) {
          inMarkup = false;
        }
      }
      
      fInputLen = dsti;
    }
    





    if ((openTags < 5) || (openTags / 5 < badTags) || ((fInputLen < 100) && (fRawLength > 600)))
    {
      int limit = fRawLength;
      
      if (limit > 8000) {
        limit = 8000;
      }
      
      for (srci = 0; srci < limit; srci++) {
        fInputBytes[srci] = fRawInput[srci];
      }
      fInputLen = srci;
    }
    




    Arrays.fill(fByteStats, (short)0);
    for (srci = 0; srci < fInputLen; srci++) {
      int val = fInputBytes[srci] & 0xFF; int 
        tmp221_219 = val; short[] tmp221_216 = fByteStats;tmp221_216[tmp221_219] = ((short)(tmp221_216[tmp221_219] + 1));
    }
    
    fC1Bytes = false;
    for (int i = 128; i <= 159; i++) {
      if (fByteStats[i] != 0) {
        fC1Bytes = true;
        break;
      }
    }
  }
  





  byte[] fInputBytes = new byte['ὀ'];
  

  int fInputLen;
  
  short[] fByteStats = new short['Ā'];
  


  boolean fC1Bytes = false;
  


  String fDeclaredEncoding;
  


  byte[] fRawInput;
  


  int fRawLength;
  


  InputStream fInputStream;
  

  boolean fStripTags = false;
  





  private static ArrayList<CharsetRecognizer> fCSRecognizers = ;
  
  private static String[] fCharsetNames;
  

  private static ArrayList<CharsetRecognizer> createRecognizers()
  {
    ArrayList<CharsetRecognizer> recognizers = new ArrayList();
    
    recognizers.add(new CharsetRecog_UTF8());
    
    recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_BE());
    recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_LE());
    recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_BE());
    recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_LE());
    
    recognizers.add(new CharsetRecog_mbcs.CharsetRecog_sjis());
    recognizers.add(new CharsetRecog_2022.CharsetRecog_2022JP());
    recognizers.add(new CharsetRecog_2022.CharsetRecog_2022CN());
    recognizers.add(new CharsetRecog_2022.CharsetRecog_2022KR());
    recognizers.add(new CharsetRecog_mbcs.CharsetRecog_gb_18030());
    recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_jp());
    recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_kr());
    recognizers.add(new CharsetRecog_mbcs.CharsetRecog_big5());
    
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_5_ru());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_6_ar());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_7_el());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_I_he());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_he());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1251());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1256());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_KOI8_R());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_9_tr());
    
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_rtl());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_ltr());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_rtl());
    recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_ltr());
    


    String[] charsetNames = new String[recognizers.size()];
    int out = 0;
    
    for (int i = 0; i < recognizers.size(); i++) {
      String name = ((CharsetRecognizer)recognizers.get(i)).getName();
      
      if ((out == 0) || (!name.equals(charsetNames[(out - 1)]))) {
        charsetNames[(out++)] = name;
      }
    }
    
    fCharsetNames = new String[out];
    System.arraycopy(charsetNames, 0, fCharsetNames, 0, out);
    
    return recognizers;
  }
}
