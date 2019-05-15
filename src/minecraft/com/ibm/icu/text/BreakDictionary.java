package com.ibm.icu.text;

import com.ibm.icu.util.CompactByteArray;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
































class BreakDictionary
{
  static void writeToFile(String inFile, String outFile)
    throws FileNotFoundException, UnsupportedEncodingException, IOException
  {
    BreakDictionary dictionary = new BreakDictionary(new FileInputStream(inFile));
    
    PrintWriter out = null;
    
    if (outFile != null) {
      out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UnicodeLittle"));
    }
    
    dictionary.printWordList("", 0, out);
    
    if (out != null) {
      out.close();
    }
  }
  

  void printWordList(String partialWord, int state, PrintWriter out)
    throws IOException
  {
    if (state == 65535) {
      System.out.println(partialWord);
      if (out != null) {
        out.println(partialWord);
      }
    }
    else {
      for (int i = 0; i < numCols; i++) {
        int newState = at(state, i) & 0xFFFF;
        
        if (newState != 0) {
          char newChar = reverseColumnMap[i];
          String newPartialWord = partialWord;
          
          if (newChar != 0) {
            newPartialWord = newPartialWord + newChar;
          }
          
          printWordList(newPartialWord, newState, out);
        }
      }
    }
  }
  





  private char[] reverseColumnMap = null;
  








  private CompactByteArray columnMap = null;
  











  private int numCols;
  










  private short[] table = null;
  



  private short[] rowIndex = null;
  






  private int[] rowIndexFlags = null;
  








  private short[] rowIndexFlagsIndex = null;
  




  private byte[] rowIndexShifts = null;
  


  BreakDictionary(InputStream dictionaryStream)
    throws IOException
  {
    readDictionaryFile(new DataInputStream(dictionaryStream));
  }
  

  void readDictionaryFile(DataInputStream in)
    throws IOException
  {
    in.readInt();
    


    int l = in.readInt();
    char[] temp = new char[l];
    for (int i = 0; i < temp.length; i++)
      temp[i] = ((char)in.readShort());
    l = in.readInt();
    byte[] temp2 = new byte[l];
    for (int i = 0; i < temp2.length; i++)
      temp2[i] = in.readByte();
    columnMap = new CompactByteArray(temp, temp2);
    

    numCols = in.readInt();
    in.readInt();
    

    l = in.readInt();
    rowIndex = new short[l];
    for (int i = 0; i < rowIndex.length; i++) {
      rowIndex[i] = in.readShort();
    }
    
    l = in.readInt();
    rowIndexFlagsIndex = new short[l];
    for (int i = 0; i < rowIndexFlagsIndex.length; i++)
      rowIndexFlagsIndex[i] = in.readShort();
    l = in.readInt();
    rowIndexFlags = new int[l];
    for (int i = 0; i < rowIndexFlags.length; i++) {
      rowIndexFlags[i] = in.readInt();
    }
    
    l = in.readInt();
    rowIndexShifts = new byte[l];
    for (int i = 0; i < rowIndexShifts.length; i++) {
      rowIndexShifts[i] = in.readByte();
    }
    
    l = in.readInt();
    table = new short[l];
    for (int i = 0; i < table.length; i++) {
      table[i] = in.readShort();
    }
    
    reverseColumnMap = new char[numCols];
    for (char c = '\000'; c < 65535; c = (char)(c + '\001')) {
      int col = columnMap.elementAt(c);
      if (col != 0) {
        reverseColumnMap[col] = c;
      }
    }
    

    in.close();
  }
  










  final short at(int row, char ch)
  {
    int col = columnMap.elementAt(ch);
    return at(row, col);
  }
  










  final short at(int row, int col)
  {
    if (cellIsPopulated(row, col))
    {





      return internalAt(rowIndex[row], col + rowIndexShifts[row]);
    }
    
    return 0;
  }
  







  private final boolean cellIsPopulated(int row, int col)
  {
    if (rowIndexFlagsIndex[row] < 0) {
      return col == -rowIndexFlagsIndex[row];
    }
    







    int flags = rowIndexFlags[(rowIndexFlagsIndex[row] + (col >> 5))];
    return (flags & 1 << (col & 0x1F)) != 0;
  }
  









  private final short internalAt(int row, int col)
  {
    return table[(row * numCols + col)];
  }
}
