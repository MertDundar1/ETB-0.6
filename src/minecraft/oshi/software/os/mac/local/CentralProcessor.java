package oshi.software.os.mac.local;

import java.util.ArrayList;
import oshi.hardware.Processor;
import oshi.util.ExecutingCommand;












public class CentralProcessor
  implements Processor
{
  private String _vendor;
  private String _name;
  private String _identifier = null;
  
  private String _stepping;
  
  private String _model;
  private String _family;
  private Boolean _cpu64;
  
  public CentralProcessor() {}
  
  public String getVendor()
  {
    if (_vendor == null)
      _vendor = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.vendor");
    return _vendor;
  }
  





  public void setVendor(String vendor)
  {
    _vendor = vendor;
  }
  




  public String getName()
  {
    if (_name == null)
      _name = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.brand_string");
    return _name;
  }
  





  public void setName(String name)
  {
    _name = name;
  }
  




  public String getIdentifier()
  {
    if (_identifier == null) {
      StringBuilder sb = new StringBuilder();
      if (getVendor().contentEquals("GenuineIntel")) {
        sb.append(isCpu64bit() ? "Intel64" : "x86");
      } else
        sb.append(getVendor());
      sb.append(" Family ");
      sb.append(getFamily());
      sb.append(" Model ");
      sb.append(getModel());
      sb.append(" Stepping ");
      sb.append(getStepping());
      _identifier = sb.toString();
    }
    return _identifier;
  }
  





  public void setIdentifier(String identifier)
  {
    _identifier = identifier;
  }
  




  public boolean isCpu64bit()
  {
    if (_cpu64 == null) {
      _cpu64 = Boolean.valueOf(ExecutingCommand.getFirstAnswer("sysctl -n hw.cpu64bit_capable")
        .equals("1"));
    }
    return _cpu64.booleanValue();
  }
  





  public void setCpu64(boolean cpu64)
  {
    _cpu64 = Boolean.valueOf(cpu64);
  }
  


  public String getStepping()
  {
    if (_stepping == null)
    {
      _stepping = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.stepping"); }
    return _stepping;
  }
  



  public void setStepping(String _stepping)
  {
    this._stepping = _stepping;
  }
  


  public String getModel()
  {
    if (_model == null)
    {
      _model = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.model");
    }
    
    return _model;
  }
  



  public void setModel(String _model)
  {
    this._model = _model;
  }
  


  public String getFamily()
  {
    if (_family == null)
    {
      _family = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.family");
    }
    return _family;
  }
  



  public void setFamily(String _family)
  {
    this._family = _family;
  }
  


  public float getLoad()
  {
    ArrayList<String> topResult = ExecutingCommand.runNative("top -l 1 -R -F -n1");
    String[] idle = ((String)topResult.get(3)).split(" ");
    return 100.0F - Float.valueOf(idle[6].replace("%", "")).floatValue();
  }
  
  public String toString()
  {
    return getName();
  }
}
