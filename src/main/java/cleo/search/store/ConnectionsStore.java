package cleo.search.store;

import java.util.Iterator;

import krati.Persistable;

/**
 * ConnectionsStore
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public interface ConnectionsStore<S> extends Persistable {

  /**
   * Get the connections
   */
  public int[] getConnections(S source);
  
  /**
   * Set the connections.
   * 
   * @param source      - source
   * @param connections - source connections
   * @param scn         - system change number
   */
  public void putConnections(S source, int[] connections, long scn) throws Exception;
  
  /**
   * Delete the connections for a source.
   * 
   * @param scn         - system change number
   */
  public void deleteConnections(S source, long scn) throws Exception;
  
  /**
   * Add a connection to a source.
   * 
   * @param source      - source
   * @param connection  - connection to add
   * @param scn         - system change number
   * @throws Exception
   */
  public void addConnection(S source, int connection, long scn) throws Exception;
  
  /**
   * Remove a connection from a source.
   * 
   * @param source      - source
   * @param connection  - connection to remove
   * @param scn         - system change number
   * @throws Exception
   */
  public void removeConnection(S source, int connection, long scn) throws Exception;
  
  /**
   * @return the connection source iterator.
   */
  public Iterator<S> sourceIterator();
}
