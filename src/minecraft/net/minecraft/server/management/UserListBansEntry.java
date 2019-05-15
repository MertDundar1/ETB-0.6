package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry
{
  private static final String __OBFID = "CL_00001872";
  
  public UserListBansEntry(GameProfile p_i1134_1_)
  {
    this(p_i1134_1_, null, null, null, null);
  }
  
  public UserListBansEntry(GameProfile p_i1135_1_, Date p_i1135_2_, String p_i1135_3_, Date p_i1135_4_, String p_i1135_5_)
  {
    super(p_i1135_1_, p_i1135_4_, p_i1135_3_, p_i1135_4_, p_i1135_5_);
  }
  
  public UserListBansEntry(JsonObject p_i1136_1_)
  {
    super(func_152648_b(p_i1136_1_), p_i1136_1_);
  }
  
  protected void onSerialization(JsonObject data)
  {
    if (getValue() != null)
    {
      data.addProperty("uuid", ((GameProfile)getValue()).getId() == null ? "" : ((GameProfile)getValue()).getId().toString());
      data.addProperty("name", ((GameProfile)getValue()).getName());
      super.onSerialization(data);
    }
  }
  
  private static GameProfile func_152648_b(JsonObject p_152648_0_)
  {
    if ((p_152648_0_.has("uuid")) && (p_152648_0_.has("name")))
    {
      String var1 = p_152648_0_.get("uuid").getAsString();
      

      try
      {
        var2 = UUID.fromString(var1);
      }
      catch (Throwable var4) {
        UUID var2;
        return null;
      }
      UUID var2;
      return new GameProfile(var2, p_152648_0_.get("name").getAsString());
    }
    

    return null;
  }
}
