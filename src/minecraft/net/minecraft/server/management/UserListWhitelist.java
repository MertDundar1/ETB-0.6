package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.Map;

public class UserListWhitelist extends UserList
{
  private static final String __OBFID = "CL_00001871";
  
  public UserListWhitelist(java.io.File p_i1132_1_)
  {
    super(p_i1132_1_);
  }
  
  protected UserListEntry createEntry(JsonObject entryData)
  {
    return new UserListWhitelistEntry(entryData);
  }
  
  public String[] getKeys()
  {
    String[] var1 = new String[getValues().size()];
    int var2 = 0;
    
    UserListWhitelistEntry var4;
    for (Iterator var3 = getValues().values().iterator(); var3.hasNext(); var1[(var2++)] = ((GameProfile)var4.getValue()).getName())
    {
      var4 = (UserListWhitelistEntry)var3.next();
    }
    
    return var1;
  }
  
  protected String func_152704_b(GameProfile p_152704_1_)
  {
    return p_152704_1_.getId().toString();
  }
  
  public GameProfile func_152706_a(String p_152706_1_)
  {
    Iterator var2 = getValues().values().iterator();
    
    UserListWhitelistEntry var3;
    do
    {
      if (!var2.hasNext())
      {
        return null;
      }
      
      var3 = (UserListWhitelistEntry)var2.next();
    }
    while (!p_152706_1_.equalsIgnoreCase(((GameProfile)var3.getValue()).getName()));
    
    return (GameProfile)var3.getValue();
  }
  



  protected String getObjectKey(Object obj)
  {
    return func_152704_b((GameProfile)obj);
  }
}
