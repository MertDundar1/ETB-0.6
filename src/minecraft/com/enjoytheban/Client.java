package com.enjoytheban;

import com.enjoytheban.api.value.Value;
import com.enjoytheban.management.CommandManager;
import com.enjoytheban.management.FileManager;
import com.enjoytheban.management.FriendManager;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.modules.render.UI.TabUI;
import com.enjoytheban.ui.login.AltManager;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence.Builder;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ResourceLocation;









public class Client
{
  public final String name = "ETB";
  public final double version = 0.6D;
  public static boolean publicMode = false;
  

  IPCClient client = new IPCClient(500494614311206913L);
  

  public static Client instance = new Client();
  
  private ModuleManager modulemanager;
  
  private CommandManager commandmanager;
  
  private AltManager altmanager;
  
  private FriendManager friendmanager;
  private TabUI tabui;
  public static ResourceLocation CLIENT_CAPE = new ResourceLocation("ETB/cape.png");
  
  public Client() {}
  
  public void initiate()
  {
    (this.commandmanager = new CommandManager()).init();
    (this.friendmanager = new FriendManager()).init();
    (this.modulemanager = new ModuleManager()).init();
    
    (this.tabui = new TabUI()).init();
    
    altmanager = new AltManager();AltManager.init();
    AltManager.setupAlts();
    
    FileManager.init();
    onReady(client);
  }
  
  public ModuleManager getModuleManager()
  {
    return modulemanager;
  }
  
  public CommandManager getCommandManager()
  {
    return commandmanager;
  }
  
  public AltManager getAltManager() {
    return altmanager;
  }
  

  public void shutDown()
  {
    String values = "";
    
    instance.getModuleManager(); Iterator localIterator2; Value v; for (Iterator localIterator1 = ModuleManager.getModules().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Module m = (Module)localIterator1.next();
      
      localIterator2 = m.getValues().iterator(); continue;v = (Value)localIterator2.next();
      
      values = values + String.format("%s:%s:%s%s", new Object[] { m.getName(), v.getName(), v.getValue(), System.lineSeparator() });
    }
    

    FileManager.save("Values.txt", values, false);
    
    String enabled = "";
    
    instance.getModuleManager(); for (Module m : ModuleManager.getModules())
    {
      if (m.isEnabled())
      {

        enabled = enabled + String.format("%s%s", new Object[] { m.getName(), System.lineSeparator() });
      }
    }
    FileManager.save("Enabled.txt", enabled, false);
  }
  
  public void onReady(IPCClient client) {
    client.setListener(new IPCListener()
    {
      public void onReady(IPCClient client) {
        RichPresence.Builder builder = new RichPresence.Builder();
        builder.setDetails("ETB 0.6")
          .setState("Minecraft 1.8")
          .setStartTimestamp(OffsetDateTime.now())
          .setDetails("https://www.enjoytheban.com/")
          .setLargeImage("etb_logo", "https://www.enjoytheban.com/");
        client.sendRichPresence(builder.build());
      }
    });
    try {
      client.connect(new DiscordBuild[0]);
      System.out.println("RPC Set!");
    } catch (NoDiscordClientException e) {
      e.printStackTrace();
    }
  }
}
