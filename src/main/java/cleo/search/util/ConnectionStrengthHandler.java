package cleo.search.util;

/**
 * ConnectionStrengthHandler
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public interface ConnectionStrengthHandler {
  
  /**
   * Handles connections and connection strengths accordingly.
   * 
   * @param source      - the connection source
   * @param connections - the connection targets
   * @param strengths   - the connection strengths
   * @throws Exception
   */
  public void handle(int source, int[] connections, int[] strengths) throws Exception;
  
}
