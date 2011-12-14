package cleo.search.test.util;

import java.io.File;
import java.io.IOException;

/**
 * FileUtils
 * 
 * @author jwu
 * @since 01/15, 2011
 */
public class FileUtils {
  
  public static File getTestDir(String testName) {
    File dir = new File(System.getProperty("cleo.test.output.dir"), testName);
    if(!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }
  
  public static void cleanDirectory(File dir) throws IOException {
    File[] files = dir.listFiles();
    
    for (File f : files) {
      if (f.isFile()) {
        boolean deleted = f.delete();
        if (!deleted) {
          throw new IOException("file:"+f.getAbsolutePath()+" not deleted");
        }
      } else {
        deleteDirectory(f);
      }
    }
  }
  
  public static void deleteDirectory(File dir) throws IOException {
    File[] files = dir.listFiles();
    
    for (File f : files) {
      if (f.isDirectory()) {
        deleteDirectory(f);
      } else {
        boolean deleted = f.delete();
        if (!deleted) {
          throw new IOException("file:"+f.getAbsolutePath()+" not deleted");
        }
      }
    }
    
    boolean deleted = dir.delete();
    if (!deleted) {
      throw new IOException("dir:"+dir.getAbsolutePath()+" not deleted");
    }
  }
}
