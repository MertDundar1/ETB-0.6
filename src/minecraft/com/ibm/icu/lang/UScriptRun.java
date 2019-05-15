package com.ibm.icu.lang;

import com.ibm.icu.text.UTF16;

















































/**
 * @deprecated
 */
public final class UScriptRun
{
  /**
   * @deprecated
   */
  public UScriptRun()
  {
    char[] nullChars = null;
    
    reset(nullChars, 0, 0);
  }
  






  /**
   * @deprecated
   */
  public UScriptRun(String text)
  {
    reset(text);
  }
  








  /**
   * @deprecated
   */
  public UScriptRun(String text, int start, int count)
  {
    reset(text, start, count);
  }
  






  /**
   * @deprecated
   */
  public UScriptRun(char[] chars)
  {
    reset(chars);
  }
  








  /**
   * @deprecated
   */
  public UScriptRun(char[] chars, int start, int count)
  {
    reset(chars, start, count);
  }
  







  /**
   * @deprecated
   */
  public final void reset()
  {
    while (stackIsNotEmpty()) {
      pop();
    }
    
    scriptStart = textStart;
    scriptLimit = textStart;
    scriptCode = -1;
    parenSP = -1;
    pushCount = 0;
    fixupCount = 0;
    
    textIndex = textStart;
  }
  









  /**
   * @deprecated
   */
  public final void reset(int start, int count)
    throws IllegalArgumentException
  {
    int len = 0;
    
    if (text != null) {
      len = text.length;
    }
    
    if ((start < 0) || (count < 0) || (start > len - count)) {
      throw new IllegalArgumentException();
    }
    
    textStart = start;
    textLimit = (start + count);
    
    reset();
  }
  









  /**
   * @deprecated
   */
  public final void reset(char[] chars, int start, int count)
  {
    if (chars == null) {
      chars = emptyCharArray;
    }
    
    text = chars;
    
    reset(start, count);
  }
  






  /**
   * @deprecated
   */
  public final void reset(char[] chars)
  {
    int length = 0;
    
    if (chars != null) {
      length = chars.length;
    }
    
    reset(chars, 0, length);
  }
  









  /**
   * @deprecated
   */
  public final void reset(String str, int start, int count)
  {
    char[] chars = null;
    
    if (str != null) {
      chars = str.toCharArray();
    }
    
    reset(chars, start, count);
  }
  






  /**
   * @deprecated
   */
  public final void reset(String str)
  {
    int length = 0;
    
    if (str != null) {
      length = str.length();
    }
    
    reset(str, 0, length);
  }
  







  /**
   * @deprecated
   */
  public final int getScriptStart()
  {
    return scriptStart;
  }
  





  /**
   * @deprecated
   */
  public final int getScriptLimit()
  {
    return scriptLimit;
  }
  






  /**
   * @deprecated
   */
  public final int getScriptCode()
  {
    return scriptCode;
  }
  







  /**
   * @deprecated
   */
  public final boolean next()
  {
    if (scriptLimit >= textLimit) {
      return false;
    }
    
    scriptCode = 0;
    scriptStart = scriptLimit;
    
    syncFixup();
    
    while (textIndex < textLimit) {
      int ch = UTF16.charAt(text, textStart, textLimit, textIndex - textStart);
      int codePointCount = UTF16.getCharCount(ch);
      int sc = UScript.getScript(ch);
      int pairIndex = getPairIndex(ch);
      
      textIndex += codePointCount;
      






      if (pairIndex >= 0) {
        if ((pairIndex & 0x1) == 0) {
          push(pairIndex, scriptCode);
        } else {
          int pi = pairIndex & 0xFFFFFFFE;
          
          while ((stackIsNotEmpty()) && (toppairIndex != pi)) {
            pop();
          }
          
          if (stackIsNotEmpty()) {
            sc = topscriptCode;
          }
        }
      }
      
      if (sameScript(scriptCode, sc)) {
        if ((scriptCode <= 1) && (sc > 1)) {
          scriptCode = sc;
          
          fixup(scriptCode);
        }
        


        if ((pairIndex >= 0) && ((pairIndex & 0x1) != 0)) {
          pop();
        }
        
      }
      else
      {
        textIndex -= codePointCount;
        break;
      }
    }
    
    scriptLimit = textIndex;
    return true;
  }
  









