package com.enjoytheban.command.commands;

import com.enjoytheban.command.Command;
import com.enjoytheban.utils.Helper;
import com.enjoytheban.utils.TimerUtil;
import com.enjoytheban.utils.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumChatFormatting;

public class VClip
  extends Command
{
  private TimerUtil timer = new TimerUtil();
  
  public VClip() {
    super("Vc", new String[] { "Vclip", "clip", "verticalclip", "clip" }, "", "Teleport down a specific ammount");
  }
  
  public String execute(String[] args)
  {
    if (!Helper.onServer("enjoytheban")) {
      if (args.length > 0) {
        if (MathUtil.parsable(args[0], (byte)4)) {
          float distance = Float.parseFloat(args[0]);
          mcthePlayer.setPosition(mcthePlayer.posX, mcthePlayer.posY + distance, 
            mcthePlayer.posZ);
          Helper.sendMessage("> Vclipped " + distance + " blocks");
        } else {
          syntaxError(EnumChatFormatting.GRAY + args[0] + " is not a valid number");
        }
      } else {
        syntaxError(EnumChatFormatting.GRAY + "Valid .vclip <number>");
      }
    } else {
      Helper.sendMessage("> You cannot use vclip on the ETB Server.");
    }
    return null;
  }
}
