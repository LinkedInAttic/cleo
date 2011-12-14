package cleo.search.connection;

/**
 * ConnectionFilter
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface ConnectionFilter {

  /**
   * Checks whether a connection is acceptable or not.
   * 
   * @param conn - connection to test
   * @return <code>true</code> if the connection is acceptable. Otherwise, <code>false</code>.
   */
  public boolean accept(Connection conn);
  
  /**
   * Checks whether a connection is acceptable or not.
   *  
   * @param source - connection source
   * @param target - connection target
   * @param active - whether the connection is active or not.
   * @return <code>true</code> if the connection is acceptable. Otherwise, <code>false</code>.
   */
  public boolean accept(int source, int target, boolean active);
}
