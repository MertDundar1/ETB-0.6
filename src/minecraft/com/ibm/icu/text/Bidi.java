package com.ibm.icu.text;

import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.lang.UCharacter;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;































































































































































































































































































































































public class Bidi
{
  public static final byte LEVEL_DEFAULT_LTR = 126;
  public static final byte LEVEL_DEFAULT_RTL = 127;
  public static final byte MAX_EXPLICIT_LEVEL = 61;
  public static final byte LEVEL_OVERRIDE = -128;
  public static final int MAP_NOWHERE = -1;
  public static final byte LTR = 0;
  public static final byte RTL = 1;
  public static final byte MIXED = 2;
  public static final byte NEUTRAL = 3;
  public static final short KEEP_BASE_COMBINING = 1;
  public static final short DO_MIRRORING = 2;
  public static final short INSERT_LRM_FOR_NUMERIC = 4;
  public static final short REMOVE_BIDI_CONTROLS = 8;
  public static final short OUTPUT_REVERSE = 16;
  public static final short REORDER_DEFAULT = 0;
  public static final short REORDER_NUMBERS_SPECIAL = 1;
  public static final short REORDER_GROUP_NUMBERS_WITH_R = 2;
  public static final short REORDER_RUNS_ONLY = 3;
  public static final short REORDER_INVERSE_NUMBERS_AS_L = 4;
  public static final short REORDER_INVERSE_LIKE_DIRECT = 5;
  public static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6;
  static final short REORDER_COUNT = 7;
  static final short REORDER_LAST_LOGICAL_TO_VISUAL = 1;
  public static final int OPTION_DEFAULT = 0;
  public static final int OPTION_INSERT_MARKS = 1;
  public static final int OPTION_REMOVE_CONTROLS = 2;
  public static final int OPTION_STREAMING = 4;
  static final byte L = 0;
  static final byte R = 1;
  static final byte EN = 2;
  static final byte ES = 3;
  static final byte ET = 4;
  static final byte AN = 5;
  static final byte CS = 6;
  static final byte B = 7;
  static final byte S = 8;
  static final byte WS = 9;
  static final byte ON = 10;
  static final byte LRE = 11;
  static final byte LRO = 12;
  static final byte AL = 13;
  static final byte RLE = 14;
  static final byte RLO = 15;
  static final byte PDF = 16;
  static final byte NSM = 17;
  static final byte BN = 18;
  static final int MASK_R_AL = 8194;
  public static final int CLASS_DEFAULT = 19;
  private static final char CR = '\r';
  private static final char LF = '\n';
  static final int LRM_BEFORE = 1;
  static final int LRM_AFTER = 2;
  static final int RLM_BEFORE = 4;
  static final int RLM_AFTER = 8;
  Bidi paraBidi;
  final UBiDiProps bdp;
  char[] text;
  int originalLength;
  int length;
  int resultLength;
  boolean mayAllocateText;
  boolean mayAllocateRuns;
  
  static class Point
  {
    int pos;
    int flag;
    
    Point() {}
  }
  
  static class InsertPoints
  {
    int size;
    int confirmed;
    Bidi.Point[] points = new Bidi.Point[0];
    








































































































































































































































    InsertPoints() {}
  }
  







































































































































































































































  byte[] dirPropsMemory = new byte[1];
  byte[] levelsMemory = new byte[1];
  

  byte[] dirProps;
  

  byte[] levels;
  

  boolean isInverse;
  

  int reorderingMode;
  

  int reorderingOptions;
  

  boolean orderParagraphsLTR;
  

  byte paraLevel;
  

  byte defaultParaLevel;
  

  String prologue;
  

  String epilogue;
  
  ImpTabPair impTabPair;
  
  byte direction;
  
  int flags;
  
  int lastArabicPos;
  
  int trailingWSStart;
  
  int paraCount;
  
  int[] parasMemory = new int[1];
  

  int[] paras;
  
  int[] simpleParas = { 0 };
  
  int runCount;
  
  BidiRun[] runsMemory = new BidiRun[0];
  
  BidiRun[] runs;
  
  BidiRun[] simpleRuns = { new BidiRun() };
  

  int[] logicalToVisualRunsMap;
  

  boolean isGoodLogicalToVisualRunsMap;
  
  BidiClassifier customClassifier = null;
  

  InsertPoints insertPoints = new InsertPoints();
  

  int controlCount;
  
  static final byte CONTEXT_RTL_SHIFT = 6;
  
  static final byte CONTEXT_RTL = 64;
  

  static int DirPropFlag(byte dir)
  {
    return 1 << dir;
  }
  
  boolean testDirPropFlagAt(int flag, int index) {
    return (DirPropFlag((byte)(dirProps[index] & 0xFFFFFFBF)) & flag) != 0;
  }
  






  static byte NoContextRTL(byte dir)
  {
    return (byte)(dir & 0xFFFFFFBF);
  }
  



  static int DirPropFlagNC(byte dir)
  {
    return 1 << (dir & 0xFFFFFFBF);
  }
  
  static final int DirPropFlagMultiRuns = DirPropFlag();
  

  static final int[] DirPropFlagLR = { DirPropFlag(0), DirPropFlag(1) };
  static final int[] DirPropFlagE = { DirPropFlag(11), DirPropFlag(14) };
  static final int[] DirPropFlagO = { DirPropFlag(12), DirPropFlag(15) };
  
  static final int DirPropFlagLR(byte level) { return DirPropFlagLR[(level & 0x1)]; }
  static final int DirPropFlagE(byte level) { return DirPropFlagE[(level & 0x1)]; }
  static final int DirPropFlagO(byte level) { return DirPropFlagO[(level & 0x1)]; }
  



  static final int MASK_LTR = DirPropFlag((byte)0) | DirPropFlag((byte)2) | DirPropFlag((byte)5) | DirPropFlag((byte)11) | DirPropFlag((byte)12);
  




  static final int MASK_RTL = DirPropFlag((byte)1) | DirPropFlag((byte)13) | DirPropFlag((byte)14) | DirPropFlag((byte)15);
  

  static final int MASK_LRX = DirPropFlag((byte)11) | DirPropFlag((byte)12);
  static final int MASK_RLX = DirPropFlag((byte)14) | DirPropFlag((byte)15);
  static final int MASK_OVERRIDE = DirPropFlag((byte)12) | DirPropFlag((byte)15);
  static final int MASK_EXPLICIT = MASK_LRX | MASK_RLX | DirPropFlag((byte)16);
  static final int MASK_BN_EXPLICIT = DirPropFlag((byte)18) | MASK_EXPLICIT;
  

  static final int MASK_B_S = DirPropFlag((byte)7) | DirPropFlag((byte)8);
  

  static final int MASK_WS = MASK_B_S | DirPropFlag((byte)9) | MASK_BN_EXPLICIT;
  static final int MASK_N = DirPropFlag((byte)10) | MASK_WS;
  


  static final int MASK_ET_NSM_BN = DirPropFlag((byte)4) | DirPropFlag((byte)17) | MASK_BN_EXPLICIT;
  

  static final int MASK_POSSIBLE_N = DirPropFlag((byte)6) | DirPropFlag((byte)3) | DirPropFlag((byte)4) | MASK_N;
  





  static final int MASK_EMBEDDING = DirPropFlag((byte)17) | MASK_POSSIBLE_N;
  
  private static final int IMPTABPROPS_COLUMNS = 14;
  private static final int IMPTABPROPS_RES = 13;
  
  static byte GetLRFromLevel(byte level)
  {
    return (byte)(level & 0x1);
  }
  
  static boolean IsDefaultLevel(byte level)
  {
    return (level & 0x7E) == 126;
  }
  
  byte GetParaLevelAt(int index)
  {
    return defaultParaLevel != 0 ? (byte)(dirProps[index] >> 6) : paraLevel;
  }
  



  static boolean IsBidiControlChar(int c)
  {
    return ((c & 0xFFFFFFFC) == 8204) || ((c >= 8234) && (c <= 8238));
  }
  
  void verifyValidPara()
  {
    if (this != paraBidi) {
      throw new IllegalStateException();
    }
  }
  
  void verifyValidParaOrLine()
  {
    Bidi para = paraBidi;
    
    if (this == para) {
      return;
    }
    
    if ((para == null) || (para != paraBidi)) {
      throw new IllegalStateException();
    }
  }
  
