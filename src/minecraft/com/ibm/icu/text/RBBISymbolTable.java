package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import java.io.PrintStream;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.HashMap;
















class RBBISymbolTable
  implements SymbolTable
{
  String fRules;
  HashMap<String, RBBISymbolTableEntry> fHashTable;
  RBBIRuleScanner fRuleScanner;
  String ffffString;
  UnicodeSet fCachedSetLookup;
  
  RBBISymbolTable(RBBIRuleScanner rs, String rules)
  {
    fRules = rules;
    fRuleScanner = rs;
    fHashTable = new HashMap();
    ffffString = "￿";
  }
  













  public char[] lookup(String s)
  {
    RBBISymbolTableEntry el = (RBBISymbolTableEntry)fHashTable.get(s);
    if (el == null) {
      return null;
    }
    

    RBBINode varRefNode = val;
    while (fLeftChild.fType == 2) {
      varRefNode = fLeftChild;
    }
    
    RBBINode exprNode = fLeftChild;
    String retString; String retString; if (fType == 0)
    {


      RBBINode usetNode = fLeftChild;
      fCachedSetLookup = fInputSet;
      retString = ffffString;

    }
    else
    {

      fRuleScanner.error(66063);
      retString = fText;
      fCachedSetLookup = null;
    }
    return retString.toCharArray();
  }
  










  public UnicodeMatcher lookupMatcher(int ch)
  {
    UnicodeSet retVal = null;
    if (ch == 65535) {
      retVal = fCachedSetLookup;
      fCachedSetLookup = null;
    }
    return retVal;
  }
  





  public String parseReference(String text, ParsePosition pos, int limit)
  {
    int start = pos.getIndex();
    int i = start;
    String result = "";
    while (i < limit) {
      int c = UTF16.charAt(text, i);
      if (((i == start) && (!UCharacter.isUnicodeIdentifierStart(c))) || (!UCharacter.isUnicodeIdentifierPart(c))) {
        break;
      }
      
      i += UTF16.getCharCount(c);
    }
    if (i == start) {
      return result;
    }
    pos.setIndex(i);
    result = text.substring(start, i);
    return result;
  }
  





  RBBINode lookupNode(String key)
  {
    RBBINode retNode = null;
    

    RBBISymbolTableEntry el = (RBBISymbolTableEntry)fHashTable.get(key);
    if (el != null) {
      retNode = val;
    }
    return retNode;
  }
  






  void addEntry(String key, RBBINode val)
  {
    RBBISymbolTableEntry e = (RBBISymbolTableEntry)fHashTable.get(key);
    if (e != null) {
      fRuleScanner.error(66055);
      return;
    }
    
    e = new RBBISymbolTableEntry();
    key = key;
    val = val;
    fHashTable.put(key, e);
  }
  



  void rbbiSymtablePrint()
  {
    System.out.print("Variable Definitions\nName               Node Val     String Val\n----------------------------------------------------------------------\n");
    



    RBBISymbolTableEntry[] syms = (RBBISymbolTableEntry[])fHashTable.values().toArray(new RBBISymbolTableEntry[0]);
    
    for (int i = 0; i < syms.length; i++) {
      RBBISymbolTableEntry s = syms[i];
      
      System.out.print("  " + key + "  ");
      System.out.print("  " + val + "  ");
      System.out.print(val.fLeftChild.fText);
      System.out.print("\n");
    }
    
    System.out.println("\nParsed Variable Definitions\n");
    for (int i = 0; i < syms.length; i++) {
      RBBISymbolTableEntry s = syms[i];
      System.out.print(key);
      val.fLeftChild.printTree(true);
      System.out.print("\n");
    }
  }
  
  static class RBBISymbolTableEntry
  {
    String key;
    RBBINode val;
    
    RBBISymbolTableEntry() {}
  }
}
