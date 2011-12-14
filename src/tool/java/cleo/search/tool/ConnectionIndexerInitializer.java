package cleo.search.tool;

import cleo.search.connection.ConnectionIndexer;

/**
 * ConnectionIndexerInitializer
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface ConnectionIndexerInitializer {

  /**
   * @return the initialized <code>ConnectionIndexer</code>.
   */
  public ConnectionIndexer getConnectionIndexer();
  
}
