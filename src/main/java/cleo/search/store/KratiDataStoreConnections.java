package cleo.search.store;

import java.util.Iterator;

/**
 * KratiDataStoreConnections
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public final class KratiDataStoreConnections extends KratiDataStoreInts implements ConnectionsStore<String> {
  
  public KratiDataStoreConnections(KratiDataStore store) {
    super(store);
  }
  
  @Override
  public int[] getConnections(String source) {
    return get(source);
  }
  
  @Override
  public void putConnections(String source, int[] connections, long scn) throws Exception {
    put(source, connections, scn);
  }
  
  @Override
  public void deleteConnections(String source, long scn) throws Exception {
    delete(source, scn);
  }
  
  @Override
  public void addConnection(String source, int connection, long scn) throws Exception {
    add(source, connection, scn);
  }
  
  @Override
  public void removeConnection(String source, int connection, long scn) throws Exception {
    remove(source, connection, scn);
  }

  @Override
  public Iterator<String> sourceIterator() {
    return keyIterator();
  }
}
