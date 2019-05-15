package com.ibm.icu.impl;

import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.BytesTrie.Result;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
















































public final class UPropertyAliases
{
  private static final int IX_VALUE_MAPS_OFFSET = 0;
  private static final int IX_BYTE_TRIES_OFFSET = 1;
  private static final int IX_NAME_GROUPS_OFFSET = 2;
  private static final int IX_RESERVED3_OFFSET = 3;
  private int[] valueMaps;
  private byte[] bytesTries;
  private String nameGroups;
  
  private static final class IsAcceptable
    implements ICUBinary.Authenticate
  {
    private IsAcceptable() {}
    
    public boolean isDataVersionAcceptable(byte[] version) { return version[0] == 2; }
  }
  
  private static final IsAcceptable IS_ACCEPTABLE = new IsAcceptable(null);
  private static final byte[] DATA_FORMAT = { 112, 110, 97, 109 };
  public static final UPropertyAliases INSTANCE;
  
  private void load(InputStream data) throws IOException { BufferedInputStream bis = new BufferedInputStream(data);
    
    ICUBinary.readHeader(bis, DATA_FORMAT, IS_ACCEPTABLE);
    DataInputStream ds = new DataInputStream(bis);
    int indexesLength = ds.readInt() / 4;
    if (indexesLength < 8) {
      throw new IOException("pnames.icu: not enough indexes");
    }
    int[] inIndexes = new int[indexesLength];
    inIndexes[0] = (indexesLength * 4);
    for (int i = 1; i < indexesLength; i++) {
      inIndexes[i] = ds.readInt();
    }
    

    int offset = inIndexes[0];
    int nextOffset = inIndexes[1];
    int numInts = (nextOffset - offset) / 4;
    valueMaps = new int[numInts];
    for (int i = 0; i < numInts; i++) {
      valueMaps[i] = ds.readInt();
    }
    

    offset = nextOffset;
    nextOffset = inIndexes[2];
    int numBytes = nextOffset - offset;
    bytesTries = new byte[numBytes];
    ds.readFully(bytesTries);
    

    offset = nextOffset;
    nextOffset = inIndexes[3];
    numBytes = nextOffset - offset;
    StringBuilder sb = new StringBuilder(numBytes);
    for (int i = 0; i < numBytes; i++) {
      sb.append((char)ds.readByte());
    }
    nameGroups = sb.toString();
    
    data.close();
  }
  
  private UPropertyAliases() throws IOException {
    load(ICUData.getRequiredStream("data/icudt51b/pnames.icu"));
  }
  
  private int findProperty(int property) {
    int i = 1;
    for (int numRanges = valueMaps[0]; numRanges > 0; numRanges--)
    {
      int start = valueMaps[i];
      int limit = valueMaps[(i + 1)];
      i += 2;
      if (property < start) {
        break;
      }
      if (property < limit) {
        return i + (property - start) * 2;
      }
      i += (limit - start) * 2;
    }
    return 0;
  }
  
  private int findPropertyValueNameGroup(int valueMapIndex, int value) {
    if (valueMapIndex == 0) {
      return 0;
    }
    valueMapIndex++;
    int numRanges = valueMaps[(valueMapIndex++)];
    if (numRanges < 16) {
      for (; 
          numRanges > 0; numRanges--)
      {
        int start = valueMaps[valueMapIndex];
        int limit = valueMaps[(valueMapIndex + 1)];
        valueMapIndex += 2;
        if (value < start) {
          break;
        }
        if (value < limit) {
          return valueMaps[(valueMapIndex + value - start)];
        }
        valueMapIndex += limit - start;
      }
    }
    
    int valuesStart = valueMapIndex;
    int nameGroupOffsetsStart = valueMapIndex + numRanges - 16;
    do {
      int v = valueMaps[valueMapIndex];
      if (value < v) {
        break;
      }
      if (value == v) {
        return valueMaps[(nameGroupOffsetsStart + valueMapIndex - valuesStart)];
      }
      valueMapIndex++; } while (valueMapIndex < nameGroupOffsetsStart);
    
    return 0;
  }
  
  private String getName(int nameGroupsIndex, int nameIndex) {
    int numNames = nameGroups.charAt(nameGroupsIndex++);
    if ((nameIndex < 0) || (numNames <= nameIndex)) {
      throw new IllegalIcuArgumentException("Invalid property (value) name choice");
    }
    for (; 
        nameIndex > 0; nameIndex--) {
      while ('\000' != nameGroups.charAt(nameGroupsIndex++)) {}
    }
    
    int nameStart = nameGroupsIndex;
    while ('\000' != nameGroups.charAt(nameGroupsIndex)) {
      nameGroupsIndex++;
    }
    if (nameStart == nameGroupsIndex) {
      return null;
    }
    return nameGroups.substring(nameStart, nameGroupsIndex);
  }
  
