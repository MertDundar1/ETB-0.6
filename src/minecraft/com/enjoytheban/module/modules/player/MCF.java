package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.management.FriendManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;





public class MCF
  extends Module
{
  private boolean down;
  
  public MCF()
  {
    super("MCF", new String[] { "middleclickfriends", "middleclick" }, ModuleType.Player);
    setColor(new Color(241, 175, 67).getRGB());
  }
  

  @EventHandler
  private void onClick(EventPreUpdate e)
  {
    if ((Mouse.isButtonDown(2)) && (!down))
    {
      if (mc.objectMouseOver.entityHit != null)
      {
        EntityPlayer player = (EntityPlayer)mc.objectMouseOver.entityHit;
        
        String playername = player.getName();
        

        if (!FriendManager.isFriend(playername)) {
          mc.thePlayer.sendChatMessage(".f add " + playername);
        }
        else {
          mc.thePlayer.sendChatMessage(".f del " + playername);
        }
      }
      down = true;
    }
    
    if (!Mouse.isButtonDown(2)) {
      down = false;
    }
  }
}
