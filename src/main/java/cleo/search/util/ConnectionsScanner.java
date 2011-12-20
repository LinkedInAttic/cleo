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
import java.util.ArrayList;


import krati.util.Chronos;

/**
 * MemberConnectionsScanner
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class ConnectionsScanner {
  private File connsHomeDir;
  
  public ConnectionsScanner(File connsHomeDir) {
    this.connsHomeDir = connsHomeDir;
  }
  
  public void scan(ConnectionsHandler handler) {
    Chronos c = new Chronos();
    
    for(File f : connsHomeDir.listFiles()) {
      if(f.isFile()) {
        try {
          scan(f, handler);
          System.out.printf("%s scanned in %d ms%n", f.getAbsolutePath(), c.tick());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void scan(File file, ConnectionsHandler handler) throws Exception {
    final BufferedReader r = new BufferedReader(new FileReader(file.getAbsolutePath()));
    final ArrayList<String> numList = new ArrayList<String>();
    
    String line = null;
    while((line = r.readLine()) != null) {
      numList.clear();
      line = line.trim();
      String[] parts = line.split(",|\\s|\\{|\\}|\\(|\\)|\\|");
      if(parts.length > 0) {
        int source = Integer.parseInt(parts[0]);
        for(int i = 1; i < parts.length; i++) {
          parts[i] = parts[i].trim();
          if(parts[i].length() > 0 && !parts[i].equals("-")) {
            numList.add(parts[i]);
          }
        }
        
        int[] connections = new int[numList.size()];
        for(int i = 0; i < connections.length; i++) {
          connections[i] = Integer.parseInt(numList.get(i));
        }
        
        if(handler != null) {
          handler.handle(source, connections);
        } else {
          StringBuilder sb = new StringBuilder();
          sb.append(source).append(' ');
          for(int i = 0; i < connections.length; i++) {
            sb.append(connections[i]).append('|');
          }
          sb.deleteCharAt(sb.length() - 1);
          System.out.println(sb);
        }
      }
    }
  }
  
  /**
   * <pre>
   *   java -server -Xms256M -Xmx2G connectionsDir
   *   java -server -Xms256M -Xmx2G bootstrap/connections/i001
   * </pre>
   * 
   * @param args
   */
  public static void main(String[] args) {
    ConnectionsScanner scanner = new ConnectionsScanner(new File(args[0]));
    scanner.scan(null);
  }
}
