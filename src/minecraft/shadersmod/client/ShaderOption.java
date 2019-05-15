package shadersmod.client;

import java.util.Arrays;
import java.util.List;
import optifine.Config;
import optifine.StrUtils;


public abstract class ShaderOption
{
  private String name = null;
  private String description = null;
  private String value = null;
  private String[] values = null;
  private String valueDefault = null;
  private String[] paths = null;
  private boolean enabled = true;
  private boolean visible = true;
  public static final String COLOR_GREEN = "§a";
  public static final String COLOR_RED = "§c";
  public static final String COLOR_BLUE = "§9";
  
  public ShaderOption(String name, String description, String value, String[] values, String valueDefault, String path)
  {
    this.name = name;
    this.description = description;
    this.value = value;
    this.values = values;
    this.valueDefault = valueDefault;
    
    if (path != null)
    {
      paths = new String[] { path };
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public String getDescriptionText()
  {
    String desc = Config.normalize(description);
    desc = StrUtils.removePrefix(desc, "//");
    desc = Shaders.translate("option." + getName() + ".comment", desc);
    return desc;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public boolean setValue(String value)
  {
    int index = getIndex(value, values);
    
    if (index < 0)
    {
      return false;
    }
    

    this.value = value;
    return true;
  }
  

  public String getValueDefault()
  {
    return valueDefault;
  }
  
  public void resetValue()
  {
    value = valueDefault;
  }
  
  public void nextValue()
  {
    int index = getIndex(value, values);
    
    if (index >= 0)
    {
      index = (index + 1) % values.length;
      value = values[index];
    }
  }
  
  private static int getIndex(String str, String[] strs)
  {
    for (int i = 0; i < strs.length; i++)
    {
      String s = strs[i];
      
      if (s.equals(str))
      {
        return i;
      }
    }
    
    return -1;
  }
  
  public String[] getPaths()
  {
    return paths;
  }
  
  public void addPaths(String[] newPaths)
  {
    List pathList = Arrays.asList(paths);
    
    for (int i = 0; i < newPaths.length; i++)
    {
      String newPath = newPaths[i];
      
      if (!pathList.contains(newPath))
      {
        paths = ((String[])Config.addObjectToArray(paths, newPath));
      }
    }
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }
  
  public boolean isChanged()
  {
    return !Config.equals(value, valueDefault);
  }
  
  public boolean isVisible()
  {
    return visible;
  }
  
  public void setVisible(boolean visible)
  {
    this.visible = visible;
  }
  
  public boolean isValidValue(String val)
  {
    return getIndex(val, values) >= 0;
  }
  
  public String getNameText()
  {
    return Shaders.translate("option." + name, name);
  }
  
  public String getValueText(String val)
  {
    return val;
  }
  
  public String getValueColor(String val)
  {
    return "";
  }
  
  public boolean matchesLine(String line)
  {
    return false;
  }
  
  public boolean checkUsed()
  {
    return false;
  }
  
  public boolean isUsedInLine(String line)
  {
    return false;
  }
  
  public String getSourceLine()
  {
    return null;
  }
  
  public String[] getValues()
  {
    return (String[])values.clone();
  }
  
  public String toString()
  {
    return name + ", value: " + value + ", valueDefault: " + valueDefault + ", paths: " + Config.arrayToString(paths);
  }
}
