package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import java.text.CharacterIterator;
import java.util.Stack;











final class UnhandledBreakEngine
  implements LanguageBreakEngine
{
  private final UnicodeSet[] fHandled = new UnicodeSet[5];
  
  public UnhandledBreakEngine() { for (int i = 0; i < fHandled.length; i++) {
      fHandled[i] = new UnicodeSet();
    }
  }
  
  public boolean handles(int c, int breakType) {
    return (breakType >= 0) && (breakType < fHandled.length) && (fHandled[breakType].contains(c));
  }
  

  public int findBreaks(CharacterIterator text, int startPos, int endPos, boolean reverse, int breakType, Stack<Integer> foundBreaks)
  {
    text.setIndex(endPos);
    return 0;
  }
  
  public synchronized void handleChar(int c, int breakType) {
    if ((breakType >= 0) && (breakType < fHandled.length) && (c != Integer.MAX_VALUE) && 
      (!fHandled[breakType].contains(c))) {
      int script = UCharacter.getIntPropertyValue(c, 4106);
      fHandled[breakType].applyIntPropertyValue(4106, script);
    }
  }
}
