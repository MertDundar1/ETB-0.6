package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.MapPopulator;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;

public class BlockState
{
  private static final Joiner COMMA_JOINER = Joiner.on(", ");
  

  private static final Function GET_NAME_FUNC = new Function()
  {
    private static final String __OBFID = "CL_00002029";
    
    public String apply(IProperty property) {
      return property == null ? "<NULL>" : property.getName();
    }
    
    public Object apply(Object p_apply_1_) {
      return apply((IProperty)p_apply_1_);
    }
  };
  private final Block block;
  private final ImmutableList properties;
  private final ImmutableList validStates;
  private static final String __OBFID = "CL_00002030";
  
  public BlockState(Block blockIn, IProperty... properties)
  {
    block = blockIn;
    Arrays.sort(properties, new Comparator()
    {
      private static final String __OBFID = "CL_00002028";
      
      public int compare(IProperty left, IProperty right) {
        return left.getName().compareTo(right.getName());
      }
      
      public int compare(Object p_compare_1_, Object p_compare_2_) {
        return compare((IProperty)p_compare_1_, (IProperty)p_compare_2_);
      }
    });
    this.properties = ImmutableList.copyOf(properties);
    LinkedHashMap var3 = Maps.newLinkedHashMap();
    ArrayList var4 = Lists.newArrayList();
    Iterable var5 = net.minecraft.util.Cartesian.cartesianProduct(getAllowedValues());
    Iterator var6 = var5.iterator();
    
    while (var6.hasNext())
    {
      List var7 = (List)var6.next();
      Map var8 = MapPopulator.createMap(this.properties, var7);
      StateImplemenation var9 = new StateImplemenation(blockIn, ImmutableMap.copyOf(var8), null);
      var3.put(var8, var9);
      var4.add(var9);
    }
    
    var6 = var4.iterator();
    
    while (var6.hasNext())
    {
      StateImplemenation var10 = (StateImplemenation)var6.next();
      var10.buildPropertyValueTable(var3);
    }
    
    validStates = ImmutableList.copyOf(var4);
  }
  
  public ImmutableList getValidStates()
  {
    return validStates;
  }
  
  private List getAllowedValues()
  {
    ArrayList var1 = Lists.newArrayList();
    
    for (int var2 = 0; var2 < properties.size(); var2++)
    {
      var1.add(((IProperty)properties.get(var2)).getAllowedValues());
    }
    
    return var1;
  }
  
  public IBlockState getBaseState()
  {
    return (IBlockState)validStates.get(0);
  }
  
  public Block getBlock()
  {
    return block;
  }
  
  public Collection getProperties()
  {
    return properties;
  }
  
  public String toString()
  {
    return com.google.common.base.Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(block)).add("properties", Iterables.transform(properties, GET_NAME_FUNC)).toString();
  }
  
  static class StateImplemenation extends BlockStateBase
  {
    private final Block block;
    private final ImmutableMap properties;
    private ImmutableTable propertyValueTable;
    private static final String __OBFID = "CL_00002027";
    
    private StateImplemenation(Block p_i45660_1_, ImmutableMap p_i45660_2_)
    {
      block = p_i45660_1_;
      properties = p_i45660_2_;
    }
    
    public Collection getPropertyNames()
    {
      return java.util.Collections.unmodifiableCollection(properties.keySet());
    }
    
    public Comparable getValue(IProperty property)
    {
      if (!properties.containsKey(property))
      {
        throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + block.getBlockState());
      }
      

      return (Comparable)property.getValueClass().cast(properties.get(property));
    }
    

    public IBlockState withProperty(IProperty property, Comparable value)
    {
      if (!properties.containsKey(property))
      {
        throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + block.getBlockState());
      }
      if (!property.getAllowedValues().contains(value))
      {
        throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.blockRegistry.getNameForObject(block) + ", it is not an allowed value");
      }
      

      return properties.get(property) == value ? this : (IBlockState)propertyValueTable.get(property, value);
    }
    

    public ImmutableMap getProperties()
    {
      return properties;
    }
    
    public Block getBlock()
    {
      return block;
    }
    
    public boolean equals(Object p_equals_1_)
    {
      return this == p_equals_1_;
    }
    
    public int hashCode()
    {
      return properties.hashCode();
    }
    
    public void buildPropertyValueTable(Map map)
    {
      if (propertyValueTable != null)
      {
        throw new IllegalStateException();
      }
      

      HashBasedTable var2 = HashBasedTable.create();
      Iterator var3 = properties.keySet().iterator();
      Iterator var5;
      for (; var3.hasNext(); 
          



          var5.hasNext())
      {
        IProperty var4 = (IProperty)var3.next();
        var5 = var4.getAllowedValues().iterator();
        
        continue;
        
        Comparable var6 = (Comparable)var5.next();
        
        if (var6 != properties.get(var4))
        {
          var2.put(var4, var6, map.get(setPropertyValue(var4, var6)));
        }
      }
      

      propertyValueTable = ImmutableTable.copyOf(var2);
    }
    

    private Map setPropertyValue(IProperty property, Comparable value)
    {
      HashMap var3 = Maps.newHashMap(properties);
      var3.put(property, value);
      return var3;
    }
    
    StateImplemenation(Block p_i45661_1_, ImmutableMap p_i45661_2_, Object p_i45661_3_)
    {
      this(p_i45661_1_, p_i45661_2_);
    }
  }
}
