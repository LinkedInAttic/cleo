/*
 * Copyright (c) 2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
