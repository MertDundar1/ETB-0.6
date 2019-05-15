package net.minecraft.client.renderer.chunk;

import com.enjoytheban.Client;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.modules.render.Xray;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import optifine.IntegerCache;


public class VisGraph
{
  private static final int field_178616_a = (int)Math.pow(16.0D, 0.0D);
  private static final int field_178614_b = (int)Math.pow(16.0D, 1.0D);
  private static final int field_178615_c = (int)Math.pow(16.0D, 2.0D);
  private final BitSet field_178612_d = new BitSet(4096);
  private static final int[] field_178613_e = new int['Ո'];
  private int field_178611_f = 4096;
  private static final String __OBFID = "CL_00002450";
  
  public void func_178606_a(BlockPos p_178606_1_)
  {
    if (Client.instance.getModuleManager().getModuleByClass(Xray.class).isEnabled()) {
      return;
    }
    field_178612_d.set(func_178608_c(p_178606_1_), true);
    field_178611_f -= 1;
  }
  
  private static int func_178608_c(BlockPos p_178608_0_)
  {
    return func_178605_a(p_178608_0_.getX() & 0xF, p_178608_0_.getY() & 0xF, p_178608_0_.getZ() & 0xF);
  }
  
  private static int func_178605_a(int p_178605_0_, int p_178605_1_, int p_178605_2_)
  {
    return p_178605_0_ << 0 | p_178605_1_ << 8 | p_178605_2_ << 4;
  }
  
  public SetVisibility func_178607_a()
  {
    SetVisibility var1 = new SetVisibility();
    
    if (4096 - field_178611_f < 256)
    {
      var1.func_178618_a(true);
    }
    else if (field_178611_f == 0)
    {
      var1.func_178618_a(false);
    }
    else
    {
      int[] var2 = field_178613_e;
      int var3 = var2.length;
      
      for (int var4 = 0; var4 < var3; var4++)
      {
        int var5 = var2[var4];
        
        if (!field_178612_d.get(var5))
        {
          var1.func_178620_a(func_178604_a(var5));
        }
      }
    }
    
    return var1;
  }
  
  public Set func_178609_b(BlockPos p_178609_1_)
  {
    return func_178604_a(func_178608_c(p_178609_1_));
  }
  
  private Set func_178604_a(int p_178604_1_)
  {
    EnumSet var2 = EnumSet.noneOf(EnumFacing.class);
    ArrayDeque var3 = new ArrayDeque(384);
    var3.add(IntegerCache.valueOf(p_178604_1_));
    field_178612_d.set(p_178604_1_, true);
    int var6;
    int var7; for (; !var3.isEmpty(); 
        





        var7 < var6)
    {
      int var4 = ((Integer)var3.poll()).intValue();
      func_178610_a(var4, var2);
      EnumFacing[] var5 = EnumFacing.VALUES;
      var6 = var5.length;
      
      var7 = 0; continue;
      
      EnumFacing var8 = var5[var7];
      int var9 = func_178603_a(var4, var8);
      
      if ((var9 >= 0) && (!field_178612_d.get(var9)))
      {
        field_178612_d.set(var9, true);
        var3.add(IntegerCache.valueOf(var9));
      }
      var7++;
    }
    










    return var2;
  }
  
  private void func_178610_a(int p_178610_1_, Set p_178610_2_)
  {
    int var3 = p_178610_1_ >> 0 & 0xF;
    
    if (var3 == 0)
    {
      p_178610_2_.add(EnumFacing.WEST);
    }
    else if (var3 == 15)
    {
      p_178610_2_.add(EnumFacing.EAST);
    }
    
    int var4 = p_178610_1_ >> 8 & 0xF;
    
    if (var4 == 0)
    {
      p_178610_2_.add(EnumFacing.DOWN);
    }
    else if (var4 == 15)
    {
      p_178610_2_.add(EnumFacing.UP);
    }
    
    int var5 = p_178610_1_ >> 4 & 0xF;
    
    if (var5 == 0)
    {
      p_178610_2_.add(EnumFacing.NORTH);
    }
    else if (var5 == 15)
    {
      p_178610_2_.add(EnumFacing.SOUTH);
    }
  }
  
  private int func_178603_a(int p_178603_1_, EnumFacing p_178603_2_)
  {
    switch (SwitchEnumFacing.field_178617_a[p_178603_2_.ordinal()])
    {
    case 1: 
      if ((p_178603_1_ >> 8 & 0xF) == 0)
      {
        return -1;
      }
      
      return p_178603_1_ - field_178615_c;
    
    case 2: 
      if ((p_178603_1_ >> 8 & 0xF) == 15)
      {
        return -1;
      }
      
      return p_178603_1_ + field_178615_c;
    
    case 3: 
      if ((p_178603_1_ >> 4 & 0xF) == 0)
      {
        return -1;
      }
      
      return p_178603_1_ - field_178614_b;
    
    case 4: 
      if ((p_178603_1_ >> 4 & 0xF) == 15)
      {
        return -1;
      }
      
      return p_178603_1_ + field_178614_b;
    
    case 5: 
      if ((p_178603_1_ >> 0 & 0xF) == 0)
      {
        return -1;
      }
      
      return p_178603_1_ - field_178616_a;
    
    case 6: 
      if ((p_178603_1_ >> 0 & 0xF) == 15)
      {
        return -1;
      }
      
      return p_178603_1_ + field_178616_a;
    }
    
    return -1;
  }
  

  static
  {
    boolean var0 = false;
    boolean var1 = true;
    int var2 = 0;
    
    for (int var3 = 0; var3 < 16; var3++)
    {
      for (int var4 = 0; var4 < 16; var4++)
      {
        for (int var5 = 0; var5 < 16; var5++)
        {
          if ((var3 == 0) || (var3 == 15) || (var4 == 0) || (var4 == 15) || (var5 == 0) || (var5 == 15))
          {
            field_178613_e[(var2++)] = func_178605_a(var3, var4, var5); }
        }
      }
    }
  }
  
  public VisGraph() {}
  
  static final class SwitchEnumFacing {
    static final int[] field_178617_a = new int[EnumFacing.values().length];
    private static final String __OBFID = "CL_00002449";
    
    static
    {
      try
      {
        field_178617_a[EnumFacing.DOWN.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        field_178617_a[EnumFacing.UP.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        field_178617_a[EnumFacing.NORTH.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      try
      {
        field_178617_a[EnumFacing.SOUTH.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
      



      try
      {
        field_178617_a[EnumFacing.WEST.ordinal()] = 5;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {}
      



      try
      {
        field_178617_a[EnumFacing.EAST.ordinal()] = 6;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {}
    }
    
    SwitchEnumFacing() {}
  }
}
