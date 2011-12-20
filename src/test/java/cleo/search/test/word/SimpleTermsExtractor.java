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

package cleo.search.test.word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import cleo.search.util.TermsExtractor;

/**
 * SimpleTermsExtractor
 * 
 * @author jwu
 * @since 01/24, 2011
 */
public class SimpleTermsExtractor implements TermsExtractor {
  private final StopWords stopWords;
  
  public SimpleTermsExtractor(StopWords stopWords) {
    this.stopWords = stopWords;
  }
  
  private String concatenate(String[] words) {
    StringBuilder sb = new StringBuilder();
    
    for(String s : words) {
      sb.append(s).append(' ');
    }
    
    if(sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    
    return sb.toString();
  }
  
  private String simplify(String line) {
    // Use lower case
    line = line.trim().toLowerCase();
    
    // Filter stop words (1st time)
    String[] words = getStopWords().filter(line.split("\\s+"));
    line = concatenate(words);
    
    // Remove non-word-char
    line = line.replaceAll("\\W+", " ");
    
    // Filter stop words (2nd time)
    words = getStopWords().filter(line.split("\\s+"));
    line = concatenate(words);
    
    // Remove single char
    line = line.replaceAll("\\s+.\\s+", " ");
    line = line.replaceAll("^.\\s+", "").replaceAll("\\s+.$", "");
    
    return line;
  }
  
  @Override
  public String[] extract(String line) {
    return (line == null) ? null : simplify(line).split("\\s+");
  }
  
  public StopWords getStopWords() {
    return stopWords;
  }
  
  public void extract(File sourceFile, PrintStream ps) throws IOException {
    final BufferedReader r = new BufferedReader(new FileReader(sourceFile.getAbsolutePath()));
    
    String line = null;
    while((line = r.readLine()) != null) {
      line = line.trim().replaceAll("\\s+", " ");
      int ind = line.indexOf(' ');
      if(ind > 0) {
        try {
          int source = Integer.parseInt(line.substring(0, ind));
          String rest = line.substring(ind + 1).trim();
          rest = simplify(rest).replaceAll("\\s+", "|");
          ps.printf("%d %s%n", source, rest);
        } catch(Exception e) {
          System.err.println(line);
          e.printStackTrace();
        }
      }
    }
    
    r.close();
  }
  
  public void extract(File sourceFile, File targetFile) throws IOException {
    final PrintStream ps = new PrintStream(targetFile);
    extract(sourceFile, ps);
    ps.close();
  }
  
  /**
   * <pre>
   *   java -server -Xms64M -Xmx1G sourceFile targetFile
   * </pre>
   * 
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    SimpleTermsExtractor extractor = new SimpleTermsExtractor(new StopWordsEn());
    extractor.extract(new File(args[0]), new File(args[1]));
  }
}
