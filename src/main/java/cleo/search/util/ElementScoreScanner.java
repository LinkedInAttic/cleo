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

package cleo.search.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


import krati.util.Chronos;

/**
 * ScoreScanner
 * 
 * @author jwu
 * @since 02/17, 2011
 */
public class ElementScoreScanner implements ScoreScanner {
  private File scoreDir;
  
  public ElementScoreScanner(File scoreDir) {
    this.scoreDir = scoreDir;
  }
  
  @Override
  public void scan(ScoreHandler handler) {
    Chronos c = new Chronos();
    if(scoreDir == null || !scoreDir.exists()) return;
      
    if(scoreDir.isDirectory()) {
      for(File f : scoreDir.listFiles()) {
        if(f.isFile()) {
          try {
            scan(f, handler);
            System.out.printf("%s scanned in %d ms%n", f.getAbsolutePath(), c.tick());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } else if(scoreDir.isFile()) {
      try {
        scan(scoreDir, handler);
        System.out.printf("%s scanned in %d ms%n", scoreDir.getAbsolutePath(), c.tick());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  private void scan(File file, ScoreHandler handler) throws Exception {
    final BufferedReader r = new BufferedReader(new FileReader(file.getAbsolutePath()));
    
    String line = null;
    while((line = r.readLine()) != null) {
      String[] parts = line.trim().split("\\s+");
      
      if(parts.length == 2) {
        try {
          int elementId = Integer.parseInt(parts[0]);
          double elementScore = Double.parseDouble(parts[1]);
          if(handler != null) {
            handler.handle(elementId, elementScore);
          } else {
            System.out.printf("%10d %16.8f%n", elementId, elementScore);
          }
        } catch(Exception e) {
          System.err.println(line);
          e.printStackTrace();
        }
      }
    }
  }
  
  public static void main(String[] args) {
    ElementScoreScanner scan = new ElementScoreScanner(new File(args[0]));
    scan.scan(null);
  }
}
