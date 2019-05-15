package com.enjoytheban.utils.proxy;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventTick;
import com.enjoytheban.utils.Helper;
import java.net.ServerSocket;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ChatComponentText;







public class ProxyConfig
{
  public ProxyConfig() {}
  
  public static int listenerPort = findFreePort(29999);
  public static TransparentProxy proxy = null;
  public static ConnectionInfo proxyAddr = null;
  
  public static ConnectionInfo connect(String ip, int port) {
    if (proxyAddr != null) {
      if (proxy == null) {
        proxy = new TransparentProxy(listenerPort);
      }
      if (proxy.isRunning()) {
        proxy.stop();
      }
      try {
        proxy.start(proxyAddrip, proxyAddrport, ip, port);
        while ((!proxy.isReady()) && (!proxy.hasFailed())) {
          Thread.sleep(50L);
        }
        if (proxy.hasFailed()) {
          throw proxy.getFailReason();
        }
        ConnectionInfo returnment = new ConnectionInfo();
        ip = "127.0.0.1";
        port = listenerPort;
        return returnment;
      } catch (Exception e) {
        e.printStackTrace();
        ConnectionInfo returnment = new ConnectionInfo();
        ip = ip;
        port = port;
        return returnment;
      }
    }
    ConnectionInfo returnment = new ConnectionInfo();
    ip = ip;
    port = port;
    return returnment;
  }
  
  @EventHandler
  public void onGlobalGameLoop(EventTick e) {
    if ((proxy != null) && (proxy.isRunning()) && (proxy.hasFailed())) {
      boolean flag = Helper.mc.isIntegratedServerRunning();
      boolean flag1 = Helper.mc.isSingleplayer();
      mctheWorld.sendQuittingDisconnectingPacket();
      Helper.mc.loadWorld(null);
      if (flag) {
        Helper.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.closed", new ChatComponentText("[AC-Proxy] Lost Connection to server!")));
      } else if (flag1) {
        RealmsBridge realmsbridge = new RealmsBridge();
        realmsbridge.switchToRealms(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.closed", new ChatComponentText("[AC-Proxy] Lost Connection to server!")));
      } else {
        Helper.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.closed", new ChatComponentText("[AC-Proxy] Lost Connection to server!")));
      }
    }
  }
  





  public static void stop()
  {
    if (proxy != null) {
      proxy.stop();
    }
  }
  
  private static int findFreePort(int start)
  {
    int cur = new Random().nextInt(10000) + start - 1;
    for (;;)
    {
      try {
        ServerSocket sock = new ServerSocket(cur, 1);
        sock.close();
        return cur;
      } catch (Exception var3) {
        cur = new Random().nextInt(10000) + start - 1;
      }
    }
  }
}
