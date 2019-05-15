package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import optifine.CrashReporter;
import optifine.Reflector;
import optifine.ReflectorMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport
{
  private static final Logger logger = ;
  

  private final String description;
  

  private final Throwable cause;
  

  private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
  

  private final List crashReportSections = Lists.newArrayList();
  
  private File crashReportFile;
  
  private boolean field_85059_f = true;
  private StackTraceElement[] stacktrace = new StackTraceElement[0];
  private static final String __OBFID = "CL_00000990";
  private boolean reported = false;
  
  public CrashReport(String descriptionIn, Throwable causeThrowable)
  {
    description = descriptionIn;
    cause = causeThrowable;
    populateEnvironment();
  }
  




  private void populateEnvironment()
  {
    theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable()
    {
      private static final String __OBFID = "CL_00001197";
      
      public String call() {
        return "1.8";
      }
    });
    theReportCategory.addCrashSectionCallable("Operating System", new Callable()
    {
      private static final String __OBFID = "CL_00001222";
      
      public String call() {
        return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
      }
    });
    theReportCategory.addCrashSectionCallable("Java Version", new Callable()
    {
      private static final String __OBFID = "CL_00001248";
      
      public String call() {
        return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
      }
    });
    theReportCategory.addCrashSectionCallable("Java VM Version", new Callable()
    {
      private static final String __OBFID = "CL_00001275";
      
      public String call() {
        return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
      }
    });
    theReportCategory.addCrashSectionCallable("Memory", new Callable()
    {
      private static final String __OBFID = "CL_00001302";
      
      public String call() {
        Runtime var1 = Runtime.getRuntime();
        long var2 = var1.maxMemory();
        long var4 = var1.totalMemory();
        long var6 = var1.freeMemory();
        long var8 = var2 / 1024L / 1024L;
        long var10 = var4 / 1024L / 1024L;
        long var12 = var6 / 1024L / 1024L;
        return var6 + " bytes (" + var12 + " MB) / " + var4 + " bytes (" + var10 + " MB) up to " + var2 + " bytes (" + var8 + " MB)";
      }
    });
    theReportCategory.addCrashSectionCallable("JVM Flags", new Callable()
    {
      private static final String __OBFID = "CL_00001329";
      
      public String call() {
        RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
        List var2 = var1.getInputArguments();
        int var3 = 0;
        StringBuilder var4 = new StringBuilder();
        Iterator var5 = var2.iterator();
        
        while (var5.hasNext())
        {
          String var6 = (String)var5.next();
          
          if (var6.startsWith("-X"))
          {
            if (var3++ > 0)
            {
              var4.append(" ");
            }
            
            var4.append(var6);
          }
        }
        
        return String.format("%d total; %s", new Object[] { Integer.valueOf(var3), var4.toString() });
      }
    });
    theReportCategory.addCrashSectionCallable("IntCache", new Callable()
    {
      private static final String __OBFID = "CL_00001355";
      
      public String call() {
        return IntCache.getCacheSizes();
      }
    });
    
    if (Reflector.FMLCommonHandler_enhanceCrashReport.exists())
    {
      Object instance = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
      Reflector.callString(instance, Reflector.FMLCommonHandler_enhanceCrashReport, new Object[] { this, theReportCategory });
    }
  }
  



  public String getDescription()
  {
    return description;
  }
  



  public Throwable getCrashCause()
  {
    return cause;
  }
  



  public void getSectionsInStringBuilder(StringBuilder builder)
  {
    if (((stacktrace == null) || (stacktrace.length <= 0)) && (crashReportSections.size() > 0))
    {
      stacktrace = ((StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)crashReportSections.get(0)).getStackTrace(), 0, 1));
    }
    
    if ((stacktrace != null) && (stacktrace.length > 0))
    {
      builder.append("-- Head --\n");
      builder.append("Stacktrace:\n");
      StackTraceElement[] var6 = stacktrace;
      int var7 = var6.length;
      
      for (int var4 = 0; var4 < var7; var4++)
      {
        StackTraceElement var5 = var6[var4];
        builder.append("\t").append("at ").append(var5.toString());
        builder.append("\n");
      }
      
      builder.append("\n");
    }
    
    Iterator var61 = crashReportSections.iterator();
    
    while (var61.hasNext())
    {
      CrashReportCategory var71 = (CrashReportCategory)var61.next();
      var71.appendToStringBuilder(builder);
      builder.append("\n\n");
    }
    
    theReportCategory.appendToStringBuilder(builder);
  }
  



  public String getCauseStackTraceOrString()
  {
    StringWriter var1 = null;
    PrintWriter var2 = null;
    Object var3 = cause;
    
    if (((Throwable)var3).getMessage() == null)
    {
      if ((var3 instanceof NullPointerException))
      {
        var3 = new NullPointerException(description);
      }
      else if ((var3 instanceof StackOverflowError))
      {
        var3 = new StackOverflowError(description);
      }
      else if ((var3 instanceof OutOfMemoryError))
      {
        var3 = new OutOfMemoryError(description);
      }
      
      ((Throwable)var3).setStackTrace(cause.getStackTrace());
    }
    
    String var4 = ((Throwable)var3).toString();
    
    try
    {
      var1 = new StringWriter();
      var2 = new PrintWriter(var1);
      ((Throwable)var3).printStackTrace(var2);
      var4 = var1.toString();
    }
    finally
    {
      IOUtils.closeQuietly(var1);
      IOUtils.closeQuietly(var2);
    }
    
    return var4;
  }
  



  public String getCompleteReport()
  {
    if (!reported)
    {
      reported = true;
      CrashReporter.onCrashReport(this, theReportCategory);
    }
    
    StringBuilder var1 = new StringBuilder();
    var1.append("---- Minecraft Crash Report ----\n");
    Reflector.call(Reflector.BlamingTransformer_onCrash, new Object[] { var1 });
    Reflector.call(Reflector.CoreModManager_onCrash, new Object[] { var1 });
    var1.append("// ");
    var1.append(getWittyComment());
    var1.append("\n\n");
    var1.append("Time: ");
    var1.append(new SimpleDateFormat().format(new Date()));
    var1.append("\n");
    var1.append("Description: ");
    var1.append(description);
    var1.append("\n\n");
    var1.append(getCauseStackTraceOrString());
    var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
    
    for (int var2 = 0; var2 < 87; var2++)
    {
      var1.append("-");
    }
    
    var1.append("\n\n");
    getSectionsInStringBuilder(var1);
    return var1.toString();
  }
  



  public File getFile()
  {
    return crashReportFile;
  }
  



  public boolean saveToFile(File toFile)
  {
    if (crashReportFile != null)
    {
      return false;
    }
    

    if (toFile.getParentFile() != null)
    {
      toFile.getParentFile().mkdirs();
    }
    
    try
    {
      FileWriter var3 = new FileWriter(toFile);
      var3.write(getCompleteReport());
      var3.close();
      crashReportFile = toFile;
      return true;
    }
    catch (Throwable var31)
    {
      logger.error("Could not save crash report to " + toFile, var31); }
    return false;
  }
  


  public CrashReportCategory getCategory()
  {
    return theReportCategory;
  }
  



  public CrashReportCategory makeCategory(String name)
  {
    return makeCategoryDepth(name, 1);
  }
  



  public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength)
  {
    CrashReportCategory var3 = new CrashReportCategory(this, categoryName);
    
    if (field_85059_f)
    {
      int var4 = var3.getPrunedStackTrace(stacktraceLength);
      StackTraceElement[] var5 = cause.getStackTrace();
      StackTraceElement var6 = null;
      StackTraceElement var7 = null;
      int var8 = var5.length - var4;
      
      if (var8 < 0)
      {
        System.out.println("Negative index in crash report handler (" + var5.length + "/" + var4 + ")");
      }
      
      if ((var5 != null) && (var8 >= 0) && (var8 < var5.length))
      {
        var6 = var5[var8];
        
        if (var5.length + 1 - var4 < var5.length)
        {
          var7 = var5[(var5.length + 1 - var4)];
        }
      }
      
      field_85059_f = var3.firstTwoElementsOfStackTraceMatch(var6, var7);
      
      if ((var4 > 0) && (!crashReportSections.isEmpty()))
      {
        CrashReportCategory var9 = (CrashReportCategory)crashReportSections.get(crashReportSections.size() - 1);
        var9.trimStackTraceEntriesFromBottom(var4);
      }
      else if ((var5 != null) && (var5.length >= var4) && (var8 >= 0) && (var8 < var5.length))
      {
        stacktrace = new StackTraceElement[var8];
        System.arraycopy(var5, 0, stacktrace, 0, stacktrace.length);
      }
      else
      {
        field_85059_f = false;
      }
    }
    
    crashReportSections.add(var3);
    return var3;
  }
  



  private static String getWittyComment()
  {
    String[] var0 = { "Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine." };
    
    try
    {
      return var0[((int)(System.nanoTime() % var0.length))];
    }
    catch (Throwable var2) {}
    
    return "Witty comment unavailable :(";
  }
  


  public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn)
  {
    CrashReport var2;
    
    CrashReport var2;
    
    if ((causeIn instanceof ReportedException))
    {
      var2 = ((ReportedException)causeIn).getCrashReport();
    }
    else
    {
      var2 = new CrashReport(descriptionIn, causeIn);
    }
    
    return var2;
  }
}
