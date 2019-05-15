package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemModelMesher
{
  private final Map simpleShapes = Maps.newHashMap();
  private final Map simpleShapesCache = Maps.newHashMap();
  private final Map shapers = Maps.newHashMap();
  private final ModelManager modelManager;
  private static final String __OBFID = "CL_00002536";
  
  public ItemModelMesher(ModelManager p_i46250_1_)
  {
    modelManager = p_i46250_1_;
  }
  
  public TextureAtlasSprite getParticleIcon(Item p_178082_1_)
  {
    return getParticleIcon(p_178082_1_, 0);
  }
  
  public TextureAtlasSprite getParticleIcon(Item p_178087_1_, int p_178087_2_)
  {
    return getItemModel(new ItemStack(p_178087_1_, 1, p_178087_2_)).getTexture();
  }
  
  public IBakedModel getItemModel(ItemStack p_178089_1_)
  {
    Item var2 = p_178089_1_.getItem();
    IBakedModel var3 = getItemModel(var2, getMetadata(p_178089_1_));
    
    if (var3 == null)
    {
      ItemMeshDefinition var4 = (ItemMeshDefinition)shapers.get(var2);
      
      if (var4 != null)
      {
        var3 = modelManager.getModel(var4.getModelLocation(p_178089_1_));
      }
    }
    
    if (var3 == null)
    {
      var3 = modelManager.getMissingModel();
    }
    
    return var3;
  }
  
  protected int getMetadata(ItemStack p_178084_1_)
  {
    return p_178084_1_.isItemStackDamageable() ? 0 : p_178084_1_.getMetadata();
  }
  
  protected IBakedModel getItemModel(Item p_178088_1_, int p_178088_2_)
  {
    return (IBakedModel)simpleShapesCache.get(Integer.valueOf(getIndex(p_178088_1_, p_178088_2_)));
  }
  
  private int getIndex(Item p_178081_1_, int p_178081_2_)
  {
    return Item.getIdFromItem(p_178081_1_) << 16 | p_178081_2_;
  }
  
  public void register(Item p_178086_1_, int p_178086_2_, ModelResourceLocation p_178086_3_)
  {
    simpleShapes.put(Integer.valueOf(getIndex(p_178086_1_, p_178086_2_)), p_178086_3_);
    simpleShapesCache.put(Integer.valueOf(getIndex(p_178086_1_, p_178086_2_)), modelManager.getModel(p_178086_3_));
  }
  
  public void register(Item p_178080_1_, ItemMeshDefinition p_178080_2_)
  {
    shapers.put(p_178080_1_, p_178080_2_);
  }
  
  public ModelManager getModelManager()
  {
    return modelManager;
  }
  
  public void rebuildCache()
  {
    simpleShapesCache.clear();
    Iterator var1 = simpleShapes.entrySet().iterator();
    
    while (var1.hasNext())
    {
      Map.Entry var2 = (Map.Entry)var1.next();
      simpleShapesCache.put(var2.getKey(), modelManager.getModel((ModelResourceLocation)var2.getValue()));
    }
  }
}
