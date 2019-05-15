package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.ICUDebug;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


















class RBBIRuleBuilder
{
  String fDebugEnv;
  String fRules;
  RBBIRuleScanner fScanner;
  RBBINode[] fTreeRoots = new RBBINode[4];
  static final int fForwardTree = 0;
  static final int fReverseTree = 1;
  static final int fSafeFwdTree = 2;
  static final int fSafeRevTree = 3;
  int fDefaultTree = 0;
  

  boolean fChainRules;
  

  boolean fLBCMNoChain;
  

  boolean fLookAheadHardBreak;
  
  RBBISetBuilder fSetBuilder;
  
  List<RBBINode> fUSetNodes;
  
  RBBITableBuilder fForwardTables;
  
  RBBITableBuilder fReverseTables;
  
  RBBITableBuilder fSafeFwdTables;
  
  RBBITableBuilder fSafeRevTables;
  
  Map<Set<Integer>, Integer> fStatusSets = new HashMap();
  


  List<Integer> fRuleStatusVals;
  


  static final int U_BRK_ERROR_START = 66048;
  


  static final int U_BRK_INTERNAL_ERROR = 66049;
  


  static final int U_BRK_HEX_DIGITS_EXPECTED = 66050;
  


  static final int U_BRK_SEMICOLON_EXPECTED = 66051;
  


  static final int U_BRK_RULE_SYNTAX = 66052;
  


  static final int U_BRK_UNCLOSED_SET = 66053;
  


  static final int U_BRK_ASSIGN_ERROR = 66054;
  


  static final int U_BRK_VARIABLE_REDFINITION = 66055;
  


  static final int U_BRK_MISMATCHED_PAREN = 66056;
  


  static final int U_BRK_NEW_LINE_IN_QUOTED_STRING = 66057;
  


  static final int U_BRK_UNDEFINED_VARIABLE = 66058;
  


  static final int U_BRK_INIT_ERROR = 66059;
  


  static final int U_BRK_RULE_EMPTY_SET = 66060;
  


  static final int U_BRK_UNRECOGNIZED_OPTION = 66061;
  

  static final int U_BRK_MALFORMED_RULE_TAG = 66062;
  

  static final int U_BRK_MALFORMED_SET = 66063;
  

  static final int U_BRK_ERROR_LIMIT = 66064;
  


  RBBIRuleBuilder(String rules)
  {
    fDebugEnv = (ICUDebug.enabled("rbbi") ? ICUDebug.value("rbbi") : null);
    
    fRules = rules;
    fUSetNodes = new ArrayList();
    fRuleStatusVals = new ArrayList();
    fScanner = new RBBIRuleScanner(this);
    fSetBuilder = new RBBISetBuilder(this);
  }
  








  static final int align8(int i)
  {
    return i + 7 & 0xFFFFFFF8;
  }
  
  void flattenData(OutputStream os) throws IOException {
    DataOutputStream dos = new DataOutputStream(os);
    


    String strippedRules = RBBIRuleScanner.stripRules(fRules);
    





    int headerSize = 96;
    int forwardTableSize = align8(fForwardTables.getTableSize());
    int reverseTableSize = align8(fReverseTables.getTableSize());
    int safeFwdTableSize = align8(fSafeFwdTables.getTableSize());
    int safeRevTableSize = align8(fSafeRevTables.getTableSize());
    int trieSize = align8(fSetBuilder.getTrieSize());
    int statusTableSize = align8(fRuleStatusVals.size() * 4);
    int rulesSize = align8(strippedRules.length() * 2);
    int totalSize = headerSize + forwardTableSize + reverseTableSize + safeFwdTableSize + safeRevTableSize + statusTableSize + trieSize + rulesSize;
    

    int outputPos = 0;
    






    byte[] ICUDataHeader = new byte[''];
    dos.write(ICUDataHeader);
    



    int[] header = new int[24];
    header[0] = 45472;
    header[1] = 50397184;
    header[2] = totalSize;
    header[3] = fSetBuilder.getNumCharCategories();
    header[4] = headerSize;
    header[5] = forwardTableSize;
    header[6] = (header[4] + forwardTableSize);
    header[7] = reverseTableSize;
    header[8] = (header[6] + reverseTableSize);
    
    header[9] = safeFwdTableSize;
    header[10] = (header[8] + safeFwdTableSize);
    
    header[11] = safeRevTableSize;
    header[12] = (header[10] + safeRevTableSize);
    
    header[13] = fSetBuilder.getTrieSize();
    header[16] = (header[12] + header[13]);
    
    header[17] = statusTableSize;
    header[14] = (header[16] + statusTableSize);
    
    header[15] = (strippedRules.length() * 2);
    for (int i = 0; i < header.length; i++) {
      dos.writeInt(header[i]);
      outputPos += 4;
    }
    


    short[] tableData = fForwardTables.exportTable();
    Assert.assrt(outputPos == header[4]);
    for (i = 0; i < tableData.length; i++) {
      dos.writeShort(tableData[i]);
      outputPos += 2;
    }
    
    tableData = fReverseTables.exportTable();
    Assert.assrt(outputPos == header[6]);
    for (i = 0; i < tableData.length; i++) {
      dos.writeShort(tableData[i]);
      outputPos += 2;
    }
    
    Assert.assrt(outputPos == header[8]);
    tableData = fSafeFwdTables.exportTable();
    for (i = 0; i < tableData.length; i++) {
      dos.writeShort(tableData[i]);
      outputPos += 2;
    }
    
    Assert.assrt(outputPos == header[10]);
    tableData = fSafeRevTables.exportTable();
    for (i = 0; i < tableData.length; i++) {
      dos.writeShort(tableData[i]);
      outputPos += 2;
    }
    

    Assert.assrt(outputPos == header[12]);
    fSetBuilder.serializeTrie(os);
    outputPos += header[13];
    while (outputPos % 8 != 0) {
      dos.write(0);
      outputPos++;
    }
    

    Assert.assrt(outputPos == header[16]);
    for (Integer val : fRuleStatusVals) {
      dos.writeInt(val.intValue());
      outputPos += 4;
    }
    
    while (outputPos % 8 != 0) {
      dos.write(0);
      outputPos++;
    }
    


    Assert.assrt(outputPos == header[14]);
    dos.writeChars(strippedRules);
    outputPos += strippedRules.length() * 2;
    while (outputPos % 8 != 0) {
      dos.write(0);
      outputPos++;
    }
  }
  









  static void compileRules(String rules, OutputStream os)
    throws IOException
  {
    RBBIRuleBuilder builder = new RBBIRuleBuilder(rules);
    fScanner.parse();
    






    fSetBuilder.build();
    



    fForwardTables = new RBBITableBuilder(builder, 0);
    fReverseTables = new RBBITableBuilder(builder, 1);
    fSafeFwdTables = new RBBITableBuilder(builder, 2);
    fSafeRevTables = new RBBITableBuilder(builder, 3);
    fForwardTables.build();
    fReverseTables.build();
    fSafeFwdTables.build();
    fSafeRevTables.build();
    if ((fDebugEnv != null) && (fDebugEnv.indexOf("states") >= 0))
    {
      fForwardTables.printRuleStatusTable();
    }
    




    builder.flattenData(os);
  }
}
