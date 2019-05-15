package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import java.io.PrintStream;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.List;



























class RBBIRuleScanner
{
  private static final int kStackSize = 100;
  RBBIRuleBuilder fRB;
  int fScanIndex;
  int fNextIndex;
  boolean fQuoteMode;
  int fLineNum;
  int fCharNum;
  int fLastChar;
  RBBIRuleChar fC = new RBBIRuleChar();
  

  String fVarName;
  

  short[] fStack = new short[100];
  
  int fStackPtr;
  
  RBBINode[] fNodeStack = new RBBINode[100];
  

  int fNodeStackPtr;
  

  boolean fReverseRule;
  

  boolean fLookAheadRule;
  

  RBBISymbolTable fSymbolTable;
  

  HashMap<String, RBBISetTableEl> fSetTable = new HashMap();
  



  UnicodeSet[] fRuleSets = new UnicodeSet[10];
  



  int fRuleNum;
  


  int fOptionStart;
  


  private static String gRuleSet_rule_char_pattern = "[^[\\p{Z}\\u0020-\\u007f]-[\\p{L}]-[\\p{N}]]";
  private static String gRuleSet_name_char_pattern = "[_\\p{L}\\p{N}]";
  private static String gRuleSet_digit_char_pattern = "[0-9]";
  private static String gRuleSet_name_start_char_pattern = "[_\\p{L}]";
  private static String gRuleSet_white_space_pattern = "[\\p{Pattern_White_Space}]";
  private static String kAny = "any";
  

  static final int chNEL = 133;
  

  static final int chLS = 8232;
  

  RBBIRuleScanner(RBBIRuleBuilder rb)
  {
    fRB = rb;
    fLineNum = 1;
    




    fRuleSets[3] = new UnicodeSet(gRuleSet_rule_char_pattern);
    fRuleSets[4] = new UnicodeSet(gRuleSet_white_space_pattern);
    fRuleSets[1] = new UnicodeSet(gRuleSet_name_char_pattern);
    fRuleSets[2] = new UnicodeSet(gRuleSet_name_start_char_pattern);
    fRuleSets[0] = new UnicodeSet(gRuleSet_digit_char_pattern);
    
    fSymbolTable = new RBBISymbolTable(this, fRules);
  }
  







  boolean doParseActions(int action)
  {
    RBBINode n = null;
    
    boolean returnVal = true;
    
    switch (action)
    {
    case 11: 
      pushNewNode(7);
      fRuleNum += 1;
      break;
    
    case 9: 
      fixOpStack(4);
      RBBINode operandNode = fNodeStack[(fNodeStackPtr--)];
      RBBINode orNode = pushNewNode(9);
      fLeftChild = operandNode;
      fParent = orNode;
      
      break;
    






    case 7: 
      fixOpStack(4);
      RBBINode operandNode = fNodeStack[(fNodeStackPtr--)];
      RBBINode catNode = pushNewNode(8);
      fLeftChild = operandNode;
      fParent = catNode;
      
      break;
    






    case 12: 
      pushNewNode(15);
      break;
    
    case 10: 
      fixOpStack(2);
      break;
    




    case 13: 
      break;
    





    case 22: 
      n = fNodeStack[(fNodeStackPtr - 1)];
      fFirstPos = fNextIndex;
      


      pushNewNode(7);
      break;
    





    case 3: 
      fixOpStack(1);
      
      RBBINode startExprNode = fNodeStack[(fNodeStackPtr - 2)];
      RBBINode varRefNode = fNodeStack[(fNodeStackPtr - 1)];
      RBBINode RHSExprNode = fNodeStack[fNodeStackPtr];
      



      fFirstPos = fFirstPos;
      fLastPos = fScanIndex;
      

      fText = fRB.fRules.substring(fFirstPos, fLastPos);
      



      fLeftChild = RHSExprNode;
      fParent = varRefNode;
      

      fSymbolTable.addEntry(fText, varRefNode);
      

      fNodeStackPtr -= 3;
      break;
    

    case 4: 
      fixOpStack(1);
      

      if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("rtree") >= 0)) {
        printNodeStack("end of rule");
      }
      Assert.assrt(fNodeStackPtr == 1);
      


