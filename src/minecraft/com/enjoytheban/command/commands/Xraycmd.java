package com.enjoytheban.command.commands;

import com.enjoytheban.Client;
import com.enjoytheban.command.Command;
import com.enjoytheban.module.modules.render.Xray;
import com.enjoytheban.utils.Helper;
import java.util.Arrays;
import java.util.List;

public class Xraycmd extends Command
{
  public Xraycmd()
  {
    super("xray", new String[] { "oreesp" }, "", "nigga");
  }
  
  public String execute(String[] args)
  {
    Xray xray = (Xray)Client.instance.getModuleManager().getModuleByClass(Xray.class);
    if (args.length == 2) {
      if (com.enjoytheban.utils.math.MathUtil.parsable(args[1], (byte)4)) {
        int id = Integer.parseInt(args[1]);
        if (args[0].equalsIgnoreCase("add")) {
          blocks.add(Integer.valueOf(id));
          Helper.sendMessage("Added Block ID " + id);
        } else if (args[0].equalsIgnoreCase("remove")) {
          blocks.remove(id);
          Helper.sendMessage("Removed Block ID " + id);
        } else {
          Helper.sendMessage("Invalid syntax");
        }
      } else {
        Helper.sendMessage("Invalid block ID");
      }
    } else if ((args.length == 1) && 
      (args[0].equalsIgnoreCase("list"))) {
      Arrays.toString(blocks.toArray());
    }
    
    return null;
  }
}
