package cleo.search.test.typeahead;

import cleo.search.SimpleElement;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;

/**
 * TestWeightedNetworkTypeaheadCached
 * 
 * @author jwu
 * @since 04/29, 2011
 */
public class TestWeightedNetworkTypeaheadCached extends TestWeightedNetworkTypeahead {

  @Override
  protected ArrayStoreElement<SimpleElement> createElementStore() throws Exception {
    return new MemoryArrayStoreElement<SimpleElement>(super.createElementStore());
  }
}
