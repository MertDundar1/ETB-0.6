package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public abstract interface VerRsrc extends StdCallLibrary
{
  public static class VS_FIXEDFILEINFO extends Structure
  {
    public WinDef.DWORD dwSignature;
    public WinDef.DWORD dwStrucVersion;
    public WinDef.DWORD dwFileVersionMS;
    public WinDef.DWORD dwFileVersionLS;
    public WinDef.DWORD dwProductVersionMS;
    public WinDef.DWORD dwProductVersionLS;
    public WinDef.DWORD dwFileFlagsMask;
    public WinDef.DWORD dwFileFlags;
    public WinDef.DWORD dwFileOS;
    public WinDef.DWORD dwFileType;
    public WinDef.DWORD dwFileSubtype;
    public WinDef.DWORD dwFileDateMS;
    public WinDef.DWORD dwFileDateLS;
    public VS_FIXEDFILEINFO() {}
    
    public static class ByReference extends VerRsrc.VS_FIXEDFILEINFO implements com.sun.jna.Structure.ByReference
    {
      public ByReference() {}
      
      public ByReference(Pointer memory)
      {
        super();
      }
    }
    


    public VS_FIXEDFILEINFO(Pointer memory)
    {
      super();
      read();
    }
  }
}