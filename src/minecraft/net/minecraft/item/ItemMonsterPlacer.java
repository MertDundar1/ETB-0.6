package net.minecraft.item;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item
{
  private static final String __OBFID = "CL_00000070";
  
  public ItemMonsterPlacer()
  {
    setHasSubtypes(true);
    setCreativeTab(CreativeTabs.tabMisc);
  }
  
  public String getItemStackDisplayName(ItemStack stack)
  {
    String var2 = StatCollector.translateToLocal(new StringBuilder(String.valueOf(getUnlocalizedName())).append(".name").toString()).trim();
    String var3 = EntityList.getStringFromID(stack.getMetadata());
    
    if (var3 != null)
    {
      var2 = var2 + " " + StatCollector.translateToLocal(new StringBuilder("entity.").append(var3).append(".name").toString());
    }
    
    return var2;
  }
  
  public int getColorFromItemStack(ItemStack stack, int renderPass)
  {
    EntityList.EntityEggInfo var3 = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(stack.getMetadata()));
    return var3 != null ? secondaryColor : renderPass == 0 ? primaryColor : 16777215;
  }
  






  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (isRemote)
    {
      return true;
    }
    if (!playerIn.func_175151_a(pos.offset(side), side, stack))
    {
      return false;
    }
    

    IBlockState var9 = worldIn.getBlockState(pos);
    
    if (var9.getBlock() == net.minecraft.init.Blocks.mob_spawner)
    {
      TileEntity var10 = worldIn.getTileEntity(pos);
      
      if ((var10 instanceof TileEntityMobSpawner))
      {
        MobSpawnerBaseLogic var11 = ((TileEntityMobSpawner)var10).getSpawnerBaseLogic();
        var11.setEntityName(EntityList.getStringFromID(stack.getMetadata()));
        var10.markDirty();
        worldIn.markBlockForUpdate(pos);
        
        if (!capabilities.isCreativeMode)
        {
          stackSize -= 1;
        }
        
        return true;
      }
    }
    
    pos = pos.offset(side);
    double var13 = 0.0D;
    
    if ((side == EnumFacing.UP) && ((var9 instanceof net.minecraft.block.BlockFence)))
    {
      var13 = 0.5D;
    }
    
    Entity var12 = spawnCreature(worldIn, stack.getMetadata(), pos.getX() + 0.5D, pos.getY() + var13, pos.getZ() + 0.5D);
    
    if (var12 != null)
    {
      if (((var12 instanceof EntityLivingBase)) && (stack.hasDisplayName()))
      {
        var12.setCustomNameTag(stack.getDisplayName());
      }
      
      if (!capabilities.isCreativeMode)
      {
        stackSize -= 1;
      }
    }
    
    return true;
  }
  




  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
  {
    if (isRemote)
    {
      return itemStackIn;
    }
    

    MovingObjectPosition var4 = getMovingObjectPositionFromPlayer(worldIn, playerIn, true);
    
    if (var4 == null)
    {
      return itemStackIn;
    }
    

    if (typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK)
    {
      BlockPos var5 = var4.func_178782_a();
      
      if (!worldIn.isBlockModifiable(playerIn, var5))
      {
        return itemStackIn;
      }
      
      if (!playerIn.func_175151_a(var5, field_178784_b, itemStackIn))
      {
        return itemStackIn;
      }
      
      if ((worldIn.getBlockState(var5).getBlock() instanceof net.minecraft.block.BlockLiquid))
      {
        Entity var6 = spawnCreature(worldIn, itemStackIn.getMetadata(), var5.getX() + 0.5D, var5.getY() + 0.5D, var5.getZ() + 0.5D);
        
        if (var6 != null)
        {
          if (((var6 instanceof EntityLivingBase)) && (itemStackIn.hasDisplayName()))
          {
            ((EntityLiving)var6).setCustomNameTag(itemStackIn.getDisplayName());
          }
          
          if (!capabilities.isCreativeMode)
          {
            stackSize -= 1;
          }
          
          playerIn.triggerAchievement(net.minecraft.stats.StatList.objectUseStats[Item.getIdFromItem(this)]);
        }
      }
    }
    
    return itemStackIn;
  }
  






  public static Entity spawnCreature(World worldIn, int p_77840_1_, double p_77840_2_, double p_77840_4_, double p_77840_6_)
  {
    if (!EntityList.entityEggs.containsKey(Integer.valueOf(p_77840_1_)))
    {
      return null;
    }
    

    Entity var8 = null;
    
    for (int var9 = 0; var9 < 1; var9++)
    {
      var8 = EntityList.createEntityByID(p_77840_1_, worldIn);
      
      if ((var8 instanceof EntityLivingBase))
      {
        EntityLiving var10 = (EntityLiving)var8;
        var8.setLocationAndAngles(p_77840_2_, p_77840_4_, p_77840_6_, MathHelper.wrapAngleTo180_float(rand.nextFloat() * 360.0F), 0.0F);
        rotationYawHead = rotationYaw;
        renderYawOffset = rotationYaw;
        var10.func_180482_a(worldIn.getDifficultyForLocation(new BlockPos(var10)), null);
        worldIn.spawnEntityInWorld(var8);
        var10.playLivingSound();
      }
    }
    
    return var8;
  }
  






  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
  {
    Iterator var4 = EntityList.entityEggs.values().iterator();
    
    while (var4.hasNext())
    {
      EntityList.EntityEggInfo var5 = (EntityList.EntityEggInfo)var4.next();
      subItems.add(new ItemStack(itemIn, 1, spawnedID));
    }
  }
}
