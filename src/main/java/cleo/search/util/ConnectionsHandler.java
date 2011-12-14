package cleo.search.util;

/**
 * ConnectionsHandler
 * 
 * @author jwu
 * @since 01/19, 2011
 */
public interface ConnectionsHandler {

  /**
   * Handles connections accordingly.
   *  
   * @param source      - the connection source
   * @param connections - the connection targets
   * @return the connections that have been processed.
   * @throws Exception
   */
  public int[] handle(int source, int[] connections) throws Exception;
  
}
