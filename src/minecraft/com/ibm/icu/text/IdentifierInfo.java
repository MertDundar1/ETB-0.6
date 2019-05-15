package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

























/**
 * @deprecated
 */
public class IdentifierInfo
{
  private static final UnicodeSet ASCII = new UnicodeSet(0, 127).freeze();
  
  private String identifier;
  private final BitSet requiredScripts = new BitSet();
  private final Set<BitSet> scriptSetSet = new HashSet();
  private final BitSet commonAmongAlternates = new BitSet();
  private final UnicodeSet numerics = new UnicodeSet();
  private final UnicodeSet identifierProfile = new UnicodeSet(0, 1114111);
  


  /**
   * @deprecated
   */
  public IdentifierInfo() {}
  


  private IdentifierInfo clear()
  {
    requiredScripts.clear();
    scriptSetSet.clear();
    numerics.clear();
    commonAmongAlternates.clear();
    return this;
  }
  




  /**
   * @deprecated
   */
  public IdentifierInfo setIdentifierProfile(UnicodeSet identifierProfile)
  {
    this.identifierProfile.set(identifierProfile);
    return this;
  }
  



  /**
   * @deprecated
   */
  public UnicodeSet getIdentifierProfile()
  {
    return new UnicodeSet(identifierProfile);
  }
  




  /**
   * @deprecated
   */
  public IdentifierInfo setIdentifier(String identifier)
  {
    this.identifier = identifier;
    clear();
    BitSet scriptsForCP = new BitSet();
    
    for (int i = 0; i < identifier.length(); i += Character.charCount(i)) {
      int cp = Character.codePointAt(identifier, i);
      
      if (UCharacter.getType(cp) == 9)
      {
        numerics.add(cp - UCharacter.getNumericValue(cp));
      }
      UScript.getScriptExtensions(cp, scriptsForCP);
      scriptsForCP.clear(0);
      scriptsForCP.clear(1);
      



      switch (scriptsForCP.cardinality()) {
      case 0: 
        break;
      case 1: 
        requiredScripts.or(scriptsForCP);
        break;
      default: 
        if ((!requiredScripts.intersects(scriptsForCP)) && (scriptSetSet.add(scriptsForCP)))
        {
          scriptsForCP = new BitSet();
        }
        
        break;
      }
      
    }
    
    Iterator<BitSet> it;
    if (scriptSetSet.size() > 0) {
      commonAmongAlternates.set(0, 159);
      for (it = scriptSetSet.iterator(); it.hasNext();) {
        next = (BitSet)it.next();
        
        if (requiredScripts.intersects(next)) {
          it.remove();
        }
        else {
          commonAmongAlternates.and(next);
          for (BitSet other : scriptSetSet)
            if ((next != other) && (contains(next, other))) {
              it.remove();
              break;
            }
        }
      }
    }
    BitSet next;
    if (scriptSetSet.size() == 0) {
      commonAmongAlternates.clear();
    }
    return this;
  }
  



  /**
   * @deprecated
   */
  public String getIdentifier()
  {
    return identifier;
  }
  



  /**
   * @deprecated
   */
  public BitSet getScripts()
  {
    return (BitSet)requiredScripts.clone();
  }
  




  /**
   * @deprecated
   */
  public Set<BitSet> getAlternates()
  {
    Set<BitSet> result = new HashSet();
    for (BitSet item : scriptSetSet) {
      result.add((BitSet)item.clone());
    }
    return result;
  }
  



  /**
   * @deprecated
   */
  public UnicodeSet getNumerics()
  {
    return new UnicodeSet(numerics);
  }
  



  /**
   * @deprecated
   */
  public BitSet getCommonAmongAlternates()
  {
    return (BitSet)commonAmongAlternates.clone();
  }
  


  private static final BitSet JAPANESE = set(new BitSet(), new int[] { 25, 17, 20, 22 });
  
  private static final BitSet CHINESE = set(new BitSet(), new int[] { 25, 17, 5 });
  private static final BitSet KOREAN = set(new BitSet(), new int[] { 25, 17, 18 });
  private static final BitSet CONFUSABLE_WITH_LATIN = set(new BitSet(), new int[] { 8, 14, 6 });
  