  void verifyRange(int index, int start, int limit)
  {
    if ((index < start) || (index >= limit)) {
      throw new IllegalArgumentException("Value " + index + " is out of range " + start + " to " + limit);
    }
  }
  














  public Bidi()
  {
    this(0, 0);
  }
  































  public Bidi(int maxLength, int maxRunCount)
  {
    if ((maxLength < 0) || (maxRunCount < 0)) {
      throw new IllegalArgumentException();
    }
    


















    bdp = UBiDiProps.INSTANCE;
    

    if (maxLength > 0) {
      getInitialDirPropsMemory(maxLength);
      getInitialLevelsMemory(maxLength);
    } else {
      mayAllocateText = true;
    }
    
    if (maxRunCount > 0)
    {
      if (maxRunCount > 1) {
        getInitialRunsMemory(maxRunCount);
      }
    } else {
      mayAllocateRuns = true;
    }
  }
  








  private Object getMemory(String label, Object array, Class<?> arrayClass, boolean mayAllocate, int sizeNeeded)
  {
    int len = Array.getLength(array);
    

    if (sizeNeeded == len) {
      return array;
    }
    if (!mayAllocate)
    {
      if (sizeNeeded <= len) {
        return array;
      }
      throw new OutOfMemoryError("Failed to allocate memory for " + label);
    }
    


    try
    {
      return Array.newInstance(arrayClass, sizeNeeded);
    } catch (Exception e) {
      throw new OutOfMemoryError("Failed to allocate memory for " + label);
    }
  }
  


  private void getDirPropsMemory(boolean mayAllocate, int len)
  {
    Object array = getMemory("DirProps", dirPropsMemory, Byte.TYPE, mayAllocate, len);
    dirPropsMemory = ((byte[])array);
  }
  
  void getDirPropsMemory(int len)
  {
    getDirPropsMemory(mayAllocateText, len);
  }
  
  private void getLevelsMemory(boolean mayAllocate, int len)
  {
    Object array = getMemory("Levels", levelsMemory, Byte.TYPE, mayAllocate, len);
    levelsMemory = ((byte[])array);
  }
  
  void getLevelsMemory(int len)
  {
    getLevelsMemory(mayAllocateText, len);
  }
  
  private void getRunsMemory(boolean mayAllocate, int len)
  {
    Object array = getMemory("Runs", runsMemory, BidiRun.class, mayAllocate, len);
    runsMemory = ((BidiRun[])array);
  }
  
  void getRunsMemory(int len)
  {
    getRunsMemory(mayAllocateRuns, len);
  }
  

  private void getInitialDirPropsMemory(int len)
  {
    getDirPropsMemory(true, len);
  }
  
  private void getInitialLevelsMemory(int len)
  {
    getLevelsMemory(true, len);
  }
  
  private void getInitialParasMemory(int len)
  {
    Object array = getMemory("Paras", parasMemory, Integer.TYPE, true, len);
    parasMemory = ((int[])array);
  }
  
  private void getInitialRunsMemory(int len)
  {
    getRunsMemory(true, len);
  }
  













































  public void setInverse(boolean isInverse)
  {
    this.isInverse = isInverse;
    reorderingMode = (isInverse ? 4 : 0);
  }
  

















  public boolean isInverse()
  {
    return isInverse;
  }
  

































































































































































  public void setReorderingMode(int reorderingMode)
  {
    if ((reorderingMode < 0) || (reorderingMode >= 7))
    {
      return; }
    this.reorderingMode = reorderingMode;
    isInverse = (reorderingMode == 4);
  }
  








  public int getReorderingMode()
  {
    return reorderingMode;
  }
  















  public void setReorderingOptions(int options)
  {
    if ((options & 0x2) != 0) {
      reorderingOptions = (options & 0xFFFFFFFE);
    } else {
      reorderingOptions = options;
    }
  }
  







  public int getReorderingOptions()
  {
    return reorderingOptions;
  }
  





  private byte firstL_R_AL()
  {
    byte result = 10;
    for (int i = 0; i < prologue.length();) {
      int uchar = prologue.codePointAt(i);
      i += Character.charCount(uchar);
      byte dirProp = (byte)getCustomizedClass(uchar);
      if (result == 10) {
        if ((dirProp == 0) || (dirProp == 1) || (dirProp == 13)) {
          result = dirProp;
        }
      }
      else if (dirProp == 7) {
        result = 10;
      }
    }
    
    return result;
  }
  
  private void getDirProps()
  {
    int i = 0;
    flags = 0;
    

    byte paraDirDefault = 0;
    boolean isDefaultLevel = IsDefaultLevel(paraLevel);
    

    boolean isDefaultLevelInverse = (isDefaultLevel) && ((reorderingMode == 5) || (reorderingMode == 6));
    

    lastArabicPos = -1;
    controlCount = 0;
    boolean removeBidiControls = (reorderingOptions & 0x2) != 0;
    
    int NOT_CONTEXTUAL = 0;
    int LOOKING_FOR_STRONG = 1;
    int FOUND_STRONG_CHAR = 2;
    

    int paraStart = 0;
    

    byte lastStrongDir = 0;
    int lastStrongLTR = 0;
    
    if ((reorderingOptions & 0x4) > 0) {
      length = 0;
      lastStrongLTR = 0; }
    int state;
    byte paraDir; if (isDefaultLevel)
    {
      paraDirDefault = (paraLevel & 0x1) != 0 ? 64 : 0;
      byte lastStrong; int state; if ((prologue != null) && ((lastStrong = firstL_R_AL()) != 10))
      {
        byte paraDir = lastStrong == 0 ? 0 : 64;
        state = 2;
      } else {
        byte paraDir = paraDirDefault;
        state = 1;
      }
      int state = 1;
    } else {
      state = 0;
      paraDir = 0;
    }
    






    for (i = 0; i < originalLength;) {
      int i0 = i;
      int uchar = UTF16.charAt(text, 0, originalLength, i);
      i += UTF16.getCharCount(uchar);
      int i1 = i - 1;
      
      byte dirProp = (byte)getCustomizedClass(uchar);
      flags |= DirPropFlag(dirProp);
      dirProps[i1] = ((byte)(dirProp | paraDir));
      if (i1 > i0) {
        flags |= DirPropFlag((byte)18);
        do {
          dirProps[(--i1)] = ((byte)(0x12 | paraDir));
        } while (i1 > i0);
      }
      if (state == 1) {
        if (dirProp == 0) {
          state = 2;
          if (paraDir == 0) continue;
          paraDir = 0;
          for (i1 = paraStart; i1 < i; i1++) {
            int tmp336_335 = i1; byte[] tmp336_332 = dirProps;tmp336_332[tmp336_335] = ((byte)(tmp336_332[tmp336_335] & 0xFFFFFFBF));
          }
          
          continue;
        }
        if ((dirProp == 1) || (dirProp == 13)) {
          state = 2;
          if (paraDir != 0) continue;
          paraDir = 64;
          for (i1 = paraStart; i1 < i; i1++) {
            int tmp387_386 = i1; byte[] tmp387_383 = dirProps;tmp387_383[tmp387_386] = ((byte)(tmp387_383[tmp387_386] | 0x40));
          }
          
          continue;
        }
      }
      if (dirProp == 0) {
        lastStrongDir = 0;
        lastStrongLTR = i;
      }
      else if (dirProp == 1) {
        lastStrongDir = 64;
      }
      else if (dirProp == 13) {
        lastStrongDir = 64;
        lastArabicPos = (i - 1);
      }
      else if (dirProp == 7) {
        if ((reorderingOptions & 0x4) != 0) {
          length = i;
        }
        if ((isDefaultLevelInverse) && (lastStrongDir == 64) && (paraDir != lastStrongDir)) {
          for (; paraStart < i; paraStart++) {
            int tmp500_498 = paraStart; byte[] tmp500_495 = dirProps;tmp500_495[tmp500_498] = ((byte)(tmp500_495[tmp500_498] | 0x40));
          }
        }
        if (i < originalLength) {
          if ((uchar != 13) || (text[i] != '\n')) {
            paraCount += 1;
          }
          if (isDefaultLevel) {
            state = 1;
            paraStart = i;
            paraDir = paraDirDefault;
            lastStrongDir = paraDirDefault;
          }
        }
      }
      if ((removeBidiControls) && (IsBidiControlChar(uchar))) {
        controlCount += 1;
      }
    }
    if ((isDefaultLevelInverse) && (lastStrongDir == 64) && (paraDir != lastStrongDir)) {
      for (int i1 = paraStart; i1 < originalLength; i1++) {
        int tmp629_628 = i1; byte[] tmp629_625 = dirProps;tmp629_625[tmp629_628] = ((byte)(tmp629_625[tmp629_628] | 0x40));
      }
    }
    if (isDefaultLevel) {
      paraLevel = GetParaLevelAt(0);
    }
    if ((reorderingOptions & 0x4) > 0) {
      if ((lastStrongLTR > length) && (GetParaLevelAt(lastStrongLTR) == 0))
      {
        length = lastStrongLTR;
      }
      if (length < originalLength) {
        paraCount -= 1;
      }
    }
    

    flags |= DirPropFlagLR(paraLevel);
    
    if ((orderParagraphsLTR) && ((flags & DirPropFlag((byte)7)) != 0)) {
      flags |= DirPropFlag((byte)0);
    }
  }
  



