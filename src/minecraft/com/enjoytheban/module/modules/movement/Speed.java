package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventMove;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.Helper;
import com.enjoytheban.utils.TimerUtil;
import com.enjoytheban.utils.math.MathUtil;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;


public class Speed
  extends Module
{
  private Mode<Enum> mode = new Mode("Mode", "mode", SpeedMode.values(), SpeedMode.HypixelHop);
  private int stage;
  private double movementSpeed;
  private double distance;
  
  public Speed()
  {
    super("Speed", new String[] { "zoom" }, ModuleType.Movement);
    setColor(new Color(99, 248, 91).getRGB());
    addValues(new Value[] { mode });
  }
  
  private TimerUtil timer = new TimerUtil();
  
  public void onDisable()
  {
    mc.timer.timerSpeed = 1.0F;
    if (mode.getValue() == SpeedMode.Area51) {
      mc.thePlayer.motionX = 0.0D;
      mc.thePlayer.motionZ = 0.0D;
    }
  }
  
  private boolean canZoom() {
    return (mc.thePlayer.moving()) && (mc.thePlayer.onGround);
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e)
  {
    if (Helper.onServer("enjoytheban")) {
      mode.setValue(SpeedMode.Bhop);
    }
    setSuffix(mode.getValue());
    if ((mode.getValue() == SpeedMode.Sloth) && (canZoom())) {
      if (mc.thePlayer.moving()) {
        boolean under = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, 
          mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX, 1.6D, mc.thePlayer.motionZ))
          .isEmpty();
        if ((mc.thePlayer.ticksExisted % 2 != 0) && (under)) {
          y += 0.42D;
        }
        mc.thePlayer.motionY = -10.0D;
        if (mc.thePlayer.onGround)
        {
          mc.thePlayer.setSpeed(mc.thePlayer.getSpeed() * (mc.thePlayer.ticksExisted % 2 == 0 ? 4.0F : 0.28F)); }
      }
    } else {
      if ((mode.getValue() == SpeedMode.Onground) && (canZoom())) {}
      switch (stage) {
      case 1: 
        e.setY(e.getY() + 0.4D);
        e.setOnground(false);
        
        stage += 1;
        break;
      case 2: 
        e.setY(e.getY() + 0.4D);
        e.setOnground(false);
        stage += 1;
        break;
      default: 
        stage = 1;
        



        break; if ((mode.getValue() != SpeedMode.Janitor) || (e.getType() != 0) || (!canZoom()))
          if (mode.getValue() == SpeedMode.AGC) {
            double speed = 0.2D;
            double x = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
            double z = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
            double n = mc.thePlayer.movementInput.moveForward * speed * x;
            double xOff = n + mc.thePlayer.movementInput.moveStrafe * speed * z;
            double n2 = mc.thePlayer.movementInput.moveForward * speed * z;
            double zOff = n2 - mc.thePlayer.movementInput.moveStrafe * 0.5F * x;
            
            mc.thePlayer.setAIMoveSpeed(mc.thePlayer.getAIMoveSpeed());
            if (mc.thePlayer.onGround) {
              if (mc.thePlayer.moving()) {
                mc.thePlayer.motionY = 0.2D;
              }
            }
            else if (mc.thePlayer.motionY <= -0.10000000149011612D) {
              double cock = 10.0D;
              mc.thePlayer.setPosition(mc.thePlayer.posX + xOff * cock, mc.thePlayer.posY, 
                mc.thePlayer.posZ + zOff * cock);
              mc.thePlayer.motionY -= 0.0010000000474974513D;
            }
          }
          else if ((mode.getValue() != SpeedMode.OldGuardian) && (mode.getValue() != SpeedMode.GuardianYport)) {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            distance = Math.sqrt(xDist * xDist + zDist * zDist);
          } else if (mode.getValue() == SpeedMode.GuardianYport) {
            if (mc.thePlayer.moving()) {
              mc.timer.timerSpeed = 1.6F;
            } else
              mc.timer.timerSpeed = 1.0F;
            if ((mc.thePlayer.moving()) && (mc.thePlayer.onGround))
            {
              mc.thePlayer.motionY = 0.12D;
              mc.thePlayer.motionY -= 0.04D;
              mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
                mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, mc.thePlayer.onGround));
              
              mc.thePlayer.setSpeed(0.7D);
            }
            else {
              mc.thePlayer.setSpeed(Math.sqrt(
                mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
            }
          }
          else if ((mc.thePlayer.moving()) && (mc.thePlayer.onGround))
          {
            mc.thePlayer.motionY = 0.4D;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
              mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, mc.thePlayer.onGround));
            
            mc.thePlayer.setSpeed(1.75D);
          }
          else {
            mc.thePlayer.setSpeed(Math.sqrt(
              mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
          }
        break; }
    }
  }
  
  @EventHandler
  private void onMove(EventMove e) {
    if (mode.getValue() == SpeedMode.HypixelHop) {
      if ((canZoom()) && (stage == 1)) {
        movementSpeed = (1.56D * MathUtil.getBaseMovementSpeed() - 0.01D);
        mc.timer.timerSpeed = 1.15F;
      } else if ((canZoom()) && (stage == 2)) {
        e.setY(mc.thePlayer.motionY = 0.3999D);
        movementSpeed *= 1.58D;
        mc.timer.timerSpeed = 1.2F;
      } else if (stage == 3) {
        double difference = 0.66D * (distance - MathUtil.getBaseMovementSpeed());
        movementSpeed = (distance - difference);
        mc.timer.timerSpeed = 1.1F;
      } else {
        List collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, 
          mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D));
        if ((collidingList.size() > 0) || ((mc.thePlayer.isCollidedVertically) && (stage > 0))) {
          stage = (mc.thePlayer.moving() ? 1 : 0);
        }
        movementSpeed = (distance - distance / 159.0D);
      }
      mc.thePlayer.setMoveSpeed(e, this.movementSpeed = Math.max(movementSpeed, MathUtil.getBaseMovementSpeed()));
      if (mc.thePlayer.moving()) {
        stage += 1;
      }
    } else if (mode.getValue() == SpeedMode.Area51) {
      if (mc.thePlayer.moving()) {
        if (getMinecraftthePlayer.motionY <= 0.0D) {
          getMinecraftthePlayer.motionY *= 1.5D;
        }
        mc.thePlayer.onGround = true;
        mc.timer.timerSpeed = 0.33F;
        mc.thePlayer.setSpeed(4.0D);
      } else {
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
      }
    } else if ((mode.getValue() == SpeedMode.Onground) && (canZoom())) {
      switch (stage) {
      case 1: 
        mc.timer.timerSpeed = 1.22F;
        movementSpeed = (1.89D * MathUtil.getBaseMovementSpeed() - 0.01D);
        distance += 1.0D;
        if (distance == 1.0D) {
          e.setY(e.getY() + 8.0E-6D);
        } else if (distance == 2.0D) {
          e.setY(e.getY() - 8.0E-6D);
          distance = 0.0D;
        }
        break;
      case 2: 
        movementSpeed = (1.2D * MathUtil.getBaseMovementSpeed() - 0.01D);
        break;
      default: 
        movementSpeed = ((float)MathUtil.getBaseMovementSpeed());
      }
      mc.thePlayer.setMoveSpeed(e, this.movementSpeed = Math.max(movementSpeed, MathUtil.getBaseMovementSpeed()));
      stage += 1;
    } else if ((mode.getValue() == SpeedMode.Janitor) && (canZoom())) {
      mc.thePlayer.setSpeed(mc.thePlayer.ticksExisted % 2 != 0 ? 0 : 2);
    }
    else if (mode.getValue() == SpeedMode.Mineplex) {
      mc.timer.timerSpeed = 1.1F;
      if ((canZoom()) && (stage == 1)) {
        movementSpeed = 0.58D;
      } else if ((canZoom()) && (stage == 2)) {
        e.setY(mc.thePlayer.motionY = 0.3D);
        movementSpeed = 0.64D;
      } else if (stage == 3) {
        double difference = 0.66D * (distance - MathUtil.getBaseMovementSpeed());
        movementSpeed = (distance - difference);
      } else {
        List collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, 
          mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D));
        if ((collidingList.size() > 0) || ((mc.thePlayer.isCollidedVertically) && (stage > 0))) {
          stage = (mc.thePlayer.moving() ? 1 : 0);
        }
        movementSpeed = (distance - distance / 159.0D);
      }
      mc.thePlayer.setMoveSpeed(e, this.movementSpeed = Math.max(movementSpeed, MathUtil.getBaseMovementSpeed()));
      if (mc.thePlayer.moving()) {
        stage += 1;
      }
    } else if (mode.getValue() == SpeedMode.Bhop) {
      mc.timer.timerSpeed = 1.07F;
      if ((canZoom()) && (stage == 1)) {
        movementSpeed = (2.55D * MathUtil.getBaseMovementSpeed() - 0.01D);
      } else if ((canZoom()) && (stage == 2)) {
        e.setY(mc.thePlayer.motionY = 0.3999D);
        movementSpeed *= 2.1D;
      } else if (stage == 3) {
        double difference = 0.66D * (distance - MathUtil.getBaseMovementSpeed());
        movementSpeed = (distance - difference);
      } else {
        List collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, 
          mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D));
        if ((collidingList.size() > 0) || ((mc.thePlayer.isCollidedVertically) && (stage > 0))) {
          stage = (mc.thePlayer.moving() ? 1 : 0);
        }
        movementSpeed = (distance - distance / 159.0D);
      }
      mc.thePlayer.setMoveSpeed(e, this.movementSpeed = Math.max(movementSpeed, MathUtil.getBaseMovementSpeed()));
      if (mc.thePlayer.moving()) {
        stage += 1;
      }
    } else if (mode.getValue() == SpeedMode.Guardian) {
      if (mc.thePlayer.moving()) {
        if (mc.thePlayer.onGround) {
          for (int i = 0; i < 20; i++)
          {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
              mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, mc.thePlayer.onGround));
          }
          mc.thePlayer.setSpeed(1.399999976158142D);
          EventMove.y = mc.thePlayer.motionY = 0.4D;
        } else {
          mc.thePlayer.setSpeed((float)Math.sqrt(
            mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
        }
      } else {
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
      }
    }
  }
  
  static enum SpeedMode {
    Bhop,  HypixelHop,  Onground,  OldGuardian,  Guardian,  GuardianYport,  Mineplex,  AGC,  Janitor,  Sloth,  Area51;
  }
}
