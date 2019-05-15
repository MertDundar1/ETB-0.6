package com.enjoytheban.api.events.rendering;

import com.enjoytheban.api.Event;
import shadersmod.client.Shaders;







public class EventRender3D
  extends Event
{
  private float ticks;
  private boolean isUsingShaders;
  
  public EventRender3D()
  {
    isUsingShaders = (Shaders.getShaderPackName() != null);
  }
  
  public EventRender3D(float ticks) {
    this.ticks = ticks;
    isUsingShaders = (Shaders.getShaderPackName() != null);
  }
  
  public float getPartialTicks() {
    return ticks;
  }
  
  public boolean isUsingShaders() {
    return isUsingShaders;
  }
}
