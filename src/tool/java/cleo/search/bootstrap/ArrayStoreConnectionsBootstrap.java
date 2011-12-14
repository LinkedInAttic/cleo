package cleo.search.bootstrap;

import java.io.File;

import cleo.search.store.ArrayStoreConnections;
import cleo.search.store.StoreFactory;
import cleo.search.util.ConnectionsHandler;
import cleo.search.util.ConnectionsScanner;

import krati.util.Chronos;

/**
 * ArrayStoreConnectionsBootstrap
 * 
 * @author jwu
 * @since 02/07, 2011
 */
public class ArrayStoreConnectionsBootstrap implements ConnectionsHandler {
  private volatile long counter = 0;
  private final long monitorBatch = 1000000;
  private final Chronos clock = new Chronos();
  
  protected final ArrayStoreConnections connectionsStore;
  
  protected final int sourceIdCount;
  protected final int sourceIdStart;
  protected final int sourceIdEnd;
  
  protected final int targetIdCount;
  protected final int targetIdStart;
  protected final int targetIdEnd;
  
  public ArrayStoreConnectionsBootstrap(ArrayStoreConnections connectionsStore,
                                        int connSourceIdStart, int connSourceIdCount,
                                        int connTargetIdStart, int connTargetIdCount) {
    this.connectionsStore = connectionsStore;
    
    this.sourceIdStart = connSourceIdStart;
    this.sourceIdCount = connSourceIdCount;
    this.sourceIdEnd = connSourceIdStart + connSourceIdCount;
    
    this.targetIdStart = connTargetIdStart;
    this.targetIdCount = connTargetIdCount;
    this.targetIdEnd = connTargetIdStart + connTargetIdCount;
  }
  
  public final int getSourceIdCount() {
    return sourceIdCount;
  }
  
  public final int getSourceIdStart() {
    return sourceIdStart;
  }
  
  public final int getSourceIdEnd() {
    return sourceIdEnd;
  }
  
  public int getTargetIdCount() {
    return targetIdCount;
  }
  
  public int getTargetIdStart() {
    return targetIdStart;
  }
  
  public int getTargetIdEnd() {
    return targetIdEnd;
  }
  
  public boolean acceptSourceId(int source) {
    if(sourceIdStart <= source && source < sourceIdEnd) {
      return true;
    }
    return false;
  }
  
  public boolean acceptTargetId(int target) {
    if(targetIdStart <= target && target < targetIdEnd) {
      return true;
    }
    return false;
  }
  
  public boolean acceptConnection(int source, int target) {
    if((sourceIdStart <= source && source < sourceIdEnd) &&
       (targetIdStart <= target && target < targetIdEnd)) {
      return true;
    }
    return false;
  }
  
  @Override
  public int[] handle(int source, int[] connections) throws Exception {
    counter++;
    
    int[] targets = connections;
    
    if(acceptSourceId(source)) {
      int cnt = 0;
      for(int i = 0; i < connections.length; i++) {
        if(acceptConnection(source, connections[i])) {
          cnt++;
        }
      }
      
      if(cnt < connections.length) {
        int ind = 0;
        targets = new int[cnt];
        for(int i = 0; i < connections.length; i++) {
          if(acceptConnection(source, connections[i])) {        
            targets[ind] = connections[i];
            ind++;
          }
        }
      }
      
      connectionsStore.setConnections(source, targets, System.currentTimeMillis());
    }
    
    if(counter % monitorBatch == 0) {
      System.out.printf("processed %d in %d ms%n", monitorBatch, clock.tick());
    }
    
    return targets;
  }
  
  /**
   *  <pre>
   * java ArrayStoreConnectionsBootstrap -server -Xms4G -Xmx16G \
   *      connectionsStorePath connectionsStoreSegmentFileSizeMB \
   *      connectionsDir
   *      connectionSourceIdStart connectionSourceIdCount
   *      connectionTargetIdStart connectionTargetIdCount
   *      
   * java ArrayStoreConnectionsBootstrap -server -Xms4G -Xmx16G \
   *      bootstrap/i001/member/typeahead/connections-store 32 \
   *      bootstrap/i001/member/connections 0 200000000 0 5000000
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    File connectionsStoreDir = new File(args[0]);
    int connectionsStoreSegMB = Integer.parseInt(args[1]);    
    
    File connectionsDir = new File(args[2]);
    
    int connSourceIdStart = 0;
    int connSourceIdCount = 5000000;
    try {
      connSourceIdStart = Integer.parseInt(args[3]);
      connSourceIdCount = Integer.parseInt(args[4]);
    } catch(Exception e) {}
    
    int connTargetIdStart = 0;
    int connTargetIdCount = 5000000;
    try {
      connTargetIdStart = Integer.parseInt(args[5]);
      connTargetIdCount = Integer.parseInt(args[6]);
    } catch(Exception e) {}
    
    Chronos c = new Chronos();
    
    // Create connectionsStore
    int capacity = connSourceIdCount;
    ArrayStoreConnections connectionsStore =
      StoreFactory.createArrayStoreConnections(connectionsStoreDir, capacity, connectionsStoreSegMB);
    
    // Bootstrap connectionsStore
    ArrayStoreConnectionsBootstrap bootstrap =
      new ArrayStoreConnectionsBootstrap(connectionsStore,
                                         connSourceIdStart,
                                         connSourceIdCount,
                                         connTargetIdStart,
                                         connTargetIdCount);
    
    ConnectionsScanner scanner = new ConnectionsScanner(connectionsDir);
    scanner.scan(bootstrap);
    connectionsStore.sync();
    
    System.out.printf("Bootstrap done in %d ms%n", c.tick());
  }
}
