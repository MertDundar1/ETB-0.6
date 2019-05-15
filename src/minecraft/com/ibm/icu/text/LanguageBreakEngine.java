package com.ibm.icu.text;

import java.text.CharacterIterator;
import java.util.Stack;

abstract interface LanguageBreakEngine
{
  public abstract boolean handles(int paramInt1, int paramInt2);
  
  public abstract int findBreaks(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, Stack<Integer> paramStack);
}
