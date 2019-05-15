package com.enjoytheban.command.commands;

import com.enjoytheban.Client;
import com.enjoytheban.command.Command;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.utils.Helper;
import org.lwjgl.input.Keyboard;






public class Bind
  extends Command
{
  public Bind()
  {
    super("Bind", new String[] { "b" }, "", "sketit");
  }
  

  public String execute(String[] args)
  {
    if (args.length >= 2)
    {
      Module m = Client.instance.getModuleManager().getAlias(args[0]);
      
      if (m != null) {
        int k = Keyboard.getKeyIndex(args[1].toUpperCase());
        
        m.setKey(k);
        
        Helper.sendMessage(
          String.format("> Bound %s to %s", new Object[] { m.getName(), k == 0 ? "none" : args[1].toUpperCase() }));
      } else {
        Helper.sendMessage("> Invalid module name, double check spelling.");
      }
    } else {
      Helper.sendMessageWithoutPrefix("§bCorrect usage:§7 .bind <module> <key>");
    }
    return null;
  }
}