  private static int asciiToLowercase(int c) {
    return (65 <= c) && (c <= 90) ? c + 32 : c;
  }
  
  private boolean containsName(BytesTrie trie, CharSequence name) {
    BytesTrie.Result result = BytesTrie.Result.NO_VALUE;
    for (int i = 0; i < name.length(); i++) {
      int c = name.charAt(i);
      
      if ((c != 45) && (c != 95) && (c != 32) && ((9 > c) || (c > 13)))
      {

        if (!result.hasNext()) {
          return false;
        }
        c = asciiToLowercase(c);
        result = trie.next(c);
      } }
    return result.hasValue();
  }
  



  static
  {
    try
    {
      INSTANCE = new UPropertyAliases();
    }
    catch (IOException e) {
      MissingResourceException mre = new MissingResourceException("Could not construct UPropertyAliases. Missing pnames.icu", "", "");
      
      mre.initCause(e);
      throw mre;
    }
  }
  





  public String getPropertyName(int property, int nameChoice)
  {
    int valueMapIndex = findProperty(property);
    if (valueMapIndex == 0) {
      throw new IllegalArgumentException("Invalid property enum " + property + " (0x" + Integer.toHexString(property) + ")");
    }
    
    return getName(valueMaps[valueMapIndex], nameChoice);
  }
  




  public String getPropertyValueName(int property, int value, int nameChoice)
  {
    int valueMapIndex = findProperty(property);
    if (valueMapIndex == 0) {
      throw new IllegalArgumentException("Invalid property enum " + property + " (0x" + Integer.toHexString(property) + ")");
    }
    
    int nameGroupOffset = findPropertyValueNameGroup(valueMaps[(valueMapIndex + 1)], value);
    if (nameGroupOffset == 0) {
      throw new IllegalArgumentException("Property " + property + " (0x" + Integer.toHexString(property) + ") does not have named values");
    }
    

    return getName(nameGroupOffset, nameChoice);
  }
  
  private int getPropertyOrValueEnum(int bytesTrieOffset, CharSequence alias) {
    BytesTrie trie = new BytesTrie(bytesTries, bytesTrieOffset);
    if (containsName(trie, alias)) {
      return trie.getValue();
    }
    return -1;
  }
  





  public int getPropertyEnum(CharSequence alias)
  {
    return getPropertyOrValueEnum(0, alias);
  }
  


  public int getPropertyValueEnum(int property, CharSequence alias)
  {
    int valueMapIndex = findProperty(property);
    if (valueMapIndex == 0) {
      throw new IllegalArgumentException("Invalid property enum " + property + " (0x" + Integer.toHexString(property) + ")");
    }
    
    valueMapIndex = valueMaps[(valueMapIndex + 1)];
    if (valueMapIndex == 0) {
      throw new IllegalArgumentException("Property " + property + " (0x" + Integer.toHexString(property) + ") does not have named values");
    }
    



    return getPropertyOrValueEnum(valueMaps[valueMapIndex], alias);
  }
  






  public static int compare(String stra, String strb)
  {
    int istra = 0;int istrb = 0;
    int cstra = 0;int cstrb = 0;
    for (;;)
    {
      if (istra < stra.length()) {
        cstra = stra.charAt(istra);
        switch (cstra) {
        case 9: case 10: case 11: case 12: 
        case 13: case 32: case 45: case 95: 
          istra++;
          break;
        }
      }
      else
      {
        while (istrb < strb.length()) {
          cstrb = strb.charAt(istrb);
          switch (cstrb) {
          case 9: case 10: case 11: case 12: 
          case 13: case 32: case 45: case 95: 
            istrb++;
            break;
          }
          
        }
        

        boolean endstra = istra == stra.length();
        boolean endstrb = istrb == strb.length();
        if (endstra) {
          if (endstrb) return 0;
          cstra = 0;
        } else if (endstrb) {
          cstrb = 0;
        }
        
        int rc = asciiToLowercase(cstra) - asciiToLowercase(cstrb);
        if (rc != 0) {
          return rc;
        }
        
        istra++;
        istrb++;
      }
    }
  }
}
