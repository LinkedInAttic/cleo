package cleo.search.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


import krati.util.Chronos;

/**
 * TermsScanner
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class TermsScanner {
  private File termsDir;
  
  public TermsScanner(File termsDir) {
    this.termsDir = termsDir;
  }
  
  public void scan(TermsHandler handler) {
    Chronos c = new Chronos();
    
    if(termsDir.exists()) {
      if(termsDir.isDirectory()) {
        for(File f : termsDir.listFiles()) {
          if(f.isFile()) {
            try {
              scan(f, handler);
              System.out.printf("%s scanned in %d ms%n", f.getAbsolutePath(), c.tick());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      } else if(termsDir.isFile()) {
        File f = termsDir;
        try {
          scan(f, handler);
          System.out.printf("%s scanned in %d ms%n", f.getAbsolutePath(), c.tick());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {
      System.err.printf("%s not found!%n", termsDir.getAbsolutePath());
    }
  }
  
  private void scan(File file, TermsHandler handler) throws Exception {
    final BufferedReader r = new BufferedReader(new FileReader(file.getAbsolutePath()));
    final ArrayList<String> termList = new ArrayList<String>();
    
    String line = null;
    while((line = r.readLine()) != null) {
      termList.clear();
      line = line.trim();
      String[] parts = line.split(",|\\s+|\\{|\\}|\\(|\\)|\\|");
      
      if(parts.length > 0) {
        try {
          int source = Integer.parseInt(parts[0]);
          for(int i = 1; i < parts.length; i++) {
            parts[i] = parts[i].trim().toLowerCase();
            if(parts[i].length() > 0 && !parts[i].equals("-")) {
              termList.add(parts[i]);
            }
          }
          
          String[] terms = new String[termList.size()];
          termList.toArray(terms);
          
          if(handler != null) {
            handler.handle(source, terms);
          } else {
            StringBuilder sb = new StringBuilder();
            sb.append(source).append(' ');
            for(int i = 0; i < terms.length; i++) {
              sb.append(terms[i]).append('|');
            }
            sb.deleteCharAt(sb.length() - 1);
            System.out.println(sb);
          }
        } catch(Exception e) {
          System.err.println(line);
          e.printStackTrace();
        }
      }
    }
  }
  
  /**
   * <pre>
   *   java -server -Xms256M -Xmx2G termsDir
   *   java -server -Xms256M -Xmx2G bootstrap/terms/i001
   * </pre>
   * 
   * @param args
   */
  public static void main(String[] args) {
    TermsScanner scanner = new TermsScanner(new File(args[0]));
    scanner.scan(null);
  }
}