      if (fLookAheadRule) {
        RBBINode thisRule = fNodeStack[fNodeStackPtr];
        RBBINode endNode = pushNewNode(6);
        RBBINode catNode = pushNewNode(8);
        fNodeStackPtr -= 2;
        fLeftChild = thisRule;
        fRightChild = endNode;
        fNodeStack[fNodeStackPtr] = catNode;
        fVal = fRuleNum;
        fLookAheadEnd = true;
      }
      










      int destRules = fReverseRule ? 1 : fRB.fDefaultTree;
      
      if (fRB.fTreeRoots[destRules] != null)
      {




        RBBINode thisRule = fNodeStack[fNodeStackPtr];
        RBBINode prevRules = fRB.fTreeRoots[destRules];
        RBBINode orNode = pushNewNode(9);
        fLeftChild = prevRules;
        fParent = orNode;
        fRightChild = thisRule;
        fParent = orNode;
        fRB.fTreeRoots[destRules] = orNode;
      }
      else
      {
        fRB.fTreeRoots[destRules] = fNodeStack[fNodeStackPtr];
      }
      fReverseRule = false;
      fLookAheadRule = false;
      fNodeStackPtr = 0;
      
      break;
    
    case 18: 
      error(66052);
      returnVal = false;
      break;
    
    case 31: 
      error(66052);
      break;
    






    case 28: 
      RBBINode operandNode = fNodeStack[(fNodeStackPtr--)];
      RBBINode plusNode = pushNewNode(11);
      fLeftChild = operandNode;
      fParent = plusNode;
      
      break;
    
    case 29: 
      RBBINode operandNode = fNodeStack[(fNodeStackPtr--)];
      RBBINode qNode = pushNewNode(12);
      fLeftChild = operandNode;
      fParent = qNode;
      
      break;
    
    case 30: 
      RBBINode operandNode = fNodeStack[(fNodeStackPtr--)];
      RBBINode starNode = pushNewNode(10);
      fLeftChild = operandNode;
      fParent = starNode;
      
      break;
    







    case 17: 
      n = pushNewNode(0);
      String s = String.valueOf((char)fC.fChar);
      findSetFor(s, n, null);
      fFirstPos = fScanIndex;
      fLastPos = fNextIndex;
      fText = fRB.fRules.substring(fFirstPos, fLastPos);
      break;
    



    case 2: 
      n = pushNewNode(0);
      findSetFor(kAny, n, null);
      fFirstPos = fScanIndex;
      fLastPos = fNextIndex;
      fText = fRB.fRules.substring(fFirstPos, fLastPos);
      break;
    



    case 21: 
      n = pushNewNode(4);
      fVal = fRuleNum;
      fFirstPos = fScanIndex;
      fLastPos = fNextIndex;
      fText = fRB.fRules.substring(fFirstPos, fLastPos);
      fLookAheadRule = true;
      break;
    


    case 23: 
      n = pushNewNode(5);
      fVal = 0;
      fFirstPos = fScanIndex;
      fLastPos = fNextIndex;
      break;
    


    case 25: 
      n = fNodeStack[fNodeStackPtr];
      int v = UCharacter.digit((char)fC.fChar, 10);
      fVal = (fVal * 10 + v);
      break;
    

    case 27: 
      n = fNodeStack[fNodeStackPtr];
      fLastPos = fNextIndex;
      fText = fRB.fRules.substring(fFirstPos, fLastPos);
      break;
    
    case 26: 
      error(66062);
      returnVal = false;
      break;
    

    case 15: 
      fOptionStart = fScanIndex;
      break;
    
