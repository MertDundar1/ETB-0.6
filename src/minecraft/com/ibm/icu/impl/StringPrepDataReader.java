package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

















public final class StringPrepDataReader
  implements ICUBinary.Authenticate
{
  private static final boolean debug = ICUDebug.enabled("NormalizerDataReader");
  
  private DataInputStream dataInputStream;
  
  private byte[] unicodeVersion;
  
  public StringPrepDataReader(InputStream inputStream)
    throws IOException
  {
    if (debug) { System.out.println("Bytes in inputStream " + inputStream.available());
    }
    unicodeVersion = ICUBinary.readHeader(inputStream, DATA_FORMAT_ID, this);
    
    if (debug) { System.out.println("Bytes left in inputStream " + inputStream.available());
    }
    dataInputStream = new DataInputStream(inputStream);
    
    if (debug) { System.out.println("Bytes left in dataInputStream " + dataInputStream.available());
    }
  }
  

  public void read(byte[] idnaBytes, char[] mappingTable)
    throws IOException
  {
    dataInputStream.readFully(idnaBytes);
    

    for (int i = 0; i < mappingTable.length; i++) {
      mappingTable[i] = dataInputStream.readChar();
    }
  }
  
  public byte[] getDataFormatVersion() {
    return DATA_FORMAT_VERSION;
  }
  
  public boolean isDataVersionAcceptable(byte[] version) {
    return (version[0] == DATA_FORMAT_VERSION[0]) && (version[2] == DATA_FORMAT_VERSION[2]) && (version[3] == DATA_FORMAT_VERSION[3]);
  }
  
  public int[] readIndexes(int length) throws IOException
  {
    int[] indexes = new int[length];
    
    for (int i = 0; i < length; i++) {
      indexes[i] = dataInputStream.readInt();
    }
    return indexes;
  }
  
  public byte[] getUnicodeVersion() {
    return unicodeVersion;
  }
  













  private static final byte[] DATA_FORMAT_ID = { 83, 80, 82, 80 };
  
  private static final byte[] DATA_FORMAT_VERSION = { 3, 2, 5, 2 };
}
