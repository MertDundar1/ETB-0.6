package optifine;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue
{
  private String[] parents = null;
  private String name = null;
  private int type = 0;
  private String value = null;
  private static final int TYPE_TEXT = 0;
  private static final int TYPE_PATTERN = 1;
  private static final int TYPE_IPATTERN = 2;
  private static final int TYPE_REGEX = 3;
  private static final int TYPE_IREGEX = 4;
  private static final String PREFIX_PATTERN = "pattern:";
  private static final String PREFIX_IPATTERN = "ipattern:";
  private static final String PREFIX_REGEX = "regex:";
  private static final String PREFIX_IREGEX = "iregex:";
  
  public NbtTagValue(String tag, String value)
  {
    String[] names = Config.tokenize(tag, ".");
    parents = ((String[])Arrays.copyOfRange(names, 0, names.length - 1));
    name = names[(names.length - 1)];
    
    if (value.startsWith("pattern:"))
    {
      type = 1;
      value = value.substring("pattern:".length());
    }
    else if (value.startsWith("ipattern:"))
    {
      type = 2;
      value = value.substring("ipattern:".length()).toLowerCase();
    }
    else if (value.startsWith("regex:"))
    {
      type = 3;
      value = value.substring("regex:".length());
    }
    else if (value.startsWith("iregex:"))
    {
      type = 4;
      value = value.substring("iregex:".length()).toLowerCase();
    }
    else
    {
      type = 0;
    }
    
    value = StringEscapeUtils.unescapeJava(value);
    this.value = value;
  }
  
  public boolean matches(NBTTagCompound nbt)
  {
    if (nbt == null)
    {
      return false;
    }
    

    Object tagBase = nbt;
    
    for (int i = 0; i < parents.length; i++)
    {
      String tag = parents[i];
      tagBase = getChildTag((NBTBase)tagBase, tag);
      
      if (tagBase == null)
      {
        return false;
      }
    }
    
    if (name.equals("*"))
    {
      return matchesAnyChild((NBTBase)tagBase);
    }
    

    NBTBase var5 = getChildTag((NBTBase)tagBase, name);
    
    if (var5 == null)
    {
      return false;
    }
    if (matches(var5))
    {
      return true;
    }
    

    return false;
  }
  



  private boolean matchesAnyChild(NBTBase tagBase)
  {
    if ((tagBase instanceof NBTTagCompound))
    {
      NBTTagCompound tagList = (NBTTagCompound)tagBase;
      Set count = tagList.getKeySet();
      Iterator i = count.iterator();
      
      while (i.hasNext())
      {
        String nbtBase = (String)i.next();
        NBTBase nbtBase1 = tagList.getTag(nbtBase);
        
        if (matches(nbtBase1))
        {
          return true;
        }
      }
    }
    
    if ((tagBase instanceof NBTTagList))
    {
      NBTTagList var7 = (NBTTagList)tagBase;
      int var8 = var7.tagCount();
      
      for (int var9 = 0; var9 < var8; var9++)
      {
        NBTBase var10 = var7.get(var9);
        
        if (matches(var10))
        {
          return true;
        }
      }
    }
    
    return false;
  }
  
  private static NBTBase getChildTag(NBTBase tagBase, String tag)
  {
    if ((tagBase instanceof NBTTagCompound))
    {
      NBTTagCompound tagList1 = (NBTTagCompound)tagBase;
      return tagList1.getTag(tag);
    }
    if ((tagBase instanceof NBTTagList))
    {
      NBTTagList tagList = (NBTTagList)tagBase;
      int index = Config.parseInt(tag, -1);
      return index < 0 ? null : tagList.get(index);
    }
    

    return null;
  }
  

  private boolean matches(NBTBase nbtBase)
  {
    if (nbtBase == null)
    {
      return false;
    }
    

    String nbtValue = getValue(nbtBase);
    
    if (nbtValue == null)
    {
      return false;
    }
    

    switch (type)
    {
    case 0: 
      return nbtValue.equals(value);
    
    case 1: 
      return matchesPattern(nbtValue, value);
    
    case 2: 
      return matchesPattern(nbtValue.toLowerCase(), value);
    
    case 3: 
      return matchesRegex(nbtValue, value);
    
    case 4: 
      return matchesRegex(nbtValue.toLowerCase(), value);
    }
    
    throw new IllegalArgumentException("Unknown NbtTagValue type: " + type);
  }
  



  private boolean matchesPattern(String str, String pattern)
  {
    return StrUtils.equalsMask(str, pattern, '*', '?');
  }
  
  private boolean matchesRegex(String str, String regex)
  {
    return str.matches(regex);
  }
  
  private static String getValue(NBTBase nbtBase)
  {
    if (nbtBase == null)
    {
      return null;
    }
    if ((nbtBase instanceof NBTTagString))
    {
      NBTTagString d6 = (NBTTagString)nbtBase;
      return d6.getString();
    }
    if ((nbtBase instanceof NBTTagInt))
    {
      NBTTagInt d5 = (NBTTagInt)nbtBase;
      return Integer.toString(d5.getInt());
    }
    if ((nbtBase instanceof NBTTagByte))
    {
      NBTTagByte d4 = (NBTTagByte)nbtBase;
      return Byte.toString(d4.getByte());
    }
    if ((nbtBase instanceof NBTTagShort))
    {
      NBTTagShort d3 = (NBTTagShort)nbtBase;
      return Short.toString(d3.getShort());
    }
    if ((nbtBase instanceof NBTTagLong))
    {
      NBTTagLong d2 = (NBTTagLong)nbtBase;
      return Long.toString(d2.getLong());
    }
    if ((nbtBase instanceof NBTTagFloat))
    {
      NBTTagFloat d1 = (NBTTagFloat)nbtBase;
      return Float.toString(d1.getFloat());
    }
    if ((nbtBase instanceof NBTTagDouble))
    {
      NBTTagDouble d = (NBTTagDouble)nbtBase;
      return Double.toString(d.getDouble());
    }
    

    return nbtBase.toString();
  }
  

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    
    for (int i = 0; i < parents.length; i++)
    {
      String parent = parents[i];
      
      if (i > 0)
      {
        sb.append(".");
      }
      
      sb.append(parent);
    }
    
    if (sb.length() > 0)
    {
      sb.append(".");
    }
    
    sb.append(name);
    sb.append(" = ");
    sb.append(value);
    return sb.toString();
  }
}
