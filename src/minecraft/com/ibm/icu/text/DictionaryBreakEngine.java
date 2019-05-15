package com.ibm.icu.text;

import java.text.CharacterIterator;
import java.util.Stack;





abstract class DictionaryBreakEngine
  implements LanguageBreakEngine
{
  protected UnicodeSet fSet = new UnicodeSet();
  


  private final int fTypes;
  


  public DictionaryBreakEngine(int breakTypes)
  {
    fTypes = breakTypes;
  }
  
  public boolean handles(int c, int breakType) {
    return (breakType >= 0) && (breakType < 32) && ((1 << breakType & fTypes) != 0) && (fSet.contains(c));
  }
  


  public int findBreaks(CharacterIterator text_, int startPos, int endPos, boolean reverse, int breakType, Stack<Integer> foundBreaks)
  {
    if ((breakType < 0) || (breakType >= 32) || ((1 << breakType & fTypes) == 0))
    {
      return 0;
    }
    
    int result = 0;
    UCharacterIterator text = UCharacterIterator.getInstance(text_);
    int start = text.getIndex();
    
    int c = text.current();
    int rangeEnd; int current; int rangeStart; int rangeEnd; if (reverse) {
      boolean isDict = fSet.contains(c);
      int current; while (((current = text.getIndex()) > startPos) && (isDict)) {
        c = text.previous();
        isDict = fSet.contains(c);
      }
      int rangeStart = current < startPos ? startPos : current + (isDict ? 0 : 1);
      
      rangeEnd = start + 1;
    } else {
      while (((current = text.getIndex()) < endPos) && (fSet.contains(c))) {
        c = text.next();
      }
      rangeStart = start;
      rangeEnd = current;
    }
    
    result = divideUpDictionaryRange(text, rangeStart, rangeEnd, foundBreaks);
    text.setIndex(current);
    
    return result;
  }
  
  protected abstract int divideUpDictionaryRange(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, Stack<Integer> paramStack);
}
