import com.enjoytheban.Client;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class InjectionAPI
{
  public InjectionAPI() {}
  
  public static void inject() throws Exception
  {
    String userHome = System.getProperty("user.home", ".");
    File workingDirectory;
    File workingDirectory; File workingDirectory; File workingDirectory; switch (getPlatform()) {
    case LINUX: 
      workingDirectory = new File(userHome, ".minecraft/");
      break;
    case SOLARIS: 
      String applicationData = System.getenv("APPDATA");
      String folder = applicationData != null ? applicationData : userHome;
      workingDirectory = new File(folder, ".minecraft/");
      break;
    case UNKNOWN: 
      workingDirectory = new File(userHome, "Library/Application Support/minecraft");
      break;
    case MACOS: default: 
      workingDirectory = new File(userHome, "minecraft/");
    }
    try {
      Client.instance.getClass();{ "--version" }[1] = "ETB"; String[] tmp139_127 = tmp127_122;tmp139_127[2] = 
        "--accessToken"; String[] tmp144_139 = tmp139_127;tmp144_139[3] = "0"; String[] tmp149_144 = tmp144_139;tmp149_144[4] = 
        "--assetIndex"; String[] tmp154_149 = tmp149_144;tmp154_149[5] = "1.8"; String[] tmp159_154 = tmp154_149;tmp159_154[6] = 
        "--userProperties"; String[] tmp165_159 = tmp159_154;tmp165_159[7] = "{}"; String[] tmp171_165 = tmp165_159;tmp171_165[8] = 
        "--gameDir"; String[] tmp177_171 = tmp171_165;tmp177_171[9] = new File(workingDirectory, ".").getAbsolutePath(); String[] tmp194_177 = tmp177_171;tmp194_177[10] = 
        "--assetsDir"; String[] tmp200_194 = tmp194_177;tmp200_194[11] = new File(workingDirectory, "assets/").getAbsolutePath();net.minecraft.client.main.Main.main(tmp200_194);
    }
    catch (Exception e1) {
      try {
        PrintWriter writer = new PrintWriter("C:\\AntiLeak\\error.txt", "UTF-8");
        writer.println(e1);
        writer.close();
        Thread.sleep(10000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }
  
  public static InjectionAPI.OS getPlatform() {
    String s = System.getProperty("os.name").toLowerCase();
    return s.contains("unix") ? InjectionAPI.OS.LINUX : s.contains("linux") ? InjectionAPI.OS.LINUX : s.contains("sunos") ? InjectionAPI.OS.SOLARIS : s.contains("solaris") ? InjectionAPI.OS.SOLARIS : s.contains("mac") ? InjectionAPI.OS.MACOS : s.contains("win") ? InjectionAPI.OS.WINDOWS : InjectionAPI.OS.UNKNOWN;
  }
  
  public static enum OS {
    LINUX, 
    SOLARIS, 
    WINDOWS, 
    MACOS, 
    UNKNOWN;
  }
}
