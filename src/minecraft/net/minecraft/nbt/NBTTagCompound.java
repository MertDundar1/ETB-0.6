package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound extends NBTBase
{
  private static final Logger logger = ;
  



  private Map tagMap = Maps.newHashMap();
  private static final String __OBFID = "CL_00001215";
  
  public NBTTagCompound() {}
  
  void write(DataOutput output)
    throws IOException
  {
    Iterator var2 = tagMap.keySet().iterator();
    
    while (var2.hasNext())
    {
      String var3 = (String)var2.next();
      NBTBase var4 = (NBTBase)tagMap.get(var3);
      writeEntry(var3, var4, output);
    }
    
    output.writeByte(0);
  }
  
  void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException
  {
    if (depth > 512)
    {
      throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
    }
    

    tagMap.clear();
    
    byte var4;
    while ((var4 = readType(input, sizeTracker)) != 0) {
      byte var4;
      String var5 = readKey(input, sizeTracker);
      sizeTracker.read(16 * var5.length());
      NBTBase var6 = readNBT(var4, var5, input, depth + 1, sizeTracker);
      tagMap.put(var5, var6);
    }
  }
  




  public Set getKeySet()
  {
    return tagMap.keySet();
  }
  



  public byte getId()
  {
    return 10;
  }
  



  public void setTag(String key, NBTBase value)
  {
    tagMap.put(key, value);
  }
  



  public void setByte(String key, byte value)
  {
    tagMap.put(key, new NBTTagByte(value));
  }
  



  public void setShort(String key, short value)
  {
    tagMap.put(key, new NBTTagShort(value));
  }
  



  public void setInteger(String key, int value)
  {
    tagMap.put(key, new NBTTagInt(value));
  }
  



  public void setLong(String key, long value)
  {
    tagMap.put(key, new NBTTagLong(value));
  }
  



  public void setFloat(String key, float value)
  {
    tagMap.put(key, new NBTTagFloat(value));
  }
  



  public void setDouble(String key, double value)
  {
    tagMap.put(key, new NBTTagDouble(value));
  }
  



  public void setString(String key, String value)
  {
    tagMap.put(key, new NBTTagString(value));
  }
  



  public void setByteArray(String key, byte[] value)
  {
    tagMap.put(key, new NBTTagByteArray(value));
  }
  



  public void setIntArray(String key, int[] value)
  {
    tagMap.put(key, new NBTTagIntArray(value));
  }
  



  public void setBoolean(String key, boolean value)
  {
    setByte(key, (byte)(value ? 1 : 0));
  }
  



  public NBTBase getTag(String key)
  {
    return (NBTBase)tagMap.get(key);
  }
  



  public byte getTagType(String key)
  {
    NBTBase var2 = (NBTBase)tagMap.get(key);
    return var2 != null ? var2.getId() : 0;
  }
  



  public boolean hasKey(String key)
  {
    return tagMap.containsKey(key);
  }
  
  public boolean hasKey(String key, int type)
  {
    byte var3 = getTagType(key);
    
    if (var3 == type)
    {
      return true;
    }
    if (type != 99)
    {
      if (var3 > 0) {}
      



      return false;
    }
    

    return (var3 == 1) || (var3 == 2) || (var3 == 3) || (var3 == 4) || (var3 == 5) || (var3 == 6);
  }
  




  public byte getByte(String key)
  {
    try
    {
      return !hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)tagMap.get(key)).getByte();
    }
    catch (ClassCastException var3) {}
    
    return 0;
  }
  




  public short getShort(String key)
  {
    try
    {
      return !hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)tagMap.get(key)).getShort();
    }
    catch (ClassCastException var3) {}
    
    return 0;
  }
  




  public int getInteger(String key)
  {
    try
    {
      return !hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)tagMap.get(key)).getInt();
    }
    catch (ClassCastException var3) {}
    
    return 0;
  }
  




  public long getLong(String key)
  {
    try
    {
      return !hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive)tagMap.get(key)).getLong();
    }
    catch (ClassCastException var3) {}
    
    return 0L;
  }
  




  public float getFloat(String key)
  {
    try
    {
      return !hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive)tagMap.get(key)).getFloat();
    }
    catch (ClassCastException var3) {}
    
    return 0.0F;
  }
  




  public double getDouble(String key)
  {
    try
    {
      return !hasKey(key, 99) ? 0.0D : ((NBTBase.NBTPrimitive)tagMap.get(key)).getDouble();
    }
    catch (ClassCastException var3) {}
    
    return 0.0D;
  }
  




  public String getString(String key)
  {
    try
    {
      return !hasKey(key, 8) ? "" : ((NBTBase)tagMap.get(key)).getString();
    }
    catch (ClassCastException var3) {}
    
    return "";
  }
  




  public byte[] getByteArray(String key)
  {
    try
    {
      return !hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray)tagMap.get(key)).getByteArray();
    }
    catch (ClassCastException var3)
    {
      throw new ReportedException(createCrashReport(key, 7, var3));
    }
  }
  



  public int[] getIntArray(String key)
  {
    try
    {
      return !hasKey(key, 11) ? new int[0] : ((NBTTagIntArray)tagMap.get(key)).getIntArray();
    }
    catch (ClassCastException var3)
    {
      throw new ReportedException(createCrashReport(key, 11, var3));
    }
  }
  




  public NBTTagCompound getCompoundTag(String key)
  {
    try
    {
      return !hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound)tagMap.get(key);
    }
    catch (ClassCastException var3)
    {
      throw new ReportedException(createCrashReport(key, 10, var3));
    }
  }
  



  public NBTTagList getTagList(String key, int type)
  {
    try
    {
      if (getTagType(key) != 9)
      {
        return new NBTTagList();
      }
      

      NBTTagList var3 = (NBTTagList)tagMap.get(key);
      return (var3.tagCount() > 0) && (var3.getTagType() != type) ? new NBTTagList() : var3;

    }
    catch (ClassCastException var4)
    {
      throw new ReportedException(createCrashReport(key, 9, var4));
    }
  }
  




  public boolean getBoolean(String key)
  {
    return getByte(key) != 0;
  }
  



  public void removeTag(String key)
  {
    tagMap.remove(key);
  }
  
  public String toString()
  {
    String var1 = "{";
    
    String var3;
    for (Iterator var2 = tagMap.keySet().iterator(); var2.hasNext(); var1 = var1 + var3 + ':' + tagMap.get(var3) + ',')
    {
      var3 = (String)var2.next();
    }
    
    return var1 + "}";
  }
  



  public boolean hasNoTags()
  {
    return tagMap.isEmpty();
  }
  



  private CrashReport createCrashReport(final String key, final int expectedType, ClassCastException ex)
  {
    CrashReport var4 = CrashReport.makeCrashReport(ex, "Reading NBT data");
    CrashReportCategory var5 = var4.makeCategoryDepth("Corrupt NBT tag", 1);
    var5.addCrashSectionCallable("Tag type found", new Callable()
    {
      private static final String __OBFID = "CL_00001216";
      
      public String call() {
        return NBTBase.NBT_TYPES[((NBTBase)tagMap.get(key)).getId()];
      }
    });
    var5.addCrashSectionCallable("Tag type expected", new Callable()
    {
      private static final String __OBFID = "CL_00001217";
      
      public String call() {
        return NBTBase.NBT_TYPES[expectedType];
      }
    });
    var5.addCrashSection("Tag name", key);
    return var4;
  }
  



  public NBTBase copy()
  {
    NBTTagCompound var1 = new NBTTagCompound();
    Iterator var2 = tagMap.keySet().iterator();
    
    while (var2.hasNext())
    {
      String var3 = (String)var2.next();
      var1.setTag(var3, ((NBTBase)tagMap.get(var3)).copy());
    }
    
    return var1;
  }
  
  public boolean equals(Object p_equals_1_)
  {
    if (super.equals(p_equals_1_))
    {
      NBTTagCompound var2 = (NBTTagCompound)p_equals_1_;
      return tagMap.entrySet().equals(tagMap.entrySet());
    }
    

    return false;
  }
  

  public int hashCode()
  {
    return super.hashCode() ^ tagMap.hashCode();
  }
  
  private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException
  {
    output.writeByte(data.getId());
    
    if (data.getId() != 0)
    {
      output.writeUTF(name);
      data.write(output);
    }
  }
  
  private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException
  {
    return input.readByte();
  }
  
  private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException
  {
    return input.readUTF();
  }
  
  static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker)
  {
    NBTBase var5 = NBTBase.createNewByType(id);
    
    try
    {
      var5.read(input, depth, sizeTracker);
      return var5;
    }
    catch (IOException var9)
    {
      CrashReport var7 = CrashReport.makeCrashReport(var9, "Loading NBT data");
      CrashReportCategory var8 = var7.makeCategory("NBT Tag");
      var8.addCrashSection("Tag name", key);
      var8.addCrashSection("Tag type", Byte.valueOf(id));
      throw new ReportedException(var7);
    }
  }
  




  public void merge(NBTTagCompound other)
  {
    Iterator var2 = tagMap.keySet().iterator();
    
    while (var2.hasNext())
    {
      String var3 = (String)var2.next();
      NBTBase var4 = (NBTBase)tagMap.get(var3);
      
      if (var4.getId() == 10)
      {
        if (hasKey(var3, 10))
        {
          NBTTagCompound var5 = getCompoundTag(var3);
          var5.merge((NBTTagCompound)var4);
        }
        else
        {
          setTag(var3, var4.copy());
        }
        
      }
      else {
        setTag(var3, var4.copy());
      }
    }
  }
}
