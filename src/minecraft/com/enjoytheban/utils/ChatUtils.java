package com.enjoytheban.utils;

import com.enjoytheban.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtils
{
  private final ChatComponentText message;
  
  private ChatUtils(ChatComponentText message)
  {
    this.message = message;
  }
  
  public static String addFormat(String message, String regex) {
    return message.replaceAll("(?i)" + regex + "([0-9a-fklmnor])", "§$1");
  }
  
  public void displayClientSided() {
    getMinecraftthePlayer.addChatMessage(message);
  }
  
  private ChatComponentText getChatComponent() {
    return message;
  }
  







  public static class ChatMessageBuilder
  {
    private static final EnumChatFormatting defaultMessageColor = EnumChatFormatting.WHITE;
    private ChatComponentText theMessage;
    private boolean useDefaultMessageColor;
    
    public ChatMessageBuilder(boolean prependDefaultPrefix, boolean useDefaultMessageColor) { theMessage = new ChatComponentText("");
      this.useDefaultMessageColor = false;
      workingStyle = new ChatStyle();
      workerMessage = new ChatComponentText("");
      if (prependDefaultPrefix) {
        Client.instance.getClass();theMessage.appendSibling(new ChatMessageBuilder(false, false).appendText(String.valueOf(EnumChatFormatting.AQUA + "ETB" + " ")).setColor(EnumChatFormatting.RED).build().getChatComponent());
      }
      this.useDefaultMessageColor = useDefaultMessageColor; }
    
    private ChatStyle workingStyle;
    private ChatComponentText workerMessage;
    public ChatMessageBuilder() { theMessage = new ChatComponentText("");
      useDefaultMessageColor = false;
      workingStyle = new ChatStyle();
      workerMessage = new ChatComponentText("");
    }
    
    public ChatMessageBuilder appendText(String text) {
      appendSibling();
      workerMessage = new ChatComponentText(text);
      workingStyle = new ChatStyle();
      if (useDefaultMessageColor) {
        setColor(defaultMessageColor);
      }
      return this;
    }
    
    public ChatMessageBuilder setColor(EnumChatFormatting color) {
      workingStyle.setColor(color);
      return this;
    }
    
    public ChatMessageBuilder bold() {
      workingStyle.setBold(Boolean.valueOf(true));
      return this;
    }
    
    public ChatMessageBuilder italic() {
      workingStyle.setItalic(Boolean.valueOf(true));
      return this;
    }
    
    public ChatMessageBuilder strikethrough() {
      workingStyle.setStrikethrough(Boolean.valueOf(true));
      return this;
    }
    
    public ChatMessageBuilder underline() {
      workingStyle.setUnderlined(Boolean.valueOf(true));
      return this;
    }
    
    public ChatUtils build() {
      appendSibling();
      return new ChatUtils(theMessage, null);
    }
    
    private void appendSibling() {
      theMessage.appendSibling(workerMessage.setChatStyle(workingStyle));
    }
  }
}
