package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityBubbleFX extends EntityFX
{
  private static final String __OBFID = "CL_00000898";
  
  protected EntityBubbleFX(World worldIn, double p_i1198_2_, double p_i1198_4_, double p_i1198_6_, double p_i1198_8_, double p_i1198_10_, double p_i1198_12_)
  {
    super(worldIn, p_i1198_2_, p_i1198_4_, p_i1198_6_, p_i1198_8_, p_i1198_10_, p_i1198_12_);
    particleRed = 1.0F;
    particleGreen = 1.0F;
    particleBlue = 1.0F;
    setParticleTextureIndex(32);
    setSize(0.02F, 0.02F);
    particleScale *= (rand.nextFloat() * 0.6F + 0.2F);
    motionX = (p_i1198_8_ * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D);
    motionY = (p_i1198_10_ * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D);
    motionZ = (p_i1198_12_ * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D);
    particleMaxAge = ((int)(8.0D / (Math.random() * 0.8D + 0.2D)));
  }
  



  public void onUpdate()
  {
    prevPosX = posX;
    prevPosY = posY;
    prevPosZ = posZ;
    motionY += 0.002D;
    moveEntity(motionX, motionY, motionZ);
    motionX *= 0.8500000238418579D;
    motionY *= 0.8500000238418579D;
    motionZ *= 0.8500000238418579D;
    
    if (worldObj.getBlockState(new net.minecraft.util.BlockPos(this)).getBlock().getMaterial() != net.minecraft.block.material.Material.water)
    {
      setDead();
    }
    
    if (particleMaxAge-- <= 0)
    {
      setDead();
    }
  }
  
  public static class Factory implements IParticleFactory {
    private static final String __OBFID = "CL_00002610";
    
    public Factory() {}
    
    public EntityFX func_178902_a(int p_178902_1_, World worldIn, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_) {
      return new EntityBubbleFX(worldIn, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_);
    }
  }
}
