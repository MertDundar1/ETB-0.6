package com.enjoytheban.command.commands;

import com.enjoytheban.Client;
import com.enjoytheban.command.Command;
import com.enjoytheban.module.Module;
import com.enjoytheban.utils.Helper;
import net.minecraft.util.EnumChatFormatting;

public class Cheats extends Command
{
  public Cheats()
  {
    super("Cheats", new String[] { "mods" }, "", "sketit");
  }
  

  public String execute(String[] args)
  {
    if (args.length == 0)
    {
      Client.instance.getModuleManager();StringBuilder list = new StringBuilder(com.enjoytheban.management.ModuleManager.getModules().size() + " Cheats - ");
      Client.instance.getModuleManager(); for (Module cheat : com.enjoytheban.management.ModuleManager.getModules()) {
        list.append(cheat.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED).append(cheat.getName()).append(", ");
      }
      Helper.sendMessage("> " + list.toString().substring(0, list.toString().length() - 2));
    }
    else {
      Helper.sendMessage("> Correct usage .cheats");
    }
    return null;
  }
}