    case 14: 
      String opt = fRB.fRules.substring(fOptionStart, fScanIndex);
      if (opt.equals("chain")) {
        fRB.fChainRules = true;
      } else if (opt.equals("LBCMNoChain")) {
        fRB.fLBCMNoChain = true;
      } else if (opt.equals("forward")) {
        fRB.fDefaultTree = 0;
      } else if (opt.equals("reverse")) {
        fRB.fDefaultTree = 1;
      } else if (opt.equals("safe_forward")) {
        fRB.fDefaultTree = 2;
      } else if (opt.equals("safe_reverse")) {
        fRB.fDefaultTree = 3;
      } else if (opt.equals("lookAheadHardBreak")) {
        fRB.fLookAheadHardBreak = true;
      } else {
        error(66061);
      }
      break;
    

    case 16: 
      fReverseRule = true;
      break;
    
    case 24: 
      n = pushNewNode(2);
      fFirstPos = fScanIndex;
      break;
    
    case 5: 
      n = fNodeStack[fNodeStackPtr];
      if ((n == null) || (fType != 2)) {
        error(66049);
      }
      else {
        fLastPos = fScanIndex;
        fText = fRB.fRules.substring(fFirstPos + 1, fLastPos);
        






        fLeftChild = fSymbolTable.lookupNode(fText); }
      break;
    
    case 1: 
      n = fNodeStack[fNodeStackPtr];
      if (fLeftChild == null) {
        error(66058);
        returnVal = false;
      }
      

      break;
    case 8: 
      break;
    case 19: 
      error(66054);
      returnVal = false;
      break;
    
    case 6: 
      returnVal = false;
      break;
    
    case 20: 
      scanSet();
      break;
    
