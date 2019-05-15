package net.minecraft.command.server;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;

public class CommandAchievement extends net.minecraft.command.CommandBase
{
  private static final String __OBFID = "CL_00000113";
  
  public CommandAchievement() {}
  
  public String getCommandName()
  {
    return "achievement";
  }
  



  public int getRequiredPermissionLevel()
  {
    return 2;
  }
  
  public String getCommandUsage(ICommandSender sender)
  {
    return "commands.achievement.usage";
  }
  
  public void processCommand(ICommandSender sender, String[] args) throws CommandException
  {
    if (args.length < 2)
    {
      throw new net.minecraft.command.WrongUsageException("commands.achievement.usage", new Object[0]);
    }
    

    final StatBase var3 = StatList.getOneShotStat(args[1]);
    
    if ((var3 == null) && (!args[1].equals("*")))
    {
      throw new CommandException("commands.achievement.unknownAchievement", new Object[] { args[1] });
    }
    

    final EntityPlayerMP var4 = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
    boolean var5 = args[0].equalsIgnoreCase("give");
    boolean var6 = args[0].equalsIgnoreCase("take");
    
    if ((var5) || (var6))
    {
      if (var3 == null)
      {



        if (var5)
        {
          Iterator var11 = AchievementList.achievementList.iterator();
          
          while (var11.hasNext())
          {
            Achievement var12 = (Achievement)var11.next();
            var4.triggerAchievement(var12);
          }
          
          notifyOperators(sender, this, "commands.achievement.give.success.all", new Object[] { var4.getName() });
        }
        else if (var6)
        {
          Iterator var11 = Lists.reverse(AchievementList.achievementList).iterator();
          
          while (var11.hasNext())
          {
            Achievement var12 = (Achievement)var11.next();
            var4.func_175145_a(var12);
          }
          
          notifyOperators(sender, this, "commands.achievement.take.success.all", new Object[] { var4.getName() });
        }
      }
      else
      {
        if ((var3 instanceof Achievement))
        {
          Achievement var7 = (Achievement)var3;
          



          if (var5)
          {
            if (var4.getStatFile().hasAchievementUnlocked(var7))
            {
              throw new CommandException("commands.achievement.alreadyHave", new Object[] { var4.getName(), var3.func_150955_j() });
            }
            
            for (ArrayList var8 = Lists.newArrayList(); (parentAchievement != null) && (!var4.getStatFile().hasAchievementUnlocked(parentAchievement)); var7 = parentAchievement)
            {
              var8.add(parentAchievement);
            }
            
            Iterator var9 = Lists.reverse(var8).iterator();
            
            while (var9.hasNext())
            {
              Achievement var10 = (Achievement)var9.next();
              var4.triggerAchievement(var10);
            }
          }
          else if (var6)
          {
            if (!var4.getStatFile().hasAchievementUnlocked(var7))
            {
              throw new CommandException("commands.achievement.dontHave", new Object[] { var4.getName(), var3.func_150955_j() });
            }
            
            ArrayList var8 = Lists.newArrayList(Iterators.filter(AchievementList.achievementList.iterator(), new com.google.common.base.Predicate()
            {
              private static final String __OBFID = "CL_00002350";
              
              public boolean func_179605_a(Achievement p_179605_1_) {
                return (var4.getStatFile().hasAchievementUnlocked(p_179605_1_)) && (p_179605_1_ != var3);
              }
              

              public boolean apply(Object p_apply_1_) { return func_179605_a((Achievement)p_apply_1_); }
            }));
            for (; (parentAchievement != null) && (var4.getStatFile().hasAchievementUnlocked(parentAchievement)); var7 = parentAchievement)
            {
              var8.remove(parentAchievement);
            }
            Iterator var9 = var8.iterator();
            
            while (var9.hasNext())
            {
              Achievement var10 = (Achievement)var9.next();
              var4.func_175145_a(var10);
            }
          }
        }
        
        if (var5)
        {
          var4.triggerAchievement(var3);
          notifyOperators(sender, this, "commands.achievement.give.success.one", new Object[] { var4.getName(), var3.func_150955_j() });
        }
        else if (var6)
        {
          var4.func_175145_a(var3);
          notifyOperators(sender, this, "commands.achievement.take.success.one", new Object[] { var3.func_150955_j(), var4.getName() });
        }
      }
    }
  }
  


  public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
  {
    if (args.length == 1)
    {
      return getListOfStringsMatchingLastWord(args, new String[] { "give", "take" });
    }
    if (args.length != 2)
    {
      return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
    

    ArrayList var4 = Lists.newArrayList();
    Iterator var5 = StatList.allStats.iterator();
    
    while (var5.hasNext())
    {
      StatBase var6 = (StatBase)var5.next();
      var4.add(statId);
    }
    
    return func_175762_a(args, var4);
  }
  




  public boolean isUsernameIndex(String[] args, int index)
  {
    return index == 2;
  }
}
