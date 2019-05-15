package com.enjoytheban.ui.font;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class Utils
{
  public Utils() {}
  
  public static boolean fuck = true;
  private static Minecraft mc = Minecraft.getMinecraft();
  
  public static boolean isContainerEmpty(Container container) {
    int i = 0; for (int slotAmount = inventorySlots.size() == 90 ? 54 : 27; i < slotAmount; i++) {
      if (container.getSlot(i).getHasStack()) {
        return false;
      }
    }
    return true;
  }
  
  public static Minecraft getMinecraft() {
    return mc;
  }
  
  public static boolean canBlock() {
    if (mc == null) {
      mc = Minecraft.getMinecraft();
    }
    if (mcthePlayer.getHeldItem() == null)
      return false;
    if ((mcthePlayer.isBlocking()) || ((mcthePlayer.isUsingItem()) && ((mcthePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword))))
      return true;
    if (((mcthePlayer.getHeldItem().getItem() instanceof ItemSword)) && (getMinecraftgameSettings.keyBindUseItem.isPressed())) {
      return true;
    }
    return false;
  }
  
  public static String getMD5(String input) {
    StringBuilder res = new StringBuilder();
    try {
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(input.getBytes());
      byte[] md5 = algorithm.digest();
      
      for (byte aMd5 : md5) {
        String tmp = Integer.toHexString(0xFF & aMd5);
        if (tmp.length() == 1) {
          res.append("0").append(tmp);
        } else {
          res.append(tmp);
        }
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
    return res.toString();
  }
  
  public static void breakAnticheats() {
    mcthePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mcthePlayer.posX + mcthePlayer.motionX, mcthePlayer.posY - 110.0D, mcthePlayer.posZ + mcthePlayer.motionZ, true));
  }
  
  public static int add(int number, int add, int max) {
    return number + add > max ? max : number + add;
  }
  
  public static int remove(int number, int remove, int min) {
    return number - remove < min ? min : number - remove;
  }
  
  public static int check(int number) {
    return number > 255 ? 255 : number <= 0 ? 1 : number;
  }
  
  public static double getDist() {
    double distance = 0.0D;
    for (double i = mcthePlayer.posY; i > 0.0D; i -= 0.1D) {
      if (i < 0.0D) {
        break;
      }
      Block block = mctheWorld.getBlockState(new net.minecraft.util.BlockPos(mcthePlayer.posX, i, mcthePlayer.posZ)).getBlock();
      if ((block.getMaterial() != net.minecraft.block.material.Material.air) && (block.isCollidable()) && ((block.isFullBlock()) || ((block instanceof BlockSlab)) || ((block instanceof net.minecraft.block.BlockBarrier)) || ((block instanceof net.minecraft.block.BlockStairs)) || 
        ((block instanceof net.minecraft.block.BlockGlass)) || ((block instanceof net.minecraft.block.BlockStainedGlass))))
      {
        if ((block instanceof BlockSlab)) {
          i -= 0.5D;
        }
        distance = i;
        break;
      }
    }
    return mcthePlayer.posY - distance;
  }
}
