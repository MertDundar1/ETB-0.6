package com.ibm.icu.text;

import com.ibm.icu.impl.ICUBinary;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;












class BreakCTDictionary
{
  private CompactTrieHeader fData;
  private CompactTrieNodes[] nodes;
  
  static class CompactTrieHeader
  {
    int size;
    int magic;
    int nodeCount;
    int root;
    int[] offset;
    
    CompactTrieHeader()
    {
      size = 0;
      magic = 0;
      nodeCount = 0;
      root = 0;
      offset = null;
    }
  }
  

  static final class CompactTrieNodeFlags
  {
    static final int kVerticalNode = 4096;
    
    static final int kParentEndsWord = 8192;
    
    static final int kReservedFlag1 = 16384;
    
    static final int kReservedFlag2 = 32768;
    
    static final int kCountMask = 4095;
    
    static final int kFlagMask = 61440;
    
    CompactTrieNodeFlags() {}
  }
  
  static class CompactTrieHorizontalNode
  {
    char ch;
    int equal;
    
    CompactTrieHorizontalNode(char newCh, int newEqual)
    {
      ch = newCh;
      equal = newEqual;
    }
  }
  
  static class CompactTrieVerticalNode
  {
    int equal;
    char[] chars;
    
    CompactTrieVerticalNode() {
      equal = 0;
      chars = null;
    }
  }
  
  private CompactTrieNodes getCompactTrieNode(int node) {
    return nodes[node];
  }
  

  static class CompactTrieNodes
  {
    short flagscount;
    BreakCTDictionary.CompactTrieHorizontalNode[] hnode;
    BreakCTDictionary.CompactTrieVerticalNode vnode;
    
    CompactTrieNodes()
    {
      flagscount = 0;
      hnode = null;
      vnode = null;
    }
  }
  

  public BreakCTDictionary(InputStream is)
    throws IOException
  {
    ICUBinary.readHeader(is, DATA_FORMAT_ID, null);
    
    DataInputStream in = new DataInputStream(is);
    
    fData = new CompactTrieHeader();
    fData.size = in.readInt();
    fData.magic = in.readInt();
    fData.nodeCount = in.readShort();
    fData.root = in.readShort();
    
    loadBreakCTDictionary(in);
  }
  
  private void loadBreakCTDictionary(DataInputStream in)
    throws IOException
  {
    for (int i = 0; i < fData.nodeCount; i++) {
      in.readInt();
    }
    

    nodes = new CompactTrieNodes[fData.nodeCount];
    nodes[0] = new CompactTrieNodes();
    

    for (int j = 1; j < fData.nodeCount; j++) {
      nodes[j] = new CompactTrieNodes();
      nodes[j].flagscount = in.readShort();
      
      int count = nodes[j].flagscount & 0xFFF;
      
      if (count != 0) {
        boolean isVerticalNode = (nodes[j].flagscount & 0x1000) != 0;
        

        if (isVerticalNode) {
          nodes[j].vnode = new CompactTrieVerticalNode();
          nodes[j].vnode.equal = in.readShort();
          
          nodes[j].vnode.chars = new char[count];
          for (int l = 0; l < count; l++) {
            nodes[j].vnode.chars[l] = in.readChar();
          }
        } else {
          nodes[j].hnode = new CompactTrieHorizontalNode[count];
          for (int n = 0; n < count; n++) {
            nodes[j].hnode[n] = new CompactTrieHorizontalNode(in.readChar(), in.readShort());
          }
        }
      }
    }
  }
  













  public int matches(CharacterIterator text, int maxLength, int[] lengths, int[] count, int limit)
  {
    CompactTrieNodes node = getCompactTrieNode(fData.root);
    int mycount = 0;
    
    char uc = text.current();
    int i = 0;
    boolean exitFlag = false;
    
    while (node != null)
    {
      if ((limit > 0) && ((flagscount & 0x2000) != 0))
      {
        lengths[(mycount++)] = i;
        limit--;
      }
      




      if (i >= maxLength) {
        break;
      }
      
      int nodeCount = flagscount & 0xFFF;
      if (nodeCount == 0) {
        break;
      }
      
      if ((flagscount & 0x1000) != 0)
      {
        CompactTrieVerticalNode vnode = vnode;
        for (int j = 0; (j < nodeCount) && (i < maxLength); j++) {
          if (uc != chars[j])
          {
            exitFlag = true;
            break;
          }
          text.next();
          uc = text.current();
          i++;
        }
        if (exitFlag) {
          break;
        }
        


        node = getCompactTrieNode(equal);
      }
      else {
        CompactTrieHorizontalNode[] hnode = hnode;
        int low = 0;
        int high = nodeCount - 1;
        
        node = null;
        while (high >= low) {
          int middle = high + low >>> 1;
          if (uc == ch)
          {
            node = getCompactTrieNode(equal);
            text.next();
            uc = text.current();
            i++;
            break; }
          if (uc < ch) {
            high = middle - 1;
          } else {
            low = middle + 1;
          }
        }
      }
    }
    
    count[0] = mycount;
    return i;
  }
  

  private static final byte[] DATA_FORMAT_ID = { 84, 114, 68, 99 };
}
