package com.enjoytheban.module.modules.render;

import com.enjoytheban.api.events.rendering.EventRender3D;
import com.enjoytheban.utils.math.Vec4f;
import com.enjoytheban.utils.render.GLUProjection.Projection;
import java.awt.Color;
import javax.vecmath.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class ChestESP extends com.enjoytheban.module.Module
{
  public ChestESP()
  {
    super("ChestESP", new String[] { "chesthack" }, com.enjoytheban.module.ModuleType.Render);
    setColor(new Color(90, 209, 165).getRGB());
  }
  
  @com.enjoytheban.api.EventHandler
  public void onRender(EventRender3D eventRender) {
    ScaledResolution scaledResolution = new ScaledResolution(mc);
    for (Object o : mc.theWorld.loadedTileEntityList) {
      TileEntity tileEntity = (TileEntity)o;
      if ((tileEntity != null) && (isStorage(tileEntity))) {
        double posX = tileEntity.getPos().getX();
        double posY = tileEntity.getPos().getY();
        double posZ = tileEntity.getPos().getZ();
        
        AxisAlignedBB axisAlignedBB = null;
        if ((tileEntity instanceof TileEntityChest)) {
          Block block = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
          Block x1 = mc.theWorld.getBlockState(new BlockPos(posX + 1.0D, posY, posZ)).getBlock();
          Block x2 = mc.theWorld.getBlockState(new BlockPos(posX - 1.0D, posY, posZ)).getBlock();
          Block z1 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ + 1.0D)).getBlock();
          Block z2 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ - 1.0D)).getBlock();
          if (block != Blocks.trapped_chest) {
            if (x1 == Blocks.chest) {
              mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806D - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + 0.05000000074505806D - RenderManager.renderPosZ, posX + 1.9500000476837158D - RenderManager.renderPosX, posY + 0.8999999761581421D - RenderManager.renderPosY, posZ + 0.949999988079071D - RenderManager.renderPosZ);
            } else if (z2 == Blocks.chest) {
              mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806D - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + 0.05000000074505806D - RenderManager.renderPosZ - 1.0D, posX + 0.949999988079071D - RenderManager.renderPosX, posY + 0.8999999761581421D - RenderManager.renderPosY, posZ + 0.949999988079071D - RenderManager.renderPosZ);
            } else if ((x1 != Blocks.chest) && (x2 != Blocks.chest) && (z1 != Blocks.chest) && (z2 != Blocks.chest)) {
              mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806D - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + 0.05000000074505806D - RenderManager.renderPosZ, posX + 0.949999988079071D - RenderManager.renderPosX, posY + 0.8999999761581421D - RenderManager.renderPosY, posZ + 0.949999988079071D - RenderManager.renderPosZ);
            }
          } else if (x1 == Blocks.trapped_chest) {
            mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806D - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + 0.05000000074505806D - RenderManager.renderPosZ, posX + 1.9500000476837158D - RenderManager.renderPosX, posY + 0.8999999761581421D - RenderManager.renderPosY, posZ + 0.949999988079071D - RenderManager.renderPosZ);
          } else if (z2 == Blocks.trapped_chest) {
            mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806D - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + 0.05000000074505806D - RenderManager.renderPosZ - 1.0D, posX + 0.949999988079071D - RenderManager.renderPosX, posY + 0.8999999761581421D - RenderManager.renderPosY, posZ + 0.949999988079071D - RenderManager.renderPosZ);
          } else if ((x1 != Blocks.trapped_chest) && (x2 != Blocks.trapped_chest) && (z1 != Blocks.trapped_chest) && (z2 != Blocks.trapped_chest)) {
            mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806D - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + 0.05000000074505806D - RenderManager.renderPosZ, posX + 0.949999988079071D - RenderManager.renderPosX, posY + 0.8999999761581421D - RenderManager.renderPosY, posZ + 0.949999988079071D - RenderManager.renderPosZ);
          }
        } else {
          mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();mc.getRenderManager();axisAlignedBB = new AxisAlignedBB(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ, posX + 1.0D - RenderManager.renderPosX, posY + 1.0D - RenderManager.renderPosY, posZ + 1.0D - RenderManager.renderPosZ);
        }
        if (axisAlignedBB != null) {
          float[] colors = getColorForTileEntity(tileEntity);
          GlStateManager.disableAlpha();
          GlStateManager.enableBlend();
          GlStateManager.blendFunc(770, 771);
          GlStateManager.disableTexture2D();
          GlStateManager.disableDepth();
          GL11.glEnable(2848);
          net.minecraft.client.renderer.RenderHelper.drawCompleteBox(axisAlignedBB, 1.0F, toRGBAHex(colors[0] / 255.0F, colors[1] / 255.0F, colors[2] / 255.0F, 0.1254902F), toRGBAHex(colors[0] / 255.0F, colors[1] / 255.0F, colors[2] / 255.0F, 1.0F));
          GL11.glDisable(2848);
          GlStateManager.enableDepth();
          GlStateManager.enableTexture2D();
          GlStateManager.enableBlend();
          GlStateManager.enableAlpha();
          
          AxisAlignedBB bb = null;
          if ((tileEntity instanceof TileEntityChest))
          {
            Block block = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
            Block posX1 = mc.theWorld.getBlockState(new BlockPos(posX + 1.0D, posY, posZ)).getBlock();
            Block posX2 = mc.theWorld.getBlockState(new BlockPos(posX - 1.0D, posY, posZ)).getBlock();
            Block posZ1 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ + 1.0D)).getBlock();
            Block posZ2 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ - 1.0D)).getBlock();
            if (block != Blocks.trapped_chest) {
              if (posX1 == Blocks.chest) {
                bb = new AxisAlignedBB(posX + 0.05000000074505806D, posY, posZ + 0.05000000074505806D, posX + 1.9500000476837158D, posY + 0.8999999761581421D, posZ + 0.949999988079071D);
              } else if (posZ2 == Blocks.chest) {
                bb = new AxisAlignedBB(posX + 0.05000000074505806D, posY, posZ + 0.05000000074505806D - 1.0D, posX + 0.949999988079071D, posY + 0.8999999761581421D, posZ + 0.949999988079071D);
              } else if ((posX1 != Blocks.chest) && (posX2 != Blocks.chest) && (posZ1 != Blocks.chest) && (posZ2 != Blocks.chest)) {
                bb = new AxisAlignedBB(posX + 0.05000000074505806D, posY, posZ + 0.05000000074505806D, posX + 0.949999988079071D, posY + 0.8999999761581421D, posZ + 0.949999988079071D);
              }
            } else if (posX1 == Blocks.trapped_chest) {
              bb = new AxisAlignedBB(posX + 0.05000000074505806D, posY, posZ + 0.05000000074505806D, posX + 1.9500000476837158D, posY + 0.8999999761581421D, posZ + 0.949999988079071D);
            } else if (posZ2 == Blocks.trapped_chest) {
              bb = new AxisAlignedBB(posX + 0.05000000074505806D, posY, posZ + 0.05000000074505806D - 1.0D, posX + 0.949999988079071D, posY + 0.8999999761581421D, posZ + 0.949999988079071D);
            } else if ((posX1 != Blocks.trapped_chest) && (posX2 != Blocks.trapped_chest) && (posZ1 != Blocks.trapped_chest) && (posZ2 != Blocks.trapped_chest)) {
              bb = new AxisAlignedBB(posX + 0.05000000074505806D, posY, posZ + 0.05000000074505806D, posX + 0.949999988079071D, posY + 0.8999999761581421D, posZ + 0.949999988079071D);
            }
          } else {
            bb = new AxisAlignedBB(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D);
          }
          if (bb == null) break;
          Vector3d[] corners = { new Vector3d(minX, minY, minZ), new Vector3d(maxX, maxY, maxZ), new Vector3d(minX, maxY, maxZ), new Vector3d(minX, minY, maxZ), new Vector3d(maxX, minY, maxZ), new Vector3d(maxX, minY, minZ), new Vector3d(maxX, maxY, minZ), new Vector3d(minX, maxY, minZ) };
          GLUProjection.Projection result = null;
          Vec4f transformed = new Vec4f(scaledResolution.getScaledWidth() * 2.0F, scaledResolution.getScaledHeight() * 2.0F, -1.0F, -1.0F);
          for (Vector3d vec : corners) {
            result = com.enjoytheban.utils.render.GLUProjection.getInstance().project(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ, com.enjoytheban.utils.render.GLUProjection.ClampMode.NONE, true);
            transformed.setX((float)Math.min(transformed.getX(), result.getX()));
            transformed.setY((float)Math.min(transformed.getY(), result.getY()));
            transformed.setW((float)Math.max(transformed.getW(), result.getX()));
            transformed.setH((float)Math.max(transformed.getH(), result.getY()));
          }
          if (result == null) break;
        }
      }
    }
  }
  
  public static boolean isStorage(TileEntity entity) { return ((entity instanceof TileEntityChest)) || ((entity instanceof net.minecraft.tileentity.TileEntityBrewingStand)) || ((entity instanceof net.minecraft.tileentity.TileEntityDropper)) || ((entity instanceof net.minecraft.tileentity.TileEntityDispenser)) || ((entity instanceof TileEntityFurnace)) || ((entity instanceof net.minecraft.tileentity.TileEntityHopper)) || ((entity instanceof net.minecraft.tileentity.TileEntityEnderChest)); }
  
  public static int toRGBAHex(float r, float g, float b, float a) {
    return ((int)(a * 255.0F) & 0xFF) << 24 | ((int)(r * 255.0F) & 0xFF) << 16 | ((int)(g * 255.0F) & 0xFF) << 8 | (int)(b * 255.0F) & 0xFF;
  }
  
  private float[] getColorForTileEntity(TileEntity entity) {
    if ((entity instanceof TileEntityChest)) {
      TileEntityChest chest = (TileEntityChest)entity;
      if (chest.getChestType() == 0) {
        return new float[] { 180.0F, 160.0F, 0.0F, 255.0F };
      }
      if (chest.getChestType() == 1) {
        return new float[] { 160.0F, 10.0F, 10.0F, 255.0F };
      }
    }
    if ((entity instanceof net.minecraft.tileentity.TileEntityEnderChest)) {
      return new float[] { 0.0F, 160.0F, 100.0F, 255.0F };
    }
    if ((entity instanceof TileEntityFurnace)) {
      return new float[] { 120.0F, 120.0F, 120.0F, 255.0F };
    }
    return new float[] { 255.0F, 255.0F, 255.0F, 255.0F };
  }
}
