package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.misc.EventCollideWithBlock;
import com.enjoytheban.api.events.world.EventMove;
import com.enjoytheban.api.events.world.EventPostUpdate;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.math.RotationUtil;
import java.awt.Color;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;


public class Phase
  extends Module
{
  private Mode<Enum> mode = new Mode("Mode", "mode", PhaseMode.values(), PhaseMode.NewNCP);
  
  public Phase() {
    super("Phase", new String[] { "noclip" }, ModuleType.World);
    setColor(new Color(255, 166, 25).getRGB());
    addValues(new Value[] { mode });
  }
  

  @EventHandler
  private void onBlockCollision(EventCollideWithBlock e)
  {
    if ((e.getBoundingBox() != null) && (getBoundingBoxmaxY > mc.thePlayer.boundingBox.minY) && 
      (mc.thePlayer.isSneaking()) && (mode.getValue() != PhaseMode.OldNCP)) {
      e.setBoundingBox(null);
    }
    if ((e.getBoundingBox() != null) && (getBoundingBoxmaxY > mc.thePlayer.boundingBox.minY) && 
      (mode.getValue() == PhaseMode.OldNCP)) {
      e.setBoundingBox(null);
    }
  }
  
  @EventHandler
  private void onMove(EventMove e) {
    if ((BlockHelper.insideBlock()) && (mc.thePlayer.isSneaking()) && (mode.getValue() == PhaseMode.SkipClip)) {
      mc.thePlayer.boundingBox.offsetAndUpdate(
        mc.thePlayer.movementInput.moveForward * 3.6D * 
        Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)) + 
        mc.thePlayer.movementInput.moveStrafe * 3.6D * 
        Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)), 
        0.0D, 
        mc.thePlayer.movementInput.moveForward * 3.6D * 
        Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)) - 
        mc.thePlayer.movementInput.moveStrafe * 3.6D * 
        Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)));
    }
  }
  
  @EventHandler
  private void onUpdate(EventPostUpdate e)
  {
    if (BlockHelper.insideBlock()) {
      if ((mode.getValue() == PhaseMode.NewNCP) && (mc.thePlayer.isSneaking())) {
        mc.thePlayer.boundingBox.offsetAndUpdate(0.0524D * Math.cos(Math.toRadians(RotationUtil.yaw() + 90.0F)), 
          0.0D, 0.0524D * Math.sin(Math.toRadians(RotationUtil.yaw() + 90.0F)));
      }
      if ((mode.getValue() == PhaseMode.OldNCP) && (mc.thePlayer.isCollidedVertically)) {
        double x = -MathHelper.sin(mc.thePlayer.getDirection()) * 0.2D;
        double z = MathHelper.cos(mc.thePlayer.getDirection()) * 0.2D;
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, 
          mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, 
          Double.MIN_VALUE, mc.thePlayer.posZ + z, true));
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
      }
      
      if ((mc.thePlayer.onGround) && (mode.getValue() == PhaseMode.NewNCP))
        mc.thePlayer.jump();
    }
  }
  
  static enum PhaseMode {
    NewNCP,  OldNCP,  SkipClip;
  }
}
