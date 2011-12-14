package cleo.search.util;

import cleo.search.store.FloatArrayPartition;

/**
 * ElementScoreHandler
 * 
 * @author jwu
 * @since 03/25, 2011
 */
public class ElementScoreHandler implements ScoreHandler {
  private final FloatArrayPartition partition;
  
  public ElementScoreHandler(FloatArrayPartition partition) {
    this.partition = partition;
  }
  
  public FloatArrayPartition getScorePartition() {
    return partition;
  }
  
  @Override
  public double handle(int elementId, double elementScore) {
    if(partition.hasIndex(elementId)) {
      partition.set(elementId, (float)elementScore);
    }
    
    return elementScore;
  }
}
