package cleo.search.store;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * MemoryConnectionsStore
 * 
 * @author jwu
 * @since 02/12, 2011
 */
public class MemoryConnectionsStore<S> implements ConnectionsStore<S> {
  private final ConnectionsStore<S> pcs;
  private final ConcurrentHashMap<S, int[]> map;
  
  private final static Logger logger = Logger.getLogger(MemoryConnectionsStore.class);
  
  public MemoryConnectionsStore(int initialCapacity, ConnectionsStore<S> persistConnectionsStore) {
    this.pcs = persistConnectionsStore;
    this.map = new ConcurrentHashMap<S, int[]>(initialCapacity);
    this.init();
  }
  
  protected void init() {
    long startTime = System.currentTimeMillis();
    
    Iterator<S> itr = pcs.sourceIterator();
    while(itr.hasNext()) {
      S source = itr.next();
      int[] connections = pcs.getConnections(source);
      map.put(source, connections);
    }
    
    long totalTime = System.currentTimeMillis() - startTime;
    logger.info("init: " + totalTime + " ms");
  }
  
  @Override
  public int[] getConnections(S source) {
    return map.get(source);
  }
  
  @Override
  public synchronized void putConnections(S source, int[] connections, long scn) throws Exception {
    pcs.putConnections(source, connections, scn);
    if(connections != null) {
      map.put(source, connections);
    } else {
      map.remove(source);
    }
  }
  
  @Override
  public synchronized void addConnection(S source, int connection, long scn) throws Exception {
    pcs.addConnection(source, connection, scn);
    map.put(source, pcs.getConnections(source));
  }
  
  @Override
  public synchronized void deleteConnections(S source, long scn) throws Exception {
    pcs.deleteConnections(source, scn);
    map.remove(source);
  }
  
  @Override
  public synchronized void removeConnection(S source, int connection, long scn) throws Exception {
    pcs.removeConnection(source, connection, scn);
    int[] connections = pcs.getConnections(source);
    if(connections != null) {
      map.put(source, connections);
    } else {
      map.remove(source);
    }
  }
  
  @Override
  public synchronized void persist() throws IOException {
    pcs.persist();
  }
  
  @Override
  public synchronized void sync() throws IOException {
    pcs.sync();
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    pcs.saveHWMark(endOfPeriod);
  }
  
  @Override
  public long getHWMark() {
    return pcs.getHWMark();
  }
  
  @Override
  public long getLWMark() {
    return pcs.getLWMark();
  }
  
  @Override
  public Iterator<S> sourceIterator() {
    return map.keySet().iterator();
  }
}
