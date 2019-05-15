package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;

public class Banwave extends Module
{
  private TimerUtil timer = new TimerUtil();
  
  public ArrayList<net.minecraft.entity.Entity> banned;
  private String banMessage = "twitter.com/CustomKKK";
  
  private Option<Boolean> tempBan = new Option("Temp Ban", "temp", Boolean.valueOf(false));
  private Numbers<Double> banDelay = new Numbers("Delay", "delay", Double.valueOf(10.0D), Double.valueOf(1.0D), Double.valueOf(20.0D), Double.valueOf(1.0D));
  
  public Banwave() {
    super("BanWave", new String[] { "dick", "banner" }, ModuleType.Player);
    setColor(new Color(255, 0, 0).getRGB());
    banned = new ArrayList();
    addValues(new Value[] { tempBan, banDelay });
  }
  
  public void onEnable()
  {
    banned.clear();
    super.onEnable();
  }
  
  @EventHandler
  public void onUpdate(EventPreUpdate event) {
    for (Object o : mc.theWorld.getLoadedEntityList()) {
      if ((o instanceof EntityOtherPlayerMP)) {
        EntityOtherPlayerMP e = (EntityOtherPlayerMP)o;
        if ((timer.hasReached(((Double)banDelay.getValue()).doubleValue() * 100.0D)) && (!com.enjoytheban.management.FriendManager.isFriend(e.getName())) && 
          (e.getName() != mc.thePlayer.getName()) && (!banned.contains(e)))
        {

          if (((Boolean)tempBan.getValue()).booleanValue()) {
            mc.thePlayer.sendChatMessage("/tempban " + e.getName() + " 7d" + " " + banMessage);
            System.out.println("/tempban " + e.getName() + " 7d" + " " + banMessage);
          } else {
            mc.thePlayer.sendChatMessage("/ban " + e.getName() + " " + banMessage);
            System.out.println("/ban " + e.getName() + " " + banMessage);
          }
          banned.add(e);
          timer.reset();
        }
      }
    }
  }
}
