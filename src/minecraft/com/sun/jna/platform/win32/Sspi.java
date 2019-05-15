package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.ByReference;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;













































































































public abstract interface Sspi
  extends StdCallLibrary
{
  public static final int MAX_TOKEN_SIZE = 12288;
  public static final int SECPKG_CRED_INBOUND = 1;
  public static final int SECPKG_CRED_OUTBOUND = 2;
  public static final int SECURITY_NATIVE_DREP = 16;
  public static final int ISC_REQ_ALLOCATE_MEMORY = 256;
  public static final int ISC_REQ_CONFIDENTIALITY = 16;
  public static final int ISC_REQ_CONNECTION = 2048;
  public static final int ISC_REQ_DELEGATE = 1;
  public static final int ISC_REQ_EXTENDED_ERROR = 16384;
  public static final int ISC_REQ_INTEGRITY = 65536;
  public static final int ISC_REQ_MUTUAL_AUTH = 2;
  public static final int ISC_REQ_REPLAY_DETECT = 4;
  public static final int ISC_REQ_SEQUENCE_DETECT = 8;
  public static final int ISC_REQ_STREAM = 32768;
  public static final int SECBUFFER_VERSION = 0;
  public static final int SECBUFFER_EMPTY = 0;
  public static final int SECBUFFER_DATA = 1;
  public static final int SECBUFFER_TOKEN = 2;
  
  public static class SecHandle
    extends Structure
  {
    public Pointer dwLower;
    public Pointer dwUpper;
    
    public SecHandle()
    {
      dwLower = null;
      dwUpper = null;
    }
    




    public boolean isNull()
    {
      return (dwLower == null) && (dwUpper == null);
    }
    

    public static class ByReference
      extends Sspi.SecHandle
      implements Structure.ByReference
    {
      public ByReference() {}
    }
  }
  

  public static class PSecHandle
    extends Structure
  {
    public Sspi.SecHandle.ByReference secHandle;
    

    public PSecHandle() {}
    
    public PSecHandle(Sspi.SecHandle h)
    {
      super();
      read();
    }
    

    public static class ByReference
      extends Sspi.PSecHandle
      implements Structure.ByReference
    {
      public ByReference() {}
    }
  }
  

  public static class CredHandle
    extends Sspi.SecHandle
  {
    public CredHandle() {}
  }
  

  public static class CtxtHandle
    extends Sspi.SecHandle
  {
    public CtxtHandle() {}
  }
  
  public static class SecBuffer
    extends Structure
  {
    public NativeLong cbBuffer;
    public NativeLong BufferType;
    public Pointer pvBuffer;
    
    public static class ByReference
      extends Sspi.SecBuffer
      implements Structure.ByReference
    {
      public ByReference() {}
      
      public ByReference(int type, int size)
      {
        super(size);
      }
      
      public ByReference(int type, byte[] token) {
        super(token);
      }
      




      public byte[] getBytes()
      {
        return super.getBytes();
      }
    }
    
















    public SecBuffer()
    {
      cbBuffer = new NativeLong(0L);
      pvBuffer = null;
      BufferType = new NativeLong(0L);
    }
    






    public SecBuffer(int type, int size)
    {
      cbBuffer = new NativeLong(size);
      pvBuffer = new Memory(size);
      BufferType = new NativeLong(type);
      allocateMemory();
    }
    






    public SecBuffer(int type, byte[] token)
    {
      cbBuffer = new NativeLong(token.length);
      pvBuffer = new Memory(token.length);
      pvBuffer.write(0L, token, 0, token.length);
      BufferType = new NativeLong(type);
      allocateMemory();
    }
    




    public byte[] getBytes()
    {
      return pvBuffer.getByteArray(0L, cbBuffer.intValue());
    }
  }
  



  public static class SecBufferDesc
    extends Structure
  {
    public NativeLong ulVersion;
    


    public NativeLong cBuffers;
    

    public Sspi.SecBuffer.ByReference[] pBuffers;
    


    public SecBufferDesc()
    {
      ulVersion = new NativeLong(0L);
      cBuffers = new NativeLong(1L);
      Sspi.SecBuffer.ByReference secBuffer = new Sspi.SecBuffer.ByReference();
      pBuffers = ((Sspi.SecBuffer.ByReference[])secBuffer.toArray(1));
      allocateMemory();
    }
    






    public SecBufferDesc(int type, byte[] token)
    {
      ulVersion = new NativeLong(0L);
      cBuffers = new NativeLong(1L);
      Sspi.SecBuffer.ByReference secBuffer = new Sspi.SecBuffer.ByReference(type, token);
      pBuffers = ((Sspi.SecBuffer.ByReference[])secBuffer.toArray(1));
      allocateMemory();
    }
    




    public SecBufferDesc(int type, int tokenSize)
    {
      ulVersion = new NativeLong(0L);
      cBuffers = new NativeLong(1L);
      Sspi.SecBuffer.ByReference secBuffer = new Sspi.SecBuffer.ByReference(type, tokenSize);
      pBuffers = ((Sspi.SecBuffer.ByReference[])secBuffer.toArray(1));
      allocateMemory();
    }
    
    public byte[] getBytes() {
      if ((pBuffers == null) || (cBuffers == null)) {
        throw new RuntimeException("pBuffers | cBuffers");
      }
      if (cBuffers.intValue() == 1) {
        return pBuffers[0].getBytes();
      }
      throw new RuntimeException("cBuffers > 1");
    }
  }
  

  public static class SECURITY_INTEGER
    extends Structure
  {
    public NativeLong dwLower;
    
    public NativeLong dwUpper;
    

    public SECURITY_INTEGER()
    {
      dwLower = new NativeLong(0L);
      dwUpper = new NativeLong(0L);
    }
  }
  




  public static class TimeStamp
    extends Sspi.SECURITY_INTEGER
  {
    public TimeStamp() {}
  }
  




  public static class PSecPkgInfo
    extends Structure
  {
    public Sspi.SecPkgInfo.ByReference pPkgInfo;
    



    public PSecPkgInfo() {}
    



    public Sspi.SecPkgInfo.ByReference[] toArray(int size)
    {
      return (Sspi.SecPkgInfo.ByReference[])pPkgInfo.toArray(size);
    }
    




    public static class ByReference
      extends Sspi.PSecPkgInfo
      implements Structure.ByReference
    {
      public ByReference() {}
    }
  }
  



  public static class SecPkgInfo
    extends Structure
  {
    public NativeLong fCapabilities;
    


    public short wVersion;
    


    public short wRPCID;
    


    public NativeLong cbMaxToken;
    


    public WString Name;
    


    public WString Comment;
    



    public SecPkgInfo()
    {
      fCapabilities = new NativeLong(0L);
      wVersion = 1;
      wRPCID = 0;
      cbMaxToken = new NativeLong(0L);
    }
    
    public static class ByReference
      extends Sspi.SecPkgInfo
      implements Structure.ByReference
    {
      public ByReference() {}
    }
  }
}
