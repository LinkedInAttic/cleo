package cleo.search.test.store;

import cleo.search.store.ConnectionsStore;
import cleo.search.store.MemoryConnectionsStore;

/**
 * TestMemoryConnectionsStore
 * 
 * @author jwu
 * @since 02/12, 2011
 */
public class TestMemoryConnectionsStore extends TestConnectionsStore {

  @Override
  protected ConnectionsStore<String> createConnectionsStore() throws Exception {
    ConnectionsStore<String> s = super.createConnectionsStore();
    return new MemoryConnectionsStore<String>(getInitialCapacity(), s);
  }  
}
