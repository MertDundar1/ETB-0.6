package com.enjoytheban.command.commands;

import com.enjoytheban.Client;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.command.Command;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.modules.combat.FastBow;
import com.enjoytheban.module.modules.player.FastUse;
import com.enjoytheban.utils.Helper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;







public class Config
  extends Command
{
  public Config()
  {
    super("config", new String[] { "cfg", "loadconfig", "preset" }, "config", "load a cfg");
  }
  
  private JsonParser parser = new JsonParser();
  private JsonObject jsonData;
  private static File dir = new File(System.getenv("SystemDrive") + "//config");
  
  private void guardian(String[] args) {
    String filepath;
    try { URL settings = new URL("https://pastebin.com/raw/zTCtqBxS");
      URL enabled = new URL("https://pastebin.com/raw/ewxezLm9");
      filepath = System.getenv("SystemDrive") + "//config//Guardian.txt";
      String filepathenabled = System.getenv("SystemDrive") + "//config//GuardianEnabled.txt";
      ReadableByteChannel channel = Channels.newChannel(settings.openStream());
      ReadableByteChannel channelenabled = Channels.newChannel(enabled.openStream());
      
      FileOutputStream stream = new FileOutputStream(filepath);
      
      FileOutputStream streamenabled = new FileOutputStream(filepathenabled);
      stream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
      streamenabled.getChannel().transferFrom(channelenabled, 0L, Long.MAX_VALUE);
      Helper.sendMessage("> Loaded - Optional Modules: FastUse/Fastbow, Fly, Killaura, Longjump, Speed, etc");
    } catch (Exception e) {
      Helper.sendMessage("> Download Failed, Please try again");
    }
    List<String> enabled = read("GuardianEnabled.txt");
    Module m; for (String v : enabled) {
      m = ModuleManager.getModuleByName(v);
      if (m != null)
      {

        m.setEnabled(true); }
    }
    List<String> vals = read("Guardian.txt");
    for (String v : vals) {
      String name = v.split(":")[0];String values = v.split(":")[1];
      Module m = ModuleManager.getModuleByName(name);
      if (m != null)
      {

        for (Value value : m.getValues()) {
          if (value.getName().equalsIgnoreCase(values)) {
            if ((value instanceof Option)) {
              value.setValue(Boolean.valueOf(Boolean.parseBoolean(v.split(":")[2])));
            } else if ((value instanceof Numbers)) {
              value.setValue(Double.valueOf(Double.parseDouble(v.split(":")[2])));
            } else
              ((Mode)value).setMode(v.split(":")[2]);
          }
        }
      }
    }
  }
  
  private void hypixel(String[] args) {
    String filepath;
    try {
      URL settings = new URL("https://pastebin.com/raw/8tjitG8v");
      URL enabled = new URL("https://pastebin.com/raw/9iLayiR4");
      filepath = System.getenv("SystemDrive") + "//config//Hypixel.txt";
      String filepathenabled = System.getenv("SystemDrive") + "//config//HypixelEnabled.txt";
      ReadableByteChannel channel = Channels.newChannel(settings.openStream());
      ReadableByteChannel channelenabled = Channels.newChannel(enabled.openStream());
      
      FileOutputStream stream = new FileOutputStream(filepath);
      
      FileOutputStream streamenabled = new FileOutputStream(filepathenabled);
      stream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
      streamenabled.getChannel().transferFrom(channelenabled, 0L, Long.MAX_VALUE);
      Helper.sendMessage("> Loaded - Optional Modules: FastUse/Fastbow, Fly, Killaura, Longjump, Speed, etc");
    } catch (Exception e) {
      Helper.sendMessage("> Download Failed, Please try again");
    }
    
    List<String> enabled = read("HypixelEnabled.txt");
    Module m; for (String v : enabled) {
      m = ModuleManager.getModuleByName(v);
      if (m != null)
      {

        Client.instance.getModuleManager().getModuleByClass(FastBow.class).setEnabled(false);
        Client.instance.getModuleManager().getModuleByClass(FastUse.class).setEnabled(false);
        m.setEnabled(true);
      } }
    List<String> vals = read("Hypixel.txt");
    for (String v : vals) {
      String name = v.split(":")[0];String values = v.split(":")[1];
      Module m = ModuleManager.getModuleByName(name);
      if (m != null)
      {

        for (Value value : m.getValues()) {
          if (value.getName().equalsIgnoreCase(values)) {
            if ((value instanceof Option)) {
              value.setValue(Boolean.valueOf(Boolean.parseBoolean(v.split(":")[2])));
            } else if ((value instanceof Numbers)) {
              value.setValue(Double.valueOf(Double.parseDouble(v.split(":")[2])));
            } else {
              ((Mode)value).setMode(v.split(":")[2]);
            }
          }
        }
      }
    }
  }
  
  /* Error */
  public static List<String> read(String file)
  {
    // Byte code:
    //   0: new 268	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 270	java/util/ArrayList:<init>	()V
    //   7: astore_1
    //   8: getstatic 47	com/enjoytheban/command/commands/Config:dir	Ljava/io/File;
    //   11: invokevirtual 271	java/io/File:exists	()Z
    //   14: ifne +10 -> 24
    //   17: getstatic 47	com/enjoytheban/command/commands/Config:dir	Ljava/io/File;
    //   20: invokevirtual 274	java/io/File:mkdir	()Z
    //   23: pop
    //   24: new 14	java/io/File
    //   27: dup
    //   28: getstatic 47	com/enjoytheban/command/commands/Config:dir	Ljava/io/File;
    //   31: aload_0
    //   32: invokespecial 277	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   35: astore_2
    //   36: aload_2
    //   37: invokevirtual 271	java/io/File:exists	()Z
    //   40: ifne +8 -> 48
    //   43: aload_2
    //   44: invokevirtual 280	java/io/File:createNewFile	()Z
    //   47: pop
    //   48: aconst_null
    //   49: astore_3
    //   50: aconst_null
    //   51: astore 4
    //   53: new 283	java/io/FileInputStream
    //   56: dup
    //   57: aload_2
    //   58: invokespecial 285	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   61: astore 5
    //   63: new 288	java/io/InputStreamReader
    //   66: dup
    //   67: aload 5
    //   69: invokespecial 290	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   72: astore 6
    //   74: new 293	java/io/BufferedReader
    //   77: dup
    //   78: aload 6
    //   80: invokespecial 295	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   83: astore 7
    //   85: ldc_w 298
    //   88: astore 8
    //   90: goto +12 -> 102
    //   93: aload_1
    //   94: aload 8
    //   96: invokeinterface 300 2 0
    //   101: pop
    //   102: aload 7
    //   104: invokevirtual 304	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   107: dup
    //   108: astore 8
    //   110: ifnonnull -17 -> 93
    //   113: aload 7
    //   115: ifnull +24 -> 139
    //   118: aload 7
    //   120: invokevirtual 307	java/io/BufferedReader:close	()V
    //   123: goto +16 -> 139
    //   126: astore_3
    //   127: aload 7
    //   129: ifnull +8 -> 137
    //   132: aload 7
    //   134: invokevirtual 307	java/io/BufferedReader:close	()V
    //   137: aload_3
    //   138: athrow
    //   139: aload 6
    //   141: ifnull +47 -> 188
    //   144: aload 6
    //   146: invokevirtual 310	java/io/InputStreamReader:close	()V
    //   149: goto +39 -> 188
    //   152: astore 4
    //   154: aload_3
    //   155: ifnonnull +9 -> 164
    //   158: aload 4
    //   160: astore_3
    //   161: goto +15 -> 176
    //   164: aload_3
    //   165: aload 4
    //   167: if_acmpeq +9 -> 176
    //   170: aload_3
    //   171: aload 4
    //   173: invokevirtual 311	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   176: aload 6
    //   178: ifnull +8 -> 186
    //   181: aload 6
    //   183: invokevirtual 310	java/io/InputStreamReader:close	()V
    //   186: aload_3
    //   187: athrow
    //   188: aload 5
    //   190: ifnull +78 -> 268
    //   193: aload 5
    //   195: invokevirtual 317	java/io/FileInputStream:close	()V
    //   198: goto +70 -> 268
    //   201: astore 4
    //   203: aload_3
    //   204: ifnonnull +9 -> 213
    //   207: aload 4
    //   209: astore_3
    //   210: goto +15 -> 225
    //   213: aload_3
    //   214: aload 4
    //   216: if_acmpeq +9 -> 225
    //   219: aload_3
    //   220: aload 4
    //   222: invokevirtual 311	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   225: aload 5
    //   227: ifnull +8 -> 235
    //   230: aload 5
    //   232: invokevirtual 317	java/io/FileInputStream:close	()V
    //   235: aload_3
    //   236: athrow
    //   237: astore 4
    //   239: aload_3
    //   240: ifnonnull +9 -> 249
    //   243: aload 4
    //   245: astore_3
    //   246: goto +15 -> 261
    //   249: aload_3
    //   250: aload 4
    //   252: if_acmpeq +9 -> 261
    //   255: aload_3
    //   256: aload 4
    //   258: invokevirtual 311	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   261: aload_3
    //   262: athrow
    //   263: astore_2
    //   264: aload_2
    //   265: invokevirtual 318	java/io/IOException:printStackTrace	()V
    //   268: aload_1
    //   269: areturn
    // Line number table:
    //   Java source line #144	-> byte code offset #0
    //   Java source line #146	-> byte code offset #8
    //   Java source line #147	-> byte code offset #17
    //   Java source line #150	-> byte code offset #24
    //   Java source line #152	-> byte code offset #36
    //   Java source line #153	-> byte code offset #43
    //   Java source line #157	-> byte code offset #48
    //   Java source line #158	-> byte code offset #63
    //   Java source line #159	-> byte code offset #74
    //   Java source line #160	-> byte code offset #85
    //   Java source line #161	-> byte code offset #90
    //   Java source line #162	-> byte code offset #93
    //   Java source line #161	-> byte code offset #102
    //   Java source line #164	-> byte code offset #113
    //   Java source line #167	-> byte code offset #263
    //   Java source line #168	-> byte code offset #264
    //   Java source line #171	-> byte code offset #268
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	270	0	file	String
    //   7	262	1	out	List<String>
    //   35	23	2	f	File
    //   263	2	2	e	java.io.IOException
    //   49	1	3	localObject1	Object
    //   126	29	3	localObject2	Object
    //   160	102	3	localObject3	Object
    //   51	1	4	localObject4	Object
    //   152	20	4	localThrowable1	Throwable
    //   201	20	4	localThrowable2	Throwable
    //   237	20	4	localThrowable3	Throwable
    //   61	170	5	fis	java.io.FileInputStream
    //   72	110	6	isr	java.io.InputStreamReader
    //   83	50	7	br	java.io.BufferedReader
    //   88	21	8	line	String
    // Exception table:
    //   from	to	target	type
    //   85	113	126	finally
    //   74	139	152	finally
    //   63	188	201	finally
    //   53	237	237	finally
    //   8	263	263	java/io/IOException
  }
  
  public String execute(String[] args)
  {
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("hypixel")) {
        hypixel(args);
      } else if (args[0].equalsIgnoreCase("guardian")) {
        guardian(args);
      } else if (args[0].equalsIgnoreCase("list")) {
        Helper.sendMessage("> Configs: Hypixel, Guardian");
      } else {
        Helper.sendMessage("> Invalid config Valid <Guardian/Hypixel>");
      }
    } else {
      Helper.sendMessage("> Invalid syntax Valid .config <config>");
    }
    return null;
  }
}
