package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

public class Teleport extends Module
{
  public Teleport()
  {
    super("Teleport", new String[] { "teleport" }, ModuleType.Movement);
    setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate event) {
    MovingObjectPosition ray = rayTrace(500.0D);
    if (ray == null) {
      return;
    }
    if (Mouse.isButtonDown(1)) {
      double x_new = ray.func_178782_a().getX() + 0.5D;
      double y_new = ray.func_178782_a().getY() + 1;
      double z_new = ray.func_178782_a().getZ() + 0.5D;
      double distance = mc.thePlayer.getDistance(x_new, y_new, z_new); for (double d = 0.0D; d < distance; d += 2.0D) {
        setPos(mc.thePlayer.posX + (x_new - mc.thePlayer.func_174811_aO().getFrontOffsetX() - mc.thePlayer.posX) * d / distance, mc.thePlayer.posY + (y_new - mc.thePlayer.posY) * d / distance, mc.thePlayer.posZ + (z_new - mc.thePlayer.func_174811_aO().getFrontOffsetZ() - mc.thePlayer.posZ) * d / distance);
      }
      setPos(x_new, y_new, z_new);
      mc.renderGlobal.loadRenderers();
    }
  }
  
  public MovingObjectPosition rayTrace(double blockReachDistance) {
    Vec3 vec3 = mc.thePlayer.func_174824_e(1.0F);
    Vec3 vec4 = mc.thePlayer.getLookVec();
    Vec3 vec5 = vec3.addVector(xCoord * blockReachDistance, yCoord * blockReachDistance, 
      zCoord * blockReachDistance);
    return mc.theWorld.rayTraceBlocks(vec3, vec5, !mc.thePlayer.isInWater(), false, false);
  }
  
  public void setPos(double x, double y, double z) {
    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
    mc.thePlayer.setPosition(x, y, z);
  }
}
