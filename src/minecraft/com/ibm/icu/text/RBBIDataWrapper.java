package com.ibm.icu.text;

import com.ibm.icu.impl.CharTrie;
import com.ibm.icu.impl.Trie.DataManipulate;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;










































final class RBBIDataWrapper
{
  RBBIDataHeader fHeader;
  short[] fFTable;
  short[] fRTable;
  short[] fSFTable;
  short[] fSRTable;
  CharTrie fTrie;
  String fRuleSource;
  int[] fStatusTable;
  static final int DH_SIZE = 24;
  static final int DH_MAGIC = 0;
  static final int DH_FORMATVERSION = 1;
  static final int DH_LENGTH = 2;
  static final int DH_CATCOUNT = 3;
  static final int DH_FTABLE = 4;
  static final int DH_FTABLELEN = 5;
  static final int DH_RTABLE = 6;
  static final int DH_RTABLELEN = 7;
  static final int DH_SFTABLE = 8;
  static final int DH_SFTABLELEN = 9;
  static final int DH_SRTABLE = 10;
  static final int DH_SRTABLELEN = 11;
  static final int DH_TRIE = 12;
  static final int DH_TRIELEN = 13;
  static final int DH_RULESOURCE = 14;
  static final int DH_RULESOURCELEN = 15;
  static final int DH_STATUSTABLE = 16;
  static final int DH_STATUSTABLELEN = 17;
  static final int ACCEPTING = 0;
  static final int LOOKAHEAD = 1;
  static final int TAGIDX = 2;
  static final int RESERVED = 3;
  static final int NEXTSTATES = 4;
  static final int NUMSTATES = 0;
  static final int ROWLEN = 2;
  static final int FLAGS = 4;
  static final int RESERVED_2 = 6;
  static final int ROW_DATA = 8;
  static final int RBBI_LOOKAHEAD_HARD_BREAK = 1;
  static final int RBBI_BOF_REQUIRED = 2;
  
  static final class RBBIDataHeader
  {
    int fMagic;
    int fVersion;
    byte[] fFormatVersion;
    int fLength;
    int fCatCount;
    int fFTable;
    int fFTableLen;
    int fRTable;
    int fRTableLen;
    int fSFTable;
    int fSFTableLen;
    int fSRTable;
    int fSRTableLen;
    int fTrie;
    int fTrieLen;
    int fRuleSource;
    int fRuleSourceLen;
    int fStatusTable;
    int fStatusTableLen;
    
    public RBBIDataHeader()
    {
      fMagic = 0;
      fFormatVersion = new byte[4];
    }
  }
  







  int getRowIndex(int state) { return 8 + state * (fHeader.fCatCount + 4); }
  
  static class TrieFoldingFunc implements Trie.DataManipulate {
    TrieFoldingFunc() {}
    
    public int getFoldingOffset(int data) { if ((data & 0x8000) != 0) {
        return data & 0x7FFF;
      }
      return 0;
    }
  }
  
  static TrieFoldingFunc fTrieFoldingFunc = new TrieFoldingFunc();
  



  RBBIDataWrapper() {}
  



