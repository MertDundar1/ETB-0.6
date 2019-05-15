package com.enjoytheban.utils;

import com.enjoytheban.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Helper
{
  public static Minecraft mc = ;
  
  public Helper() {}
  
  public static void sendMessageOLD(String msg) { Object[] tmp16_13 = new Object[2];
    Client.instance.getClass();tmp16_13[0] = (EnumChatFormatting.BLUE + "ETB" + EnumChatFormatting.GRAY + ": "); Object[] tmp58_16 = tmp16_13;tmp58_16[1] = msg;mcthePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", tmp58_16)));
  }
  
  public static void sendMessage(String message)
  {
    new ChatUtils.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
  }
  
  public static void sendMessageWithoutPrefix(String message)
  {
    new ChatUtils.ChatMessageBuilder(false, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
  }
  
  public static boolean onServer(String server)
  {
    return (!mc.isSingleplayer()) && (mcgetCurrentServerDataserverIP.toLowerCase().contains(server));
  }
}
