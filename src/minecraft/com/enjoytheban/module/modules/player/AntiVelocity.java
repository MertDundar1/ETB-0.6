package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPacketRecieve;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;








public class AntiVelocity
  extends Module
{
  private Numbers<Double> percentage = new Numbers("Percentage", "percentage", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), Double.valueOf(5.0D));
  
  public AntiVelocity() {
    super("Velocity", new String[] { "antivelocity", "antiknockback", "antikb" }, ModuleType.Player);
    addValues(new Value[] { percentage });
    setColor(new Color(191, 191, 191).getRGB());
  }
  


  @EventHandler
  private void onPacket(EventPacketRecieve e)
  {
    if (((e.getPacket() instanceof S12PacketEntityVelocity)) || ((e.getPacket() instanceof S27PacketExplosion)))
    {
      if (((Double)percentage.getValue()).equals(Double.valueOf(0.0D))) {
        e.setCancelled(true);
      }
      else {
        S12PacketEntityVelocity packet = (S12PacketEntityVelocity)e.getPacket();
        field_149415_b = ((int)(((Double)percentage.getValue()).doubleValue() / 100.0D));
        field_149416_c = ((int)(((Double)percentage.getValue()).doubleValue() / 100.0D));
        field_149414_d = ((int)(((Double)percentage.getValue()).doubleValue() / 100.0D));
      }
    }
  }
}
