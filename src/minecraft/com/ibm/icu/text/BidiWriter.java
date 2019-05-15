package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;











final class BidiWriter
{
  static final char LRM_CHAR = '‎';
  static final char RLM_CHAR = '‏';
  static final int MASK_R_AL = 8194;
  
  BidiWriter() {}
  
  private static boolean IsCombining(int type)
  {
    return (1 << type & 0x1C0) != 0;
  }
  












  private static String doWriteForward(String src, int options)
  {
    switch (options & 0xA)
    {
    case 0: 
      return src;
    
    case 2: 
      StringBuffer dest = new StringBuffer(src.length());
      

      int i = 0;
      
      do
      {
        int c = UTF16.charAt(src, i);
        i += UTF16.getCharCount(c);
        UTF16.append(dest, UCharacter.getMirror(c));
      } while (i < src.length());
      return dest.toString();
    
    case 8: 
      StringBuilder dest = new StringBuilder(src.length());
      

      int i = 0;
      do
      {
        char c = src.charAt(i++);
        if (!Bidi.IsBidiControlChar(c)) {
          dest.append(c);
        }
      } while (i < src.length());
      return dest.toString();
    }
    
    StringBuffer dest = new StringBuffer(src.length());
    

    int i = 0;
    do
    {
      int c = UTF16.charAt(src, i);
      i += UTF16.getCharCount(c);
      if (!Bidi.IsBidiControlChar(c)) {
        UTF16.append(dest, UCharacter.getMirror(c));
      }
    } while (i < src.length());
    return dest.toString();
  }
  



  private static String doWriteForward(char[] text, int start, int limit, int options)
  {
    return doWriteForward(new String(text, start, limit - start), options);
  }
  

















  static String writeReverse(String src, int options)
  {
    StringBuffer dest = new StringBuffer(src.length());
    
    int srcLength;
    switch (options & 0xB)
    {












    case 0: 
      srcLength = src.length();
      


      do
      {
        int i = srcLength;
        

        srcLength -= UTF16.getCharCount(UTF16.charAt(src, srcLength - 1));
        


        dest.append(src.substring(srcLength, i));
      } while (srcLength > 0);
      break;
    







    case 1: 
      srcLength = src.length();
      



      do
      {
        int i = srcLength;
        
        int c;
        do
        {
          c = UTF16.charAt(src, srcLength - 1);
          srcLength -= UTF16.getCharCount(c);
        } while ((srcLength > 0) && (IsCombining(UCharacter.getType(c))));
        

        dest.append(src.substring(srcLength, i));
      } while (srcLength > 0);
      break;
    







    default: 
      srcLength = src.length();
      


      do
      {
        int i = srcLength;
        

        int c = UTF16.charAt(src, srcLength - 1);
        srcLength -= UTF16.getCharCount(c);
        if ((options & 0x1) != 0)
        {
          while ((srcLength > 0) && (IsCombining(UCharacter.getType(c)))) {
            c = UTF16.charAt(src, srcLength - 1);
            srcLength -= UTF16.getCharCount(c);
          }
        }
        
        if (((options & 0x8) == 0) || (!Bidi.IsBidiControlChar(c)))
        {





          int j = srcLength;
          if ((options & 0x2) != 0)
          {
            c = UCharacter.getMirror(c);
            UTF16.append(dest, c);
            j += UTF16.getCharCount(c);
          }
          dest.append(src.substring(j, i));
        } } while (srcLength > 0);
    }
    
    
    return dest.toString();
  }
  
  static String doWriteReverse(char[] text, int start, int limit, int options)
  {
    return writeReverse(new String(text, start, limit - start), options);
  }
  


