package com.enjoytheban.module.modules.combat;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPostUpdate;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.module.Module;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AutoHeal extends Module
{
  private Numbers<Double> health = new Numbers("Health", "health", Double.valueOf(3.0D), Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(0.5D)); private Numbers<Double> delay = new Numbers("Delay", "delay", Double.valueOf(400.0D), Double.valueOf(0.0D), Double.valueOf(1000.0D), Double.valueOf(10.0D));
  
  private Option<Boolean> jump = new Option("Jump", "jump", Boolean.valueOf(true));
  
  private Mode<Enum> mode = new Mode("Mode", "mode", HealMode.values(), HealMode.Potion);
  
  static boolean currentlyPotting = false;
  

  private TimerUtil timer = new TimerUtil();
  private boolean isUsing;
  
  public AutoHeal() { super("AutoHeal", new String[] { "autopot", "autop", "autosoup" }, com.enjoytheban.module.ModuleType.Combat);
    setColor(new Color(76, 249, 247).getRGB());
    addValues(new com.enjoytheban.api.value.Value[] { health, delay, jump, mode });
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e) {
    if ((timer.hasReached(((Double)delay.getValue()).doubleValue())) && (mc.thePlayer.getHealth() <= ((Double)health.getValue()).doubleValue() * 2.0D)) {
      isUsing = (((this.slot = mode.getValue() == HealMode.Soup ? getSoupSlot() : mode.getValue() == HealMode.Potion ? getPotionSlot() : getPotionSlot()) != -1) && ((!((Boolean)jump.getValue()).booleanValue()) || (mc.thePlayer.onGround)));
      if (isUsing) {
        timer.reset();
        if (mode.getValue() == HealMode.Potion) {
          currentlyPotting = true;
          e.setPitch(((Boolean)jump.getValue()).booleanValue() ? -90 : 90);
          if (timer.hasReached(1.0D)) {
            currentlyPotting = false;
            timer.reset();
          }
        }
      }
    }
  }
  
  @EventHandler
  private void onUpdatePost(EventPostUpdate e) {
    if (isUsing) {
      int current = mc.thePlayer.inventory.currentItem;int next = mc.thePlayer.nextSlot();
      mc.thePlayer.moveToHotbar(slot, next);
      mc.thePlayer.sendQueue
        .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = next));
      mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
      mc.thePlayer.sendQueue
        .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = current));
      isUsing = false;
      if ((mc.thePlayer.onGround) && (((Boolean)jump.getValue()).booleanValue()) && (mode.getValue() == HealMode.Potion)) {
        mc.thePlayer.setSpeed(0.0D);
        mc.thePlayer.motionY = 0.42D;
      }
    }
  }
  
  private int getPotionSlot()
  {
    int slot = -1;
    for (Slot s : mc.thePlayer.inventoryContainer.inventorySlots)
      if (s.getHasStack())
      {

        ItemStack is = s.getStack();
        if ((is.getItem() instanceof ItemPotion))
        {

          ItemPotion ip = (ItemPotion)is.getItem();
          if (ItemPotion.isSplash(is.getMetadata()))
          {

            boolean hasHealing = false;
            for (PotionEffect pe : ip.getEffects(is))
              if (pe.getPotionID() == healid)
              {

                hasHealing = true;
                break;
              }
            if (hasHealing)
            {

              slot = slotNumber;
              break;
            } } } }
    return slot;
  }
  
  private int getSoupSlot() {
    int slot = -1;
    for (Slot s : mc.thePlayer.inventoryContainer.inventorySlots)
      if (s.getHasStack())
      {

        ItemStack is = s.getStack();
        if ((is.getItem() instanceof ItemSoup))
        {

          slot = slotNumber;
          break;
        } }
    return slot;
  }
  
  private int slot;
  private int getPotionCount()
  {
    int count = 0;
    for (Slot s : mc.thePlayer.inventoryContainer.inventorySlots)
      if (s.getHasStack())
      {

        ItemStack is = s.getStack();
        if ((is.getItem() instanceof ItemPotion))
        {

          ItemPotion ip = (ItemPotion)is.getItem();
          if (ItemPotion.isSplash(is.getMetadata()))
          {

            boolean hasHealing = false;
            for (PotionEffect pe : ip.getEffects(is))
              if (pe.getPotionID() == healid)
              {

                hasHealing = true;
                break;
              }
            if (hasHealing)
            {

              count++; }
          } } }
    return count;
  }
  
  private int getSoupCount() {
    int count = 0;
    for (Slot s : mc.thePlayer.inventoryContainer.inventorySlots)
      if (s.getHasStack())
      {

        ItemStack is = s.getStack();
        if ((is.getItem() instanceof ItemSoup))
        {

          count++; }
      }
    return count;
  }
  
  static enum HealMode {
    Potion,  Soup;
  }
}
