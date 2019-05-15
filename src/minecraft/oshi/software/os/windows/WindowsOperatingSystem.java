package oshi.software.os.windows;

import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;
import oshi.software.os.windows.nt.OSVersionInfoEx;










public class WindowsOperatingSystem
  implements OperatingSystem
{
  public WindowsOperatingSystem() {}
  
  private OperatingSystemVersion _version = null;
  
  public OperatingSystemVersion getVersion() {
    if (_version == null) {
      _version = new OSVersionInfoEx();
    }
    return _version;
  }
  
  public String getFamily() {
    return "Windows";
  }
  
  public String getManufacturer() {
    return "Microsoft";
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getManufacturer());
    sb.append(" ");
    sb.append(getFamily());
    sb.append(" ");
    sb.append(getVersion().toString());
    return sb.toString();
  }
}
