package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer
{
  public static final PropertyBool HAS_RECORD = PropertyBool.create("has_record");
  private static final String __OBFID = "CL_00000260";
  
  protected BlockJukebox()
  {
    super(net.minecraft.block.material.Material.wood);
    setDefaultState(blockState.getBaseState().withProperty(HAS_RECORD, Boolean.valueOf(false)));
    setCreativeTab(CreativeTabs.tabDecorations);
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (((Boolean)state.getValue(HAS_RECORD)).booleanValue())
    {
      dropRecord(worldIn, pos, state);
      state = state.withProperty(HAS_RECORD, Boolean.valueOf(false));
      worldIn.setBlockState(pos, state, 2);
      return true;
    }
    

    return false;
  }
  

  public void insertRecord(World worldIn, BlockPos pos, IBlockState state, ItemStack recordStack)
  {
    if (!isRemote)
    {
      TileEntity var5 = worldIn.getTileEntity(pos);
      
      if ((var5 instanceof TileEntityJukebox))
      {
        ((TileEntityJukebox)var5).setRecord(new ItemStack(recordStack.getItem(), 1, recordStack.getMetadata()));
        worldIn.setBlockState(pos, state.withProperty(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
    }
  }
  
  private void dropRecord(World worldIn, BlockPos pos, IBlockState state)
  {
    if (!isRemote)
    {
      TileEntity var4 = worldIn.getTileEntity(pos);
      
      if ((var4 instanceof TileEntityJukebox))
      {
        TileEntityJukebox var5 = (TileEntityJukebox)var4;
        ItemStack var6 = var5.getRecord();
        
        if (var6 != null)
        {
          worldIn.playAuxSFX(1005, pos, 0);
          worldIn.func_175717_a(pos, null);
          var5.setRecord(null);
          float var7 = 0.7F;
          double var8 = rand.nextFloat() * var7 + (1.0F - var7) * 0.5D;
          double var10 = rand.nextFloat() * var7 + (1.0F - var7) * 0.2D + 0.6D;
          double var12 = rand.nextFloat() * var7 + (1.0F - var7) * 0.5D;
          ItemStack var14 = var6.copy();
          EntityItem var15 = new EntityItem(worldIn, pos.getX() + var8, pos.getY() + var10, pos.getZ() + var12, var14);
          var15.setDefaultPickupDelay();
          worldIn.spawnEntityInWorld(var15);
        }
      }
    }
  }
  
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
  {
    dropRecord(worldIn, pos, state);
    super.breakBlock(worldIn, pos, state);
  }
  






  public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
  {
    if (!isRemote)
    {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
    }
  }
  



  public TileEntity createNewTileEntity(World worldIn, int meta)
  {
    return new TileEntityJukebox();
  }
  
  public boolean hasComparatorInputOverride()
  {
    return true;
  }
  
  public int getComparatorInputOverride(World worldIn, BlockPos pos)
  {
    TileEntity var3 = worldIn.getTileEntity(pos);
    
    if ((var3 instanceof TileEntityJukebox))
    {
      ItemStack var4 = ((TileEntityJukebox)var3).getRecord();
      
      if (var4 != null)
      {
        return Item.getIdFromItem(var4.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
      }
    }
    
    return 0;
  }
  



  public int getRenderType()
  {
    return 3;
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    return getDefaultState().withProperty(HAS_RECORD, Boolean.valueOf(meta > 0));
  }
  



  public int getMetaFromState(IBlockState state)
  {
    return ((Boolean)state.getValue(HAS_RECORD)).booleanValue() ? 1 : 0;
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new IProperty[] { HAS_RECORD });
  }
  
  public static class TileEntityJukebox extends TileEntity {
    private ItemStack record;
    private static final String __OBFID = "CL_00000261";
    
    public TileEntityJukebox() {}
    
    public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      
      if (compound.hasKey("RecordItem", 10))
      {
        setRecord(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("RecordItem")));
      }
      else if (compound.getInteger("Record") > 0)
      {
        setRecord(new ItemStack(Item.getItemById(compound.getInteger("Record")), 1, 0));
      }
    }
    
    public void writeToNBT(NBTTagCompound compound)
    {
      super.writeToNBT(compound);
      
      if (getRecord() != null)
      {
        compound.setTag("RecordItem", getRecord().writeToNBT(new NBTTagCompound()));
      }
    }
    
    public ItemStack getRecord()
    {
      return record;
    }
    
    public void setRecord(ItemStack recordStack)
    {
      record = recordStack;
      markDirty();
    }
  }
}
