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
