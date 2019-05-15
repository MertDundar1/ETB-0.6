package oshi;

import com.sun.jna.Platform;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxHardwareAbstractionLayer;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.software.os.mac.MacHardwareAbstractionLayer;
import oshi.software.os.mac.MacOperatingSystem;
import oshi.software.os.windows.WindowsHardwareAbstractionLayer;
import oshi.software.os.windows.WindowsOperatingSystem;







public class SystemInfo
{
  private OperatingSystem _os;
  private HardwareAbstractionLayer _hardware;
  private PlatformEnum currentPlatformEnum;
  
  public SystemInfo()
  {
    _os = null;
    _hardware = null;
    


    if (Platform.isWindows()) {
      currentPlatformEnum = PlatformEnum.WINDOWS;
    } else if (Platform.isLinux()) {
      currentPlatformEnum = PlatformEnum.LINUX;
    } else if (Platform.isMac()) {
      currentPlatformEnum = PlatformEnum.MACOSX;
    } else {
      currentPlatformEnum = PlatformEnum.UNKNOWN;
    }
  }
  



  public OperatingSystem getOperatingSystem()
  {
    if (_os == null) {
      switch (1.$SwitchMap$oshi$PlatformEnum[currentPlatformEnum.ordinal()])
      {
      case 1: 
        _os = new WindowsOperatingSystem();
        break;
      case 2: 
        _os = new LinuxOperatingSystem();
        break;
      case 3: 
        _os = new MacOperatingSystem();
        break;
      
      default: 
        throw new RuntimeException("Operating system not supported: " + Platform.getOSType());
      }
    }
    return _os;
  }
  




  public HardwareAbstractionLayer getHardware()
  {
    if (_hardware == null) {
      switch (1.$SwitchMap$oshi$PlatformEnum[currentPlatformEnum.ordinal()])
      {
      case 1: 
        _hardware = new WindowsHardwareAbstractionLayer();
        break;
      case 2: 
        _hardware = new LinuxHardwareAbstractionLayer();
        break;
      case 3: 
        _hardware = new MacHardwareAbstractionLayer();
        break;
      
      default: 
        throw new RuntimeException("Operating system not supported: " + Platform.getOSType());
      }
    }
    return _hardware;
  }
}
