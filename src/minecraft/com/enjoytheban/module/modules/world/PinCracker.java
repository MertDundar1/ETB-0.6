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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;


public class PinCracker
  extends Module
{
  private TimerUtil time = new TimerUtil();
  
  int num;
  private Option<Boolean> login = new Option("/login?", "login", Boolean.valueOf(false));
  private Numbers<Double> delay = new Numbers("Delay", "Delay", Double.valueOf(1.0D), Double.valueOf(0.0D), Double.valueOf(20.0D), Double.valueOf(1.0D));
  
  public PinCracker() {
    super("PinCracker", new String[] { "pincracker" }, ModuleType.World);
    addValues(new Value[] { login, delay });
  }
  
  @EventHandler
  public void onUpdate(EventPreUpdate event) {
    setColor(new Color(200, 200, 100).getRGB());
    if (((Boolean)login.getValue()).booleanValue()) {
      if (time.delay((float)(((Double)delay.getValue()).doubleValue() * 100.0D))) {
        mc.thePlayer.sendChatMessage("/login " + numbers());
        time.reset();
      }
    }
    else if (time.delay((float)(((Double)delay.getValue()).doubleValue() * 100.0D))) {
      mc.thePlayer.sendChatMessage("/pin " + numbers());
      time.reset();
    }
  }
  
  private int numbers()
  {
    if (num <= 10000) {
      num += 1;
    }
    return num;
  }
  
  public void onDisable()
  {
    num = 0;
    super.onDisable();
  }
}