  static RBBIDataWrapper get(InputStream is)
    throws IOException
  {
    DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
    RBBIDataWrapper This = new RBBIDataWrapper();
    


    dis.skip(128L);
    

    fHeader = new RBBIDataHeader();
    fHeader.fMagic = dis.readInt();
    fHeader.fVersion = dis.readInt();
    fHeader.fFormatVersion[0] = ((byte)(fHeader.fVersion >> 24));
    fHeader.fFormatVersion[1] = ((byte)(fHeader.fVersion >> 16));
    fHeader.fFormatVersion[2] = ((byte)(fHeader.fVersion >> 8));
    fHeader.fFormatVersion[3] = ((byte)fHeader.fVersion);
    fHeader.fLength = dis.readInt();
    fHeader.fCatCount = dis.readInt();
    fHeader.fFTable = dis.readInt();
    fHeader.fFTableLen = dis.readInt();
    fHeader.fRTable = dis.readInt();
    fHeader.fRTableLen = dis.readInt();
    fHeader.fSFTable = dis.readInt();
    fHeader.fSFTableLen = dis.readInt();
    fHeader.fSRTable = dis.readInt();
    fHeader.fSRTableLen = dis.readInt();
    fHeader.fTrie = dis.readInt();
    fHeader.fTrieLen = dis.readInt();
    fHeader.fRuleSource = dis.readInt();
    fHeader.fRuleSourceLen = dis.readInt();
    fHeader.fStatusTable = dis.readInt();
    fHeader.fStatusTableLen = dis.readInt();
    dis.skip(24L);
    

    if ((fHeader.fMagic != 45472) || ((fHeader.fVersion != 1) && (fHeader.fFormatVersion[0] != 3)))
    {


      throw new IOException("Break Iterator Rule Data Magic Number Incorrect, or unsupported data version.");
    }
    

    int pos = 96;
    





    if ((fHeader.fFTable < pos) || (fHeader.fFTable > fHeader.fLength)) {
      throw new IOException("Break iterator Rule data corrupt");
    }
    

    dis.skip(fHeader.fFTable - pos);
    pos = fHeader.fFTable;
    
    fFTable = new short[fHeader.fFTableLen / 2];
    for (int i = 0; i < fFTable.length; i++) {
      fFTable[i] = dis.readShort();
      pos += 2;
    }
    





    dis.skip(fHeader.fRTable - pos);
    pos = fHeader.fRTable;
    

    fRTable = new short[fHeader.fRTableLen / 2];
    for (i = 0; i < fRTable.length; i++) {
      fRTable[i] = dis.readShort();
      pos += 2;
    }
    



    if (fHeader.fSFTableLen > 0)
    {
      dis.skip(fHeader.fSFTable - pos);
      pos = fHeader.fSFTable;
      

      fSFTable = new short[fHeader.fSFTableLen / 2];
      for (i = 0; i < fSFTable.length; i++) {
        fSFTable[i] = dis.readShort();
        pos += 2;
      }
    }
    



    if (fHeader.fSRTableLen > 0)
    {
      dis.skip(fHeader.fSRTable - pos);
      pos = fHeader.fSRTable;
      

      fSRTable = new short[fHeader.fSRTableLen / 2];
      for (i = 0; i < fSRTable.length; i++) {
        fSRTable[i] = dis.readShort();
        pos += 2;
      }
    }
    







    dis.skip(fHeader.fTrie - pos);
    pos = fHeader.fTrie;
    
    dis.mark(fHeader.fTrieLen + 100);
    



    fTrie = new CharTrie(dis, fTrieFoldingFunc);
    


    dis.reset();
    






    if (pos > fHeader.fStatusTable) {
      throw new IOException("Break iterator Rule data corrupt");
    }
    dis.skip(fHeader.fStatusTable - pos);
    pos = fHeader.fStatusTable;
    fStatusTable = new int[fHeader.fStatusTableLen / 4];
    for (i = 0; i < fStatusTable.length; i++) {
      fStatusTable[i] = dis.readInt();
      pos += 4;
    }
    



    if (pos > fHeader.fRuleSource) {
      throw new IOException("Break iterator Rule data corrupt");
    }
    dis.skip(fHeader.fRuleSource - pos);
    pos = fHeader.fRuleSource;
    StringBuilder sb = new StringBuilder(fHeader.fRuleSourceLen / 2);
    for (i = 0; i < fHeader.fRuleSourceLen; i += 2) {
      sb.append(dis.readChar());
      pos += 2;
    }
    fRuleSource = sb.toString();
    
    if ((RuleBasedBreakIterator.fDebugEnv != null) && (RuleBasedBreakIterator.fDebugEnv.indexOf("data") >= 0)) {
      This.dump();
    }
    return This;
  }
  


  static final int getNumStates(short[] table)
  {
    int hi = table[0];
    int lo = table[1];
    int val = (hi << 16) + (lo & 0xFFFF);
    return val;
  }
  


  void dump()
  {
    if (fFTable.length == 0)
    {
      throw new NullPointerException();
    }
    System.out.println("RBBI Data Wrapper dump ...");
    System.out.println();
    System.out.println("Forward State Table");
    dumpTable(fFTable);
    System.out.println("Reverse State Table");
    dumpTable(fRTable);
    System.out.println("Forward Safe Points Table");
    dumpTable(fSFTable);
    System.out.println("Reverse Safe Points Table");
    dumpTable(fSRTable);
    
    dumpCharCategories();
    System.out.println("Source Rules: " + fRuleSource);
  }
  



