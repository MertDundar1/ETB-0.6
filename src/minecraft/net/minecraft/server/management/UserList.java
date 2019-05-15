package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList
{
  protected static final Logger logger = ;
  protected final Gson gson;
  private final File saveFile;
  private final Map values = Maps.newHashMap();
  private boolean lanServer = true;
  private static final ParameterizedType saveFileFormat = new ParameterizedType()
  {
    private static final String __OBFID = "CL_00001875";
    
    public Type[] getActualTypeArguments() {
      return new Type[] { UserListEntry.class };
    }
    
    public Type getRawType() {
      return java.util.List.class;
    }
    
    public Type getOwnerType() {
      return null;
    }
  };
  private static final String __OBFID = "CL_00001876";
  
  public UserList(File saveFile)
  {
    this.saveFile = saveFile;
    GsonBuilder var2 = new GsonBuilder().setPrettyPrinting();
    var2.registerTypeHierarchyAdapter(UserListEntry.class, new Serializer(null));
    gson = var2.create();
  }
  
  public boolean isLanServer()
  {
    return lanServer;
  }
  
  public void setLanServer(boolean state)
  {
    lanServer = state;
  }
  



  public void addEntry(UserListEntry entry)
  {
    values.put(getObjectKey(entry.getValue()), entry);
    
    try
    {
      writeChanges();
    }
    catch (IOException var3)
    {
      logger.warn("Could not save the list after adding a user.", var3);
    }
  }
  
  public UserListEntry getEntry(Object obj)
  {
    removeExpired();
    return (UserListEntry)values.get(getObjectKey(obj));
  }
  
  public void removeEntry(Object p_152684_1_)
  {
    values.remove(getObjectKey(p_152684_1_));
    
    try
    {
      writeChanges();
    }
    catch (IOException var3)
    {
      logger.warn("Could not save the list after removing a user.", var3);
    }
  }
  
  public String[] getKeys()
  {
    return (String[])values.keySet().toArray(new String[values.size()]);
  }
  



  protected String getObjectKey(Object obj)
  {
    return obj.toString();
  }
  
  protected boolean hasEntry(Object entry)
  {
    return values.containsKey(getObjectKey(entry));
  }
  




  private void removeExpired()
  {
    ArrayList var1 = Lists.newArrayList();
    Iterator var2 = values.values().iterator();
    
    while (var2.hasNext())
    {
      UserListEntry var3 = (UserListEntry)var2.next();
      
      if (var3.hasBanExpired())
      {
        var1.add(var3.getValue());
      }
    }
    
    var2 = var1.iterator();
    
    while (var2.hasNext())
    {
      Object var4 = var2.next();
      values.remove(var4);
    }
  }
  
  protected UserListEntry createEntry(JsonObject entryData)
  {
    return new UserListEntry(null, entryData);
  }
  
  protected Map getValues()
  {
    return values;
  }
  
  public void writeChanges() throws IOException
  {
    Collection var1 = values.values();
    String var2 = gson.toJson(var1);
    BufferedWriter var3 = null;
    
    try
    {
      var3 = Files.newWriter(saveFile, Charsets.UTF_8);
      var3.write(var2);
    }
    finally
    {
      IOUtils.closeQuietly(var3);
    }
  }
  
  class Serializer implements JsonDeserializer, JsonSerializer
  {
    private static final String __OBFID = "CL_00001874";
    
    private Serializer() {}
    
    public JsonElement serializeEntry(UserListEntry p_152751_1_, Type p_152751_2_, JsonSerializationContext p_152751_3_)
    {
      JsonObject var4 = new JsonObject();
      p_152751_1_.onSerialization(var4);
      return var4;
    }
    
    public UserListEntry deserializeEntry(JsonElement p_152750_1_, Type p_152750_2_, JsonDeserializationContext p_152750_3_)
    {
      if (p_152750_1_.isJsonObject())
      {
        JsonObject var4 = p_152750_1_.getAsJsonObject();
        UserListEntry var5 = createEntry(var4);
        return var5;
      }
      

      return null;
    }
    

    public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
    {
      return serializeEntry((UserListEntry)p_serialize_1_, p_serialize_2_, p_serialize_3_);
    }
    
    public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_)
    {
      return deserializeEntry(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
    }
    
    Serializer(Object p_i1141_2_)
    {
      this();
    }
  }
}
