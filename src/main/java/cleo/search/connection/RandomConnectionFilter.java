package cleo.search.connection;

import java.io.Serializable;
import java.util.Random;

/**
 * RandomConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public class RandomConnectionFilter implements ConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;
  private final Random random = new Random();
  
  /**
   * Accepts a connection randomly.
   */
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : (random.nextFloat() < 0.5F ? false : true);
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return random.nextFloat() < 0.5F ? false : true;
  }
  
  @Override
  public String toString() {
    return getClass().getName();
  }
}