    default: 
      error(66049);
      returnVal = false;
    }
    
    return returnVal;
  }
  





  void error(int e)
  {
    String s = "Error " + e + " at line " + fLineNum + " column " + fCharNum;
    
    IllegalArgumentException ex = new IllegalArgumentException(s);
    throw ex;
  }
  








  void fixOpStack(int p)
  {
    RBBINode n;
    







    for (;;)
    {
      n = fNodeStack[(fNodeStackPtr - 1)];
      if (fPrecedence == 0) {
        System.out.print("RBBIRuleScanner.fixOpStack, bad operator node");
        error(66049);
        return;
      }
      
      if ((fPrecedence < p) || (fPrecedence <= 2)) {
        break;
      }
      




      fRightChild = fNodeStack[fNodeStackPtr];
      fNodeStack[fNodeStackPtr].fParent = n;
      fNodeStackPtr -= 1;
    }
    

    if (p <= 2)
    {




      if (fPrecedence != p)
      {

        error(66056);
      }
      fNodeStack[(fNodeStackPtr - 1)] = fNodeStack[fNodeStackPtr];
      fNodeStackPtr -= 1;
    }
  }
  










































  void findSetFor(String s, RBBINode node, UnicodeSet setToAdopt)
  {
    RBBISetTableEl el = (RBBISetTableEl)fSetTable.get(s);
    if (el != null) {
      fLeftChild = val;
      Assert.assrt(fLeftChild.fType == 1);
      return;
    }
    



    if (setToAdopt == null) {
      if (s.equals(kAny)) {
        setToAdopt = new UnicodeSet(0, 1114111);
      }
      else {
        int c = UTF16.charAt(s, 0);
        setToAdopt = new UnicodeSet(c, c);
      }
    }
    





    RBBINode usetNode = new RBBINode(1);
    fInputSet = setToAdopt;
    fParent = node;
    fLeftChild = usetNode;
    fText = s;
    



    fRB.fUSetNodes.add(usetNode);
    



    el = new RBBISetTableEl();
    key = s;
    val = usetNode;
    fSetTable.put(key, el);
  }
  
















  static String stripRules(String rules)
  {
    StringBuilder strippedRules = new StringBuilder();
    int rulesLength = rules.length();
    for (int idx = 0; idx < rulesLength;) {
      char ch = rules.charAt(idx++);
      if (ch == '#')
      {
        while ((idx < rulesLength) && (ch != '\r') && (ch != '\n') && (ch != '')) {
          ch = rules.charAt(idx++);
        }
      }
      if (!UCharacter.isISOControl(ch)) {
        strippedRules.append(ch);
      }
    }
    return strippedRules.toString();
  }
  








  int nextCharLL()
  {
    if (fNextIndex >= fRB.fRules.length()) {
      return -1;
    }
    int ch = UTF16.charAt(fRB.fRules, fNextIndex);
    fNextIndex = UTF16.moveCodePointOffset(fRB.fRules, fNextIndex, 1);
    
    if ((ch == 13) || (ch == 133) || (ch == 8232) || ((ch == 10) && (fLastChar != 13)))
    {




      fLineNum += 1;
      fCharNum = 0;
      if (fQuoteMode) {
        error(66057);
        fQuoteMode = false;
      }
      

    }
    else if (ch != 10) {
      fCharNum += 1;
    }
    
    fLastChar = ch;
    return ch;
  }
  










  void nextChar(RBBIRuleChar c)
  {
    fScanIndex = fNextIndex;
    fChar = nextCharLL();
    fEscaped = false;
    




    if (fChar == 39) {
      if (UTF16.charAt(fRB.fRules, fNextIndex) == 39) {
        fChar = nextCharLL();
        fEscaped = true;

      }
      else
      {
        fQuoteMode = (!fQuoteMode);
        if (fQuoteMode == true) {
          fChar = 40;
        } else {
          fChar = 41;
        }
        fEscaped = false;
        return;
      }
    }
    
    if (fQuoteMode) {
      fEscaped = true;
    }
    else
    {
      if (fChar == 35)
      {

        for (;;)
        {


          fChar = nextCharLL();
          if ((fChar != -1) && (fChar != 13) && (fChar != 10) && (fChar != 133)) { if (fChar == 8232) {
              break;
            }
          }
        }
      }
      



      if (fChar == -1) {
        return;
      }
      




      if (fChar == 92) {
        fEscaped = true;
        int[] unescapeIndex = new int[1];
        unescapeIndex[0] = fNextIndex;
        fChar = Utility.unescapeAt(fRB.fRules, unescapeIndex);
        if (unescapeIndex[0] == fNextIndex) {
          error(66050);
        }
        
        fCharNum += unescapeIndex[0] - fNextIndex;
        fNextIndex = unescapeIndex[0];
      }
    }
  }
  











  void parse()
  {
    int state = 1;
    nextChar(fC);
    










    while (state != 0)
    {








      RBBIRuleParseTable.RBBIRuleTableElement tableEl = RBBIRuleParseTable.gRuleParseStateTable[state];
      if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("scan") >= 0)) {
        System.out.println("char, line, col = ('" + (char)fC.fChar + "', " + fLineNum + ", " + fCharNum + "    state = " + fStateName);
      }
      


      for (int tableRow = state;; tableRow++) {
        tableEl = RBBIRuleParseTable.gRuleParseStateTable[tableRow];
        if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("scan") >= 0)) {
          System.out.print(".");
        }
        if ((fCharClass < 127) && (!fC.fEscaped) && (fCharClass == fC.fChar)) {
          break;
        }
        



        if (fCharClass == 255) {
          break;
        }
        
        if ((fCharClass == 254) && (fC.fEscaped)) {
          break;
        }
        
        if ((fCharClass == 253) && (fC.fEscaped) && ((fC.fChar == 80) || (fC.fChar == 112))) {
          break;
        }
        

        if ((fCharClass == 252) && (fC.fChar == -1)) {
          break;
        }
        

        if ((fCharClass >= 128) && (fCharClass < 240) && (!fC.fEscaped) && (fC.fChar != -1))
        {

          UnicodeSet uniset = fRuleSets[(fCharClass - 128)];
          if (uniset.contains(fC.fChar)) {
            break;
          }
        }
      }
      


      if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("scan") >= 0)) {
        System.out.println("");
      }
      



      if (!doParseActions(fAction)) {
        break;
      }
      



      if (fPushState != 0) {
        fStackPtr += 1;
        if (fStackPtr >= 100) {
          System.out.println("RBBIRuleScanner.parse() - state stack overflow.");
          error(66049);
        }
        fStack[fStackPtr] = fPushState;
      }
      
      if (fNextChar) {
        nextChar(fC);
      }
      


      if (fNextState != 255) {
        state = fNextState;
      } else {
        state = fStack[fStackPtr];
        fStackPtr -= 1;
        if (fStackPtr < 0) {
          System.out.println("RBBIRuleScanner.parse() - state stack underflow.");
          error(66049);
        }
      }
    }
    




    if (fRB.fTreeRoots[1] == null) {
      fRB.fTreeRoots[1] = pushNewNode(10);
      RBBINode operand = pushNewNode(0);
      findSetFor(kAny, operand, null);
      fRB.fTreeRoots[1].fLeftChild = operand;
      fParent = fRB.fTreeRoots[1];
      fNodeStackPtr -= 2;
    }
    





    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("symbols") >= 0)) {
      fSymbolTable.rbbiSymtablePrint();
    }
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("ptree") >= 0)) {
      System.out.println("Completed Forward Rules Parse Tree...");
      fRB.fTreeRoots[0].printTree(true);
      System.out.println("\nCompleted Reverse Rules Parse Tree...");
      fRB.fTreeRoots[1].printTree(true);
      System.out.println("\nCompleted Safe Point Forward Rules Parse Tree...");
      if (fRB.fTreeRoots[2] == null) {
        System.out.println("  -- null -- ");
      } else {
        fRB.fTreeRoots[2].printTree(true);
      }
      System.out.println("\nCompleted Safe Point Reverse Rules Parse Tree...");
      if (fRB.fTreeRoots[3] == null) {
        System.out.println("  -- null -- ");
      } else {
        fRB.fTreeRoots[3].printTree(true);
      }
    }
  }
  






  void printNodeStack(String title)
  {
    System.out.println(title + ".  Dumping node stack...\n");
    for (int i = fNodeStackPtr; i > 0; i--) {
      fNodeStack[i].printTree(true);
    }
  }
  






  RBBINode pushNewNode(int nodeType)
  {
    fNodeStackPtr += 1;
    if (fNodeStackPtr >= 100) {
      System.out.println("RBBIRuleScanner.pushNewNode - stack overflow.");
      error(66049);
    }
    fNodeStack[fNodeStackPtr] = new RBBINode(nodeType);
    return fNodeStack[fNodeStackPtr];
  }
  













  void scanSet()
  {
    UnicodeSet uset = null;
    
    ParsePosition pos = new ParsePosition(fScanIndex);
    

    int startPos = fScanIndex;
    try {
      uset = new UnicodeSet(fRB.fRules, pos, fSymbolTable, 1);
    }
    catch (Exception e) {
      error(66063);
    }
    


    if (uset.isEmpty())
    {




      error(66060);
    }
    



    int i = pos.getIndex();
    
    while (fNextIndex < i)
    {

      nextCharLL();
    }
    


    RBBINode n = pushNewNode(0);
    fFirstPos = startPos;
    fLastPos = fNextIndex;
    fText = fRB.fRules.substring(fFirstPos, fLastPos);
    





    findSetFor(fText, n, uset);
  }
  
  static class RBBISetTableEl
  {
    String key;
    RBBINode val;
    
    RBBISetTableEl() {}
  }
  
  static class RBBIRuleChar
  {
    int fChar;
    boolean fEscaped;
    
    RBBIRuleChar() {}
  }
}
