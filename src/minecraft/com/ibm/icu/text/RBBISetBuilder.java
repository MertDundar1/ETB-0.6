package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.IntTrieBuilder;
import com.ibm.icu.impl.TrieBuilder.DataManipulate;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


















class RBBISetBuilder
{
  RBBIRuleBuilder fRB;
  RangeDescriptor fRangeList;
  IntTrieBuilder fTrie;
  int fTrieSize;
  int fGroupCount;
  boolean fSawBOF;
  
  static class RangeDescriptor
  {
    int fStartChar;
    int fEndChar;
    int fNum;
    List<RBBINode> fIncludesSets;
    RangeDescriptor fNext;
    
    RangeDescriptor()
    {
      fIncludesSets = new ArrayList();
    }
    
    RangeDescriptor(RangeDescriptor other) {
      fStartChar = fStartChar;
      fEndChar = fEndChar;
      fNum = fNum;
      fIncludesSets = new ArrayList(fIncludesSets);
    }
    




    void split(int where)
    {
      Assert.assrt((where > fStartChar) && (where <= fEndChar));
      RangeDescriptor nr = new RangeDescriptor(this);
      


      fStartChar = where;
      fEndChar = (where - 1);
      fNext = fNext;
      fNext = nr;
    }
    
























    void setDictionaryFlag()
    {
      for (int i = 0; i < fIncludesSets.size(); i++) {
        RBBINode usetNode = (RBBINode)fIncludesSets.get(i);
        String setName = "";
        RBBINode setRef = fParent;
        if (setRef != null) {
          RBBINode varRef = fParent;
          if ((varRef != null) && (fType == 2)) {
            setName = fText;
          }
        }
        if (setName.equals("dictionary")) {
          fNum |= 0x4000;
          break;
        }
      }
    }
  }
  
























  RBBISetBuilder(RBBIRuleBuilder rb)
  {
    fRB = rb;
  }
  








