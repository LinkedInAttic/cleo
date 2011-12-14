package cleo.search.connection;

import java.io.IOException;

/**
 * ConnectionIndexer
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface ConnectionIndexer extends ConnectionFilter {
  
  /**
   * Indexes a connection.
   * 
   * @param  conn - a connection to be indexed.
   * @return <code>true</code> if the underlying indexes changed
   *         as a result of this operation. Otherwise, <code>false</code>.
   * @throws Exception if this operation failed.
   */
  public boolean index(Connection conn) throws Exception;
  
  /**
   * Flushes indexes.
   * 
   * @throws IOException if this operation failed.
   */
  public void flush() throws IOException;
}
