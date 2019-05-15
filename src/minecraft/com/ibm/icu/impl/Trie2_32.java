package com.ibm.icu.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;







































public class Trie2_32
  extends Trie2
{
  Trie2_32() {}
  
  public static Trie2_32 createFromSerialized(InputStream is)
    throws IOException
  {
    return (Trie2_32)Trie2.createFromSerialized(is);
  }
  









  public final int get(int codePoint)
  {
    if (codePoint >= 0) {
      if ((codePoint < 55296) || ((codePoint > 56319) && (codePoint <= 65535)))
      {


        int ix = index[(codePoint >> 5)];
        ix = (ix << 2) + (codePoint & 0x1F);
        int value = data32[ix];
        return value;
      }
      if (codePoint <= 65535)
      {





        int ix = index[(2048 + (codePoint - 55296 >> 5))];
        ix = (ix << 2) + (codePoint & 0x1F);
        int value = data32[ix];
        return value;
      }
      if (codePoint < highStart)
      {
        int ix = 2080 + (codePoint >> 11);
        ix = index[ix];
        ix += (codePoint >> 5 & 0x3F);
        ix = index[ix];
        ix = (ix << 2) + (codePoint & 0x1F);
        int value = data32[ix];
        return value;
      }
      if (codePoint <= 1114111) {
        int value = data32[highValueIndex];
        return value;
      }
    }
    

    return errorValue;
  }
  

















  public int getFromU16SingleLead(char codeUnit)
  {
    int ix = index[(codeUnit >> '\005')];
    ix = (ix << 2) + (codeUnit & 0x1F);
    int value = data32[ix];
    return value;
  }
  










  public int serialize(OutputStream os)
    throws IOException
  {
    DataOutputStream dos = new DataOutputStream(os);
    int bytesWritten = 0;
    
    bytesWritten += serializeHeader(dos);
    for (int i = 0; i < dataLength; i++) {
      dos.writeInt(data32[i]);
    }
    bytesWritten += dataLength * 4;
    return bytesWritten;
  }
  


  public int getSerializedLength()
  {
    return 16 + header.indexLength * 2 + dataLength * 4;
  }
  









  int rangeEnd(int startingCP, int limit, int value)
  {
    int cp = startingCP;
    int block = 0;
    int index2Block = 0;
    







    while (cp < limit)
    {

      if ((cp < 55296) || ((cp > 56319) && (cp <= 65535)))
      {


        index2Block = 0;
        block = index[(cp >> 5)] << '\002';
      } else if (cp < 65535)
      {
        index2Block = 2048;
        block = index[(index2Block + (cp - 55296 >> 5))] << '\002';
      } else if (cp < highStart)
      {
        int ix = 2080 + (cp >> 11);
        index2Block = index[ix];
        block = index[(index2Block + (cp >> 5 & 0x3F))] << '\002';
      }
      else {
        if (value != data32[highValueIndex]) break;
        cp = limit; break;
      }
      


      if (index2Block == index2NullOffset) {
        if (value != initialValue) {
          break;
        }
        cp += 2048;
      } else if (block == dataNullOffset)
      {



        if (value != initialValue) {
          break;
        }
        cp += 32;
      }
      else
      {
        int startIx = block + (cp & 0x1F);
        int limitIx = block + 32;
        for (int ix = startIx; ix < limitIx; ix++) {
          if (data32[ix] != value)
          {

            cp += ix - startIx;
            
            break label288;
          }
        }
        
        cp += limitIx - startIx;
      } }
    label288:
    if (cp > limit) {
      cp = limit;
    }
    
    return cp - 1;
  }
}
