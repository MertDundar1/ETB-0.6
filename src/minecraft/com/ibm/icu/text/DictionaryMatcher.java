package com.ibm.icu.text;

import java.text.CharacterIterator;





















abstract class DictionaryMatcher
{
  DictionaryMatcher() {}
  
  public abstract int matches(CharacterIterator paramCharacterIterator, int paramInt1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt2, int[] paramArrayOfInt3);
  
  public int matches(CharacterIterator text, int maxLength, int[] lengths, int[] count, int limit)
  {
    return matches(text, maxLength, lengths, count, limit, null);
  }
  
  public abstract int getType();
}