  /**
   * @deprecated
   */
  public SpoofChecker.RestrictionLevel getRestrictionLevel()
  {
    if ((!identifierProfile.containsAll(identifier)) || (getNumerics().size() > 1)) {
      return SpoofChecker.RestrictionLevel.UNRESTRICTIVE;
    }
    if (ASCII.containsAll(identifier)) {
      return SpoofChecker.RestrictionLevel.ASCII;
    }
    






    int cardinalityPlus = requiredScripts.cardinality() + (commonAmongAlternates.cardinality() == 0 ? scriptSetSet.size() : 1);
    if (cardinalityPlus < 2) {
      return SpoofChecker.RestrictionLevel.HIGHLY_RESTRICTIVE;
    }
    if ((containsWithAlternates(JAPANESE, requiredScripts)) || (containsWithAlternates(CHINESE, requiredScripts)) || (containsWithAlternates(KOREAN, requiredScripts)))
    {
      return SpoofChecker.RestrictionLevel.HIGHLY_RESTRICTIVE;
    }
    if ((cardinalityPlus == 2) && (requiredScripts.get(25)) && (!requiredScripts.intersects(CONFUSABLE_WITH_LATIN))) {
      return SpoofChecker.RestrictionLevel.MODERATELY_RESTRICTIVE;
    }
    return SpoofChecker.RestrictionLevel.MINIMALLY_RESTRICTIVE;
  }
  











  /**
   * @deprecated
   */
  public int getScriptCount()
  {
    int count = requiredScripts.cardinality() + (commonAmongAlternates.cardinality() == 0 ? scriptSetSet.size() : 1);
    
    return count;
  }
  



  /**
   * @deprecated
   */
  public String toString()
  {
    return identifier + ", " + identifierProfile.toPattern(false) + ", " + getRestrictionLevel() + ", " + displayScripts(requiredScripts) + ", " + displayAlternates(scriptSetSet) + ", " + numerics.toPattern(false);
  }
  

  private boolean containsWithAlternates(BitSet container, BitSet containee)
  {
    if (!contains(container, containee)) {
      return false;
    }
    for (BitSet alternatives : scriptSetSet) {
      if (!container.intersects(alternatives)) {
        return false;
      }
    }
    return true;
  }
  




  /**
   * @deprecated
   */
  public static String displayAlternates(Set<BitSet> alternates)
  {
    if (alternates.size() == 0) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    
    Set<BitSet> sorted = new TreeSet(BITSET_COMPARATOR);
    sorted.addAll(alternates);
    for (BitSet item : sorted) {
      if (result.length() != 0) {
        result.append("; ");
      }
      result.append(displayScripts(item));
    }
    return result.toString();
  }
  


  /**
   * @deprecated
   */
  public static final Comparator<BitSet> BITSET_COMPARATOR = new Comparator()
  {
    public int compare(BitSet arg0, BitSet arg1) {
      int diff = arg0.cardinality() - arg1.cardinality();
      if (diff != 0) return diff;
      int i0 = arg0.nextSetBit(0);
      int i1 = arg1.nextSetBit(0);
      while (((diff = i0 - i1) == 0) && (i0 > 0)) {
        i0 = arg0.nextSetBit(i0 + 1);
        i1 = arg1.nextSetBit(i1 + 1);
      }
      return diff;
    }
  };
  





  /**
   * @deprecated
   */
  public static String displayScripts(BitSet scripts)
  {
    StringBuilder result = new StringBuilder();
    for (int i = scripts.nextSetBit(0); i >= 0; i = scripts.nextSetBit(i + 1)) {
      if (result.length() != 0) {
        result.append(' ');
      }
      result.append(UScript.getShortName(i));
    }
    return result.toString();
  }
  




  /**
   * @deprecated
   */
  public static BitSet parseScripts(String scriptsString)
  {
    BitSet result = new BitSet();
    for (String item : scriptsString.trim().split(",?\\s+")) {
      if (item.length() != 0) {
        result.set(UScript.getCodeFromName(item));
      }
    }
    return result;
  }
  




  /**
   * @deprecated
   */
  public static Set<BitSet> parseAlternates(String scriptsSetString)
  {
    Set<BitSet> result = new HashSet();
    for (String item : scriptsSetString.trim().split("\\s*;\\s*")) {
      if (item.length() != 0) {
        result.add(parseScripts(item));
      }
    }
    return result;
  }
  





  /**
   * @deprecated
   */
  public static final boolean contains(BitSet container, BitSet containee)
  {
    for (int i = containee.nextSetBit(0); i >= 0; i = containee.nextSetBit(i + 1)) {
      if (!container.get(i)) {
        return false;
      }
    }
    return true;
  }
  





  /**
   * @deprecated
   */
  public static final BitSet set(BitSet bitset, int... values)
  {
    for (int value : values) {
      bitset.set(value);
    }
    return bitset;
  }
}
