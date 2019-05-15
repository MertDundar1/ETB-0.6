package com.enjoytheban.management;

import com.enjoytheban.Client;
import com.enjoytheban.command.Command;
import com.enjoytheban.utils.Helper;
import java.util.HashMap;
import java.util.List;
import net.minecraft.util.EnumChatFormatting;








public class FriendManager
  implements Manager
{
  private static HashMap<String, String> friends;
  
  public FriendManager() {}
  
  public void init()
  {
    friends = new HashMap();
    
    List<String> frriends = FileManager.read("Friends.txt");
    for (String v : frriends) {
      if (v.contains(":")) {
        String name = v.split(":")[0];
        String alias = v.split(":")[1];
        friends.put(name, alias);
      } else {
        friends.put(v, v);
      }
    }
    
    Client.instance.getCommandManager().add(new Command("f", new String[] { "friend", "fren", "fr" }, 
      "add/del/list name alias", "Manage client friends")
      {
        private final FriendManager fm = FriendManager.this;
        


        public String execute(String[] args)
        {
          if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
              String f = "";
              f = f + String.format("%s:%s%s", new Object[] { args[1], args[2], System.lineSeparator() });
              FriendManager.friends.put(args[1], args[2]);
              Helper.sendMessage("> " + String.format("%s has been added as %s", new Object[] { args[1], args[2] }));
              FileManager.save("Friends.txt", f, true);
            }
            else if (args[0].equalsIgnoreCase("del")) {
              FriendManager.friends.remove(args[1]);
              Helper.sendMessage("> " + 
                String.format("%s has been removed from your friends list", new Object[] { args[1] }));
            }
            else if (args[0].equalsIgnoreCase("list")) {
              if (FriendManager.friends.size() > 0) {
                int friends = 1;
                for (String fr : FriendManager.friends.values()) {
                  Helper.sendMessage("> " + String.format("%s. %s", new Object[] { Integer.valueOf(friends), fr }));
                  friends++;
                }
              }
              else {
                Helper.sendMessage("> get some friends fag lmao");
              }
            }
          }
          else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
              String f = "";
              f = f + String.format("%s%s", new Object[] { args[1], System.lineSeparator() });
              FriendManager.friends.put(args[1], args[1]);
              Helper.sendMessage("> " + String.format("%s has been added as %s", new Object[] { args[1], args[1] }));
              FileManager.save("Friends.txt", f, true);
            }
            else if (args[0].equalsIgnoreCase("del")) {
              FriendManager.friends.remove(args[1]);
              Helper.sendMessage("> " + 
                String.format("%s has been removed from your friends list", new Object[] { args[1] }));
            }
            else if (args[0].equalsIgnoreCase("list")) {
              if (FriendManager.friends.size() > 0) {
                int friends = 1;
                for (String fr : FriendManager.friends.values()) {
                  Helper.sendMessage("> " + String.format("%s. %s", new Object[] { Integer.valueOf(friends), fr }));
                  friends++;
                }
              } else {
                Helper.sendMessage("> you dont have any you lonely fuck");
              }
            }
          }
          else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
              if (FriendManager.friends.size() > 0) {
                int friends = 1;
                for (String fr : FriendManager.friends.values()) {
                  Helper.sendMessage(String.format("%s. %s", new Object[] { Integer.valueOf(friends), fr }));
                  friends++;
                }
              } else {
                Helper.sendMessage("you dont have any you lonely fuck");
              }
            }
            else if ((args[0].equalsIgnoreCase("add")) || (args[0].equalsIgnoreCase("del"))) {
              Helper.sendMessage(
                "> " + EnumChatFormatting.GRAY + "Please enter a players name");
            } else {
              Helper.sendMessage(
                "> Correct usage: " + EnumChatFormatting.GRAY + "Valid .f add/del <player>");
            }
          }
          else if (args.length == 0) {
            Helper.sendMessage(
              "> Correct usage: " + EnumChatFormatting.GRAY + "Valid .f add/del <player>");
          }
          return null;
        }
      });
  }
  
  public static boolean isFriend(String name)
  {
    return friends.containsKey(name);
  }
  
  public static String getAlias(String name)
  {
    return (String)friends.get(name);
  }
  
  public static HashMap<String, String> getFriends()
  {
    return friends;
  }
}
