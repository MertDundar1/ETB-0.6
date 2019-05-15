package com.enjoytheban.module.modules.combat;

import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.Helper;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;


public class AntiBot
  extends Module
{
  public AntiBot()
  {
    super("AntiBot", new String[] { "nobot", "botkiller" }, ModuleType.Combat);
    setColor(new Color(217, 149, 251).getRGB());
  }
  
  public boolean isServerBot(Entity entity)
  {
    if (isEnabled()) {
      if (Helper.onServer("hypixel"))
        return (!entity.getDisplayName().getFormattedText().startsWith("§")) || (entity.isInvisible()) || (entity.getDisplayName().getFormattedText().toLowerCase().contains("npc"));
      if (Helper.onServer("mineplex")) {
        for (Object object : mc.theWorld.playerEntities) {
          EntityPlayer entityPlayer = (EntityPlayer)object;
          if ((entityPlayer != null) && (entityPlayer != mc.thePlayer) && (
            (entityPlayer.getName().startsWith("Body #")) || (entityPlayer.getMaxHealth() == 20.0F))) {
            return true;
          }
        }
      }
    }
    

    return false;
  }
}
