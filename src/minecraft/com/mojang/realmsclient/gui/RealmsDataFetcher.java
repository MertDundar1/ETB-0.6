package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.McoServerComparator;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

public class RealmsDataFetcher
{
  private static final Logger LOGGER = ;
  
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
  
  private static final int SERVER_UPDATE_INTERVAL = 60;
  
  private static final int PENDING_INVITES_INTERVAL = 10;
  private static final int TRIAL_UPDATE_INTERVAL = 60;
  private volatile boolean stopped = true;
  
  private ServerListUpdateTask serverListUpdateTask = new ServerListUpdateTask(null);
  private PendingInviteUpdateTask pendingInviteUpdateTask = new PendingInviteUpdateTask(null);
  private TrialAvailabilityTask trialAvailabilityTask = new TrialAvailabilityTask(null);
  
  private Set<RealmsServer> removedServers = Sets.newHashSet();
  private List<RealmsServer> servers = Lists.newArrayList();
  private int pendingInvitesCount;
  private boolean trialAvailable = false;
  
  private ScheduledFuture<?> serverListScheduledFuture;
  
  private ScheduledFuture<?> pendingInviteScheduledFuture;
  private ScheduledFuture<?> trialAvailableScheduledFuture;
  private Map<String, Boolean> fetchStatus = new ConcurrentHashMap(Task.values().length);
  
  public RealmsDataFetcher() {
    scheduleTasks();
  }
  
  public synchronized void init() {
    if (stopped) {
      stopped = false;
      cancelTasks();
      scheduleTasks();
    }
  }
  
  public synchronized boolean isFetchedSinceLastTry(Task task) {
    Boolean result = (Boolean)fetchStatus.get(task.toString());
    return result == null ? false : result.booleanValue();
  }
  
  public synchronized void markClean() {
    for (String task : fetchStatus.keySet()) {
      fetchStatus.put(task, Boolean.valueOf(false));
    }
  }
  
  public synchronized void forceUpdate() {
    stop();
    init();
  }
  
  public synchronized List<RealmsServer> getServers() {
    return Lists.newArrayList(servers);
  }
  
  public int getPendingInvitesCount() {
    return pendingInvitesCount;
  }
  
  public boolean isTrialAvailable() {
    return trialAvailable;
  }
  
  public synchronized void stop() {
    stopped = true;
    cancelTasks();
  }
  
  private void scheduleTasks() {
    for (Task task : ) {
      fetchStatus.put(task.toString(), Boolean.valueOf(false));
    }
    
    serverListScheduledFuture = scheduler.scheduleAtFixedRate(serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
    pendingInviteScheduledFuture = scheduler.scheduleAtFixedRate(pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
    trialAvailableScheduledFuture = scheduler.scheduleAtFixedRate(trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
  }
  
  private void cancelTasks() {
    try {
      serverListScheduledFuture.cancel(false);
      pendingInviteScheduledFuture.cancel(false);
      trialAvailableScheduledFuture.cancel(false);
    } catch (Exception e) {
      LOGGER.error("Failed to cancel Realms tasks");
    }
  }
  
  private synchronized void setServers(List<RealmsServer> newServers) {
    int removedCnt = 0;
    
    for (RealmsServer server : removedServers) {
      if (newServers.remove(server)) {
        removedCnt++;
      }
    }
    
    if (removedCnt == 0) {
      removedServers.clear();
    }
    
    servers = newServers;
  }
  
  private synchronized void setTrialAvailabile(boolean trialAvailabile) {
    trialAvailable = trialAvailabile;
  }
  
  public synchronized void removeItem(RealmsServer server) {
    servers.remove(server);
    removedServers.add(server);
  }
  
  private void sort(List<RealmsServer> servers) {
    java.util.Collections.sort(servers, new RealmsServer.McoServerComparator(Realms.getName()));
  }
  
  private boolean isActive() {
    return (!stopped) && (Display.isActive());
  }
  
  private class ServerListUpdateTask
    implements Runnable
  {
    private ServerListUpdateTask() {}
    
    public void run()
    {
      if (RealmsDataFetcher.this.isActive()) {
        updateServersList();
      }
    }
    
    private void updateServersList() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        
        if (client != null) {
          List<RealmsServer> servers = listWorldsservers;
          
          if (servers != null) {
            RealmsDataFetcher.this.sort(servers);
            RealmsDataFetcher.this.setServers(servers);
            fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST.toString(), Boolean.valueOf(true));
          } else {
            RealmsDataFetcher.LOGGER.warn("Realms server list was null or empty");
          }
        }
      } catch (RealmsServiceException e) {
        RealmsDataFetcher.LOGGER.error("Couldn't get server list", e);
      } catch (IOException e) {
        RealmsDataFetcher.LOGGER.error("Couldn't parse response from server getting list");
      }
    }
  }
  

  private class PendingInviteUpdateTask
    implements Runnable
  {
    private PendingInviteUpdateTask() {}
    
    public void run()
    {
      if (RealmsDataFetcher.this.isActive()) {
        updatePendingInvites();
      }
    }
    
    private void updatePendingInvites() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        
        if (client != null) {
          pendingInvitesCount = client.pendingInvitesCount();
          fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE.toString(), Boolean.valueOf(true));
        }
      } catch (RealmsServiceException e) {
        RealmsDataFetcher.LOGGER.error("Couldn't get pending invite count", e);
      }
    }
  }
  

  private class TrialAvailabilityTask
    implements Runnable
  {
    private TrialAvailabilityTask() {}
    
    public void run()
    {
      if (RealmsDataFetcher.this.isActive()) {
        getTrialAvailable();
      }
    }
    
    private void getTrialAvailable() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        
        if (client != null) {
          trialAvailable = client.trialAvailable().booleanValue();
          fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE.toString(), Boolean.valueOf(true));
        }
      } catch (RealmsServiceException e) {
        RealmsDataFetcher.LOGGER.error("Couldn't get trial availability", e);
      } catch (IOException e) {
        RealmsDataFetcher.LOGGER.error("Couldn't parse response from checking trial availability");
      }
    }
  }
  
  public static enum Task
  {
    SERVER_LIST, 
    PENDING_INVITE, 
    TRIAL_AVAILABLE;
    
    private Task() {}
  }
}
