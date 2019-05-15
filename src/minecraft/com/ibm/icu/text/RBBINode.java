package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;













class RBBINode
{
  static final int setRef = 0;
  static final int uset = 1;
  static final int varRef = 2;
  static final int leafChar = 3;
  static final int lookAhead = 4;
  static final int tag = 5;
  static final int endMark = 6;
  static final int opStart = 7;
  static final int opCat = 8;
  static final int opOr = 9;
  static final int opStar = 10;
  static final int opPlus = 11;
  static final int opQuestion = 12;
  static final int opBreak = 13;
  static final int opReverse = 14;
  static final int opLParen = 15;
  static final int nodeTypeLimit = 16;
  static final String[] nodeTypeNames = { "setRef", "uset", "varRef", "leafChar", "lookAhead", "tag", "endMark", "opStart", "opCat", "opOr", "opStar", "opPlus", "opQuestion", "opBreak", "opReverse", "opLParen" };
  

  static final int precZero = 0;
  

  static final int precStart = 1;
  

  static final int precLParen = 2;
  

  static final int precOpOr = 3;
  

  static final int precOpCat = 4;
  

  int fType;
  

  RBBINode fParent;
  

  RBBINode fLeftChild;
  

  RBBINode fRightChild;
  
  UnicodeSet fInputSet;
  
  int fPrecedence = 0;
  

  String fText;
  

  int fFirstPos;
  

  int fLastPos;
  

  boolean fNullable;
  
  int fVal;
  
  boolean fLookAheadEnd;
  
  Set<RBBINode> fFirstPosSet;
  
  Set<RBBINode> fLastPosSet;
  
  Set<RBBINode> fFollowPos;
  
  int fSerialNum;
  
  static int gLastSerial;
  

  RBBINode(int t)
  {
    Assert.assrt(t < 16);
    fSerialNum = (++gLastSerial);
    fType = t;
    
    fFirstPosSet = new HashSet();
    fLastPosSet = new HashSet();
    fFollowPos = new HashSet();
    if (t == 8) {
      fPrecedence = 4;
    } else if (t == 9) {
      fPrecedence = 3;
    } else if (t == 7) {
      fPrecedence = 1;
    } else if (t == 15) {
      fPrecedence = 2;
    } else {
      fPrecedence = 0;
    }
  }
  
  RBBINode(RBBINode other) {
    fSerialNum = (++gLastSerial);
    fType = fType;
    fInputSet = fInputSet;
    fPrecedence = fPrecedence;
    fText = fText;
    fFirstPos = fFirstPos;
    fLastPos = fLastPos;
    fNullable = fNullable;
    fVal = fVal;
    fFirstPosSet = new HashSet(fFirstPosSet);
    fLastPosSet = new HashSet(fLastPosSet);
    fFollowPos = new HashSet(fFollowPos);
  }
  



  RBBINode cloneTree()
  {
    RBBINode n;
    


    RBBINode n;
    

    if (fType == 2)
    {

      n = fLeftChild.cloneTree(); } else { RBBINode n;
      if (fType == 1) {
        n = this;
      } else {
        n = new RBBINode(this);
        if (fLeftChild != null) {
          fLeftChild = fLeftChild.cloneTree();
          fLeftChild.fParent = n;
        }
        if (fRightChild != null) {
          fRightChild = fRightChild.cloneTree();
          fRightChild.fParent = n;
        }
      } }
    return n;
  }
  



















  RBBINode flattenVariables()
  {
    if (fType == 2) {
      RBBINode retNode = fLeftChild.cloneTree();
      
      return retNode;
    }
    
    if (fLeftChild != null) {
      fLeftChild = fLeftChild.flattenVariables();
      fLeftChild.fParent = this;
    }
    if (fRightChild != null) {
      fRightChild = fRightChild.flattenVariables();
      fRightChild.fParent = this;
    }
    return this;
  }
  







  void flattenSets()
  {
    Assert.assrt(fType != 0);
    
    if (fLeftChild != null) {
      if (fLeftChild.fType == 0) {
        RBBINode setRefNode = fLeftChild;
        RBBINode usetNode = fLeftChild;
        RBBINode replTree = fLeftChild;
        fLeftChild = replTree.cloneTree();
        fLeftChild.fParent = this;
      } else {
        fLeftChild.flattenSets();
      }
    }
    
    if (fRightChild != null) {
      if (fRightChild.fType == 0) {
        RBBINode setRefNode = fRightChild;
        RBBINode usetNode = fLeftChild;
        RBBINode replTree = fLeftChild;
        fRightChild = replTree.cloneTree();
        fRightChild.fParent = this;
      }
      else {
        fRightChild.flattenSets();
      }
    }
  }
  





  void findNodes(List<RBBINode> dest, int kind)
  {
    if (fType == kind) {
      dest.add(this);
    }
    if (fLeftChild != null) {
      fLeftChild.findNodes(dest, kind);
    }
    if (fRightChild != null) {
      fRightChild.findNodes(dest, kind);
    }
  }
  








  static void printNode(RBBINode n)
  {
    if (n == null) {
      System.out.print(" -- null --\n");
    } else {
      printInt(fSerialNum, 10);
      printString(nodeTypeNames[fType], 11);
      printInt(fParent == null ? 0 : fParent.fSerialNum, 11);
      printInt(fLeftChild == null ? 0 : fLeftChild.fSerialNum, 11);
      printInt(fRightChild == null ? 0 : fRightChild.fSerialNum, 12);
      printInt(fFirstPos, 12);
      printInt(fVal, 7);
      
      if (fType == 2) {
        System.out.print(" " + fText);
      }
    }
    System.out.println("");
  }
  




  static void printString(String s, int minWidth)
  {
    for (int i = minWidth; i < 0; i++)
    {
      System.out.print(' ');
    }
    for (int i = s.length(); i < minWidth; i++) {
      System.out.print(' ');
    }
    System.out.print(s);
  }
  





  static void printInt(int i, int minWidth)
  {
    String s = Integer.toString(i);
    printString(s, Math.max(minWidth, s.length() + 1));
  }
  

  static void printHex(int i, int minWidth)
  {
    String s = Integer.toString(i, 16);
    String leadingZeroes = "00000".substring(0, Math.max(0, 5 - s.length()));
    
    s = leadingZeroes + s;
    printString(s, minWidth);
  }
  







  void printTree(boolean printHeading)
  {
    if (printHeading) {
      System.out.println("-------------------------------------------------------------------");
      System.out.println("    Serial       type     Parent  LeftChild  RightChild    position  value");
    }
    printNode(this);
    

    if (fType != 2) {
      if (fLeftChild != null) {
        fLeftChild.printTree(false);
      }
      
      if (fRightChild != null) {
        fRightChild.printTree(false);
      }
    }
  }
}
