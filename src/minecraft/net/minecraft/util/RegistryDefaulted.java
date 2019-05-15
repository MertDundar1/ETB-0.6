package net.minecraft.util;


public class RegistryDefaulted
  extends RegistrySimple
{
  private final Object defaultObject;
  
  private static final String __OBFID = "CL_00001198";
  
  public RegistryDefaulted(Object p_i1366_1_)
  {
    defaultObject = p_i1366_1_;
  }
  
  public Object getObject(Object p_82594_1_)
  {
    Object var2 = super.getObject(p_82594_1_);
    return var2 == null ? defaultObject : var2;
  }
}
