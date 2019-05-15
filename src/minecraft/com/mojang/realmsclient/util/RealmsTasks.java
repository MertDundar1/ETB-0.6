package com.mojang.realmsclient.util;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.annotation.Nullable;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RealmsTasks
{
  private static final Logger LOGGER = ;
  private static final int NUMBER_OF_RETRIES = 25;
  
  public RealmsTasks() {}
  
  private static void pause(int seconds) {
    try { Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      LOGGER.error("", e);
    }
  }
  

  public static class OpenServerTask
    extends LongRunningTask
  {
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    private final boolean join;
    private final RealmsScreen lastScreen;
    
    public OpenServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen, RealmsScreen lastScreen, boolean join)
    {
      serverData = realmsServer;
      configureScreen = configureWorldScreen;
      this.join = join;
      this.lastScreen = lastScreen;
    }
    
    public void run()
    {
      setTitle(RealmsScreen.getLocalizedString("mco.configure.world.opening"));
      RealmsClient client = RealmsClient.createRealmsClient();
      
      for (int i = 0; i < 25; i++) {
        if (aborted()) return;
        try
        {
          boolean openResult = client.open(serverData.id).booleanValue();
          if (openResult) {
            configureScreen.stateChanged();
            serverData.state = RealmsServer.State.OPEN;
            
            if (join) {
              ((RealmsMainScreen)lastScreen).play(serverData);
              break;
            }
            
            Realms.setScreen(configureScreen);
            break;
          }
        } catch (RetryCallException e) {
          if (aborted()) return;
          RealmsTasks.pause(delaySeconds);
        } catch (Exception e) {
          if (aborted()) return;
          RealmsTasks.LOGGER.error("Failed to open server", e);
          error("Failed to open the server");
        }
      }
    }
  }
  

  public static class CloseServerTask
    extends LongRunningTask
  {
    private final RealmsServer serverData;
    
    private final RealmsConfigureWorldScreen configureScreen;
    

    public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen)
    {
      serverData = realmsServer;
      configureScreen = configureWorldScreen;
    }
    
    public void run()
    {
      setTitle(RealmsScreen.getLocalizedString("mco.configure.world.closing"));
      RealmsClient client = RealmsClient.createRealmsClient();
      
      for (int i = 0; i < 25; i++) {
        if (aborted()) return;
        try
        {
          boolean closeResult = client.close(serverData.id).booleanValue();
          if (closeResult) {
            configureScreen.stateChanged();
            serverData.state = RealmsServer.State.CLOSED;
            Realms.setScreen(configureScreen);
            break;
          }
        } catch (RetryCallException e) {
          if (aborted()) return;
          RealmsTasks.pause(delaySeconds);
        } catch (Exception e) {
          if (aborted()) return;
          RealmsTasks.LOGGER.error("Failed to close server", e);
          error("Failed to close the server");
        }
      }
    }
  }
  

  public static class SwitchSlotTask
    extends LongRunningTask
  {
    private final long worldId;
    
    private final int slot;
    
    private final RealmsScreen lastScreen;
    private final int confirmId;
    
    public SwitchSlotTask(long worldId, int slot, RealmsScreen lastScreen, int confirmId)
    {
      this.worldId = worldId;
      this.slot = slot;
      this.lastScreen = lastScreen;
      this.confirmId = confirmId;
    }
    
    public void run()
    {
      RealmsClient client = RealmsClient.createRealmsClient();
      
      String title = RealmsScreen.getLocalizedString("mco.minigame.world.slot.screen.title");
      setTitle(title);
      
      for (int i = 0; i < 25; i++) {
        try {
          if (aborted()) { return;
          }
          if (client.switchSlot(worldId, slot)) {
            lastScreen.confirmResult(true, confirmId);
            break;
          }
        }
        catch (RetryCallException e) {
          if (aborted()) return;
          RealmsTasks.pause(delaySeconds);
        } catch (Exception e) {
          if (aborted()) return;
          RealmsTasks.LOGGER.error("Couldn't switch world!");
          error(e.toString());
        }
      }
    }
  }
  

  public static class SwitchMinigameTask
    extends LongRunningTask
  {
    private final long worldId;
    private final WorldTemplate worldTemplate;
    private final RealmsConfigureWorldScreen lastScreen;
    
    public SwitchMinigameTask(long worldId, WorldTemplate worldTemplate, RealmsConfigureWorldScreen lastScreen)
    {
      this.worldId = worldId;
      this.worldTemplate = worldTemplate;
      this.lastScreen = lastScreen;
    }
    
    public void run()
    {
      RealmsClient client = RealmsClient.createRealmsClient();
      String title = RealmsScreen.getLocalizedString("mco.minigame.world.starting.screen.title");
      setTitle(title);
      
      for (int i = 0; i < 25; i++) {
        try {
          if (aborted()) { return;
          }
          if (client.putIntoMinigameMode(worldId, worldTemplate.id).booleanValue()) {
            Realms.setScreen(lastScreen);
            break;
          }
        } catch (RetryCallException e) {
          if (aborted()) return;
          RealmsTasks.pause(delaySeconds);
        } catch (Exception e) {
          if (aborted()) return;
          RealmsTasks.LOGGER.error("Couldn't start mini game!");
          error(e.toString());
        }
      }
    }
  }
  

  public static class ResettingWorldTask
    extends LongRunningTask
  {
    private final String seed;
    
    private final WorldTemplate worldTemplate;
    
    private final int levelType;
    private final boolean generateStructures;
    private final long serverId;
    private final RealmsScreen lastScreen;
    private String title = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");
    
    public ResettingWorldTask(long serverId, RealmsScreen lastScreen, WorldTemplate worldTemplate) {
      seed = null;
      this.worldTemplate = worldTemplate;
      levelType = -1;
      generateStructures = true;
      this.serverId = serverId;
      this.lastScreen = lastScreen;
    }
    
    public ResettingWorldTask(long serverId, RealmsScreen lastScreen, String seed, int levelType, boolean generateStructures) {
      this.seed = seed;
      worldTemplate = null;
      this.levelType = levelType;
      this.generateStructures = generateStructures;
      this.serverId = serverId;
      this.lastScreen = lastScreen;
    }
    
    public void setResetTitle(String title) {
      this.title = title;
    }
    
    public void run()
    {
      RealmsClient client = RealmsClient.createRealmsClient();
      setTitle(title);
      for (int i = 0; i < 25; i++) {
        try {
          if (aborted()) { return;
          }
          if (worldTemplate != null) {
            client.resetWorldWithTemplate(serverId, worldTemplate.id);
          } else {
            client.resetWorldWithSeed(serverId, seed, Integer.valueOf(levelType), generateStructures);
          }
          
          if (aborted()) return;
          Realms.setScreen(lastScreen);
          return;
        } catch (RetryCallException e) {
          if (aborted()) return;
          RealmsTasks.pause(delaySeconds);
        } catch (Exception e) {
          if (aborted()) return;
          RealmsTasks.LOGGER.error("Couldn't reset world");
          error(e.toString());
          return;
        }
      }
    }
  }
  

  public static class RealmsConnectTask
    extends LongRunningTask
  {
    private final RealmsConnect realmsConnect;
    
    private final RealmsServer data;
    private final RealmsScreen onlineScreen;
    
    public RealmsConnectTask(RealmsScreen onlineScreen, RealmsServer server)
    {
      this.onlineScreen = onlineScreen;
      realmsConnect = new RealmsConnect(onlineScreen);
      data = server;
    }
    
    public void run()
    {
      setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
      
      RealmsClient client = RealmsClient.createRealmsClient();
      boolean addressRetrieved = false;
      boolean hasError = false;
      int sleepTime = 5;
      com.mojang.realmsclient.dto.RealmsServerAddress a = null;
      boolean tosNotAccepted = false;
      
      for (int i = 0; i < 20; i++) {
        if (aborted())
          break;
        try { a = client.join(data.id);
          addressRetrieved = true;
        }
        catch (RetryCallException e) {
          sleepTime = delaySeconds;
        } catch (RealmsServiceException e) {
          if (errorCode == 6002) {
            tosNotAccepted = true;
            break;
          }
          hasError = true;
          error(e.toString());
          RealmsTasks.LOGGER.error("Couldn't connect to world", e);
          break;
        }
        catch (IOException e) {
          RealmsTasks.LOGGER.error("Couldn't parse response connecting to world", e);
        } catch (Exception e) {
          hasError = true;
          RealmsTasks.LOGGER.error("Couldn't connect to world", e);
          error(e.getLocalizedMessage());
        }
        
        if (addressRetrieved) {
          break;
        }
        
        sleep(sleepTime);
      }
      
      if (tosNotAccepted) {
        Realms.setScreen(new RealmsTermsScreen(onlineScreen, data));
      } else if ((!aborted()) && (!hasError)) {
        if (addressRetrieved) {
          if ((data.resourcePackUrl != null) && (data.resourcePackHash != null)) {
            try {
              final com.mojang.realmsclient.dto.RealmsServerAddress finalA = a;
              
              Futures.addCallback(Realms.downloadResourcePack(data.resourcePackUrl, data.resourcePackHash), new FutureCallback()
              {
                public void onSuccess(@Nullable Object result) {
                  net.minecraft.realms.RealmsServerAddress address = net.minecraft.realms.RealmsServerAddress.parseString(finalAaddress);
                  realmsConnect.connect(address.getHost(), address.getPort());
                }
                
                public void onFailure(Throwable t)
                {
                  RealmsTasks.LOGGER.error(t);
                  error("Failed to download resource pack!");
                }
              });
            } catch (Exception e) {
              Realms.clearResourcePack();
              RealmsTasks.LOGGER.error(e);
              error("Failed to download resource pack!");
            }
          } else {
            net.minecraft.realms.RealmsServerAddress address = net.minecraft.realms.RealmsServerAddress.parseString(address);
            realmsConnect.connect(address.getHost(), address.getPort());
          }
        } else {
          error(RealmsScreen.getLocalizedString("mco.errorMessage.connectionFailure"));
        }
      }
    }
    
    private void sleep(int sleepTimeSeconds) {
      try {
        Thread.sleep(sleepTimeSeconds * 1000);
      } catch (InterruptedException e1) {
        RealmsTasks.LOGGER.warn(e1.getLocalizedMessage());
      }
    }
    
    public void abortTask()
    {
      realmsConnect.abort();
    }
    
    public void tick()
    {
      realmsConnect.tick();
    }
  }
  
  public static class WorldCreationTask extends LongRunningTask
  {
    private final String name;
    private final String motd;
    private final long worldId;
    private final RealmsScreen lastScreen;
    
    public WorldCreationTask(long worldId, String name, String motd, RealmsScreen lastScreen) {
      this.worldId = worldId;
      this.name = name;
      this.motd = motd;
      this.lastScreen = lastScreen;
    }
    
    public void run()
    {
      String title = RealmsScreen.getLocalizedString("mco.create.world.wait");
      setTitle(title);
      RealmsClient client = RealmsClient.createRealmsClient();
      try
      {
        client.initializeWorld(worldId, name, motd);
        Realms.setScreen(lastScreen);
      } catch (RealmsServiceException e) {
        RealmsTasks.LOGGER.error("Couldn't create world");
        error(e.toString());
      } catch (UnsupportedEncodingException e) {
        RealmsTasks.LOGGER.error("Couldn't create world");
        error(e.getLocalizedMessage());
      } catch (IOException e) {
        RealmsTasks.LOGGER.error("Could not parse response creating world");
        error(e.getLocalizedMessage());
      } catch (Exception e) {
        RealmsTasks.LOGGER.error("Could not create world");
        error(e.getLocalizedMessage());
      }
    }
  }
}
