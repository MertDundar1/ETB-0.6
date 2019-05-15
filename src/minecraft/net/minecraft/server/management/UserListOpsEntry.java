package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListOpsEntry extends UserListEntry
{
  private final int field_152645_a;
  private static final String __OBFID = "CL_00001878";
  
  public UserListOpsEntry(GameProfile p_i46328_1_, int p_i46328_2_)
  {
    super(p_i46328_1_);
    field_152645_a = p_i46328_2_;
  }
  
  public UserListOpsEntry(JsonObject p_i1150_1_)
  {
    super(func_152643_b(p_i1150_1_), p_i1150_1_);
    field_152645_a = (p_i1150_1_.has("level") ? p_i1150_1_.get("level").getAsInt() : 0);
  }
  
  public int func_152644_a()
  {
    return field_152645_a;
  }
  
  protected void onSerialization(JsonObject data)
  {
    if (getValue() != null)
    {
      data.addProperty("uuid", ((GameProfile)getValue()).getId() == null ? "" : ((GameProfile)getValue()).getId().toString());
      data.addProperty("name", ((GameProfile)getValue()).getName());
      super.onSerialization(data);
      data.addProperty("level", Integer.valueOf(field_152645_a));
    }
  }
  
  private static GameProfile func_152643_b(JsonObject p_152643_0_)
  {
    if ((p_152643_0_.has("uuid")) && (p_152643_0_.has("name")))
    {
      String var1 = p_152643_0_.get("uuid").getAsString();
      

      try
      {
        var2 = UUID.fromString(var1);
      }
      catch (Throwable var4) {
        UUID var2;
        return null;
      }
      UUID var2;
      return new GameProfile(var2, p_152643_0_.get("name").getAsString());
    }
    

    return null;
  }
}
