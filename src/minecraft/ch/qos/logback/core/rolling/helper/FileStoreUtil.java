package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import java.io.File;
import java.lang.reflect.Method;

























public class FileStoreUtil
{
  static final String PATH_CLASS_STR = "java.nio.file.Path";
  static final String FILES_CLASS_STR = "java.nio.file.Files";
  
  public FileStoreUtil() {}
  
  public static boolean areOnSameFileStore(File a, File b)
    throws RolloverFailure
  {
    if (!a.exists()) {
      throw new IllegalArgumentException("File [" + a + "] does not exist.");
    }
    if (!b.exists()) {
      throw new IllegalArgumentException("File [" + b + "] does not exist.");
    }
    








    try
    {
      Class<?> pathClass = Class.forName("java.nio.file.Path");
      Class<?> filesClass = Class.forName("java.nio.file.Files");
      
      Method toPath = File.class.getMethod("toPath", new Class[0]);
      Method getFileStoreMethod = filesClass.getMethod("getFileStore", new Class[] { pathClass });
      

      Object pathA = toPath.invoke(a, new Object[0]);
      Object pathB = toPath.invoke(b, new Object[0]);
      
      Object fileStoreA = getFileStoreMethod.invoke(null, new Object[] { pathA });
      Object fileStoreB = getFileStoreMethod.invoke(null, new Object[] { pathB });
      return fileStoreA.equals(fileStoreB);
    } catch (Exception e) {
      throw new RolloverFailure("Failed to check file store equality for [" + a + "] and [" + b + "]", e);
    }
  }
}
