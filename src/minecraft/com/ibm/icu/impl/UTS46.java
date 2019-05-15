package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.IDNA;
import com.ibm.icu.text.IDNA.Error;
import com.ibm.icu.text.IDNA.Info;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.Normalizer2.Mode;
import com.ibm.icu.text.StringPrepParseException;
import java.util.EnumSet;
import java.util.Set;
















public final class UTS46
  extends IDNA
{
  public UTS46(int options)
  {
    this.options = options;
  }
  
  public StringBuilder labelToASCII(CharSequence label, StringBuilder dest, IDNA.Info info)
  {
    return process(label, true, true, dest, info);
  }
  
  public StringBuilder labelToUnicode(CharSequence label, StringBuilder dest, IDNA.Info info)
  {
    return process(label, true, false, dest, info);
  }
  
  public StringBuilder nameToASCII(CharSequence name, StringBuilder dest, IDNA.Info info)
  {
    process(name, false, true, dest, info);
    if ((dest.length() >= 254) && (!info.getErrors().contains(IDNA.Error.DOMAIN_NAME_TOO_LONG)) && (isASCIIString(dest)) && ((dest.length() > 254) || (dest.charAt(253) != '.')))
    {


      addError(info, IDNA.Error.DOMAIN_NAME_TOO_LONG);
    }
    return dest;
  }
  
  public StringBuilder nameToUnicode(CharSequence name, StringBuilder dest, IDNA.Info info)
  {
    return process(name, false, false, dest, info);
  }
  
  private static final Normalizer2 uts46Norm2 = Normalizer2.getInstance(null, "uts46", Normalizer2.Mode.COMPOSE);
  

  final int options;
  
  private static final EnumSet<IDNA.Error> severeErrors = EnumSet.of(IDNA.Error.LEADING_COMBINING_MARK, IDNA.Error.DISALLOWED, IDNA.Error.PUNYCODE, IDNA.Error.LABEL_HAS_DOT, IDNA.Error.INVALID_ACE_LABEL);
  





  private static boolean isASCIIString(CharSequence dest)
  {
    int length = dest.length();
    for (int i = 0; i < length; i++) {
      if (dest.charAt(i) > '') {
        return false;
      }
    }
    return true;
  }
  







  private static final byte[] asciiData = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1 };
  



















  private StringBuilder process(CharSequence src, boolean isLabel, boolean toASCII, StringBuilder dest, IDNA.Info info)
  {
    if (dest == src) {
      throw new IllegalArgumentException();
    }
    
    dest.delete(0, Integer.MAX_VALUE);
    resetInfo(info);
    int srcLength = src.length();
    if (srcLength == 0) {
      if (toASCII) {
        addError(info, IDNA.Error.EMPTY_LABEL);
      }
      return dest;
    }
    
    boolean disallowNonLDHDot = (options & 0x2) != 0;
    int labelStart = 0;
    
    for (int i = 0;; i++) {
      if (i == srcLength) {
        if (toASCII) {
          if (i - labelStart > 63) {
            addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
          }
          
          if ((!isLabel) && (i >= 254) && ((i > 254) || (labelStart < i))) {
            addError(info, IDNA.Error.DOMAIN_NAME_TOO_LONG);
          }
        }
        promoteAndResetLabelErrors(info);
        return dest;
      }
      char c = src.charAt(i);
      if (c > '') {
        break;
      }
      int cData = asciiData[c];
      if (cData > 0) {
        dest.append((char)(c + ' '));
      } else { if ((cData < 0) && (disallowNonLDHDot)) {
          break;
        }
        dest.append(c);
        if (c == '-') {
          if ((i == labelStart + 3) && (src.charAt(i - 1) == '-'))
          {
            i++;
            break;
          }
          if (i == labelStart)
          {
            addLabelError(info, IDNA.Error.LEADING_HYPHEN);
          }
          if ((i + 1 == srcLength) || (src.charAt(i + 1) == '.'))
          {
            addLabelError(info, IDNA.Error.TRAILING_HYPHEN);
          }
        } else if (c == '.') {
          if (isLabel)
          {
            i++;
            break;
          }
          if (toASCII)
          {
            if ((i == labelStart) && (i < srcLength - 1)) {
              addLabelError(info, IDNA.Error.EMPTY_LABEL);
            } else if (i - labelStart > 63) {
              addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
            }
          }
          promoteAndResetLabelErrors(info);
          labelStart = i + 1;
        }
      }
    }
    promoteAndResetLabelErrors(info);
    processUnicode(src, labelStart, i, isLabel, toASCII, dest, info);
    if ((isBiDi(info)) && (!hasCertainErrors(info, severeErrors)) && ((!isOkBiDi(info)) || ((labelStart > 0) && (!isASCIIOkBiDi(dest, labelStart)))))
    {

      addError(info, IDNA.Error.BIDI);
    }
    return dest;
  }
  




  private StringBuilder processUnicode(CharSequence src, int labelStart, int mappingStart, boolean isLabel, boolean toASCII, StringBuilder dest, IDNA.Info info)
  {
    if (mappingStart == 0) {
      uts46Norm2.normalize(src, dest);
    } else {
      uts46Norm2.normalizeSecondAndAppend(dest, src.subSequence(mappingStart, src.length()));
    }
    boolean doMapDevChars = (options & 0x10) == 0;
    

    int destLength = dest.length();
    int labelLimit = labelStart;
    while (labelLimit < destLength) {
      char c = dest.charAt(labelLimit);
      if ((c == '.') && (!isLabel)) {
        int labelLength = labelLimit - labelStart;
        int newLength = processLabel(dest, labelStart, labelLength, toASCII, info);
        
        promoteAndResetLabelErrors(info);
        destLength += newLength - labelLength;
        labelLimit = labelStart += newLength + 1;
      } else if (('ß' <= c) && (c <= '‍') && ((c == 'ß') || (c == 'ς') || (c >= '‌'))) {
        setTransitionalDifferent(info);
        if (doMapDevChars) {
          destLength = mapDevChars(dest, labelStart, labelLimit);
          

          doMapDevChars = false;
        } else {
          labelLimit++;
        }
      } else {
        labelLimit++;
      }
    }
    


    if ((0 == labelStart) || (labelStart < labelLimit)) {
      processLabel(dest, labelStart, labelLimit - labelStart, toASCII, info);
      promoteAndResetLabelErrors(info);
    }
    return dest;
  }
  

  private int mapDevChars(StringBuilder dest, int labelStart, int mappingStart)
  {
    int length = dest.length();
    boolean didMapDevChars = false;
    for (int i = mappingStart; i < length;) {
      char c = dest.charAt(i);
      switch (c)
      {
      case 'ß': 
        didMapDevChars = true;
        dest.setCharAt(i++, 's');
        dest.insert(i++, 's');
        length++;
        break;
      case 'ς': 
        didMapDevChars = true;
        dest.setCharAt(i++, 'σ');
        break;
      case '‌': 
      case '‍': 
        didMapDevChars = true;
        dest.delete(i, i + 1);
        length--;
        break;
      default: 
        i++;
      }
      
    }
    if (didMapDevChars)
    {


      String normalized = uts46Norm2.normalize(dest.subSequence(labelStart, dest.length()));
      dest.replace(labelStart, Integer.MAX_VALUE, normalized);
      return dest.length();
    }
    return length;
  }
  


  private static boolean isNonASCIIDisallowedSTD3Valid(int c)
  {
    return (c == 8800) || (c == 8814) || (c == 8815);
  }
  







  private static int replaceLabel(StringBuilder dest, int destLabelStart, int destLabelLength, CharSequence label, int labelLength)
  {
    if (label != dest) {
      dest.delete(destLabelStart, destLabelStart + destLabelLength).insert(destLabelStart, label);
    }
    

    return labelLength;
  }
  






  private int processLabel(StringBuilder dest, int labelStart, int labelLength, boolean toASCII, IDNA.Info info)
  {
    int destLabelStart = labelStart;
    int destLabelLength = labelLength;
    boolean wasPunycode;
    StringBuilder labelString; if ((labelLength >= 4) && (dest.charAt(labelStart) == 'x') && (dest.charAt(labelStart + 1) == 'n') && (dest.charAt(labelStart + 2) == '-') && (dest.charAt(labelStart + 3) == '-'))
    {



      boolean wasPunycode = true;
      StringBuilder fromPunycode;
      try { fromPunycode = Punycode.decode(dest.subSequence(labelStart + 4, labelStart + labelLength), null);
      } catch (StringPrepParseException e) {
        addLabelError(info, IDNA.Error.PUNYCODE);
        return markBadACELabel(dest, labelStart, labelLength, toASCII, info);
      }
      






      boolean isValid = uts46Norm2.isNormalized(fromPunycode);
      if (!isValid) {
        addLabelError(info, IDNA.Error.INVALID_ACE_LABEL);
        return markBadACELabel(dest, labelStart, labelLength, toASCII, info);
      }
      StringBuilder labelString = fromPunycode;
      labelStart = 0;
      labelLength = fromPunycode.length();
    } else {
      wasPunycode = false;
      labelString = dest;
    }
    
    if (labelLength == 0) {
      if (toASCII) {
        addLabelError(info, IDNA.Error.EMPTY_LABEL);
      }
      return replaceLabel(dest, destLabelStart, destLabelLength, labelString, labelLength);
    }
    
    if ((labelLength >= 4) && (labelString.charAt(labelStart + 2) == '-') && (labelString.charAt(labelStart + 3) == '-'))
    {
      addLabelError(info, IDNA.Error.HYPHEN_3_4);
    }
    if (labelString.charAt(labelStart) == '-')
    {
      addLabelError(info, IDNA.Error.LEADING_HYPHEN);
    }
    if (labelString.charAt(labelStart + labelLength - 1) == '-')
    {
      addLabelError(info, IDNA.Error.TRAILING_HYPHEN);
    }
    








    int i = labelStart;
    int limit = labelStart + labelLength;
    char oredChars = '\000';
    
    boolean disallowNonLDHDot = (options & 0x2) != 0;
    do {
      char c = labelString.charAt(i);
      if (c <= '') {
        if (c == '.') {
          addLabelError(info, IDNA.Error.LABEL_HAS_DOT);
          labelString.setCharAt(i, 65533);
        } else if ((disallowNonLDHDot) && (asciiData[c] < 0)) {
          addLabelError(info, IDNA.Error.DISALLOWED);
          labelString.setCharAt(i, 65533);
        }
      } else {
        oredChars = (char)(oredChars | c);
        if ((disallowNonLDHDot) && (isNonASCIIDisallowedSTD3Valid(c))) {
          addLabelError(info, IDNA.Error.DISALLOWED);
          labelString.setCharAt(i, 65533);
        } else if (c == 65533) {
          addLabelError(info, IDNA.Error.DISALLOWED);
        }
      }
      i++;
    } while (i < limit);
    



    int c = labelString.codePointAt(labelStart);
    if ((U_GET_GC_MASK(c) & U_GC_M_MASK) != 0) {
      addLabelError(info, IDNA.Error.LEADING_COMBINING_MARK);
      labelString.setCharAt(labelStart, 65533);
      if (c > 65535)
      {
        labelString.deleteCharAt(labelStart + 1);
        labelLength--;
        if (labelString == dest) {
          destLabelLength--;
        }
      }
    }
    if (!hasCertainLabelErrors(info, severeErrors))
    {

      if (((options & 0x4) != 0) && ((!isBiDi(info)) || (isOkBiDi(info)))) {
        checkLabelBiDi(labelString, labelStart, labelLength, info);
      }
      if (((options & 0x8) != 0) && ((oredChars & 0x200C) == '‌') && (!isLabelOkContextJ(labelString, labelStart, labelLength)))
      {

        addLabelError(info, IDNA.Error.CONTEXTJ);
      }
      if (((options & 0x40) != 0) && (oredChars >= '·')) {
        checkLabelContextO(labelString, labelStart, labelLength, info);
      }
      if (toASCII) {
        if (wasPunycode)
        {
          if (destLabelLength > 63) {
            addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
          }
          return destLabelLength; }
        if (oredChars >= '')
        {
          StringBuilder punycode;
          try {
            punycode = Punycode.encode(labelString.subSequence(labelStart, labelStart + labelLength), null);
          } catch (StringPrepParseException e) {
            throw new RuntimeException(e);
          }
          punycode.insert(0, "xn--");
          if (punycode.length() > 63) {
            addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
          }
          return replaceLabel(dest, destLabelStart, destLabelLength, punycode, punycode.length());
        }
        

        if (labelLength > 63) {
          addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
        }
        
      }
      

    }
    else if (wasPunycode) {
      addLabelError(info, IDNA.Error.INVALID_ACE_LABEL);
      return markBadACELabel(dest, destLabelStart, destLabelLength, toASCII, info);
    }
    
    return replaceLabel(dest, destLabelStart, destLabelLength, labelString, labelLength);
  }
  

  private int markBadACELabel(StringBuilder dest, int labelStart, int labelLength, boolean toASCII, IDNA.Info info)
  {
    boolean disallowNonLDHDot = (options & 0x2) != 0;
    boolean isASCII = true;
    boolean onlyLDH = true;
    int i = labelStart + 4;
    int limit = labelStart + labelLength;
    do {
      char c = dest.charAt(i);
      if (c <= '') {
        if (c == '.') {
          addLabelError(info, IDNA.Error.LABEL_HAS_DOT);
          dest.setCharAt(i, 65533);
          isASCII = onlyLDH = 0;
        } else if (asciiData[c] < 0) {
          onlyLDH = false;
          if (disallowNonLDHDot) {
            dest.setCharAt(i, 65533);
            isASCII = false;
          }
        }
      } else {
        isASCII = onlyLDH = 0;
      }
      i++; } while (i < limit);
    if (onlyLDH) {
      dest.insert(labelStart + labelLength, 65533);
      labelLength++;
    }
    else if ((toASCII) && (isASCII) && (labelLength > 63)) {
      addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
    }
    
    return labelLength;
  }
  
  private static final int L_MASK = U_MASK(0);
  private static final int R_AL_MASK = U_MASK(1) | U_MASK(13);
  

  private static final int L_R_AL_MASK = L_MASK | R_AL_MASK;
  
  private static final int R_AL_AN_MASK = R_AL_MASK | U_MASK(5);
  
  private static final int EN_AN_MASK = U_MASK(2) | U_MASK(5);
  

  private static final int R_AL_EN_AN_MASK = R_AL_MASK | EN_AN_MASK;
  private static final int L_EN_MASK = L_MASK | U_MASK(2);
  
  private static final int ES_CS_ET_ON_BN_NSM_MASK = U_MASK(3) | U_MASK(6) | U_MASK(4) | U_MASK(10) | U_MASK(18) | U_MASK(17);
  





  private static final int L_EN_ES_CS_ET_ON_BN_NSM_MASK = L_EN_MASK | ES_CS_ET_ON_BN_NSM_MASK;
  private static final int R_AL_AN_EN_ES_CS_ET_ON_BN_NSM_MASK = R_AL_MASK | EN_AN_MASK | ES_CS_ET_ON_BN_NSM_MASK;
  








  private void checkLabelBiDi(CharSequence label, int labelStart, int labelLength, IDNA.Info info)
  {
    int i = labelStart;
    int c = Character.codePointAt(label, i);
    i += Character.charCount(c);
    int firstMask = U_MASK(UBiDiProps.INSTANCE.getClass(c));
    


    if ((firstMask & (L_R_AL_MASK ^ 0xFFFFFFFF)) != 0) {
      setNotOkBiDi(info);
    }
    

    int labelLimit = labelStart + labelLength;
    for (;;) {
      if (i >= labelLimit) {
        int lastMask = firstMask;
        break;
      }
      c = Character.codePointBefore(label, labelLimit);
      labelLimit -= Character.charCount(c);
      int dir = UBiDiProps.INSTANCE.getClass(c);
      if (dir != 17) {
        int lastMask = U_MASK(dir);
        break;
      }
    }
    


    int lastMask;
    

    if ((firstMask & L_MASK) != 0 ? (lastMask & (L_EN_MASK ^ 0xFFFFFFFF)) != 0 : (lastMask & (R_AL_EN_AN_MASK ^ 0xFFFFFFFF)) != 0)
    {


      setNotOkBiDi(info);
    }
    
    int mask = 0;
    while (i < labelLimit) {
      c = Character.codePointAt(label, i);
      i += Character.charCount(c);
      mask |= U_MASK(UBiDiProps.INSTANCE.getClass(c));
    }
    if ((firstMask & L_MASK) != 0)
    {

      if ((mask & (L_EN_ES_CS_ET_ON_BN_NSM_MASK ^ 0xFFFFFFFF)) != 0) {
        setNotOkBiDi(info);
      }
    }
    else
    {
      if ((mask & (R_AL_AN_EN_ES_CS_ET_ON_BN_NSM_MASK ^ 0xFFFFFFFF)) != 0) {
        setNotOkBiDi(info);
      }
      

      if ((mask & EN_AN_MASK) == EN_AN_MASK) {
        setNotOkBiDi(info);
      }
    }
    





    if (((firstMask | mask | lastMask) & R_AL_AN_MASK) != 0) {
      setBiDi(info);
    }
  }
  













  private static boolean isASCIIOkBiDi(CharSequence s, int length)
  {
    int labelStart = 0;
    for (int i = 0; i < length; i++) {
      char c = s.charAt(i);
      if (c == '.') {
        if (i > labelStart) {
          c = s.charAt(i - 1);
          if ((('a' > c) || (c > 'z')) && (('0' > c) || (c > '9')))
          {
            return false;
          }
        }
        labelStart = i + 1;
      } else if (i == labelStart) {
        if (('a' > c) || (c > 'z'))
        {
          return false;
        }
      }
      else if ((c <= ' ') && ((c >= '\034') || (('\t' <= c) && (c <= '\r'))))
      {
        return false;
      }
    }
    
    return true;
  }
  


  private boolean isLabelOkContextJ(CharSequence label, int labelStart, int labelLength)
  {
    int labelLimit = labelStart + labelLength;
    for (int i = labelStart; i < labelLimit; i++) {
      if (label.charAt(i) == '‌')
      {





        if (i == labelStart) {
          return false;
        }
        
        int j = i;
        int c = Character.codePointBefore(label, j);
        j -= Character.charCount(c);
        if (uts46Norm2.getCombiningClass(c) != 9)
        {

          for (;;)
          {
            int type = UBiDiProps.INSTANCE.getJoiningType(c);
            if (type == 5) {
              if (j == 0) {
                return false;
              }
              c = Character.codePointBefore(label, j);
              j -= Character.charCount(c);
            } else { if ((type == 3) || (type == 2)) {
                break;
              }
              return false;
            }
          }
          
          j = i + 1;
          for (;;) { if (j == labelLimit) {
              return false;
            }
            c = Character.codePointAt(label, j);
            j += Character.charCount(c);
            int type = UBiDiProps.INSTANCE.getJoiningType(c);
            if (type != 5)
            {
              if ((type == 4) || (type == 2)) {
                break;
              }
              return false;
            }
          }
        } } else if (label.charAt(i) == '‍')
      {



        if (i == labelStart) {
          return false;
        }
        int c = Character.codePointBefore(label, i);
        if (uts46Norm2.getCombiningClass(c) != 9) {
          return false;
        }
      }
    }
    return true;
  }
  
  private void checkLabelContextO(CharSequence label, int labelStart, int labelLength, IDNA.Info info)
  {
    int labelEnd = labelStart + labelLength - 1;
    int arabicDigits = 0;
    for (int i = labelStart; i <= labelEnd; i++) {
      int c = label.charAt(i);
      if (c >= 183)
      {
        if (c <= 1785) {
          if (c == 183)
          {




            if ((labelStart >= i) || (label.charAt(i - 1) != 'l') || (i >= labelEnd) || (label.charAt(i + 1) != 'l'))
            {
              addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
            }
          } else if (c == 885)
          {



            if ((i >= labelEnd) || (14 != UScript.getScript(Character.codePointAt(label, i + 1))))
            {
              addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
            }
          } else if ((c == 1523) || (c == 1524))
          {








            if ((labelStart >= i) || (19 != UScript.getScript(Character.codePointBefore(label, i))))
            {
              addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
            }
          } else if (1632 <= c)
          {












            if (c <= 1641) {
              if (arabicDigits > 0) {
                addLabelError(info, IDNA.Error.CONTEXTO_DIGITS);
              }
              arabicDigits = -1;
            } else if (1776 <= c) {
              if (arabicDigits < 0) {
                addLabelError(info, IDNA.Error.CONTEXTO_DIGITS);
              }
              arabicDigits = 1;
            }
          }
        } else if (c == 12539)
        {





          for (int j = labelStart;; j += Character.charCount(c)) {
            if (j > labelEnd) {
              addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
            }
            else {
              c = Character.codePointAt(label, j);
              int script = UScript.getScript(c);
              if ((script == 20) || (script == 22) || (script == 17))
                break;
            }
          }
        }
      }
    }
  }
  
  private static int U_MASK(int x) {
    return 1 << x;
  }
  
  private static int U_GET_GC_MASK(int c) { return 1 << UCharacter.getType(c); }
  
  private static int U_GC_M_MASK = U_MASK(6) | U_MASK(7) | U_MASK(8);
}
