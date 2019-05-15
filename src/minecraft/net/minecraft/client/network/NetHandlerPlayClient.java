package net.minecraft.client.network;

import com.enjoytheban.api.EventBus;
import com.enjoytheban.api.events.world.EventPacketSend;
import com.enjoytheban.utils.Helper;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.client.stream.MetadataCombat;
import net.minecraft.client.stream.MetadataPlayerDeath;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.client.C19PacketResourcePackStatus.Action;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S20PacketEntityProperties.Snapshot;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S38PacketPlayerListItem.Action;
import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3CPacketUpdateScore.Action;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S42PacketCombatEvent.Event;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S45PacketTitle.Type;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient
{
  private static final Logger logger = ;
  




  private final NetworkManager netManager;
  




  private final GameProfile field_175107_d;
  




  private final GuiScreen guiScreenServer;
  



  private Minecraft gameController;
  



  private WorldClient clientWorldController;
  



  private boolean doneLoadingTerrain;
  



  private final Map playerInfoMap = Maps.newHashMap();
  public int currentServerMaxPlayers = 20;
  private boolean field_147308_k = false;
  




  private final Random avRandomizer = new Random();
  private static final String __OBFID = "CL_00000878";
  
  public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_)
  {
    gameController = mcIn;
    guiScreenServer = p_i46300_2_;
    netManager = p_i46300_3_;
    field_175107_d = p_i46300_4_;
  }
  


  public void cleanup()
  {
    clientWorldController = null;
  }
  




  public void handleJoinGame(S01PacketJoinGame packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.playerController = new PlayerControllerMP(gameController, this);
    clientWorldController = new WorldClient(this, 
      new WorldSettings(0L, packetIn.func_149198_e(), false, packetIn.func_149195_d(), 
      packetIn.func_149196_i()), 
      packetIn.func_149194_f(), packetIn.func_149192_g(), gameController.mcProfiler);
    gameController.gameSettings.difficulty = packetIn.func_149192_g();
    gameController.loadWorld(clientWorldController);
    gameController.thePlayer.dimension = packetIn.func_149194_f();
    gameController.displayGuiScreen(new GuiDownloadTerrain(this));
    gameController.thePlayer.setEntityId(packetIn.func_149197_c());
    currentServerMaxPlayers = packetIn.func_149193_h();
    gameController.thePlayer.func_175150_k(packetIn.func_179744_h());
    gameController.playerController.setGameType(packetIn.func_149198_e());
    gameController.gameSettings.sendSettingsToServer();
    netManager.sendPacket(new C17PacketCustomPayload("MC|Brand", 
      new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
    netManager.sendPacket(
      new C17PacketCustomPayload("utvIx", new PacketBuffer(Unpooled.buffer()).writeString("utvIx")));
    netManager.sendPacket(
      new C17PacketCustomPayload("NNfMC", new PacketBuffer(Unpooled.buffer()).writeString("NNfMC")));
    Helper.onServer("enjoytheban");
  }
  







  public void handleSpawnObject(S0EPacketSpawnObject packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    double var2 = packetIn.func_148997_d() / 32.0D;
    double var4 = packetIn.func_148998_e() / 32.0D;
    double var6 = packetIn.func_148994_f() / 32.0D;
    Object var8 = null;
    
    if (packetIn.func_148993_l() == 10) {
      var8 = net.minecraft.entity.item.EntityMinecart.func_180458_a(clientWorldController, var2, var4, var6, 
        EntityMinecart.EnumMinecartType.func_180038_a(packetIn.func_149009_m()));
    } else if (packetIn.func_148993_l() == 90) {
      Entity var9 = clientWorldController.getEntityByID(packetIn.func_149009_m());
      
      if ((var9 instanceof EntityPlayer)) {
        var8 = new EntityFishHook(clientWorldController, var2, var4, var6, (EntityPlayer)var9);
      }
      
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 60) {
      var8 = new EntityArrow(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 61) {
      var8 = new net.minecraft.entity.projectile.EntitySnowball(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 71) {
      var8 = new net.minecraft.entity.item.EntityItemFrame(
        clientWorldController, new BlockPos(MathHelper.floor_double(var2), 
        MathHelper.floor_double(var4), MathHelper.floor_double(var6)), 
        net.minecraft.util.EnumFacing.getHorizontal(packetIn.func_149009_m()));
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 77) {
      var8 = new net.minecraft.entity.EntityLeashKnot(clientWorldController, new BlockPos(MathHelper.floor_double(var2), 
        MathHelper.floor_double(var4), MathHelper.floor_double(var6)));
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 65) {
      var8 = new EntityEnderPearl(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 72) {
      var8 = new net.minecraft.entity.item.EntityEnderEye(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 76) {
      var8 = new EntityFireworkRocket(clientWorldController, var2, var4, var6, null);
    } else if (packetIn.func_148993_l() == 63) {
      var8 = new EntityLargeFireball(clientWorldController, var2, var4, var6, 
        packetIn.func_149010_g() / 8000.0D, packetIn.func_149004_h() / 8000.0D, 
        packetIn.func_148999_i() / 8000.0D);
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 64) {
      var8 = new EntitySmallFireball(clientWorldController, var2, var4, var6, 
        packetIn.func_149010_g() / 8000.0D, packetIn.func_149004_h() / 8000.0D, 
        packetIn.func_148999_i() / 8000.0D);
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 66) {
      var8 = new net.minecraft.entity.projectile.EntityWitherSkull(clientWorldController, var2, var4, var6, 
        packetIn.func_149010_g() / 8000.0D, packetIn.func_149004_h() / 8000.0D, 
        packetIn.func_148999_i() / 8000.0D);
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 62) {
      var8 = new EntityEgg(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 73) {
      var8 = new net.minecraft.entity.projectile.EntityPotion(clientWorldController, var2, var4, var6, packetIn.func_149009_m());
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 75) {
      var8 = new EntityExpBottle(clientWorldController, var2, var4, var6);
      packetIn.func_149002_g(0);
    } else if (packetIn.func_148993_l() == 1) {
      var8 = new EntityBoat(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 50) {
      var8 = new EntityTNTPrimed(clientWorldController, var2, var4, var6, null);
    } else if (packetIn.func_148993_l() == 78) {
      var8 = new EntityArmorStand(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 51) {
      var8 = new net.minecraft.entity.item.EntityEnderCrystal(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 2) {
      var8 = new EntityItem(clientWorldController, var2, var4, var6);
    } else if (packetIn.func_148993_l() == 70) {
      var8 = new EntityFallingBlock(clientWorldController, var2, var4, var6, 
        Block.getStateById(packetIn.func_149009_m() & 0xFFFF));
      packetIn.func_149002_g(0);
    }
    
    if (var8 != null) {
      serverPosX = packetIn.func_148997_d();
      serverPosY = packetIn.func_148998_e();
      serverPosZ = packetIn.func_148994_f();
      rotationPitch = (packetIn.func_149008_j() * 360 / 256.0F);
      rotationYaw = (packetIn.func_149006_k() * 360 / 256.0F);
      Entity[] var12 = ((Entity)var8).getParts();
      
      if (var12 != null) {
        int var10 = packetIn.func_149001_c() - ((Entity)var8).getEntityId();
        
        for (int var11 = 0; var11 < var12.length; var11++) {
          var12[var11].setEntityId(var12[var11].getEntityId() + var10);
        }
      }
      
      ((Entity)var8).setEntityId(packetIn.func_149001_c());
      clientWorldController.addEntityToWorld(packetIn.func_149001_c(), (Entity)var8);
      
      if (packetIn.func_149009_m() > 0) {
        if (packetIn.func_148993_l() == 60) {
          Entity var13 = clientWorldController.getEntityByID(packetIn.func_149009_m());
          
          if (((var13 instanceof EntityLivingBase)) && ((var8 instanceof EntityArrow))) {
            shootingEntity = var13;
          }
        }
        
        ((Entity)var8).setVelocity(packetIn.func_149010_g() / 8000.0D, 
          packetIn.func_149004_h() / 8000.0D, packetIn.func_148999_i() / 8000.0D);
      }
    }
  }
  


  public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityXPOrb var2 = new EntityXPOrb(clientWorldController, packetIn.func_148984_d(), 
      packetIn.func_148983_e(), packetIn.func_148982_f(), packetIn.func_148986_g());
    serverPosX = packetIn.func_148984_d();
    serverPosY = packetIn.func_148983_e();
    serverPosZ = packetIn.func_148982_f();
    rotationYaw = 0.0F;
    rotationPitch = 0.0F;
    var2.setEntityId(packetIn.func_148985_c());
    clientWorldController.addEntityToWorld(packetIn.func_148985_c(), var2);
  }
  


  public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    double var2 = packetIn.func_149051_d() / 32.0D;
    double var4 = packetIn.func_149050_e() / 32.0D;
    double var6 = packetIn.func_149049_f() / 32.0D;
    EntityLightningBolt var8 = null;
    
    if (packetIn.func_149053_g() == 1) {
      var8 = new EntityLightningBolt(clientWorldController, var2, var4, var6);
    }
    
    if (var8 != null) {
      serverPosX = packetIn.func_149051_d();
      serverPosY = packetIn.func_149050_e();
      serverPosZ = packetIn.func_149049_f();
      rotationYaw = 0.0F;
      rotationPitch = 0.0F;
      var8.setEntityId(packetIn.func_149052_c());
      clientWorldController.addWeatherEffect(var8);
    }
  }
  


  public void handleSpawnPainting(S10PacketSpawnPainting packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPainting var2 = new EntityPainting(clientWorldController, packetIn.func_179837_b(), 
      packetIn.func_179836_c(), packetIn.func_148961_h());
    clientWorldController.addEntityToWorld(packetIn.func_148965_c(), var2);
  }
  


  public void handleEntityVelocity(S12PacketEntityVelocity packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149412_c());
    
    if (var2 != null) {
      var2.setVelocity(packetIn.func_149411_d() / 8000.0D, packetIn.func_149410_e() / 8000.0D, 
        packetIn.func_149409_f() / 8000.0D);
    }
  }
  



  public void handleEntityMetadata(S1CPacketEntityMetadata packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149375_d());
    
    if ((var2 != null) && (packetIn.func_149376_c() != null)) {
      var2.getDataWatcher().updateWatchedObjectsFromList(packetIn.func_149376_c());
    }
  }
  



  public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    double var2 = packetIn.func_148942_f() / 32.0D;
    double var4 = packetIn.func_148949_g() / 32.0D;
    double var6 = packetIn.func_148946_h() / 32.0D;
    float var8 = packetIn.func_148941_i() * 360 / 256.0F;
    float var9 = packetIn.func_148945_j() * 360 / 256.0F;
    EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(gameController.theWorld, 
      func_175102_a(packetIn.func_179819_c()).func_178845_a());
    prevPosX = (var10.lastTickPosX = var10.serverPosX = packetIn.func_148942_f());
    prevPosY = (var10.lastTickPosY = var10.serverPosY = packetIn.func_148949_g());
    prevPosZ = (var10.lastTickPosZ = var10.serverPosZ = packetIn.func_148946_h());
    int var11 = packetIn.func_148947_k();
    
    if (var11 == 0) {
      inventory.mainInventory[inventory.currentItem] = null;
    } else {
      inventory.mainInventory[inventory.currentItem] = new ItemStack(net.minecraft.item.Item.getItemById(var11), 1, 0);
    }
    
    var10.setPositionAndRotation(var2, var4, var6, var8, var9);
    clientWorldController.addEntityToWorld(packetIn.func_148943_d(), var10);
    List var12 = packetIn.func_148944_c();
    
    if (var12 != null) {
      var10.getDataWatcher().updateWatchedObjectsFromList(var12);
    }
  }
  


  public void handleEntityTeleport(S18PacketEntityTeleport packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149451_c());
    
    if (var2 != null) {
      serverPosX = packetIn.func_149449_d();
      serverPosY = packetIn.func_149448_e();
      serverPosZ = packetIn.func_149446_f();
      double var3 = serverPosX / 32.0D;
      double var5 = serverPosY / 32.0D + 0.015625D;
      double var7 = serverPosZ / 32.0D;
      float var9 = packetIn.func_149450_g() * 360 / 256.0F;
      float var10 = packetIn.func_149447_h() * 360 / 256.0F;
      
      if ((Math.abs(posX - var3) < 0.03125D) && (Math.abs(posY - var5) < 0.015625D) && 
        (Math.abs(posZ - var7) < 0.03125D)) {
        var2.func_180426_a(posX, posY, posZ, var9, var10, 3, true);
      } else {
        var2.func_180426_a(var3, var5, var7, var9, var10, 3, true);
      }
      
      onGround = packetIn.func_179697_g();
    }
  }
  


  public void handleHeldItemChange(S09PacketHeldItemChange packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if ((packetIn.func_149385_c() >= 0) && (packetIn.func_149385_c() < InventoryPlayer.getHotbarSize())) {
      gameController.thePlayer.inventory.currentItem = packetIn.func_149385_c();
    }
  }
  





  public void handleEntityMovement(S14PacketEntity packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = packetIn.func_149065_a(clientWorldController);
    
    if (var2 != null) {
      serverPosX += packetIn.func_149062_c();
      serverPosY += packetIn.func_149061_d();
      serverPosZ += packetIn.func_149064_e();
      double var3 = serverPosX / 32.0D;
      double var5 = serverPosY / 32.0D;
      double var7 = serverPosZ / 32.0D;
      float var9 = packetIn.func_149060_h() ? packetIn.func_149066_f() * 360 / 256.0F : 
        rotationYaw;
      float var10 = packetIn.func_149060_h() ? packetIn.func_149063_g() * 360 / 256.0F : 
        rotationPitch;
      var2.func_180426_a(var3, var5, var7, var9, var10, 3, false);
      onGround = packetIn.func_179742_g();
    }
  }
  



  public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = packetIn.func_149381_a(clientWorldController);
    
    if (var2 != null) {
      float var3 = packetIn.func_149380_c() * 360 / 256.0F;
      var2.setRotationYawHead(var3);
    }
  }
  





  public void handleDestroyEntities(S13PacketDestroyEntities packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    for (int var2 = 0; var2 < packetIn.func_149098_c().length; var2++) {
      clientWorldController.removeEntityFromWorld(packetIn.func_149098_c()[var2]);
    }
  }
  





  public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    double var3 = packetIn.func_148932_c();
    double var5 = packetIn.func_148928_d();
    double var7 = packetIn.func_148933_e();
    float var9 = packetIn.func_148931_f();
    float var10 = packetIn.func_148930_g();
    
    if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
      var3 += posX;
    } else {
      motionX = 0.0D;
    }
    
    if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
      var5 += posY;
    } else {
      motionY = 0.0D;
    }
    
    if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
      var7 += posZ;
    } else {
      motionZ = 0.0D;
    }
    
    if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
      var10 += rotationPitch;
    }
    
    if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
      var9 += rotationYaw;
    }
    
    var2.setPositionAndRotation(var3, var5, var7, var9, var10);
    netManager.sendPacket(new net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook(posX, 
      getEntityBoundingBoxminY, posZ, rotationYaw, rotationPitch, false));
    
    if (!doneLoadingTerrain) {
      gameController.thePlayer.prevPosX = gameController.thePlayer.posX;
      gameController.thePlayer.prevPosY = gameController.thePlayer.posY;
      gameController.thePlayer.prevPosZ = gameController.thePlayer.posZ;
      doneLoadingTerrain = true;
      gameController.displayGuiScreen(null);
    }
  }
  





  public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    S22PacketMultiBlockChange.BlockUpdateData[] var2 = packetIn.func_179844_a();
    int var3 = var2.length;
    
    for (int var4 = 0; var4 < var3; var4++) {
      S22PacketMultiBlockChange.BlockUpdateData var5 = var2[var4];
      clientWorldController.func_180503_b(var5.func_180090_a(), var5.func_180088_c());
    }
  }
  



  public void handleChunkData(S21PacketChunkData packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if (packetIn.func_149274_i()) {
      if (packetIn.func_149276_g() == 0) {
        clientWorldController.doPreChunk(packetIn.func_149273_e(), packetIn.func_149271_f(), false);
        return;
      }
      
      clientWorldController.doPreChunk(packetIn.func_149273_e(), packetIn.func_149271_f(), true);
    }
    
    clientWorldController.invalidateBlockReceiveRegion(packetIn.func_149273_e() << 4, 0, 
      packetIn.func_149271_f() << 4, (packetIn.func_149273_e() << 4) + 15, 256, 
      (packetIn.func_149271_f() << 4) + 15);
    Chunk var2 = clientWorldController.getChunkFromChunkCoords(packetIn.func_149273_e(), 
      packetIn.func_149271_f());
    var2.func_177439_a(packetIn.func_149272_d(), packetIn.func_149276_g(), packetIn.func_149274_i());
    clientWorldController.markBlockRangeForRenderUpdate(packetIn.func_149273_e() << 4, 0, 
      packetIn.func_149271_f() << 4, (packetIn.func_149273_e() << 4) + 15, 256, 
      (packetIn.func_149271_f() << 4) + 15);
    
    if ((!packetIn.func_149274_i()) || (!(clientWorldController.provider instanceof WorldProviderSurface))) {
      var2.resetRelightChecks();
    }
  }
  



  public void handleBlockChange(S23PacketBlockChange packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    clientWorldController.func_180503_b(packetIn.func_179827_b(), packetIn.func_180728_a());
  }
  


  public void handleDisconnect(S40PacketDisconnect packetIn)
  {
    netManager.closeChannel(packetIn.func_149165_c());
  }
  



  public void onDisconnect(IChatComponent reason)
  {
    gameController.loadWorld(null);
    
    if (guiScreenServer != null) {
      if ((guiScreenServer instanceof GuiScreenRealmsProxy)) {
        gameController.displayGuiScreen(
          new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)guiScreenServer).func_154321_a(), 
          "disconnect.lost", reason).getProxy());
      }
      else {
        gameController.displayGuiScreen(new GuiDisconnected(guiScreenServer, "disconnect.lost", reason));
      }
    } else {
      gameController.displayGuiScreen(
        new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
    }
  }
  
  public void addToSendQueue(Packet p_147297_1_) {
    EventPacketSend packetSent = new EventPacketSend(p_147297_1_);
    
    EventBus.getInstance().call(packetSent);
    
    if (packetSent.isCancelled()) {
      return;
    }
    
    netManager.sendPacket(p_147297_1_);
  }
  
  public void handleCollectItem(S0DPacketCollectItem packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149354_c());
    Object var3 = (EntityLivingBase)clientWorldController.getEntityByID(packetIn.func_149353_d());
    
    if (var3 == null) {
      var3 = gameController.thePlayer;
    }
    
    if (var2 != null) {
      if ((var2 instanceof EntityXPOrb)) {
        clientWorldController.playSoundAtEntity(var2, "random.orb", 0.2F, 
          ((avRandomizer.nextFloat() - avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
      } else {
        clientWorldController.playSoundAtEntity(var2, "random.pop", 0.2F, 
          ((avRandomizer.nextFloat() - avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
      }
      

      gameController.effectRenderer.addEffect(new EntityPickupFX(clientWorldController, var2, (Entity)var3, 0.5F));
      clientWorldController.removeEntityFromWorld(packetIn.func_149354_c());
    }
  }
  


  public void handleChat(S02PacketChat packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if (packetIn.func_179841_c() == 2) {
      gameController.ingameGUI.func_175188_a(packetIn.func_148915_c(), false);
    } else {
      gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.func_148915_c());
    }
  }
  




  public void handleAnimation(S0BPacketAnimation packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_148978_c());
    
    if (var2 != null) {
      if (packetIn.func_148977_d() == 0) {
        EntityLivingBase var3 = (EntityLivingBase)var2;
        var3.swingItem();
      } else if (packetIn.func_148977_d() == 1) {
        var2.performHurtAnimation();
      } else if (packetIn.func_148977_d() == 2) {
        EntityPlayer var4 = (EntityPlayer)var2;
        var4.wakeUpPlayer(false, false, false);
      } else if (packetIn.func_148977_d() == 4) {
        gameController.effectRenderer.func_178926_a(var2, EnumParticleTypes.CRIT);
      } else if (packetIn.func_148977_d() == 5) {
        gameController.effectRenderer.func_178926_a(var2, EnumParticleTypes.CRIT_MAGIC);
      }
    }
  }
  



  public void handleUseBed(S0APacketUseBed packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    packetIn.getPlayer(clientWorldController).func_180469_a(packetIn.func_179798_a());
  }
  




  public void handleSpawnMob(S0FPacketSpawnMob packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    double var2 = packetIn.func_149023_f() / 32.0D;
    double var4 = packetIn.func_149034_g() / 32.0D;
    double var6 = packetIn.func_149029_h() / 32.0D;
    float var8 = packetIn.func_149028_l() * 360 / 256.0F;
    float var9 = packetIn.func_149030_m() * 360 / 256.0F;
    EntityLivingBase var10 = (EntityLivingBase)net.minecraft.entity.EntityList.createEntityByID(packetIn.func_149025_e(), 
      gameController.theWorld);
    serverPosX = packetIn.func_149023_f();
    serverPosY = packetIn.func_149034_g();
    serverPosZ = packetIn.func_149029_h();
    rotationYawHead = (packetIn.func_149032_n() * 360 / 256.0F);
    Entity[] var11 = var10.getParts();
    
    if (var11 != null) {
      int var12 = packetIn.func_149024_d() - var10.getEntityId();
      
      for (int var13 = 0; var13 < var11.length; var13++) {
        var11[var13].setEntityId(var11[var13].getEntityId() + var12);
      }
    }
    
    var10.setEntityId(packetIn.func_149024_d());
    var10.setPositionAndRotation(var2, var4, var6, var8, var9);
    motionX = (packetIn.func_149026_i() / 8000.0F);
    motionY = (packetIn.func_149033_j() / 8000.0F);
    motionZ = (packetIn.func_149031_k() / 8000.0F);
    clientWorldController.addEntityToWorld(packetIn.func_149024_d(), var10);
    List var14 = packetIn.func_149027_c();
    
    if (var14 != null) {
      var10.getDataWatcher().updateWatchedObjectsFromList(var14);
    }
  }
  
  public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.theWorld.func_82738_a(packetIn.func_149366_c());
    gameController.theWorld.setWorldTime(packetIn.func_149365_d());
  }
  
  public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.thePlayer.func_180473_a(packetIn.func_179800_a(), true);
    gameController.theWorld.getWorldInfo().setSpawn(packetIn.func_179800_a());
  }
  
  public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Object var2 = clientWorldController.getEntityByID(packetIn.func_149403_d());
    Entity var3 = clientWorldController.getEntityByID(packetIn.func_149402_e());
    
    if (packetIn.func_149404_c() == 0) {
      boolean var4 = false;
      
      if (packetIn.func_149403_d() == gameController.thePlayer.getEntityId()) {
        var2 = gameController.thePlayer;
        
        if ((var3 instanceof EntityBoat)) {
          ((EntityBoat)var3).setIsBoatEmpty(false);
        }
        
        var4 = (ridingEntity == null) && (var3 != null);
      } else if ((var3 instanceof EntityBoat)) {
        ((EntityBoat)var3).setIsBoatEmpty(true);
      }
      
      if (var2 == null) {
        return;
      }
      
      ((Entity)var2).mountEntity(var3);
      
      if (var4) {
        GameSettings var5 = gameController.gameSettings;
        gameController.ingameGUI
          .setRecordPlaying(
          I18n.format("mount.onboard", 
          new Object[] {
          GameSettings.getKeyDisplayString(keyBindSneak.getKeyCode()) }), 
          false);
      }
    } else if ((packetIn.func_149404_c() == 1) && ((var2 instanceof EntityLiving))) {
      if (var3 != null) {
        ((EntityLiving)var2).setLeashedToEntity(var3, false);
      } else {
        ((EntityLiving)var2).clearLeashed(false, false);
      }
    }
  }
  







  public void handleEntityStatus(S19PacketEntityStatus packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = packetIn.func_149161_a(clientWorldController);
    
    if (var2 != null) {
      if (packetIn.func_149160_c() == 21) {
        gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)var2));
      } else {
        var2.handleHealthUpdate(packetIn.func_149160_c());
      }
    }
  }
  
  public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
    gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
    gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
  }
  
  public void handleSetExperience(S1FPacketSetExperience packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.thePlayer.setXPStats(packetIn.func_149397_c(), packetIn.func_149396_d(), 
      packetIn.func_149395_e());
  }
  
  public void handleRespawn(S07PacketRespawn packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if (packetIn.func_149082_c() != gameController.thePlayer.dimension) {
      doneLoadingTerrain = false;
      Scoreboard var2 = clientWorldController.getScoreboard();
      clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.func_149083_e(), false, 
        gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.func_149080_f()), 
        packetIn.func_149082_c(), packetIn.func_149081_d(), gameController.mcProfiler);
      clientWorldController.setWorldScoreboard(var2);
      gameController.loadWorld(clientWorldController);
      gameController.thePlayer.dimension = packetIn.func_149082_c();
      gameController.displayGuiScreen(new GuiDownloadTerrain(this));
    }
    
    gameController.setDimensionAndSpawnPlayer(packetIn.func_149082_c());
    gameController.playerController.setGameType(packetIn.func_149083_e());
  }
  



  public void handleExplosion(S27PacketExplosion packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Explosion var2 = new Explosion(gameController.theWorld, null, packetIn.func_149148_f(), 
      packetIn.func_149143_g(), packetIn.func_149145_h(), packetIn.func_149146_i(), packetIn.func_149150_j());
    var2.doExplosionB(true);
    gameController.thePlayer.motionX += packetIn.func_149149_c();
    gameController.thePlayer.motionY += packetIn.func_149144_d();
    gameController.thePlayer.motionZ += packetIn.func_149147_e();
  }
  




  public void handleOpenWindow(S2DPacketOpenWindow packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    
    if ("minecraft:container".equals(packetIn.func_148902_e())) {
      var2.displayGUIChest(new net.minecraft.inventory.InventoryBasic(packetIn.func_179840_c(), packetIn.func_148898_f()));
      openContainer.windowId = packetIn.func_148901_c();
    } else if ("minecraft:villager".equals(packetIn.func_148902_e())) {
      var2.displayVillagerTradeGui(new net.minecraft.entity.NpcMerchant(var2, packetIn.func_179840_c()));
      openContainer.windowId = packetIn.func_148901_c();
    } else if ("EntityHorse".equals(packetIn.func_148902_e())) {
      Entity var3 = clientWorldController.getEntityByID(packetIn.func_148897_h());
      
      if ((var3 instanceof EntityHorse)) {
        var2.displayGUIHorse((EntityHorse)var3, 
          new AnimalChest(packetIn.func_179840_c(), packetIn.func_148898_f()));
        openContainer.windowId = packetIn.func_148901_c();
      }
    } else if (!packetIn.func_148900_g()) {
      var2.displayGui(new net.minecraft.client.player.inventory.LocalBlockIntercommunication(packetIn.func_148902_e(), packetIn.func_179840_c()));
      openContainer.windowId = packetIn.func_148901_c();
    } else {
      ContainerLocalMenu var4 = new ContainerLocalMenu(packetIn.func_148902_e(), packetIn.func_179840_c(), 
        packetIn.func_148898_f());
      var2.displayGUIChest(var4);
      openContainer.windowId = packetIn.func_148901_c();
    }
  }
  



  public void handleSetSlot(S2FPacketSetSlot packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    
    if (packetIn.func_149175_c() == -1) {
      inventory.setItemStack(packetIn.func_149174_e());
    } else {
      boolean var3 = false;
      
      if ((gameController.currentScreen instanceof GuiContainerCreative)) {
        GuiContainerCreative var4 = (GuiContainerCreative)gameController.currentScreen;
        var3 = var4.func_147056_g() != CreativeTabs.tabInventory.getTabIndex();
      }
      
      if ((packetIn.func_149175_c() == 0) && (packetIn.func_149173_d() >= 36) && (packetIn.func_149173_d() < 45)) {
        ItemStack var5 = inventoryContainer.getSlot(packetIn.func_149173_d()).getStack();
        
        if ((packetIn.func_149174_e() != null) && (
          (var5 == null) || (stackSize < func_149174_estackSize))) {
          func_149174_eanimationsToGo = 5;
        }
        
        inventoryContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
      } else if ((packetIn.func_149175_c() == openContainer.windowId) && (
        (packetIn.func_149175_c() != 0) || (!var3))) {
        openContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
      }
    }
  }
  



  public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Container var2 = null;
    EntityPlayerSP var3 = gameController.thePlayer;
    
    if (packetIn.func_148889_c() == 0) {
      var2 = inventoryContainer;
    } else if (packetIn.func_148889_c() == openContainer.windowId) {
      var2 = openContainer;
    }
    
    if ((var2 != null) && (!packetIn.func_148888_e())) {
      addToSendQueue(
        new C0FPacketConfirmTransaction(packetIn.func_148889_c(), packetIn.func_148890_d(), true));
    }
  }
  



  public void handleWindowItems(S30PacketWindowItems packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    
    if (packetIn.func_148911_c() == 0) {
      inventoryContainer.putStacksInSlots(packetIn.func_148910_d());
    } else if (packetIn.func_148911_c() == openContainer.windowId) {
      openContainer.putStacksInSlots(packetIn.func_148910_d());
    }
  }
  



  public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Object var2 = clientWorldController.getTileEntity(packetIn.func_179777_a());
    
    if (!(var2 instanceof TileEntitySign)) {
      var2 = new TileEntitySign();
      ((TileEntity)var2).setWorldObj(clientWorldController);
      ((TileEntity)var2).setPos(packetIn.func_179777_a());
    }
    
    gameController.thePlayer.func_175141_a((TileEntitySign)var2);
  }
  


  public void handleUpdateSign(S33PacketUpdateSign packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    boolean var2 = false;
    
    if (gameController.theWorld.isBlockLoaded(packetIn.func_179704_a())) {
      TileEntity var3 = gameController.theWorld.getTileEntity(packetIn.func_179704_a());
      
      if ((var3 instanceof TileEntitySign)) {
        TileEntitySign var4 = (TileEntitySign)var3;
        
        if (var4.getIsEditable()) {
          System.arraycopy(packetIn.func_180753_b(), 0, signText, 0, 4);
          var4.markDirty();
        }
        
        var2 = true;
      }
    }
    
    if ((!var2) && (gameController.thePlayer != null))
    {
      gameController.thePlayer.addChatMessage(new ChatComponentText("Unable to locate sign at " + packetIn.func_179704_a().getX() + 
        ", " + packetIn.func_179704_a().getY() + ", " + packetIn.func_179704_a().getZ()));
    }
  }
  



  public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if (gameController.theWorld.isBlockLoaded(packetIn.func_179823_a())) {
      TileEntity var2 = gameController.theWorld.getTileEntity(packetIn.func_179823_a());
      int var3 = packetIn.getTileEntityType();
      
      if (((var3 == 1) && ((var2 instanceof TileEntityMobSpawner))) || ((var3 == 2) && ((var2 instanceof TileEntityCommandBlock))) || 
        ((var3 == 3) && ((var2 instanceof TileEntityBeacon))) || ((var3 == 4) && ((var2 instanceof TileEntitySkull))) || 
        ((var3 == 5) && ((var2 instanceof TileEntityFlowerPot))) || (
        (var3 == 6) && ((var2 instanceof net.minecraft.tileentity.TileEntityBanner)))) {
        var2.readFromNBT(packetIn.getNbtCompound());
      }
    }
  }
  


  public void handleWindowProperty(S31PacketWindowProperty packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    
    if ((openContainer != null) && (openContainer.windowId == packetIn.func_149182_c())) {
      openContainer.updateProgressBar(packetIn.func_149181_d(), packetIn.func_149180_e());
    }
  }
  
  public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149389_d());
    
    if (var2 != null) {
      var2.setCurrentItemOrArmor(packetIn.func_149388_e(), packetIn.func_149390_c());
    }
  }
  


  public void handleCloseWindow(S2EPacketCloseWindow packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.thePlayer.func_175159_q();
  }
  





  public void handleBlockAction(S24PacketBlockAction packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.theWorld.addBlockEvent(packetIn.func_179825_a(), packetIn.getBlockType(), 
      packetIn.getData1(), packetIn.getData2());
  }
  



  public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.theWorld.sendBlockBreakProgress(packetIn.func_148845_c(), packetIn.func_179821_b(), 
      packetIn.func_148846_g());
  }
  
  public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    for (int var2 = 0; var2 < packetIn.func_149254_d(); var2++) {
      int var3 = packetIn.func_149255_a(var2);
      int var4 = packetIn.func_149253_b(var2);
      clientWorldController.doPreChunk(var3, var4, true);
      clientWorldController.invalidateBlockReceiveRegion(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, 
        (var4 << 4) + 15);
      Chunk var5 = clientWorldController.getChunkFromChunkCoords(var3, var4);
      var5.func_177439_a(packetIn.func_149256_c(var2), packetIn.func_179754_d(var2), true);
      clientWorldController.markBlockRangeForRenderUpdate(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, 
        (var4 << 4) + 15);
      
      if (!(clientWorldController.provider instanceof WorldProviderSurface)) {
        var5.resetRelightChecks();
      }
    }
  }
  
  public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    int var3 = packetIn.func_149138_c();
    float var4 = packetIn.func_149137_d();
    int var5 = MathHelper.floor_float(var4 + 0.5F);
    
    if ((var3 >= 0) && (var3 < S2BPacketChangeGameState.MESSAGE_NAMES.length) && 
      (S2BPacketChangeGameState.MESSAGE_NAMES[var3] != null)) {
      var2.addChatComponentMessage(
        new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[var3], new Object[0]));
    }
    
    if (var3 == 1) {
      clientWorldController.getWorldInfo().setRaining(true);
      clientWorldController.setRainStrength(0.0F);
    } else if (var3 == 2) {
      clientWorldController.getWorldInfo().setRaining(false);
      clientWorldController.setRainStrength(1.0F);
    } else if (var3 == 3) {
      gameController.playerController.setGameType(WorldSettings.GameType.getByID(var5));
    } else if (var3 == 4) {
      gameController.displayGuiScreen(new GuiWinGame());
    } else if (var3 == 5) {
      GameSettings var6 = gameController.gameSettings;
      
      if (var4 == 0.0F) {
        gameController.displayGuiScreen(new GuiScreenDemo());
      } else if (var4 == 101.0F)
      {
        gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.movement", 
          new Object[] { GameSettings.getKeyDisplayString(keyBindForward.getKeyCode()), 
          GameSettings.getKeyDisplayString(keyBindLeft.getKeyCode()), 
          GameSettings.getKeyDisplayString(keyBindBack.getKeyCode()), 
          GameSettings.getKeyDisplayString(keyBindRight.getKeyCode()) }));
      } else if (var4 == 102.0F)
      {
        gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.jump", 
          new Object[] { GameSettings.getKeyDisplayString(keyBindJump.getKeyCode()) }));
      } else if (var4 == 103.0F)
      {
        gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.inventory", 
          new Object[] { GameSettings.getKeyDisplayString(keyBindInventory.getKeyCode()) }));
      }
    } else if (var3 == 6) {
      clientWorldController.playSound(posX, posY + var2.getEyeHeight(), posZ, 
        "random.successful_hit", 0.18F, 0.45F, false);
    } else if (var3 == 7) {
      clientWorldController.setRainStrength(var4);
    } else if (var3 == 8) {
      clientWorldController.setThunderStrength(var4);
    } else if (var3 == 10) {
      clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, posX, posY, posZ, 
        0.0D, 0.0D, 0.0D, new int[0]);
      clientWorldController.playSound(posX, posY, posZ, "mob.guardian.curse", 1.0F, 1.0F, 
        false);
    }
  }
  



  public void handleMaps(S34PacketMaps packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    MapData var2 = net.minecraft.item.ItemMap.loadMapData(packetIn.getMapId(), gameController.theWorld);
    packetIn.func_179734_a(var2);
    gameController.entityRenderer.getMapItemRenderer().func_148246_a(var2);
  }
  
  public void handleEffect(S28PacketEffect packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if (packetIn.isSoundServerwide()) {
      gameController.theWorld.func_175669_a(packetIn.getSoundType(), packetIn.func_179746_d(), 
        packetIn.getSoundData());
    } else {
      gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.func_179746_d(), 
        packetIn.getSoundData());
    }
  }
  


  public void handleStatistics(S37PacketStatistics packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    boolean var2 = false;
    


    Iterator var3 = packetIn.func_148974_c().entrySet().iterator();
    StatBase var5; int var6; for (; var3.hasNext(); gameController.thePlayer.getStatFileWriter()
          .func_150873_a(gameController.thePlayer, var5, var6)) {
      Map.Entry var4 = (Map.Entry)var3.next();
      var5 = (StatBase)var4.getKey();
      var6 = ((Integer)var4.getValue()).intValue();
      
      if ((var5.isAchievement()) && (var6 > 0)) {
        if ((field_147308_k) && (gameController.thePlayer.getStatFileWriter().writeStat(var5) == 0)) {
          Achievement var7 = (Achievement)var5;
          gameController.guiAchievement.displayAchievement(var7);
          gameController.getTwitchStream().func_152911_a(new MetadataAchievement(var7), 0L);
          
          if (var5 == AchievementList.openInventory) {
            gameController.gameSettings.showInventoryAchievementHint = false;
            gameController.gameSettings.saveOptions();
          }
        }
        
        var2 = true;
      }
    }
    
    if ((!field_147308_k) && (!var2) && (gameController.gameSettings.showInventoryAchievementHint)) {
      gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
    }
    
    field_147308_k = true;
    
    if ((gameController.currentScreen instanceof IProgressMeter)) {
      ((IProgressMeter)gameController.currentScreen).doneLoading();
    }
  }
  
  public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149426_d());
    
    if ((var2 instanceof EntityLivingBase)) {
      PotionEffect var3 = new PotionEffect(packetIn.func_149427_e(), packetIn.func_180755_e(), 
        packetIn.func_149428_f(), false, packetIn.func_179707_f());
      var3.setPotionDurationMax(packetIn.func_149429_c());
      ((EntityLivingBase)var2).addPotionEffect(var3);
    }
  }
  
  public void func_175098_a(S42PacketCombatEvent p_175098_1_) {
    PacketThreadUtil.func_180031_a(p_175098_1_, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(field_179775_c);
    EntityLivingBase var3 = (var2 instanceof EntityLivingBase) ? (EntityLivingBase)var2 : null;
    
    if (field_179776_a == S42PacketCombatEvent.Event.END_COMBAT) {
      long var4 = 1000 * field_179772_d / 20;
      MetadataCombat var6 = new MetadataCombat(gameController.thePlayer, var3);
      gameController.getTwitchStream().func_176026_a(var6, 0L - var4, 0L);
    } else if (field_179776_a == S42PacketCombatEvent.Event.ENTITY_DIED) {
      Entity var7 = clientWorldController.getEntityByID(field_179774_b);
      
      if ((var7 instanceof EntityPlayer)) {
        MetadataPlayerDeath var5 = new MetadataPlayerDeath((EntityPlayer)var7, var3);
        var5.func_152807_a(field_179773_e);
        gameController.getTwitchStream().func_152911_a(var5, 0L);
      }
    }
  }
  
  public void func_175101_a(S41PacketServerDifficulty p_175101_1_) {
    PacketThreadUtil.func_180031_a(p_175101_1_, this, gameController);
    gameController.theWorld.getWorldInfo().setDifficulty(p_175101_1_.func_179831_b());
    gameController.theWorld.getWorldInfo().setDifficultyLocked(p_175101_1_.func_179830_a());
  }
  
  public void func_175094_a(S43PacketCamera p_175094_1_) {
    PacketThreadUtil.func_180031_a(p_175094_1_, this, gameController);
    Entity var2 = p_175094_1_.func_179780_a(clientWorldController);
    
    if (var2 != null) {
      gameController.func_175607_a(var2);
    }
  }
  
  public void func_175093_a(S44PacketWorldBorder p_175093_1_) {
    PacketThreadUtil.func_180031_a(p_175093_1_, this, gameController);
    p_175093_1_.func_179788_a(clientWorldController.getWorldBorder());
  }
  
  public void func_175099_a(S45PacketTitle p_175099_1_) {
    PacketThreadUtil.func_180031_a(p_175099_1_, this, gameController);
    S45PacketTitle.Type var2 = p_175099_1_.func_179807_a();
    String var3 = null;
    String var4 = null;
    String var5 = p_175099_1_.func_179805_b() != null ? p_175099_1_.func_179805_b().getFormattedText() : "";
    
    switch (SwitchAction.field_178885_a[var2.ordinal()]) {
    case 1: 
      var3 = var5;
      break;
    
    case 2: 
      var4 = var5;
      break;
    
    case 3: 
      gameController.ingameGUI.func_175178_a("", "", -1, -1, -1);
      gameController.ingameGUI.func_175177_a();
      return;
    }
    
    gameController.ingameGUI.func_175178_a(var3, var4, p_175099_1_.func_179806_c(), 
      p_175099_1_.func_179804_d(), p_175099_1_.func_179803_e());
  }
  
  public void func_175100_a(S46PacketSetCompressionLevel p_175100_1_) {
    if (!netManager.isLocalChannel()) {
      netManager.setCompressionTreshold(p_175100_1_.func_179760_a());
    }
  }
  
  public void func_175096_a(S47PacketPlayerListHeaderFooter p_175096_1_) {
    gameController.ingameGUI.getTabList().setHeader(
      p_175096_1_.func_179700_a().getFormattedText().length() == 0 ? null : p_175096_1_.func_179700_a());
    gameController.ingameGUI.getTabList().setFooter(
      p_175096_1_.func_179701_b().getFormattedText().length() == 0 ? null : p_175096_1_.func_179701_b());
  }
  
  public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149076_c());
    
    if ((var2 instanceof EntityLivingBase)) {
      ((EntityLivingBase)var2).removePotionEffectClient(packetIn.func_149075_d());
    }
  }
  
  public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Iterator var2 = packetIn.func_179767_a().iterator();
    
    while (var2.hasNext()) {
      S38PacketPlayerListItem.AddPlayerData var3 = (S38PacketPlayerListItem.AddPlayerData)var2.next();
      
      if (packetIn.func_179768_b() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
        playerInfoMap.remove(var3.func_179962_a().getId());
      } else {
        NetworkPlayerInfo var4 = (NetworkPlayerInfo)playerInfoMap.get(var3.func_179962_a().getId());
        
        if (packetIn.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
          var4 = new NetworkPlayerInfo(var3);
          playerInfoMap.put(var4.func_178845_a().getId(), var4);
        }
        
        if (var4 != null) {
          switch (SwitchAction.field_178884_b[packetIn.func_179768_b().ordinal()]) {
          case 1: 
            var4.func_178839_a(var3.func_179960_c());
            var4.func_178838_a(var3.func_179963_b());
            break;
          
          case 2: 
            var4.func_178839_a(var3.func_179960_c());
            break;
          
          case 3: 
            var4.func_178838_a(var3.func_179963_b());
            break;
          
          case 4: 
            var4.func_178859_a(var3.func_179961_d());
          }
        }
      }
    }
  }
  
  public void handleKeepAlive(S00PacketKeepAlive packetIn) {
    addToSendQueue(new net.minecraft.network.play.client.C00PacketKeepAlive(packetIn.func_149134_c()));
  }
  
  public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    EntityPlayerSP var2 = gameController.thePlayer;
    capabilities.isFlying = packetIn.isFlying();
    capabilities.isCreativeMode = packetIn.isCreativeMode();
    capabilities.disableDamage = packetIn.isInvulnerable();
    capabilities.allowFlying = packetIn.isAllowFlying();
    capabilities.setFlySpeed(packetIn.getFlySpeed());
    capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
  }
  


  public void handleTabComplete(S3APacketTabComplete packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    String[] var2 = packetIn.func_149630_c();
    
    if ((gameController.currentScreen instanceof GuiChat)) {
      GuiChat var3 = (GuiChat)gameController.currentScreen;
      var3.onAutocompleteResponse(var2);
    }
  }
  
  public void handleSoundEffect(S29PacketSoundEffect packetIn) {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    gameController.theWorld.playSound(packetIn.func_149207_d(), packetIn.func_149211_e(), 
      packetIn.func_149210_f(), packetIn.func_149212_c(), packetIn.func_149208_g(), packetIn.func_149209_h(), 
      false);
  }
  
  public void func_175095_a(S48PacketResourcePackSend p_175095_1_) {
    final String var2 = p_175095_1_.func_179783_a();
    final String var3 = p_175095_1_.func_179784_b();
    
    if (var2.startsWith("level://")) {
      String var4 = var2.substring("level://".length());
      File var5 = new File(gameController.mcDataDir, "saves");
      File var6 = new File(var5, var4);
      
      if (var6.isFile())
      {
        netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
        Futures.addCallback(gameController.getResourcePackRepository().func_177319_a(var6), 
          new FutureCallback() {
            private static final String __OBFID = "CL_00000879";
            
            public void onSuccess(Object p_onSuccess_1_) {
              netManager.sendPacket(new C19PacketResourcePackStatus(var3, 
                C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            }
            
            public void onFailure(Throwable p_onFailure_1_) {
              netManager.sendPacket(new C19PacketResourcePackStatus(var3, 
                C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
          });
      } else {
        netManager.sendPacket(
          new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
      }
    }
    else if ((gameController.getCurrentServerData() != null) && 
      (gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED))
    {
      netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
      Futures.addCallback(gameController.getResourcePackRepository().func_180601_a(var2, var3), 
        new FutureCallback() {
          private static final String __OBFID = "CL_00002624";
          
          public void onSuccess(Object p_onSuccess_1_) {
            netManager.sendPacket(new C19PacketResourcePackStatus(var3, 
              C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
          }
          
          public void onFailure(Throwable p_onFailure_1_) {
            netManager.sendPacket(new C19PacketResourcePackStatus(var3, 
              C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
          }
        });
    } else if ((gameController.getCurrentServerData() != null) && 
      (gameController.getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.PROMPT))
    {
      netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.DECLINED));
    } else {
      gameController.addScheduledTask(new Runnable() {
        private static final String __OBFID = "CL_00002623";
        
        public void run() {
          gameController.displayGuiScreen(new GuiYesNo(new net.minecraft.client.gui.GuiYesNoCallback() {
            private static final String __OBFID = "CL_00002622";
            
            public void confirmClicked(boolean result, int id) {
              gameController = Minecraft.getMinecraft();
              
              if (result) {
                if (gameController.getCurrentServerData() != null)
                {
                  gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.ENABLED);
                }
                
                netManager.sendPacket(new C19PacketResourcePackStatus(
                  val$var3, C19PacketResourcePackStatus.Action.ACCEPTED));
                Futures.addCallback(gameController
                  .getResourcePackRepository().func_180601_a(val$var2, val$var3), 
                  new FutureCallback()
                  {
                    private static final String __OBFID = "CL_00002621";
                    
                    public void onSuccess(Object p_onSuccess_1_) {
                      netManager.sendPacket(new C19PacketResourcePackStatus(val$var3, 
                        C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                    }
                    
                    public void onFailure(Throwable p_onFailure_1_)
                    {
                      netManager.sendPacket(new C19PacketResourcePackStatus(val$var3, 
                        C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                    }
                  });
              } else {
                if (gameController.getCurrentServerData() != null)
                {
                  gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.DISABLED);
                }
                
                netManager.sendPacket(new C19PacketResourcePackStatus(
                  val$var3, C19PacketResourcePackStatus.Action.DECLINED));
              }
              

              net.minecraft.client.multiplayer.ServerList.func_147414_b(gameController.getCurrentServerData());
              gameController.displayGuiScreen(null);
            }
          }, I18n.format("multiplayer.texturePrompt.line1", new Object[0]), 
            I18n.format("multiplayer.texturePrompt.line2", new Object[0]), 0));
        }
      });
    }
  }
  
  public void func_175097_a(S49PacketUpdateEntityNBT p_175097_1_)
  {
    PacketThreadUtil.func_180031_a(p_175097_1_, this, gameController);
    Entity var2 = p_175097_1_.func_179764_a(clientWorldController);
    
    if (var2 != null) {
      var2.func_174834_g(p_175097_1_.func_179763_a());
    }
  }
  






  public void handleCustomPayload(S3FPacketCustomPayload packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if ("MC|TrList".equals(packetIn.getChannelName())) {
      PacketBuffer var2 = packetIn.getBufferData();
      label130:
      try {
        int var3 = var2.readInt();
        GuiScreen var4 = gameController.currentScreen;
        
        if ((var4 != null) && ((var4 instanceof GuiMerchant))) {
          if (var3 != gameController.thePlayer.openContainer.windowId) break label130;
          IMerchant var5 = ((GuiMerchant)var4).getMerchant();
          MerchantRecipeList var6 = MerchantRecipeList.func_151390_b(var2);
          var5.setRecipes(var6);
        }
      } catch (IOException var10) {
        logger.error("Couldn't load trade info", var10);
      } finally {
        var2.release();
      }
    } else if ("MC|Brand".equals(packetIn.getChannelName())) {
      gameController.thePlayer.func_175158_f(packetIn.getBufferData().readStringFromBuffer(32767));
    } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
      ItemStack var12 = gameController.thePlayer.getCurrentEquippedItem();
      
      if ((var12 != null) && (var12.getItem() == net.minecraft.init.Items.written_book)) {
        gameController.displayGuiScreen(new net.minecraft.client.gui.GuiScreenBook(gameController.thePlayer, var12, false));
      }
    }
  }
  



  public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Scoreboard var2 = clientWorldController.getScoreboard();
    

    if (packetIn.func_149338_e() == 0) {
      ScoreObjective var3 = var2.addScoreObjective(packetIn.func_149339_c(), net.minecraft.scoreboard.IScoreObjectiveCriteria.DUMMY);
      var3.setDisplayName(packetIn.func_149337_d());
      var3.func_178767_a(packetIn.func_179817_d());
    } else {
      ScoreObjective var3 = var2.getObjective(packetIn.func_149339_c());
      
      if (packetIn.func_149338_e() == 1) {
        var2.func_96519_k(var3);
      } else if (packetIn.func_149338_e() == 2) {
        var3.setDisplayName(packetIn.func_149337_d());
        var3.func_178767_a(packetIn.func_179817_d());
      }
    }
  }
  



  public void handleUpdateScore(S3CPacketUpdateScore packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Scoreboard var2 = clientWorldController.getScoreboard();
    ScoreObjective var3 = var2.getObjective(packetIn.func_149321_d());
    
    if (packetIn.func_180751_d() == S3CPacketUpdateScore.Action.CHANGE) {
      Score var4 = var2.getValueFromObjective(packetIn.func_149324_c(), var3);
      var4.setScorePoints(packetIn.func_149323_e());
    } else if (packetIn.func_180751_d() == S3CPacketUpdateScore.Action.REMOVE) {
      if (net.minecraft.util.StringUtils.isNullOrEmpty(packetIn.func_149321_d())) {
        var2.func_178822_d(packetIn.func_149324_c(), null);
      } else if (var3 != null) {
        var2.func_178822_d(packetIn.func_149324_c(), var3);
      }
    }
  }
  



  public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Scoreboard var2 = clientWorldController.getScoreboard();
    
    if (packetIn.func_149370_d().length() == 0) {
      var2.setObjectiveInDisplaySlot(packetIn.func_149371_c(), null);
    } else {
      ScoreObjective var3 = var2.getObjective(packetIn.func_149370_d());
      var2.setObjectiveInDisplaySlot(packetIn.func_149371_c(), var3);
    }
  }
  




  public void handleTeams(S3EPacketTeams packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Scoreboard var2 = clientWorldController.getScoreboard();
    ScorePlayerTeam var3;
    ScorePlayerTeam var3;
    if (packetIn.func_149307_h() == 0) {
      var3 = var2.createTeam(packetIn.func_149312_c());
    } else {
      var3 = var2.getTeam(packetIn.func_149312_c());
    }
    
    if ((packetIn.func_149307_h() == 0) || (packetIn.func_149307_h() == 2)) {
      var3.setTeamName(packetIn.func_149306_d());
      var3.setNamePrefix(packetIn.func_149311_e());
      var3.setNameSuffix(packetIn.func_149309_f());
      var3.func_178774_a(EnumChatFormatting.func_175744_a(packetIn.func_179813_h()));
      var3.func_98298_a(packetIn.func_149308_i());
      Team.EnumVisible var4 = Team.EnumVisible.func_178824_a(packetIn.func_179814_i());
      
      if (var4 != null) {
        var3.func_178772_a(var4);
      }
    }
    



    if ((packetIn.func_149307_h() == 0) || (packetIn.func_149307_h() == 3)) {
      Iterator var6 = packetIn.func_149310_g().iterator();
      
      while (var6.hasNext()) {
        String var5 = (String)var6.next();
        var2.func_151392_a(var5, packetIn.func_149312_c());
      }
    }
    
    if (packetIn.func_149307_h() == 4) {
      Iterator var6 = packetIn.func_149310_g().iterator();
      
      while (var6.hasNext()) {
        String var5 = (String)var6.next();
        var2.removePlayerFromTeam(var5, var3);
      }
    }
    
    if (packetIn.func_149307_h() == 1) {
      var2.removeTeam(var3);
    }
  }
  



  public void handleParticles(S2APacketParticles packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    
    if (packetIn.func_149222_k() == 0) {
      double var2 = packetIn.func_149227_j() * packetIn.func_149221_g();
      double var4 = packetIn.func_149227_j() * packetIn.func_149224_h();
      double var6 = packetIn.func_149227_j() * packetIn.func_149223_i();
      try
      {
        clientWorldController.spawnParticle(packetIn.func_179749_a(), packetIn.func_179750_b(), 
          packetIn.func_149220_d(), packetIn.func_149226_e(), packetIn.func_149225_f(), var2, var4, var6, 
          packetIn.func_179748_k());
      } catch (Throwable var17) {
        logger.warn("Could not spawn particle effect " + packetIn.func_179749_a());
      }
    } else {
      for (int var18 = 0; var18 < packetIn.func_149222_k(); var18++) {
        double var3 = avRandomizer.nextGaussian() * packetIn.func_149221_g();
        double var5 = avRandomizer.nextGaussian() * packetIn.func_149224_h();
        double var7 = avRandomizer.nextGaussian() * packetIn.func_149223_i();
        double var9 = avRandomizer.nextGaussian() * packetIn.func_149227_j();
        double var11 = avRandomizer.nextGaussian() * packetIn.func_149227_j();
        double var13 = avRandomizer.nextGaussian() * packetIn.func_149227_j();
        try
        {
          clientWorldController.spawnParticle(packetIn.func_179749_a(), packetIn.func_179750_b(), 
            packetIn.func_149220_d() + var3, packetIn.func_149226_e() + var5, 
            packetIn.func_149225_f() + var7, var9, var11, var13, packetIn.func_179748_k());
        } catch (Throwable var16) {
          logger.warn("Could not spawn particle effect " + packetIn.func_179749_a());
          return;
        }
      }
    }
  }
  





  public void handleEntityProperties(S20PacketEntityProperties packetIn)
  {
    PacketThreadUtil.func_180031_a(packetIn, this, gameController);
    Entity var2 = clientWorldController.getEntityByID(packetIn.func_149442_c());
    
    if (var2 != null) {
      if (!(var2 instanceof EntityLivingBase)) {
        throw new IllegalStateException(
          "Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
      }
      BaseAttributeMap var3 = ((EntityLivingBase)var2).getAttributeMap();
      Iterator var4 = packetIn.func_149441_d().iterator();
      Iterator var7;
      for (; var4.hasNext(); 
          











          var7.hasNext())
      {
        S20PacketEntityProperties.Snapshot var5 = (S20PacketEntityProperties.Snapshot)var4.next();
        IAttributeInstance var6 = var3.getAttributeInstanceByName(var5.func_151409_a());
        
        if (var6 == null) {
          var6 = var3.registerAttribute(new net.minecraft.entity.ai.attributes.RangedAttribute(null, var5.func_151409_a(), 0.0D, 
            2.2250738585072014E-308D, Double.MAX_VALUE));
        }
        
        var6.setBaseValue(var5.func_151410_b());
        var6.removeAllModifiers();
        var7 = var5.func_151408_c().iterator();
        
        continue;
        AttributeModifier var8 = (AttributeModifier)var7.next();
        var6.applyModifier(var8);
      }
    }
  }
  





  public NetworkManager getNetworkManager()
  {
    return netManager;
  }
  
  public Collection func_175106_d() {
    return playerInfoMap.values();
  }
  
  public NetworkPlayerInfo func_175102_a(UUID p_175102_1_) {
    return (NetworkPlayerInfo)playerInfoMap.get(p_175102_1_);
  }
  
  public NetworkPlayerInfo func_175104_a(String p_175104_1_) {
    Iterator var2 = playerInfoMap.values().iterator();
    NetworkPlayerInfo var3;
    do
    {
      if (!var2.hasNext()) {
        return null;
      }
      
      var3 = (NetworkPlayerInfo)var2.next();
    } while (!var3.func_178845_a().getName().equals(p_175104_1_));
    
    return var3;
  }
  
  public GameProfile func_175105_e() {
    return field_175107_d;
  }
  
  static final class SwitchAction
  {
    static final int[] field_178885_a;
    static final int[] field_178884_b = new int[S38PacketPlayerListItem.Action.values().length];
    private static final String __OBFID = "CL_00002620";
    
    static {
      try {
        field_178884_b[S38PacketPlayerListItem.Action.ADD_PLAYER.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      
      try
      {
        field_178884_b[S38PacketPlayerListItem.Action.UPDATE_GAME_MODE.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      
      try
      {
        field_178884_b[S38PacketPlayerListItem.Action.UPDATE_LATENCY.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      
      try
      {
        field_178884_b[S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
      

      field_178885_a = new int[S45PacketTitle.Type.values().length];
      try
      {
        field_178885_a[S45PacketTitle.Type.TITLE.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {}
      
      try
      {
        field_178885_a[S45PacketTitle.Type.SUBTITLE.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {}
      
      try
      {
        field_178885_a[S45PacketTitle.Type.RESET.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError7) {}
    }
    
    SwitchAction() {}
  }
}
