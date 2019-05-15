package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.ByReference;








public abstract interface Guid
{
  public static class GUID
    extends Structure
  {
    public int Data1;
    public short Data2;
    public short Data3;
    public GUID() {}
    
    public static class ByReference
      extends Guid.GUID
      implements Structure.ByReference
    {
      public ByReference() {}
      
      public ByReference(Guid.GUID guid)
      {
        super();
        
        Data1 = Data1;
        Data2 = Data2;
        Data3 = Data3;
        Data4 = Data4;
      }
      
      public ByReference(Pointer memory) {
        super();
      }
    }
    



    public GUID(Pointer memory)
    {
      super();
      read();
    }
    
    public GUID(byte[] data) {
      if (data.length != 16) {
        throw new IllegalArgumentException("Invalid data length: " + data.length);
      }
      
      long data1Temp = data[3] & 0xFF;
      data1Temp <<= 8;
      data1Temp |= data[2] & 0xFF;
      data1Temp <<= 8;
      data1Temp |= data[1] & 0xFF;
      data1Temp <<= 8;
      data1Temp |= data[0] & 0xFF;
      Data1 = ((int)data1Temp);
      
      int data2Temp = data[5] & 0xFF;
      data2Temp <<= 8;
      data2Temp |= data[4] & 0xFF;
      Data2 = ((short)data2Temp);
      
      int data3Temp = data[7] & 0xFF;
      data3Temp <<= 8;
      data3Temp |= data[6] & 0xFF;
      Data3 = ((short)data3Temp);
      
      Data4[0] = data[8];
      Data4[1] = data[9];
      Data4[2] = data[10];
      Data4[3] = data[11];
      Data4[4] = data[12];
      Data4[5] = data[13];
      Data4[6] = data[14];
      Data4[7] = data[15];
    }
    



    public byte[] Data4 = new byte[8];
  }
}
