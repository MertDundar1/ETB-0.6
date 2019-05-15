package oshi.software.os.linux;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;
import oshi.software.os.linux.proc.OSVersionInfoEx;












public class LinuxOperatingSystem
  implements OperatingSystem
{
  public LinuxOperatingSystem() {}
  
  private OperatingSystemVersion _version = null;
  private String _family = null;
  
  public String getFamily() {
    if (_family == null)
    {
      try {
        in = new Scanner(new FileReader("/etc/os-release"));
      } catch (FileNotFoundException e) { Scanner in;
        return ""; }
      Scanner in;
      in.useDelimiter("\n");
      while (in.hasNext()) {
        String[] splittedLine = in.next().split("=");
        if (splittedLine[0].equals("NAME"))
        {

          _family = splittedLine[1].replaceAll("^\"|\"$", "");
          break;
        }
      }
      in.close();
    }
    return _family;
  }
  
  public String getManufacturer() {
    return "GNU/Linux";
  }
  
  public OperatingSystemVersion getVersion() {
    if (_version == null) {
      _version = new OSVersionInfoEx();
    }
    return _version;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getManufacturer());
    sb.append(" ");
    sb.append(getFamily());
    sb.append(" ");
    sb.append(getVersion().toString());
    return sb.toString();
  }
}
