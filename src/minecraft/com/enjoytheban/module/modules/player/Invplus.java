package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.misc.EventInventory;
import com.enjoytheban.api.events.rendering.EventRender2D;
import com.enjoytheban.api.events.world.EventPacketSend;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.math.RotationUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import org.lwjgl.input.Keyboard;







public class Invplus
  extends Module
{
  public Option<Boolean> sw = new Option("ScreenWalk", "screenwalk", Boolean.valueOf(true));
  private Option<Boolean> xc = new Option("MoreInventory", "MoreInventory", Boolean.valueOf(false));
  
  public Invplus()
  {
    super("Inventory+", new String[] { "inventorywalk", "invwalk", "inventorymove", "inv+" }, ModuleType.Player);
    setColor(new Color(174, 174, 227).getRGB());
    addValues(new Value[] { sw, xc });
  }
  
  @EventHandler
  public void onEvent(EventPacketSend event) {
    if (((event.getPacket() instanceof C0DPacketCloseWindow)) && 
      (((Boolean)xc.getValue()).booleanValue())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onInv(EventInventory event) {
    if (((Boolean)xc.getValue()).booleanValue()) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  private void onRender(EventRender2D e)
  {
    if ((mc.currentScreen != null) && (!(mc.currentScreen instanceof GuiChat)) && (((Boolean)sw.getValue()).booleanValue()))
    {
      if (Keyboard.isKeyDown(200)) {
        RotationUtil.pitch(RotationUtil.pitch() - 2.0F);
      }
      
      if (Keyboard.isKeyDown(208)) {
        RotationUtil.pitch(RotationUtil.pitch() + 2.0F);
      }
      
      if (Keyboard.isKeyDown(203)) {
        RotationUtil.yaw(RotationUtil.yaw() - 2.0F);
      }
      
      if (Keyboard.isKeyDown(205)) {
        RotationUtil.yaw(RotationUtil.yaw() + 2.0F);
      }
    }
  }
}
