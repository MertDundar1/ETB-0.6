package com.mojang.realmsclient;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.RealmsClient.CompatibleVersionResponse;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsDataFetcher.Task;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsServerPing;
import net.minecraft.realms.RealmsServerStatusPinger;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class RealmsMainScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private static boolean overrideConfigure = false;
  private static boolean stageEnabled = false;
  
  private boolean dontSetConnectedToRealms = false;
  
  protected static final int BUTTON_BACK_ID = 0;
  
  protected static final int BUTTON_PLAY_ID = 1;
  
  protected static final int BUTTON_CONFIGURE_ID = 2;
  
  protected static final int BUTTON_LEAVE_ID = 3;
  
  protected static final int BUTTON_BUY_ID = 4;
  
  protected static final int RESOURCEPACK_ID = 100;
  
  private RealmsServer resourcePackServer;
  private static final String ON_ICON_LOCATION = "realms:textures/gui/realms/on_icon.png";
  private static final String OFF_ICON_LOCATION = "realms:textures/gui/realms/off_icon.png";
  private static final String EXPIRED_ICON_LOCATION = "realms:textures/gui/realms/expired_icon.png";
  private static final String INVITATION_ICONS_LOCATION = "realms:textures/gui/realms/invitation_icons.png";
  private static final String INVITE_ICON_LOCATION = "realms:textures/gui/realms/invite_icon.png";
  private static final String WORLDICON_LOCATION = "realms:textures/gui/realms/world_icon.png";
  private static final String LOGO_LOCATION = "realms:textures/gui/title/realms.png";
  private static RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
  private static RealmsServerStatusPinger statusPinger = new RealmsServerStatusPinger();
  private static final ThreadPoolExecutor THREAD_POOL = new java.util.concurrent.ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());
  
  private static int lastScrollYPosition = -1;
  
  private RealmsScreen lastScreen;
  
  private volatile ServerSelectionList serverSelectionList;
  private long selectedServerId = -1L;
  
  private RealmsButton configureButton;
  
  private RealmsButton leaveButton;
  private RealmsButton playButton;
  private RealmsButton buyButton;
  private String toolTip;
  private List<RealmsServer> realmsServers = com.google.common.collect.Lists.newArrayList();
  
  private static final String mcoInfoUrl = "https://minecraft.net/realms";
  private volatile int numberOfPendingInvites = 0;
  
  private int animTick;
  
  private static volatile boolean mcoEnabled;
  private static volatile boolean mcoEnabledCheck;
  private static boolean checkedMcoAvailability;
  private static volatile boolean trialsAvailable;
  private static volatile boolean createdTrial = false;
  private static final ReentrantLock trialLock = new ReentrantLock();
  
  private static RealmsScreen realmsGenericErrorScreen = null;
  
  private static boolean regionsPinged = false;
  
  private boolean onLink = false;
  
  private int mindex = 0;
  private char[] mchars = { '3', '2', '1', '4', '5', '6' };
  
  private int sindex = 0;
  private char[] schars = { '9', '8', '7', '1', '2', '3' };
  
  static
  {
    String version = RealmsVersion.getVersion();
    
    if (version != null) {
      LOGGER.info("Realms library version == " + version);
    }
  }
  
  public RealmsMainScreen(RealmsScreen lastScreen) {
    this.lastScreen = lastScreen;
    checkIfMcoEnabled();
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    serverSelectionList.mouseEvent();
  }
  
  public void init()
  {
    if (!dontSetConnectedToRealms) {
      Realms.setConnectedToRealms(false);
    }
    
    if (realmsGenericErrorScreen != null) {
      Realms.setScreen(realmsGenericErrorScreen);
      return;
    }
    
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    postInit();
    
    if (isMcoEnabled()) {
      realmsDataFetcher.init();
    }
  }
  
  public void postInit() {
    buttonsAdd(this.playButton = newButton(1, width() / 2 - 154, height() - 52, 154, 20, getLocalizedString("mco.selectServer.play")));
    buttonsAdd(this.configureButton = newButton(2, width() / 2 + 6, height() - 52, 154, 20, getLocalizedString("mco.selectServer.configure")));
    
    buttonsAdd(this.leaveButton = newButton(3, width() / 2 - 154, height() - 28, 102, 20, getLocalizedString("mco.selectServer.leave")));
    buttonsAdd(this.buyButton = newButton(4, width() / 2 - 48, height() - 28, 102, 20, getLocalizedString("mco.selectServer.buy")));
    buttonsAdd(newButton(0, width() / 2 + 58, height() - 28, 102, 20, getLocalizedString("gui.back")));
    
    serverSelectionList = new ServerSelectionList();
    
    if (lastScrollYPosition != -1) {
      serverSelectionList.scroll(lastScrollYPosition);
    }
    
    RealmsServer server = findServer(selectedServerId);
    playButton.active((server != null) && (state == RealmsServer.State.OPEN) && (!expired));
    configureButton.active((overrideConfigure) || ((server != null) && (state != RealmsServer.State.ADMIN_LOCK) && (ownerUUID.equals(Realms.getUUID()))));
    leaveButton.active((server != null) && (!ownerUUID.equals(Realms.getUUID())));
  }
  
  public void tick()
  {
    animTick += 1;
    
    if (noParentalConsent()) {
      Realms.setScreen(new com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen(lastScreen));
    }
    
    if (isMcoEnabled()) {
      realmsDataFetcher.init();
    } else {
      return;
    }
    

    if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
      List<RealmsServer> newServers = realmsDataFetcher.getServers();
      
      boolean ownsNonExpiredRealmServer = false;
      
      for (Iterator i$ = newServers.iterator(); i$.hasNext();) { retrievedServer = (RealmsServer)i$.next();
        if (isSelfOwnedNonExpiredServer(retrievedServer)) {
          ownsNonExpiredRealmServer = true;
        }
        
        for (RealmsServer oldServer : realmsServers) {
          if (id == id) {
            retrievedServer.latestStatFrom(oldServer);
            break;
          }
        }
      }
      RealmsServer retrievedServer;
      realmsServers = newServers;
      
      if ((!regionsPinged) && (ownsNonExpiredRealmServer)) {
        regionsPinged = true;
        pingRegions();
      }
    }
    
    if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
      numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
    }
    
    if ((realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) && (!createdTrial)) {
      trialsAvailable = realmsDataFetcher.isTrialAvailable();
    }
    
    realmsDataFetcher.markClean();
  }
  
  private void pingRegions() {
    new Thread()
    {
      public void run() {
        List<com.mojang.realmsclient.dto.RegionPingResult> regionPingResultList = com.mojang.realmsclient.client.Ping.pingAllRegions();
        RealmsClient client = RealmsClient.createRealmsClient();
        PingResult pingResult = new PingResult();
        pingResults = regionPingResultList;
        worldIds = RealmsMainScreen.this.getOwnedNonExpiredWorldIds();
        try {
          client.sendPingResults(pingResult);
        } catch (Throwable t) {
          RealmsMainScreen.LOGGER.warn("Could not send ping result to Realms: ", t);
        }
      }
    }.start();
  }
  
  private List<Long> getOwnedNonExpiredWorldIds() {
    List<Long> ids = new java.util.ArrayList();
    for (RealmsServer server : realmsServers) {
      if (isSelfOwnedNonExpiredServer(server)) {
        ids.add(Long.valueOf(id));
      }
    }
    return ids;
  }
  
  private boolean isMcoEnabled() {
    return mcoEnabled;
  }
  
  private boolean noParentalConsent() {
    return (mcoEnabledCheck) && (!mcoEnabled);
  }
  
  public void removed()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    switch (button.id()) {
    case 1: 
      play(findServer(selectedServerId));
      break;
    case 2: 
      configureClicked();
      break;
    case 3: 
      leaveClicked();
      break;
    case 4: 
      saveListScrollPosition();
      stopRealmsFetcherAndPinger();
      Realms.setScreen(new com.mojang.realmsclient.gui.screens.RealmsBuyRealmsScreen(this));
      break;
    case 0: 
      stopRealmsFetcherAndPinger();
      Realms.setScreen(lastScreen);
      break;
    }
    
  }
  
  private void createTrial()
  {
    if (createdTrial) {
      trialsAvailable = false;
      return;
    }
    
    final RealmsScreen mainScreen = this;
    
    new Thread("Realms-create-trial")
    {
      public void run() {
        try {
          if (!RealmsMainScreen.trialLock.tryLock(10L, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            return;
          }
          
          RealmsClient client = RealmsClient.createRealmsClient();
          
          RealmsMainScreen.access$302(false);
          
          if (client.createTrial().booleanValue()) {
            RealmsMainScreen.access$402(true);
            RealmsMainScreen.realmsDataFetcher.forceUpdate();
          } else {
            Realms.setScreen(new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.trial.unavailable"), mainScreen));
          }
        } catch (RealmsServiceException e) {
          RealmsMainScreen.LOGGER.error("Trials wasn't available: " + e.toString());
          Realms.setScreen(new RealmsGenericErrorScreen(e, RealmsMainScreen.this));
        } catch (IOException e) {
          RealmsMainScreen.LOGGER.error("Couldn't parse response when trying to create trial: " + e.toString());
          RealmsMainScreen.access$302(false);
        } catch (InterruptedException e) {
          RealmsMainScreen.LOGGER.error("Trial Interrupted exception: " + e.toString());
        } finally {
          if (RealmsMainScreen.trialLock.isHeldByCurrentThread()) {
            RealmsMainScreen.trialLock.unlock();
          }
        }
      }
    }.start();
  }
  
  private void checkIfMcoEnabled() {
    if (!checkedMcoAvailability) {
      checkedMcoAvailability = true;
      new Thread("MCO Availability Checker #1")
      {
        public void run() {
          RealmsClient client = RealmsClient.createRealmsClient();
          
          try
          {
            RealmsClient.CompatibleVersionResponse versionResponse = client.clientCompatible();
            
            if (versionResponse.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
              Realms.setScreen(RealmsMainScreen.access$602(new com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen(lastScreen, true)));
              return; }
            if (versionResponse.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
              Realms.setScreen(RealmsMainScreen.access$602(new com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen(lastScreen, false)));
              return;
            }
          } catch (RealmsServiceException e) {
            RealmsMainScreen.access$802(false);
            RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", new Object[] { e.toString() });
            
            if (httpResultCode == 401) {
              RealmsMainScreen.access$602(new RealmsGenericErrorScreen(e, lastScreen));
            }
            
            Realms.setScreen(new RealmsGenericErrorScreen(e, lastScreen));
            return;
          } catch (IOException e) {
            RealmsMainScreen.access$802(false);
            RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", new Object[] { e.getMessage() });
            Realms.setScreen(new RealmsGenericErrorScreen(e.getMessage(), lastScreen));
            return;
          }
          

          boolean retry = false;
          
          for (int i = 0; i < 3; i++) {
            try {
              Boolean result = client.mcoEnabled();
              
              if (result.booleanValue()) {
                RealmsMainScreen.LOGGER.info("Realms is available for this user");
                RealmsMainScreen.access$902(true);
              } else {
                RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                RealmsMainScreen.access$902(false);
              }
              
              RealmsMainScreen.access$1002(true);
            } catch (com.mojang.realmsclient.exception.RetryCallException e) {
              retry = true;
            } catch (RealmsServiceException e) {
              RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: " + e.toString());
            } catch (IOException e) {
              RealmsMainScreen.LOGGER.error("Couldn't parse response connecting to Realms: " + e.getMessage());
            }
            
            if (!retry)
              break;
            try { Thread.sleep(5000L);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
        }
      }.start();
    }
  }
  

  private void switchToStage()
  {
    if (!stageEnabled) {
      new Thread("MCO Stage Availability Checker #1")
      {
        public void run() {
          RealmsClient client = RealmsClient.createRealmsClient();
          try {
            Boolean result = client.stageAvailable();
            
            if (result.booleanValue()) {
              RealmsMainScreen.this.stopRealmsFetcherAndPinger();
              RealmsClient.switchToStage();
              RealmsMainScreen.LOGGER.info("Switched to stage");
              RealmsMainScreen.realmsDataFetcher.init();
              RealmsMainScreen.access$1202(true);
            } else {
              RealmsMainScreen.access$1202(false);
            }
          } catch (RealmsServiceException e) {
            RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: " + e.toString());
          } catch (IOException e) {
            RealmsMainScreen.LOGGER.error("Couldn't parse response connecting to Realms: " + e.getMessage());
          }
        }
      }.start();
    }
  }
  
  private void switchToProd() {
    if (stageEnabled) {
      stageEnabled = false;
      stopRealmsFetcherAndPinger();
      RealmsClient.switchToProd();
      realmsDataFetcher.init();
    }
  }
  
  private void stopRealmsFetcherAndPinger() {
    if (isMcoEnabled()) {
      realmsDataFetcher.stop();
      statusPinger.removeAll();
    }
  }
  
  private void configureClicked() {
    RealmsServer selectedServer = findServer(selectedServerId);
    if ((selectedServer != null) && (
      (Realms.getUUID().equals(ownerUUID)) || (overrideConfigure))) {
      stopRealmsFetcherAndPinger();
      saveListScrollPosition();
      Realms.setScreen(new com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen(this, id));
    }
  }
  
  private void leaveClicked()
  {
    RealmsServer selectedServer = findServer(selectedServerId);
    if ((selectedServer != null) && 
      (!Realms.getUUID().equals(ownerUUID))) {
      saveListScrollPosition();
      String line2 = getLocalizedString("mco.configure.world.leave.question.line1");
      String line3 = getLocalizedString("mco.configure.world.leave.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen(this, com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 3));
    }
  }
  
  private void saveListScrollPosition()
  {
    lastScrollYPosition = serverSelectionList.getScroll();
  }
  
  private RealmsServer findServer(long id) {
    for (RealmsServer server : realmsServers) {
      if (id == id) {
        return server;
      }
    }
    return null;
  }
  
  private int findIndex(long serverId) {
    for (int i = 0; i < realmsServers.size(); i++) {
      if (realmsServers.get(i)).id == serverId) {
        return i;
      }
    }
    return -1;
  }
  
  public void confirmResult(boolean result, int id)
  {
    if (id == 3) {
      if (result) {
        new Thread("Realms-leave-server")
        {
          public void run() {
            try {
              RealmsServer server = RealmsMainScreen.this.findServer(selectedServerId);
              
              if (server != null) {
                RealmsClient client = RealmsClient.createRealmsClient();
                RealmsMainScreen.realmsDataFetcher.removeItem(server);
                realmsServers.remove(server);
                client.uninviteMyselfFrom(id);
                RealmsMainScreen.realmsDataFetcher.removeItem(server);
                realmsServers.remove(server);
                RealmsMainScreen.this.updateSelectedItemPointer();
              }
            } catch (RealmsServiceException e) {
              RealmsMainScreen.LOGGER.error("Couldn't configure world");
              Realms.setScreen(new RealmsGenericErrorScreen(e, RealmsMainScreen.this));
            }
          }
        }.start();
      }
      
      Realms.setScreen(this);
    } else if (id == 100) {
      if (!result)
      {
        Realms.setScreen(this);
      } else {
        connectToServer(resourcePackServer);
      }
    }
  }
  
  private void updateSelectedItemPointer()
  {
    int originalIndex = findIndex(selectedServerId);
    
    if (realmsServers.size() - 1 == originalIndex) {
      originalIndex--;
    }
    
    if (realmsServers.size() == 0) {
      originalIndex = -1;
    }
    
    if ((originalIndex >= 0) && (originalIndex < realmsServers.size())) {
      selectedServerId = realmsServers.get(originalIndex)).id;
    }
  }
  
  public void removeSelection()
  {
    selectedServerId = -1L;
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    switch (eventKey) {
    case 28: 
    case 156: 
      mindex = 0;
      sindex = 0;
      buttonClicked(playButton);
      break;
    case 1: 
      mindex = 0;
      sindex = 0;
      stopRealmsFetcherAndPinger();
      Realms.setScreen(lastScreen);
      break;
    default: 
      if (mchars[mindex] == ch) {
        mindex += 1;
        
        if (mindex == mchars.length) {
          mindex = 0;
          overrideConfigure = true;
        }
      } else {
        mindex = 0;
      }
      
      if (schars[sindex] == ch) {
        sindex += 1;
        
        if (sindex == schars.length) {
          sindex = 0;
          
          if (!stageEnabled) {
            switchToStage();
          } else {
            switchToProd();
          }
        }
        
        return;
      }
      sindex = 0;
    }
    
  }
  


  public void render(int xm, int ym, float a)
  {
    toolTip = null;
    
    renderBackground();
    
    serverSelectionList.render(xm, ym, a);
    
    drawRealmsLogo(width() / 2 - 50, 7);
    
    renderLink(xm, ym);
    
    if (toolTip != null) {
      renderMousehoverTooltip(toolTip, xm, ym);
    }
    
    drawInvitationPendingIcon(xm, ym);
    
    if (stageEnabled) {
      renderStage();
    }
    
    super.render(xm, ym, a);
  }
  
  private void drawRealmsLogo(int x, int y) {
    RealmsScreen.bind("realms:textures/gui/title/realms.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2 - 5, 0.0F, 0.0F, 200, 50, 200.0F, 50.0F);
    
    GL11.glPopMatrix();
  }
  
  public void mouseClicked(int x, int y, int buttonNum)
  {
    if (inPendingInvitationArea(x, y)) {
      stopRealmsFetcherAndPinger();
      RealmsPendingInvitesScreen pendingInvitationScreen = new RealmsPendingInvitesScreen(lastScreen);
      Realms.setScreen(pendingInvitationScreen);
    }
    
    if (onLink) {
      com.mojang.realmsclient.util.RealmsUtil.browseTo("https://minecraft.net/realms");
    }
  }
  
  private void drawInvitationPendingIcon(int xm, int ym)
  {
    int pendingInvitesCount = numberOfPendingInvites;
    boolean hovering = inPendingInvitationArea(xm, ym);
    
    int baseX = width() / 2 + 50;
    int baseY = 8;
    
    if (pendingInvitesCount != 0) {
      float scale = 0.25F + (1.0F + RealmsMth.sin(animTick * 0.5F)) * 0.25F;
      int color = 0xFF000000 | (int)(scale * 64.0F) << 16 | (int)(scale * 64.0F) << 8 | (int)(scale * 64.0F) << 0;
      
      fillGradient(baseX - 2, 6, baseX + 18, 26, color, color);
      
      color = 0xFF000000 | (int)(scale * 255.0F) << 16 | (int)(scale * 255.0F) << 8 | (int)(scale * 255.0F) << 0;
      fillGradient(baseX - 2, 6, baseX + 18, 7, color, color);
      fillGradient(baseX - 2, 6, baseX - 1, 26, color, color);
      fillGradient(baseX + 17, 6, baseX + 18, 26, color, color);
      fillGradient(baseX - 2, 25, baseX + 18, 26, color, color);
    }
    
    RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    RealmsScreen.blit(baseX, 2, hovering ? 16.0F : 0.0F, 0.0F, 15, 25, 31.0F, 25.0F);
    GL11.glPopMatrix();
    
    if (pendingInvitesCount != 0) {
      int spritePos = (Math.min(pendingInvitesCount, 6) - 1) * 8;
      
      int yOff = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((10 + animTick) * 0.57F), RealmsMth.cos(animTick * 0.35F))) * -6.0F);
      
      RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(baseX + 4, 12 + yOff, spritePos, hovering ? 8.0F : 0.0F, 8, 8, 48.0F, 16.0F);
      
      GL11.glPopMatrix();
    }
    
    if (hovering) {
      int rx = xm + 12;
      int ry = ym - 12;
      
      String message = pendingInvitesCount == 0 ? getLocalizedString("mco.invites.nopending") : getLocalizedString("mco.invites.pending");
      
      int width = fontWidth(message);
      fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, -1073741824, -1073741824);
      fontDrawShadow(message, rx, ry, -1);
    }
  }
  
  private boolean inPendingInvitationArea(int xm, int ym)
  {
    int x1 = width() / 2 + 50;
    int x2 = width() / 2 + 66;
    int y1 = 13;
    int y2 = 27;
    
    return (x1 <= xm) && (xm <= x2) && (y1 <= ym) && (ym <= y2);
  }
  
  public void play(RealmsServer server) {
    if (server != null) {
      stopRealmsFetcherAndPinger();
      dontSetConnectedToRealms = true;
      
      if ((resourcePackUrl != null) && (resourcePackHash != null)) {
        resourcePackServer = server;
        saveListScrollPosition();
        String line2 = getLocalizedString("mco.configure.world.resourcepack.question.line1");
        String line3 = getLocalizedString("mco.configure.world.resourcepack.question.line2");
        Realms.setScreen(new RealmsLongConfirmationScreen(this, com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 100));
      } else {
        connectToServer(server);
      }
    }
  }
  
  private void connectToServer(RealmsServer server) {
    RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this, new com.mojang.realmsclient.util.RealmsTasks.RealmsConnectTask(this, server));
    longRunningMcoTaskScreen.start();
    Realms.setScreen(longRunningMcoTaskScreen);
  }
  
  private class ServerSelectionList extends net.minecraft.realms.RealmsScrolledSelectionList
  {
    public ServerSelectionList() {
      super(height(), 32, height() - 64, 36);
    }
    
    public int getItemCount()
    {
      if (RealmsMainScreen.trialsAvailable) {
        return realmsServers.size() + 1;
      }
      
      return realmsServers.size();
    }
    
    public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse)
    {
      if (RealmsMainScreen.trialsAvailable) {
        if (item == 0) {
          RealmsMainScreen.this.createTrial();
          return;
        }
        item--;
      }
      

      if (item >= realmsServers.size()) {
        return;
      }
      
      RealmsServer server = (RealmsServer)realmsServers.get(item);
      
      if (state == RealmsServer.State.UNINITIALIZED) {
        selectedServerId = -1L;
        RealmsMainScreen.this.stopRealmsFetcherAndPinger();
        Realms.setScreen(new com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen(server, RealmsMainScreen.this));
      } else {
        selectedServerId = id;
      }
      
      configureButton.active((RealmsMainScreen.overrideConfigure) || ((RealmsMainScreen.this.isSelfOwnedServer(server)) && (state != RealmsServer.State.ADMIN_LOCK) && (state != RealmsServer.State.UNINITIALIZED)));
      leaveButton.active(!RealmsMainScreen.this.isSelfOwnedServer(server));
      playButton.active((state == RealmsServer.State.OPEN) && (!expired));
      
      if ((doubleClick) && (playButton.active())) {
        play(RealmsMainScreen.this.findServer(selectedServerId));
      }
    }
    
    public boolean isSelectedItem(int item)
    {
      if (RealmsMainScreen.trialsAvailable) {
        if (item == 0) {
          return false;
        }
        item--;
      }
      

      return item == RealmsMainScreen.this.findIndex(selectedServerId);
    }
    
    public int getMaxPosition()
    {
      return getItemCount() * 36;
    }
    
    public void renderBackground()
    {
      RealmsMainScreen.this.renderBackground();
    }
    
    protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY)
    {
      if (RealmsMainScreen.trialsAvailable) {
        if (i == 0) {
          renderTrialItem(i, x, y);
          return;
        }
        i--;
      }
      

      if (i < realmsServers.size()) {
        renderMcoServerItem(i, x, y);
      }
    }
    
    private void renderTrialItem(int i, int x, int y) {
      int ry = y + 12;
      int index = 0;
      
      String msg = RealmsScreen.getLocalizedString("mco.trial.message");
      
      boolean hovered = false;
      

      if ((x <= xm()) && (xm() <= getScrollbarPosition()) && (y <= ym()) && (ym() <= y + 32)) {
        hovered = true;
      }
      
      float scale = 0.5F + (1.0F + RealmsMth.sin(animTick * 0.25F)) * 0.25F;
      int textColor;
      int textColor; if (hovered) {
        textColor = 0xFF | (int)(127.0F * scale) << 16 | (int)(255.0F * scale) << 8 | (int)(127.0F * scale);
      } else {
        textColor = 0xFF000000 | (int)(127.0F * scale) << 16 | (int)(255.0F * scale) << 8 | (int)(127.0F * scale);
      }
      for (String s : msg.split("\\\\n")) {
        drawCenteredString(s, width() / 2, ry + index, textColor);
        index += 10;
      }
    }
    
    private void renderMcoServerItem(int i, int x, int y) {
      final RealmsServer serverData = (RealmsServer)realmsServers.get(i);
      
      int nameColor = RealmsMainScreen.this.isSelfOwnedServer(serverData) ? 8388479 : 16777215;
      
      if (state == RealmsServer.State.UNINITIALIZED) {
        RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3008);
        GL11.glPushMatrix();
        RealmsScreen.blit(x + 10, y + 6, 0.0F, 0.0F, 40, 20, 40.0F, 20.0F);
        GL11.glPopMatrix();
        
        float scale = 0.5F + (1.0F + RealmsMth.sin(animTick * 0.25F)) * 0.25F;
        int textColor = 0xFF000000 | (int)(127.0F * scale) << 16 | (int)(255.0F * scale) << 8 | (int)(127.0F * scale);
        drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), x + 10 + 40 + 75, y + 12, textColor);
        
        return;
      }
      
      if (serverData.shouldPing(Realms.currentTimeMillis())) {
        serverPing.lastPingSnapshot = Realms.currentTimeMillis();
        
        RealmsMainScreen.THREAD_POOL.submit(new Runnable()
        {
          public void run() {
            try {
              RealmsMainScreen.statusPinger.pingServer(serverDataip, serverDataserverPing);
            } catch (UnknownHostException e) {
              RealmsMainScreen.LOGGER.error("Pinger: Could not resolve host");
            }
          }
        });
      }
      
      drawString(serverData.getName(), x + 2, y + 1, nameColor);
      
      int dx = 207;
      int dy = 1;
      
      if (expired) {
        RealmsMainScreen.this.drawExpired(x + dx, y + dy, xm(), ym());
      } else if (state == RealmsServer.State.CLOSED) {
        RealmsMainScreen.this.drawClose(x + dx, y + dy, xm(), ym());
      } else if ((RealmsMainScreen.this.isSelfOwnedServer(serverData)) && (daysLeft < 7)) {
        showStatus(x - 14, y, serverData);
        RealmsMainScreen.this.drawExpiring(x + dx, y + dy, xm(), ym(), daysLeft);
      } else if (state == RealmsServer.State.OPEN) {
        RealmsMainScreen.this.drawOpen(x + dx, y + dy, xm(), ym());
        showStatus(x - 14, y, serverData);
      } else if (state == RealmsServer.State.ADMIN_LOCK) {
        RealmsMainScreen.this.drawLocked(x + dx, y + dy, xm(), ym());
      }
      
      String noPlayers = "0";
      
      if (!serverPing.nrOfPlayers.equals(noPlayers)) {
        String coloredNumPlayers = com.mojang.realmsclient.gui.ChatFormatting.GRAY + "" + serverPing.nrOfPlayers;
        drawString(coloredNumPlayers, x + 200 - fontWidth(coloredNumPlayers), y + 1, 8421504);
        
        if ((xm() >= x + 200 - fontWidth(coloredNumPlayers)) && (xm() <= x + 200) && (ym() >= y + 1) && (ym() <= y + 9) && (ym() < height() - 64) && (ym() > 32)) {
          toolTip = serverPing.playerList;
        }
      }
      
      if (worldType.equals(RealmsServer.WorldType.MINIGAME)) {
        int motdColor = 9206892;
        
        if (animTick % 10 < 5) {
          motdColor = 13413468;
        }
        
        String miniGameStr = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
        int mgWidth = fontWidth(miniGameStr);
        
        drawString(miniGameStr, x + 2, y + 12, motdColor);
        drawString(serverData.getMinigameName(), x + 2 + mgWidth, y + 12, 7105644);
      } else {
        drawString(serverData.getDescription(), x + 2, y + 12, 7105644);
      }
      
      drawString(owner, x + 2, y + 12 + 11, 5000268);
      


      RealmsScreen.bindFace(ownerUUID, owner);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.blit(x - 36, y, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
      RealmsScreen.blit(x - 36, y, 40.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
    }
    
    private void showStatus(int x, int y, RealmsServer serverData) {
      if (ip == null) {
        return;
      }
      
      if (status != null) {
        drawString(status, x + 215 - fontWidth(status), y + 1, 8421504);
      }
      
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.bind("textures/gui/icons.png");
    }
  }
  
  private boolean isSelfOwnedServer(RealmsServer serverData)
  {
    return (ownerUUID != null) && (ownerUUID.equals(Realms.getUUID()));
  }
  
  private boolean isSelfOwnedNonExpiredServer(RealmsServer serverData) {
    return (ownerUUID != null) && (ownerUUID.equals(Realms.getUUID())) && (!expired);
  }
  
  private void drawExpired(int x, int y, int xm, int ym) {
    RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 64) && (ym > 32)) {
      toolTip = getLocalizedString("mco.selectServer.expired");
    }
  }
  
  private void drawExpiring(int x, int y, int xm, int ym, int daysLeft) {
    if (animTick % 20 < 10) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
    }
    
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 64) && (ym > 32)) {
      if (daysLeft == 0) {
        toolTip = getLocalizedString("mco.selectServer.expires.soon");
      } else if (daysLeft == 1) {
        toolTip = getLocalizedString("mco.selectServer.expires.day");
      } else {
        toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[] { Integer.valueOf(daysLeft) });
      }
    }
  }
  
  private void drawOpen(int x, int y, int xm, int ym) {
    RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 64) && (ym > 32)) {
      toolTip = getLocalizedString("mco.selectServer.open");
    }
  }
  
  private void drawClose(int x, int y, int xm, int ym) {
    RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 64) && (ym > 32)) {
      toolTip = getLocalizedString("mco.selectServer.closed");
    }
  }
  
  private void drawLocked(int x, int y, int xm, int ym) {
    RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9) && (ym < height() - 64) && (ym > 32)) {
      toolTip = getLocalizedString("mco.selectServer.locked");
    }
  }
  
  protected void renderMousehoverTooltip(String msg, int x, int y) {
    if (msg == null) {
      return;
    }
    
    int rx = x + 12;
    int ry = y - 12;
    int index = 0;
    int width = 0;
    
    for (String s : msg.split("\n")) {
      int the_width = fontWidth(s);
      
      if (the_width > width) {
        width = the_width;
      }
    }
    
    for (String s : msg.split("\n")) {
      fillGradient(rx - 3, ry - (index == 0 ? 3 : 0) + index, rx + width + 3, ry + 8 + 3 + index, -1073741824, -1073741824);
      fontDrawShadow(s, rx, ry + index, 16777215);
      index += 10;
    }
  }
  
  private void renderLink(int xm, int ym) {
    String text = getLocalizedString("mco.selectServer.whatisrealms");
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    

    int textWidth = fontWidth(text);
    int leftPadding = 10;
    int topPadding = 12;
    
    int x1 = leftPadding;
    int x2 = x1 + textWidth + 1;
    int y1 = topPadding;
    int y2 = y1 + fontLineHeight();
    
    GL11.glTranslatef(x1, y1, 0.0F);
    
    if ((x1 <= xm) && (xm <= x2) && (y1 <= ym) && (ym <= y2)) {
      onLink = true;
      drawString(text, 0, 0, 7107012);
    } else {
      onLink = false;
      drawString(text, 0, 0, 3368635);
    }
    
    GL11.glPopMatrix();
  }
  
  private void renderStage()
  {
    String text = "STAGE!";
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    
    GL11.glTranslatef(width() / 2 - 25, 20.0F, 0.0F);
    GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
    GL11.glScalef(1.5F, 1.5F, 1.5F);
    
    drawString(text, 0, 0, 65280);
    
    GL11.glPopMatrix();
  }
  
  public RealmsScreen newScreen() {
    return new RealmsMainScreen(lastScreen);
  }
}