  public static String intToString(int n, int width)
  {
    StringBuilder dest = new StringBuilder(width);
    dest.append(n);
    while (dest.length() < width) {
      dest.insert(0, ' ');
    }
    return dest.toString();
  }
  


  public static String intToHexString(int n, int width)
  {
    StringBuilder dest = new StringBuilder(width);
    dest.append(Integer.toHexString(n));
    while (dest.length() < width) {
      dest.insert(0, ' ');
    }
    return dest.toString();
  }
  


  private void dumpTable(short[] table)
  {
    if (table == null) {
      System.out.println("  -- null -- ");
    }
    else
    {
      StringBuilder header = new StringBuilder(" Row  Acc Look  Tag");
      for (int n = 0; n < fHeader.fCatCount; n++) {
        header.append(intToString(n, 5));
      }
      System.out.println(header.toString());
      for (n = 0; n < header.length(); n++) {
        System.out.print("-");
      }
      System.out.println();
      for (int state = 0; state < getNumStates(table); state++) {
        dumpRow(table, state);
      }
      System.out.println();
    }
  }
  






  private void dumpRow(short[] table, int state)
  {
    StringBuilder dest = new StringBuilder(fHeader.fCatCount * 5 + 20);
    dest.append(intToString(state, 4));
    int row = getRowIndex(state);
    if (table[(row + 0)] != 0) {
      dest.append(intToString(table[(row + 0)], 5));
    } else {
      dest.append("     ");
    }
    if (table[(row + 1)] != 0) {
      dest.append(intToString(table[(row + 1)], 5));
    } else {
      dest.append("     ");
    }
    dest.append(intToString(table[(row + 2)], 5));
    
    for (int col = 0; col < fHeader.fCatCount; col++) {
      dest.append(intToString(table[(row + 4 + col)], 5));
    }
    
    System.out.println(dest);
  }
  

  private void dumpCharCategories()
  {
    int n = fHeader.fCatCount;
    String[] catStrings = new String[n + 1];
    int rangeStart = 0;
    int rangeEnd = 0;
    int lastCat = -1;
    

    int[] lastNewline = new int[n + 1];
    
    for (int category = 0; category <= fHeader.fCatCount; category++) {
      catStrings[category] = "";
    }
    System.out.println("\nCharacter Categories");
    System.out.println("--------------------");
    for (int char32 = 0; char32 <= 1114111; char32++) {
      category = fTrie.getCodePointValue(char32);
      category &= 0xBFFF;
      if ((category < 0) || (category > fHeader.fCatCount)) {
        System.out.println("Error, bad category " + Integer.toHexString(category) + " for char " + Integer.toHexString(char32));
        
        break;
      }
      if (category == lastCat) {
        rangeEnd = char32;
      } else {
        if (lastCat >= 0) {
          if (catStrings[lastCat].length() > lastNewline[lastCat] + 70) {
            lastNewline[lastCat] = (catStrings[lastCat].length() + 10); int 
              tmp226_224 = lastCat; String[] tmp226_223 = catStrings;tmp226_223[tmp226_224] = (tmp226_223[tmp226_224] + "\n       ");
          }
          
          int tmp250_248 = lastCat; String[] tmp250_247 = catStrings;tmp250_247[tmp250_248] = (tmp250_247[tmp250_248] + " " + Integer.toHexString(rangeStart));
          if (rangeEnd != rangeStart) {
            int tmp287_285 = lastCat; String[] tmp287_284 = catStrings;tmp287_284[tmp287_285] = (tmp287_284[tmp287_285] + "-" + Integer.toHexString(rangeEnd));
          }
        }
        lastCat = category;
        rangeStart = rangeEnd = char32;
      }
    }
    int tmp335_333 = lastCat; String[] tmp335_332 = catStrings;tmp335_332[tmp335_333] = (tmp335_332[tmp335_333] + " " + Integer.toHexString(rangeStart));
    if (rangeEnd != rangeStart) {
      int tmp372_370 = lastCat; String[] tmp372_369 = catStrings;tmp372_369[tmp372_370] = (tmp372_369[tmp372_370] + "-" + Integer.toHexString(rangeEnd));
    }
    
    for (category = 0; category <= fHeader.fCatCount; category++) {
      System.out.println(intToString(category, 5) + "  " + catStrings[category]);
    }
    System.out.println();
  }
}
