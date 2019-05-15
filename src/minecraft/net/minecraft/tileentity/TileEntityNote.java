package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity
{
  public byte note;
  public boolean previousRedstoneState;
  private static final String __OBFID = "CL_00000362";
  
  public TileEntityNote() {}
  
  public void writeToNBT(NBTTagCompound compound)
  {
    super.writeToNBT(compound);
    compound.setByte("note", note);
  }
  
  public void readFromNBT(NBTTagCompound compound)
  {
    super.readFromNBT(compound);
    note = compound.getByte("note");
    note = ((byte)MathHelper.clamp_int(note, 0, 24));
  }
  



  public void changePitch()
  {
    note = ((byte)((note + 1) % 25));
    markDirty();
  }
  
  public void func_175108_a(World worldIn, BlockPos p_175108_2_)
  {
    if (worldIn.getBlockState(p_175108_2_.offsetUp()).getBlock().getMaterial() == Material.air)
    {
      Material var3 = worldIn.getBlockState(p_175108_2_.offsetDown()).getBlock().getMaterial();
      byte var4 = 0;
      
      if (var3 == Material.rock)
      {
        var4 = 1;
      }
      
      if (var3 == Material.sand)
      {
        var4 = 2;
      }
      
      if (var3 == Material.glass)
      {
        var4 = 3;
      }
      
      if (var3 == Material.wood)
      {
        var4 = 4;
      }
      
      worldIn.addBlockEvent(p_175108_2_, net.minecraft.init.Blocks.noteblock, var4, note);
    }
  }
}
