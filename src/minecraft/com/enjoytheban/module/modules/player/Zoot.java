package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;





public class Zoot
  extends Module
{
  public Zoot()
  {
    super("Zoot", new String[] { "Firion", "antipotion", "antifire" }, ModuleType.Player);
    setColor(new Color(208, 203, 229).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e)
  {
    for (Potion potion : Potion.potionTypes)
    {
      PotionEffect effect;
      if ((e.getType() == 0) && (potion != null) && ((((effect = mc.thePlayer.getActivePotionEffect(potion)) != null) && (potion.isBadEffect())) || (
        (mc.thePlayer.isBurning()) && (!mc.thePlayer.isInWater()) && (mc.thePlayer.onGround))))
      {
        for (int i = 0; mc.thePlayer.isBurning() ? i < 20 : i < effect.getDuration() / 20; i++) {
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
        }
      }
    }
  }
}
