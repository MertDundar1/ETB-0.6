package net.minecraft.client.gui;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IntHashMap;

public class GuiPageButtonList extends GuiListExtended
{
  private final List field_178074_u = Lists.newArrayList();
  private final IntHashMap field_178073_v = new IntHashMap();
  private final List field_178072_w = Lists.newArrayList();
  private final GuiListEntry[][] field_178078_x;
  private int field_178077_y;
  private GuiResponder field_178076_z;
  private Gui field_178075_A;
  private static final String __OBFID = "CL_00001950";
  
  public GuiPageButtonList(Minecraft mcIn, int p_i45536_2_, int p_i45536_3_, int p_i45536_4_, int p_i45536_5_, int p_i45536_6_, GuiResponder p_i45536_7_, GuiListEntry[]... p_i45536_8_)
  {
    super(mcIn, p_i45536_2_, p_i45536_3_, p_i45536_4_, p_i45536_5_, p_i45536_6_);
    field_178076_z = p_i45536_7_;
    field_178078_x = p_i45536_8_;
    field_148163_i = false;
    func_178069_s();
    func_178055_t();
  }
  
  private void func_178069_s()
  {
    GuiListEntry[][] var1 = field_178078_x;
    int var2 = var1.length;
    
    for (int var3 = 0; var3 < var2; var3++)
    {
      GuiListEntry[] var4 = var1[var3];
      
      for (int var5 = 0; var5 < var4.length; var5 += 2)
      {
        GuiListEntry var6 = var4[var5];
        GuiListEntry var7 = var5 < var4.length - 1 ? var4[(var5 + 1)] : null;
        Gui var8 = func_178058_a(var6, 0, var7 == null);
        Gui var9 = func_178058_a(var7, 160, var6 == null);
        GuiEntry var10 = new GuiEntry(var8, var9);
        field_178074_u.add(var10);
        
        if ((var6 != null) && (var8 != null))
        {
          field_178073_v.addKey(var6.func_178935_b(), var8);
          
          if ((var8 instanceof GuiTextField))
          {
            field_178072_w.add((GuiTextField)var8);
          }
        }
        
        if ((var7 != null) && (var9 != null))
        {
          field_178073_v.addKey(var7.func_178935_b(), var9);
          
          if ((var9 instanceof GuiTextField))
          {
            field_178072_w.add((GuiTextField)var9);
          }
        }
      }
    }
  }
  
  private void func_178055_t()
  {
    field_178074_u.clear();
    
    for (int var1 = 0; var1 < field_178078_x[field_178077_y].length; var1 += 2)
    {
      GuiListEntry var2 = field_178078_x[field_178077_y][var1];
      GuiListEntry var3 = var1 < field_178078_x[field_178077_y].length - 1 ? field_178078_x[field_178077_y][(var1 + 1)] : null;
      Gui var4 = (Gui)field_178073_v.lookup(var2.func_178935_b());
      Gui var5 = var3 != null ? (Gui)field_178073_v.lookup(var3.func_178935_b()) : null;
      GuiEntry var6 = new GuiEntry(var4, var5);
      field_178074_u.add(var6);
    }
  }
  
  public int func_178059_e()
  {
    return field_178077_y;
  }
  
  public int func_178057_f()
  {
    return field_178078_x.length;
  }
  
  public Gui func_178056_g()
  {
    return field_178075_A;
  }
  
  public void func_178071_h()
  {
    if (field_178077_y > 0)
    {
      int var1 = field_178077_y--;
      func_178055_t();
      func_178060_e(var1, field_178077_y);
      amountScrolled = 0.0F;
    }
  }
  
  public void func_178064_i()
  {
    if (field_178077_y < field_178078_x.length - 1)
    {
      int var1 = field_178077_y++;
      func_178055_t();
      func_178060_e(var1, field_178077_y);
      amountScrolled = 0.0F;
    }
  }
  
  public Gui func_178061_c(int p_178061_1_)
  {
    return (Gui)field_178073_v.lookup(p_178061_1_);
  }
  
