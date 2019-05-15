package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.CharTrie;
import com.ibm.icu.impl.CharacterIteration;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.lang.UCharacter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;





public class RuleBasedBreakIterator
  extends BreakIterator
{
  public static final int WORD_NONE = 0;
  public static final int WORD_NONE_LIMIT = 100;
  public static final int WORD_NUMBER = 100;
  public static final int WORD_NUMBER_LIMIT = 200;
  public static final int WORD_LETTER = 200;
  public static final int WORD_LETTER_LIMIT = 300;
  public static final int WORD_KANA = 300;
  public static final int WORD_KANA_LIMIT = 400;
  public static final int WORD_IDEO = 400;
  public static final int WORD_IDEO_LIMIT = 500;
  private static final int START_STATE = 1;
  private static final int STOP_STATE = 0;
  private static final int RBBI_START = 0;
  private static final int RBBI_RUN = 1;
  private static final int RBBI_END = 2;
  
  /**
   * @deprecated
   */
  private RuleBasedBreakIterator()
  {
    fLastStatusIndexValid = true;
    fDictionaryCharCount = 0;
    fBreakEngines.add(fUnhandledBreakEngine);
  }
  













  public static RuleBasedBreakIterator getInstanceFromCompiledRules(InputStream is)
    throws IOException
  {
    RuleBasedBreakIterator This = new RuleBasedBreakIterator();
    fRData = RBBIDataWrapper.get(is);
    return This;
  }
  




  public RuleBasedBreakIterator(String rules)
  {
    this();
    try {
      ByteArrayOutputStream ruleOS = new ByteArrayOutputStream();
      compileRules(rules, ruleOS);
      byte[] ruleBA = ruleOS.toByteArray();
      InputStream ruleIS = new ByteArrayInputStream(ruleBA);
      fRData = RBBIDataWrapper.get(ruleIS);

    }
    catch (IOException e)
    {
      RuntimeException rte = new RuntimeException("RuleBasedBreakIterator rule compilation internal error: " + e.getMessage());
      
      throw rte;
    }
  }
  











  public Object clone()
  {
    RuleBasedBreakIterator result = (RuleBasedBreakIterator)super.clone();
    if (fText != null) {
      fText = ((CharacterIterator)fText.clone());
    }
    return result;
  }
  




  public boolean equals(Object that)
  {
    if (that == null) {
      return false;
    }
    if (this == that) {
      return true;
    }
    try {
      RuleBasedBreakIterator other = (RuleBasedBreakIterator)that;
      if ((fRData != fRData) && ((fRData == null) || (fRData == null))) {
        return false;
      }
      if ((fRData != null) && (fRData != null) && (!fRData.fRuleSource.equals(fRData.fRuleSource)))
      {
        return false;
      }
      if ((fText == null) && (fText == null)) {
        return true;
      }
      if ((fText == null) || (fText == null)) {
        return false;
      }
      return fText.equals(fText);
    }
    catch (ClassCastException e) {}
    return false;
  }
  





  public String toString()
  {
    String retStr = "";
    if (fRData != null) {
      retStr = fRData.fRuleSource;
    }
    return retStr;
  }
  





  public int hashCode()
  {
    return fRData.fRuleSource.hashCode();
  }
  





















































































  private CharacterIterator fText = new StringCharacterIterator("");
  




  /**
   * @deprecated
   */
  RBBIDataWrapper fRData;
  




  private int fLastRuleStatusIndex;
  




  private boolean fLastStatusIndexValid;
  




  private int fDictionaryCharCount;
  



  private static final String RBBI_DEBUG_ARG = "rbbi";
  



  /**
   * @deprecated
   */
  private static final boolean TRACE = (ICUDebug.enabled("rbbi")) && (ICUDebug.value("rbbi").indexOf("trace") >= 0);
  





  private int fBreakType = 2;
  





  private final UnhandledBreakEngine fUnhandledBreakEngine = new UnhandledBreakEngine();
  






  private int[] fCachedBreakPositions;
  






  private int fPositionInCache;
  





  private boolean fUseDictionary = true;
  
  private final Set<LanguageBreakEngine> fBreakEngines = Collections.synchronizedSet(new HashSet());
  


  /**
   * @deprecated
   */
  public void dump()
  {
    fRData.dump();
  }
  













  public static void compileRules(String rules, OutputStream ruleBinary)
    throws IOException
  {
    RBBIRuleBuilder.compileRules(rules, ruleBinary);
  }
  









  public int first()
  {
    fCachedBreakPositions = null;
    fDictionaryCharCount = 0;
    fPositionInCache = 0;
    fLastRuleStatusIndex = 0;
    fLastStatusIndexValid = true;
    if (fText == null) {
      return -1;
    }
    fText.first();
    return fText.getIndex();
  }
  





  public int last()
  {
    fCachedBreakPositions = null;
    fDictionaryCharCount = 0;
    fPositionInCache = 0;
    
    if (fText == null) {
      fLastRuleStatusIndex = 0;
      fLastStatusIndexValid = true;
      return -1;
    }
    




    fLastStatusIndexValid = false;
    int pos = fText.getEndIndex();
    fText.setIndex(pos);
    return pos;
  }
  









  public int next(int n)
  {
    int result = current();
    while (n > 0) {
      result = handleNext();
      n--;
    }
    while (n < 0) {
      result = previous();
      n++;
    }
    return result;
  }
  




  public int next()
  {
    return handleNext();
  }
  





  public int previous()
  {
    CharacterIterator text = getText();
    
    fLastStatusIndexValid = false;
    


    if ((fCachedBreakPositions != null) && (fPositionInCache > 0)) {
      fPositionInCache -= 1;
      text.setIndex(fCachedBreakPositions[fPositionInCache]);
      return fCachedBreakPositions[fPositionInCache];
    }
    







    fCachedBreakPositions = null;
    
    int offset = current();
    int result = rulesPrevious();
    if (result == -1) {
      return result;
    }
    
    if (fDictionaryCharCount == 0) {
      return result;
    }
    
    if (fCachedBreakPositions != null) {
      fPositionInCache = (fCachedBreakPositions.length - 2);
      return result;
    }
    
    while (result < offset) {
      int nextResult = handleNext();
      if (nextResult >= offset) {
        break;
      }
      
      result = nextResult;
    }
    
    if (fCachedBreakPositions != null) {
      for (fPositionInCache = 0; fPositionInCache < fCachedBreakPositions.length; fPositionInCache += 1) {
        if (fCachedBreakPositions[fPositionInCache] >= offset) {
          fPositionInCache -= 1;
          break;
        }
      }
    }
    




    fLastStatusIndexValid = false;
    text.setIndex(result);
    
    return result;
  }
  

  private int rulesPrevious()
  {
    if ((fText == null) || (current() == fText.getBeginIndex())) {
      fLastRuleStatusIndex = 0;
      fLastStatusIndexValid = true;
      return -1;
    }
    
    if ((fRData.fSRTable != null) || (fRData.fSFTable != null)) {
      return handlePrevious(fRData.fRTable);
    }
    







    int start = current();
    
    CharacterIteration.previous32(fText);
    int lastResult = handlePrevious(fRData.fRTable);
    if (lastResult == -1) {
      lastResult = fText.getBeginIndex();
      fText.setIndex(lastResult);
    }
    int result = lastResult;
    int lastTag = 0;
    boolean breakTagValid = false;
    



    for (;;)
    {
      result = handleNext();
      if ((result == -1) || (result >= start)) {
        break;
      }
      lastResult = result;
      lastTag = fLastRuleStatusIndex;
      breakTagValid = true;
    }
    









    fText.setIndex(lastResult);
    fLastRuleStatusIndex = lastTag;
    fLastStatusIndexValid = breakTagValid;
    return lastResult;
  }
  






  public int following(int offset)
  {
    CharacterIterator text = getText();
    




    if ((fCachedBreakPositions == null) || (offset < fCachedBreakPositions[0]) || (offset >= fCachedBreakPositions[(fCachedBreakPositions.length - 1)]))
    {
      fCachedBreakPositions = null;
      return rulesFollowing(offset);
    }
    




    fPositionInCache = 0;
    
    while ((fPositionInCache < fCachedBreakPositions.length) && (offset >= fCachedBreakPositions[fPositionInCache]))
      fPositionInCache += 1;
    text.setIndex(fCachedBreakPositions[fPositionInCache]);
    return text.getIndex();
  }
  



  private int rulesFollowing(int offset)
  {
    fLastRuleStatusIndex = 0;
    fLastStatusIndexValid = true;
    if ((fText == null) || (offset >= fText.getEndIndex())) {
      last();
      return next();
    }
    if (offset < fText.getBeginIndex()) {
      return first();
    }
    




    int result = 0;
    
    if (fRData.fSRTable != null)
    {

      fText.setIndex(offset);
      


      CharacterIteration.next32(fText);
      
      handlePrevious(fRData.fSRTable);
      result = next();
      while (result <= offset) {
        result = next();
      }
      return result;
    }
    if (fRData.fSFTable != null)
    {

      fText.setIndex(offset);
      CharacterIteration.previous32(fText);
      
      handleNext(fRData.fSFTable);
      


      int oldresult = previous();
      while (oldresult > offset) {
        result = previous();
        if (result <= offset) {
          return oldresult;
        }
        oldresult = result;
      }
      result = next();
      if (result <= offset) {
        return next();
      }
      return result;
    }
    








    fText.setIndex(offset);
    if (offset == fText.getBeginIndex()) {
      return handleNext();
    }
    result = previous();
    
    while ((result != -1) && (result <= offset)) {
      result = next();
    }
    
    return result;
  }
  





  public int preceding(int offset)
  {
    CharacterIterator text = getText();
    




    if ((fCachedBreakPositions == null) || (offset <= fCachedBreakPositions[0]) || (offset > fCachedBreakPositions[(fCachedBreakPositions.length - 1)]))
    {
      fCachedBreakPositions = null;
      return rulesPreceding(offset);
    }
    




    fPositionInCache = 0;
    
    while ((fPositionInCache < fCachedBreakPositions.length) && (offset > fCachedBreakPositions[fPositionInCache]))
      fPositionInCache += 1;
    fPositionInCache -= 1;
    text.setIndex(fCachedBreakPositions[fPositionInCache]);
    return text.getIndex();
  }
  




  private int rulesPreceding(int offset)
  {
    if ((fText == null) || (offset > fText.getEndIndex()))
    {
      return last();
    }
    if (offset < fText.getBeginIndex()) {
      return first();
    }
    





    if (fRData.fSFTable != null)
    {

      fText.setIndex(offset);
      


      CharacterIteration.previous32(fText);
      handleNext(fRData.fSFTable);
      int result = previous();
      while (result >= offset) {
        result = previous();
      }
      return result;
    }
    if (fRData.fSRTable != null)
    {
      fText.setIndex(offset);
      CharacterIteration.next32(fText);
      
      handlePrevious(fRData.fSRTable);
      



      int oldresult = next();
      while (oldresult < offset) {
        int result = next();
        if (result >= offset) {
          return oldresult;
        }
        oldresult = result;
      }
      int result = previous();
      if (result >= offset) {
        return previous();
      }
      return result;
    }
    

    fText.setIndex(offset);
    return previous();
  }
  



  protected static final void checkOffset(int offset, CharacterIterator text)
  {
    if ((offset < text.getBeginIndex()) || (offset > text.getEndIndex())) {
      throw new IllegalArgumentException("offset out of bounds");
    }
  }
  








  public boolean isBoundary(int offset)
  {
    checkOffset(offset, fText);
    

    if (offset == fText.getBeginIndex()) {
      first();
      return true;
    }
    
    if (offset == fText.getEndIndex()) {
      last();
      return true;
    }
    







    fText.setIndex(offset);
    CharacterIteration.previous32(fText);
    int pos = fText.getIndex();
    boolean result = following(pos) == offset;
    return result;
  }
  




  public int current()
  {
    return fText != null ? fText.getIndex() : -1;
  }
  
  private void makeRuleStatusValid() {
    if (!fLastStatusIndexValid)
    {
      int curr = current();
      if ((curr == -1) || (curr == fText.getBeginIndex()))
      {
        fLastRuleStatusIndex = 0;
        fLastStatusIndexValid = true;
      }
      else {
        int pa = fText.getIndex();
        first();
        int pb = current();
        while (fText.getIndex() < pa) {
          pb = next();
        }
        Assert.assrt(pa == pb);
      }
      Assert.assrt(fLastStatusIndexValid == true);
      Assert.assrt((fLastRuleStatusIndex >= 0) && (fLastRuleStatusIndex < fRData.fStatusTable.length));
    }
  }
  






















  public int getRuleStatus()
  {
    makeRuleStatusValid();
    







    int idx = fLastRuleStatusIndex + fRData.fStatusTable[fLastRuleStatusIndex];
    int tagVal = fRData.fStatusTable[idx];
    return tagVal;
  }
  





















  public int getRuleStatusVec(int[] fillInArray)
  {
    makeRuleStatusValid();
    int numStatusVals = fRData.fStatusTable[fLastRuleStatusIndex];
    if (fillInArray != null) {
      int numToCopy = Math.min(numStatusVals, fillInArray.length);
      for (int i = 0; i < numToCopy; i++) {
        fillInArray[i] = fRData.fStatusTable[(fLastRuleStatusIndex + i + 1)];
      }
    }
    return numStatusVals;
  }
  







  public CharacterIterator getText()
  {
    return fText;
  }
  





  public void setText(CharacterIterator newText)
  {
    fText = newText;
    
    int firstIdx = first();
    if (newText != null) {
      fUseDictionary = (((fBreakType == 1) || (fBreakType == 2)) && (newText.getEndIndex() != firstIdx));
    }
  }
  

  /**
   * @deprecated
   */
  void setBreakType(int type)
  {
    fBreakType = type;
    if ((type != 1) && (type != 2)) {
      fUseDictionary = false;
    }
  }
  
  /**
   * @deprecated
   */
  int getBreakType()
  {
    return fBreakType;
  }
  




  static final String fDebugEnv = ICUDebug.enabled("rbbi") ? ICUDebug.value("rbbi") : null;
  



  /**
   * @deprecated
   */
  private LanguageBreakEngine getEngineFor(int c)
  {
    if ((c == Integer.MAX_VALUE) || (!fUseDictionary)) {
      return null;
    }
    
    for (LanguageBreakEngine candidate : fBreakEngines) {
      if (candidate.handles(c, fBreakType)) {
        return candidate;
      }
    }
    

    int script = UCharacter.getIntPropertyValue(c, 4106);
    LanguageBreakEngine eng = null;
    try {
      switch (script) {
      case 38: 
        eng = new ThaiBreakEngine();
        break;
      case 17: 
      case 20: 
      case 22: 
        if (getBreakType() == 1) {
          eng = new CjkBreakEngine(false);
        }
        else {
          fUnhandledBreakEngine.handleChar(c, getBreakType());
          eng = fUnhandledBreakEngine;
        }
        break;
      case 18: 
        if (getBreakType() == 1) {
          eng = new CjkBreakEngine(true);
        } else {
          fUnhandledBreakEngine.handleChar(c, getBreakType());
          eng = fUnhandledBreakEngine;
        }
        break;
      default: 
        fUnhandledBreakEngine.handleChar(c, getBreakType());
        eng = fUnhandledBreakEngine;
      }
    }
    catch (IOException e) {
      eng = null;
    }
    
    if (eng != null) {
      fBreakEngines.add(eng);
    }
    return eng;
  }
  







  private int handleNext()
  {
    if ((fCachedBreakPositions == null) || (fPositionInCache == fCachedBreakPositions.length - 1)) {
      int startPos = fText.getIndex();
      



      fDictionaryCharCount = 0;
      int result = handleNext(fRData.fFTable);
      



      if ((fDictionaryCharCount > 1) && (result - startPos > 1)) {
        fText.setIndex(startPos);
        LanguageBreakEngine e = getEngineFor(CharacterIteration.current32(fText));
        if (e != null)
        {
          Stack<Integer> breaks = new Stack();
          e.findBreaks(fText, startPos, result, false, getBreakType(), breaks);
          
          int breaksSize = breaks.size();
          fCachedBreakPositions = new int[breaksSize + 2];
          fCachedBreakPositions[0] = startPos;
          for (int i = 0; i < breaksSize; i++) {
            fCachedBreakPositions[(i + 1)] = ((Integer)breaks.elementAt(i)).intValue();
          }
          fCachedBreakPositions[(breaksSize + 1)] = result;
          
          fPositionInCache = 0;
        }
        else {
          fText.setIndex(result);
          return result;
        }
        
      }
      else
      {
        fCachedBreakPositions = null;
        return result;
      }
    }
    



    if (fCachedBreakPositions != null) {
      fPositionInCache += 1;
      fText.setIndex(fCachedBreakPositions[fPositionInCache]);
      return fCachedBreakPositions[fPositionInCache];
    }
    

    Assert.assrt(false);
    return -1;
  }
  















  private int handleNext(short[] stateTable)
  {
    if (TRACE) {
      System.out.println("Handle Next   pos      char  state category");
    }
    

    fLastStatusIndexValid = true;
    fLastRuleStatusIndex = 0;
    

    CharacterIterator text = fText;
    CharTrie trie = fRData.fTrie;
    

    int c = text.current();
    if (c >= 55296) {
      c = CharacterIteration.nextTrail32(text, c);
      if (c == Integer.MAX_VALUE) {
        return -1;
      }
    }
    int initialPosition = text.getIndex();
    int result = initialPosition;
    

    int state = 1;
    int row = fRData.getRowIndex(state);
    short category = 3;
    short flagsState = stateTable[5];
    int mode = 1;
    if ((flagsState & 0x2) != 0) {
      category = 2;
      mode = 0;
      if (TRACE) {
        System.out.print("            " + RBBIDataWrapper.intToString(text.getIndex(), 5));
        System.out.print(RBBIDataWrapper.intToHexString(c, 10));
        System.out.println(RBBIDataWrapper.intToString(state, 7) + RBBIDataWrapper.intToString(category, 6));
      }
    }
    int lookaheadStatus = 0;
    int lookaheadTagIdx = 0;
    int lookaheadResult = 0;
    

    while (state != 0) {
      if (c == Integer.MAX_VALUE)
      {
        if (mode == 2)
        {



          if (lookaheadResult <= result) {
            break;
          }
          


          result = lookaheadResult;
          fLastRuleStatusIndex = lookaheadTagIdx; break;
        }
        


        mode = 2;
        category = 1;
      }
      else if (mode == 1)
      {







        category = (short)trie.getCodePointValue(c);
        





        if ((category & 0x4000) != 0) {
          fDictionaryCharCount += 1;
          
          category = (short)(category & 0xBFFF);
        }
        
        if (TRACE) {
          System.out.print("            " + RBBIDataWrapper.intToString(text.getIndex(), 5));
          System.out.print(RBBIDataWrapper.intToHexString(c, 10));
          System.out.println(RBBIDataWrapper.intToString(state, 7) + RBBIDataWrapper.intToString(category, 6));
        }
        



        c = text.next();
        if (c >= 55296) {
          c = CharacterIteration.nextTrail32(text, c);
        }
      }
      else {
        mode = 1;
      }
      

      state = stateTable[(row + 4 + category)];
      row = fRData.getRowIndex(state);
      
      if (stateTable[(row + 0)] == -1)
      {
        result = text.getIndex();
        if ((c >= 65536) && (c <= 1114111))
        {

          result--;
        }
        

        fLastRuleStatusIndex = stateTable[(row + 2)];
      }
      
      if (stateTable[(row + 1)] != 0) {
        if ((lookaheadStatus != 0) && (stateTable[(row + 0)] == lookaheadStatus))
        {


          result = lookaheadResult;
          fLastRuleStatusIndex = lookaheadTagIdx;
          lookaheadStatus = 0;
          
          if ((flagsState & 0x1) != 0) {
            text.setIndex(result);
            return result;
          }
          

        }
        else
        {
          lookaheadResult = text.getIndex();
          if ((c >= 65536) && (c <= 1114111))
          {

            lookaheadResult--;
          }
          lookaheadStatus = stateTable[(row + 1)];
          lookaheadTagIdx = stateTable[(row + 2)];
        }
        
      }
      else if (stateTable[(row + 0)] != 0)
      {

        lookaheadStatus = 0;
      }
    }
    






    if (result == initialPosition) {
      if (TRACE) {
        System.out.println("Iterator did not move. Advancing by 1.");
      }
      text.setIndex(initialPosition);
      CharacterIteration.next32(text);
      result = text.getIndex();

    }
    else
    {

      text.setIndex(result);
    }
    if (TRACE) {
      System.out.println("result = " + result);
    }
    return result;
  }
  
  private int handlePrevious(short[] stateTable) {
    if ((fText == null) || (stateTable == null)) {
      return 0;
    }
    

    int category = 0;
    


    int lookaheadStatus = 0;
    int result = 0;
    int initialPosition = 0;
    int lookaheadResult = 0;
    boolean lookAheadHardBreak = (stateTable[5] & 0x1) != 0;
    





    fLastStatusIndexValid = false;
    fLastRuleStatusIndex = 0;
    

    initialPosition = fText.getIndex();
    result = initialPosition;
    int c = CharacterIteration.previous32(fText);
    

    int state = 1;
    int row = fRData.getRowIndex(state);
    category = 3;
    int mode = 1;
    if ((stateTable[5] & 0x2) != 0) {
      category = 2;
      mode = 0;
    }
    
    if (TRACE) {
      System.out.println("Handle Prev   pos   char  state category ");
    }
    


    for (;;)
    {
      if (c == Integer.MAX_VALUE)
      {
        if ((mode == 2) || (fRData.fHeader.fVersion == 1))
        {



          if (lookaheadResult < result)
          {


            result = lookaheadResult;
            lookaheadStatus = 0; break; }
          if (result != initialPosition) {
            break;
          }
          fText.setIndex(initialPosition);
          CharacterIteration.previous32(fText); break;
        }
        

        mode = 2;
        category = 1;
      }
      
      if (mode == 1)
      {


        category = (short)fRData.fTrie.getCodePointValue(c);
        





        if ((category & 0x4000) != 0) {
          fDictionaryCharCount += 1;
          
          category &= 0xBFFF;
        }
      }
      

      if (TRACE) {
        System.out.print("             " + fText.getIndex() + "   ");
        if ((32 <= c) && (c < 127)) {
          System.out.print("  " + c + "  ");
        } else {
          System.out.print(" " + Integer.toHexString(c) + " ");
        }
        System.out.println(" " + state + "  " + category + " ");
      }
      


      state = stateTable[(row + 4 + category)];
      row = fRData.getRowIndex(state);
      
      if (stateTable[(row + 0)] == -1)
      {

        result = fText.getIndex();
      }
      
      if (stateTable[(row + 1)] != 0) {
        if ((lookaheadStatus != 0) && (stateTable[(row + 0)] == lookaheadStatus))
        {




          result = lookaheadResult;
          lookaheadStatus = 0;
          

          if (lookAheadHardBreak)
          {
            break;
          }
          

        }
        else
        {

          lookaheadResult = fText.getIndex();
          lookaheadStatus = stateTable[(row + 1)];
        }
        

      }
      else if (stateTable[(row + 0)] != 0)
      {
        if (!lookAheadHardBreak)
        {






          lookaheadStatus = 0;
        }
      }
      



      if (state == 0) {
        break;
      }
      



      if (mode == 1) {
        c = CharacterIteration.previous32(fText);
      }
      else if (mode == 0) {
        mode = 1;
      }
    }
    








    if (result == initialPosition) {
      result = fText.setIndex(initialPosition);
      CharacterIteration.previous32(fText);
      result = fText.getIndex();
    }
    
    fText.setIndex(result);
    if (TRACE) {
      System.out.println("Result = " + result);
    }
    
    return result;
  }
}
