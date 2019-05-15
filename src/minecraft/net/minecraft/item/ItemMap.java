package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapInfo;
import net.minecraft.world.storage.WorldInfo;

public class ItemMap extends ItemMapBase
{
  private static final String __OBFID = "CL_00000047";
  
  protected ItemMap()
  {
    setHasSubtypes(true);
  }
  
  public static MapData loadMapData(int p_150912_0_, World worldIn)
  {
    String var2 = "map_" + p_150912_0_;
    MapData var3 = (MapData)worldIn.loadItemData(MapData.class, var2);
    
    if (var3 == null)
    {
      var3 = new MapData(var2);
      worldIn.setItemData(var2, var3);
    }
    
    return var3;
  }
  
  public MapData getMapData(ItemStack p_77873_1_, World worldIn)
  {
    String var3 = "map_" + p_77873_1_.getMetadata();
    MapData var4 = (MapData)worldIn.loadItemData(MapData.class, var3);
    
    if ((var4 == null) && (!isRemote))
    {
      p_77873_1_.setItemDamage(worldIn.getUniqueDataId("map"));
      var3 = "map_" + p_77873_1_.getMetadata();
      var4 = new MapData(var3);
      scale = 3;
      var4.func_176054_a(worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), scale);
      dimension = ((byte)provider.getDimensionId());
      var4.markDirty();
      worldIn.setItemData(var3, var4);
    }
    