  private void func_178060_e(int p_178060_1_, int p_178060_2_)
  {
    GuiListEntry[] var3 = field_178078_x[p_178060_1_];
    int var4 = var3.length;
    


    for (int var5 = 0; var5 < var4; var5++)
    {
      GuiListEntry var6 = var3[var5];
      
      if (var6 != null)
      {
        func_178066_a((Gui)field_178073_v.lookup(var6.func_178935_b()), false);
      }
    }
    
    var3 = field_178078_x[p_178060_2_];
    var4 = var3.length;
    
    for (var5 = 0; var5 < var4; var5++)
    {
      GuiListEntry var6 = var3[var5];
      
      if (var6 != null)
      {
        func_178066_a((Gui)field_178073_v.lookup(var6.func_178935_b()), true);
      }
    }
  }
  
  private void func_178066_a(Gui p_178066_1_, boolean p_178066_2_)
  {
    if ((p_178066_1_ instanceof GuiButton))
    {
      visible = p_178066_2_;
    }
    else if ((p_178066_1_ instanceof GuiTextField))
    {
      ((GuiTextField)p_178066_1_).setVisible(p_178066_2_);
    }
    else if ((p_178066_1_ instanceof GuiLabel))
    {
      visible = p_178066_2_;
    }
  }
  
  private Gui func_178058_a(GuiListEntry p_178058_1_, int p_178058_2_, boolean p_178058_3_)
  {
    return (p_178058_1_ instanceof GuiLabelEntry) ? func_178063_a(width / 2 - 155 + p_178058_2_, 0, (GuiLabelEntry)p_178058_1_, p_178058_3_) : (p_178058_1_ instanceof EditBoxEntry) ? func_178068_a(width / 2 - 155 + p_178058_2_, 0, (EditBoxEntry)p_178058_1_) : (p_178058_1_ instanceof GuiButtonEntry) ? func_178065_a(width / 2 - 155 + p_178058_2_, 0, (GuiButtonEntry)p_178058_1_) : (p_178058_1_ instanceof GuiSlideEntry) ? func_178067_a(width / 2 - 155 + p_178058_2_, 0, (GuiSlideEntry)p_178058_1_) : null;
  }
  
  public boolean func_148179_a(int p_148179_1_, int p_148179_2_, int p_148179_3_)
  {
    boolean var4 = super.func_148179_a(p_148179_1_, p_148179_2_, p_148179_3_);
    int var5 = getSlotIndexFromScreenCoords(p_148179_1_, p_148179_2_);
    
    if (var5 >= 0)
    {
      GuiEntry var6 = func_178070_d(var5);
      
      if ((field_178075_A != field_178028_d) && (field_178075_A != null) && ((field_178075_A instanceof GuiTextField)))
      {
        ((GuiTextField)field_178075_A).setFocused(false);
      }
      
      field_178075_A = field_178028_d;
    }
    
    return var4;
  }
  
  private GuiSlider func_178067_a(int p_178067_1_, int p_178067_2_, GuiSlideEntry p_178067_3_)
  {
    GuiSlider var4 = new GuiSlider(field_178076_z, p_178067_3_.func_178935_b(), p_178067_1_, p_178067_2_, p_178067_3_.func_178936_c(), p_178067_3_.func_178943_e(), p_178067_3_.func_178944_f(), p_178067_3_.func_178942_g(), p_178067_3_.func_178945_a());
    visible = p_178067_3_.func_178934_d();
    return var4;
  }
  
  private GuiListButton func_178065_a(int p_178065_1_, int p_178065_2_, GuiButtonEntry p_178065_3_)
  {
    GuiListButton var4 = new GuiListButton(field_178076_z, p_178065_3_.func_178935_b(), p_178065_1_, p_178065_2_, p_178065_3_.func_178936_c(), p_178065_3_.func_178940_a());
    visible = p_178065_3_.func_178934_d();
    return var4;
  }
  
  private GuiTextField func_178068_a(int p_178068_1_, int p_178068_2_, EditBoxEntry p_178068_3_)
  {
    GuiTextField var4 = new GuiTextField(p_178068_3_.func_178935_b(), mc.fontRendererObj, p_178068_1_, p_178068_2_, 150, 20);
    var4.setText(p_178068_3_.func_178936_c());
    var4.func_175207_a(field_178076_z);
    var4.setVisible(p_178068_3_.func_178934_d());
    var4.func_175205_a(p_178068_3_.func_178950_a());
    return var4;
  }
  
