package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher
{
  private final Entity owner;
  private boolean isBlank = true;
  private static final Map dataTypes = ;
  private final Map watchedObjects = Maps.newHashMap();
  
  private boolean objectChanged;
  
  private ReadWriteLock lock = new ReentrantReadWriteLock();
  private static final String __OBFID = "CL_00001559";
  
  public DataWatcher(Entity owner)
  {
    this.owner = owner;
  }
  




  public void addObject(int id, Object object)
  {
    Integer var3 = (Integer)dataTypes.get(object.getClass());
    
    if (var3 == null)
    {
      throw new IllegalArgumentException("Unknown data type: " + object.getClass());
    }
    if (id > 31)
    {
      throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
    }
    if (watchedObjects.containsKey(Integer.valueOf(id)))
    {
      throw new IllegalArgumentException("Duplicate id value for " + id + "!");
    }
    

    WatchableObject var4 = new WatchableObject(var3.intValue(), id, object);
    lock.writeLock().lock();
    watchedObjects.put(Integer.valueOf(id), var4);
    lock.writeLock().unlock();
    isBlank = false;
  }
  




  public void addObjectByDataType(int id, int type)
  {
    WatchableObject var3 = new WatchableObject(type, id, null);
    lock.writeLock().lock();
    watchedObjects.put(Integer.valueOf(id), var3);
    lock.writeLock().unlock();
    isBlank = false;
  }
  



  public byte getWatchableObjectByte(int id)
  {
    return ((Byte)getWatchedObject(id).getObject()).byteValue();
  }
  
  public short getWatchableObjectShort(int id)
  {
    return ((Short)getWatchedObject(id).getObject()).shortValue();
  }
  



  public int getWatchableObjectInt(int id)
  {
    return ((Integer)getWatchedObject(id).getObject()).intValue();
  }
  
  public float getWatchableObjectFloat(int id)
  {
    return ((Float)getWatchedObject(id).getObject()).floatValue();
  }
  



  public String getWatchableObjectString(int id)
  {
    return (String)getWatchedObject(id).getObject();
  }
  



  public ItemStack getWatchableObjectItemStack(int id)
  {
    return (ItemStack)getWatchedObject(id).getObject();
  }
  



  private WatchableObject getWatchedObject(int id)
  {
    lock.readLock().lock();
    

    try
    {
      var2 = (WatchableObject)watchedObjects.get(Integer.valueOf(id));
    }
    catch (Throwable var6) {
      WatchableObject var2;
      CrashReport var4 = CrashReport.makeCrashReport(var6, "Getting synched entity data");
      CrashReportCategory var5 = var4.makeCategory("Synched entity data");
      var5.addCrashSection("Data ID", Integer.valueOf(id));
      throw new ReportedException(var4);
    }
    WatchableObject var2;
    lock.readLock().unlock();
    return var2;
  }
  
  public Rotations getWatchableObjectRotations(int id)
  {
    return (Rotations)getWatchedObject(id).getObject();
  }
  



  public void updateObject(int id, Object newData)
  {
    WatchableObject var3 = getWatchedObject(id);
    
    if (ObjectUtils.notEqual(newData, var3.getObject()))
    {
      var3.setObject(newData);
      owner.func_145781_i(id);
      var3.setWatched(true);
      objectChanged = true;
    }
  }
  
  public void setObjectWatched(int id)
  {
    getWatchedObjectwatched = true;
    objectChanged = true;
  }
  



  public boolean hasObjectChanged()
  {
    return objectChanged;
  }
  



  public static void writeWatchedListToPacketBuffer(List objectsList, PacketBuffer buffer)
    throws IOException
  {
    if (objectsList != null)
    {
      Iterator var2 = objectsList.iterator();
      
      while (var2.hasNext())
      {
        WatchableObject var3 = (WatchableObject)var2.next();
        writeWatchableObjectToPacketBuffer(buffer, var3);
      }
    }
    
    buffer.writeByte(127);
  }
  
  public List getChanged()
  {
    ArrayList var1 = null;
    
    if (objectChanged)
    {
      lock.readLock().lock();
      Iterator var2 = watchedObjects.values().iterator();
      
      while (var2.hasNext())
      {
        WatchableObject var3 = (WatchableObject)var2.next();
        
        if (var3.isWatched())
        {
          var3.setWatched(false);
          
          if (var1 == null)
          {
            var1 = Lists.newArrayList();
          }
          
          var1.add(var3);
        }
      }
      
      lock.readLock().unlock();
    }
    
    objectChanged = false;
    return var1;
  }
  
  public void writeTo(PacketBuffer buffer) throws IOException
  {
    lock.readLock().lock();
    Iterator var2 = watchedObjects.values().iterator();
    
    while (var2.hasNext())
    {
      WatchableObject var3 = (WatchableObject)var2.next();
      writeWatchableObjectToPacketBuffer(buffer, var3);
    }
    
    lock.readLock().unlock();
    buffer.writeByte(127);
  }
  
  public List getAllWatched()
  {
    ArrayList var1 = null;
    lock.readLock().lock();
    
    WatchableObject var3;
    for (Iterator var2 = watchedObjects.values().iterator(); var2.hasNext(); var1.add(var3))
    {
      var3 = (WatchableObject)var2.next();
      
      if (var1 == null)
      {
        var1 = Lists.newArrayList();
      }
    }
    
    lock.readLock().unlock();
    return var1;
  }
  



  private static void writeWatchableObjectToPacketBuffer(PacketBuffer buffer, WatchableObject object)
    throws IOException
  {
    int var2 = (object.getObjectType() << 5 | object.getDataValueId() & 0x1F) & 0xFF;
    buffer.writeByte(var2);
    
    switch (object.getObjectType())
    {
    case 0: 
      buffer.writeByte(((Byte)object.getObject()).byteValue());
      break;
    
    case 1: 
      buffer.writeShort(((Short)object.getObject()).shortValue());
      break;
    
    case 2: 
      buffer.writeInt(((Integer)object.getObject()).intValue());
      break;
    
    case 3: 
      buffer.writeFloat(((Float)object.getObject()).floatValue());
      break;
    
    case 4: 
      buffer.writeString((String)object.getObject());
      break;
    
    case 5: 
      ItemStack var3 = (ItemStack)object.getObject();
      buffer.writeItemStackToBuffer(var3);
      break;
    
    case 6: 
      BlockPos var4 = (BlockPos)object.getObject();
      buffer.writeInt(var4.getX());
      buffer.writeInt(var4.getY());
      buffer.writeInt(var4.getZ());
      break;
    
    case 7: 
      Rotations var5 = (Rotations)object.getObject();
      buffer.writeFloat(var5.func_179415_b());
      buffer.writeFloat(var5.func_179416_c());
      buffer.writeFloat(var5.func_179413_d());
    }
    
  }
  


  public static List readWatchedListFromPacketBuffer(PacketBuffer buffer)
    throws IOException
  {
    ArrayList var1 = null;
    
    for (byte var2 = buffer.readByte(); var2 != Byte.MAX_VALUE; var2 = buffer.readByte())
    {
      if (var1 == null)
      {
        var1 = Lists.newArrayList();
      }
      
      int var3 = (var2 & 0xE0) >> 5;
      int var4 = var2 & 0x1F;
      WatchableObject var5 = null;
      
      switch (var3)
      {
      case 0: 
        var5 = new WatchableObject(var3, var4, Byte.valueOf(buffer.readByte()));
        break;
      
      case 1: 
        var5 = new WatchableObject(var3, var4, Short.valueOf(buffer.readShort()));
        break;
      
      case 2: 
        var5 = new WatchableObject(var3, var4, Integer.valueOf(buffer.readInt()));
        break;
      
      case 3: 
        var5 = new WatchableObject(var3, var4, Float.valueOf(buffer.readFloat()));
        break;
      
      case 4: 
        var5 = new WatchableObject(var3, var4, buffer.readStringFromBuffer(32767));
        break;
      
      case 5: 
        var5 = new WatchableObject(var3, var4, buffer.readItemStackFromBuffer());
        break;
      
      case 6: 
        int var6 = buffer.readInt();
        int var7 = buffer.readInt();
        int var8 = buffer.readInt();
        var5 = new WatchableObject(var3, var4, new BlockPos(var6, var7, var8));
        break;
      
      case 7: 
        float var9 = buffer.readFloat();
        float var10 = buffer.readFloat();
        float var11 = buffer.readFloat();
        var5 = new WatchableObject(var3, var4, new Rotations(var9, var10, var11));
      }
      
      var1.add(var5);
    }
    
    return var1;
  }
  
  public void updateWatchedObjectsFromList(List p_75687_1_)
  {
    lock.writeLock().lock();
    Iterator var2 = p_75687_1_.iterator();
    
    while (var2.hasNext())
    {
      WatchableObject var3 = (WatchableObject)var2.next();
      WatchableObject var4 = (WatchableObject)watchedObjects.get(Integer.valueOf(var3.getDataValueId()));
      
      if (var4 != null)
      {
        var4.setObject(var3.getObject());
        owner.func_145781_i(var3.getDataValueId());
      }
    }
    
    lock.writeLock().unlock();
    objectChanged = true;
  }
  
  public boolean getIsBlank()
  {
    return isBlank;
  }
  
  public void func_111144_e()
  {
    objectChanged = false;
  }
  
  static
  {
    dataTypes.put(Byte.class, Integer.valueOf(0));
    dataTypes.put(Short.class, Integer.valueOf(1));
    dataTypes.put(Integer.class, Integer.valueOf(2));
    dataTypes.put(Float.class, Integer.valueOf(3));
    dataTypes.put(String.class, Integer.valueOf(4));
    dataTypes.put(ItemStack.class, Integer.valueOf(5));
    dataTypes.put(BlockPos.class, Integer.valueOf(6));
    dataTypes.put(Rotations.class, Integer.valueOf(7));
  }
  
  public static class WatchableObject
  {
    private final int objectType;
    private final int dataValueId;
    private Object watchedObject;
    private boolean watched;
    private static final String __OBFID = "CL_00001560";
    
    public WatchableObject(int type, int id, Object object)
    {
      dataValueId = id;
      watchedObject = object;
      objectType = type;
      watched = true;
    }
    
    public int getDataValueId()
    {
      return dataValueId;
    }
    
    public void setObject(Object object)
    {
      watchedObject = object;
    }
    
    public Object getObject()
    {
      return watchedObject;
    }
    
    public int getObjectType()
    {
      return objectType;
    }
    
    public boolean isWatched()
    {
      return watched;
    }
    
    public void setWatched(boolean watched)
    {
      this.watched = watched;
    }
  }
}