  void build()
  {
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("usets") >= 0)) { printSets();
    }
    


    fRangeList = new RangeDescriptor();
    fRangeList.fStartChar = 0;
    fRangeList.fEndChar = 1114111;
    



    for (RBBINode usetNode : fRB.fUSetNodes) {
      UnicodeSet inputSet = fInputSet;
      int inputSetRangeCount = inputSet.getRangeCount();
      int inputSetRangeIndex = 0;
      RangeDescriptor rlRange = fRangeList;
      

      while (inputSetRangeIndex < inputSetRangeCount)
      {

        int inputSetRangeBegin = inputSet.getRangeStart(inputSetRangeIndex);
        int inputSetRangeEnd = inputSet.getRangeEnd(inputSetRangeIndex);
        


        while (fEndChar < inputSetRangeBegin) {
          rlRange = fNext;
        }
        






        if (fStartChar < inputSetRangeBegin) {
          rlRange.split(inputSetRangeBegin);



        }
        else
        {


          if (fEndChar > inputSetRangeEnd) {
            rlRange.split(inputSetRangeEnd + 1);
          }
          


          if (fIncludesSets.indexOf(usetNode) == -1) {
            fIncludesSets.add(usetNode);
          }
          

          if (inputSetRangeEnd == fEndChar) {
            inputSetRangeIndex++;
          }
          rlRange = fNext;
        }
      }
    }
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("range") >= 0)) { printRanges();
    }
    











    for (RangeDescriptor rlRange = fRangeList; rlRange != null; rlRange = fNext) {
      for (RangeDescriptor rlSearchRange = fRangeList; rlSearchRange != rlRange; rlSearchRange = fNext) {
        if (fIncludesSets.equals(fIncludesSets)) {
          fNum = fNum;
          break;
        }
      }
      if (fNum == 0) {
        fGroupCount += 1;
        fNum = (fGroupCount + 2);
        rlRange.setDictionaryFlag();
        addValToSets(fIncludesSets, fGroupCount + 2);
      }
    }
    










    String eofString = "eof";
    String bofString = "bof";
    
    for (RBBINode usetNode : fRB.fUSetNodes) {
      UnicodeSet inputSet = fInputSet;
      if (inputSet.contains(eofString)) {
        addValToSet(usetNode, 1);
      }
      if (inputSet.contains(bofString)) {
        addValToSet(usetNode, 2);
        fSawBOF = true;
      }
    }
    

    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("rgroup") >= 0)) printRangeGroups();
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("esets") >= 0)) { printSets();
    }
    




    fTrie = new IntTrieBuilder(null, 100000, 0, 0, true);
    




    for (rlRange = fRangeList; rlRange != null; rlRange = fNext) {
      fTrie.setRange(fStartChar, fEndChar + 1, fNum, true);
    }
  }
  



  class RBBIDataManipulate
    implements TrieBuilder.DataManipulate
  {
    RBBIDataManipulate() {}
    


    public int getFoldedValue(int start, int offset)
    {
      boolean[] inBlockZero = new boolean[1];
      
      int limit = start + 1024;
      while (start < limit) {
        int value = fTrie.getValue(start, inBlockZero);
        if (inBlockZero[0] != 0) {
          start += 32;
        } else { if (value != 0) {
            return offset | 0x8000;
          }
          start++;
        }
      }
      return 0;
    } }
  
  RBBIDataManipulate dm = new RBBIDataManipulate();
  




  int getTrieSize()
  {
    int size = 0;
    
    try
    {
      size = fTrie.serialize(null, true, dm);
    } catch (IOException e) {
      Assert.assrt(false);
    }
    return size;
  }
  




  void serializeTrie(OutputStream os)
    throws IOException
  {
    fTrie.serialize(os, true, dm);
  }
  













  void addValToSets(List<RBBINode> sets, int val)
  {
    for (RBBINode usetNode : sets) {
      addValToSet(usetNode, val);
    }
  }
  
  void addValToSet(RBBINode usetNode, int val) {
    RBBINode leafNode = new RBBINode(3);
    fVal = val;
    if (fLeftChild == null) {
      fLeftChild = leafNode;
      fParent = usetNode;

    }
    else
    {
      RBBINode orNode = new RBBINode(9);
      fLeftChild = fLeftChild;
      fRightChild = leafNode;
      fLeftChild.fParent = orNode;
      fRightChild.fParent = orNode;
      fLeftChild = orNode;
      fParent = usetNode;
    }
  }
  





  int getNumCharCategories()
  {
    return fGroupCount + 3;
  }
  





  boolean sawBOF()
  {
    return fSawBOF;
  }
  







  int getFirstChar(int category)
  {
    int retVal = -1;
    for (RangeDescriptor rlRange = fRangeList; rlRange != null; rlRange = fNext) {
      if (fNum == category) {
        retVal = fStartChar;
        break;
      }
    }
    return retVal;
  }
  











  void printRanges()
  {
    System.out.print("\n\n Nonoverlapping Ranges ...\n");
    for (RangeDescriptor rlRange = fRangeList; rlRange != null; rlRange = fNext) {
      System.out.print(" " + fNum + "   " + fStartChar + "-" + fEndChar);
      
      for (int i = 0; i < fIncludesSets.size(); i++) {
        RBBINode usetNode = (RBBINode)fIncludesSets.get(i);
        String setName = "anon";
        RBBINode setRef = fParent;
        if (setRef != null) {
          RBBINode varRef = fParent;
          if ((varRef != null) && (fType == 2)) {
            setName = fText;
          }
        }
        System.out.print(setName);System.out.print("  ");
      }
      System.out.println("");
    }
  }
  











  void printRangeGroups()
  {
    int lastPrintedGroupNum = 0;
    
    System.out.print("\nRanges grouped by Unicode Set Membership...\n");
    for (RangeDescriptor rlRange = fRangeList; rlRange != null; rlRange = fNext) {
      int groupNum = fNum & 0xBFFF;
      if (groupNum > lastPrintedGroupNum) {
        lastPrintedGroupNum = groupNum;
        if (groupNum < 10) System.out.print(" ");
        System.out.print(groupNum + " ");
        
        if ((fNum & 0x4000) != 0) { System.out.print(" <DICT> ");
        }
        for (int i = 0; i < fIncludesSets.size(); i++) {
          RBBINode usetNode = (RBBINode)fIncludesSets.get(i);
          String setName = "anon";
          RBBINode setRef = fParent;
          if (setRef != null) {
            RBBINode varRef = fParent;
            if ((varRef != null) && (fType == 2)) {
              setName = fText;
            }
          }
          System.out.print(setName);System.out.print(" ");
        }
        
        i = 0;
        for (RangeDescriptor tRange = rlRange; tRange != null; tRange = fNext) {
          if (fNum == fNum) {
            if (i++ % 5 == 0) {
              System.out.print("\n    ");
            }
            RBBINode.printHex(fStartChar, -1);
            System.out.print("-");
            RBBINode.printHex(fEndChar, 0);
          }
        }
        System.out.print("\n");
      }
    }
    System.out.print("\n");
  }
  









  void printSets()
  {
    System.out.print("\n\nUnicode Sets List\n------------------\n");
    for (int i = 0; i < fRB.fUSetNodes.size(); i++)
    {




      RBBINode usetNode = (RBBINode)fRB.fUSetNodes.get(i);
      

      RBBINode.printInt(2, i);
      String setName = "anonymous";
      RBBINode setRef = fParent;
      if (setRef != null) {
        RBBINode varRef = fParent;
        if ((varRef != null) && (fType == 2)) {
          setName = fText;
        }
      }
      System.out.print("  " + setName);
      System.out.print("   ");
      System.out.print(fText);
      System.out.print("\n");
      if (fLeftChild != null) {
        fLeftChild.printTree(true);
      }
    }
    System.out.print("\n");
  }
}