  private GuiLabel func_178063_a(int p_178063_1_, int p_178063_2_, GuiLabelEntry p_178063_3_, boolean p_178063_4_)
  {
    GuiLabel var5;
    GuiLabel var5;
    if (p_178063_4_)
    {
      var5 = new GuiLabel(mc.fontRendererObj, p_178063_3_.func_178935_b(), p_178063_1_, p_178063_2_, width - p_178063_1_ * 2, 20, -1);
    }
    else
    {
      var5 = new GuiLabel(mc.fontRendererObj, p_178063_3_.func_178935_b(), p_178063_1_, p_178063_2_, 150, 20, -1);
    }
    
    visible = p_178063_3_.func_178934_d();
    var5.func_175202_a(p_178063_3_.func_178936_c());
    var5.func_175203_a();
    return var5;
  }
  
  public void func_178062_a(char p_178062_1_, int p_178062_2_)
  {
    if ((field_178075_A instanceof GuiTextField))
    {
      GuiTextField var3 = (GuiTextField)field_178075_A;
      

      if (!GuiScreen.func_175279_e(p_178062_2_))
      {
        if (p_178062_2_ == 15)
        {
          var3.setFocused(false);
          int var12 = field_178072_w.indexOf(field_178075_A);
          
          if (GuiScreen.isShiftKeyDown())
          {
            if (var12 == 0)
            {
              var12 = field_178072_w.size() - 1;
            }
            else
            {
              var12--;
            }
          }
          else if (var12 == field_178072_w.size() - 1)
          {
            var12 = 0;
          }
          else
          {
            var12++;
          }
          
          field_178075_A = ((Gui)field_178072_w.get(var12));
          var3 = (GuiTextField)field_178075_A;
          var3.setFocused(true);
          int var13 = yPosition + slotHeight;
          int var6 = yPosition;
          
          if (var13 > bottom)
          {
            amountScrolled += var13 - bottom;
          }
          else if (var6 < top)
          {
            amountScrolled = var6;
          }
        }
        else
        {
          var3.textboxKeyTyped(p_178062_1_, p_178062_2_);
        }
      }
      else
      {
        String var4 = GuiScreen.getClipboardString();
        String[] var5 = var4.split(";");
        int var6 = field_178072_w.indexOf(field_178075_A);
        int var7 = var6;
        String[] var8 = var5;
        int var9 = var5.length;
        
        for (int var10 = 0; var10 < var9; var10++)
        {
          String var11 = var8[var10];
          ((GuiTextField)field_178072_w.get(var7)).setText(var11);
          
          if (var7 == field_178072_w.size() - 1)
          {
            var7 = 0;
          }
          else
          {
            var7++;
          }
          
          if (var7 == var6) {
            break;
          }
        }
      }
    }
  }
  

  public GuiEntry func_178070_d(int p_178070_1_)
  {
    return (GuiEntry)field_178074_u.get(p_178070_1_);
  }
  
  public int getSize()
  {
    return field_178074_u.size();
  }
  



  public int getListWidth()
  {
    return 400;
  }
  
  protected int getScrollBarX()
  {
    return super.getScrollBarX() + 32;
  }
  



  public GuiListExtended.IGuiListEntry getListEntry(int p_148180_1_)
  {
    return func_178070_d(p_148180_1_);
  }
  
  public static class EditBoxEntry extends GuiPageButtonList.GuiListEntry
  {
    private final Predicate field_178951_a;
    private static final String __OBFID = "CL_00001948";
    
    public EditBoxEntry(int p_i45534_1_, String p_i45534_2_, boolean p_i45534_3_, Predicate p_i45534_4_)
    {
      super(p_i45534_2_, p_i45534_3_);
      field_178951_a = ((Predicate)Objects.firstNonNull(p_i45534_4_, Predicates.alwaysTrue()));
    }
    
    public Predicate func_178950_a()
    {
      return field_178951_a;
    }
  }
  
