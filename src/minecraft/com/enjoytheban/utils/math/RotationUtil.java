package com.enjoytheban.utils.math;

import com.enjoytheban.utils.Helper;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;




public class RotationUtil
{
  public RotationUtil() {}
  
  public static float pitch()
  {
    return mcthePlayer.rotationPitch;
  }
  
  public static void pitch(float pitch) {
    mcthePlayer.rotationPitch = pitch;
  }
  
  public static float yaw()
  {
    return mcthePlayer.rotationYaw;
  }
  
  public static void yaw(float yaw) {
    mcthePlayer.rotationYaw = yaw;
  }
  
  public static float[] faceTarget(Entity target, float p_706252, float p_706253, boolean miss)
  {
    double var4 = posX - mcthePlayer.posX;
    double var8 = posZ - mcthePlayer.posZ;
    double var6;
    double var6; if ((target instanceof EntityLivingBase)) {
      EntityLivingBase var10 = (EntityLivingBase)target;
      var6 = posY + var10.getEyeHeight() - (
        mcthePlayer.posY + mcthePlayer.getEyeHeight());
    } else {
      var6 = (getEntityBoundingBoxminY + getEntityBoundingBoxmaxY) / 2.0D - (
        mcthePlayer.posY + mcthePlayer.getEyeHeight());
    }
    Random rnd = new Random();
    double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
    float var12 = (float)(Math.atan2(var8, var4) * 180.0D / 3.141592653589793D) - 90.0F;
    float var13 = (float)-(Math.atan2(var6 - ((target instanceof EntityPlayer) ? 0.25D : 0.0D), var14) * 180.0D / 
      3.141592653589793D);
    float pitch = changeRotation(mcthePlayer.rotationPitch, var13, p_706253);
    float yaw = changeRotation(mcthePlayer.rotationYaw, var12, p_706252);
    return new float[] { yaw, pitch };
  }
  
  public static float changeRotation(float p_706631, float p_706632, float p_706633) {
    float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
    if (var4 > p_706633)
      var4 = p_706633;
    if (var4 < -p_706633)
      var4 = -p_706633;
    return p_706631 + var4;
  }
  
  public static double[] getRotationToEntity(Entity entity) {
    double pX = mcthePlayer.posX;
    double pY = mcthePlayer.posY + mcthePlayer.getEyeHeight();
    double pZ = mcthePlayer.posZ;
    
    double eX = posX;
    double eY = posY + height / 2.0F;
    double eZ = posZ;
    
    double dX = pX - eX;
    double dY = pY - eY;
    double dZ = pZ - eZ;
    double dH = Math.sqrt(Math.pow(dX, 2.0D) + Math.pow(dZ, 2.0D));
    
    double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0D;
    double pitch = Math.toDegrees(Math.atan2(dH, dY));
    
    return new double[] { yaw, 90.0D - pitch };
  }
  

  public static float[] getRotations(Entity entity)
  {
    if (mcbreakTheGame)
      return new float[] { 0.0F, -90.0F };
    if (entity == null) {
      return null;
    }
    

    double diffX = posX - mcthePlayer.posX;
    double diffZ = posZ - mcthePlayer.posZ;
    
    double diffY;
    
    double diffY;
    if ((entity instanceof EntityLivingBase))
    {
      EntityLivingBase elb = (EntityLivingBase)entity;
      
      diffY = posY + (elb.getEyeHeight() - 0.4D) - (
        mcthePlayer.posY + mcthePlayer.getEyeHeight());
    } else {
      diffY = (boundingBox.minY + boundingBox.maxY) / 2.0D - (
        mcthePlayer.posY + mcthePlayer.getEyeHeight());
    }
    

    double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
    float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
    float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D);
    return new float[] { yaw, pitch };
  }
  

  public static float getDistanceBetweenAngles(float angle1, float angle2)
  {
    float angle3 = Math.abs(angle1 - angle2) % 360.0F;
    
    if (angle3 > 180.0F) {
      angle3 = 0.0F;
    }
    return angle3;
  }
  
  public static float[] grabBlockRotations(BlockPos pos)
  {
    return 
      getVecRotation(
      mcthePlayer.getPositionVector().addVector(0.0D, 
      mcthePlayer.getEyeHeight(), 0.0D), 
      new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D));
  }
  
  public static float[] getVecRotation(Vec3 position) {
    return getVecRotation(mcthePlayer.getPositionVector().addVector(0.0D, 
      mcthePlayer.getEyeHeight(), 0.0D), position);
  }
  
  public static float[] getVecRotation(Vec3 origin, Vec3 position) {
    Vec3 difference = position.subtract(origin);
    double distance = difference.flat().lengthVector();
    float yaw = (float)Math.toDegrees(Math.atan2(zCoord, xCoord)) - 90.0F;
    float pitch = (float)-Math.toDegrees(Math.atan2(yCoord, distance));
    return new float[] { yaw, pitch };
  }
  
  public static int wrapAngleToDirection(float yaw, int zones)
  {
    int angle = (int)(yaw + 360 / (2 * zones) + 0.5D) % 360;
    if (angle < 0) {
      angle += 360;
    }
    return angle / (360 / zones);
  }
}