  private byte directionFromFlags()
  {
    if (((flags & MASK_RTL) == 0) && (((flags & DirPropFlag((byte)5)) == 0) || ((flags & MASK_POSSIBLE_N) == 0)))
    {

      return 0; }
    if ((flags & MASK_LTR) == 0) {
      return 1;
    }
    return 2;
  }
  




















































  private byte resolveExplicitLevels()
  {
    int i = 0;
    
    byte level = GetParaLevelAt(0);
    

    int paraIndex = 0;
    

    byte dirct = directionFromFlags();
    


    if ((dirct == 2) || (paraCount != 1))
    {
      if ((paraCount == 1) && (((flags & MASK_EXPLICIT) == 0) || (reorderingMode > 1)))
      {





        for (i = 0; i < length;) {
          levels[i] = level;i++; continue;
          





          byte embeddingLevel = level;
          
          byte stackTop = 0;
          
          byte[] stack = new byte[61];
          int countOver60 = 0;
          int countOver61 = 0;
          

          flags = 0;
          
          for (i = 0; i < length; i++) {
            byte dirProp = NoContextRTL(dirProps[i]);
            byte newLevel; switch (dirProp)
            {
            case 11: 
            case 12: 
              newLevel = (byte)(embeddingLevel + 2 & 0x7E);
              if (newLevel <= 61) {
                stack[stackTop] = embeddingLevel;
                stackTop = (byte)(stackTop + 1);
                embeddingLevel = newLevel;
                if (dirProp == 12) {
                  embeddingLevel = (byte)(embeddingLevel | 0xFFFFFF80);

                }
                

              }
              else if ((embeddingLevel & 0x7F) == 61) {
                countOver61++;
              } else {
                countOver60++;
              }
              flags |= DirPropFlag((byte)18);
              break;
            
            case 14: 
            case 15: 
              newLevel = (byte)((embeddingLevel & 0x7F) + 1 | 0x1);
              if (newLevel <= 61) {
                stack[stackTop] = embeddingLevel;
                stackTop = (byte)(stackTop + 1);
                embeddingLevel = newLevel;
                if (dirProp == 15) {
                  embeddingLevel = (byte)(embeddingLevel | 0xFFFFFF80);
                }
                

              }
              else
              {
                countOver61++;
              }
              flags |= DirPropFlag((byte)18);
              break;
            

            case 16: 
              if (countOver61 > 0) {
                countOver61--;
              } else if ((countOver60 > 0) && ((embeddingLevel & 0x7F) != 61))
              {
                countOver60--;
              } else if (stackTop > 0)
              {
                stackTop = (byte)(stackTop - 1);
                embeddingLevel = stack[stackTop];
              }
              
              flags |= DirPropFlag((byte)18);
              break;
            case 7: 
              stackTop = 0;
              countOver60 = 0;
              countOver61 = 0;
              level = GetParaLevelAt(i);
              if (i + 1 < length) {
                embeddingLevel = GetParaLevelAt(i + 1);
                if ((text[i] != '\r') || (text[(i + 1)] != '\n')) {
                  paras[(paraIndex++)] = (i + 1);
                }
              }
              flags |= DirPropFlag((byte)7);
              break;
            

            case 18: 
              flags |= DirPropFlag((byte)18);
              break;
            case 8: case 9: case 10: 
            case 13: case 17: default: 
              if (level != embeddingLevel) {
                level = embeddingLevel;
                if ((level & 0xFFFFFF80) != 0) {
                  flags |= DirPropFlagO(level) | DirPropFlagMultiRuns;
                } else {
                  flags |= DirPropFlagE(level) | DirPropFlagMultiRuns;
                }
              }
              if ((level & 0xFFFFFF80) == 0) {
                flags |= DirPropFlag(dirProp);
              }
              

              break;
            }
            
            

            levels[i] = level;
          }
          if ((flags & MASK_EMBEDDING) != 0) {
            flags |= DirPropFlagLR(paraLevel);
          }
          if ((orderParagraphsLTR) && ((flags & DirPropFlag((byte)7)) != 0)) {
            flags |= DirPropFlag((byte)0);
          }
          



          dirct = directionFromFlags();
        } }
    }
    return dirct;
  }
  











  private byte checkExplicitLevels()
  {
    flags = 0;
    
    int paraIndex = 0;
    
    for (int i = 0; i < length; i++) {
      byte level = levels[i];
      byte dirProp = NoContextRTL(dirProps[i]);
      if ((level & 0xFFFFFF80) != 0)
      {
        level = (byte)(level & 0x7F);
        flags |= DirPropFlagO(level);
      }
      else {
        flags |= DirPropFlagE(level) | DirPropFlag(dirProp);
      }
      if (((level < GetParaLevelAt(i)) && ((0 != level) || (dirProp != 7))) || (61 < level))
      {


        throw new IllegalArgumentException("level " + level + " out of bounds at " + i);
      }
      
      if ((dirProp == 7) && (i + 1 < length) && (
        (text[i] != '\r') || (text[(i + 1)] != '\n'))) {
        paras[(paraIndex++)] = (i + 1);
      }
    }
    
    if ((flags & MASK_EMBEDDING) != 0) {
      flags |= DirPropFlagLR(paraLevel);
    }
    

    return directionFromFlags();
  }
  

















  private static short GetStateProps(short cell)
  {
    return (short)(cell & 0x1F);
  }
  
  private static short GetActionProps(short cell) { return (short)(cell >> 5); }
  

  private static final short[] groupProp = { 0, 1, 2, 7, 8, 3, 9, 6, 5, 4, 4, 10, 10, 12, 10, 10, 10, 11, 10 };
  




  private static final short _L = 0;
  




  private static final short _R = 1;
  




  private static final short _EN = 2;
  




  private static final short _AN = 3;
  




  private static final short _ON = 4;
  




  private static final short _S = 5;
  




  private static final short _B = 6;
  



  private static final short[][] impTabProps = { { 1, 2, 4, 5, 7, 15, 17, 7, 9, 7, 0, 7, 3, 4 }, { 1, 34, 36, 37, 39, 47, 49, 39, 41, 39, 1, 1, 35, 0 }, { 33, 2, 36, 37, 39, 47, 49, 39, 41, 39, 2, 2, 35, 1 }, { 33, 34, 38, 38, 40, 48, 49, 40, 40, 40, 3, 3, 3, 1 }, { 33, 34, 4, 37, 39, 47, 49, 74, 11, 74, 4, 4, 35, 2 }, { 33, 34, 36, 5, 39, 47, 49, 39, 41, 76, 5, 5, 35, 3 }, { 33, 34, 6, 6, 40, 48, 49, 40, 40, 77, 6, 6, 35, 3 }, { 33, 34, 36, 37, 7, 47, 49, 7, 78, 7, 7, 7, 35, 4 }, { 33, 34, 38, 38, 8, 48, 49, 8, 8, 8, 8, 8, 35, 4 }, { 33, 34, 4, 37, 7, 47, 49, 7, 9, 7, 9, 9, 35, 4 }, { 97, 98, 4, 101, 135, 111, 113, 135, 142, 135, 10, 135, 99, 2 }, { 33, 34, 4, 37, 39, 47, 49, 39, 11, 39, 11, 11, 35, 2 }, { 97, 98, 100, 5, 135, 111, 113, 135, 142, 135, 12, 135, 99, 3 }, { 97, 98, 6, 6, 136, 112, 113, 136, 136, 136, 13, 136, 99, 3 }, { 33, 34, 132, 37, 7, 47, 49, 7, 14, 7, 14, 14, 35, 4 }, { 33, 34, 36, 37, 39, 15, 49, 39, 41, 39, 15, 39, 35, 5 }, { 33, 34, 38, 38, 40, 16, 49, 40, 40, 40, 16, 40, 35, 5 }, { 33, 34, 36, 37, 39, 47, 17, 39, 41, 39, 17, 39, 35, 6 } };
  













