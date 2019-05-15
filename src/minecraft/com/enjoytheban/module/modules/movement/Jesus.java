package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.misc.EventCollideWithBlock;
import com.enjoytheban.api.events.world.EventPacketSend;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;


public class Jesus
  extends Module
{
  public Jesus()
  {
    super("Jesus", new String[] { "waterwalk", "float" }, ModuleType.Movement);
    setColor(new Color(188, 233, 248).getRGB());
  }
  



  private boolean canJeboos()
  {
    return (mc.thePlayer.fallDistance < 3.0F) && (!mc.gameSettings.keyBindJump.isPressed()) && (!BlockHelper.isInLiquid()) && 
      (!mc.thePlayer.isSneaking());
  }
  
  @EventHandler
  public void onPre(EventPreUpdate e)
  {
    if ((BlockHelper.isInLiquid()) && (!mc.thePlayer.isSneaking()) && (!mc.gameSettings.keyBindJump.isPressed())) {
      mc.thePlayer.motionY = 0.05D;
      mc.thePlayer.onGround = true;
    }
  }
  
  @EventHandler
  public void onPacket(EventPacketSend e)
  {
    if (((e.getPacket() instanceof C03PacketPlayer)) && (canJeboos()) && (BlockHelper.isOnLiquid())) {
      C03PacketPlayer packet = (C03PacketPlayer)e.getPacket();
      y = (mc.thePlayer.ticksExisted % 2 == 0 ? y + 0.01D : y - 0.01D);
    }
  }
  
  @EventHandler
  public void onBB(EventCollideWithBlock e)
  {
    if (((e.getBlock() instanceof BlockLiquid)) && (canJeboos())) {
      e.setBoundingBox(new AxisAlignedBB(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), 
        e.getPos().getX() + 1.0D, e.getPos().getY() + 1.0D, e.getPos().getZ() + 1.0D));
    }
  }
}
