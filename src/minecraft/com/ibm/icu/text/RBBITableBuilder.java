package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.lang.UCharacter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;





















class RBBITableBuilder
{
  private RBBIRuleBuilder fRB;
  private int fRootIx;
  private List<RBBIStateDescriptor> fDStates;
  
  static class RBBIStateDescriptor
  {
    boolean fMarked;
    int fAccepting;
    int fLookAhead;
    SortedSet<Integer> fTagVals;
    int fTagsIdx;
    Set<RBBINode> fPositions;
    int[] fDtran;
    
    RBBIStateDescriptor(int maxInputSymbol)
    {
      fTagVals = new TreeSet();
      fPositions = new HashSet();
      fDtran = new int[maxInputSymbol + 1];
    }
  }
  



















  RBBITableBuilder(RBBIRuleBuilder rb, int rootNodeIx)
  {
    fRootIx = rootNodeIx;
    fRB = rb;
    fDStates = new ArrayList();
  }
  










  void build()
  {
    if (fRB.fTreeRoots[fRootIx] == null) {
      return;
    }
    




    fRB.fTreeRoots[fRootIx] = fRB.fTreeRoots[fRootIx].flattenVariables();
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("ftree") >= 0)) {
      System.out.println("Parse tree after flattening variable references.");
      fRB.fTreeRoots[fRootIx].printTree(true);
    }
    






    if (fRB.fSetBuilder.sawBOF()) {
      RBBINode bofTop = new RBBINode(8);
      RBBINode bofLeaf = new RBBINode(3);
      fLeftChild = bofLeaf;
      fRightChild = fRB.fTreeRoots[fRootIx];
      fParent = bofTop;
      fVal = 2;
      fRB.fTreeRoots[fRootIx] = bofTop;
    }
    





    RBBINode cn = new RBBINode(8);
    fLeftChild = fRB.fTreeRoots[fRootIx];
    fRB.fTreeRoots[fRootIx].fParent = cn;
    fRightChild = new RBBINode(6);
    fRightChild.fParent = cn;
    fRB.fTreeRoots[fRootIx] = cn;
    




    fRB.fTreeRoots[fRootIx].flattenSets();
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("stree") >= 0)) {
      System.out.println("Parse tree after flattening Unicode Set references.");
      fRB.fTreeRoots[fRootIx].printTree(true);
    }
    








    calcNullable(fRB.fTreeRoots[fRootIx]);
    calcFirstPos(fRB.fTreeRoots[fRootIx]);
    calcLastPos(fRB.fTreeRoots[fRootIx]);
    calcFollowPos(fRB.fTreeRoots[fRootIx]);
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("pos") >= 0)) {
      System.out.print("\n");
      printPosSets(fRB.fTreeRoots[fRootIx]);
    }
    



    if (fRB.fChainRules) {
      calcChainedFollowPos(fRB.fTreeRoots[fRootIx]);
    }
    



    if (fRB.fSetBuilder.sawBOF()) {
      bofFixup();
    }
    



    buildStateTable();
    flagAcceptingStates();
    flagLookAheadStates();
    flagTaggedStates();
    





    mergeRuleStatusVals();
    
    if ((fRB.fDebugEnv != null) && (fRB.fDebugEnv.indexOf("states") >= 0)) { printStates();
    }
  }
  





  void calcNullable(RBBINode n)
  {
    if (n == null) {
      return;
    }
    if ((fType == 0) || (fType == 6))
    {

      fNullable = false;
      return;
    }
    
    if ((fType == 4) || (fType == 5))
    {

      fNullable = true;
      return;
    }
    



    calcNullable(fLeftChild);
    calcNullable(fRightChild);
    

    if (fType == 9) {
      fNullable = ((fLeftChild.fNullable) || (fRightChild.fNullable));
    }
    else if (fType == 8) {
      fNullable = ((fLeftChild.fNullable) && (fRightChild.fNullable));
    }
    else if ((fType == 10) || (fType == 12)) {
      fNullable = true;
    }
    else {
      fNullable = false;
    }
  }
  







  void calcFirstPos(RBBINode n)
  {
    if (n == null) {
      return;
    }
    if ((fType == 3) || (fType == 6) || (fType == 4) || (fType == 5))
    {



      fFirstPosSet.add(n);
      return;
    }
    


    calcFirstPos(fLeftChild);
    calcFirstPos(fRightChild);
    

    if (fType == 9) {
      fFirstPosSet.addAll(fLeftChild.fFirstPosSet);
      fFirstPosSet.addAll(fRightChild.fFirstPosSet);
    }
    else if (fType == 8) {
      fFirstPosSet.addAll(fLeftChild.fFirstPosSet);
      if (fLeftChild.fNullable) {
        fFirstPosSet.addAll(fRightChild.fFirstPosSet);
      }
    }
    else if ((fType == 10) || (fType == 12) || (fType == 11))
    {

      fFirstPosSet.addAll(fLeftChild.fFirstPosSet);
    }
  }
  






  void calcLastPos(RBBINode n)
  {
    if (n == null) {
      return;
    }
    if ((fType == 3) || (fType == 6) || (fType == 4) || (fType == 5))
    {



      fLastPosSet.add(n);
      return;
    }
    


    calcLastPos(fLeftChild);
    calcLastPos(fRightChild);
    

    if (fType == 9) {
      fLastPosSet.addAll(fLeftChild.fLastPosSet);
      fLastPosSet.addAll(fRightChild.fLastPosSet);
    }
    else if (fType == 8) {
      fLastPosSet.addAll(fRightChild.fLastPosSet);
      if (fRightChild.fNullable) {
        fLastPosSet.addAll(fLeftChild.fLastPosSet);
      }
    }
    else if ((fType == 10) || (fType == 12) || (fType == 11))
    {

      fLastPosSet.addAll(fLeftChild.fLastPosSet);
    }
  }
  






  void calcFollowPos(RBBINode n)
  {
    if ((n == null) || (fType == 3) || (fType == 6))
    {

      return;
    }
    
    calcFollowPos(fLeftChild);
    calcFollowPos(fRightChild);
    

    if (fType == 8) {
      for (RBBINode i : fLeftChild.fLastPosSet) {
        fFollowPos.addAll(fRightChild.fFirstPosSet);
      }
    }
    

    if ((fType == 10) || (fType == 11))
    {
      for (RBBINode i : fLastPosSet) {
        fFollowPos.addAll(fFirstPosSet);
      }
    }
  }
  







  void calcChainedFollowPos(RBBINode tree)
  {
    List<RBBINode> endMarkerNodes = new ArrayList();
    List<RBBINode> leafNodes = new ArrayList();
    

    tree.findNodes(endMarkerNodes, 6);
    

    tree.findNodes(leafNodes, 3);
    



    RBBINode userRuleRoot = tree;
    if (fRB.fSetBuilder.sawBOF()) {
      userRuleRoot = fLeftChild.fRightChild;
    }
    Assert.assrt(userRuleRoot != null);
    Set<RBBINode> matchStartNodes = fFirstPosSet;
    


    for (RBBINode tNode : leafNodes) {
      endNode = null;
      


      for (RBBINode endMarkerNode : endMarkerNodes) {
        if (fFollowPos.contains(endMarkerNode)) {
          endNode = tNode;
          break;
        }
      }
      if (endNode != null)
      {









        if (fRB.fLBCMNoChain) {
          int c = fRB.fSetBuilder.getFirstChar(fVal);
          if (c != -1)
          {
            int cLBProp = UCharacter.getIntPropertyValue(c, 4104);
            if (cLBProp == 9) {
              continue;
            }
          }
        }
        



        for (RBBINode startNode : matchStartNodes) {
          if (fType == 3)
          {


            if (fVal == fVal)
            {






              fFollowPos.addAll(fFollowPos);
            }
          }
        }
      }
    }
    








    RBBINode endNode;
  }
  









  void bofFixup()
  {
    RBBINode bofNode = fRB.fTreeRoots[fRootIx].fLeftChild.fLeftChild;
    Assert.assrt(fType == 3);
    Assert.assrt(fVal == 2);
    





    Set<RBBINode> matchStartNodes = fRB.fTreeRoots[fRootIx].fLeftChild.fRightChild.fFirstPosSet;
    for (RBBINode startNode : matchStartNodes) {
      if (fType == 3)
      {


        if (fVal == fVal)
        {




          fFollowPos.addAll(fFollowPos);
        }
      }
    }
  }
  









  void buildStateTable()
  {
    int lastInputSymbol = fRB.fSetBuilder.getNumCharCategories() - 1;
    RBBIStateDescriptor failState = new RBBIStateDescriptor(lastInputSymbol);
    fDStates.add(failState);
    


    RBBIStateDescriptor initialState = new RBBIStateDescriptor(lastInputSymbol);
    fPositions.addAll(fRB.fTreeRoots[fRootIx].fFirstPosSet);
    fDStates.add(initialState);
    
    for (;;)
    {
      RBBIStateDescriptor T = null;
      
      for (int tx = 1; tx < fDStates.size(); tx++) {
        RBBIStateDescriptor temp = (RBBIStateDescriptor)fDStates.get(tx);
        if (!fMarked) {
          T = temp;
          break;
        }
      }
      if (T == null) {
        break;
      }
      

      fMarked = true;
      


      for (int a = 1; a <= lastInputSymbol; a++)
      {


        Set<RBBINode> U = null;
        for (RBBINode p : fPositions) {
          if ((fType == 3) && (fVal == a)) {
            if (U == null) {
              U = new HashSet();
            }
            U.addAll(fFollowPos);
          }
        }
        

        int ux = 0;
        boolean UinDstates = false;
        if (U != null) {
          Assert.assrt(U.size() > 0);
          
          for (int ix = 0; ix < fDStates.size(); ix++)
          {
            RBBIStateDescriptor temp2 = (RBBIStateDescriptor)fDStates.get(ix);
            if (U.equals(fPositions)) {
              U = fPositions;
              ux = ix;
              UinDstates = true;
              break;
            }
          }
          

          if (!UinDstates)
          {
            RBBIStateDescriptor newState = new RBBIStateDescriptor(lastInputSymbol);
            fPositions = U;
            fDStates.add(newState);
            ux = fDStates.size() - 1;
          }
          

          fDtran[a] = ux;
        }
      }
    }
  }
  










  void flagAcceptingStates()
  {
    List<RBBINode> endMarkerNodes = new ArrayList();
    



    fRB.fTreeRoots[fRootIx].findNodes(endMarkerNodes, 6);
    
    for (int i = 0; i < endMarkerNodes.size(); i++) {
      RBBINode endMarker = (RBBINode)endMarkerNodes.get(i);
      for (int n = 0; n < fDStates.size(); n++) {
        RBBIStateDescriptor sd = (RBBIStateDescriptor)fDStates.get(n);
        
        if (fPositions.contains(endMarker))
        {



          if (fAccepting == 0)
          {
            fAccepting = fVal;
            if (fAccepting == 0) {
              fAccepting = -1;
            }
          }
          if ((fAccepting == -1) && (fVal != 0))
          {


            fAccepting = fVal;
          }
          




          if (fLookAheadEnd)
          {


            fLookAhead = fAccepting;
          }
        }
      }
    }
  }
  





  void flagLookAheadStates()
  {
    List<RBBINode> lookAheadNodes = new ArrayList();
    



    fRB.fTreeRoots[fRootIx].findNodes(lookAheadNodes, 4);
    for (int i = 0; i < lookAheadNodes.size(); i++) {
      RBBINode lookAheadNode = (RBBINode)lookAheadNodes.get(i);
      
      for (int n = 0; n < fDStates.size(); n++) {
        RBBIStateDescriptor sd = (RBBIStateDescriptor)fDStates.get(n);
        if (fPositions.contains(lookAheadNode)) {
          fLookAhead = fVal;
        }
      }
    }
  }
  







  void flagTaggedStates()
  {
    List<RBBINode> tagNodes = new ArrayList();
    



    fRB.fTreeRoots[fRootIx].findNodes(tagNodes, 5);
    for (int i = 0; i < tagNodes.size(); i++) {
      RBBINode tagNode = (RBBINode)tagNodes.get(i);
      
      for (int n = 0; n < fDStates.size(); n++) {
        RBBIStateDescriptor sd = (RBBIStateDescriptor)fDStates.get(n);
        if (fPositions.contains(tagNode)) {
          fTagVals.add(Integer.valueOf(fVal));
        }
      }
    }
  }
  







































  void mergeRuleStatusVals()
  {
    if (fRB.fRuleStatusVals.size() == 0) {
      fRB.fRuleStatusVals.add(Integer.valueOf(1));
      fRB.fRuleStatusVals.add(Integer.valueOf(0));
      
      SortedSet<Integer> s0 = new TreeSet();
      Integer izero = Integer.valueOf(0);
      fRB.fStatusSets.put(s0, izero);
      SortedSet<Integer> s1 = new TreeSet();
      s1.add(izero);
      fRB.fStatusSets.put(s0, izero);
    }
    


    for (int n = 0; n < fDStates.size(); n++) {
      RBBIStateDescriptor sd = (RBBIStateDescriptor)fDStates.get(n);
      Set<Integer> statusVals = fTagVals;
      Integer arrayIndexI = (Integer)fRB.fStatusSets.get(statusVals);
      if (arrayIndexI == null)
      {



        arrayIndexI = Integer.valueOf(fRB.fRuleStatusVals.size());
        fRB.fStatusSets.put(statusVals, arrayIndexI);
        


        fRB.fRuleStatusVals.add(Integer.valueOf(statusVals.size()));
        fRB.fRuleStatusVals.addAll(statusVals);
      }
      

      fTagsIdx = arrayIndexI.intValue();
    }
  }
  












  void printPosSets(RBBINode n)
  {
    if (n == null) {
      return;
    }
    RBBINode.printNode(n);
    System.out.print("         Nullable:  " + fNullable);
    
    System.out.print("         firstpos:  ");
    printSet(fFirstPosSet);
    
    System.out.print("         lastpos:   ");
    printSet(fLastPosSet);
    
    System.out.print("         followpos: ");
    printSet(fFollowPos);
    
    printPosSets(fLeftChild);
    printPosSets(fRightChild);
  }
  











  int getTableSize()
  {
    int size = 0;
    



    if (fRB.fTreeRoots[fRootIx] == null) {
      return 0;
    }
    
    size = 16;
    
    int numRows = fDStates.size();
    int numCols = fRB.fSetBuilder.getNumCharCategories();
    




    int rowSize = 8 + 2 * numCols;
    size += numRows * rowSize;
    while (size % 8 > 0) {
      size++;
    }
    
    return size;
  }
  


















  short[] exportTable()
  {
    if (fRB.fTreeRoots[fRootIx] == null) {
      return new short[0];
    }
    
    Assert.assrt((fRB.fSetBuilder.getNumCharCategories() < 32767) && (fDStates.size() < 32767));
    

    int numStates = fDStates.size();
    


    int rowLen = 4 + fRB.fSetBuilder.getNumCharCategories();
    int tableSize = getTableSize() / 2;
    

    short[] table = new short[tableSize];
    





    table[0] = ((short)(numStates >>> 16));
    table[1] = ((short)(numStates & 0xFFFF));
    

    table[2] = ((short)(rowLen >>> 16));
    table[3] = ((short)(rowLen & 0xFFFF));
    

    int flags = 0;
    if (fRB.fLookAheadHardBreak) {
      flags |= 0x1;
    }
    if (fRB.fSetBuilder.sawBOF()) {
      flags |= 0x2;
    }
    table[4] = ((short)(flags >>> 16));
    table[5] = ((short)(flags & 0xFFFF));
    
    int numCharCategories = fRB.fSetBuilder.getNumCharCategories();
    for (int state = 0; state < numStates; state++) {
      RBBIStateDescriptor sd = (RBBIStateDescriptor)fDStates.get(state);
      int row = 8 + state * rowLen;
      Assert.assrt((32768 < fAccepting) && (fAccepting <= 32767));
      Assert.assrt((32768 < fLookAhead) && (fLookAhead <= 32767));
      table[(row + 0)] = ((short)fAccepting);
      table[(row + 1)] = ((short)fLookAhead);
      table[(row + 2)] = ((short)fTagsIdx);
      for (int col = 0; col < numCharCategories; col++) {
        table[(row + 4 + col)] = ((short)fDtran[col]);
      }
    }
    return table;
  }
  







  void printSet(Collection<RBBINode> s)
  {
    for (RBBINode n : s) {
      RBBINode.printInt(fSerialNum, 8);
    }
    System.out.println();
  }
  










  void printStates()
  {
    System.out.print("state |           i n p u t     s y m b o l s \n");
    System.out.print("      | Acc  LA    Tag");
    for (int c = 0; c < fRB.fSetBuilder.getNumCharCategories(); c++) {
      RBBINode.printInt(c, 3);
    }
    System.out.print("\n");
    System.out.print("      |---------------");
    for (c = 0; c < fRB.fSetBuilder.getNumCharCategories(); c++) {
      System.out.print("---");
    }
    System.out.print("\n");
    
    for (int n = 0; n < fDStates.size(); n++) {
      RBBIStateDescriptor sd = (RBBIStateDescriptor)fDStates.get(n);
      RBBINode.printInt(n, 5);
      System.out.print(" | ");
      
      RBBINode.printInt(fAccepting, 3);
      RBBINode.printInt(fLookAhead, 4);
      RBBINode.printInt(fTagsIdx, 6);
      System.out.print(" ");
      for (c = 0; c < fRB.fSetBuilder.getNumCharCategories(); c++) {
        RBBINode.printInt(fDtran[c], 3);
      }
      System.out.print("\n");
    }
    System.out.print("\n\n");
  }
  








  void printRuleStatusTable()
  {
    int thisRecord = 0;
    int nextRecord = 0;
    
    List<Integer> tbl = fRB.fRuleStatusVals;
    
    System.out.print("index |  tags \n");
    System.out.print("-------------------\n");
    
    while (nextRecord < tbl.size()) {
      thisRecord = nextRecord;
      nextRecord = thisRecord + ((Integer)tbl.get(thisRecord)).intValue() + 1;
      RBBINode.printInt(thisRecord, 7);
      for (int i = thisRecord + 1; i < nextRecord; i++) {
        int val = ((Integer)tbl.get(i)).intValue();
        RBBINode.printInt(val, 7);
      }
      System.out.print("\n");
    }
    System.out.print("\n\n");
  }
}
