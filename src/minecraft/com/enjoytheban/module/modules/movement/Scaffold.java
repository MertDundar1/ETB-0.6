package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPostUpdate;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.math.RotationUtil;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class Scaffold extends Module
{
  private Option<Boolean> tower = new Option("Tower", "tower", Boolean.valueOf(true));
  private Option<Boolean> silent = new Option("Silent", "Silent", Boolean.valueOf(true));
  private Option<Boolean> aac = new Option("AAC", "AAC", Boolean.valueOf(false));
  


  private java.util.List<Block> invalid = Arrays.asList(new Block[] { Blocks.air, Blocks.water, Blocks.fire, Blocks.flowing_water, Blocks.lava, 
    Blocks.flowing_lava, Blocks.chest, Blocks.enchanting_table, Blocks.tnt });
  
  private BlockCache blockCache;
  
  private int currentItem;
  

  public Scaffold()
  {
    super("Scaffold", new String[] { "magiccarpet", "blockplacer", "airwalk" }, ModuleType.Movement);
    addValues(new com.enjoytheban.api.value.Value[] { tower, silent, aac });
    currentItem = 0;
    setColor(new Color(244, 119, 194).getRGB());
  }
  
  public void onEnable()
  {
    currentItem = mc.thePlayer.inventory.currentItem;
  }
  
  public void onDisable()
  {
    mc.thePlayer.inventory.currentItem = currentItem;
  }
  






  @EventHandler
  private void onUpdate(EventPreUpdate event)
  {
    if (((Boolean)aac.getValue()).booleanValue()) {
      mc.thePlayer.setSprinting(false);
    }
    if (grabBlockSlot() == -1) {
      return;
    }
    blockCache = grab();
    if (blockCache == null) {
      return;
    }
    float[] rotations = RotationUtil.grabBlockRotations(blockCache.getPosition());
    event.setYaw(rotations[0]);
    event.setPitch(
      RotationUtil.getVecRotation(grabPosition(blockCache.getPosition(), blockCache.getFacing()))[1] - 
      3.0F);
  }
  
  @EventHandler
  private void onPostUpdate(EventPostUpdate event) {
    if (blockCache == null) {
      return;
    }
    if ((mc.gameSettings.keyBindJump.getIsKeyPressed()) && (((Boolean)tower.getValue()).booleanValue())) {
      mc.thePlayer.setSprinting(false);
      mc.thePlayer.motionY = 0.0D;
      mc.thePlayer.motionX = 0.0D;
      mc.thePlayer.motionZ = 0.0D;
      mc.thePlayer.jump();
    }
    
    int currentSlot = mc.thePlayer.inventory.currentItem;
    int slot = grabBlockSlot();
    mc.thePlayer.inventory.currentItem = slot;
    if (placeBlock(blockCache.position, blockCache.facing)) {
      if (((Boolean)silent.getValue()).booleanValue()) {
        mc.thePlayer.inventory.currentItem = currentSlot;
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
      }
      
      blockCache = null;
    }
  }
  
  private boolean placeBlock(BlockPos pos, EnumFacing facing)
  {
    Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), 
      mc.thePlayer.posZ);
    

    if (mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, new Vec3(blockCache.position).addVector(0.5D, 0.5D, 0.5D).add(new Vec3(blockCache.facing.getDirectionVec()).scale(0.5D)))) {
      mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
      return true;
    }
    return false;
  }
  
  private Vec3 grabPosition(BlockPos position, EnumFacing facing)
  {
    Vec3 offset = new Vec3(facing.getDirectionVec().getX() / 2.0D, facing.getDirectionVec().getY() / 2.0D, 
      facing.getDirectionVec().getZ() / 2.0D);
    
    Vec3 point = new Vec3(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D);
    
    return point.add(offset);
  }
  
  private BlockCache grab()
  {
    EnumFacing[] invert = { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, 
      EnumFacing.WEST };
    BlockPos position = new BlockPos(mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN);
    if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir))
      return null;
    BlockPos offset;
    for (EnumFacing facing : EnumFacing.values()) {
      offset = position.offset(facing);
      IBlockState blockState = mc.theWorld.getBlockState(offset);
      if (!(mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir)) {
        return new BlockCache(offset, invert[facing.ordinal()], null);
      }
    }
    BlockPos[] offsets = { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), 
      new BlockPos(0, 0, 1) };
    if (mc.thePlayer.onGround) {
      for (BlockPos offset : offsets) {
        BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
        IBlockState blockState2 = mc.theWorld.getBlockState(offsetPos);
        if ((mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir)) {
          for (EnumFacing facing2 : EnumFacing.values()) {
            BlockPos offset2 = offsetPos.offset(facing2);
            IBlockState blockState3 = mc.theWorld.getBlockState(offset2);
            if (!(mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir)) {
              return new BlockCache(offset2, invert[facing2.ordinal()], null);
            }
          }
        }
      }
    }
    return null;
  }
  
  private int grabBlockSlot()
  {
    for (int i = 0; i < 9; i++) {
      ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
      if ((itemStack != null) && ((itemStack.getItem() instanceof ItemBlock))) {
        return i;
      }
    }
    return -1;
  }
  
  private class BlockCache
  {
    private BlockPos position;
    private EnumFacing facing;
    
    private BlockCache(BlockPos position, EnumFacing facing) {
      this.position = position;
      this.facing = facing;
    }
    
    private BlockPos getPosition() {
      return position;
    }
    
    private EnumFacing getFacing() {
      return facing;
    }
  }
}
