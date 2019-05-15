package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventTick;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;


public class ChestStealer
  extends Module
{
  private Numbers<Double> delay = new Numbers("Delay", "delay", Double.valueOf(50.0D), Double.valueOf(0.0D), Double.valueOf(1000.0D), Double.valueOf(10.0D));
  
  private TimerUtil timer = new TimerUtil();
  
  public ChestStealer() {
    super("ChestStealer", new String[] { "cheststeal", "chests", "stealer" }, ModuleType.World);
    addValues(new Value[] { delay });
    setColor(new Color(218, 97, 127).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventTick event) {
    if ((mc.thePlayer.openContainer != null) && ((mc.thePlayer.openContainer instanceof ContainerChest))) {
      ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;
      
      for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
        if ((container.getLowerChestInventory().getStackInSlot(i) != null) && 
          (timer.hasReached(((Double)delay.getValue()).doubleValue())))
        {
          mc.playerController.windowClick(windowId, i, 0, 1, mc.thePlayer);
          timer.reset();
        }
      }
      
      if (isEmpty()) {
        mc.thePlayer.closeScreen();
      }
    }
  }
  

  private boolean isEmpty()
  {
    if ((mc.thePlayer.openContainer != null) && ((mc.thePlayer.openContainer instanceof ContainerChest))) {
      ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;
      
      for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
        ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
        
        if ((itemStack != null) && (itemStack.getItem() != null)) {
          return false;
        }
      }
    }
    return true;
  }
}
