package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.Helper;
import java.awt.Color;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;


public class StaffAlerts
  extends Module
{
  public StaffAlerts()
  {
    super("StaffAlerts", new String[] { "staff", "stafffinder" }, ModuleType.World);
    setColor(new Color(198, 253, 191).getRGB());
  }
  
  private static List<String> staff = new ArrayList();
  
  private static Scanner scanner;
  public boolean isStaff = false;
  
  public void onEnable()
  {
    checkStaff();
    isStaff = false;
    super.onEnable();
  }
  
  @EventHandler
  public void onUpdate(EventPreUpdate event) {
    if (mc.theWorld.playerEntities != null) { Iterator localIterator2;
      for (Iterator localIterator1 = mc.theWorld.playerEntities.iterator(); localIterator1.hasNext(); 
          
          localIterator2.hasNext())
      {
        Object object = localIterator1.next();
        EntityPlayer entityPlayer = (EntityPlayer)object;
        localIterator2 = staff.iterator(); continue;String staffxd = (String)localIterator2.next();
        if ((entityPlayer != null) && (entityPlayer.getName().equalsIgnoreCase(staffxd)) && (!isStaff)) {
          Helper.sendMessage(entityPlayer.getName() + " is staff!");
          isStaff = true;
          staff.clear();
        }
      }
    }
  }
  
  public void checkStaff()
  {
    try {
      URLConnection openConnection = new URL("http://box.enjoytheban.com/staffnames.txt").openConnection();
      openConnection.addRequestProperty("User-Agent", 
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
      
      scanner = new Scanner(new InputStreamReader(openConnection.getInputStream()));
      
      while (scanner.hasNextLine()) {
        String meme = scanner.nextLine();
        if ((!meme.contains(":")) && (!meme.contains("(")) && (meme.length() > 1)) {
          staff.add(meme);
        }
      }
      
      scanner.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
