package cleo.search.typeahead;

import java.io.Serializable;

/**
 * HitStats
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public final class HitStats implements Serializable {
  private static final long serialVersionUID = 1L;
  
  int numBrowseHits = 0;
  int numFilterHits = 0;
  int numResultHits = 0;
  long startTime = 0;
  long totalTime = 0;
  long lastTickTime = 0;
  
  void start() {
    startTime = System.currentTimeMillis();
    lastTickTime = startTime;
  }
  
  void stop() {
    totalTime = System.currentTimeMillis() - startTime;
  }
  
  public long tick() {
    long now = System.currentTimeMillis();
    long elapsedTime = now - lastTickTime;
    lastTickTime = now;
    return elapsedTime;
  }
  
  public final int getNumBrowseHits() {
    return numBrowseHits;
  }
  
  public final int getNumFilterHits() {
    return numFilterHits;
  }
  
  public final int getNumResultHits() {
    return numResultHits;
  }
  
  public final long getStartTime() {
    return startTime;
  }
  
  public final long getTotalTime() {
    return totalTime;
  }
}