  public static class GuiButtonEntry extends GuiPageButtonList.GuiListEntry
  {
    private final boolean field_178941_a;
    private static final String __OBFID = "CL_00001949";
    
    public GuiButtonEntry(int p_i45535_1_, String p_i45535_2_, boolean p_i45535_3_, boolean p_i45535_4_)
    {
      super(p_i45535_2_, p_i45535_3_);
      field_178941_a = p_i45535_4_;
    }
    
    public boolean func_178940_a()
    {
      return field_178941_a;
    }
  }
  
  public static class GuiEntry implements GuiListExtended.IGuiListEntry
  {
    private final Minecraft field_178031_a = Minecraft.getMinecraft();
    private final Gui field_178029_b;
    private final Gui field_178030_c;
    private Gui field_178028_d;
    private static final String __OBFID = "CL_00001947";
    
    public GuiEntry(Gui p_i45533_1_, Gui p_i45533_2_)
    {
      field_178029_b = p_i45533_1_;
      field_178030_c = p_i45533_2_;
    }
    
    public Gui func_178022_a()
    {
      return field_178029_b;
    }
    
    public Gui func_178021_b()
    {
      return field_178030_c;
    }
    
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
      func_178017_a(field_178029_b, y, mouseX, mouseY, false);
      func_178017_a(field_178030_c, y, mouseX, mouseY, false);
    }
    
    private void func_178017_a(Gui p_178017_1_, int p_178017_2_, int p_178017_3_, int p_178017_4_, boolean p_178017_5_)
    {
      if (p_178017_1_ != null)
      {
        if ((p_178017_1_ instanceof GuiButton))
        {
          func_178024_a((GuiButton)p_178017_1_, p_178017_2_, p_178017_3_, p_178017_4_, p_178017_5_);
        }
        else if ((p_178017_1_ instanceof GuiTextField))
        {
          func_178027_a((GuiTextField)p_178017_1_, p_178017_2_, p_178017_5_);
        }
        else if ((p_178017_1_ instanceof GuiLabel))
        {
          func_178025_a((GuiLabel)p_178017_1_, p_178017_2_, p_178017_3_, p_178017_4_, p_178017_5_);
        }
      }
    }
    
    private void func_178024_a(GuiButton p_178024_1_, int p_178024_2_, int p_178024_3_, int p_178024_4_, boolean p_178024_5_)
    {
      yPosition = p_178024_2_;
      
      if (!p_178024_5_)
      {
        p_178024_1_.drawButton(field_178031_a, p_178024_3_, p_178024_4_);
      }
    }
    
    private void func_178027_a(GuiTextField p_178027_1_, int p_178027_2_, boolean p_178027_3_)
    {
      yPosition = p_178027_2_;
      
      if (!p_178027_3_)
      {
        p_178027_1_.drawTextBox();
      }
    }
    
