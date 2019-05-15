package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;

public class IMetadataSerializer
{
  private final IRegistry metadataSectionSerializerRegistry = new RegistrySimple();
  private final GsonBuilder gsonBuilder = new GsonBuilder();
  

  private Gson gson;
  
  private static final String __OBFID = "CL_00001101";
  

  public IMetadataSerializer()
  {
    gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
    gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new net.minecraft.util.ChatStyle.Serializer());
    gsonBuilder.registerTypeAdapterFactory(new net.minecraft.util.EnumTypeAdapterFactory());
  }
  
  public void registerMetadataSectionType(IMetadataSectionSerializer p_110504_1_, Class p_110504_2_)
  {
    metadataSectionSerializerRegistry.putObject(p_110504_1_.getSectionName(), new Registration(p_110504_1_, p_110504_2_, null));
    gsonBuilder.registerTypeAdapter(p_110504_2_, p_110504_1_);
    gson = null;
  }
  
  public IMetadataSection parseMetadataSection(String p_110503_1_, JsonObject p_110503_2_)
  {
    if (p_110503_1_ == null)
    {
      throw new IllegalArgumentException("Metadata section name cannot be null");
    }
    if (!p_110503_2_.has(p_110503_1_))
    {
      return null;
    }
    if (!p_110503_2_.get(p_110503_1_).isJsonObject())
    {
      throw new IllegalArgumentException("Invalid metadata for '" + p_110503_1_ + "' - expected object, found " + p_110503_2_.get(p_110503_1_));
    }
    

    Registration var3 = (Registration)metadataSectionSerializerRegistry.getObject(p_110503_1_);
    
    if (var3 == null)
    {
      throw new IllegalArgumentException("Don't know how to handle metadata section '" + p_110503_1_ + "'");
    }
    

    return (IMetadataSection)getGson().fromJson(p_110503_2_.getAsJsonObject(p_110503_1_), field_110500_b);
  }
  





  private Gson getGson()
  {
    if (gson == null)
    {
      gson = gsonBuilder.create();
    }
    
    return gson;
  }
  
  class Registration
  {
    final IMetadataSectionSerializer field_110502_a;
    final Class field_110500_b;
    private static final String __OBFID = "CL_00001103";
    
    private Registration(IMetadataSectionSerializer p_i1305_2_, Class p_i1305_3_)
    {
      field_110502_a = p_i1305_2_;
      field_110500_b = p_i1305_3_;
    }
    
    Registration(IMetadataSectionSerializer p_i1306_2_, Class p_i1306_3_, Object p_i1306_4_)
    {
      this(p_i1306_2_, p_i1306_3_);
    }
  }
}
