package optifine;

import java.io.ByteArrayInputStream;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ReflectorForge
{
  public ReflectorForge() {}
  
  public static void FMLClientHandler_trackBrokenTexture(ResourceLocation loc, String message)
  {
    if (!Reflector.FMLClientHandler_trackBrokenTexture.exists())
    {
      Object instance = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0]);
      Reflector.call(instance, Reflector.FMLClientHandler_trackBrokenTexture, new Object[] { loc, message });
    }
  }
  
  public static void FMLClientHandler_trackMissingTexture(ResourceLocation loc)
  {
    if (!Reflector.FMLClientHandler_trackMissingTexture.exists())
    {
      Object instance = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0]);
      Reflector.call(instance, Reflector.FMLClientHandler_trackMissingTexture, new Object[] { loc });
    }
  }
  
  public static void putLaunchBlackboard(String key, Object value)
  {
    Map blackboard = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);
    
    if (blackboard != null)
    {
      blackboard.put(key, value);
    }
  }
  
  public static java.io.InputStream getOptiFineResourceStream(String path)
  {
    if (!Reflector.OptiFineClassTransformer_instance.exists())
    {
      return null;
    }
    

    Object instance = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);
    
    if (instance == null)
    {
      return null;
    }
    

    if (path.startsWith("/"))
    {
      path = path.substring(1);
    }
    
    byte[] bytes = (byte[])Reflector.call(instance, Reflector.OptiFineClassTransformer_getOptiFineResource, new Object[] { path });
    
    if (bytes == null)
    {
      return null;
    }
    

    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    return in;
  }
  



  public static boolean blockHasTileEntity(IBlockState state)
  {
    net.minecraft.block.Block block = state.getBlock();
    return !Reflector.ForgeBlock_hasTileEntity.exists() ? block.hasTileEntity() : Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, new Object[] { state });
  }
  
  public static boolean isItemDamaged(ItemStack stack)
  {
    return !Reflector.ForgeItem_showDurabilityBar.exists() ? stack.isItemDamaged() : Reflector.callBoolean(stack.getItem(), Reflector.ForgeItem_showDurabilityBar, new Object[] { stack });
  }
}
