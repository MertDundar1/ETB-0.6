package net.minecraft.client.renderer;

import com.enjoytheban.Client;
import com.enjoytheban.module.modules.render.ESP;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.ClassInheratanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.EnumBorderStatus;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import optifine.ChunkUtils;
import optifine.CloudRenderer;
import optifine.Config;
import optifine.CustomColors;
import optifine.CustomSky;
import optifine.DynamicLights;
import optifine.Lagometer;
import optifine.Lagometer.TimerNano;
import optifine.RandomMobs;
import optifine.Reflector;
import optifine.ReflectorMethod;
import optifine.RenderInfoLazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener
{
  private static final Logger logger = ;
  private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
  private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
  private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
  private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
  private static final ResourceLocation field_175006_g = new ResourceLocation("textures/misc/forcefield.png");
  
  public final Minecraft mc;
  
  private final TextureManager renderEngine;
  
  private final RenderManager field_175010_j;
  
  private WorldClient theWorld;
  private Set field_175009_l = Sets.newLinkedHashSet();
  

  private List glRenderLists = com.google.common.collect.Lists.newArrayListWithCapacity(69696);
  
  private ViewFrustum field_175008_n;
  
  private int starGLCallList = -1;
  

  private int glSkyList = -1;
  

  private int glSkyList2 = -1;
  

  private VertexFormat field_175014_r;
  

  private VertexBuffer field_175013_s;
  

  private VertexBuffer field_175012_t;
  
  private VertexBuffer field_175011_u;
  
  private int cloudTickCounter;
  
  public final Map damagedBlocks = Maps.newHashMap();
  

  private final Map mapSoundPositions = Maps.newHashMap();
  private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
  private Framebuffer field_175015_z;
  private ShaderGroup field_174991_A;
  private double field_174992_B = Double.MIN_VALUE;
  private double field_174993_C = Double.MIN_VALUE;
  private double field_174987_D = Double.MIN_VALUE;
  private int field_174988_E = Integer.MIN_VALUE;
  private int field_174989_F = Integer.MIN_VALUE;
  private int field_174990_G = Integer.MIN_VALUE;
  private double field_174997_H = Double.MIN_VALUE;
  private double field_174998_I = Double.MIN_VALUE;
  private double field_174999_J = Double.MIN_VALUE;
  private double field_175000_K = Double.MIN_VALUE;
  private double field_174994_L = Double.MIN_VALUE;
  private final ChunkRenderDispatcher field_174995_M = new ChunkRenderDispatcher();
  private ChunkRenderContainer field_174996_N;
  private int renderDistanceChunks = -1;
  

  private int renderEntitiesStartupCounter = 2;
  

  private int countEntitiesTotal;
  

  private int countEntitiesRendered;
  
  private int countEntitiesHidden;
  
  private boolean field_175002_T = false;
  private ClippingHelper field_175001_U;
  private final Vector4f[] field_175004_V = new Vector4f[8];
  private final Vector3d field_175003_W = new Vector3d();
  private boolean field_175005_X = false;
  net.minecraft.client.renderer.chunk.IRenderChunkFactory field_175007_a;
  private double prevRenderSortX;
  private double prevRenderSortY;
  private double prevRenderSortZ;
  public boolean displayListEntitiesDirty = true;
  private static final String __OBFID = "CL_00000954";
  private CloudRenderer cloudRenderer;
  public Entity renderedEntity;
  public Set chunksToResortTransparency = new LinkedHashSet();
  public Set chunksToUpdateForced = new LinkedHashSet();
  private Deque visibilityDeque = new ArrayDeque();
  private List renderInfosEntities = new ArrayList(1024);
  private List renderInfosTileEntities = new ArrayList(1024);
  private List renderInfosNormal = new ArrayList(1024);
  private List renderInfosEntitiesNormal = new ArrayList(1024);
  private List renderInfosTileEntitiesNormal = new ArrayList(1024);
  private List renderInfosShadow = new ArrayList(1024);
  private List renderInfosEntitiesShadow = new ArrayList(1024);
  private List renderInfosTileEntitiesShadow = new ArrayList(1024);
  private int renderDistance = 0;
  private int renderDistanceSq = 0;
  private static final Set SET_ALL_FACINGS = java.util.Collections.unmodifiableSet(new java.util.HashSet(Arrays.asList(EnumFacing.VALUES)));
  private int countTileEntitiesRendered;
  
  public RenderGlobal(Minecraft mcIn)
  {
    cloudRenderer = new CloudRenderer(mcIn);
    mc = mcIn;
    field_175010_j = mcIn.getRenderManager();
    renderEngine = mcIn.getTextureManager();
    renderEngine.bindTexture(field_175006_g);
    GL11.glTexParameteri(3553, 10242, 10497);
    GL11.glTexParameteri(3553, 10243, 10497);
    GlStateManager.func_179144_i(0);
    func_174971_n();
    field_175005_X = OpenGlHelper.func_176075_f();
    
    if (field_175005_X)
    {
      field_174996_N = new VboRenderList();
      field_175007_a = new VboChunkFactory();
    }
    else
    {
      field_174996_N = new RenderList();
      field_175007_a = new ListChunkFactory();
    }
    
    field_175014_r = new VertexFormat();
    field_175014_r.func_177349_a(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
    func_174963_q();
    func_174980_p();
    func_174964_o();
  }
  
  public void onResourceManagerReload(IResourceManager resourceManager)
  {
    func_174971_n();
  }
  
  private void func_174971_n()
  {
    TextureMap var1 = mc.getTextureMapBlocks();
    
    for (int var2 = 0; var2 < destroyBlockIcons.length; var2++)
    {
      destroyBlockIcons[var2] = var1.getAtlasSprite("minecraft:blocks/destroy_stage_" + var2);
    }
  }
  
  public void func_174966_b()
  {
    if (OpenGlHelper.shadersSupported)
    {
      if (ShaderLinkHelper.getStaticShaderLinkHelper() == null)
      {
        ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }
      
      ResourceLocation var1 = new ResourceLocation("shaders/post/entity_outline.json");
      
      try
      {
        field_174991_A = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), var1);
        field_174991_A.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
        field_175015_z = field_174991_A.func_177066_a("final");
      }
      catch (IOException var3)
      {
        logger.warn("Failed to load shader: " + var1, var3);
        field_174991_A = null;
        field_175015_z = null;
      }
      catch (JsonSyntaxException var4)
      {
        logger.warn("Failed to load shader: " + var1, var4);
        field_174991_A = null;
        field_175015_z = null;
      }
    }
    else
    {
      field_174991_A = null;
      field_175015_z = null;
    }
  }
  
  public void func_174975_c()
  {
    if (func_174985_d())
    {
      mc.gameSettings.ofFastRender = false;
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 769, 1, 1);
      
      field_175015_z.func_178038_a(mc.displayWidth, mc.displayHeight, false);
      GlStateManager.disableBlend();
    }
  }
  
  protected boolean func_174985_d()
  {
    ESP esp = (ESP)Client.instance.getModuleManager().getModuleByClass(ESP.class);
    
    return false;
  }
  
  private void func_174964_o()
  {
    Tessellator var1 = Tessellator.getInstance();
    WorldRenderer var2 = var1.getWorldRenderer();
    
    if (field_175011_u != null)
    {
      field_175011_u.func_177362_c();
    }
    
    if (glSkyList2 >= 0)
    {
      GLAllocation.deleteDisplayLists(glSkyList2);
      glSkyList2 = -1;
    }
    
    if (field_175005_X)
    {
      field_175011_u = new VertexBuffer(field_175014_r);
      func_174968_a(var2, -16.0F, true);
      var2.draw();
      var2.reset();
      field_175011_u.func_177360_a(var2.func_178966_f(), var2.func_178976_e());
    }
    else
    {
      glSkyList2 = GLAllocation.generateDisplayLists(1);
      GL11.glNewList(glSkyList2, 4864);
      func_174968_a(var2, -16.0F, true);
      var1.draw();
      GL11.glEndList();
    }
  }
  
  private void func_174980_p()
  {
    Tessellator var1 = Tessellator.getInstance();
    WorldRenderer var2 = var1.getWorldRenderer();
    
    if (field_175012_t != null)
    {
      field_175012_t.func_177362_c();
    }
    
    if (glSkyList >= 0)
    {
      GLAllocation.deleteDisplayLists(glSkyList);
      glSkyList = -1;
    }
    
    if (field_175005_X)
    {
      field_175012_t = new VertexBuffer(field_175014_r);
      func_174968_a(var2, 16.0F, false);
      var2.draw();
      var2.reset();
      field_175012_t.func_177360_a(var2.func_178966_f(), var2.func_178976_e());
    }
    else
    {
      glSkyList = GLAllocation.generateDisplayLists(1);
      GL11.glNewList(glSkyList, 4864);
      func_174968_a(var2, 16.0F, false);
      var1.draw();
      GL11.glEndList();
    }
  }
  
  private void func_174968_a(WorldRenderer worldRendererIn, float p_174968_2_, boolean p_174968_3_)
  {
    boolean var4 = true;
    boolean var5 = true;
    worldRendererIn.startDrawingQuads();
    
    for (int var6 = 65152; var6 <= 384; var6 += 64)
    {
      for (int var7 = 65152; var7 <= 384; var7 += 64)
      {
        float var8 = var6;
        float var9 = var6 + 64;
        
        if (p_174968_3_)
        {
          var9 = var6;
          var8 = var6 + 64;
        }
        
        worldRendererIn.addVertex(var8, p_174968_2_, var7);
        worldRendererIn.addVertex(var9, p_174968_2_, var7);
        worldRendererIn.addVertex(var9, p_174968_2_, var7 + 64);
        worldRendererIn.addVertex(var8, p_174968_2_, var7 + 64);
      }
    }
  }
  
  private void func_174963_q()
  {
    Tessellator var1 = Tessellator.getInstance();
    WorldRenderer var2 = var1.getWorldRenderer();
    
    if (field_175013_s != null)
    {
      field_175013_s.func_177362_c();
    }
    
    if (starGLCallList >= 0)
    {
      GLAllocation.deleteDisplayLists(starGLCallList);
      starGLCallList = -1;
    }
    
    if (field_175005_X)
    {
      field_175013_s = new VertexBuffer(field_175014_r);
      func_180444_a(var2);
      var2.draw();
      var2.reset();
      field_175013_s.func_177360_a(var2.func_178966_f(), var2.func_178976_e());
    }
    else
    {
      starGLCallList = GLAllocation.generateDisplayLists(1);
      GlStateManager.pushMatrix();
      GL11.glNewList(starGLCallList, 4864);
      func_180444_a(var2);
      var1.draw();
      GL11.glEndList();
      GlStateManager.popMatrix();
    }
  }
  
  private void func_180444_a(WorldRenderer worldRendererIn)
  {
    Random var2 = new Random(10842L);
    worldRendererIn.startDrawingQuads();
    
    for (int var3 = 0; var3 < 1500; var3++)
    {
      double var4 = var2.nextFloat() * 2.0F - 1.0F;
      double var6 = var2.nextFloat() * 2.0F - 1.0F;
      double var8 = var2.nextFloat() * 2.0F - 1.0F;
      double var10 = 0.15F + var2.nextFloat() * 0.1F;
      double var12 = var4 * var4 + var6 * var6 + var8 * var8;
      
      if ((var12 < 1.0D) && (var12 > 0.01D))
      {
        var12 = 1.0D / Math.sqrt(var12);
        var4 *= var12;
        var6 *= var12;
        var8 *= var12;
        double var14 = var4 * 100.0D;
        double var16 = var6 * 100.0D;
        double var18 = var8 * 100.0D;
        double var20 = Math.atan2(var4, var8);
        double var22 = Math.sin(var20);
        double var24 = Math.cos(var20);
        double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
        double var28 = Math.sin(var26);
        double var30 = Math.cos(var26);
        double var32 = var2.nextDouble() * 3.141592653589793D * 2.0D;
        double var34 = Math.sin(var32);
        double var36 = Math.cos(var32);
        
        for (int var38 = 0; var38 < 4; var38++)
        {
          double var39 = 0.0D;
          double var41 = ((var38 & 0x2) - 1) * var10;
          double var43 = ((var38 + 1 & 0x2) - 1) * var10;
          double var45 = 0.0D;
          double var47 = var41 * var36 - var43 * var34;
          double var49 = var43 * var36 + var41 * var34;
          double var53 = var47 * var28 + 0.0D * var30;
          double var55 = 0.0D * var28 - var47 * var30;
          double var57 = var55 * var22 - var49 * var24;
          double var61 = var49 * var22 + var55 * var24;
          worldRendererIn.addVertex(var14 + var57, var16 + var53, var18 + var61);
        }
      }
    }
  }
  



  public void setWorldAndLoadRenderers(WorldClient worldClientIn)
  {
    if (theWorld != null)
    {
      theWorld.removeWorldAccess(this);
    }
    
    field_174992_B = Double.MIN_VALUE;
    field_174993_C = Double.MIN_VALUE;
    field_174987_D = Double.MIN_VALUE;
    field_174988_E = Integer.MIN_VALUE;
    field_174989_F = Integer.MIN_VALUE;
    field_174990_G = Integer.MIN_VALUE;
    field_175010_j.set(worldClientIn);
    theWorld = worldClientIn;
    
    if (Config.isDynamicLights())
    {
      DynamicLights.clear();
    }
    
    if (worldClientIn != null)
    {
      worldClientIn.addWorldAccess(this);
      loadRenderers();
    }
  }
  



  public void loadRenderers()
  {
    if (theWorld != null)
    {
      displayListEntitiesDirty = true;
      Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
      Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
      BlockModelRenderer.updateAoLightValue();
      
      if (Config.isDynamicLights())
      {
        DynamicLights.clear();
      }
      
      renderDistanceChunks = mc.gameSettings.renderDistanceChunks;
      renderDistance = (renderDistanceChunks * 16);
      renderDistanceSq = (renderDistance * renderDistance);
      boolean var1 = field_175005_X;
      field_175005_X = OpenGlHelper.func_176075_f();
      
      if ((var1) && (!field_175005_X))
      {
        field_174996_N = new RenderList();
        field_175007_a = new ListChunkFactory();
      }
      else if ((!var1) && (field_175005_X))
      {
        field_174996_N = new VboRenderList();
        field_175007_a = new VboChunkFactory();
      }
      
      if (var1 != field_175005_X)
      {
        func_174963_q();
        func_174980_p();
        func_174964_o();
      }
      
      if (field_175008_n != null)
      {
        field_175008_n.func_178160_a();
      }
      
      func_174986_e();
      field_175008_n = new ViewFrustum(theWorld, mc.gameSettings.renderDistanceChunks, this, field_175007_a);
      
      if (theWorld != null)
      {
        Entity var2 = mc.func_175606_aa();
        
        if (var2 != null)
        {
          field_175008_n.func_178163_a(posX, posZ);
        }
      }
      
      renderEntitiesStartupCounter = 2;
    }
  }
  
  protected void func_174986_e()
  {
    field_175009_l.clear();
    field_174995_M.func_178514_b();
  }
  
  public void checkOcclusionQueryResult(int p_72720_1_, int p_72720_2_)
  {
    if ((OpenGlHelper.shadersSupported) && (field_174991_A != null))
    {
      field_174991_A.createBindFramebuffers(p_72720_1_, p_72720_2_);
    }
  }
  
  public void func_180446_a(Entity p_180446_1_, ICamera p_180446_2_, float partialTicks)
  {
    int pass = 0;
    
    if (Reflector.MinecraftForgeClient_getRenderPass.exists())
    {
      pass = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass, new Object[0]);
    }
    
    if (renderEntitiesStartupCounter > 0)
    {
      if (pass > 0)
      {
        return;
      }
      
      renderEntitiesStartupCounter -= 1;
    }
    else
    {
      double var4 = prevPosX + (posX - prevPosX) * partialTicks;
      double var6 = prevPosY + (posY - prevPosY) * partialTicks;
      double var8 = prevPosZ + (posZ - prevPosZ) * partialTicks;
      theWorld.theProfiler.startSection("prepare");
      TileEntityRendererDispatcher.instance.func_178470_a(theWorld, mc.getTextureManager(), mc.fontRendererObj, mc.func_175606_aa(), partialTicks);
      field_175010_j.func_180597_a(theWorld, mc.fontRendererObj, mc.func_175606_aa(), mc.pointedEntity, mc.gameSettings, partialTicks);
      
      if (pass == 0)
      {
        countEntitiesTotal = 0;
        countEntitiesRendered = 0;
        countEntitiesHidden = 0;
        countTileEntitiesRendered = 0;
      }
      
      Entity var10 = mc.func_175606_aa();
      double var11 = lastTickPosX + (posX - lastTickPosX) * partialTicks;
      double var13 = lastTickPosY + (posY - lastTickPosY) * partialTicks;
      double var15 = lastTickPosZ + (posZ - lastTickPosZ) * partialTicks;
      TileEntityRendererDispatcher.staticPlayerX = var11;
      TileEntityRendererDispatcher.staticPlayerY = var13;
      TileEntityRendererDispatcher.staticPlayerZ = var15;
      field_175010_j.func_178628_a(var11, var13, var15);
      mc.entityRenderer.func_180436_i();
      theWorld.theProfiler.endStartSection("global");
      List var17 = theWorld.getLoadedEntityList();
      
      if (pass == 0)
      {
        countEntitiesTotal = var17.size();
      }
      
      if ((Config.isFogOff()) && (mc.entityRenderer.fogStandard))
      {
        GlStateManager.disableFog();
      }
      
      boolean forgeEntityPass = Reflector.ForgeEntity_shouldRenderInPass.exists();
      boolean forgeTileEntityPass = Reflector.ForgeTileEntity_shouldRenderInPass.exists();
      


      for (int var18 = 0; var18 < theWorld.weatherEffects.size(); var18++)
      {
        Entity var19 = (Entity)theWorld.weatherEffects.get(var18);
        
        if (forgeEntityPass) { if (!Reflector.callBoolean(var19, Reflector.ForgeEntity_shouldRenderInPass, new Object[] { Integer.valueOf(pass) })) {}
        } else {
          countEntitiesRendered += 1;
          
          if (var19.isInRangeToRender3d(var4, var6, var8))
          {
            field_175010_j.renderEntitySimple(var19, partialTicks);
          }
        }
      }
      


      if (func_174985_d())
      {
        GlStateManager.depthFunc(519);
        GlStateManager.disableFog();
        field_175015_z.framebufferClear();
        field_175015_z.bindFramebuffer(false);
        theWorld.theProfiler.endStartSection("entityOutlines");
        RenderHelper.disableStandardItemLighting();
        field_175010_j.func_178632_c(true);
        
        for (var18 = 0; var18 < var17.size(); var18++)
        {
          Entity var19 = (Entity)var17.get(var18);
          
          if (forgeEntityPass) { if (!Reflector.callBoolean(var19, Reflector.ForgeEntity_shouldRenderInPass, new Object[] { Integer.valueOf(pass) })) {}
          } else {
            boolean isShaders = ((mc.func_175606_aa() instanceof EntityLivingBase)) && (((EntityLivingBase)mc.func_175606_aa()).isPlayerSleeping());
            boolean var25 = (var19.isInRangeToRender3d(var4, var6, var8)) && ((ignoreFrustumCheck) || (p_180446_2_.isBoundingBoxInFrustum(var19.getEntityBoundingBox())) || (riddenByEntity == mc.thePlayer)) && ((var19 instanceof EntityPlayer));
            
            if (((var19 != mc.func_175606_aa()) || (mc.gameSettings.thirdPersonView != 0) || (isShaders)) && (var25))
            {
              field_175010_j.renderEntitySimple(var19, partialTicks);
            }
          }
        }
        
        field_175010_j.func_178632_c(false);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.depthMask(false);
        field_174991_A.loadShaderGroup(partialTicks);
        GlStateManager.depthMask(true);
        mc.getFramebuffer().bindFramebuffer(false);
        GlStateManager.enableFog();
        GlStateManager.depthFunc(515);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
      }
      
      theWorld.theProfiler.endStartSection("entities");
      boolean isShaders = Config.isShaders();
      
      if (isShaders)
      {
        Shaders.beginEntities();
      }
      
      Iterator var35 = renderInfosEntities.iterator();
      boolean oldFancyGraphics = mc.gameSettings.fancyGraphics;
      mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
      Iterator var32;
      for (; 
          var35.hasNext(); 
          




          var32.hasNext())
      {
        ContainerLocalRenderInformation var26 = (ContainerLocalRenderInformation)var35.next();
        Chunk fontRenderer = theWorld.getChunkFromBlockCoords(field_178036_a.func_178568_j());
        var32 = fontRenderer.getEntityLists()[(field_178036_a.func_178568_j().getY() / 16)].iterator();
        
        continue;
        
        Entity var27 = (Entity)var32.next();
        
        if (forgeEntityPass) { if (!Reflector.callBoolean(var27, Reflector.ForgeEntity_shouldRenderInPass, new Object[] { Integer.valueOf(pass) })) {}
        } else {
          boolean var30 = (field_175010_j.func_178635_a(var27, p_180446_2_, var4, var6, var8)) || (riddenByEntity == mc.thePlayer);
          
          if (var30)
          {
            boolean var34 = (mc.func_175606_aa() instanceof EntityLivingBase) ? ((EntityLivingBase)mc.func_175606_aa()).isPlayerSleeping() : false;
            
            if (((var27 != mc.func_175606_aa()) || (mc.gameSettings.thirdPersonView != 0) || (var34)) && ((posY < 0.0D) || (posY >= 256.0D) || (theWorld.isBlockLoaded(new BlockPos(var27)))))
            {



              countEntitiesRendered += 1;
              
              if (var27.getClass() == net.minecraft.entity.item.EntityItemFrame.class)
              {
                renderDistanceWeight = 0.06D;
              }
              
              renderedEntity = var27;
              
              if (isShaders)
              {
                Shaders.nextEntity(var27);
              }
              
              field_175010_j.renderEntitySimple(var27, partialTicks);
              renderedEntity = null;
            }
          }
          else if ((!var30) && ((var27 instanceof net.minecraft.entity.projectile.EntityWitherSkull)))
          {
            if (isShaders)
            {
              Shaders.nextEntity(var27);
            }
            
            mc.getRenderManager().func_178630_b(var27, partialTicks);
          }
        }
      }
      

      mc.gameSettings.fancyGraphics = oldFancyGraphics;
      net.minecraft.client.gui.FontRenderer var36 = TileEntityRendererDispatcher.instance.getFontRenderer();
      
      if (isShaders)
      {
        Shaders.endEntities();
        Shaders.beginBlockEntities();
      }
      
      theWorld.theProfiler.endStartSection("blockentities");
      RenderHelper.enableStandardItemLighting();
      
      if (Reflector.ForgeTileEntityRendererDispatcher_preDrawBatch.exists())
      {
        Reflector.call(TileEntityRendererDispatcher.instance, Reflector.ForgeTileEntityRendererDispatcher_preDrawBatch, new Object[0]);
      }
      
      var35 = renderInfosTileEntities.iterator();
      Iterator var38;
      for (; 
          var35.hasNext(); 
          



          var38.hasNext())
      {
        ContainerLocalRenderInformation var26 = (ContainerLocalRenderInformation)var35.next();
        var38 = field_178036_a.func_178571_g().func_178485_b().iterator();
        
        continue;
        
        TileEntity var37 = (TileEntity)var38.next();
        
        if (forgeTileEntityPass)
        {
          if (Reflector.callBoolean(var37, Reflector.ForgeTileEntity_shouldRenderInPass, new Object[] { Integer.valueOf(pass) }))
          {


            AxisAlignedBB var40 = (AxisAlignedBB)Reflector.call(var37, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0]);
            
            if ((var40 != null) && (!p_180446_2_.isBoundingBoxInFrustum(var40))) {}
          }
          
        }
        else
        {
          Class var42 = var37.getClass();
          
          if ((var42 == TileEntitySign.class) && (!Config.zoomMode))
          {
            EntityPlayerSP shouldRender = mc.thePlayer;
            double tileEntity = var37.getDistanceSq(posX, posY, posZ);
            
            if (tileEntity > 256.0D)
            {
              enabled = false;
            }
          }
          
          if (isShaders)
          {
            Shaders.nextBlockEntity(var37);
          }
          
          TileEntityRendererDispatcher.instance.func_180546_a(var37, partialTicks, -1);
          countTileEntitiesRendered += 1;
          enabled = true;
        }
      }
      
      if (Reflector.ForgeTileEntityRendererDispatcher_drawBatch.exists())
      {
        Reflector.call(TileEntityRendererDispatcher.instance, Reflector.ForgeTileEntityRendererDispatcher_drawBatch, new Object[] { Integer.valueOf(pass) });
      }
      
      func_180443_s();
      var35 = damagedBlocks.values().iterator();
      
      while (var35.hasNext())
      {
        DestroyBlockProgress var39 = (DestroyBlockProgress)var35.next();
        BlockPos var41 = var39.func_180246_b();
        TileEntity var37 = theWorld.getTileEntity(var41);
        
        if ((var37 instanceof TileEntityChest))
        {
          TileEntityChest var43 = (TileEntityChest)var37;
          
          if (adjacentChestXNeg != null)
          {
            var41 = var41.offset(EnumFacing.WEST);
            var37 = theWorld.getTileEntity(var41);
          }
          else if (adjacentChestZNeg != null)
          {
            var41 = var41.offset(EnumFacing.NORTH);
            var37 = theWorld.getTileEntity(var41);
          }
        }
        
        Block var44 = theWorld.getBlockState(var41).getBlock();
        
        boolean var45;
        if (forgeTileEntityPass)
        {
          boolean var45 = false;
          
          if (var37 != null) if ((Reflector.callBoolean(var37, Reflector.ForgeTileEntity_shouldRenderInPass, new Object[] { Integer.valueOf(pass) })) && (Reflector.callBoolean(var37, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0])))
            {
              AxisAlignedBB aabb = (AxisAlignedBB)Reflector.call(var37, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0]);
              
              if (aabb != null)
              {
                var45 = p_180446_2_.isBoundingBoxInFrustum(aabb);
              }
            }
        }
        else
        {
          var45 = (var37 != null) && (((var44 instanceof BlockChest)) || ((var44 instanceof BlockEnderChest)) || ((var44 instanceof BlockSign)) || ((var44 instanceof BlockSkull)));
        }
        
        if (var45)
        {
          if (isShaders)
          {
            Shaders.nextBlockEntity(var37);
          }
          
          TileEntityRendererDispatcher.instance.func_180546_a(var37, partialTicks, var39.getPartialBlockDamage());
        }
      }
      
      func_174969_t();
      mc.entityRenderer.func_175072_h();
      mc.mcProfiler.endSection();
    }
  }
  



  public String getDebugInfoRenders()
  {
    int var1 = field_175008_n.field_178164_f.length;
    int var2 = 0;
    Iterator var3 = glRenderLists.iterator();
    
    while (var3.hasNext())
    {
      ContainerLocalRenderInformation var4 = (ContainerLocalRenderInformation)var3.next();
      CompiledChunk var5 = field_178036_a.field_178590_b;
      
      if ((var5 != CompiledChunk.field_178502_a) && (!var5.func_178489_a()))
      {
        var2++;
      }
    }
    
    return String.format("C: %d/%d %sD: %d, %s", new Object[] { Integer.valueOf(var2), Integer.valueOf(var1), mc.field_175612_E ? "(s) " : "", Integer.valueOf(renderDistanceChunks), field_174995_M.func_178504_a() });
  }
  



  public String getDebugInfoEntities()
  {
    return "E: " + countEntitiesRendered + "/" + countEntitiesTotal + ", B: " + countEntitiesHidden + ", I: " + (countEntitiesTotal - countEntitiesHidden - countEntitiesRendered) + ", " + Config.getVersionDebug();
  }
  
  public void func_174970_a(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator)
  {
    if (mc.gameSettings.renderDistanceChunks != renderDistanceChunks)
    {
      loadRenderers();
    }
    
    theWorld.theProfiler.startSection("camera");
    double var7 = posX - field_174992_B;
    double var9 = posY - field_174993_C;
    double var11 = posZ - field_174987_D;
    
    if ((field_174988_E != chunkCoordX) || (field_174989_F != chunkCoordY) || (field_174990_G != chunkCoordZ) || (var7 * var7 + var9 * var9 + var11 * var11 > 16.0D))
    {
      field_174992_B = posX;
      field_174993_C = posY;
      field_174987_D = posZ;
      field_174988_E = chunkCoordX;
      field_174989_F = chunkCoordY;
      field_174990_G = chunkCoordZ;
      field_175008_n.func_178163_a(posX, posZ);
    }
    
    if (Config.isDynamicLights())
    {
      DynamicLights.update(this);
    }
    
    theWorld.theProfiler.endStartSection("renderlistcamera");
    double var13 = lastTickPosX + (posX - lastTickPosX) * partialTicks;
    double var15 = lastTickPosY + (posY - lastTickPosY) * partialTicks;
    double var17 = lastTickPosZ + (posZ - lastTickPosZ) * partialTicks;
    field_174996_N.func_178004_a(var13, var15, var17);
    theWorld.theProfiler.endStartSection("cull");
    
    if (field_175001_U != null)
    {
      Frustrum var35 = new Frustrum(field_175001_U);
      var35.setPosition(field_175003_W.x, field_175003_W.y, field_175003_W.z);
      camera = var35;
    }
    
    mc.mcProfiler.endStartSection("culling");
    BlockPos var351 = new BlockPos(var13, var15 + viewEntity.getEyeHeight(), var17);
    RenderChunk var20 = field_175008_n.func_178161_a(var351);
    BlockPos var21 = new BlockPos(MathHelper.floor_double(var13) / 16 * 16, MathHelper.floor_double(var15) / 16 * 16, MathHelper.floor_double(var17) / 16 * 16);
    displayListEntitiesDirty = ((displayListEntitiesDirty) || (!field_175009_l.isEmpty()) || (posX != field_174997_H) || (posY != field_174998_I) || (posZ != field_174999_J) || (rotationPitch != field_175000_K) || (rotationYaw != field_174994_L));
    field_174997_H = posX;
    field_174998_I = posY;
    field_174999_J = posZ;
    field_175000_K = rotationPitch;
    field_174994_L = rotationYaw;
    boolean var22 = field_175001_U != null;
    Lagometer.timerVisibility.start();
    
    if (Shaders.isShadowPass)
    {
      glRenderLists = renderInfosShadow;
      renderInfosEntities = renderInfosEntitiesShadow;
      renderInfosTileEntities = renderInfosTileEntitiesShadow;
      
      if ((!var22) && (displayListEntitiesDirty))
      {
        glRenderLists.clear();
        renderInfosEntities.clear();
        renderInfosTileEntities.clear();
        RenderInfoLazy var39 = new RenderInfoLazy();
        
        for (int var41 = 0; var41 < field_175008_n.field_178164_f.length; var41++)
        {
          RenderChunk var36 = field_175008_n.field_178164_f[var41];
          var39.setRenderChunk(var36);
          
          if ((!field_178590_b.func_178489_a()) || (var36.func_178569_m()))
          {
            glRenderLists.add(var39.getRenderInfo());
          }
          
          BlockPos var37 = var36.func_178568_j();
          
          if (ChunkUtils.hasEntities(theWorld.getChunkFromBlockCoords(var37)))
          {
            renderInfosEntities.add(var39.getRenderInfo());
          }
          
          if (var36.func_178571_g().func_178485_b().size() > 0)
          {
            renderInfosTileEntities.add(var39.getRenderInfo());
          }
        }
      }
    }
    else
    {
      glRenderLists = renderInfosNormal;
      renderInfosEntities = renderInfosEntitiesNormal;
      renderInfosTileEntities = renderInfosTileEntitiesNormal;
    }
    



    if ((!var22) && (displayListEntitiesDirty) && (!Shaders.isShadowPass))
    {
      displayListEntitiesDirty = false;
      glRenderLists.clear();
      renderInfosEntities.clear();
      renderInfosTileEntities.clear();
      visibilityDeque.clear();
      Deque var38 = visibilityDeque;
      boolean var40 = mc.field_175612_E;
      

      if (var20 == null)
      {
        int var46 = var351.getY() > 0 ? 248 : 8;
        
        for (int var30 = -renderDistanceChunks; var30 <= renderDistanceChunks; var30++)
        {
          for (int var43 = -renderDistanceChunks; var43 <= renderDistanceChunks; var43++)
          {
            RenderChunk var45 = field_175008_n.func_178161_a(new BlockPos((var30 << 4) + 8, var46, (var43 << 4) + 8));
            
            if ((var45 != null) && (camera.isBoundingBoxInFrustum(field_178591_c)))
            {
              var45.func_178577_a(frameCount);
              var38.add(new ContainerLocalRenderInformation(var45, null, 0, null));
            }
          }
        }
      }
      else
      {
        boolean var42 = false;
        ContainerLocalRenderInformation var44 = new ContainerLocalRenderInformation(var20, null, 0, null);
        Set var451 = SET_ALL_FACINGS;
        
        if ((!var451.isEmpty()) && (var451.size() == 1))
        {
          Vector3f var47 = func_174962_a(viewEntity, partialTicks);
          EnumFacing var31 = EnumFacing.func_176737_a(x, y, z).getOpposite();
          var451.remove(var31);
        }
        
        if (var451.isEmpty())
        {
          var42 = true;
        }
        
        if ((var42) && (!playerSpectator))
        {
          glRenderLists.add(var44);
        }
        else
        {
          if ((playerSpectator) && (theWorld.getBlockState(var351).getBlock().isOpaqueCube()))
          {
            var40 = false;
          }
          
          var20.func_178577_a(frameCount);
          var38.add(var44);
        }
      }
      
      EnumFacing[] var431 = EnumFacing.VALUES;
      int var30 = var431.length;
      int var49;
      for (; !var38.isEmpty(); 
          




















          var49 < var30)
      {
        ContainerLocalRenderInformation var361 = (ContainerLocalRenderInformation)var38.poll();
        RenderChunk var371 = field_178036_a;
        EnumFacing var461 = field_178034_b;
        BlockPos var48 = var371.func_178568_j();
        
        if ((!field_178590_b.func_178489_a()) || (var371.func_178569_m()))
        {
          glRenderLists.add(var361);
        }
        
        if (ChunkUtils.hasEntities(theWorld.getChunkFromBlockCoords(var48)))
        {
          renderInfosEntities.add(var361);
        }
        
        if (var371.func_178571_g().func_178485_b().size() > 0)
        {
          renderInfosTileEntities.add(var361);
        }
        
        var49 = 0; continue;
        
        EnumFacing var32 = var431[var49];
        
        if (((!var40) || (!field_178035_c.contains(var32.getOpposite()))) && ((!var40) || (var461 == null) || (var371.func_178571_g().func_178495_a(var461.getOpposite(), var32))))
        {
          RenderChunk var33 = getRenderChunkOffset(var351, var371, var32);
          
          if ((var33 != null) && (var33.func_178577_a(frameCount)) && (camera.isBoundingBoxInFrustum(field_178591_c)))
          {
            ContainerLocalRenderInformation var34 = new ContainerLocalRenderInformation(var33, var32, field_178032_d + 1, null);
            field_178035_c.addAll(field_178035_c);
            field_178035_c.add(var32);
            var38.add(var34);
          }
        }
        var49++;
      }
    }
    
















    if (field_175002_T)
    {
      func_174984_a(var13, var15, var17);
      field_175002_T = false;
    }
    
    Lagometer.timerVisibility.end();
    
    if (Shaders.isShadowPass)
    {
      Shaders.mcProfilerEndSection();
    }
    else
    {
      field_174995_M.func_178513_e();
      Set var391 = field_175009_l;
      field_175009_l = Sets.newLinkedHashSet();
      Iterator var411 = glRenderLists.iterator();
      Lagometer.timerChunkUpdate.start();
      
      while (var411.hasNext())
      {
        ContainerLocalRenderInformation var361 = (ContainerLocalRenderInformation)var411.next();
        RenderChunk var371 = field_178036_a;
        
        if ((var371.func_178569_m()) || (var391.contains(var371)))
        {
          displayListEntitiesDirty = true;
          
          if (func_174983_a(var21, field_178036_a))
          {
            if (!var371.isPlayerUpdate())
            {
              chunksToUpdateForced.add(var371);
            }
            else
            {
              mc.mcProfiler.startSection("build near");
              field_174995_M.func_178505_b(var371);
              var371.func_178575_a(false);
              mc.mcProfiler.endSection();
            }
            
          }
          else {
            field_175009_l.add(var371);
          }
        }
      }
      
      Lagometer.timerChunkUpdate.end();
      field_175009_l.addAll(var391);
      mc.mcProfiler.endSection();
    }
  }
  
  private boolean func_174983_a(BlockPos p_174983_1_, RenderChunk p_174983_2_)
  {
    BlockPos var3 = p_174983_2_.func_178568_j();
    return MathHelper.abs_int(p_174983_1_.getX() - var3.getX()) <= 16;
  }
  
  private Set func_174978_c(BlockPos p_174978_1_)
  {
    VisGraph var2 = new VisGraph();
    BlockPos var3 = new BlockPos(p_174978_1_.getX() >> 4 << 4, p_174978_1_.getY() >> 4 << 4, p_174978_1_.getZ() >> 4 << 4);
    Chunk var4 = theWorld.getChunkFromBlockCoords(var3);
    Iterator var5 = BlockPos.getAllInBoxMutable(var3, var3.add(15, 15, 15)).iterator();
    
    while (var5.hasNext())
    {
      BlockPos.MutableBlockPos var6 = (BlockPos.MutableBlockPos)var5.next();
      
      if (var4.getBlock(var6).isOpaqueCube())
      {
        var2.func_178606_a(var6);
      }
    }
    return var2.func_178609_b(p_174978_1_);
  }
  
  private RenderChunk getRenderChunkOffset(BlockPos p_174973_1_, RenderChunk renderChunk, EnumFacing p_174973_3_)
  {
    BlockPos var4 = renderChunk.getPositionOffset16(p_174973_3_);
    
    if ((var4.getY() >= 0) && (var4.getY() < 256))
    {
      int dx = MathHelper.abs_int(p_174973_1_.getX() - var4.getX());
      int dz = MathHelper.abs_int(p_174973_1_.getZ() - var4.getZ());
      
      if (Config.isFogOff())
      {
        if ((dx > renderDistance) || (dz > renderDistance))
        {
          return null;
        }
      }
      else
      {
        int distSq = dx * dx + dz * dz;
        
        if (distSq > renderDistanceSq)
        {
          return null;
        }
      }
      
      return field_175008_n.func_178161_a(var4);
    }
    

    return null;
  }
  

  private void func_174984_a(double p_174984_1_, double p_174984_3_, double p_174984_5_)
  {
    field_175001_U = new ClippingHelperImpl();
    ((ClippingHelperImpl)field_175001_U).init();
    Matrix4f var7 = new Matrix4f(field_175001_U.field_178626_c);
    var7.transpose();
    Matrix4f var8 = new Matrix4f(field_175001_U.field_178625_b);
    var8.transpose();
    Matrix4f var9 = new Matrix4f();
    var9.mul(var8, var7);
    var9.invert();
    field_175003_W.x = p_174984_1_;
    field_175003_W.y = p_174984_3_;
    field_175003_W.z = p_174984_5_;
    field_175004_V[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
    field_175004_V[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
    field_175004_V[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
    field_175004_V[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
    field_175004_V[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
    field_175004_V[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
    field_175004_V[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    field_175004_V[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);
    
    for (int var10 = 0; var10 < 8; var10++)
    {
      var9.transform(field_175004_V[var10]);
      field_175004_V[var10].x /= field_175004_V[var10].w;
      field_175004_V[var10].y /= field_175004_V[var10].w;
      field_175004_V[var10].z /= field_175004_V[var10].w;
      field_175004_V[var10].w = 1.0F;
    }
  }
  
  protected Vector3f func_174962_a(Entity entityIn, double partialTicks)
  {
    float var4 = (float)(prevRotationPitch + (rotationPitch - prevRotationPitch) * partialTicks);
    float var5 = (float)(prevRotationYaw + (rotationYaw - prevRotationYaw) * partialTicks);
    
    if (getMinecraftgameSettings.thirdPersonView == 2)
    {
      var4 += 180.0F;
    }
    
    float var6 = MathHelper.cos(-var5 * 0.017453292F - 3.1415927F);
    float var7 = MathHelper.sin(-var5 * 0.017453292F - 3.1415927F);
    float var8 = -MathHelper.cos(-var4 * 0.017453292F);
    float var9 = MathHelper.sin(-var4 * 0.017453292F);
    return new Vector3f(var7 * var8, var9, var6 * var8);
  }
  

  public int func_174977_a(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn)
  {
    
    if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT)
    {
      mc.mcProfiler.startSection("translucent_sort");
      double var15 = posX - prevRenderSortX;
      double var16 = posY - prevRenderSortY;
      double var17 = posZ - prevRenderSortZ;
      
      if (var15 * var15 + var16 * var16 + var17 * var17 > 1.0D)
      {
        prevRenderSortX = posX;
        prevRenderSortY = posY;
        prevRenderSortZ = posZ;
        int var18 = 0;
        Iterator var13 = glRenderLists.iterator();
        chunksToResortTransparency.clear();
        
        while (var13.hasNext())
        {
          ContainerLocalRenderInformation var14 = (ContainerLocalRenderInformation)var13.next();
          
          if ((field_178036_a.field_178590_b.func_178492_d(blockLayerIn)) && (var18++ < 15))
          {
            chunksToResortTransparency.add(field_178036_a);
          }
        }
      }
      
      mc.mcProfiler.endSection();
    }
    
    mc.mcProfiler.startSection("filterempty");
    int var151 = 0;
    boolean var7 = blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT;
    int var161 = var7 ? glRenderLists.size() - 1 : 0;
    int var9 = var7 ? -1 : glRenderLists.size();
    int var171 = var7 ? -1 : 1;
    
    for (int var11 = var161; var11 != var9; var11 += var171)
    {
      RenderChunk var181 = glRenderLists.get(var11)).field_178036_a;
      
      if (!var181.func_178571_g().func_178491_b(blockLayerIn))
      {
        var151++;
        field_174996_N.func_178002_a(var181, blockLayerIn);
      }
    }
    
    if (var151 == 0)
    {
      mc.mcProfiler.endSection();
      return var151;
    }
    

    if ((Config.isFogOff()) && (mc.entityRenderer.fogStandard))
    {
      GlStateManager.disableFog();
    }
    
    mc.mcProfiler.endStartSection("render_" + blockLayerIn);
    func_174982_a(blockLayerIn);
    mc.mcProfiler.endSection();
    return var151;
  }
  

  private void func_174982_a(EnumWorldBlockLayer blockLayerIn)
  {
    mc.entityRenderer.func_180436_i();
    
    if (OpenGlHelper.func_176075_f())
    {
      GL11.glEnableClientState(32884);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      GL11.glEnableClientState(32888);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glEnableClientState(32888);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      GL11.glEnableClientState(32886);
    }
    
    if (Config.isShaders())
    {
      ShadersRender.preRenderChunkLayer();
    }
    
    field_174996_N.func_178001_a(blockLayerIn);
    
    if (Config.isShaders())
    {
      ShadersRender.postRenderChunkLayer();
    }
    
    if (OpenGlHelper.func_176075_f())
    {
      List var2 = DefaultVertexFormats.field_176600_a.func_177343_g();
      Iterator var3 = var2.iterator();
      
      while (var3.hasNext())
      {
        VertexFormatElement var4 = (VertexFormatElement)var3.next();
        VertexFormatElement.EnumUseage var5 = var4.func_177375_c();
        int var6 = var4.func_177369_e();
        
        switch (SwitchEnumUseage.field_178037_a[var5.ordinal()])
        {
        case 1: 
          GL11.glDisableClientState(32884);
          break;
        
        case 2: 
          OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + var6);
          GL11.glDisableClientState(32888);
          OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
          break;
        
        case 3: 
          GL11.glDisableClientState(32886);
          GlStateManager.func_179117_G();
        }
        
      }
    }
    mc.entityRenderer.func_175072_h();
  }
  
  private void func_174965_a(Iterator p_174965_1_)
  {
    while (p_174965_1_.hasNext())
    {
      DestroyBlockProgress var2 = (DestroyBlockProgress)p_174965_1_.next();
      int var3 = var2.getCreationCloudUpdateTick();
      
      if (cloudTickCounter - var3 > 400)
      {
        p_174965_1_.remove();
      }
    }
  }
  
  public void updateClouds()
  {
    if ((Config.isShaders()) && (Keyboard.isKeyDown(61)) && (Keyboard.isKeyDown(19)))
    {
      Shaders.uninit();
    }
    
    cloudTickCounter += 1;
    
    if (cloudTickCounter % 20 == 0)
    {
      func_174965_a(damagedBlocks.values().iterator());
    }
  }
  
  private void func_180448_r()
  {
    if (Config.isSkyEnabled())
    {
      GlStateManager.disableFog();
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.depthMask(false);
      renderEngine.bindTexture(locationEndSkyPng);
      Tessellator var1 = Tessellator.getInstance();
      WorldRenderer var2 = var1.getWorldRenderer();
      
      for (int var3 = 0; var3 < 6; var3++)
      {
        GlStateManager.pushMatrix();
        
        if (var3 == 1)
        {
          GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }
        
        if (var3 == 2)
        {
          GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        }
        
        if (var3 == 3)
        {
          GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        }
        
        if (var3 == 4)
        {
          GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        }
        
        if (var3 == 5)
        {
          GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
        }
        
        var2.startDrawingQuads();
        var2.func_178991_c(2631720);
        var2.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
        var2.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
        var2.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
        var2.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
        var1.draw();
        GlStateManager.popMatrix();
      }
      
      GlStateManager.depthMask(true);
      GlStateManager.func_179098_w();
      GlStateManager.enableAlpha();
    }
  }
  
  public void func_174976_a(float partialTicks, int pass)
  {
    if (Reflector.ForgeWorldProvider_getSkyRenderer.exists())
    {
      WorldProvider isShaders = mc.theWorld.provider;
      Object var3 = Reflector.call(isShaders, Reflector.ForgeWorldProvider_getSkyRenderer, new Object[0]);
      
      if (var3 != null)
      {
        Reflector.callVoid(var3, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(partialTicks), theWorld, mc });
        return;
      }
    }
    
    if (mc.theWorld.provider.getDimensionId() == 1)
    {
      func_180448_r();
    }
    else if (mc.theWorld.provider.isSurfaceWorld())
    {
      GlStateManager.func_179090_x();
      boolean var231 = Config.isShaders();
      
      if (var231)
      {
        Shaders.disableTexture2D();
      }
      
      Vec3 var241 = theWorld.getSkyColor(mc.func_175606_aa(), partialTicks);
      var241 = CustomColors.getSkyColor(var241, mc.theWorld, mc.func_175606_aa().posX, mc.func_175606_aa().posY + 1.0D, mc.func_175606_aa().posZ);
      
      if (var231)
      {
        Shaders.setSkyColor(var241);
      }
      
      float var4 = (float)xCoord;
      float var5 = (float)yCoord;
      float var6 = (float)zCoord;
      
      if (pass != 2)
      {
        float var23 = (var4 * 30.0F + var5 * 59.0F + var6 * 11.0F) / 100.0F;
        float var24 = (var4 * 30.0F + var5 * 70.0F) / 100.0F;
        float var25 = (var4 * 30.0F + var6 * 70.0F) / 100.0F;
        var4 = var23;
        var5 = var24;
        var6 = var25;
      }
      
      GlStateManager.color(var4, var5, var6);
      Tessellator var251 = Tessellator.getInstance();
      WorldRenderer var261 = var251.getWorldRenderer();
      GlStateManager.depthMask(false);
      GlStateManager.enableFog();
      
      if (var231)
      {
        Shaders.enableFog();
      }
      
      GlStateManager.color(var4, var5, var6);
      
      if (var231)
      {
        Shaders.preSkyList();
      }
      
      if (Config.isSkyEnabled())
      {
        if (field_175005_X)
        {
          field_175012_t.func_177359_a();
          GL11.glEnableClientState(32884);
          GL11.glVertexPointer(3, 5126, 12, 0L);
          field_175012_t.func_177358_a(7);
          field_175012_t.func_177361_b();
          GL11.glDisableClientState(32884);
        }
        else
        {
          GlStateManager.callList(glSkyList);
        }
      }
      
      GlStateManager.disableFog();
      
      if (var231)
      {
        Shaders.disableFog();
      }
      
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.disableStandardItemLighting();
      float[] var27 = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(partialTicks), partialTicks);
      









      if ((var27 != null) && (Config.isSunMoonEnabled()))
      {
        GlStateManager.func_179090_x();
        
        if (var231)
        {
          Shaders.disableTexture2D();
        }
        
        GlStateManager.shadeModel(7425);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(MathHelper.sin(theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        float var10 = var27[0];
        float var11 = var27[1];
        float var12 = var27[2];
        
        if (pass != 2)
        {
          float var13 = (var10 * 30.0F + var11 * 59.0F + var12 * 11.0F) / 100.0F;
          float var14 = (var10 * 30.0F + var11 * 70.0F) / 100.0F;
          float var22 = (var10 * 30.0F + var12 * 70.0F) / 100.0F;
          var10 = var13;
          var11 = var14;
          var12 = var22;
        }
        
        var261.startDrawing(6);
        var261.func_178960_a(var10, var11, var12, var27[3]);
        var261.addVertex(0.0D, 100.0D, 0.0D);
        boolean var26 = true;
        var261.func_178960_a(var27[0], var27[1], var27[2], 0.0F);
        
        for (int var31 = 0; var31 <= 16; var31++)
        {
          float var22 = var31 * 3.1415927F * 2.0F / 16.0F;
          float var18 = MathHelper.sin(var22);
          float var19 = MathHelper.cos(var22);
          var261.addVertex(var18 * 120.0F, var19 * 120.0F, -var19 * 40.0F * var27[3]);
        }
        
        var251.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(7424);
      }
      
      GlStateManager.func_179098_w();
      
      if (var231)
      {
        Shaders.enableTexture2D();
      }
      
      GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
      GlStateManager.pushMatrix();
      float var10 = 1.0F - theWorld.getRainStrength(partialTicks);
      float var11 = 0.0F;
      float var12 = 0.0F;
      float var13 = 0.0F;
      GlStateManager.color(1.0F, 1.0F, 1.0F, var10);
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
      CustomSky.renderSky(theWorld, renderEngine, theWorld.getCelestialAngle(partialTicks), var10);
      
      if (var231)
      {
        Shaders.preCelestialRotate();
      }
      
      GlStateManager.rotate(theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
      
      if (var231)
      {
        Shaders.postCelestialRotate();
      }
      
      if (Config.isSunMoonEnabled())
      {
        float var14 = 30.0F;
        renderEngine.bindTexture(locationSunPng);
        var261.startDrawingQuads();
        var261.addVertexWithUV(-var14, 100.0D, -var14, 0.0D, 0.0D);
        var261.addVertexWithUV(var14, 100.0D, -var14, 1.0D, 0.0D);
        var261.addVertexWithUV(var14, 100.0D, var14, 1.0D, 1.0D);
        var261.addVertexWithUV(-var14, 100.0D, var14, 0.0D, 1.0D);
        var251.draw();
        var14 = 20.0F;
        renderEngine.bindTexture(locationMoonPhasesPng);
        int var28 = theWorld.getMoonPhase();
        int var29 = var28 % 4;
        int var31 = var28 / 4 % 2;
        float var18 = (var29 + 0) / 4.0F;
        float var19 = (var31 + 0) / 2.0F;
        float var20 = (var29 + 1) / 4.0F;
        float var21 = (var31 + 1) / 2.0F;
        var261.startDrawingQuads();
        var261.addVertexWithUV(-var14, -100.0D, var14, var20, var21);
        var261.addVertexWithUV(var14, -100.0D, var14, var18, var21);
        var261.addVertexWithUV(var14, -100.0D, -var14, var18, var19);
        var261.addVertexWithUV(-var14, -100.0D, -var14, var20, var19);
        var251.draw();
      }
      
      GlStateManager.func_179090_x();
      
      if (var231)
      {
        Shaders.disableTexture2D();
      }
      
      float var22 = theWorld.getStarBrightness(partialTicks) * var10;
      
      if ((var22 > 0.0F) && (Config.isStarsEnabled()) && (!CustomSky.hasSkyLayers(theWorld)))
      {
        GlStateManager.color(var22, var22, var22, var22);
        
        if (field_175005_X)
        {
          field_175013_s.func_177359_a();
          GL11.glEnableClientState(32884);
          GL11.glVertexPointer(3, 5126, 12, 0L);
          field_175013_s.func_177358_a(7);
          field_175013_s.func_177361_b();
          GL11.glDisableClientState(32884);
        }
        else
        {
          GlStateManager.callList(starGLCallList);
        }
      }
      
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableFog();
      
      if (var231)
      {
        Shaders.enableFog();
      }
      
      GlStateManager.popMatrix();
      GlStateManager.func_179090_x();
      
      if (var231)
      {
        Shaders.disableTexture2D();
      }
      
      GlStateManager.color(0.0F, 0.0F, 0.0F);
      double var30 = mc.thePlayer.func_174824_e(partialTicks).yCoord - theWorld.getHorizon();
      
      if (var30 < 0.0D)
      {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 12.0F, 0.0F);
        
        if (field_175005_X)
        {
          field_175011_u.func_177359_a();
          GL11.glEnableClientState(32884);
          GL11.glVertexPointer(3, 5126, 12, 0L);
          field_175011_u.func_177358_a(7);
          field_175011_u.func_177361_b();
          GL11.glDisableClientState(32884);
        }
        else
        {
          GlStateManager.callList(glSkyList2);
        }
        
        GlStateManager.popMatrix();
        var12 = 1.0F;
        var13 = -(float)(var30 + 65.0D);
        float var14 = -1.0F;
        var261.startDrawingQuads();
        var261.func_178974_a(0, 255);
        var261.addVertex(-1.0D, var13, 1.0D);
        var261.addVertex(1.0D, var13, 1.0D);
        var261.addVertex(1.0D, -1.0D, 1.0D);
        var261.addVertex(-1.0D, -1.0D, 1.0D);
        var261.addVertex(-1.0D, -1.0D, -1.0D);
        var261.addVertex(1.0D, -1.0D, -1.0D);
        var261.addVertex(1.0D, var13, -1.0D);
        var261.addVertex(-1.0D, var13, -1.0D);
        var261.addVertex(1.0D, -1.0D, -1.0D);
        var261.addVertex(1.0D, -1.0D, 1.0D);
        var261.addVertex(1.0D, var13, 1.0D);
        var261.addVertex(1.0D, var13, -1.0D);
        var261.addVertex(-1.0D, var13, -1.0D);
        var261.addVertex(-1.0D, var13, 1.0D);
        var261.addVertex(-1.0D, -1.0D, 1.0D);
        var261.addVertex(-1.0D, -1.0D, -1.0D);
        var261.addVertex(-1.0D, -1.0D, -1.0D);
        var261.addVertex(-1.0D, -1.0D, 1.0D);
        var261.addVertex(1.0D, -1.0D, 1.0D);
        var261.addVertex(1.0D, -1.0D, -1.0D);
        var251.draw();
      }
      
      if (theWorld.provider.isSkyColored())
      {
        GlStateManager.color(var4 * 0.2F + 0.04F, var5 * 0.2F + 0.04F, var6 * 0.6F + 0.1F);
      }
      else
      {
        GlStateManager.color(var4, var5, var6);
      }
      
      if (mc.gameSettings.renderDistanceChunks <= 4)
      {
        GlStateManager.color(mc.entityRenderer.field_175080_Q, mc.entityRenderer.field_175082_R, mc.entityRenderer.field_175081_S);
      }
      
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, -(float)(var30 - 16.0D), 0.0F);
      
      if (Config.isSkyEnabled())
      {
        GlStateManager.callList(glSkyList2);
      }
      
      GlStateManager.popMatrix();
      GlStateManager.func_179098_w();
      
      if (var231)
      {
        Shaders.enableTexture2D();
      }
      
      GlStateManager.depthMask(true);
    }
  }
  
  public void func_180447_b(float p_180447_1_, int p_180447_2_)
  {
    if (!Config.isCloudsOff())
    {
      if (Reflector.ForgeWorldProvider_getCloudRenderer.exists())
      {
        WorldProvider partialTicks = mc.theWorld.provider;
        Object var3 = Reflector.call(partialTicks, Reflector.ForgeWorldProvider_getCloudRenderer, new Object[0]);
        
        if (var3 != null)
        {
          Reflector.callVoid(var3, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(p_180447_1_), theWorld, mc });
          return;
        }
      }
      
      if (mc.theWorld.provider.isSurfaceWorld())
      {
        if (Config.isShaders())
        {
          Shaders.beginClouds();
        }
        
        if (Config.isCloudsFancy())
        {
          func_180445_c(p_180447_1_, p_180447_2_);
        }
        else
        {
          cloudRenderer.prepareToRender(false, cloudTickCounter, p_180447_1_);
          p_180447_1_ = 0.0F;
          GlStateManager.disableCull();
          float var31 = (float)(mc.func_175606_aa().lastTickPosY + (mc.func_175606_aa().posY - mc.func_175606_aa().lastTickPosY) * p_180447_1_);
          boolean var4 = true;
          boolean var5 = true;
          Tessellator var6 = Tessellator.getInstance();
          WorldRenderer var7 = var6.getWorldRenderer();
          renderEngine.bindTexture(locationCloudsPng);
          GlStateManager.enableBlend();
          GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
          
          if (cloudRenderer.shouldUpdateGlList())
          {
            cloudRenderer.startUpdateGlList();
            Vec3 var8 = theWorld.getCloudColour(p_180447_1_);
            float var9 = (float)xCoord;
            float var10 = (float)yCoord;
            float var11 = (float)zCoord;
            

            if (p_180447_2_ != 2)
            {
              float var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
              float var26 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
              float var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
              var9 = var12;
              var10 = var26;
              var11 = var14;
            }
            
            float var12 = 4.8828125E-4F;
            double var261 = cloudTickCounter + p_180447_1_;
            double var15 = mc.func_175606_aa().prevPosX + (mc.func_175606_aa().posX - mc.func_175606_aa().prevPosX) * p_180447_1_ + var261 * 0.029999999329447746D;
            double var17 = mc.func_175606_aa().prevPosZ + (mc.func_175606_aa().posZ - mc.func_175606_aa().prevPosZ) * p_180447_1_;
            int var19 = MathHelper.floor_double(var15 / 2048.0D);
            int var20 = MathHelper.floor_double(var17 / 2048.0D);
            var15 -= var19 * 2048;
            var17 -= var20 * 2048;
            float var21 = theWorld.provider.getCloudHeight() - var31 + 0.33F;
            var21 += mc.gameSettings.ofCloudsHeight * 128.0F;
            float var22 = (float)(var15 * 4.8828125E-4D);
            float var23 = (float)(var17 * 4.8828125E-4D);
            var7.startDrawingQuads();
            var7.func_178960_a(var9, var10, var11, 0.8F);
            
            for (int var24 = 65280; var24 < 256; var24 += 32)
            {
              for (int var25 = 65280; var25 < 256; var25 += 32)
              {
                var7.addVertexWithUV(var24 + 0, var21, var25 + 32, (var24 + 0) * 4.8828125E-4F + var22, (var25 + 32) * 4.8828125E-4F + var23);
                var7.addVertexWithUV(var24 + 32, var21, var25 + 32, (var24 + 32) * 4.8828125E-4F + var22, (var25 + 32) * 4.8828125E-4F + var23);
                var7.addVertexWithUV(var24 + 32, var21, var25 + 0, (var24 + 32) * 4.8828125E-4F + var22, (var25 + 0) * 4.8828125E-4F + var23);
                var7.addVertexWithUV(var24 + 0, var21, var25 + 0, (var24 + 0) * 4.8828125E-4F + var22, (var25 + 0) * 4.8828125E-4F + var23);
              }
            }
            
            var6.draw();
            cloudRenderer.endUpdateGlList();
          }
          
          cloudRenderer.renderGlList();
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          GlStateManager.disableBlend();
          GlStateManager.enableCull();
        }
        
        if (Config.isShaders())
        {
          Shaders.endClouds();
        }
      }
    }
  }
  



  public boolean hasCloudFog(double p_72721_1_, double p_72721_3_, double p_72721_5_, float p_72721_7_)
  {
    return false;
  }
  
  private void func_180445_c(float p_180445_1_, int p_180445_2_)
  {
    cloudRenderer.prepareToRender(true, cloudTickCounter, p_180445_1_);
    p_180445_1_ = 0.0F;
    GlStateManager.disableCull();
    float var3 = (float)(mc.func_175606_aa().lastTickPosY + (mc.func_175606_aa().posY - mc.func_175606_aa().lastTickPosY) * p_180445_1_);
    Tessellator var4 = Tessellator.getInstance();
    WorldRenderer var5 = var4.getWorldRenderer();
    float var6 = 12.0F;
    float var7 = 4.0F;
    double var8 = cloudTickCounter + p_180445_1_;
    double var10 = (mc.func_175606_aa().prevPosX + (mc.func_175606_aa().posX - mc.func_175606_aa().prevPosX) * p_180445_1_ + var8 * 0.029999999329447746D) / 12.0D;
    double var12 = (mc.func_175606_aa().prevPosZ + (mc.func_175606_aa().posZ - mc.func_175606_aa().prevPosZ) * p_180445_1_) / 12.0D + 0.33000001311302185D;
    float var14 = theWorld.provider.getCloudHeight() - var3 + 0.33F;
    var14 += mc.gameSettings.ofCloudsHeight * 128.0F;
    int var15 = MathHelper.floor_double(var10 / 2048.0D);
    int var16 = MathHelper.floor_double(var12 / 2048.0D);
    var10 -= var15 * 2048;
    var12 -= var16 * 2048;
    renderEngine.bindTexture(locationCloudsPng);
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    Vec3 var17 = theWorld.getCloudColour(p_180445_1_);
    float var18 = (float)xCoord;
    float var19 = (float)yCoord;
    float var20 = (float)zCoord;
    



    if (p_180445_2_ != 2)
    {
      float var21 = (var18 * 30.0F + var19 * 59.0F + var20 * 11.0F) / 100.0F;
      float var22 = (var18 * 30.0F + var19 * 70.0F) / 100.0F;
      float var23 = (var18 * 30.0F + var20 * 70.0F) / 100.0F;
      var18 = var21;
      var19 = var22;
      var20 = var23;
    }
    
    float var21 = 0.00390625F;
    float var22 = MathHelper.floor_double(var10) * 0.00390625F;
    float var23 = MathHelper.floor_double(var12) * 0.00390625F;
    float var24 = (float)(var10 - MathHelper.floor_double(var10));
    float var25 = (float)(var12 - MathHelper.floor_double(var12));
    boolean var26 = true;
    boolean var27 = true;
    float var28 = 9.765625E-4F;
    GlStateManager.scale(12.0F, 1.0F, 12.0F);
    

    for (int var30 = 0; var30 < 2; var30++)
    {
      if (var30 == 0)
      {
        GlStateManager.colorMask(false, false, false, false);
      }
      else
      {
        switch (p_180445_2_)
        {
        case 0: 
          GlStateManager.colorMask(false, true, true, true);
          break;
        
        case 1: 
          GlStateManager.colorMask(true, false, false, true);
          break;
        
        case 2: 
          GlStateManager.colorMask(true, true, true, true);
        }
        
      }
      cloudRenderer.renderGlList();
    }
    
    if (cloudRenderer.shouldUpdateGlList())
    {
      cloudRenderer.startUpdateGlList();
      
      for (var30 = -3; var30 <= 4; var30++)
      {
        for (int var31 = -3; var31 <= 4; var31++)
        {
          var5.startDrawingQuads();
          float var32 = var30 * 8;
          float var33 = var31 * 8;
          float var34 = var32 - var24;
          float var35 = var33 - var25;
          
          if (var14 > -5.0F)
          {
            var5.func_178960_a(var18 * 0.7F, var19 * 0.7F, var20 * 0.7F, 0.8F);
            var5.func_178980_d(0.0F, -1.0F, 0.0F);
            var5.addVertexWithUV(var34 + 0.0F, var14 + 0.0F, var35 + 8.0F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
            var5.addVertexWithUV(var34 + 8.0F, var14 + 0.0F, var35 + 8.0F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
            var5.addVertexWithUV(var34 + 8.0F, var14 + 0.0F, var35 + 0.0F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
            var5.addVertexWithUV(var34 + 0.0F, var14 + 0.0F, var35 + 0.0F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
          }
          
          if (var14 <= 5.0F)
          {
            var5.func_178960_a(var18, var19, var20, 0.8F);
            var5.func_178980_d(0.0F, 1.0F, 0.0F);
            var5.addVertexWithUV(var34 + 0.0F, var14 + 4.0F - 9.765625E-4F, var35 + 8.0F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
            var5.addVertexWithUV(var34 + 8.0F, var14 + 4.0F - 9.765625E-4F, var35 + 8.0F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
            var5.addVertexWithUV(var34 + 8.0F, var14 + 4.0F - 9.765625E-4F, var35 + 0.0F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
            var5.addVertexWithUV(var34 + 0.0F, var14 + 4.0F - 9.765625E-4F, var35 + 0.0F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
          }
          
          var5.func_178960_a(var18 * 0.9F, var19 * 0.9F, var20 * 0.9F, 0.8F);
          

          if (var30 > -1)
          {
            var5.func_178980_d(-1.0F, 0.0F, 0.0F);
            
            for (int var36 = 0; var36 < 8; var36++)
            {
              var5.addVertexWithUV(var34 + var36 + 0.0F, var14 + 0.0F, var35 + 8.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + var36 + 0.0F, var14 + 4.0F, var35 + 8.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + var36 + 0.0F, var14 + 4.0F, var35 + 0.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + var36 + 0.0F, var14 + 0.0F, var35 + 0.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
            }
          }
          
          if (var30 <= 1)
          {
            var5.func_178980_d(1.0F, 0.0F, 0.0F);
            
            for (int var36 = 0; var36 < 8; var36++)
            {
              var5.addVertexWithUV(var34 + var36 + 1.0F - 9.765625E-4F, var14 + 0.0F, var35 + 8.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + var36 + 1.0F - 9.765625E-4F, var14 + 4.0F, var35 + 8.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 8.0F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + var36 + 1.0F - 9.765625E-4F, var14 + 4.0F, var35 + 0.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + var36 + 1.0F - 9.765625E-4F, var14 + 0.0F, var35 + 0.0F, (var32 + var36 + 0.5F) * 0.00390625F + var22, (var33 + 0.0F) * 0.00390625F + var23);
            }
          }
          
          var5.func_178960_a(var18 * 0.8F, var19 * 0.8F, var20 * 0.8F, 0.8F);
          
          if (var31 > -1)
          {
            var5.func_178980_d(0.0F, 0.0F, -1.0F);
            
            for (int var36 = 0; var36 < 8; var36++)
            {
              var5.addVertexWithUV(var34 + 0.0F, var14 + 4.0F, var35 + var36 + 0.0F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + 8.0F, var14 + 4.0F, var35 + var36 + 0.0F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + 8.0F, var14 + 0.0F, var35 + var36 + 0.0F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + 0.0F, var14 + 0.0F, var35 + var36 + 0.0F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
            }
          }
          
          if (var31 <= 1)
          {
            var5.func_178980_d(0.0F, 0.0F, 1.0F);
            
            for (int var36 = 0; var36 < 8; var36++)
            {
              var5.addVertexWithUV(var34 + 0.0F, var14 + 4.0F, var35 + var36 + 1.0F - 9.765625E-4F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + 8.0F, var14 + 4.0F, var35 + var36 + 1.0F - 9.765625E-4F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + 8.0F, var14 + 0.0F, var35 + var36 + 1.0F - 9.765625E-4F, (var32 + 8.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
              var5.addVertexWithUV(var34 + 0.0F, var14 + 0.0F, var35 + var36 + 1.0F - 9.765625E-4F, (var32 + 0.0F) * 0.00390625F + var22, (var33 + var36 + 0.5F) * 0.00390625F + var23);
            }
          }
          
          var4.draw();
        }
      }
      
      cloudRenderer.endUpdateGlList();
    }
    
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
  }
  
  public void func_174967_a(long p_174967_1_)
  {
    displayListEntitiesDirty |= field_174995_M.func_178516_a(p_174967_1_);
    


    if (chunksToUpdateForced.size() > 0)
    {
      Iterator countUpdated = chunksToUpdateForced.iterator();
      
      while (countUpdated.hasNext())
      {
        RenderChunk updatesPerFrame = (RenderChunk)countUpdated.next();
        
        if (!field_174995_M.func_178507_a(updatesPerFrame)) {
          break;
        }
        

        updatesPerFrame.func_178575_a(false);
        countUpdated.remove();
        field_175009_l.remove(updatesPerFrame);
        chunksToResortTransparency.remove(updatesPerFrame);
      }
    }
    
    if (chunksToResortTransparency.size() > 0)
    {
      Iterator countUpdated = chunksToResortTransparency.iterator();
      
      if (countUpdated.hasNext())
      {
        RenderChunk updatesPerFrame = (RenderChunk)countUpdated.next();
        
        if (field_174995_M.func_178509_c(updatesPerFrame))
        {
          countUpdated.remove();
        }
      }
    }
    
    int var8 = 0;
    int var9 = Config.getUpdatesPerFrame();
    int maxUpdatesPerFrame = var9 * 2;
    Iterator var3 = field_175009_l.iterator();
    
    while (var3.hasNext())
    {
      RenderChunk var4 = (RenderChunk)var3.next();
      
      if (!field_174995_M.func_178507_a(var4)) {
        break;
      }
      

      var4.func_178575_a(false);
      var3.remove();
      
      if ((var4.func_178571_g().func_178489_a()) && (var9 < maxUpdatesPerFrame))
      {
        var9++;
      }
      
      var8++;
      
      if (var8 >= var9) {
        break;
      }
    }
  }
  

  public void func_180449_a(Entity p_180449_1_, float p_180449_2_)
  {
    Tessellator var3 = Tessellator.getInstance();
    WorldRenderer var4 = var3.getWorldRenderer();
    WorldBorder var5 = theWorld.getWorldBorder();
    double var6 = mc.gameSettings.renderDistanceChunks * 16;
    
    if ((posX >= var5.maxX() - var6) || (posX <= var5.minX() + var6) || (posZ >= var5.maxZ() - var6) || (posZ <= var5.minZ() + var6))
    {
      double var8 = 1.0D - var5.getClosestDistance(p_180449_1_) / var6;
      var8 = Math.pow(var8, 4.0D);
      double var10 = lastTickPosX + (posX - lastTickPosX) * p_180449_2_;
      double var12 = lastTickPosY + (posY - lastTickPosY) * p_180449_2_;
      double var14 = lastTickPosZ + (posZ - lastTickPosZ) * p_180449_2_;
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
      renderEngine.bindTexture(field_175006_g);
      GlStateManager.depthMask(false);
      GlStateManager.pushMatrix();
      int var16 = var5.getStatus().func_177766_a();
      float var17 = (var16 >> 16 & 0xFF) / 255.0F;
      float var18 = (var16 >> 8 & 0xFF) / 255.0F;
      float var19 = (var16 & 0xFF) / 255.0F;
      GlStateManager.color(var17, var18, var19, (float)var8);
      GlStateManager.doPolygonOffset(-3.0F, -3.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlpha();
      GlStateManager.disableCull();
      float var20 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
      float var21 = 0.0F;
      float var22 = 0.0F;
      float var23 = 128.0F;
      var4.startDrawingQuads();
      var4.setTranslation(-var10, -var12, -var14);
      var4.markDirty();
      double var24 = Math.max(MathHelper.floor_double(var14 - var6), var5.minZ());
      double var26 = Math.min(MathHelper.ceiling_double_int(var14 + var6), var5.maxZ());
      




      if (var10 > var5.maxX() - var6)
      {
        float var28 = 0.0F;
        
        for (double var29 = var24; var29 < var26; var28 += 0.5F)
        {
          double var31 = Math.min(1.0D, var26 - var29);
          float var33 = (float)var31 * 0.5F;
          var4.addVertexWithUV(var5.maxX(), 256.0D, var29, var20 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var5.maxX(), 256.0D, var29 + var31, var20 + var33 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var5.maxX(), 0.0D, var29 + var31, var20 + var33 + var28, var20 + 128.0F);
          var4.addVertexWithUV(var5.maxX(), 0.0D, var29, var20 + var28, var20 + 128.0F);
          var29 += 1.0D;
        }
      }
      
      if (var10 < var5.minX() + var6)
      {
        float var28 = 0.0F;
        
        for (double var29 = var24; var29 < var26; var28 += 0.5F)
        {
          double var31 = Math.min(1.0D, var26 - var29);
          float var33 = (float)var31 * 0.5F;
          var4.addVertexWithUV(var5.minX(), 256.0D, var29, var20 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var5.minX(), 256.0D, var29 + var31, var20 + var33 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var5.minX(), 0.0D, var29 + var31, var20 + var33 + var28, var20 + 128.0F);
          var4.addVertexWithUV(var5.minX(), 0.0D, var29, var20 + var28, var20 + 128.0F);
          var29 += 1.0D;
        }
      }
      
      var24 = Math.max(MathHelper.floor_double(var10 - var6), var5.minX());
      var26 = Math.min(MathHelper.ceiling_double_int(var10 + var6), var5.maxX());
      
      if (var14 > var5.maxZ() - var6)
      {
        float var28 = 0.0F;
        
        for (double var29 = var24; var29 < var26; var28 += 0.5F)
        {
          double var31 = Math.min(1.0D, var26 - var29);
          float var33 = (float)var31 * 0.5F;
          var4.addVertexWithUV(var29, 256.0D, var5.maxZ(), var20 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var29 + var31, 256.0D, var5.maxZ(), var20 + var33 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var29 + var31, 0.0D, var5.maxZ(), var20 + var33 + var28, var20 + 128.0F);
          var4.addVertexWithUV(var29, 0.0D, var5.maxZ(), var20 + var28, var20 + 128.0F);
          var29 += 1.0D;
        }
      }
      
      if (var14 < var5.minZ() + var6)
      {
        float var28 = 0.0F;
        
        for (double var29 = var24; var29 < var26; var28 += 0.5F)
        {
          double var31 = Math.min(1.0D, var26 - var29);
          float var33 = (float)var31 * 0.5F;
          var4.addVertexWithUV(var29, 256.0D, var5.minZ(), var20 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var29 + var31, 256.0D, var5.minZ(), var20 + var33 + var28, var20 + 0.0F);
          var4.addVertexWithUV(var29 + var31, 0.0D, var5.minZ(), var20 + var33 + var28, var20 + 128.0F);
          var4.addVertexWithUV(var29, 0.0D, var5.minZ(), var20 + var28, var20 + 128.0F);
          var29 += 1.0D;
        }
      }
      
      var3.draw();
      var4.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.enableCull();
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
    }
  }
  
  private void func_180443_s()
  {
    GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
    GlStateManager.enableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
    GlStateManager.doPolygonOffset(-3.0F, -3.0F);
    GlStateManager.enablePolygonOffset();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.enableAlpha();
    GlStateManager.pushMatrix();
    
    if (Config.isShaders())
    {
      ShadersRender.beginBlockDamage();
    }
  }
  
  private void func_174969_t()
  {
    GlStateManager.disableAlpha();
    GlStateManager.doPolygonOffset(0.0F, 0.0F);
    GlStateManager.disablePolygonOffset();
    GlStateManager.enableAlpha();
    GlStateManager.depthMask(true);
    GlStateManager.popMatrix();
    
    if (Config.isShaders())
    {
      ShadersRender.endBlockDamage();
    }
  }
  
  public void func_174981_a(Tessellator p_174981_1_, WorldRenderer p_174981_2_, Entity p_174981_3_, float p_174981_4_)
  {
    double var5 = lastTickPosX + (posX - lastTickPosX) * p_174981_4_;
    double var7 = lastTickPosY + (posY - lastTickPosY) * p_174981_4_;
    double var9 = lastTickPosZ + (posZ - lastTickPosZ) * p_174981_4_;
    
    if (!damagedBlocks.isEmpty())
    {
      renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      func_180443_s();
      p_174981_2_.startDrawingQuads();
      p_174981_2_.setVertexFormat(DefaultVertexFormats.field_176600_a);
      p_174981_2_.setTranslation(-var5, -var7, -var9);
      p_174981_2_.markDirty();
      Iterator var11 = damagedBlocks.values().iterator();
      
      while (var11.hasNext())
      {
        DestroyBlockProgress var12 = (DestroyBlockProgress)var11.next();
        BlockPos var13 = var12.func_180246_b();
        double var14 = var13.getX() - var5;
        double var16 = var13.getY() - var7;
        double var18 = var13.getZ() - var9;
        Block var20 = theWorld.getBlockState(var13).getBlock();
        boolean renderBreaking;
        boolean renderBreaking;
        if (Reflector.ForgeTileEntity_canRenderBreaking.exists())
        {
          boolean var22 = ((var20 instanceof BlockChest)) || ((var20 instanceof BlockEnderChest)) || ((var20 instanceof BlockSign)) || ((var20 instanceof BlockSkull));
          
          if (!var22)
          {
            TileEntity var23 = theWorld.getTileEntity(var13);
            
            if (var23 != null)
            {
              var22 = Reflector.callBoolean(var23, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0]);
            }
          }
          
          renderBreaking = !var22;
        }
        else
        {
          renderBreaking = (!(var20 instanceof BlockChest)) && (!(var20 instanceof BlockEnderChest)) && (!(var20 instanceof BlockSign)) && (!(var20 instanceof BlockSkull));
        }
        
        if (renderBreaking)
        {
          if (var14 * var14 + var16 * var16 + var18 * var18 > 1024.0D)
          {
            var11.remove();
          }
          else
          {
            IBlockState var21 = theWorld.getBlockState(var13);
            
            if (var21.getBlock().getMaterial() != Material.air)
            {
              int var221 = var12.getPartialBlockDamage();
              TextureAtlasSprite var231 = destroyBlockIcons[var221];
              BlockRendererDispatcher var24 = mc.getBlockRendererDispatcher();
              var24.func_175020_a(var21, var13, var231, theWorld);
            }
          }
        }
      }
      
      p_174981_1_.draw();
      p_174981_2_.setTranslation(0.0D, 0.0D, 0.0D);
      func_174969_t();
    }
  }
  



  public void drawSelectionBox(EntityPlayer p_72731_1_, MovingObjectPosition p_72731_2_, int p_72731_3_, float p_72731_4_)
  {
    if ((p_72731_3_ == 0) && (typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK))
    {
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
      GL11.glLineWidth(2.0F);
      GlStateManager.func_179090_x();
      
      if (Config.isShaders())
      {
        Shaders.disableTexture2D();
      }
      
      GlStateManager.depthMask(false);
      float var5 = 0.002F;
      BlockPos var6 = p_72731_2_.func_178782_a();
      Block var7 = theWorld.getBlockState(var6).getBlock();
      
      if ((var7.getMaterial() != Material.air) && (theWorld.getWorldBorder().contains(var6)))
      {
        var7.setBlockBoundsBasedOnState(theWorld, var6);
        double var8 = lastTickPosX + (posX - lastTickPosX) * p_72731_4_;
        double var10 = lastTickPosY + (posY - lastTickPosY) * p_72731_4_;
        double var12 = lastTickPosZ + (posZ - lastTickPosZ) * p_72731_4_;
        drawOutlinedBoundingBox(var7.getSelectedBoundingBox(theWorld, var6).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-var8, -var10, -var12), -1);
      }
      
      GlStateManager.depthMask(true);
      GlStateManager.func_179098_w();
      
      if (Config.isShaders())
      {
        Shaders.enableTexture2D();
      }
      
      GlStateManager.disableBlend();
    }
  }
  



  public static void drawOutlinedBoundingBox(AxisAlignedBB p_147590_0_, int p_147590_1_)
  {
    Tessellator var2 = Tessellator.getInstance();
    WorldRenderer var3 = var2.getWorldRenderer();
    var3.startDrawing(3);
    
    if (p_147590_1_ != -1)
    {
      var3.func_178991_c(p_147590_1_);
    }
    
    var3.addVertex(minX, minY, minZ);
    var3.addVertex(maxX, minY, minZ);
    var3.addVertex(maxX, minY, maxZ);
    var3.addVertex(minX, minY, maxZ);
    var3.addVertex(minX, minY, minZ);
    var2.draw();
    var3.startDrawing(3);
    
    if (p_147590_1_ != -1)
    {
      var3.func_178991_c(p_147590_1_);
    }
    
    var3.addVertex(minX, maxY, minZ);
    var3.addVertex(maxX, maxY, minZ);
    var3.addVertex(maxX, maxY, maxZ);
    var3.addVertex(minX, maxY, maxZ);
    var3.addVertex(minX, maxY, minZ);
    var2.draw();
    var3.startDrawing(1);
    
    if (p_147590_1_ != -1)
    {
      var3.func_178991_c(p_147590_1_);
    }
    
    var3.addVertex(minX, minY, minZ);
    var3.addVertex(minX, maxY, minZ);
    var3.addVertex(maxX, minY, minZ);
    var3.addVertex(maxX, maxY, minZ);
    var3.addVertex(maxX, minY, maxZ);
    var3.addVertex(maxX, maxY, maxZ);
    var3.addVertex(minX, minY, maxZ);
    var3.addVertex(minX, maxY, maxZ);
    var2.draw();
  }
  



  private void markBlocksForUpdate(int p_72725_1_, int p_72725_2_, int p_72725_3_, int p_72725_4_, int p_72725_5_, int p_72725_6_)
  {
    field_175008_n.func_178162_a(p_72725_1_, p_72725_2_, p_72725_3_, p_72725_4_, p_72725_5_, p_72725_6_);
  }
  
  public void markBlockForUpdate(BlockPos pos)
  {
    int var2 = pos.getX();
    int var3 = pos.getY();
    int var4 = pos.getZ();
    markBlocksForUpdate(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
  }
  
  public void notifyLightSet(BlockPos pos)
  {
    int var2 = pos.getX();
    int var3 = pos.getY();
    int var4 = pos.getZ();
    markBlocksForUpdate(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
  }
  




  public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
  {
    markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
  }
  
  public void func_174961_a(String p_174961_1_, BlockPos p_174961_2_)
  {
    ISound var3 = (ISound)mapSoundPositions.get(p_174961_2_);
    
    if (var3 != null)
    {
      mc.getSoundHandler().stopSound(var3);
      mapSoundPositions.remove(p_174961_2_);
    }
    
    if (p_174961_1_ != null)
    {
      ItemRecord var4 = ItemRecord.getRecord(p_174961_1_);
      
      if (var4 != null)
      {
        mc.ingameGUI.setRecordPlayingMessage(var4.getRecordNameLocal());
      }
      
      ResourceLocation resource = null;
      
      if ((Reflector.ForgeItemRecord_getRecordResource.exists()) && (var4 != null))
      {
        resource = (ResourceLocation)Reflector.call(var4, Reflector.ForgeItemRecord_getRecordResource, new Object[] { p_174961_1_ });
      }
      
      if (resource == null)
      {
        resource = new ResourceLocation(p_174961_1_);
      }
      
      PositionedSoundRecord var5 = PositionedSoundRecord.createRecordSoundAtPosition(resource, p_174961_2_.getX(), p_174961_2_.getY(), p_174961_2_.getZ());
      mapSoundPositions.put(p_174961_2_, var5);
      mc.getSoundHandler().playSound(var5);
    }
  }
  


  public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {}
  


  public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {}
  


  public void func_180442_a(int p_180442_1_, boolean p_180442_2_, final double p_180442_3_, double p_180442_5_, final double p_180442_7_, double p_180442_9_, double p_180442_11_, double p_180442_13_, int... p_180442_15_)
  {
    try
    {
      func_174974_b(p_180442_1_, p_180442_2_, p_180442_3_, p_180442_5_, p_180442_7_, p_180442_9_, p_180442_11_, p_180442_13_, p_180442_15_);
    }
    catch (Throwable var19)
    {
      CrashReport var17 = CrashReport.makeCrashReport(var19, "Exception while adding particle");
      CrashReportCategory var18 = var17.makeCategory("Particle being added");
      var18.addCrashSection("ID", Integer.valueOf(p_180442_1_));
      
      if (p_180442_15_ != null)
      {
        var18.addCrashSection("Parameters", p_180442_15_);
      }
      
      var18.addCrashSectionCallable("Position", new Callable()
      {
        public String call()
        {
          return CrashReportCategory.getCoordinateInfo(p_180442_3_, p_180442_7_, val$p_180442_7_);
        }
      });
      throw new net.minecraft.util.ReportedException(var17);
    }
  }
  
  private void func_174972_a(EnumParticleTypes p_174972_1_, double p_174972_2_, double p_174972_4_, double p_174972_6_, double p_174972_8_, double p_174972_10_, double p_174972_12_, int... p_174972_14_)
  {
    func_180442_a(p_174972_1_.func_179348_c(), p_174972_1_.func_179344_e(), p_174972_2_, p_174972_4_, p_174972_6_, p_174972_8_, p_174972_10_, p_174972_12_, p_174972_14_);
  }
  
  private EntityFX func_174974_b(int p_174974_1_, boolean p_174974_2_, double p_174974_3_, double p_174974_5_, double p_174974_7_, double p_174974_9_, double p_174974_11_, double p_174974_13_, int... p_174974_15_)
  {
    if ((mc != null) && (mc.func_175606_aa() != null) && (mc.effectRenderer != null))
    {
      int var16 = mc.gameSettings.particleSetting;
      
      if ((var16 == 1) && (theWorld.rand.nextInt(3) == 0))
      {
        var16 = 2;
      }
      
      double var17 = mc.func_175606_aa().posX - p_174974_3_;
      double var19 = mc.func_175606_aa().posY - p_174974_5_;
      double var21 = mc.func_175606_aa().posZ - p_174974_7_;
      
      if ((p_174974_1_ == EnumParticleTypes.EXPLOSION_HUGE.func_179348_c()) && (!Config.isAnimatedExplosion()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.EXPLOSION_LARGE.func_179348_c()) && (!Config.isAnimatedExplosion()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.EXPLOSION_NORMAL.func_179348_c()) && (!Config.isAnimatedExplosion()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SUSPENDED.func_179348_c()) && (!Config.isWaterParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SUSPENDED_DEPTH.func_179348_c()) && (!Config.isVoidParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SMOKE_NORMAL.func_179348_c()) && (!Config.isAnimatedSmoke()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SMOKE_LARGE.func_179348_c()) && (!Config.isAnimatedSmoke()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SPELL_MOB.func_179348_c()) && (!Config.isPotionParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SPELL_MOB_AMBIENT.func_179348_c()) && (!Config.isPotionParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SPELL.func_179348_c()) && (!Config.isPotionParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SPELL_INSTANT.func_179348_c()) && (!Config.isPotionParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.SPELL_WITCH.func_179348_c()) && (!Config.isPotionParticles()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.PORTAL.func_179348_c()) && (!Config.isAnimatedPortal()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.FLAME.func_179348_c()) && (!Config.isAnimatedFlame()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.REDSTONE.func_179348_c()) && (!Config.isAnimatedRedstone()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.DRIP_WATER.func_179348_c()) && (!Config.isDrippingWaterLava()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.DRIP_LAVA.func_179348_c()) && (!Config.isDrippingWaterLava()))
      {
        return null;
      }
      if ((p_174974_1_ == EnumParticleTypes.FIREWORKS_SPARK.func_179348_c()) && (!Config.isFireworkParticles()))
      {
        return null;
      }
      if (p_174974_2_)
      {
        return mc.effectRenderer.func_178927_a(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);
      }
      

      double var23 = 16.0D;
      double maxDistSq = 256.0D;
      
      if (p_174974_1_ == EnumParticleTypes.CRIT.func_179348_c())
      {
        maxDistSq = 38416.0D;
      }
      
      if (var17 * var17 + var19 * var19 + var21 * var21 > maxDistSq)
      {
        return null;
      }
      if (var16 > 1)
      {
        return null;
      }
      

      EntityFX entityFx = mc.effectRenderer.func_178927_a(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);
      
      if (p_174974_1_ == EnumParticleTypes.WATER_BUBBLE.func_179348_c())
      {
        CustomColors.updateWaterFX(entityFx, theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
      }
      
      if (p_174974_1_ == EnumParticleTypes.WATER_SPLASH.func_179348_c())
      {
        CustomColors.updateWaterFX(entityFx, theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
      }
      
      if (p_174974_1_ == EnumParticleTypes.WATER_DROP.func_179348_c())
      {
        CustomColors.updateWaterFX(entityFx, theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
      }
      
      if (p_174974_1_ == EnumParticleTypes.TOWN_AURA.func_179348_c())
      {
        CustomColors.updateMyceliumFX(entityFx);
      }
      
      if (p_174974_1_ == EnumParticleTypes.PORTAL.func_179348_c())
      {
        CustomColors.updatePortalFX(entityFx);
      }
      
      if (p_174974_1_ == EnumParticleTypes.REDSTONE.func_179348_c())
      {
        CustomColors.updateReddustFX(entityFx, theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
      }
      
      return entityFx;
    }
    



    return null;
  }
  





  public void onEntityAdded(Entity entityIn)
  {
    RandomMobs.entityLoaded(entityIn, theWorld);
    
    if (Config.isDynamicLights())
    {
      DynamicLights.entityAdded(entityIn, this);
    }
  }
  




  public void onEntityRemoved(Entity entityIn)
  {
    if (Config.isDynamicLights())
    {
      DynamicLights.entityRemoved(entityIn, this);
    }
  }
  


  public void deleteAllDisplayLists() {}
  

  public void func_180440_a(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_)
  {
    switch (p_180440_1_)
    {
    case 1013: 
    case 1018: 
      if (mc.func_175606_aa() != null)
      {
        double var4 = p_180440_2_.getX() - mc.func_175606_aa().posX;
        double var6 = p_180440_2_.getY() - mc.func_175606_aa().posY;
        double var8 = p_180440_2_.getZ() - mc.func_175606_aa().posZ;
        double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
        double var12 = mc.func_175606_aa().posX;
        double var14 = mc.func_175606_aa().posY;
        double var16 = mc.func_175606_aa().posZ;
        
        if (var10 > 0.0D)
        {
          var12 += var4 / var10 * 2.0D;
          var14 += var6 / var10 * 2.0D;
          var16 += var8 / var10 * 2.0D;
        }
        
        if (p_180440_1_ == 1013)
        {
          theWorld.playSound(var12, var14, var16, "mob.wither.spawn", 1.0F, 1.0F, false);
        }
        else
        {
          theWorld.playSound(var12, var14, var16, "mob.enderdragon.end", 5.0F, 1.0F, false);
        }
      }
      break;
    }
    
  }
  
  public void func_180439_a(EntityPlayer p_180439_1_, int p_180439_2_, BlockPos p_180439_3_, int p_180439_4_)
  {
    Random var5 = theWorld.rand;
    











    switch (p_180439_2_)
    {
    case 1000: 
      theWorld.func_175731_a(p_180439_3_, "random.click", 1.0F, 1.0F, false);
      break;
    
    case 1001: 
      theWorld.func_175731_a(p_180439_3_, "random.click", 1.0F, 1.2F, false);
      break;
    
    case 1002: 
      theWorld.func_175731_a(p_180439_3_, "random.bow", 1.0F, 1.2F, false);
      break;
    
    case 1003: 
      theWorld.func_175731_a(p_180439_3_, "random.door_open", 1.0F, theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
      break;
    
    case 1004: 
      theWorld.func_175731_a(p_180439_3_, "random.fizz", 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);
      break;
    
    case 1005: 
      if ((Item.getItemById(p_180439_4_) instanceof ItemRecord))
      {
        theWorld.func_175717_a(p_180439_3_, "records." + getItemByIdrecordName);
      }
      else
      {
        theWorld.func_175717_a(p_180439_3_, null);
      }
      
      break;
    
    case 1006: 
      theWorld.func_175731_a(p_180439_3_, "random.door_close", 1.0F, theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
      break;
    
    case 1007: 
      theWorld.func_175731_a(p_180439_3_, "mob.ghast.charge", 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1008: 
      theWorld.func_175731_a(p_180439_3_, "mob.ghast.fireball", 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1009: 
      theWorld.func_175731_a(p_180439_3_, "mob.ghast.fireball", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1010: 
      theWorld.func_175731_a(p_180439_3_, "mob.zombie.wood", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1011: 
      theWorld.func_175731_a(p_180439_3_, "mob.zombie.metal", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1012: 
      theWorld.func_175731_a(p_180439_3_, "mob.zombie.woodbreak", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1014: 
      theWorld.func_175731_a(p_180439_3_, "mob.wither.shoot", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1015: 
      theWorld.func_175731_a(p_180439_3_, "mob.bat.takeoff", 0.05F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1016: 
      theWorld.func_175731_a(p_180439_3_, "mob.zombie.infect", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1017: 
      theWorld.func_175731_a(p_180439_3_, "mob.zombie.unfect", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
      break;
    
    case 1020: 
      theWorld.func_175731_a(p_180439_3_, "random.anvil_break", 1.0F, theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
      break;
    
    case 1021: 
      theWorld.func_175731_a(p_180439_3_, "random.anvil_use", 1.0F, theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
      break;
    
    case 1022: 
      theWorld.func_175731_a(p_180439_3_, "random.anvil_land", 0.3F, theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
      break;
    
    case 2000: 
      int var31 = p_180439_4_ % 3 - 1;
      int var8 = p_180439_4_ / 3 % 3 - 1;
      double var9 = p_180439_3_.getX() + var31 * 0.6D + 0.5D;
      double var11 = p_180439_3_.getY() + 0.5D;
      double var32 = p_180439_3_.getZ() + var8 * 0.6D + 0.5D;
      
      for (int var39 = 0; var39 < 10; var39++)
      {
        double var40 = var5.nextDouble() * 0.2D + 0.01D;
        double var41 = var9 + var31 * 0.01D + (var5.nextDouble() - 0.5D) * var8 * 0.5D;
        double var25 = var11 + (var5.nextDouble() - 0.5D) * 0.5D;
        double var27 = var32 + var8 * 0.01D + (var5.nextDouble() - 0.5D) * var31 * 0.5D;
        double var42 = var31 * var40 + var5.nextGaussian() * 0.01D;
        double var26 = -0.03D + var5.nextGaussian() * 0.01D;
        double var28 = var8 * var40 + var5.nextGaussian() * 0.01D;
        func_174972_a(EnumParticleTypes.SMOKE_NORMAL, var41, var25, var27, var42, var26, var28, new int[0]);
      }
      
      return;
    
    case 2001: 
      Block var6 = Block.getBlockById(p_180439_4_ & 0xFFF);
      
      if (var6.getMaterial() != Material.air)
      {
        mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(stepSound.getBreakSound()), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getFrequency() * 0.8F, p_180439_3_.getX() + 0.5F, p_180439_3_.getY() + 0.5F, p_180439_3_.getZ() + 0.5F));
      }
      
      mc.effectRenderer.func_180533_a(p_180439_3_, var6.getStateFromMeta(p_180439_4_ >> 12 & 0xFF));
      break;
    
    case 2002: 
      double var7 = p_180439_3_.getX();
      double var9 = p_180439_3_.getY();
      double var11 = p_180439_3_.getZ();
      
      for (int var13 = 0; var13 < 8; var13++)
      {
        func_174972_a(EnumParticleTypes.ITEM_CRACK, var7, var9, var11, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.potionitem), p_180439_4_ });
      }
      
      var13 = Items.potionitem.getColorFromDamage(p_180439_4_);
      float var14 = (var13 >> 16 & 0xFF) / 255.0F;
      float var15 = (var13 >> 8 & 0xFF) / 255.0F;
      float var16 = (var13 >> 0 & 0xFF) / 255.0F;
      EnumParticleTypes var17 = EnumParticleTypes.SPELL;
      
      if (Items.potionitem.isEffectInstant(p_180439_4_))
      {
        var17 = EnumParticleTypes.SPELL_INSTANT;
      }
      
      for (int var18 = 0; var18 < 100; var18++)
      {
        double var19 = var5.nextDouble() * 4.0D;
        double var21 = var5.nextDouble() * 3.141592653589793D * 2.0D;
        double var23 = Math.cos(var21) * var19;
        double var25 = 0.01D + var5.nextDouble() * 0.5D;
        double var27 = Math.sin(var21) * var19;
        EntityFX var29 = func_174974_b(var17.func_179348_c(), var17.func_179344_e(), var7 + var23 * 0.1D, var9 + 0.3D, var11 + var27 * 0.1D, var23, var25, var27, new int[0]);
        
        if (var29 != null)
        {
          float var30 = 0.75F + var5.nextFloat() * 0.25F;
          var29.setRBGColorF(var14 * var30, var15 * var30, var16 * var30);
          var29.multiplyVelocity((float)var19);
        }
      }
      
      theWorld.func_175731_a(p_180439_3_, "game.potion.smash", 1.0F, theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
      break;
    
    case 2003: 
      double var7 = p_180439_3_.getX() + 0.5D;
      double var9 = p_180439_3_.getY();
      double var11 = p_180439_3_.getZ() + 0.5D;
      
      for (int var13 = 0; var13 < 8; var13++)
      {
        func_174972_a(EnumParticleTypes.ITEM_CRACK, var7, var9, var11, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.ender_eye) });
      }
      
      for (double var32 = 0.0D; var32 < 6.283185307179586D; var32 += 0.15707963267948966D)
      {
        func_174972_a(EnumParticleTypes.PORTAL, var7 + Math.cos(var32) * 5.0D, var9 - 0.4D, var11 + Math.sin(var32) * 5.0D, Math.cos(var32) * -5.0D, 0.0D, Math.sin(var32) * -5.0D, new int[0]);
        func_174972_a(EnumParticleTypes.PORTAL, var7 + Math.cos(var32) * 5.0D, var9 - 0.4D, var11 + Math.sin(var32) * 5.0D, Math.cos(var32) * -7.0D, 0.0D, Math.sin(var32) * -7.0D, new int[0]);
      }
      
      return;
    
    case 2004: 
      for (int var18 = 0; var18 < 20; var18++)
      {
        double var19 = p_180439_3_.getX() + 0.5D + (theWorld.rand.nextFloat() - 0.5D) * 2.0D;
        double var21 = p_180439_3_.getY() + 0.5D + (theWorld.rand.nextFloat() - 0.5D) * 2.0D;
        double var23 = p_180439_3_.getZ() + 0.5D + (theWorld.rand.nextFloat() - 0.5D) * 2.0D;
        theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var19, var21, var23, 0.0D, 0.0D, 0.0D, new int[0]);
        theWorld.spawnParticle(EnumParticleTypes.FLAME, var19, var21, var23, 0.0D, 0.0D, 0.0D, new int[0]);
      }
      
      return;
    
    case 2005: 
      ItemDye.func_180617_a(theWorld, p_180439_3_, p_180439_4_);
    }
  }
  
  public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
  {
    if ((progress >= 0) && (progress < 10))
    {
      DestroyBlockProgress var4 = (DestroyBlockProgress)damagedBlocks.get(Integer.valueOf(breakerId));
      
      if ((var4 == null) || (var4.func_180246_b().getX() != pos.getX()) || (var4.func_180246_b().getY() != pos.getY()) || (var4.func_180246_b().getZ() != pos.getZ()))
      {
        var4 = new DestroyBlockProgress(breakerId, pos);
        damagedBlocks.put(Integer.valueOf(breakerId), var4);
      }
      
      var4.setPartialBlockDamage(progress);
      var4.setCloudUpdateTick(cloudTickCounter);
    }
    else
    {
      damagedBlocks.remove(Integer.valueOf(breakerId));
    }
  }
  
  public void func_174979_m()
  {
    displayListEntitiesDirty = true;
  }
  
  public void resetClouds()
  {
    cloudRenderer.reset();
  }
  
  public int getCountRenderers()
  {
    return field_175008_n.field_178164_f.length;
  }
  
  public int getCountActiveRenderers()
  {
    return glRenderLists.size();
  }
  
  public int getCountEntitiesRendered()
  {
    return countEntitiesRendered;
  }
  
  public int getCountTileEntitiesRendered()
  {
    return countTileEntitiesRendered;
  }
  
  public RenderChunk getRenderChunk(BlockPos pos)
  {
    return field_175008_n.func_178161_a(pos);
  }
  
  public RenderChunk getRenderChunk(RenderChunk renderChunk, EnumFacing facing)
  {
    if (renderChunk == null)
    {
      return null;
    }
    

    BlockPos pos = renderChunk.getPositionOffset16(facing);
    return field_175008_n.func_178161_a(pos);
  }
  

  public WorldClient getWorld()
  {
    return theWorld;
  }
  
  public static class ContainerLocalRenderInformation
  {
    final RenderChunk field_178036_a;
    final EnumFacing field_178034_b;
    final Set field_178035_c;
    final int field_178032_d;
    
    public ContainerLocalRenderInformation(RenderChunk p_i46248_2_, EnumFacing p_i46248_3_, int p_i46248_4_)
    {
      field_178035_c = EnumSet.noneOf(EnumFacing.class);
      field_178036_a = p_i46248_2_;
      field_178034_b = p_i46248_3_;
      field_178032_d = p_i46248_4_;
    }
    
    ContainerLocalRenderInformation(RenderChunk p_i46249_2_, EnumFacing p_i46249_3_, int p_i46249_4_, Object p_i46249_5_)
    {
      this(p_i46249_2_, p_i46249_3_, p_i46249_4_);
    }
  }
  
  static final class SwitchEnumUseage
  {
    static final int[] field_178037_a = new int[VertexFormatElement.EnumUseage.values().length];
    
    static
    {
      try {
        field_178037_a[VertexFormatElement.EnumUseage.POSITION.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        field_178037_a[VertexFormatElement.EnumUseage.UV.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        field_178037_a[VertexFormatElement.EnumUseage.COLOR.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
    }
    
    SwitchEnumUseage() {}
  }
}
