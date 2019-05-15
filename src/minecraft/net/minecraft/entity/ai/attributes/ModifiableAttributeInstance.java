package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



public class ModifiableAttributeInstance
  implements IAttributeInstance
{
  private final BaseAttributeMap attributeMap;
  private final IAttribute genericAttribute;
  private final Map mapByOperation = Maps.newHashMap();
  private final Map mapByName = Maps.newHashMap();
  private final Map mapByUUID = Maps.newHashMap();
  private double baseValue;
  private boolean needsUpdate = true;
  private double cachedValue;
  private static final String __OBFID = "CL_00001567";
  
  public ModifiableAttributeInstance(BaseAttributeMap p_i1608_1_, IAttribute p_i1608_2_)
  {
    attributeMap = p_i1608_1_;
    genericAttribute = p_i1608_2_;
    baseValue = p_i1608_2_.getDefaultValue();
    
    for (int var3 = 0; var3 < 3; var3++)
    {
      mapByOperation.put(Integer.valueOf(var3), Sets.newHashSet());
    }
  }
  



  public IAttribute getAttribute()
  {
    return genericAttribute;
  }
  
  public double getBaseValue()
  {
    return baseValue;
  }
  
  public void setBaseValue(double p_111128_1_)
  {
    if (p_111128_1_ != getBaseValue())
    {
      baseValue = p_111128_1_;
      flagForUpdate();
    }
  }
  
  public Collection getModifiersByOperation(int p_111130_1_)
  {
    return (Collection)mapByOperation.get(Integer.valueOf(p_111130_1_));
  }
  
  public Collection func_111122_c()
  {
    HashSet var1 = Sets.newHashSet();
    
    for (int var2 = 0; var2 < 3; var2++)
    {
      var1.addAll(getModifiersByOperation(var2));
    }
    
    return var1;
  }
  



  public AttributeModifier getModifier(UUID p_111127_1_)
  {
    return (AttributeModifier)mapByUUID.get(p_111127_1_);
  }
  
  public boolean func_180374_a(AttributeModifier p_180374_1_)
  {
    return mapByUUID.get(p_180374_1_.getID()) != null;
  }
  
  public void applyModifier(AttributeModifier p_111121_1_)
  {
    if (getModifier(p_111121_1_.getID()) != null)
    {
      throw new IllegalArgumentException("Modifier is already applied on this attribute!");
    }
    

    Object var2 = (Set)mapByName.get(p_111121_1_.getName());
    
    if (var2 == null)
    {
      var2 = Sets.newHashSet();
      mapByName.put(p_111121_1_.getName(), var2);
    }
    
    ((Set)mapByOperation.get(Integer.valueOf(p_111121_1_.getOperation()))).add(p_111121_1_);
    ((Set)var2).add(p_111121_1_);
    mapByUUID.put(p_111121_1_.getID(), p_111121_1_);
    flagForUpdate();
  }
  

  protected void flagForUpdate()
  {
    needsUpdate = true;
    attributeMap.func_180794_a(this);
  }
  
  public void removeModifier(AttributeModifier p_111124_1_)
  {
    for (int var2 = 0; var2 < 3; var2++)
    {
      Set var3 = (Set)mapByOperation.get(Integer.valueOf(var2));
      var3.remove(p_111124_1_);
    }
    
    Set var4 = (Set)mapByName.get(p_111124_1_.getName());
    
    if (var4 != null)
    {
      var4.remove(p_111124_1_);
      
      if (var4.isEmpty())
      {
        mapByName.remove(p_111124_1_.getName());
      }
    }
    
    mapByUUID.remove(p_111124_1_.getID());
    flagForUpdate();
  }
  
  public void removeAllModifiers()
  {
    Collection var1 = func_111122_c();
    
    if (var1 != null)
    {
      ArrayList var4 = Lists.newArrayList(var1);
      Iterator var2 = var4.iterator();
      
      while (var2.hasNext())
      {
        AttributeModifier var3 = (AttributeModifier)var2.next();
        removeModifier(var3);
      }
    }
  }
  
  public double getAttributeValue()
  {
    if (needsUpdate)
    {
      cachedValue = computeValue();
      needsUpdate = false;
    }
    
    return cachedValue;
  }
  
  private double computeValue()
  {
    double var1 = getBaseValue();
    
    AttributeModifier var4;
    for (Iterator var3 = func_180375_b(0).iterator(); var3.hasNext(); var1 += var4.getAmount())
    {
      var4 = (AttributeModifier)var3.next();
    }
    
    double var7 = var1;
    
    AttributeModifier var6;
    
    for (Iterator var5 = func_180375_b(1).iterator(); var5.hasNext(); var7 += var1 * var6.getAmount())
    {
      var6 = (AttributeModifier)var5.next();
    }
    AttributeModifier var6;
    for (var5 = func_180375_b(2).iterator(); var5.hasNext(); var7 *= (1.0D + var6.getAmount()))
    {
      var6 = (AttributeModifier)var5.next();
    }
    
    return genericAttribute.clampValue(var7);
  }
  
  private Collection func_180375_b(int p_180375_1_)
  {
    HashSet var2 = Sets.newHashSet(getModifiersByOperation(p_180375_1_));
    
    for (IAttribute var3 = genericAttribute.func_180372_d(); var3 != null; var3 = var3.func_180372_d())
    {
      IAttributeInstance var4 = attributeMap.getAttributeInstance(var3);
      
      if (var4 != null)
      {
        var2.addAll(var4.getModifiersByOperation(p_180375_1_));
      }
    }
    
    return var2;
  }
}
