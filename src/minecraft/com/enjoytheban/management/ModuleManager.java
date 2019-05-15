package com.enjoytheban.management;

import com.enjoytheban.api.EventBus;
import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.misc.EventKey;
import com.enjoytheban.api.events.rendering.EventRender2D;
import com.enjoytheban.api.events.rendering.EventRender3D;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.module.modules.combat.AntiBot;
import com.enjoytheban.module.modules.combat.AutoHeal;
import com.enjoytheban.module.modules.combat.AutoSword;
import com.enjoytheban.module.modules.combat.BowAimBot;
import com.enjoytheban.module.modules.combat.Criticals;
import com.enjoytheban.module.modules.combat.FastBow;
import com.enjoytheban.module.modules.combat.Killaura;
import com.enjoytheban.module.modules.combat.Regen;
import com.enjoytheban.module.modules.combat.TPAura;
import com.enjoytheban.module.modules.movement.Boost;
import com.enjoytheban.module.modules.movement.Flight;
import com.enjoytheban.module.modules.movement.Jesus;
import com.enjoytheban.module.modules.movement.Longjump;
import com.enjoytheban.module.modules.movement.NoSlow;
import com.enjoytheban.module.modules.movement.Scaffold;
import com.enjoytheban.module.modules.movement.Sneak;
import com.enjoytheban.module.modules.movement.Speed;
import com.enjoytheban.module.modules.movement.Sprint;
import com.enjoytheban.module.modules.movement.Step;
import com.enjoytheban.module.modules.movement.Teleport;
import com.enjoytheban.module.modules.player.AntiVelocity;
import com.enjoytheban.module.modules.player.AutoAccept;
import com.enjoytheban.module.modules.player.Bobbing;
import com.enjoytheban.module.modules.player.Dab;
import com.enjoytheban.module.modules.player.FastUse;
import com.enjoytheban.module.modules.player.Freecam;
import com.enjoytheban.module.modules.player.InvCleaner;
import com.enjoytheban.module.modules.player.Invplus;
import com.enjoytheban.module.modules.player.MCF;
import com.enjoytheban.module.modules.player.NoFall;
import com.enjoytheban.module.modules.player.NoStrike;
import com.enjoytheban.module.modules.player.SkinFlash;
import com.enjoytheban.module.modules.player.Zoot;
import com.enjoytheban.module.modules.render.Chams;
import com.enjoytheban.module.modules.render.ChestESP;
import com.enjoytheban.module.modules.render.ESP;
import com.enjoytheban.module.modules.render.FullBright;
import com.enjoytheban.module.modules.render.HUD;
import com.enjoytheban.module.modules.render.Nametags;
import com.enjoytheban.module.modules.render.NoRender;
import com.enjoytheban.module.modules.render.Tracers;
import com.enjoytheban.module.modules.render.Xray;
import com.enjoytheban.module.modules.world.AntiVoid;
import com.enjoytheban.module.modules.world.AutoArmor;
import com.enjoytheban.module.modules.world.Banwave;
import com.enjoytheban.module.modules.world.Blink;
import com.enjoytheban.module.modules.world.ChestStealer;
import com.enjoytheban.module.modules.world.Deathclip;
import com.enjoytheban.module.modules.world.FastPlace;
import com.enjoytheban.module.modules.world.NoRotate;
import com.enjoytheban.module.modules.world.Phase;
import com.enjoytheban.module.modules.world.PinCracker;
import com.enjoytheban.module.modules.world.PingSpoof;
import com.enjoytheban.module.modules.world.SafeWalk;
import com.enjoytheban.module.modules.world.StaffAlerts;
import com.enjoytheban.utils.render.gl.GLUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;








