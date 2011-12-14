package cleo.search.connection;

/**
 * Connection
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface Connection {
  
  /**
   * @return the source of connection.
   */
  public int source();
  
  /**
   * @return the target of connection.
   */
  public int target();
  
  /**
   * @return the strength of this connection.
   */
  public int getStrength();
  
  /**
   * Sets the strength of this connection.
   * 
   * @param strength
   */
  public void setStrength(int strength);
  
  /**
   * @return the timestamp of this connection.
   */
  public long getTimestamp();
  
  /**
   * Sets the timestamp of this connection.
   * 
   * @param timestamp
   */
  public void setTimestamp(long timestamp);
  
  /**
   * @return <code>true</code> if this connection is active. Otherwise, <code>false</code>.
   */
  public boolean isActive();
  
  /**
   * Indicate whether this connection is active.
   * 
   * @param b
   */
  public void setActive(boolean b);
}
