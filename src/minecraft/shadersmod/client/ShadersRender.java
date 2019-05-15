package shadersmod.client;

import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumWorldBlockLayer;
import optifine.Reflector;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShadersRender
{
  public ShadersRender() {}
  
  public static void setFrustrumPosition(Frustrum frustrum, double x, double y, double z)
  {
    frustrum.setPosition(x, y, z);
  }
  
  public static void setupTerrain(RenderGlobal renderGlobal, Entity viewEntity, double partialTicks, net.minecraft.client.renderer.culling.ICamera camera, int frameCount, boolean playerSpectator)
  {
    renderGlobal.func_174970_a(viewEntity, partialTicks, camera, frameCount, playerSpectator);
  }
  
  public static void updateChunks(RenderGlobal renderGlobal, long finishTimeNano)
  {
    renderGlobal.func_174967_a(finishTimeNano);
  }
  
  public static void beginTerrainSolid()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.fogEnabled = true;
      Shaders.useProgram(7);
    }
  }
  
  public static void beginTerrainCutoutMipped()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.useProgram(7);
    }
  }
  
  public static void beginTerrainCutout()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.useProgram(7);
    }
  }
  
  public static void endTerrain()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.useProgram(3);
    }
  }
  
  public static void beginTranslucent()
  {
    if (Shaders.isRenderingWorld)
    {
      if (Shaders.usedDepthBuffers >= 2)
      {
        GlStateManager.setActiveTexture(33995);
        Shaders.checkGLError("pre copy depth");
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
        Shaders.checkGLError("copy depth");
        GlStateManager.setActiveTexture(33984);
      }
      
      Shaders.useProgram(12);
    }
  }
  
  public static void endTranslucent()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.useProgram(3);
    }
  }
  
  public static void renderHand0(EntityRenderer er, float par1, int par2)
  {
    if (!Shaders.isShadowPass)
    {
      net.minecraft.item.Item item = Shaders.itemToRender != null ? Shaders.itemToRender.getItem() : null;
      Block block = (item instanceof ItemBlock) ? ((ItemBlock)item).getBlock() : null;
      
      if ((!(item instanceof ItemBlock)) || (!(block instanceof Block)) || (block.getBlockLayer() == EnumWorldBlockLayer.SOLID))
      {
        Shaders.readCenterDepth();
        Shaders.beginHand();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        er.renderHand(par1, par2);
        Shaders.endHand();
        Shaders.isHandRendered = true;
      }
    }
  }
  
  public static void renderHand1(EntityRenderer er, float par1, int par2)
  {
    if ((!Shaders.isShadowPass) && (!Shaders.isHandRendered))
    {
      Shaders.readCenterDepth();
      GlStateManager.enableBlend();
      Shaders.beginHand();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      er.renderHand(par1, par2);
      Shaders.endHand();
      Shaders.isHandRendered = true;
    }
  }
  
  public static void renderItemFP(ItemRenderer itemRenderer, float par1)
  {
    GlStateManager.depthMask(true);
    GlStateManager.depthFunc(515);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    itemRenderer.renderItemInFirstPerson(par1);
  }
  
  public static void renderFPOverlay(EntityRenderer er, float par1, int par2)
  {
    if (!Shaders.isShadowPass)
    {
      Shaders.beginFPOverlay();
      er.renderHand(par1, par2);
      Shaders.endFPOverlay();
    }
  }
  
  public static void beginBlockDamage()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.useProgram(11);
      
      if (Shaders.programsID[11] == Shaders.programsID[7])
      {
        Shaders.setDrawBuffers(Shaders.drawBuffersColorAtt0);
        GlStateManager.depthMask(false);
      }
    }
  }
  
  public static void endBlockDamage()
  {
    if (Shaders.isRenderingWorld)
    {
      GlStateManager.depthMask(true);
      Shaders.useProgram(3);
    }
  }
  
  public static void renderShadowMap(EntityRenderer entityRenderer, int pass, float partialTicks, long finishTimeNano)
  {
    if ((Shaders.usedShadowDepthBuffers > 0) && (--Shaders.shadowPassCounter <= 0))
    {
      Minecraft mc = Minecraft.getMinecraft();
      mcProfiler.endStartSection("shadow pass");
      RenderGlobal renderGlobal = renderGlobal;
      Shaders.isShadowPass = true;
      Shaders.shadowPassCounter = Shaders.shadowPassInterval;
      Shaders.preShadowPassThirdPersonView = gameSettings.thirdPersonView;
      gameSettings.thirdPersonView = 1;
      Shaders.checkGLError("pre shadow");
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      mcProfiler.endStartSection("shadow clear");
      EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb);
      Shaders.checkGLError("shadow bind sfb");
      Shaders.useProgram(30);
      mcProfiler.endStartSection("shadow camera");
      entityRenderer.setupCameraTransform(partialTicks, 2);
      Shaders.setCameraShadow(partialTicks);
      net.minecraft.client.renderer.ActiveRenderInfo.updateRenderInfo(thePlayer, gameSettings.thirdPersonView == 2);
      Shaders.checkGLError("shadow camera");
      GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
      Shaders.checkGLError("shadow drawbuffers");
      GL11.glReadBuffer(0);
      Shaders.checkGLError("shadow readbuffer");
      EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
      
      if (Shaders.usedShadowColorBuffers != 0)
      {
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.sfbColorTextures.get(0), 0);
      }
      
      Shaders.checkFramebufferStatus("shadow fb");
      GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glClear(Shaders.usedShadowColorBuffers != 0 ? 16640 : 256);
      Shaders.checkGLError("shadow clear");
      mcProfiler.endStartSection("shadow frustum");
      net.minecraft.client.renderer.culling.ClippingHelper clippingHelper = ClippingHelperShadow.getInstance();
      mcProfiler.endStartSection("shadow culling");
      Frustrum frustum = new Frustrum(clippingHelper);
      Entity viewEntity = mc.func_175606_aa();
      double viewPosX = lastTickPosX + (posX - lastTickPosX) * partialTicks;
      double viewPosY = lastTickPosY + (posY - lastTickPosY) * partialTicks;
      double viewPosZ = lastTickPosZ + (posZ - lastTickPosZ) * partialTicks;
      frustum.setPosition(viewPosX, viewPosY, viewPosZ);
      GlStateManager.shadeModel(7425);
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.disableCull();
      mcProfiler.endStartSection("shadow prepareterrain");
      mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      mcProfiler.endStartSection("shadow setupterrain");
      boolean frameCount = false;
      int var17 = field_175084_ae;
      field_175084_ae = (var17 + 1);
      renderGlobal.func_174970_a(viewEntity, partialTicks, frustum, var17, thePlayer.func_175149_v());
      mcProfiler.endStartSection("shadow updatechunks");
      mcProfiler.endStartSection("shadow terrain");
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      renderGlobal.func_174977_a(EnumWorldBlockLayer.SOLID, partialTicks, 2, viewEntity);
      Shaders.checkGLError("shadow terrain solid");
      GlStateManager.enableAlpha();
      renderGlobal.func_174977_a(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, 2, viewEntity);
      Shaders.checkGLError("shadow terrain cutoutmipped");
      mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).func_174936_b(false, false);
      renderGlobal.func_174977_a(EnumWorldBlockLayer.CUTOUT, partialTicks, 2, viewEntity);
      Shaders.checkGLError("shadow terrain cutout");
      mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).func_174935_a();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      mcProfiler.endStartSection("shadow entities");
      
      if (Reflector.ForgeHooksClient_setRenderPass.exists())
      {
        Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(0) });
      }
      
      renderGlobal.func_180446_a(viewEntity, frustum, partialTicks);
      Shaders.checkGLError("shadow entities");
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      
      if (Shaders.usedShadowDepthBuffers >= 2)
      {
        GlStateManager.setActiveTexture(33989);
        Shaders.checkGLError("pre copy shadow depth");
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
        Shaders.checkGLError("copy shadow depth");
        GlStateManager.setActiveTexture(33984);
      }
      
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.shadeModel(7425);
      Shaders.checkGLError("shadow pre-translucent");
      GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
      Shaders.checkGLError("shadow drawbuffers pre-translucent");
      Shaders.checkFramebufferStatus("shadow pre-translucent");
      mcProfiler.endStartSection("shadow translucent");
      renderGlobal.func_174977_a(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, 2, viewEntity);
      Shaders.checkGLError("shadow translucent");
      
      if (Reflector.ForgeHooksClient_setRenderPass.exists())
      {
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        Reflector.call(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(1) });
        renderGlobal.func_180446_a(viewEntity, frustum, partialTicks);
        Reflector.call(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(-1) });
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Shaders.checkGLError("shadow entities 1");
      }
      
      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GL11.glFlush();
      Shaders.checkGLError("shadow flush");
      Shaders.isShadowPass = false;
      gameSettings.thirdPersonView = Shaders.preShadowPassThirdPersonView;
      mcProfiler.endStartSection("shadow postprocess");
      
      if (Shaders.hasGlGenMipmap)
      {
        if (Shaders.usedShadowDepthBuffers >= 1)
        {
          if (Shaders.shadowMipmapEnabled[0] != 0)
          {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(0));
            GL30.glGenerateMipmap(3553);
            GL11.glTexParameteri(3553, 10241, Shaders.shadowFilterNearest[0] != 0 ? 9984 : 9987);
          }
          
          if ((Shaders.usedShadowDepthBuffers >= 2) && (Shaders.shadowMipmapEnabled[1] != 0))
          {
            GlStateManager.setActiveTexture(33989);
            GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(1));
            GL30.glGenerateMipmap(3553);
            GL11.glTexParameteri(3553, 10241, Shaders.shadowFilterNearest[1] != 0 ? 9984 : 9987);
          }
          
          GlStateManager.setActiveTexture(33984);
        }
        
        if (Shaders.usedShadowColorBuffers >= 1)
        {
          if (Shaders.shadowColorMipmapEnabled[0] != 0)
          {
            GlStateManager.setActiveTexture(33997);
            GlStateManager.func_179144_i(Shaders.sfbColorTextures.get(0));
            GL30.glGenerateMipmap(3553);
            GL11.glTexParameteri(3553, 10241, Shaders.shadowColorFilterNearest[0] != 0 ? 9984 : 9987);
          }
          
          if ((Shaders.usedShadowColorBuffers >= 2) && (Shaders.shadowColorMipmapEnabled[1] != 0))
          {
            GlStateManager.setActiveTexture(33998);
            GlStateManager.func_179144_i(Shaders.sfbColorTextures.get(1));
            GL30.glGenerateMipmap(3553);
            GL11.glTexParameteri(3553, 10241, Shaders.shadowColorFilterNearest[1] != 0 ? 9984 : 9987);
          }
          
          GlStateManager.setActiveTexture(33984);
        }
      }
      
      Shaders.checkGLError("shadow postprocess");
      EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
      GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
      Shaders.activeDrawBuffers = null;
      mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      Shaders.useProgram(7);
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      Shaders.checkGLError("shadow end");
    }
  }
  
  public static void preRenderChunkLayer()
  {
    if (OpenGlHelper.func_176075_f())
    {
      GL11.glEnableClientState(32885);
      GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
      GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
      GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
    }
  }
  
  public static void postRenderChunkLayer()
  {
    if (OpenGlHelper.func_176075_f())
    {
      GL11.glDisableClientState(32885);
      GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
      GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
      GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
    }
  }
  
  public static void setupArrayPointersVbo()
  {
    boolean vertexSizeI = true;
    GL11.glVertexPointer(3, 5126, 56, 0L);
    GL11.glColorPointer(4, 5121, 56, 12L);
    GL11.glTexCoordPointer(2, 5126, 56, 16L);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glTexCoordPointer(2, 5122, 56, 24L);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    GL11.glNormalPointer(5120, 56, 28L);
    GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, 56, 32L);
    GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, 56, 40L);
    GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, 56, 48L);
  }
  
  public static void beaconBeamBegin()
  {
    Shaders.useProgram(14);
  }
  

  public static void beaconBeamStartQuad1() {}
  

  public static void beaconBeamStartQuad2() {}
  

  public static void beaconBeamDraw1() {}
  
  public static void beaconBeamDraw2() {}
  
  public static void layerArmorBaseDrawEnchantedGlintBegin()
  {
    Shaders.useProgram(17);
  }
  
  public static void layerArmorBaseDrawEnchantedGlintEnd()
  {
    if (Shaders.isRenderingWorld)
    {
      Shaders.useProgram(16);
    }
    else
    {
      Shaders.useProgram(0);
    }
  }
}
