package cleo.search.util;

/**
 * ScoreScanner
 * 
 * @author jwu
 * @since 02/18, 2011
 */
public interface ScoreScanner {
  
  /**
   * Scan element scores.
   * 
   * @param handler - the handler to process element scores scanned by this scanner.
   */
  public void scan(ScoreHandler handler);
  
}
