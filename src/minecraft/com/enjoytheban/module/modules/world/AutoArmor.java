package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor
  extends Module
{
  private Numbers<Double> delay = new Numbers("Delay", "delay", Double.valueOf(50.0D), Double.valueOf(0.0D), Double.valueOf(1000.0D), Double.valueOf(10.0D));
  
  private TimerUtil timer = new TimerUtil();
  

  private int[] boots = { 313, 309, 317, 305, 301 };
  private int[] chestplate = { 311, 307, 315, 303, 299 };
  private int[] helmet = { 310, 306, 314, 302, 298 };
  private int[] leggings = { 312, 308, 316, 304, 300 };
  

  private int slot = 5;
  private double enchantmentValue = -1.0D;
  private double protectionValue;
  private int item = -1;
  
  public AutoArmor() {
    super("AutoArmor", new String[] { "armorswap", "autoarmour" }, ModuleType.World);
    addValues(new Value[] { delay });
    setColor(new Color(27, 104, 204).getRGB());
  }
  
  @EventHandler
  private void onPre(EventPreUpdate e) {
    if (e.getType() == 0) {
      if ((mc.thePlayer.capabilities.isCreativeMode) || (
        (mc.thePlayer.openContainer != null) && (mc.thePlayer.openContainer.windowId != 0))) {
        return;
      }
      

      if (timer.hasReached(((Double)delay.getValue()).doubleValue() + new Random().nextInt(4))) {
        enchantmentValue = -1.0D;
        item = -1;
        for (int i = 9; i < 45; i++) {
          if ((mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null) && 
            (canEquip(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) != -1) && 
            (canEquip(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == slot)) {
            change(slot, i);
          }
        }
        if (item != -1) {
          if (mc.thePlayer.inventoryContainer.getSlot(item).getStack() != null)
            mc.playerController.windowClick(0, slot, 0, 1, mc.thePlayer);
          mc.playerController.windowClick(0, item, 0, 1, mc.thePlayer);
        }
        
        if (slot == 8) {
          slot = 5;
        } else {
          slot += 1;
        }
        timer.reset();
      }
    }
  }
  
  private int canEquip(ItemStack stack)
  {
    for (int id : boots) {
      stack.getItem(); if (Item.getIdFromItem(stack.getItem()) == id)
        return 8;
    }
    for (int id : leggings) {
      stack.getItem(); if (Item.getIdFromItem(stack.getItem()) == id)
        return 7;
    }
    for (int id : chestplate) {
      stack.getItem(); if (Item.getIdFromItem(stack.getItem()) == id)
        return 6;
    }
    for (int id : helmet) {
      stack.getItem(); if (Item.getIdFromItem(stack.getItem()) == id)
        return 5;
    }
    return -1;
  }
  
  private void change(int numy, int i)
  {
    if (enchantmentValue == -1.0D) {
      if (mc.thePlayer.inventoryContainer.getSlot(numy).getStack() != null) {
        protectionValue = getProtValue(mc.thePlayer.inventoryContainer.getSlot(numy).getStack());
      } else
        protectionValue = enchantmentValue;
    } else {
      protectionValue = enchantmentValue;
    }
    
    if (protectionValue <= getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
      if (protectionValue == getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
        int currentD = mc.thePlayer.inventoryContainer.getSlot(numy).getStack() != null ? 
          mc.thePlayer.inventoryContainer.getSlot(numy).getStack().getItemDamage() : 
          999;
        int newD = mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null ? 
          mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItemDamage() : 
          500;
        if ((newD <= currentD) && 
          (newD != currentD))
        {
          item = i;
          enchantmentValue = getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
        }
      }
      else {
        item = i;
        enchantmentValue = getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
      }
    }
  }
  
  private double getProtValue(ItemStack stack)
  {
    if (stack != null) {
      return getItemdamageReduceAmount + 
        (100 - getItemdamageReduceAmount * 4) * 
        EnchantmentHelper.getEnchantmentLevel(field_180310_ceffectId, stack) * 4 * 
        0.0075D;
    }
    return 0.0D;
  }
}
