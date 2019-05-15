package com.enjoytheban.module.modules.movement;

import com.enjoytheban.Client;
import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventMove;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import com.enjoytheban.utils.math.MathUtil;
import java.awt.Color;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;

public class Flight extends Module
{
  public Mode mode = new Mode("Mode", "mode", FlightMode.values(), FlightMode.Guardian);
  
  private TimerUtil timer;
  
  private double movementSpeed;
  
  private int counter;
  private int hypixelCounter;
  private int hypixelCounter2;
  
  public Flight()
  {
    super("Flight", new String[] { "fly", "angel" }, ModuleType.Movement);
    
    timer = new TimerUtil();
    setColor(new Color(158, 114, 243).getRGB());
    addValues(new Value[] { mode });
  }
  


  public void onEnable()
  {
    if ((mode.getValue() == FlightMode.Hypixel) || (mode.getValue() == FlightMode.HypixelBoost)) {
      hypixelCounter = 0;
      hypixelCounter2 = 1000;
      mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2D, mc.thePlayer.posZ);
    }
  }
  

  public void onDisable()
  {
    if (mode.getValue() == FlightMode.Area51) {
      mc.thePlayer.motionX = 0.0D;
      mc.thePlayer.motionZ = 0.0D;
    }
    hypixelCounter = 0;
    hypixelCounter2 = 100;
    mc.timer.timerSpeed = 1.0F;
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e)
  {
    setSuffix(mode.getValue());
    if (mode.getValue() == FlightMode.Guardian)
    {
      mc.timer.timerSpeed = 1.7F;
      


      if ((!mc.thePlayer.onGround) && (mc.thePlayer.ticksExisted % 2 == 0)) {
        mc.thePlayer.motionY = 0.04D;
      }
      

      if (mc.gameSettings.keyBindJump.getIsKeyPressed()) {
        mc.thePlayer.motionY += 1.0D;
      }
      

      if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
        mc.thePlayer.motionY -= 1.0D;
      }
    } else if (mode.getValue() == FlightMode.Vanilla) {
      if (mc.thePlayer.movementInput.jump) {
        mc.thePlayer.motionY = 1.0D;
      } else if (mc.thePlayer.movementInput.sneak) {
        mc.thePlayer.motionY = -1.0D;
      } else {
        mc.thePlayer.motionY = 0.0D;
      }
      if (mc.thePlayer.moving()) {
        mc.thePlayer.setSpeed(1.0D);
      } else {
        mc.thePlayer.setSpeed(0.0D);
      }
    } else if (mode.getValue() == FlightMode.Area51) {
      if (mc.thePlayer.movementInput.jump) {
        mc.thePlayer.motionY = 1.0D;
      } else if (mc.thePlayer.movementInput.sneak) {
        mc.thePlayer.motionY = -1.0D;
      } else {
        mc.thePlayer.motionY = 0.0D;
      }
      
    }
    else if ((mode.getValue() == FlightMode.Hypixel) && (e.getType() == 0)) {
      mc.thePlayer.motionY = 0.0D;
      counter += 1;
      if (counter == 1) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 8.0E-6D, mc.thePlayer.posZ);
      } else if (counter == 2) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8.0E-6D, mc.thePlayer.posZ);
        counter = 0;
      }
      if (timer.hasReached(50.0D)) {
        if (mc.thePlayer.movementInput.jump)
          mc.thePlayer.motionY += 0.5D;
        if (mc.thePlayer.movementInput.sneak)
          mc.thePlayer.motionY -= 0.5D;
        timer.reset();
      }
    } else if ((mode.getValue() == FlightMode.HypixelBoost) && (e.getType() == 0)) {
      mc.thePlayer.motionY = 0.0D;
      counter += 1;
      if (counter == 1) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 8.0E-6D, mc.thePlayer.posZ);
      } else if (counter == 2) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8.0E-6D, mc.thePlayer.posZ);
        counter = 0;
      }
      if (timer.hasReached(50.0D)) {
        if (mc.thePlayer.movementInput.jump)
          mc.thePlayer.motionY += 0.5D;
        if (mc.thePlayer.movementInput.sneak)
          mc.thePlayer.motionY -= 0.5D;
        timer.reset();
      }
    } else if ((mode.getValue() == FlightMode.OldGuardianLongJumpFly) && 
      (mc.thePlayer.moving()) && 
      (!Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled())) {
      if (mc.thePlayer.isAirBorne) {
        if (mc.thePlayer.ticksExisted % 12 == 0)
        {
          if ((mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
            mc.thePlayer.setSpeed(6.5D);
            mc.thePlayer.sendQueue
              .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
              mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, mc.thePlayer.onGround));
            mc.thePlayer.motionY = 0.455D;
            break label1154; } }
        mc.thePlayer.setSpeed((float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + 
          mc.thePlayer.motionZ * mc.thePlayer.motionZ));
      }
      else {
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D; }
      label1154:
      if (mc.thePlayer.movementInput.jump) {
        mc.thePlayer.motionY = 0.85D;
      } else if (mc.thePlayer.movementInput.sneak) {
        mc.thePlayer.motionY = -0.85D;
      }
    }
  }
  
  @EventHandler
  private void onMove(EventMove e)
  {
    if ((mc.thePlayer.moving()) && (mode.getValue() == FlightMode.Hypixel)) {
      mc.thePlayer.cameraYaw = 0.07272727F;
      mc.timer.timerSpeed = 1.15F;
      movementSpeed = MathUtil.getBaseMovementSpeed();
      double x = -(Math.sin(mc.thePlayer.getDirection()) * movementSpeed);
      double z = Math.cos(mc.thePlayer.getDirection()) * movementSpeed;
      e.setX(x);
      e.setZ(z);
    } else if ((mc.thePlayer.moving()) && (mode.getValue() == FlightMode.HypixelBoost)) {
      mc.thePlayer.cameraYaw = 0.07272727F;
      if (hypixelCounter < 5) {
        hypixelCounter += 1;
        mc.timer.timerSpeed = 2.0F;
      } else {
        mc.timer.timerSpeed = 1.0F;
      }
      if (hypixelCounter2 < 112) {
        hypixelCounter += 1;
        mc.timer.timerSpeed = 2.0F;
      }
      if (hypixelCounter2 < 160) {
        hypixelCounter2 += 1;
      }
      if (hypixelCounter2 >= 160) {
        hypixelCounter2 = 100;
      }
      movementSpeed = MathUtil.getBaseMovementSpeed();
      double x = -(Math.sin(mc.thePlayer.getDirection()) * movementSpeed);
      double z = Math.cos(mc.thePlayer.getDirection()) * movementSpeed;
      e.setX(x);
      e.setZ(z);
    } else if (mode.getValue() == FlightMode.Area51) {
      if (mc.thePlayer.moving()) {
        mc.thePlayer.setSpeed(3.3299999237060547D);
        mc.timer.timerSpeed = 0.32F;
      } else {
        mc.timer.timerSpeed = 0.88F;
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
        mc.thePlayer.motionY = 0.0D;
      }
    } else if (mode.getValue() == FlightMode.AGC) {
      if (mc.thePlayer.moving()) {
        mc.thePlayer.setSpeed(mc.thePlayer.ticksExisted % 3 == 0 ? 5 : 0);
      }
      if ((mc.thePlayer.ticksExisted % 10 == 0) && (mc.gameSettings.keyBindJump.pressed)) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 5.0D, mc.thePlayer.posZ);
      } else {
        mc.thePlayer.motionY = 0.05D;
      }
      if ((mc.thePlayer.ticksExisted >= 10) && (!mc.gameSettings.keyBindJump.pressed)) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5D, mc.thePlayer.posZ);
        mc.thePlayer.ticksExisted = 0;
      }
    } else if (mode.getValue() == FlightMode.GuardianLongJumpFly) {
      if (getMinecraftgameSettings.keyBindJump.pressed) {
        EventMove.y = getMinecraftthePlayer.motionY = 1.5D;
      }
      if (getMinecraftgameSettings.keyBindSneak.pressed) {
        EventMove.y = getMinecraftthePlayer.motionY = -1.5D;
      }
      if ((mc.thePlayer.moving()) && (!getMinecraftgameSettings.keyBindJump.pressed) && 
        (!getMinecraftgameSettings.keyBindSneak.pressed))
      {
        if ((getMinecraftthePlayer.motionY > -0.41D) && 
          (!getMinecraftthePlayer.onGround)) {
          return;
        }
        for (int i = 0; i < 10; i++) {
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
            mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, true));
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
          C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        EventMove.y = getMinecraftthePlayer.motionY = 0.455D;
        mc.thePlayer.setSpeed(7.0D);
      }
    } else {
      mc.timer.timerSpeed = 1.0F;
    }
  }
  
  double getBaseMoveSpeed()
  {
    double baseSpeed = 0.275D;
    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
      int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
      baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
    }
    return baseSpeed;
  }
  
  public static enum FlightMode {
    Vanilla,  Guardian,  Hypixel,  Area51,  HypixelBoost,  OldGuardianLongJumpFly,  GuardianLongJumpFly,  AGC;
  }
}
