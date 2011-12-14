package cleo.search.tool;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import cleo.search.store.KratiArrayStore;


import krati.core.segment.MappedSegmentFactory;

/**
 * KratiArrayStoreIntsScan
 * 
 * @author jwu
 * @since 12/20, 2010
 */
public class KratiArrayStoreIntsScan {
  private final KratiArrayStore arrayStore;
  
  public KratiArrayStoreIntsScan(int length,
                                 int batchSize,
                                 int numSyncBatches,
                                 String homeDirectoryStr) throws Exception {
    arrayStore =
      new KratiArrayStore(length, batchSize, numSyncBatches, homeDirectoryStr, new MappedSegmentFactory());
  }
  
  /**
   * Dumps full information about an element (i.e integer set) at an index.
   * 
   * @param out      - Stream to print to
   * @param index    - Element (i.e integer set) index
   */
  public void dumpFull(PrintStream out, int index) {
    byte[] dat = arrayStore.get(index);
    String addrInfo = arrayStore.address(index);
    
    if(dat == null) {
      out.printf("[" + index + "] %s null", addrInfo);
      return;
    }
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    bb.position(0);
    
    out.printf("[%d] %s length %d vs %d%n", index, addrInfo, arrayStore.getLength(index), dat.length);
    
    out.printf(" %9d", dat.length);
    while (bb.position() + 4 <= dat.length) {
      int n = bb.getInt();
      
      out.printf(" %9d", n);
      if(bb.position() % 64 == 60) {
        out.println();
      }
    }
    if(bb.position() < bb.limit()) {
      out.print("...");
    }
    out.println();
    out.flush();
  }
  
  /**
   * Dumps partial information about an element (i.e integer set) at an index.
   * 
   * @param out      - Stream to print to
   * @param index    - Element (i.e integer set) index
   * @param elemInts - Integers which may possibly be in the integer set.
   */
  public void dump(PrintStream out, int index, int... elemInts) {
    byte[] dat = arrayStore.get(index);
    String addrInfo = arrayStore.address(index);
    
    if(dat == null) {
      out.printf("[" + index + "] %s null%n", addrInfo);
      return;
    }
    
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    for(int n : elemInts) {
      map.put(n, 0);
    }
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    bb.position(0);
    
    out.printf("[%d] %s length %d vs %d%n", index, addrInfo, arrayStore.getLength(index), dat.length);
    
    out.printf(" %9d", dat.length);
    while (bb.position() + 4 <= dat.length) {
      int n = bb.getInt();
      if(bb.position() < 256) {
        out.printf(" %9d", n);
        if(bb.position() % 64 == 60) {
          out.println();
        }
      }
      
      Integer cnt = map.get(n);
      if(cnt != null) {
        map.put(n, ++cnt);
      }
    }
    if(bb.position() > 256) {
      out.print("...");
    }
    out.println();
    
    for(int n : elemInts) {
      out.printf("%9d  %4d%n", n, map.get(n));
    }
    
    out.flush();
  }

  /**
   * Dumps the entire array store in the form below:
   * <pre>
   *    Index  Length
   *    Index  LengthInAddress != LengthInSegment
   * </pre>
   * 
   * @param out - Stream to print to
   */
  public void dump(PrintStream out) {
    for(int i = 0, cnt = arrayStore.length(); i < cnt; i++) {
      byte[] dat = arrayStore.get(i);
      
      int datLen1 = arrayStore.getLength(i);
      int datLen2 = (dat == null) ? -1 : dat.length;
      
      if(datLen1 != datLen2) {
        out.printf("%10d %10d != %10d%n", i, datLen1, datLen2);
      } else {
        out.printf("%10d %10d%n", i, datLen1);
      }
    }
    
    out.flush();
  }
  
  /**
   * Scan the entire array store to report any element with wrong data lengths.
   * 
   * @param out - Stream to print to
   */
  public void scan(PrintStream out) {
    int nullCnt = 0;
    int zeroCnt = 0;
    int intCnt = 0;
    int errCnt = 0;
    
    int nextAvailable = 0;
    
    out.println("----- Scan Results -----");
    
    for(int i = 0, cnt = arrayStore.length(); i < cnt; i++) {
      boolean errDump = false;
      byte[] dat = arrayStore.get(i);
      
      int datLen1 = arrayStore.getLength(i);
      int datLen2 = (dat == null) ? -1 : dat.length;
      if(datLen1 != datLen2) {
        out.printf("[%d].length %d != %d%n", i, datLen1, datLen2);
        errDump = true;
      }
      
      if(dat == null) {
        if(nextAvailable == 0) nextAvailable = i;
        nullCnt++;
      } else {
        nextAvailable = 0;
        
        if (dat.length == 0) {
          zeroCnt++;
        } else if (dat.length % 4 == 0) {
          intCnt++;
        } else {
          errDump = true;
          errCnt++;
        }
      }
      
      if(errDump) {
        dump(out, i);
        out.println();
      }
    }
    
    out.printf("nullCnt=%d zeroCnt=%d intCnt=%d errCnt=%d nextAvailable=%d%n", nullCnt, zeroCnt, intCnt, errCnt, nextAvailable);
    out.flush();
    
    out.println("----- Water Marks -----");
    out.printf("lwm=%d hwm=%d%n", arrayStore.getLWMark(), arrayStore.getHWMark());
    out.flush();
  }
  
  /**
   * Scan an ArrayStoreIntSet.
   * 
   * <pre>
   * java com.linkedin.companysearch.tools.KratiArrayStoreIntSetScanner <StoreHomeDir>
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    String storeHomeDir = args[0];
    PrintStream out = new PrintStream(new java.io.FileOutputStream(args[0]+".out"));
    KratiArrayStoreIntsScan s = new KratiArrayStoreIntsScan(1000000, 100, 5, storeHomeDir);
    
    /*
    s.dump(out, 1009);
    s.dump(out, 1028);
    s.dump(out, 1025);
    s.dump(out, 1208);
    s.dump(out, 1315);
    s.dump(out, 1694);
    s.dump(out, 425831);
    s.dump(out, 1599414);
    s.dump(out, 1618930);
    s.dump(out, 1618931);
    s.dump(out, 1618944);
    s.dump(out, 1618946, 97552413, 31749798);
    s.dump(out, 1618952, 97522425, 70257273);
    s.dump(out, 1618953);
    s.dump(out, 1618958);
    s.dump(out, 1618959);
    s.dump(out, 1618960);
    */
    
    s.scan(out);
    
    s.dump(out);
    
    System.exit(0);
  }  
}