  private static final int IMPTABLEVELS_COLUMNS = 8;
  












  private static final int IMPTABLEVELS_RES = 7;
  












  private static short GetState(byte cell) { return (short)(cell & 0xF); }
  private static short GetAction(byte cell) { return (short)(cell >> 4); }
  
  private static class ImpTabPair
  {
    byte[][][] imptab;
    short[][] impact;
    
    ImpTabPair(byte[][] table1, byte[][] table2, short[] act1, short[] act2) {
      imptab = new byte[][][] { table1, table2 };
      impact = new short[][] { act1, act2 };
    }
  }
  




































  private static final byte[][] impTabL_DEFAULT = { { 0, 1, 0, 2, 0, 0, 0, 0 }, { 0, 1, 3, 3, 20, 20, 0, 1 }, { 0, 1, 0, 2, 21, 21, 0, 2 }, { 0, 1, 3, 3, 20, 20, 0, 2 }, { 32, 1, 3, 3, 4, 4, 32, 1 }, { 32, 1, 32, 2, 5, 5, 32, 1 } };
  












  private static final byte[][] impTabR_DEFAULT = { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 3, 20, 20, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 1, 0, 1, 3, 5, 5, 0, 1 }, { 33, 0, 33, 3, 4, 4, 0, 0 }, { 1, 0, 1, 3, 5, 5, 0, 0 } };
  












  private static final short[] impAct0 = { 0, 1, 2, 3, 4, 5, 6 };
  
  private static final ImpTabPair impTab_DEFAULT = new ImpTabPair(impTabL_DEFAULT, impTabR_DEFAULT, impAct0, impAct0);
  

  private static final byte[][] impTabL_NUMBERS_SPECIAL = { { 0, 2, 1, 1, 0, 0, 0, 0 }, { 0, 2, 1, 1, 0, 0, 0, 2 }, { 0, 2, 4, 4, 19, 0, 0, 1 }, { 32, 2, 4, 4, 3, 3, 32, 1 }, { 0, 2, 4, 4, 19, 19, 0, 2 } };
  









  private static final ImpTabPair impTab_NUMBERS_SPECIAL = new ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_DEFAULT, impAct0, impAct0);
  

  private static final byte[][] impTabL_GROUP_NUMBERS_WITH_R = { { 0, 3, 17, 17, 0, 0, 0, 0 }, { 32, 3, 1, 1, 2, 32, 32, 2 }, { 32, 3, 1, 1, 2, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 1 }, { 32, 3, 5, 5, 4, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 2 } };
  










  private static final byte[][] impTabR_GROUP_NUMBERS_WITH_R = { { 2, 0, 1, 1, 0, 0, 0, 0 }, { 2, 0, 1, 1, 0, 0, 0, 1 }, { 2, 0, 20, 20, 19, 0, 0, 1 }, { 34, 0, 4, 4, 3, 0, 0, 0 }, { 34, 0, 4, 4, 3, 0, 0, 1 } };
  









  private static final ImpTabPair impTab_GROUP_NUMBERS_WITH_R = new ImpTabPair(impTabL_GROUP_NUMBERS_WITH_R, impTabR_GROUP_NUMBERS_WITH_R, impAct0, impAct0);
  


  private static final byte[][] impTabL_INVERSE_NUMBERS_AS_L = { { 0, 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 20, 20, 0, 1 }, { 0, 1, 0, 0, 21, 21, 0, 2 }, { 0, 1, 0, 0, 20, 20, 0, 2 }, { 32, 1, 32, 32, 4, 4, 32, 1 }, { 32, 1, 32, 32, 5, 5, 32, 1 } };
  










  private static final byte[][] impTabR_INVERSE_NUMBERS_AS_L = { { 1, 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 1, 1, 20, 20, 0, 1 }, { 1, 0, 1, 1, 0, 0, 0, 1 }, { 1, 0, 1, 1, 5, 5, 0, 1 }, { 33, 0, 33, 33, 4, 4, 0, 0 }, { 1, 0, 1, 1, 5, 5, 0, 0 } };
  










  private static final ImpTabPair impTab_INVERSE_NUMBERS_AS_L = new ImpTabPair(impTabL_INVERSE_NUMBERS_AS_L, impTabR_INVERSE_NUMBERS_AS_L, impAct0, impAct0);
  


  private static final byte[][] impTabR_INVERSE_LIKE_DIRECT = { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 2, 19, 19, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 33, 48, 6, 4, 3, 3, 48, 0 }, { 33, 48, 6, 4, 5, 5, 48, 3 }, { 33, 48, 6, 4, 5, 5, 48, 2 }, { 33, 48, 6, 4, 3, 3, 48, 1 } };
  











  private static final short[] impAct1 = { 0, 1, 11, 12 };
  private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT = new ImpTabPair(impTabL_DEFAULT, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
  

  private static final byte[][] impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS = { { 0, 99, 0, 1, 0, 0, 0, 0 }, { 0, 99, 0, 1, 18, 48, 0, 4 }, { 32, 99, 32, 1, 2, 48, 32, 3 }, { 0, 99, 85, 86, 20, 48, 0, 3 }, { 48, 67, 85, 86, 4, 48, 48, 3 }, { 48, 67, 5, 86, 20, 48, 48, 4 }, { 48, 67, 85, 6, 20, 48, 48, 4 } };
  










  private static final byte[][] impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS = { { 19, 0, 1, 1, 0, 0, 0, 0 }, { 35, 0, 1, 1, 2, 64, 0, 1 }, { 35, 0, 1, 1, 2, 64, 0, 0 }, { 3, 0, 3, 54, 20, 64, 0, 1 }, { 83, 64, 5, 54, 4, 64, 64, 0 }, { 83, 64, 5, 54, 4, 64, 64, 1 }, { 83, 64, 6, 6, 4, 64, 64, 3 } };
  











  private static final short[] impAct2 = { 0, 1, 7, 8, 9, 10 };
  private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT_WITH_MARKS = new ImpTabPair(impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
  


  private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL = new ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
  

  private static final byte[][] impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = { { 0, 98, 1, 1, 0, 0, 0, 0 }, { 0, 98, 1, 1, 0, 48, 0, 4 }, { 0, 98, 84, 84, 19, 48, 0, 3 }, { 48, 66, 84, 84, 3, 48, 48, 3 }, { 48, 66, 4, 4, 19, 48, 48, 4 } };
  








  private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new ImpTabPair(impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
  


  static final int FIRSTALLOC = 10;
  


  public static final int DIRECTION_LEFT_TO_RIGHT = 0;
  

  public static final int DIRECTION_RIGHT_TO_LEFT = 1;
  

  public static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126;
  

  public static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127;
  


  private void addPoint(int pos, int flag)
  {
    Point point = new Point();
    
    int len = insertPoints.points.length;
    if (len == 0) {
      insertPoints.points = new Point[10];
      len = 10;
    }
    if (insertPoints.size >= len) {
      Point[] savePoints = insertPoints.points;
      insertPoints.points = new Point[len * 2];
      System.arraycopy(savePoints, 0, insertPoints.points, 0, len);
    }
    pos = pos;
    flag = flag;
    insertPoints.points[insertPoints.size] = point;
    insertPoints.size += 1;
  }
  
















  private void processPropertySeq(LevState levState, short _prop, int start, int limit)
  {
    byte[][] impTab = impTab;
    short[] impAct = impAct;
    



    int start0 = start;
    short oldStateSeq = state;
    byte cell = impTab[oldStateSeq][_prop];
    state = GetState(cell);
    short actionSeq = impAct[GetAction(cell)];
    byte addLevel = impTab[state][7];
    int k;
    byte level; if (actionSeq != 0)
      switch (actionSeq) {
      case 1: 
        startON = start0;
        break;
      
      case 2: 
        start = startON;
        break;
      

      case 3: 
        if (startL2EN >= 0) {
          addPoint(startL2EN, 1);
        }
        startL2EN = -1;
        
        if ((insertPoints.points.length == 0) || (insertPoints.size <= insertPoints.confirmed))
        {

          lastStrongRTL = -1;
          
          byte level = impTab[oldStateSeq][7];
          if (((level & 0x1) != 0) && (startON > 0)) {
            start = startON;
          }
          if (_prop == 5) {
            addPoint(start0, 1);
            insertPoints.confirmed = insertPoints.size;
          }
        }
        else
        {
          for (k = lastStrongRTL + 1; k < start0; k++)
          {
            levels[k] = ((byte)(levels[k] - 2 & 0xFFFFFFFE));
          }
          
          insertPoints.confirmed = insertPoints.size;
          lastStrongRTL = -1;
          if (_prop == 5) {
            addPoint(start0, 1);
            insertPoints.confirmed = insertPoints.size;
          }
        }
        
        break;
      case 4: 
        if (insertPoints.points.length > 0)
        {
          insertPoints.size = insertPoints.confirmed; }
        startON = -1;
        startL2EN = -1;
        lastStrongRTL = (limit - 1);
        break;
      

      case 5: 
        if ((_prop == 3) && (NoContextRTL(dirProps[start0]) == 5) && (reorderingMode != 6))
        {


          if (startL2EN == -1)
          {
            lastStrongRTL = (limit - 1);
          }
          else {
            if (startL2EN >= 0) {
              addPoint(startL2EN, 1);
              startL2EN = -2;
            }
            
            addPoint(start0, 1);
          }
          
        }
        else if (startL2EN == -1) {
          startL2EN = start0;
        }
        
        break;
      case 6: 
        lastStrongRTL = (limit - 1);
        startON = -1;
        break;
      

      case 7: 
        for (k = start0 - 1; (k >= 0) && ((levels[k] & 0x1) == 0); k--) {}
        
        if (k >= 0) {
          addPoint(k, 4);
          insertPoints.confirmed = insertPoints.size;
        }
        startON = start0;
        break;
      


      case 8: 
        addPoint(start0, 1);
        addPoint(start0, 2);
        break;
      

      case 9: 
        insertPoints.size = insertPoints.confirmed;
        if (_prop == 5) {
          addPoint(start0, 4);
          insertPoints.confirmed = insertPoints.size;
        }
        
        break;
      case 10: 
        level = (byte)(runLevel + addLevel);
        for (k = startON; k < start0; k++) {
          if (levels[k] < level) {
            levels[k] = level;
          }
        }
        insertPoints.confirmed = insertPoints.size;
        startON = start0;
        break;
      
      case 11: 
        level = runLevel;
        for (k = start0 - 1; k >= startON;) {
          if (levels[k] == level + 3) {
            for (; levels[k] == level + 3; 
                tmp763_755[tmp763_760] = ((byte)(tmp763_755[tmp763_760] - 2))) {}
            
            while (levels[k] == level) {
              k--;
            }
          }
          if (levels[k] == level + 2) {
            levels[k] = level;
          }
          else {
            levels[k] = ((byte)(level + 1));
          }
          k--; continue;
          
















          level = (byte)(runLevel + 1);
          for (k = start0 - 1; k >= startON;) {
            if (levels[k] > level) {
              int tmp876_874 = k; byte[] tmp876_871 = levels;tmp876_871[tmp876_874] = ((byte)(tmp876_871[tmp876_874] - 2));
            }
            k--; continue;
            






            throw new IllegalStateException("Internal ICU error in processPropertySeq");
          }
        } }
    if ((addLevel != 0) || (start < start0)) {
      level = (byte)(runLevel + addLevel);
      for (k = start; k < limit; k++) {
        levels[k] = level;
      }
    }
  }
  



  private byte lastL_R_AL()
  {
    for (int i = prologue.length(); i > 0;) {
      int uchar = prologue.codePointBefore(i);
      i -= Character.charCount(uchar);
      byte dirProp = (byte)getCustomizedClass(uchar);
      if (dirProp == 0) {
        return 0;
      }
      if ((dirProp == 1) || (dirProp == 13)) {
        return 1;
      }
      if (dirProp == 7) {
        return 4;
      }
    }
    return 4;
  }
  



  private byte firstL_R_AL_EN_AN()
  {
    for (int i = 0; i < epilogue.length();) {
      int uchar = epilogue.codePointAt(i);
      i += Character.charCount(uchar);
      byte dirProp = (byte)getCustomizedClass(uchar);
      if (dirProp == 0) {
        return 0;
      }
      if ((dirProp == 1) || (dirProp == 13)) {
        return 1;
      }
      if (dirProp == 2) {
        return 2;
      }
      if (dirProp == 5) {
        return 3;
      }
    }
    return 4;
  }
  
  private void resolveImplicitLevels(int start, int limit, short sor, short eor)
  {
    LevState levState = new LevState(null);
    



    short nextStrongProp = 1;
    int nextStrongPos = -1;
    








    boolean inverseRTL = (start < lastArabicPos) && ((GetParaLevelAt(start) & 0x1) > 0) && ((reorderingMode == 5) || (reorderingMode == 6));
    


    startL2EN = -1;
    lastStrongRTL = -1;
    state = 0;
    runLevel = levels[start];
    impTab = impTabPair.imptab[(runLevel & 0x1)];
    impAct = impTabPair.impact[(runLevel & 0x1)];
    if ((start == 0) && (prologue != null)) {
      byte lastStrong = lastL_R_AL();
      if (lastStrong != 4) {
        sor = (short)lastStrong;
      }
    }
    processPropertySeq(levState, sor, start, start);
    short stateImp;
    short stateImp; if (NoContextRTL(dirProps[start]) == 17) {
      stateImp = (short)(1 + sor);
    } else {
      stateImp = 0;
    }
    int start1 = start;
    int start2 = 0;
    
    for (int i = start; i <= limit; i++) { short gprop;
      short gprop; if (i >= limit) {
        gprop = eor;
      }
      else {
        short prop = (short)NoContextRTL(dirProps[i]);
        if (inverseRTL) {
          if (prop == 13)
          {
            prop = 1;
          } else if (prop == 2) {
            if (nextStrongPos <= i)
            {

              nextStrongProp = 1;
              nextStrongPos = limit;
              for (int j = i + 1; j < limit; j++) {
                short prop1 = (short)NoContextRTL(dirProps[j]);
                if ((prop1 == 0) || (prop1 == 1) || (prop1 == 13)) {
                  nextStrongProp = prop1;
                  nextStrongPos = j;
                  break;
                }
              }
            }
            if (nextStrongProp == 13) {
              prop = 5;
            }
          }
        }
        gprop = groupProp[prop];
      }
      short oldStateImp = stateImp;
      short cell = impTabProps[oldStateImp][gprop];
      stateImp = GetStateProps(cell);
      short actionImp = GetActionProps(cell);
      if ((i == limit) && (actionImp == 0))
      {
        actionImp = 1;
      }
      if (actionImp != 0) {
        short resProp = impTabProps[oldStateImp][13];
        switch (actionImp) {
        case 1: 
          processPropertySeq(levState, resProp, start1, i);
          start1 = i;
          break;
        case 2: 
          start2 = i;
          break;
        case 3: 
          processPropertySeq(levState, resProp, start1, start2);
          processPropertySeq(levState, (short)4, start2, i);
          start1 = i;
          break;
        case 4: 
          processPropertySeq(levState, resProp, start1, start2);
          start1 = start2;
          start2 = i;
          break;
        default: 
          throw new IllegalStateException("Internal ICU error in resolveImplicitLevels");
        }
        
      }
    }
    if ((limit == length) && (epilogue != null)) {
      byte firstStrong = firstL_R_AL_EN_AN();
      if (firstStrong != 4) {
        eor = (short)firstStrong;
      }
    }
    processPropertySeq(levState, eor, limit, limit);
  }
  









  private void adjustWSLevels()
  {
    if ((flags & MASK_WS) != 0)
    {
      int i = trailingWSStart;
      for (;;) { if (i <= 0) return;
        int flag;
        while ((i > 0) && (((flag = DirPropFlagNC(dirProps[(--i)])) & MASK_WS) != 0)) {
          if ((orderParagraphsLTR) && ((flag & DirPropFlag((byte)7)) != 0)) {
            levels[i] = 0;
          } else {
            levels[i] = GetParaLevelAt(i);
          }
        }
        
        int flag;
        
        while (i > 0) {
          flag = DirPropFlagNC(dirProps[(--i)]);
          if ((flag & MASK_BN_EXPLICIT) == 0) break label128;
          levels[i] = levels[(i + 1)]; }
        continue; label128: if ((orderParagraphsLTR) && ((flag & DirPropFlag((byte)7)) != 0)) {
          levels[i] = 0;
        } else {
          if ((flag & MASK_B_S) == 0) break;
          levels[i] = GetParaLevelAt(i);
        }
      }
    }
  }
  

  int Bidi_Min(int x, int y)
  {
    return x < y ? x : y;
  }
  
  int Bidi_Abs(int x) {
    return x >= 0 ? x : -x;
  }
  










  void setParaRunsOnly(char[] parmText, byte parmParaLevel)
  {
    reorderingMode = 0;
    int parmLength = parmText.length;
    if (parmLength == 0) {
      setPara(parmText, parmParaLevel, null);
      reorderingMode = 3;
      return;
    }
    
    int saveOptions = reorderingOptions;
    if ((saveOptions & 0x1) > 0) {
      reorderingOptions &= 0xFFFFFFFE;
      reorderingOptions |= 0x2;
    }
    parmParaLevel = (byte)(parmParaLevel & 0x1);
    setPara(parmText, parmParaLevel, null);
    


    byte[] saveLevels = new byte[length];
    System.arraycopy(getLevels(), 0, saveLevels, 0, length);
    int saveTrailingWSStart = trailingWSStart;
    






    String visualText = writeReordered(2);
    int[] visualMap = getVisualMap();
    reorderingOptions = saveOptions;
    int saveLength = length;
    byte saveDirection = direction;
    
    reorderingMode = 5;
    parmParaLevel = (byte)(parmParaLevel ^ 0x1);
    setPara(visualText, parmParaLevel, null);
    BidiLine.getRuns(this);
    
    int addedRuns = 0;
    int oldRunCount = runCount;
    int visualStart = 0;
    int runLength; for (int i = 0; i < oldRunCount; visualStart += runLength) {
      runLength = runs[i].limit - visualStart;
      if (runLength >= 2)
      {

        int logicalStart = runs[i].start;
        for (int j = logicalStart + 1; j < logicalStart + runLength; j++) {
          int index = visualMap[j];
          int index1 = visualMap[(j - 1)];
          if ((Bidi_Abs(index - index1) != 1) || (saveLevels[index] != saveLevels[index1])) {
            addedRuns++;
          }
        }
      }
      i++;
    }
    











    if (addedRuns > 0) {
      getRunsMemory(oldRunCount + addedRuns);
      if (runCount == 1)
      {
        runsMemory[0] = runs[0];
      } else {
        System.arraycopy(runs, 0, runsMemory, 0, runCount);
      }
      runs = runsMemory;
      runCount += addedRuns;
      for (i = oldRunCount; i < runCount; i++) {
        if (runs[i] == null) {
          runs[i] = new BidiRun(0, 0, 0);
        }
      }
    }
    

    for (i = oldRunCount - 1; i >= 0; i--) {
      int newI = i + addedRuns;
      int runLength = i == 0 ? runs[0].limit : runs[i].limit - runs[(i - 1)].limit;
      
      int logicalStart = runs[i].start;
      int indexOddBit = runs[i].level & 0x1;
      if (runLength < 2) {
        if (addedRuns > 0) {
          runs[newI].copyFrom(runs[i]);
        }
        int logicalPos = visualMap[logicalStart];
        runs[newI].start = logicalPos;
        runs[newI].level = ((byte)(saveLevels[logicalPos] ^ indexOddBit)); } else { int step;
        int start;
        int limit;
        int step; if (indexOddBit > 0) {
          int start = logicalStart;
          int limit = logicalStart + runLength - 1;
          step = 1;
        } else {
          start = logicalStart + runLength - 1;
          limit = logicalStart;
          step = -1;
        }
        for (int j = start; j != limit; j += step) {
          int index = visualMap[j];
          int index1 = visualMap[(j + step)];
          if ((Bidi_Abs(index - index1) != 1) || (saveLevels[index] != saveLevels[index1])) {
            int logicalPos = Bidi_Min(visualMap[start], index);
            runs[newI].start = logicalPos;
            runs[newI].level = ((byte)(saveLevels[logicalPos] ^ indexOddBit));
            runs[newI].limit = runs[i].limit;
            runs[i].limit -= Bidi_Abs(j - start) + 1;
            int insertRemove = runs[i].insertRemove & 0xA;
            runs[newI].insertRemove = insertRemove;
            runs[i].insertRemove &= (insertRemove ^ 0xFFFFFFFF);
            start = j + step;
            addedRuns--;
            newI--;
          }
        }
        if (addedRuns > 0) {
          runs[newI].copyFrom(runs[i]);
        }
        int logicalPos = Bidi_Min(visualMap[start], visualMap[limit]);
        runs[newI].start = logicalPos;
        runs[newI].level = ((byte)(saveLevels[logicalPos] ^ indexOddBit));
      }
    }
    

    paraLevel = ((byte)(paraLevel ^ 0x1));
    

    text = parmText;
    length = saveLength;
    originalLength = parmLength;
    direction = saveDirection;
    levels = saveLevels;
    trailingWSStart = saveTrailingWSStart;
    if (runCount > 1) {
      direction = 2;
    }
    
    reorderingMode = 3;
  }
  
  private void setParaSuccess() {
    prologue = null;
    epilogue = null;
    paraBidi = this;
  }
  



































































  public void setContext(String prologue, String epilogue)
  {
    this.prologue = ((prologue != null) && (prologue.length() > 0) ? prologue : null);
    this.epilogue = ((epilogue != null) && (epilogue.length() > 0) ? epilogue : null);
  }
  












































































  public void setPara(String text, byte paraLevel, byte[] embeddingLevels)
  {
    if (text == null) {
      setPara(new char[0], paraLevel, embeddingLevels);
    } else {
      setPara(text.toCharArray(), paraLevel, embeddingLevels);
    }
  }
  












































































  public void setPara(char[] chars, byte paraLevel, byte[] embeddingLevels)
  {
    if (paraLevel < 126) {
      verifyRange(paraLevel, 0, 62);
    }
    if (chars == null) {
      chars = new char[0];
    }
    

    if (reorderingMode == 3) {
      setParaRunsOnly(chars, paraLevel);
      return;
    }
    

    paraBidi = null;
    text = chars;
    length = (this.originalLength = this.resultLength = text.length);
    this.paraLevel = paraLevel;
    direction = 0;
    paraCount = 1;
    



    dirProps = new byte[0];
    levels = new byte[0];
    runs = new BidiRun[0];
    isGoodLogicalToVisualRunsMap = false;
    insertPoints.size = 0;
    insertPoints.confirmed = 0;
    



    if (IsDefaultLevel(paraLevel)) {
      defaultParaLevel = paraLevel;
    } else {
      defaultParaLevel = 0;
    }
    
    if (length == 0)
    {




      if (IsDefaultLevel(paraLevel)) {
        this.paraLevel = ((byte)(this.paraLevel & 0x1));
        defaultParaLevel = 0;
      }
      if ((this.paraLevel & 0x1) != 0) {
        flags = DirPropFlag((byte)1);
        direction = 1;
      } else {
        flags = DirPropFlag((byte)0);
        direction = 0;
      }
      
      runCount = 0;
      paraCount = 0;
      setParaSuccess();
      return;
    }
    
    runCount = -1;
    





    getDirPropsMemory(length);
    dirProps = dirPropsMemory;
    getDirProps();
    
    trailingWSStart = length;
    

    if (paraCount > 1) {
      getInitialParasMemory(paraCount);
      paras = parasMemory;
      paras[(paraCount - 1)] = length;
    }
    else {
      paras = simpleParas;
      simpleParas[0] = length;
    }
    

    if (embeddingLevels == null)
    {
      getLevelsMemory(length);
      levels = levelsMemory;
      direction = resolveExplicitLevels();
    }
    else {
      levels = embeddingLevels;
      direction = checkExplicitLevels();
    }
    




    switch (direction)
    {
    case 0: 
      paraLevel = (byte)(paraLevel + 1 & 0xFFFFFFFE);
      

      trailingWSStart = 0;
      break;
    
    case 1: 
      paraLevel = (byte)(paraLevel | 0x1);
      

      trailingWSStart = 0;
      break;
    


    default: 
      switch (reorderingMode) {
      case 0: 
        impTabPair = impTab_DEFAULT;
        break;
      case 1: 
        impTabPair = impTab_NUMBERS_SPECIAL;
        break;
      case 2: 
        impTabPair = impTab_GROUP_NUMBERS_WITH_R;
        break;
      
      case 3: 
        throw new InternalError("Internal ICU error in setPara");
      
      case 4: 
        impTabPair = impTab_INVERSE_NUMBERS_AS_L;
        break;
      case 5: 
        if ((reorderingOptions & 0x1) != 0) {
          impTabPair = impTab_INVERSE_LIKE_DIRECT_WITH_MARKS;
        } else {
          impTabPair = impTab_INVERSE_LIKE_DIRECT;
        }
        break;
      case 6: 
        if ((reorderingOptions & 0x1) != 0) {
          impTabPair = impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS;
        } else {
          impTabPair = impTab_INVERSE_FOR_NUMBERS_SPECIAL;
        }
        




        break;
      }
      
      




      if ((embeddingLevels == null) && (paraCount <= 1) && ((flags & DirPropFlagMultiRuns) == 0))
      {
        resolveImplicitLevels(0, length, (short)GetLRFromLevel(GetParaLevelAt(0)), (short)GetLRFromLevel(GetParaLevelAt(length - 1)));

      }
      else
      {
        int limit = 0;
        



        byte level = GetParaLevelAt(0);
        byte nextLevel = levels[0];
        short eor; short eor; if (level < nextLevel) {
          eor = (short)GetLRFromLevel(nextLevel);
        } else {
          eor = (short)GetLRFromLevel(level);
        }
        


        do
        {
          int start = limit;
          level = nextLevel;
          short sor; short sor; if ((start > 0) && (NoContextRTL(dirProps[(start - 1)]) == 7))
          {
            sor = (short)GetLRFromLevel(GetParaLevelAt(start));
          } else {
            sor = eor;
          }
          do
          {
            limit++; } while ((limit < length) && (levels[limit] == level));
          

          if (limit < length) {
            nextLevel = levels[limit];
          } else {
            nextLevel = GetParaLevelAt(length - 1);
          }
          

          if ((level & 0x7F) < (nextLevel & 0x7F)) {
            eor = (short)GetLRFromLevel(nextLevel);
          } else {
            eor = (short)GetLRFromLevel(level);
          }
          


          if ((level & 0xFFFFFF80) == 0) {
            resolveImplicitLevels(start, limit, sor, eor);
          } else {
            do
            {
              int tmp852_849 = (start++); byte[] tmp852_844 = levels;tmp852_844[tmp852_849] = ((byte)(tmp852_844[tmp852_849] & 0x7F));
            } while (start < limit);
          }
        } while (limit < length);
      }
      

      adjustWSLevels();
    }
    
    



    if ((defaultParaLevel > 0) && ((reorderingOptions & 0x1) != 0) && ((reorderingMode == 5) || (reorderingMode == 6)))
    {




      for (int i = 0; i < paraCount; i++) {
        int last = paras[i] - 1;
        if ((dirProps[last] & 0x40) != 0)
        {

          int start = i == 0 ? 0 : paras[(i - 1)];
          for (int j = last; j >= start; j--) {
            byte dirProp = NoContextRTL(dirProps[j]);
            if (dirProp == 0) {
              if (j < last) {
                while (NoContextRTL(dirProps[last]) == 7) {
                  last--;
                }
              }
              addPoint(last, 4);
            }
            else {
              if ((DirPropFlag(dirProp) & 0x2002) != 0)
                break;
            }
          }
        }
      }
    }
    if ((reorderingOptions & 0x2) != 0) {
      resultLength -= controlCount;
    } else {
      resultLength += insertPoints.size;
    }
    setParaSuccess();
  }
  









































  public void setPara(AttributedCharacterIterator paragraph)
  {
    Boolean runDirection = (Boolean)paragraph.getAttribute(TextAttribute.RUN_DIRECTION);
    byte paraLvl; byte paraLvl; if (runDirection == null) {
      paraLvl = 126;
    } else {
      paraLvl = runDirection.equals(TextAttribute.RUN_DIRECTION_LTR) ? 0 : 1;
    }
    

    byte[] lvls = null;
    int len = paragraph.getEndIndex() - paragraph.getBeginIndex();
    byte[] embeddingLevels = new byte[len];
    char[] txt = new char[len];
    int i = 0;
    char ch = paragraph.first();
    while (ch != 65535) {
      txt[i] = ch;
      Integer embedding = (Integer)paragraph.getAttribute(TextAttribute.BIDI_EMBEDDING);
      if (embedding != null) {
        byte level = embedding.byteValue();
        if (level != 0)
        {
          if (level < 0) {
            lvls = embeddingLevels;
            embeddingLevels[i] = ((byte)(0 - level | 0xFFFFFF80));
          } else {
            lvls = embeddingLevels;
            embeddingLevels[i] = level;
          } }
      }
      ch = paragraph.next();
      i++;
    }
    
    NumericShaper shaper = (NumericShaper)paragraph.getAttribute(TextAttribute.NUMERIC_SHAPING);
    if (shaper != null) {
      shaper.shape(txt, 0, len);
    }
    setPara(txt, paraLvl, lvls);
  }
  

















  public void orderParagraphsLTR(boolean ordarParaLTR)
  {
    orderParagraphsLTR = ordarParaLTR;
  }
  








  public boolean isOrderParagraphsLTR()
  {
    return orderParagraphsLTR;
  }
  
















  public byte getDirection()
  {
    verifyValidParaOrLine();
    return direction;
  }
  













  public String getTextAsString()
  {
    verifyValidParaOrLine();
    return new String(text);
  }
  













  public char[] getText()
  {
    verifyValidParaOrLine();
    return text;
  }
  










  public int getLength()
  {
    verifyValidParaOrLine();
    return originalLength;
  }
  







































  public int getProcessedLength()
  {
    verifyValidParaOrLine();
    return length;
  }
  




























  public int getResultLength()
  {
    verifyValidParaOrLine();
    return resultLength;
  }
  



















  public byte getParaLevel()
  {
    verifyValidParaOrLine();
    return paraLevel;
  }
  









  public int countParagraphs()
  {
    verifyValidParaOrLine();
    return paraCount;
  }
  






















  public BidiRun getParagraphByIndex(int paraIndex)
  {
    verifyValidParaOrLine();
    verifyRange(paraIndex, 0, paraCount);
    
    Bidi bidi = paraBidi;
    int paraStart;
    int paraStart; if (paraIndex == 0) {
      paraStart = 0;
    } else {
      paraStart = paras[(paraIndex - 1)];
    }
    BidiRun bidiRun = new BidiRun();
    start = paraStart;
    limit = paras[paraIndex];
    level = GetParaLevelAt(paraStart);
    return bidiRun;
  }
  
























  public BidiRun getParagraph(int charIndex)
  {
    verifyValidParaOrLine();
    Bidi bidi = paraBidi;
    verifyRange(charIndex, 0, length);
    
    for (int paraIndex = 0; charIndex >= paras[paraIndex]; paraIndex++) {}
    
    return getParagraphByIndex(paraIndex);
  }
  

















  public int getParagraphIndex(int charIndex)
  {
    verifyValidParaOrLine();
    Bidi bidi = paraBidi;
    verifyRange(charIndex, 0, length);
    
    for (int paraIndex = 0; charIndex >= paras[paraIndex]; paraIndex++) {}
    
    return paraIndex;
  }
  








  public void setCustomClassifier(BidiClassifier classifier)
  {
    customClassifier = classifier;
  }
  








  public BidiClassifier getCustomClassifier()
  {
    return customClassifier;
  }
  







  public int getCustomizedClass(int c)
  {
    int dir;
    






    if ((customClassifier == null) || ((dir = customClassifier.classify(c)) == 19))
    {
      return bdp.getClass(c); }
    int dir;
    return dir;
  }
  








































  public Bidi setLine(int start, int limit)
  {
    verifyValidPara();
    verifyRange(start, 0, limit);
    verifyRange(limit, 0, length + 1);
    if (getParagraphIndex(start) != getParagraphIndex(limit - 1))
    {
      throw new IllegalArgumentException();
    }
    return BidiLine.setLine(this, start, limit);
  }
  















  public byte getLevelAt(int charIndex)
  {
    verifyValidParaOrLine();
    verifyRange(charIndex, 0, length);
    return BidiLine.getLevelAt(this, charIndex);
  }
  













  public byte[] getLevels()
  {
    verifyValidParaOrLine();
    if (length <= 0) {
      return new byte[0];
    }
    return BidiLine.getLevels(this);
  }
  

























  public BidiRun getLogicalRun(int logicalPosition)
  {
    verifyValidParaOrLine();
    verifyRange(logicalPosition, 0, length);
    return BidiLine.getLogicalRun(this, logicalPosition);
  }
  














  public int countRuns()
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    return runCount;
  }
  
































































  public BidiRun getVisualRun(int runIndex)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    verifyRange(runIndex, 0, runCount);
    return BidiLine.getVisualRun(this, runIndex);
  }
  










































  public int getVisualIndex(int logicalIndex)
  {
    verifyValidParaOrLine();
    verifyRange(logicalIndex, 0, length);
    return BidiLine.getVisualIndex(this, logicalIndex);
  }
  






































  public int getLogicalIndex(int visualIndex)
  {
    verifyValidParaOrLine();
    verifyRange(visualIndex, 0, resultLength);
    
    if ((insertPoints.size == 0) && (controlCount == 0)) {
      if (direction == 0) {
        return visualIndex;
      }
      if (direction == 1) {
        return length - visualIndex - 1;
      }
    }
    BidiLine.getRuns(this);
    return BidiLine.getLogicalIndex(this, visualIndex);
  }
  








































  public int[] getLogicalMap()
  {
    countRuns();
    if (length <= 0) {
      return new int[0];
    }
    return BidiLine.getLogicalMap(this);
  }
  

































  public int[] getVisualMap()
  {
    countRuns();
    if (resultLength <= 0) {
      return new int[0];
    }
    return BidiLine.getVisualMap(this);
  }
  


















  public static int[] reorderLogical(byte[] levels)
  {
    return BidiLine.reorderLogical(levels);
  }
  


















  public static int[] reorderVisual(byte[] levels)
  {
    return BidiLine.reorderVisual(levels);
  }
  


































  public static int[] invertMap(int[] srcMap)
  {
    if (srcMap == null) {
      return null;
    }
    return BidiLine.invertMap(srcMap);
  }
  

















































  public Bidi(String paragraph, int flags)
  {
    this(paragraph.toCharArray(), 0, null, 0, paragraph.length(), flags);
  }
  


























  public Bidi(AttributedCharacterIterator paragraph)
  {
    this();
    setPara(paragraph);
  }
  







































  public Bidi(char[] text, int textStart, byte[] embeddings, int embStart, int paragraphLength, int flags)
  {
    this();
    byte paraLvl;
    switch (flags) {
    case 0: 
    default: 
      paraLvl = 0;
      break;
    case 1: 
      paraLvl = 1;
      break;
    case 126: 
      paraLvl = 126;
      break;
    case 127: 
      paraLvl = Byte.MAX_VALUE;
    }
    byte[] paraEmbeddings;
    byte[] paraEmbeddings;
    if (embeddings == null) {
      paraEmbeddings = null;
    } else {
      paraEmbeddings = new byte[paragraphLength];
      
      for (int i = 0; i < paragraphLength; i++) {
        byte lev = embeddings[(i + embStart)];
        if (lev < 0) {
          lev = (byte)(-lev | 0xFFFFFF80);
        } else if (lev == 0) {
          lev = paraLvl;
          if (paraLvl > 61) {
            lev = (byte)(lev & 0x1);
          }
        }
        paraEmbeddings[i] = lev;
      }
    }
    if ((textStart == 0) && (embStart == 0) && (paragraphLength == text.length)) {
      setPara(text, paraLvl, paraEmbeddings);
    } else {
      char[] paraText = new char[paragraphLength];
      System.arraycopy(text, textStart, paraText, 0, paragraphLength);
      setPara(paraText, paraLvl, paraEmbeddings);
    }
  }
  

















  public Bidi createLineBidi(int lineStart, int lineLimit)
  {
    return setLine(lineStart, lineLimit);
  }
  











  public boolean isMixed()
  {
    return (!isLeftToRight()) && (!isRightToLeft());
  }
  











  public boolean isLeftToRight()
  {
    return (getDirection() == 0) && ((paraLevel & 0x1) == 0);
  }
  











  public boolean isRightToLeft()
  {
    return (getDirection() == 1) && ((paraLevel & 0x1) == 1);
  }
  










  public boolean baseIsLeftToRight()
  {
    return getParaLevel() == 0;
  }
  










  public int getBaseLevel()
  {
    return getParaLevel();
  }
  










  public int getRunCount()
  {
    return countRuns();
  }
  



  void getLogicalToVisualRunsMap()
  {
    if (isGoodLogicalToVisualRunsMap) {
      return;
    }
    int count = countRuns();
    if ((logicalToVisualRunsMap == null) || (logicalToVisualRunsMap.length < count))
    {
      logicalToVisualRunsMap = new int[count];
    }
    
    long[] keys = new long[count];
    for (int i = 0; i < count; i++) {
      keys[i] = ((runs[i].start << 32) + i);
    }
    Arrays.sort(keys);
    for (i = 0; i < count; i++) {
      logicalToVisualRunsMap[i] = ((int)(keys[i] & 0xFFFFFFFFFFFFFFFF));
    }
    isGoodLogicalToVisualRunsMap = true;
  }
  













  public int getRunLevel(int run)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    verifyRange(run, 0, runCount);
    getLogicalToVisualRunsMap();
    return runs[logicalToVisualRunsMap[run]].level;
  }
  














  public int getRunStart(int run)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    verifyRange(run, 0, runCount);
    getLogicalToVisualRunsMap();
    return runs[logicalToVisualRunsMap[run]].start;
  }
  















  public int getRunLimit(int run)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    verifyRange(run, 0, runCount);
    getLogicalToVisualRunsMap();
    int idx = logicalToVisualRunsMap[run];
    int len = idx == 0 ? runs[idx].limit : runs[idx].limit - runs[(idx - 1)].limit;
    
    return runs[idx].start + len;
  }
  

















  public static boolean requiresBidi(char[] text, int start, int limit)
  {
    int RTLMask = 57378;
    




    for (int i = start; i < limit; i++) {
      if ((1 << UCharacter.getDirection(text[i]) & 0xE022) != 0) {
        return true;
      }
    }
    return false;
  }
  





















  public static void reorderVisually(byte[] levels, int levelStart, Object[] objects, int objectStart, int count)
  {
    byte[] reorderLevels = new byte[count];
    System.arraycopy(levels, levelStart, reorderLevels, 0, count);
    int[] indexMap = reorderVisual(reorderLevels);
    Object[] temp = new Object[count];
    System.arraycopy(objects, objectStart, temp, 0, count);
    for (int i = 0; i < count; i++) {
      objects[(objectStart + i)] = temp[indexMap[i]];
    }
  }
  


























































  public String writeReordered(int options)
  {
    verifyValidParaOrLine();
    if (length == 0)
    {
      return "";
    }
    
    return BidiWriter.writeReordered(this, options);
  }
  






































  public static String writeReverse(String src, int options)
  {
    if (src == null) {
      throw new IllegalArgumentException();
    }
    
    if (src.length() > 0) {
      return BidiWriter.writeReverse(src, options);
    }
    
    return "";
  }
  
















  public static byte getBaseDirection(CharSequence paragraph)
  {
    if ((paragraph == null) || (paragraph.length() == 0)) {
      return 3;
    }
    
    int length = paragraph.length();
    


    for (int i = 0; i < length;)
    {
      int c = UCharacter.codePointAt(paragraph, i);
      byte direction = UCharacter.getDirectionality(c);
      if (direction == 0)
        return 0;
      if ((direction == 1) || (direction == 13))
      {
        return 1;
      }
      
      i = UCharacter.offsetByCodePoints(paragraph, i, 1);
    }
    return 3;
  }
  
  private static class LevState
  {
    byte[][] impTab;
    short[] impAct;
    int startON;
    int startL2EN;
    int lastStrongRTL;
    short state;
    byte runLevel;
    
    private LevState() {}
  }
}
