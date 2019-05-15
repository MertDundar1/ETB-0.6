package com.enjoytheban.module.modules.render;

import com.enjoytheban.Client;
import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.rendering.EventRenderCape;
import com.enjoytheban.management.FriendManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;


public class Capes
  extends Module
{
  public Capes()
  {
    super("Capes", new String[] { "kape" }, ModuleType.Render);
    setColor(new Color(159, 190, 192).getRGB());
    setEnabled(true);
    setRemoved(true);
  }
  
  @EventHandler
  public void onRender(EventRenderCape event) {
    if ((mc.theWorld != null) && (FriendManager.isFriend(event.getPlayer().getName()))) {
      event.setLocation(Client.CLIENT_CAPE);
      event.setCancelled(true);
    }
  }
}
