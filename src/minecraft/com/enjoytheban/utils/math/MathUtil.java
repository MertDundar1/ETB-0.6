package com.enjoytheban.utils.math;

import com.enjoytheban.utils.Helper;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class MathUtil
{
  public static Random random = new Random();
  
  public MathUtil() {}
  
  public static double toDecimalLength(double in, int places) {
    return Double.parseDouble(String.format("%." + places + "f", new Object[] { Double.valueOf(in) }));
  }
  


  public static double round(double in, int places)
  {
    places = (int)net.minecraft.util.MathHelper.clamp_double(places, 0.0D, 2.147483647E9D);
    return Double.parseDouble(String.format("%." + places + "f", new Object[] { Double.valueOf(in) }));
  }
  

  public static boolean parsable(String s, byte type)
  {
    try
    {
      switch (type) {
      case 0: 
        Short.parseShort(s);
        break;
      case 1: 
        Byte.parseByte(s);
        break;
      case 2: 
        Integer.parseInt(s);
        break;
      case 3: 
        Float.parseFloat(s);
        break;
      case 4: 
        Double.parseDouble(s);
        break;
      case 5: 
        Long.parseLong(s);
      }
    }
    catch (NumberFormatException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  


  public static double square(double in) { return in * in; }
  
  public static class NumberType {
    public static final byte SHORT = 0;
    public static final byte BYTE = 1;
    public static final byte INT = 2;
    
    public NumberType() {}
    
    public static byte getByType(Class cls) {
      if (cls == Short.class)
        return 0;
      if (cls == Byte.class)
        return 1;
      if (cls == Integer.class)
        return 2;
      if (cls == Float.class)
        return 3;
      if (cls == Double.class)
        return 4;
      if (cls == Long.class) {
        return 5;
      }
      return -1;
    }
    
    public static final byte FLOAT = 3;
    public static final byte DOUBLE = 4;
    public static final byte LONG = 5;
  }
  
  public static double randomDouble(double min, double max) {
    return ThreadLocalRandom.current().nextDouble(min, max);
  }
  
  public static double getBaseMovementSpeed()
  {
    double baseSpeed = 0.2873D;
    if (mcthePlayer.isPotionActive(Potion.moveSpeed)) {
      int amplifier = mcthePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
      baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
    }
    return baseSpeed;
  }
  
  public static double getHighestOffset(double max)
  {
    for (double i = 0.0D; i < max; i += 0.01D) {
      for (int offset : new int[] { -2, -1, 0, 1, 2 })
      {


        if (mctheWorld.getCollidingBoundingBoxes(mcthePlayer, mcthePlayer.getEntityBoundingBox().offset(mcthePlayer.motionX * offset, i, mcthePlayer.motionZ * offset)).size() > 0) {
          return i - 0.01D;
        }
      }
    }
    return max;
  }
}
