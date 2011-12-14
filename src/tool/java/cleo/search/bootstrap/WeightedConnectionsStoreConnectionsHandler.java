package cleo.search.bootstrap;

import krati.util.Chronos;
import cleo.search.connection.ConnectionFilter;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.util.ConnectionsHandler;

/**
 * WeightedConnectionsStoreConnectionStrengthHandler
 * 
 * @author jwu
 * @since 04/25, 2011
 */
public class WeightedConnectionsStoreConnectionsHandler implements ConnectionsHandler {
  private final ArrayStoreWeights weightedConnectionsStore;
  private final ConnectionFilter connectionFilter;
  private final long timestamp;
  
  private volatile long counter = 0;
  private final long monitorBatch = 1000000;
  private final Chronos clock = new Chronos();
  
  public WeightedConnectionsStoreConnectionsHandler(ArrayStoreWeights weightedConnectionsStore, ConnectionFilter connectionFilter, long timestamp) {
    this.weightedConnectionsStore = weightedConnectionsStore;
    this.connectionFilter = connectionFilter;
    this.timestamp = timestamp;
  }
  
  public final ArrayStoreWeights getWeightedConnectionsStore() {
    return weightedConnectionsStore;
  }
  
  public final ConnectionFilter getConnectionFilter() {
    return connectionFilter;
  }
  
  @Override
  public int[] handle(int source, int[] connections) throws Exception {
    counter++;

    int cnt = 0;
    int[] targets = connections;
    for(int i = 0; i < connections.length; i++) {
      if(connectionFilter.accept(source, connections[i], true)) {
        cnt++;
      }
    }
    
    if(cnt < connections.length) {
      int ind = 0;
      targets = new int[cnt];
      for(int i = 0; i < connections.length; i++) {
        if(connectionFilter.accept(source, connections[i], true)) {        
          targets[ind] = connections[i];
          ind++;
        }
      }
    }
    
    int[][] weightData = new int[2][];
    weightData[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX] = targets;
    weightData[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX] = new int[targets.length];
    weightedConnectionsStore.setWeightData(source, weightData, timestamp);
    
    if(counter % monitorBatch == 0) {
      System.out.printf("processed %d in %d ms%n", monitorBatch, clock.tick());
    }
    
    return targets;
  }
}