public class ModuleManager
  implements Manager
{
  public static List<Module> modules = new ArrayList();
  
  private boolean enabledNeededMod = true;
  
  public boolean nicetry = true;
  
  public ModuleManager() {}
  
  public void init() {
    modules.add(new HUD());
    modules.add(new Sprint());
    modules.add(new Killaura());
    modules.add(new AntiVelocity());
    modules.add(new Criticals());
    modules.add(new Speed());
    modules.add(new Longjump());
    modules.add(new Flight());
    modules.add(new NoFall());
    modules.add(new Invplus());
    modules.add(new NoSlow());
    modules.add(new FastBow());
    modules.add(new AntiBot());
    modules.add(new Freecam());
    modules.add(new MCF());
    modules.add(new Nametags());
    modules.add(new Tracers());
    modules.add(new ESP());
    modules.add(new Regen());
    modules.add(new FastPlace());
    modules.add(new NoRender());
    modules.add(new FullBright());
    modules.add(new ChestStealer());
    modules.add(new AutoArmor());
    modules.add(new AntiVoid());
    modules.add(new AutoHeal());
    modules.add(new NoRotate());
    modules.add(new Scaffold());
    modules.add(new Sneak());
    modules.add(new SafeWalk());
    modules.add(new Zoot());
    modules.add(new Jesus());
    modules.add(new Phase());
    modules.add(new Chams());
    modules.add(new Deathclip());
    modules.add(new NoStrike());
    modules.add(new SkinFlash());
    modules.add(new AutoAccept());
    modules.add(new Blink());
    modules.add(new Banwave());
    modules.add(new FastUse());
    modules.add(new PingSpoof());
    modules.add(new BowAimBot());
    modules.add(new Xray());
    modules.add(new ChestESP());
    modules.add(new InvCleaner());
    modules.add(new Step());
    modules.add(new Dab());
    modules.add(new Teleport());
    modules.add(new AutoSword());
    modules.add(new Boost());
    modules.add(new Bobbing());
    modules.add(new PinCracker());
    modules.add(new TPAura());
    modules.add(new StaffAlerts());
    
    readSettings();
    

    for (Module m : modules) {
      m.makeCommand();
    }
    

    EventBus.getInstance().register(new Object[] { this });
  }
  

  public static List<Module> getModules()
  {
    return modules;
  }
  
  public Module getModuleByClass(Class<? extends Module> cls)
  {
    for (Module m : modules) {
      if (m.getClass() == cls)
      {

        return m; }
    }
    return null;
  }
  
  public static Module getModuleByName(String name)
  {
    for (Module m : modules) {
      if (m.getName().equalsIgnoreCase(name))
      {

        return m; }
    }
    return null;
  }
  
  public Module getAlias(String name) { int length;
    int i;
    for (Iterator localIterator = modules.iterator(); localIterator.hasNext(); 
        



        i < length)
    {
      Module f = (Module)localIterator.next();
      if (f.getName().equalsIgnoreCase(name)) {
        return f;
      }
      String[] alias;
      length = (alias = f.getAlias()).length;i = 0; continue;
      String s = alias[i];
      if (s.equalsIgnoreCase(name)) {
        return f;
      }
      i++;
    }
    




    return null;
  }
  
  public List<Module> getModulesInType(ModuleType t)
  {
    List<Module> output = new ArrayList();
    for (Module m : modules) {
      if (m.getType() == t)
      {

        output.add(m); }
    }
    return output;
  }
  

  @EventHandler
  private void onKeyPress(EventKey e)
  {
    for (Module m : modules)
    {
      if (m.getKey() == e.getKey())
      {


        m.setEnabled(!m.isEnabled()); }
    }
  }
  
  @EventHandler
  private void onGLHack(EventRender3D e) {
    GlStateManager.getFloat(2982, (FloatBuffer)GLUtils.MODELVIEW.clear());
    GlStateManager.getFloat(2983, (FloatBuffer)GLUtils.PROJECTION.clear());
    GlStateManager.glGetInteger(2978, (IntBuffer)GLUtils.VIEWPORT.clear());
  }
  
  @EventHandler
  private void on2DRender(EventRender2D e)
  {
    if (enabledNeededMod) {
      enabledNeededMod = false;
      
      for (Module m : modules)
      {
        if (enabledOnStartup)
        {


          m.setEnabled(true);
        }
      }
    }
  }
  
  private void readSettings()
  {
    List<String> binds = FileManager.read("Binds.txt");
    String name; for (String v : binds) {
      name = v.split(":")[0];String bind = v.split(":")[1];
      Module m = getModuleByName(name);
      
      if (m != null)
      {


        m.setKey(Keyboard.getKeyIndex(bind.toUpperCase()));
      }
    }
    
    List<String> enabled = FileManager.read("Enabled.txt");
    Module m; for (String v : enabled)
    {
      m = getModuleByName(v);
      
      if (m != null)
      {


        enabledOnStartup = true;
      }
    }
    
    Object vals = FileManager.read("Values.txt");
    for (String v : (List)vals)
    {
      String name = v.split(":")[0];String values = v.split(":")[1];
      
      Module m = getModuleByName(name);
      
      if (m != null)
      {



        for (Value value : m.getValues()) {
          if (value.getName().equalsIgnoreCase(values))
          {
            if ((value instanceof Option)) {
              value.setValue(Boolean.valueOf(Boolean.parseBoolean(v.split(":")[2])));

            }
            else if ((value instanceof Numbers)) {
              value.setValue(Double.valueOf(Double.parseDouble(v.split(":")[2])));
            }
            else
            {
              ((Mode)value).setMode(v.split(":")[2]);
            }
          }
        }
      }
    }
  }
}
