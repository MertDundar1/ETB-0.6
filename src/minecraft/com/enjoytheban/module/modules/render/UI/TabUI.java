package com.enjoytheban.module.modules.render.UI;

import com.enjoytheban.Client;
import com.enjoytheban.api.EventBus;
import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.misc.EventKey;
import com.enjoytheban.api.events.rendering.EventRender2D;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.management.Manager;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.module.modules.render.HUD;
import com.enjoytheban.ui.font.CFontRenderer;
import com.enjoytheban.ui.font.FontLoaders;
import com.enjoytheban.utils.Helper;
import com.enjoytheban.utils.math.MathUtil;
import com.enjoytheban.utils.render.RenderUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.GameSettings;




public class TabUI
  implements Manager
{
  private Section section = Section.TYPES;
  private ModuleType selectedType = ModuleType.values()[0];
  
  private Module selectedModule = null;
  private Value selectedValue = null;
  
  private int currentType = 0; private int currentModule = 0; private int currentValue = 0; private int height = 12;
  private int maxType;
  private int maxModule;
  private int maxValue;
  
  public TabUI() {}
  
  public void init() {
    for (ModuleType mt : ) {
      if (maxType <= mcfontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4)
      {

        maxType = (mcfontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4);
      }
    }
    Client.instance.getModuleManager(); for (Module m : ModuleManager.getModules()) {
      if (maxModule <= mcfontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4)
      {

        maxModule = (mcfontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4);
      }
    }
    Client.instance.getModuleManager(); for (Module m : ModuleManager.getModules()) {
      if (!m.getValues().isEmpty())
      {


        for (??? = m.getValues().iterator(); ((Iterator)???).hasNext();) { Value val = (Value)((Iterator)???).next();
          if (maxValue <= mcfontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4)
          {

            maxValue = (mcfontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4); }
        }
      }
    }
    maxModule += 12;
    maxValue += 24;
    int highestWidth = 0;
    maxType = (maxType < maxModule ? maxModule : maxType);
    maxModule += maxType;
    maxValue += maxModule;
    

    EventBus.getInstance().register(new Object[] { this });
  }
  
  private void resetValuesLength()
  {
    maxValue = 0;
    for (Value val : selectedModule.getValues()) {
      int off = (val instanceof Option) ? 6 : 
        mcfontRendererObj.getStringWidth(String.format(" §7%s", new Object[] { val.getValue().toString() })) + 
        6;
      if (maxValue <= mcfontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off)
      {

        maxValue = (mcfontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off); }
    }
    maxValue += maxModule;
  }
  
  @EventHandler
  private void renderTabGUI(EventRender2D e)
  {
    CFontRenderer font = FontLoaders.kiona18;
    
    if (HUD.useFont) {
      if ((!mcgameSettings.showDebugInfo) && 
        (Client.instance.getModuleManager().getModuleByClass(HUD.class).isEnabled()))
      {
        int categoryY;
        
        int moduleY = categoryY = HUD.shouldMove ? 26 : height;
        int valueY = categoryY;
        

        RenderUtil.drawBorderedRect(2.0F, categoryY, maxType - 25, 
          categoryY + 12 * ModuleType.values().length, 2.0F, new Color(0, 0, 0, 130).getRGB(), 
          new Color(0, 0, 0, 180).getRGB());
        

        ModuleType[] moduleArray = ModuleType.values();
        int mA = moduleArray.length;
        int mA2 = 0;
        while (mA2 < mA) {
          ModuleType mt = moduleArray[mA2];
          if (selectedType == mt)
          {
            Gui.drawRect(2.5D, categoryY + 0.5D, maxType - 25.5D, 
              categoryY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
              new Color(102, 172, 255).getRGB());
            moduleY = categoryY;
          }
          
          if (selectedType == mt) {
            font.drawStringWithShadow(mt.name(), 7.0D, categoryY + 3, -1);
          } else {
            font.drawStringWithShadow(mt.name(), 5.0D, categoryY + 3, new Color(180, 180, 180).getRGB());
          }
          categoryY += 12;
          mA2++;
        }
        
        if ((section == Section.MODULES) || (section == Section.VALUES))
        {
          RenderUtil.drawBorderedRect(maxType - 20, moduleY, maxModule - 38, 
            moduleY + 12 * 
            Client.instance.getModuleManager().getModulesInType(selectedType).size(), 
            2.0F, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
          
          for (Module m : Client.instance.getModuleManager().getModulesInType(selectedType)) {
            if (selectedModule == m) {
              Gui.drawRect(maxType - 19.5D, moduleY + 0.5D, maxModule - 38.5D, 
                moduleY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
                new Color(102, 172, 255).getRGB());
              valueY = moduleY;
            }
            
            if (selectedModule == m) {
              font.drawStringWithShadow(m.getName(), maxType - 15, moduleY + 3, 
                m.isEnabled() ? -1 : 11184810);
            } else {
              font.drawStringWithShadow(m.getName(), maxType - 17, moduleY + 3, 
                m.isEnabled() ? -1 : 11184810);
            }
            
            if (!m.getValues().isEmpty()) {
              Gui.drawRect(maxModule - 38, moduleY + 0.5D, maxModule - 39, 
                moduleY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
                new Color(153, 200, 255).getRGB());
              
              if ((section == Section.VALUES) && (selectedModule == m)) {
                RenderUtil.drawBorderedRect(maxModule - 32, valueY, maxValue - 25, 
                  valueY + 12 * selectedModule.getValues().size(), 2.0F, 
                  new Color(10, 10, 10, 180).getRGB(), new Color(10, 10, 10, 180).getRGB());
                
                for (Value val : selectedModule.getValues()) {
                  Gui.drawRect(maxModule - 31.5D, valueY + 0.5D, maxValue - 25.5D, 
                    valueY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
                    selectedValue == val ? new Color(102, 172, 255).getRGB() : 0);
                  if ((val instanceof Option)) {
                    font.drawStringWithShadow(val.getDisplayName(), 
                      selectedValue == val ? maxModule - 27 : maxModule - 29, 
                      valueY + 3, 
                      ((Boolean)val.getValue()).booleanValue() ? new Color(153, 200, 255).getRGB() : 
                      11184810);
                  } else {
                    String toRender = String.format("%s: §7%s", new Object[] { val.getDisplayName(), 
                      val.getValue().toString() });
                    
                    if (selectedValue == val)
                    {
                      font.drawStringWithShadow(toRender, maxModule - 27, valueY + 3, -1);
                    } else {
                      font.drawStringWithShadow(toRender, maxModule - 29, valueY + 3, -1);
                    }
                  }
                  valueY += 12;
                }
              }
            }
            moduleY += 12;
          }
        }
      }
    }
    else if ((!mcgameSettings.showDebugInfo) && 
      (Client.instance.getModuleManager().getModuleByClass(HUD.class).isEnabled()))
    {
      int categoryY;
      
      int moduleY = categoryY = HUD.shouldMove ? 26 : height;
      int valueY = categoryY;
      

      RenderUtil.drawBorderedRect(2.0F, categoryY, maxType - 25, 
        categoryY + 12 * ModuleType.values().length, 2.0F, new Color(0, 0, 0, 130).getRGB(), 
        new Color(0, 0, 0, 180).getRGB());
      

      ModuleType[] moduleArray = ModuleType.values();
      int mA = moduleArray.length;
      int mA2 = 0;
      while (mA2 < mA) {
        ModuleType mt = moduleArray[mA2];
        if (selectedType == mt)
        {
          Gui.drawRect(2.5D, categoryY + 0.5D, maxType - 25.5D, 
            categoryY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
            new Color(102, 172, 255).getRGB());
          moduleY = categoryY;
        }
        
        if (selectedType == mt) {
          mcfontRendererObj.drawStringWithShadow(mt.name(), 7.0F, categoryY + 2, -1);
        } else {
          mcfontRendererObj.drawStringWithShadow(mt.name(), 5.0F, categoryY + 2, 
            new Color(180, 180, 180).getRGB());
        }
        categoryY += 12;
        mA2++;
      }
      
      if ((section == Section.MODULES) || (section == Section.VALUES))
      {
        RenderUtil.drawBorderedRect(maxType - 20, moduleY, maxModule - 38, 
          moduleY + 12 * 
          Client.instance.getModuleManager().getModulesInType(selectedType).size(), 
          2.0F, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
        
        for (Module m : Client.instance.getModuleManager().getModulesInType(selectedType)) {
          if (selectedModule == m) {
            Gui.drawRect(maxType - 19.5D, moduleY + 0.5D, maxModule - 38.5D, 
              moduleY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
              new Color(102, 172, 255).getRGB());
            valueY = moduleY;
          }
          
          if (selectedModule == m) {
            mcfontRendererObj.drawStringWithShadow(m.getName(), maxType - 15, moduleY + 2, 
              m.isEnabled() ? -1 : 11184810);
          } else {
            mcfontRendererObj.drawStringWithShadow(m.getName(), maxType - 17, moduleY + 2, 
              m.isEnabled() ? -1 : 11184810);
          }
          
          if (!m.getValues().isEmpty()) {
            Gui.drawRect(maxModule - 38, moduleY + 0.5D, maxModule - 39, 
              moduleY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
              new Color(153, 200, 255).getRGB());
            
            if ((section == Section.VALUES) && (selectedModule == m)) {
              RenderUtil.drawBorderedRect(maxModule - 32, valueY, maxValue - 25, 
                valueY + 12 * selectedModule.getValues().size(), 2.0F, 
                new Color(10, 10, 10, 180).getRGB(), new Color(10, 10, 10, 180).getRGB());
              
              for (Value val : selectedModule.getValues()) {
                Gui.drawRect(maxModule - 31.5D, valueY + 0.5D, maxValue - 25.5D, 
                  valueY + mcfontRendererObj.FONT_HEIGHT + 2.5D, 
                  selectedValue == val ? new Color(102, 172, 255).getRGB() : 0);
                if ((val instanceof Option)) {
                  mcfontRendererObj.drawStringWithShadow(val.getDisplayName(), 
                    selectedValue == val ? maxModule - 27 : maxModule - 29, 
                    valueY + 2, 
                    ((Boolean)val.getValue()).booleanValue() ? new Color(153, 200, 255).getRGB() : 
                    11184810);
                } else {
                  String toRender = String.format("%s: §7%s", new Object[] { val.getDisplayName(), 
                    val.getValue().toString() });
                  
                  if (selectedValue == val)
                  {
                    mcfontRendererObj.drawStringWithShadow(toRender, 
                      maxModule - 27, valueY + 2, -1);
                  } else {
                    mcfontRendererObj.drawStringWithShadow(toRender, 
                      maxModule - 29, valueY + 2, -1);
                  }
                }
                valueY += 12;
              }
            }
          }
          moduleY += 12;
        }
      }
    }
  }
  

  @EventHandler
  private void onKey(EventKey e)
  {
    if (!mcgameSettings.showDebugInfo) {
      switch (e.getKey()) {
      case 208: 
        switch (section) {
        case MODULES: 
          currentType += 1;
          if (currentType > ModuleType.values().length - 1) {
            currentType = 0;
          }
          selectedType = ModuleType.values()[currentType];
          break;
        
        case TYPES: 
          currentModule += 1;
          
          if (currentModule > Client.instance.getModuleManager().getModulesInType(selectedType).size() - 1) {
            currentModule = 0;
          }
          selectedModule = 
            ((Module)Client.instance.getModuleManager().getModulesInType(selectedType).get(currentModule));
          break;
        
        case VALUES: 
          currentValue += 1;
          if (currentValue > selectedModule.getValues().size() - 1) {
            currentValue = 0;
          }
          selectedValue = ((Value)selectedModule.getValues().get(currentValue));
        }
        
        break;
      
      case 200: 
        switch (section) {
        case MODULES: 
          currentType -= 1;
          if (currentType < 0) {
            currentType = (ModuleType.values().length - 1);
          }
          selectedType = ModuleType.values()[currentType];
          break;
        
        case TYPES: 
          currentModule -= 1;
          if (currentModule < 0) {
            currentModule = 
              (Client.instance.getModuleManager().getModulesInType(selectedType).size() - 1);
          }
          selectedModule = 
            ((Module)Client.instance.getModuleManager().getModulesInType(selectedType).get(currentModule));
          break;
        
        case VALUES: 
          currentValue -= 1;
          if (currentValue < 0) {
            currentValue = (selectedModule.getValues().size() - 1);
          }
          selectedValue = ((Value)selectedModule.getValues().get(currentValue));
        }
        
        break;
      
      case 205: 
        switch (section) {
        case MODULES: 
          currentModule = 0;
          selectedModule = 
            ((Module)Client.instance.getModuleManager().getModulesInType(selectedType).get(currentModule));
          section = Section.MODULES;
          break;
        
        case TYPES: 
          if (!selectedModule.getValues().isEmpty()) {
            resetValuesLength();
            currentValue = 0;
            selectedValue = ((Value)selectedModule.getValues().get(currentValue));
            section = Section.VALUES;
          }
          break;
        
        case VALUES: 
          if (!Helper.onServer("enjoytheban")) {
            if ((selectedValue instanceof Option)) {
              selectedValue.setValue(Boolean.valueOf(!((Boolean)selectedValue.getValue()).booleanValue()));
            } else if ((selectedValue instanceof Numbers)) {
              Numbers value = (Numbers)selectedValue;
              double inc = ((Double)value.getValue()).doubleValue();
              inc += ((Double)value.getIncrement()).doubleValue();
              inc = MathUtil.toDecimalLength(inc, 1);
              if (inc > ((Double)value.getMaximum()).doubleValue()) {
                inc = ((Double)((Numbers)selectedValue).getMinimum()).doubleValue();
              }
              selectedValue.setValue(Double.valueOf(inc));
            } else if ((selectedValue instanceof Mode)) {
              Mode theme = (Mode)selectedValue;
              Enum current = (Enum)theme.getValue();
              int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
              selectedValue.setValue(theme.getModes()[next]);
            }
            resetValuesLength();
          }
          break;
        }
        break;
      
      case 28: 
        switch (section)
        {
        case MODULES: 
          break;
        case TYPES: 
          selectedModule.setEnabled(!selectedModule.isEnabled());
          break;
        
        case VALUES: 
          section = Section.MODULES;
        }
        
        break;
      
      case 203: 
        switch (section)
        {
        case MODULES: 
          break;
        case TYPES: 
          section = Section.TYPES;
          currentModule = 0;
          break;
        
        case VALUES: 
          if (!Helper.onServer("enjoytheban")) { Enum current;
            if ((selectedValue instanceof Option)) {
              selectedValue.setValue(Boolean.valueOf(!((Boolean)selectedValue.getValue()).booleanValue()));

            }
            else if ((selectedValue instanceof Numbers)) {
              Numbers value = (Numbers)selectedValue;
              double inc = ((Double)value.getValue()).doubleValue();
              inc -= ((Double)value.getIncrement()).doubleValue();
              inc = MathUtil.toDecimalLength(inc, 1);
              if (inc < ((Double)value.getMinimum()).doubleValue()) {
                inc = ((Double)((Numbers)selectedValue).getMaximum()).doubleValue();
              }
              selectedValue.setValue(Double.valueOf(inc));

            }
            else if ((selectedValue instanceof Mode)) {
              Mode theme = (Mode)selectedValue;
              current = (Enum)theme.getValue();
              int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
              selectedValue.setValue(theme.getModes()[next]);
            }
            maxValue = 0;
            for (Value val : selectedModule.getValues()) {
              int off = (val instanceof Option) ? 6 : 
                getMinecraftfontRendererObj
                .getStringWidth(String.format(" §7%s", new Object[] { val.getValue().toString() })) + 6;
              
              if (maxValue <= getMinecraftfontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off)
              {

                maxValue = 
                  (getMinecraftfontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off); }
            }
            maxValue += maxModule;
          }
          break;
        }
        break;
      }
    }
  }
  
  public static enum Section
  {
    TYPES,  MODULES,  VALUES;
  }
}