    return var4;
  }
  
  public void updateMapData(World worldIn, Entity p_77872_2_, MapData p_77872_3_)
  {
    if ((provider.getDimensionId() == dimension) && ((p_77872_2_ instanceof EntityPlayer)))
    {
      int var4 = 1 << scale;
      int var5 = xCenter;
      int var6 = zCenter;
      int var7 = MathHelper.floor_double(posX - var5) / var4 + 64;
      int var8 = MathHelper.floor_double(posZ - var6) / var4 + 64;
      int var9 = 128 / var4;
      
      if (provider.getHasNoSky())
      {
        var9 /= 2;
      }
      
      MapData.MapInfo var10 = p_77872_3_.func_82568_a((EntityPlayer)p_77872_2_);
      field_82569_d += 1;
      boolean var11 = false;
      
      for (int var12 = var7 - var9 + 1; var12 < var7 + var9; var12++)
      {
        if (((var12 & 0xF) == (field_82569_d & 0xF)) || (var11))
        {
          var11 = false;
          double var13 = 0.0D;
          
          for (int var15 = var8 - var9 - 1; var15 < var8 + var9; var15++)
          {
            if ((var12 >= 0) && (var15 >= -1) && (var12 < 128) && (var15 < 128))
            {
              int var16 = var12 - var7;
              int var17 = var15 - var8;
              boolean var18 = var16 * var16 + var17 * var17 > (var9 - 2) * (var9 - 2);
              int var19 = (var5 / var4 + var12 - 64) * var4;
              int var20 = (var6 / var4 + var15 - 64) * var4;
              HashMultiset var21 = HashMultiset.create();
              Chunk var22 = worldIn.getChunkFromBlockCoords(new BlockPos(var19, 0, var20));
              
              if (!var22.isEmpty())
              {
                int var23 = var19 & 0xF;
                int var24 = var20 & 0xF;
                int var25 = 0;
                double var26 = 0.0D;
                

                if (provider.getHasNoSky())
                {
                  int var28 = var19 + var20 * 231871;
                  var28 = var28 * var28 * 31287121 + var28 * 11;
                  
                  if ((var28 >> 20 & 0x1) == 0)
                  {
                    var21.add(Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState().withProperty(net.minecraft.block.BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.DIRT)), 10);
                  }
                  else
                  {
                    var21.add(Blocks.stone.getMapColor(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT_PROP, net.minecraft.block.BlockStone.EnumType.STONE)), 100);
                  }
                  
                  var26 = 100.0D;
                }
                else
                {
                  for (int var28 = 0; var28 < var4; var28++)
                  {
                    for (int var29 = 0; var29 < var4; var29++)
                    {
                      int var30 = var22.getHeight(var28 + var23, var29 + var24) + 1;
                      IBlockState var31 = Blocks.air.getDefaultState();
                      
                      if (var30 > 1)
                      {
                        do
                        {
                          var30--;
                          var31 = var22.getBlockState(new BlockPos(var28 + var23, var30, var29 + var24));
                        }
                        while ((var31.getBlock().getMapColor(var31) == MapColor.airColor) && (var30 > 0));
                        
                        if ((var30 > 0) && (var31.getBlock().getMaterial().isLiquid()))
                        {
                          int var32 = var30 - 1;
                          
                          Block var33;
                          do
                          {
                            var33 = var22.getBlock(var28 + var23, var32--, var29 + var24);
                            var25++;
                          }
                          while ((var32 > 0) && (var33.getMaterial().isLiquid()));
                        }
                      }
                      
                      var26 += var30 / (var4 * var4);
                      var21.add(var31.getBlock().getMapColor(var31));
                    }
                  }
                }
                
                var25 /= var4 * var4;
                double var34 = (var26 - var13) * 4.0D / (var4 + 4) + ((var12 + var15 & 0x1) - 0.5D) * 0.4D;
                byte var35 = 1;
                
                if (var34 > 0.6D)
                {
                  var35 = 2;
                }
                
                if (var34 < -0.6D)
                {
                  var35 = 0;
                }
                
                MapColor var36 = (MapColor)com.google.common.collect.Iterables.getFirst(com.google.common.collect.Multisets.copyHighestCountFirst(var21), MapColor.airColor);
                
                if (var36 == MapColor.waterColor)
                {
                  var34 = var25 * 0.1D + (var12 + var15 & 0x1) * 0.2D;
                  var35 = 1;
                  
                  if (var34 < 0.5D)
                  {
                    var35 = 2;
                  }
                  
                  if (var34 > 0.9D)
                  {
                    var35 = 0;
                  }
                }
                
                var13 = var26;
                
                if ((var15 >= 0) && (var16 * var16 + var17 * var17 < var9 * var9) && ((!var18) || ((var12 + var15 & 0x1) != 0)))
                {
                  byte var37 = colors[(var12 + var15 * 128)];
                  byte var38 = (byte)(colorIndex * 4 + var35);
                  
                  if (var37 != var38)
                  {
                    colors[(var12 + var15 * 128)] = var38;
                    p_77872_3_.func_176053_a(var12, var15);
                    var11 = true;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  




  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
  {
    if (!isRemote)
    {
      MapData var6 = getMapData(stack, worldIn);
      
      if ((entityIn instanceof EntityPlayer))
      {
        EntityPlayer var7 = (EntityPlayer)entityIn;
        var6.updateVisiblePlayers(var7, stack);
      }
      
      if (isSelected)
      {
        updateMapData(worldIn, entityIn, var6);
      }
    }
  }
  
  public net.minecraft.network.Packet createMapDataPacket(ItemStack p_150911_1_, World worldIn, EntityPlayer p_150911_3_)
  {
    return getMapData(p_150911_1_, worldIn).func_176052_a(p_150911_1_, worldIn, p_150911_3_);
  }
  



  public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
  {
    if ((stack.hasTagCompound()) && (stack.getTagCompound().getBoolean("map_is_scaling")))
    {
      MapData var4 = Items.filled_map.getMapData(stack, worldIn);
      stack.setItemDamage(worldIn.getUniqueDataId("map"));
      MapData var5 = new MapData("map_" + stack.getMetadata());
      scale = ((byte)(scale + 1));
      
      if (scale > 4)
      {
        scale = 4;
      }
      
      var5.func_176054_a(xCenter, zCenter, scale);
      dimension = dimension;
      var5.markDirty();
      worldIn.setItemData("map_" + stack.getMetadata(), var5);
    }
  }
  






  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
  {
    MapData var5 = getMapData(stack, worldObj);
    
    if (advanced)
    {
      if (var5 == null)
      {
        tooltip.add("Unknown map");
      }
      else
      {
        tooltip.add("Scaling at 1:" + (1 << scale));
        tooltip.add("(Level " + scale + "/" + 4 + ")");
      }
    }
  }
}