  static String writeReordered(Bidi bidi, int options)
  {
    char[] text = bidi.text;
    int runCount = bidi.countRuns();
    




    if ((reorderingOptions & 0x1) != 0) {
      options |= 0x4;
      options &= 0xFFFFFFF7;
    }
    



    if ((reorderingOptions & 0x2) != 0) {
      options |= 0x8;
      options &= 0xFFFFFFFB;
    }
    



    if ((reorderingMode != 4) && (reorderingMode != 5) && (reorderingMode != 6) && (reorderingMode != 3))
    {


      options &= 0xFFFFFFFB;
    }
    StringBuilder dest = new StringBuilder((options & 0x4) != 0 ? length * 2 : length);
    









    if ((options & 0x10) == 0)
    {
      if ((options & 0x4) == 0)
      {
        for (int run = 0; run < runCount; run++) {
          BidiRun bidiRun = bidi.getVisualRun(run);
          if (bidiRun.isEvenRun()) {
            dest.append(doWriteForward(text, start, limit, options & 0xFFFFFFFD));
          }
          else
          {
            dest.append(doWriteReverse(text, start, limit, options));
          }
        }
      }
      

      byte[] dirProps = bidi.dirProps;
      


      for (int run = 0; run < runCount; run++) {
        BidiRun bidiRun = bidi.getVisualRun(run);
        int markFlag = 0;
        
        markFlag = runs[run].insertRemove;
        if (markFlag < 0) {
          markFlag = 0;
        }
        if (bidiRun.isEvenRun()) {
          if ((bidi.isInverse()) && (dirProps[start] != 0))
          {
            markFlag |= 0x1; }
          char uc;
          char uc; if ((markFlag & 0x1) != 0) {
            uc = '‎'; } else { char uc;
            if ((markFlag & 0x4) != 0) {
              uc = '‏';
            } else
              uc = '\000';
          }
          if (uc != 0) {
            dest.append(uc);
          }
          dest.append(doWriteForward(text, start, limit, options & 0xFFFFFFFD));
          


          if ((bidi.isInverse()) && (dirProps[(limit - 1)] != 0))
          {
            markFlag |= 0x2;
          }
          if ((markFlag & 0x2) != 0) {
            uc = '‎';
          } else if ((markFlag & 0x8) != 0) {
            uc = '‏';
          } else {
            uc = '\000';
          }
          if (uc != 0) {
            dest.append(uc);
          }
        } else {
          if ((bidi.isInverse()) && (!bidi.testDirPropFlagAt(8194, limit - 1)))
          {

            markFlag |= 0x4; }
          char uc;
          char uc; if ((markFlag & 0x1) != 0) {
            uc = '‎'; } else { char uc;
            if ((markFlag & 0x4) != 0) {
              uc = '‏';
            } else
              uc = '\000';
          }
          if (uc != 0) {
            dest.append(uc);
          }
          dest.append(doWriteReverse(text, start, limit, options));
          

          if ((bidi.isInverse()) && ((0x2002 & Bidi.DirPropFlag(dirProps[start])) == 0))
          {
            markFlag |= 0x8;
          }
          if ((markFlag & 0x2) != 0) {
            uc = '‎';
          } else if ((markFlag & 0x8) != 0) {
            uc = '‏';
          } else {
            uc = '\000';
          }
          if (uc != 0) {
            dest.append(uc);
          }
        }
      }
    }
    else
    {
      if ((options & 0x4) == 0)
      {
        int run = runCount; for (;;) { run--; if (run < 0) break;
          BidiRun bidiRun = bidi.getVisualRun(run);
          if (bidiRun.isEvenRun()) {
            dest.append(doWriteReverse(text, start, limit, options & 0xFFFFFFFD));
          }
          else
          {
            dest.append(doWriteForward(text, start, limit, options));
          }
        }
      }
      


      byte[] dirProps = bidi.dirProps;
      
      int run = runCount; for (;;) { run--; if (run < 0)
          break;
        BidiRun bidiRun = bidi.getVisualRun(run);
        if (bidiRun.isEvenRun()) {
          if (dirProps[(limit - 1)] != 0) {
            dest.append('‎');
          }
          
          dest.append(doWriteReverse(text, start, limit, options & 0xFFFFFFFD));
          

          if (dirProps[start] != 0) {
            dest.append('‎');
          }
        } else {
          if ((0x2002 & Bidi.DirPropFlag(dirProps[start])) == 0) {
            dest.append('‏');
          }
          
          dest.append(doWriteForward(text, start, limit, options));
          

          if ((0x2002 & Bidi.DirPropFlag(dirProps[(limit - 1)])) == 0) {
            dest.append('‏');
          }
        }
      }
    }
    

    return dest.toString();
  }
}
