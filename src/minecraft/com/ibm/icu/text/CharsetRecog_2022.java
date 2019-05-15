package com.ibm.icu.text;














abstract class CharsetRecog_2022
  extends CharsetRecognizer
{
  CharsetRecog_2022() {}
  












  int match(byte[] text, int textLen, byte[][] escapeSequences)
  {
    int hits = 0;
    int misses = 0;
    int shifts = 0;
    label107:
    label137:
    for (int i = 0; i < textLen; i++) {
      if (text[i] == 27)
      {
        for (int escN = 0; escN < escapeSequences.length; escN++) {
          byte[] seq = escapeSequences[escN];
          
          if (textLen - i >= seq.length)
          {


            for (int j = 1; j < seq.length; j++) {
              if (seq[j] != text[(i + j)]) {
                break label107;
              }
            }
            
            hits++;
            i += seq.length - 1;
            break label137;
          }
        }
        misses++;
      }
      
      if ((text[i] == 14) || (text[i] == 15))
      {
        shifts++;
      }
    }
    
    if (hits == 0) {
      return 0;
    }
    






    int quality = (100 * hits - 100 * misses) / (hits + misses);
    



    if (hits + shifts < 5) {
      quality -= (5 - (hits + shifts)) * 10;
    }
    
    if (quality < 0) {
      quality = 0;
    }
    return quality;
  }
  

  static class CharsetRecog_2022JP
    extends CharsetRecog_2022
  {
    private byte[][] escapeSequences = { { 27, 36, 40, 67 }, { 27, 36, 40, 68 }, { 27, 36, 64 }, { 27, 36, 65 }, { 27, 36, 66 }, { 27, 38, 64 }, { 27, 40, 66 }, { 27, 40, 72 }, { 27, 40, 73 }, { 27, 40, 74 }, { 27, 46, 65 }, { 27, 46, 70 } };
    





    CharsetRecog_2022JP() {}
    





    String getName()
    {
      return "ISO-2022-JP";
    }
    
    CharsetMatch match(CharsetDetector det) {
      int confidence = match(fInputBytes, fInputLen, escapeSequences);
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }
  
  static class CharsetRecog_2022KR extends CharsetRecog_2022 {
    private byte[][] escapeSequences = { { 27, 36, 41, 67 } };
    
    CharsetRecog_2022KR() {}
    
    String getName() {
      return "ISO-2022-KR";
    }
    
    CharsetMatch match(CharsetDetector det) {
      int confidence = match(fInputBytes, fInputLen, escapeSequences);
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }
  
  static class CharsetRecog_2022CN extends CharsetRecog_2022 {
    private byte[][] escapeSequences = { { 27, 36, 41, 65 }, { 27, 36, 41, 71 }, { 27, 36, 42, 72 }, { 27, 36, 41, 69 }, { 27, 36, 43, 73 }, { 27, 36, 43, 74 }, { 27, 36, 43, 75 }, { 27, 36, 43, 76 }, { 27, 36, 43, 77 }, { 27, 78 }, { 27, 79 } };
    





    CharsetRecog_2022CN() {}
    




    String getName()
    {
      return "ISO-2022-CN";
    }
    
    CharsetMatch match(CharsetDetector det) {
      int confidence = match(fInputBytes, fInputLen, escapeSequences);
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }
}
