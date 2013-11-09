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

package cleo.search.tool;

import java.io.PrintStream;

import cleo.search.store.KratiArrayStore;


import krati.core.segment.MappedSegmentFactory;

/**
 * KratiArrayStoreIntsDiff
 * 
 * @author jwu
 * @since 12/21, 2010
 */
public class KratiArrayStoreIntsDiff {
  private final KratiArrayStore arrayStore1;
  private final KratiArrayStore arrayStore2;
  
  public KratiArrayStoreIntsDiff(int length,
                                 int batchSize,
                                 int numSyncBatches,
                                 String store1HomeDirectory,
                                 String store2HomeDirectory)
  throws Exception {
    arrayStore1 =
      new KratiArrayStore(length, batchSize, numSyncBatches, store1HomeDirectory, new MappedSegmentFactory());
    arrayStore2 =
      new KratiArrayStore(length, batchSize, numSyncBatches, store2HomeDirectory, new MappedSegmentFactory());
  }
  
  public void report(PrintStream out, int lengthDiffThreshold) {
    int cnt = Math.min(arrayStore1.length(), arrayStore2.length());
    
    for(int i = 0; i < cnt; i++) {
      int len1 = arrayStore1.getLength(i);
      int len2 = arrayStore2.getLength(i);
      int diff = Math.abs(len1 -len2);
      if(diff > lengthDiffThreshold) {
        out.printf("%10d %10d %10d %10d%n", i, len1, len2, diff);
      }
    }
    
    out.flush();
  }
  
  /**
   * Diff two KratiArrayStoreInts.
   * 
   * <pre>
   * java com.linkedin.companysearch.tools.KratiArrayStoreIntsScan <StoreHomeDir-1> <StoreHomeDir-2>
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    String store1HomeDir = args[0];
    String store2HomeDir = args[1];
    
    KratiArrayStoreIntsDiff diff = new KratiArrayStoreIntsDiff(1000000, 100, 5, store1HomeDir, store2HomeDir);
    diff.report(System.out, 10);
    System.exit(0);
  }
}
