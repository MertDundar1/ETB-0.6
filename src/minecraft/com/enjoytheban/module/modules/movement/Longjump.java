package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventMove;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.math.MathUtil;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;

public class Longjump
  extends Module
{
  private Mode<JumpMode> mode = new Mode("Mode", "mode", JumpMode.values(), JumpMode.NCP);
  
  private int stage;
  private double moveSpeed;
  private double lastDist;
  
  public Longjump()
  {
    super("LongJump", new String[] { "lj", "jumpman", "jump" }, ModuleType.Movement);
    addValues(new Value[] { mode });
    setColor(new Color(76, 67, 216).getRGB());
  }
  
  public void onDisable()
  {
    mc.timer.timerSpeed = 1.0F;
    if (mode.getValue() == JumpMode.Area51) {
      mc.thePlayer.motionX = 0.0D;
      mc.thePlayer.motionZ = 0.0D;
    }
    if (mc.thePlayer != null) {
      moveSpeed = getBaseMoveSpeed();
    }
    lastDist = 0.0D;
    stage = 0;
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e)
  {
    setSuffix(mode.getValue());
    if (mode.getValue() == JumpMode.OldGuardian) {
      if ((mc.thePlayer.moving()) && (mc.thePlayer.onGround))
      {
        mc.thePlayer.motionY = 0.44D;
        
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
          mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, mc.thePlayer.onGround));
        mc.thePlayer.setSpeed(7.0D);
      }
      else {
        mc.thePlayer.setSpeed(Math.sqrt(
          mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
      }
    }
    else if (mode.getValue() == JumpMode.Area51) {
      if (mc.thePlayer.moving()) {
        mc.timer.timerSpeed = 0.33F;
        if (mc.thePlayer.onGround) {
          mc.thePlayer.setSpeed(5.0D);
          mc.thePlayer.motionY = 0.45500001311302185D;
        } else {
          mc.thePlayer.setSpeed(7.0D);
        }
      } else {
        mc.timer.timerSpeed = 0.33F;
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
      }
    } else if ((mode.getValue() == JumpMode.Janitor) && (e.getType() == 0) && (mc.thePlayer.moving()) && 
      (mc.thePlayer.onGround)) {
      e.setY(e.getY() + (mc.thePlayer.ticksExisted % 2 == 0 ? MathUtil.getHighestOffset(0.1D) : 0.0D));
    }
    else if (e.getType() == 0) {
      double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }
  }
  

  @EventHandler
  private void onMove(EventMove e)
  {
    if (mode.getValue() == JumpMode.NCP) {
      if ((mc.thePlayer.moveStrafing <= 0.0F) && (mc.thePlayer.moveForward <= 0.0F)) {
        stage = 1;
      }
      
      if ((stage == 1) && ((mc.thePlayer.moveForward != 0.0F) || (mc.thePlayer.moveStrafing != 0.0F))) {
        stage = 2;
        moveSpeed = (3.0D * getBaseMoveSpeed() - 0.01D);
      } else if (stage == 2) {
        stage = 3;
        mc.thePlayer.motionY = 0.424D;
        EventMove.y = 0.424D;
        moveSpeed *= 2.149802D;
      } else if (stage == 3) {
        stage = 4;
        double difference = 0.66D * (lastDist - getBaseMoveSpeed());
        moveSpeed = (lastDist - difference);
      }
      else {
        if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D)).size() > 0) || 
          (mc.thePlayer.isCollidedVertically)) {
          stage = 1;
        }
        moveSpeed = (lastDist - lastDist / 159.0D);
      }
      moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
      MovementInput movementInput = mc.thePlayer.movementInput;
      float forward = moveForward;
      float strafe = moveStrafe;
      float yaw = mc.thePlayer.rotationYaw;
      if ((forward == 0.0F) && (strafe == 0.0F)) {
        EventMove.x = 0.0D;
        EventMove.z = 0.0D;
      } else if (forward != 0.0F) {
        if (strafe >= 1.0F) {
          yaw += (forward > 0.0F ? -45 : 45);
          strafe = 0.0F;
        } else if (strafe <= -1.0F) {
          yaw += (forward > 0.0F ? 45 : -45);
          strafe = 0.0F;
        }
        if (forward > 0.0F) {
          forward = 1.0F;
        } else if (forward < 0.0F) {
          forward = -1.0F;
        }
      }
      double mx = Math.cos(Math.toRadians(yaw + 90.0F));
      double mz = Math.sin(Math.toRadians(yaw + 90.0F));
      EventMove.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
      EventMove.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
      if ((forward == 0.0F) && (strafe == 0.0F)) {
        EventMove.x = 0.0D;
        EventMove.z = 0.0D;
      } else if (forward != 0.0F) {
        if (strafe >= 1.0F) {
          yaw += (forward > 0.0F ? -45 : 45);
          strafe = 0.0F;
        } else if (strafe <= -1.0F) {
          yaw += (forward > 0.0F ? 45 : -45);
          strafe = 0.0F;
        }
        if (forward > 0.0F) {
          forward = 1.0F;
        } else if (forward < 0.0F) {
          forward = -1.0F;
        }
      }
    } else if ((mode.getValue() == JumpMode.Janitor) && (mc.thePlayer.moving())) {
      moveSpeed = (MathUtil.getBaseMovementSpeed() * (mc.thePlayer.ticksExisted % 2 != 0 ? 5 : 6));
      double x = -(Math.sin(mc.thePlayer.getDirection()) * moveSpeed);
      double z = Math.cos(mc.thePlayer.getDirection()) * moveSpeed;
      e.setX(x);
      e.setZ(z);
      if (mc.thePlayer.onGround)
        e.setY(mc.thePlayer.motionY = 0.3D);
    } else if ((mode.getValue() == JumpMode.Guardian) && (mc.thePlayer.moving())) {
      if ((mc.thePlayer.moveForward != 0.0F) || (mc.thePlayer.moveStrafing != 0.0F)) {
        if (mc.thePlayer.onGround) {
          for (int i = 0; i < 20; i++)
          {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
              mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, mc.thePlayer.onGround));
          }
          EventMove.y = mc.thePlayer.motionY = 0.4D;
          mc.thePlayer.setSpeed(8.0D);
        }
      } else {
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
      }
    }
  }
  
  double getBaseMoveSpeed()
  {
    double baseSpeed = 0.2873D;
    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
      int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
      baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
    }
    return baseSpeed;
  }
  
  static enum JumpMode {
    NCP,  OldGuardian,  Guardian,  Janitor,  Area51;
  }
}
