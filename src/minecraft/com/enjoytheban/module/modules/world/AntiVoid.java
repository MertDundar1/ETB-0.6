package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

public class AntiVoid extends Module
{
  public AntiVoid()
  {
    super("AntiVoid", new String[] { "novoid", "antifall" }, ModuleType.World);
    setColor(new Color(223, 233, 233).getRGB());
  }
  
  @com.enjoytheban.api.EventHandler
  private void onUpdate(EventPreUpdate e)
  {
    boolean blockUnderneath = false;
    
    for (int i = 0; i < mc.thePlayer.posY + 2.0D; i++) {
      BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
      
      if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockAir))
      {

        blockUnderneath = true;
      }
    }
    if (blockUnderneath) {
      return;
    }
    if (mc.thePlayer.fallDistance < 2.0F) {
      return;
    }
    if ((!mc.thePlayer.onGround) && (!mc.thePlayer.isCollidedVertically)) {
      mc.thePlayer.motionY += 0.07D;
    }
  }
}