    private void func_178025_a(GuiLabel p_178025_1_, int p_178025_2_, int p_178025_3_, int p_178025_4_, boolean p_178025_5_)
    {
      field_146174_h = p_178025_2_;
      
      if (!p_178025_5_)
      {
        p_178025_1_.drawLabel(field_178031_a, p_178025_3_, p_178025_4_);
      }
    }
    
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
      func_178017_a(field_178029_b, p_178011_3_, 0, 0, true);
      func_178017_a(field_178030_c, p_178011_3_, 0, 0, true);
    }
    
    public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
      boolean var7 = func_178026_a(field_178029_b, p_148278_2_, p_148278_3_, p_148278_4_);
      boolean var8 = func_178026_a(field_178030_c, p_148278_2_, p_148278_3_, p_148278_4_);
      return (var7) || (var8);
    }
    
    private boolean func_178026_a(Gui p_178026_1_, int p_178026_2_, int p_178026_3_, int p_178026_4_)
    {
      if (p_178026_1_ == null)
      {
        return false;
      }
      if ((p_178026_1_ instanceof GuiButton))
      {
        return func_178023_a((GuiButton)p_178026_1_, p_178026_2_, p_178026_3_, p_178026_4_);
      }
      

      if ((p_178026_1_ instanceof GuiTextField))
      {
        func_178018_a((GuiTextField)p_178026_1_, p_178026_2_, p_178026_3_, p_178026_4_);
      }
      
      return false;
    }
    

    private boolean func_178023_a(GuiButton p_178023_1_, int p_178023_2_, int p_178023_3_, int p_178023_4_)
    {
      boolean var5 = p_178023_1_.mousePressed(field_178031_a, p_178023_2_, p_178023_3_);
      
      if (var5)
      {
        field_178028_d = p_178023_1_;
      }
      
      return var5;
    }
    
    private void func_178018_a(GuiTextField p_178018_1_, int p_178018_2_, int p_178018_3_, int p_178018_4_)
    {
      p_178018_1_.mouseClicked(p_178018_2_, p_178018_3_, p_178018_4_);
      
      if (p_178018_1_.isFocused())
      {
        field_178028_d = p_178018_1_;
      }
    }
    
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
      func_178016_b(field_178029_b, x, y, mouseEvent);
      func_178016_b(field_178030_c, x, y, mouseEvent);
    }
    
    private void func_178016_b(Gui p_178016_1_, int p_178016_2_, int p_178016_3_, int p_178016_4_)
    {
      if (p_178016_1_ != null)
      {
        if ((p_178016_1_ instanceof GuiButton))
        {
          func_178019_b((GuiButton)p_178016_1_, p_178016_2_, p_178016_3_, p_178016_4_);
        }
      }
    }
    
    private void func_178019_b(GuiButton p_178019_1_, int p_178019_2_, int p_178019_3_, int p_178019_4_)
    {
      p_178019_1_.mouseReleased(p_178019_2_, p_178019_3_);
    }
  }
  
  public static class GuiLabelEntry extends GuiPageButtonList.GuiListEntry
  {
    private static final String __OBFID = "CL_00001946";
    
    public GuiLabelEntry(int p_i45532_1_, String p_i45532_2_, boolean p_i45532_3_)
    {
      super(p_i45532_2_, p_i45532_3_);
    }
  }
  
  public static class GuiListEntry
  {
    private final int field_178939_a;
    private final String field_178937_b;
    private final boolean field_178938_c;
    private static final String __OBFID = "CL_00001945";
    
    public GuiListEntry(int p_i45531_1_, String p_i45531_2_, boolean p_i45531_3_)
    {
      field_178939_a = p_i45531_1_;
      field_178937_b = p_i45531_2_;
      field_178938_c = p_i45531_3_;
    }
    
    public int func_178935_b()
    {
      return field_178939_a;
    }
    
    public String func_178936_c()
    {
      return field_178937_b;
    }
    
    public boolean func_178934_d()
    {
      return field_178938_c;
    }
  }
  
  public static abstract interface GuiResponder
  {
    public abstract void func_175321_a(int paramInt, boolean paramBoolean);
    
    public abstract void func_175320_a(int paramInt, float paramFloat);
    
    public abstract void func_175319_a(int paramInt, String paramString);
  }
  
  public static class GuiSlideEntry extends GuiPageButtonList.GuiListEntry
  {
    private final GuiSlider.FormatHelper field_178949_a;
    private final float field_178947_b;
    private final float field_178948_c;
    private final float field_178946_d;
    private static final String __OBFID = "CL_00001944";
    
    public GuiSlideEntry(int p_i45530_1_, String p_i45530_2_, boolean p_i45530_3_, GuiSlider.FormatHelper p_i45530_4_, float p_i45530_5_, float p_i45530_6_, float p_i45530_7_)
    {
      super(p_i45530_2_, p_i45530_3_);
      field_178949_a = p_i45530_4_;
      field_178947_b = p_i45530_5_;
      field_178948_c = p_i45530_6_;
      field_178946_d = p_i45530_7_;
    }
    
    public GuiSlider.FormatHelper func_178945_a()
    {
      return field_178949_a;
    }
    
    public float func_178943_e()
    {
      return field_178947_b;
    }
    
    public float func_178944_f()
    {
      return field_178948_c;
    }
    
    public float func_178942_g()
    {
      return field_178946_d;
    }
  }
}
