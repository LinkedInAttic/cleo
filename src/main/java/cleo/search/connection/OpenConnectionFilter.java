package cleo.search.connection;

import java.io.Serializable;

/**
 * OpenConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public class OpenConnectionFilter implements ConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Accepts all non-null connections.
   * 
   * @return <code>true</code> for all non-null connections.
   */
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : true;
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return true;
  }
  
  @Override
  public String toString() {
    return getClass().getName();
  }
}
