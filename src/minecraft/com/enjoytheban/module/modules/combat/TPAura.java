package com.enjoytheban.module.modules.combat;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventTick;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;



public class TPAura
  extends Module
{
  private int ticks;
  private List<EntityLivingBase> loaded = new ArrayList();
  private EntityLivingBase target;
  private int tpdelay;
  public boolean criticals;
  private TimerUtil timer = new TimerUtil();
  
  public TPAura() {
    super("TPAura", new String[] { "tpaura" }, ModuleType.Combat);
  }
  
  @EventHandler
  public void onUpdate(EventTick event) {
    setColor(new Color(255, 50, 70).getRGB());
    ticks += 1;
    tpdelay += 1;
    if (ticks >= 20 - speed()) {
      ticks = 0;
      for (Object object : mc.theWorld.loadedEntityList) {
        if ((object instanceof EntityLivingBase)) {
          EntityLivingBase entity = (EntityLivingBase)object;
          if (!(entity instanceof EntityPlayerSP))
          {

            if (mc.thePlayer.getDistanceToEntity(entity) <= 10.0F)
            {

              if (entity.isEntityAlive()) {
                if (tpdelay >= 4) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    posX, posY, posZ, false));
                }
                if (mc.thePlayer.getDistanceToEntity(entity) < 10.0F)
                  attack(entity);
              } }
          }
        }
      }
    }
  }
  
  public void attack(EntityLivingBase entity) {
    attack(entity, false);
  }
  
  public void attack(EntityLivingBase entity, boolean crit) {
    mc.thePlayer.swingItem();
    float sharpLevel = EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
    boolean vanillaCrit = (mc.thePlayer.fallDistance > 0.0F) && (!mc.thePlayer.onGround) && (!mc.thePlayer.isOnLadder()) && 
      (!mc.thePlayer.isInWater()) && (!mc.thePlayer.isPotionActive(Potion.blindness)) && 
      (mc.thePlayer.ridingEntity == null);
    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
    if ((crit) || (vanillaCrit)) {
      mc.thePlayer.onCriticalHit(entity);
    }
    if (sharpLevel > 0.0F) {
      mc.thePlayer.onEnchantmentCritical(entity);
    }
  }
  
  private int speed() {
    return 8;
  }
}
