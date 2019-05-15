package net.minecraft.block.state.pattern;

import com.enjoytheban.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class BlockHelper implements com.google.common.base.Predicate
{
  private final Block block;
  private static final String __OBFID = "CL_00002020";
  
  private BlockHelper(Block p_i45654_1_)
  {
    block = p_i45654_1_;
  }
  
  public static BlockHelper forBlock(Block p_177642_0_) {
    return new BlockHelper(p_177642_0_);
  }
  
  public boolean isBlockEqualTo(IBlockState p_177643_1_) {
    return (p_177643_1_ != null) && (p_177643_1_.getBlock() == block);
  }
  
  public boolean apply(Object p_apply_1_) {
    return isBlockEqualTo((IBlockState)p_apply_1_);
  }
  
  public static Block getBlock(double x, double y, double z) {
    return mctheWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
  }
  
  public static boolean insideBlock() {
    for (int x = MathHelper.floor_double(mcthePlayer.boundingBox.minX); x < 
          MathHelper.floor_double(mcthePlayer.boundingBox.maxX) + 1; x++) {
      for (int y = MathHelper.floor_double(mcthePlayer.boundingBox.minY); y < 
            MathHelper.floor_double(mcthePlayer.boundingBox.maxY) + 1; y++) {
        for (int z = MathHelper.floor_double(mcthePlayer.boundingBox.minZ); z < 
              MathHelper.floor_double(mcthePlayer.boundingBox.maxZ) + 1; z++) {
          Block block = mctheWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
          if ((block != null) && (!(block instanceof BlockAir))) {
            AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mctheWorld, 
              new BlockPos(x, y, z), mctheWorld.getBlockState(new BlockPos(x, y, z)));
            if ((block instanceof net.minecraft.block.BlockHopper)) {
              boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
            }
            if ((boundingBox != null) && (mcthePlayer.boundingBox.intersectsWith(boundingBox))) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  public static boolean isOnLiquid() {
    boolean onLiquid = false;
    int y = (int)mcthePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
    for (int x = MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().minX); x < 
          MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().maxX) + 1; x++) {
      for (int z = MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().minZ); z < 
            MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
        Block block = getBlock(x, y, z);
        if ((block != null) && (!(block instanceof BlockAir))) {
          if (!(block instanceof net.minecraft.block.BlockLiquid)) {
            return false;
          }
          onLiquid = true;
        }
      }
    }
    return onLiquid;
  }
  
  public static boolean isInLiquid() {
    boolean inLiquid = false;
    int y = (int)mcthePlayer.getEntityBoundingBox().minY;
    for (int x = MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().minX); x < 
          MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().maxX) + 1; x++) {
      for (int z = MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().minZ); z < 
            MathHelper.floor_double(mcthePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
        Block block = getBlock(x, y, z);
        if ((block != null) && (!(block instanceof BlockAir))) {
          if (!(block instanceof net.minecraft.block.BlockLiquid)) {
            return false;
          }
          inLiquid = true;
        }
      }
    }
    return inLiquid;
  }
}
