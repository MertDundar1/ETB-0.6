package com.enjoytheban.module.modules.render;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.rendering.EventPostRenderPlayer;
import com.enjoytheban.api.events.rendering.EventPreRenderPlayer;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import org.lwjgl.opengl.GL11;

public class Chams
  extends Module
{
  public Mode<Enum> mode = new Mode("Mode", "mode", ChamsMode.values(), ChamsMode.Textured);
  
  public Chams() {
    super("Chams", new String[] { "seethru", "cham" }, ModuleType.Render);
    addValues(new Value[] { mode });
    setColor(new Color(159, 190, 192).getRGB());
  }
  
  @EventHandler
  private void preRenderPlayer(EventPreRenderPlayer e) {
    GL11.glEnable(32823);
    GL11.glPolygonOffset(1.0F, -1100000.0F);
  }
  
  @EventHandler
  private void postRenderPlayer(EventPostRenderPlayer e)
  {
    GL11.glDisable(32823);
    GL11.glPolygonOffset(1.0F, 1100000.0F);
  }
  
  public static enum ChamsMode {
    Textured,  Normal;
  }
}