  private static boolean sameScript(int scriptOne, int scriptTwo)
  {
    return (scriptOne <= 1) || (scriptTwo <= 1) || (scriptOne == scriptTwo);
  }
  

  private static final class ParenStackEntry
  {
    int pairIndex;
    
    int scriptCode;
    

    public ParenStackEntry(int thePairIndex, int theScriptCode)
    {
      pairIndex = thePairIndex;
      scriptCode = theScriptCode;
    }
  }
  
  private static final int mod(int sp)
  {
    return sp % PAREN_STACK_DEPTH;
  }
  
  private static final int inc(int sp, int count)
  {
    return mod(sp + count);
  }
  
  private static final int inc(int sp)
  {
    return inc(sp, 1);
  }
  
  private static final int dec(int sp, int count)
  {
    return mod(sp + PAREN_STACK_DEPTH - count);
  }
  
  private static final int dec(int sp)
  {
    return dec(sp, 1);
  }
  
  private static final int limitInc(int count)
  {
    if (count < PAREN_STACK_DEPTH) {
      count++;
    }
    
    return count;
  }
  
  private final boolean stackIsEmpty()
  {
    return pushCount <= 0;
  }
  
  private final boolean stackIsNotEmpty()
  {
    return !stackIsEmpty();
  }
  
  private final void push(int pairIndex, int scrptCode)
  {
    pushCount = limitInc(pushCount);
    fixupCount = limitInc(fixupCount);
    
    parenSP = inc(parenSP);
    parenStack[parenSP] = new ParenStackEntry(pairIndex, scrptCode);
  }
  

  private final void pop()
  {
    if (stackIsEmpty()) {
      return;
    }
    
    parenStack[parenSP] = null;
    
    if (fixupCount > 0) {
      fixupCount -= 1;
    }
    
    pushCount -= 1;
    parenSP = dec(parenSP);
    


    if (stackIsEmpty()) {
      parenSP = -1;
    }
  }
  
  private final ParenStackEntry top()
  {
    return parenStack[parenSP];
  }
  
  private final void syncFixup()
  {
    fixupCount = 0;
  }
  
  private final void fixup(int scrptCode)
  {
    int fixupSP = dec(parenSP, fixupCount);
    
    while (fixupCount-- > 0) {
      fixupSP = inc(fixupSP);
      parenStackscriptCode = scrptCode;
    }
  }
  
  private char[] emptyCharArray = new char[0];
  
  private char[] text;
  
  private int textIndex;
  
  private int textStart;
  
  private int textLimit;
  private int scriptStart;
  private int scriptLimit;
  private int scriptCode;
  private static int PAREN_STACK_DEPTH = 32;
  private static ParenStackEntry[] parenStack = new ParenStackEntry[PAREN_STACK_DEPTH];
  private int parenSP = -1;
  private int pushCount = 0;
  private int fixupCount = 0;
  







  private static final byte highBit(int n)
  {
    if (n <= 0) {
      return -32;
    }
    
    byte bit = 0;
    
    if (n >= 65536) {
      n >>= 16;
      bit = (byte)(bit + 16);
    }
    
    if (n >= 256) {
      n >>= 8;
      bit = (byte)(bit + 8);
    }
    
    if (n >= 16) {
      n >>= 4;
      bit = (byte)(bit + 4);
    }
    
    if (n >= 4) {
      n >>= 2;
      bit = (byte)(bit + 2);
    }
    
    if (n >= 2) {
      n >>= 1;
      bit = (byte)(bit + 1);
    }
    
    return bit;
  }
  






  private static int getPairIndex(int ch)
  {
    int probe = pairedCharPower;
    int index = 0;
    
    if (ch >= pairedChars[pairedCharExtra]) {
      index = pairedCharExtra;
    }
    
    while (probe > 1) {
      probe >>= 1;
      
      if (ch >= pairedChars[(index + probe)]) {
        index += probe;
      }
    }
    
    if (pairedChars[index] != ch) {
      index = -1;
    }
    
    return index;
  }
  
  private static int[] pairedChars = { 40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315 };
  


















  private static int pairedCharPower = 1 << highBit(pairedChars.length);
  private static int pairedCharExtra = pairedChars.length - pairedCharPower;
}
