package com.enjoytheban.api.events.misc;

import com.enjoytheban.api.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;







public class EventCollideWithBlock
  extends Event
{
  private Block block;
  private BlockPos blockPos;
  public AxisAlignedBB boundingBox;
  
  public EventCollideWithBlock(Block block, BlockPos pos, AxisAlignedBB boundingBox)
  {
    this.block = block;
    blockPos = pos;
    this.boundingBox = boundingBox;
  }
  
  public Block getBlock()
  {
    return block;
  }
  
  public BlockPos getPos() {
    return blockPos;
  }
  
  public AxisAlignedBB getBoundingBox() { return boundingBox; }
  
  public void setBlock(Block block)
  {
    this.block = block;
  }
  
  public void setBlockPos(BlockPos blockPos) {
    this.blockPos = blockPos;
  }
  
  public void setBoundingBox(AxisAlignedBB boundingBox) {
    this.boundingBox = boundingBox;
  }
}
