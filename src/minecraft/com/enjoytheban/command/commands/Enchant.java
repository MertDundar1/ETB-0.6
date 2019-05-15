package com.enjoytheban.command.commands;

import com.enjoytheban.command.Command;
import com.enjoytheban.utils.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;



public class Enchant
  extends Command
{
  public Enchant()
  {
    super("Enchant", new String[] { "e" }, "", "enchanth");
  }
  
  public String execute(String[] args)
  {
    if (args.length < 1) {
      getMinecraftthePlayer.sendChatMessage("/give " + getMinecraftthePlayer.getName() + 
        " diamond_sword 1 0 {ench:[{id:16,lvl:127}]}");
    } else {
      Helper.sendMessage("invalid syntax Valid .enchant");
    }
    return null;
  }
}
